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

    def getByOwnerId(ownerId: String): Future[Seq[NFTOwner]] = filter(_.ownerId === ownerId)

    def getByOwnerIdAndPageNumber(ownerId: String, pageNumber: Int): Future[Seq[String]] = filterAndSortWithPagination(offset = (pageNumber - 1) * constants.CommonConfig.Pagination.NFTsPerPage, limit = constants.CommonConfig.Pagination.NFTsPerPage)(_.ownerId === ownerId)(_.createdOnMillisEpoch).map(_.map(_.fileName))

    def update(NFTOwner: NFTOwner): Future[Unit] = updateById1AndId2(NFTOwner)

    def countForOwner(collectionId: String, ownerId: String): Future[Int] = filterAndCount(x => x.collectionId === collectionId && x.ownerId === ownerId)

    def countForOwnerNotOnSale(collectionId: String, currentOnSaleIds: Seq[String], ownerId: String): Future[Int] = filterAndCount(x => x.collectionId === collectionId && x.ownerId === ownerId && !x.saleId.inSet(currentOnSaleIds))

    def addRandomNFTsToSale(collectionId: String, ownerId: String, nfts: Int, saleId: String, currentOnSaleIds: Seq[String]): Future[Unit] = {
      val notOnSaleNFTs = filter(x => x.ownerId === ownerId && x.collectionId === collectionId && !x.saleId.inSet(currentOnSaleIds))
      for {
        notOnSaleNFTs <- notOnSaleNFTs
        _ <- upsertMultiple(Random.shuffle(notOnSaleNFTs).take(nfts).map(_.copy(saleId = Option(saleId))))
      } yield ()
    }


    //    https://scala-slick.org/doc/3.1.1/sql-to-slick.html#id21
    //    def getQuery = NFTOwners.TableQuery.filter(x => x.ownerId === "asd" && x.creatorId)
  }
}