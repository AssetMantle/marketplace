package models.blockchain

import models.traits.{Entity, GenericDaoImpl, ModelTable}
import play.api.Logger
import play.api.db.slick.DatabaseConfigProvider
import play.db.NamedDatabase
import slick.jdbc.H2Profile.api._

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

case class ArchiveTransaction(hash: String, height: Int, code: Int, gasWanted: String, gasUsed: String, txBytes: Array[Byte], log: Option[String]) extends Entity[String] {

  def id: String = hash

  def toTx: Transaction = Transaction(hash = hash, height = height, code = code, gasWanted = gasWanted, gasUsed = gasUsed, txBytes = txBytes, log = log)
}

private[blockchain] object ArchiveTransactions {

  class ArchiveTransactionTable(tag: Tag) extends Table[ArchiveTransaction](tag, Option("archive"), "Transaction") with ModelTable[String] {

    def * = (hash, height, code, gasWanted, gasUsed, txBytes, log.?) <> (ArchiveTransaction.tupled, ArchiveTransaction.unapply)

    def hash = column[String]("hash", O.PrimaryKey)

    def height = column[Int]("height")

    def code = column[Int]("code")

    def gasWanted = column[String]("gasWanted")

    def gasUsed = column[String]("gasUsed")

    def txBytes = column[Array[Byte]]("txBytes")

    def log = column[String]("log")

    def id = hash

  }

}

@Singleton
class ArchiveTransactions @Inject()(
                                     @NamedDatabase("explorer")
                                     protected val dbConfigProvider: DatabaseConfigProvider,
                                   )(implicit executionContext: ExecutionContext)
  extends GenericDaoImpl[ArchiveTransactions.ArchiveTransactionTable, ArchiveTransaction, String]() {

  implicit val logger: Logger = Logger(this.getClass)

  implicit val module: String = constants.Module.ARCHIVE_TRANSACTION

  val tableQuery = new TableQuery(tag => new ArchiveTransactions.ArchiveTransactionTable(tag))

  object Service {

    def get(hash: String): Future[Option[ArchiveTransaction]] = getById(hash)

    def getByHeight(height: Int): Future[Seq[ArchiveTransaction]] = filter(_.height === height)

    def getByHashes(hashes: Seq[String]): Future[Seq[ArchiveTransaction]] = if (hashes.nonEmpty) filter(_.hash.inSet(hashes)) else Future(Seq())

  }
}