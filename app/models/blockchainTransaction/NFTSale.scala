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

case class NFTSale(txHash: String, txRawBytes: Array[Byte], fromAddress: String, toAddress: String, amount: Seq[Coin], broadcasted: Boolean, status: Option[Boolean], memo: Option[String], log: Option[String], createdBy: Option[String] = None, createdOnMillisEpoch: Option[Long] = None, updatedBy: Option[String] = None, updatedOnMillisEpoch: Option[Long] = None) extends Logging with BlockchainTransaction {

  def serialize(): NFTSales.NFTSaleSerialized = NFTSales.NFTSaleSerialized(txHash = this.txHash, txRawBytes = this.txRawBytes, fromAddress = this.fromAddress, toAddress = this.toAddress, amount = Json.toJson(this.amount).toString, broadcasted = this.broadcasted, status = this.status, memo = this.memo, log = this.log, createdBy = this.createdBy, createdOnMillisEpoch = this.createdOnMillisEpoch, updatedBy = this.updatedBy, updatedOnMillisEpoch = this.updatedOnMillisEpoch)
}

object NFTSales {

  private implicit val logger: Logger = Logger(this.getClass)

  private implicit val module: String = constants.Module.BLOCKCHAIN_TRANSACTION_NFT_SALE

  case class NFTSaleSerialized(txHash: String, txRawBytes: Array[Byte], fromAddress: String, toAddress: String, amount: String, broadcasted: Boolean, status: Option[Boolean], memo: Option[String], log: Option[String], createdBy: Option[String], createdOnMillisEpoch: Option[Long], updatedBy: Option[String], updatedOnMillisEpoch: Option[Long]) extends Entity[String] {
    def deserialize: NFTSale = NFTSale(txHash = txHash, txRawBytes = this.txRawBytes, fromAddress = fromAddress, toAddress = toAddress, amount = utilities.JSON.convertJsonStringToObject[Seq[Coin]](amount), broadcasted = broadcasted, status = status, memo = memo, log = log, createdBy = createdBy, createdOnMillisEpoch = createdOnMillisEpoch, updatedBy = updatedBy, updatedOnMillisEpoch = updatedOnMillisEpoch)

    def id: String = txHash
  }

  class NFTSaleTable(tag: Tag) extends Table[NFTSaleSerialized](tag, "NFTSale") with ModelTable[String] {

    def * = (txHash, txRawBytes, fromAddress, toAddress, amount, broadcasted, status.?, memo.?, log.?, createdBy.?, createdOnMillisEpoch.?, updatedBy.?, updatedOnMillisEpoch.?) <> (NFTSaleSerialized.tupled, NFTSaleSerialized.unapply)

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

  val TableQuery = new TableQuery(tag => new NFTSaleTable(tag))

}

@Singleton
class NFTSales @Inject()(
                         protected val databaseConfigProvider: DatabaseConfigProvider,
                         blockchainAccounts: models.blockchain.Accounts,
                         blockchainTransactions: models.blockchain.Transactions,
                         masterTransactionSaleNFTTransactions: models.masterTransaction.SaleNFTTransactions,
                         broadcastTxSync: transactions.blockchain.BroadcastTxSync,
                         utilitiesOperations: utilities.Operations,
                         utilitiesTransactionComplete: utilities.TransactionComplete,
                         getUnconfirmedTxs: queries.blockchain.GetUnconfirmedTxs,
                         getAccount: queries.blockchain.GetAccount,
                       )(implicit override val executionContext: ExecutionContext)
  extends GenericDaoImpl[NFTSales.NFTSaleTable, NFTSales.NFTSaleSerialized, String](
    databaseConfigProvider,
    NFTSales.TableQuery,
    executionContext,
    NFTSales.module,
    NFTSales.logger
  ) {

  private val schedulerExecutionContext: ExecutionContext = actors.Service.actorSystem.dispatchers.lookup("akka.actor.scheduler-dispatcher")

  object Service {

    def add(txHash: String, txRawBytes: Array[Byte], fromAddress: String, toAddress: String, amount: Seq[Coin], broadcasted: Boolean, status: Option[Boolean], memo: Option[String]): Future[NFTSale] = {
      val nftSale = NFTSale(txHash = txHash, txRawBytes = txRawBytes, fromAddress = fromAddress, toAddress = toAddress, amount = amount, broadcasted = broadcasted, status = status, log = None, memo = memo)
      for {
        _ <- create(nftSale.serialize())
      } yield nftSale
    }

    def tryGet(txHash: String): Future[NFTSale] = tryGetById(txHash).map(_.deserialize)

    def updateNFTSale(nftSale: NFTSale): Future[NFTSale] = {
      for {
        _ <- updateById(nftSale.serialize())
      } yield nftSale
    }

    def getAllPendingStatus: Future[Seq[NFTSale]] = filter(_.status.?.isEmpty).map(_.map(_.deserialize))

    def fetchAll: Future[Seq[NFTSale]] = getAll.map(_.map(_.deserialize))
  }

  object Utility {

    def transaction(buyerAccountId: String, sellerAccountId: String, nftIds: Seq[String], saleId: String, fromAddress: String, toAddress: String, amount: MicroNumber, gasPrice: BigDecimal, gasLimit: Int, ecKey: ECKey): Future[BlockchainTransaction] = {
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
              nftSale <- Service.add(txHash = txHash, txRawBytes = txRawBytes, fromAddress = fromAddress, toAddress = toAddress, amount = Seq(Coin(denom = constants.Blockchain.StakingToken, amount = amount)), broadcasted = false, status = None, memo = Option(memo))
              _ <- masterTransactionSaleNFTTransactions.Service.addWithNoneStatus(buyerAccountId = buyerAccountId, sellerAccountId = sellerAccountId, txHash = txHash, nftIds = nftIds, saleId = saleId)
            } yield nftSale
          }
          else constants.Response.TRANSACTION_ALREADY_IN_MEMPOOL.throwFutureBaseException()
        }

        for {
          nftSale <- checkAndAdd(unconfirmedTxHashes)
        } yield nftSale
      }

      def broadcastTxAndUpdate(nftSale: NFTSale) = {

        def update(successResponse: Option[BroadcastTxSyncResponse.Response], errorResponse: Option[BroadcastTxSyncResponse.ErrorResponse]) = if (errorResponse.nonEmpty) Service.updateNFTSale(nftSale.copy(broadcasted = true, status = Option(false), log = Option(errorResponse.get.error.data)))
        else if (successResponse.nonEmpty && successResponse.get.result.code != 0) Service.updateNFTSale(nftSale.copy(broadcasted = true, status = Option(false), log = Option(successResponse.get.result.log)))
        else Service.updateNFTSale(nftSale.copy(broadcasted = true))

        for {
          (successResponse, errorResponse) <- broadcastTxSync.Service.get(nftSale.getTxRawAsHexString)
          updatedNFTSale <- update(successResponse, errorResponse)
        } yield updatedNFTSale
      }

      for {
        bcAccount <- bcAccount
        unconfirmedTxs <- unconfirmedTxs
        nftSale <- checkMempoolAndAddTx(bcAccount, unconfirmedTxs.result.txs.map(x => utilities.Secrets.base64URLDecode(x).map("%02x".format(_)).mkString.toUpperCase))
        updatedNFTSale <- broadcastTxAndUpdate(nftSale)
      } yield updatedNFTSale
    }

    private val txSchedulerRunnable = new Runnable {
      def run(): Unit = {
        val nftSales = Service.getAllPendingStatus

        def checkAndUpdate(nftSales: Seq[NFTSale]) = utilitiesOperations.traverse(nftSales) { nftSale =>
          val transaction = blockchainTransactions.Service.get(nftSale.txHash)

          def update(transaction: Option[blockchain.Transaction]) = transaction.fold[Future[Option[NFTSale]]](Future(None))(tx => Service.updateNFTSale(nftSale.copy(status = Option(tx.status), log = if (tx.rawLog != "") Option(tx.rawLog) else None)).map(Option(_)))

          def onTxComplete(transaction: Option[blockchain.Transaction], price: MicroNumber) = if (transaction.isDefined) {
            utilitiesTransactionComplete.onNFTSale(transaction = transaction.get, price = price)
          } else Future()

          for {
            transaction <- transaction
            nftSale <- update(transaction)
            _ <- onTxComplete(transaction, nftSale.fold(MicroNumber.zero)(_.amount.head.amount))
          } yield nftSale

        }

        val forComplete = (for {
          nftSales <- nftSales
          _ <- checkAndUpdate(nftSales)
        } yield ()).recover {
          case baseException: BaseException => logger.error(baseException.failure.logMessage)
        }

        Await.result(forComplete, Duration.Inf)
      }
    }

    actors.Service.actorSystem.scheduler.scheduleWithFixedDelay(initialDelay = constants.Scheduler.InitialDelay, delay = constants.Scheduler.FixedDelay)(txSchedulerRunnable)(schedulerExecutionContext)

  }

}