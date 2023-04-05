package models.master

import models.traits.{Entity, GenericDaoImpl, ModelTable}
import play.api.Logger
import play.api.db.slick.DatabaseConfigProvider
import schema.id.base.{AssetID, HashID}
import schema.list.PropertyList
import schema.property.base.{MesaProperty, MetaProperty}
import schema.qualified.{Immutables, Mutables}
import slick.jdbc.H2Profile.api._

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

case class NFT(id: String, assetId: Option[String], collectionId: String, name: String, description: String, totalSupply: Long, isMinted: Option[Boolean], mintReady: Boolean, fileExtension: String, ipfsLink: String, edition: Option[Int], createdBy: Option[String] = None, createdOnMillisEpoch: Option[Long] = None, updatedBy: Option[String] = None, updatedOnMillisEpoch: Option[Long] = None) extends Entity[String] {

  def getFileHash: String = id

  def getFileName: String = this.id + "." + this.fileExtension

  def getAssetID: AssetID = AssetID(HashID(utilities.Secrets.base64URLDecode(this.assetId.getOrElse("UNKNOWN_ASSET_ID"))))

  def getAssetID(nftProperties: Seq[NFTProperty], collection: Collection): AssetID = utilities.NFT.getAssetID(collection.getClassificationID, this.getImmutables(nftProperties, collection))

  def getS3Url: String = constants.CommonConfig.AmazonS3.s3BucketURL + utilities.Collection.getNFTFileAwsKey(collectionId = this.collectionId, fileName = this.getFileName)

  def getImmutableMetaProperties(nftProperties: Seq[NFTProperty], collection: Collection): Seq[MetaProperty] = nftProperties.filter(x => x.meta && !x.mutable && x.nftId == this.id).map(_.toMetaProperty()(Collections.module, Collections.logger)) ++ utilities.NFT.getDefaultImmutableMetaProperties(name = this.name, description = this.description, fileHash = this.getFileHash, bondAmount = collection.getBondAmount.value.toLong)

  def getImmutableProperties(nftProperties: Seq[NFTProperty], collection: Collection): Seq[MesaProperty] = nftProperties.filter(x => !x.meta && !x.mutable && x.nftId == this.id).map(_.toMesaProperty()(Collections.module, Collections.logger)) ++ constants.Collection.DefaultProperty.allImmutableMesaProperties(collection.getCreatorIdentityID)

  def getMutableMetaProperties(nftProperties: Seq[NFTProperty]): Seq[MetaProperty] = nftProperties.filter(x => x.meta && x.mutable && x.nftId == this.id).map(_.toMetaProperty()(Collections.module, Collections.logger))

  def getMutableProperties(nftProperties: Seq[NFTProperty]): Seq[MesaProperty] = nftProperties.filter(x => !x.meta && x.mutable && x.nftId == this.id).map(_.toMesaProperty()(Collections.module, Collections.logger))

  def getImmutables(nftProperties: Seq[NFTProperty], collection: Collection): Immutables = Immutables(PropertyList(this.getImmutableMetaProperties(nftProperties, collection) ++ this.getImmutableProperties(nftProperties, collection)))

  def getMutables(nftProperties: Seq[NFTProperty]): Mutables = Mutables(PropertyList(this.getMutableMetaProperties(nftProperties) ++ this.getMutableProperties(nftProperties)))

}

object NFTs {

  implicit val module: String = constants.Module.MASTER_NFT

  implicit val logger: Logger = Logger(this.getClass)

  class NFTTable(tag: Tag) extends Table[NFT](tag, "NFT") with ModelTable[String] {

    def * = (id, assetId.?, collectionId, name, description, totalSupply, isMinted.?, mintReady, fileExtension, ipfsLink, edition.?, createdBy.?, createdOnMillisEpoch.?, updatedBy.?, updatedOnMillisEpoch.?) <> (NFT.tupled, NFT.unapply)

    def id = column[String]("id", O.PrimaryKey)

    def assetId = column[String]("assetId")

    def collectionId = column[String]("collectionId")

    def name = column[String]("name")

    def description = column[String]("description")

    def totalSupply = column[Long]("totalSupply")

    def isMinted = column[Boolean]("isMinted")

    def mintReady = column[Boolean]("mintReady")

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

    def getAllINFTsForCollections(collectionIds: Seq[String]): Future[Seq[NFT]] = filter(_.collectionId.inSet(collectionIds))

    def getByPageNumber(collectionId: String, pageNumber: Int): Future[Seq[NFT]] = filterAndSortWithPagination(offset = (pageNumber - 1) * constants.CommonConfig.Pagination.NFTsPerPage, limit = constants.CommonConfig.Pagination.NFTsPerPage)(_.collectionId === collectionId)(_.createdOnMillisEpoch)

    def checkExists(id: String): Future[Boolean] = exists(id)

    def deleteByCollectionId(id: String): Future[Int] = filterAndDelete(_.collectionId === id)

    def getByIds(ids: Seq[String]): Future[Seq[NFT]] = filter(_.id.inSet(ids))

    def getForMinting: Future[Seq[NFT]] = filter(_.mintReady).map(_.take(300))

    def deleteCollections(collectionIds: Seq[String]): Future[Int] = filterAndDelete(_.collectionId.inSet(collectionIds))

    def update(nft: NFT): Future[Unit] = updateById(nft)

    def countNFTs(collectionId: String): Future[Int] = filterAndCount(_.collectionId === collectionId)

    def markNFTsMintPending(ids: Seq[String]): Future[Int] = customUpdate(NFTs.TableQuery.filter(_.id.inSet(ids)).map(_.isMinted.?).update(null))

    def markNFTsMinted(ids: Seq[String]): Future[Int] = customUpdate(NFTs.TableQuery.filter(_.id.inSet(ids)).map(_.isMinted).update(true))

    def markNFTsMintFailed(ids: Seq[String]): Future[Int] = customUpdate(NFTs.TableQuery.filter(_.id.inSet(ids)).map(_.isMinted).update(false))

    def updateAssetID(id: String, assetID: AssetID): Future[Int] = customUpdate(NFTs.TableQuery.filter(_.id === id).map(_.assetId).update(assetID.asString))

    def fetchAllWithNullAssetID(): Future[Seq[NFT]] = filter(_.assetId.?.isEmpty)

    def getRandomNFTs(collectionId: String, n: Int, filterOut: Seq[String]): Future[Seq[NFT]] = filter(x => x.collectionId === collectionId && !x.id.inSet(filterOut)).map(util.Random.shuffle(_).take(n))

    def getUnmintedNFTs(collectionId: String): Future[Seq[NFT]] = filter(x => x.collectionId === collectionId && !x.isMinted.?.getOrElse(true))

    def getByAssetId(assetId: AssetID): Future[Option[NFT]] = filter(_.assetId === assetId.asString).map(_.headOption)

    def getByAssetId(assetId: String): Future[Option[NFT]] = filter(_.assetId === assetId).map(_.headOption)

    def delete(id: String): Future[Int] = deleteById(id)

    def markReadyForMint(ids: Seq[String]): Future[Int] = if (ids.nonEmpty) customUpdate(NFTs.TableQuery.filter(_.id.inSet(ids)).map(_.mintReady).update(true)) else Future(0)
  }
}