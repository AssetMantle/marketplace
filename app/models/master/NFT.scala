package models.master

import models.Trait.{Entity, GenericDaoImpl, Logged, ModelTable}
import play.api.Logger
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.H2Profile.api._

import java.sql.Timestamp
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

case class NFT(fileName: String, collectionId: String, name: String, description: String, ownerId: Option[String], supply: Long, ipfsLink: String, edition: Option[Int], createdBy: Option[String] = None, createdOn: Option[Timestamp] = None, createdOnTimeZone: Option[String] = None, updatedBy: Option[String] = None, updatedOn: Option[Timestamp] = None, updatedOnTimeZone: Option[String] = None) extends Logged {

  def getFileHash: String = utilities.FileOperations.getFileNameWithoutExtension(fileName)

  def getS3Url: String = constants.CommonConfig.AmazonS3.s3BucketURL + this.collectionId + "/nfts/" + this.fileName

  def serialize(): NFTs.NFTSerialized = NFTs.NFTSerialized(
    fileName = this.fileName,
    collectionId = collectionId,
    name = this.name,
    description = this.description,
    ownerId = this.ownerId,
    supply = this.supply,
    ipfsLink = this.ipfsLink,
    edition = this.edition,
    createdBy = this.createdBy,
    createdOn = this.createdOn,
    createdOnTimeZone = this.createdOnTimeZone,
    updatedBy = this.updatedBy,
    updatedOn = this.updatedOn,
    updatedOnTimeZone = this.updatedOnTimeZone)
}

object NFTs {

  implicit val module: String = constants.Module.MASTER_NFT

  implicit val logger: Logger = Logger(this.getClass)

  case class NFTSerialized(fileName: String, collectionId: String, name: String, description: String, ownerId: Option[String], supply: Long, ipfsLink: String, edition: Option[Int], createdBy: Option[String], createdOn: Option[Timestamp], createdOnTimeZone: Option[String], updatedBy: Option[String], updatedOn: Option[Timestamp], updatedOnTimeZone: Option[String]) extends Entity[String] {
    def deserialize: NFT = NFT(
      fileName = fileName,
      collectionId = collectionId,
      name = name,
      description = description,
      ownerId = ownerId,
      supply = supply,
      ipfsLink = ipfsLink,
      edition = edition,
      createdBy = createdBy, createdOn = createdOn, createdOnTimeZone = createdOnTimeZone, updatedBy = updatedBy, updatedOn = updatedOn, updatedOnTimeZone = updatedOnTimeZone)

    def id: String = fileName
  }

  class NFTTable(tag: Tag) extends Table[NFTSerialized](tag, "NFT") with ModelTable[String] {

    def * = (fileName, collectionId, name, description, ownerId.?, supply, ipfsLink, edition.?, createdBy.?, createdOn.?, createdOnTimeZone.?, updatedBy.?, updatedOn.?, updatedOnTimeZone.?) <> (NFTSerialized.tupled, NFTSerialized.unapply)

    def fileName = column[String]("fileName", O.PrimaryKey)

    def collectionId = column[String]("collectionId")

    def name = column[String]("name")

    def description = column[String]("description")

    def ownerId = column[String]("ownerId")

    def supply = column[Long]("supply")

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
  extends GenericDaoImpl[NFTs.NFTTable, NFTs.NFTSerialized, String](
    databaseConfigProvider,
    NFTs.TableQuery,
    executionContext,
    NFTs.module,
    NFTs.logger
  ) {


  object Service {

    def add(fileName: String, collectionId: String, name: String, description: String, ownerId: Option[String], supply: Long, ipfsLink: String, edition: Option[Int]): Future[String] = {
      val nft = NFT(
        fileName = fileName,
        collectionId = collectionId,
        name = name,
        description = description,
        ownerId = ownerId,
        supply = supply,
        ipfsLink = ipfsLink,
        edition = edition)
      create(nft.serialize())
    }

    def add(nft: NFT): Future[String] = create(nft.serialize())

    def fetchAll(): Future[Seq[NFT]] = getAll.map(_.map(_.deserialize))

    def tryGet(nftId: String): Future[NFT] = tryGetById(nftId).map(_.deserialize)

    def getByPageNumber(collectionId: String, pageNumber: Int): Future[Seq[NFT]] = filterAndSortWithPagination(offset = (pageNumber - 1) * constants.CommonConfig.Pagination.NFTsPerPage, limit = constants.CommonConfig.Pagination.NFTsPerPage)(_.collectionId === collectionId)(_.createdOn).map(_.map(_.deserialize))

    def getByOwnerIdAndPageNumber(ownerId: String, pageNumber: Int): Future[Seq[NFT]] = filterAndSortWithPagination(offset = (pageNumber - 1) * constants.CommonConfig.Pagination.NFTsPerPage, limit = constants.CommonConfig.Pagination.NFTsPerPage)(_.ownerId.? === Option(ownerId))(_.collectionId).map(_.map(_.deserialize))

    def getAllForCollection(collectionId: String): Future[Seq[NFT]] = filter(_.collectionId === collectionId).map(_.map(_.deserialize))

    def getIdsAllForCollection(collectionId: String): Future[Seq[String]] = filter(_.collectionId === collectionId).map(_.map(_.fileName))

    def getAllForCollectionOwner(collectionId: String): Future[Seq[NFT]] = {
      val ownerId: Option[String] = null
      filter(x => x.collectionId === collectionId && x.ownerId.? === ownerId).map(_.map(_.deserialize))
    }


    def checkExists(id: String): Future[Boolean] = exists(id)

    def deleteByCollectionId(id: String): Future[Int] = filterAndDelete(_.collectionId === id)

    def getByIds(ids: Seq[String]): Future[Seq[NFT]] = filter(_.id.inSet(ids)).map(_.map(_.deserialize))

    def deleteCollections(collectionIds: Seq[String]): Future[Int] = filterAndDelete(_.collectionId.inSet(collectionIds))

    def update(nft: NFT): Future[Unit] = updateById(nft.serialize())

    def getCollectionIds(nfts: Seq[String]): Future[Seq[String]] = filter(_.id.inSet(nfts)).map(_.map(_.collectionId))

    def verifyNFTsCollection(fileNames: Seq[String], collectionId: String): Future[Boolean] = filter(x => x.fileName.inSet(fileNames) && x.collectionId === collectionId).map(_.length == fileNames.length)
  }
}