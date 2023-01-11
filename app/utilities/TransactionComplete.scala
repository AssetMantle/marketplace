package utilities

import exceptions.BaseException
import models.blockchain.Transaction
import models.master.NFT
import models.masterTransaction.BuyNFTTransaction
import models.{analytics, blockchain, master, masterTransaction}
import play.api.Logger

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class TransactionComplete @Inject()(
                                     collectionsAnalysis: analytics.CollectionsAnalysis,
                                     blockchainBalances: blockchain.Balances,
                                     masterAccounts: master.Accounts,
                                     masterKeys: master.Keys,
                                     masterWhitelists: master.Whitelists,
                                     masterCollections: master.Collections,
                                     masterNFTs: master.NFTs,
                                     masterNFTOwners: master.NFTOwners,
                                     masterSales: master.Sales,
                                     masterWhitelistMembers: master.WhitelistMembers,
                                     masterTransactionBuyNFTTransactions: masterTransaction.BuyNFTTransactions,
                                     utilitiesNotification: utilities.Notification,
                                     utilitiesOperations: utilities.Operations,
                                   )(implicit executionContext: ExecutionContext) {

  private implicit val logger: Logger = Logger(this.getClass)

  private implicit val module: String = constants.Module.UTILITIES_SALE

  def onBuyNFT(transaction: Transaction, price: MicroNumber): Future[Unit] = if (transaction.status) {
    val boughtNFTs = masterTransactionBuyNFTTransactions.Service.getByTxHash(transaction.hash)
    val markMasterSuccess = masterTransactionBuyNFTTransactions.Service.markSuccess(transaction.hash)

    def transferNFTOwnership(boughtNFTs: Seq[BuyNFTTransaction]) = utilitiesOperations.traverse(boughtNFTs){ boughtNFT =>
      masterNFTOwners.Service.markNFTSold(nftId = boughtNFT.nftId, saleId = boughtNFT.saleId, sellerAccountId = boughtNFT.sellerAccountId, buyerAccountId = boughtNFT.buyerAccountId)
    }

    def nft(buyNFTTx: BuyNFTTransaction) = masterNFTs.Service.tryGet(buyNFTTx.nftId)

    def analysisUpdate(nft: NFT) = collectionsAnalysis.Utility.onSuccessfulSale(collectionId = nft.collectionId, price = price)

    def sendNotifications(boughtNFT: BuyNFTTransaction, count: Int) = {
      utilitiesNotification.send(boughtNFT.sellerAccountId, constants.Notification.SELLER_BUY_NFT_SUCCESSFUL, count.toString)("")
      utilitiesNotification.send(boughtNFT.buyerAccountId, constants.Notification.BUYER_BUY_NFT_SUCCESSFUL, count.toString)(s"'${boughtNFT.buyerAccountId}', '${constants.View.COLLECTED}'")
    }

    (for {
      boughtNFTs <- boughtNFTs
      _ <- transferNFTOwnership(boughtNFTs)
      _ <- markMasterSuccess
      nft <- nft(boughtNFTs.head)
      _ <- analysisUpdate(nft)
      _ <- sendNotifications(boughtNFTs.head, boughtNFTs.length)
    } yield ()
      ).recover {
      case _: BaseException => logger.error("[PANIC] Something is seriously wrong with logic. Code should not reach here.")
    }
  } else {
    val boughtNFTs = masterTransactionBuyNFTTransactions.Service.getByTxHash(transaction.hash)
    val markMasterFailed = masterTransactionBuyNFTTransactions.Service.markFailed(transaction.hash)

    def sendNotifications(buyNFTTx: BuyNFTTransaction, count: Int) = utilitiesNotification.send(buyNFTTx.buyerAccountId, constants.Notification.BUYER_BUY_NFT_FAILED, count.toString)("")

    (for {
      boughtNFTs <- boughtNFTs
      _ <- markMasterFailed
      _ <- sendNotifications(boughtNFTs.head, boughtNFTs.length)
    } yield ()
      ).recover {
      case _: BaseException => logger.error("[PANIC] Something is seriously wrong with logic. Code should not reach here.")
    }
  }


}