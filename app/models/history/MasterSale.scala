package models.history

import constants.Scheduler
import exceptions.BaseException
import models.Trait.{Entity, GenericDaoImpl, HistoryLogging, ModelTable}
import models.analytics.CollectionsAnalysis
import models.master
import models.master.Sales
import play.api.Logger
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.H2Profile.api._
import utilities.MicroNumber

import javax.inject.{Inject, Singleton}
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}

case class MasterSale(id: String, whitelistId: String, collectionId: String, numberOfNFTs: Long, maxMintPerAccount: Long, price: MicroNumber, denom: String, startTimeEpoch: Long, endTimeEpoch: Long, createdBy: Option[String] = None, createdOnMillisEpoch: Option[Long] = None, updatedBy: Option[String] = None, updatedOnMillisEpoch: Option[Long] = None, deletedBy: Option[String] = None, deletedOnMillisEpoch: Option[Long] = None) extends HistoryLogging {

  def serialize(): MasterSales.MasterSaleSerialized = MasterSales.MasterSaleSerialized(
    id = this.id,
    whitelistId = this.whitelistId,
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

object MasterSales {

  implicit val module: String = constants.Module.HISTORY_MASTER_SALE

  implicit val logger: Logger = Logger(this.getClass)

  case class MasterSaleSerialized(id: String, whitelistId: String, collectionId: String, numberOfNFTs: Long, maxMintPerAccount: Long, price: BigDecimal, denom: String, startTimeEpoch: Long, endTimeEpoch: Long, createdBy: Option[String] = None, createdOnMillisEpoch: Option[Long] = None, updatedBy: Option[String] = None, updatedOnMillisEpoch: Option[Long] = None, deletedBy: Option[String] = None, deletedOnMillisEpoch: Option[Long] = None) extends Entity[String] {

    def deserialize: MasterSale = MasterSale(id = id, whitelistId = whitelistId, collectionId = collectionId, numberOfNFTs = numberOfNFTs, maxMintPerAccount = maxMintPerAccount, price = MicroNumber(price), denom = denom, startTimeEpoch = startTimeEpoch, endTimeEpoch = endTimeEpoch, createdBy = createdBy, createdOnMillisEpoch = createdOnMillisEpoch, updatedBy = updatedBy, updatedOnMillisEpoch = updatedOnMillisEpoch, deletedBy = this.deletedBy, deletedOnMillisEpoch = this.deletedOnMillisEpoch)
  }

  class MasterSaleTable(tag: Tag) extends Table[MasterSaleSerialized](tag, "MasterSale") with ModelTable[String] {

    def * = (id, whitelistId, collectionId, numberOfNFTs, maxMintPerAccount, price, denom, startTimeEpoch, endTimeEpoch, createdBy.?, createdOnMillisEpoch.?, updatedBy.?, updatedOnMillisEpoch.?, deletedBy.?, deletedOnMillisEpoch.?) <> (MasterSaleSerialized.tupled, MasterSaleSerialized.unapply)

    def id = column[String]("id", O.PrimaryKey)

    def whitelistId = column[String]("whitelistId")

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

  val TableQuery = new TableQuery(tag => new MasterSaleTable(tag))
}

@Singleton
class MasterSales @Inject()(
                             sales: Sales,
                             utilitiesOperations: utilities.Operations,
                             masterNFTOwners: master.NFTOwners,
                             collectionsAnalysis: CollectionsAnalysis,
                             protected val databaseConfigProvider: DatabaseConfigProvider
                           )(implicit override val executionContext: ExecutionContext)
  extends GenericDaoImpl[MasterSales.MasterSaleTable, MasterSales.MasterSaleSerialized, String](
    databaseConfigProvider,
    MasterSales.TableQuery,
    executionContext,
    MasterSales.module,
    MasterSales.logger
  ) {

  object Service {

    def add(masterSale: MasterSale): Future[String] = create(masterSale.serialize())

    def add(masterSales: Seq[MasterSale]): Future[Unit] = create(masterSales.map(_.serialize()))

    def tryGet(id: String): Future[MasterSale] = filterHead(_.id === id).map(_.deserialize)

    def get(ids: Seq[String]): Future[Seq[MasterSale]] = filter(_.id.inSet(ids)).map(_.map(_.deserialize))

    def getByWhitelistId(whitelistId: String): Future[Seq[MasterSale]] = filter(_.whitelistId === whitelistId).map(_.map(_.deserialize))

    def tryGetWhitelistId(id: String): Future[String] = filterHead(_.id === id).map(_.whitelistId)

  }

  object Utility {

    val scheduler: Scheduler = new Scheduler {
      val name: String = constants.Scheduler.HISTORY_MASTER_SALE

      def runner(): Unit = {
        val expiredSales = sales.Service.getExpiredSales

        def deleteExpiredSales(expiredSales: Seq[master.Sale]) = utilitiesOperations.traverse(expiredSales) { expiredSale =>
          val addToHistory = Service.add(expiredSale.toHistory)

          def markSaleNull = masterNFTOwners.Service.markSaleNull(expiredSale.id)

          def deleteSale() = sales.Service.delete(expiredSale.id)

          def updateAnalysis() = collectionsAnalysis.Utility.onSaleExpiry(expiredSale.collectionId)

          for {
            _ <- addToHistory
            _ <- markSaleNull
            _ <- deleteSale()
            _ <- updateAnalysis()
          } yield ()

        }

        val forComplete = (for {
          expiredSales <- expiredSales
          _ <- deleteExpiredSales(expiredSales)
        } yield ()).recover {
          case baseException: BaseException => logger.error(baseException.failure.logMessage)
        }

        Await.result(forComplete, Duration.Inf)
      }
    }
  }

}