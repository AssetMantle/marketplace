package models.master

import models.Trait.{Entity2, GenericDaoImpl2, Logged, ModelTable2}
import play.api.Logger
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.H2Profile.api._

import java.sql.Timestamp
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

case class WishList(accountId: String, nftId: String, collectionId: String, createdBy: Option[String] = None, createdOn: Option[Timestamp] = None, createdOnTimeZone: Option[String] = None, updatedBy: Option[String] = None, updatedOn: Option[Timestamp] = None, updatedOnTimeZone: Option[String] = None) extends Logged {

  def serialize(): WishLists.WishListSerialized = WishLists.WishListSerialized(
    accountId = this.accountId,
    nftId = this.nftId,
    collectionId = this.collectionId,
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

  case class WishListSerialized(accountId: String, nftId: String, collectionId: String, createdBy: Option[String], createdOn: Option[Timestamp], createdOnTimeZone: Option[String], updatedBy: Option[String], updatedOn: Option[Timestamp], updatedOnTimeZone: Option[String]) extends Entity2[String, String] {
    def deserialize: WishList = WishList(accountId = accountId, nftId = nftId, collectionId = collectionId, createdBy = createdBy, createdOn = createdOn, createdOnTimeZone = createdOnTimeZone, updatedBy = updatedBy, updatedOn = updatedOn, updatedOnTimeZone = updatedOnTimeZone)

    def id1: String = accountId

    def id2: String = nftId
  }

  class WishListTable(tag: Tag) extends Table[WishListSerialized](tag, "WishList") with ModelTable2[String, String] {

    def * = (accountId, nftId, collectionId, createdBy.?, createdOn.?, createdOnTimeZone.?, updatedBy.?, updatedOn.?, updatedOnTimeZone.?) <> (WishListSerialized.tupled, WishListSerialized.unapply)

    def accountId = column[String]("accountId", O.PrimaryKey)

    def nftId = column[String]("nftId", O.PrimaryKey)

    def collectionId = column[String]("collectionId")

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

    def add(accountId: String, nftId: String, collectionId: String): Future[Unit] = create(WishList(accountId = accountId, nftId = nftId, collectionId = collectionId).serialize())

    def getByCollection(accountId: String, collectionId: String): Future[Seq[String]] = filter(x => x.accountId === accountId && x.collectionId === collectionId).map(_.map(_.nftId))

    def getByCollectionAndPageNumber(accountId: String, collectionId: String, pageNumber: Int, perPage: Int): Future[Seq[String]] = filterAndSortWithPagination(offset = (pageNumber - 1) * perPage, limit = perPage)(x => x.accountId === accountId && x.collectionId === collectionId)(_.createdOn).map(_.map(_.nftId))

    def getCollections(accountId: String): Future[Seq[String]] = filter(_.accountId === accountId).map(_.map(_.collectionId).distinct)

    def get(accountId: String, nftIds: Seq[String]): Future[Seq[WishList]] = filter(x => x.accountId === accountId && x.nftId.inSet(nftIds)).map(_.map(_.deserialize))

    // TODO
    //    def getCollections(accountId: String): Future[Seq[String]] = customQuery[Seq[String]](WishLists.TableQuery.filter(_.accountId === accountId).map(_.collectionId).distinct.result)

    def delete(accountId: String, nftId: String): Future[Int] = deleteById1AndId2(id1 = accountId, id2 = nftId)

    def checkExists(accountId: String, nftId: String): Future[Boolean] = exists(id1 = accountId, id2 = nftId)

    def countLikes(nftId: String): Future[Int] = filter(_.nftId === nftId).map(_.length)

    def deleteCollections(collectionIds: Seq[String]): Future[Int] = filterAndDelete(_.collectionId.inSet(collectionIds))

  }
}