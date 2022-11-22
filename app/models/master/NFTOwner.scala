package models.master

import models.Trait.{Entity2, GenericDaoImpl2, Logging, ModelTable2}
import play.api.Logger
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.H2Profile.api._

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}
import scala.util.Random

case class NFTOwner(fileName: String, ownerId: String, creatorId: String, collectionId: String, quantity: Long, saleId: Option[String], createdBy: Option[String] = None, createdOnMillisEpoch: Option[Long] = None, updatedBy: Option[String] = None, updatedOnMillisEpoch: Option[Long] = None) extends Entity2[String, String] with Logging {
  def id1: String = fileName

  def id2: String = ownerId
}

object NFTOwners {

  implicit val module: String = constants.Module.MASTER_NFT_OWNER

  implicit val logger: Logger = Logger(this.getClass)


  class NFTOwnerTable(tag: Tag) extends Table[NFTOwner](tag, "NFTOwner") with ModelTable2[String, String] {

    def * = (fileName, ownerId, creatorId, collectionId, quantity, saleId.?, createdBy.?, createdOnMillisEpoch.?, updatedBy.?, updatedOnMillisEpoch.?) <> (NFTOwner.tupled, NFTOwner.unapply)

    def fileName = column[String]("fileName", O.PrimaryKey)

    def ownerId = column[String]("ownerId", O.PrimaryKey)

    def creatorId = column[String]("creatorId")

    def collectionId = column[String]("collectionId")

    def quantity = column[Long]("quantity")

    def saleId = column[String]("saleId")

    def createdBy = column[String]("createdBy")

    def createdOnMillisEpoch = column[Long]("createdOnMillisEpoch")

    def updatedBy = column[String]("updatedBy")

    def updatedOnMillisEpoch = column[Long]("updatedOnMillisEpoch")

    def id1 = fileName

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

    def getByOwnerIdAndPageNumber(ownerId: String, pageNumber: Int): Future[Seq[String]] = filterAndSortWithPagination(offset = (pageNumber - 1) * constants.CommonConfig.Pagination.NFTsPerPage, limit = constants.CommonConfig.Pagination.NFTsPerPage)(x => x.ownerId === ownerId && x.ownerId =!= x.creatorId)(_.fileName).map(_.map(_.fileName))

    def update(NFTOwner: NFTOwner): Future[Unit] = updateById1AndId2(NFTOwner)

    def countForOwnerNotOnSale(collectionId: String, currentOnSaleIds: Seq[String], ownerId: String): Future[Int] = {
      val nullSaleId: Option[String] = null
      filterAndCount(x => x.collectionId === collectionId && x.ownerId === ownerId && (!x.saleId.inSet(currentOnSaleIds) || x.saleId.? === nullSaleId))
    }

    def addRandomNFTsToSale(collectionId: String, ownerId: String, nfts: Int, saleId: String, currentOnSaleIds: Seq[String]): Future[Unit] = {
      val notOnSaleNFTs = filter(x => x.ownerId === ownerId && x.collectionId === collectionId && !x.saleId.inSet(currentOnSaleIds))
      for {
        notOnSaleNFTs <- notOnSaleNFTs
        _ <- upsertMultiple(Random.shuffle(notOnSaleNFTs).take(nfts).map(_.copy(saleId = Option(saleId))))
      } yield ()
    }

    def delete(nftId: String, ownerId: String) = deleteById1AndId2(id1 = nftId, id2 = ownerId)

    def markNFTSold(nftId: String, saleId: String, sellerAccountId: String, buyerAccountId: String): Future[Unit] = {
      val nftOwner = tryGet(fileName = nftId, ownerId = sellerAccountId)

      def verifyAndUpdate(nftOwner: NFTOwner) = if (nftOwner.saleId.getOrElse("") == saleId) {
        if (nftOwner.quantity == 1) {
          for {
            _ <- delete(nftId = nftOwner.fileName, ownerId = nftOwner.ownerId)
            _ <- create(nftOwner.copy(saleId = None, ownerId = buyerAccountId))
          } yield ()
        } else constants.Response.HANDLE_MULTIPLE_NFT_QUANTITY_CASE.throwFutureBaseException()
      } else constants.Response.NFT_NOT_ON_SALE.throwFutureBaseException()

      for {
        nftOwner <- nftOwner
        _ <- verifyAndUpdate(nftOwner)
      } yield ()
    }

    def getSaleId(fileName: String): Future[Option[String]] = filterHead(_.fileName === fileName).map(_.saleId)

    def tryGet(fileName: String, ownerId: String): Future[NFTOwner] = tryGetById1AndId2(id1 = fileName, id2 = ownerId)

    def tryGetByNFTAndSaleId(fileName: String, saleId: String): Future[NFTOwner] = filterHead(x => x.saleId === saleId && x.fileName === fileName)
    //    https://scala-slick.org/doc/3.1.1/sql-to-slick.html#id21
    //    def getQuery = NFTOwners.TableQuery.filter(x => x.ownerId === "asd" && x.creatorId)
  }
}