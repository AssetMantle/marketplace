package models.blockchain

import models.Abstract.PublicKey
import models.Trait.{Entity, GenericDaoImpl, Logged, ModelTable}
import models.common.Account.Vesting.VestingParameters
import play.api.Logger
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.json.Json
import play.db.NamedDatabase
import slick.jdbc.H2Profile.api._

import java.sql.Timestamp
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

case class Account(address: String, username: String, accountType: Option[String], publicKey: Option[PublicKey], accountNumber: Int, sequence: Int, vestingParameters: Option[VestingParameters], createdBy: Option[String] = None, createdOn: Option[Timestamp] = None, createdOnTimeZone: Option[String] = None, updatedBy: Option[String] = None, updatedOn: Option[Timestamp] = None, updatedOnTimeZone: Option[String] = None) extends Logged {

  def serialize(): Accounts.AccountSerialized = Accounts.AccountSerialized(address = this.address, username = this.username, accountType = this.accountType, publicKey = this.publicKey.fold[Option[String]](None)(x => Option(Json.toJson(x).toString)), accountNumber = this.accountNumber, sequence = this.sequence, vestingParameters = this.vestingParameters.fold[Option[String]](None)(x => Option(Json.toJson(x).toString)))

}

object Accounts {

  private implicit val logger: Logger = Logger(this.getClass)

  private implicit val module: String = constants.Module.BLOCKCHAIN_ACCOUNT

  case class AccountSerialized(address: String, username: String, accountType: Option[String], publicKey: Option[String], accountNumber: Int, sequence: Int, vestingParameters: Option[String]) extends Entity[String] {
    def deserialize: Account = Account(address = address, username = username, accountType = accountType, publicKey = publicKey.fold[Option[PublicKey]](None)(x => Option(utilities.JSON.convertJsonStringToObject[PublicKey](x))), accountNumber = accountNumber, sequence = sequence, vestingParameters = vestingParameters.fold[Option[VestingParameters]](None)(x => Option(utilities.JSON.convertJsonStringToObject[VestingParameters](x))))

    def id: String = address
  }

  class AccountTable(tag: Tag) extends Table[AccountSerialized](tag, "Account") with ModelTable[String] {

    def * = (address, username, accountType.?, publicKey.?, accountNumber, sequence, vestingParameters.?) <> (AccountSerialized.tupled, AccountSerialized.unapply)

    def address = column[String]("address", O.PrimaryKey)

    def username = column[String]("username")

    def accountType = column[String]("accountType")

    def publicKey = column[String]("publicKey")

    def accountNumber = column[Int]("accountNumber")

    def sequence = column[Int]("sequence")

    def vestingParameters = column[String]("vestingParameters")

    def id = address
  }

  val TableQuery = new TableQuery(tag => new AccountTable(tag))

}

@Singleton
class Accounts @Inject()(
                          @NamedDatabase("explorer")
                          protected val databaseConfigProvider: DatabaseConfigProvider
                        )(implicit override val executionContext: ExecutionContext)
  extends GenericDaoImpl[Accounts.AccountTable, Accounts.AccountSerialized, String](
    databaseConfigProvider,
    Accounts.TableQuery,
    executionContext,
    Accounts.module,
    Accounts.logger
  ) {

  object Service {

    def tryGet(address: String): Future[Account] = tryGetById(address).map(_.deserialize)

    def get(address: String): Future[Option[Account]] = getById(address).map(_.map(_.deserialize))

    def checkAccountExists(address: String): Future[Boolean] = exists(address)

  }

}