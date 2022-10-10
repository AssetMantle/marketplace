package models.master

import models.Trait.{Entity2, GenericDaoImpl2, Logging, ModelTable2}
import play.api.Logger
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.H2Profile.api._

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

case class CollectionProperty(id: String, propertyName: String, propertyType: String, required: Boolean, mutable: Boolean, fixedValue: Option[String], hideValue: Boolean, createdBy: Option[String] = None, createdOnMillisEpoch: Option[Long] = None, updatedOnMillisEpoch: Option[Long] = None, updatedBy: Option[String] = None) extends Logging {
  def serialize(): CollectionProperties.CollectionPropertySerialized = CollectionProperties.CollectionPropertySerialized(
    id = this.id,
    propertyName = this.propertyName,
    propertyType = this.propertyType,
    required = this.required,
    mutable = this.mutable,
    fixedValue = this.fixedValue,
    hideValue = this.hideValue,
    createdBy = this.createdBy,
    createdOnMillisEpoch = this.createdOnMillisEpoch,
    updatedBy = this.updatedBy,
    updatedOnMillisEpoch = this.updatedOnMillisEpoch)
}

object CollectionProperties {

  implicit val module: String = constants.Module.MASTER_COLLECTION_FILE

  implicit val logger: Logger = Logger(this.getClass)

  case class CollectionPropertySerialized(id: String, propertyName: String, propertyType: String, required: Boolean, mutable: Boolean, fixedValue: Option[String], hideValue: Boolean, createdBy: Option[String], createdOnMillisEpoch: Option[Long], updatedBy: Option[String], updatedOnMillisEpoch: Option[Long]) extends Entity2[String, String] {
    def deserialize: CollectionProperty = CollectionProperty(id = id, propertyName = propertyName, propertyType = propertyType, required = required, mutable = mutable, fixedValue = fixedValue, hideValue = hideValue, createdBy = createdBy, createdOnMillisEpoch = createdOnMillisEpoch, updatedBy = updatedBy, updatedOnMillisEpoch = updatedOnMillisEpoch)

    def id1: String = id

    def id2: String = propertyName
  }

  class CollectionPropertyTable(tag: Tag) extends Table[CollectionPropertySerialized](tag, "CollectionProperty") with ModelTable2[String, String] {

    def * = (id, propertyName, propertyType, required, mutable, fixedValue.?, hideValue, createdBy.?, createdOnMillisEpoch.?, updatedBy.?, updatedOnMillisEpoch.?) <> (CollectionPropertySerialized.tupled, CollectionPropertySerialized.unapply)

    def id = column[String]("id", O.PrimaryKey)

    def propertyName = column[String]("propertyName", O.PrimaryKey)

    def propertyType = column[String]("propertyType")

    def required = column[Boolean]("required")

    def mutable = column[Boolean]("mutable")

    def fixedValue = column[String]("fixedValue")

    def hideValue = column[Boolean]("hideValue")

    def createdBy = column[String]("createdBy")

    def createdOnMillisEpoch = column[Long]("createdOnMillisEpoch")

    def updatedBy = column[String]("updatedBy")

    def updatedOnMillisEpoch = column[Long]("updatedOnMillisEpoch")

    def id1 = id

    def id2 = propertyName
  }

  lazy val TableQuery = new TableQuery(tag => new CollectionPropertyTable(tag))
}

@Singleton
class CollectionProperties @Inject()(
                                      protected val databaseConfigProvider: DatabaseConfigProvider
                                    )(implicit override val executionContext: ExecutionContext)
  extends GenericDaoImpl2[CollectionProperties.CollectionPropertyTable, CollectionProperties.CollectionPropertySerialized, String, String](
    databaseConfigProvider,
    CollectionProperties.TableQuery,
    executionContext,
    CollectionProperties.module,
    CollectionProperties.logger
  ) {


  object Service {

    def add(id: String, propertyName: String, propertyType: String, required: Boolean, mutable: Boolean, fixedValue: Option[String], hideValue: Boolean): Future[Unit] = {
      create(CollectionProperty(
        id = id,
        propertyName = propertyName,
        propertyType = propertyType,
        required = required,
        mutable = mutable,
        fixedValue = fixedValue,
        hideValue = hideValue,
      ).serialize())
    }

    def addMultiple(collectionProperties: Seq[CollectionProperty]): Future[Unit] = create(collectionProperties.map(_.serialize()))

    def insertOrUpdate(id: String, propertyName: String, propertyType: String, required: Boolean, mutable: Boolean, fixedValue: Option[String], hideValue: Boolean): Future[Unit] = {
      upsert(CollectionProperty(
        id = id,
        propertyName = propertyName,
        propertyType = propertyType,
        required = required,
        mutable = mutable,
        fixedValue = fixedValue,
        hideValue = hideValue,
      ).serialize())
    }

    def get(id: String): Future[Seq[CollectionProperty]] = filter(_.id === id).map(_.map(_.deserialize))

    def countProperties(id: String): Future[Int] = filterAndCount(_.id === id)

  }
}