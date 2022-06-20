package models.master

import models.Trait.{Entity, GenericDaoImpl, IdentifyableTable, Logged}
import play.api.Logger
import play.api.db.slick.DatabaseConfigProvider
import play.api.i18n.Lang
import slick.jdbc.H2Profile.api._

import java.sql.Timestamp
import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

case class Account(id: String, passwordHash: String, salt: Array[Byte], iterations: Int, language: Lang, userType: String, createdBy: Option[String] = None, createdOn: Option[Timestamp] = None, createdOnTimeZone: Option[String] = None, updatedBy: Option[String] = None, updatedOn: Option[Timestamp] = None, updatedOnTimeZone: Option[String] = None) extends Logged

object Accounts {

  private implicit val module: String = constants.Module.MASTER_ACCOUNT

  private implicit val logger: Logger = Logger(this.getClass)

  case class AccountSerialized(id: String, passwordHash: String, salt: Array[Byte], iterations: Int, language: String, userType: String, createdBy: Option[String], createdOn: Option[Timestamp], createdOnTimeZone: Option[String], updatedBy: Option[String], updatedOn: Option[Timestamp], updatedOnTimeZone: Option[String]) extends Entity[String] {
    def deserialize: Account = Account(id = id, passwordHash = passwordHash, salt = salt, iterations = iterations, language = Lang(language), userType = userType, createdBy = createdBy, createdOn = createdOn, createdOnTimeZone = createdOnTimeZone, updatedBy = updatedBy, updatedOn = updatedOn, updatedOnTimeZone = updatedOnTimeZone)
  }

  class AccountTable(tag: Tag) extends Table[AccountSerialized](tag, "Account") with IdentifyableTable[String] {

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

  val TableQuery = new TableQuery(tag => new AccountTable(tag))
}

@Singleton
class Accounts @Inject()(
                          protected val databaseConfigProvider: DatabaseConfigProvider
                        )(implicit override val executionContext: ExecutionContext)
  extends GenericDaoImpl[Accounts.AccountTable, Accounts.AccountSerialized, String](databaseConfigProvider, Accounts.TableQuery, executionContext) {

  def serialize(account: Account): Accounts.AccountSerialized = Accounts.AccountSerialized(
    id = account.id,
    passwordHash = account.passwordHash,
    salt = account.salt,
    iterations = account.iterations,
    language = account.language.toString.stripPrefix("Lang(").stripSuffix(")").trim.split("_")(0),
    userType = account.userType,
    createdBy = account.createdBy,
    createdOn = account.createdOn,
    createdOnTimeZone = account.createdOnTimeZone,
    updatedBy = account.updatedBy,
    updatedOn = account.updatedOn,
    updatedOnTimeZone = account.updatedOnTimeZone)


  object Service {

    def add(username: String, password: String, language: Lang, userType: String) = {
      val salt = utilities.Secrets.getNewSalt
      val account = Account(
        id = username,
        passwordHash = utilities.Secrets.hashPassword(password = password, salt = salt, iterations = constants.Security.DefaultIterations),
        salt = salt,
        iterations = constants.Security.DefaultIterations,
        language = language,
        userType = userType)
      (for {
        _ <- create(serialize(account))
      } yield ()
        )
    }
    //
    //    def validateUsernamePasswordAndGetAccount(username: String, password: String): Future[(Boolean, Account)] = {
    //      val account = tryGet(username)
    //      for {
    //        account <- account
    //      } yield (utilities.Secrets.verifyPassword(password = password, passwordHash = account.passwordHash, salt = account.salt, iterations = account.iterations), account)
    //    }

    //    def validateAndUpdatePassword(username: String, oldPassword: String, newPassword: String): Future[Unit] = {
    //      val account = tryGet(username)
    //
    //      def verifyAndUpdate(account: Account) = if (utilities.Secrets.verifyPassword(password = oldPassword, passwordHash = account.passwordHash, salt = account.salt, iterations = account.iterations)) {
    //        updatePasswordHashByID(id = username, passwordHash = utilities.Secrets.hashPassword(password = newPassword, salt = account.salt, iterations = constants.Security.DefaultIterations))
    //      } else Future(throw new BaseException(constants.Response.INVALID_USERNAME_OR_PASSWORD))
    //
    //      for {
    //        account <- account
    //        _ <- verifyAndUpdate(account)
    //      } yield ()
    //    }
    //
    //    def updateOnForgotPassword(account: Account, newPassword: String): Future[Int] = updatePasswordHashByID(id = account.id, passwordHash = utilities.Secrets.hashPassword(password = newPassword, salt = account.salt, iterations = constants.Security.DefaultIterations))
    //
    //    def checkUsernameAvailable(username: String): Future[Boolean] = checkById(username).map(!_)
    //
    //    def tryGet(username: String): Future[Account] = tryGetById(username).map(_.deserialize)
    //
    //    def get(username: String): Future[Option[Account]] = getByID(username).map(_.map(_.deserialize))
    //
    //    def tryGetLanguage(id: String): Future[String] = tryGetLanguageById(id)
    //
    //    def getAccountType(id: String): Future[String] = getAccountTypeById(id)
    //
    //    def tryVerifyingUserType(id: String, accountType: String): Future[Boolean] = {
    //      getAccountType(id).map(accountTypeResult => accountTypeResult == accountType)
    //    }
    //
    //    def checkAccountExists(username: String): Future[Boolean] = checkById(username)

  }
}