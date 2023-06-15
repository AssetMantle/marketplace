package models.blockchain

import models.traits._
import play.api.Logger
import play.api.db.slick.DatabaseConfigProvider
import play.db.NamedDatabase
import schema.data.base.AccAddressData
import slick.jdbc.H2Profile.api._

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

case class Account(address: String, accountType: Option[String], accountNumber: Int, sequence: Int, publicKey: Option[Array[Byte]]) extends Entity[String] {

  def id: String = this.address

  def getAccAddressBytes: Array[Byte] = utilities.Crypto.convertAddressToAccAddressBytes(this.address)

  def getAccAddressData: AccAddressData = AccAddressData(this.getAccAddressBytes)
}


private[blockchain] object Accounts {

  class AccountTable(tag: Tag) extends Table[Account](tag, "Account") with ModelTable[String] {

    def * = (address, accountType.?, accountNumber, sequence, publicKey.?) <> (Account.tupled, Account.unapply)

    def address = column[String]("address", O.PrimaryKey)

    def accountType = column[String]("accountType")

    def accountNumber = column[Int]("accountNumber")

    def sequence = column[Int]("sequence")

    def publicKey = column[Array[Byte]]("publicKey")

    def id = address
  }
}

@Singleton
class Accounts @Inject()(
                          @NamedDatabase("explorer")
                          protected val dbConfigProvider: DatabaseConfigProvider,
                        )(implicit val executionContext: ExecutionContext)
  extends GenericDaoImpl[Accounts.AccountTable, Account, String]() {

  implicit val module: String = constants.Module.BLOCKCHAIN_ASSET

  implicit val logger: Logger = Logger(this.getClass)

  val tableQuery = new TableQuery(tag => new Accounts.AccountTable(tag))

  object Service {
    def tryGet(address: String): Future[Account] = tryGetById(address)

    def get(address: String): Future[Option[Account]] = getById(address)

  }

}