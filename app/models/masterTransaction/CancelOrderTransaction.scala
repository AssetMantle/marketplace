package models.masterTransaction

import constants.Scheduler
import constants.Transaction.TxUtil
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
                                         utilitiesTransaction: utilities.Transaction,
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

    implicit val txUtil: TxUtil = TxUtil("CANCEL_ORDER", 150000)

    def transaction(secondaryMarket: SecondaryMarket, fromAddress: String, gasPrice: BigDecimal, ecKey: ECKey): Future[BlockchainTransaction] = {
      val messages = Seq(utilities.BlockchainTransaction.getCancelOrderMsg(fromAddress = fromAddress, fromID = utilities.Identity.getMantlePlaceIdentityID(secondaryMarket.sellerId), orderID = secondaryMarket.getOrderID))

      def masterTxFunc(txHash: String) = Service.addWithNoneStatus(txHash = txHash, secondaryMarketId = secondaryMarket.id, sellerId = secondaryMarket.sellerId)

      val userTx = utilitiesTransaction.doUserTx(messages = messages, gasPrice = gasPrice, accountId = secondaryMarket.sellerId, fromAddress = fromAddress, ecKey = ecKey, masterTxFunction = masterTxFunc)

      for {
        (userTx, _) <- userTx
      } yield userTx
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

              for {
                _ <- markSuccess
                _ <- markOnCancellation
                _ <- sendNotifications
              } yield ()

            } else {
              val markMasterFailed = Service.markFailed(cancelOrderTx.txHash)
              val sendNotifications = utilitiesNotification.send(cancelOrderTx.sellerId, constants.Notification.CANCEL_ORDER_FAILED)("")

              for {
                _ <- markMasterFailed
                _ <- sendNotifications
              } yield ()
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
          case baseException: BaseException => baseException.notifyLog("[PANIC]")
            logger.error("[PANIC] Something is seriously wrong with logic. Code should not reach here.")
        }

        Await.result(forComplete, Duration.Inf)
      }
    }
  }

}