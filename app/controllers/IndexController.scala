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
                                 nftPublicListings: blockchainTransaction.NFTPublicListings,
                                 sendCoins: blockchainTransaction.SendCoins,
                                 nftSales: blockchainTransaction.NFTSales,
                                 issueIdentities: blockchainTransaction.IssueIdentities,
                                 masterTransactionTokenPrices: masterTransaction.TokenPrices,
                                 masterAccounts: master.Accounts,
                                 masterKeys: master.Keys,
                                 publicListingNFTTransactions: masterTransaction.PublicListingNFTTransactions,
                                 issueIdentityTransactions: masterTransaction.IssueIdentityTransactions,
                                 saleNFTTransactions: masterTransaction.SaleNFTTransactions,
                                 masterTransactionSessionTokens: masterTransaction.SessionTokens,
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

  def fixAllMultipleActiveKeys() = {
    val allActiveKeys = Await.result(masterKeys.Service.fetchAllActive, Duration.Inf)
    val allAccountIds = allActiveKeys.map(_.accountId).distinct
    if (allAccountIds.length != allActiveKeys.length) {
      println("correcting active")
      val wrongAccountIds = allAccountIds.flatMap(x => if (allActiveKeys.count(_.accountId == x) > 1) Option(x) else None)
      println(wrongAccountIds)
      println(wrongAccountIds.length)
      Await.result(masterKeys.Service.insertOrUpdateMultiple(allActiveKeys.filter(x => wrongAccountIds.contains(x.accountId) && x.encryptedPrivateKey.length == 0).map(_.copy(active = false))), Duration.Inf)
      val updatedAllActiveKeys = Await.result(masterKeys.Service.fetchAllActive, Duration.Inf)
      val updatedAllAccountIds = updatedAllActiveKeys.map(_.accountId).distinct
      val wrongManagedAccountIds = updatedAllAccountIds.flatMap(x => if (updatedAllActiveKeys.count(_.accountId == x) > 1) Option(x) else None)
      println(wrongManagedAccountIds)
      println(wrongManagedAccountIds.length)
      val wrongManagedKeys = updatedAllActiveKeys.filter(x => wrongManagedAccountIds.contains(x.accountId) && x.encryptedPrivateKey.length > 0)
      wrongManagedAccountIds.foreach(x => {
        val updateKeys = wrongManagedKeys.filter(_.accountId == x).sortBy(_.createdOnMillisEpoch.getOrElse(0L)).reverse.drop(1)
        Await.result(masterKeys.Service.insertOrUpdateMultiple(updateKeys.map(_.copy(active = false))), Duration.Inf)
      })
      val finalAllActiveKeys = Await.result(masterKeys.Service.fetchAllActive, Duration.Inf)
      val finalAllAccountIds = finalAllActiveKeys.map(_.accountId).distinct
      println(finalAllAccountIds.flatMap(x => if (finalAllActiveKeys.count(_.accountId == x) > 1) Option(x) else None))
      println(finalAllAccountIds.flatMap(x => if (finalAllActiveKeys.count(_.accountId == x) > 1) Option(x) else None).length)
    } else {
      println("all correct")
    }
  }

  fixAllMultipleActiveKeys()

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
    issueIdentities.Utility.scheduler,
    issueIdentityTransactions.Utility.scheduler
  )

  coordinatedShutdown.addTask(CoordinatedShutdown.PhaseBeforeServiceUnbind, "ThreadShutdown")(utilities.Scheduler.shutdownListener())
  starter.start()
}
