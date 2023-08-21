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
import scala.concurrent.ExecutionContext

case class Account(address: String, username: String, accountType: Option[String], publicKey: Option[PublicKey], accountNumber: Int, sequence: Int, vestingParameters: Option[VestingParameters], createdBy: Option[String] = None, createdOn: Option[Timestamp] = None, createdOnTimeZone: Option[String] = None, updatedBy: Option[String] = None, updatedOn: Option[Timestamp] = None, updatedOnTimeZone: Option[String] = None) extends Logged {

  def serialize(): Accounts.AccountSerialized = Accounts.AccountSerialized(address = this.address, username = this.username, accountType = this.accountType, publicKey = this.publicKey.fold[Option[String]](None)(x => Option(Json.toJson(x).toString)), accountNumber = this.accountNumber, sequence = this.sequence, vestingParameters = this.vestingParameters.fold[Option[String]](None)(x => Option(Json.toJson(x).toString)), createdBy = this.createdBy, createdOn = this.createdOn, createdOnTimeZone = this.createdOnTimeZone, updatedBy = this.updatedBy, updatedOn = this.updatedOn, updatedOnTimeZone = this.updatedOnTimeZone)

}

object Accounts {

  private implicit val logger: Logger = Logger(this.getClass)

  private implicit val module: String = constants.Module.BLOCKCHAIN_ACCOUNT

  case class AccountSerialized(address: String, username: String, accountType: Option[String], publicKey: Option[String], accountNumber: Int, sequence: Int, vestingParameters: Option[String], createdBy: Option[String], createdOn: Option[Timestamp], createdOnTimeZone: Option[String], updatedBy: Option[String], updatedOn: Option[Timestamp], updatedOnTimeZone: Option[String]) extends Entity[String] {
    def deserialize: Account = Account(address = address, username = username, accountType = accountType, publicKey = publicKey.fold[Option[PublicKey]](None)(x => Option(utilities.JSON.convertJsonStringToObject[PublicKey](x))), accountNumber = accountNumber, sequence = sequence, vestingParameters = vestingParameters.fold[Option[VestingParameters]](None)(x => Option(utilities.JSON.convertJsonStringToObject[VestingParameters](x))), createdBy = createdBy, createdOn = createdOn, createdOnTimeZone = createdOnTimeZone, updatedBy = updatedBy, updatedOn = updatedOn, updatedOnTimeZone = updatedOnTimeZone)

    def id: String = address
  }

  class AccountTable(tag: Tag) extends Table[AccountSerialized](tag, "Account_BC") with ModelTable[String] {

    def * = (address, username, accountType.?, publicKey.?, accountNumber, sequence, vestingParameters.?, createdBy.?, createdOn.?, createdOnTimeZone.?, updatedBy.?, updatedOn.?, updatedOnTimeZone.?) <> (AccountSerialized.tupled, AccountSerialized.unapply)

    def address = column[String]("address", O.PrimaryKey)

    def username = column[String]("username")

    def accountType = column[String]("accountType")

    def publicKey = column[String]("publicKey")

    def accountNumber = column[Int]("accountNumber")

    def sequence = column[Int]("sequence")

    def vestingParameters = column[String]("vestingParameters")

    def createdBy = column[String]("createdBy")

    def createdOn = column[Timestamp]("createdOn")

    def createdOnTimeZone = column[String]("createdOnTimeZone")

    def updatedBy = column[String]("updatedBy")

    def updatedOn = column[Timestamp]("updatedOn")

    def updatedOnTimeZone = column[String]("updatedOnTimeZone")

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

}