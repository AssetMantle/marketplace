package models.history

import akka.actor.Cancellable
import exceptions.BaseException
import models.Trait.{Entity, GenericDaoImpl, HistoryLogging, ModelTable}
import models.analytics.CollectionsAnalysis
import models.master
import play.api.Logger
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.H2Profile.api._
import utilities.MicroNumber

import javax.inject.{Inject, Singleton}
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}

case class MasterPublicListing(id: String, collectionId: String, numberOfNFTs: Long, maxMintPerAccount: Long, price: MicroNumber, denom: String, startTimeEpoch: Long, endTimeEpoch: Long, createdBy: Option[String] = None, createdOnMillisEpoch: Option[Long] = None, updatedBy: Option[String] = None, updatedOnMillisEpoch: Option[Long] = None, deletedBy: Option[String] = None, deletedOnMillisEpoch: Option[Long] = None) extends HistoryLogging {

  def serialize(): MasterPublicListings.MasterPublicListingSerialized = MasterPublicListings.MasterPublicListingSerialized(
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
    updatedOnMillisEpoch = this.updatedOnMillisEpoch,
    deletedBy = this.deletedBy,
    deletedOnMillisEpoch = this.deletedOnMillisEpoch)

}

object MasterPublicListings {

  implicit val module: String = constants.Module.HISTORY_MASTER_PUBLIC_LISTING

  implicit val logger: Logger = Logger(this.getClass)

  case class MasterPublicListingSerialized(id: String, collectionId: String, numberOfNFTs: Long, maxMintPerAccount: Long, price: BigDecimal, denom: String, startTimeEpoch: Long, endTimeEpoch: Long, createdBy: Option[String] = None, createdOnMillisEpoch: Option[Long] = None, updatedBy: Option[String] = None, updatedOnMillisEpoch: Option[Long] = None, deletedBy: Option[String] = None, deletedOnMillisEpoch: Option[Long] = None) extends Entity[String] {

    def deserialize: MasterPublicListing = MasterPublicListing(id = id, collectionId = collectionId, numberOfNFTs = numberOfNFTs, maxMintPerAccount = maxMintPerAccount, price = MicroNumber(price), denom = denom, startTimeEpoch = startTimeEpoch, endTimeEpoch = endTimeEpoch, createdBy = createdBy, createdOnMillisEpoch = createdOnMillisEpoch, updatedBy = updatedBy, updatedOnMillisEpoch = updatedOnMillisEpoch, deletedBy = this.deletedBy, deletedOnMillisEpoch = this.deletedOnMillisEpoch)
  }

  class MasterPublicListingTable(tag: Tag) extends Table[MasterPublicListingSerialized](tag, "MasterPublicListing") with ModelTable[String] {

    def * = (id, collectionId, numberOfNFTs, maxMintPerAccount, price, denom, startTimeEpoch, endTimeEpoch, createdBy.?, createdOnMillisEpoch.?, updatedBy.?, updatedOnMillisEpoch.?, deletedBy.?, deletedOnMillisEpoch.?) <> (MasterPublicListingSerialized.tupled, MasterPublicListingSerialized.unapply)

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

    def deletedBy = column[String]("deletedBy")

    def deletedOnMillisEpoch = column[Long]("deletedOnMillisEpoch")
  }

  val TableQuery = new TableQuery(tag => new MasterPublicListingTable(tag))
}

@Singleton
class MasterPublicListings @Inject()(
                                      masterPublicListings: master.PublicListings,
                                      utilitiesOperations: utilities.Operations,
                                      masterNFTOwners: master.NFTOwners,
                                      collectionsAnalysis: CollectionsAnalysis,
                                      protected val databaseConfigProvider: DatabaseConfigProvider
                                    )(implicit override val executionContext: ExecutionContext)
  extends GenericDaoImpl[MasterPublicListings.MasterPublicListingTable, MasterPublicListings.MasterPublicListingSerialized, String](
    databaseConfigProvider,
    MasterPublicListings.TableQuery,
    executionContext,
    MasterPublicListings.module,
    MasterPublicListings.logger
  ) {

  object Service {

    def add(masterPublicListing: MasterPublicListing): Future[String] = create(masterPublicListing.serialize())

    def add(masterPublicListings: Seq[MasterPublicListing]): Future[Unit] = create(masterPublicListings.map(_.serialize()))

    def tryGet(id: String): Future[MasterPublicListing] = filterHead(_.id === id).map(_.deserialize)

    def get(ids: Seq[String]): Future[Seq[MasterPublicListing]] = filter(_.id.inSet(ids)).map(_.map(_.deserialize))

  }

  object Utility {
    private val schedulerExecutionContext: ExecutionContext = actors.Service.actorSystem.dispatchers.lookup("akka.actor.scheduler-dispatcher")

    private val historySchedulerRunnable = new Runnable {
      def run(): Unit = {
        val expiredPublicListings = masterPublicListings.Service.getExpiredPublicListings

        def deleteExpiredPublicListings(expiredPublicListings: Seq[master.PublicListing]) = utilitiesOperations.traverse(expiredPublicListings) { expiredPublicListing =>
          val addToHistory = Service.add(expiredPublicListing.toHistory)

          def markPublicListingNull = masterNFTOwners.Service.markPublicListingNull(expiredPublicListing.id)

          def deletePublicListing() = masterPublicListings.Service.delete(expiredPublicListing.id)

          def updateAnalysis() = collectionsAnalysis.Utility.onPublicListingExpiry(expiredPublicListing.collectionId)

          for {
            _ <- addToHistory
            _ <- markPublicListingNull
            _ <- deletePublicListing()
            _ <- updateAnalysis()
          } yield ()

        }

        val forComplete = (for {
          expiredPublicListings <- expiredPublicListings
          _ <- deleteExpiredPublicListings(expiredPublicListings)
        } yield ()).recover {
          case baseException: BaseException => logger.error(baseException.failure.logMessage)
        }

        Await.result(forComplete, Duration.Inf)
      }
    }

    def start: Cancellable = actors.Service.actorSystem.scheduler.scheduleWithFixedDelay(initialDelay = constants.Scheduler.InitialDelay, delay = constants.Scheduler.FixedDelay)(historySchedulerRunnable)(schedulerExecutionContext)
  }

}