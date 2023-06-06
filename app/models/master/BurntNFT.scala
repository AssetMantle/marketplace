package models.master

import models.common.NFT._
import models.traits._
import play.api.Logger
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.json.Json
import slick.jdbc.H2Profile.api._

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

case class BurntNFT(nftId: String, txHash: String, collectionId: String, assetId: String, classificationId: String, supply: BigInt, name: String, description: String, properties: Seq[BaseNFTProperty], fileExtension: String, createdBy: Option[String] = None, createdOnMillisEpoch: Option[Long] = None, updatedBy: Option[String] = None, updatedOnMillisEpoch: Option[Long] = None) extends Logging {

  def serialize(): BurntNFTs.BurntNFTSerialized = BurntNFTs.BurntNFTSerialized(
    nftId = this.nftId,
    txHash = this.txHash,
    collectionId = collectionId,
    assetId = this.assetId,
    classificationId = this.classificationId,
    supply = BigDecimal(this.supply),
    name = this.name,
    description = this.description,
    fileExtension = this.fileExtension,
    properties = Json.toJson(this.properties).toString(),
    createdBy = this.createdBy,
    createdOnMillisEpoch = this.createdOnMillisEpoch,
    updatedBy = this.updatedBy,
    updatedOnMillisEpoch = this.updatedOnMillisEpoch)
}

object BurntNFTs {

  implicit val module: String = constants.Module.MASTER_BURNT_NFT

  implicit val logger: Logger = Logger(this.getClass)

  case class BurntNFTSerialized(nftId: String, txHash: String, collectionId: String, assetId: String, classificationId: String, supply: BigDecimal, name: String, description: String, properties: String, fileExtension: String, createdBy: Option[String], createdOnMillisEpoch: Option[Long], updatedBy: Option[String], updatedOnMillisEpoch: Option[Long]) extends Entity2[String, String] {
    def deserialize: BurntNFT = BurntNFT(nftId = nftId, collectionId = collectionId, assetId = assetId, classificationId = classificationId, supply = supply.toBigInt, txHash = txHash, name = name, description = description, fileExtension = fileExtension, properties = utilities.JSON.convertJsonStringToObject[Seq[BaseNFTProperty]](properties), createdBy = createdBy, createdOnMillisEpoch = createdOnMillisEpoch, updatedBy = updatedBy, updatedOnMillisEpoch = updatedOnMillisEpoch)

    def id1 = nftId

    def id2 = txHash
  }

  class BurntNFTTable(tag: Tag) extends Table[BurntNFTSerialized](tag, "BurntNFT") with ModelTable2[String, String] {

    def * = (nftId, txHash, collectionId, assetId, classificationId, supply, name, description, properties, fileExtension, createdBy.?, createdOnMillisEpoch.?, updatedBy.?, updatedOnMillisEpoch.?) <> (BurntNFTSerialized.tupled, BurntNFTSerialized.unapply)

    def nftId = column[String]("nftId", O.PrimaryKey)

    def txHash = column[String]("txHash", O.PrimaryKey)

    def collectionId = column[String]("collectionId")

    def assetId = column[String]("assetId")

    def classificationId = column[String]("classificationId")

    def supply = column[BigDecimal]("supply")

    def name = column[String]("name")

    def description = column[String]("description")

    def properties = column[String]("properties")

    def fileExtension = column[String]("fileExtension")

    def createdBy = column[String]("createdBy")

    def createdOnMillisEpoch = column[Long]("createdOnMillisEpoch")

    def updatedBy = column[String]("updatedBy")

    def updatedOnMillisEpoch = column[Long]("updatedOnMillisEpoch")

    def id1 = nftId

    def id2 = txHash

  }

  lazy val TableQuery = new TableQuery(tag => new BurntNFTTable(tag))
}

@Singleton
class BurntNFTs @Inject()(
                           protected val databaseConfigProvider: DatabaseConfigProvider
                         )(implicit override val executionContext: ExecutionContext)
  extends GenericDaoImpl2[BurntNFTs.BurntNFTTable, BurntNFTs.BurntNFTSerialized, String, String](
    databaseConfigProvider,
    BurntNFTs.TableQuery,
    executionContext,
    BurntNFTs.module,
    BurntNFTs.logger
  ) {


  object Service {

    def add(nftId: String, txHash: String, collectionId: String, assetId: String, classificationId: String, supply: BigInt, name: String, description: String, properties: Seq[BaseNFTProperty], fileExtension: String): Future[Unit] = {
      val burntNFT = BurntNFT(nftId = nftId, txHash = txHash, collectionId = collectionId, assetId = assetId, classificationId = classificationId, supply = supply, name = name, description = description, properties = properties, fileExtension = fileExtension)
      create(burntNFT.serialize())
    }

    def getByPageNumber(collectionId: String, pageNumber: Int): Future[Seq[BurntNFT]] = filterAndSortWithPagination(offset = (pageNumber - 1) * constants.CommonConfig.Pagination.NFTsPerPage, limit = constants.CommonConfig.Pagination.NFTsPerPage)(_.collectionId === collectionId)(_.createdOnMillisEpoch).map(_.map(_.deserialize))

    def getAllForCollection(collectionId: String): Future[Seq[BurntNFT]] = filter(_.collectionId === collectionId).map(_.map(_.deserialize))

    def countAllForCollection(collectionId: String): Future[Int] = filterAndCount(_.collectionId === collectionId)

    def checkExists(nftId: String): Future[Boolean] = filterAndExists(_.nftId === nftId)

  }
}