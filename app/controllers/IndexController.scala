package controllers

import controllers.actions._
import controllers.result.WithUsernameToken
import models.{blockchainTransaction, history, masterTransaction}
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
                                 // Do not delete, need to initialize object to start the scheduler
                                 historyMasterSales: history.MasterSales,
                                 historyMasterPublicListings: history.MasterPublicListings,
                                 nftPublicListings: blockchainTransaction.NFTPublicListings,
                                 nftSales: blockchainTransaction.NFTSales,
                                 publicListingNFTTransactions: masterTransaction.PublicListingNFTTransactions,
                                 saleNFTTransactions: masterTransaction.SaleNFTTransactions,
                               )(implicit executionContext: ExecutionContext) extends AbstractController(messagesControllerComponents) with I18nSupport {

  private implicit val logger: Logger = Logger(this.getClass)

  private implicit val module: String = constants.Module.INDEX_CONTROLLER

  implicit val callbackOnSessionTimeout: Call = routes.CollectionController.viewCollections(constants.View.DEFAULT_COLLECTION_SECTION)

  def index: Action[AnyContent] = withoutLoginActionAsync { implicit loginState =>
    implicit request =>
      Future(Ok(views.html.collection.viewPublicListedCollections()))
  }

  def sitemap: EssentialAction = cached(req => utilities.Session.getSessionCachingKey(req), Duration(7, DAYS)) {
    withoutLoginAction { implicit request =>
      Ok(utilities.Sitemap.generate).as("application/xml; charset=utf-8")
    }
  }

  starter.start()

  historyMasterPublicListings.Utility.start
  nftPublicListings.Utility.start
  publicListingNFTTransactions.Utility.start

  historyMasterSales.Utility.start
  saleNFTTransactions.Utility.start
  nftSales.Utility.start
}
