package models.master

import models.Trait.{Entity, GenericDaoImpl, Logged, ModelTable}
import play.api.Logger
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.json.{Json, Reads, Writes}
import slick.jdbc.H2Profile.api._

import java.sql.Timestamp
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

case class SocialProfile(name: String, url: String)

object SocialProfile {
  implicit val writes: Writes[SocialProfile] = Json.writes[SocialProfile]

  implicit val reads: Reads[SocialProfile] = Json.reads[SocialProfile]
}

case class Collection(id: String, creatorId: String, classificationId: Option[String], name: String, description: String, website: String, socialProfiles: Seq[SocialProfile], category: String, nsfw: Boolean, createdBy: Option[String] = None, createdOn: Option[Timestamp] = None, createdOnTimeZone: Option[String] = None, updatedBy: Option[String] = None, updatedOn: Option[Timestamp] = None, updatedOnTimeZone: Option[String] = None) extends Logged {
  def serialize(): Collections.CollectionSerialized = Collections.CollectionSerialized(
    id = this.id,
    creatorId = this.creatorId,
    classificationId = this.classificationId,
    name = this.name,
    description = this.description,
    website = this.website,
    socialProfiles = Json.toJson(this.socialProfiles).toString(),
    category = this.category,
    nsfw = this.nsfw,
    createdBy = this.createdBy,
    createdOn = this.createdOn,
    createdOnTimeZone = this.createdOnTimeZone,
    updatedBy = this.updatedBy,
    updatedOn = this.updatedOn,
    updatedOnTimeZone = this.updatedOnTimeZone)

  def getWebsite: Option[String] = this.socialProfiles.find(_.name == constants.Collection.SocialProfile.WEBSITE).map(_.url)

  def getTwitter: Option[String] = this.socialProfiles.find(_.name == constants.Collection.SocialProfile.TWITTER).map(_.url)

  def getInstagram: Option[String] = this.socialProfiles.find(_.name == constants.Collection.SocialProfile.INSTAGRAM).map(_.url)
}

object Collections {

  implicit val module: String = constants.Module.MASTER_COLLECTION

  implicit val logger: Logger = Logger(this.getClass)

  case class CollectionSerialized(id: String, creatorId: String, classificationId: Option[String], name: String, description: String, website: String, socialProfiles: String, category: String, nsfw: Boolean, createdBy: Option[String], createdOn: Option[Timestamp], createdOnTimeZone: Option[String], updatedBy: Option[String], updatedOn: Option[Timestamp], updatedOnTimeZone: Option[String]) extends Entity[String] {
    def deserialize: Collection = Collection(id = id, creatorId = creatorId, classificationId = classificationId, name = name, description = description, website = website, socialProfiles = utilities.JSON.convertJsonStringToObject[Seq[SocialProfile]](socialProfiles), category = category, nsfw = nsfw, createdBy = createdBy, createdOn = createdOn, createdOnTimeZone = createdOnTimeZone, updatedBy = updatedBy, updatedOn = updatedOn, updatedOnTimeZone = updatedOnTimeZone)
  }

  class CollectionTable(tag: Tag) extends Table[CollectionSerialized](tag, "Collection") with ModelTable[String] {

    def * = (id, creatorId, classificationId.?, name, description, website, socialProfiles, category, nsfw, createdBy.?, createdOn.?, createdOnTimeZone.?, updatedBy.?, updatedOn.?, updatedOnTimeZone.?) <> (CollectionSerialized.tupled, CollectionSerialized.unapply)

    def id = column[String]("id", O.PrimaryKey)

    def creatorId = column[String]("creatorId")

    def classificationId = column[String]("classificationId")

    def name = column[String]("name")

    def description = column[String]("description")

    def website = column[String]("website")

    def socialProfiles = column[String]("socialProfiles")

    def category = column[String]("category")

    def nsfw = column[Boolean]("nsfw")

    def createdBy = column[String]("createdBy")

    def createdOn = column[Timestamp]("createdOn")

    def createdOnTimeZone = column[String]("createdOnTimeZone")

    def updatedBy = column[String]("updatedBy")

    def updatedOn = column[Timestamp]("updatedOn")

    def updatedOnTimeZone = column[String]("updatedOnTimeZone")

  }

  lazy val TableQuery = new TableQuery(tag => new CollectionTable(tag))
}

@Singleton
class Collections @Inject()(
                             protected val databaseConfigProvider: DatabaseConfigProvider
                           )(implicit override val executionContext: ExecutionContext)
  extends GenericDaoImpl[Collections.CollectionTable, Collections.CollectionSerialized, String](
    databaseConfigProvider,
    Collections.TableQuery,
    executionContext,
    Collections.module,
    Collections.logger
  ) {


  object Service {

    def add(name: String, creatorId: String, description: String, website: String, socialProfiles: Seq[SocialProfile], category: String, nsfw: Boolean): Future[String] = {
      val id = utilities.IdGenerator.getRandomHexadecimal
      val collection = Collection(
        id = id,
        creatorId = creatorId,
        classificationId = None,
        name = name,
        description = description,
        website = website,
        socialProfiles = socialProfiles,
        category = category,
        nsfw = nsfw)
      for {
        _ <- create(collection.serialize())
      } yield id
    }

    def insertOrUpdate(id: String, creatorId: String, name: String, description: String, website: String, socialProfiles: Seq[SocialProfile], category: String, nsfw: Boolean): Future[String] = {
      val collection = Collection(
        id = id,
        creatorId = creatorId,
        classificationId = None,
        name = name,
        description = description,
        website = website,
        socialProfiles = socialProfiles,
        category = category,
        nsfw = nsfw)
      for {
        _ <- upsert(collection.serialize())
      } yield id
    }

    def updateById(collection: Collection): Future[Unit] = update(collection.serialize())

    def fetchAll(): Future[Seq[Collection]] = getAll.map(_.map(_.deserialize))

    def get(id: String): Future[Option[Collection]] = getById(id).map(_.map(_.deserialize))

    def getByName(name: String): Future[Option[Collection]] = filter(_.name === name).map(_.map(_.deserialize).headOption)

    def tryGet(id: String): Future[Collection] = tryGetById(id).map(_.deserialize)

    def tryGetByName(name: String): Future[Collection] = filterHead(_.name === name).map(_.deserialize)

    def getByPageNumber(category: String, pageNumber: Int): Future[Seq[Collection]] = filterAndSortWithPagination(offset = (pageNumber - 1) * constants.CommonConfig.Pagination.CollectionsPerPage, limit = constants.CommonConfig.Pagination.CollectionsPerPage)(_.category === category)(_.createdOn).map(_.map(_.deserialize))

    def deleteById(id: String): Future[Int] = delete(id)

    def deleteById(ids: Seq[String]): Future[Int] = filterAndDelete(_.id.inSet(ids))

    def getCollectionsByPage(collectionIds: Seq[String], pageNumber: Int): Future[Seq[Collection]] = filterAndSortWithPagination(offset = (pageNumber - 1) * constants.CommonConfig.Pagination.CollectionsPerPage, limit = constants.CommonConfig.Pagination.CollectionsPerPage)(_.id.inSet(collectionIds))(_.createdOn).map(_.map(_.deserialize))

    def isCreator(accountId: String): Future[Boolean] = filterAndExists(_.creatorId === accountId)

    def totalCreated(creatorId: String): Future[Int] = filterAndCount(_.creatorId === creatorId)

    def total(category: String): Future[Int] = filterAndCount(_.category === category)

    def getByCreatorAndPage(creatorId: String, pageNumber: Int): Future[Seq[Collection]] = filterAndSortWithPagination(offset = (pageNumber - 1) * constants.CommonConfig.Pagination.CollectionsPerPage, limit = constants.CommonConfig.Pagination.CollectionsPerPage)(_.creatorId === creatorId)(_.createdOn).map(_.map(_.deserialize))

    def isOwner(id: String, accountId: String): Future[Boolean] = filterAndExists(x => x.id === id && x.creatorId === accountId)

    def checkOwnerAndUpdate(id: String, ownerId: String, name: String, description: String, socialProfiles: Seq[SocialProfile], category: String, nsfw: Boolean): Future[Unit] = {
      val collection = tryGet(id)

      def validateAndUpdate(collection: Collection) = if (collection.classificationId.isEmpty) {
        if (collection.creatorId == ownerId) {
          update(collection.copy(name = name, description = description, socialProfiles = socialProfiles, category = category, nsfw = nsfw).serialize())
        } else constants.Response.NOT_COLLECTION_OWNER.throwFutureBaseException()
      } else constants.Response.CLASSIFICATION_ALREADY_DEFINED.throwFutureBaseException()

      for {
        collection <- collection
        _ <- validateAndUpdate(collection)
      } yield ()
    }

  }
}