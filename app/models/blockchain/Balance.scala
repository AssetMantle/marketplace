package models.blockchain

import models.common.Coin
import models.traits.{Entity, GenericDaoImpl, ModelTable}
import play.api.Logger
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.json.Json
import play.db.NamedDatabase
import slick.jdbc.H2Profile.api._
import utilities.MicroNumber

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

case class Balance(address: String, coins: Seq[Coin]) {

  def serialize(balance: Balance): Balances.BalanceSerialized = Balances.BalanceSerialized(address = balance.address, coins = Json.toJson(balance.coins).toString)

}

private[blockchain] object Balances {

  case class BalanceSerialized(address: String, coins: String) extends Entity[String] {
    def deserialize()(implicit module: String, logger: Logger): Balance = Balance(address = address, coins = utilities.JSON.convertJsonStringToObject[Seq[Coin]](coins))

    def id: String = address
  }

  class BalanceTable(tag: Tag) extends Table[BalanceSerialized](tag, "Balance") with ModelTable[String] {

    def * = (address, coins) <> (BalanceSerialized.tupled, BalanceSerialized.unapply)

    def address = column[String]("address", O.PrimaryKey)

    def coins = column[String]("coins")

    def id = address
  }

}

@Singleton
class Balances @Inject()(
                          @NamedDatabase("explorer")
                          protected val dbConfigProvider: DatabaseConfigProvider,
                        )(implicit executionContext: ExecutionContext)
  extends GenericDaoImpl[Balances.BalanceTable, Balances.BalanceSerialized, String]() {

  implicit val logger: Logger = Logger(this.getClass)

  implicit val module: String = constants.Module.BLOCKCHAIN_BALANCE

  val tableQuery = new TableQuery(tag => new Balances.BalanceTable(tag))

  object Service {

    def tryGet(address: String): Future[Balance] = tryGetById(address).map(_.deserialize)

    def get(address: String): Future[Option[Balance]] = getById(address).map(_.map(_.deserialize))

    def getTokenBalance(address: String, denom: String = constants.Blockchain.StakingToken): Future[MicroNumber] = getById(address).map(_.fold(MicroNumber.zero)(_.deserialize.coins.find(_.denom == denom).fold(MicroNumber.zero)(_.amount)))
  }
}