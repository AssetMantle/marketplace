package models.blockchainTransaction

import exceptions.BaseException
import models.Trait._
import models.blockchain
import models.common.Coin
import org.bitcoinj.core.ECKey
import play.api.Logger
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.json.Json
import slick.jdbc.H2Profile.api._
import transactions.responses.blockchain.BroadcastTxSyncResponse
import utilities.MicroNumber

import javax.inject.{Inject, Singleton}
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}

case class BuyNFT(txHash: String, txRawBytes: Array[Byte], fromAddress: String, toAddress: String, amount: Seq[Coin], broadcasted: Boolean, status: Option[Boolean], memo: Option[String], log: Option[String], createdBy: Option[String] = None, createdOnMillisEpoch: Option[Long] = None, updatedBy: Option[String] = None, updatedOnMillisEpoch: Option[Long] = None) extends Logging with BlockchainTransaction {

  def serialize(): BuyNFTs.BuyNFTSerialized = BuyNFTs.BuyNFTSerialized(txHash = this.txHash, txRawBytes = this.txRawBytes, fromAddress = this.fromAddress, toAddress = this.toAddress, amount = Json.toJson(this.amount).toString, broadcasted = this.broadcasted, status = this.status, memo = this.memo, log = this.log, createdBy = this.createdBy, createdOnMillisEpoch = this.createdOnMillisEpoch, updatedBy = this.updatedBy, updatedOnMillisEpoch = this.updatedOnMillisEpoch)
}

object BuyNFTs {

  private implicit val logger: Logger = Logger(this.getClass)

  private implicit val module: String = constants.Module.BLOCKCHAIN_TRANSACTION_BUY_NFT

  case class BuyNFTSerialized(txHash: String, txRawBytes: Array[Byte], fromAddress: String, toAddress: String, amount: String, broadcasted: Boolean, status: Option[Boolean], memo: Option[String], log: Option[String], createdBy: Option[String], createdOnMillisEpoch: Option[Long], updatedBy: Option[String], updatedOnMillisEpoch: Option[Long]) extends Entity[String] {
    def deserialize: BuyNFT = BuyNFT(txHash = txHash, txRawBytes = this.txRawBytes, fromAddress = fromAddress, toAddress = toAddress, amount = utilities.JSON.convertJsonStringToObject[Seq[Coin]](amount), broadcasted = broadcasted, status = status, memo = memo, log = log, createdBy = createdBy, createdOnMillisEpoch = createdOnMillisEpoch, updatedBy = updatedBy, updatedOnMillisEpoch = updatedOnMillisEpoch)

    def id: String = txHash
  }

  class BuyNFTTable(tag: Tag) extends Table[BuyNFTSerialized](tag, "BuyNFT") with ModelTable[String] {

    def * = (txHash, txRawBytes, fromAddress, toAddress, amount, broadcasted, status.?, memo.?, log.?, createdBy.?, createdOnMillisEpoch.?, updatedBy.?, updatedOnMillisEpoch.?) <> (BuyNFTSerialized.tupled, BuyNFTSerialized.unapply)

    def txHash = column[String]("txHash", O.PrimaryKey)

    def txRawBytes = column[Array[Byte]]("txRawBytes")

    def fromAddress = column[String]("fromAddress")

    def toAddress = column[String]("toAddress")

    def amount = column[String]("amount")

    def broadcasted = column[Boolean]("broadcasted")

    def status = column[Boolean]("status")

    def memo = column[String]("memo")

    def log = column[String]("log")

    def createdBy = column[String]("createdBy")

    def createdOnMillisEpoch = column[Long]("createdOnMillisEpoch")

    def updatedBy = column[String]("updatedBy")

    def updatedOnMillisEpoch = column[Long]("updatedOnMillisEpoch")

    def id = txHash
  }

  val TableQuery = new TableQuery(tag => new BuyNFTTable(tag))

}

@Singleton
class BuyNFTs @Inject()(
                         protected val databaseConfigProvider: DatabaseConfigProvider,
                         blockchainAccounts: models.blockchain.Accounts,
                         blockchainTransactions: models.blockchain.Transactions,
                         masterTransactionBuyNFTTransactions: models.masterTransaction.BuyNFTTransactions,
                         broadcastTxSync: transactions.blockchain.BroadcastTxSync,
                         utilitiesOperations: utilities.Operations,
                         utilitiesTransactionComplete: utilities.TransactionComplete,
                         getUnconfirmedTxs: queries.blockchain.GetUnconfirmedTxs,
                         getAccount: queries.blockchain.GetAccount,
                       )(implicit override val executionContext: ExecutionContext)
  extends GenericDaoImpl[BuyNFTs.BuyNFTTable, BuyNFTs.BuyNFTSerialized, String](
    databaseConfigProvider,
    BuyNFTs.TableQuery,
    executionContext,
    BuyNFTs.module,
    BuyNFTs.logger
  ) {

  private val schedulerExecutionContext: ExecutionContext = actors.Service.actorSystem.dispatchers.lookup("akka.actor.scheduler-dispatcher")

  object Service {

    def add(txHash: String, txRawBytes: Array[Byte], fromAddress: String, toAddress: String, amount: Seq[Coin], broadcasted: Boolean, status: Option[Boolean], memo: Option[String]): Future[BuyNFT] = {
      val buyNFT = BuyNFT(txHash = txHash, txRawBytes = txRawBytes, fromAddress = fromAddress, toAddress = toAddress, amount = amount, broadcasted = broadcasted, status = status, log = None, memo = memo)
      for {
        _ <- create(buyNFT.serialize())
      } yield buyNFT
    }

    def tryGet(txHash: String): Future[BuyNFT] = tryGetById(txHash).map(_.deserialize)

    def updateBuyNFT(buyNFT: BuyNFT): Future[BuyNFT] = {
      for {
        _ <- updateById(buyNFT.serialize())
      } yield buyNFT
    }

    def getAllPendingStatus: Future[Seq[BuyNFT]] = filter(_.status.?.isEmpty).map(_.map(_.deserialize))

    def fetchAll: Future[Seq[BuyNFT]] = getAll.map(_.map(_.deserialize))
  }

  object Utility {

    def transaction(buyerAccountId: String, sellerAccountId: String, nftIds: Seq[String], saleId: String, fromAddress: String, toAddress: String, amount: MicroNumber, gasPrice: Double, gasLimit: Int, ecKey: ECKey): Future[BlockchainTransaction] = {
      // TODO
      // val bcAccount = blockchainAccounts.Service.tryGet(fromAddress)
      val bcAccount = getAccount.Service.get(fromAddress).map(_.account.toSerializableAccount(fromAddress))
      val unconfirmedTxs = getUnconfirmedTxs.Service.get()

      def checkMempoolAndAddTx(bcAccount: models.blockchain.Account, unconfirmedTxHashes: Seq[String]) = {
        val (txRawBytes, memo) = utilities.BlockchainTransaction.getTxRawBytesWithSignedMemo(
          messages = Seq(utilities.BlockchainTransaction.getSendCoinMsgAsAny(fromAddress = fromAddress, toAddress = toAddress, amount = Seq(Coin(denom = constants.Blockchain.StakingToken, amount = amount)))),
          fee = utilities.BlockchainTransaction.getFee(gasPrice = gasPrice, gasLimit = gasLimit),
          gasLimit = gasLimit,
          account = bcAccount,
          ecKey = ecKey)
        val txHash = utilities.Secrets.sha256HashHexString(txRawBytes)

        def checkAndAdd(unconfirmedTxHashes: Seq[String]) = {
          if (!unconfirmedTxHashes.contains(txHash)) {
            for {
              buyNFT <- Service.add(txHash = txHash, txRawBytes = txRawBytes, fromAddress = fromAddress, toAddress = toAddress, amount = Seq(Coin(denom = constants.Blockchain.StakingToken, amount = amount)), broadcasted = false, status = None, memo = Option(memo))
              _ <- masterTransactionBuyNFTTransactions.Service.addWithNoneStatus(buyerAccountId = buyerAccountId, sellerAccountId = sellerAccountId, txHash = txHash, nftIds = nftIds, saleId = saleId)
            } yield buyNFT
          }
          else constants.Response.TRANSACTION_ALREADY_IN_MEMPOOL.throwFutureBaseException()
        }

        for {
          buyNFT <- checkAndAdd(unconfirmedTxHashes)
        } yield buyNFT
      }

      def broadcastTxAndUpdate(buyNFT: BuyNFT) = {

        def update(successResponse: Option[BroadcastTxSyncResponse.Response], errorResponse: Option[BroadcastTxSyncResponse.ErrorResponse]) = if (errorResponse.nonEmpty) Service.updateBuyNFT(buyNFT.copy(broadcasted = true, status = Option(false), log = Option(errorResponse.get.error.data)))
        else if (successResponse.nonEmpty && successResponse.get.result.code != 0) Service.updateBuyNFT(buyNFT.copy(broadcasted = true, status = Option(false), log = Option(successResponse.get.result.log)))
        else Service.updateBuyNFT(buyNFT.copy(broadcasted = true))

        for {
          (successResponse, errorResponse) <- broadcastTxSync.Service.get(buyNFT.getTxRawAsHexString)
          updatedBuyNFT <- update(successResponse, errorResponse)
        } yield updatedBuyNFT
      }

      for {
        bcAccount <- bcAccount
        unconfirmedTxs <- unconfirmedTxs
        buyNFT <- checkMempoolAndAddTx(bcAccount, unconfirmedTxs.result.txs.map(x => utilities.Secrets.base64URLDecode(x).map("%02x".format(_)).mkString.toUpperCase))
        updatedBuyNFT <- broadcastTxAndUpdate(buyNFT)
      } yield updatedBuyNFT
    }

    private val txSchedulerRunnable = new Runnable {
      def run(): Unit = {
        val buyNFTs = Service.getAllPendingStatus

        def checkAndUpdate(buyNFTs: Seq[BuyNFT]) = utilitiesOperations.traverse(buyNFTs) { buyNFT =>
          val transaction = blockchainTransactions.Service.get(buyNFT.txHash)

          def update(transaction: Option[blockchain.Transaction]) = transaction.fold[Future[Option[BuyNFT]]](Future(None))(tx => Service.updateBuyNFT(buyNFT.copy(status = Option(tx.status), log = if (tx.rawLog != "") Option(tx.rawLog) else None)).map(Option(_)))

          def onTxComplete(transaction: Option[blockchain.Transaction], price: MicroNumber) = if (transaction.isDefined) {
            utilitiesTransactionComplete.onBuyNFT(transaction = transaction.get, price = price)
          } else Future()

          for {
            transaction <- transaction
            buyNFT <- update(transaction)
            _ <- onTxComplete(transaction, buyNFT.fold(MicroNumber.zero)(_.amount.head.amount))
          } yield buyNFT

        }

        val forComplete = (for {
          buyNFTs <- buyNFTs
          _ <- checkAndUpdate(buyNFTs)
        } yield ()).recover {
          case baseException: BaseException => logger.error(baseException.failure.logMessage)
        }

        Await.result(forComplete, Duration.Inf)
      }
    }

    actors.Service.actorSystem.scheduler.scheduleWithFixedDelay(initialDelay = constants.Scheduler.InitialDelay, delay = constants.Scheduler.FixedDelay)(txSchedulerRunnable)(schedulerExecutionContext)

  }

}