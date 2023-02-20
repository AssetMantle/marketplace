package models.master

import models.Trait.{Entity, GenericDaoImpl, Logging, ModelTable}
import models.history
import play.api.Logger
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.H2Profile.api._
import utilities.MicroNumber

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

case class SecondaryMarket(id: String, orderId: Array[Byte], collectionId: String, price: MicroNumber, denom: String, endTimeEpoch: Long, createdBy: Option[String] = None, createdOnMillisEpoch: Option[Long] = None, updatedBy: Option[String] = None, updatedOnMillisEpoch: Option[Long] = None) extends Logging {

  def getStatus: constants.SecondaryMarket.Status = {
    val currentEpoch = System.currentTimeMillis() / 1000
    if (currentEpoch < this.endTimeEpoch) constants.SecondaryMarket.LIVE // Live
    else if (currentEpoch >= this.endTimeEpoch) constants.SecondaryMarket.ENDED // Expired
    else constants.SecondaryMarket.NOT_STARTED
  }

  def serialize(): SecondaryMarkets.SecondaryMarketSerialized = SecondaryMarkets.SecondaryMarketSerialized(
    id = this.id,
    orderId = this.orderId,
    collectionId = this.collectionId,
    price = this.price.toBigDecimal,
    denom = this.denom,
    endTimeEpoch = this.endTimeEpoch,
    createdBy = this.createdBy,
    createdOnMillisEpoch = this.createdOnMillisEpoch,
    updatedBy = this.updatedBy,
    updatedOnMillisEpoch = this.updatedOnMillisEpoch)

  def toHistory: history.MasterSecondaryMarket = history.MasterSecondaryMarket(
    id = this.id,
    orderId = this.orderId,
    collectionId = this.collectionId,
    price = this.price,
    denom = this.denom,
    endTimeEpoch = this.endTimeEpoch,
    createdBy = this.createdBy,
    createdOnMillisEpoch = this.createdOnMillisEpoch,
    updatedBy = this.updatedBy,
    updatedOnMillisEpoch = this.updatedOnMillisEpoch)
}

object SecondaryMarkets {

  implicit val module: String = constants.Module.MASTER_SECONDARY_MARKET

  implicit val logger: Logger = Logger(this.getClass)

  case class SecondaryMarketSerialized(id: String, orderId: Array[Byte], collectionId: String, price: BigDecimal, denom: String, endTimeEpoch: Long, createdBy: Option[String] = None, createdOnMillisEpoch: Option[Long] = None, updatedBy: Option[String] = None, updatedOnMillisEpoch: Option[Long] = None) extends Entity[String] {

    def deserialize: SecondaryMarket = SecondaryMarket(id = id, orderId = orderId, collectionId = collectionId, price = MicroNumber(price), denom = denom, endTimeEpoch = endTimeEpoch, createdBy = createdBy, createdOnMillisEpoch = createdOnMillisEpoch, updatedBy = updatedBy, updatedOnMillisEpoch = updatedOnMillisEpoch)
  }

  class SecondaryMarketTable(tag: Tag) extends Table[SecondaryMarketSerialized](tag, "SecondaryMarket") with ModelTable[String] {

    def * = (id, orderId, collectionId, price, denom, endTimeEpoch, createdBy.?, createdOnMillisEpoch.?, updatedBy.?, updatedOnMillisEpoch.?) <> (SecondaryMarketSerialized.tupled, SecondaryMarketSerialized.unapply)

    def id = column[String]("id", O.PrimaryKey)

    def orderId = column[Array[Byte]]("orderId")

    def collectionId = column[String]("collectionId")

    def price = column[BigDecimal]("price")

    def denom = column[String]("denom")

    def endTimeEpoch = column[Long]("endTimeEpoch")

    def createdBy = column[String]("createdBy")

    def createdOnMillisEpoch = column[Long]("createdOnMillisEpoch")

    def updatedBy = column[String]("updatedBy")

    def updatedOnMillisEpoch = column[Long]("updatedOnMillisEpoch")
  }

  val TableQuery = new TableQuery(tag => new SecondaryMarketTable(tag))
}

@Singleton
class SecondaryMarkets @Inject()(
                                  protected val databaseConfigProvider: DatabaseConfigProvider
                                )(implicit override val executionContext: ExecutionContext)
  extends GenericDaoImpl[SecondaryMarkets.SecondaryMarketTable, SecondaryMarkets.SecondaryMarketSerialized, String](
    databaseConfigProvider,
    SecondaryMarkets.TableQuery,
    executionContext,
    SecondaryMarkets.module,
    SecondaryMarkets.logger
  ) {

  object Service {

    def add(secondaryMarket: SecondaryMarket): Future[String] = create(secondaryMarket.serialize())

    def update(secondaryMarket: SecondaryMarket): Future[Unit] = updateById(secondaryMarket.serialize())

    def tryGet(id: String): Future[SecondaryMarket] = filterHead(_.id === id).map(_.deserialize)

    def get(ids: Seq[String]): Future[Seq[SecondaryMarket]] = filter(_.id.inSet(ids)).map(_.map(_.deserialize))

    def getForDeletion: Future[Seq[SecondaryMarket]] = filter(_.endTimeEpoch <= (utilities.Date.currentEpoch - constants.Date.DaySeconds)).map(_.map(_.deserialize))

    def getSecondaryMarketByCollectionId(collectionId: String): Future[Seq[SecondaryMarket]] = filter(_.collectionId === collectionId).map(_.map(_.deserialize))

    def delete(secondaryMarketId: String): Future[Int] = deleteById(secondaryMarketId)

    def total: Future[Int] = countTotal()

    def getByPageNumber(pageNumber: Int): Future[Seq[SecondaryMarket]] = getAllByPageNumber(offset = (pageNumber - 1) * constants.CommonConfig.Pagination.CollectionsPerPage, limit = constants.CommonConfig.Pagination.CollectionsPerPage)(_.endTimeEpoch).map(_.map(_.deserialize))
  }

}