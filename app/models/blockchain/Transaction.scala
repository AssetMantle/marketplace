package models.blockchain

import com.cosmos.tx.v1beta1.Tx
import com.google.protobuf.{Any => protoAny}
import models.traits.{Entity, GenericDaoImpl, ModelTable}
import play.api.Logger
import play.api.db.slick.DatabaseConfigProvider
import play.db.NamedDatabase
import slick.jdbc.H2Profile.api._

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}
import scala.jdk.CollectionConverters.CollectionHasAsScala

case class Transaction(hash: String, height: Int, code: Int, log: Option[String], gasWanted: String, gasUsed: String, txBytes: Array[Byte]) extends Entity[String] {
  def id: String = hash

  def status: Boolean = code == 0

  lazy val parsedTx: Tx = Tx.parseFrom(txBytes)

  def getMessages: Seq[protoAny] = parsedTx.getBody.getMessagesList.asScala.toSeq
}

object Transactions {

  private implicit val logger: Logger = Logger(this.getClass)

  private implicit val module: String = constants.Module.BLOCKCHAIN_TRANSACTION

  class TransactionTable(tag: Tag) extends Table[Transaction](tag, "Transaction") with ModelTable[String] {

    def * = (hash, height, code, log.?, gasWanted, gasUsed, txBytes) <> (Transaction.tupled, Transaction.unapply)

    def hash = column[String]("hash", O.PrimaryKey)

    def height = column[Int]("height")

    def code = column[Int]("code")

    def log = column[String]("log")

    def gasWanted = column[String]("gasWanted")

    def gasUsed = column[String]("gasUsed")

    def txBytes = column[Array[Byte]]("txBytes")

    def id = hash
  }

  val TableQuery = new TableQuery(tag => new TransactionTable(tag))

}

@Singleton
class Transactions @Inject()(
                              @NamedDatabase("explorer")
                              protected val databaseConfigProvider: DatabaseConfigProvider
                            )(implicit override val executionContext: ExecutionContext)
  extends GenericDaoImpl[Transactions.TransactionTable, Transaction, String](
    databaseConfigProvider,
    Transactions.TableQuery,
    executionContext,
    Transactions.module,
    Transactions.logger
  ) {

  object Service {

    def get(hash: String): Future[Option[Transaction]] = getById(hash)

    def getStatus(hash: String): Future[Option[Boolean]] = getById(hash).map(_.map(_.code == 0))

    def getByHashes(hashes: Seq[String]): Future[Seq[Transaction]] = if (hashes.nonEmpty) filter(_.hash.inSet(hashes)) else Future(Seq[Transaction]())

    def getByHeight(height: Int): Future[Seq[Transaction]] = filter(_.height === height)

  }
}