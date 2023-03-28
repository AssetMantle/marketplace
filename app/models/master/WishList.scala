package models.master

import models.traits.{Entity2, GenericDaoImpl2, Logging, ModelTable2}
import play.api.Logger
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.H2Profile.api._

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

case class WishList(accountId: String, nftId: String, collectionId: String, createdBy: Option[String] = None, createdOnMillisEpoch: Option[Long] = None, updatedBy: Option[String] = None, updatedOnMillisEpoch: Option[Long] = None) extends Logging with Entity2[String, String] {

  def id1: String = accountId

  def id2: String = nftId

}

object WishLists {

  implicit val module: String = constants.Module.MASTER_WISHLIST

  implicit val logger: Logger = Logger(this.getClass)

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

  val TableQuery = new TableQuery(tag => new WishListTable(tag))
}

@Singleton
class WishLists @Inject()(
                           protected val databaseConfigProvider: DatabaseConfigProvider
                         )(implicit override val executionContext: ExecutionContext)
  extends GenericDaoImpl2[WishLists.WishListTable, WishList, String, String](
    databaseConfigProvider,
    WishLists.TableQuery,
    executionContext,
    WishLists.module,
    WishLists.logger
  ) {


  object Service {

    def add(accountId: String, nftId: String, collectionId: String): Future[Unit] = create(WishList(accountId = accountId, nftId = nftId, collectionId = collectionId))

    def getByCollection(accountId: String, collectionId: String): Future[Seq[String]] = filter(x => x.accountId === accountId && x.collectionId === collectionId).map(_.map(_.nftId))

    def getByCollectionAndPageNumber(accountId: String, collectionId: String, pageNumber: Int, perPage: Int): Future[Seq[String]] = filterAndSortWithPagination(offset = (pageNumber - 1) * perPage, limit = perPage)(x => x.accountId === accountId && x.collectionId === collectionId)(_.createdOnMillisEpoch).map(_.map(_.nftId))

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