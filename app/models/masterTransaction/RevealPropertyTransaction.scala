package models.masterTransaction

import constants.Scheduler
import constants.Transaction.TxUtil
import exceptions.BaseException
import models.blockchain
import models.blockchainTransaction.{UserTransaction, UserTransactions}
import models.traits._
import org.bitcoinj.core.ECKey
import play.api.Logger
import play.api.db.slick.DatabaseConfigProvider
import schema.data.Data
import slick.jdbc.H2Profile.api._

import javax.inject.{Inject, Singleton}
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}

case class RevealPropertyTransaction(txHash: String, accountId: String, data: String, status: Option[Boolean], createdBy: Option[String] = None, createdOnMillisEpoch: Option[Long] = None, updatedBy: Option[String] = None, updatedOnMillisEpoch: Option[Long] = None) extends Logging with Entity[String] {
  def id: String = txHash
}

private[masterTransaction] object RevealPropertyTransactions {
  class RevealPropertyTransactionTable(tag: Tag) extends Table[RevealPropertyTransaction](tag, "RevealPropertyTransaction") with ModelTable[String] {

    def * = (txHash, accountId, data, status.?, createdBy.?, createdOnMillisEpoch.?, updatedBy.?, updatedOnMillisEpoch.?) <> (RevealPropertyTransaction.tupled, RevealPropertyTransaction.unapply)

    def txHash = column[String]("txHash", O.PrimaryKey)

    def accountId = column[String]("accountId")

    def data = column[String]("data")

    def status = column[Boolean]("status")

    def createdBy = column[String]("createdBy")

    def createdOnMillisEpoch = column[Long]("createdOnMillisEpoch")

    def updatedBy = column[String]("updatedBy")

    def updatedOnMillisEpoch = column[Long]("updatedOnMillisEpoch")

    def id = txHash

  }

}

@Singleton
class RevealPropertyTransactions @Inject()(
                                            protected val dbConfigProvider: DatabaseConfigProvider,
                                            blockchainIdentities: blockchain.Identities,
                                            utilitiesOperations: utilities.Operations,
                                            utilitiesTransaction: utilities.Transaction,
                                            utilitiesNotification: utilities.Notification,
                                            userTransactions: UserTransactions,
                                          )(implicit val executionContext: ExecutionContext)
  extends GenericDaoImpl[RevealPropertyTransactions.RevealPropertyTransactionTable, RevealPropertyTransaction, String]() {

  implicit val logger: Logger = Logger(this.getClass)

  implicit val module: String = constants.Module.MASTER_TRANSACTION_REVEAL_PROPERTY_TRANSACTION

  val tableQuery = new TableQuery(tag => new RevealPropertyTransactions.RevealPropertyTransactionTable(tag))

  object Service {

    def addWithNoneStatus(txHash: String, accountId: String, data: Data): Future[String] = create(RevealPropertyTransaction(txHash = txHash, accountId = accountId, data = data.viewString, status = None)).map(_.id)

    def getByTxHash(txHash: String): Future[Seq[RevealPropertyTransaction]] = filter(_.txHash === txHash)

    def markSuccess(txHash: String): Future[Int] = customUpdate(tableQuery.filter(_.txHash === txHash).map(_.status).update(true))

    def markFailed(txHash: String): Future[Int] = customUpdate(tableQuery.filter(_.txHash === txHash).map(_.status).update(false))

    def getAllPendingStatus: Future[Seq[RevealPropertyTransaction]] = filter(_.status.?.isEmpty)

    def checkAnyPendingTx: Future[Boolean] = filterAndExists(_.status.?.isEmpty)
  }

  object Utility {

    implicit val txUtil: TxUtil = TxUtil("REVEAL_PROPERTY", 100000)

    def transaction(fromAddress: String, accountId: String, data: Data, gasPrice: BigDecimal, ecKey: ECKey): Future[UserTransaction] = {
      val messages = Seq(utilities.BlockchainTransaction.getRevealMsg(fromAddress = fromAddress, data = data))

      def masterTxFunc(txHash: String) = Service.addWithNoneStatus(txHash = txHash, accountId = accountId, data = data)

      val userTx = utilitiesTransaction.doUserTx(messages = messages, gasPrice = gasPrice, accountId = accountId, fromAddress = fromAddress, ecKey = ecKey, masterTxFunction = masterTxFunc)

      for {
        (userTx, _) <- userTx
      } yield userTx
    }

    val scheduler: Scheduler = new Scheduler {
      val name: String = module

      def runner(): Unit = {
        val revealPropertyTxs = Service.getAllPendingStatus

        def checkAndUpdate(revealPropertyTxs: Seq[RevealPropertyTransaction]) = utilitiesOperations.traverse(revealPropertyTxs) { revealPropertyTx =>
          val transaction = userTransactions.Service.tryGet(revealPropertyTx.txHash)

          def onTxComplete(userTransaction: UserTransaction) = if (userTransaction.status.isDefined) {
            if (userTransaction.status.get) {
              val markSuccess = Service.markSuccess(revealPropertyTx.txHash)
              val sendNotification = utilitiesNotification.send(accountID = revealPropertyTx.accountId, notification = constants.Notification.PROPERTY_REVEALED_SUCCESSFULLY, revealPropertyTx.data)("")

              for {
                _ <- markSuccess
                _ <- sendNotification
              } yield ()
            } else {
              val markMasterFailed = Service.markFailed(revealPropertyTx.txHash)
              val sendNotification = utilitiesNotification.send(accountID = revealPropertyTx.accountId, notification = constants.Notification.PROPERTY_REVEALED_FAILED, revealPropertyTx.data)("")

              for {
                _ <- markMasterFailed
                _ <- sendNotification
              } yield ()
            }
          } else Future()

          for {
            transaction <- transaction
            _ <- onTxComplete(transaction)
          } yield ()

        }

        val forComplete = (for {
          revealPropertyTxs <- revealPropertyTxs
          _ <- checkAndUpdate(revealPropertyTxs)
        } yield ()).recover {
          case baseException: BaseException => baseException.notifyLog("[PANIC]")
            logger.error("[PANIC] Something is seriously wrong with logic. Code should not reach here.")
        }

        Await.result(forComplete, Duration.Inf)
      }
    }
  }

}