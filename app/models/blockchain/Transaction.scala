package models.blockchain

import models.Trait.{Entity, GenericDaoImpl, ModelTable}
import models.common.Fee
import play.api.Logger
import play.api.db.slick.DatabaseConfigProvider
import play.db.NamedDatabase
import slick.jdbc.H2Profile.api._
import utilities.Date.RFC3339

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

case class Transaction(hash: String, height: Int, code: Int, rawLog: String, gasWanted: String, gasUsed: String, fee: Fee, memo: String, timestamp: RFC3339) {

  def status: Boolean = code == 0
}

object Transactions {

  private implicit val logger: Logger = Logger(this.getClass)

  private implicit val module: String = constants.Module.BLOCKCHAIN_TRANSACTION

  case class TransactionSerialized(hash: String, height: Int, code: Int, rawLog: String, gasWanted: String, gasUsed: String, fee: String, memo: String, timestamp: String) extends Entity[String] {
    def deserialize: Transaction = Transaction(hash = hash, height = height, code = code, rawLog = rawLog, gasWanted = gasWanted, gasUsed = gasUsed, fee = utilities.JSON.convertJsonStringToObject[Fee](fee), memo = memo, timestamp = RFC3339(timestamp))

    def id: String = hash
  }

  class TransactionTable(tag: Tag) extends Table[TransactionSerialized](tag, "Transaction") with ModelTable[String] {

    def * = (hash, height, code, rawLog, gasWanted, gasUsed, fee, memo, timestamp) <> (TransactionSerialized.tupled, TransactionSerialized.unapply)

    def hash = column[String]("hash", O.PrimaryKey)

    def height = column[Int]("height")

    def code = column[Int]("code")

    def rawLog = column[String]("rawLog")

    def gasWanted = column[String]("gasWanted")

    def gasUsed = column[String]("gasUsed")

    def fee = column[String]("fee")

    def memo = column[String]("memo")

    def timestamp = column[String]("timestamp")

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

    def getByHashes(hashes: Seq[String]): Future[Seq[Transaction]] = if (hashes.nonEmpty) filter(_.hash.inSet(hashes)).map(_.map(_.deserialize)) else Future(Seq[Transaction]())

  }
}