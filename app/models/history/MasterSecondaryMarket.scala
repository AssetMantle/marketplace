package models.history

import constants.Scheduler
import exceptions.BaseException
import models.Trait.{Entity, GenericDaoImpl, HistoryLogging, ModelTable}
import models.analytics.CollectionsAnalysis
import models.{master, masterTransaction}
import play.api.Logger
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.H2Profile.api._
import utilities.MicroNumber

import javax.inject.{Inject, Singleton}
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}

case class MasterSecondaryMarket(id: String, nftId: String, collectionId: String, price: MicroNumber, denom: String, endTimeEpoch: Long, createdBy: Option[String] = None, createdOnMillisEpoch: Option[Long] = None, updatedBy: Option[String] = None, updatedOnMillisEpoch: Option[Long] = None, deletedBy: Option[String] = None, deletedOnMillisEpoch: Option[Long] = None) extends HistoryLogging {

  def serialize(): MasterSecondaryMarkets.MasterSecondaryMarketSerialized = MasterSecondaryMarkets.MasterSecondaryMarketSerialized(
    id = this.id,
    nftId = this.nftId,
    collectionId = this.collectionId,
    price = this.price.toBigDecimal,
    denom = this.denom,
    endTimeEpoch = this.endTimeEpoch,
    createdBy = this.createdBy,
    createdOnMillisEpoch = this.createdOnMillisEpoch,
    updatedBy = this.updatedBy,
    updatedOnMillisEpoch = this.updatedOnMillisEpoch,
    deletedBy = this.deletedBy,
    deletedOnMillisEpoch = this.deletedOnMillisEpoch)

}

object MasterSecondaryMarkets {

  implicit val module: String = constants.Module.HISTORY_MASTER_SECONDARY_MARKET

  implicit val logger: Logger = Logger(this.getClass)

  case class MasterSecondaryMarketSerialized(id: String, nftId: String, collectionId: String, price: BigDecimal, denom: String, endTimeEpoch: Long, createdBy: Option[String] = None, createdOnMillisEpoch: Option[Long] = None, updatedBy: Option[String] = None, updatedOnMillisEpoch: Option[Long] = None, deletedBy: Option[String] = None, deletedOnMillisEpoch: Option[Long] = None) extends Entity[String] {

    def deserialize: MasterSecondaryMarket = MasterSecondaryMarket(id = id, nftId = nftId, collectionId = collectionId, price = MicroNumber(price), denom = denom, endTimeEpoch = endTimeEpoch, createdBy = createdBy, createdOnMillisEpoch = createdOnMillisEpoch, updatedBy = updatedBy, updatedOnMillisEpoch = updatedOnMillisEpoch, deletedBy = this.deletedBy, deletedOnMillisEpoch = this.deletedOnMillisEpoch)
  }

  class MasterSecondaryMarketTable(tag: Tag) extends Table[MasterSecondaryMarketSerialized](tag, "MasterSecondaryMarket") with ModelTable[String] {

    def * = (id, nftId, collectionId, price, denom, endTimeEpoch, createdBy.?, createdOnMillisEpoch.?, updatedBy.?, updatedOnMillisEpoch.?, deletedBy.?, deletedOnMillisEpoch.?) <> (MasterSecondaryMarketSerialized.tupled, MasterSecondaryMarketSerialized.unapply)

    def id = column[String]("id", O.PrimaryKey)

    def nftId = column[String]("nftId")

    def collectionId = column[String]("collectionId")

    def price = column[BigDecimal]("price")

    def denom = column[String]("denom")

    def endTimeEpoch = column[Long]("endTimeEpoch")

    def createdBy = column[String]("createdBy")

    def createdOnMillisEpoch = column[Long]("createdOnMillisEpoch")

    def updatedBy = column[String]("updatedBy")

    def updatedOnMillisEpoch = column[Long]("updatedOnMillisEpoch")

    def deletedBy = column[String]("deletedBy")

    def deletedOnMillisEpoch = column[Long]("deletedOnMillisEpoch")
  }

  val TableQuery = new TableQuery(tag => new MasterSecondaryMarketTable(tag))
}

@Singleton
class MasterSecondaryMarkets @Inject()(
                                        masterSecondaryMarkets: master.SecondaryMarkets,
                                        utilitiesOperations: utilities.Operations,
                                        masterNFTOwners: master.NFTOwners,
                                        collectionsAnalysis: CollectionsAnalysis,
                                        masterTransactionSecondaryMarketTransferTransactions: masterTransaction.SecondaryMarketTransferTransactions,
                                        protected val databaseConfigProvider: DatabaseConfigProvider
                                      )(implicit override val executionContext: ExecutionContext)
  extends GenericDaoImpl[MasterSecondaryMarkets.MasterSecondaryMarketTable, MasterSecondaryMarkets.MasterSecondaryMarketSerialized, String](
    databaseConfigProvider,
    MasterSecondaryMarkets.TableQuery,
    executionContext,
    MasterSecondaryMarkets.module,
    MasterSecondaryMarkets.logger
  ) {

  object Service {

    def insertOrUpdate(masterSecondaryMarket: MasterSecondaryMarket): Future[Unit] = upsert(masterSecondaryMarket.serialize())

    def add(masterSecondaryMarkets: Seq[MasterSecondaryMarket]): Future[Unit] = create(masterSecondaryMarkets.map(_.serialize()))

    def tryGet(id: String): Future[MasterSecondaryMarket] = filterHead(_.id === id).map(_.deserialize)

    def get(ids: Seq[String]): Future[Seq[MasterSecondaryMarket]] = filter(_.id.inSet(ids)).map(_.map(_.deserialize))

  }

  object Utility {

    val scheduler: Scheduler = new Scheduler {
      val name: String = MasterSecondaryMarkets.module

      def runner(): Unit = {
        val deleteSecondaryMarket = masterSecondaryMarkets.Service.getForDeletion

        def txWithPendingStatus(ids: Seq[String]) = masterTransactionSecondaryMarketTransferTransactions.Service.checkAnyPendingTx(ids)

        def deleteExpiredSecondaryMarkets(deleteSecondaryMarkets: Seq[master.SecondaryMarket]) = utilitiesOperations.traverse(deleteSecondaryMarkets) { sale =>
          val addToHistory = Service.insertOrUpdate(sale.toHistory)

          def markSecondaryMarketNull = masterNFTOwners.Service.markSecondaryMarketNull(sale.id)

          def deleteSecondaryMarket() = masterSecondaryMarkets.Service.delete(sale.id)

          for {
            _ <- addToHistory
            _ <- markSecondaryMarketNull
            _ <- deleteSecondaryMarket()
          } yield ()

        }

        val forComplete = (for {
          deleteSecondaryMarket <- deleteSecondaryMarket
          txWithPendingStatus <- txWithPendingStatus(deleteSecondaryMarket.map(_.id))
          _ <- deleteExpiredSecondaryMarkets(deleteSecondaryMarket.filterNot(x => txWithPendingStatus.contains(x.id)))
        } yield ()).recover {
          case baseException: BaseException => logger.error(baseException.failure.logMessage)
        }

        Await.result(forComplete, Duration.Inf)
      }
    }
  }

}