package models.masterTransaction

import constants.Scheduler
import exceptions.BaseException
import models.Trait.{Entity, GenericDaoImpl, ModelTable}
import play.api.Logger
import play.api.db.slick.DatabaseConfigProvider
import play.db.NamedDatabase
import slick.jdbc.H2Profile.api._

import javax.inject.{Inject, Singleton}
import scala.concurrent.duration.{Duration, FiniteDuration, SECONDS}
import scala.concurrent.{Await, ExecutionContext, Future}

case class TokenPrice(serial: Int = 0, denom: String, price: Double) extends Entity[Int] {
  def id: Int = serial
}

object TokenPrices {
  implicit val logger: Logger = Logger(this.getClass)

  implicit val module: String = constants.Module.MASTER_TRANSACTION_TOKEN_PRICE

  class TokenPriceTable(tag: Tag) extends Table[TokenPrice](tag, "TokenPrice") with ModelTable[Int] {

    def * = (serial, denom, price) <> (TokenPrice.tupled, TokenPrice.unapply)

    def serial = column[Int]("serial", O.PrimaryKey)

    def denom = column[String]("denom")

    def price = column[Double]("price")

    def id = serial
  }

  val TableQuery = new TableQuery(tag => new TokenPriceTable(tag))
}

@Singleton
class TokenPrices @Inject()(
                             @NamedDatabase("explorer")
                             protected val databaseConfigProvider: DatabaseConfigProvider,
                           )(implicit executionContext: ExecutionContext)
  extends GenericDaoImpl[TokenPrices.TokenPriceTable, TokenPrice, Int](
    databaseConfigProvider,
    TokenPrices.TableQuery,
    executionContext,
    TokenPrices.module,
    TokenPrices.logger
  ) {

  object Service {

    private var latestPrice: Double = 0.0

    def getLatestPrice: Double = latestPrice

    def setLatestPrice(): Future[Unit] = {
      val tokenPrice = tryGetLatestTokenPrice(constants.Blockchain.StakingToken)

      for {
        tokenPrice <- tokenPrice
      } yield latestPrice = tokenPrice.price
    }

    def tryGetLatestTokenPrice(denom: String): Future[TokenPrice] = filterAndSortWithOrderHead(_.denom === denom)(_.serial.desc)

  }

  object Utility {

    val scheduler: Scheduler = new Scheduler {
      val name: String = constants.Scheduler.MASTER_TRANSACTION_TOKEN_PRICE

      override val fixedDelay: FiniteDuration = FiniteDuration(constants.Date.HourSeconds, SECONDS)

      def runner(): Unit = {
        val setPrice = Service.setLatestPrice()

        val forComplete = (for {
          _ <- setPrice
        } yield ()).recover {
          case baseException: BaseException => logger.error(baseException.failure.message)
        }
        Await.result(forComplete, Duration.Inf)
      }
    }

  }

}