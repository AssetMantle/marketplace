package models.blockchain

import constants.Scheduler
import exceptions.BaseException
import models.traits.{Entity, GenericDaoImpl, ModelTable}
import play.api.Logger
import play.api.db.slick.DatabaseConfigProvider
import play.db.NamedDatabase
import slick.jdbc.H2Profile.api._

import javax.inject.{Inject, Singleton}
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}

case class Block(height: Int, time: Long) extends Entity[Int] {
  def id: Int = height
}

private[blockchain] object Blocks {

  class BlockTable(tag: Tag) extends Table[Block](tag, "Block") with ModelTable[Int] {

    def * = (height, time) <> (Block.tupled, Block.unapply)

    def height = column[Int]("height", O.PrimaryKey)

    def time = column[Long]("time")

    def id = height
  }
}

@Singleton
class Blocks @Inject()(
                        @NamedDatabase("explorer")
                        protected val dbConfigProvider: DatabaseConfigProvider,
                        archiveBlocks: ArchiveBlocks,
                      )(implicit executionContext: ExecutionContext)
  extends GenericDaoImpl[Blocks.BlockTable, Block, Int]() {

  implicit val logger: Logger = Logger(this.getClass)

  implicit val module: String = constants.Module.BLOCKCHAIN_BLOCK

  val tableQuery = new TableQuery(tag => new Blocks.BlockTable(tag))

  object Service {
    private var latestBlockHeight: Int = 0

    private var firstBlockHeight: Int = 0

    def getLatestHeight: Int = latestBlockHeight

    def getFirstHeight: Int = firstBlockHeight

    def setHeights(): Future[Unit] = {
      val heights = customQuery(tableQuery.groupBy { _ => true }.map {
        case (_, group) =>
          (group.map(_.height).max, group.map(_.height).min)
      }.result.head)

      for {
        (max, min) <- heights
      } yield {
        firstBlockHeight = min.getOrElse(0)
        latestBlockHeight = max.fold(0)(x => x - 1)
      }
    }

    def getByHeights(heights: Seq[Int]): Future[Seq[Block]] = if (heights.nonEmpty) {
      if (heights.min > getFirstHeight) filter(_.height.inSet(heights))
      else if (heights.max < getFirstHeight) archiveBlocks.Service.get(heights)
      else {
        for {
          blocks1 <- filter(_.height.inSet(heights))
          blocks2 <- archiveBlocks.Service.get(heights)
        } yield blocks1 ++ blocks2
      }
    } else Future(Seq())

  }

  object Utility {

    val scheduler: Scheduler = new Scheduler {
      val name: String = module

      def runner(): Unit = {
        val setHeight = Service.setHeights()

        val forComplete = (for {
          _ <- setHeight
        } yield ()).recover {
          case baseException: BaseException => logger.error(baseException.failure.message)
        }
        Await.result(forComplete, Duration.Inf)
      }
    }

  }

}