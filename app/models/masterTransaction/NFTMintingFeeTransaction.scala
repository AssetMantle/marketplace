package models.masterTransaction

import constants.Scheduler
import constants.Transaction.TxUtil
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
                                           utilitiesTransaction: utilities.Transaction,
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

    implicit val txUtil: TxUtil = TxUtil("NFT_MINTING_FEE", 150000)

    def transaction(accountId: String, nft: NFT, fromAddress: String, amount: MicroNumber, gasPrice: BigDecimal, ecKey: ECKey): Future[UserTransaction] = {

      val messages = Seq(utilities.BlockchainTransaction.getSendCoinMsgAsAny(fromAddress = fromAddress, toAddress = constants.Secret.mintAssetWallet.address, amount = Seq(Coin(denom = constants.Blockchain.StakingToken, amount = amount))))

      def masterTxFunc(txHash: String) = Service.addWithNoneStatus(accountId = accountId, collectionId = nft.collectionId, txHash = txHash, nftId = nft.id)

      val userTx = utilitiesTransaction.doUserTx(messages = messages, gasPrice = gasPrice, accountId = accountId, fromAddress = fromAddress, ecKey = ecKey, masterTxFunction = masterTxFunc)

      for {
        (userTx, _) <- userTx
      } yield userTx
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

              for {
                _ <- markSuccess
                _ <- markMintReady
                nft <- nft
                _ <- sendNotifications(mintingNFTs.head, nft)
              } yield ()
            } else {
              val mintingNFTs = Service.getByTxHash(txHash)
              val markMasterFailed = Service.markFailed(txHash)

              def sendNotifications(mintingNFT: NFTMintingFeeTransaction) = {
                utilitiesNotification.send(mintingNFT.accountId, constants.Notification.NFT_MINTING_FEE_TRANSACTION_SUCCESSFUL)("")
              }

              for {
                mintingNFTs <- mintingNFTs
                _ <- markMasterFailed
                _ <- sendNotifications(mintingNFTs.head)
              } yield ()
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
          case baseException: BaseException => baseException.notifyLog("[PANIC]")
            logger.error("[PANIC] Something is seriously wrong with logic. Code should not reach here.")
        }

        Await.result(forComplete, Duration.Inf)
      }
    }
  }

}