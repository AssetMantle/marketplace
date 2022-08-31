package models.masterTransaction

import akka.actor.ActorSystem
import exceptions.BaseException
import models.Trait.{Entity, GenericDaoImpl, Logged, ModelTable}
import play.api.Logger
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.H2Profile.api._
import play.api.db.slick.DatabaseConfigProvider
import play.api.{Configuration, Logger}
import play.db.NamedDatabase
import slick.jdbc.JdbcProfile

import java.sql.Timestamp
import javax.inject.{Inject, Singleton}
import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

case class TokenPrice(serial: Int = 0, denom: String, price: Double, createdBy: Option[String] = None, createdOn: Option[Timestamp] = None, createdOnTimeZone: Option[String] = None, updatedBy: Option[String] = None, updatedOn: Option[Timestamp] = None, updatedOnTimeZone: Option[String] = None) extends Logged with Entity[Int] {
  def id: Int = serial
}

object TokenPrices {
  implicit val logger: Logger = Logger(this.getClass)

  implicit val module: String = constants.Module.MASTER_TRANSACTION_TOKEN_PRICE

  class TokenPriceTable(tag: Tag) extends Table[TokenPrice](tag, "TokenPrice") with ModelTable[Int] {

    def * = (serial, denom, price, createdBy.?, createdOn.?, createdOnTimeZone.?, updatedBy.?, updatedOn.?, updatedOnTimeZone.?) <> (TokenPrice.tupled, TokenPrice.unapply)

    def serial = column[Int]("serial", O.PrimaryKey)

    def denom = column[String]("denom")

    def price = column[Double]("price")

    def createdBy = column[String]("createdBy")

    def createdOn = column[Timestamp]("createdOn")

    def createdOnTimeZone = column[String]("createdOnTimeZone")

    def updatedBy = column[String]("updatedBy")

    def updatedOn = column[Timestamp]("updatedOn")

    def updatedOnTimeZone = column[String]("updatedOnTimeZone")

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

    def tryGetLatestTokenPrice(denom: String): Future[TokenPrice] = filterAndSortWithOrderHead(_.denom === denom)(_.serial.desc)

  }

}