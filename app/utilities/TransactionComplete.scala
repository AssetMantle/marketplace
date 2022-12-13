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
                                   )(implicit executionContext: ExecutionContext) {

  private implicit val logger: Logger = Logger(this.getClass)

  private implicit val module: String = constants.Module.UTILITIES_SALE

  def onBuyNFT(transaction: Transaction, price: MicroNumber): Future[Unit] = if (transaction.status) {

    val buyNFTTx = masterTransactionBuyNFTTransactions.Service.tryGetByTxHash(transaction.hash)

    def transferNFTOwnership(buyNFTTx: BuyNFTTransaction) = masterNFTOwners.Service.markNFTSold(nftId = buyNFTTx.nftId, saleId = buyNFTTx.saleId, sellerAccountId = buyNFTTx.sellerAccountId, buyerAccountId = buyNFTTx.buyerAccountId)

    def nft(buyNFTTx: BuyNFTTransaction) = masterNFTs.Service.tryGet(buyNFTTx.nftId)

    def analysisUpdate(nft: NFT) = collectionsAnalysis.Utility.onSale(collectionId = nft.collectionId, price = price)

    def sendNotifications(nft: NFT, buyNFTTx: BuyNFTTransaction) = {
      utilitiesNotification.send(buyNFTTx.sellerAccountId, constants.Notification.SELLER_BUY_NFT_SUCCESSFUL, nft.name)(s"'${nft.id}'")
      utilitiesNotification.send(buyNFTTx.buyerAccountId, constants.Notification.BUYER_BUY_NFT_SUCCESSFUL, nft.name)(s"'${nft.id}'")
    }

    (for {
      buyNFTTx <- buyNFTTx
      _ <- transferNFTOwnership(buyNFTTx)
      nft <- nft(buyNFTTx)
      _ <- analysisUpdate(nft)
      _ <- sendNotifications(nft, buyNFTTx)
    } yield ()
      ).recover {
      case _: BaseException => logger.error("[PANIC] Something is seriously wrong with logic. Code should not reach here.")
    }
  } else {
    val buyNFTTx = masterTransactionBuyNFTTransactions.Service.tryGetByTxHash(transaction.hash)

    def nft(buyNFTTx: BuyNFTTransaction) = masterNFTs.Service.tryGet(buyNFTTx.nftId)

    def sendNotifications(nft: NFT, buyNFTTx: BuyNFTTransaction) = {
      utilitiesNotification.send(buyNFTTx.sellerAccountId, constants.Notification.SELLER_BUY_NFT_FAILED, nft.name)(s"'${nft.id}'")
      utilitiesNotification.send(buyNFTTx.buyerAccountId, constants.Notification.BUYER_BUY_NFT_FAILED, nft.name)(s"'${nft.id}'")
    }

    (for {
      buyNFTTx <- buyNFTTx
      nft <- nft(buyNFTTx)
      _ <- sendNotifications(nft, buyNFTTx)
    } yield ()
      ).recover {
      case _: BaseException => logger.error("[PANIC] Something is seriously wrong with logic. Code should not reach here.")
    }
  }


}