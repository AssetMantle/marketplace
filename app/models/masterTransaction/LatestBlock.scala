package models.masterTransaction

import models.blockchain
import models.traits._
import play.api.Logger
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.H2Profile.api._

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

case class LatestBlock(height: Long, createdBy: Option[String] = None, createdOnMillisEpoch: Option[Long] = None, updatedBy: Option[String] = None, updatedOnMillisEpoch: Option[Long] = None) extends Logging with Entity[Long] {

  def id: Long = height

}

object LatestBlocks {

  implicit val module: String = constants.Module.MASTER_TRANSACTION_LATEST_BLOCK

  implicit val logger: Logger = Logger(this.getClass)

  class LatestBlockTable(tag: Tag) extends Table[LatestBlock](tag, "LatestBlock") with ModelTable[Long] {

    def * = (height, createdBy.?, createdOnMillisEpoch.?, updatedBy.?, updatedOnMillisEpoch.?) <> (LatestBlock.tupled, LatestBlock.unapply)

    def height = column[Long]("height", O.PrimaryKey)

    def createdBy = column[String]("createdBy")

    def createdOnMillisEpoch = column[Long]("createdOnMillisEpoch")

    def updatedBy = column[String]("updatedBy")

    def updatedOnMillisEpoch = column[Long]("updatedOnMillisEpoch")

    def id = height

  }

  lazy val TableQuery = new TableQuery(tag => new LatestBlockTable(tag))
}

@Singleton
class LatestBlocks @Inject()(
                              blockchainAssets: blockchain.Assets,
                              blockchainSplits: blockchain.Splits,
                              protected val databaseConfigProvider: DatabaseConfigProvider
                            )(implicit override val executionContext: ExecutionContext)
  extends GenericDaoImpl[LatestBlocks.LatestBlockTable, LatestBlock, Long](
    databaseConfigProvider,
    LatestBlocks.TableQuery,
    executionContext,
    LatestBlocks.module,
    LatestBlocks.logger
  ) {

  object Service {

    def getLatest: Future[Long] = customQuery(LatestBlocks.TableQuery.sortBy(_.height.desc).result.headOption).map(_.fold(0L)(_.height))

    def update(latestHeight: Long): Future[Unit] = {
      for {
        _ <- create(LatestBlock(latestHeight))
        _ <- filterAndDelete(_.height < latestHeight)
      } yield ()
    }

  }
}