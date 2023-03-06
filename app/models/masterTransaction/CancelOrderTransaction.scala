package models.masterTransaction

import constants.Scheduler
import exceptions.BaseException
import models.traits._
import models.blockchainTransaction.CancelOrder
import models.{blockchain, blockchainTransaction, master}
import play.api.Logger
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.H2Profile.api._

import javax.inject.{Inject, Singleton}
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}

case class CancelOrderTransaction(txHash: String, orderId: String, sellerId: String, status: Option[Boolean], createdBy: Option[String] = None, createdOnMillisEpoch: Option[Long] = None, updatedBy: Option[String] = None, updatedOnMillisEpoch: Option[Long] = None) extends Logging with Entity[String] {
  def id: String = txHash

}

object CancelOrderTransactions {

  private implicit val logger: Logger = Logger(this.getClass)

  private implicit val module: String = constants.Module.MASTER_TRANSACTION_CANCEL_ORDER_TRANSACTION

  class CancelOrderTransactionTable(tag: Tag) extends Table[CancelOrderTransaction](tag, "CancelOrderTransaction") with ModelTable[String] {

    def * = (txHash, orderId, sellerId, status.?, createdBy.?, createdOnMillisEpoch.?, updatedBy.?, updatedOnMillisEpoch.?) <> (CancelOrderTransaction.tupled, CancelOrderTransaction.unapply)

    def txHash = column[String]("txHash", O.PrimaryKey)

    def orderId = column[String]("orderId", O.PrimaryKey)

    def sellerId = column[String]("sellerId")

    def status = column[Boolean]("status")

    def createdBy = column[String]("createdBy")

    def createdOnMillisEpoch = column[Long]("createdOnMillisEpoch")

    def updatedBy = column[String]("updatedBy")

    def updatedOnMillisEpoch = column[Long]("updatedOnMillisEpoch")

    def id = txHash

  }

  val TableQuery = new TableQuery(tag => new CancelOrderTransactionTable(tag))

}

@Singleton
class CancelOrderTransactions @Inject()(
                                         protected val databaseConfigProvider: DatabaseConfigProvider,
                                         blockchainAccounts: blockchain.Accounts,
                                         blockchainIdentities: blockchain.Identities,
                                         broadcastTxSync: transactions.blockchain.BroadcastTxSync,
                                         masterSecondaryMarkets: master.SecondaryMarkets,
                                         utilitiesOperations: utilities.Operations,
                                         getUnconfirmedTxs: queries.blockchain.GetUnconfirmedTxs,
                                         getAccount: queries.blockchain.GetAccount,
                                         getAbciInfo: queries.blockchain.GetABCIInfo,
                                         utilitiesNotification: utilities.Notification,
                                         blockchainTransactionCancelOrders: blockchainTransaction.CancelOrders,
                                       )(implicit override val executionContext: ExecutionContext)
  extends GenericDaoImpl[CancelOrderTransactions.CancelOrderTransactionTable, CancelOrderTransaction, String](
    databaseConfigProvider,
    CancelOrderTransactions.TableQuery,
    executionContext,
    CancelOrderTransactions.module,
    CancelOrderTransactions.logger
  ) {

  object Service {

    def addWithNoneStatus(txHash: String, orderId: String, sellerId: String): Future[String] = create(CancelOrderTransaction(txHash = txHash, orderId = orderId, sellerId = sellerId, status = None))

    def getByTxHash(txHash: String): Future[Seq[CancelOrderTransaction]] = filter(_.txHash === txHash)

    def markSuccess(txHash: String): Future[Int] = customUpdate(CancelOrderTransactions.TableQuery.filter(_.txHash === txHash).map(_.status).update(true))

    def markFailed(txHash: String): Future[Int] = customUpdate(CancelOrderTransactions.TableQuery.filter(_.txHash === txHash).map(_.status).update(false))

    def getAllPendingStatus: Future[Seq[CancelOrderTransaction]] = filter(_.status.?.isEmpty)

    def checkAnyPendingTx: Future[Boolean] = filterAndExists(_.status.?.isEmpty)

    def getWithStatusTrueOrPending(orderIds: Seq[String]): Future[Seq[String]] = filter(x => x.orderId.inSet(orderIds) && (x.status || x.status.?.isEmpty)).map(_.map(_.orderId))
  }

  object Utility {
    val scheduler: Scheduler = new Scheduler {
      val name: String = CancelOrderTransactions.module

      def runner(): Unit = {
        val cancelOrderTxs = Service.getAllPendingStatus

        def checkAndUpdate(cancelOrderTxs: Seq[CancelOrderTransaction]) = utilitiesOperations.traverse(cancelOrderTxs) { cancelOrderTx =>
          val transaction = blockchainTransactionCancelOrders.Service.tryGet(cancelOrderTx.txHash)

          def onTxComplete(transaction: CancelOrder) = if (transaction.status.isDefined) {
            if (transaction.status.get) {
              val markSuccess = Service.markSuccess(cancelOrderTx.txHash)
              val markForDeletion = masterSecondaryMarkets.Service.markForDeletion(cancelOrderTx.orderId)
              val sendNotifications = utilitiesNotification.send(cancelOrderTx.sellerId, constants.Notification.CANCEL_ORDER_SUCCESSFUL)("")

              (for {
                _ <- markSuccess
                _ <- markForDeletion
                _ <- sendNotifications
              } yield ()
                ).recover {
                case _: BaseException => logger.error("[PANIC] Something is seriously wrong with logic. Code should not reach here.")
              }
            } else {
              val markMasterFailed = Service.markFailed(cancelOrderTx.txHash)
              val sendNotifications = utilitiesNotification.send(cancelOrderTx.sellerId, constants.Notification.CANCEL_ORDER_FAILED)("")

              (for {
                _ <- markMasterFailed
                _ <- sendNotifications
              } yield ()
                ).recover {
                case _: BaseException => logger.error("[PANIC] Something is seriously wrong with logic. Code should not reach here.")
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