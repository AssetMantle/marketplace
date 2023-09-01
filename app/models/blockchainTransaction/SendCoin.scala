package models.blockchainTransaction

import models.blockchain.Transaction
import models.blockchainTransaction.SendCoins.SendCoinTable
import models.common.Coin
import models.masterTransaction.{SendCoinTransaction, SendCoinTransactions}
import models.traits._
import play.api.Logger
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.json.Json
import slick.jdbc.H2Profile.api._

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

case class SendCoin(accountId: String, txHash: String, fromAddress: String, toAddress: String, amount: Seq[Coin], status: Option[Boolean], memo: Option[String], timeoutHeight: Int, log: Option[String], createdBy: Option[String] = None, createdOnMillisEpoch: Option[Long] = None, updatedBy: Option[String] = None, updatedOnMillisEpoch: Option[Long] = None) extends Logging with BlockchainTransaction {

  def serialize(): SendCoins.SendCoinSerialized = SendCoins.SendCoinSerialized(accountId = this.accountId, txHash = this.txHash, fromAddress = this.fromAddress, toAddress = this.toAddress, amount = Json.toJson(this.amount).toString, status = this.status, memo = this.memo, timeoutHeight = this.timeoutHeight, log = this.log, createdBy = this.createdBy, createdOnMillisEpoch = this.createdOnMillisEpoch, updatedBy = this.updatedBy, updatedOnMillisEpoch = this.updatedOnMillisEpoch)

  val txHeight: Option[Int] = None
}

private[blockchainTransaction] object SendCoins {

  case class SendCoinSerialized(accountId: String, txHash: String, fromAddress: String, toAddress: String, amount: String, status: Option[Boolean], memo: Option[String], timeoutHeight: Int, log: Option[String], createdBy: Option[String], createdOnMillisEpoch: Option[Long], updatedBy: Option[String], updatedOnMillisEpoch: Option[Long]) extends Entity2[String, String] {
    def deserialize()(implicit module: String, logger: Logger): SendCoin = SendCoin(accountId = accountId, txHash = txHash, fromAddress = fromAddress, toAddress = toAddress, amount = utilities.JSON.convertJsonStringToObject[Seq[Coin]](amount), status = status, memo = memo, timeoutHeight = timeoutHeight, log = log, createdBy = createdBy, createdOnMillisEpoch = createdOnMillisEpoch, updatedBy = updatedBy, updatedOnMillisEpoch = updatedOnMillisEpoch)

    def id1: String = accountId

    def id2: String = txHash
  }

  class SendCoinTable(tag: Tag) extends Table[SendCoinSerialized](tag, "SendCoin") with ModelTable2[String, String] {

    def * = (accountId, txHash, fromAddress, toAddress, amount, status.?, memo.?, timeoutHeight, log.?, createdBy.?, createdOnMillisEpoch.?, updatedBy.?, updatedOnMillisEpoch.?) <> (SendCoinSerialized.tupled, SendCoinSerialized.unapply)

    def accountId = column[String]("accountId", O.PrimaryKey)

    def txHash = column[String]("txHash", O.PrimaryKey)

    def fromAddress = column[String]("fromAddress")

    def toAddress = column[String]("toAddress")

    def amount = column[String]("amount")

    def status = column[Boolean]("status")

    def memo = column[String]("memo")

    def timeoutHeight = column[Int]("timeoutHeight")

    def log = column[String]("log")

    def createdBy = column[String]("createdBy")

    def createdOnMillisEpoch = column[Long]("createdOnMillisEpoch")

    def updatedBy = column[String]("updatedBy")

    def updatedOnMillisEpoch = column[Long]("updatedOnMillisEpoch")

    def id1 = accountId

    def id2 = txHash
  }

}

@Singleton
class SendCoins @Inject()(
                           protected val dbConfigProvider: DatabaseConfigProvider,
                           blockchainTransactions: models.blockchain.Transactions,
                           utilitiesOperations: utilities.Operations,
                           sendCoinTransactions: SendCoinTransactions,
                           userTransactions: UserTransactions,
                         )(implicit val executionContext: ExecutionContext)
  extends GenericDaoImpl2[SendCoins.SendCoinTable, SendCoins.SendCoinSerialized, String, String]() {

  implicit val logger: Logger = Logger(this.getClass)

  implicit val module: String = constants.Module.BLOCKCHAIN_TRANSACTION_SEND_COIN

  val tableQuery = new TableQuery(tag => new SendCoinTable(tag))

  object Service {

    def fetchAll: Future[Seq[SendCoin]] = getAll.map(_.map(_.deserialize))
  }

  object Utility {

    def migrate: Future[Unit] = {
      val allSendCoinTxs = Service.fetchAll

      def bcTxs(txHashes: Seq[String]) = blockchainTransactions.Utility.getByHashes(txHashes)

      def update(allSendCoinTxs: Seq[SendCoin], txs: Seq[Transaction]) = {
        val updateUserTx = userTransactions.Service.add(allSendCoinTxs.map(sendCoinTx => UserTransaction(txHash = sendCoinTx.txHash, accountId = sendCoinTx.accountId, fromAddress = sendCoinTx.fromAddress, status = sendCoinTx.status, timeoutHeight = sendCoinTx.timeoutHeight, log = sendCoinTx.log, txHeight = txs.find(_.hash == sendCoinTx.txHash).map(_.height), txType = constants.Transaction.User.SEND_COIN, createdBy = sendCoinTx.createdBy, createdOnMillisEpoch = sendCoinTx.createdOnMillisEpoch, updatedBy = sendCoinTx.updatedBy, updatedOnMillisEpoch = sendCoinTx.updatedOnMillisEpoch)))

        def updateMaster() = sendCoinTransactions.Service.add(allSendCoinTxs.map(sendCoinTx => SendCoinTransaction(txHash = sendCoinTx.txHash, fromAccountId = sendCoinTx.accountId, toAddress = sendCoinTx.toAddress, amount = sendCoinTx.amount, toAccountId = None, status = sendCoinTx.status, createdBy = sendCoinTx.createdBy, createdOnMillisEpoch = sendCoinTx.createdOnMillisEpoch, updatedBy = sendCoinTx.updatedBy, updatedOnMillisEpoch = sendCoinTx.updatedOnMillisEpoch)))

        for {
          _ <- updateUserTx
          _ <- updateMaster()
        } yield ()

      }


      (for {
        allSendCoinTxs <- allSendCoinTxs
        bcTxs <- bcTxs(allSendCoinTxs.map(_.txHash))
        _ <- update(allSendCoinTxs, bcTxs)
      } yield ()
        ).recover {
        case exception: Exception => logger.error(exception.getLocalizedMessage)
          logger.error("MIGRATION_SEND_COIN_FAILED")
      }
    }

  }

}