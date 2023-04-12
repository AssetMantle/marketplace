package models.blockchainTransaction

import constants.Scheduler
import exceptions.BaseException
import models.blockchain
import models.blockchain.Transaction
import models.traits._
import play.api.Logger
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.H2Profile.api._

import javax.inject.{Inject, Singleton}
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}

case class Wrap(txHash: String, fromAddress: String, status: Option[Boolean], memo: Option[String], timeoutHeight: Int, log: Option[String], createdBy: Option[String] = None, createdOnMillisEpoch: Option[Long] = None, updatedBy: Option[String] = None, updatedOnMillisEpoch: Option[Long] = None) extends Logging with BlockchainTransaction with Entity[String] {

  def id: String = txHash
}

object Wraps {

  private implicit val logger: Logger = Logger(this.getClass)

  private implicit val module: String = constants.Module.BLOCKCHAIN_TRANSACTION_WRAP

  class WrapTable(tag: Tag) extends Table[Wrap](tag, "Wrap") with ModelTable[String] {

    def * = (txHash, fromAddress, status.?, memo.?, timeoutHeight, log.?, createdBy.?, createdOnMillisEpoch.?, updatedBy.?, updatedOnMillisEpoch.?) <> (Wrap.tupled, Wrap.unapply)

    def txHash = column[String]("txHash", O.PrimaryKey)

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

  val TableQuery = new TableQuery(tag => new WrapTable(tag))

}

@Singleton
class Wraps @Inject()(
                       protected val databaseConfigProvider: DatabaseConfigProvider,
                       blockchainTransactions: models.blockchain.Transactions,
                       blockchainBlocks: blockchain.Blocks,
                     )(implicit override val executionContext: ExecutionContext)
  extends GenericDaoImpl[Wraps.WrapTable, Wrap, String](
    databaseConfigProvider,
    Wraps.TableQuery,
    executionContext,
    Wraps.module,
    Wraps.logger
  ) {

  object Service {

    def add(txHash: String, fromAddress: String, status: Option[Boolean], memo: Option[String], timeoutHeight: Int): Future[Wrap] = {
      val wrap = Wrap(txHash = txHash, fromAddress = fromAddress, status = status, log = None, memo = memo, timeoutHeight = timeoutHeight)
      for {
        _ <- create(wrap)
      } yield wrap
    }

    def tryGet(txHash: String): Future[Wrap] = tryGetById(txHash)

    def markSuccess(txHashes: Seq[String]): Future[Int] = customUpdate(Wraps.TableQuery.filter(_.txHash.inSet(txHashes)).map(_.status).update(true))

    def markFailed(txHashes: Seq[String]): Future[Int] = customUpdate(Wraps.TableQuery.filter(_.txHash.inSet(txHashes)).map(_.status).update(false))

    def markFailedWithLog(txHashes: Seq[String], log: String): Future[Int] = customUpdate(Wraps.TableQuery.filter(_.txHash.inSet(txHashes)).map(x => (x.status, x.log)).update((false, log)))

    def getAllPendingStatus: Future[Seq[Wrap]] = filter(_.status.?.isEmpty)

  }

  object Utility {

    val scheduler: Scheduler = new Scheduler {
      val name: String = Wraps.module

      def runner(): Unit = {
        val wraps = Service.getAllPendingStatus

        def getTransactions(hashes: Seq[String]) = blockchainTransactions.Service.getByHashes(hashes)

        def markSuccess(hashes: Seq[String]) = if (hashes.nonEmpty) Service.markSuccess(hashes) else Future(0)

        def markFailed(hashes: Seq[String]) = if (hashes.nonEmpty) Service.markFailed(hashes) else Future(0)

        def markFailedTimedOut(wraps: Seq[Wrap], allTxs: Seq[Transaction]) = if (wraps.nonEmpty) {
          val notFoundTxHashes = wraps.map(_.txHash).diff(allTxs.map(_.hash))
          val timedoutFailedTxs = wraps.filter(x => notFoundTxHashes.contains(x.txHash) && x.timeoutHeight > 0 && blockchainBlocks.Service.getLatestHeight > x.timeoutHeight).map(_.txHash)
          if (timedoutFailedTxs.nonEmpty) Service.markFailedWithLog(timedoutFailedTxs, constants.Response.TRANSACTION_NOT_FOUND.logMessage) else Future(0)
        } else Future(0)

        val forComplete = (for {
          wraps <- wraps
          txs <- getTransactions(wraps.map(_.txHash))
          _ <- markSuccess(txs.filter(_.status).map(_.hash))
          _ <- markFailed(txs.filter(!_.status).map(_.hash))
          _ <- markFailedTimedOut(wraps, txs)
        } yield ()).recover {
          case baseException: BaseException => logger.error(baseException.failure.logMessage)
        }

        Await.result(forComplete, Duration.Inf)
      }
    }

  }

}