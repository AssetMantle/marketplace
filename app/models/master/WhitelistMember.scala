package models.master

import models.master.WhitelistMembers.WhitelistMemberTable
import models.traits._
import play.api.Logger
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.H2Profile.api._

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

case class WhitelistMember(whitelistId: String, accountId: String, createdBy: Option[String] = None, createdOnMillisEpoch: Option[Long] = None, updatedBy: Option[String] = None, updatedOnMillisEpoch: Option[Long] = None) extends Logging with Entity2[String, String] {
  def id1: String = whitelistId

  def id2: String = accountId
}

private[master] object WhitelistMembers {

  class WhitelistMemberTable(tag: Tag) extends Table[WhitelistMember](tag, "WhitelistMember") with ModelTable2[String, String] {

    def * = (whitelistId, accountId, createdBy.?, createdOnMillisEpoch.?, updatedBy.?, updatedOnMillisEpoch.?) <> (WhitelistMember.tupled, WhitelistMember.unapply)

    def whitelistId = column[String]("whitelistId", O.PrimaryKey)

    def accountId = column[String]("accountId", O.PrimaryKey)

    def createdBy = column[String]("createdBy")

    def createdOnMillisEpoch = column[Long]("createdOnMillisEpoch")

    def updatedBy = column[String]("updatedBy")

    def updatedOnMillisEpoch = column[Long]("updatedOnMillisEpoch")

    def id1 = whitelistId

    def id2 = accountId
  }
}

@Singleton
class WhitelistMembers @Inject()(
                                  protected val dbConfigProvider: DatabaseConfigProvider,
                                )(implicit val executionContext: ExecutionContext)
  extends GenericDaoImpl2[WhitelistMembers.WhitelistMemberTable, WhitelistMember, String, String]() {

  implicit val module: String = constants.Module.MASTER_WHITELIST_MEMBER

  implicit val logger: Logger = Logger(this.getClass)

  val tableQuery = new TableQuery(tag => new WhitelistMemberTable(tag))

  object Service {

    def add(whitelistId: String, accountId: String): Future[String] = create(WhitelistMember(whitelistId = whitelistId, accountId = accountId)).map(_.whitelistId)

    def add(whitelistId: String, accountIds: Seq[String]): Future[Int] = create(accountIds.map(x => WhitelistMember(whitelistId = whitelistId, accountId = x)))

    def getAllForWhitelist(whitelistId: String): Future[Seq[String]] = filter(_.whitelistId === whitelistId).map(_.map(_.accountId))

    def getAllForMember(accountId: String, pageNumber: Int, perPage: Int): Future[Seq[String]] = filterAndSortWithPagination(_.accountId === accountId)(_.createdOnMillisEpoch)((pageNumber - 1) * perPage, limit = perPage).map(_.map(_.whitelistId))

    def getAllForMember(accountId: String): Future[Seq[String]] = filter(_.accountId === accountId).map(_.map(_.whitelistId))

    def getAllMembers(id: String): Future[Seq[String]] = filter(_.whitelistId === id).map(_.map(_.accountId))

    def getAllMembers(ids: Seq[String]): Future[Seq[String]] = filter(_.whitelistId.inSet(ids)).map(_.map(_.accountId).distinct)

    def totalJoined(accountId: String): Future[Int] = filterAndCount(_.accountId === accountId)

    def isMember(whitelistId: String, accountId: String): Future[Boolean] = exists(id1 = whitelistId, id2 = accountId)

    def isMember(whitelistIds: Seq[String], accountId: String): Future[Boolean] = filterAndExists(x => x.whitelistId.inSet(whitelistIds) && x.accountId === accountId)

    def deleteAllMembers(whitelistId: String): Future[Int] = filterAndDelete(_.whitelistId === whitelistId)

    def delete(whitelistId: String, accountId: String): Future[Int] = deleteById1AndId2(id1 = whitelistId, id2 = accountId)

    def getWhitelistsMemberCount(whitelistId: String): Future[Int] = filterAndCount(_.whitelistId === whitelistId)

    def checkAndAdd(whitelist: Whitelist, accountId: String): Future[Unit] = {
      val totalMembers = getWhitelistsMemberCount(whitelist.id)

      def addMember(totalMembers: Int) = if (totalMembers < whitelist.maxMembers) add(whitelistId = whitelist.id, accountId = accountId) else constants.Response.WHITELIST_MAX_MEMBERS_REACHED.throwBaseException()

      for {
        totalMembers <- totalMembers
        _ <- addMember(totalMembers)
      } yield ()
    }
  }
}