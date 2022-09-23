package models.master

import models.Trait.{Entity2, GenericDaoImpl2, Logged, ModelTable2}
import play.api.Logger
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.H2Profile.api._

import java.sql.Timestamp
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

case class CollectionFile(id: String, documentType: String, fileName: String, file: Array[Byte], createdBy: Option[String] = None, createdOn: Option[Timestamp] = None, createdOnTimeZone: Option[String] = None, updatedBy: Option[String] = None, updatedOn: Option[Timestamp] = None, updatedOnTimeZone: Option[String] = None) extends Logged {
  def serialize(): CollectionFiles.CollectionFileSerialized = CollectionFiles.CollectionFileSerialized(
    id = this.id,
    documentType = this.documentType,
    fileName = this.fileName,
    file = this.file,
    createdBy = this.createdBy,
    createdOn = this.createdOn,
    createdOnTimeZone = this.createdOnTimeZone,
    updatedBy = this.updatedBy,
    updatedOn = this.updatedOn,
    updatedOnTimeZone = this.updatedOnTimeZone)
}

object CollectionFiles {

  implicit val module: String = constants.Module.MASTER_COLLECTION_FILE

  implicit val logger: Logger = Logger(this.getClass)

  case class CollectionFileSerialized(id: String, documentType: String, fileName: String, file: Array[Byte], createdBy: Option[String], createdOn: Option[Timestamp], createdOnTimeZone: Option[String], updatedBy: Option[String], updatedOn: Option[Timestamp], updatedOnTimeZone: Option[String]) extends Entity2[String, String] {
    def deserialize: CollectionFile = CollectionFile(id = id, documentType = documentType, fileName = fileName, file = file, createdBy = createdBy, createdOn = createdOn, createdOnTimeZone = createdOnTimeZone, updatedBy = updatedBy, updatedOn = updatedOn, updatedOnTimeZone = updatedOnTimeZone)

    def id1: String = id

    def id2: String = documentType
  }

  class CollectionFileTable(tag: Tag) extends Table[CollectionFileSerialized](tag, "CollectionFile") with ModelTable2[String, String] {

    def * = (id, documentType, fileName, file, createdBy.?, createdOn.?, createdOnTimeZone.?, updatedBy.?, updatedOn.?, updatedOnTimeZone.?) <> (CollectionFileSerialized.tupled, CollectionFileSerialized.unapply)

    def id = column[String]("id", O.PrimaryKey)

    def documentType = column[String]("documentType", O.PrimaryKey)

    def fileName = column[String]("fileName")

    def file = column[Array[Byte]]("file")

    def createdBy = column[String]("createdBy")

    def createdOn = column[Timestamp]("createdOn")

    def createdOnTimeZone = column[String]("createdOnTimeZone")

    def updatedBy = column[String]("updatedBy")

    def updatedOn = column[Timestamp]("updatedOn")

    def updatedOnTimeZone = column[String]("updatedOnTimeZone")

    def id1 = id

    def id2 = documentType
  }

  lazy val TableQuery = new TableQuery(tag => new CollectionFileTable(tag))
}

@Singleton
class CollectionFiles @Inject()(
                                 protected val databaseConfigProvider: DatabaseConfigProvider
                               )(implicit override val executionContext: ExecutionContext)
  extends GenericDaoImpl2[CollectionFiles.CollectionFileTable, CollectionFiles.CollectionFileSerialized, String, String](
    databaseConfigProvider,
    CollectionFiles.TableQuery,
    executionContext,
    CollectionFiles.module,
    CollectionFiles.logger
  ) {


  object Service {

    def add(id: String, documentType: String, fileName: String): Future[Unit] = {
      create(CollectionFile(
        id = id,
        documentType = documentType,
        fileName = fileName,
        file = Array()
      ).serialize())
    }

    def insertOrUpdate(id: String, documentType: String, fileName: String, file: Array[Byte]): Future[Unit] = {
      upsert(CollectionFile(
        id = id,
        documentType = documentType,
        fileName = fileName,
        file = file
      ).serialize())
    }

    def get(id: String, documentType: String): Future[Option[CollectionFile]] = getById(id1 = id, id2 = documentType).map(_.map(_.deserialize))

    def get(ids: Seq[String]): Future[Seq[CollectionFile]] = filter(_.id.inSet(ids)).map(_.map(_.deserialize))

    def fetchAll(): Future[Seq[CollectionFile]] = getAll.map(_.map(_.deserialize))

    def deleteByCollectionId(id: String): Future[Int] = deleteMultipleById1(id)

    def deleteCollections(collectionIds: Seq[String]): Future[Int] = filterAndDelete(_.id.inSet(collectionIds))

  }
}