package models.blockchainTransaction

import exceptions.BaseException
import models.Trait._
import models.blockchain.Transaction
import models.common.Coin
import models.{blockchain, master}
import org.bitcoinj.core.ECKey
import play.api.Logger
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.json.Json
import slick.jdbc.H2Profile.api._
import transactions.responses.blockchain.BroadcastTxSyncResponse

import javax.inject.{Inject, Singleton}
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}

case class BuyAssetWithoutMint(sellerAccountId: String, buyerAccountId: String, txHash: String, txRawBytes: Array[Byte], nftId: String, saleId: String, fromAddress: String, toAddress: String, amount: Seq[Coin], broadcasted: Boolean, status: Option[Boolean], memo: Option[String], log: Option[String], createdBy: Option[String] = None, createdOnMillisEpoch: Option[Long] = None, updatedBy: Option[String] = None, updatedOnMillisEpoch: Option[Long] = None) extends Logging with BlockchainTransaction {

  def serialize(): BuyAssetWithoutMints.BuyAssetWithoutMintSerialized = BuyAssetWithoutMints.BuyAssetWithoutMintSerialized(sellerAccountId = this.sellerAccountId, buyerAccountId = this.buyerAccountId, txHash = this.txHash, txRawBytes = this.txRawBytes, nftId = this.nftId, saleId = this.saleId, fromAddress = this.fromAddress, toAddress = this.toAddress, amount = Json.toJson(this.amount).toString, broadcasted = this.broadcasted, status = this.status, memo = this.memo, log = this.log, createdBy = this.createdBy, createdOnMillisEpoch = this.createdOnMillisEpoch, updatedBy = this.updatedBy, updatedOnMillisEpoch = this.updatedOnMillisEpoch)

  def getTxRawAsHexString: String = this.txRawBytes.map("%02x".format(_)).mkString.toUpperCase
}

object BuyAssetWithoutMints {

  private implicit val logger: Logger = Logger(this.getClass)

  private implicit val module: String = constants.Module.BLOCKCHAIN_TRANSACTION_BUY_ASSET_WITHOUT_MINT

  case class BuyAssetWithoutMintSerialized(sellerAccountId: String, buyerAccountId: String, txHash: String, txRawBytes: Array[Byte], nftId: String, saleId: String, fromAddress: String, toAddress: String, amount: String, broadcasted: Boolean, status: Option[Boolean], memo: Option[String], log: Option[String], createdBy: Option[String], createdOnMillisEpoch: Option[Long], updatedBy: Option[String], updatedOnMillisEpoch: Option[Long]) extends Entity3[String, String, String] {
    def deserialize: BuyAssetWithoutMint = BuyAssetWithoutMint(sellerAccountId = this.sellerAccountId, buyerAccountId = buyerAccountId, txHash = txHash, txRawBytes = this.txRawBytes, nftId = this.nftId, saleId = this.saleId, fromAddress = fromAddress, toAddress = toAddress, amount = utilities.JSON.convertJsonStringToObject[Seq[Coin]](amount), broadcasted = broadcasted, status = status, memo = memo, log = log, createdBy = createdBy, createdOnMillisEpoch = createdOnMillisEpoch, updatedBy = updatedBy, updatedOnMillisEpoch = updatedOnMillisEpoch)

    def id1: String = sellerAccountId

    def id2: String = buyerAccountId

    def id3: String = txHash
  }

  class BuyAssetWithoutMintTable(tag: Tag) extends Table[BuyAssetWithoutMintSerialized](tag, "BuyAssetWithoutMint") with ModelTable3[String, String, String] {

    def * = (sellerAccountId, buyerAccountId, txHash, txRawBytes, nftId, saleId, fromAddress, toAddress, amount, broadcasted, status.?, memo.?, log.?, createdBy.?, createdOnMillisEpoch.?, updatedBy.?, updatedOnMillisEpoch.?) <> (BuyAssetWithoutMintSerialized.tupled, BuyAssetWithoutMintSerialized.unapply)

    def sellerAccountId = column[String]("sellerAccountId", O.PrimaryKey)

    def buyerAccountId = column[String]("buyerAccountId", O.PrimaryKey)

    def txHash = column[String]("txHash", O.PrimaryKey)

    def txRawBytes = column[Array[Byte]]("txRawBytes")

    def nftId = column[String]("nftId")

    def saleId = column[String]("saleId")

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

    def id1 = sellerAccountId

    def id2 = buyerAccountId

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
                                      utilitiesNotification: utilities.Notification,
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

    def add(sellerAccountId: String, buyerAccountId: String, txHash: String, txRawBytes: Array[Byte], saleId: String, nftId: String, fromAddress: String, toAddress: String, amount: Seq[Coin], broadcasted: Boolean, status: Option[Boolean], memo: Option[String]): Future[BuyAssetWithoutMint] = {
      val buyAssetWithoutMint = BuyAssetWithoutMint(sellerAccountId = sellerAccountId, buyerAccountId = buyerAccountId, txHash = txHash, txRawBytes = txRawBytes, saleId = saleId, nftId = nftId, fromAddress = fromAddress, toAddress = toAddress, amount = amount, broadcasted = broadcasted, status = status, log = None, memo = memo)
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

  }

  object Utility {

    def transaction(sellerAccountId: String, buyerAccountId: String, nftId: String, saleId: String, fromAddress: String, toAddress: String, amount: Seq[Coin], gasPrice: Double, gasLimit: Int, ecKey: ECKey): Future[BlockchainTransaction] = {
      // TODO
      // val bcAccount = blockchainAccounts.Service.tryGet(fromAddress)
      val bcAccount = getAccount.Service.get(fromAddress).map(_.account.toSerializableAccount(fromAddress))
      val unconfirmedTxs = getUnconfirmedTxs.Service.get()
      val memo = s"BAWM/$saleId/${utilities.IdGenerator.getRandomHexadecimal}"

      def checkMempoolAndAddTx(bcAccount: blockchain.Account, unconfirmedTxHashes: Seq[String]): Future[BuyAssetWithoutMint] = {
        val txRawBytes = utilities.BlockchainTransaction.getTxRawBytes(
          messages = Seq(utilities.BlockchainTransaction.getSendCoinMsgAsAny(fromAddress = fromAddress, toAddress = toAddress, amount = amount)),
          fee = utilities.BlockchainTransaction.getFee(gasPrice = gasPrice, gasLimit = gasLimit),
          gasLimit = gasLimit,
          account = bcAccount,
          ecKey = ecKey,
          memo = memo)
        val txHash = utilities.Secrets.sha256HashHexString(txRawBytes)

        for {
          buyAssetWithoutMint <- if (!unconfirmedTxHashes.contains(txHash)) Service.add(sellerAccountId = sellerAccountId, buyerAccountId = buyerAccountId, txHash = txHash, txRawBytes = txRawBytes, nftId = nftId, saleId = saleId, fromAddress = fromAddress, toAddress = toAddress, amount = amount, broadcasted = false, status = None, memo = Option(memo)) else constants.Response.TRANSACTION_ALREADY_IN_MEMPOOL.throwFutureBaseException()
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

          def update(transaction: Option[blockchain.Transaction]) = transaction.fold[Future[Option[BuyAssetWithoutMint]]](Future(None))(tx => Service.update(buyAssetWithoutMint.copy(status = Option(tx.status), log = if (tx.rawLog != "") Option(tx.rawLog) else None)).map(Option(_)))

          def transferNFTOwnership(transaction: Option[blockchain.Transaction]) = if (transaction.isDefined && transaction.get.status) {
            masterNFTOwners.Service.markNFTSold(nftId = buyAssetWithoutMint.nftId, saleId = buyAssetWithoutMint.saleId, sellerAccountId = buyAssetWithoutMint.sellerAccountId, buyerAccountId = buyAssetWithoutMint.buyerAccountId)
          } else Future()

          def sendNotifications(transaction: Option[Transaction]) = if (transaction.isDefined) {

            val nft = masterNFTs.Service.tryGet(buyAssetWithoutMint.nftId)

            def send(nft: master.NFT) = if (transaction.get.status) {
              utilitiesNotification.send(buyAssetWithoutMint.sellerAccountId, constants.Notification.SELLER_NFT_SALE_WITHOUT_MINT_SUCCESSFUL, nft.name)(nft.fileName)
              utilitiesNotification.send(buyAssetWithoutMint.buyerAccountId, constants.Notification.BUYER_NFT_SALE_WITHOUT_MINT_SUCCESSFUL, nft.name)(nft.fileName)
            } else {
              utilitiesNotification.send(buyAssetWithoutMint.sellerAccountId, constants.Notification.SELLER_NFT_SALE_WITHOUT_MINT_FAILED, nft.name)(nft.fileName)
              utilitiesNotification.send(buyAssetWithoutMint.buyerAccountId, constants.Notification.BUYER_NFT_SALE_WITHOUT_MINT_FAILED, nft.name)(nft.fileName)
            }

            for {
              nft <- nft
            } yield {
              send(nft)
              ()
            }
          } else Future()

          for {
            transaction <- transaction
            _ <- update(transaction)
            _ <- transferNFTOwnership(transaction)
            _ <- sendNotifications(transaction)
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