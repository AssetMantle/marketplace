package models.master

import models.Trait.Logged
import play.api.Logger
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.json.Json
import slick.jdbc.H2Profile.api._
import slick.jdbc.JdbcProfile

import java.sql.Timestamp
import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

case class Wallet(address: String, mnemonics: Seq[String], accountID: String, provisioned: Option[Boolean], createdBy: Option[String] = None, createdOn: Option[Timestamp] = None, createdOnTimeZone: Option[String] = None, updatedBy: Option[String] = None, updatedOn: Option[Timestamp] = None, updatedOnTimeZone: Option[String] = None) extends Logged

@Singleton
class Wallets @Inject()(
                         protected val databaseConfigProvider: DatabaseConfigProvider
                       )(implicit executionContext: ExecutionContext) {

  private implicit val module: String = constants.Module.MASTER_ACCOUNT

  private val db = databaseConfigProvider.get[JdbcProfile].db

  private implicit val logger: Logger = Logger(this.getClass)

  private val walletTable = TableQuery[WalletTable]

  case class WalletSerialized(address: String, mnemonics: String, accountID: String, provisioned: Option[Boolean], createdBy: Option[String], createdOn: Option[Timestamp], createdOnTimeZone: Option[String], updatedBy: Option[String], updatedOn: Option[Timestamp], updatedOnTimeZone: Option[String]) {
    def deserialize: Wallet = Wallet(address = address, mnemonics = utilities.JSON.convertJsonStringToObject[Seq[String]](mnemonics), accountID = accountID, provisioned = provisioned, createdBy = createdBy, createdOn = createdOn, createdOnTimeZone = createdOnTimeZone, updatedBy = updatedBy, updatedOn = updatedOn, updatedOnTimeZone = updatedOnTimeZone)
  }

  def serialize(wallet: Wallet): WalletSerialized = WalletSerialized(address = wallet.address, mnemonics = Json.toJson(wallet.mnemonics).toString(), accountID = wallet.accountID, provisioned = wallet.provisioned, createdBy = wallet.createdBy, createdOn = wallet.createdOn, createdOnTimeZone = wallet.createdOnTimeZone, updatedBy = wallet.updatedBy, updatedOn = wallet.updatedOn, updatedOnTimeZone = wallet.updatedOnTimeZone)

  private class WalletTable(tag: Tag) extends Table[WalletSerialized](tag, "Wallet") {

    def * = (address, mnemonics, accountID, provisioned.?, createdBy.?, createdOn.?, createdOnTimeZone.?, updatedBy.?, updatedOn.?, updatedOnTimeZone.?) <> (WalletSerialized.tupled, WalletSerialized.unapply)

    def address = column[String]("address", O.PrimaryKey)

    def mnemonics = column[String]("mnemonics")

    def accountID = column[String]("accountID")

    def provisioned = column[Boolean]("provisioned")

    def createdBy = column[String]("createdBy")

    def createdOn = column[Timestamp]("createdOn")

    def createdOnTimeZone = column[String]("createdOnTimeZone")

    def updatedBy = column[String]("updatedBy")

    def updatedOn = column[Timestamp]("updatedOn")

    def updatedOnTimeZone = column[String]("updatedOnTimeZone")

  }

  object Service {

  }

}