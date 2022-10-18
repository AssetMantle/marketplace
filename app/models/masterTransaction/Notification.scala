package models.masterTransaction

import models.Trait.{Entity, GenericDaoImpl, Logging, ModelTable}
import play.api.Logger
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.json.Json
import slick.jdbc.H2Profile.api._

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

case class Notification(id: String, accountID: Option[String], title: String, messageParameters: Seq[String], jsRoute: Option[String], read: Boolean = false, createdBy: Option[String] = None, createdOnMillisEpoch: Option[Long] = None, updatedOnMillisEpoch: Option[Long] = None, updatedBy: Option[String] = None) extends Logging {
  def getTitle: String = Seq("NOTIFICATION", this.title, "TITLE").mkString(".")

  def message: String = Seq("NOTIFICATION", this.title, "MESSAGE").mkString(".")

  def serialize(): Notifications.NotificationSerializable = Notifications.NotificationSerializable(id = this.id, accountID = this.accountID, title = title, messageParameters = Json.toJson(this.messageParameters).toString, jsRoute = this.jsRoute, read = this.read, createdOnMillisEpoch = this.createdOnMillisEpoch, createdBy = this.createdBy, updatedBy = this.updatedBy, updatedOnMillisEpoch = this.updatedOnMillisEpoch)

}

object Notifications {
  implicit val module: String = constants.Module.MASTER_TRANSACTION_NOTIFICATION

  implicit val logger: Logger = Logger(this.getClass)
  val TableQuery = new TableQuery(tag => new NotificationTable(tag))

  case class NotificationSerializable(id: String, accountID: Option[String], title: String, messageParameters: String, jsRoute: Option[String], read: Boolean, createdOnMillisEpoch: Option[Long], createdBy: Option[String], updatedOnMillisEpoch: Option[Long], updatedBy: Option[String]) extends Entity[String] {
    def deserialize(): Notification = Notification(id = id, accountID = accountID, title = title, messageParameters = utilities.JSON.convertJsonStringToObject[Seq[String]](messageParameters), jsRoute = jsRoute, read = read, createdOnMillisEpoch = createdOnMillisEpoch, createdBy = createdBy, updatedBy = updatedBy, updatedOnMillisEpoch = updatedOnMillisEpoch)
  }

  class NotificationTable(tag: Tag) extends Table[NotificationSerializable](tag, "Notification") with ModelTable[String] {

    def * = (id, accountID.?, title, messageParameters, jsRoute.?, read, createdOnMillisEpoch.?, createdBy.?, updatedOnMillisEpoch.?, updatedBy.?) <> (NotificationSerializable.tupled, NotificationSerializable.unapply)

    def id = column[String]("id", O.PrimaryKey)

    def accountID = column[String]("accountID")

    def title = column[String]("title")

    def messageParameters = column[String]("messageParameters")

    def jsRoute = column[String]("jsRoute")

    def read = column[Boolean]("read")

    def createdOnMillisEpoch = column[Long]("createdOnMillisEpoch")

    def createdBy = column[String]("createdBy")

    def updatedOnMillisEpoch = column[Long]("updatedOnMillisEpoch")

    def updatedBy = column[String]("updatedBy")

  }

}

@Singleton
class Notifications @Inject()(protected val databaseConfigProvider: DatabaseConfigProvider
                             )(implicit override val executionContext: ExecutionContext)
  extends GenericDaoImpl[Notifications.NotificationTable, Notifications.NotificationSerializable, String](
    databaseConfigProvider,
    Notifications.TableQuery,
    executionContext,
    Notifications.module,
    Notifications.logger
  ) {

  object Service {

    def add(accountID: String, notification: constants.Notification, parameters: String*)(routeParameters: String*): Future[String] = create(Notification(id = utilities.IdGenerator.getRandomHexadecimal, accountID = Option(accountID), title = notification.name, messageParameters = parameters, jsRoute = notification.route.fold[Option[String]](None)(x => Option(utilities.JsRoutes.getJsRouteString(x, routeParameters: _*)))).serialize())

    def get(accountID: String, pageNumber: Int): Future[Seq[Notification]] = filterAndSortWithPagination(offset = (pageNumber - 1) * constants.CommonConfig.Pagination.NotificationsPerPage, limit = constants.CommonConfig.Pagination.NotificationsPerPage)(_.accountID.? === Option(accountID))(_.createdOnMillisEpoch).map(_.map(_.deserialize()))

    def add(notification: constants.Notification, parameters: String*)(routeParameters: String*): Future[String] = create(Notification(id = utilities.IdGenerator.getRandomHexadecimal, accountID = None, title = notification.name, messageParameters = parameters, jsRoute = notification.route.fold[Option[String]](None)(x => Option(utilities.JsRoutes.getJsRouteString(x, routeParameters: _*)))).serialize())

    def getPublic(pageNumber: Int): Future[Seq[Notification]] = {
      val accountId: Option[String] = null
      filterAndSortWithPagination(offset = (pageNumber - 1) * constants.CommonConfig.Pagination.NotificationsPerPage, limit = constants.CommonConfig.Pagination.NotificationsPerPage)(_.accountID.? === accountId)(_.createdOnMillisEpoch).map(_.map(_.deserialize()))
    }

    def getNumberOfUnread(accountID: String): Future[Int] = filterAndCount(x => x.accountID === accountID && !x.read)

  }

}