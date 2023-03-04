package models.masterTransaction

import constants.Scheduler
import exceptions.BaseException
import models.Trait._
import models.blockchainTransaction.NFTSale
import models.common.Coin
import models.master.NFT
import models.{analytics, blockchain, blockchainTransaction, master}
import org.bitcoinj.core.ECKey
import play.api.Logger
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.H2Profile.api._
import transactions.responses.blockchain.BroadcastTxSyncResponse
import utilities.MicroNumber

import javax.inject.{Inject, Singleton}
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}

case class SaleNFTTransaction(txHash: String, nftId: String, buyerAccountId: String, sellerAccountId: String, saleId: String, status: Option[Boolean], createdBy: Option[String] = None, createdOnMillisEpoch: Option[Long] = None, updatedBy: Option[String] = None, updatedOnMillisEpoch: Option[Long] = None) extends Logging with Entity2[String, String] {
  def id1: String = txHash

  def id2: String = nftId
}

object SaleNFTTransactions {

  private implicit val logger: Logger = Logger(this.getClass)

  private implicit val module: String = constants.Module.MASTER_TRANSACTION_SALE_NFT_TRANSACTION

  class SaleNFTTransactionTable(tag: Tag) extends Table[SaleNFTTransaction](tag, "SaleNFTTransaction") with ModelTable2[String, String] {

    def * = (txHash, nftId, buyerAccountId, sellerAccountId, saleId, status.?, createdBy.?, createdOnMillisEpoch.?, updatedBy.?, updatedOnMillisEpoch.?) <> (SaleNFTTransaction.tupled, SaleNFTTransaction.unapply)

    def txHash = column[String]("txHash", O.PrimaryKey)

    def nftId = column[String]("nftId", O.PrimaryKey)

    def buyerAccountId = column[String]("buyerAccountId")

    def sellerAccountId = column[String]("sellerAccountId")

    def saleId = column[String]("saleId")

    def status = column[Boolean]("status")

    def createdBy = column[String]("createdBy")

    def createdOnMillisEpoch = column[Long]("createdOnMillisEpoch")

    def updatedBy = column[String]("updatedBy")

    def updatedOnMillisEpoch = column[Long]("updatedOnMillisEpoch")

    def id1 = txHash

    def id2 = nftId

  }

  val TableQuery = new TableQuery(tag => new SaleNFTTransactionTable(tag))

}

@Singleton
class SaleNFTTransactions @Inject()(
                                     protected val databaseConfigProvider: DatabaseConfigProvider,
                                     blockchainAccounts: blockchain.Accounts,
                                     blockchainTransactions: blockchain.Transactions,
                                     broadcastTxSync: transactions.blockchain.BroadcastTxSync,
                                     utilitiesOperations: utilities.Operations,
                                     getUnconfirmedTxs: queries.blockchain.GetUnconfirmedTxs,
                                     getAccount: queries.blockchain.GetAccount,
                                     getAbciInfo: queries.blockchain.GetABCIInfo,
                                     masterNFTOwners: master.NFTOwners,
                                     collectionsAnalysis: analytics.CollectionsAnalysis,
                                     masterNFTs: master.NFTs,
                                     masterSales: master.Sales,
                                     blockchainTransactionNFTSales: blockchainTransaction.NFTSales,
                                     utilitiesNotification: utilities.Notification,
                                   )(implicit override val executionContext: ExecutionContext)
  extends GenericDaoImpl2[SaleNFTTransactions.SaleNFTTransactionTable, SaleNFTTransaction, String, String](
    databaseConfigProvider,
    SaleNFTTransactions.TableQuery,
    executionContext,
    SaleNFTTransactions.module,
    SaleNFTTransactions.logger
  ) {

  object Service {

    def addWithNoneStatus(buyerAccountId: String, sellerAccountId: String, txHash: String, nftId: String, saleId: String): Future[Unit] = create(SaleNFTTransaction(buyerAccountId = buyerAccountId, sellerAccountId = sellerAccountId, txHash = txHash, nftId = nftId, saleId = saleId, status = None))

    def addWithNoneStatus(buyerAccountId: String, sellerAccountId: String, txHash: String, nftIds: Seq[String], saleId: String): Future[Unit] = create(nftIds.map(x => SaleNFTTransaction(buyerAccountId = buyerAccountId, sellerAccountId = sellerAccountId, txHash = txHash, nftId = x, saleId = saleId, status = None)))

    def tryGetBySaleIdAndBuyerAccountId(buyerAccountId: String, saleId: String): Future[Seq[SaleNFTTransaction]] = filter(x => x.saleId === saleId && x.buyerAccountId === buyerAccountId)

    def checkAlreadySold(nftId: String, saleId: String): Future[Boolean] = {
      val nullStatus: Option[Boolean] = null
      filterAndExists(x => x.nftId === nftId && x.saleId === saleId && (x.status || x.status.? === nullStatus))
    }

    def checkAlreadySold(nftIds: Seq[String], saleId: String): Future[Boolean] = {
      val nullStatus: Option[Boolean] = null
      filter(x => x.nftId.inSet(nftIds) && x.saleId === saleId && (x.status || x.status.? === nullStatus)).map(_.nonEmpty)
    }

    def countBuyerNFTsFromSale(buyerAccountId: String, saleId: String): Future[Int] = {
      val nullStatus: Option[Boolean] = null
      filterAndCount(x => x.buyerAccountId === buyerAccountId && x.saleId === saleId && (x.status.? === nullStatus || x.status))
    }

    def getByTxHash(txHash: String): Future[Seq[SaleNFTTransaction]] = filter(_.txHash === txHash)

    def markSuccess(txHash: String): Future[Int] = customUpdate(SaleNFTTransactions.TableQuery.filter(_.txHash === txHash).map(_.status).update(true))

    def markFailed(txHash: String): Future[Int] = customUpdate(SaleNFTTransactions.TableQuery.filter(_.txHash === txHash).map(_.status).update(false))

    def getAllPendingStatus: Future[Seq[SaleNFTTransaction]] = filter(_.status.?.isEmpty)

    def getTotalWhitelistSaleSold(saleId: String): Future[Int] = {
      val nullStatus: Option[Boolean] = null
      filterAndCount(x => x.saleId === saleId && (x.status.? === nullStatus || x.status))
    }

    def checkAnyPendingTx(saleIDs: Seq[String]): Future[Seq[String]] = customQuery(SaleNFTTransactions.TableQuery.filter(x => x.saleId.inSet(saleIDs) && x.status.?.isEmpty).map(_.saleId).distinct.result)
  }

  object Utility {
    def transaction(buyerAccountId: String, sellerAccountId: String, nftIds: Seq[String], saleId: String, fromAddress: String, toAddress: String, amount: MicroNumber, gasPrice: BigDecimal, gasLimit: Int, ecKey: ECKey): Future[BlockchainTransaction] = {
      // TODO
      // val bcAccount = blockchainAccounts.Service.tryGet(fromAddress)
      val abciInfo = getAbciInfo.Service.get
      val bcAccount = getAccount.Service.get(fromAddress).map(_.account.toSerializableAccount)
      val unconfirmedTxs = getUnconfirmedTxs.Service.get()

      def checkMempoolAndAddTx(bcAccount: models.blockchain.Account, latestBlockHeight: Int, unconfirmedTxHashes: Seq[String]) = {
        val timeoutHeight = latestBlockHeight + constants.Blockchain.TxTimeoutHeight
        val (txRawBytes, memo) = utilities.BlockchainTransaction.getTxRawBytesWithSignedMemo(
          messages = Seq(utilities.BlockchainTransaction.getSendCoinMsgAsAny(fromAddress = fromAddress, toAddress = toAddress, amount = Seq(Coin(denom = constants.Blockchain.StakingToken, amount = amount)))),
          fee = utilities.BlockchainTransaction.getFee(gasPrice = gasPrice, gasLimit = gasLimit),
          gasLimit = gasLimit,
          account = bcAccount,
          ecKey = ecKey,
          timeoutHeight = timeoutHeight)
        val txHash = utilities.Secrets.sha256HashHexString(txRawBytes)

        def checkAndAdd(unconfirmedTxHashes: Seq[String]) = {
          if (!unconfirmedTxHashes.contains(txHash)) {
            for {
              nftSale <- blockchainTransactionNFTSales.Service.add(txHash = txHash, txRawBytes = txRawBytes, fromAddress = fromAddress, toAddress = toAddress, amount = Seq(Coin(denom = constants.Blockchain.StakingToken, amount = amount)), status = None, memo = Option(memo), timeoutHeight = timeoutHeight)
              _ <- Service.addWithNoneStatus(buyerAccountId = buyerAccountId, sellerAccountId = sellerAccountId, txHash = txHash, nftIds = nftIds, saleId = saleId)
            } yield nftSale
          }
          else constants.Response.TRANSACTION_ALREADY_IN_MEMPOOL.throwFutureBaseException()
        }

        for {
          nftSale <- checkAndAdd(unconfirmedTxHashes)
        } yield nftSale
      }

      def broadcastTxAndUpdate(nftSale: NFTSale) = {

        val broadcastTx = broadcastTxSync.Service.get(nftSale.getTxRawAsHexString)

        def update(successResponse: Option[BroadcastTxSyncResponse.Response], errorResponse: Option[BroadcastTxSyncResponse.ErrorResponse]) = if (errorResponse.nonEmpty) blockchainTransactionNFTSales.Service.updateNFTSale(nftSale.copy(status = Option(false), log = Option(errorResponse.get.error.data)))
        else if (successResponse.nonEmpty && successResponse.get.result.code != 0) blockchainTransactionNFTSales.Service.updateNFTSale(nftSale.copy(status = Option(false), log = Option(successResponse.get.result.log)))
        else Future(nftSale)

        for {
          (successResponse, errorResponse) <- broadcastTx
          updatedNFTSale <- update(successResponse, errorResponse)
        } yield updatedNFTSale
      }

      for {
        abciInfo <- abciInfo
        bcAccount <- bcAccount
        unconfirmedTxs <- unconfirmedTxs
        nftSale <- checkMempoolAndAddTx(bcAccount, abciInfo.result.response.last_block_height.toInt, unconfirmedTxs.result.txs.map(x => utilities.Secrets.base64URLDecode(x).map("%02x".format(_)).mkString.toUpperCase))
        updatedNFTSale <- broadcastTxAndUpdate(nftSale)
      } yield updatedNFTSale
    }

    val scheduler: Scheduler = new Scheduler {
      val name: String = SaleNFTTransactions.module

      def runner(): Unit = {
        val saleNFTTxS = Service.getAllPendingStatus

        def checkAndUpdate(saleNFTTxs: Seq[SaleNFTTransaction]) = utilitiesOperations.traverse(saleNFTTxs.map(_.txHash).distinct) { txHash =>
          val transaction = blockchainTransactionNFTSales.Service.tryGet(txHash)

          def onTxComplete(transaction: NFTSale) = if (transaction.status.isDefined) {
            if (transaction.status.get) {
              val boughtNFTs = saleNFTTxs.filter(_.txHash == transaction.txHash)
              val markMasterSuccess = Service.markSuccess(transaction.txHash)
              val sale = masterSales.Service.tryGet(boughtNFTs.head.saleId)
              val nft = masterNFTs.Service.tryGet(boughtNFTs.head.nftId)

              def transferNFTOwnership(boughtNFTs: Seq[SaleNFTTransaction]) = utilitiesOperations.traverse(boughtNFTs) { boughtNFT =>
                masterNFTOwners.Service.markNFTSoldFromSale(nftId = boughtNFT.nftId, saleId = boughtNFT.saleId, sellerAccountId = boughtNFT.sellerAccountId, buyerAccountId = boughtNFT.buyerAccountId)
              }

              def analysisUpdate(nft: NFT, price: MicroNumber, quantity: Int) = collectionsAnalysis.Utility.onSuccessfulSell(collectionId = nft.collectionId, price = price, quantity = quantity)

              def sendNotifications(boughtNFT: SaleNFTTransaction, count: Int) = {
                utilitiesNotification.send(boughtNFT.sellerAccountId, constants.Notification.SELLER_BUY_NFT_SUCCESSFUL_FROM_SALE, count.toString)("")
                utilitiesNotification.send(boughtNFT.buyerAccountId, constants.Notification.BUYER_BUY_NFT_SUCCESSFUL_FROM_SALE, count.toString)(s"'${boughtNFT.buyerAccountId}', '${constants.View.COLLECTED}'")
              }

              (for {
                _ <- markMasterSuccess
                _ <- transferNFTOwnership(boughtNFTs)
                sale <- sale
                nft <- nft
                _ <- analysisUpdate(nft, sale.price, boughtNFTs.length)
                _ <- sendNotifications(boughtNFTs.head, boughtNFTs.length)
              } yield ()
                ).recover {
                case _: BaseException => logger.error("[PANIC] Something is seriously wrong with logic. Code should not reach here.")
              }
            } else {
              val boughtNFTs = Service.getByTxHash(transaction.txHash)
              val markMasterFailed = Service.markFailed(transaction.txHash)

              def sendNotifications(buyNFTTx: SaleNFTTransaction, count: Int) = utilitiesNotification.send(buyNFTTx.buyerAccountId, constants.Notification.BUYER_BUY_NFT_FAILED, count.toString)("")

              (for {
                boughtNFTs <- boughtNFTs
                _ <- markMasterFailed
                _ <- sendNotifications(boughtNFTs.head, boughtNFTs.length)
              } yield ()
                ).recover {
                case _: BaseException => logger.error("[PANIC] Something is seriously wrong with logic. Code should not reach here.")
              }
            }
          } else Future()

          for {
            transaction <- transaction
            _ <- onTxComplete(transaction)
          } yield ()
        }

        val forComplete = (for {
          saleNFTTxS <- saleNFTTxS
          _ <- checkAndUpdate(saleNFTTxS)
        } yield ()).recover {
          case baseException: BaseException => logger.error(baseException.failure.logMessage)
        }

        Await.result(forComplete, Duration.Inf)
      }
    }
  }
}