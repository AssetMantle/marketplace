package models.master

import models.history
import models.master.PublicListings.PublicListingTable
import models.traits._
import play.api.Logger
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.H2Profile.api._
import utilities.MicroNumber

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

case class PublicListing(id: String, collectionId: String, numberOfNFTs: Long, maxMintPerAccount: Long, price: MicroNumber, denom: String, startTimeEpoch: Long, endTimeEpoch: Long, soldOut: Boolean = false, createdBy: Option[String] = None, createdOnMillisEpoch: Option[Long] = None, updatedBy: Option[String] = None, updatedOnMillisEpoch: Option[Long] = None) extends Logging {

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
    soldOut = this.soldOut,
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
    soldOut = this.soldOut,
    createdBy = this.createdBy,
    createdOnMillisEpoch = this.createdOnMillisEpoch,
    updatedBy = this.updatedBy,
    updatedOnMillisEpoch = this.updatedOnMillisEpoch)
}

private[master] object PublicListings {

  case class PublicListingSerialized(id: String, collectionId: String, numberOfNFTs: Long, maxMintPerAccount: Long, price: BigDecimal, denom: String, startTimeEpoch: Long, endTimeEpoch: Long, soldOut: Boolean, createdBy: Option[String] = None, createdOnMillisEpoch: Option[Long] = None, updatedBy: Option[String] = None, updatedOnMillisEpoch: Option[Long] = None) extends Entity[String] {

    def deserialize()(implicit module: String, logger: Logger): PublicListing = PublicListing(id = id, collectionId = collectionId, numberOfNFTs = numberOfNFTs, maxMintPerAccount = maxMintPerAccount, price = MicroNumber(price), denom = denom, startTimeEpoch = startTimeEpoch, endTimeEpoch = endTimeEpoch, soldOut = soldOut, createdBy = createdBy, createdOnMillisEpoch = createdOnMillisEpoch, updatedBy = updatedBy, updatedOnMillisEpoch = updatedOnMillisEpoch)
  }

  class PublicListingTable(tag: Tag) extends Table[PublicListingSerialized](tag, "PublicListing") with ModelTable[String] {

    def * = (id, collectionId, numberOfNFTs, maxMintPerAccount, price, denom, startTimeEpoch, endTimeEpoch, soldOut, createdBy.?, createdOnMillisEpoch.?, updatedBy.?, updatedOnMillisEpoch.?) <> (PublicListingSerialized.tupled, PublicListingSerialized.unapply)

    def id = column[String]("id", O.PrimaryKey)

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
class PublicListings @Inject()(
                                protected val dbConfigProvider: DatabaseConfigProvider,
                                utilitiesOperations: utilities.Operations,
                                masterNFTOwners: NFTOwners,
                              )(implicit val executionContext: ExecutionContext)
  extends GenericDaoImpl[PublicListings.PublicListingTable, PublicListings.PublicListingSerialized, String]() {

  implicit val module: String = constants.Module.MASTER_PUBLIC_LISTING

  implicit val logger: Logger = Logger(this.getClass)

  val tableQuery = new TableQuery(tag => new PublicListingTable(tag))

  object Service {

    def add(publicListing: PublicListing): Future[String] = create(publicListing.serialize()).map(_.id)

    def update(publicListing: PublicListing): Future[Unit] = updateById(publicListing.serialize())

    def tryGet(id: String): Future[PublicListing] = filterHead(_.id === id).map(_.deserialize)

    def get(ids: Seq[String]): Future[Seq[PublicListing]] = filter(_.id.inSet(ids)).map(_.map(_.deserialize))

    def getLivePublicListingIDs: Future[Seq[String]] = {
      val currentEpoch = utilities.Date.currentEpoch
      filter(x => x.startTimeEpoch <= currentEpoch && x.endTimeEpoch > currentEpoch).map(_.map(_.id))
    }

    def markSold(id: String): Future[Int] = customUpdate(tableQuery.filter(_.id === id).map(_.soldOut).update(true))

    def getForDeletion: Future[Seq[PublicListing]] = filter(x => x.endTimeEpoch <= (utilities.Date.currentEpoch - constants.Date.DaySeconds) || x.soldOut).map(_.map(_.deserialize))

    def getPublicListingByCollectionId(collectionId: String): Future[Option[PublicListing]] = filter(_.collectionId === collectionId).map(_.map(_.deserialize).headOption)

    def tryGetPublicListingByCollectionId(collectionId: String): Future[PublicListing] = filterHead(_.collectionId === collectionId).map(_.deserialize)

    def delete(publicListingId: String): Future[Int] = deleteById(publicListingId)

    def total: Future[Int] = countTotal()

    def getByPageNumber(pageNumber: Int): Future[Seq[PublicListing]] = sortWithPagination(_.endTimeEpoch)(offset = (pageNumber - 1) * constants.CommonConfig.Pagination.CollectionsPerPage, limit = constants.CommonConfig.Pagination.CollectionsPerPage).map(_.map(_.deserialize))

    def checkExistsByCollectionId(collectionId: String): Future[Boolean] = filterAndExists(_.collectionId === collectionId)
  }

  object Utility {

    def checkPublicListing(publicListingID: String): Future[Unit] = {
      val notAllSold = masterNFTOwners.Service.checkAnyPublicListingSaleExists(publicListingID)

      def checkAndMarkSold(notAllSold: Boolean) = if (!notAllSold) Service.markSold(publicListingID) else Future(0)

      for {
        notAllSold <- notAllSold
        _ <- checkAndMarkSold(notAllSold)
      } yield ()

    }

  }

}