package models.analytics

import models.Trait.{Entity, GenericDaoImpl, Logging, ModelTable}
import play.api.Logger
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.H2Profile.api._

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

case class CollectionAnalysis(id: String, totalNFTs: Long, totalSold: Long, totalTraded: Long, floorPrice: BigDecimal, totalVolume: BigDecimal, bestOffer: BigDecimal, listed: Long, owners: Long, uniqueOwners: Long, createdBy: Option[String] = None, createdOnMillisEpoch: Option[Long] = None, updatedBy: Option[String] = None, updatedOnMillisEpoch: Option[Long] = None) extends Entity[String] with Logging

object CollectionsAnalysis {

  implicit val module: String = constants.Module.MASTER_NFT_WHITELIST_SALE

  implicit val logger: Logger = Logger(this.getClass)

  class CollectionAnalysisTable(tag: Tag) extends Table[CollectionAnalysis](tag, "CollectionAnalysis") with ModelTable[String] {

    def * = (id, totalNFTs, totalSold, totalTraded, floorPrice, totalVolume, bestOffer, listed, owners, uniqueOwners, createdBy.?, createdOnMillisEpoch.?, updatedBy.?, updatedOnMillisEpoch.?) <> (CollectionAnalysis.tupled, CollectionAnalysis.unapply)

    def id = column[String]("id", O.PrimaryKey)

    def totalNFTs = column[Long]("totalNFTs")

    def totalSold = column[Long]("totalSold")

    def totalTraded = column[Long]("totalTraded")

    def floorPrice = column[BigDecimal]("floorPrice")

    def totalVolume = column[BigDecimal]("totalVolume")

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
                                     protected val databaseConfigProvider: DatabaseConfigProvider
                                   )(implicit override val executionContext: ExecutionContext)
  extends GenericDaoImpl[CollectionsAnalysis.CollectionAnalysisTable, CollectionAnalysis, String](
    databaseConfigProvider,
    CollectionsAnalysis.TableQuery,
    executionContext,
    CollectionsAnalysis.module,
    CollectionsAnalysis.logger
  ) {

  object Service {

    def add(collectionAnalysis: CollectionAnalysis): Future[String] = create(collectionAnalysis)

    def add(collectionsAnalysis: Seq[CollectionAnalysis]): Future[Unit] = create(collectionsAnalysis)

    def tryGet(id: String): Future[CollectionAnalysis] = filterHead(_.id === id)

    def getForCollections(ids: Seq[String]): Future[Seq[CollectionAnalysis]] = filter(_.id.inSet(ids))

  }

  object Utility {

    def onNewCollection(id: String): Future[Unit] = {
      Future()
    }

    def onNewNFT(id: String): Future[Unit] = {
      Future()
    }

  }
}