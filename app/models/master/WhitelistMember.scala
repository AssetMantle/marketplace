package models.master

import models.Trait.{Entity2, GenericDaoImpl2, Logged, ModelTable2}
import play.api.Logger
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.H2Profile.api._

import java.sql.Timestamp
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

case class WhitelistMember(whitelistId: String, accountId: String, createdBy: Option[String] = None, createdOn: Option[Timestamp] = None, createdOnTimeZone: Option[String] = None, updatedBy: Option[String] = None, updatedOn: Option[Timestamp] = None, updatedOnTimeZone: Option[String] = None) extends Logged {

  def serialize(): WhitelistMembers.WhitelistMemberSerialized = WhitelistMembers.WhitelistMemberSerialized(
    whitelistId = this.whitelistId,
    accountId = this.accountId,
    createdBy = this.createdBy,
    createdOn = this.createdOn,
    createdOnTimeZone = this.createdOnTimeZone,
    updatedBy = this.updatedBy,
    updatedOn = this.updatedOn,
    updatedOnTimeZone = this.updatedOnTimeZone)
}


object WhitelistMembers {

  implicit val module: String = constants.Module.MASTER_WHITELIST_MEMBER

  implicit val logger: Logger = Logger(this.getClass)

  case class WhitelistMemberSerialized(whitelistId: String, accountId: String, createdBy: Option[String], createdOn: Option[Timestamp], createdOnTimeZone: Option[String], updatedBy: Option[String], updatedOn: Option[Timestamp], updatedOnTimeZone: Option[String]) extends Entity2[String, String] {
    def deserialize: WhitelistMember = WhitelistMember(whitelistId = whitelistId, accountId = accountId, createdBy = createdBy, createdOn = createdOn, createdOnTimeZone = createdOnTimeZone, updatedBy = updatedBy, updatedOn = updatedOn, updatedOnTimeZone = updatedOnTimeZone)

    def id1: String = whitelistId

    def id2: String = accountId
  }

  class WhitelistMemberTable(tag: Tag) extends Table[WhitelistMemberSerialized](tag, "WhitelistMember") with ModelTable2[String, String] {

    def * = (whitelistId, accountId, createdBy.?, createdOn.?, createdOnTimeZone.?, updatedBy.?, updatedOn.?, updatedOnTimeZone.?) <> (WhitelistMemberSerialized.tupled, WhitelistMemberSerialized.unapply)

    def whitelistId = column[String]("whitelistId", O.PrimaryKey)

    def accountId = column[String]("accountId", O.PrimaryKey)

    def createdBy = column[String]("createdBy")

    def createdOn = column[Timestamp]("createdOn")

    def createdOnTimeZone = column[String]("createdOnTimeZone")

    def updatedBy = column[String]("updatedBy")

    def updatedOn = column[Timestamp]("updatedOn")

    def updatedOnTimeZone = column[String]("updatedOnTimeZone")

    def id1 = whitelistId

    def id2 = accountId
  }

  val TableQuery = new TableQuery(tag => new WhitelistMemberTable(tag))
}

@Singleton
class WhitelistMembers @Inject()(
                                  protected val databaseConfigProvider: DatabaseConfigProvider
                                )(implicit override val executionContext: ExecutionContext)
  extends GenericDaoImpl2[WhitelistMembers.WhitelistMemberTable, WhitelistMembers.WhitelistMemberSerialized, String, String](
    databaseConfigProvider,
    WhitelistMembers.TableQuery,
    executionContext,
    WhitelistMembers.module,
    WhitelistMembers.logger
  ) {


  object Service {

    def add(whitelistId: String, accountId: String): Future[Unit] = create(WhitelistMember(whitelistId = whitelistId, accountId = accountId).serialize())

    def getAllForWhitelist(whitelistId: String): Future[Seq[String]] = filter(_.whitelistId === whitelistId).map(_.map(_.accountId))

    def getAllForMember(accountId: String, pageNumber: Int, perPage: Int): Future[Seq[String]] = filterAndSortWithPagination((pageNumber - 1) * perPage, limit = perPage)(_.accountId === accountId)(_.createdOn).map(_.map(_.whitelistId))

    def getAllForMember(accountId: String): Future[Seq[String]] = filter(_.accountId === accountId).map(_.map(_.whitelistId))

    def getAllMembers(id: String): Future[Seq[String]] = filter(_.whitelistId === id).map(_.map(_.accountId))

    def totalJoined(accountId: String): Future[Int] = filterAndCount(_.accountId === accountId)

    def isMember(whitelistId: String, accountId: String): Future[Boolean] = exists(id1 = whitelistId, id2 = accountId)

    def deleteAllMembers(whitelistId: String): Future[Int] = filterAndDelete(_.whitelistId === whitelistId)

    def delete(whitelistId: String, accountId: String): Future[Int] = deleteById1AndId2(id1 = whitelistId, id2 = accountId)

    def getWhitelistsMemberCount(whitelistId: String): Future[Int] = filterAndCount(_.whitelistId === whitelistId)

    def checkAndAdd(whitelist: Whitelist, accountId: String): Future[Unit] = {
      val totalMembers = getWhitelistsMemberCount(whitelist.id)

      def addMember(totalMembers: Int) = if (totalMembers < whitelist.maxMembers) add(whitelistId = whitelist.id, accountId = accountId) else constants.Response.WHITELIST_MAX_MEMBERS_REACHED.throwFutureBaseException()

      for {
        totalMembers <- totalMembers
        _ <- addMember(totalMembers)
      } yield ()
    }
  }
}