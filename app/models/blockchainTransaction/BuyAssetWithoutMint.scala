package models.blockchainTransaction

import exceptions.BaseException
import models.Trait._
import models.common.Coin
import models.{blockchain, master}
import org.bitcoinj.core.ECKey
import play.api.Logger
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.H2Profile.api._
import transactions.responses.blockchain.BroadcastTxSyncResponse
import utilities.MicroNumber

import javax.inject.{Inject, Singleton}
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}

case class BuyAssetWithoutMint(buyerAccountId: String, sellerAccountId: String, txHash: String, txRawBytes: Array[Byte], fromAddress: String, toAddress: String, amount: MicroNumber, nftId: String, saleId: String, broadcasted: Boolean, status: Option[Boolean], memo: Option[String], log: Option[String], createdBy: Option[String] = None, createdOnMillisEpoch: Option[Long] = None, updatedBy: Option[String] = None, updatedOnMillisEpoch: Option[Long] = None) extends Logging with BlockchainTransaction {

  def serialize(): BuyAssetWithoutMints.BuyAssetWithoutMintSerialized = BuyAssetWithoutMints.BuyAssetWithoutMintSerialized(sellerAccountId = this.sellerAccountId, buyerAccountId = this.buyerAccountId, txHash = this.txHash, txRawBytes = this.txRawBytes, nftId = this.nftId, saleId = this.saleId, fromAddress = this.fromAddress, toAddress = this.toAddress, amount = this.amount.toBigDecimal, broadcasted = this.broadcasted, status = this.status, memo = this.memo, log = this.log, createdBy = this.createdBy, createdOnMillisEpoch = this.createdOnMillisEpoch, updatedBy = this.updatedBy, updatedOnMillisEpoch = this.updatedOnMillisEpoch)
}

object BuyAssetWithoutMints {

  private implicit val logger: Logger = Logger(this.getClass)

  private implicit val module: String = constants.Module.BLOCKCHAIN_TRANSACTION_BUY_ASSET_WITHOUT_MINT

  case class BuyAssetWithoutMintSerialized(buyerAccountId: String, sellerAccountId: String, txHash: String, txRawBytes: Array[Byte], fromAddress: String, toAddress: String, amount: BigDecimal, nftId: String, saleId: String, broadcasted: Boolean, status: Option[Boolean], memo: Option[String], log: Option[String], createdBy: Option[String], createdOnMillisEpoch: Option[Long], updatedBy: Option[String], updatedOnMillisEpoch: Option[Long]) extends Entity3[String, String, String] {
    def deserialize: BuyAssetWithoutMint = BuyAssetWithoutMint(buyerAccountId = this.buyerAccountId, sellerAccountId = this.sellerAccountId, txHash = txHash, txRawBytes = this.txRawBytes, nftId = this.nftId, saleId = this.saleId, fromAddress = fromAddress, toAddress = toAddress, amount = MicroNumber(amount), broadcasted = broadcasted, status = status, memo = memo, log = log, createdBy = createdBy, createdOnMillisEpoch = createdOnMillisEpoch, updatedBy = updatedBy, updatedOnMillisEpoch = updatedOnMillisEpoch)

    def id1: String = buyerAccountId

    def id2: String = sellerAccountId

    def id3: String = txHash
  }

  class BuyAssetWithoutMintTable(tag: Tag) extends Table[BuyAssetWithoutMintSerialized](tag, "BuyAssetWithoutMint") with ModelTable3[String, String, String] {

    def * = (buyerAccountId, sellerAccountId, txHash, txRawBytes, fromAddress, toAddress, amount, nftId, saleId, broadcasted, status.?, memo.?, log.?, createdBy.?, createdOnMillisEpoch.?, updatedBy.?, updatedOnMillisEpoch.?) <> (BuyAssetWithoutMintSerialized.tupled, BuyAssetWithoutMintSerialized.unapply)

    def buyerAccountId = column[String]("buyerAccountId", O.PrimaryKey)

    def sellerAccountId = column[String]("sellerAccountId", O.PrimaryKey)

    def txHash = column[String]("txHash", O.PrimaryKey)

    def txRawBytes = column[Array[Byte]]("txRawBytes")

    def fromAddress = column[String]("fromAddress")

    def toAddress = column[String]("toAddress")

    def amount = column[BigDecimal]("amount")

    def nftId = column[String]("nftId")

    def saleId = column[String]("saleId")

    def broadcasted = column[Boolean]("broadcasted")

    def status = column[Boolean]("status")

    def memo = column[String]("memo")

    def log = column[String]("log")

    def createdBy = column[String]("createdBy")

    def createdOnMillisEpoch = column[Long]("createdOnMillisEpoch")

    def updatedBy = column[String]("updatedBy")

    def updatedOnMillisEpoch = column[Long]("updatedOnMillisEpoch")

    def id1 = buyerAccountId

    def id2 = sellerAccountId

    def id3 = txHash
  }

  val TableQuery = new TableQuery(tag => new BuyAssetWithoutMintTable(tag))

}

@Singleton
class BuyAssetWithoutMints @Inject()(
                                      protected val databaseConfigProvider: DatabaseConfigProvider,
                                      blockchainAccounts: blockchain.Accounts,
                                      blockchainTransactions: blockchain.Transactions,
                                      masterNFTOwners: master.NFTOwners,
                                      masterNFTs: master.NFTs,
                                      broadcastTxSync: transactions.blockchain.BroadcastTxSync,
                                      utilitiesOperations: utilities.Operations,
                                      getUnconfirmedTxs: queries.blockchain.GetUnconfirmedTxs,
                                      getAccount: queries.blockchain.GetAccount,
                                      utilitiesTransactionComplete: utilities.TransactionComplete
                                    )(implicit override val executionContext: ExecutionContext)
  extends GenericDaoImpl3[BuyAssetWithoutMints.BuyAssetWithoutMintTable, BuyAssetWithoutMints.BuyAssetWithoutMintSerialized, String, String, String](
    databaseConfigProvider,
    BuyAssetWithoutMints.TableQuery,
    executionContext,
    BuyAssetWithoutMints.module,
    BuyAssetWithoutMints.logger
  ) {

  private val schedulerExecutionContext: ExecutionContext = actors.Service.actorSystem.dispatchers.lookup("akka.actor.scheduler-dispatcher")

  object Service {

    def add(buyerAccountId: String, sellerAccountId: String, txHash: String, txRawBytes: Array[Byte], saleId: String, nftId: String, fromAddress: String, toAddress: String, amount: MicroNumber, broadcasted: Boolean, status: Option[Boolean], memo: Option[String]): Future[BuyAssetWithoutMint] = {
      val buyAssetWithoutMint = BuyAssetWithoutMint(buyerAccountId = buyerAccountId, sellerAccountId = sellerAccountId, txHash = txHash, txRawBytes = txRawBytes, saleId = saleId, nftId = nftId, fromAddress = fromAddress, toAddress = toAddress, amount = amount, broadcasted = broadcasted, status = status, log = None, memo = memo)
      for {
        _ <- create(buyAssetWithoutMint.serialize())
      } yield buyAssetWithoutMint
    }

    def tryGet(sellerAccountId: String, buyerAccountId: String, txHash: String): Future[BuyAssetWithoutMint] = tryGetById1Id2Id3(id1 = sellerAccountId, id2 = buyerAccountId, id3 = txHash).map(_.deserialize)

    def update(buyAssetWithoutMint: BuyAssetWithoutMint): Future[BuyAssetWithoutMint] = {
      for {
        _ <- updateById1Id2Id3(buyAssetWithoutMint.serialize())
      } yield buyAssetWithoutMint
    }

    def getAllPendingStatus: Future[Seq[BuyAssetWithoutMint]] = filter(_.status.?.isEmpty).map(_.map(_.deserialize))

    def countBuyerNFTsFromSale(buyerAccountId: String, saleId: String): Future[Int] = {
      val nullStatus: Option[Boolean] = null
      filterAndCount(x => x.buyerAccountId === buyerAccountId && x.status.? === nullStatus || x.status && x.saleId === saleId)
    }

  }

  object Utility {

    def transaction(sellerAccountId: String, buyerAccountId: String, nftId: String, saleId: String, fromAddress: String, toAddress: String, amount: MicroNumber, gasPrice: Double, gasLimit: Int, ecKey: ECKey): Future[BlockchainTransaction] = {
      // TODO
      // val bcAccount = blockchainAccounts.Service.tryGet(fromAddress)
      val bcAccount = getAccount.Service.get(fromAddress).map(_.account.toSerializableAccount(fromAddress))
      val unconfirmedTxs = getUnconfirmedTxs.Service.get()
      val memo = s"BAWM/$saleId/${utilities.IdGenerator.getRandomHexadecimal}"

      def checkMempoolAndAddTx(bcAccount: blockchain.Account, unconfirmedTxHashes: Seq[String]): Future[BuyAssetWithoutMint] = {
        val txRawBytes = utilities.BlockchainTransaction.getTxRawBytes(
          messages = Seq(utilities.BlockchainTransaction.getSendCoinMsgAsAny(fromAddress = fromAddress, toAddress = toAddress, amount = Seq(Coin(denom = constants.Blockchain.StakingToken, amount = amount)))),
          fee = utilities.BlockchainTransaction.getFee(gasPrice = gasPrice, gasLimit = gasLimit),
          gasLimit = gasLimit,
          account = bcAccount,
          ecKey = ecKey,
          memo = memo)
        val txHash = utilities.Secrets.sha256HashHexString(txRawBytes)

        for {
          buyAssetWithoutMint <- if (!unconfirmedTxHashes.contains(txHash)) Service.add(buyerAccountId = buyerAccountId, sellerAccountId = sellerAccountId, txHash = txHash, txRawBytes = txRawBytes, nftId = nftId, saleId = saleId, fromAddress = fromAddress, toAddress = toAddress, amount = amount, broadcasted = false, status = None, memo = Option(memo)) else constants.Response.TRANSACTION_ALREADY_IN_MEMPOOL.throwFutureBaseException()
        } yield buyAssetWithoutMint
      }

      def broadcastTxAndUpdate(buyAssetWithoutMint: BuyAssetWithoutMint) = {
        val broadcastTx = broadcastTxSync.Service.get(buyAssetWithoutMint.getTxRawAsHexString)

        def update(successResponse: Option[BroadcastTxSyncResponse.Response], errorResponse: Option[BroadcastTxSyncResponse.ErrorResponse]) = {
          val updatedBuyAssetWithoutMint = if (errorResponse.isDefined || (successResponse.isDefined && successResponse.get.result.code != 0)) {
            buyAssetWithoutMint.copy(broadcasted = true, status = Option(false), log = Option(errorResponse.fold(successResponse.get.result.log)(_.error.data)))
          } else buyAssetWithoutMint.copy(broadcasted = true)
          Service.update(updatedBuyAssetWithoutMint)
        }

        for {
          (successResponse, errorResponse) <- broadcastTx
          updatedBuyAssetWithoutMint <- update(successResponse, errorResponse)
        } yield updatedBuyAssetWithoutMint
      }

      for {
        bcAccount <- bcAccount
        unconfirmedTxs <- unconfirmedTxs
        buyAssetWithoutMint <- checkMempoolAndAddTx(bcAccount, unconfirmedTxs.result.txs.map(x => utilities.Secrets.base64URLDecode(x).map("%02x".format(_)).mkString.toUpperCase))
        updatedBuyAssetWithoutMint <- broadcastTxAndUpdate(buyAssetWithoutMint)
      } yield updatedBuyAssetWithoutMint
    }

    private val txSchedulerRunnable = new Runnable {
      def run(): Unit = {
        val buyAssetWithoutMints = Service.getAllPendingStatus

        def checkAndUpdate(buyAssetWithoutMints: Seq[BuyAssetWithoutMint]) = utilitiesOperations.traverse(buyAssetWithoutMints) { buyAssetWithoutMint =>
          val transaction = blockchainTransactions.Service.get(buyAssetWithoutMint.txHash)

          def update(transaction: Option[blockchain.Transaction]): Future[Option[BuyAssetWithoutMint]] = if (transaction.isDefined) {
            Service.update(buyAssetWithoutMint.copy(status = Option(transaction.get.status), log = if (transaction.get.rawLog != "") Option(transaction.get.rawLog) else None)).map(Option(_))
          } else Future(None)

          def onTxComplete(transaction: Option[blockchain.Transaction], buyAssetWithoutMint: Option[BuyAssetWithoutMint]) = if (transaction.isDefined) {
            utilitiesTransactionComplete.onBuyAssetWithoutMint(transaction = transaction.get, nftId = buyAssetWithoutMint.get.nftId, buyerAccountId = buyAssetWithoutMint.get.buyerAccountId, sellerAccountId = buyAssetWithoutMint.get.sellerAccountId, saleId = buyAssetWithoutMint.get.saleId, price = buyAssetWithoutMint.get.amount)
          } else Future()

          for {
            transaction <- transaction
            buyAssetWithoutMint <- update(transaction)
            _ <- onTxComplete(transaction, buyAssetWithoutMint)
          } yield ()

        }

        val forComplete = (for {
          buyAssetWithoutMints <- buyAssetWithoutMints
          _ <- checkAndUpdate(buyAssetWithoutMints)
        } yield ()).recover {
          case baseException: BaseException => logger.error(baseException.failure.logMessage)
        }

        Await.result(forComplete, Duration.Inf)
      }
    }

    actors.Service.actorSystem.scheduler.scheduleWithFixedDelay(initialDelay = constants.CommonConfig.Scheduler.InitialDelay, delay = constants.CommonConfig.Scheduler.FixedDelay)(txSchedulerRunnable)(schedulerExecutionContext)

  }

}