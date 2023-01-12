package utilities

import exceptions.BaseException
import models.blockchain.Transaction
import models.master.NFT
import models.masterTransaction._
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
                                     masterTransactionSaleNFTTransactions: masterTransaction.SaleNFTTransactions,
                                     masterTransactionPublicListingNFTTransactions: masterTransaction.PublicListingNFTTransactions,
                                     utilitiesNotification: utilities.Notification,
                                     utilitiesOperations: utilities.Operations,
                                   )(implicit executionContext: ExecutionContext) {

  private implicit val logger: Logger = Logger(this.getClass)

  private implicit val module: String = constants.Module.UTILITIES_SALE

  def onNFTSale(transaction: Transaction, price: MicroNumber): Future[Unit] = {
    if (transaction.status) {
      val boughtNFTs = masterTransactionSaleNFTTransactions.Service.getByTxHash(transaction.hash)
      val markMasterSuccess = masterTransactionSaleNFTTransactions.Service.markSuccess(transaction.hash)

      def transferNFTOwnership(boughtNFTs: Seq[SaleNFTTransaction]) = utilitiesOperations.traverse(boughtNFTs){ boughtNFT =>
        masterNFTOwners.Service.markNFTSoldFromSale(nftId = boughtNFT.nftId, saleId = boughtNFT.saleId, sellerAccountId = boughtNFT.sellerAccountId, buyerAccountId = boughtNFT.buyerAccountId)
      }

      def nft(buyNFTTx: SaleNFTTransaction) = masterNFTs.Service.tryGet(buyNFTTx.nftId)

      def analysisUpdate(nft: NFT) = collectionsAnalysis.Utility.onSuccessfulSell(collectionId = nft.collectionId, price = price)

      def sendNotifications(boughtNFT: SaleNFTTransaction, count: Int) = {
        utilitiesNotification.send(boughtNFT.sellerAccountId, constants.Notification.SELLER_BUY_NFT_SUCCESSFUL_FROM_SALE, count.toString)("")
        utilitiesNotification.send(boughtNFT.buyerAccountId, constants.Notification.BUYER_BUY_NFT_SUCCESSFUL_FROM_SALE, count.toString)(s"'${boughtNFT.buyerAccountId}', '${constants.View.COLLECTED}'")
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
      val boughtNFTs = masterTransactionSaleNFTTransactions.Service.getByTxHash(transaction.hash)
      val markMasterFailed = masterTransactionSaleNFTTransactions.Service.markFailed(transaction.hash)

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
  }

  def onNFTPublicListing(transaction: Transaction, price: MicroNumber): Future[Unit] = {
    if (transaction.status) {
      val boughtNFT = masterTransactionPublicListingNFTTransactions.Service.tryGetByTxHash(transaction.hash)
      val markMasterSuccess = masterTransactionPublicListingNFTTransactions.Service.markSuccess(transaction.hash)

      def transferNFTOwnership(boughtNFT: PublicListingNFTTransaction) = masterNFTOwners.Service.markNFTSoldFromPublicListing(nftId = boughtNFT.nftId, publicListingId = boughtNFT.publicListingId, sellerAccountId = boughtNFT.sellerAccountId, buyerAccountId = boughtNFT.buyerAccountId)

      def nft(buyNFTTx: PublicListingNFTTransaction) = masterNFTs.Service.tryGet(buyNFTTx.nftId)

      def analysisUpdate(nft: NFT) = collectionsAnalysis.Utility.onSuccessfulSell(collectionId = nft.collectionId, price = price)

      def sendNotifications(boughtNFT: PublicListingNFTTransaction, name: String) = {
        utilitiesNotification.send(boughtNFT.sellerAccountId, constants.Notification.SELLER_BUY_NFT_SUCCESSFUL_FROM_PUBLIC_LISTING, name)("")
        utilitiesNotification.send(boughtNFT.buyerAccountId, constants.Notification.BUYER_BUY_NFT_SUCCESSFUL_FROM_PUBLIC_LISTING, name)(s"'${boughtNFT.buyerAccountId}', '${constants.View.COLLECTED}'")
      }

      (for {
        boughtNFT <- boughtNFT
        _ <- transferNFTOwnership(boughtNFT)
        _ <- markMasterSuccess
        nft <- nft(boughtNFT)
        _ <- analysisUpdate(nft)
        _ <- sendNotifications(boughtNFT, nft.name)
      } yield ()
        ).recover {
        case _: BaseException => logger.error("[PANIC] Something is seriously wrong with logic. Code should not reach here.")
      }
    } else {
      val boughtNFT = masterTransactionPublicListingNFTTransactions.Service.tryGetByTxHash(transaction.hash)
      val markMasterFailed = masterTransactionPublicListingNFTTransactions.Service.markFailed(transaction.hash)
      def nft(nftId: String) = masterNFTs.Service.tryGet(nftId)

      def sendNotifications(buyNFTTx: PublicListingNFTTransaction, name: String) = utilitiesNotification.send(buyNFTTx.buyerAccountId, constants.Notification.BUYER_BUY_NFT_FAILED, name)("")

      (for {
        boughtNFT <- boughtNFT
        _ <- markMasterFailed
        nft <- nft(boughtNFT.nftId)
        _ <- sendNotifications(boughtNFT, nft.name)
      } yield ()
        ).recover {
        case _: BaseException => logger.error("[PANIC] Something is seriously wrong with logic. Code should not reach here.")
      }
    }
  }


}