package models.blockchain

import models.Trait.{Entity, GenericDaoImpl, Logged, ModelTable}
import play.api.Logger
import play.api.db.slick.DatabaseConfigProvider
import play.db.NamedDatabase
import slick.jdbc.H2Profile.api._

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

case class Transaction(hash: String, height: Int, code: Int, memo: String) {

  def status: Boolean = code == 0

  def serialize(transaction: Transaction): Transactions.TransactionSerialized = Transactions.TransactionSerialized(hash = transaction.hash, height = transaction.height, code = transaction.code, memo = transaction.memo)

}

object Transactions {

  private implicit val logger: Logger = Logger(this.getClass)

  private implicit val module: String = constants.Module.BLOCKCHAIN_TRANSACTION

  case class TransactionSerialized(hash: String, height: Int, code: Int, memo: String) extends Entity[String] {
    def deserialize: Transaction = Transaction(hash = hash, height = height, code = code, memo = memo)

    def id: String = hash
  }

  class TransactionTable(tag: Tag) extends Table[TransactionSerialized](tag, "Transaction") with ModelTable[String] {

    def * = (hash, height, code, memo) <> (TransactionSerialized.tupled, TransactionSerialized.unapply)

    def hash = column[String]("hash", O.PrimaryKey)

    def height = column[Int]("height")

    def code = column[Int]("code")

    def memo = column[String]("memo")

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

    def getByHashes(hashes: Seq[String]): Future[Seq[Transaction]] = if (hashes.nonEmpty) filter(_.hash.inSet(hashes)).map(_.map(_.deserialize)) else Future(Seq[Transaction]())

  }
}