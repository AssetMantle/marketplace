package models.blockchainTransaction

import constants.Scheduler
import exceptions.BaseException
import models.traits._
import models.blockchain
import models.blockchain.Transaction
import play.api.Logger
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.H2Profile.api._

import javax.inject.{Inject, Singleton}
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}

case class CancelOrder(txHash: String, txRawBytes: Array[Byte], fromAddress: String, status: Option[Boolean], memo: Option[String], timeoutHeight: Int, log: Option[String], createdBy: Option[String] = None, createdOnMillisEpoch: Option[Long] = None, updatedBy: Option[String] = None, updatedOnMillisEpoch: Option[Long] = None) extends Logging with BlockchainTransaction with Entity[String] {

  def id: String = txHash
}

object CancelOrders {

  private implicit val logger: Logger = Logger(this.getClass)

  private implicit val module: String = constants.Module.BLOCKCHAIN_TRANSACTION_CANCEL_ORDER

  class CancelOrderTable(tag: Tag) extends Table[CancelOrder](tag, "CancelOrder") with ModelTable[String] {

    def * = (txHash, txRawBytes, fromAddress, status.?, memo.?, timeoutHeight, log.?, createdBy.?, createdOnMillisEpoch.?, updatedBy.?, updatedOnMillisEpoch.?) <> (CancelOrder.tupled, CancelOrder.unapply)

    def txHash = column[String]("txHash", O.PrimaryKey)

    def txRawBytes = column[Array[Byte]]("txRawBytes")

    def fromAddress = column[String]("fromAddress")

    def status = column[Boolean]("status")

    def memo = column[String]("memo")

    def timeoutHeight = column[Int]("timeoutHeight")

    def log = column[String]("log")

    def createdBy = column[String]("createdBy")

    def createdOnMillisEpoch = column[Long]("createdOnMillisEpoch")

    def updatedBy = column[String]("updatedBy")

    def updatedOnMillisEpoch = column[Long]("updatedOnMillisEpoch")

    def id = txHash
  }

  val TableQuery = new TableQuery(tag => new CancelOrderTable(tag))

}

@Singleton
class CancelOrders @Inject()(
                              protected val databaseConfigProvider: DatabaseConfigProvider,
                              blockchainTransactions: models.blockchain.Transactions,
                              blockchainBlocks: blockchain.Blocks,
                            )(implicit override val executionContext: ExecutionContext)
  extends GenericDaoImpl[CancelOrders.CancelOrderTable, CancelOrder, String](
    databaseConfigProvider,
    CancelOrders.TableQuery,
    executionContext,
    CancelOrders.module,
    CancelOrders.logger
  ) {

  object Service {

    def add(txHash: String, txRawBytes: Array[Byte], fromAddress: String, status: Option[Boolean], memo: Option[String], timeoutHeight: Int): Future[CancelOrder] = {
      val cancelOrder = CancelOrder(txHash = txHash, txRawBytes = txRawBytes, fromAddress = fromAddress, status = status, log = None, memo = memo, timeoutHeight = timeoutHeight)
      for {
        _ <- create(cancelOrder)
      } yield cancelOrder
    }

    def tryGet(txHash: String): Future[CancelOrder] = tryGetById(txHash)

    def markSuccess(txHashes: Seq[String]): Future[Int] = customUpdate(CancelOrders.TableQuery.filter(_.txHash.inSet(txHashes)).map(_.status).update(true))

    def markFailed(txHashes: Seq[String]): Future[Int] = customUpdate(CancelOrders.TableQuery.filter(_.txHash.inSet(txHashes)).map(_.status).update(false))

    def markFailedWithLog(txHashes: Seq[String], log: String): Future[Int] = customUpdate(CancelOrders.TableQuery.filter(_.txHash.inSet(txHashes)).map(x => (x.status, x.log)).update((false, log)))

    def getAllPendingStatus: Future[Seq[CancelOrder]] = filter(_.status.?.isEmpty)

  }

  object Utility {

    val scheduler: Scheduler = new Scheduler {
      val name: String = CancelOrders.module

      def runner(): Unit = {
        val cancelOrders = Service.getAllPendingStatus

        def getTransactions(hashes: Seq[String]) = blockchainTransactions.Service.getByHashes(hashes)

        def markSuccess(hashes: Seq[String]) = if (hashes.nonEmpty) Service.markSuccess(hashes) else Future(0)

        def markFailed(hashes: Seq[String]) = if (hashes.nonEmpty) Service.markFailed(hashes) else Future(0)

        def markFailedTimedOut(cancelOrders: Seq[CancelOrder], allTxs: Seq[Transaction]) = if (cancelOrders.nonEmpty) {
          val notFoundTxHashes = cancelOrders.map(_.txHash).diff(allTxs.map(_.hash))
          val timedoutFailedTxs = cancelOrders.filter(x => notFoundTxHashes.contains(x.txHash) && x.timeoutHeight > 0 && blockchainBlocks.Service.getLatestHeight > x.timeoutHeight).map(_.txHash)
          if (timedoutFailedTxs.nonEmpty) Service.markFailedWithLog(timedoutFailedTxs, constants.Response.TRANSACTION_NOT_FOUND.logMessage) else Future(0)
        } else Future(0)

        val forComplete = (for {
          cancelOrders <- cancelOrders
          txs <- getTransactions(cancelOrders.map(_.txHash))
          _ <- markSuccess(txs.filter(_.status).map(_.hash))
          _ <- markFailed(txs.filter(!_.status).map(_.hash))
          _ <- markFailedTimedOut(cancelOrders, txs)
        } yield ()).recover {
          case baseException: BaseException => logger.error(baseException.failure.logMessage)
        }

        Await.result(forComplete, Duration.Inf)
      }
    }

  }

}