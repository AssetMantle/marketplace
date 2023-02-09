package models.masterTransaction

import constants.Scheduler
import exceptions.BaseException
import models.Trait._
import models.blockchainTransaction.NFTPublicListing
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

case class PublicListingNFTTransaction(txHash: String, nftId: String, buyerAccountId: String, sellerAccountId: String, publicListingId: String, status: Option[Boolean], createdBy: Option[String] = None, createdOnMillisEpoch: Option[Long] = None, updatedBy: Option[String] = None, updatedOnMillisEpoch: Option[Long] = None) extends Logging with Entity2[String, String] {
  def id1: String = txHash

  def id2: String = nftId
}

object PublicListingNFTTransactions {

  private implicit val logger: Logger = Logger(this.getClass)

  private implicit val module: String = constants.Module.MASTER_TRANSACTION_SALE_NFT_TRANSACTION

  class PublicListingNFTTransactionTable(tag: Tag) extends Table[PublicListingNFTTransaction](tag, "PublicListingNFTTransaction") with ModelTable2[String, String] {

    def * = (txHash, nftId, buyerAccountId, sellerAccountId, publicListingId, status.?, createdBy.?, createdOnMillisEpoch.?, updatedBy.?, updatedOnMillisEpoch.?) <> (PublicListingNFTTransaction.tupled, PublicListingNFTTransaction.unapply)

    def txHash = column[String]("txHash", O.PrimaryKey)

    def nftId = column[String]("nftId", O.PrimaryKey)

    def buyerAccountId = column[String]("buyerAccountId")

    def sellerAccountId = column[String]("sellerAccountId")

    def publicListingId = column[String]("publicListingId")

    def status = column[Boolean]("status")

    def createdBy = column[String]("createdBy")

    def createdOnMillisEpoch = column[Long]("createdOnMillisEpoch")

    def updatedBy = column[String]("updatedBy")

    def updatedOnMillisEpoch = column[Long]("updatedOnMillisEpoch")

    def id1 = txHash

    def id2 = nftId

  }

  val TableQuery = new TableQuery(tag => new PublicListingNFTTransactionTable(tag))

}

@Singleton
class PublicListingNFTTransactions @Inject()(
                                              protected val databaseConfigProvider: DatabaseConfigProvider,
                                              blockchainAccounts: blockchain.Accounts,
                                              blockchainTransactionNFTPublicListings: blockchainTransaction.NFTPublicListings,
                                              broadcastTxSync: transactions.blockchain.BroadcastTxSync,
                                              utilitiesOperations: utilities.Operations,
                                              getUnconfirmedTxs: queries.blockchain.GetUnconfirmedTxs,
                                              getAccount: queries.blockchain.GetAccount,
                                              getAbciInfo: queries.blockchain.GetABCIInfo,
                                              masterCollections: master.Collections,
                                              masterNFTs: master.NFTs,
                                              masterNFTOwners: master.NFTOwners,
                                              masterSales: master.Sales,
                                              masterPublicListings: master.PublicListings,
                                              collectionsAnalysis: analytics.CollectionsAnalysis,
                                              utilitiesNotification: utilities.Notification,
                                            )(implicit override val executionContext: ExecutionContext)
  extends GenericDaoImpl2[PublicListingNFTTransactions.PublicListingNFTTransactionTable, PublicListingNFTTransaction, String, String](
    databaseConfigProvider,
    PublicListingNFTTransactions.TableQuery,
    executionContext,
    PublicListingNFTTransactions.module,
    PublicListingNFTTransactions.logger
  ) {

  object Service {

    def addWithNoneStatus(buyerAccountId: String, sellerAccountId: String, txHash: String, nftIds: Seq[String], publicListingId: String): Future[Unit] = create(nftIds.map(x => PublicListingNFTTransaction(buyerAccountId = buyerAccountId, sellerAccountId = sellerAccountId, txHash = txHash, nftId = x, publicListingId = publicListingId, status = None)))

    def checkAlreadySold(nftIds: Seq[String], publicListingId: String): Future[Boolean] = {
      val nullStatus: Option[Boolean] = null
      filter(x => x.nftId.inSet(nftIds) && x.publicListingId === publicListingId && (x.status || x.status.? === nullStatus)).map(_.nonEmpty)
    }

    def countBuyerNFTsFromPublicListing(buyerAccountId: String, publicListingId: String): Future[Int] = {
      val nullStatus: Option[Boolean] = null
      filterAndCount(x => x.buyerAccountId === buyerAccountId && x.publicListingId === publicListingId && (x.status.? === nullStatus || x.status))
    }

    def getTotalPublicListingSold(publicListingId: String): Future[Int] = {
      val nullStatus: Option[Boolean] = null
      filterAndCount(x => x.publicListingId === publicListingId && (x.status.? === nullStatus || x.status))
    }

    def getByTxHash(txHash: String): Future[Seq[PublicListingNFTTransaction]] = filter(_.txHash === txHash)

    def markSuccess(txHash: String): Future[Int] = customUpdate(PublicListingNFTTransactions.TableQuery.filter(_.txHash === txHash).map(_.status).update(true))

    def markFailed(txHash: String): Future[Int] = customUpdate(PublicListingNFTTransactions.TableQuery.filter(_.txHash === txHash).map(_.status).update(false))

    def getAllPendingStatus: Future[Seq[PublicListingNFTTransaction]] = filter(_.status.?.isEmpty)

    def checkAnyPendingTx(publicListingIDs: Seq[String]): Future[Seq[String]] = customQuery(PublicListingNFTTransactions.TableQuery.filter(_.publicListingId.inSet(publicListingIDs)).map(_.publicListingId).distinct.result)
  }

  object Utility {
    def transaction(buyerAccountId: String, sellerAccountId: String, nftIds: Seq[String], publicListingId: String, fromAddress: String, toAddress: String, amount: MicroNumber, gasPrice: BigDecimal, gasLimit: Int, ecKey: ECKey): Future[BlockchainTransaction] = {
      // TODO
      // val bcAccount = blockchainAccounts.Service.tryGet(fromAddress)
      val abciInfo = getAbciInfo.Service.get
      val bcAccount = getAccount.Service.get(fromAddress).map(_.account.toSerializableAccount(fromAddress))
      val unconfirmedTxs = getUnconfirmedTxs.Service.get()

      def checkMempoolAndAddTx(bcAccount: models.blockchain.Account, latestBlockHeight: Long, unconfirmedTxHashes: Seq[String]) = {
        val (txRawBytes, memo) = utilities.BlockchainTransaction.getTxRawBytesWithSignedMemo(
          messages = Seq(utilities.BlockchainTransaction.getSendCoinMsgAsAny(fromAddress = fromAddress, toAddress = toAddress, amount = Seq(Coin(denom = constants.Blockchain.StakingToken, amount = amount)))),
          fee = utilities.BlockchainTransaction.getFee(gasPrice = gasPrice, gasLimit = gasLimit),
          gasLimit = gasLimit,
          account = bcAccount,
          ecKey = ecKey,
          timeoutHeight = latestBlockHeight + constants.Blockchain.TxTimeoutHeight)
        val txHash = utilities.Secrets.sha256HashHexString(txRawBytes)

        def checkAndAdd(unconfirmedTxHashes: Seq[String]) = {
          if (!unconfirmedTxHashes.contains(txHash)) {
            for {
              nftSale <- blockchainTransactionNFTPublicListings.Service.add(txHash = txHash, txRawBytes = txRawBytes, fromAddress = fromAddress, toAddress = toAddress, amount = Seq(Coin(denom = constants.Blockchain.StakingToken, amount = amount)), status = None, memo = Option(memo))
              _ <- Service.addWithNoneStatus(buyerAccountId = buyerAccountId, sellerAccountId = sellerAccountId, txHash = txHash, nftIds = nftIds, publicListingId = publicListingId)
            } yield nftSale
          } else constants.Response.TRANSACTION_ALREADY_IN_MEMPOOL.throwFutureBaseException()
        }

        for {
          nftSale <- checkAndAdd(unconfirmedTxHashes)
        } yield nftSale
      }

      def broadcastTxAndUpdate(nftPublicListing: NFTPublicListing) = {

        val broadcastTx = broadcastTxSync.Service.get(nftPublicListing.getTxRawAsHexString)

        def update(successResponse: Option[BroadcastTxSyncResponse.Response], errorResponse: Option[BroadcastTxSyncResponse.ErrorResponse]) = if (errorResponse.nonEmpty) blockchainTransactionNFTPublicListings.Service.updateNFTPublicListing(nftPublicListing.copy(status = Option(false), log = Option(errorResponse.get.error.data)))
        else if (successResponse.nonEmpty && successResponse.get.result.code != 0) blockchainTransactionNFTPublicListings.Service.updateNFTPublicListing(nftPublicListing.copy(status = Option(false), log = Option(successResponse.get.result.log)))
        else Future(nftPublicListing)

        for {
          (successResponse, errorResponse) <- broadcastTx
          updatedNFTSale <- update(successResponse, errorResponse)
        } yield updatedNFTSale
      }

      for {
        abciInfo <- abciInfo
        bcAccount <- bcAccount
        unconfirmedTxs <- unconfirmedTxs
        nftSale <- checkMempoolAndAddTx(bcAccount, abciInfo.result.response.last_block_height.toLong, unconfirmedTxs.result.txs.map(x => utilities.Secrets.base64URLDecode(x).map("%02x".format(_)).mkString.toUpperCase))
        updatedNFTSale <- broadcastTxAndUpdate(nftSale)
      } yield updatedNFTSale
    }

    val scheduler: Scheduler = new Scheduler {
      val name: String = constants.Scheduler.MASTER_TRANSACTION_NFT_PUBLIC_LISTING

      def runner(): Unit = {
        val nftPublicListingTxs = Service.getAllPendingStatus

        def checkAndUpdate(nftPublicListingTxs: Seq[PublicListingNFTTransaction]) = utilitiesOperations.traverse(nftPublicListingTxs.map(_.txHash).distinct) { txHash =>
          val transaction = blockchainTransactionNFTPublicListings.Service.tryGet(txHash)

          def onTxComplete(transaction: NFTPublicListing) = if (transaction.status.isDefined) {
            if (transaction.status.get) {
              val boughtNFTs = nftPublicListingTxs.filter(_.txHash == txHash)
              val markSuccess = Service.markSuccess(txHash)
              val publicListing = masterPublicListings.Service.tryGet(boughtNFTs.head.publicListingId)
              val nft = masterNFTs.Service.tryGet(boughtNFTs.head.nftId)

              def transferNFTOwnership(boughtNFTs: Seq[PublicListingNFTTransaction]) = utilitiesOperations.traverse(boughtNFTs) { boughtNFT =>
                masterNFTOwners.Service.markNFTSoldFromPublicListing(nftId = boughtNFT.nftId, publicListingId = boughtNFT.publicListingId, sellerAccountId = boughtNFT.sellerAccountId, buyerAccountId = boughtNFT.buyerAccountId)
              }

              def analysisUpdate(nft: NFT, quantity: Int, price: MicroNumber) = collectionsAnalysis.Utility.onSuccessfulSell(collectionId = nft.collectionId, price = price, quantity = quantity)

              def sendNotifications(boughtNFT: PublicListingNFTTransaction, count: Int) = {
                utilitiesNotification.send(boughtNFT.sellerAccountId, constants.Notification.SELLER_BUY_NFT_SUCCESSFUL_FROM_PUBLIC_LISTING, count.toString)("")
                utilitiesNotification.send(boughtNFT.buyerAccountId, constants.Notification.BUYER_BUY_NFT_SUCCESSFUL_FROM_PUBLIC_LISTING, count.toString)(s"'${boughtNFT.buyerAccountId}', '${constants.View.COLLECTED}'")
              }

              (for {
                _ <- markSuccess
                publicListing <- publicListing
                _ <- transferNFTOwnership(boughtNFTs)
                nft <- nft
                _ <- analysisUpdate(nft, boughtNFTs.length, publicListing.price)
                _ <- sendNotifications(boughtNFTs.head, boughtNFTs.length)
              } yield ()
                ).recover {
                case _: BaseException => logger.error("[PANIC] Something is seriously wrong with logic. Code should not reach here.")
              }
            } else {
              val boughtNFTs = Service.getByTxHash(txHash)
              val markMasterFailed = Service.markFailed(txHash)

              def sendNotifications(buyNFTTx: PublicListingNFTTransaction, count: Int) = utilitiesNotification.send(buyNFTTx.buyerAccountId, constants.Notification.BUYER_BUY_NFT_FAILED, count.toString)("")

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
          nftPublicListingTxs <- nftPublicListingTxs
          _ <- checkAndUpdate(nftPublicListingTxs)
        } yield ()).recover {
          case baseException: BaseException => logger.error(baseException.failure.logMessage)
        }

        Await.result(forComplete, Duration.Inf)
      }
    }
  }

}