package models.masterTransaction

import constants.Scheduler
import constants.Transaction.TxUtil
import exceptions.BaseException
import models.blockchain
import models.blockchainTransaction.{UserTransaction, UserTransactions}
import models.common.Coin
import models.masterTransaction.UnwrapTransactions.UnwrapTransactionTable
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

case class UnwrapTransaction(txHash: String, accountId: String, assetId: String, amount: BigInt, status: Option[Boolean], createdBy: Option[String] = None, createdOnMillisEpoch: Option[Long] = None, updatedBy: Option[String] = None, updatedOnMillisEpoch: Option[Long] = None) extends Logging {
  def getIdentityID: IdentityID = utilities.Identity.getMantlePlaceIdentityID(this.accountId)

  def getAssetID: AssetID = AssetID(HashID(utilities.Secrets.base64URLDecode(this.assetId)))

  def serialize: UnwrapTransactions.UnwrapTransactionSerialized = UnwrapTransactions.UnwrapTransactionSerialized(
    txHash = this.txHash, accountId = this.accountId, assetId = this.assetId, amount = BigDecimal(this.amount), status = this.status, createdBy = this.createdBy, createdOnMillisEpoch = this.createdOnMillisEpoch, updatedBy = this.updatedBy, updatedOnMillisEpoch = this.updatedOnMillisEpoch
  )
}

private[masterTransaction] object UnwrapTransactions {
  case class UnwrapTransactionSerialized(txHash: String, accountId: String, assetId: String, amount: BigDecimal, status: Option[Boolean], createdBy: Option[String] = None, createdOnMillisEpoch: Option[Long] = None, updatedBy: Option[String] = None, updatedOnMillisEpoch: Option[Long] = None) extends Logging with Entity[String] {
    def id: String = txHash

    def deserialize()(implicit module: String, logger: Logger): UnwrapTransaction = UnwrapTransaction(
      txHash = this.txHash, accountId = this.accountId, assetId = this.assetId, amount = this.amount.toBigInt, status = this.status, createdBy = this.createdBy, createdOnMillisEpoch = this.createdOnMillisEpoch, updatedBy = this.updatedBy, updatedOnMillisEpoch = this.updatedOnMillisEpoch
    )
  }

  class UnwrapTransactionTable(tag: Tag) extends Table[UnwrapTransactionSerialized](tag, "UnwrapTransaction") with ModelTable[String] {

    def * = (txHash, accountId, assetId, amount, status.?, createdBy.?, createdOnMillisEpoch.?, updatedBy.?, updatedOnMillisEpoch.?) <> (UnwrapTransactionSerialized.tupled, UnwrapTransactionSerialized.unapply)

    def txHash = column[String]("txHash", O.PrimaryKey)

    def accountId = column[String]("accountId")

    def assetId = column[String]("assetId")

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
class UnwrapTransactions @Inject()(
                                    protected val dbConfigProvider: DatabaseConfigProvider,
                                    blockchainIdentities: blockchain.Identities,
                                    utilitiesOperations: utilities.Operations,
                                    utilitiesNotification: utilities.Notification,
                                    userTransactions: UserTransactions,
                                    utilitiesTransaction: utilities.Transaction,
                                  )(implicit val executionContext: ExecutionContext)
  extends GenericDaoImpl[UnwrapTransactions.UnwrapTransactionTable, UnwrapTransactions.UnwrapTransactionSerialized, String]() {

  implicit val logger: Logger = Logger(this.getClass)

  implicit val module: String = constants.Module.MASTER_TRANSACTION_UNWRAP_TRANSACTION

  val tableQuery = new TableQuery(tag => new UnwrapTransactionTable(tag))

  object Service {

    def addWithNoneStatus(txHash: String, assetId: AssetID, amount: BigInt, accountId: String): Future[String] = create(UnwrapTransaction(txHash = txHash, assetId = assetId.asString, amount = amount, accountId = accountId, status = None).serialize).map(_.id)

    def getByTxHash(txHash: String): Future[Seq[UnwrapTransaction]] = filter(_.txHash === txHash).map(_.map(_.deserialize))

    def markSuccess(txHash: String): Future[Int] = customUpdate(tableQuery.filter(_.txHash === txHash).map(_.status).update(true))

    def markFailed(txHash: String): Future[Int] = customUpdate(tableQuery.filter(_.txHash === txHash).map(_.status).update(false))

    def getAllPendingStatus: Future[Seq[UnwrapTransaction]] = filter(_.status.?.isEmpty).map(_.map(_.deserialize))

    def checkAnyPendingTx: Future[Boolean] = filterAndExists(_.status.?.isEmpty)
  }

  object Utility {

    implicit val txUtil: TxUtil = TxUtil("UNWRAP", 150000)

    def transaction(fromAddress: String, accountId: String, amount: BigInt, gasPrice: BigDecimal, ecKey: ECKey): Future[BlockchainTransaction] = {
      val messages = Seq(utilities.BlockchainTransaction.getUnwrapTokenMsg(fromAddress = fromAddress, fromID = utilities.Identity.getMantlePlaceIdentityID(accountId), coins = Seq(Coin(denom = constants.Blockchain.StakingToken, amount = MicroNumber(amount)))))

      def masterTxFunc(txHash: String) = Service.addWithNoneStatus(txHash = txHash, assetId = constants.Blockchain.StakingTokenAssetID, amount = amount, accountId = accountId)

      val userTx = utilitiesTransaction.doUserTx(messages = messages, gasPrice = gasPrice, accountId = accountId, fromAddress = fromAddress, ecKey = ecKey, masterTxFunction = masterTxFunc)

      for {
        (userTx, _) <- userTx
      } yield userTx
    }

    private def checkTransactions() = {
      val unwrapTxs = Service.getAllPendingStatus

      def checkAndUpdate(unwrapTxs: Seq[UnwrapTransaction]) = utilitiesOperations.traverse(unwrapTxs) { unwrapTx =>
        val transaction = userTransactions.Service.tryGet(unwrapTx.txHash)

        def onTxComplete(userTransaction: UserTransaction) = if (userTransaction.status.isDefined) {
          if (userTransaction.status.get) {
            val markSuccess = Service.markSuccess(unwrapTx.txHash)
            val sendNotification = {
              val param = s"${unwrapTx.amount / MicroNumber.factor} MNTL"
              utilitiesNotification.send(unwrapTx.accountId, constants.Notification.UNWRAPPED_TOKEN_SUCCESSFULLY, param)()
            }

            for {
              _ <- markSuccess
              _ <- sendNotification
            } yield ()
          } else {
            val markMasterFailed = Service.markFailed(unwrapTx.txHash)
            val sendNotification = {
              utilitiesNotification.send(unwrapTx.accountId, constants.Notification.UNWRAPPED_TOKEN_FAILED, s"${unwrapTx.amount / MicroNumber.factor} MNTL")()
            }

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

      for {
        unwrapTxs <- unwrapTxs
        _ <- checkAndUpdate(unwrapTxs)
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