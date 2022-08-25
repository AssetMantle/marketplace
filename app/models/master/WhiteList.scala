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

case class WhiteList(id: String, ownerId: String, name: String, description: String, maxMembers: Int, createdBy: Option[String] = None, createdOn: Option[Timestamp] = None, createdOnTimeZone: Option[String] = None, updatedBy: Option[String] = None, updatedOn: Option[Timestamp] = None, updatedOnTimeZone: Option[String] = None) extends Logged {

  def serialize(): WhiteLists.WhiteListSerialized = WhiteLists.WhiteListSerialized(
    id = this.id,
    ownerId = this.ownerId,
    name = this.name,
    description = this.description,
    maxMembers = this.maxMembers,
    createdBy = this.createdBy,
    createdOn = this.createdOn,
    createdOnTimeZone = this.createdOnTimeZone,
    updatedBy = this.updatedBy,
    updatedOn = this.updatedOn,
    updatedOnTimeZone = this.updatedOnTimeZone)
}


object WhiteLists {

  implicit val module: String = constants.Module.MASTER_WHITE_LIST

  implicit val logger: Logger = Logger(this.getClass)

  case class WhiteListSerialized(id: String, ownerId: String, name: String, description: String, maxMembers: Int, createdBy: Option[String], createdOn: Option[Timestamp], createdOnTimeZone: Option[String], updatedBy: Option[String], updatedOn: Option[Timestamp], updatedOnTimeZone: Option[String]) extends Entity[String] {
    def deserialize: WhiteList = WhiteList(id = id, ownerId = ownerId, name = name, description = description, maxMembers = maxMembers, createdBy = createdBy, createdOn = createdOn, createdOnTimeZone = createdOnTimeZone, updatedBy = updatedBy, updatedOn = updatedOn, updatedOnTimeZone = updatedOnTimeZone)
  }

  class WhiteListTable(tag: Tag) extends Table[WhiteListSerialized](tag, "WhiteList") with ModelTable[String] {

    def * = (id, ownerId, name, description, maxMembers, createdBy.?, createdOn.?, createdOnTimeZone.?, updatedBy.?, updatedOn.?, updatedOnTimeZone.?) <> (WhiteListSerialized.tupled, WhiteListSerialized.unapply)

    def id = column[String]("id", O.PrimaryKey)

    def ownerId = column[String]("ownerId")

    def name = column[String]("name")

    def description = column[String]("description")

    def maxMembers = column[Int]("maxMembers")

    def createdBy = column[String]("createdBy")

    def createdOn = column[Timestamp]("createdOn")

    def createdOnTimeZone = column[String]("createdOnTimeZone")

    def updatedBy = column[String]("updatedBy")

    def updatedOn = column[Timestamp]("updatedOn")

    def updatedOnTimeZone = column[String]("updatedOnTimeZone")

  }

  val TableQuery = new TableQuery(tag => new WhiteListTable(tag))
}

@Singleton
class WhiteLists @Inject()(
                            protected val databaseConfigProvider: DatabaseConfigProvider
                          )(implicit override val executionContext: ExecutionContext)
  extends GenericDaoImpl[WhiteLists.WhiteListTable, WhiteLists.WhiteListSerialized, String](
    databaseConfigProvider,
    WhiteLists.TableQuery,
    executionContext,
    WhiteLists.module,
    WhiteLists.logger
  ) {


  object Service {

    def addWhiteList(ownerId: String, name: String, description: String, maxMembers: Int): Future[String] = {
      val id = utilities.IdGenerator.getRandomHexadecimal
      for {
        _ <- create(WhiteList(id = id, ownerId = ownerId, name = name, description = description, maxMembers = maxMembers).serialize())
      } yield id
    }

    def tryGet(id: String): Future[WhiteList] = tryGetById(id).map(_.deserialize)

    def getByOwner(ownerId: String): Future[Seq[WhiteList]] = filter(_.ownerId === ownerId).map(_.map(_.deserialize))

    def deleteById(id: String): Future[Int] = delete(id)

  }
}