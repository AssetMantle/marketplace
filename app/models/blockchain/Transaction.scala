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

private[blockchain] object Transactions {

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

}

@Singleton
class Transactions @Inject()(
                              @NamedDatabase("explorer")
                              protected val dbConfigProvider: DatabaseConfigProvider,
                              archiveTransactions: ArchiveTransactions,
                              blockchainBlocks: Blocks,
                            )(implicit val executionContext: ExecutionContext)
  extends GenericDaoImpl[Transactions.TransactionTable, Transaction, String]() {

  implicit val logger: Logger = Logger(this.getClass)

  implicit val module: String = constants.Module.BLOCKCHAIN_TRANSACTION

  val tableQuery = new TableQuery(tag => new Transactions.TransactionTable(tag))

  object Service {

    def get(hash: String): Future[Option[Transaction]] = getById(hash)

    def getByHashes(hashes: Seq[String]): Future[Seq[Transaction]] = if (hashes.nonEmpty) filter(_.hash.inSet(hashes)) else Future(Seq())

    def getByHeight(height: Int): Future[Seq[Transaction]] = filter(_.height === height)

  }

  object Utility {
    def get(hash: String): Future[Option[Transaction]] = {
      val tx1 = Service.get(hash)

      def getArchiveTx(tx: Option[Transaction]) = if (tx.isEmpty) archiveTransactions.Service.get(hash).map(_.map(_.toTx)) else Future(tx)

      for {
        tx1 <- tx1
        tx2 <- getArchiveTx(tx1)
      } yield tx2
    }

    def getByHashes(hashes: Seq[String]): Future[Seq[Transaction]] = if (hashes.nonEmpty) {
      val tx1 = Service.getByHashes(hashes)
      val tx2 = archiveTransactions.Service.getByHashes(hashes).map(_.map(_.toTx))

      for {
        tx1 <- tx1
        tx2 <- tx2
      } yield tx1 ++ tx2

    } else Future(Seq[Transaction]())

    def getByHeight(height: Int): Future[Seq[Transaction]] = if (height < blockchainBlocks.Service.getFirstHeight) {
      archiveTransactions.Service.getByHeight(height).map(_.map(_.toTx))
    } else {
      Service.getByHeight(height)
    }
  }

}