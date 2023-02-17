package models.analytics

import models.Trait.{Entity, GenericDaoImpl, Logging, ModelTable}
import models.master._
import play.api.Logger
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.H2Profile.api._
import utilities.MicroNumber

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

case class CollectionAnalysis(id: String, totalNFTs: Long, totalMinted: Long, totalSold: Long, totalTraded: Long, floorPrice: MicroNumber, salePrice: MicroNumber, publicListingPrice: MicroNumber, totalVolumeTraded: MicroNumber, bestOffer: MicroNumber, listed: Long, owners: Long, uniqueOwners: Long, createdBy: Option[String] = None, createdOnMillisEpoch: Option[Long] = None, updatedBy: Option[String] = None, updatedOnMillisEpoch: Option[Long] = None) extends Entity[String] with Logging {

  def serialize: CollectionsAnalysis.CollectionAnalysisSerialized = CollectionsAnalysis.CollectionAnalysisSerialized(
    id = this.id,
    totalNFTs = this.totalNFTs,
    totalMinted = this.totalMinted,
    totalSold = this.totalSold,
    totalTraded = this.totalTraded,
    floorPrice = this.floorPrice.toBigDecimal,
    salePrice = this.salePrice.toBigDecimal,
    publicListingPrice = this.publicListingPrice.toBigDecimal,
    totalVolumeTraded = this.totalVolumeTraded.toBigDecimal,
    bestOffer = this.bestOffer.toBigDecimal,
    listed = this.listed,
    owners = this.owners,
    uniqueOwners = this.uniqueOwners,
    createdBy = this.createdBy,
    createdOnMillisEpoch = this.createdOnMillisEpoch,
    updatedBy = this.updatedBy,
    updatedOnMillisEpoch = this.updatedOnMillisEpoch
  )

}

object CollectionsAnalysis {

  case class CollectionAnalysisSerialized(id: String, totalNFTs: Long, totalMinted: Long, totalSold: Long, totalTraded: Long, floorPrice: BigDecimal, salePrice: BigDecimal, publicListingPrice: BigDecimal, totalVolumeTraded: BigDecimal, bestOffer: BigDecimal, listed: Long, owners: Long, uniqueOwners: Long, createdBy: Option[String] = None, createdOnMillisEpoch: Option[Long] = None, updatedBy: Option[String] = None, updatedOnMillisEpoch: Option[Long] = None) extends Entity[String] with Logging {

    def deserialize: CollectionAnalysis = CollectionAnalysis(
      id = this.id,
      totalNFTs = this.totalNFTs,
      totalMinted = this.totalMinted,
      totalSold = this.totalSold,
      totalTraded = this.totalTraded,
      floorPrice = MicroNumber(this.floorPrice),
      salePrice = MicroNumber(this.salePrice),
      publicListingPrice = MicroNumber(this.publicListingPrice),
      totalVolumeTraded = MicroNumber(this.totalVolumeTraded),
      bestOffer = MicroNumber(this.bestOffer),
      listed = this.listed,
      owners = this.owners,
      uniqueOwners = this.uniqueOwners,
      createdBy = this.createdBy,
      createdOnMillisEpoch = this.createdOnMillisEpoch,
      updatedBy = this.updatedBy,
      updatedOnMillisEpoch = this.updatedOnMillisEpoch
    )

  }

  implicit val module: String = constants.Module.ANALYTICS_COLLECTION

  implicit val logger: Logger = Logger(this.getClass)

  class CollectionAnalysisTable(tag: Tag) extends Table[CollectionAnalysisSerialized](tag, "CollectionAnalysis") with ModelTable[String] {

    def * = (id, totalNFTs, totalMinted, totalSold, totalTraded, floorPrice, salePrice, publicListingPrice, totalVolumeTraded, bestOffer, listed, owners, uniqueOwners, createdBy.?, createdOnMillisEpoch.?, updatedBy.?, updatedOnMillisEpoch.?) <> (CollectionAnalysisSerialized.tupled, CollectionAnalysisSerialized.unapply)

    def id = column[String]("id", O.PrimaryKey)

    def totalNFTs = column[Long]("totalNFTs")

    def totalMinted = column[Long]("totalMinted")

    def totalSold = column[Long]("totalSold")

    def totalTraded = column[Long]("totalTraded")

    def floorPrice = column[BigDecimal]("floorPrice")

    def salePrice = column[BigDecimal]("salePrice")

    def publicListingPrice = column[BigDecimal]("publicListingPrice")

    def totalVolumeTraded = column[BigDecimal]("totalVolumeTraded")

    def bestOffer = column[BigDecimal]("bestOffer")

    def listed = column[Long]("listed")

    def owners = column[Long]("owners")

    def uniqueOwners = column[Long]("uniqueOwners")

    def createdBy = column[String]("createdBy")

    def createdOnMillisEpoch = column[Long]("createdOnMillisEpoch")

    def updatedBy = column[String]("updatedBy")

    def updatedOnMillisEpoch = column[Long]("updatedOnMillisEpoch")

  }

  val TableQuery = new TableQuery(tag => new CollectionAnalysisTable(tag))
}

@Singleton
class CollectionsAnalysis @Inject()(
                                     masterCollections: Collections,
                                     masterNFTs: NFTs,
                                     protected val databaseConfigProvider: DatabaseConfigProvider
                                   )(implicit override val executionContext: ExecutionContext)
  extends GenericDaoImpl[CollectionsAnalysis.CollectionAnalysisTable, CollectionsAnalysis.CollectionAnalysisSerialized, String](
    databaseConfigProvider,
    CollectionsAnalysis.TableQuery,
    executionContext,
    CollectionsAnalysis.module,
    CollectionsAnalysis.logger
  ) {

  object Service {

    def add(collectionAnalysis: CollectionAnalysis): Future[String] = create(collectionAnalysis.serialize)

    def add(collectionsAnalysis: Seq[CollectionAnalysis]): Future[Unit] = create(collectionsAnalysis.map(_.serialize))

    def tryGet(id: String): Future[CollectionAnalysis] = filterHead(_.id === id).map(_.deserialize)

    def update(collectionAnalysis: CollectionAnalysis): Future[Unit] = updateById(collectionAnalysis.serialize)

    def delete(ids: Seq[String]): Future[Int] = filterAndDelete(_.id.inSet(ids))
  }

  object Utility {

    def onNewCollection(id: String): Future[String] = Service.add(CollectionAnalysis(id = id, totalNFTs = 0, totalSold = 0, totalTraded = 0, floorPrice = 0, salePrice = 0, publicListingPrice = 0, totalVolumeTraded = 0, bestOffer = 0, listed = 0, owners = 0, uniqueOwners = 0, totalMinted = 0))

    def onNewNFT(collectionId: String): Future[Unit] = {
      val collectionAnalysis = Service.tryGet(collectionId)

      for {
        collectionAnalysis <- collectionAnalysis
        _ <- Service.update(collectionAnalysis.copy(totalNFTs = collectionAnalysis.totalNFTs + 1))
      } yield ()
    }

    def onCreateSale(collectionId: String, totalListed: Long, salePrice: MicroNumber): Future[Unit] = {
      val collectionAnalysis = Service.tryGet(collectionId)

      for {
        collectionAnalysis <- collectionAnalysis
        _ <- Service.update(collectionAnalysis.copy(listed = totalListed, salePrice = salePrice))
      } yield ()
    }

    def onCreatePublicListing(collectionId: String, totalListed: Long, listingPrice: MicroNumber): Future[Unit] = {
      val collectionAnalysis = Service.tryGet(collectionId)

      for {
        collectionAnalysis <- collectionAnalysis
        _ <- Service.update(collectionAnalysis.copy(listed = collectionAnalysis.listed + totalListed, publicListingPrice = listingPrice))
      } yield ()
    }

    def onCreateSecondaryMarket(collectionId: String, totalListed: Int, listingPrice: MicroNumber): Future[Unit] = {
      val collectionAnalysis = Service.tryGet(collectionId)

      def update(collectionAnalysis: CollectionAnalysis) = {
        val oldFloorPrice = Seq(collectionAnalysis.salePrice, collectionAnalysis.publicListingPrice, collectionAnalysis.floorPrice).min
        val newFloorPrice = if (oldFloorPrice > MicroNumber.zero) Seq(listingPrice, oldFloorPrice).min else listingPrice
        Service.update(collectionAnalysis.copy(listed = collectionAnalysis.listed + totalListed, floorPrice = newFloorPrice))
      }

      for {
        collectionAnalysis <- collectionAnalysis
        _ <- update(collectionAnalysis)
      } yield ()
    }

    def onEditPublicListing(collectionId: String, listingPrice: MicroNumber): Future[Unit] = {
      val collectionAnalysis = Service.tryGet(collectionId)

      for {
        collectionAnalysis <- collectionAnalysis
        _ <- Service.update(collectionAnalysis.copy(publicListingPrice = listingPrice))
      } yield ()
    }

    def onSuccessfulSell(collectionId: String, price: MicroNumber, quantity: Int): Future[Unit] = {
      val collectionAnalysis = Service.tryGet(collectionId)

      for {
        collectionAnalysis <- collectionAnalysis
        _ <- Service.update(collectionAnalysis.copy(listed = collectionAnalysis.listed - 1, totalTraded = collectionAnalysis.totalTraded + quantity, totalSold = collectionAnalysis.totalSold + quantity, totalVolumeTraded = collectionAnalysis.totalVolumeTraded + (price * quantity)))
      } yield ()
    }

    def onMint(collectionId: String): Future[Unit] = {
      val collectionAnalysis = Service.tryGet(collectionId)

      for {
        collectionAnalysis <- collectionAnalysis
        _ <- Service.update(collectionAnalysis.copy(totalMinted = collectionAnalysis.totalMinted + 1))
      } yield ()
    }

    def onMintAndSale(collectionId: String, price: MicroNumber): Future[Unit] = {
      val collectionAnalysis = Service.tryGet(collectionId)

      for {
        collectionAnalysis <- collectionAnalysis
        _ <- Service.update(collectionAnalysis.copy(totalMinted = collectionAnalysis.totalMinted + 1, listed = collectionAnalysis.listed - 1, totalTraded = collectionAnalysis.totalTraded, totalVolumeTraded = collectionAnalysis.totalVolumeTraded + price))
      } yield ()
    }

    def onSaleExpiry(collectionId: String): Future[Unit] = {
      val collectionAnalysis = Service.tryGet(collectionId)

      for {
        collectionAnalysis <- collectionAnalysis
        _ <- Service.update(collectionAnalysis.copy(salePrice = MicroNumber.zero))
      } yield ()
    }

    def onPublicListingExpiry(collectionId: String): Future[Unit] = {
      val collectionAnalysis = Service.tryGet(collectionId)

      for {
        collectionAnalysis <- collectionAnalysis
        _ <- Service.update(collectionAnalysis.copy(publicListingPrice = MicroNumber.zero))
      } yield ()
    }

  }
}