package controllers

import akka.actor.CoordinatedShutdown
import controllers.actions._
import controllers.result.WithUsernameToken
import models.{blockchain, blockchainTransaction, history, masterTransaction}
import play.api.Logger
import play.api.cache.Cached
import play.api.i18n.I18nSupport
import play.api.mvc._
import service.Starter

import javax.inject._
import scala.concurrent.duration.{DAYS, Duration}
import scala.concurrent.{ExecutionContext, Future}

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
                                 nftPublicListings: blockchainTransaction.NFTPublicListings,
                                 sendCoins: blockchainTransaction.SendCoins,
                                 nftSales: blockchainTransaction.NFTSales,
                                 masterTransactionTokenPrices: masterTransaction.TokenPrices,
                                 publicListingNFTTransactions: masterTransaction.PublicListingNFTTransactions,
                                 saleNFTTransactions: masterTransaction.SaleNFTTransactions,
                                 masterTransactionSessionTokens: masterTransaction.SessionTokens,
                               )(implicit executionContext: ExecutionContext) extends AbstractController(messagesControllerComponents) with I18nSupport {

  private implicit val logger: Logger = Logger(this.getClass)

  private implicit val module: String = constants.Module.INDEX_CONTROLLER

  implicit val callbackOnSessionTimeout: Call = routes.CollectionController.viewCollections()

  def index: Action[AnyContent] = withoutLoginActionAsync { implicit loginState =>
    implicit request =>
      println(blockchainBlocks.Service.getLatestHeight)
      Future(Ok(views.html.index()))
  }

  def sitemap: EssentialAction = cached(req => utilities.Session.getSessionCachingKey(req), Duration(7, DAYS)) {
    withoutLoginAction { implicit request =>
      Ok(utilities.Sitemap.generate).as("application/xml; charset=utf-8")
    }
  }

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
    blockchainBlocks.Utility.scheduler
  )

  coordinatedShutdown.addTask(CoordinatedShutdown.PhaseBeforeServiceUnbind, "ThreadShutdown")(utilities.Scheduler.shutdownListener())
  starter.start()
}
