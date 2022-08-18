package models.blockchainTransaction

import cosmos.bank.v1beta1.Tx
import models.Trait.Logged
import models.Trait.{Entity2, GenericDaoImpl2, ModelTable2}
import models.common.Coin
import org.bitcoinj.core.ECKey
import play.api.Logger
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.H2Profile.api._

import java.sql.Timestamp
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}
import play.api.libs.json.Json
import transactions.blockchain.BroadcastTxSync
import transactions.responses.blockchain.BroadcastTxSyncResponse

import scala.jdk.CollectionConverters.IterableHasAsJava

case class SendCoin(accountId: String, txHash: String, txRawHex: String, fromAddress: String, toAddress: String, amount: Seq[Coin], broadcasted: Boolean, status: Option[Boolean], log: Option[String], createdBy: Option[String] = None, createdOn: Option[Timestamp] = None, createdOnTimeZone: Option[String] = None, updatedBy: Option[String] = None, updatedOn: Option[Timestamp] = None, updatedOnTimeZone: Option[String] = None) extends Logged {

  def serialize(): SendCoins.SendCoinSerialized = SendCoins.SendCoinSerialized(accountId = this.accountId, txHash = this.txHash, txRawHex = this.txRawHex, fromAddress = this.fromAddress, toAddress = this.toAddress, amount = Json.toJson(this.amount).toString, broadcasted = this.broadcasted, status = this.status, log = this.log, createdBy = this.createdBy, createdOn = this.createdOn, createdOnTimeZone = this.createdOnTimeZone, updatedBy = this.updatedBy, updatedOn = this.updatedOn, updatedOnTimeZone = this.updatedOnTimeZone)

}

object SendCoins {

  private implicit val logger: Logger = Logger(this.getClass)

  private implicit val module: String = constants.Module.BLOCKCHAIN_TRANSACTION_SEND_COIN

  case class SendCoinSerialized(accountId: String, txHash: String, txRawHex: String, fromAddress: String, toAddress: String, amount: String, broadcasted: Boolean, status: Option[Boolean], log: Option[String], createdBy: Option[String], createdOn: Option[Timestamp], createdOnTimeZone: Option[String], updatedBy: Option[String], updatedOn: Option[Timestamp], updatedOnTimeZone: Option[String]) extends Entity2[String, String] {
    def deserialize: SendCoin = SendCoin(accountId = accountId, txHash = txHash, txRawHex = txRawHex, fromAddress = fromAddress, toAddress = toAddress, amount = utilities.JSON.convertJsonStringToObject[Seq[Coin]](amount), broadcasted = broadcasted, status = status, log = log, createdBy = createdBy, createdOn = createdOn, createdOnTimeZone = createdOnTimeZone, updatedBy = updatedBy, updatedOn = updatedOn, updatedOnTimeZone = updatedOnTimeZone)

    def id1: String = accountId

    def id2: String = txHash
  }

  class SendCoinTable(tag: Tag) extends Table[SendCoinSerialized](tag, "SendCoin") with ModelTable2[String, String] {

    def * = (accountId, txHash, txRawHex, fromAddress, toAddress, amount, broadcasted, status.?, log.?, createdBy.?, createdOn.?, createdOnTimeZone.?, updatedBy.?, updatedOn.?, updatedOnTimeZone.?) <> (SendCoinSerialized.tupled, SendCoinSerialized.unapply)

    def accountId = column[String]("accountId", O.PrimaryKey)

    def txHash = column[String]("txHash", O.PrimaryKey)

    def txRawHex = column[String]("txRawHex")

    def fromAddress = column[String]("fromAddress")

    def toAddress = column[String]("toAddress")

    def amount = column[String]("amount")

    def broadcasted = column[Boolean]("broadcasted")

    def status = column[Boolean]("status")

    def log = column[String]("log")

    def createdBy = column[String]("createdBy")

    def createdOn = column[Timestamp]("createdOn")

    def createdOnTimeZone = column[String]("createdOnTimeZone")

    def updatedBy = column[String]("updatedBy")

    def updatedOn = column[Timestamp]("updatedOn")

    def updatedOnTimeZone = column[String]("updatedOnTimeZone")

    def id1 = accountId

    def id2 = txHash
  }

  val TableQuery = new TableQuery(tag => new SendCoinTable(tag))

}

@Singleton
class SendCoins @Inject()(
                           protected val databaseConfigProvider: DatabaseConfigProvider,
                           blockchainAccounts: models.blockchain.Accounts,
                           broadcastTxSync: transactions.blockchain.BroadcastTxSync,
                         )(implicit override val executionContext: ExecutionContext)
  extends GenericDaoImpl2[SendCoins.SendCoinTable, SendCoins.SendCoinSerialized, String, String](
    databaseConfigProvider,
    SendCoins.TableQuery,
    executionContext,
    SendCoins.module,
    SendCoins.logger
  ) {

  object Service {

    def add(accountId: String, txHash: String, txRawHex: String, fromAddress: String, toAddress: String, amount: Seq[Coin], broadcasted: Boolean, status: Option[Boolean]): Future[SendCoin] = {
      val sendCoin = SendCoin(accountId = accountId, txHash = txHash, txRawHex = txRawHex, fromAddress = fromAddress, toAddress = toAddress, amount = amount, broadcasted = broadcasted, status = status, log = None)
      for {
        _ <- create(sendCoin.serialize())
      } yield sendCoin
    }

    def tryGet(accountId: String, txHash: String): Future[SendCoin] = tryGetById(id1 = accountId, id2 = txHash).map(_.deserialize)

    def updateSendCoin(sendCoin: SendCoin): Future[Unit] = update(sendCoin.serialize())

  }

  object Utility {

    def transaction(accountId: String, fromAddress: String, toAddress: String, amount: Seq[Coin], fee: Coin, gasLimit: Int, ecKey: ECKey, memo: String = ""): Future[String] = {
      val bcAccount = blockchainAccounts.Service.tryGet(fromAddress)

      def addTx(bcAccount: models.blockchain.Account) = {
        val (txHash, txRawHex) = utilities.BlockchainTransaction.getTxHashAndRawHex(
          messages = Seq(com.google.protobuf.Any.newBuilder().setTypeUrl(constants.Blockchain.TransactionMessage.SEND_COIN).setValue(Tx.MsgSend.newBuilder().setFromAddress(fromAddress).setToAddress(toAddress).addAllAmount(amount.map(_.toProtoCoin).asJava).build().toByteString).build()),
          fee = fee,
          gasLimit = gasLimit,
          account = bcAccount,
          ecKey = ecKey,
          memo = memo)
        for {
          sendCoin <- Service.add(accountId = accountId, txHash = txHash, txRawHex = txRawHex, fromAddress = fromAddress, toAddress = toAddress, amount = amount, broadcasted = false, status = None)
        } yield sendCoin
      }

      def broadcastTxAndUpdate(sendCoin: SendCoin) = {

        def update(successResponse: Option[BroadcastTxSyncResponse.Response], errorResponse: Option[BroadcastTxSyncResponse.ErrorResponse]) = if (errorResponse.nonEmpty) Service.updateSendCoin(sendCoin.copy(broadcasted = true, status = Option(false), log = Option(errorResponse.get.error.data)))
        else if (successResponse.nonEmpty && successResponse.get.result.code != 0) Service.updateSendCoin(sendCoin.copy(broadcasted = true, status = Option(false), log = Option(successResponse.get.result.log)))
        else Service.updateSendCoin(sendCoin.copy(broadcasted = true))

        for {
          (successResponse, errorResponse) <- broadcastTxSync.Service.get(sendCoin.txRawHex)
          _ <- update(successResponse, errorResponse)
        } yield ()
      }

      for {
        bcAccount <- bcAccount
        sendCoin <- addTx(bcAccount)
        _ <- broadcastTxAndUpdate(sendCoin)
      } yield sendCoin.txHash
    }

  }

}