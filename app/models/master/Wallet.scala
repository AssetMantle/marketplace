package models.master

import models.Trait.{Entity, GenericDaoImpl, Logged, ModelTable}
import play.api.Logger
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.json.Json
import slick.jdbc.H2Profile.api._

import java.sql.Timestamp
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

case class Wallet(address: String, partialMnemonics: Seq[String], accountId: String, provisioned: Option[Boolean], verified: Option[Boolean], preference: Int, createdBy: Option[String] = None, createdOn: Option[Timestamp] = None, createdOnTimeZone: Option[String] = None, updatedBy: Option[String] = None, updatedOn: Option[Timestamp] = None, updatedOnTimeZone: Option[String] = None) extends Logged {
  def serialize(): Wallets.WalletSerialized = Wallets.WalletSerialized(
    address = this.address,
    partialMnemonics = Json.toJson(this.partialMnemonics).toString(),
    accountId = this.accountId,
    provisioned = this.provisioned,
    verified = this.verified,
    preference = this.preference,
    createdBy = this.createdBy,
    createdOn = this.createdOn,
    createdOnTimeZone = this.createdOnTimeZone,
    updatedBy = this.updatedBy,
    updatedOn = this.updatedOn,
    updatedOnTimeZone = this.updatedOnTimeZone)
}

object Wallets {

  implicit val module: String = constants.Module.MASTER_WALLET

  implicit val logger: Logger = Logger(this.getClass)

  case class WalletSerialized(address: String, partialMnemonics: String, accountId: String, provisioned: Option[Boolean], verified: Option[Boolean], preference: Int, createdBy: Option[String], createdOn: Option[Timestamp], createdOnTimeZone: Option[String], updatedBy: Option[String], updatedOn: Option[Timestamp], updatedOnTimeZone: Option[String]) extends Entity[String] {
    def deserialize: Wallet = Wallet(address = address, partialMnemonics = utilities.JSON.convertJsonStringToObject[Seq[String]](partialMnemonics), accountId = accountId, provisioned = provisioned, verified = verified, preference = preference, createdBy = createdBy, createdOn = createdOn, createdOnTimeZone = createdOnTimeZone, updatedBy = updatedBy, updatedOn = updatedOn, updatedOnTimeZone = updatedOnTimeZone)

    def id: String = address
  }

  class WalletTable(tag: Tag) extends Table[WalletSerialized](tag, "Wallet") with ModelTable[String] {

    def * = (address, partialMnemonics, accountId, provisioned.?, verified.?, preference, createdBy.?, createdOn.?, createdOnTimeZone.?, updatedBy.?, updatedOn.?, updatedOnTimeZone.?) <> (WalletSerialized.tupled, WalletSerialized.unapply)

    def address = column[String]("address", O.PrimaryKey)

    def partialMnemonics = column[String]("partialMnemonics")

    def accountId = column[String]("accountId")

    def provisioned = column[Boolean]("provisioned")

    def verified = column[Boolean]("verified")

    def preference = column[Int]("preference")

    def createdBy = column[String]("createdBy")

    def createdOn = column[Timestamp]("createdOn")

    def createdOnTimeZone = column[String]("createdOnTimeZone")

    def updatedBy = column[String]("updatedBy")

    def updatedOn = column[Timestamp]("updatedOn")

    def updatedOnTimeZone = column[String]("updatedOnTimeZone")

    override def id = address
  }

  lazy val TableQuery = new TableQuery(tag => new WalletTable(tag))
}

@Singleton
class Wallets @Inject()(
                         protected val databaseConfigProvider: DatabaseConfigProvider
                       )(implicit executionContext: ExecutionContext)
  extends GenericDaoImpl[Wallets.WalletTable, Wallets.WalletSerialized, String](
    databaseConfigProvider,
    Wallets.TableQuery,
    executionContext,
    Wallets.module,
    Wallets.logger
  ) {


  object Service {

    def add(address: String, partialMnemonics: Seq[String], accountId: String, provisioned: Option[Boolean], verified: Option[Boolean]): Future[String] = create(Wallet(address = address, partialMnemonics = partialMnemonics, accountId = accountId, provisioned = provisioned, verified = verified, preference = 0).serialize())

    def tryGet(address: String): Future[Wallet] = tryGetById(address).map(_.deserialize)

    def get(address: String): Future[Option[Wallet]] = getById(address).map(_.map(_.deserialize))

    def updateWallet(wallet: Wallet): Future[Unit] = update(wallet.serialize())

    def getAllByAccountId(username: String): Future[Seq[Wallet]] = filter(_.accountId === username).map(_.map(_.deserialize))

    def getByAccountId(username: String): Future[Option[Wallet]] = filter(_.accountId === username).map(_.map(_.deserialize).headOption)

    def tryGetByAccountID(accountId: String): Future[Wallet] = filterAndSortHead(_.accountId === accountId)(_.preference).map(_.deserialize)

    def deleteWallets(address: Seq[String]): Future[Unit] = deleteMultiple(address)

    def deleteWallet(address: String): Future[Int] = delete(address)

    def walletExists(address: String): Future[Boolean] = exists(address)
  }

}