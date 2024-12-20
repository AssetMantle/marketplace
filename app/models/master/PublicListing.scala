package models.master

import models.Trait.{Entity, GenericDaoImpl, Logging, ModelTable}
import models.history
import play.api.Logger
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.H2Profile.api._
import utilities.MicroNumber

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

case class PublicListing(id: String, collectionId: String, numberOfNFTs: Long, maxMintPerAccount: Long, price: MicroNumber, denom: String, startTimeEpoch: Long, endTimeEpoch: Long, createdBy: Option[String] = None, createdOnMillisEpoch: Option[Long] = None, updatedBy: Option[String] = None, updatedOnMillisEpoch: Option[Long] = None) extends Logging {

  def getStatus(numberOfSold: Long): constants.PublicListing.Status = {
    val currentEpoch = System.currentTimeMillis() / 1000
    if (numberOfSold >= numberOfNFTs) constants.PublicListing.SOLD_OUT // Sold out
    else if (currentEpoch >= this.startTimeEpoch && currentEpoch < this.endTimeEpoch) constants.PublicListing.LIVE // Live
    else if (currentEpoch >= this.endTimeEpoch) constants.PublicListing.ENDED // Expired
    else constants.PublicListing.NOT_STARTED //
  }

  def serialize(): PublicListings.PublicListingSerialized = PublicListings.PublicListingSerialized(
    id = this.id,
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

  def toHistory: history.MasterPublicListing = history.MasterPublicListing(
    id = this.id,
    collectionId = this.collectionId,
    numberOfNFTs = this.numberOfNFTs,
    maxMintPerAccount = this.maxMintPerAccount,
    price = this.price,
    denom = this.denom,
    startTimeEpoch = this.startTimeEpoch,
    endTimeEpoch = this.endTimeEpoch,
    createdBy = this.createdBy,
    createdOnMillisEpoch = this.createdOnMillisEpoch,
    updatedBy = this.updatedBy,
    updatedOnMillisEpoch = this.updatedOnMillisEpoch)
}

object PublicListings {

  implicit val module: String = constants.Module.MASTER_PUBLIC_LISTING

  implicit val logger: Logger = Logger(this.getClass)

  case class PublicListingSerialized(id: String, collectionId: String, numberOfNFTs: Long, maxMintPerAccount: Long, price: BigDecimal, denom: String, startTimeEpoch: Long, endTimeEpoch: Long, createdBy: Option[String] = None, createdOnMillisEpoch: Option[Long] = None, updatedBy: Option[String] = None, updatedOnMillisEpoch: Option[Long] = None) extends Entity[String] {

    def deserialize: PublicListing = PublicListing(id = id, collectionId = collectionId, numberOfNFTs = numberOfNFTs, maxMintPerAccount = maxMintPerAccount, price = MicroNumber(price), denom = denom, startTimeEpoch = startTimeEpoch, endTimeEpoch = endTimeEpoch, createdBy = createdBy, createdOnMillisEpoch = createdOnMillisEpoch, updatedBy = updatedBy, updatedOnMillisEpoch = updatedOnMillisEpoch)
  }

  class PublicListingTable(tag: Tag) extends Table[PublicListingSerialized](tag, "PublicListing") with ModelTable[String] {

    def * = (id, collectionId, numberOfNFTs, maxMintPerAccount, price, denom, startTimeEpoch, endTimeEpoch, createdBy.?, createdOnMillisEpoch.?, updatedBy.?, updatedOnMillisEpoch.?) <> (PublicListingSerialized.tupled, PublicListingSerialized.unapply)

    def id = column[String]("id", O.PrimaryKey)

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

  val TableQuery = new TableQuery(tag => new PublicListingTable(tag))
}

@Singleton
class PublicListings @Inject()(
                                protected val databaseConfigProvider: DatabaseConfigProvider
                              )(implicit override val executionContext: ExecutionContext)
  extends GenericDaoImpl[PublicListings.PublicListingTable, PublicListings.PublicListingSerialized, String](
    databaseConfigProvider,
    PublicListings.TableQuery,
    executionContext,
    PublicListings.module,
    PublicListings.logger
  ) {

  object Service {

    def add(publicListing: PublicListing): Future[String] = create(publicListing.serialize())

    def update(publicListing: PublicListing): Future[Unit] = updateById(publicListing.serialize())

    def tryGet(id: String): Future[PublicListing] = filterHead(_.id === id).map(_.deserialize)

    def get(ids: Seq[String]): Future[Seq[PublicListing]] = filter(_.id.inSet(ids)).map(_.map(_.deserialize))

    def getLivePublicListings: Future[Seq[String]] = {
      val currentEpoch = utilities.Date.currentEpoch
      filter(x => x.startTimeEpoch <= currentEpoch && x.endTimeEpoch > currentEpoch).map(_.map(_.id))
    }

    def getForDeletion: Future[Seq[PublicListing]] = filter(_.endTimeEpoch <= (utilities.Date.currentEpoch - constants.Date.DaySeconds)).map(_.map(_.deserialize))

    def getPublicListingByCollectionId(collectionId: String): Future[Option[PublicListing]] = filter(_.collectionId === collectionId).map(_.map(_.deserialize).headOption)

    def delete(publicListingId: String): Future[Int] = deleteById(publicListingId)

    def total: Future[Int] = countTotal()

    def getByPageNumber(pageNumber: Int): Future[Seq[PublicListing]] = getAllByPageNumber(offset = (pageNumber - 1) * constants.CommonConfig.Pagination.CollectionsPerPage, limit = constants.CommonConfig.Pagination.CollectionsPerPage)(_.endTimeEpoch).map(_.map(_.deserialize))
  }

}