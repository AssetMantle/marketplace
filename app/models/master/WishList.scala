package models.master

import models.Trait.{Entity2, GenericDaoImpl2, Logged, ModelTable2}
import play.api.Logger
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.H2Profile.api._

import java.sql.Timestamp
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

case class WishList(accountId: String, nftId: String, createdBy: Option[String] = None, createdOn: Option[Timestamp] = None, createdOnTimeZone: Option[String] = None, updatedBy: Option[String] = None, updatedOn: Option[Timestamp] = None, updatedOnTimeZone: Option[String] = None) extends Logged {

  def serialize(): WishLists.WishListSerialized = WishLists.WishListSerialized(
    accountId = this.accountId,
    nftId = this.nftId,
    createdBy = this.createdBy,
    createdOn = this.createdOn,
    createdOnTimeZone = this.createdOnTimeZone,
    updatedBy = this.updatedBy,
    updatedOn = this.updatedOn,
    updatedOnTimeZone = this.updatedOnTimeZone)
}

object WishLists {

  implicit val module: String = constants.Module.MASTER_WISHLIST

  implicit val logger: Logger = Logger(this.getClass)

  case class WishListSerialized(accountId: String, nftId: String, createdBy: Option[String], createdOn: Option[Timestamp], createdOnTimeZone: Option[String], updatedBy: Option[String], updatedOn: Option[Timestamp], updatedOnTimeZone: Option[String]) extends Entity2[String, String] {
    def deserialize: WishList = WishList(accountId = accountId, nftId = nftId, createdBy = createdBy, createdOn = createdOn, createdOnTimeZone = createdOnTimeZone, updatedBy = updatedBy, updatedOn = updatedOn, updatedOnTimeZone = updatedOnTimeZone)

    def id1: String = accountId

    def id2: String = nftId
  }

  class WishListTable(tag: Tag) extends Table[WishListSerialized](tag, "WishList") with ModelTable2[String, String] {

    def * = (accountId, nftId, createdBy.?, createdOn.?, createdOnTimeZone.?, updatedBy.?, updatedOn.?, updatedOnTimeZone.?) <> (WishListSerialized.tupled, WishListSerialized.unapply)

    def accountId = column[String]("accountId", O.PrimaryKey)

    def nftId = column[String]("nftId", O.PrimaryKey)

    def createdBy = column[String]("createdBy")

    def createdOn = column[Timestamp]("createdOn")

    def createdOnTimeZone = column[String]("createdOnTimeZone")

    def updatedBy = column[String]("updatedBy")

    def updatedOn = column[Timestamp]("updatedOn")

    def updatedOnTimeZone = column[String]("updatedOnTimeZone")

    def id1 = accountId

    def id2 = nftId
  }

  val TableQuery = new TableQuery(tag => new WishListTable(tag))
}

@Singleton
class WishLists @Inject()(
                           protected val databaseConfigProvider: DatabaseConfigProvider
                         )(implicit override val executionContext: ExecutionContext)
  extends GenericDaoImpl2[WishLists.WishListTable, WishLists.WishListSerialized, String, String](
    databaseConfigProvider,
    WishLists.TableQuery,
    executionContext,
    WishLists.module,
    WishLists.logger
  ) {


  object Service {

    def add(accountId: String, nftId: String): Future[Unit] = create(WishList(accountId = accountId, nftId = nftId).serialize())

    def getByPageNumber(accountId: String, pageNumber: Int, perPage: Int): Future[Seq[WishList]] = filter(_.accountId === accountId).map(_.sortBy(_.createdOn).slice((pageNumber - 1) * perPage, pageNumber * perPage).map(_.deserialize))

    def deleteWishItem(accountId: String, nftId: String): Future[Int] = delete(id1 = accountId, id2 = nftId)

    def checkExists(accountId: String, nftId: String): Future[Boolean] = exists(id1 = accountId, id2 = nftId)

    def getAllForAccount(id: String): Future[Seq[String]] = filter(_.accountId === id).map(_.map(_.nftId))

  }
}