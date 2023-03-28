package models.blockchainTransaction

import constants.Scheduler
import exceptions.BaseException
import models.traits._
import models.blockchain
import models.blockchain.Transaction
import models.common.Coin
import play.api.Logger
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.json.Json
import slick.jdbc.H2Profile.api._

import javax.inject.{Inject, Singleton}
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}

case class NFTMintingFee(txHash: String, txRawBytes: Array[Byte], fromAddress: String, toAddress: String, amount: Seq[Coin], status: Option[Boolean], memo: Option[String], timeoutHeight: Int, log: Option[String], createdBy: Option[String] = None, createdOnMillisEpoch: Option[Long] = None, updatedBy: Option[String] = None, updatedOnMillisEpoch: Option[Long] = None) extends Logging with BlockchainTransaction {

  def serialize(): NFTMintingFees.NFTMintingFeeSerialized = NFTMintingFees.NFTMintingFeeSerialized(txHash = this.txHash, txRawBytes = this.txRawBytes, fromAddress = this.fromAddress, toAddress = this.toAddress, amount = Json.toJson(this.amount).toString, status = this.status, memo = this.memo, timeoutHeight = this.timeoutHeight, log = this.log, createdBy = this.createdBy, createdOnMillisEpoch = this.createdOnMillisEpoch, updatedBy = this.updatedBy, updatedOnMillisEpoch = this.updatedOnMillisEpoch)
}

object NFTMintingFees {

  private implicit val logger: Logger = Logger(this.getClass)

  private implicit val module: String = constants.Module.BLOCKCHAIN_TRANSACTION_NFT_MINTING_FEE

  case class NFTMintingFeeSerialized(txHash: String, txRawBytes: Array[Byte], fromAddress: String, toAddress: String, amount: String, status: Option[Boolean], memo: Option[String], timeoutHeight: Int, log: Option[String], createdBy: Option[String], createdOnMillisEpoch: Option[Long], updatedBy: Option[String], updatedOnMillisEpoch: Option[Long]) extends Entity[String] {
    def deserialize: NFTMintingFee = NFTMintingFee(txHash = txHash, txRawBytes = this.txRawBytes, fromAddress = fromAddress, toAddress = toAddress, amount = utilities.JSON.convertJsonStringToObject[Seq[Coin]](amount), status = status, memo = memo, timeoutHeight = timeoutHeight, log = log, createdBy = createdBy, createdOnMillisEpoch = createdOnMillisEpoch, updatedBy = updatedBy, updatedOnMillisEpoch = updatedOnMillisEpoch)

    def id: String = txHash
  }

  class NFTMintingFeeTable(tag: Tag) extends Table[NFTMintingFeeSerialized](tag, "NFTMintingFee") with ModelTable[String] {

    def * = (txHash, txRawBytes, fromAddress, toAddress, amount, status.?, memo.?, timeoutHeight, log.?, createdBy.?, createdOnMillisEpoch.?, updatedBy.?, updatedOnMillisEpoch.?) <> (NFTMintingFeeSerialized.tupled, NFTMintingFeeSerialized.unapply)

    def txHash = column[String]("txHash", O.PrimaryKey)

    def txRawBytes = column[Array[Byte]]("txRawBytes")

    def fromAddress = column[String]("fromAddress")

    def toAddress = column[String]("toAddress")

    def amount = column[String]("amount")

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
  extends GenericDaoImpl[NFTMintingFees.NFTMintingFeeTable, NFTMintingFees.NFTMintingFeeSerialized, String](
    databaseConfigProvider,
    NFTMintingFees.TableQuery,
    executionContext,
    NFTMintingFees.module,
    NFTMintingFees.logger
  ) {

  object Service {

    def add(txHash: String, txRawBytes: Array[Byte], fromAddress: String, toAddress: String, amount: Seq[Coin], status: Option[Boolean], memo: Option[String], timeoutHeight: Int): Future[NFTMintingFee] = {
      val nftMintingFee = NFTMintingFee(txHash = txHash, txRawBytes = txRawBytes, fromAddress = fromAddress, toAddress = toAddress, amount = amount, status = status, log = None, memo = memo, timeoutHeight = timeoutHeight)
      for {
        _ <- create(nftMintingFee.serialize())
      } yield nftMintingFee
    }

    def tryGet(txHash: String): Future[NFTMintingFee] = tryGetById(txHash).map(_.deserialize)

    def updateNFTMintingFee(nftMintingFee: NFTMintingFee): Future[NFTMintingFee] = {
      for {
        _ <- updateById(nftMintingFee.serialize())
      } yield nftMintingFee
    }

    def markSuccess(txHashes: Seq[String]): Future[Int] = customUpdate(NFTMintingFees.TableQuery.filter(_.txHash.inSet(txHashes)).map(_.status).update(true))

    def markFailed(txHashes: Seq[String]): Future[Int] = customUpdate(NFTMintingFees.TableQuery.filter(_.txHash.inSet(txHashes)).map(_.status).update(false))

    def markFailedWithLog(txHashes: Seq[String], log: String): Future[Int] = customUpdate(NFTMintingFees.TableQuery.filter(_.txHash.inSet(txHashes)).map(x => (x.status, x.log)).update((false, log)))

    def getAllPendingStatus: Future[Seq[NFTMintingFee]] = filter(_.status.?.isEmpty).map(_.map(_.deserialize))

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