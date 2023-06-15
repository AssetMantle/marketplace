package models.master

import models.master.WishLists.WishListTable
import models.traits._
import play.api.Logger
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.H2Profile.api._

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

case class WishList(accountId: String, nftId: String, collectionId: String, createdBy: Option[String] = None, createdOnMillisEpoch: Option[Long] = None, updatedBy: Option[String] = None, updatedOnMillisEpoch: Option[Long] = None) extends Logging with Entity2[String, String] {

  def id1: String = accountId

  def id2: String = nftId

}

private[master] object WishLists {

  class WishListTable(tag: Tag) extends Table[WishList](tag, "WishList") with ModelTable2[String, String] {

    def * = (accountId, nftId, collectionId, createdBy.?, createdOnMillisEpoch.?, updatedBy.?, updatedOnMillisEpoch.?) <> (WishList.tupled, WishList.unapply)

    def accountId = column[String]("accountId", O.PrimaryKey)

    def nftId = column[String]("nftId", O.PrimaryKey)

    def collectionId = column[String]("collectionId")

    def createdBy = column[String]("createdBy")

    def createdOnMillisEpoch = column[Long]("createdOnMillisEpoch")

    def updatedBy = column[String]("updatedBy")

    def updatedOnMillisEpoch = column[Long]("updatedOnMillisEpoch")

    def id1 = accountId

    def id2 = nftId
  }
}

@Singleton
class WishLists @Inject()(
                           protected val dbConfigProvider: DatabaseConfigProvider,
                         )(implicit val executionContext: ExecutionContext)
  extends GenericDaoImpl2[WishLists.WishListTable, WishList, String, String]() {

  implicit val module: String = constants.Module.MASTER_WISHLIST

  implicit val logger: Logger = Logger(this.getClass)

  val tableQuery = new TableQuery(tag => new WishListTable(tag))

  object Service {

    def add(accountId: String, nftId: String, collectionId: String): Future[String] = create(WishList(accountId = accountId, nftId = nftId, collectionId = collectionId)).map(_.nftId)

    def getByCollection(accountId: String, collectionId: String): Future[Seq[String]] = filter(x => x.accountId === accountId && x.collectionId === collectionId).map(_.map(_.nftId))

    def getByCollectionAndPageNumber(accountId: String, collectionId: String, pageNumber: Int, perPage: Int): Future[Seq[String]] = filterAndSortWithPagination(x => x.accountId === accountId && x.collectionId === collectionId)(_.createdOnMillisEpoch)(offset = (pageNumber - 1) * perPage, limit = perPage).map(_.map(_.nftId))

    def getCollections(accountId: String): Future[Seq[String]] = filter(_.accountId === accountId).map(_.map(_.collectionId).distinct)

    def get(accountId: String, nftIds: Seq[String]): Future[Seq[WishList]] = filter(x => x.accountId === accountId && x.nftId.inSet(nftIds))

    // TODO
    //    def getCollections(accountId: String): Future[Seq[String]] = customQuery[Seq[String]](WishLists.TableQuery.filter(_.accountId === accountId).map(_.collectionId).distinct.result)

    def delete(accountId: String, nftId: String): Future[Int] = deleteById1AndId2(id1 = accountId, id2 = nftId)

    def checkExists(accountId: String, nftId: String): Future[Boolean] = exists(id1 = accountId, id2 = nftId)

    def countLikes(nftId: String): Future[Int] = filter(_.nftId === nftId).map(_.length)

    def deleteCollections(collectionIds: Seq[String]): Future[Int] = filterAndDelete(_.collectionId.inSet(collectionIds))

    def deleteForNFT(nftId: String): Future[Int] = filterAndDelete(_.nftId === nftId)

  }
}