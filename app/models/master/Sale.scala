package models.master

import models.Trait.{Entity, GenericDaoImpl, Logging, ModelTable}
import play.api.Logger
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.H2Profile.api._
import utilities.MicroNumber

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

case class Sale(id: String, whitelistId: String, collectionId: String, numberOfNFTs: Long, maxMintPerAccount: Long, price: MicroNumber, denom: String, startTimeEpoch: Long, endTimeEpoch: Long, createdBy: Option[String] = None, createdOnMillisEpoch: Option[Long] = None, updatedBy: Option[String] = None, updatedOnMillisEpoch: Option[Long] = None) extends Logging {

  def getStatus: String = {
    val currentEpoch = System.currentTimeMillis() / 1000
    if (currentEpoch >= this.startTimeEpoch && currentEpoch < this.endTimeEpoch) constants.View.SALE_ONGOING
    else if (currentEpoch >= this.endTimeEpoch) constants.View.SALE_EXPIRED
    else constants.View.SALE_NOT_STARTED
  }

  def serialize(): Sales.SaleSerialized = Sales.SaleSerialized(
    id = this.id,
    whitelistId = this.whitelistId,
    collectionId = this.collectionId,
    numberOfNFTs = this.numberOfNFTs,
    maxMintPerAccount = this.maxMintPerAccount,
    price = this.price.toBigDecimal,
    denom = this.denom,
    startTimeEpoch = this.startTimeEpoch,
    endTimeEpoch = this.endTimeEpoch,
    createdBy = this.createdBy,
    createdOnMillisEpoch = this.createdOnMillisEpoch,
    updatedBy = this.updatedBy,
    updatedOnMillisEpoch = this.updatedOnMillisEpoch)
}

object Sales {

  implicit val module: String = constants.Module.MASTER_SALE

  implicit val logger: Logger = Logger(this.getClass)

  case class SaleSerialized(id: String, whitelistId: String, collectionId: String, numberOfNFTs: Long, maxMintPerAccount: Long, price: BigDecimal, denom: String, startTimeEpoch: Long, endTimeEpoch: Long, createdBy: Option[String] = None, createdOnMillisEpoch: Option[Long] = None, updatedBy: Option[String] = None, updatedOnMillisEpoch: Option[Long] = None) extends Entity[String] {

    def deserialize: Sale = Sale(id = id, whitelistId = whitelistId, collectionId = collectionId, numberOfNFTs = numberOfNFTs, maxMintPerAccount = maxMintPerAccount, price = MicroNumber(price), denom = denom, startTimeEpoch = startTimeEpoch, endTimeEpoch = endTimeEpoch, createdBy = createdBy, createdOnMillisEpoch = createdOnMillisEpoch, updatedBy = updatedBy, updatedOnMillisEpoch = updatedOnMillisEpoch)
  }

  class SaleTable(tag: Tag) extends Table[SaleSerialized](tag, "Sale") with ModelTable[String] {

    def * = (id, whitelistId, collectionId, numberOfNFTs, maxMintPerAccount, price, denom, startTimeEpoch, endTimeEpoch, createdBy.?, createdOnMillisEpoch.?, updatedBy.?, updatedOnMillisEpoch.?) <> (SaleSerialized.tupled, SaleSerialized.unapply)

    def id = column[String]("id", O.PrimaryKey)

    def whitelistId = column[String]("whitelistId")

    def collectionId = column[String]("collectionId")

    def numberOfNFTs = column[Long]("numberOfNFTs")

    def maxMintPerAccount = column[Long]("maxMintPerAccount")

    def price = column[BigDecimal]("price")

    def denom = column[String]("denom")

    def startTimeEpoch = column[Long]("startTimeEpoch")

    def endTimeEpoch = column[Long]("endTimeEpoch")

    def createdBy = column[String]("createdBy")

    def createdOnMillisEpoch = column[Long]("createdOnMillisEpoch")

    def updatedBy = column[String]("updatedBy")

    def updatedOnMillisEpoch = column[Long]("updatedOnMillisEpoch")
  }

  val TableQuery = new TableQuery(tag => new SaleTable(tag))
}

@Singleton
class Sales @Inject()(
                       masterNFTs: NFTs,
                       protected val databaseConfigProvider: DatabaseConfigProvider
                     )(implicit override val executionContext: ExecutionContext)
  extends GenericDaoImpl[Sales.SaleTable, Sales.SaleSerialized, String](
    databaseConfigProvider,
    Sales.TableQuery,
    executionContext,
    Sales.module,
    Sales.logger
  ) {

  object Service {

    def add(sale: Sale): Future[String] = create(sale.serialize())

    def add(sales: Seq[Sale]): Future[Unit] = create(sales.map(_.serialize()))

    def tryGet(id: String): Future[Sale] = filterHead(_.id === id).map(_.deserialize)

    def get(ids: Seq[String]) = filter(_.id.inSet(ids)).map(_.map(_.deserialize))

    def getByWhitelistId(whitelistId: String): Future[Seq[Sale]] = filter(_.whitelistId === whitelistId).map(_.map(_.deserialize))

    def tryGetWhitelistId(id: String): Future[String] = filterHead(_.id === id).map(_.whitelistId)

    def getIdsCurrentOnSaleByWhitelistIds(whitelistIds: Seq[String]): Future[Seq[String]] = {
      val currentEpoch = utilities.Date.currentEpoch
      filter(x => x.whitelistId.inSet(whitelistIds) && x.endTimeEpoch > currentEpoch).map(_.map(_.id))
    }
  }

}