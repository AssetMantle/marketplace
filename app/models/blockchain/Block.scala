package models.blockchain

import constants.Scheduler
import exceptions.BaseException
import models.Trait.{Entity, GenericDaoImpl, ModelTable}
import play.api.db.slick.DatabaseConfigProvider
import play.api.{Configuration, Logger}
import play.db.NamedDatabase
import slick.jdbc.H2Profile.api._

import javax.inject.{Inject, Singleton}
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}

case class Block(height: Int, time: Long) extends Entity[Int] {
  def id: Int = height
}

object Blocks {

  private implicit val logger: Logger = Logger(this.getClass)

  private implicit val module: String = constants.Module.BLOCKCHAIN_BLOCK

  private[models] class BlockTable(tag: Tag) extends Table[Block](tag, "Block") with ModelTable[Int] {

    def * = (height, time) <> (Block.tupled, Block.unapply)

    def height = column[Int]("height", O.PrimaryKey)

    def time = column[Long]("time")

    def id = height
  }

  val TableQuery = new TableQuery(tag => new BlockTable(tag))

}

@Singleton
class Blocks @Inject()(
                        @NamedDatabase("explorer")
                        protected val databaseConfigProvider: DatabaseConfigProvider,
                        configuration: Configuration
                      )(implicit executionContext: ExecutionContext)
  extends GenericDaoImpl[Blocks.BlockTable, Block, Int](
    databaseConfigProvider,
    Blocks.TableQuery,
    executionContext,
    Blocks.module,
    Blocks.logger
  ) {


  object Service {
    private var latestBlockHeight: Int = 0

    def getLatestHeight: Int = latestBlockHeight

    def setLatestHeight(): Future[Unit] = {
      val latestHeight = customQuery(Blocks.TableQuery.map(_.height).max.result)

      for {
        latestHeight <- latestHeight
      } yield {
        latestBlockHeight = latestHeight.getOrElse(0)
      }
    }

  }

  object Utility {

    val scheduler: Scheduler = new Scheduler {
      val name: String = constants.Scheduler.BLOCKCHAIN_BLOCK

      def runner(): Unit = {
        val setHeight = Service.setLatestHeight()

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