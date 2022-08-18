package models.blockchainTransaction

import models.Trait.Logged
import models.Trait.{Entity2, GenericDaoImpl2, ModelTable2}
import models.common.Coin
import play.api.Logger
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.H2Profile.api._

import java.sql.Timestamp
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}
import play.api.libs.json.Json

case class SendCoin(accountId: String, txHash: String, txRawBytes: Array[Byte], fromAddress: String, toAddress: String, amount: Seq[Coin], broadcasted: Boolean, status: Option[Boolean], createdBy: Option[String] = None, createdOn: Option[Timestamp] = None, createdOnTimeZone: Option[String] = None, updatedBy: Option[String] = None, updatedOn: Option[Timestamp] = None, updatedOnTimeZone: Option[String] = None) extends Logged {

  def serialize(): SendCoins.SendCoinSerialized = SendCoins.SendCoinSerialized(accountId = this.accountId, txHash = this.txHash, txRawBytes = this.txRawBytes, fromAddress = this.fromAddress, toAddress = this.toAddress, amount = Json.toJson(this.amount).toString, broadcasted = this.broadcasted, status = this.status, createdBy = this.createdBy, createdOn = this.createdOn, createdOnTimeZone = this.createdOnTimeZone, updatedBy = this.updatedBy, updatedOn = this.updatedOn, updatedOnTimeZone = this.updatedOnTimeZone)

}

object SendCoins {

  private implicit val logger: Logger = Logger(this.getClass)

  private implicit val module: String = constants.Module.BLOCKCHAIN_TRANSACTION_SEND_COIN

  case class SendCoinSerialized(accountId: String, txHash: String, txRawBytes: Array[Byte], fromAddress: String, toAddress: String, amount: String, broadcasted: Boolean, status: Option[Boolean], createdBy: Option[String], createdOn: Option[Timestamp], createdOnTimeZone: Option[String], updatedBy: Option[String], updatedOn: Option[Timestamp], updatedOnTimeZone: Option[String]) extends Entity2[String, String] {
    def deserialize: SendCoin = SendCoin(accountId = accountId, txHash = txHash, txRawBytes = txRawBytes, fromAddress = fromAddress, toAddress = toAddress, amount = utilities.JSON.convertJsonStringToObject[Seq[Coin]](amount), broadcasted = broadcasted, status = status, createdBy = createdBy, createdOn = createdOn, createdOnTimeZone = createdOnTimeZone, updatedBy = updatedBy, updatedOn = updatedOn, updatedOnTimeZone = updatedOnTimeZone)

    def id1: String = accountId

    def id2: String = txHash
  }

  class SendCoinTable(tag: Tag) extends Table[SendCoinSerialized](tag, "SendCoin") with ModelTable2[String, String] {

    def * = (accountId, txHash, txRawBytes, fromAddress, toAddress, amount, broadcasted, status.?, createdBy.?, createdOn.?, createdOnTimeZone.?, updatedBy.?, updatedOn.?, updatedOnTimeZone.?) <> (SendCoinSerialized.tupled, SendCoinSerialized.unapply)

    def accountId = column[String]("accountId", O.PrimaryKey)

    def txHash = column[String]("txHash", O.PrimaryKey)

    def txRawBytes = column[Array[Byte]]("txRawBytes")

    def fromAddress = column[String]("fromAddress")

    def toAddress = column[String]("toAddress")

    def amount = column[String]("amount")

    def broadcasted = column[Boolean]("broadcasted")

    def status = column[Boolean]("status")

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
                           protected val databaseConfigProvider: DatabaseConfigProvider
                         )(implicit override val executionContext: ExecutionContext)
  extends GenericDaoImpl2[SendCoins.SendCoinTable, SendCoins.SendCoinSerialized, String, String](
    databaseConfigProvider,
    SendCoins.TableQuery,
    executionContext,
    SendCoins.module,
    SendCoins.logger
  ) {

  object Service {

    def add(accountId: String, txHash: String, txRawBytes: Array[Byte], fromAddress: String, toAddress: String, amount: Seq[Coin], broadcasted: Boolean, status: Option[Boolean]): Future[Unit] = {
      val sendCoin = SendCoin(accountId = accountId, txHash = txHash, txRawBytes = txRawBytes, fromAddress = fromAddress, toAddress = toAddress, amount = amount, broadcasted = broadcasted, status = status)
      create(sendCoin.serialize())
    }

    def tryGet(accountId: String, txHash: String): Future[SendCoin] = tryGetById(id1 = accountId, id2 = txHash).map(_.deserialize)


  }

  object Utility {

  }

}