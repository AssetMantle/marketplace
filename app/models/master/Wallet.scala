package models.master

import models.master.Wallets.WalletTable
import models.traits._
import play.api.Logger
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.json.Json
import slick.jdbc.H2Profile.api._

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

case class Wallet(address: String, partialMnemonics: Seq[String], accountId: String, provisioned: Option[Boolean], verified: Option[Boolean], preference: Int, createdBy: Option[String] = None, createdOnMillisEpoch: Option[Long] = None, updatedBy: Option[String] = None, updatedOnMillisEpoch: Option[Long] = None) extends Logging {
  def serialize(): Wallets.WalletSerialized = Wallets.WalletSerialized(
    address = this.address,
    partialMnemonics = Json.toJson(this.partialMnemonics).toString(),
    accountId = this.accountId,
    provisioned = this.provisioned,
    verified = this.verified,
    preference = this.preference,
    createdBy = this.createdBy,
    createdOnMillisEpoch = this.createdOnMillisEpoch,
    updatedBy = this.updatedBy,
    updatedOnMillisEpoch = this.updatedOnMillisEpoch)
}

private[master] object Wallets {

  case class WalletSerialized(address: String, partialMnemonics: String, accountId: String, provisioned: Option[Boolean], verified: Option[Boolean], preference: Int, createdBy: Option[String], createdOnMillisEpoch: Option[Long], updatedBy: Option[String], updatedOnMillisEpoch: Option[Long]) extends Entity[String] {
    def deserialize()(implicit module: String, logger: Logger): Wallet = Wallet(address = address, partialMnemonics = utilities.JSON.convertJsonStringToObject[Seq[String]](partialMnemonics), accountId = accountId, provisioned = provisioned, verified = verified, preference = preference, createdBy = createdBy, createdOnMillisEpoch = createdOnMillisEpoch, updatedBy = updatedBy, updatedOnMillisEpoch = updatedOnMillisEpoch)

    def id: String = address
  }

  class WalletTable(tag: Tag) extends Table[WalletSerialized](tag, "Wallet") with ModelTable[String] {

    def * = (address, partialMnemonics, accountId, provisioned.?, verified.?, preference, createdBy.?, createdOnMillisEpoch.?, updatedBy.?, updatedOnMillisEpoch.?) <> (WalletSerialized.tupled, WalletSerialized.unapply)

    def address = column[String]("address", O.PrimaryKey)

    def partialMnemonics = column[String]("partialMnemonics")

    def accountId = column[String]("accountId")

    def provisioned = column[Boolean]("provisioned")

    def verified = column[Boolean]("verified")

    def preference = column[Int]("preference")

    def createdBy = column[String]("createdBy")

    def createdOnMillisEpoch = column[Long]("createdOnMillisEpoch")

    def updatedBy = column[String]("updatedBy")

    def updatedOnMillisEpoch = column[Long]("updatedOnMillisEpoch")

    override def id = address
  }
}

@Singleton
class Wallets @Inject()(
                         protected val dbConfigProvider: DatabaseConfigProvider,
                       )(implicit executionContext: ExecutionContext)
  extends GenericDaoImpl[Wallets.WalletTable, Wallets.WalletSerialized, String]() {

  implicit val module: String = constants.Module.MASTER_WALLET

  implicit val logger: Logger = Logger(this.getClass)

  lazy val tableQuery = new TableQuery(tag => new WalletTable(tag))

  object Service {

    def add(address: String, partialMnemonics: Seq[String], accountId: String, provisioned: Option[Boolean], verified: Option[Boolean]): Future[String] = create(Wallet(address = address, partialMnemonics = partialMnemonics, accountId = accountId, provisioned = provisioned, verified = verified, preference = 0).serialize()).map(_.accountId)

    def tryGet(address: String): Future[Wallet] = tryGetById(address).map(_.deserialize)

    def get(address: String): Future[Option[Wallet]] = getById(address).map(_.map(_.deserialize))

    def update(wallet: Wallet): Future[Unit] = updateById(wallet.serialize())

    def getAllByAccountId(username: String): Future[Seq[Wallet]] = filter(_.accountId === username).map(_.map(_.deserialize))

    def getByAccountId(username: String): Future[Option[Wallet]] = filter(_.accountId === username).map(_.map(_.deserialize).headOption)

    def walletExists(address: String): Future[Boolean] = exists(address)
  }

}