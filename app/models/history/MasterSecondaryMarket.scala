package models.history

import constants.Scheduler
import exceptions.BaseException
import models.analytics.CollectionsAnalysis
import models.history.MasterSecondaryMarkets.MasterSecondaryMarketTable
import models.master.{Collection, NFT}
import models.traits._
import models.{master, masterTransaction}
import play.api.Logger
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.H2Profile.api._
import utilities.MicroNumber

import javax.inject.{Inject, Singleton}
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}

case class MasterSecondaryMarket(id: String, orderId: Option[String], nftId: String, collectionId: String, sellerId: String, quantity: BigInt, price: MicroNumber, denom: String, endHours: Int, externallyMade: Boolean, completed: Boolean, cancelled: Boolean, expired: Boolean, status: Option[Boolean], createdBy: Option[String] = None, createdOnMillisEpoch: Option[Long] = None, updatedBy: Option[String] = None, updatedOnMillisEpoch: Option[Long] = None, deletedBy: Option[String] = None, deletedOnMillisEpoch: Option[Long] = None) extends HistoryLogging {

  def serialize(): MasterSecondaryMarkets.MasterSecondaryMarketSerialized = MasterSecondaryMarkets.MasterSecondaryMarketSerialized(
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
    updatedOnMillisEpoch = this.updatedOnMillisEpoch,
    deletedBy = this.deletedBy,
    deletedOnMillisEpoch = this.deletedOnMillisEpoch)

}

private[history] object MasterSecondaryMarkets {

  case class MasterSecondaryMarketSerialized(id: String, orderId: Option[String], nftId: String, collectionId: String, sellerId: String, quantity: BigDecimal, price: BigDecimal, denom: String, endHours: Int, externallyMade: Boolean, completed: Boolean, cancelled: Boolean, expired: Boolean, status: Option[Boolean], createdBy: Option[String] = None, createdOnMillisEpoch: Option[Long] = None, updatedBy: Option[String] = None, updatedOnMillisEpoch: Option[Long] = None, deletedBy: Option[String] = None, deletedOnMillisEpoch: Option[Long] = None) extends Entity[String] {

    def deserialize()(implicit module: String, logger: Logger): MasterSecondaryMarket = MasterSecondaryMarket(id = id, orderId = orderId, nftId = nftId, collectionId = collectionId, sellerId = sellerId, quantity = this.quantity.toBigInt, price = MicroNumber(price), denom = denom, endHours = endHours, externallyMade = externallyMade, completed = completed, expired = expired, status = status, cancelled = cancelled, createdBy = createdBy, createdOnMillisEpoch = createdOnMillisEpoch, updatedBy = updatedBy, updatedOnMillisEpoch = updatedOnMillisEpoch, deletedBy = this.deletedBy, deletedOnMillisEpoch = this.deletedOnMillisEpoch)
  }

  class MasterSecondaryMarketTable(tag: Tag) extends Table[MasterSecondaryMarketSerialized](tag, "MasterSecondaryMarket") with ModelTable[String] {

    def * = (id, orderId.?, nftId, collectionId, sellerId, quantity, price, denom, endHours, externallyMade, completed, cancelled, expired, status.?, createdBy.?, createdOnMillisEpoch.?, updatedBy.?, updatedOnMillisEpoch.?, deletedBy.?, deletedOnMillisEpoch.?) <> (MasterSecondaryMarketSerialized.tupled, MasterSecondaryMarketSerialized.unapply)

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

    def deletedBy = column[String]("deletedBy")

    def deletedOnMillisEpoch = column[Long]("deletedOnMillisEpoch")
  }

}

@Singleton
class MasterSecondaryMarkets @Inject()(
                                        masterCollections: master.Collections,
                                        masterSecondaryMarkets: master.SecondaryMarkets,
                                        utilitiesOperations: utilities.Operations,
                                        masterNFTs: master.NFTs,
                                        masterNFTOwners: master.NFTOwners,
                                        collectionsAnalysis: CollectionsAnalysis,
                                        masterTransactionMakeOrderTransactions: masterTransaction.MakeOrderTransactions,
                                        masterTransactionTakeOrderTransactions: masterTransaction.TakeOrderTransactions,
                                        protected val dbConfigProvider: DatabaseConfigProvider,
                                      )(implicit val executionContext: ExecutionContext)
  extends GenericDaoImpl[MasterSecondaryMarkets.MasterSecondaryMarketTable, MasterSecondaryMarkets.MasterSecondaryMarketSerialized, String]() {

  implicit val module: String = constants.Module.HISTORY_MASTER_SECONDARY_MARKET

  implicit val logger: Logger = Logger(this.getClass)

  val tableQuery = new TableQuery(tag => new MasterSecondaryMarketTable(tag))

  object Service {

    def insertOrUpdate(masterSecondaryMarket: MasterSecondaryMarket): Future[Int] = upsert(masterSecondaryMarket.serialize())

    def add(masterSecondaryMarkets: Seq[MasterSecondaryMarket]): Future[Int] = create(masterSecondaryMarkets.map(_.serialize()))

    def tryGet(id: String): Future[MasterSecondaryMarket] = filterHead(_.id === id).map(_.deserialize)

    def get(ids: Seq[String]): Future[Seq[MasterSecondaryMarket]] = filter(_.id.inSet(ids)).map(_.map(_.deserialize))

  }

  object Utility {

    private def checkAndUpdateCollection(collectionId: String) = {
      val checkCollectionExists = masterSecondaryMarkets.Service.existByCollectionId(collectionId)

      for {
        checkCollectionExists <- checkCollectionExists
        _ <- if (!checkCollectionExists) masterCollections.Service.removeFromListedOnSecondaryMarket(collectionId) else Future(0)
      } yield ()
    }

    private def deleteSecondaryMarket(secondaryMarket: master.SecondaryMarket) = {
      val addToHistory = Service.insertOrUpdate(secondaryMarket.toHistory)

      def deleteSecondaryMarket() = masterSecondaryMarkets.Service.delete(secondaryMarket.id)

      for {
        _ <- addToHistory
        _ <- deleteSecondaryMarket()
        _ <- checkAndUpdateCollection(secondaryMarket.collectionId)
      } yield ()
    }

    private def removeFromSecondaryMarket(secondaryMarket: master.SecondaryMarket) = {
      val addToHistory = Service.insertOrUpdate(secondaryMarket.toHistory)
      val nft = masterNFTs.Service.tryGet(secondaryMarket.nftId)
      val collection = masterCollections.Service.tryGet(secondaryMarket.collectionId)

      def unlistSecondaryMarket(nft: NFT, collection: Collection) = masterNFTOwners.Service.unlistFromSecondaryMarket(nft = nft, collection = collection, ownerId = secondaryMarket.sellerId, sellQuantity = secondaryMarket.quantity)

      def deleteSecondaryMarket() = masterSecondaryMarkets.Service.delete(secondaryMarket.id)

      for {
        _ <- addToHistory
        nft <- nft
        collection <- collection
        _ <- unlistSecondaryMarket(nft, collection)
        _ <- deleteSecondaryMarket()
        _ <- checkAndUpdateCollection(secondaryMarket.collectionId)
      } yield ()
    }

    val scheduler: Scheduler = new Scheduler {
      val name: String = module

      def runner(): Unit = {
        val completedOrders = masterSecondaryMarkets.Service.getCompletedOrders
        val cancelledOrders = masterSecondaryMarkets.Service.getCancelledOrders
        val expiredOrders = masterSecondaryMarkets.Service.getExpiredOrders
        val failedOrders = masterSecondaryMarkets.Service.getFailedOrders

        def makeOrderTxWithPendingStatus(ids: Seq[String]) = masterTransactionMakeOrderTransactions.Service.checkAnyPendingTx(ids)

        def takeOrderTxWithPendingStatus(ids: Seq[String]) = masterTransactionTakeOrderTransactions.Service.checkAnyPendingTx(ids)

        def removeOrdersFromMarket(txWithPendingStatus: Seq[String], unfilteredDeleteSecondaryMarkets: Seq[master.SecondaryMarket]) = utilitiesOperations.traverse(unfilteredDeleteSecondaryMarkets.filterNot(x => txWithPendingStatus.contains(x.id))) { secondaryMarket =>
          removeFromSecondaryMarket(secondaryMarket)
        }

        def deleteOrders(txWithPendingStatus: Seq[String], unfilteredDeleteSecondaryMarkets: Seq[master.SecondaryMarket]) = utilitiesOperations.traverse(unfilteredDeleteSecondaryMarkets.filterNot(x => txWithPendingStatus.contains(x.id))) { secondaryMarket =>
          deleteSecondaryMarket(secondaryMarket)
        }

        val forComplete = (for {
          completedOrders <- completedOrders
          cancelledOrders <- cancelledOrders
          expiredOrders <- expiredOrders
          failedOrders <- failedOrders
          makeOrderTxWithPendingStatus <- makeOrderTxWithPendingStatus(completedOrders.map(_.id) ++ cancelledOrders.map(_.id) ++ expiredOrders.map(_.id) ++ failedOrders.map(_.id))
          takeOrderTxWithPendingStatus <- takeOrderTxWithPendingStatus(completedOrders.map(_.id) ++ cancelledOrders.map(_.id) ++ expiredOrders.map(_.id) ++ failedOrders.map(_.id))
          _ <- removeOrdersFromMarket(makeOrderTxWithPendingStatus ++ takeOrderTxWithPendingStatus, cancelledOrders ++ expiredOrders)
          _ <- deleteOrders(makeOrderTxWithPendingStatus ++ takeOrderTxWithPendingStatus, completedOrders ++ failedOrders)
        } yield ()).recover {
          case _: BaseException => logger.error("FAILED_IN_" + module)
        }

        Await.result(forComplete, Duration.Inf)
      }
    }
  }

}