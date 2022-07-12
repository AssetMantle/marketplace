package models.master

import models.Trait._
import play.api.Logger
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.json.Json
import slick.jdbc.H2Profile.api._

import java.sql.Timestamp
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

case class Key(accountId: String, address: String, passwordHash: Array[Byte], salt: Array[Byte], iterations: Int, encryptedPrivateKey: Option[Array[Byte]], partialMnemonics: Option[Seq[String]], retryCounter: Int, active: Boolean, backupUsed: Boolean, verified: Option[Boolean], createdBy: Option[String] = None, createdOn: Option[Timestamp] = None, createdOnTimeZone: Option[String] = None, updatedBy: Option[String] = None, updatedOn: Option[Timestamp] = None, updatedOnTimeZone: Option[String] = None) extends Logged {
  def serialize(): Keys.KeySerialized = Keys.KeySerialized(
    accountId = this.accountId,
    address = this.address,
    passwordHash = this.passwordHash,
    salt = this.salt,
    iterations = this.iterations,
    encryptedPrivateKey = this.encryptedPrivateKey,
    partialMnemonics = this.partialMnemonics.fold[Option[String]](None)(x => Option(Json.toJson(x).toString())),
    retryCounter = this.retryCounter,
    active = this.active,
    backupUsed = this.backupUsed,
    verified = this.verified,
    createdBy = this.createdBy,
    createdOn = this.createdOn,
    createdOnTimeZone = this.createdOnTimeZone,
    updatedBy = this.updatedBy,
    updatedOn = this.updatedOn,
    updatedOnTimeZone = this.updatedOnTimeZone)
}

object Keys {

  implicit val module: String = constants.Module.MASTER_KEY

  implicit val logger: Logger = Logger(this.getClass)

  case class KeySerialized(accountId: String, address: String, passwordHash: Array[Byte], salt: Array[Byte], iterations: Int, encryptedPrivateKey: Option[Array[Byte]], partialMnemonics: Option[String], retryCounter: Int, active: Boolean, backupUsed: Boolean, verified: Option[Boolean], createdBy: Option[String], createdOn: Option[Timestamp], createdOnTimeZone: Option[String], updatedBy: Option[String], updatedOn: Option[Timestamp], updatedOnTimeZone: Option[String]) extends Entity2[String, String] {
    def deserialize: Key = Key(accountId = accountId, address = address, passwordHash = passwordHash, salt = salt, iterations = iterations, encryptedPrivateKey = encryptedPrivateKey, partialMnemonics = partialMnemonics.fold[Option[Seq[String]]](None)(x => Option(utilities.JSON.convertJsonStringToObject[Seq[String]](x))), retryCounter = retryCounter, active = active, backupUsed = backupUsed, verified = verified, createdBy = createdBy, createdOn = createdOn, createdOnTimeZone = createdOnTimeZone, updatedBy = updatedBy, updatedOn = updatedOn, updatedOnTimeZone = updatedOnTimeZone)

    def id1: String = accountId

    def id2: String = address
  }

  class KeyTable(tag: Tag) extends Table[KeySerialized](tag, "Key") with ModelTable2[String, String] {

    def * = (accountId, address, passwordHash, salt, iterations, encryptedPrivateKey.?, partialMnemonics.?, retryCounter, active, backupUsed, verified.?, createdBy.?, createdOn.?, createdOnTimeZone.?, updatedBy.?, updatedOn.?, updatedOnTimeZone.?) <> (KeySerialized.tupled, KeySerialized.unapply)

    def accountId = column[String]("accountId", O.PrimaryKey)

    def address = column[String]("address", O.PrimaryKey)

    def passwordHash = column[Array[Byte]]("passwordHash")

    def salt = column[Array[Byte]]("salt")

    def iterations = column[Int]("iterations")

    def encryptedPrivateKey = column[Array[Byte]]("encryptedPrivateKey")

    def partialMnemonics = column[String]("partialMnemonics")

    def retryCounter = column[Int]("retryCounter")

    def active = column[Boolean]("active")

    def backupUsed = column[Boolean]("backupUsed")

    def verified = column[Boolean]("verified")

    def createdBy = column[String]("createdBy")

    def createdOn = column[Timestamp]("createdOn")

    def createdOnTimeZone = column[String]("createdOnTimeZone")

    def updatedBy = column[String]("updatedBy")

    def updatedOn = column[Timestamp]("updatedOn")

    def updatedOnTimeZone = column[String]("updatedOnTimeZone")

    override def id1 = accountId

    override def id2 = address
  }

  val TableQuery = new TableQuery(tag => new KeyTable(tag))
}

@Singleton
class Keys @Inject()(
                      protected val databaseConfigProvider: DatabaseConfigProvider
                    )(implicit executionContext: ExecutionContext)
  extends GenericDaoImpl2[Keys.KeyTable, Keys.KeySerialized, String, String](
    databaseConfigProvider,
    Keys.TableQuery,
    executionContext,
    Keys.module,
    Keys.logger
  ) {


  object Service {

    def add(accountId: String, address: String, password: String, privateKey: Option[Array[Byte]], partialMnemonics: Option[Seq[String]], retryCounter: Int, active: Boolean, backupUsed: Boolean, verified: Option[Boolean]): Future[Unit] = {
      val salt = utilities.Secrets.getNewSalt
      create(Key(
        accountId = accountId,
        address = address,
        passwordHash = utilities.Secrets.hashPassword(password = password, salt = salt, iterations = constants.Security.DefaultIterations),
        salt = salt,
        iterations = constants.Security.DefaultIterations,
        partialMnemonics = partialMnemonics,
        encryptedPrivateKey = privateKey.fold[Option[Array[Byte]]](None)(x => Option(utilities.Secrets.encryptData(x, password))),
        retryCounter = retryCounter,
        active = active,
        backupUsed = backupUsed,
        verified = verified
      ).serialize())
    }

    def validateUsernamePasswordAndGetKey(username: String, address: String, password: String): Future[(Boolean, Key)] = {
      val key = tryGetById(username, address)
      for {
        key <- key
      } yield (utilities.Secrets.verifyPassword(password = password, passwordHash = key.passwordHash, salt = key.salt, iterations = key.iterations), key.deserialize)
    }

    def validateActiveKeyUsernamePasswordAndGet(username: String, password: String): Future[(Boolean, Key)] = {
      val key = tryGetActive(username)
      for {
        key <- key
      } yield (utilities.Secrets.verifyPassword(password = password, passwordHash = key.passwordHash, salt = key.salt, iterations = key.iterations), key)
    }

    def tryGetActive(accountId: String): Future[Key] = filterHead(x => x.id1 === accountId && x.active).map(_.deserialize)

    def updateKey(key: Key): Future[Unit] = update(key.serialize())

    def deleteKey(accountId: String, address: String): Future[Unit] = delete(accountId, address)
  }

}