package models.master

import models.Trait.{Entity2, GenericDaoImpl2, Logging, ModelTable2}
import play.api.Logger
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.H2Profile.api._

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}
import scala.util.Random

case class NFTOwner(nftId: String, ownerId: String, creatorId: String, collectionId: String, quantity: Long, saleId: Option[String], publicListingId: Option[String], createdBy: Option[String] = None, createdOnMillisEpoch: Option[Long] = None, updatedBy: Option[String] = None, updatedOnMillisEpoch: Option[Long] = None) extends Entity2[String, String] with Logging {
  def id1: String = nftId

  def id2: String = ownerId
}

object NFTOwners {

  implicit val module: String = constants.Module.MASTER_NFT_OWNER

  implicit val logger: Logger = Logger(this.getClass)


  class NFTOwnerTable(tag: Tag) extends Table[NFTOwner](tag, "NFTOwner") with ModelTable2[String, String] {

    def * = (nftId, ownerId, creatorId, collectionId, quantity, saleId.?, publicListingId.?, createdBy.?, createdOnMillisEpoch.?, updatedBy.?, updatedOnMillisEpoch.?) <> (NFTOwner.tupled, NFTOwner.unapply)

    def nftId = column[String]("nftId", O.PrimaryKey)

    def ownerId = column[String]("ownerId", O.PrimaryKey)

    def creatorId = column[String]("creatorId")

    def collectionId = column[String]("collectionId")

    def quantity = column[Long]("quantity")

    def saleId = column[String]("saleId")

    def publicListingId = column[String]("publicListingId")

    def createdBy = column[String]("createdBy")

    def createdOnMillisEpoch = column[Long]("createdOnMillisEpoch")

    def updatedBy = column[String]("updatedBy")

    def updatedOnMillisEpoch = column[Long]("updatedOnMillisEpoch")

    def id1 = nftId

    def id2 = ownerId
  }

  val TableQuery = new TableQuery(tag => new NFTOwnerTable(tag))
}

@Singleton
class NFTOwners @Inject()(
                           protected val databaseConfigProvider: DatabaseConfigProvider
                         )(implicit override val executionContext: ExecutionContext)
  extends GenericDaoImpl2[NFTOwners.NFTOwnerTable, NFTOwner, String, String](
    databaseConfigProvider,
    NFTOwners.TableQuery,
    executionContext,
    NFTOwners.module,
    NFTOwners.logger
  ) {

  object Service {

    def add(nftOwner: NFTOwner): Future[Unit] = create(nftOwner)

    def add(nftOwners: Seq[NFTOwner]): Future[Unit] = create(nftOwners)

    def getByOwnerIdAndPageNumber(ownerId: String, pageNumber: Int): Future[Seq[String]] = filterAndSortWithPagination(offset = (pageNumber - 1) * constants.CommonConfig.Pagination.NFTsPerPage, limit = constants.CommonConfig.Pagination.NFTsPerPage)(x => x.ownerId === ownerId && x.ownerId =!= x.creatorId)(_.nftId).map(_.map(_.nftId))

    def countByOwnerIdAndCollectionId(ownerId: String, collectionId: String): Future[Int] = filterAndCount(x => x.ownerId === ownerId && x.collectionId === collectionId)

    def update(NFTOwner: NFTOwner): Future[Unit] = updateById1AndId2(NFTOwner)

    def countForCreatorNotForSell(collectionId: String, creatorId: String): Future[Int] = {
      val nullString: Option[String] = null
      filterAndCount(x => x.collectionId === collectionId && x.creatorId === creatorId && x.ownerId === creatorId && x.saleId.? === nullString && x.publicListingId.? === nullString)
    }

    def whitelistSaleRandomNFTs(collectionId: String, creatorId: String, nfts: Int, saleId: String): Future[Unit] = {
      val nullString: Option[String] = null
      val notOnSaleNFTs = filter(x => x.ownerId === creatorId && x.creatorId === creatorId && x.collectionId === collectionId && x.saleId === nullString && x.publicListingId === nullString)
      for {
        notOnSaleNFTs <- notOnSaleNFTs
        _ <- upsertMultiple(Random.shuffle(notOnSaleNFTs).take(nfts).map(_.copy(saleId = Option(saleId))))
      } yield ()
    }

    def publicListRandomNFTs(collectionId: String, creatorId: String, nfts: Int, publicListingId: String): Future[Unit] = {
      val nullString: Option[String] = null
      val notOnSaleNFTs = filter(x => x.ownerId === creatorId && x.creatorId === creatorId && x.collectionId === collectionId && x.saleId === nullString && x.publicListingId === nullString)
      for {
        notOnSaleNFTs <- notOnSaleNFTs
        _ <- upsertMultiple(Random.shuffle(notOnSaleNFTs).take(nfts).map(_.copy(publicListingId = Option(publicListingId))))
      } yield ()
    }

    def delete(nftId: String, ownerId: String): Future[Int] = deleteById1AndId2(id1 = nftId, id2 = ownerId)

    def deleteByNFTID(nftId: String): Future[Int] = filterAndDelete(_.nftId === nftId)

    def markNFTSoldFromSale(nftId: String, saleId: String, sellerAccountId: String, buyerAccountId: String): Future[Unit] = {
      val nftOwner = tryGet(nftId = nftId, ownerId = sellerAccountId)

      def verifyAndUpdate(nftOwner: NFTOwner) = if (nftOwner.saleId.getOrElse("") == saleId) {
        if (nftOwner.quantity == 1) {
          val deleteOld = delete(nftId = nftOwner.nftId, ownerId = nftOwner.ownerId)
          val addNew = create(nftOwner.copy(saleId = None, ownerId = buyerAccountId))
          for {
            _ <- deleteOld
            _ <- addNew
          } yield ()
        } else constants.Response.HANDLE_MULTIPLE_NFT_QUANTITY_CASE.throwFutureBaseException()
      } else constants.Response.NFT_NOT_ON_SALE.throwFutureBaseException()

      for {
        nftOwner <- nftOwner
        _ <- verifyAndUpdate(nftOwner)
      } yield ()
    }

    def markNFTSoldFromPublicListing(nftId: String, publicListingId: String, sellerAccountId: String, buyerAccountId: String): Future[Unit] = {
      val nftOwner = tryGet(nftId = nftId, ownerId = sellerAccountId)

      def verifyAndUpdate(nftOwner: NFTOwner) = if (nftOwner.publicListingId.getOrElse("") == publicListingId) {
        if (nftOwner.quantity == 1) {
          for {
            _ <- delete(nftId = nftOwner.nftId, ownerId = nftOwner.ownerId)
            _ <- create(nftOwner.copy(publicListingId = None, ownerId = buyerAccountId))
          } yield ()
        } else constants.Response.HANDLE_MULTIPLE_NFT_QUANTITY_CASE.throwFutureBaseException()
      } else constants.Response.NFT_NOT_ON_PUBLIC_LISTING.throwFutureBaseException()

      for {
        nftOwner <- nftOwner
        _ <- verifyAndUpdate(nftOwner)
      } yield ()
    }

    def getSaleId(nftId: String): Future[Option[String]] = filterHead(_.nftId === nftId).map(_.saleId)

    def tryGet(nftId: String, ownerId: String): Future[NFTOwner] = tryGetById1AndId2(id1 = nftId, id2 = ownerId)

    def getOwners(nftId: String): Future[Seq[NFTOwner]] = filter(_.id1 === nftId)

    def checkExists(nftId: String, ownerId: String): Future[Boolean] = exists(id1 = nftId, id2 = ownerId)

    def markSaleNull(saleId: String): Future[Int] = {
      val nullString: Option[String] = null
      customUpdate(NFTOwners.TableQuery.filter(_.saleId === saleId).map(_.saleId.?).update(nullString))
    }

    def markPublicListingNull(publicListingId: String): Future[Int] = {
      val nullString: Option[String] = null
      customUpdate(NFTOwners.TableQuery.filter(_.publicListingId === publicListingId).map(_.publicListingId.?).update(nullString))
    }

    def countOwnedNFTs(accountId: String): Future[Int] = filterAndCount(x => x.ownerId === accountId && x.creatorId =!= accountId)

    def countCollectionOwnedNFTs(accountId: String, collectionID: String): Future[Int] = filterAndCount(x => x.ownerId === accountId && x.creatorId =!= accountId && x.collectionId === collectionID)

    def countOwnedAndCreatedNFTsForCollection(accountId: String, collectionId: String): Future[Int] = filterAndCount(x => x.ownerId === accountId && x.creatorId === accountId && x.collectionId === collectionId)

    def getRandomNFTsBySaleId(saleId: String, take: Int, creatorId: String): Future[Seq[NFTOwner]] = filter(x => x.saleId === saleId && x.ownerId === creatorId && x.creatorId === creatorId).map(x => util.Random.shuffle(x).take(take))

    def getRandomNFTsByPublicListingId(publicListingId: String, take: Int, creatorId: String): Future[Seq[NFTOwner]] = filter(x => x.publicListingId === publicListingId && x.ownerId === creatorId && x.creatorId === creatorId).map(x => util.Random.shuffle(x).take(take))

    def deleteCollections(collectionIds: Seq[String]): Future[Int] = filterAndDelete(_.collectionId.inSet(collectionIds))

    def getCollectedCollection(accountId: String): Future[Seq[String]] = filter(x => x.ownerId === accountId && x.creatorId =!= accountId).map(_.map(_.collectionId).distinct)

    def getByCollectionAndPageNumber(accountId: String, collectionId: String, pageNumber: Int, perPage: Int): Future[Seq[String]] = filterAndSortWithPagination(offset = (pageNumber - 1) * perPage, limit = perPage)(x => x.ownerId === accountId && x.creatorId =!= accountId && x.collectionId === collectionId)(_.updatedOnMillisEpoch).map(_.map(_.nftId))

    def tryGetByNFTAndSaleId(nftId: String, saleId: String): Future[NFTOwner] = filterHead(x => x.saleId === saleId && x.nftId === nftId)
    //    https://scala-slick.org/doc/3.1.1/sql-to-slick.html#id21
    //    def getQuery = NFTOwners.TableQuery.filter(x => x.ownerId === "asd" && x.creatorId)
  }
}