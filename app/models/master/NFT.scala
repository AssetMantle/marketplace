package models.master

import models.Trait.{Entity, GenericDaoImpl, ModelTable}
import play.api.Logger
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.H2Profile.api._

import java.sql.Timestamp
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

case class NFT(fileName: String, collectionId: String, name: String, description: String, totalSupply: Long, isMinted: Boolean, ipfsLink: String, edition: Option[Int], createdBy: Option[String] = None, createdOn: Option[Timestamp] = None, createdOnTimeZone: Option[String] = None, updatedBy: Option[String] = None, updatedOn: Option[Timestamp] = None, updatedOnTimeZone: Option[String] = None) extends Entity[String] {

  def getFileHash: String = utilities.FileOperations.getFileNameWithoutExtension(fileName)

  def getS3Url: String = constants.CommonConfig.AmazonS3.s3BucketURL + utilities.Collection.getNFTFileAwsKey(collectionId = this.collectionId, fileName = this.fileName)

  def id: String = fileName
}

object NFTs {

  implicit val module: String = constants.Module.MASTER_NFT

  implicit val logger: Logger = Logger(this.getClass)

  class NFTTable(tag: Tag) extends Table[NFT](tag, "NFT") with ModelTable[String] {

    def * = (fileName, collectionId, name, description, totalSupply, isMinted, ipfsLink, edition.?, createdBy.?, createdOn.?, createdOnTimeZone.?, updatedBy.?, updatedOn.?, updatedOnTimeZone.?) <> (NFT.tupled, NFT.unapply)

    def fileName = column[String]("fileName", O.PrimaryKey)

    def collectionId = column[String]("collectionId")

    def name = column[String]("name")

    def description = column[String]("description")

    def totalSupply = column[Long]("totalSupply")

    def isMinted = column[Boolean]("isMinted")

    def ipfsLink = column[String]("ipfsLink")

    def edition = column[Int]("edition")

    def createdBy = column[String]("createdBy")

    def createdOn = column[Timestamp]("createdOn")

    def createdOnTimeZone = column[String]("createdOnTimeZone")

    def updatedBy = column[String]("updatedBy")

    def updatedOn = column[Timestamp]("updatedOn")

    def updatedOnTimeZone = column[String]("updatedOnTimeZone")

    override def id = fileName
  }

  val TableQuery = new TableQuery(tag => new NFTTable(tag))
}

@Singleton
class NFTs @Inject()(
                      protected val databaseConfigProvider: DatabaseConfigProvider
                    )(implicit override val executionContext: ExecutionContext)
  extends GenericDaoImpl[NFTs.NFTTable, NFT, String](
    databaseConfigProvider,
    NFTs.TableQuery,
    executionContext,
    NFTs.module,
    NFTs.logger
  ) {


  object Service {

    def add(fileName: String, collectionId: String, name: String, description: String, totalSupply: Long, isMinted: Boolean, ipfsLink: String, edition: Option[Int]): Future[String] = {
      val nft = NFT(
        fileName = fileName,
        collectionId = collectionId,
        name = name,
        description = description,
        totalSupply = totalSupply,
        ipfsLink = ipfsLink,
        edition = edition,
        isMinted = isMinted)
      create(nft)
    }

    def add(nft: NFT): Future[String] = create(nft)

    def tryGet(nftId: String): Future[NFT] = tryGetById(nftId)

    def getAllIdsForCollection(collectionId: String): Future[Seq[String]] = filter(_.collectionId === collectionId).map(_.map(_.id))

    def getByPageNumber(collectionId: String, pageNumber: Int): Future[Seq[NFT]] = filterAndSortWithPagination(offset = (pageNumber - 1) * constants.CommonConfig.Pagination.NFTsPerPage, limit = constants.CommonConfig.Pagination.NFTsPerPage)(_.collectionId === collectionId)(_.createdOn)

    def checkExists(id: String): Future[Boolean] = exists(id)

    def deleteByCollectionId(id: String): Future[Int] = filterAndDelete(_.collectionId === id)

    def getByIds(ids: Seq[String]): Future[Seq[NFT]] = filter(_.id.inSet(ids))

    def deleteCollections(collectionIds: Seq[String]): Future[Int] = filterAndDelete(_.collectionId.inSet(collectionIds))

    def update(nft: NFT): Future[Unit] = updateById(nft)

    def countNFTs(collectionId: String): Future[Int] = filterAndCount(_.collectionId === collectionId)

    def getRandomNFTs(collectionId: String, n: Int, filterOut: Seq[String]): Future[Seq[String]] = filter(x => x.collectionId === collectionId && !x.fileName.inSet(filterOut)).map(x => util.Random.shuffle(x.map(_.fileName)).take(n))
  }
}