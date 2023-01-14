package models.master

import models.Trait.{Entity, GenericDaoImpl, Logging, ModelTable}
import models.common.Collection._
import play.api.Logger
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.json.Json
import slick.jdbc.H2Profile.api._

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

case class Collection(id: String, creatorId: String, classificationId: Option[String], name: String, description: String, socialProfiles: Seq[SocialProfile], category: String, nsfw: Boolean, properties: Option[Seq[Property]], profileFileName: Option[String], coverFileName: Option[String], public: Boolean, createdBy: Option[String] = None, createdOnMillisEpoch: Option[Long] = None, updatedBy: Option[String] = None, updatedOnMillisEpoch: Option[Long] = None) extends Logging {
  def serialize(): Collections.CollectionSerialized = Collections.CollectionSerialized(
    id = this.id,
    creatorId = this.creatorId,
    classificationId = this.classificationId,
    name = this.name,
    description = this.description,
    socialProfiles = Json.toJson(this.socialProfiles).toString(),
    category = this.category,
    nsfw = this.nsfw,
    properties = this.properties.map(Json.toJson(_).toString()),
    profileFileName = this.profileFileName,
    coverFileName = this.coverFileName,
    public = this.public,
    createdBy = this.createdBy,
    createdOnMillisEpoch = this.createdOnMillisEpoch,
    updatedBy = this.updatedBy,
    updatedOnMillisEpoch = this.updatedOnMillisEpoch)

  def getWebsite: Option[String] = this.socialProfiles.find(_.name == constants.Collection.SocialProfile.WEBSITE).map(_.url)

  def getTwitter: Option[String] = this.socialProfiles.find(_.name == constants.Collection.SocialProfile.TWITTER).map("https://www.twitter.com/" + _.url)

  def getInstagram: Option[String] = this.socialProfiles.find(_.name == constants.Collection.SocialProfile.INSTAGRAM).map("https://www.instagram.com/" + _.url)

  def getProfileFileURL: Option[String] = this.profileFileName.map(x => constants.CommonConfig.AmazonS3.s3BucketURL + utilities.Collection.getOthersFileAwsKey(collectionId = this.id, fileName = x))

  def getCoverFileURL: Option[String] = this.coverFileName.map(x => constants.CommonConfig.AmazonS3.s3BucketURL + utilities.Collection.getOthersFileAwsKey(collectionId = this.id, fileName = x))

}

object Collections {

  implicit val module: String = constants.Module.MASTER_COLLECTION

  implicit val logger: Logger = Logger(this.getClass)

  case class CollectionSerialized(id: String, creatorId: String, classificationId: Option[String], name: String, description: String, socialProfiles: String, category: String, nsfw: Boolean, properties: Option[String], profileFileName: Option[String], coverFileName: Option[String], public: Boolean, createdBy: Option[String], createdOnMillisEpoch: Option[Long], updatedBy: Option[String], updatedOnMillisEpoch: Option[Long]) extends Entity[String] {
    def deserialize: Collection = Collection(id = id, creatorId = creatorId, classificationId = classificationId, name = name, description = description, socialProfiles = utilities.JSON.convertJsonStringToObject[Seq[SocialProfile]](socialProfiles), category = category, nsfw = nsfw, properties = properties.map(utilities.JSON.convertJsonStringToObject[Seq[Property]](_)), profileFileName = profileFileName, coverFileName = coverFileName, public = public, createdBy = createdBy, createdOnMillisEpoch = createdOnMillisEpoch, updatedBy = updatedBy, updatedOnMillisEpoch = updatedOnMillisEpoch)
  }

  class CollectionTable(tag: Tag) extends Table[CollectionSerialized](tag, "Collection") with ModelTable[String] {

    def * = (id, creatorId, classificationId.?, name, description, socialProfiles, category, nsfw, properties.?, profileFileName.?, coverFileName.?, public, createdBy.?, createdOnMillisEpoch.?, updatedBy.?, updatedOnMillisEpoch.?) <> (CollectionSerialized.tupled, CollectionSerialized.unapply)

    def id = column[String]("id", O.PrimaryKey)

    def creatorId = column[String]("creatorId")

    def classificationId = column[String]("classificationId")

    def name = column[String]("name")

    def description = column[String]("description")

    def socialProfiles = column[String]("socialProfiles")

    def category = column[String]("category")

    def nsfw = column[Boolean]("nsfw")

    def properties = column[String]("properties")

    def profileFileName = column[String]("profileFileName")

    def coverFileName = column[String]("coverFileName")

    def public = column[Boolean]("public")

    def createdBy = column[String]("createdBy")

    def createdOnMillisEpoch = column[Long]("createdOnMillisEpoch")

    def updatedBy = column[String]("updatedBy")

    def updatedOnMillisEpoch = column[Long]("updatedOnMillisEpoch")


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

    def add(collection: Collection): Future[String] = create(collection.serialize())

    def update(collection: Collection): Future[Unit] = updateById(collection.serialize())

    def fetchAll(): Future[Seq[Collection]] = getAll.map(_.map(_.deserialize))

    def fetchAllPublic(): Future[Seq[Collection]] = filter(_.public).map(_.map(_.deserialize))

    def get(id: String): Future[Option[Collection]] = getById(id).map(_.map(_.deserialize))

    def tryGet(id: String): Future[Collection] = tryGetById(id).map(_.deserialize)

    def getByPageNumber(category: String, pageNumber: Int): Future[Seq[Collection]] = filterAndSortWithPagination(offset = (pageNumber - 1) * constants.CommonConfig.Pagination.CollectionsPerPage, limit = constants.CommonConfig.Pagination.CollectionsPerPage)(x => x.category === category && x.public)(_.createdOnMillisEpoch).map(_.map(_.deserialize))

    def getCollectionsByPage(collectionIds: Seq[String], pageNumber: Int): Future[Seq[Collection]] = filterAndSortWithPagination(offset = (pageNumber - 1) * constants.CommonConfig.Pagination.CollectionsPerPage, limit = constants.CommonConfig.Pagination.CollectionsPerPage)(_.id.inSet(collectionIds))(_.createdOnMillisEpoch).map(_.map(_.deserialize))

    def getCollections(collectionIds: Seq[String]): Future[Seq[Collection]] = filter(_.id.inSet(collectionIds)).map(_.map(_.deserialize))

    def totalCreated(creatorId: String): Future[Int] = filterAndCount(_.creatorId === creatorId)

    def total(category: String): Future[Int] = filterAndCount(_.category === category)

    def getByCreatorAndPage(creatorId: String, pageNumber: Int): Future[Seq[Collection]] = filterAndSortWithPagination(offset = (pageNumber - 1) * constants.CommonConfig.Pagination.CollectionsPerPage, limit = constants.CommonConfig.Pagination.CollectionsPerPage)(_.creatorId === creatorId)(_.createdOnMillisEpoch).map(_.map(_.deserialize))

    def getByCreator(creatorId: String): Future[Seq[(String, String)]] = filterAndSort(_.creatorId === creatorId)(_.name).map(_.map(x => x.id -> x.name))

    def isOwner(id: String, accountId: String): Future[Boolean] = filterAndExists(x => x.id === id && x.creatorId === accountId)

    def updateProfile(id: String, fileName: String): Future[Unit] = {
      val collection = tryGet(id)
      for {
        collection <- collection
        _ <- updateById(collection.copy(profileFileName = Option(fileName)).serialize())
      } yield ()
    }

    def updateCover(id: String, fileName: String): Future[Unit] = {
      val collection = tryGet(id)
      for {
        collection <- collection
        _ <- updateById(collection.copy(coverFileName = Option(fileName)).serialize())
      } yield ()
    }

    def countCreated(accountId: String): Future[Int] = filterAndCount(_.creatorId === accountId)

    def getCollectionNames(ids: Seq[String]): Future[Map[String, String]] = filter(_.id.inSet(ids)).map(_.map(x => x.id -> x.name).toMap)

    def delete(ids: Seq[String]): Future[Int] = filterAndDelete(_.id.inSet(ids))
  }
}