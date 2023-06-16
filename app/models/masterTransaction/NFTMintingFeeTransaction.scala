package models.masterTransaction

import constants.Scheduler
import exceptions.BaseException
import models.blockchainTransaction.{UserTransaction, UserTransactions}
import models.common.Coin
import models.master
import models.master.NFT
import models.masterTransaction.NFTMintingFeeTransactions.NFTMintingFeeTransactionTable
import models.traits._
import org.bitcoinj.core.ECKey
import play.api.Logger
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.H2Profile.api._
import utilities.MicroNumber

import javax.inject.{Inject, Singleton}
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}

case class NFTMintingFeeTransaction(txHash: String, nftId: String, collectionId: String, accountId: String, status: Option[Boolean], createdBy: Option[String] = None, createdOnMillisEpoch: Option[Long] = None, updatedBy: Option[String] = None, updatedOnMillisEpoch: Option[Long] = None) extends Logging with Entity[String] {
  def id: String = txHash
}

private[masterTransaction] object NFTMintingFeeTransactions {

  class NFTMintingFeeTransactionTable(tag: Tag) extends Table[NFTMintingFeeTransaction](tag, "NFTMintingFeeTransaction") with ModelTable[String] {

    def * = (txHash, nftId, collectionId, accountId, status.?, createdBy.?, createdOnMillisEpoch.?, updatedBy.?, updatedOnMillisEpoch.?) <> (NFTMintingFeeTransaction.tupled, NFTMintingFeeTransaction.unapply)

    def txHash = column[String]("txHash", O.PrimaryKey)

    def nftId = column[String]("nftId")

    def collectionId = column[String]("collectionId")

    def accountId = column[String]("accountId")

    def status = column[Boolean]("status")

    def createdBy = column[String]("createdBy")

    def createdOnMillisEpoch = column[Long]("createdOnMillisEpoch")

    def updatedBy = column[String]("updatedBy")

    def updatedOnMillisEpoch = column[Long]("updatedOnMillisEpoch")

    def id = txHash
  }

}

@Singleton
class NFTMintingFeeTransactions @Inject()(
                                           protected val dbConfigProvider: DatabaseConfigProvider,
                                           utilitiesOperations: utilities.Operations,
                                           masterCollections: master.Collections,
                                           masterNFTs: master.NFTs,
                                           masterNFTOwners: master.NFTOwners,
                                           utilitiesNotification: utilities.Notification,
                                           userTransactions: UserTransactions,
                                         )(implicit val executionContext: ExecutionContext)
  extends GenericDaoImpl[NFTMintingFeeTransactions.NFTMintingFeeTransactionTable, NFTMintingFeeTransaction, String]() {

  implicit val logger: Logger = Logger(this.getClass)

  implicit val module: String = constants.Module.MASTER_TRANSACTION_NFT_MINTING_FEE_TRANSACTION

  val tableQuery = new TableQuery(tag => new NFTMintingFeeTransactionTable(tag))

  object Service {

    def addWithNoneStatus(accountId: String, collectionId: String, txHash: String, nftId: String): Future[String] = create(NFTMintingFeeTransaction(txHash = txHash, nftId = nftId, status = None, collectionId = collectionId, accountId = accountId)).map(_.id)

    def getByTxHash(txHash: String): Future[Seq[NFTMintingFeeTransaction]] = filter(_.txHash === txHash)

    def markSuccess(txHash: String): Future[Int] = customUpdate(tableQuery.filter(_.txHash === txHash).map(_.status).update(true))

    def markFailed(txHash: String): Future[Int] = customUpdate(tableQuery.filter(_.txHash === txHash).map(_.status).update(false))

    def getAllPendingStatus: Future[Seq[NFTMintingFeeTransaction]] = filter(_.status.?.isEmpty)

  }

  object Utility {
    def transaction(accountId: String, nft: NFT, fromAddress: String, amount: MicroNumber, gasPrice: BigDecimal, ecKey: ECKey): Future[BlockchainTransaction] = {
      val latestHeightAccountUnconfirmedTxs = userTransactions.Utility.getLatestHeightAccountAndUnconfirmedTxs(fromAddress)

      def checkMempoolAndAddTx(bcAccount: models.blockchain.Account, latestBlockHeight: Int, unconfirmedTxHashes: Seq[String]) = {
        val timeoutHeight = latestBlockHeight + constants.Transaction.TimeoutHeight
        val (txRawBytes, memo) = utilities.BlockchainTransaction.getTxRawBytesWithSignedMemo(
          messages = Seq(utilities.BlockchainTransaction.getSendCoinMsgAsAny(fromAddress = fromAddress, toAddress = constants.Transaction.Wallet.FeeCollectorAddress, amount = Seq(Coin(denom = constants.Blockchain.StakingToken, amount = amount)))),
          fee = utilities.BlockchainTransaction.getFee(gasPrice = gasPrice, gasLimit = constants.Transaction.DefaultMintAssetGasLimit),
          gasLimit = constants.Transaction.DefaultMintAssetGasLimit,
          account = bcAccount,
          ecKey = ecKey,
          timeoutHeight = timeoutHeight)
        val txHash = utilities.Secrets.sha256HashHexString(txRawBytes)

        val checkAndAdd = {
          if (!unconfirmedTxHashes.contains(txHash)) {
            for {
              userTransaction <- userTransactions.Service.addWithNoneStatus(txHash = txHash, accountId = accountId, fromAddress = fromAddress, memo = Option(memo), timeoutHeight = timeoutHeight, txType = constants.Transaction.User.NFT_MINTING_FEE)
              _ <- Service.addWithNoneStatus(accountId = accountId, collectionId = nft.collectionId, txHash = txHash, nftId = nft.id)
            } yield userTransaction
          } else constants.Response.TRANSACTION_ALREADY_IN_MEMPOOL.throwBaseException()
        }

        for {
          userTransaction <- checkAndAdd
        } yield (userTransaction, txRawBytes)
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

      def runner(): Unit = {
        val nftMintingFeeTxs = Service.getAllPendingStatus

        def checkAndUpdate(nftMintingFeeTxs: Seq[NFTMintingFeeTransaction]) = utilitiesOperations.traverse(nftMintingFeeTxs.map(_.txHash).distinct) { txHash =>
          val transaction = userTransactions.Service.tryGet(txHash)

          def onTxComplete(userTransaction: UserTransaction) = if (userTransaction.status.isDefined) {
            if (userTransaction.status.get) {
              val mintingNFTs = nftMintingFeeTxs.filter(_.txHash == txHash)
              val markSuccess = Service.markSuccess(txHash)
              val markMintReady = masterNFTs.Service.markReadyForMint(mintingNFTs.map(_.nftId))
              val nft = masterNFTs.Service.tryGet(mintingNFTs.head.nftId)

              def sendNotifications(nftMintingFeeTransaction: NFTMintingFeeTransaction, nft: NFT) = {
                val messageParameter = if (mintingNFTs.length == 1) nft.name else s"${nft.name} (+${mintingNFTs.length - 1})"
                utilitiesNotification.send(nftMintingFeeTransaction.accountId, constants.Notification.NFT_MINTING_FEE_TRANSACTION_SUCCESSFUL, messageParameter)(s"'${nft.id}'")
              }

              (for {
                _ <- markSuccess
                _ <- markMintReady
                nft <- nft
                _ <- sendNotifications(mintingNFTs.head, nft)
              } yield ()
                ).recover {
                case exception: Exception => logger.error(exception.getLocalizedMessage)
                  logger.error("[PANIC] Something is seriously wrong with logic. Code should not reach here.")
              }
            } else {
              val mintingNFTs = Service.getByTxHash(txHash)
              val markMasterFailed = Service.markFailed(txHash)

              def sendNotifications(mintingNFT: NFTMintingFeeTransaction) = {
                utilitiesNotification.send(mintingNFT.accountId, constants.Notification.NFT_MINTING_FEE_TRANSACTION_SUCCESSFUL)("")
              }

              (for {
                mintingNFTs <- mintingNFTs
                _ <- markMasterFailed
                _ <- sendNotifications(mintingNFTs.head)
              } yield ()
                ).recover {
                case exception: Exception => logger.error(exception.getLocalizedMessage)
                  logger.error("[PANIC] Something is seriously wrong with logic. Code should not reach here.")
              }
            }
          } else Future()

          for {
            transaction <- transaction
            _ <- onTxComplete(transaction)
          } yield ()

        }

        val forComplete = (for {
          nftMintingFeeTxs <- nftMintingFeeTxs
          _ <- checkAndUpdate(nftMintingFeeTxs)
        } yield ()).recover {
          case baseException: BaseException => logger.error(baseException.failure.logMessage)
        }

        Await.result(forComplete, Duration.Inf)
      }
    }
  }

}