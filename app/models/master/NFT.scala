package models.master

import models.Trait.{Entity, GenericDaoImpl, ModelTable}
import play.api.Logger
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.H2Profile.api._

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

case class NFT(id: String, assetId: Option[String], collectionId: String, name: String, description: String, totalSupply: Long, isMinted: Boolean, fileExtension: String, ipfsLink: String, edition: Option[Int], createdBy: Option[String] = None, createdOnMillisEpoch: Option[Long] = None, updatedBy: Option[String] = None, updatedOnMillisEpoch: Option[Long] = None) extends Entity[String] {

  def getFileHash: String = id

  def getFileName: String = this.id + "." + this.fileExtension

  def getS3Url: String = constants.CommonConfig.AmazonS3.s3BucketURL + utilities.Collection.getNFTFileAwsKey(collectionId = this.collectionId, fileName = this.getFileName)

  def getDefaultProperties(classificationId: String): Seq[constants.NFT.Property] = Seq(
    constants.NFT.Property(name = constants.Collection.DefaultProperty.NFT_NAME, `type` = constants.NFT.Data.STRING, `value` = this.name, meta = true, mutable = false),
    constants.NFT.Property(name = constants.Collection.DefaultProperty.NFT_DESCRIPTION, `type` = constants.NFT.Data.STRING, `value` = this.description, meta = true, mutable = false),
    constants.NFT.Property(name = constants.Collection.DefaultProperty.FILE_HASH, `type` = constants.NFT.Data.STRING, `value` = this.id, meta = true, mutable = false),
    constants.NFT.Property(name = constants.Collection.DefaultProperty.CLASSIFICATION_ID, `type` = constants.NFT.Data.STRING, `value` = classificationId, meta = true, mutable = false),
  )

  def getAllProperties(classificationId: String, nftProperties: Seq[NFTProperty]): Seq[constants.NFT.Property] = this.getDefaultProperties(classificationId) ++ nftProperties.map(_.asProperty)
}

object NFTs {

  implicit val module: String = constants.Module.MASTER_NFT

  implicit val logger: Logger = Logger(this.getClass)

  class NFTTable(tag: Tag) extends Table[NFT](tag, "NFT") with ModelTable[String] {

    def * = (id, assetId.?, collectionId, name, description, totalSupply, isMinted, fileExtension, ipfsLink, edition.?, createdBy.?, createdOnMillisEpoch.?, updatedBy.?, updatedOnMillisEpoch.?) <> (NFT.tupled, NFT.unapply)

    def id = column[String]("id", O.PrimaryKey)

    def assetId = column[String]("assetId")

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

    def getAllIdsForCollections(collectionIds: Seq[String]): Future[Seq[String]] = filter(_.collectionId.inSet(collectionIds)).map(_.map(_.id))

    def getByPageNumber(collectionId: String, pageNumber: Int): Future[Seq[NFT]] = filterAndSortWithPagination(offset = (pageNumber - 1) * constants.CommonConfig.Pagination.NFTsPerPage, limit = constants.CommonConfig.Pagination.NFTsPerPage)(_.collectionId === collectionId)(_.createdOnMillisEpoch)

    def checkExists(id: String): Future[Boolean] = exists(id)

    def deleteByCollectionId(id: String): Future[Int] = filterAndDelete(_.collectionId === id)

    def getByIds(ids: Seq[String]): Future[Seq[NFT]] = filter(_.id.inSet(ids))

    def deleteCollections(collectionIds: Seq[String]): Future[Int] = filterAndDelete(_.collectionId.inSet(collectionIds))

    def update(nft: NFT): Future[Unit] = updateById(nft)

    def countNFTs(collectionId: String): Future[Int] = filterAndCount(_.collectionId === collectionId)

    def markNFTMinted(id: String): Future[NFT] =
      for {
        nft <- tryGet(id)
        _ <- update(nft.copy(isMinted = true))
      } yield nft.copy(isMinted = true)

    def getRandomNFTs(collectionId: String, n: Int, filterOut: Seq[String]): Future[Seq[NFT]] = filter(x => x.collectionId === collectionId && !x.id.inSet(filterOut)).map(util.Random.shuffle(_).take(n))
  }
}