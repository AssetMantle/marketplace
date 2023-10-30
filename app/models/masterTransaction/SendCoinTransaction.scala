package models.masterTransaction

import constants.Scheduler
import constants.Transaction.TxUtil
import exceptions.BaseException
import models.blockchainTransaction.{UserTransaction, UserTransactions}
import models.common.Coin
import models.masterTransaction.SendCoinTransactions.SendCoinTransactionTable
import models.traits._
import org.bitcoinj.core.ECKey
import play.api.Logger
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.json.Json
import slick.jdbc.H2Profile.api._

import javax.inject.{Inject, Singleton}
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}

case class SendCoinTransaction(txHash: String, fromAccountId: String, toAddress: String, amount: Seq[Coin], toAccountId: Option[String], status: Option[Boolean], createdBy: Option[String] = None, createdOnMillisEpoch: Option[Long] = None, updatedBy: Option[String] = None, updatedOnMillisEpoch: Option[Long] = None) extends Logging {

  def serialize: SendCoinTransactions.SendCoinTransactionSerialize = SendCoinTransactions.SendCoinTransactionSerialize(
    txHash = this.txHash,
    fromAccountId = this.fromAccountId,
    toAddress = this.toAddress,
    amount = Json.toJson(this.amount).toString,
    toAccountId = this.toAccountId,
    status = this.status,
    createdBy = this.createdBy, createdOnMillisEpoch = this.createdOnMillisEpoch, updatedBy = this.updatedBy, updatedOnMillisEpoch = this.updatedOnMillisEpoch)
}

private[masterTransaction] object SendCoinTransactions {

  case class SendCoinTransactionSerialize(txHash: String, fromAccountId: String, toAddress: String, amount: String, toAccountId: Option[String], status: Option[Boolean], createdBy: Option[String], createdOnMillisEpoch: Option[Long], updatedBy: Option[String], updatedOnMillisEpoch: Option[Long]) extends Entity[String] {
    def deserialize()(implicit module: String, logger: Logger): SendCoinTransaction = SendCoinTransaction(
      txHash = this.txHash,
      fromAccountId = this.fromAccountId,
      toAddress = this.toAddress,
      amount = utilities.JSON.convertJsonStringToObject[Seq[Coin]](amount),
      toAccountId = this.toAccountId,
      status = this.status,
      createdBy = this.createdBy, createdOnMillisEpoch = this.createdOnMillisEpoch, updatedBy = this.updatedBy, updatedOnMillisEpoch = this.updatedOnMillisEpoch)

    def id: String = txHash

  }

  class SendCoinTransactionTable(tag: Tag) extends Table[SendCoinTransactionSerialize](tag, "SendCoinTransaction") with ModelTable[String] {

    def * = (txHash, fromAccountId, toAddress, amount, toAccountId.?, status.?, createdBy.?, createdOnMillisEpoch.?, updatedBy.?, updatedOnMillisEpoch.?) <> (SendCoinTransactionSerialize.tupled, SendCoinTransactionSerialize.unapply)

    def txHash = column[String]("txHash", O.PrimaryKey)

    def fromAccountId = column[String]("fromAccountId")

    def toAddress = column[String]("toAddress")

    def amount = column[String]("amount")

    def toAccountId = column[String]("toAccountId")

    def status = column[Boolean]("status")

    def createdBy = column[String]("createdBy")

    def createdOnMillisEpoch = column[Long]("createdOnMillisEpoch")

    def updatedBy = column[String]("updatedBy")

    def updatedOnMillisEpoch = column[Long]("updatedOnMillisEpoch")

    def id = txHash
  }

}

@Singleton
class SendCoinTransactions @Inject()(
                                      protected val dbConfigProvider: DatabaseConfigProvider,
                                      utilitiesTransaction: utilities.Transaction,
                                      utilitiesOperations: utilities.Operations,
                                      userTransactions: UserTransactions,
                                      utilitiesNotification: utilities.Notification,
                                    )(implicit val executionContext: ExecutionContext)
  extends GenericDaoImpl[SendCoinTransactions.SendCoinTransactionTable, SendCoinTransactions.SendCoinTransactionSerialize, String]() {

  implicit val logger: Logger = Logger(this.getClass)

  implicit val module: String = constants.Module.MASTER_TRANSACTION_SEND_COIN_TRANSACTION

  val tableQuery = new TableQuery(tag => new SendCoinTransactionTable(tag))

  object Service {

    def addWithNoneStatus(txHash: String, fromAccountId: String, toAddress: String, amount: Seq[Coin], toAccountId: Option[String]): Future[String] = create(SendCoinTransaction(txHash = txHash, fromAccountId = fromAccountId, toAddress = toAddress, amount = amount, toAccountId = toAccountId, status = None).serialize).map(_.id)

    def add(sendCoinTransactions: Seq[SendCoinTransaction]): Future[Int] = create(sendCoinTransactions.map(_.serialize))

    def getByTxHash(txHash: String): Future[Seq[SendCoinTransaction]] = filter(_.txHash === txHash).map(_.map(_.deserialize))

    def markSuccess(txHash: String): Future[Int] = customUpdate(tableQuery.filter(_.txHash === txHash).map(_.status).update(true))

    def markFailed(txHash: String): Future[Int] = customUpdate(tableQuery.filter(_.txHash === txHash).map(_.status).update(false))

    def getAllPendingStatus: Future[Seq[SendCoinTransaction]] = filter(_.status.?.isEmpty).map(_.map(_.deserialize))

  }

  object Utility {

    implicit val txUtil: TxUtil = TxUtil("SEND_COIN", 120000)

    def transaction(fromAccountId: String, fromAddress: String, toAddress: String, amount: Seq[Coin], gasPrice: BigDecimal, ecKey: ECKey): Future[UserTransaction] = {
      val messages = Seq(utilities.BlockchainTransaction.getSendCoinMsgAsAny(fromAddress = fromAddress, toAddress = toAddress, amount = amount))

      def masterTxFunc(txHash: String) = Service.addWithNoneStatus(txHash = txHash, fromAccountId = fromAccountId, toAddress = toAddress, toAccountId = None, amount = amount)

      val userTx = utilitiesTransaction.doUserTx(messages = messages, gasPrice = gasPrice, accountId = fromAccountId, fromAddress = fromAddress, ecKey = ecKey, masterTxFunction = masterTxFunc)

      for {
        (userTx, _) <- userTx
      } yield userTx
    }

    val scheduler: Scheduler = new Scheduler {
      val name: String = module

      def runner(): Unit = {
        val sendCoins = Service.getAllPendingStatus

        def checkAndUpdate(sendCoinTxs: Seq[SendCoinTransaction]) = utilitiesOperations.traverse(sendCoinTxs) { sendCoinTx =>
          val transaction = userTransactions.Service.tryGet(sendCoinTx.txHash)

          def onTxComplete(userTransaction: UserTransaction) = if (userTransaction.status.isDefined) {
            if (userTransaction.status.get) {
              val markSuccess = Service.markSuccess(userTransaction.txHash)
              val senderNotification = utilitiesNotification.send(sendCoinTx.fromAccountId, constants.Notification.SEND_COIN_FROM_ACCOUNT, sendCoinTx.amount.map(_.getAmountWithNormalizedDenom()).mkString(", "), sendCoinTx.toAccountId.getOrElse(sendCoinTx.toAddress))()
              val receiverNotification = if (sendCoinTx.toAccountId.isDefined) utilitiesNotification.send(sendCoinTx.toAccountId.get, constants.Notification.SEND_COIN_TO_ACCOUNT, sendCoinTx.amount.map(_.getAmountWithNormalizedDenom()).mkString(", "), sendCoinTx.fromAccountId)() else Future()

              for {
                _ <- markSuccess
                _ <- senderNotification
                _ <- receiverNotification
              } yield ()
            } else {
              val markFailed = Service.markFailed(userTransaction.txHash)
              val senderNotification = utilitiesNotification.send(sendCoinTx.fromAccountId, constants.Notification.SEND_COIN_FROM_ACCOUNT_FAILED, sendCoinTx.amount.map(_.getAmountWithNormalizedDenom()).mkString(", "), sendCoinTx.toAccountId.getOrElse(sendCoinTx.toAddress))()
              for {
                _ <- markFailed
                _ <- senderNotification
              } yield ()
            }

          } else Future()

          for {
            transaction <- transaction
            _ <- onTxComplete(transaction)
          } yield ()
        }


        val forComplete = (for {
          sendCoins <- sendCoins
          _ <- checkAndUpdate(sendCoins)
        } yield ()).recover {
          case baseException: BaseException => baseException.notifyLog("[PANIC]")
            logger.error("[PANIC] Something is seriously wrong with logic. Code should not reach here.")
        }

        Await.result(forComplete, Duration.Inf)
      }
    }

  }

}