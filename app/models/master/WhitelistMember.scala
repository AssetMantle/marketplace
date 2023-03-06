package models.master

import models.traits.{Entity2, GenericDaoImpl2, Logging, ModelTable2}
import play.api.Logger
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.H2Profile.api._

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

case class WhitelistMember(whitelistId: String, accountId: String, createdBy: Option[String] = None, createdOnMillisEpoch: Option[Long] = None, updatedBy: Option[String] = None, updatedOnMillisEpoch: Option[Long] = None) extends Logging with Entity2[String, String] {
  def id1: String = whitelistId

  def id2: String = accountId
}


object WhitelistMembers {

  implicit val module: String = constants.Module.MASTER_WHITELIST_MEMBER

  implicit val logger: Logger = Logger(this.getClass)

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

  val TableQuery = new TableQuery(tag => new WhitelistMemberTable(tag))
}

@Singleton
class WhitelistMembers @Inject()(
                                  protected val databaseConfigProvider: DatabaseConfigProvider
                                )(implicit override val executionContext: ExecutionContext)
  extends GenericDaoImpl2[WhitelistMembers.WhitelistMemberTable, WhitelistMember, String, String](
    databaseConfigProvider,
    WhitelistMembers.TableQuery,
    executionContext,
    WhitelistMembers.module,
    WhitelistMembers.logger
  ) {


  object Service {

    def add(whitelistId: String, accountId: String): Future[Unit] = create(WhitelistMember(whitelistId = whitelistId, accountId = accountId))

    def getAllForWhitelist(whitelistId: String): Future[Seq[String]] = filter(_.whitelistId === whitelistId).map(_.map(_.accountId))

    def getAllForMember(accountId: String, pageNumber: Int, perPage: Int): Future[Seq[String]] = filterAndSortWithPagination((pageNumber - 1) * perPage, limit = perPage)(_.accountId === accountId)(_.createdOnMillisEpoch).map(_.map(_.whitelistId))

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

      def addMember(totalMembers: Int) = if (totalMembers < whitelist.maxMembers) add(whitelistId = whitelist.id, accountId = accountId) else constants.Response.WHITELIST_MAX_MEMBERS_REACHED.throwFutureBaseException()

      for {
        totalMembers <- totalMembers
        _ <- addMember(totalMembers)
      } yield ()
    }
  }
}