package models.master

import models.master.NFTs.NFTTable
import models.traits._
import play.api.Logger
import play.api.db.slick.DatabaseConfigProvider
import schema.id.base.{AssetID, HashID}
import schema.list.PropertyList
import schema.property.base.{MesaProperty, MetaProperty}
import schema.qualified.{Immutables, Mutables}
import slick.jdbc.H2Profile.api._

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

case class NFT(id: String, assetId: Option[String], collectionId: String, name: String, description: String, totalSupply: BigInt, isMinted: Option[Boolean], mintReady: Boolean, fileExtension: String, createdBy: Option[String] = None, createdOnMillisEpoch: Option[Long] = None, updatedBy: Option[String] = None, updatedOnMillisEpoch: Option[Long] = None) extends Logging {

  def getFileHash: String = id

  def getFileName: String = this.id + "." + this.fileExtension

  def getAssetID: AssetID = AssetID(HashID(utilities.Secrets.base64URLDecode(this.assetId.getOrElse("UNKNOWN_ASSET_ID"))))

  def getAssetID(nftProperties: Seq[NFTProperty], collection: Collection)(implicit module: String, logger: Logger): AssetID = utilities.NFT.getAssetID(collection.getClassificationID, this.getImmutables(nftProperties, collection))

  def getAwsKey: String = utilities.NFT.getAWSKey(this.getFileName)

  def getS3Url: String = constants.CommonConfig.AmazonS3.s3BucketURL + this.getAwsKey

  def getImmutableMetaProperties(nftProperties: Seq[NFTProperty], collection: Collection): Seq[MetaProperty] = nftProperties.filter(x => x.meta && !x.mutable && x.nftId == this.id).map(_.toMetaProperty) ++ utilities.Properties.getNFTDefaultImmutableMetaProperties(name = this.name, collectionName = collection.name, fileHash = this.getFileHash, bondAmount = collection.getBondAmount.value.toLong, creatorID = collection.creatorId)

  def getImmutableProperties(nftProperties: Seq[NFTProperty]): Seq[MesaProperty] = nftProperties.filter(x => !x.meta && !x.mutable && x.nftId == this.id).map(_.toMesaProperty)

  def getMutableMetaProperties(nftProperties: Seq[NFTProperty]): Seq[MetaProperty] = nftProperties.filter(x => x.meta && x.mutable && x.nftId == this.id).map(_.toMetaProperty)

  def getMutableProperties(nftProperties: Seq[NFTProperty]): Seq[MesaProperty] = nftProperties.filter(x => !x.meta && x.mutable && x.nftId == this.id).map(_.toMesaProperty)

  def getImmutables(nftProperties: Seq[NFTProperty], collection: Collection): Immutables = Immutables(PropertyList(this.getImmutableMetaProperties(nftProperties, collection) ++ this.getImmutableProperties(nftProperties)))

  def getMutables(nftProperties: Seq[NFTProperty]): Mutables = Mutables(PropertyList(this.getMutableMetaProperties(nftProperties) ++ this.getMutableProperties(nftProperties)))

  def serialize: NFTs.NFTSerialized = NFTs.NFTSerialized(
    id = this.id, assetId = this.assetId, collectionId = this.collectionId, name = this.name, description = this.description, totalSupply = BigDecimal(this.totalSupply), isMinted = this.isMinted, mintReady = this.mintReady, fileExtension = this.fileExtension.toLowerCase, createdBy = this.createdBy, createdOnMillisEpoch = this.createdOnMillisEpoch, updatedBy = this.updatedBy, updatedOnMillisEpoch = this.updatedOnMillisEpoch
  )

  def isImageType: Boolean = constants.File.ALL_IMAGES_WITH_GIF.contains(this.fileExtension)

  def isAudioType: Boolean = constants.File.ALL_AUDIO.contains(this.fileExtension)

}

private[master] object NFTs {

  case class NFTSerialized(id: String, assetId: Option[String], collectionId: String, name: String, description: String, totalSupply: BigDecimal, isMinted: Option[Boolean], mintReady: Boolean, fileExtension: String, createdBy: Option[String] = None, createdOnMillisEpoch: Option[Long] = None, updatedBy: Option[String] = None, updatedOnMillisEpoch: Option[Long] = None) extends Entity[String] {

    def deserialize()(implicit module: String, logger: Logger): NFT = NFT(id = this.id, assetId = this.assetId, collectionId = this.collectionId, name = this.name, description = this.description, totalSupply = this.totalSupply.toBigInt, isMinted = this.isMinted, mintReady = this.mintReady, fileExtension = this.fileExtension, createdBy = this.createdBy, createdOnMillisEpoch = this.createdOnMillisEpoch, updatedBy = this.updatedBy, updatedOnMillisEpoch = this.updatedOnMillisEpoch)
  }

  class NFTTable(tag: Tag) extends Table[NFTSerialized](tag, "NFT") with ModelTable[String] {

    def * = (id, assetId.?, collectionId, name, description, totalSupply, isMinted.?, mintReady, fileExtension, createdBy.?, createdOnMillisEpoch.?, updatedBy.?, updatedOnMillisEpoch.?) <> (NFTSerialized.tupled, NFTSerialized.unapply)

    def id = column[String]("id", O.PrimaryKey)

    def assetId = column[String]("assetId")

    def collectionId = column[String]("collectionId")

    def name = column[String]("name")

    def description = column[String]("description")

    def totalSupply = column[BigDecimal]("totalSupply")

    def isMinted = column[Boolean]("isMinted")

    def mintReady = column[Boolean]("mintReady")

    def fileExtension = column[String]("fileExtension")

    def createdBy = column[String]("createdBy")

    def createdOnMillisEpoch = column[Long]("createdOnMillisEpoch")

    def updatedBy = column[String]("updatedBy")

    def updatedOnMillisEpoch = column[Long]("updatedOnMillisEpoch")
  }
}

@Singleton
class NFTs @Inject()(
                      protected val dbConfigProvider: DatabaseConfigProvider,
                    )(implicit val executionContext: ExecutionContext)
  extends GenericDaoImpl[NFTs.NFTTable, NFTs.NFTSerialized, String]() {

  implicit val module: String = constants.Module.MASTER_NFT

  implicit val logger: Logger = Logger(this.getClass)

  val tableQuery = new TableQuery(tag => new NFTTable(tag))

  object Service {
    def add(nft: NFT): Future[String] = create(nft.serialize).map(_.id)

    def tryGet(nftId: String): Future[NFT] = tryGetById(nftId).map(_.deserialize)

    def getAllIdsForCollection(collectionId: String): Future[Seq[String]] = filter(_.collectionId === collectionId).map(_.map(_.id))

    def getAllIdsForCollections(collectionIds: Seq[String]): Future[Seq[String]] = filter(_.collectionId.inSet(collectionIds)).map(_.map(_.id))

    def getAllINFTsForCollections(collectionIds: Seq[String]): Future[Seq[NFT]] = filter(_.collectionId.inSet(collectionIds)).map(_.map(_.deserialize))

    def getByPageNumber(collectionId: String, pageNumber: Int): Future[Seq[NFT]] = filterAndSortWithPagination(_.collectionId === collectionId)(_.name)(offset = (pageNumber - 1) * constants.CommonConfig.Pagination.NFTsPerPage, limit = constants.CommonConfig.Pagination.NFTsPerPage).map(_.map(_.deserialize))

    def checkExists(id: String): Future[Boolean] = exists(id)

    def getByIds(ids: Seq[String]): Future[Seq[NFT]] = filter(_.id.inSet(ids)).map(_.map(_.deserialize))

    def getForMinting: Future[Seq[NFT]] = filter(x => x.mintReady && !x.isMinted).map(_.take(300)).map(_.map(_.deserialize))

    def deleteCollections(collectionIds: Seq[String]): Future[Int] = filterAndDelete(_.collectionId.inSet(collectionIds))

    def update(nft: NFT): Future[Unit] = updateById(nft.serialize)

    def countNFTs(collectionId: String): Future[Int] = filterAndCount(_.collectionId === collectionId)

    def markNFTsMintPending(ids: Seq[String]): Future[Int] = customUpdate(tableQuery.filter(_.id.inSet(ids)).map(_.isMinted.?).update(null))

    def markNFTsMinted(ids: Seq[String]): Future[Int] = customUpdate(tableQuery.filter(_.id.inSet(ids)).map(_.isMinted).update(true))

    def markNFTsMintFailed(ids: Seq[String]): Future[Int] = customUpdate(tableQuery.filter(_.id.inSet(ids)).map(_.isMinted).update(false))

    def updateAssetID(id: String, assetID: AssetID): Future[Int] = customUpdate(tableQuery.filter(_.id === id).map(_.assetId).update(assetID.asString))

    def fetchAllWithNullAssetID(): Future[Seq[NFT]] = filter(_.assetId.?.isEmpty).map(_.map(_.deserialize))

    def getRandomNFTs(collectionId: String, n: Int, filterOut: Seq[String]): Future[Seq[NFT]] = filter(x => x.collectionId === collectionId && !x.id.inSet(filterOut)).map(util.Random.shuffle(_).take(n)).map(_.map(_.deserialize))

    def getUnmintedNFTIDs(collectionId: String): Future[Seq[String]] = customQuery(tableQuery.filter(x => x.collectionId === collectionId && !x.isMinted.?.getOrElse(true)).map(_.id).result)

    def getByAssetId(assetId: AssetID): Future[Option[NFT]] = filter(_.assetId === assetId.asString).map(_.headOption).map(_.map(_.deserialize))

    def getByAssetId(assetId: String): Future[Option[NFT]] = filter(_.assetId === assetId).map(_.headOption).map(_.map(_.deserialize))

    def delete(id: String): Future[Int] = deleteById(id)

    def markReadyForMint(ids: Seq[String]): Future[Int] = if (ids.nonEmpty) customUpdate(tableQuery.filter(_.id.inSet(ids)).map(_.mintReady).update(true)) else Future(0)

    def getAllNFTs: Future[Seq[NFT]] = getAll.map(_.map(_.deserialize))
  }
}