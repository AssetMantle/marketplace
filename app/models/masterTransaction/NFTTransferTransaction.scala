package models.masterTransaction

import constants.Scheduler
import exceptions.BaseException
import models.blockchainTransaction.SplitSend
import models.master.NFT
import models.traits._
import models.{analytics, blockchainTransaction, master}
import org.bitcoinj.core.ECKey
import play.api.Logger
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.H2Profile.api._
import transactions.responses.blockchain.BroadcastTxSyncResponse
import utilities.AttoNumber

import javax.inject.{Inject, Singleton}
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}

case class NFTTransferTransaction(txHash: String, nftId: String, ownerId: String, quantity: Int, toIdentityId: String, toAccountId: Option[String], status: Option[Boolean], createdBy: Option[String] = None, createdOnMillisEpoch: Option[Long] = None, updatedBy: Option[String] = None, updatedOnMillisEpoch: Option[Long] = None) extends Logging with Entity3[String, String, String] {

  def id1: String = txHash

  def id2: String = nftId

  def id3: String = ownerId
}

object NFTTransferTransactions {

  private implicit val logger: Logger = Logger(this.getClass)

  private implicit val module: String = constants.Module.MASTER_TRANSACTION_NFT_TRANSFER_TRANSACTION


  class NFTTransferTransactionTable(tag: Tag) extends Table[NFTTransferTransaction](tag, "NFTTransferTransaction") with ModelTable3[String, String, String] {

    def * = (txHash, nftId, ownerId, quantity, toIdentityId, toAccountId.?, status.?, createdBy.?, createdOnMillisEpoch.?, updatedBy.?, updatedOnMillisEpoch.?) <> (NFTTransferTransaction.tupled, NFTTransferTransaction.unapply)

    def txHash = column[String]("txHash", O.PrimaryKey)

    def nftId = column[String]("nftId", O.PrimaryKey)

    def ownerId = column[String]("ownerId", O.PrimaryKey)

    def quantity = column[Int]("quantity")

    def toIdentityId = column[String]("toIdentityId")

    def toAccountId = column[String]("toAccountId")

    def status = column[Boolean]("status")

    def createdBy = column[String]("createdBy")

    def createdOnMillisEpoch = column[Long]("createdOnMillisEpoch")

    def updatedBy = column[String]("updatedBy")

    def updatedOnMillisEpoch = column[Long]("updatedOnMillisEpoch")

    def id1 = txHash

    def id2 = nftId

    def id3 = ownerId

  }

  val TableQuery = new TableQuery(tag => new NFTTransferTransactionTable(tag))

}

@Singleton
class NFTTransferTransactions @Inject()(
                                         protected val databaseConfigProvider: DatabaseConfigProvider,
                                         blockchainTransactionSplitSends: blockchainTransaction.SplitSends,
                                         broadcastTxSync: transactions.blockchain.BroadcastTxSync,
                                         utilitiesOperations: utilities.Operations,
                                         getUnconfirmedTxs: queries.blockchain.GetUnconfirmedTxs,
                                         getAccount: queries.blockchain.GetAccount,
                                         getAbciInfo: queries.blockchain.GetABCIInfo,
                                         masterCollections: master.Collections,
                                         masterNFTs: master.NFTs,
                                         masterNFTProperties: master.NFTProperties,
                                         masterNFTOwners: master.NFTOwners,
                                         masterSecondaryMarkets: master.SecondaryMarkets,
                                         collectionsAnalysis: analytics.CollectionsAnalysis,
                                         utilitiesNotification: utilities.Notification,
                                       )(implicit override val executionContext: ExecutionContext)
  extends GenericDaoImpl3[NFTTransferTransactions.NFTTransferTransactionTable, NFTTransferTransaction, String, String, String](
    databaseConfigProvider,
    NFTTransferTransactions.TableQuery,
    executionContext,
    NFTTransferTransactions.module,
    NFTTransferTransactions.logger
  ) {

  object Service {

    def addWithNoneStatus(txHash: String, nftId: String, ownerId: String, quantity: Int, toIdentityId: String, toAccountId: String): Future[Unit] = create(NFTTransferTransaction(txHash = txHash, nftId = nftId, ownerId = ownerId, quantity = quantity, toIdentityId = toIdentityId, toAccountId = Option(toAccountId), status = None))

    def getByTxHash(txHash: String): Future[Seq[NFTTransferTransaction]] = filter(_.txHash === txHash)

    def markSuccess(txHash: String): Future[Int] = customUpdate(NFTTransferTransactions.TableQuery.filter(_.txHash === txHash).map(_.status).update(true))

    def markFailed(txHash: String): Future[Int] = customUpdate(NFTTransferTransactions.TableQuery.filter(_.txHash === txHash).map(_.status).update(false))

    def getAllPendingStatus: Future[Seq[NFTTransferTransaction]] = filter(_.status.?.isEmpty)

  }

  object Utility {
    def transaction(nft: NFT, ownerId: String, quantity: Int, fromAddress: String, toAccountId: String, gasPrice: BigDecimal, ecKey: ECKey): Future[BlockchainTransaction] = {
      // TODO
      // val bcAccount = blockchainAccounts.Service.tryGet(fromAddress)
      val abciInfo = getAbciInfo.Service.get
      val bcAccount = getAccount.Service.get(fromAddress).map(_.account.toSerializableAccount).recover {
        case exception: Exception => models.blockchain.Account(address = fromAddress, accountType = None, accountNumber = 0, sequence = 0, publicKey = None)
      }
      val unconfirmedTxs = getUnconfirmedTxs.Service.get()

      def checkMempoolAndAddTx(bcAccount: models.blockchain.Account, latestBlockHeight: Int, unconfirmedTxHashes: Seq[String]) = {
        val timeoutHeight = latestBlockHeight + constants.Blockchain.TxTimeoutHeight
        val nftTransferMsg = utilities.BlockchainTransaction.getSplitSendMsg(
          fromID = utilities.Identity.getMantlePlaceIdentityID(ownerId),
          fromAddress = fromAddress,
          toID = utilities.Identity.getMantlePlaceIdentityID(toAccountId),
          assetId = nft.getAssetID,
          amount = BigDecimal(quantity) / AttoNumber.factor,
        )
        val (txRawBytes, memo) = utilities.BlockchainTransaction.getTxRawBytesWithSignedMemo(
          messages = Seq(nftTransferMsg),
          fee = utilities.BlockchainTransaction.getFee(gasPrice = gasPrice, gasLimit = constants.Blockchain.DefaultNFTTransferGasLimit),
          gasLimit = constants.Blockchain.DefaultNFTTransferGasLimit,
          account = bcAccount,
          ecKey = ecKey,
          timeoutHeight = timeoutHeight)
        val txHash = utilities.Secrets.sha256HashHexString(txRawBytes)

        def checkAndAdd(unconfirmedTxHashes: Seq[String]) = {
          if (!unconfirmedTxHashes.contains(txHash)) {
            for {
              nftTransfer <- blockchainTransactionSplitSends.Service.add(txHash = txHash, txRawBytes = txRawBytes, fromAddress = fromAddress, status = None, memo = Option(memo), timeoutHeight = timeoutHeight)
              _ <- Service.addWithNoneStatus(txHash = txHash, nftId = nft.id, ownerId = ownerId, quantity = quantity, toIdentityId = utilities.Identity.getMantlePlaceIdentityID(toAccountId).asString, toAccountId = toAccountId)
            } yield nftTransfer
          } else constants.Response.TRANSACTION_ALREADY_IN_MEMPOOL.throwFutureBaseException()
        }

        for {
          nftTransfer <- checkAndAdd(unconfirmedTxHashes)
        } yield nftTransfer
      }

      def broadcastTxAndUpdate(splitSend: SplitSend) = {
        val broadcastTx = broadcastTxSync.Service.get(splitSend.getTxRawAsHexString)

        def update(successResponse: Option[BroadcastTxSyncResponse.Response], errorResponse: Option[BroadcastTxSyncResponse.ErrorResponse]) = if (errorResponse.nonEmpty) blockchainTransactionSplitSends.Service.markFailedWithLog(txHashes = Seq(splitSend.txHash), log = errorResponse.get.error.data)
        else if (successResponse.nonEmpty && successResponse.get.result.code != 0) blockchainTransactionSplitSends.Service.markFailedWithLog(txHashes = Seq(splitSend.txHash), log = successResponse.get.result.log)
        else Future(0)

        for {
          (successResponse, errorResponse) <- broadcastTx
          _ <- update(successResponse, errorResponse)
        } yield ()
      }

      for {
        abciInfo <- abciInfo
        bcAccount <- bcAccount
        unconfirmedTxs <- unconfirmedTxs
        nftTransfer <- checkMempoolAndAddTx(bcAccount, abciInfo.result.response.last_block_height.toInt, unconfirmedTxs.result.txs.map(x => utilities.Secrets.base64URLDecode(x).map("%02x".format(_)).mkString.toUpperCase))
        _ <- broadcastTxAndUpdate(nftTransfer)
      } yield nftTransfer
    }

    val scheduler: Scheduler = new Scheduler {
      val name: String = NFTTransferTransactions.module

      //      override val initialDelay: FiniteDuration = constants.Scheduler.QuarterHour

      def runner(): Unit = {

        val nftTransferTxs = Service.getAllPendingStatus

        def getTxs(hashes: Seq[String]) = blockchainTransactionSplitSends.Service.getByHashes(hashes)

        def checkAndUpdate(nftTransferTxs: Seq[NFTTransferTransaction], txs: Seq[SplitSend]) = utilitiesOperations.traverse(txs.filter(_.status.isDefined)) { tx =>

          val nftTransferTx = nftTransferTxs.find(_.txHash == tx.txHash).get
          val onTxComplete = if (tx.status.get) {
            val markSuccess = Service.markSuccess(nftTransferTx.txHash)
            val nft = masterNFTs.Service.tryGet(nftTransferTx.nftId)
            val oldNFTOwner = if (nftTransferTx.toAccountId.isDefined) masterNFTOwners.Service.onSuccessfulNFTTransfer(nftId = nftTransferTx.nftId, fromOwnerID = nftTransferTx.ownerId, quantity = nftTransferTx.quantity, toOwnerID = nftTransferTx.toAccountId.get) else Future()

            def sendNotifications(nft: NFT) = {
              utilitiesNotification.send(nftTransferTx.ownerId, constants.Notification.FROM_OWNER_NFT_TRANSFER_SUCCESSFUL, nft.name, nftTransferTx.toAccountId.getOrElse(nftTransferTx.toIdentityId))(s"${nftTransferTx.nftId}")
              if (nftTransferTx.toAccountId.isDefined) utilitiesNotification.send(nftTransferTx.toAccountId.get, constants.Notification.TO_OWNER_NFT_TRANSFER_SUCCESSFUL, nft.name, nftTransferTx.ownerId)(s"'${nftTransferTx.nftId}'") else Future()
            }

            (for {
              _ <- markSuccess
              nft <- nft
              oldNFTOwner <- oldNFTOwner
              _ <- sendNotifications(nft)
            } yield ()
              ).recover {
              case _: BaseException => logger.error("[PANIC] Something is seriously wrong with logic. Code should not reach here.")
            }
          } else {
            val markFailed = Service.markFailed(nftTransferTx.txHash)
            val nft = masterNFTs.Service.tryGet(nftTransferTx.nftId)

            def sendNotifications(nft: NFT) = utilitiesNotification.send(nftTransferTx.ownerId, constants.Notification.NFT_TRANSFER_FAILED, nft.name, nftTransferTx.toAccountId.getOrElse(nftTransferTx.toIdentityId))("")

            (for {
              _ <- markFailed
              nft <- nft
              _ <- sendNotifications(nft)
            } yield ()
              ).recover {
              case _: BaseException => logger.error("[PANIC] Something is seriously wrong with logic. Code should not reach here.")
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