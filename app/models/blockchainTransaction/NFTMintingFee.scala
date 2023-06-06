package models.blockchainTransaction

import constants.Scheduler
import exceptions.BaseException
import models.blockchain
import models.blockchain.Transaction
import models.common.Coin
import models.traits._
import play.api.Logger
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.json.Json
import slick.jdbc.H2Profile.api._

import javax.inject.{Inject, Singleton}
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}

case class NFTMintingFee(txHash: String, fromAddress: String, status: Option[Boolean], memo: Option[String], timeoutHeight: Int, log: Option[String], createdBy: Option[String] = None, createdOnMillisEpoch: Option[Long] = None, updatedBy: Option[String] = None, updatedOnMillisEpoch: Option[Long] = None) extends Logging with BlockchainTransaction with Entity[String]{
  def id: String = txHash
}
object NFTMintingFees {

  private implicit val logger: Logger = Logger(this.getClass)

  private implicit val module: String = constants.Module.BLOCKCHAIN_TRANSACTION_NFT_MINTING_FEE

  class NFTMintingFeeTable(tag: Tag) extends Table[NFTMintingFee](tag, "NFTMintingFee") with ModelTable[String] {

    def * = (txHash, fromAddress, status.?, memo.?, timeoutHeight, log.?, createdBy.?, createdOnMillisEpoch.?, updatedBy.?, updatedOnMillisEpoch.?) <> (NFTMintingFee.tupled, NFTMintingFee.unapply)

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

  val TableQuery = new TableQuery(tag => new NFTMintingFeeTable(tag))

}

@Singleton
class NFTMintingFees @Inject()(
                                protected val databaseConfigProvider: DatabaseConfigProvider,
                                blockchainTransactions: models.blockchain.Transactions,
                                blockchainBlocks: blockchain.Blocks,
                              )(implicit override val executionContext: ExecutionContext)
  extends GenericDaoImpl[NFTMintingFees.NFTMintingFeeTable, NFTMintingFee, String](
    databaseConfigProvider,
    NFTMintingFees.TableQuery,
    executionContext,
    NFTMintingFees.module,
    NFTMintingFees.logger
  ) {

  object Service {

    def add(txHash: String, fromAddress: String, memo: Option[String], timeoutHeight: Int): Future[NFTMintingFee] = {
      val nftMintingFee = NFTMintingFee(txHash = txHash, fromAddress = fromAddress, status = None, log = None, memo = memo, timeoutHeight = timeoutHeight)
      for {
        _ <- create(nftMintingFee)
      } yield nftMintingFee
    }

    def tryGet(txHash: String): Future[NFTMintingFee] = tryGetById(txHash)

    def updateNFTMintingFee(nftMintingFee: NFTMintingFee): Future[NFTMintingFee] = {
      for {
        _ <- updateById(nftMintingFee)
      } yield nftMintingFee
    }

    def markSuccess(txHashes: Seq[String]): Future[Int] = customUpdate(NFTMintingFees.TableQuery.filter(_.txHash.inSet(txHashes)).map(_.status).update(true))

    def markFailed(txHashes: Seq[String]): Future[Int] = customUpdate(NFTMintingFees.TableQuery.filter(_.txHash.inSet(txHashes)).map(_.status).update(false))

    def markFailedWithLog(txHashes: Seq[String], log: String): Future[Int] = customUpdate(NFTMintingFees.TableQuery.filter(_.txHash.inSet(txHashes)).map(x => (x.status, x.log)).update((false, log)))

    def getAllPendingStatus: Future[Seq[NFTMintingFee]] = filter(_.status.?.isEmpty)

  }

  object Utility {

    val scheduler: Scheduler = new Scheduler {
      val name: String = NFTMintingFees.module

      def runner(): Unit = {
        val nftMintingFees = Service.getAllPendingStatus

        def getTransactions(hashes: Seq[String]) = blockchainTransactions.Service.getByHashes(hashes)

        def markSuccess(hashes: Seq[String]) = if (hashes.nonEmpty) Service.markSuccess(hashes) else Future(0)

        def markFailed(hashes: Seq[String]) = if (hashes.nonEmpty) Service.markFailed(hashes) else Future(0)

        def markFailedTimedOut(nftMintingFees: Seq[NFTMintingFee], allTxs: Seq[Transaction]) = if (nftMintingFees.nonEmpty) {
          val notFoundTxHashes = nftMintingFees.map(_.txHash).diff(allTxs.map(_.hash))
          val timedoutFailedTxs = nftMintingFees.filter(x => notFoundTxHashes.contains(x.txHash) && x.timeoutHeight > 0 && blockchainBlocks.Service.getLatestHeight > x.timeoutHeight).map(_.txHash)
          if (timedoutFailedTxs.nonEmpty) Service.markFailedWithLog(timedoutFailedTxs, constants.Response.TRANSACTION_NOT_FOUND.logMessage) else Future(0)
        } else Future(0)

        val forComplete = (for {
          nftMintingFees <- nftMintingFees
          txs <- getTransactions(nftMintingFees.map(_.txHash))
          _ <- markSuccess(txs.filter(_.status).map(_.hash))
          _ <- markFailed(txs.filter(!_.status).map(_.hash))
          _ <- markFailedTimedOut(nftMintingFees, txs)
        } yield ()).recover {
          case baseException: BaseException => logger.error(baseException.failure.logMessage)
        }

        Await.result(forComplete, Duration.Inf)
      }
    }

  }

}