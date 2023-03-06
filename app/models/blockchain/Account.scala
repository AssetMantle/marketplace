package models.blockchain

import models.traits._
import play.api.Logger
import play.api.db.slick.DatabaseConfigProvider
import play.db.NamedDatabase
import slick.jdbc.H2Profile.api._

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

case class Account(address: String, accountType: Option[String], accountNumber: Int, sequence: Int, publicKey: Option[Array[Byte]], createdBy: Option[String] = None, createdOnMillisEpoch: Option[Long] = None, updatedBy: Option[String] = None, updatedOnMillisEpoch: Option[Long] = None) extends Logging with Entity[String] {

  def id = this.address
}


object Accounts {


  implicit val module: String = constants.Module.BLOCKCHAIN_ASSET

  implicit val logger: Logger = Logger(this.getClass)

  class AccountTable(tag: Tag) extends Table[Account](tag, "Account") with ModelTable[String] {

    def * = (address, accountType.?, accountNumber, sequence, publicKey.?, createdBy.?, createdOnMillisEpoch.?, updatedBy.?, updatedOnMillisEpoch.?) <> (Account.tupled, Account.unapply)

    def address = column[String]("address", O.PrimaryKey)

    def accountType = column[String]("accountType")

    def accountNumber = column[Int]("accountNumber")

    def sequence = column[Int]("sequence")

    def publicKey = column[Array[Byte]]("publicKey")

    def createdBy = column[String]("createdBy")

    def createdOnMillisEpoch = column[Long]("createdOnMillisEpoch")

    def updatedBy = column[String]("updatedBy")

    def updatedOnMillisEpoch = column[Long]("updatedOnMillisEpoch")

    def id = address
  }

  val TableQuery = new TableQuery(tag => new AccountTable(tag))
}

@Singleton
class Accounts @Inject()(
                          @NamedDatabase("explorer")
                          protected val databaseConfigProvider: DatabaseConfigProvider
                        )(implicit override val executionContext: ExecutionContext)
  extends GenericDaoImpl[Accounts.AccountTable, Account, String](
    databaseConfigProvider,
    Accounts.TableQuery,
    executionContext,
    Accounts.module,
    Accounts.logger
  ) {

  object Service {
    def tryGet(address: String): Future[Account] = tryGetById(address)

    def get(address: String): Future[Option[Account]] = getById(address)

  }

}