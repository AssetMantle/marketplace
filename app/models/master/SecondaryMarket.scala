package models.master

import models.history
import models.master.SecondaryMarkets.SecondaryMarketTable
import models.traits._
import play.api.Logger
import play.api.db.slick.DatabaseConfigProvider
import schema.id.base.{HashID, OrderID}
import slick.jdbc.H2Profile.api._
import utilities.MicroNumber

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

case class SecondaryMarket(id: String, orderId: String, nftId: String, collectionId: String, sellerId: String, quantity: BigInt, price: MicroNumber, denom: String, endHours: Int, externallyMade: Boolean, completed: Boolean, cancelled: Boolean, expired: Boolean, status: Option[Boolean], createdBy: Option[String] = None, createdOnMillisEpoch: Option[Long] = None, updatedBy: Option[String] = None, updatedOnMillisEpoch: Option[Long] = None) extends Logging {

  def getOrderID: OrderID = OrderID(HashID(utilities.Secrets.base64URLDecode(this.orderId)))

  def getTotal: MicroNumber = MicroNumber(this.quantity * this.price.value)

  def serialize(): SecondaryMarkets.SecondaryMarketSerialized = SecondaryMarkets.SecondaryMarketSerialized(
    id = this.id,
    orderId = this.orderId,
    nftId = this.nftId,
    collectionId = this.collectionId,
    sellerId = this.sellerId,
    quantity = BigDecimal(this.quantity),
    price = this.price.toBigDecimal,
    denom = this.denom,
    endHours = this.endHours,
    externallyMade = this.externallyMade,
    completed = this.completed,
    cancelled = this.cancelled,
    expired = this.expired,
    status = this.status,
    createdBy = this.createdBy,
    createdOnMillisEpoch = this.createdOnMillisEpoch,
    updatedBy = this.updatedBy,
    updatedOnMillisEpoch = this.updatedOnMillisEpoch)

  def toHistory: history.MasterSecondaryMarket = history.MasterSecondaryMarket(
    id = this.id,
    orderId = this.orderId,
    nftId = this.nftId,
    collectionId = this.collectionId,
    sellerId = this.sellerId,
    quantity = this.quantity,
    price = this.price,
    denom = this.denom,
    endHours = this.endHours,
    externallyMade = this.externallyMade,
    completed = this.completed,
    cancelled = this.cancelled,
    expired = this.expired,
    status = this.status,
    createdBy = this.createdBy,
    createdOnMillisEpoch = this.createdOnMillisEpoch,
    updatedBy = this.updatedBy,
    updatedOnMillisEpoch = this.updatedOnMillisEpoch)
}

private[master] object SecondaryMarkets {
  case class SecondaryMarketSerialized(id: String, orderId: String, nftId: String, collectionId: String, sellerId: String, quantity: BigDecimal, price: BigDecimal, denom: String, endHours: Int, externallyMade: Boolean, completed: Boolean, cancelled: Boolean, expired: Boolean, status: Option[Boolean], createdBy: Option[String] = None, createdOnMillisEpoch: Option[Long] = None, updatedBy: Option[String] = None, updatedOnMillisEpoch: Option[Long] = None) extends Entity[String] {

    def deserialize()(implicit module: String, logger: Logger): SecondaryMarket = SecondaryMarket(id = id, orderId = orderId, nftId = nftId, collectionId = collectionId, sellerId = sellerId, quantity = this.quantity.toBigInt, price = MicroNumber(price), denom = denom, endHours = endHours, externallyMade = externallyMade, completed = completed, cancelled = cancelled, expired = expired, status = status, createdBy = createdBy, createdOnMillisEpoch = createdOnMillisEpoch, updatedBy = updatedBy, updatedOnMillisEpoch = updatedOnMillisEpoch)
  }

  class SecondaryMarketTable(tag: Tag) extends Table[SecondaryMarketSerialized](tag, "SecondaryMarket") with ModelTable[String] {

    def * = (id, orderId, nftId, collectionId, sellerId, quantity, price, denom, endHours, externallyMade, completed, cancelled, expired, status.?, createdBy.?, createdOnMillisEpoch.?, updatedBy.?, updatedOnMillisEpoch.?) <> (SecondaryMarketSerialized.tupled, SecondaryMarketSerialized.unapply)

    def id = column[String]("id", O.PrimaryKey)

    def orderId = column[String]("orderId", O.Unique)

    def nftId = column[String]("nftId")

    def collectionId = column[String]("collectionId")

    def sellerId = column[String]("sellerId")

    def quantity = column[BigDecimal]("quantity")

    def price = column[BigDecimal]("price")

    def denom = column[String]("denom")

    def endHours = column[Int]("endHours")

    def externallyMade = column[Boolean]("externallyMade")

    def completed = column[Boolean]("completed")

    def cancelled = column[Boolean]("cancelled")

    def expired = column[Boolean]("expired")

    def status = column[Boolean]("status")

    def createdBy = column[String]("createdBy")

    def createdOnMillisEpoch = column[Long]("createdOnMillisEpoch")

    def updatedBy = column[String]("updatedBy")

    def updatedOnMillisEpoch = column[Long]("updatedOnMillisEpoch")
  }
}

@Singleton
class SecondaryMarkets @Inject()(
                                  protected val dbConfigProvider: DatabaseConfigProvider,
                                )(implicit val executionContext: ExecutionContext)
  extends GenericDaoImpl[SecondaryMarkets.SecondaryMarketTable, SecondaryMarkets.SecondaryMarketSerialized, String]() {

  implicit val module: String = constants.Module.MASTER_SECONDARY_MARKET

  implicit val logger: Logger = Logger(this.getClass)

  val tableQuery = new TableQuery(tag => new SecondaryMarketTable(tag))

  object Service {

    def add(secondaryMarket: SecondaryMarket): Future[String] = create(secondaryMarket.serialize()).map(_.id)

    def update(secondaryMarket: SecondaryMarket): Future[Unit] = updateById(secondaryMarket.serialize())

    def tryGet(id: String): Future[SecondaryMarket] = filterHead(_.id === id).map(_.deserialize)

    def get(ids: Seq[String]): Future[Seq[SecondaryMarket]] = filter(_.id.inSet(ids)).map(_.map(_.deserialize))

    def getBySortedNFTIDs(nftIds: Seq[String]): Future[Seq[SecondaryMarket]] = filter(_.nftId.inSet(nftIds)).map(_.map(_.deserialize)).map(_.sortBy(_.price))

    def getByNFTIdAndLowestPrice(nftId: String): Future[Option[SecondaryMarket]] = filterAndSort(x => x.nftId === nftId && !x.completed && !x.cancelled && !x.expired && x.status)(_.price).map(_.headOption.map(_.deserialize))

    def getByNFTIdAndSellerId(nftId: String, sellerId: String): Future[Seq[SecondaryMarket]] = filter(x => x.nftId === nftId && x.sellerId === sellerId && !x.completed && !x.cancelled && !x.expired && x.status).map(_.map(_.deserialize))

    def getByNFTIdAndSellerIdAndPageNumber(nftId: String, sellerId: String, pageNumber: Int): Future[Seq[SecondaryMarket]] = filterAndSortWithPagination(x => x.nftId === nftId && x.sellerId === sellerId && !x.completed && !x.cancelled && !x.expired && x.status)(_.price)(offset = (pageNumber - 1) * constants.CommonConfig.Pagination.OrdersPerPage, limit = constants.CommonConfig.Pagination.OrdersPerPage).map(_.map(_.deserialize))

    def getByNFTIdAndPageNumber(nftId: String, pageNumber: Int): Future[Seq[SecondaryMarket]] = filterAndSortWithPagination(x => x.nftId === nftId && !x.completed && !x.cancelled && !x.expired && x.status)(_.price)(offset = (pageNumber - 1) * constants.CommonConfig.Pagination.OrdersPerPage, limit = constants.CommonConfig.Pagination.OrdersPerPage).map(_.map(_.deserialize))

    def nftExists(nftId: String): Future[Boolean] = filterAndExists(_.nftId === nftId)

    def delete(secondaryMarketId: String): Future[Int] = deleteById(secondaryMarketId)

    def getFloorPriceForCollection(collectionId: String): Future[MicroNumber] = filterAndSortHead(x => x.collectionId === collectionId && !x.completed && !x.cancelled && !x.expired && x.status)(_.price).map(_.deserialize().price)

    def existByCollectionId(collectionId: String): Future[Boolean] = filterAndExists(_.collectionId === collectionId)

    def getByCollectionId(collectionId: String, pageNumber: Int): Future[Seq[SecondaryMarket]] = filterAndSortWithPagination(_.collectionId === collectionId)(_.price)(offset = (pageNumber - 1) * constants.CommonConfig.Pagination.CollectionsPerPage, limit = constants.CommonConfig.Pagination.CollectionsPerPage).map(_.map(_.deserialize))

    def total: Future[Int] = countTotal()

    def totalForNFT(nftId: String): Future[Int] = filterAndCount(_.nftId === nftId)

    def totalYourOrders(nftId: String, sellerId: String): Future[Int] = filterAndCount(x => x.nftId === nftId && x.sellerId === sellerId)

    def markOnOrderCreationFailed(secondaryMarketIds: Seq[String]): Future[Int] = customUpdate(tableQuery.filter(_.id.inSet(secondaryMarketIds)).map(_.status).update(false))

    def markSecondaryMarketCreated(id: String): Future[Int] = customUpdate(tableQuery.filter(_.id === id).map(_.status).update(true))

    def getFailedOrders: Future[Seq[SecondaryMarket]] = filter(!_.status).map(_.map(_.deserialize))

    def markOnCompletion(id: String): Future[Int] = customUpdate(tableQuery.filter(_.id === id).map(_.completed).update(true))

    def getCompletedOrders: Future[Seq[SecondaryMarket]] = filter(_.completed).map(_.map(_.deserialize))

    def markOnCancellation(id: String): Future[Int] = customUpdate(tableQuery.filter(_.id === id).map(_.cancelled).update(true))

    def getCancelledOrders: Future[Seq[SecondaryMarket]] = filter(_.cancelled).map(_.map(_.deserialize))

    def markOnExpiry(ids: Seq[String]): Future[Int] = customUpdate(tableQuery.filter(_.id.inSet(ids)).map(_.expired).update(true))

    def getExpiredOrders: Future[Seq[SecondaryMarket]] = filter(_.expired).map(_.map(_.deserialize))

    def getByPageNumber(pageNumber: Int): Future[Seq[SecondaryMarket]] = sortWithPagination(_.endHours)(offset = (pageNumber - 1) * constants.CommonConfig.Pagination.CollectionsPerPage, limit = constants.CommonConfig.Pagination.CollectionsPerPage).map(_.map(_.deserialize))

    def getAllOrderIDs: Future[Seq[String]] = customQuery(tableQuery.map(_.orderId).result)

    def getByOrderId(orderId: OrderID): Future[Option[SecondaryMarket]] = filter(_.orderId === orderId.asString).map(_.headOption.map(_.deserialize))

    def getCollectionBySeller(sellerId: String, pageNumber: Int): Future[Seq[String]] = filterAndSortWithPagination(_.sellerId === sellerId)(_.createdOnMillisEpoch)(offset = (pageNumber - 1) * constants.CommonConfig.Pagination.CollectionsPerPage, limit = constants.CommonConfig.Pagination.CollectionsPerPage).map(_.map(_.collectionId))

    def totalOnSellBySeller(sellerId: String): Future[Int] = filterAndCount(_.sellerId === sellerId)

    def totalOnSellBySellerAndCollection(sellerId: String, collectionId: String): Future[Int] = filterAndCount(x => x.sellerId === sellerId && x.collectionId === collectionId)

    def getByCollectionSellerAndPageNumber(sellerId: String, collectionId: String, pageNumber: Int): Future[Seq[SecondaryMarket]] = filterAndSortWithPagination(x => x.sellerId === sellerId && x.collectionId === collectionId)(_.createdOnMillisEpoch)(offset = (pageNumber - 1) * constants.CommonConfig.Pagination.CollectionsPerPage, limit = constants.CommonConfig.Pagination.CollectionsPerPage).map(_.map(_.deserialize()))
  }

}