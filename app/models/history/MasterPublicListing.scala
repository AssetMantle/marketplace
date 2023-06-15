package models.history

import constants.Scheduler
import exceptions.BaseException
import models.analytics.CollectionsAnalysis
import models.history.MasterPublicListings.MasterPublicListingTable
import models.traits._
import models.{master, masterTransaction}
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

private[history] object MasterPublicListings {

  case class MasterPublicListingSerialized(id: String, collectionId: String, numberOfNFTs: Long, maxMintPerAccount: Long, price: BigDecimal, denom: String, startTimeEpoch: Long, endTimeEpoch: Long, createdBy: Option[String] = None, createdOnMillisEpoch: Option[Long] = None, updatedBy: Option[String] = None, updatedOnMillisEpoch: Option[Long] = None, deletedBy: Option[String] = None, deletedOnMillisEpoch: Option[Long] = None) extends Entity[String] {

    def deserialize()(implicit module: String, logger: Logger): MasterPublicListing = MasterPublicListing(id = id, collectionId = collectionId, numberOfNFTs = numberOfNFTs, maxMintPerAccount = maxMintPerAccount, price = MicroNumber(price), denom = denom, startTimeEpoch = startTimeEpoch, endTimeEpoch = endTimeEpoch, createdBy = createdBy, createdOnMillisEpoch = createdOnMillisEpoch, updatedBy = updatedBy, updatedOnMillisEpoch = updatedOnMillisEpoch, deletedBy = this.deletedBy, deletedOnMillisEpoch = this.deletedOnMillisEpoch)
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
}

@Singleton
class MasterPublicListings @Inject()(
                                      masterPublicListings: master.PublicListings,
                                      utilitiesOperations: utilities.Operations,
                                      masterNFTOwners: master.NFTOwners,
                                      collectionsAnalysis: CollectionsAnalysis,
                                      masterTransactionPublicListingNFTTransactions: masterTransaction.PublicListingNFTTransactions,
                                      protected val dbConfigProvider: DatabaseConfigProvider,
                                    )(implicit val executionContext: ExecutionContext)
  extends GenericDaoImpl[MasterPublicListings.MasterPublicListingTable, MasterPublicListings.MasterPublicListingSerialized, String]() {

  implicit val module: String = constants.Module.HISTORY_MASTER_PUBLIC_LISTING

  implicit val logger: Logger = Logger(this.getClass)

  val tableQuery = new TableQuery(tag => new MasterPublicListingTable(tag))

  object Service {

    def insertOrUpdate(masterPublicListing: MasterPublicListing): Future[Int] = upsert(masterPublicListing.serialize())

    def add(masterPublicListings: Seq[MasterPublicListing]): Future[Int] = create(masterPublicListings.map(_.serialize()))

    def tryGet(id: String): Future[MasterPublicListing] = filterHead(_.id === id).map(_.deserialize)

    def get(ids: Seq[String]): Future[Seq[MasterPublicListing]] = filter(_.id.inSet(ids)).map(_.map(_.deserialize))

  }

  object Utility {

    val scheduler: Scheduler = new Scheduler {
      val name: String = module

      def runner(): Unit = {
        val deletePublicListings = masterPublicListings.Service.getForDeletion

        def publicListingsWithPendingTx(ids: Seq[String]) = masterTransactionPublicListingNFTTransactions.Service.checkAnyPendingTx(ids)

        def deleteExpiredPublicListings(deletePublicListings: Seq[master.PublicListing]) = utilitiesOperations.traverse(deletePublicListings) { publicListing =>
          val addToHistory = Service.insertOrUpdate(publicListing.toHistory)

          def markPublicListingNull = masterNFTOwners.Service.markPublicListingNull(publicListing.id)

          def deletePublicListing() = masterPublicListings.Service.delete(publicListing.id)

          def updateAnalysis() = collectionsAnalysis.Utility.onPublicListingExpiry(publicListing.collectionId)

          for {
            _ <- addToHistory
            _ <- markPublicListingNull
            _ <- deletePublicListing()
            _ <- updateAnalysis()
          } yield ()

        }

        val forComplete = (for {
          deletePublicListings <- deletePublicListings
          publicListingsWithPendingTx <- publicListingsWithPendingTx(deletePublicListings.map(_.id))
          _ <- deleteExpiredPublicListings(deletePublicListings.filterNot(x => publicListingsWithPendingTx.contains(x.id)))
        } yield ()).recover {
          case _: BaseException => logger.error("FAILED_IN_" + module)
        }

        Await.result(forComplete, Duration.Inf)
      }
    }
  }

}