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

case class Collection(id: String, classificationId: Option[String], name: String, description: String, website: String, socialProfiles: Seq[SocialProfile], createdBy: Option[String] = None, createdOn: Option[Timestamp] = None, createdOnTimeZone: Option[String] = None, updatedBy: Option[String] = None, updatedOn: Option[Timestamp] = None, updatedOnTimeZone: Option[String] = None) extends Logged {
  def serialize(): Collections.CollectionSerialized = Collections.CollectionSerialized(
    id = this.id,
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
}

object Collections {

  implicit val module: String = constants.Module.MASTER_COLLECTION

  implicit val logger: Logger = Logger(this.getClass)

  case class CollectionSerialized(id: String, classificationId: Option[String], name: String, description: String, website: String, socialProfiles: String, createdBy: Option[String], createdOn: Option[Timestamp], createdOnTimeZone: Option[String], updatedBy: Option[String], updatedOn: Option[Timestamp], updatedOnTimeZone: Option[String]) extends Entity[String] {
    def deserialize: Collection = Collection(id = id, classificationId = classificationId, name = name, description = description, website = website, socialProfiles = utilities.JSON.convertJsonStringToObject[Seq[SocialProfile]](socialProfiles), createdBy = createdBy, createdOn = createdOn, createdOnTimeZone = createdOnTimeZone, updatedBy = updatedBy, updatedOn = updatedOn, updatedOnTimeZone = updatedOnTimeZone)
  }

  class CollectionTable(tag: Tag) extends Table[CollectionSerialized](tag, "Collection") with ModelTable[String] {

    def * = (id, classificationId.?, name, description, website, socialProfiles, createdBy.?, createdOn.?, createdOnTimeZone.?, updatedBy.?, updatedOn.?, updatedOnTimeZone.?) <> (CollectionSerialized.tupled, CollectionSerialized.unapply)

    def id = column[String]("id", O.PrimaryKey)

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

    def add(name: String, description: String, website: String, socialProfiles: Seq[SocialProfile]): Future[String] = {
      val id = utilities.IdGenerator.getRandomHexadecimal
      val collection = Collection(
        id = id,
        classificationId = None,
        name = name,
        description = description,
        website = website,
        socialProfiles = socialProfiles)
      for {
        _ <- create(collection.serialize())
      } yield id
    }

    def fetchAll(): Future[Seq[Collection]] = getAll.map(_.map(_.deserialize))

    def get(id: String): Future[Option[Collection]] = getById(id).map(_.map(_.deserialize))

    def tryGet(id: String): Future[Collection] = tryGetById(id).map(_.deserialize)

  }
}