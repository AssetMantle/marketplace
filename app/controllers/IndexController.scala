package controllers

import akka.actor.CoordinatedShutdown
import controllers.actions._
import controllers.result.WithUsernameToken
import models._
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
                                 defineAssets: blockchainTransaction.DefineAssets,
                                 mintAssets: blockchainTransaction.MintAssets,
                                 sendCoins: blockchainTransaction.SendCoins,
                                 nftSales: blockchainTransaction.NFTSales,
                                 cancelOrders: blockchainTransaction.CancelOrders,
                                 makeOrders: blockchainTransaction.MakeOrders,
                                 takeOrders: blockchainTransaction.TakeOrders,
                                 issueIdentities: blockchainTransaction.IssueIdentities,
                                 provisionAddresses: blockchainTransaction.ProvisionAddresses,
                                 unprovisionAddresses: blockchainTransaction.UnprovisionAddresses,
                                 unwraps: blockchainTransaction.Unwraps,
                                 masterCollections: master.Collections,
                                 masterTransactionTokenPrices: masterTransaction.TokenPrices,
                                 masterAccounts: master.Accounts,
                                 masterKeys: master.Keys,
                                 publicListingNFTTransactions: masterTransaction.PublicListingNFTTransactions,
                                 issueIdentityTransactions: masterTransaction.IssueIdentityTransactions,
                                 defineAssetTransactions: masterTransaction.DefineAssetTransactions,
                                 mintAssetTransactions: masterTransaction.MintAssetTransactions,
                                 saleNFTTransactions: masterTransaction.SaleNFTTransactions,
                                 masterTransactionSessionTokens: masterTransaction.SessionTokens,
                                 makeOrderTransactions: masterTransaction.MakeOrderTransactions,
                                 takeOrderTransactions: masterTransaction.TakeOrderTransactions,
                                 cancelOrderTransactions: masterTransaction.CancelOrderTransactions,
                                 unwrapTransactions: masterTransaction.UnwrapTransactions,
                                 provisionAddressTransactions: masterTransaction.ProvisionAddressTransactions,
                                 unprovisionAddressTransactions: masterTransaction.UnprovisionAddressTransactions,
                               )(implicit executionContext: ExecutionContext) extends AbstractController(messagesControllerComponents) with I18nSupport {

  private implicit val logger: Logger = Logger(this.getClass)

  private implicit val module: String = constants.Module.INDEX_CONTROLLER

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
  starter.changeAwsKey()

  //  starter.start()

  Await.result(starter.updateIdentityIDs(), Duration.Inf)
  Await.result(starter.updateAssetIDs(), Duration.Inf)
  Await.result(starter.markMintReady(), Duration.Inf)
  starter.fixAllMultipleActiveKeys()

  utilities.Scheduler.startSchedulers(
    historyMasterPublicListings.Utility.scheduler,
    nftPublicListings.Utility.scheduler,
    publicListingNFTTransactions.Utility.scheduler,
    historyMasterSales.Utility.scheduler,
    saleNFTTransactions.Utility.scheduler,
    nftSales.Utility.scheduler,
    masterTransactionSessionTokens.Utility.scheduler,
    sendCoins.Utility.scheduler,
    masterTransactionTokenPrices.Utility.scheduler,
    blockchainBlocks.Utility.scheduler,
    defineAssets.Utility.scheduler,
    defineAssetTransactions.Utility.scheduler,
    issueIdentities.Utility.scheduler,
    issueIdentityTransactions.Utility.scheduler,
    mintAssets.Utility.scheduler,
    mintAssetTransactions.Utility.scheduler,
    makeOrders.Utility.scheduler,
    makeOrderTransactions.Utility.scheduler,
    takeOrders.Utility.scheduler,
    takeOrderTransactions.Utility.scheduler,
    historyMasterSecondaryMarkets.Utility.scheduler,
    cancelOrders.Utility.scheduler,
    cancelOrderTransactions.Utility.scheduler,
    unwraps.Utility.scheduler,
    unwrapTransactions.Utility.scheduler,
    provisionAddresses.Utility.scheduler,
    provisionAddressTransactions.Utility.scheduler,
    unprovisionAddresses.Utility.scheduler,
    unprovisionAddressTransactions.Utility.scheduler,
  )

  coordinatedShutdown.addTask(CoordinatedShutdown.PhaseBeforeServiceUnbind, "ThreadShutdown")(utilities.Scheduler.shutdownListener())
}
