package models.masterTransaction

import constants.Scheduler
import constants.Transaction.TxUtil
import exceptions.BaseException
import models.blockchainTransaction.{UserTransaction, UserTransactions}
import models.masterTransaction.ProvisionAddressTransactions.ProvisionAddressTransactionTable
import models.traits._
import models.{blockchain, master}
import org.bitcoinj.core.ECKey
import play.api.Logger
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.H2Profile.api._

import javax.inject.{Inject, Singleton}
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}

case class ProvisionAddressTransaction(txHash: String, accountId: String, toAddress: String, status: Option[Boolean], createdBy: Option[String] = None, createdOnMillisEpoch: Option[Long] = None, updatedBy: Option[String] = None, updatedOnMillisEpoch: Option[Long] = None) extends Logging with Entity[String] {
  def id: String = txHash

}

private[masterTransaction] object ProvisionAddressTransactions {
  class ProvisionAddressTransactionTable(tag: Tag) extends Table[ProvisionAddressTransaction](tag, "ProvisionAddressTransaction") with ModelTable[String] {

    def * = (txHash, accountId, toAddress, status.?, createdBy.?, createdOnMillisEpoch.?, updatedBy.?, updatedOnMillisEpoch.?) <> (ProvisionAddressTransaction.tupled, ProvisionAddressTransaction.unapply)

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
class ProvisionAddressTransactions @Inject()(
                                              protected val dbConfigProvider: DatabaseConfigProvider,
                                              blockchainIdentities: blockchain.Identities,
                                              utilitiesOperations: utilities.Operations,
                                              utilitiesTransaction: utilities.Transaction,
                                              masterKeys: master.Keys,
                                              utilitiesNotification: utilities.Notification,
                                              userTransactions: UserTransactions,
                                            )(implicit val executionContext: ExecutionContext)
  extends GenericDaoImpl[ProvisionAddressTransactions.ProvisionAddressTransactionTable, ProvisionAddressTransaction, String]() {

  implicit val logger: Logger = Logger(this.getClass)

  implicit val module: String = constants.Module.MASTER_TRANSACTION_PROVISION_ADDRESS_TRANSACTION

  val tableQuery = new TableQuery(tag => new ProvisionAddressTransactionTable(tag))

  object Service {

    def addWithNoneStatus(txHash: String, accountId: String, toAddress: String): Future[String] = create(ProvisionAddressTransaction(txHash = txHash, accountId = accountId, toAddress = toAddress, status = None)).map(_.id)

    def getByTxHash(txHash: String): Future[Seq[ProvisionAddressTransaction]] = filter(_.txHash === txHash)

    def markSuccess(txHash: String): Future[Int] = customUpdate(tableQuery.filter(_.txHash === txHash).map(_.status).update(true))

    def markFailed(txHash: String): Future[Int] = customUpdate(tableQuery.filter(_.txHash === txHash).map(_.status).update(false))

    def getAllPendingStatus: Future[Seq[ProvisionAddressTransaction]] = filter(_.status.?.isEmpty)

    def checkAnyPendingTx: Future[Boolean] = filterAndExists(_.status.?.isEmpty)
  }

  object Utility {

    implicit val txUtil: TxUtil = TxUtil("PROVISION_ADDRESS", 150000)

    def transaction(fromAddress: String, accountId: String, toAddress: String, gasPrice: BigDecimal, ecKey: ECKey): Future[BlockchainTransaction] = {
      val messages = Seq(utilities.BlockchainTransaction.getProvisionMsg(fromAddress = fromAddress, fromID = utilities.Identity.getMantlePlaceIdentityID(accountId), toAddress = toAddress))

      def masterTxFunc(txHash: String) = Service.addWithNoneStatus(txHash = txHash, accountId = accountId, toAddress = toAddress)

      val userTx = utilitiesTransaction.doUserTx(messages = messages, gasPrice = gasPrice, accountId = accountId, fromAddress = fromAddress, ecKey = ecKey, masterTxFunction = masterTxFunc)

      for {
        (userTx, _) <- userTx
      } yield userTx
    }

    val scheduler: Scheduler = new Scheduler {
      val name: String = module

      def runner(): Unit = {
        val provisionAddressTxs = Service.getAllPendingStatus

        def checkAndUpdate(provisionAddressTxs: Seq[ProvisionAddressTransaction]) = utilitiesOperations.traverse(provisionAddressTxs) { provisionAddressTx =>
          val transaction = userTransactions.Service.tryGet(provisionAddressTx.txHash)

          def onTxComplete(userTransaction: UserTransaction) = if (userTransaction.status.isDefined) {
            if (userTransaction.status.get) {
              val markSuccess = Service.markSuccess(provisionAddressTx.txHash)
              val updateMaster = masterKeys.Service.markAddressProvisioned(accountId = provisionAddressTx.accountId, address = provisionAddressTx.toAddress)
              val sendNotification = utilitiesNotification.send(accountID = provisionAddressTx.accountId, notification = constants.Notification.ADDRESS_PROVISIONED_SUCCESSFULLY, provisionAddressTx.toAddress)("")

              for {
                _ <- markSuccess
                _ <- updateMaster
                _ <- sendNotification
              } yield ()
            } else {
              val markMasterFailed = Service.markFailed(provisionAddressTx.txHash)
              val updateMaster = masterKeys.Service.markAddressProvisionFailed(accountId = provisionAddressTx.accountId, address = provisionAddressTx.toAddress)
              val sendNotification = utilitiesNotification.send(accountID = provisionAddressTx.accountId, notification = constants.Notification.ADDRESS_PROVISIONED_FAILED, provisionAddressTx.toAddress)("")

              for {
                _ <- markMasterFailed
                _ <- updateMaster
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
          provisionAddressTxs <- provisionAddressTxs
          _ <- checkAndUpdate(provisionAddressTxs)
        } yield ()).recover {
          case baseException: BaseException => baseException.notifyLog("[PANIC]")
            logger.error("[PANIC] Something is seriously wrong with logic. Code should not reach here.")
        }

        Await.result(forComplete, Duration.Inf)
      }
    }
  }

}