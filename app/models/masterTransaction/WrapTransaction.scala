package models.masterTransaction

import constants.Scheduler
import constants.Transaction.TxUtil
import exceptions.BaseException
import models.blockchain
import models.blockchainTransaction.{UserTransaction, UserTransactions}
import models.common.Coin
import models.masterTransaction.WrapTransactions.WrapTransactionTable
import models.traits._
import org.bitcoinj.core.ECKey
import play.api.Logger
import play.api.db.slick.DatabaseConfigProvider
import schema.id.base._
import slick.jdbc.H2Profile.api._
import utilities.MicroNumber

import javax.inject.{Inject, Singleton}
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}

case class WrapTransaction(txHash: String, accountId: String, denom: String, amount: BigDecimal, status: Option[Boolean], createdBy: Option[String] = None, createdOnMillisEpoch: Option[Long] = None, updatedBy: Option[String] = None, updatedOnMillisEpoch: Option[Long] = None) extends Logging with Entity[String] {
  def id: String = txHash

  def getIdentityID: IdentityID = utilities.Identity.getMantlePlaceIdentityID(this.accountId)

  def getAssetId: AssetID = schema.document.CoinAsset.getCoinAssetID(this.denom)
}

private[masterTransaction] object WrapTransactions {
  class WrapTransactionTable(tag: Tag) extends Table[WrapTransaction](tag, "WrapTransaction") with ModelTable[String] {

    def * = (txHash, accountId, denom, amount, status.?, createdBy.?, createdOnMillisEpoch.?, updatedBy.?, updatedOnMillisEpoch.?) <> (WrapTransaction.tupled, WrapTransaction.unapply)

    def txHash = column[String]("txHash", O.PrimaryKey)

    def accountId = column[String]("accountId")

    def denom = column[String]("denom")

    def amount = column[BigDecimal]("amount")

    def status = column[Boolean]("status")

    def createdBy = column[String]("createdBy")

    def createdOnMillisEpoch = column[Long]("createdOnMillisEpoch")

    def updatedBy = column[String]("updatedBy")

    def updatedOnMillisEpoch = column[Long]("updatedOnMillisEpoch")

    def id = txHash

  }

}

@Singleton
class WrapTransactions @Inject()(
                                  protected val dbConfigProvider: DatabaseConfigProvider,
                                  blockchainIdentities: blockchain.Identities,
                                  utilitiesOperations: utilities.Operations,
                                  utilitiesNotification: utilities.Notification,
                                  userTransactions: UserTransactions,
                                  utilitiesTransaction: utilities.Transaction,
                                )(implicit val executionContext: ExecutionContext)
  extends GenericDaoImpl[WrapTransactions.WrapTransactionTable, WrapTransaction, String]() {

  implicit val logger: Logger = Logger(this.getClass)

  implicit val module: String = constants.Module.MASTER_TRANSACTION_WRAP_TRANSACTION

  val tableQuery = new TableQuery(tag => new WrapTransactionTable(tag))

  object Service {

    def addWithNoneStatus(txHash: String, denom: String, amount: BigDecimal, accountId: String): Future[String] = create(WrapTransaction(txHash = txHash, denom = denom, amount = amount, accountId = accountId, status = None)).map(_.id)

    def getByTxHash(txHash: String): Future[Seq[WrapTransaction]] = filter(_.txHash === txHash)

    def markSuccess(txHash: String): Future[Int] = customUpdate(tableQuery.filter(_.txHash === txHash).map(_.status).update(true))

    def markFailed(txHash: String): Future[Int] = customUpdate(tableQuery.filter(_.txHash === txHash).map(_.status).update(false))

    def getAllPendingStatus: Future[Seq[WrapTransaction]] = filter(_.status.?.isEmpty)

    def checkAnyPendingTx: Future[Boolean] = filterAndExists(_.status.?.isEmpty)
  }

  object Utility {
    implicit val txUtil: TxUtil = TxUtil("WRAP", 150000)

    def transaction(fromAddress: String, accountId: String, coin: Coin, gasPrice: BigDecimal, ecKey: ECKey): Future[BlockchainTransaction] = {
      val messages = Seq(utilities.BlockchainTransaction.getWrapTokenMsg(fromAddress = fromAddress, fromID = utilities.Identity.getMantlePlaceIdentityID(accountId), coins = Seq(coin)))

      def masterTxFunc(txHash: String) = Service.addWithNoneStatus(txHash = txHash, denom = coin.denom, amount = coin.amount.toMicroBigDecimal, accountId = accountId)

      val userTx = utilitiesTransaction.doUserTx(messages = messages, gasPrice = gasPrice, accountId = accountId, fromAddress = fromAddress, ecKey = ecKey, masterTxFunction = masterTxFunc)

      for {
        (userTx, _) <- userTx
      } yield userTx
    }

    private def checkTransactions() = {
      val wrapTxs = Service.getAllPendingStatus

      def checkAndUpdate(wrapTxs: Seq[WrapTransaction]) = utilitiesOperations.traverse(wrapTxs) { wrapTx =>
        val userTransaction = userTransactions.Service.tryGet(wrapTx.txHash)

        def onTxComplete(userTransaction: UserTransaction) = if (userTransaction.status.isDefined) {
          if (userTransaction.status.get) {
            val markSuccess = Service.markSuccess(wrapTx.txHash)
            val sendNotification = {
              val param = s"${wrapTx.amount / MicroNumber.factor} MNTL"
              utilitiesNotification.send(wrapTx.accountId, constants.Notification.WRAPPED_TOKEN_SUCCESSFULLY, param)()
            }

            for {
              _ <- markSuccess
              _ <- sendNotification
            } yield ()
          } else {
            val markMasterFailed = Service.markFailed(wrapTx.txHash)
            val sendNotification = {
              val param = s"${wrapTx.amount / MicroNumber.factor} MNTL"
              utilitiesNotification.send(wrapTx.accountId, constants.Notification.WRAPPED_TOKEN_FAILED, param)()
            }

            for {
              _ <- markMasterFailed
              _ <- sendNotification
            } yield ()
          }
        } else Future()

        for {
          userTransaction <- userTransaction
          _ <- onTxComplete(userTransaction)
        } yield ()
      }

      for {
        wrapTxs <- wrapTxs
        _ <- checkAndUpdate(wrapTxs)
      } yield ()

    }

    val scheduler: Scheduler = new Scheduler {
      val name: String = module

      //      override val initialDelay: FiniteDuration = constants.Scheduler.FiveMinutes

      def runner(): Unit = {

        val forComplete = (for {
          _ <- checkTransactions()
        } yield ()).recover {
          case baseException: BaseException => baseException.notifyLog("[PANIC]")
            logger.error("[PANIC] Something is seriously wrong with logic. Code should not reach here.")
        }

        Await.result(forComplete, Duration.Inf)
      }
    }
  }
}