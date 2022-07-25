package models.masterTransaction

import models.Trait.{Entity, GenericDaoImpl, Logged, ModelTable}
import play.api.Logger
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.H2Profile.api._

import java.sql.Timestamp
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

case class PushNotificationToken(accountId: String, token: String, createdBy: Option[String] = None, createdOn: Option[Timestamp] = None, createdOnTimeZone: Option[String] = None, updatedBy: Option[String] = None, updatedOn: Option[Timestamp] = None, updatedOnTimeZone: Option[String] = None) extends Logged with Entity[String] {
  def id: String = accountId
}

object PushNotificationTokens {
  implicit val module: String = constants.Module.MASTER_TRANSACTION_PUSH_NOTIFICATION_TOKEN

  implicit val logger: Logger = Logger(this.getClass)

  class PushNotificationTokenTable(tag: Tag) extends Table[PushNotificationToken](tag, "PushNotificationToken") with ModelTable[String] {

    def * = (accountId, token, createdBy.?, createdOn.?, createdOnTimeZone.?, updatedBy.?, updatedOn.?, updatedOnTimeZone.?) <> (PushNotificationToken.tupled, PushNotificationToken.unapply)

    def accountId = column[String]("accountId", O.PrimaryKey)

    def token = column[String]("token")

    def createdBy = column[String]("createdBy")

    def createdOn = column[Timestamp]("createdOn")

    def createdOnTimeZone = column[String]("createdOnTimeZone")

    def updatedBy = column[String]("updatedBy")

    def updatedOn = column[Timestamp]("updatedOn")

    def updatedOnTimeZone = column[String]("updatedOnTimeZone")

    def id = accountId
  }

  val TableQuery = new TableQuery(tag => new PushNotificationTokenTable(tag))
}

@Singleton
class PushNotificationTokens @Inject()(
                               protected val databaseConfigProvider: DatabaseConfigProvider
                             )(implicit override val executionContext: ExecutionContext)
  extends GenericDaoImpl[PushNotificationTokens.PushNotificationTokenTable, PushNotificationToken, String](
    databaseConfigProvider,
    PushNotificationTokens.TableQuery,
    executionContext,
    PushNotificationTokens.module,
    PushNotificationTokens.logger
  ) {

  object Service {

    def upsert(id: String, token: String): Future[Unit] = {
      for {
        _ <- delete(id)
        _ <- create(PushNotificationToken(id, token))
      } yield ()
    }

    def getPushNotificationToken(id: String): Future[Option[String]] = getById(id).map(_.map(_.token))

    def deleteByID(id: String): Future[Int] = delete(id)

  }

}

