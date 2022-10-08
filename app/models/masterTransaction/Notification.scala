package models.masterTransaction

import java.sql.Timestamp
import javax.inject.{Inject, Singleton}
import models.Trait.{Entity, Logged}
import models.common.Notification.Template
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.json.Json
import play.api.Logger
import models.Trait.{GenericDaoImpl, ModelTable}
import slick.jdbc.H2Profile.api._
import scala.concurrent.{ExecutionContext, Future}

case class Notification(id: String, accountID: Option[String], template: Template, jsRoute: Option[String], read: Boolean = false, createdBy: Option[String] = None, createdOn: Option[Timestamp] = None, createdOnTimeZone: Option[String] = None, updatedOn: Option[Timestamp] = None, updatedBy: Option[String] = None, updatedOnTimeZone: Option[String] = None) extends Logged {
  def title: String = Seq("NOTIFICATION", this.template.name, "TITLE").mkString(".")

  def message: String = Seq("NOTIFICATION", this.template.name, "MESSAGE").mkString(".")

  def serialize(): Notifications.NotificationSerializable = Notifications.NotificationSerializable(id = this.id, accountID = this.accountID, template = Json.toJson(this.template).toString, jsRoute = this.jsRoute, read = this.read, createdOn = this.createdOn, createdBy = this.createdBy, createdOnTimeZone = this.createdOnTimeZone, updatedBy = this.updatedBy, updatedOn = this.updatedOn, updatedOnTimeZone = this.updatedOnTimeZone)

}

object Notifications {
  implicit val module: String = constants.Module.MASTER_TRANSACTION_NOTIFICATION

  implicit val logger: Logger = Logger(this.getClass)

  case class NotificationSerializable(id: String, accountID: Option[String], template: String, jsRoute: Option[String], read: Boolean, createdOn: Option[Timestamp], createdBy: Option[String], createdOnTimeZone: Option[String], updatedOn: Option[Timestamp], updatedBy: Option[String], updatedOnTimeZone: Option[String]) extends Entity[String] {
    def deserialize(): Notification = Notification(id = id, accountID = accountID, template = utilities.JSON.convertJsonStringToObject[Template](template), jsRoute = jsRoute, read = read, createdOn = createdOn, createdBy = createdBy, createdOnTimeZone = createdOnTimeZone, updatedBy = updatedBy, updatedOn = updatedOn, updatedOnTimeZone = updatedOnTimeZone)
  }

  class NotificationTable(tag: Tag) extends Table[NotificationSerializable](tag, "Notification") with ModelTable[String] {

    def * = (id, accountID.?, template, jsRoute.?, read, createdOn.?, createdBy.?, createdOnTimeZone.?, updatedOn.?, updatedBy.?, updatedOnTimeZone.?) <> (NotificationSerializable.tupled, NotificationSerializable.unapply)

    def id = column[String]("id", O.PrimaryKey)

    def accountID = column[String]("accountID")

    def template = column[String]("template")

    def jsRoute = column[String]("jsRoute")

    def read = column[Boolean]("read")

    def createdOn = column[Timestamp]("createdOn")

    def createdBy = column[String]("createdBy")

    def createdOnTimeZone = column[String]("createdOnTimeZone")

    def updatedOn = column[Timestamp]("updatedOn")

    def updatedBy = column[String]("updatedBy")

    def updatedOnTimeZone = column[String]("updatedOnTimeZone")
  }

  val TableQuery = new TableQuery(tag => new NotificationTable(tag))

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

    def add(accountID: String, notification: constants.Notification, parameters: String*)(routeParameters: String*): Future[String] = create(Notification(id = utilities.IdGenerator.getRandomHexadecimal, accountID = Option(accountID), template = Template(name = notification.name, parameters = parameters), jsRoute = notification.route.fold[Option[String]](None)(x => Option(utilities.JsRoutes.getJsRouteString(x, routeParameters: _*)))).serialize())

    def get(accountID: String, pageNumber: Int): Future[Seq[Notification]] = filterAndSortWithPagination(offset = (pageNumber - 1) * constants.CommonConfig.Pagination.NotificationsPerPage, limit = constants.CommonConfig.Pagination.NotificationsPerPage)(_.accountID.? === Option(accountID))(_.createdOn).map(_.map(_.deserialize()))

    def add(notification: constants.Notification, parameters: String*)(routeParameters: String*): Future[String] = create(Notification(id = utilities.IdGenerator.getRandomHexadecimal, accountID = None, template = Template(name = notification.name, parameters = parameters), jsRoute = notification.route.fold[Option[String]](None)(x => Option(utilities.JsRoutes.getJsRouteString(x, routeParameters: _*)))).serialize())

    def getPublic(pageNumber: Int): Future[Seq[Notification]] = {
      val accountId: Option[String] = null
      filterAndSortWithPagination(offset = (pageNumber - 1) * constants.CommonConfig.Pagination.NotificationsPerPage, limit = constants.CommonConfig.Pagination.NotificationsPerPage)(_.accountID.? === accountId)(_.createdOn).map(_.map(_.deserialize()))
    }

    def getNumberOfUnread(accountID: String): Future[Int] = filterAndCount(x => x.accountID === accountID && !x.read)

  }

}