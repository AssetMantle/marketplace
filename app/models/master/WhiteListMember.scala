package models.master

import exceptions.BaseException
import models.Trait.{Entity2, GenericDaoImpl2, Logged, ModelTable2}
import play.api.Logger
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.H2Profile.api._

import java.sql.Timestamp
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

case class WhiteListMember(whiteListId: String, accountId: String, createdBy: Option[String] = None, createdOn: Option[Timestamp] = None, createdOnTimeZone: Option[String] = None, updatedBy: Option[String] = None, updatedOn: Option[Timestamp] = None, updatedOnTimeZone: Option[String] = None) extends Logged {

  def serialize(): WhiteListMembers.WhiteListMemberSerialized = WhiteListMembers.WhiteListMemberSerialized(
    whiteListId = this.whiteListId,
    accountId = this.accountId,
    createdBy = this.createdBy,
    createdOn = this.createdOn,
    createdOnTimeZone = this.createdOnTimeZone,
    updatedBy = this.updatedBy,
    updatedOn = this.updatedOn,
    updatedOnTimeZone = this.updatedOnTimeZone)
}


object WhiteListMembers {

  implicit val module: String = constants.Module.MASTER_WHITE_LIST_MEMBER

  implicit val logger: Logger = Logger(this.getClass)

  case class WhiteListMemberSerialized(whiteListId: String, accountId: String, createdBy: Option[String], createdOn: Option[Timestamp], createdOnTimeZone: Option[String], updatedBy: Option[String], updatedOn: Option[Timestamp], updatedOnTimeZone: Option[String]) extends Entity2[String, String] {
    def deserialize: WhiteListMember = WhiteListMember(whiteListId = whiteListId, accountId = accountId, createdBy = createdBy, createdOn = createdOn, createdOnTimeZone = createdOnTimeZone, updatedBy = updatedBy, updatedOn = updatedOn, updatedOnTimeZone = updatedOnTimeZone)

    def id1: String = whiteListId

    def id2: String = accountId
  }

  class WhiteListMemberTable(tag: Tag) extends Table[WhiteListMemberSerialized](tag, "WhiteListMember") with ModelTable2[String, String] {

    def * = (whiteListId, accountId, createdBy.?, createdOn.?, createdOnTimeZone.?, updatedBy.?, updatedOn.?, updatedOnTimeZone.?) <> (WhiteListMemberSerialized.tupled, WhiteListMemberSerialized.unapply)

    def whiteListId = column[String]("whiteListId", O.PrimaryKey)

    def accountId = column[String]("accountId", O.PrimaryKey)

    def createdBy = column[String]("createdBy")

    def createdOn = column[Timestamp]("createdOn")

    def createdOnTimeZone = column[String]("createdOnTimeZone")

    def updatedBy = column[String]("updatedBy")

    def updatedOn = column[Timestamp]("updatedOn")

    def updatedOnTimeZone = column[String]("updatedOnTimeZone")

    def id1 = whiteListId

    def id2 = accountId
  }

  val TableQuery = new TableQuery(tag => new WhiteListMemberTable(tag))
}

@Singleton
class WhiteListMembers @Inject()(
                                  protected val databaseConfigProvider: DatabaseConfigProvider
                                )(implicit override val executionContext: ExecutionContext)
  extends GenericDaoImpl2[WhiteListMembers.WhiteListMemberTable, WhiteListMembers.WhiteListMemberSerialized, String, String](
    databaseConfigProvider,
    WhiteListMembers.TableQuery,
    executionContext,
    WhiteListMembers.module,
    WhiteListMembers.logger
  ) {


  object Service {
  }
}