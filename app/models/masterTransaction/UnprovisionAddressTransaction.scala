package models.masterTransaction

import constants.Scheduler
import constants.Transaction.TxUtil
import exceptions.BaseException
import models.blockchainTransaction.{UserTransaction, UserTransactions}
import models.master
import models.masterTransaction.UnprovisionAddressTransactions.UnprovisionAddressTransactionTable
import models.traits._
import org.bitcoinj.core.ECKey
import play.api.Logger
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.H2Profile.api._

import javax.inject.{Inject, Singleton}
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}

case class UnprovisionAddressTransaction(txHash: String, accountId: String, toAddress: String, status: Option[Boolean], createdBy: Option[String] = None, createdOnMillisEpoch: Option[Long] = None, updatedBy: Option[String] = None, updatedOnMillisEpoch: Option[Long] = None) extends Logging with Entity[String] {
  def id: String = txHash

}

private[masterTransaction] object UnprovisionAddressTransactions {
  class UnprovisionAddressTransactionTable(tag: Tag) extends Table[UnprovisionAddressTransaction](tag, "UnprovisionAddressTransaction") with ModelTable[String] {

    def * = (txHash, accountId, toAddress, status.?, createdBy.?, createdOnMillisEpoch.?, updatedBy.?, updatedOnMillisEpoch.?) <> (UnprovisionAddressTransaction.tupled, UnprovisionAddressTransaction.unapply)

    def txHash = column[String]("txHash", O.PrimaryKey)

    def accountId = column[String]("accountId")

    def toAddress = column[String]("toAddress")

    def status = column[Boolean]("status")

    def createdBy = column[String]("createdBy")

    def createdOnMillisEpoch = column[Long]("createdOnMillisEpoch")

    def updatedBy = column[String]("updatedBy")

    def updatedOnMillisEpoch = column[Long]("updatedOnMillisEpoch")

    def id = txHash

  }

}

@Singleton
class UnprovisionAddressTransactions @Inject()(
                                                protected val dbConfigProvider: DatabaseConfigProvider,
                                                utilitiesOperations: utilities.Operations,
                                                masterKeys: master.Keys,
                                                utilitiesNotification: utilities.Notification,
                                                utilitiesTransaction: utilities.Transaction,
                                                userTransactions: UserTransactions,
                                              )(implicit val executionContext: ExecutionContext)
  extends GenericDaoImpl[UnprovisionAddressTransactions.UnprovisionAddressTransactionTable, UnprovisionAddressTransaction, String]() {

  implicit val logger: Logger = Logger(this.getClass)

  implicit val module: String = constants.Module.MASTER_TRANSACTION_UNPROVISION_ADDRESS_TRANSACTION

  val tableQuery = new TableQuery(tag => new UnprovisionAddressTransactionTable(tag))

  object Service {

    def addWithNoneStatus(txHash: String, accountId: String, toAddress: String): Future[String] = create(UnprovisionAddressTransaction(txHash = txHash, accountId = accountId, toAddress = toAddress, status = None)).map(_.id)

    def getByTxHash(txHash: String): Future[Seq[UnprovisionAddressTransaction]] = filter(_.txHash === txHash)

    def markSuccess(txHash: String): Future[Int] = customUpdate(tableQuery.filter(_.txHash === txHash).map(_.status).update(true))

    def markFailed(txHash: String): Future[Int] = customUpdate(tableQuery.filter(_.txHash === txHash).map(_.status).update(false))

    def getAllPendingStatus: Future[Seq[UnprovisionAddressTransaction]] = filter(_.status.?.isEmpty)

    def checkAnyPendingTx: Future[Boolean] = filterAndExists(_.status.?.isEmpty)
  }

  object Utility {

    implicit val txUtil: TxUtil = TxUtil("UNPROVISION_ADDRESS", 150000)

    def transaction(fromAddress: String, accountId: String, toAddress: String, gasPrice: BigDecimal, ecKey: ECKey): Future[UserTransaction] = {
      val messages = Seq(utilities.BlockchainTransaction.getUnprovisionMsg(fromAddress = fromAddress, fromID = utilities.Identity.getMantlePlaceIdentityID(accountId), toAddress = toAddress))

      def masterTxFunc(txHash: String) = Service.addWithNoneStatus(txHash = txHash, accountId = accountId, toAddress = toAddress)

      val userTx = utilitiesTransaction.doUserTx(messages = messages, gasPrice = gasPrice, accountId = accountId, fromAddress = fromAddress, ecKey = ecKey, masterTxFunction = masterTxFunc)

      for {
        (userTx, _) <- userTx
      } yield userTx
    }

    val scheduler: Scheduler = new Scheduler {
      val name: String = module

      def runner(): Unit = {
        val unprovisionAddressTxs = Service.getAllPendingStatus

        def checkAndUpdate(unprovisionAddressTxs: Seq[UnprovisionAddressTransaction]) = utilitiesOperations.traverse(unprovisionAddressTxs) { unprovisionAddressTx =>
          val transaction = userTransactions.Service.tryGet(unprovisionAddressTx.txHash)

          def onTxComplete(userTransaction: UserTransaction) = if (userTransaction.status.isDefined) {
            if (userTransaction.status.get) {
              val markSuccess = Service.markSuccess(unprovisionAddressTx.txHash)
              val deletKey = masterKeys.Service.delete(accountId = unprovisionAddressTx.accountId, address = unprovisionAddressTx.toAddress)
              val sendNotification = utilitiesNotification.send(accountID = unprovisionAddressTx.accountId, notification = constants.Notification.ADDRESS_UNPROVISIONED_SUCCESSFULLY, unprovisionAddressTx.toAddress)("")

              for {
                _ <- markSuccess
                _ <- deletKey
                _ <- sendNotification
              } yield ()
            } else {
              val markMasterFailed = Service.markFailed(unprovisionAddressTx.txHash)
              val sendNotification = utilitiesNotification.send(accountID = unprovisionAddressTx.accountId, notification = constants.Notification.ADDRESS_UNPROVISIONED_FAILED, unprovisionAddressTx.toAddress)("")

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
          unprovisionAddressTxs <- unprovisionAddressTxs
          _ <- checkAndUpdate(unprovisionAddressTxs)
        } yield ()).recover {
          case baseException: BaseException => baseException.notifyLog("[PANIC]")
            logger.error("[PANIC] Something is seriously wrong with logic. Code should not reach here.")
        }

        Await.result(forComplete, Duration.Inf)
      }
    }
  }

}