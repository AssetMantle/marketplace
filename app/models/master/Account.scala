package models.master

import exceptions.BaseException
import models.Trait.{Entity, GenericDaoImpl, Logged, ModelTable}
import play.api.Logger
import play.api.db.slick.DatabaseConfigProvider
import play.api.i18n.Lang
import slick.jdbc.H2Profile.api._

import java.sql.Timestamp
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

case class Account(id: String, passwordHash: String, salt: Array[Byte], iterations: Int, language: Lang, accountType: String, createdBy: Option[String] = None, createdOn: Option[Timestamp] = None, createdOnTimeZone: Option[String] = None, updatedBy: Option[String] = None, updatedOn: Option[Timestamp] = None, updatedOnTimeZone: Option[String] = None) extends Logged {
  def serialize(): Accounts.AccountSerialized = Accounts.AccountSerialized(
    id = this.id,
    passwordHash = this.passwordHash,
    salt = this.salt,
    iterations = this.iterations,
    language = this.language.toString.stripPrefix("Lang(").stripSuffix(")").trim.split("_")(0),
    accountType = this.accountType,
    createdBy = this.createdBy,
    createdOn = this.createdOn,
    createdOnTimeZone = this.createdOnTimeZone,
    updatedBy = this.updatedBy,
    updatedOn = this.updatedOn,
    updatedOnTimeZone = this.updatedOnTimeZone)
}

object Accounts {

  implicit val module: String = constants.Module.MASTER_ACCOUNT

  implicit val logger: Logger = Logger(this.getClass)

  case class AccountSerialized(id: String, passwordHash: String, salt: Array[Byte], iterations: Int, language: String, accountType: String, createdBy: Option[String], createdOn: Option[Timestamp], createdOnTimeZone: Option[String], updatedBy: Option[String], updatedOn: Option[Timestamp], updatedOnTimeZone: Option[String]) extends Entity[String] {
    def deserialize: Account = Account(id = id, passwordHash = passwordHash, salt = salt, iterations = iterations, language = Lang(language), accountType = accountType, createdBy = createdBy, createdOn = createdOn, createdOnTimeZone = createdOnTimeZone, updatedBy = updatedBy, updatedOn = updatedOn, updatedOnTimeZone = updatedOnTimeZone)
  }

  class AccountTable(tag: Tag) extends Table[AccountSerialized](tag, "Account") with ModelTable[String] {

    def * = (id, passwordHash, salt, iterations, accountType, language, createdBy.?, createdOn.?, createdOnTimeZone.?, updatedBy.?, updatedOn.?, updatedOnTimeZone.?) <> (AccountSerialized.tupled, AccountSerialized.unapply)

    def id = column[String]("id", O.PrimaryKey)

    def passwordHash = column[String]("passwordHash")

    def salt = column[Array[Byte]]("salt")

    def iterations = column[Int]("iterations")

    def accountType = column[String]("accountType")

    def language = column[String]("language")

    def createdBy = column[String]("createdBy")

    def createdOn = column[Timestamp]("createdOn")

    def createdOnTimeZone = column[String]("createdOnTimeZone")

    def updatedBy = column[String]("updatedBy")

    def updatedOn = column[Timestamp]("updatedOn")

    def updatedOnTimeZone = column[String]("updatedOnTimeZone")

  }

  lazy val TableQuery = new TableQuery(tag => new AccountTable(tag))
}

@Singleton
class Accounts @Inject()(
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

    def add(username: String, password: String, language: Lang, accountType: String): Future[Unit] = {
      val salt = utilities.Secrets.getNewSalt
      val account = Account(
        id = username,
        passwordHash = utilities.Secrets.hashPassword(password = password, salt = salt, iterations = constants.Security.DefaultIterations),
        salt = salt,
        iterations = constants.Security.DefaultIterations,
        language = language,
        accountType = accountType)
      for {
        _ <- create(account.serialize())
      } yield ()
    }

    def validateUsernamePasswordAndGetAccount(username: String, password: String): Future[(Boolean, Account)] = {
      val account = tryGetById(username)
      for {
        account <- account
      } yield (utilities.Secrets.verifyPassword(password = password, passwordHash = account.passwordHash, salt = account.salt, iterations = account.iterations), account.deserialize)
    }

    def validateAndUpdatePassword(username: String, oldPassword: String, newPassword: String): Future[Unit] = {
      val account = tryGetById(username).map(_.deserialize)

      def verifyAndUpdate(account: Account) = if (utilities.Secrets.verifyPassword(password = oldPassword, passwordHash = account.passwordHash, salt = account.salt, iterations = account.iterations)) {
        update(account.copy(passwordHash = utilities.Secrets.hashPassword(password = newPassword, salt = account.salt, iterations = constants.Security.DefaultIterations)).serialize())
      } else Future(throw new BaseException(constants.Response.INVALID_USERNAME_OR_PASSWORD))

      for {
        account <- account
        _ <- verifyAndUpdate(account)
      } yield ()
    }

    def updateOnForgotPassword(account: Account, newPassword: String): Future[Unit] = update(account.copy(passwordHash = utilities.Secrets.hashPassword(password = newPassword, salt = account.salt, iterations = constants.Security.DefaultIterations)).serialize())

    def checkUsernameAvailable(username: String): Future[Boolean] = exists(username).map(!_)

    def tryGet(username: String): Future[Account] = tryGetById(username).map(_.deserialize)

    def get(username: String): Future[Option[Account]] = getById(username).map(_.map(_.deserialize))

    def tryGetLanguage(id: String): Future[Lang] = tryGetById(id).map(_.deserialize.language)

    def getAccountType(id: String): Future[String] = tryGetById(id).map(_.accountType)

    def tryVerifyingAccountType(id: String, accountType: String): Future[Boolean] = getAccountType(id).map(_ == accountType)

    def checkAccountExists(username: String): Future[Boolean] = exists(username)

  }
}