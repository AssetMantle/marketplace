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

case class NFT(fileName: String, file: Array[Byte], collectionId: String, name: String, description: String, properties: Seq[Property], ipfsLink: String, edition: Option[Int], createdBy: Option[String] = None, createdOn: Option[Timestamp] = None, createdOnTimeZone: Option[String] = None, updatedBy: Option[String] = None, updatedOn: Option[Timestamp] = None, updatedOnTimeZone: Option[String] = None) extends Logged {

  def serialize(): NFTs.NFTSerialized = NFTs.NFTSerialized(
    fileName = this.fileName,
    file = this.file,
    collectionId = collectionId,
    name = this.name,
    description = this.description,
    properties = Json.toJson(this.properties).toString(),
    ipfsLink = this.ipfsLink,
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

  case class NFTSerialized(fileName: String, file: Array[Byte], collectionId: String, name: String, description: String, properties: String, ipfsLink: String, edition: Option[Int], createdBy: Option[String], createdOn: Option[Timestamp], createdOnTimeZone: Option[String], updatedBy: Option[String], updatedOn: Option[Timestamp], updatedOnTimeZone: Option[String]) extends Entity[String] {
    def deserialize: NFT = NFT(fileName = fileName, file = file, collectionId = collectionId, name = name, description = description, properties = utilities.JSON.convertJsonStringToObject[Seq[Property]](properties), ipfsLink = ipfsLink, edition = edition, createdBy = createdBy, createdOn = createdOn, createdOnTimeZone = createdOnTimeZone, updatedBy = updatedBy, updatedOn = updatedOn, updatedOnTimeZone = updatedOnTimeZone)

    def id: String = fileName
  }

  class NFTTable(tag: Tag) extends Table[NFTSerialized](tag, "NFT") with ModelTable[String] {

    def * = (fileName, file, collectionId, name, description, properties, ipfsLink, edition.?, createdBy.?, createdOn.?, createdOnTimeZone.?, updatedBy.?, updatedOn.?, updatedOnTimeZone.?) <> (NFTSerialized.tupled, NFTSerialized.unapply)

    def fileName = column[String]("fileName", O.PrimaryKey)

    def file = column[Array[Byte]]("file")

    def collectionId = column[String]("collectionId")

    def name = column[String]("name")

    def description = column[String]("description")

    def properties = column[String]("properties")

    def ipfsLink = column[String]("ipfsLink")

    def edition = column[Int]("edition")

    def createdBy = column[String]("createdBy")

    def createdOn = column[Timestamp]("createdOn")

    def createdOnTimeZone = column[String]("createdOnTimeZone")

    def updatedBy = column[String]("updatedBy")

    def updatedOn = column[Timestamp]("updatedOn")

    def updatedOnTimeZone = column[String]("updatedOnTimeZone")

    override def id = fileName
  }

  val TableQuery = new TableQuery(tag => new NFTTable(tag))
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

    def add(fileName: String, file: Array[Byte], collectionId: String, name: String, description: String, properties: Seq[Property], ipfsLink: String, edition: Option[Int]) = {
      val nft = NFT(
        fileName = fileName,
        file = file,
        collectionId = collectionId,
        name = name,
        description = description,
        properties = properties,
        ipfsLink = ipfsLink,
        edition = edition
      )
      create(nft.serialize())
    }

    def fetchAll(): Future[Seq[NFT]] = getAll.map(_.map(_.deserialize))

    def tryGet(nftId: String): Future[NFT] = tryGetById(nftId).map(_.deserialize)

    def getByPageNumber(id: String, pageNumber: Int): Future[Seq[NFT]] = filter(_.collectionId === id).map(_.sortBy(_.createdOn).slice((pageNumber - 1) * constants.CommonConfig.Collections.CollectionsPerPage, pageNumber * constants.CommonConfig.Collections.CollectionsPerPage).map(_.deserialize))

    def getAllForCollection(collectionId: String): Future[Seq[NFT]] = filter(_.collectionId === collectionId).map(_.map(_.deserialize))

    def checkExistsByName(name: String): Future[Boolean] = filter(_.name === name).map(_.nonEmpty)

  }
}