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

case class BuyAndMintAsset(buyerAccountId: String, sellerAccountId: String, txHash: String, txRawBytes: Array[Byte], fromAddress: String, toAddress: String, amount: MicroNumber, fromId: String, toId: String, classificationId: String, assetId: String, nftId: String, saleId: String, broadcasted: Boolean, status: Option[Boolean], memo: Option[String], log: Option[String], createdBy: Option[String] = None, createdOnMillisEpoch: Option[Long] = None, updatedBy: Option[String] = None, updatedOnMillisEpoch: Option[Long] = None) extends Logging with BlockchainTransaction {

  def serialize(): BuyAndMintAssets.BuyAndMintAssetSerialized = BuyAndMintAssets.BuyAndMintAssetSerialized(
    buyerAccountId = this.buyerAccountId,
    sellerAccountId = this.sellerAccountId,
    txHash = this.txHash,
    txRawBytes = this.txRawBytes,
    fromAddress = this.fromAddress,
    toAddress = this.toAddress,
    amount = this.amount.toBigDecimal,
    fromId = this.fromId,
    toId = this.toId,
    classificationId = this.classificationId,
    assetId = this.assetId,
    nftId = this.nftId,
    saleId = this.saleId,
    broadcasted = this.broadcasted,
    status = this.status,
    memo = this.memo,
    log = this.log,
    createdBy = this.createdBy,
    createdOnMillisEpoch = this.createdOnMillisEpoch,
    updatedBy = this.updatedBy,
    updatedOnMillisEpoch = this.updatedOnMillisEpoch)
}

object BuyAndMintAssets {

  private implicit val logger: Logger = Logger(this.getClass)

  private implicit val module: String = constants.Module.BLOCKCHAIN_TRANSACTION_BUY_AND_MINT_ASSET

  case class BuyAndMintAssetSerialized(buyerAccountId: String, sellerAccountId: String, txHash: String, txRawBytes: Array[Byte], fromAddress: String, toAddress: String, amount: BigDecimal, fromId: String, toId: String, classificationId: String, assetId: String, nftId: String, saleId: String, broadcasted: Boolean, status: Option[Boolean], memo: Option[String], log: Option[String], createdBy: Option[String], createdOnMillisEpoch: Option[Long], updatedBy: Option[String], updatedOnMillisEpoch: Option[Long]) extends Entity3[String, String, String] {
    def deserialize: BuyAndMintAsset = BuyAndMintAsset(
      buyerAccountId = this.buyerAccountId,
      sellerAccountId = this.sellerAccountId,
      txHash = this.txHash,
      txRawBytes = this.txRawBytes,
      fromAddress = this.fromAddress,
      toAddress = this.toAddress,
      amount = MicroNumber(this.amount),
      fromId = this.fromId,
      toId = this.toId,
      classificationId = this.classificationId,
      assetId = this.assetId,
      nftId = this.nftId,
      saleId = this.saleId,
      broadcasted = this.broadcasted,
      status = this.status,
      memo = this.memo,
      log = this.log,
      createdBy = this.createdBy,
      createdOnMillisEpoch = this.createdOnMillisEpoch,
      updatedBy = this.updatedBy,
      updatedOnMillisEpoch = this.updatedOnMillisEpoch)

    def id1: String = buyerAccountId

    def id2: String = sellerAccountId

    def id3: String = txHash
  }

  class BuyAndMintAssetTable(tag: Tag) extends Table[BuyAndMintAssetSerialized](tag, "BuyAndMintAsset") with ModelTable3[String, String, String] {

    def * = (buyerAccountId, sellerAccountId, txHash, txRawBytes, fromAddress, toAddress, amount, fromId, toId, classificationId, assetId, nftId, saleId, broadcasted, status.?, memo.?, log.?, createdBy.?, createdOnMillisEpoch.?, updatedBy.?, updatedOnMillisEpoch.?) <> (BuyAndMintAssetSerialized.tupled, BuyAndMintAssetSerialized.unapply)

    def buyerAccountId = column[String]("buyerAccountId", O.PrimaryKey)

    def sellerAccountId = column[String]("sellerAccountId", O.PrimaryKey)

    def txHash = column[String]("txHash", O.PrimaryKey)

    def txRawBytes = column[Array[Byte]]("txRawBytes")

    def fromAddress = column[String]("fromAddress")

    def toAddress = column[String]("toAddress")

    def amount = column[BigDecimal]("amount")

    def fromId = column[String]("fromId")

    def toId = column[String]("toId")

    def classificationId = column[String]("classificationId")

    def assetId = column[String]("assetId")

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

  val TableQuery = new TableQuery(tag => new BuyAndMintAssetTable(tag))

}

@Singleton
class BuyAndMintAssets @Inject()(
                                  protected val databaseConfigProvider: DatabaseConfigProvider,
                                  blockchainAccounts: blockchain.Accounts,
                                  blockchainTransactions: blockchain.Transactions,
                                  masterNFTOwners: master.NFTOwners,
                                  masterNFTs: master.NFTs,
                                  broadcastTxSync: transactions.blockchain.BroadcastTxSync,
                                  utilitiesOperations: utilities.Operations,
                                  getUnconfirmedTxs: queries.blockchain.GetUnconfirmedTxs,
                                  getAccount: queries.blockchain.GetAccount,
                                  utilitiesTransactionComplete: utilities.TransactionComplete,
                                )(implicit override val executionContext: ExecutionContext)
  extends GenericDaoImpl3[BuyAndMintAssets.BuyAndMintAssetTable, BuyAndMintAssets.BuyAndMintAssetSerialized, String, String, String](
    databaseConfigProvider,
    BuyAndMintAssets.TableQuery,
    executionContext,
    BuyAndMintAssets.module,
    BuyAndMintAssets.logger
  ) {

  private val schedulerExecutionContext: ExecutionContext = actors.Service.actorSystem.dispatchers.lookup("akka.actor.scheduler-dispatcher")

  object Service {

    def add(buyerAccountId: String, sellerAccountId: String, txHash: String, txRawBytes: Array[Byte], fromAddress: String, toAddress: String, amount: MicroNumber, fromId: String, toId: String, classificationId: String, assetId: String, saleId: String, nftId: String, broadcasted: Boolean, status: Option[Boolean], memo: Option[String]): Future[BuyAndMintAsset] = {
      val buyAndMintAsset = BuyAndMintAsset(buyerAccountId = buyerAccountId, sellerAccountId = sellerAccountId, txHash = txHash, txRawBytes = txRawBytes, fromAddress = fromAddress, toAddress = toAddress, amount = amount, fromId = fromId, toId = toId, classificationId = classificationId, assetId = assetId, nftId = nftId, saleId = saleId, broadcasted = broadcasted, status = status, log = None, memo = memo)
      for {
        _ <- create(buyAndMintAsset.serialize())
      } yield buyAndMintAsset
    }

    def tryGet(sellerAccountId: String, buyerAccountId: String, txHash: String): Future[BuyAndMintAsset] = tryGetById1Id2Id3(id1 = sellerAccountId, id2 = buyerAccountId, id3 = txHash).map(_.deserialize)

    def update(buyAndMintAsset: BuyAndMintAsset): Future[BuyAndMintAsset] = {
      for {
        _ <- updateById1Id2Id3(buyAndMintAsset.serialize())
      } yield buyAndMintAsset
    }

    def getAllPendingStatus: Future[Seq[BuyAndMintAsset]] = filter(_.status.?.isEmpty).map(_.map(_.deserialize))

    def countBuyerTransactionByCollectionId(buyerAccountId: String, saleId: String): Future[Int] = {
      val nullStatus: Option[Boolean] = null
      filterAndCount(x => x.buyerAccountId === buyerAccountId && x.status.? === nullStatus || x.status && x.saleId === saleId)
    }

  }

  object Utility {

    def transaction(buyerAccountId: String, sellerAccountId: String, fromAddress: String, toAddress: String, amount: MicroNumber, fromId: String, toId: String, classificationId: String, assetId: String, nftId: String, saleId: String, gasPrice: Double, gasLimit: Int, ecKey: ECKey): Future[BlockchainTransaction] = {
      // TODO
      // val bcAccount = blockchainAccounts.Service.tryGet(fromAddress)
      val bcAccount = getAccount.Service.get(fromAddress).map(_.account.toSerializableAccount(fromAddress))
      val unconfirmedTxs = getUnconfirmedTxs.Service.get()
      val memo = s"BAWM/$saleId/${utilities.IdGenerator.getRandomHexadecimal}"

      def checkMempoolAndAddTx(bcAccount: blockchain.Account, unconfirmedTxHashes: Seq[String]): Future[BuyAndMintAsset] = {
        val txRawBytes = utilities.BlockchainTransaction.getTxRawBytes(
          messages = Seq(
            utilities.BlockchainTransaction.getSendCoinMsgAsAny(fromAddress = fromAddress, toAddress = toAddress, amount = Seq(Coin(denom = constants.Blockchain.StakingToken, amount = amount))),
            //            utilities.BlockchainTransaction.getMintMsgAsAny(fromAddress = fromAddress, fromId = fromId, toId = toId, classificationId = classificationId), // TODO
          ),
          fee = utilities.BlockchainTransaction.getFee(gasPrice = gasPrice, gasLimit = gasLimit),
          gasLimit = gasLimit,
          account = bcAccount,
          ecKey = ecKey,
          memo = memo)
        val txHash = utilities.Secrets.sha256HashHexString(txRawBytes)

        for {
          buyAndMintAsset <- if (!unconfirmedTxHashes.contains(txHash)) Service.add(buyerAccountId = buyerAccountId, sellerAccountId = sellerAccountId, txHash = txHash, txRawBytes = txRawBytes, fromAddress = fromAddress, toAddress = toAddress, amount = amount, fromId = fromId, toId, classificationId = classificationId, assetId = assetId, nftId = nftId, saleId = saleId, broadcasted = false, status = None, memo = Option(memo)) else constants.Response.TRANSACTION_ALREADY_IN_MEMPOOL.throwFutureBaseException()
        } yield buyAndMintAsset
      }

      def broadcastTxAndUpdate(buyAndMintAsset: BuyAndMintAsset) = {
        val broadcastTx = broadcastTxSync.Service.get(buyAndMintAsset.getTxRawAsHexString)

        def update(successResponse: Option[BroadcastTxSyncResponse.Response], errorResponse: Option[BroadcastTxSyncResponse.ErrorResponse]) = {
          val updatedBuyAndMintAsset = if (errorResponse.isDefined || (successResponse.isDefined && successResponse.get.result.code != 0)) {
            buyAndMintAsset.copy(broadcasted = true, status = Option(false), log = Option(errorResponse.fold(successResponse.get.result.log)(_.error.data)))
          } else buyAndMintAsset.copy(broadcasted = true)
          Service.update(updatedBuyAndMintAsset)
        }

        for {
          (successResponse, errorResponse) <- broadcastTx
          updatedBuyAndMintAsset <- update(successResponse, errorResponse)
        } yield updatedBuyAndMintAsset
      }

      for {
        bcAccount <- bcAccount
        unconfirmedTxs <- unconfirmedTxs
        buyAndMintAsset <- checkMempoolAndAddTx(bcAccount, unconfirmedTxs.result.txs.map(x => utilities.Secrets.base64URLDecode(x).map("%02x".format(_)).mkString.toUpperCase))
        updatedBuyAndMintAsset <- broadcastTxAndUpdate(buyAndMintAsset)
      } yield updatedBuyAndMintAsset
    }

    private val txSchedulerRunnable = new Runnable {
      def run(): Unit = {
        val buyAndMintAssets = Service.getAllPendingStatus

        def checkAndUpdate(buyAndMintAssets: Seq[BuyAndMintAsset]) = utilitiesOperations.traverse(buyAndMintAssets) { buyAndMintAsset =>
          val transaction = blockchainTransactions.Service.get(buyAndMintAsset.txHash)

          def update(transaction: Option[blockchain.Transaction]): Future[Option[BuyAndMintAsset]] = if (transaction.isDefined) {
            Service.update(buyAndMintAsset.copy(status = Option(transaction.get.status), log = if (transaction.get.rawLog != "") Option(transaction.get.rawLog) else None)).map(Option(_))
          } else Future(None)

          def onTxComplete(transaction: Option[blockchain.Transaction], buyAndMintAsset: Option[BuyAndMintAsset]) = if (transaction.isDefined) {
            utilitiesTransactionComplete.onBuyAndMintAsset(transaction = transaction.get, nftId = buyAndMintAsset.get.nftId, buyerAccountId = buyAndMintAsset.get.buyerAccountId, sellerAccountId = buyAndMintAsset.get.sellerAccountId, saleId = buyAndMintAsset.get.saleId, price = buyAndMintAsset.get.amount)
          } else Future()

          for {
            transaction <- transaction
            buyAndMintAsset <- update(transaction)
            _ <- onTxComplete(transaction, buyAndMintAsset)
          } yield ()

        }

        val forComplete = (for {
          buyAndMintAssets <- buyAndMintAssets
          _ <- checkAndUpdate(buyAndMintAssets)
        } yield ()).recover {
          case baseException: BaseException => logger.error(baseException.failure.logMessage)
        }

        Await.result(forComplete, Duration.Inf)
      }
    }

    actors.Service.actorSystem.scheduler.scheduleWithFixedDelay(initialDelay = constants.Scheduler.InitialDelay, delay = constants.Scheduler.FixedDelay)(txSchedulerRunnable)(schedulerExecutionContext)

  }

}