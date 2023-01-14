package models.blockchain

import models.Trait.{Entity, GenericDaoImpl, Logged, ModelTable}
import models.common.Fee
import models.common.TransactionMessages.StdMsg
import play.api.Logger
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.json.Json
import play.db.NamedDatabase
import slick.jdbc.H2Profile.api._
import utilities.Date.RFC3339

import java.sql.Timestamp
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

case class Transaction(hash: String, height: Int, code: Int, rawLog: String, gasWanted: String, gasUsed: String, messages: Seq[StdMsg], fee: Fee, memo: String, timestamp: RFC3339, createdBy: Option[String] = None, createdOn: Option[Timestamp] = None, createdOnTimeZone: Option[String] = None, updatedBy: Option[String] = None, updatedOn: Option[Timestamp] = None, updatedOnTimeZone: Option[String] = None) extends Logged {

  def status: Boolean = code == 0

  // Since Seq in scala is by default immutable and ordering is maintained, we can use these methods directly
  def getSigners: Seq[String] = messages.flatMap(_.getSigners).distinct

  def getFeePayer: String = if (fee.payer != "") fee.payer else getSigners.headOption.getOrElse("")

  def getFeeGranter: String = fee.granter

  def serialize(transaction: Transaction): Transactions.TransactionSerialized = Transactions.TransactionSerialized(hash = transaction.hash, height = transaction.height, code = transaction.code, rawLog = transaction.rawLog, gasWanted = transaction.gasWanted, gasUsed = transaction.gasUsed, messages = Json.toJson(transaction.messages).toString, fee = Json.toJson(transaction.fee).toString, memo = transaction.memo, timestamp = transaction.timestamp.toString, createdBy = transaction.createdBy, createdOn = transaction.createdOn, createdOnTimeZone = transaction.createdOnTimeZone, updatedBy = transaction.updatedBy, updatedOn = transaction.updatedOn, updatedOnTimeZone = transaction.updatedOnTimeZone)

}

object Transactions {

  private implicit val logger: Logger = Logger(this.getClass)

  private implicit val module: String = constants.Module.BLOCKCHAIN_TRANSACTION

  case class TransactionSerialized(hash: String, height: Int, code: Int, rawLog: String, gasWanted: String, gasUsed: String, messages: String, fee: String, memo: String, timestamp: String, createdBy: Option[String], createdOn: Option[Timestamp], createdOnTimeZone: Option[String], updatedBy: Option[String], updatedOn: Option[Timestamp], updatedOnTimeZone: Option[String]) extends Entity[String] {
    def deserialize: Transaction = Transaction(hash = hash, height = height, code = code, rawLog = rawLog, gasWanted = gasWanted, gasUsed = gasUsed, messages = utilities.JSON.convertJsonStringToObject[Seq[StdMsg]](messages), fee = utilities.JSON.convertJsonStringToObject[Fee](fee), memo = memo, timestamp = RFC3339(timestamp), createdBy = createdBy, createdOn = createdOn, createdOnTimeZone = createdOnTimeZone, updatedBy = updatedBy, updatedOn = updatedOn, updatedOnTimeZone = updatedOnTimeZone)

    def id: String = hash
  }

  class TransactionTable(tag: Tag) extends Table[TransactionSerialized](tag, "Transaction") with ModelTable[String] {

    def * = (hash, height, code, rawLog, gasWanted, gasUsed, messages, fee, memo, timestamp, createdBy.?, createdOn.?, createdOnTimeZone.?, updatedBy.?, updatedOn.?, updatedOnTimeZone.?) <> (TransactionSerialized.tupled, TransactionSerialized.unapply)

    def hash = column[String]("hash", O.PrimaryKey)

    def height = column[Int]("height")

    def code = column[Int]("code")

    def rawLog = column[String]("rawLog")

    def gasWanted = column[String]("gasWanted")

    def gasUsed = column[String]("gasUsed")

    def messages = column[String]("messages")

    def fee = column[String]("fee")

    def memo = column[String]("memo")

    def timestamp = column[String]("timestamp")

    def createdBy = column[String]("createdBy")

    def createdOn = column[Timestamp]("createdOn")

    def createdOnTimeZone = column[String]("createdOnTimeZone")

    def updatedBy = column[String]("updatedBy")

    def updatedOn = column[Timestamp]("updatedOn")

    def updatedOnTimeZone = column[String]("updatedOnTimeZone")

    def id = hash
  }

  val TableQuery = new TableQuery(tag => new TransactionTable(tag))

}

@Singleton
class Transactions @Inject()(
                              @NamedDatabase("explorer")
                              protected val databaseConfigProvider: DatabaseConfigProvider
                            )(implicit override val executionContext: ExecutionContext)
  extends GenericDaoImpl[Transactions.TransactionTable, Transactions.TransactionSerialized, String](
    databaseConfigProvider,
    Transactions.TableQuery,
    executionContext,
    Transactions.module,
    Transactions.logger
  ) {

  object Service {

    def get(hash: String): Future[Option[Transaction]] = getById(hash).map(_.map(_.deserialize))

    def getStatus(hash: String): Future[Option[Boolean]] = getById(hash).map(_.map(_.code == 0))

    def getByHashes(hashes: Seq[String]): Future[Seq[Transaction]] = filter(_.hash.inSet(hashes)).map(_.map(_.deserialize))

  }
}