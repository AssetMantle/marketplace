package models.masterTransaction

import models.common.NFT._
import models.master._
import models.masterTransaction.NFTDrafts.NFTDraftTable
import models.traits._
import play.api.Logger
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.json.Json
import slick.jdbc.H2Profile.api._

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

case class NFTDraft(id: String, collectionId: String, name: Option[String], description: Option[String], properties: Option[Seq[BaseNFTProperty]], tagNames: Option[Seq[String]], fileExtension: String, createdBy: Option[String] = None, createdOnMillisEpoch: Option[Long] = None, updatedBy: Option[String] = None, updatedOnMillisEpoch: Option[Long] = None) extends Logging {

  def getSupply: Long = this.properties.fold(1L)(_.find(_.name == schema.constants.Properties.SupplyProperty.id.keyID.value).fold(1L)(_.valueAsString.toLong))

  def getBondAmount(collection: Collection): Long = this.properties.fold(collection.getBaseDenomBondAmount)(_.find(_.name == schema.constants.Properties.BondAmountProperty.id.keyID.value).fold(collection.getBaseDenomBondAmount)(_.valueAsString.toLong))

  def getFileHash: String = id

  def getFileName: String = this.id + "." + this.fileExtension

  def toNFT(collection: Collection): NFT = {
    val nft = NFT(id = id, assetId = None, fileExtension = fileExtension, collectionId = collectionId, name = name.getOrElse(""), description = description.getOrElse(""), totalSupply = this.getSupply, customBondAmount = Option(this.getBondAmount(collection)), isMinted = Option(false), mintReady = false)
    nft.copy(assetId = Option(nft.getAssetID(this.getNFTProperties, collection).asString))
  }

  def toNFTOwner(ownerID: String, creatorId: String): NFTOwner = NFTOwner(nftId = id, ownerId = ownerID, creatorId = creatorId, collectionId = collectionId, quantity = this.getSupply, saleId = None, publicListingId = None)

  def getNFTProperties: Seq[NFTProperty] = this.properties.fold[Seq[NFTProperty]](Seq())(x => x.map(_.toNFTProperty(this.id)))

  def getTags: Seq[NFTTag] = this.tagNames.fold[Seq[NFTTag]](Seq())(_.map(x => NFTTag(tagName = x, nftId = this.id)))

  def serialize(): NFTDrafts.NFTDraftSerialized = NFTDrafts.NFTDraftSerialized(
    id = this.id,
    collectionId = collectionId,
    name = this.name,
    description = this.description,
    fileExtension = this.fileExtension.toLowerCase,
    properties = this.properties.map(Json.toJson(_).toString()),
    tagNames = this.tagNames.map(Json.toJson(_).toString()),
    createdBy = this.createdBy,
    createdOnMillisEpoch = this.createdOnMillisEpoch,
    updatedBy = this.updatedBy,
    updatedOnMillisEpoch = this.updatedOnMillisEpoch)
}

private[masterTransaction] object NFTDrafts {

  case class NFTDraftSerialized(id: String, collectionId: String, name: Option[String], description: Option[String], fileExtension: String, properties: Option[String], tagNames: Option[String], createdBy: Option[String], createdOnMillisEpoch: Option[Long], updatedBy: Option[String], updatedOnMillisEpoch: Option[Long]) extends Entity[String] {
    def deserialize()(implicit module: String, logger: Logger): NFTDraft = NFTDraft(id = id, collectionId = collectionId, name = name, description = description, fileExtension = fileExtension, properties = properties.map(utilities.JSON.convertJsonStringToObject[Seq[BaseNFTProperty]](_)), tagNames = tagNames.map(utilities.JSON.convertJsonStringToObject[Seq[String]](_)), createdBy = createdBy, createdOnMillisEpoch = createdOnMillisEpoch, updatedBy = updatedBy, updatedOnMillisEpoch = updatedOnMillisEpoch)

  }

  class NFTDraftTable(tag: Tag) extends Table[NFTDraftSerialized](tag, "NFTDraft") with ModelTable[String] {

    def * = (id, collectionId, name.?, description.?, fileExtension, properties.?, tagNames.?, createdBy.?, createdOnMillisEpoch.?, updatedBy.?, updatedOnMillisEpoch.?) <> (NFTDraftSerialized.tupled, NFTDraftSerialized.unapply)

    def id = column[String]("id", O.PrimaryKey)

    def collectionId = column[String]("collectionId")

    def fileExtension = column[String]("fileExtension")

    def name = column[String]("name")

    def description = column[String]("description")

    def properties = column[String]("properties")

    def tagNames = column[String]("tagNames")

    def createdBy = column[String]("createdBy")

    def createdOnMillisEpoch = column[Long]("createdOnMillisEpoch")

    def updatedBy = column[String]("updatedBy")

    def updatedOnMillisEpoch = column[Long]("updatedOnMillisEpoch")

  }
}

@Singleton
class NFTDrafts @Inject()(
                           protected val dbConfigProvider: DatabaseConfigProvider,
                         )(implicit val executionContext: ExecutionContext)
  extends GenericDaoImpl[NFTDrafts.NFTDraftTable, NFTDrafts.NFTDraftSerialized, String]() {

  implicit val module: String = constants.Module.MASTER_TRANSACTION_NFT_DRAFT

  implicit val logger: Logger = Logger(this.getClass)

  val tableQuery = new TableQuery(tag => new NFTDraftTable(tag))

  object Service {

    def add(id: String, fileExtension: String, collectionId: String): Future[String] = {
      val nft = NFTDraft(
        id = id,
        fileExtension = fileExtension,
        collectionId = collectionId,
        name = None,
        description = None,
        properties = None,
        tagNames = None,
      )
      create(nft.serialize()).map(_.id)
    }

    def tryGet(id: String): Future[NFTDraft] = tryGetById(id).map(_.deserialize)

    def getByPageNumber(collectionId: String, pageNumber: Int): Future[Seq[NFTDraft]] = filterAndSortWithPagination(_.collectionId === collectionId)(_.updatedOnMillisEpoch.desc)(offset = (pageNumber - 1) * constants.CommonConfig.Pagination.NFTsPerPage, limit = constants.CommonConfig.Pagination.NFTsPerPage).map(_.map(_.deserialize))

    def getAllForCollection(collectionId: String): Future[Seq[NFTDraft]] = filter(_.collectionId === collectionId).map(_.map(_.deserialize))

    def get(id: String): Future[Option[NFTDraft]] = getById(id).map(_.map(_.deserialize))

    def countAllForCollection(collectionId: String): Future[Int] = filterAndCount(_.collectionId === collectionId)

    def checkExists(id: String): Future[Boolean] = exists(id)

    def deleteByCollectionId(id: String): Future[Int] = filterAndDelete(_.collectionId === id)

    def deleteNFT(id: String): Future[Int] = filterAndDelete(_.id === id)

    def getByIds(ids: Seq[String]): Future[Seq[NFTDraft]] = filter(_.id.inSet(ids)).map(_.map(_.deserialize))

    def updateBasicAndTags(id: String, name: String, description: String, tags: Option[Seq[String]]): Future[NFTDraft] = {
      val draft = tryGet(id)
      for {
        draft <- draft
        _ <- updateById(draft.copy(name = Option(name), description = Option(description), tagNames = tags).serialize())
      } yield draft.copy(name = Option(name), description = Option(description))
    }

    def updateProperties(id: String, properties: Seq[BaseNFTProperty]): Future[NFTDraft] = {
      val draft = tryGet(id)
      for {
        nftDraft <- draft
        _ <- updateById(nftDraft.copy(properties = Option(properties)).serialize())
      } yield nftDraft.copy(properties = Option(properties))
    }

    def updateTagNames(id: String, tagNames: Seq[String]): Future[Unit] = {
      val draft = tryGet(id)
      for {
        draft <- draft
        _ <- updateById(draft.copy(tagNames = if (tagNames.nonEmpty) Option(tagNames) else None).serialize())
      } yield ()
    }

    def delete(id: String): Future[Int] = deleteById(id)

    def deleteByCollectionIds(ids: Seq[String]): Future[Int] = filterAndDelete(_.collectionId.inSet(ids))

    def getAllNFTs: Future[Seq[NFTDraft]] = getAll.map(_.map(_.deserialize))
  }
}