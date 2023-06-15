package models.master

import models.master.Whitelists.WhitelistTable
import models.traits._
import play.api.Logger
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.H2Profile.api._

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

case class Whitelist(id: String, ownerId: String, name: String, description: String, maxMembers: Int, startEpoch: Int, endEpoch: Int, createdBy: Option[String] = None, createdOnMillisEpoch: Option[Long] = None, updatedBy: Option[String] = None, updatedOnMillisEpoch: Option[Long] = None) extends Logging with Entity[String]

private[master] object Whitelists {

  class WhitelistTable(tag: Tag) extends Table[Whitelist](tag, "Whitelist") with ModelTable[String] {

    def * = (id, ownerId, name, description, maxMembers, startEpoch, endEpoch, createdBy.?, createdOnMillisEpoch.?, updatedBy.?, updatedOnMillisEpoch.?) <> (Whitelist.tupled, Whitelist.unapply)

    def id = column[String]("id", O.PrimaryKey)

    def ownerId = column[String]("ownerId")

    def name = column[String]("name")

    def description = column[String]("description")

    def maxMembers = column[Int]("maxMembers")

    def startEpoch = column[Int]("startEpoch")

    def endEpoch = column[Int]("endEpoch")

    def createdBy = column[String]("createdBy")

    def createdOnMillisEpoch = column[Long]("createdOnMillisEpoch")

    def updatedBy = column[String]("updatedBy")

    def updatedOnMillisEpoch = column[Long]("updatedOnMillisEpoch")

  }
}

@Singleton
class Whitelists @Inject()(
                            protected val dbConfigProvider: DatabaseConfigProvider,
                          )(implicit val executionContext: ExecutionContext)
  extends GenericDaoImpl[Whitelists.WhitelistTable, Whitelist, String]() {

  implicit val module: String = constants.Module.MASTER_WHITELIST

  implicit val logger: Logger = Logger(this.getClass)

  val tableQuery = new TableQuery(tag => new WhitelistTable(tag))

  object Service {

    def addWhitelist(ownerId: String, name: String, description: String, maxMembers: Int, startEpoch: Int, endEpoch: Int): Future[String] = {
      val id = utilities.IdGenerator.getRandomHexadecimal
      create(Whitelist(id = id, ownerId = ownerId, name = name, description = description, maxMembers = maxMembers, startEpoch = startEpoch, endEpoch = endEpoch)).map(_.id)
    }

    def edit(id: String, name: String, description: String, maxMembers: Int, startEpoch: Int, endEpoch: Int): Future[Unit] = {
      val whitelist = tryGet(id)
      for {
        whitelist <- whitelist
        _ <- updateById(whitelist.copy(name = name, description = description, maxMembers = maxMembers, startEpoch = startEpoch, endEpoch = endEpoch))
      } yield ()
    }

    def tryGet(id: String): Future[Whitelist] = tryGetById(id)

    def getByOwner(ownerId: String, pageNumber: Int): Future[Seq[Whitelist]] = filterAndSortWithPagination(_.ownerId === ownerId)(_.startEpoch)((pageNumber - 1) * constants.CommonConfig.Pagination.WhitelistPerPage, limit = constants.CommonConfig.Pagination.WhitelistPerPage)

    def totalWhitelistsByOwner(ownerId: String): Future[Int] = filterAndCount(_.ownerId === ownerId)

    def getByIds(whitelistIds: Seq[String]): Future[Seq[Whitelist]] = filter(_.id.inSet(whitelistIds))

    def getIdsByOwnerId(ownerId: String): Future[Seq[String]] = filter(_.ownerId === ownerId).map(_.map(_.id))

    def hasWhitelist(accountId: String): Future[Boolean] = filter(_.ownerId === accountId).map(_.nonEmpty)

    def getAllByOwner(ownerId: String): Future[Seq[String]] = filter(_.ownerId === ownerId).map(_.map(_.id))

    def getIdNameMapForOwner(ownerId: String): Future[Map[String, String]] = filter(_.ownerId === ownerId).map(_.map(x => x.id -> x.name).toMap)

    def delete(id: String): Future[Int] = deleteById(id)

  }
}