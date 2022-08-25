package models.masterTransaction

import models.Trait.{Entity, GenericDaoImpl, Logged, ModelTable}
import play.api.Logger
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.H2Profile.api._

import java.sql.Timestamp
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

case class WhiteListInvite(id: String, whiteListId: String, startEpoch: Int, endEpoch: Int, createdBy: Option[String] = None, createdOn: Option[Timestamp] = None, createdOnTimeZone: Option[String] = None, updatedBy: Option[String] = None, updatedOn: Option[Timestamp] = None, updatedOnTimeZone: Option[String] = None) extends Logged {

  def serialize(): WhiteListInvites.WhiteListInviteSerialized = WhiteListInvites.WhiteListInviteSerialized(
    id = this.id,
    whiteListId = this.whiteListId,
    startEpoch = this.startEpoch,
    endEpoch = this.endEpoch,
    createdBy = this.createdBy,
    createdOn = this.createdOn,
    createdOnTimeZone = this.createdOnTimeZone,
    updatedBy = this.updatedBy,
    updatedOn = this.updatedOn,
    updatedOnTimeZone = this.updatedOnTimeZone)
}


object WhiteListInvites {

  implicit val module: String = constants.Module.MASTER_TRANSACTION_WHITE_LIST_INVITE

  implicit val logger: Logger = Logger(this.getClass)

  case class WhiteListInviteSerialized(id: String, whiteListId: String, startEpoch: Int, endEpoch: Int, createdBy: Option[String], createdOn: Option[Timestamp], createdOnTimeZone: Option[String], updatedBy: Option[String], updatedOn: Option[Timestamp], updatedOnTimeZone: Option[String]) extends Entity[String] {
    def deserialize: WhiteListInvite = WhiteListInvite(id = id, whiteListId = whiteListId, startEpoch = startEpoch, endEpoch = endEpoch, createdBy = createdBy, createdOn = createdOn, createdOnTimeZone = createdOnTimeZone, updatedBy = updatedBy, updatedOn = updatedOn, updatedOnTimeZone = updatedOnTimeZone)
  }

  class WhiteListInviteTable(tag: Tag) extends Table[WhiteListInviteSerialized](tag, "WhiteListInvite") with ModelTable[String] {

    def * = (id, whiteListId, startEpoch, endEpoch, createdBy.?, createdOn.?, createdOnTimeZone.?, updatedBy.?, updatedOn.?, updatedOnTimeZone.?) <> (WhiteListInviteSerialized.tupled, WhiteListInviteSerialized.unapply)

    def id = column[String]("id", O.PrimaryKey)

    def whiteListId = column[String]("startEpoch")

    def startEpoch = column[Int]("startEpoch")

    def endEpoch = column[Int]("endEpoch")

    def createdBy = column[String]("createdBy")

    def createdOn = column[Timestamp]("createdOn")

    def createdOnTimeZone = column[String]("createdOnTimeZone")

    def updatedBy = column[String]("updatedBy")

    def updatedOn = column[Timestamp]("updatedOn")

    def updatedOnTimeZone = column[String]("updatedOnTimeZone")

  }

  val TableQuery = new TableQuery(tag => new WhiteListInviteTable(tag))
}

@Singleton
class WhiteListInvites @Inject()(
                                  protected val databaseConfigProvider: DatabaseConfigProvider
                                )(implicit override val executionContext: ExecutionContext)
  extends GenericDaoImpl[WhiteListInvites.WhiteListInviteTable, WhiteListInvites.WhiteListInviteSerialized, String](
    databaseConfigProvider,
    WhiteListInvites.TableQuery,
    executionContext,
    WhiteListInvites.module,
    WhiteListInvites.logger
  ) {


  object Service {

    def add(whiteListId: String, startEpoch: Int, endEpoch: Int): Future[String] = {
      val id = utilities.IdGenerator.getRandomHexadecimal
      create(WhiteListInvite(id = id, whiteListId = whiteListId, startEpoch = startEpoch, endEpoch = endEpoch).serialize())
    }

    def tryGet(whiteListId: String): Future[WhiteListInvite] = tryGetById(whiteListId).map(_.deserialize)

    def updateEndEpoch(whiteListId: String, endEpoch: Int): Future[Unit] = {
      val whiteListInvite = tryGet(whiteListId)
      for {
        whiteListInvite <- whiteListInvite
        _ <- update(whiteListInvite.copy(endEpoch = endEpoch).serialize())
      } yield ()
    }

    def deleteInvite(whiteListId: String): Future[Int] = delete(whiteListId)

    def checkInviteValid(whiteListId: String): Future[Boolean] = {
      val invite = tryGet(whiteListId)
      for {
        invite <- invite
      } yield (System.currentTimeMillis / 1000) <= invite.endEpoch
    }

  }
}