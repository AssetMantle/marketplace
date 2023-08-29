package models.masterTransaction

import constants.Scheduler
import exceptions.BaseException
import models.blockchainTransaction.{UserTransaction, UserTransactions}
import models.master.NFT
import models.masterTransaction.NFTTransferTransactions.NFTTransferTransactionTable
import models.traits._
import models.{analytics, master}
import org.bitcoinj.core.ECKey
import play.api.Logger
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.H2Profile.api._

import javax.inject.{Inject, Singleton}
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}

case class NFTTransferTransaction(txHash: String, nftId: String, fromId: String, quantity: Int, toIdentityId: String, toAccountId: Option[String], status: Option[Boolean], createdBy: Option[String] = None, createdOnMillisEpoch: Option[Long] = None, updatedBy: Option[String] = None, updatedOnMillisEpoch: Option[Long] = None) extends Logging with Entity[String] {

  def id: String = txHash

}

private[masterTransaction] object NFTTransferTransactions {

  class NFTTransferTransactionTable(tag: Tag) extends Table[NFTTransferTransaction](tag, "NFTTransferTransaction") with ModelTable[String] {

    def * = (txHash, nftId, fromId, quantity, toIdentityId, toAccountId.?, status.?, createdBy.?, createdOnMillisEpoch.?, updatedBy.?, updatedOnMillisEpoch.?) <> (NFTTransferTransaction.tupled, NFTTransferTransaction.unapply)

    def txHash = column[String]("txHash", O.PrimaryKey)

    def nftId = column[String]("nftId")

    def fromId = column[String]("fromId")

    def quantity = column[Int]("quantity")

    def toIdentityId = column[String]("toIdentityId")

    def toAccountId = column[String]("toAccountId")

    def status = column[Boolean]("status")

    def createdBy = column[String]("createdBy")

    def createdOnMillisEpoch = column[Long]("createdOnMillisEpoch")

    def updatedBy = column[String]("updatedBy")

    def updatedOnMillisEpoch = column[Long]("updatedOnMillisEpoch")

    def id = txHash

  }

}

@Singleton
class NFTTransferTransactions @Inject()(
                                         protected val dbConfigProvider: DatabaseConfigProvider,
                                         utilitiesOperations: utilities.Operations,
                                         masterCollections: master.Collections,
                                         masterNFTs: master.NFTs,
                                         masterNFTProperties: master.NFTProperties,
                                         masterNFTOwners: master.NFTOwners,
                                         masterSecondaryMarkets: master.SecondaryMarkets,
                                         collectionsAnalysis: analytics.CollectionsAnalysis,
                                         utilitiesNotification: utilities.Notification,
                                         userTransactions: UserTransactions,
                                       )(implicit val executionContext: ExecutionContext)
  extends GenericDaoImpl[NFTTransferTransactions.NFTTransferTransactionTable, NFTTransferTransaction, String]() {

  implicit val logger: Logger = Logger(this.getClass)

  implicit val module: String = constants.Module.MASTER_TRANSACTION_NFT_TRANSFER_TRANSACTION

  val tableQuery = new TableQuery(tag => new NFTTransferTransactionTable(tag))

  object Service {

    def addWithNoneStatus(txHash: String, nftId: String, fromId: String, quantity: Int, toIdentityId: String, toAccountId: String): Future[String] = create(NFTTransferTransaction(txHash = txHash, nftId = nftId, fromId = fromId, quantity = quantity, toIdentityId = toIdentityId, toAccountId = Option(toAccountId), status = None)).map(_.id)

    def getByTxHash(txHash: String): Future[Seq[NFTTransferTransaction]] = filter(_.txHash === txHash)

    def markSuccess(txHash: String): Future[Int] = customUpdate(tableQuery.filter(_.txHash === txHash).map(_.status).update(true))

    def markFailed(txHash: String): Future[Int] = customUpdate(tableQuery.filter(_.txHash === txHash).map(_.status).update(false))

    def getAllPendingStatus: Future[Seq[NFTTransferTransaction]] = filter(_.status.?.isEmpty)

  }

  object Utility {
    def transaction(nft: NFT, fromId: String, quantity: Int, fromAddress: String, toAccountId: String, gasPrice: BigDecimal, ecKey: ECKey): Future[BlockchainTransaction] = {
      val latestHeightAccountUnconfirmedTxs = userTransactions.Utility.getLatestHeightAccountAndUnconfirmedTxs(fromAddress)

      def checkMempoolAndAddTx(bcAccount: models.blockchain.Account, latestBlockHeight: Int, unconfirmedTxHashes: Seq[String]) = {
        val timeoutHeight = latestBlockHeight + constants.Transaction.TimeoutHeight
        val nftTransferMsg = utilities.BlockchainTransaction.getAssetSendMsg(
          fromID = utilities.Identity.getMantlePlaceIdentityID(fromId),
          fromAddress = fromAddress,
          toID = utilities.Identity.getMantlePlaceIdentityID(toAccountId),
          assetId = nft.getAssetID,
          amount = quantity,
        )
        val (txRawBytes, _) = utilities.BlockchainTransaction.getTxRawBytesWithSignedMemo(
          messages = Seq(nftTransferMsg),
          fee = utilities.BlockchainTransaction.getFee(gasPrice = gasPrice, gasLimit = constants.Transaction.DefaultNFTTransferGasLimit),
          gasLimit = constants.Transaction.DefaultNFTTransferGasLimit,
          account = bcAccount,
          ecKey = ecKey,
          timeoutHeight = timeoutHeight)
        val txHash = utilities.Secrets.sha256HashHexString(txRawBytes)

        val checkAndAdd = {
          if (!unconfirmedTxHashes.contains(txHash)) {
            for {
              nftTransfer <- userTransactions.Service.addWithNoneStatus(txHash = txHash, accountId = fromId, fromAddress = fromAddress, timeoutHeight = timeoutHeight, txType = constants.Transaction.User.NFT_TRANSFER)
              _ <- Service.addWithNoneStatus(txHash = txHash, nftId = nft.id, fromId = fromId, quantity = quantity, toIdentityId = utilities.Identity.getMantlePlaceIdentityID(toAccountId).asString, toAccountId = toAccountId)
            } yield nftTransfer
          } else constants.Response.TRANSACTION_ALREADY_IN_MEMPOOL.throwBaseException()
        }

        for {
          nftTransfer <- checkAndAdd
        } yield (nftTransfer, txRawBytes)
      }

      def broadcastTxAndUpdate(userTransaction: UserTransaction, txRawBytes: Array[Byte]) = userTransactions.Utility.broadcastTxAndUpdate(userTransaction, txRawBytes)

      for {
        (latestHeight, bcAccount, unconfirmedTxs) <- latestHeightAccountUnconfirmedTxs
        (userTransaction, txRawBytes) <- checkMempoolAndAddTx(bcAccount, latestHeight, unconfirmedTxs.result.txs.map(x => utilities.Secrets.base64URLDecode(x).map("%02x".format(_)).mkString.toUpperCase))
        updatedUserTransaction <- broadcastTxAndUpdate(userTransaction, txRawBytes)
      } yield updatedUserTransaction
    }

    val scheduler: Scheduler = new Scheduler {
      val name: String = module

      //      override val initialDelay: FiniteDuration = constants.Scheduler.QuarterHour

      def runner(): Unit = {

        val nftTransferTxs = Service.getAllPendingStatus

        def getTxs(hashes: Seq[String]) = userTransactions.Service.getByHashes(hashes)

        def checkAndUpdate(nftTransferTxs: Seq[NFTTransferTransaction], userTransactions: Seq[UserTransaction]) = utilitiesOperations.traverse(userTransactions.filter(_.status.isDefined)) { userTransaction =>

          val nftTransferTx = nftTransferTxs.find(_.txHash == userTransaction.txHash).get
          val onTxComplete = if (userTransaction.status.get) {
            val markSuccess = Service.markSuccess(nftTransferTx.txHash)
            val nft = masterNFTs.Service.tryGet(nftTransferTx.nftId)
            val oldNFTOwner = if (nftTransferTx.toAccountId.isDefined) masterNFTOwners.Service.onSuccessfulNFTTransfer(nftId = nftTransferTx.nftId, fromOwnerID = nftTransferTx.fromId, quantity = nftTransferTx.quantity, toOwnerID = nftTransferTx.toAccountId.get) else Future()

            def sendNotifications(nft: NFT) = {
              utilitiesNotification.send(nftTransferTx.fromId, constants.Notification.FROM_OWNER_NFT_TRANSFER_SUCCESSFUL, nft.name, nftTransferTx.toAccountId.getOrElse(nftTransferTx.toIdentityId))(s"${nftTransferTx.nftId}")
              if (nftTransferTx.toAccountId.isDefined) utilitiesNotification.send(nftTransferTx.toAccountId.get, constants.Notification.TO_OWNER_NFT_TRANSFER_SUCCESSFUL, nft.name, nftTransferTx.fromId)(s"'${nftTransferTx.nftId}'") else Future()
            }

            (for {
              _ <- markSuccess
              nft <- nft
              oldNFTOwner <- oldNFTOwner
              _ <- sendNotifications(nft)
            } yield ()
              ).recover {
              case exception: Exception => logger.error(exception.getLocalizedMessage)
                logger.error("[PANIC] Something is seriously wrong with logic. Code should not reach here.")
            }
          } else {
            val markFailed = Service.markFailed(nftTransferTx.txHash)
            val nft = masterNFTs.Service.tryGet(nftTransferTx.nftId)

            def sendNotifications(nft: NFT) = utilitiesNotification.send(nftTransferTx.fromId, constants.Notification.NFT_TRANSFER_FAILED, nft.name, nftTransferTx.toAccountId.getOrElse(nftTransferTx.toIdentityId))("")

            (for {
              _ <- markFailed
              nft <- nft
              _ <- sendNotifications(nft)
            } yield ()
              ).recover {
              case exception: Exception => logger.error(exception.getLocalizedMessage)
                logger.error("[PANIC] Something is seriously wrong with logic. Code should not reach here.")
            }
          }

          for {
            _ <- onTxComplete
          } yield ()

        }

        val forComplete = (for {
          nftTransferTxs <- nftTransferTxs
          txs <- getTxs(nftTransferTxs.map(_.txHash).distinct)
          _ <- checkAndUpdate(nftTransferTxs, txs)
        } yield ()).recover {
          case baseException: BaseException => logger.error(baseException.failure.logMessage)
        }

        Await.result(forComplete, Duration.Inf)
      }
    }
  }

}