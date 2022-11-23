package models.master

import models.Trait.{Entity, GenericDaoImpl, ModelTable}
import play.api.Logger
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.H2Profile.api._

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

case class NFT(id: String, collectionId: String, name: String, description: String, totalSupply: Long, isMinted: Boolean, fileExtension: String, ipfsLink: String, edition: Option[Int], createdBy: Option[String] = None, createdOnMillisEpoch: Option[Long] = None, updatedBy: Option[String] = None, updatedOnMillisEpoch: Option[Long] = None) extends Entity[String] {

  def getFileHash: String = id

  def getFileName: String = this.id + "." + this.fileExtension

  def getS3Url: String = constants.CommonConfig.AmazonS3.s3BucketURL + utilities.Collection.getNFTFileAwsKey(collectionId = this.collectionId, fileName = this.getFileName)

  //  def getAllProperties(nftProperties: Seq[NFTProperty]) = constants.Collection.DefaultProperty.list.map(_.asNFTProperty())
}

object NFTs {

  implicit val module: String = constants.Module.MASTER_NFT

  implicit val logger: Logger = Logger(this.getClass)

  class NFTTable(tag: Tag) extends Table[NFT](tag, "NFT") with ModelTable[String] {

    def * = (id, collectionId, name, description, totalSupply, isMinted, fileExtension, ipfsLink, edition.?, createdBy.?, createdOnMillisEpoch.?, updatedBy.?, updatedOnMillisEpoch.?) <> (NFT.tupled, NFT.unapply)

    def id = column[String]("id", O.PrimaryKey)

    def collectionId = column[String]("collectionId")

    def name = column[String]("name")

    def description = column[String]("description")

    def totalSupply = column[Long]("totalSupply")

    def isMinted = column[Boolean]("isMinted")

    def fileExtension = column[String]("fileExtension")

    def ipfsLink = column[String]("ipfsLink")

    def edition = column[Int]("edition")

    def createdBy = column[String]("createdBy")

    def createdOnMillisEpoch = column[Long]("createdOnMillisEpoch")

    def updatedBy = column[String]("updatedBy")

    def updatedOnMillisEpoch = column[Long]("updatedOnMillisEpoch")
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
    def add(nft: NFT): Future[String] = create(nft)

    def tryGet(nftId: String): Future[NFT] = tryGetById(nftId)

    def getAllIdsForCollection(collectionId: String): Future[Seq[String]] = filter(_.collectionId === collectionId).map(_.map(_.id))

    def getByPageNumber(collectionId: String, pageNumber: Int): Future[Seq[NFT]] = filterAndSortWithPagination(offset = (pageNumber - 1) * constants.CommonConfig.Pagination.NFTsPerPage, limit = constants.CommonConfig.Pagination.NFTsPerPage)(_.collectionId === collectionId)(_.createdOnMillisEpoch)

    def checkExists(id: String): Future[Boolean] = exists(id)

    def deleteByCollectionId(id: String): Future[Int] = filterAndDelete(_.collectionId === id)

    def getByIds(ids: Seq[String]): Future[Seq[NFT]] = filter(_.id.inSet(ids))

    def deleteCollections(collectionIds: Seq[String]): Future[Int] = filterAndDelete(_.collectionId.inSet(collectionIds))

    def update(nft: NFT): Future[Unit] = updateById(nft)

    def countNFTs(collectionId: String): Future[Int] = filterAndCount(_.collectionId === collectionId)

    def getRandomNFTs(collectionId: String, n: Int, filterOut: Seq[String]): Future[Seq[String]] = filter(x => x.collectionId === collectionId && !x.id.inSet(filterOut)).map(x => util.Random.shuffle(x.map(_.id)).take(n))
  }
}