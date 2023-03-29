package models.masterTransaction

import models.common.Collection._
import models.master.Collection
import models.traits.{Entity, GenericDaoImpl, Logging, ModelTable}
import play.api.Logger
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.json.Json
import slick.jdbc.H2Profile.api._

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

case class CollectionDraft(id: String, creatorId: String, name: String, description: String, socialProfiles: Seq[SocialProfile], category: String, nsfw: Boolean, properties: Seq[Property], profileFileName: Option[String], coverFileName: Option[String], royalty: BigDecimal, createdBy: Option[String] = None, createdOnMillisEpoch: Option[Long] = None, updatedBy: Option[String] = None, updatedOnMillisEpoch: Option[Long] = None) extends Logging {

  def getWebsite: Option[String] = this.socialProfiles.find(_.name == constants.Collection.SocialProfile.WEBSITE).map(_.url)

  def getTwitter: Option[String] = this.socialProfiles.find(_.name == constants.Collection.SocialProfile.TWITTER).map(_.url)

  def getInstagram: Option[String] = this.socialProfiles.find(_.name == constants.Collection.SocialProfile.INSTAGRAM).map(_.url)

  def toCollection(public: Boolean = false): Collection = Collection(id = id, creatorId = creatorId, classificationId = None, name = name, description = description, socialProfiles = socialProfiles, category = category, nsfw = nsfw, properties = Option(this.properties), profileFileName = this.profileFileName, coverFileName = this.coverFileName, public = public, royalty = royalty)

  def getProfileFileURL: Option[String] = this.profileFileName.map(x => constants.CommonConfig.AmazonS3.s3BucketURL + utilities.Collection.getOthersFileAwsKey(collectionId = this.id, fileName = x))

  def getCoverFileURL: Option[String] = this.coverFileName.map(x => constants.CommonConfig.AmazonS3.s3BucketURL + utilities.Collection.getOthersFileAwsKey(collectionId = this.id, fileName = x))

  def serialize(): CollectionDrafts.CollectionDraftSerialized = CollectionDrafts.CollectionDraftSerialized(
    id = this.id,
    creatorId = creatorId,
    name = this.name,
    description = this.description,
    socialProfiles = Json.toJson(this.socialProfiles).toString(),
    category = this.category,
    nsfw = this.nsfw,
    properties = Json.toJson(this.properties).toString(),
    profileFileName = this.profileFileName,
    coverFileName = this.coverFileName,
    royalty = this.royalty,
    createdBy = this.createdBy,
    createdOnMillisEpoch = this.createdOnMillisEpoch,
    updatedBy = this.updatedBy,
    updatedOnMillisEpoch = this.updatedOnMillisEpoch)
}

object CollectionDrafts {

  implicit val module: String = constants.Module.MASTER_TRANSACTION_COLLECTION_DRAFT

  implicit val logger: Logger = Logger(this.getClass)

  case class CollectionDraftSerialized(id: String, creatorId: String, name: String, description: String, socialProfiles: String, category: String, nsfw: Boolean, properties: String, profileFileName: Option[String], coverFileName: Option[String], royalty: BigDecimal, createdBy: Option[String], createdOnMillisEpoch: Option[Long], updatedBy: Option[String], updatedOnMillisEpoch: Option[Long]) extends Entity[String] {
    def deserialize: CollectionDraft = CollectionDraft(id = id, creatorId = creatorId, name = name, description = description, socialProfiles = utilities.JSON.convertJsonStringToObject[Seq[SocialProfile]](socialProfiles), category = category, nsfw = nsfw, properties = utilities.JSON.convertJsonStringToObject[Seq[Property]](properties), profileFileName = profileFileName, coverFileName = coverFileName, royalty = royalty, createdBy = createdBy, createdOnMillisEpoch = createdOnMillisEpoch, updatedBy = updatedBy, updatedOnMillisEpoch = updatedOnMillisEpoch)
  }

  class CollectionDraftTable(tag: Tag) extends Table[CollectionDraftSerialized](tag, "CollectionDraft") with ModelTable[String] {

    def * = (id, creatorId, name, description, socialProfiles, category, nsfw, properties, profileFileName.?, coverFileName.?, royalty, createdBy.?, createdOnMillisEpoch.?, updatedBy.?, updatedOnMillisEpoch.?) <> (CollectionDraftSerialized.tupled, CollectionDraftSerialized.unapply)

    def id = column[String]("id", O.PrimaryKey)

    def creatorId = column[String]("creatorId")

    def name = column[String]("name")

    def description = column[String]("description")

    def socialProfiles = column[String]("socialProfiles")

    def category = column[String]("category")

    def nsfw = column[Boolean]("nsfw")

    def properties = column[String]("properties")

    def profileFileName = column[String]("profileFileName")

    def coverFileName = column[String]("coverFileName")

    def royalty = column[BigDecimal]("royalty")

    def createdBy = column[String]("createdBy")

    def createdOnMillisEpoch = column[Long]("createdOnMillisEpoch")

    def updatedBy = column[String]("updatedBy")

    def updatedOnMillisEpoch = column[Long]("updatedOnMillisEpoch")
  }

  lazy val TableQuery = new TableQuery(tag => new CollectionDraftTable(tag))
}

@Singleton
class CollectionDrafts @Inject()(
                                  protected val databaseConfigProvider: DatabaseConfigProvider
                                )(implicit override val executionContext: ExecutionContext)
  extends GenericDaoImpl[CollectionDrafts.CollectionDraftTable, CollectionDrafts.CollectionDraftSerialized, String](
    databaseConfigProvider,
    CollectionDrafts.TableQuery,
    executionContext,
    CollectionDrafts.module,
    CollectionDrafts.logger
  ) {


  object Service {

    def add(name: String, creatorId: String, description: String, socialProfiles: Seq[SocialProfile], category: String, nsfw: Boolean, royalty: BigDecimal): Future[CollectionDraft] = {
      val id = utilities.IdGenerator.getRandomHexadecimal
      val collection = CollectionDraft(
        id = id,
        creatorId = creatorId,
        name = name,
        description = description,
        socialProfiles = socialProfiles,
        category = category,
        nsfw = nsfw,
        properties = Seq(),
        profileFileName = None,
        coverFileName = None,
        royalty = royalty)
      for {
        _ <- create(collection.serialize())
      } yield collection
    }

    def tryGet(id: String): Future[CollectionDraft] = tryGetById(id).map(_.deserialize)

    def checkOwnerAndUpdate(id: String, creatorId: String, name: String, description: String, socialProfiles: Seq[SocialProfile], category: String, nsfw: Boolean, royalty: BigDecimal): Future[CollectionDraft] = {
      val collectionDraft = tryGet(id)

      def validateAndUpdate(collectionDraft: CollectionDraft) = if (collectionDraft.creatorId == creatorId) {
        updateById(collectionDraft.copy(name = name, description = description, socialProfiles = socialProfiles, category = category, nsfw = nsfw, royalty = royalty).serialize())
      } else constants.Response.NOT_COLLECTION_OWNER.throwFutureBaseException()

      for {
        collectionDraft <- collectionDraft
        _ <- validateAndUpdate(collectionDraft)
      } yield collectionDraft.copy(name = name, description = description, socialProfiles = socialProfiles, category = category, nsfw = nsfw)
    }

    def isOwner(id: String, accountId: String): Future[Boolean] = filterAndExists(x => x.id === id && x.creatorId === accountId)

    def updateProfile(id: String, fileName: String): Future[Unit] = {
      val collectionDraft = tryGet(id)
      for {
        collectionDraft <- collectionDraft
        _ <- updateById(collectionDraft.copy(profileFileName = Option(fileName)).serialize())
      } yield ()
    }

    def updateCover(id: String, fileName: String): Future[Unit] = {
      val collectionDraft = tryGet(id)
      for {
        collectionDraft <- collectionDraft
        _ <- updateById(collectionDraft.copy(coverFileName = Option(fileName)).serialize())
      } yield ()
    }

    def updateProperties(id: String, properties: Seq[Property]): Future[CollectionDraft] = {
      val collectionDraft = tryGet(id)
      for {
        collectionDraft <- collectionDraft
        _ <- updateById(collectionDraft.copy(properties = properties).serialize())
      } yield collectionDraft.copy(properties = properties)
    }

    def totalDrafts(creatorId: String): Future[Int] = filterAndCount(_.creatorId === creatorId)

    def getByCreatorAndPage(creatorId: String, pageNumber: Int): Future[Seq[CollectionDraft]] = filterAndSortWithPagination(offset = (pageNumber - 1) * constants.CommonConfig.Pagination.CollectionsPerPage, limit = constants.CommonConfig.Pagination.CollectionsPerPage)(_.creatorId === creatorId)(_.createdOnMillisEpoch).map(_.map(_.deserialize))

    def delete(id: String): Future[Int] = deleteById(id)

    def checkOwnerAndDelete(id: String, accountId: String): Future[Unit] = {
      val draft = tryGet(id)

      for {
        draft <- draft
        _ <- if (draft.creatorId == accountId) delete(id) else constants.Response.NOT_COLLECTION_OWNER.throwBaseException()
      } yield ()
    }

  }
}