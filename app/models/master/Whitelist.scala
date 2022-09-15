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

case class Whitelist(id: String, ownerId: String, name: String, description: String, maxMembers: Int, startEpoch: Int, endEpoch: Int, createdBy: Option[String] = None, createdOn: Option[Timestamp] = None, createdOnTimeZone: Option[String] = None, updatedBy: Option[String] = None, updatedOn: Option[Timestamp] = None, updatedOnTimeZone: Option[String] = None) extends Logged {

  def serialize(): Whitelists.WhitelistSerialized = Whitelists.WhitelistSerialized(
    id = this.id,
    ownerId = this.ownerId,
    name = this.name,
    description = this.description,
    maxMembers = this.maxMembers,
    startEpoch = this.startEpoch,
    endEpoch = this.endEpoch,
    createdBy = this.createdBy,
    createdOn = this.createdOn,
    createdOnTimeZone = this.createdOnTimeZone,
    updatedBy = this.updatedBy,
    updatedOn = this.updatedOn,
    updatedOnTimeZone = this.updatedOnTimeZone)
}


object Whitelists {

  implicit val module: String = constants.Module.MASTER_WHITELIST

  implicit val logger: Logger = Logger(this.getClass)

  case class WhitelistSerialized(id: String, ownerId: String, name: String, description: String, maxMembers: Int, startEpoch: Int, endEpoch: Int, createdBy: Option[String], createdOn: Option[Timestamp], createdOnTimeZone: Option[String], updatedBy: Option[String], updatedOn: Option[Timestamp], updatedOnTimeZone: Option[String]) extends Entity[String] {
    def deserialize: Whitelist = Whitelist(id = id, ownerId = ownerId, name = name, description = description, maxMembers = maxMembers, startEpoch = startEpoch, endEpoch = endEpoch, createdBy = createdBy, createdOn = createdOn, createdOnTimeZone = createdOnTimeZone, updatedBy = updatedBy, updatedOn = updatedOn, updatedOnTimeZone = updatedOnTimeZone)
  }

  class WhitelistTable(tag: Tag) extends Table[WhitelistSerialized](tag, "Whitelist") with ModelTable[String] {

    def * = (id, ownerId, name, description, maxMembers, startEpoch, endEpoch, createdBy.?, createdOn.?, createdOnTimeZone.?, updatedBy.?, updatedOn.?, updatedOnTimeZone.?) <> (WhitelistSerialized.tupled, WhitelistSerialized.unapply)

    def id = column[String]("id", O.PrimaryKey)

    def ownerId = column[String]("ownerId")

    def name = column[String]("name")

    def description = column[String]("description")

    def maxMembers = column[Int]("maxMembers")

    def startEpoch = column[Int]("startEpoch")

    def endEpoch = column[Int]("endEpoch")

    def createdBy = column[String]("createdBy")

    def createdOn = column[Timestamp]("createdOn")

    def createdOnTimeZone = column[String]("createdOnTimeZone")

    def updatedBy = column[String]("updatedBy")

    def updatedOn = column[Timestamp]("updatedOn")

    def updatedOnTimeZone = column[String]("updatedOnTimeZone")

  }

  val TableQuery = new TableQuery(tag => new WhitelistTable(tag))
}

@Singleton
class Whitelists @Inject()(
                            protected val databaseConfigProvider: DatabaseConfigProvider
                          )(implicit override val executionContext: ExecutionContext)
  extends GenericDaoImpl[Whitelists.WhitelistTable, Whitelists.WhitelistSerialized, String](
    databaseConfigProvider,
    Whitelists.TableQuery,
    executionContext,
    Whitelists.module,
    Whitelists.logger
  ) {


  object Service {

    def addWhitelist(ownerId: String, name: String, description: String, maxMembers: Int, startEpoch: Int, endEpoch: Int): Future[String] = {
      val id = utilities.IdGenerator.getRandomHexadecimal
      create(Whitelist(id = id, ownerId = ownerId, name = name, description = description, maxMembers = maxMembers, startEpoch = startEpoch, endEpoch = endEpoch).serialize())
    }

    def edit(id: String, name: String, description: String, maxMembers: Int, startEpoch: Int, endEpoch: Int): Future[Unit] = {
      val whitelist = tryGet(id)
      for {
        whitelist <- whitelist
        _ <- update(whitelist.copy(name = name, description = description, maxMembers = maxMembers, startEpoch = startEpoch, endEpoch = endEpoch).serialize())
      } yield ()
    }

    def tryGet(id: String): Future[Whitelist] = tryGetById(id).map(_.deserialize)

    def getByOwner(ownerId: String, pageNumber: Int): Future[Seq[Whitelist]] = filterAndSortWithPagination((pageNumber - 1) * constants.CommonConfig.Pagination.WhitelistPerPage, limit = constants.CommonConfig.Pagination.WhitelistPerPage)(_.ownerId === ownerId)(_.startEpoch).map(_.map(_.deserialize))

    def totalWhitelistsByOwner(ownerId: String): Future[Int] = filterAndCount(_.ownerId === ownerId)

    def getByIds(whitelistIds: Seq[String]): Future[Seq[Whitelist]] = filter(_.id.inSet(whitelistIds)).map(_.map(_.deserialize))

    def deleteById(id: String): Future[Int] = delete(id)

    def checkInviteValidAndGetWhitelist(whiteListId: String): Future[(Boolean, Whitelist)] = {
      val whitelist = tryGet(whiteListId)
      for {
        whitelist <- whitelist
      } yield ((System.currentTimeMillis / 1000) <= whitelist.endEpoch, whitelist)
    }

  }
}