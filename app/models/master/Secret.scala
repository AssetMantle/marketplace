package models.master

import models.master.Secrets.SecretTable
import models.traits._
import play.api.Logger
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.H2Profile.api._

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

case class Secret(id: String, secret: String, salt: String, createdBy: Option[String] = None, createdOnMillisEpoch: Option[Long] = None, updatedBy: Option[String] = None, updatedOnMillisEpoch: Option[Long] = None) extends Logging with Entity[String] {

  def decrypt: Array[Byte] = utilities.Secrets.decryptData(utilities.Secrets.base64Decoder(this.secret), constants.CommonConfig.AppSecret ++ utilities.Secrets.base64Decoder(this.salt))

  def decryptedString: String = new String(this.decrypt)

}

private[master] object Secrets {

  class SecretTable(tag: Tag) extends Table[Secret](tag, "Secret") with ModelTable[String] {

    def * = (id, secret, salt, createdBy.?, createdOnMillisEpoch.?, updatedBy.?, updatedOnMillisEpoch.?) <> (Secret.tupled, Secret.unapply)

    def id = column[String]("id", O.PrimaryKey)

    def secret = column[String]("secret")

    def salt = column[String]("salt")

    def createdBy = column[String]("createdBy")

    def createdOnMillisEpoch = column[Long]("createdOnMillisEpoch")

    def updatedBy = column[String]("updatedBy")

    def updatedOnMillisEpoch = column[Long]("updatedOnMillisEpoch")
  }

}

@Singleton
class Secrets @Inject()(
                         protected val dbConfigProvider: DatabaseConfigProvider,
                       )(implicit val executionContext: ExecutionContext)
  extends GenericDaoImpl[Secrets.SecretTable, Secret, String]() {

  implicit val module: String = constants.Module.MASTERS_SECRET

  implicit val logger: Logger = Logger(this.getClass)

  val tableQuery = new TableQuery(tag => new SecretTable(tag))

  private val memoKey = "MEMO_SIGNER"

  private val mantlePlaceKey = "MANTLE_PLACE"

  private object Service {

    def fetchAll: Future[Seq[Secret]] = getAll

  }

  object Utility {

    private def getKeyValue(allKeys: Seq[Secret], key: String): String = if (allKeys.exists(_.id == key)) allKeys.find(_.id == key).get.decryptedString else constants.Response.SECRET_KEY_ID_NOT_FOUND.throwBaseException()

    private def set(allKeys: Seq[Secret]): Unit = constants.Secret.setSecrets(
      memoSignerSeeds = getKeyValue(allKeys, memoKey).split(" "),
      mantlePlaceSeeds = getKeyValue(allKeys, mantlePlaceKey).split(" ")
    )

    def setAll(): Future[Unit] = {
      //      val seeds = "comfort broccoli urban cheap noise income ensure wheat maze cement panel clinic opinion acoustic select sentence code purchase casual dose brown fish salt coral"
      //      val privateKey = "Jj%Sr!xrC9G!xKS5^yF!UPYa!DkWo@N*R^xjF6vC@nAL@zaD24WqHfbTZ^QbYFsj^^j9LExb3@Rt9Yfj!4amcHY%5$bC#SU$DorA6Hv6h2i%bh%M$XFfDVVpW!fJd"
      //      val salt = "a!DkWo@N*R^xjF6vC@nAL@zaD24WqHfbTZ"
      //      val encryptData = utilities.Secrets.encryptData(seeds.getBytes, privateKey.getBytes ++ salt.getBytes)
      //      val encryptDataB64 = utilities.Secrets.base64Encoder(encryptData)
      //      val saltB64 = utilities.Secrets.base64Encoder(salt.getBytes)
      //      println(encryptDataB64)
      //      println(saltB64)

      val allKeys = Service.fetchAll

      for {
        allKeys <- allKeys
      } yield set(allKeys)
    }

  }

}