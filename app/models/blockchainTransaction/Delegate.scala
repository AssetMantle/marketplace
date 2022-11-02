package models.blockchainTransaction

import akka.actor.Cancellable
import cosmos.bank.v1beta1.Tx
import exceptions.BaseException
import models.Trait.Logged
import models.Trait.{Entity2, GenericDaoImpl2, ModelTable2}
import models.common.Coin
import org.bitcoinj.core.ECKey
import play.api.Logger
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.H2Profile.api._

import java.sql.Timestamp
import javax.inject.{Inject, Singleton}
import scala.concurrent.{Await, ExecutionContext, Future}
import play.api.libs.json.Json
import transactions.blockchain.BroadcastTxSync
import transactions.responses.blockchain.BroadcastTxSyncResponse
import utilities.MicroNumber
import scala.concurrent.duration.Duration
import scala.jdk.CollectionConverters.IterableHasAsJava

case class Delegate(accountId: String, txHash: String, txRawHex: String, delegateAddress: String, validatorAddress: String, amount: Seq[Coin], broadcasted: Boolean, status: Option[Boolean], log: Option[String], createdBy: Option[String] = None, createdOn: Option[Timestamp] = None, createdOnTimeZone: Option[String] = None, updatedBy: Option[String] = None, updatedOn: Option[Timestamp] = None, updatedOnTimeZone: Option[String] = None) extends Logged {

  def serialize(): Delegate.DelegateSerialized = Delegate.DelegateSerialized(accountId = this.accountId, txHash = this.txHash, txRawHex = this.txRawHex, delegateAddress = this.delegateAddress, validatorAddress = this.validatorAddress, amount = Json.toJson(this.amount).toString, broadcasted = this.broadcasted, status = this.status, log = this.log, createdBy = this.createdBy, createdOn = this.createdOn, createdOnTimeZone = this.createdOnTimeZone, updatedBy = this.updatedBy, updatedOn = this.updatedOn, updatedOnTimeZone = this.updatedOnTimeZone)

}

object Delegate {

  private implicit val logger: Logger = Logger(this.getClass)

  private implicit val module: String = constants.Module.BLOCKCHAIN_TRANSACTION_SEND_COIN

  case class DelegateSerialized(accountId: String, txHash: String, txRawHex: String, delegateAddress: String, validatorAddress: String, amount: String, broadcasted: Boolean, status: Option[Boolean], log: Option[String], createdBy: Option[String], createdOn: Option[Timestamp], createdOnTimeZone: Option[String], updatedBy: Option[String], updatedOn: Option[Timestamp], updatedOnTimeZone: Option[String]) extends Entity2[String, String] {
    def deserialize: SendCoin = SendCoin(accountId = accountId, txHash = txHash, txRawHex = txRawHex, delegateAddress = delegateAddress, validatorAddress = validatorAddress, amount = utilities.JSON.convertJsonStringToObject[Seq[Coin]](amount), broadcasted = broadcasted, status = status, log = log, createdBy = createdBy, createdOn = createdOn, createdOnTimeZone = createdOnTimeZone, updatedBy = updatedBy, updatedOn = updatedOn, updatedOnTimeZone = updatedOnTimeZone)

    def id1: String = accountId

    def id2: String = txHash
  }

  class DelegateTable(tag: Tag) extends Table[DelegateSerialized](tag, "Delegate") with ModelTable2[String, String] {

    def * = (accountId, txHash, txRawHex, delegateAddress, validatorAddress, amount, broadcasted, status.?, log.?, createdBy.?, createdOn.?, createdOnTimeZone.?, updatedBy.?, updatedOn.?, updatedOnTimeZone.?) <> (SendCoinSerialized.tupled, SendCoinSerialized.unapply)

    def accountId = column[String]("accountId", O.PrimaryKey)

    def txHash = column[String]("txHash", O.PrimaryKey)

    def txRawHex = column[String]("txRawHex")

    def delegateAddress = column[String]("fromAddress")

    def validatorAddress = column[String]("toAddress")

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
class Delegate @Inject()(
                           protected val databaseConfigProvider: DatabaseConfigProvider,
                           blockchainAccounts: models.blockchain.Accounts,
                           blockchainTransactions: models.blockchain.Transactions,
                           broadcastTxSync: transactions.blockchain.BroadcastTxSync,
                           utilitiesOperations: utilities.Operations,
                           getUnconfirmedTxs: queries.blockchain.GetUnconfirmedTxs,
                           getAccount: queries.blockchain.GetAccount,
                         )(implicit override val executionContext: ExecutionContext)
  extends GenericDaoImpl2[SendCoins.SendCoinTable, SendCoins.SendCoinSerialized, String, String](
    databaseConfigProvider,
    SendCoins.TableQuery,
    executionContext,
    SendCoins.module,
    SendCoins.logger
  )
