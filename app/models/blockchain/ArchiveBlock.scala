package models.blockchain

import models.blockchain
import models.traits.{Entity, GenericDaoImpl, ModelTable}
import play.api.Logger
import play.api.db.slick.DatabaseConfigProvider
import play.db.NamedDatabase
import slick.jdbc.H2Profile.api._

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

case class ArchiveBlock(height: Int, time: Long) extends Entity[Int] {
  def toBlock: blockchain.Block = blockchain.Block(height = this.height, time = this.time)

  def id: Int = height
}

private[blockchain] object ArchiveBlocks {

  class ArchiveBlockTable(tag: Tag) extends Table[ArchiveBlock](tag, Option("archive"), "Block") with ModelTable[Int] {

    def * = (height, time) <> (ArchiveBlock.tupled, ArchiveBlock.unapply)

    def height = column[Int]("height", O.PrimaryKey)

    def time = column[Long]("time")

    def id = height

  }

}

@Singleton
class ArchiveBlocks @Inject()(
                               @NamedDatabase("explorer")
                               protected val dbConfigProvider: DatabaseConfigProvider,
                             )(implicit executionContext: ExecutionContext)
  extends GenericDaoImpl[ArchiveBlocks.ArchiveBlockTable, ArchiveBlock, Int]() {

  implicit val logger: Logger = Logger(this.getClass)

  implicit val module: String = constants.Module.ARCHIVE_BLOCK

  val tableQuery = new TableQuery(tag => new ArchiveBlocks.ArchiveBlockTable(tag))

  object Service {

    def get(heights: Seq[Int]): Future[Seq[Block]] = filter(_.height.inSet(heights)).map(_.map(_.toBlock))

  }

}