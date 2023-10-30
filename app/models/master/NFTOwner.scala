package models.master

import models.master.NFTOwners.NFTOwnerTable
import models.traits._
import play.api.Logger
import play.api.db.slick.DatabaseConfigProvider
import schema.id.base.IdentityID
import slick.jdbc.H2Profile.api._

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}
import scala.util.Random

case class NFTOwner(nftId: String, ownerId: String, creatorId: String, collectionId: String, quantity: BigInt, saleId: Option[String], publicListingId: Option[String], createdBy: Option[String] = None, createdOnMillisEpoch: Option[Long] = None, updatedBy: Option[String] = None, updatedOnMillisEpoch: Option[Long] = None) extends Logging {

  def getCreatorIdentityID: IdentityID = utilities.Identity.getMantlePlaceIdentityID(this.creatorId)

  def getOwnerIdentityID: IdentityID = utilities.Identity.getMantlePlaceIdentityID(this.ownerId)

  def serialize: NFTOwners.NFTOwnerSerialized = NFTOwners.NFTOwnerSerialized(nftId = this.nftId, ownerId = this.ownerId, creatorId = this.creatorId, collectionId = this.collectionId, quantity = BigDecimal(this.quantity), saleId = this.saleId, publicListingId = this.publicListingId, createdBy = this.createdBy, createdOnMillisEpoch = this.createdOnMillisEpoch, updatedBy = this.updatedBy, updatedOnMillisEpoch = this.updatedOnMillisEpoch)

}

private[master] object NFTOwners {
  case class NFTOwnerSerialized(nftId: String, ownerId: String, creatorId: String, collectionId: String, quantity: BigDecimal, saleId: Option[String], publicListingId: Option[String], createdBy: Option[String] = None, createdOnMillisEpoch: Option[Long] = None, updatedBy: Option[String] = None, updatedOnMillisEpoch: Option[Long] = None) extends Entity2[String, String] {
    def id1: String = nftId

    def id2: String = ownerId

    def deserialize()(implicit module: String, logger: Logger): NFTOwner = NFTOwner(nftId = this.nftId, ownerId = this.ownerId, creatorId = this.creatorId, collectionId = this.collectionId, quantity = this.quantity.toBigInt, saleId = this.saleId, publicListingId = this.publicListingId, createdBy = this.createdBy, createdOnMillisEpoch = this.createdOnMillisEpoch, updatedBy = this.updatedBy, updatedOnMillisEpoch = this.updatedOnMillisEpoch)
  }

  class NFTOwnerTable(tag: Tag) extends Table[NFTOwners.NFTOwnerSerialized](tag, "NFTOwner") with ModelTable2[String, String] {

    def * = (nftId, ownerId, creatorId, collectionId, quantity, saleId.?, publicListingId.?, createdBy.?, createdOnMillisEpoch.?, updatedBy.?, updatedOnMillisEpoch.?) <> (NFTOwners.NFTOwnerSerialized.tupled, NFTOwners.NFTOwnerSerialized.unapply)

    def nftId = column[String]("nftId", O.PrimaryKey)

    def ownerId = column[String]("ownerId", O.PrimaryKey)

    def creatorId = column[String]("creatorId")

    def collectionId = column[String]("collectionId")

    def quantity = column[BigDecimal]("quantity")

    def saleId = column[String]("saleId")

    def publicListingId = column[String]("publicListingId")

    def createdBy = column[String]("createdBy")

    def createdOnMillisEpoch = column[Long]("createdOnMillisEpoch")

    def updatedBy = column[String]("updatedBy")

    def updatedOnMillisEpoch = column[Long]("updatedOnMillisEpoch")

    def id1 = nftId

    def id2 = ownerId
  }
}

@Singleton
class NFTOwners @Inject()(
                           protected val dbConfigProvider: DatabaseConfigProvider,
                         )(implicit val executionContext: ExecutionContext)
  extends GenericDaoImpl2[NFTOwners.NFTOwnerTable, NFTOwners.NFTOwnerSerialized, String, String]() {

  implicit val module: String = constants.Module.MASTER_NFT_OWNER

  implicit val logger: Logger = Logger(this.getClass)

  val tableQuery = new TableQuery(tag => new NFTOwnerTable(tag))

  object Service {

    def add(nftOwner: NFTOwner): Future[String] = create(nftOwner.serialize).map(_.nftId)

    def add(nftOwners: Seq[NFTOwner]): Future[Int] = create(nftOwners.map(_.serialize))

    def update(NFTOwner: NFTOwner): Future[Int] = updateById1AndId2(NFTOwner.serialize)

    def countForCreatorForPrimarySale(collectionId: String, creatorId: String, unmintedNFTs: Seq[String]): Future[Int] = {
      val nullString: Option[String] = null
      filterAndCount(x => x.collectionId === collectionId && x.nftId.inSet(unmintedNFTs) && x.creatorId === creatorId && x.ownerId === creatorId && x.saleId.? === nullString && x.publicListingId.? === nullString)
    }

    def whitelistSaleRandomNFTs(collectionId: String, creatorId: String, nfts: Int, saleId: String, unmintedNFTs: Seq[String]): Future[Unit] = {
      val nullString: Option[String] = null
      val notOnSaleNFTs = filter(x => x.ownerId === creatorId && x.creatorId === creatorId && x.nftId.inSet(unmintedNFTs) && x.collectionId === collectionId && x.saleId === nullString && x.publicListingId === nullString)
      for {
        notOnSaleNFTs <- notOnSaleNFTs
        _ <- upsertMultiple(Random.shuffle(notOnSaleNFTs).take(nfts).map(_.copy(saleId = Option(saleId))))
      } yield ()
    }

    def publicListRandomNFTs(collectionId: String, creatorId: String, nfts: Int, publicListingId: String, unmintedNFTs: Seq[String]): Future[Unit] = {
      val nullString: Option[String] = null
      val notOnSaleNFTs = filter(x => x.ownerId === creatorId && x.creatorId === creatorId && x.nftId.inSet(unmintedNFTs) && x.collectionId === collectionId && x.saleId === nullString && x.publicListingId === nullString)
      for {
        notOnSaleNFTs <- notOnSaleNFTs
        _ <- upsertMultiple(Random.shuffle(notOnSaleNFTs).take(nfts).map(_.copy(publicListingId = Option(publicListingId))))
      } yield ()
    }

    def onSecondaryMarket(nftId: String, ownerId: String, sellQuantity: BigInt): Future[Int] = {
      val nftOwner = tryGet(nftId = nftId, ownerId = ownerId)

      def checkAndUpdate(nftOwner: NFTOwner) = if (nftOwner.quantity > sellQuantity) update(nftOwner.copy(quantity = nftOwner.quantity - sellQuantity))
      else if (nftOwner.quantity == sellQuantity) delete(nftId = nftId, ownerId = ownerId)
      else constants.Response.INVALID_QUANTITY_FOR_NFT_SECONDARY_MARKET.throwBaseException()

      for {
        nftOwner <- nftOwner
        result <- checkAndUpdate(nftOwner)
      } yield result
    }

    def delete(nftId: String, ownerId: String): Future[Int] = deleteById1AndId2(id1 = nftId, id2 = ownerId)

    def deleteByNFT(nftId: String): Future[Int] = filterAndDelete(_.nftId === nftId)

    def getAllByCollectionID(collectionID: String): Future[Seq[NFTOwner]] = filter(_.collectionId === collectionID).map(_.map(_.deserialize()))

    // From sale whole quantity gets transferred since cannot mint to multiple accounts
    def markNFTSoldFromSale(nftId: String, saleId: String, sellerAccountId: String, buyerAccountId: String): Future[Unit] = {
      val nftOwner = tryGet(nftId = nftId, ownerId = sellerAccountId)

      def verifyAndUpdate(nftOwner: NFTOwner) = if (nftOwner.saleId.getOrElse("") == saleId) {
        //        if (nftOwner.quantity == 1) {
        val deleteOld = delete(nftId = nftOwner.nftId, ownerId = nftOwner.ownerId)
        val addNew = add(nftOwner.copy(saleId = None, ownerId = buyerAccountId))
        for {
          _ <- deleteOld
          _ <- addNew
        } yield ()
        //        } else constants.Response.HANDLE_MULTIPLE_NFT_QUANTITY_CASE.throwBaseException()
      } else constants.Response.NFT_NOT_ON_SALE.throwBaseException()

      for {
        nftOwner <- nftOwner
        _ <- verifyAndUpdate(nftOwner)
      } yield ()
    }

    def checkAnyPublicListingSaleExists(publicListingId: String): Future[Boolean] = filterAndExists(_.publicListingId === publicListingId)

    def checkAnySaleExists(saleId: String): Future[Boolean] = filterAndExists(_.saleId === saleId)

    // From sale whole quantity gets transferred since cannot mint to multiple accounts
    def markNFTSoldFromPublicListing(nftId: String, publicListingId: String, sellerAccountId: String, buyerAccountId: String): Future[Unit] = {
      val nftOwner = tryGet(nftId = nftId, ownerId = sellerAccountId)

      def verifyAndUpdate(nftOwner: NFTOwner) = if (nftOwner.publicListingId.getOrElse("") == publicListingId) {
        //        if (nftOwner.quantity == 1) {
        for {
          _ <- delete(nftId = nftOwner.nftId, ownerId = nftOwner.ownerId)
          _ <- add(nftOwner.copy(publicListingId = None, ownerId = buyerAccountId))
        } yield ()
        //        } else constants.Response.HANDLE_MULTIPLE_NFT_QUANTITY_CASE.throwBaseException()
      } else constants.Response.NFT_NOT_ON_PUBLIC_LISTING.throwBaseException()

      for {
        nftOwner <- nftOwner
        _ <- verifyAndUpdate(nftOwner)
      } yield ()
    }

    def onSuccessfulBuyFromSecondaryMarket(nftId: String, collection: Collection, totalSold: BigInt, buyerId: String): Future[Unit] = {
      val oldNFTOwner = get(nftId = nftId, ownerId = buyerId)

      def verifyAndUpdate(oldNFTOwner: Option[NFTOwner]) = if (oldNFTOwner.isDefined) {
        for {
          _ <- update(oldNFTOwner.get.copy(quantity = oldNFTOwner.get.quantity + totalSold))
        } yield ()
      } else {
        for {
          _ <- add(NFTOwner(nftId = nftId, ownerId = buyerId, creatorId = collection.creatorId, collectionId = collection.id, quantity = totalSold, saleId = None, publicListingId = None))
        } yield ()
      }

      for {
        oldNFTOwner <- oldNFTOwner
        _ <- verifyAndUpdate(oldNFTOwner)
      } yield ()
    }

    def getSaleId(nftId: String): Future[Option[String]] = filterHead(_.nftId === nftId).map(_.saleId)

    def tryGet(nftId: String, ownerId: String): Future[NFTOwner] = tryGetById1AndId2(id1 = nftId, id2 = ownerId).map(_.deserialize)

    def get(nftId: String, ownerId: String): Future[Option[NFTOwner]] = getById(id1 = nftId, id2 = ownerId).map(_.map(_.deserialize))

    def getOwners(nftId: String): Future[Seq[NFTOwner]] = filter(_.id1 === nftId).map(_.map(_.deserialize))

    def getOwnersByCollectionId(collectionId: String): Future[Seq[String]] = customQuery(tableQuery.filter(_.collectionId === collectionId).map(_.ownerId).result)

    def checkExists(nftId: String, ownerId: String): Future[Boolean] = exists(id1 = nftId, id2 = ownerId)

    def markSaleNull(saleId: String): Future[Int] = {
      val nullString: Option[String] = null
      customUpdate(tableQuery.filter(_.saleId === saleId).map(_.saleId.?).update(nullString))
    }

    def markPublicListingNull(publicListingId: String): Future[Int] = {
      val nullString: Option[String] = null
      customUpdate(tableQuery.filter(_.publicListingId === publicListingId).map(_.publicListingId.?).update(nullString))
    }

    def unlistFromSecondaryMarket(nft: NFT, collection: Collection, ownerId: String, sellQuantity: BigInt): Future[Unit] = {
      val nftOwner = get(nftId = nft.id, ownerId = ownerId)

      def checkAndUpdate(nftOwner: Option[NFTOwner]) = if (nftOwner.isEmpty) {
        add(NFTOwner(nftId = nft.id, ownerId = ownerId, creatorId = collection.creatorId, collectionId = collection.id, quantity = sellQuantity, saleId = None, publicListingId = None))
      } else update(nftOwner.get.copy(quantity = nftOwner.get.quantity + sellQuantity))

      for {
        nftOwner <- nftOwner
        _ <- checkAndUpdate(nftOwner)
      } yield ()
    }

    def countOwnedNFTs(accountId: String): Future[Int] = filterAndCount(x => x.ownerId === accountId && x.creatorId =!= accountId)

    def countCollectionOwnedNFTs(accountId: String, collectionID: String): Future[Int] = filterAndCount(x => x.ownerId === accountId && x.creatorId =!= accountId && x.collectionId === collectionID)

    def getRandomNFTsBySaleId(saleId: String, take: Int, creatorId: String): Future[Seq[NFTOwner]] = filter(x => x.saleId === saleId && x.ownerId === creatorId && x.creatorId === creatorId).map(x => util.Random.shuffle(x).take(take)).map(_.map(_.deserialize))

    def getRandomNFTsByPublicListingId(publicListingId: String, take: Int, creatorId: String): Future[Seq[NFTOwner]] = filter(x => x.publicListingId === publicListingId && x.ownerId === creatorId && x.creatorId === creatorId).map(x => util.Random.shuffle(x).take(take)).map(_.map(_.deserialize))

    def deleteCollections(collectionIds: Seq[String]): Future[Int] = filterAndDelete(_.collectionId.inSet(collectionIds))

    def getCollectedCollection(accountId: String): Future[Seq[String]] = filter(x => x.ownerId === accountId && x.creatorId =!= accountId).map(_.map(_.collectionId).distinct)

    def getByCollectionAndPageNumber(accountId: String, collectionId: String, pageNumber: Int): Future[Seq[NFTOwner]] = filterAndSortWithPagination(x => x.ownerId === accountId && x.creatorId =!= accountId && x.collectionId === collectionId)(_.updatedOnMillisEpoch)(offset = (pageNumber - 1) * constants.CommonConfig.Pagination.NFTsPerPage, limit = constants.CommonConfig.Pagination.NFTsPerPage).map(_.map(_.deserialize()))

    def getSoldNFTs(collectionIDs: Seq[String]): Future[Seq[String]] = filter(x => x.ownerId =!= x.creatorId && x.collectionId.inSet(collectionIDs)).map(_.map(_.nftId))

    def getByIds(nftIDs: Seq[String]): Future[Seq[NFTOwner]] = filter(_.nftId.inSet(nftIDs)).map(_.map(_.deserialize))

    def getByOwnerAndIds(ownerId: String, nftIDs: Seq[String]): Future[Seq[NFTOwner]] = filter(x => x.ownerId === ownerId && x.nftId.inSet(nftIDs)).map(_.map(_.deserialize))

    def countOwners(nftId: String): Future[Int] = filterAndCount(_.nftId === nftId)

    def getByNFTID(nftId: String): Future[NFTOwner] = filterHead(_.nftId === nftId).map(_.deserialize)

    def onSuccessfulNFTTransfer(nftId: String, fromOwnerID: String, quantity: Long, toOwnerID: String): Future[Unit] = {
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

    def tryGetByNFTAndSaleId(nftId: String, saleId: String): Future[NFTOwner] = filterHead(x => x.saleId === saleId && x.nftId === nftId).map(_.deserialize)
    //    https://scala-slick.org/doc/3.1.1/sql-to-slick.html#id21
    //    def getQuery = tableQuery.filter(x => x.ownerId === "asd" && x.creatorId)
  }
}