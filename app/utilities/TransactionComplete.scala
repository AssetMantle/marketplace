package utilities

import exceptions.BaseException
import models.blockchain.Transaction
import models.master.NFT
import models.{analytics, blockchain, master}
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
                                     utilitiesNotification: utilities.Notification,
                                   )(implicit executionContext: ExecutionContext) {

  private implicit val logger: Logger = Logger(this.getClass)

  private implicit val module: String = constants.Module.UTILITIES_SALE

  def onBuyAssetWithoutMint(transaction: Transaction, nftId: String, buyerAccountId: String, sellerAccountId: String, saleId: String, price: MicroNumber): Future[Unit] = {

    val transferNFTOwnership = if (transaction.status) masterNFTOwners.Service.markNFTSold(nftId = nftId, saleId = saleId, sellerAccountId = sellerAccountId, buyerAccountId = buyerAccountId)
    else Future()
    val nft = masterNFTs.Service.tryGet(nftId)

    def analysisUpdate(nft: NFT) = if (transaction.status) collectionsAnalysis.Utility.onSale(collectionId = nft.collectionId, price = price) else Future()

    def sendNotifications(nft: NFT) = if (transaction.status) {
      utilitiesNotification.send(sellerAccountId, constants.Notification.SELLER_NFT_SALE_WITHOUT_MINT_SUCCESSFUL, nft.name)(s"'${nft.id}'")
      utilitiesNotification.send(buyerAccountId, constants.Notification.BUYER_NFT_SALE_WITHOUT_MINT_SUCCESSFUL, nft.name)(s"'${nft.id}'")
    } else {
      utilitiesNotification.send(sellerAccountId, constants.Notification.SELLER_NFT_SALE_WITHOUT_MINT_FAILED, nft.name)(s"'${nft.id}'")
      utilitiesNotification.send(buyerAccountId, constants.Notification.BUYER_NFT_SALE_WITHOUT_MINT_FAILED, nft.name)(s"'${nft.id}'")
    }

    (for {
      _ <- transferNFTOwnership
      nft <- nft
      _ <- analysisUpdate(nft)
      _ <- sendNotifications(nft)
    } yield ()
      ).recover {
      case _: BaseException => logger.error("[PANIC] Something is seriously wrong with logic. Code should not reach here.")
    }
  }

  def onBuyAndMintAsset(transaction: Transaction, nftId: String, buyerAccountId: String, sellerAccountId: String, saleId: String, price: MicroNumber): Future[Unit] = {

    val transferNFTOwnership = if (transaction.status) {
      for {
        _ <- masterNFTOwners.Service.markNFTSold(nftId = nftId, saleId = saleId, sellerAccountId = sellerAccountId, buyerAccountId = buyerAccountId)
        _ <- masterNFTs.Service.markNFTMinted(nftId)
      } yield ()
    } else Future()

    val nft = masterNFTs.Service.tryGet(nftId)

    def analysisUpdate(nft: NFT) = if (transaction.status) collectionsAnalysis.Utility.onMintAndSale(collectionId = nft.collectionId, price = price) else Future()

    def sendNotifications(nft: NFT) = if (transaction.status) {
      utilitiesNotification.send(sellerAccountId, constants.Notification.SELLER_NFT_SALE_WITHOUT_MINT_SUCCESSFUL, nft.name)(s"'${nft.id}'")
      utilitiesNotification.send(buyerAccountId, constants.Notification.BUYER_NFT_SALE_WITHOUT_MINT_SUCCESSFUL, nft.name)(s"'${nft.id}'")
    } else {
      utilitiesNotification.send(sellerAccountId, constants.Notification.SELLER_NFT_SALE_WITHOUT_MINT_FAILED, nft.name)(s"'${nft.id}'")
      utilitiesNotification.send(buyerAccountId, constants.Notification.BUYER_NFT_SALE_WITHOUT_MINT_FAILED, nft.name)(s"'${nft.id}'")
    }


    (for {
      _ <- transferNFTOwnership
      nft <- nft
      _ <- analysisUpdate(nft)
      _ <- sendNotifications(nft)
    } yield ()
      ).recover {
      case _: BaseException => logger.error("[PANIC] Something is seriously wrong with logic. Code should not reach here.")
    }
  }

  def onMint(transaction: Transaction, nftId: String, fromAccountId: String): Future[Unit] = {

    val markAsMinted = if (transaction.status) {
      masterNFTs.Service.markNFTMinted(nftId)
    } else Future()
    val nft = masterNFTs.Service.tryGet(nftId)

    def analysisUpdate(nft: NFT) = collectionsAnalysis.Utility.onMint(collectionId = nft.collectionId)

    def sendNotifications(nft: NFT) = if (transaction.status) {
      utilitiesNotification.send(fromAccountId, constants.Notification.NFT_MINT_SUCCESSFUL, nft.name)(s"'${nft.id}'")
    } else {
      utilitiesNotification.send(fromAccountId, constants.Notification.NFT_MINT_FAILED, nft.name)(s"'${nft.id}'")
    }


    (for {
      _ <- markAsMinted
      nft <- nft
      _ <- analysisUpdate(nft)
      _ <- sendNotifications(nft)
    } yield ()
      ).recover {
      case _: BaseException => logger.error("[PANIC] Something is seriously wrong with logic. Code should not reach here.")
    }
  }

  def onNub(transaction: Transaction, fromAccountId: String): Future[Unit] = {
    val a = Future()


    (for {
      _ <- a
    } yield ()
      ).recover {
      case _: BaseException => logger.error("[PANIC] Something is seriously wrong with logic. Code should not reach here.")
    }
  }


}