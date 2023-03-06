package controllers

import controllers.actions._
import models.analytics.CollectionsAnalysis
import models.{blockchain, master, masterTransaction}
import play.api.Logger
import play.api.cache.Cached
import play.api.i18n.I18nSupport
import play.api.mvc._

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class WalletController @Inject()(
                                  messagesControllerComponents: MessagesControllerComponents,
                                  cached: Cached,
                                  withoutLoginActionAsync: WithoutLoginActionAsync,
                                  withLoginAction: WithLoginAction,
                                  withLoginActionAsync: WithLoginActionAsync,
                                  withoutLoginAction: WithoutLoginAction,
                                  collectionsAnalysis: CollectionsAnalysis,
                                  blockchainBalances: blockchain.Balances,
                                  masterAccounts: master.Accounts,
                                  masterKeys: master.Keys,
                                  masterWishLists: master.WishLists,
                                  masterCollections: master.Collections,
                                  masterNFTs: master.NFTs,
                                  masterSecondaryMarkets: master.SecondaryMarkets,
                                  masterNFTOwners: master.NFTOwners,
                                  masterNFTProperties: master.NFTProperties,
                                  masterTransactionTokenPrices: masterTransaction.TokenPrices,
                                  masterTransactionMakeOrderTransactions: masterTransaction.MakeOrderTransactions,
                                  masterTransactionTakeOrderTransactions: masterTransaction.TakeOrderTransactions,
                                  masterTransactionCancelOrderTransactions: masterTransaction.CancelOrderTransactions,
                                  utilitiesNotification: utilities.Notification,
                                  utilitiesOperations: utilities.Operations,
                                )(implicit executionContext: ExecutionContext) extends AbstractController(messagesControllerComponents) with I18nSupport {

  private implicit val logger: Logger = Logger(this.getClass)

  private implicit val module: String = constants.Module.WALLET_CONTROLLER

  implicit val callbackOnSessionTimeout: Call = routes.ProfileController.viewDefaultProfile()

}
