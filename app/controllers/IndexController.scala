package controllers

import akka.actor.CoordinatedShutdown
import controllers.actions._
import controllers.result.WithUsernameToken
import models._
import models.blockchainTransaction.{AdminTransactions, UserTransactions}
import play.api.Logger
import play.api.cache.Cached
import play.api.i18n.I18nSupport
import play.api.mvc._
import service.Starter

import javax.inject._
import scala.concurrent.duration.{DAYS, Duration}
import scala.concurrent.{Await, ExecutionContext, Future}

@Singleton
class IndexController @Inject()(
                                 messagesControllerComponents: MessagesControllerComponents,
                                 cached: Cached,
                                 withoutLoginActionAsync: WithoutLoginActionAsync,
                                 withoutLoginAction: WithoutLoginAction,
                                 withUsernameToken: WithUsernameToken,
                                 starter: Starter,
                                 blockchainBlocks: blockchain.Blocks,
                                 coordinatedShutdown: CoordinatedShutdown,
                                 historyMasterSales: history.MasterSales,
                                 historyMasterPublicListings: history.MasterPublicListings,
                                 historyMasterSecondaryMarkets: history.MasterSecondaryMarkets,
                                 nftPublicListings: blockchainTransaction.NFTPublicListings,
                                 sendCoins: blockchainTransaction.SendCoins,
                                 sendCoinTransactions: masterTransaction.SendCoinTransactions,
                                 nftSales: blockchainTransaction.NFTSales,
                                 mintNFTAirDrops: campaign.MintNFTAirDrops,
                                 masterTransactionLatestBlocks: masterTransaction.LatestBlocks,
                                 masterTransactionTokenPrices: masterTransaction.TokenPrices,
                                 publicListingNFTTransactions: masterTransaction.PublicListingNFTTransactions,
                                 issueIdentityTransactions: masterTransaction.IssueIdentityTransactions,
                                 defineAssetTransactions: masterTransaction.DefineAssetTransactions,
                                 mintAssetTransactions: masterTransaction.MintAssetTransactions,
                                 nftMintingFeeTransactions: masterTransaction.NFTMintingFeeTransactions,
                                 nftTransferTransactions: masterTransaction.NFTTransferTransactions,
                                 saleNFTTransactions: masterTransaction.SaleNFTTransactions,
                                 masterTransactionSessionTokens: masterTransaction.SessionTokens,
                                 makeOrderTransactions: masterTransaction.MakeOrderTransactions,
                                 takeOrderTransactions: masterTransaction.TakeOrderTransactions,
                                 cancelOrderTransactions: masterTransaction.CancelOrderTransactions,
                                 unwrapTransactions: masterTransaction.UnwrapTransactions,
                                 wrapTransactions: masterTransaction.WrapTransactions,
                                 provisionAddressTransactions: masterTransaction.ProvisionAddressTransactions,
                                 unprovisionAddressTransactions: masterTransaction.UnprovisionAddressTransactions,
                                 userTransactions: UserTransactions,
                                 adminTransactions: AdminTransactions,
                               )(implicit executionContext: ExecutionContext) extends AbstractController(messagesControllerComponents) with I18nSupport {

  implicit val logger: Logger = Logger(this.getClass)

  implicit val module: String = constants.Module.INDEX_CONTROLLER

  implicit val callbackOnSessionTimeout: Call = routes.CollectionController.viewCollections()

  def index: Action[AnyContent] = withoutLoginActionAsync { implicit loginState =>
    implicit request =>
      Future(Ok(views.html.index()))
  }

  def sitemap: EssentialAction = cached(req => utilities.Session.getSessionCachingKey(req), Duration(7, DAYS)) {
    withoutLoginAction { implicit request =>
      Ok(utilities.Sitemap.generate).as("application/xml; charset=utf-8")
    }
  }

  Await.result(starter.fixMantleMonkeys(), Duration.Inf)
  Await.result(starter.correctCollectionProperties(), Duration.Inf)
  Await.result(nftPublicListings.Utility.migrate, Duration.Inf)
  Await.result(nftSales.Utility.migrate, Duration.Inf)
  Await.result(sendCoins.Utility.migrate, Duration.Inf)
  starter.changeAwsKey()

  //  starter.start()

  Await.result(starter.updateIdentityIDs(), Duration.Inf)
  Await.result(starter.updateAssetIDs(), Duration.Inf)
  Await.result(starter.markMintReady(), Duration.Inf)
  starter.fixAllMultipleActiveKeys()

  utilities.Scheduler.startSchedulers(
    // blockchain
    blockchainBlocks.Utility.scheduler,
    // blockchainTransaction
    adminTransactions.Utility.scheduler,
    userTransactions.Utility.scheduler,
    // campaign
    mintNFTAirDrops.Utility.scheduler,
    // history
    historyMasterPublicListings.Utility.scheduler,
    historyMasterSales.Utility.scheduler,
    historyMasterSecondaryMarkets.Utility.scheduler,
    // masterTransaction
    cancelOrderTransactions.Utility.scheduler,
    defineAssetTransactions.Utility.scheduler,
    issueIdentityTransactions.Utility.scheduler,
    masterTransactionLatestBlocks.Utility.scheduler,
    makeOrderTransactions.Utility.scheduler,
    mintAssetTransactions.Utility.scheduler,
    nftMintingFeeTransactions.Utility.scheduler,
    nftTransferTransactions.Utility.scheduler,
    provisionAddressTransactions.Utility.scheduler,
    publicListingNFTTransactions.Utility.scheduler,
    saleNFTTransactions.Utility.scheduler,
    masterTransactionSessionTokens.Utility.scheduler,
    sendCoinTransactions.Utility.scheduler,
    takeOrderTransactions.Utility.scheduler,
    masterTransactionTokenPrices.Utility.scheduler,
    unprovisionAddressTransactions.Utility.scheduler,
    unwrapTransactions.Utility.scheduler,
    wrapTransactions.Utility.scheduler,
  )

  coordinatedShutdown.addTask(CoordinatedShutdown.PhaseBeforeServiceUnbind, "ThreadShutdown")(utilities.Scheduler.shutdownListener())
}
