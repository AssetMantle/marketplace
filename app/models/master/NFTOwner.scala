package models.master

import models.traits.{Entity2, GenericDaoImpl2, Logging, ModelTable2}
import play.api.Logger
import play.api.db.slick.DatabaseConfigProvider
import schema.id.base.IdentityID
import slick.jdbc.H2Profile.api._

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}
import scala.util.Random

case class NFTOwner(nftId: String, ownerId: String, creatorId: String, collectionId: String, quantity: Long, saleId: Option[String], publicListingId: Option[String], secondaryMarketId: Option[String], createdBy: Option[String] = None, createdOnMillisEpoch: Option[Long] = None, updatedBy: Option[String] = None, updatedOnMillisEpoch: Option[Long] = None) extends Entity2[String, String] with Logging {
  def id1: String = nftId

  def id2: String = ownerId

  def getCreatorIdentityID: IdentityID = utilities.Identity.getMantlePlaceIdentityID(this.creatorId)

  def getOwnerIdentityID: IdentityID = utilities.Identity.getMantlePlaceIdentityID(this.ownerId)

  //  def getSplitValue: AttoNumber = AttoNumber(this.quantity * constants.Blockchain.SmallestDec)
}

object NFTOwners {

  implicit val module: String = constants.Module.MASTER_NFT_OWNER

  implicit val logger: Logger = Logger(this.getClass)


  class NFTOwnerTable(tag: Tag) extends Table[NFTOwner](tag, "NFTOwner") with ModelTable2[String, String] {

    def * = (nftId, ownerId, creatorId, collectionId, quantity, saleId.?, publicListingId.?, secondaryMarketId.?, createdBy.?, createdOnMillisEpoch.?, updatedBy.?, updatedOnMillisEpoch.?) <> (NFTOwner.tupled, NFTOwner.unapply)

    def nftId = column[String]("nftId", O.PrimaryKey)

    def ownerId = column[String]("ownerId", O.PrimaryKey)

    def creatorId = column[String]("creatorId")

    def collectionId = column[String]("collectionId")

    def quantity = column[Long]("quantity")

    def saleId = column[String]("saleId")

    def publicListingId = column[String]("publicListingId")

    def secondaryMarketId = column[String]("secondaryMarketId")

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

    def update(NFTOwner: NFTOwner): Future[Int] = updateById1AndId2(NFTOwner)

    def countForCreatorForPrimarySale(collectionId: String, creatorId: String, unmintedNFTs: Seq[String]): Future[Int] = {
      val nullString: Option[String] = null
      filterAndCount(x => x.collectionId === collectionId && x.nftId.inSet(unmintedNFTs) && x.creatorId === creatorId && x.ownerId === creatorId && x.saleId.? === nullString && x.publicListingId.? === nullString && x.secondaryMarketId.? === nullString)
    }

    def whitelistSaleRandomNFTs(collectionId: String, creatorId: String, nfts: Int, saleId: String, unmintedNFTs: Seq[String]): Future[Unit] = {
      val nullString: Option[String] = null
      val notOnSaleNFTs = filter(x => x.ownerId === creatorId && x.creatorId === creatorId && x.nftId.inSet(unmintedNFTs) && x.collectionId === collectionId && x.saleId === nullString && x.publicListingId === nullString && x.secondaryMarketId === nullString)
      for {
        notOnSaleNFTs <- notOnSaleNFTs
        _ <- upsertMultiple(Random.shuffle(notOnSaleNFTs).take(nfts).map(_.copy(saleId = Option(saleId))))
      } yield ()
    }

    def publicListRandomNFTs(collectionId: String, creatorId: String, nfts: Int, publicListingId: String, unmintedNFTs: Seq[String]): Future[Unit] = {
      val nullString: Option[String] = null
      val notOnSaleNFTs = filter(x => x.ownerId === creatorId && x.creatorId === creatorId && x.nftId.inSet(unmintedNFTs) && x.collectionId === collectionId && x.saleId === nullString && x.publicListingId === nullString && x.secondaryMarketId === nullString)
      for {
        notOnSaleNFTs <- notOnSaleNFTs
        _ <- upsertMultiple(Random.shuffle(notOnSaleNFTs).take(nfts).map(_.copy(publicListingId = Option(publicListingId))))
      } yield ()
    }

    def listNFTOnSecondaryMarket(NFTOwner: NFTOwner, secondaryMarketID: String): Future[Int] = if (NFTOwner.secondaryMarketId.isEmpty && NFTOwner.publicListingId.isEmpty && NFTOwner.saleId.isEmpty) {
      update(NFTOwner.copy(secondaryMarketId = Option(secondaryMarketID)))
    } else constants.Response.NFT_ALREADY_ON_SALE.throwBaseException()

    def delete(nftId: String, ownerId: String): Future[Int] = deleteById1AndId2(id1 = nftId, id2 = ownerId)

    def deleteByNFT(nftId: String): Future[Int] = filterAndDelete(_.nftId === nftId)

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

    def onSuccessfulTakeOrder(secondaryMarketId: String, totalSold: Int, buyerId: String): Future[NFTOwner] = {
      val oldNFTOwner = Service.tryGetBySecondaryMarketId(secondaryMarketId)

      def verifyAndUpdate(oldNFTOwner: NFTOwner) = {
        if (oldNFTOwner.quantity == 1) {
          for {
            _ <- delete(nftId = oldNFTOwner.nftId, ownerId = oldNFTOwner.ownerId)
            _ <- create(oldNFTOwner.copy(secondaryMarketId = None, ownerId = buyerId))
          } yield ()
        } else
          for {
            _ <- update(oldNFTOwner.copy(quantity = oldNFTOwner.quantity - totalSold))
            _ <- create(oldNFTOwner.copy(secondaryMarketId = None, quantity = totalSold, ownerId = buyerId))
          } yield ()
      }

      for {
        oldNFTOwner <- oldNFTOwner
        _ <- verifyAndUpdate(oldNFTOwner)
      } yield oldNFTOwner
    }

    def getSaleId(nftId: String): Future[Option[String]] = filterHead(_.nftId === nftId).map(_.saleId)

    def tryGet(nftId: String, ownerId: String): Future[NFTOwner] = tryGetById1AndId2(id1 = nftId, id2 = ownerId)

    def get(nftId: String, ownerId: String): Future[Option[NFTOwner]] = getById(id1 = nftId, id2 = ownerId)

    def getOwners(nftId: String): Future[Seq[NFTOwner]] = filter(_.id1 === nftId)

    def getOwnersByCollectionId(collectionId: String): Future[Seq[String]] = customQuery(NFTOwners.TableQuery.filter(_.collectionId === collectionId).map(_.ownerId).result)

    def checkExists(nftId: String, ownerId: String): Future[Boolean] = exists(id1 = nftId, id2 = ownerId)

    def markSaleNull(saleId: String): Future[Int] = {
      val nullString: Option[String] = null
      customUpdate(NFTOwners.TableQuery.filter(_.saleId === saleId).map(_.saleId.?).update(nullString))
    }

    def markPublicListingNull(publicListingId: String): Future[Int] = {
      val nullString: Option[String] = null
      customUpdate(NFTOwners.TableQuery.filter(_.publicListingId === publicListingId).map(_.publicListingId.?).update(nullString))
    }

    def markSecondaryMarketNull(secondaryMarketId: String): Future[Int] = {
      val nullString: Option[String] = null
      customUpdate(NFTOwners.TableQuery.filter(_.secondaryMarketId === secondaryMarketId).map(_.secondaryMarketId.?).update(nullString))
    }

    def tryGetBySecondaryMarketId(secondaryMarketId: String): Future[NFTOwner] = filterHead(_.secondaryMarketId === secondaryMarketId)

    def countOwnedNFTs(accountId: String): Future[Int] = filterAndCount(x => x.ownerId === accountId && x.creatorId =!= accountId)

    def countCollectionOwnedNFTs(accountId: String, collectionID: String): Future[Int] = filterAndCount(x => x.ownerId === accountId && x.creatorId =!= accountId && x.collectionId === collectionID)

    def getRandomNFTsBySaleId(saleId: String, take: Int, creatorId: String): Future[Seq[NFTOwner]] = filter(x => x.saleId === saleId && x.ownerId === creatorId && x.creatorId === creatorId).map(x => util.Random.shuffle(x).take(take))

    def getRandomNFTsByPublicListingId(publicListingId: String, take: Int, creatorId: String): Future[Seq[NFTOwner]] = filter(x => x.publicListingId === publicListingId && x.ownerId === creatorId && x.creatorId === creatorId).map(x => util.Random.shuffle(x).take(take))

    def deleteCollections(collectionIds: Seq[String]): Future[Int] = filterAndDelete(_.collectionId.inSet(collectionIds))

    def getCollectedCollection(accountId: String): Future[Seq[String]] = filter(x => x.ownerId === accountId && x.creatorId =!= accountId).map(_.map(_.collectionId).distinct)

    def getByCollectionAndPageNumber(accountId: String, collectionId: String, pageNumber: Int): Future[Seq[String]] = filterAndSortWithPagination(offset = (pageNumber - 1) * constants.CommonConfig.Pagination.NFTsPerPage, limit = constants.CommonConfig.Pagination.NFTsPerPage)(x => x.ownerId === accountId && x.creatorId =!= accountId && x.collectionId === collectionId)(_.updatedOnMillisEpoch).map(_.map(_.nftId))

    def getSoldNFTs(collectionIDs: Seq[String]): Future[Seq[String]] = filter(x => x.ownerId =!= x.creatorId && x.collectionId.inSet(collectionIDs)).map(_.map(_.nftId))

    def getByIds(nftIDs: Seq[String]): Future[Seq[NFTOwner]] = filter(_.nftId.inSet(nftIDs))

    def getByMarketIDs(collectionId: String, pageNumber: Int): Future[Seq[String]] = filterAndSortWithPagination(offset = (pageNumber - 1) * constants.CommonConfig.Pagination.NFTsPerPage, limit = constants.CommonConfig.Pagination.NFTsPerPage)(x => x.collectionId === collectionId && x.secondaryMarketId.?.isDefined)(_.updatedOnMillisEpoch).map(_.map(_.nftId))

    def countOwners(nftId: String): Future[Int] = filterAndCount(_.nftId === nftId)

    def getByNFTID(nftId: String): Future[NFTOwner] = filterHead(_.nftId === nftId)

    def onSuccessfulNFTTransfer(nftId: String, fromOwnerID: String, quantity: Int, toOwnerID: String): Future[Unit] = {
      val fromNFTOwner = tryGet(nftId = nftId, ownerId = fromOwnerID)
      val toNFTOwner = get(nftId = nftId, ownerId = toOwnerID)

      def fromUpdate(fromOwner: NFTOwner) = if (fromOwner.quantity - quantity >= 0) {
        if (fromOwner.quantity - quantity == 0) delete(nftId = nftId, ownerId = fromOwnerID) else update(fromOwner.copy(quantity = fromOwner.quantity - quantity))
      } else constants.Response.INSUFFICIENT_NFT_BALANCE.throwBaseException()

      def toUpdate(fromOwner: NFTOwner, toOwner: Option[NFTOwner]) = if (toOwner.isDefined) update(toOwner.get.copy(quantity = toOwner.get.quantity + quantity)) else add(fromOwner.copy(ownerId = toOwnerID, quantity = quantity))

      for {
        fromNFTOwner <- fromNFTOwner
        toNFTOwner <- toNFTOwner
        _ <- fromUpdate(fromNFTOwner)
        _ <- toUpdate(fromNFTOwner, toNFTOwner)
      } yield ()
    }

    def tryGetByNFTAndSaleId(nftId: String, saleId: String): Future[NFTOwner] = filterHead(x => x.saleId === saleId && x.nftId === nftId)
    //    https://scala-slick.org/doc/3.1.1/sql-to-slick.html#id21
    //    def getQuery = NFTOwners.TableQuery.filter(x => x.ownerId === "asd" && x.creatorId)
  }
}