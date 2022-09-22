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

case class Collection(id: String, creatorId: String, classificationId: Option[String], name: String, description: String, website: String, socialProfiles: Seq[SocialProfile], createdBy: Option[String] = None, createdOn: Option[Timestamp] = None, createdOnTimeZone: Option[String] = None, updatedBy: Option[String] = None, updatedOn: Option[Timestamp] = None, updatedOnTimeZone: Option[String] = None) extends Logged {
  def serialize(): Collections.CollectionSerialized = Collections.CollectionSerialized(
    id = this.id,
    creatorId = this.creatorId,
    classificationId = this.classificationId,
    name = this.name,
    description = this.description,
    website = this.website,
    socialProfiles = Json.toJson(this.socialProfiles).toString(),
    createdBy = this.createdBy,
    createdOn = this.createdOn,
    createdOnTimeZone = this.createdOnTimeZone,
    updatedBy = this.updatedBy,
    updatedOn = this.updatedOn,
    updatedOnTimeZone = this.updatedOnTimeZone)

  def getTwitter: Option[String] = this.socialProfiles.find(_.name == constants.Collection.SocialProfile.TWITTER).map(_.url)

  def getInstagram: Option[String] = this.socialProfiles.find(_.name == constants.Collection.SocialProfile.INSTAGRAM).map(_.url)
}

object Collections {

  implicit val module: String = constants.Module.MASTER_COLLECTION

  implicit val logger: Logger = Logger(this.getClass)

  case class CollectionSerialized(id: String, creatorId: String, classificationId: Option[String], name: String, description: String, website: String, socialProfiles: String, createdBy: Option[String], createdOn: Option[Timestamp], createdOnTimeZone: Option[String], updatedBy: Option[String], updatedOn: Option[Timestamp], updatedOnTimeZone: Option[String]) extends Entity[String] {
    def deserialize: Collection = Collection(id = id, creatorId = creatorId, classificationId = classificationId, name = name, description = description, website = website, socialProfiles = utilities.JSON.convertJsonStringToObject[Seq[SocialProfile]](socialProfiles), createdBy = createdBy, createdOn = createdOn, createdOnTimeZone = createdOnTimeZone, updatedBy = updatedBy, updatedOn = updatedOn, updatedOnTimeZone = updatedOnTimeZone)
  }

  class CollectionTable(tag: Tag) extends Table[CollectionSerialized](tag, "Collection") with ModelTable[String] {

    def * = (id, creatorId, classificationId.?, name, description, website, socialProfiles, createdBy.?, createdOn.?, createdOnTimeZone.?, updatedBy.?, updatedOn.?, updatedOnTimeZone.?) <> (CollectionSerialized.tupled, CollectionSerialized.unapply)

    def id = column[String]("id", O.PrimaryKey)

    def creatorId = column[String]("creatorId")

    def classificationId = column[String]("classificationId")

    def name = column[String]("name")

    def description = column[String]("description")

    def website = column[String]("website")

    def socialProfiles = column[String]("socialProfiles")

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

    def add(name: String, creatorId: String, description: String, website: String, socialProfiles: Seq[SocialProfile]): Future[String] = {
      val id = utilities.IdGenerator.getRandomHexadecimal
      val collection = Collection(
        id = id,
        creatorId = creatorId,
        classificationId = None,
        name = name,
        description = description,
        website = website,
        socialProfiles = socialProfiles)
      for {
        _ <- create(collection.serialize())
      } yield id
    }

    def insertOrUpdate(id: String, creatorId: String, name: String, description: String, website: String, socialProfiles: Seq[SocialProfile]): Future[String] = {
      val collection = Collection(
        id = id,
        creatorId = creatorId,
        classificationId = None,
        name = name,
        description = description,
        website = website,
        socialProfiles = socialProfiles)
      for {
        _ <- upsert(collection.serialize())
      } yield id
    }

    def fetchAll(): Future[Seq[Collection]] = getAll.map(_.map(_.deserialize))

    def get(id: String): Future[Option[Collection]] = getById(id).map(_.map(_.deserialize))

    def getByName(name: String): Future[Option[Collection]] = filter(_.name === name).map(_.map(_.deserialize).headOption)

    def tryGet(id: String): Future[Collection] = tryGetById(id).map(_.deserialize)

    def tryGetByName(name: String): Future[Collection] = filterHead(_.name === name).map(_.deserialize)

    def getByPageNumber(pageNumber: Int): Future[Seq[Collection]] = getAllByPageNumber(offset = (pageNumber - 1) * constants.CommonConfig.Pagination.CollectionsPerPage, limit = constants.CommonConfig.Pagination.CollectionsPerPage)(_.createdOn).map(_.map(_.deserialize))

    def deleteById(id: String): Future[Int] = delete(id)

    def deleteById(ids: Seq[String]): Future[Int] = filterAndDelete(_.id.inSet(ids))

    def getCollectionsByPage(collectionIds: Seq[String], pageNumber: Int): Future[Seq[Collection]] = filterAndSortWithPagination(offset = (pageNumber - 1) * constants.CommonConfig.Pagination.CollectionsPerPage, limit = constants.CommonConfig.Pagination.CollectionsPerPage)(_.id.inSet(collectionIds))(_.createdOn).map(_.map(_.deserialize))

    def isCreator(accountId: String): Future[Boolean] = filter(_.creatorId === accountId).map(_.nonEmpty)

    def updateAccountId(id: String, description: String, website: String, socialProfiles: Seq[SocialProfile], accountId: String): Future[Unit] = {
      val collection = tryGet(id)
      for {
        collection <- collection
        _ <- update(collection.copy(creatorId = accountId, description = description, website = website, socialProfiles = socialProfiles).serialize())
      } yield ()
    }

  }
}