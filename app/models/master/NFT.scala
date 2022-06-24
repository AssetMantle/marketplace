package models.master

import models.Trait.{Entity, GenericDaoImpl, Logged, ModelTable}
import play.api.Logger
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.json.{Json, Reads, Writes}
import slick.jdbc.H2Profile.api._

import java.sql.Timestamp
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

case class Property(name: String, `type`: String, `value`: String)

object Property {
  implicit val propertyWrites: Writes[Property] = Json.writes[Property]

  implicit val propertyReads: Reads[Property] = Json.reads[Property]
}

case class NFT(fileHash: String, collectionId: String, name: String, description: String, properties: Seq[Property], edition: Option[String], createdBy: Option[String] = None, createdOn: Option[Timestamp] = None, createdOnTimeZone: Option[String] = None, updatedBy: Option[String] = None, updatedOn: Option[Timestamp] = None, updatedOnTimeZone: Option[String] = None) extends Logged {

  def serialize(): NFTs.NFTSerialized = NFTs.NFTSerialized(
    fileHash = this.fileHash,
    collectionId = collectionId,
    name = this.name,
    description = this.description,
    properties = Json.toJson(this.properties).toString(),
    edition = this.edition,
    createdBy = this.createdBy,
    createdOn = this.createdOn,
    createdOnTimeZone = this.createdOnTimeZone,
    updatedBy = this.updatedBy,
    updatedOn = this.updatedOn,
    updatedOnTimeZone = this.updatedOnTimeZone)
}

object NFTs {

  implicit val module: String = constants.Module.MASTER_NFT

  implicit val logger: Logger = Logger(this.getClass)

  case class NFTSerialized(fileHash: String, collectionId: String, name: String, description: String, properties: String, edition: Option[String], createdBy: Option[String], createdOn: Option[Timestamp], createdOnTimeZone: Option[String], updatedBy: Option[String], updatedOn: Option[Timestamp], updatedOnTimeZone: Option[String]) extends Entity[String] {
    def deserialize: NFT = NFT(fileHash = fileHash, collectionId = collectionId, name = name, description = description, properties = utilities.JSON.convertJsonStringToObject[Seq[Property]](properties), edition = edition, createdBy = createdBy, createdOn = createdOn, createdOnTimeZone = createdOnTimeZone, updatedBy = updatedBy, updatedOn = updatedOn, updatedOnTimeZone = updatedOnTimeZone)

    def id: String = fileHash
  }

  class NFTTable(tag: Tag) extends Table[NFTSerialized](tag, "NFT") with ModelTable[String] {

    def * = (id, collectionId, name, description, properties, edition.?, createdBy.?, createdOn.?, createdOnTimeZone.?, updatedBy.?, updatedOn.?, updatedOnTimeZone.?) <> (NFTSerialized.tupled, NFTSerialized.unapply)

    def fileHash = column[String]("fileHash", O.PrimaryKey)

    def collectionId = column[String]("collectionId")

    def name = column[String]("name")

    def description = column[String]("description")

    def properties = column[String]("description")

    def edition = column[String]("edition")

    def createdBy = column[String]("createdBy")

    def createdOn = column[Timestamp]("createdOn")

    def createdOnTimeZone = column[String]("createdOnTimeZone")

    def updatedBy = column[String]("updatedBy")

    def updatedOn = column[Timestamp]("updatedOn")

    def updatedOnTimeZone = column[String]("updatedOnTimeZone")

    override def id = fileHash
  }

  lazy val TableQuery = new TableQuery(tag => new NFTTable(tag))
}

@Singleton
class NFTs @Inject()(
                      protected val databaseConfigProvider: DatabaseConfigProvider
                    )(implicit override val executionContext: ExecutionContext)
  extends GenericDaoImpl[NFTs.NFTTable, NFTs.NFTSerialized, String](
    databaseConfigProvider,
    NFTs.TableQuery,
    executionContext,
    NFTs.module,
    NFTs.logger
  ) {


  object Service {

    def fetchAll(): Future[Seq[NFT]] = getAll.map(_.map(_.deserialize))

  }
}