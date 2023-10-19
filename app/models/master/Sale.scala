package models.master

import models.history
import models.master.Sales.SaleTable
import models.traits._
import play.api.Logger
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.H2Profile.api._
import utilities.MicroNumber

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

case class Sale(id: String, whitelistId: String, collectionId: String, numberOfNFTs: Long, maxMintPerAccount: Long, price: MicroNumber, denom: String, startTimeEpoch: Long, endTimeEpoch: Long, soldOut: Boolean, createdBy: Option[String] = None, createdOnMillisEpoch: Option[Long] = None, updatedBy: Option[String] = None, updatedOnMillisEpoch: Option[Long] = None) extends Logging {

  def getStatus(allSold: Boolean): constants.Sale.Status = {
    val currentEpoch = System.currentTimeMillis() / 1000
    if (allSold && currentEpoch >= this.startTimeEpoch && currentEpoch < this.endTimeEpoch) constants.Sale.SOLD_OUT // Sold out
    else if (currentEpoch >= this.startTimeEpoch && currentEpoch < this.endTimeEpoch) constants.Sale.LIVE // Live
    else if (currentEpoch >= this.endTimeEpoch) constants.Sale.ENDED // Expired
    else constants.Sale.NOT_STARTED //
  }

  def getStatus: constants.Sale.Status = {
    val currentEpoch = System.currentTimeMillis() / 1000
    if (currentEpoch >= this.startTimeEpoch && currentEpoch < this.endTimeEpoch) constants.Sale.LIVE
    else if (currentEpoch >= this.endTimeEpoch) constants.Sale.ENDED
    else constants.Sale.NOT_STARTED
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
    soldOut = this.soldOut,
    createdBy = this.createdBy,
    createdOnMillisEpoch = this.createdOnMillisEpoch,
    updatedBy = this.updatedBy,
    updatedOnMillisEpoch = this.updatedOnMillisEpoch)

  def toHistory: history.MasterSale = history.MasterSale(
    id = this.id,
    whitelistId = this.whitelistId,
    collectionId = this.collectionId,
    numberOfNFTs = this.numberOfNFTs,
    maxMintPerAccount = this.maxMintPerAccount,
    price = this.price,
    denom = this.denom,
    startTimeEpoch = this.startTimeEpoch,
    endTimeEpoch = this.endTimeEpoch,
    soldOut = this.soldOut,
    createdBy = this.createdBy,
    createdOnMillisEpoch = this.createdOnMillisEpoch,
    updatedBy = this.updatedBy,
    updatedOnMillisEpoch = this.updatedOnMillisEpoch)
}

private[master] object Sales {
  case class SaleSerialized(id: String, whitelistId: String, collectionId: String, numberOfNFTs: Long, maxMintPerAccount: Long, price: BigDecimal, denom: String, startTimeEpoch: Long, endTimeEpoch: Long, soldOut: Boolean, createdBy: Option[String] = None, createdOnMillisEpoch: Option[Long] = None, updatedBy: Option[String] = None, updatedOnMillisEpoch: Option[Long] = None) extends Entity[String] {

    def deserialize()(implicit module: String, logger: Logger): Sale = Sale(id = id, whitelistId = whitelistId, collectionId = collectionId, numberOfNFTs = numberOfNFTs, maxMintPerAccount = maxMintPerAccount, price = MicroNumber(price), denom = denom, startTimeEpoch = startTimeEpoch, endTimeEpoch = endTimeEpoch, soldOut = soldOut, createdBy = createdBy, createdOnMillisEpoch = createdOnMillisEpoch, updatedBy = updatedBy, updatedOnMillisEpoch = updatedOnMillisEpoch)
  }

  class SaleTable(tag: Tag) extends Table[SaleSerialized](tag, "Sale") with ModelTable[String] {

    def * = (id, whitelistId, collectionId, numberOfNFTs, maxMintPerAccount, price, denom, startTimeEpoch, endTimeEpoch, soldOut, createdBy.?, createdOnMillisEpoch.?, updatedBy.?, updatedOnMillisEpoch.?) <> (SaleSerialized.tupled, SaleSerialized.unapply)

    def id = column[String]("id", O.PrimaryKey)

    def whitelistId = column[String]("whitelistId")

    def collectionId = column[String]("collectionId")

    def numberOfNFTs = column[Long]("numberOfNFTs")

    def maxMintPerAccount = column[Long]("maxMintPerAccount")

    def price = column[BigDecimal]("price")

    def denom = column[String]("denom")

    def startTimeEpoch = column[Long]("startTimeEpoch")

    def endTimeEpoch = column[Long]("endTimeEpoch")

    def soldOut = column[Boolean]("soldOut")

    def createdBy = column[String]("createdBy")

    def createdOnMillisEpoch = column[Long]("createdOnMillisEpoch")

    def updatedBy = column[String]("updatedBy")

    def updatedOnMillisEpoch = column[Long]("updatedOnMillisEpoch")
  }
}

@Singleton
class Sales @Inject()(
                       protected val dbConfigProvider: DatabaseConfigProvider,
                       masterNFTOwners: NFTOwners,
                     )(implicit val executionContext: ExecutionContext)
  extends GenericDaoImpl[Sales.SaleTable, Sales.SaleSerialized, String]() {

  implicit val module: String = constants.Module.MASTER_SALE

  implicit val logger: Logger = Logger(this.getClass)

  val tableQuery = new TableQuery(tag => new SaleTable(tag))

  object Service {

    def add(sale: Sale): Future[String] = create(sale.serialize()).map(_.id)

    def add(sales: Seq[Sale]): Future[Int] = create(sales.map(_.serialize()))

    def tryGet(id: String): Future[Sale] = filterHead(_.id === id).map(_.deserialize)

    def get(ids: Seq[String]): Future[Seq[Sale]] = filter(_.id.inSet(ids)).map(_.map(_.deserialize))

    def getByWhitelistId(whitelistId: String): Future[Seq[Sale]] = filter(_.whitelistId === whitelistId).map(_.map(_.deserialize))

    def tryGetWhitelistId(id: String): Future[String] = filterHead(_.id === id).map(_.whitelistId)

    def getLiveSales: Future[Seq[String]] = {
      val currentEpoch = utilities.Date.currentEpoch
      filter(x => x.startTimeEpoch <= currentEpoch && x.endTimeEpoch > currentEpoch).map(_.map(_.id))
    }

    def getForDeletion: Future[Seq[Sale]] = filter(x => x.endTimeEpoch <= (utilities.Date.currentEpoch - constants.Date.DaySeconds) || x.soldOut).map(_.map(_.deserialize))

    def getIdsCurrentOnSaleByWhitelistIds(whitelistIds: Seq[String]): Future[Seq[String]] = {
      val currentEpoch = utilities.Date.currentEpoch
      filter(x => x.whitelistId.inSet(whitelistIds) && x.endTimeEpoch > currentEpoch).map(_.map(_.id))
    }

    def markSold(id: String): Future[Int] = customUpdate(tableQuery.filter(_.id === id).map(_.soldOut).update(true))

    def getSaleByCollectionId(collectionId: String): Future[Option[Sale]] = filter(_.collectionId === collectionId).map(_.map(_.deserialize).headOption)

    def tryGetSaleByCollectionId(collectionId: String): Future[Sale] = filterHead(_.collectionId === collectionId).map(_.deserialize)

    def delete(saleId: String): Future[Int] = deleteById(saleId)

    def total: Future[Int] = countTotal()

    def getSalesByCollectionId(collectionId: String): Future[Option[Sale]] = filter(_.collectionId === collectionId).map(_.map(_.deserialize).headOption)

    def getByPageNumber(pageNumber: Int): Future[Seq[Sale]] = sortWithPagination(_.endTimeEpoch)(offset = (pageNumber - 1) * constants.CommonConfig.Pagination.CollectionsPerPage, limit = constants.CommonConfig.Pagination.CollectionsPerPage).map(_.map(_.deserialize))

    def checkExistsByCollectionId(collectionId: String): Future[Boolean] = filterAndExists(_.collectionId === collectionId)
  }

  object Utility {

    def checkSale(saleId: String): Future[Unit] = {
      val notAllSold = masterNFTOwners.Service.checkAnySaleExists(saleId)

      def checkAndMarkSold(notAllSold: Boolean) = if (!notAllSold) Service.markSold(saleId) else Future(0)

      for {
        notAllSold <- notAllSold
        _ <- checkAndMarkSold(notAllSold)
      } yield ()

    }

  }

}