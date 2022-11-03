package models.masterTransaction

import models.Trait.{Entity, GenericDaoImpl, Logging, ModelTable}
import models.common.NFT._
import models.master.NFT
import play.api.Logger
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.json.Json
import slick.jdbc.H2Profile.api._

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

case class NFTDraft(fileName: String, collectionId: String, name: Option[String], description: Option[String], properties: Option[Seq[BaseNFTProperty]], hashTags: Option[Seq[String]], createdBy: Option[String] = None, createdOnMillisEpoch: Option[Long] = None, updatedBy: Option[String] = None, updatedOnMillisEpoch: Option[Long] = None) extends Logging {

  def toNFT: NFT = NFT(fileName = fileName, collectionId = collectionId, name = name.getOrElse(""), description = description.getOrElse(""), properties = Seq(), ipfsLink = "", edition = None, baseProperties = properties.getOrElse(Seq()))

  def getFileHash: String = utilities.FileOperations.getFileNameWithoutExtension(fileName)

  def serialize(): NFTDrafts.NFTDraftSerialized = NFTDrafts.NFTDraftSerialized(
    fileName = this.fileName,
    collectionId = collectionId,
    name = this.name,
    description = this.description,
    properties = this.properties.map(Json.toJson(_).toString()),
    hashTags = this.hashTags.map(Json.toJson(_).toString()),
    createdBy = this.createdBy,
    createdOnMillisEpoch = this.createdOnMillisEpoch,
    updatedBy = this.updatedBy,
    updatedOnMillisEpoch = this.updatedOnMillisEpoch)
}

object NFTDrafts {

  implicit val module: String = constants.Module.MASTER_TRANSACTION_NFT_DRAFT

  implicit val logger: Logger = Logger(this.getClass)

  case class NFTDraftSerialized(fileName: String, collectionId: String, name: Option[String], description: Option[String], properties: Option[String], hashTags: Option[String], createdBy: Option[String], createdOnMillisEpoch: Option[Long], updatedBy: Option[String], updatedOnMillisEpoch: Option[Long]) extends Entity[String] {
    def deserialize: NFTDraft = NFTDraft(fileName = fileName, collectionId = collectionId, name = name, description = description, properties = properties.map(utilities.JSON.convertJsonStringToObject[Seq[BaseNFTProperty]](_)), hashTags = hashTags.map(utilities.JSON.convertJsonStringToObject[Seq[String]](_)), createdBy = createdBy, createdOnMillisEpoch = createdOnMillisEpoch, updatedBy = updatedBy, updatedOnMillisEpoch = updatedOnMillisEpoch)

    def id: String = fileName
  }

  class NFTDraftTable(tag: Tag) extends Table[NFTDraftSerialized](tag, "NFTDraft") with ModelTable[String] {

    def * = (fileName, collectionId, name.?, description.?, properties.?, hashTags.?, createdBy.?, createdOnMillisEpoch.?, updatedBy.?, updatedOnMillisEpoch.?) <> (NFTDraftSerialized.tupled, NFTDraftSerialized.unapply)

    def fileName = column[String]("fileName", O.PrimaryKey)

    def collectionId = column[String]("collectionId")

    def name = column[String]("name")

    def description = column[String]("description")

    def properties = column[String]("properties")

    def hashTags = column[String]("hashTags")

    def createdBy = column[String]("createdBy")

    def createdOnMillisEpoch = column[Long]("createdOnMillisEpoch")

    def updatedBy = column[String]("updatedBy")

    def updatedOnMillisEpoch = column[Long]("updatedOnMillisEpoch")

    def id = fileName
  }

  lazy val TableQuery = new TableQuery(tag => new NFTDraftTable(tag))
}

@Singleton
class NFTDrafts @Inject()(
                           protected val databaseConfigProvider: DatabaseConfigProvider
                         )(implicit override val executionContext: ExecutionContext)
  extends GenericDaoImpl[NFTDrafts.NFTDraftTable, NFTDrafts.NFTDraftSerialized, String](
    databaseConfigProvider,
    NFTDrafts.TableQuery,
    executionContext,
    NFTDrafts.module,
    NFTDrafts.logger
  ) {


  object Service {

    def add(fileName: String, collectionId: String): Future[String] = {
      val nft = NFTDraft(
        fileName = fileName,
        collectionId = collectionId,
        name = None,
        description = None,
        properties = None,
        hashTags = None,
      )
      create(nft.serialize())
    }

    def tryGet(nftId: String): Future[NFTDraft] = tryGetById(nftId).map(_.deserialize)

    def getByPageNumber(collectionId: String, pageNumber: Int): Future[Seq[NFTDraft]] = filterAndSortWithPagination(offset = (pageNumber - 1) * constants.CommonConfig.Pagination.NFTsPerPage, limit = constants.CommonConfig.Pagination.NFTsPerPage)(_.collectionId === collectionId)(_.createdOnMillisEpoch).map(_.map(_.deserialize))

    def getAllForCollection(collectionId: String): Future[Seq[NFTDraft]] = filter(_.collectionId === collectionId).map(_.map(_.deserialize))

    def get(nftId: String): Future[Option[NFTDraft]] = getById(nftId).map(_.map(_.deserialize))

    def countAllForCollection(collectionId: String): Future[Int] = filterAndCount(_.collectionId === collectionId)

    def checkExists(id: String): Future[Boolean] = exists(id)

    def deleteByCollectionId(id: String): Future[Int] = filterAndDelete(_.collectionId === id)

    def deleteNFT(fileName: String): Future[Int] = filterAndDelete(_.fileName === fileName)

    def getByIds(ids: Seq[String]): Future[Seq[NFTDraft]] = filter(_.id.inSet(ids)).map(_.map(_.deserialize))

    def updateNameDescription(fileName: String, name: String, description: String): Future[NFTDraft] = {
      val draft = tryGet(fileName)
      for {
        draft <- draft
        _ <- update(draft.copy(name = Option(name), description = Option(description)).serialize())
      } yield draft.copy(name = Option(name), description = Option(description))
    }

    def updateProperties(fileName: String, properties: Seq[BaseNFTProperty]): Future[NFTDraft] = {
      val draft = tryGet(fileName)
      for {
        nftDraft <- draft
        _ <- update(nftDraft.copy(properties = Option(properties)).serialize())
      } yield nftDraft.copy(properties = Option(properties))
    }

    def updateHashTags(fileName: String, hashTags: Seq[String]): Future[Unit] = {
      val draft = tryGet(fileName)
      for {
        draft <- draft
        _ <- update(draft.copy(hashTags = Option(hashTags)).serialize())
      } yield ()
    }

    def deleteById(fileName: String): Future[Int] = delete(fileName)
  }
}