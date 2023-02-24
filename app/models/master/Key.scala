package models.master

import models.Trait._
import org.bitcoinj.core.ECKey
import org.bitcoinj.crypto.ChildNumber
import play.api.Logger
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.json.Json
import slick.jdbc.H2Profile.api._

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

case class Key(accountId: String, address: String, lowercaseId: String, hdPath: Option[Seq[ChildNumber]], passwordHash: Array[Byte], salt: Array[Byte], iterations: Int, encryptedPrivateKey: Array[Byte], partialMnemonics: Option[Seq[String]], name: Option[String], retryCounter: Int, active: Boolean, backupUsed: Boolean, verified: Option[Boolean], identityIssued: Option[Boolean], createdBy: Option[String] = None, createdOnMillisEpoch: Option[Long] = None, updatedBy: Option[String] = None, updatedOnMillisEpoch: Option[Long] = None) extends Logging {
  def serialize(): Keys.KeySerialized = Keys.KeySerialized(
    accountId = this.accountId,
    address = this.address,
    lowercaseId = this.lowercaseId,
    hdPath = this.hdPath.fold[Option[String]](None)(x => Option(Json.toJson(x.map(_.toString)).toString())),
    passwordHash = this.passwordHash,
    salt = this.salt,
    iterations = this.iterations,
    encryptedPrivateKey = this.encryptedPrivateKey,
    partialMnemonics = this.partialMnemonics.fold[Option[String]](None)(x => Option(Json.toJson(x).toString())),
    name = this.name,
    retryCounter = this.retryCounter,
    active = this.active,
    backupUsed = this.backupUsed,
    verified = this.verified,
    identityIssued = this.identityIssued,
    createdBy = this.createdBy,
    createdOnMillisEpoch = this.createdOnMillisEpoch,
    updatedBy = this.updatedBy,
    updatedOnMillisEpoch = this.updatedOnMillisEpoch)

  def getECKey(password: String): Option[ECKey] = if (this.encryptedPrivateKey.nonEmpty) Option(ECKey.fromPrivate(utilities.Secrets.decryptData(this.encryptedPrivateKey, password)))
  else None
}

object Keys {

  implicit val module: String = constants.Module.MASTER_KEY

  implicit val logger: Logger = Logger(this.getClass)

  case class KeySerialized(accountId: String, address: String, lowercaseId: String, hdPath: Option[String], passwordHash: Array[Byte], salt: Array[Byte], iterations: Int, encryptedPrivateKey: Array[Byte], partialMnemonics: Option[String], name: Option[String], retryCounter: Int, active: Boolean, backupUsed: Boolean, verified: Option[Boolean], identityIssued: Option[Boolean], createdBy: Option[String], createdOnMillisEpoch: Option[Long], updatedBy: Option[String], updatedOnMillisEpoch: Option[Long]) extends Entity2[String, String] {
    def deserialize: Key = Key(
      accountId = accountId,
      address = address,
      lowercaseId = lowercaseId,
      hdPath = this.hdPath.fold[Option[Seq[ChildNumber]]](None)(x => Option(utilities.JSON.convertJsonStringToObject[Seq[String]](x).map(x => if (x.contains("H")) new ChildNumber(x.split("H")(0).toInt, true) else new ChildNumber(x.toInt)))),
      passwordHash = passwordHash,
      salt = salt,
      iterations = iterations,
      encryptedPrivateKey = encryptedPrivateKey,
      partialMnemonics = partialMnemonics.fold[Option[Seq[String]]](None)(x => Option(utilities.JSON.convertJsonStringToObject[Seq[String]](x))),
      name = name,
      retryCounter = retryCounter,
      active = active,
      backupUsed = backupUsed,
      verified = verified,
      identityIssued = identityIssued,
      createdBy = createdBy, createdOnMillisEpoch = createdOnMillisEpoch, updatedBy = updatedBy, updatedOnMillisEpoch = updatedOnMillisEpoch)

    def id1: String = accountId

    def id2: String = address
  }

  class KeyTable(tag: Tag) extends Table[KeySerialized](tag, "Key") with ModelTable2[String, String] {

    def * = (accountId, address, lowercaseId, hdPath.?, passwordHash, salt, iterations, encryptedPrivateKey, partialMnemonics.?, name.?, retryCounter, active, backupUsed, verified.?, identityIssued.?, createdBy.?, createdOnMillisEpoch.?, updatedBy.?, updatedOnMillisEpoch.?) <> (KeySerialized.tupled, KeySerialized.unapply)

    def accountId = column[String]("accountId", O.PrimaryKey)

    def address = column[String]("address", O.PrimaryKey)

    def lowercaseId = column[String]("lowercaseId")

    def hdPath = column[String]("hdPath")

    def passwordHash = column[Array[Byte]]("passwordHash")

    def salt = column[Array[Byte]]("salt")

    def iterations = column[Int]("iterations")

    def encryptedPrivateKey = column[Array[Byte]]("encryptedPrivateKey")

    def partialMnemonics = column[String]("partialMnemonics")

    def name = column[String]("name")

    def retryCounter = column[Int]("retryCounter")

    def active = column[Boolean]("active")

    def backupUsed = column[Boolean]("backupUsed")

    def verified = column[Boolean]("verified")

    def identityIssued = column[Boolean]("identityIssued")

    def createdBy = column[String]("createdBy")

    def createdOnMillisEpoch = column[Long]("createdOnMillisEpoch")

    def updatedBy = column[String]("updatedBy")

    def updatedOnMillisEpoch = column[Long]("updatedOnMillisEpoch")

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

    def addOnSignUp(accountId: String, address: String, hdPath: Seq[ChildNumber], partialMnemonics: Seq[String], name: String, retryCounter: Int, active: Boolean, backupUsed: Boolean, verified: Option[Boolean]): Future[Unit] = {
      val salt = utilities.Secrets.getNewSalt
      val key = Key(
        accountId = accountId,
        lowercaseId = accountId.toLowerCase,
        address = address,
        hdPath = Option(hdPath),
        passwordHash = Array[Byte](),
        salt = salt,
        iterations = constants.Security.DefaultIterations,
        partialMnemonics = Option(partialMnemonics),
        name = Option(name),
        encryptedPrivateKey = Array[Byte](),
        retryCounter = retryCounter,
        active = active,
        backupUsed = backupUsed,
        verified = verified,
        identityIssued = Option(false),
      )
      create(key.serialize())
    }

    def addManagedKey(accountId: String, address: String, hdPath: Seq[ChildNumber], password: String, privateKey: Array[Byte], partialMnemonics: Option[Seq[String]], name: String, retryCounter: Int, active: Boolean, backupUsed: Boolean, verified: Option[Boolean]): Future[Unit] = {
      val salt = utilities.Secrets.getNewSalt
      create(Key(
        accountId = accountId,
        lowercaseId = accountId.toLowerCase,
        address = address,
        hdPath = Option(hdPath),
        passwordHash = if (password != "") utilities.Secrets.hashPassword(password = password, salt = salt, iterations = constants.Security.DefaultIterations) else Array[Byte](),
        salt = salt,
        iterations = constants.Security.DefaultIterations,
        partialMnemonics = partialMnemonics,
        name = Option(name),
        encryptedPrivateKey = if (privateKey.nonEmpty) utilities.Secrets.encryptData(privateKey, password) else Array[Byte](),
        retryCounter = retryCounter,
        active = active,
        backupUsed = backupUsed,
        verified = verified,
        identityIssued = Option(false),
      ).serialize())
    }

    def addUnmanagedKey(accountId: String, address: String, password: String, name: String, retryCounter: Int, active: Boolean, backupUsed: Boolean, verified: Option[Boolean]): Future[Unit] = {
      val salt = utilities.Secrets.getNewSalt
      create(Key(
        accountId = accountId,
        lowercaseId = accountId.toLowerCase,
        address = address,
        hdPath = None,
        passwordHash = if (password != "") utilities.Secrets.hashPassword(password = password, salt = salt, iterations = constants.Security.DefaultIterations) else Array[Byte](),
        salt = salt,
        iterations = constants.Security.DefaultIterations,
        partialMnemonics = None,
        name = Option(name),
        encryptedPrivateKey = Array[Byte](),
        retryCounter = retryCounter,
        active = active,
        backupUsed = backupUsed,
        verified = verified,
        identityIssued = Option(false),
      ).serialize())
    }

    def addOnMigration(accountId: String, address: String, hdPath: Seq[ChildNumber], partialMnemonics: Seq[String], passwordHash: Array[Byte], salt: Array[Byte], iterations: Int, name: String, retryCounter: Int, active: Boolean, backupUsed: Boolean, verified: Option[Boolean]): Future[Unit] = {
      create(Key(
        accountId = accountId,
        address = address,
        lowercaseId = accountId.toLowerCase,
        hdPath = Option(hdPath),
        passwordHash = passwordHash,
        salt = salt,
        iterations = iterations,
        partialMnemonics = Option(partialMnemonics),
        name = Option(name),
        encryptedPrivateKey = Array[Byte](),
        retryCounter = retryCounter,
        active = active,
        backupUsed = backupUsed,
        verified = verified,
        identityIssued = Option(false),
      ).serialize())
    }

    def updateOnVerifyMnemonics(key: Key, password: String, privateKey: Array[Byte]): Future[Unit] = updateKey(key.copy(
      passwordHash = utilities.Secrets.hashPassword(password = password, salt = key.salt, iterations = key.iterations),
      encryptedPrivateKey = utilities.Secrets.encryptData(privateKey, password),
      verified = Option(true)
    ))

    def updateOnMigration(key: Key, password: String, privateKey: Array[Byte]): Future[Unit] = updateKey(key.copy(
      encryptedPrivateKey = utilities.Secrets.encryptData(privateKey, password),
      verified = Option(true)
    ))

    def validateUsernamePasswordAndGetKey(username: String, address: String, password: String): Future[(Boolean, Key)] = {
      val key = tryGetById1AndId2(username, address)
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

    def getAllActiveKeys(accountIds: Seq[String]): Future[Seq[Key]] = filter(x => x.accountId.inSet(accountIds) && x.active).map(_.map(_.deserialize))

    def tryGetActive(accountId: String): Future[Key] = filterHead(x => x.id1 === accountId && x.active).map(_.deserialize)

    def getActive(accountId: String): Future[Option[Key]] = filter(x => x.id1 === accountId && x.active).map(_.headOption.map(_.deserialize))

    def updateKey(key: Key): Future[Unit] = updateById1AndId2(key.serialize())

    def getActiveByAccountId(accountId: String): Future[Option[Key]] = filter(x => x.id1 === accountId && x.active).map(_.map(_.deserialize).headOption)

    def delete(accountId: String, address: String): Future[Int] = deleteById1AndId2(accountId, address)

    def tryGet(accountId: String, address: String): Future[Key] = tryGetById1AndId2(id1 = accountId, id2 = address).map(_.deserialize)

    def updateKeyName(accountId: String, address: String, keyName: String): Future[Unit] = {
      val key = tryGet(accountId = accountId, address = address)
      for {
        key <- key
        _ <- updateKey(key.copy(name = Option(keyName)))
      } yield ()
    }

    def fetchAllForId(accountId: String): Future[Seq[Key]] = filter(_.id1 === accountId).map(_.map(_.deserialize))

    def updateOnForgotPassword(accountId: String, address: String, lastWords: Seq[String], newPassword: String): Future[Unit] = {
      val key = tryGet(accountId = accountId, address = address)

      def getWallet(key: Key) = if (key.hdPath.isDefined && key.partialMnemonics.isDefined) {
        Future(utilities.Wallet.getWallet(mnemonics = key.partialMnemonics.get ++ lastWords, hdPath = key.hdPath.get))
      } else constants.Response.INVALID_ACTIVE_KEY.throwFutureBaseException()

      def updatePassword(key: Key, wallet: utilities.Wallet) = if (key.address == wallet.address) {
        updateById1AndId2(key.copy(
          passwordHash = utilities.Secrets.hashPassword(password = newPassword, salt = key.salt, iterations = constants.Security.DefaultIterations),
          encryptedPrivateKey = utilities.Secrets.encryptData(data = wallet.privateKey, secret = newPassword)
        ).serialize())
      } else constants.Response.INVALID_ACTIVE_KEY.throwFutureBaseException()

      for {
        key <- key
        wallet <- getWallet(key)
        _ <- updatePassword(key, wallet)
      } yield ()
    }

    def changePassword(accountId: String, address: String, oldPassword: String, newPassword: String): Future[Unit] = {
      val validateAndOldKey = validateActiveKeyUsernamePasswordAndGet(username = accountId, password = oldPassword)

      def updatePassword(passwordValidated: Boolean, oldKey: Key) = if (passwordValidated) {
        if (oldKey.encryptedPrivateKey.nonEmpty && oldKey.partialMnemonics.isDefined) {
          val decryptedPrivateKey = utilities.Secrets.decryptData(encryptedData = oldKey.encryptedPrivateKey, secret = oldPassword)
          updateById1AndId2(oldKey.copy(
            passwordHash = utilities.Secrets.hashPassword(password = newPassword, salt = oldKey.salt, iterations = constants.Security.DefaultIterations),
            encryptedPrivateKey = utilities.Secrets.encryptData(data = decryptedPrivateKey, secret = newPassword),
          ).serialize())
        } else constants.Response.INVALID_ACTIVE_KEY.throwFutureBaseException()
      } else constants.Response.INVALID_PASSWORD.throwFutureBaseException()

      for {
        (passwordValidated, oldKey) <- validateAndOldKey
        _ <- updatePassword(passwordValidated, oldKey)
      } yield ()
    }

    def changeActive(accountId: String, oldAddress: String, newAddress: String): Future[Unit] = {
      val oldKey = tryGet(accountId = accountId, address = oldAddress)
      val key = tryGet(accountId = accountId, address = newAddress)

      def updateKeys(oldKey: Key, newKey: Key) = if (newKey.encryptedPrivateKey.nonEmpty) {
        for {
          _ <- updateById1AndId2(oldKey.copy(active = false).serialize())
          _ <- updateById1AndId2(newKey.copy(active = true).serialize())
        } yield ()
      } else constants.Response.ACTIVATING_UNMANAGED_KEY.throwBaseException()

      for {
        oldKey <- oldKey
        key <- key
        _ <- updateKeys(oldKey = oldKey, newKey = key)
      } yield ()
    }

    def changeManagedToUnmanaged(accountId: String, address: String, password: String): Future[Unit] = {
      val validate = validateUsernamePasswordAndGetKey(username = accountId, address = address, password = password)

      def validateAndUpdate(validated: Boolean, key: Key) = if (validated) updateById1AndId2(key.copy(encryptedPrivateKey = Array[Byte](), partialMnemonics = None).serialize())
      else constants.Response.INVALID_PASSWORD.throwFutureBaseException()

      for {
        (validated, key) <- validate
        _ <- validateAndUpdate(validated, key)
      } yield ()
    }

    def checkVerifiedKeyExists(accountId: String): Future[Boolean] = filter(x => (x.accountId === accountId || x.lowercaseId === accountId.toLowerCase) && x.verified).map(_.nonEmpty)

    def deleteUnverifiedKeys(accountId: String): Future[Int] = {
      val verified: Option[Boolean] = null
      filterAndDelete(x => x.accountId === accountId && x.verified.? === verified)
    }

    def insertOrUpdateMultiple(keys: Seq[Keys.KeySerialized]): Future[Unit] = upsertMultiple(keys)

    def fetchAllActive: Future[Seq[Keys.KeySerialized]] = filter(_.active)

    def getNotIssuedIdentityAccountIDs: Future[Seq[String]] = filterAndSortWithPagination(offset = 0, limit = 250)(x => !x.identityIssued && x.verified && x.active)(_.accountId).map(_.map(_.accountId))

    def markIdentityIssuePending(accountIds: Seq[String]): Future[Int] = customUpdate(Keys.TableQuery.filter(x => !x.identityIssued && x.verified && x.active && x.accountId.inSet(accountIds)).map(_.identityIssued.?).update(null))

    def markIdentityIssued(accountIds: Seq[String]): Future[Int] = customUpdate(Keys.TableQuery.filter(x => x.identityIssued.?.isEmpty && x.verified && x.active && x.accountId.inSet(accountIds)).map(_.identityIssued).update(true))

  }

}