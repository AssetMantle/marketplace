package models.master

import models.Trait.{Entity, GenericDaoImpl, Logging, ModelTable}
import play.api.Logger
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.H2Profile.api._
import utilities.MicroNumber

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

case class NFTWhitelistSale(id: String, fileName: String, whitelistId: String, quantity: Long, price: MicroNumber, denom: String, creatorFee: BigDecimal, startTimeEpoch: Long, endTimeEpoch: Long, createdBy: Option[String] = None, createdOnMillisEpoch: Option[Long] = None, updatedBy: Option[String] = None, updatedOnMillisEpoch: Option[Long] = None) extends Logging {

  def serialize(): NFTWhitelistSales.NFTWhitelistSaleSerialized = NFTWhitelistSales.NFTWhitelistSaleSerialized(
    id = this.id,
    fileName = this.fileName,
    whitelistId = this.whitelistId,
    quantity = this.quantity,
    price = this.price.toBigDecimal,
    denom = this.denom,
    startTimeEpoch = this.startTimeEpoch,
    endTimeEpoch = this.endTimeEpoch,
    creatorFee = this.creatorFee,
    createdBy = this.createdBy,
    createdOnMillisEpoch = this.createdOnMillisEpoch,
    updatedBy = this.updatedBy,
    updatedOnMillisEpoch = this.updatedOnMillisEpoch)
}

object NFTWhitelistSales {

  implicit val module: String = constants.Module.MASTER_NFT_WHITELIST_SALE

  implicit val logger: Logger = Logger(this.getClass)

  case class NFTWhitelistSaleSerialized(id: String, fileName: String, whitelistId: String, quantity: Long, price: BigDecimal, denom: String, creatorFee: BigDecimal, startTimeEpoch: Long, endTimeEpoch: Long, createdBy: Option[String] = None, createdOnMillisEpoch: Option[Long] = None, updatedBy: Option[String] = None, updatedOnMillisEpoch: Option[Long] = None) extends Entity[String] {

    def deserialize: NFTWhitelistSale = NFTWhitelistSale(id = id, fileName = fileName, whitelistId = whitelistId, quantity = quantity, price = MicroNumber(price), denom = denom, creatorFee = creatorFee, startTimeEpoch = startTimeEpoch, endTimeEpoch = endTimeEpoch, createdBy = createdBy, createdOnMillisEpoch = createdOnMillisEpoch, updatedBy = updatedBy, updatedOnMillisEpoch = updatedOnMillisEpoch)

  }

  class NFTWhitelistSaleTable(tag: Tag) extends Table[NFTWhitelistSaleSerialized](tag, "NFTWhitelistSale") with ModelTable[String] {

    def * = (id, fileName, whitelistId, quantity, price, denom, creatorFee, startTimeEpoch, endTimeEpoch, createdBy.?, createdOnMillisEpoch.?, updatedBy.?, updatedOnMillisEpoch.?) <> (NFTWhitelistSaleSerialized.tupled, NFTWhitelistSaleSerialized.unapply)

    def id = column[String]("id", O.PrimaryKey)

    def fileName = column[String]("fileName")

    def whitelistId = column[String]("whitelistId")

    def quantity = column[Long]("quantity")

    def price = column[BigDecimal]("price")

    def denom = column[String]("denom")

    def startTimeEpoch = column[Long]("startTimeEpoch")

    def endTimeEpoch = column[Long]("endTimeEpoch")

    def creatorFee = column[BigDecimal]("creatorFee")

    def createdBy = column[String]("createdBy")

    def createdOnMillisEpoch = column[Long]("createdOnMillisEpoch")

    def updatedBy = column[String]("updatedBy")

    def updatedOnMillisEpoch = column[Long]("updatedOnMillisEpoch")

  }

  val TableQuery = new TableQuery(tag => new NFTWhitelistSaleTable(tag))
}

@Singleton
class NFTWhitelistSales @Inject()(
                                   protected val databaseConfigProvider: DatabaseConfigProvider
                                 )(implicit override val executionContext: ExecutionContext)
  extends GenericDaoImpl[NFTWhitelistSales.NFTWhitelistSaleTable, NFTWhitelistSales.NFTWhitelistSaleSerialized, String](
    databaseConfigProvider,
    NFTWhitelistSales.TableQuery,
    executionContext,
    NFTWhitelistSales.module,
    NFTWhitelistSales.logger
  ) {

  object Service {

    def add(nftWhitelistSale: NFTWhitelistSale): Future[String] = create(nftWhitelistSale.serialize())

    def add(nftWhitelistSales: Seq[NFTWhitelistSale]): Future[Unit] = create(nftWhitelistSales.map(_.serialize()))

    def tryGet(fileName: String, whitelistId: String): Future[NFTWhitelistSale] = filterHead(x => x.fileName === fileName && x.whitelistId === whitelistId).map(_.deserialize)

    def tryGet(id: String): Future[NFTWhitelistSale] = filterHead(_.id === id).map(_.deserialize)

    def getByPageNumber(whitelistIds: Seq[String], pageNumber: Int): Future[Seq[NFTWhitelistSale]] = filterAndSortWithPagination(offset = (pageNumber - 1) * constants.CommonConfig.Pagination.NFTsPerPage, limit = constants.CommonConfig.Pagination.NFTsPerPage)(_.whitelistId.inSet(whitelistIds))(_.endTimeEpoch).map(_.map(_.deserialize))
  }
}