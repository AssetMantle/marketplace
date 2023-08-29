package models.masterTransaction

import constants.Scheduler
import exceptions.BaseException
import models.blockchainTransaction.UserTransaction
import models.master.SecondaryMarket
import models.masterTransaction.CancelOrderTransactions.CancelOrderTransactionTable
import models.traits._
import models.{blockchain, blockchainTransaction, master}
import org.bitcoinj.core.ECKey
import play.api.Logger
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.H2Profile.api._

import javax.inject.{Inject, Singleton}
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}

case class CancelOrderTransaction(txHash: String, secondaryMarketId: String, sellerId: String, status: Option[Boolean], createdBy: Option[String] = None, createdOnMillisEpoch: Option[Long] = None, updatedBy: Option[String] = None, updatedOnMillisEpoch: Option[Long] = None) extends Logging with Entity[String] {
  def id: String = txHash

}

private[masterTransaction] object CancelOrderTransactions {

  class CancelOrderTransactionTable(tag: Tag) extends Table[CancelOrderTransaction](tag, "CancelOrderTransaction") with ModelTable[String] {

    def * = (txHash, secondaryMarketId, sellerId, status.?, createdBy.?, createdOnMillisEpoch.?, updatedBy.?, updatedOnMillisEpoch.?) <> (CancelOrderTransaction.tupled, CancelOrderTransaction.unapply)

    def txHash = column[String]("txHash", O.PrimaryKey)

    def secondaryMarketId = column[String]("secondaryMarketId")

    def sellerId = column[String]("sellerId")

    def status = column[Boolean]("status")

    def createdBy = column[String]("createdBy")

    def createdOnMillisEpoch = column[Long]("createdOnMillisEpoch")

    def updatedBy = column[String]("updatedBy")

    def updatedOnMillisEpoch = column[Long]("updatedOnMillisEpoch")

    def id = txHash

  }

}

@Singleton
class CancelOrderTransactions @Inject()(
                                         protected val dbConfigProvider: DatabaseConfigProvider,
                                         blockchainIdentities: blockchain.Identities,
                                         masterSecondaryMarkets: master.SecondaryMarkets,
                                         masterNFTs: master.NFTs,
                                         utilitiesOperations: utilities.Operations,
                                         utilitiesNotification: utilities.Notification,
                                         userTransactions: blockchainTransaction.UserTransactions,
                                       )(implicit val executionContext: ExecutionContext)
  extends GenericDaoImpl[CancelOrderTransactions.CancelOrderTransactionTable, CancelOrderTransaction, String]() {

  implicit val logger: Logger = Logger(this.getClass)

  implicit val module: String = constants.Module.MASTER_TRANSACTION_CANCEL_ORDER_TRANSACTION

  val tableQuery = new TableQuery(tag => new CancelOrderTransactionTable(tag))

  object Service {

    def addWithNoneStatus(txHash: String, secondaryMarketId: String, sellerId: String): Future[String] = create(CancelOrderTransaction(txHash = txHash, secondaryMarketId = secondaryMarketId, sellerId = sellerId, status = None)).map(_.id)

    def getByTxHash(txHash: String): Future[Seq[CancelOrderTransaction]] = filter(_.txHash === txHash)

    def markSuccess(txHash: String): Future[Int] = customUpdate(tableQuery.filter(_.txHash === txHash).map(_.status).update(true))

    def markFailed(txHash: String): Future[Int] = customUpdate(tableQuery.filter(_.txHash === txHash).map(_.status).update(false))

    def getAllPendingStatus: Future[Seq[CancelOrderTransaction]] = filter(_.status.?.isEmpty)

    def checkAnyPendingTx: Future[Boolean] = filterAndExists(_.status.?.isEmpty)
  }

  object Utility {

    def transaction(secondaryMarket: SecondaryMarket, fromAddress: String, gasPrice: BigDecimal, ecKey: ECKey): Future[BlockchainTransaction] = {
      val latestHeightAccountUnconfirmedTxs = userTransactions.Utility.getLatestHeightAccountAndUnconfirmedTxs(fromAddress)

      def checkMempoolAndAddTx(bcAccount: models.blockchain.Account, latestBlockHeight: Int, unconfirmedTxHashes: Seq[String]) = {
        val timeoutHeight = latestBlockHeight + constants.Transaction.TimeoutHeight
        val (txRawBytes, _) = utilities.BlockchainTransaction.getTxRawBytesWithSignedMemo(
          messages = Seq(utilities.BlockchainTransaction.getCancelOrderMsg(fromAddress = fromAddress, fromID = utilities.Identity.getMantlePlaceIdentityID(secondaryMarket.sellerId), orderID = secondaryMarket.getOrderID)),
          fee = utilities.BlockchainTransaction.getFee(gasPrice = gasPrice, gasLimit = constants.Transaction.DefaultCancelOrderGasLimit),
          gasLimit = constants.Transaction.DefaultCancelOrderGasLimit,
          account = bcAccount,
          ecKey = ecKey,
          timeoutHeight = timeoutHeight)
        val txHash = utilities.Secrets.sha256HashHexString(txRawBytes)

        val checkAndAdd = {
          if (!unconfirmedTxHashes.contains(txHash)) {
            for {
              userTx <- userTransactions.Service.addWithNoneStatus(txHash = txHash, accountId = secondaryMarket.sellerId, fromAddress = fromAddress, timeoutHeight = timeoutHeight, txType = constants.Transaction.User.CANCEL_ORDER)
              _ <- Service.addWithNoneStatus(txHash = txHash, secondaryMarketId = secondaryMarket.id, sellerId = secondaryMarket.sellerId)
            } yield userTx
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
        val cancelOrderTxs = Service.getAllPendingStatus

        def checkAndUpdate(cancelOrderTxs: Seq[CancelOrderTransaction]) = utilitiesOperations.traverse(cancelOrderTxs) { cancelOrderTx =>
          val transaction = userTransactions.Service.tryGet(cancelOrderTx.txHash)

          def onTxComplete(userTx: UserTransaction) = if (userTx.status.isDefined) {
            if (userTx.status.get) {
              val markSuccess = Service.markSuccess(cancelOrderTx.txHash)
              val markOnCancellation = masterSecondaryMarkets.Service.markOnCancellation(cancelOrderTx.secondaryMarketId)
              val sendNotifications = utilitiesNotification.send(cancelOrderTx.sellerId, constants.Notification.CANCEL_ORDER_SUCCESSFUL)("")

              (for {
                _ <- markSuccess
                _ <- markOnCancellation
                _ <- sendNotifications
              } yield ()
                ).recover {
                case exception: Exception => logger.error(exception.getLocalizedMessage)
                  logger.error("[PANIC] Something is seriously wrong with logic. Code should not reach here.")
              }
            } else {
              val markMasterFailed = Service.markFailed(cancelOrderTx.txHash)
              val sendNotifications = utilitiesNotification.send(cancelOrderTx.sellerId, constants.Notification.CANCEL_ORDER_FAILED)("")

              (for {
                _ <- markMasterFailed
                _ <- sendNotifications
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
          cancelOrderTxs <- cancelOrderTxs
          _ <- checkAndUpdate(cancelOrderTxs)
        } yield ()).recover {
          case baseException: BaseException => logger.error(baseException.failure.logMessage)
        }

        Await.result(forComplete, Duration.Inf)
      }
    }
  }

}