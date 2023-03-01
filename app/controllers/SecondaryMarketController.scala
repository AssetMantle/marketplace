package controllers

import controllers.actions._
import exceptions.BaseException
import models.analytics.CollectionsAnalysis
import models.master.{Collection, Key, NFTOwner}
import models.{blockchain, master, masterTransaction}
import play.api.Logger
import play.api.cache.Cached
import play.api.i18n.I18nSupport
import play.api.mvc._
import views.secondaryMarket.companion.CreateSecondaryMarket

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class SecondaryMarketController @Inject()(
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
                                           masterCollections: master.Collections,
                                           masterNFTs: master.NFTs,
                                           masterSecondaryMarkets: master.SecondaryMarkets,
                                           masterNFTOwners: master.NFTOwners,
                                           masterNFTProperties: master.NFTProperties,
                                           masterTransactionMakeOrderTransactions: masterTransaction.MakeOrderTransactions,
                                           utilitiesNotification: utilities.Notification,
                                           utilitiesOperations: utilities.Operations,
                                         )(implicit executionContext: ExecutionContext) extends AbstractController(messagesControllerComponents) with I18nSupport {

  private implicit val logger: Logger = Logger(this.getClass)

  private implicit val module: String = constants.Module.SECONDARY_MARKET_CONTROLLER

  implicit val callbackOnSessionTimeout: Call = routes.SecondaryMarketController.viewSecondaryMarket()

  def viewSecondaryMarket(): EssentialAction = cached(req => utilities.Session.getSessionCachingKey(req), constants.CommonConfig.WebAppCacheDuration) {
    withoutLoginActionAsync { implicit loginState =>
      implicit request =>
        Future(Ok(views.html.secondaryMarket.viewSecondaryMarkets()))
    }
  }

  def secondaryMarketCollectionsSection(): EssentialAction = cached(req => utilities.Session.getSessionCachingKey(req), constants.CommonConfig.WebAppCacheDuration) {
    withoutLoginActionAsync { implicit loginState =>
      implicit request =>
        val totalCollections = masterSecondaryMarkets.Service.total
        (for {
          totalCollections <- totalCollections
        } yield Ok(views.html.secondaryMarket.collectionsSection(totalCollections))
          ).recover {
          case baseException: BaseException => BadRequest(baseException.failure.message)
        }
    }
  }

  def secondaryMarketCollectionsPerPage(pageNumber: Int): EssentialAction = cached(req => utilities.Session.getSessionCachingKey(req), constants.CommonConfig.WebAppCacheDuration) {
    withoutLoginActionAsync { implicit loginState =>
      implicit request =>
        val secondaryMarkets = if (pageNumber < 1) Future(throw new BaseException(constants.Response.INVALID_PAGE_NUMBER))
        else masterSecondaryMarkets.Service.getByPageNumber(pageNumber)

        def collections(ids: Seq[String]) = masterCollections.Service.getCollections(ids)

        (for {
          secondaryMarkets <- secondaryMarkets
          collections <- collections(secondaryMarkets.map(_.collectionId))
        } yield Ok(views.html.secondaryMarket.collectionsPerPage(collections))
          ).recover {
          case baseException: BaseException => InternalServerError(baseException.failure.message)
        }
    }
  }

  def createForm(nftId: String): Action[AnyContent] = withLoginActionAsync { implicit loginState =>
    implicit request =>
      Future(Ok(views.html.secondaryMarket.createSecondaryMarket(nftId = nftId)))
  }

  def create(): Action[AnyContent] = withLoginActionAsync { implicit loginState =>
    implicit request =>
      CreateSecondaryMarket.form.bindFromRequest().fold(
        formWithErrors => {
          val nftID = formWithErrors.data.getOrElse(constants.FormField.NFT_ID.name, "")
          Future(BadRequest(views.html.secondaryMarket.createSecondaryMarket(formWithErrors, nftID)))
        },
        createData => {
          val nftOwner = masterNFTOwners.Service.tryGet(nftId = createData.nftId, ownerId = loginState.username)
          val activeKey = masterKeys.Service.tryGetActive(loginState.username)

          def collection(id: String) = masterCollections.Service.tryGet(id)

          def addToSecondaryMarket(nftOwner: NFTOwner, collection: Collection, activeKey: Key) = {
            val errors = Seq(
              if (nftOwner.creatorId == "Mint.E") Option(constants.Response.SELLING_MINT_E_COLLECTIONS_NOT_ALLOWED) else None,
              if (nftOwner.ownerId != loginState.username) Option(constants.Response.NOT_NFT_OWNER) else None,
              if (nftOwner.creatorId == loginState.username) Option(constants.Response.NFT_CREATOR_NOT_ALLOWED_SECONDARY_SALE) else None,
              if (nftOwner.saleId.isDefined || nftOwner.publicListingId.isDefined || nftOwner.secondaryMarketId.isDefined) Option(constants.Response.NFT_ALREADY_ON_SALE) else None,
              if (!collection.public) Option(constants.Response.COLLECTION_NOT_PUBLIC) else None,
            ).flatten
            if (errors.isEmpty) {
              for {
                secondaryMarketId <- masterSecondaryMarkets.Service.add(createData.toNewSecondaryMarket(nftOwner.collectionId))
                _ <- masterNFTOwners.Service.listNFTOnSecondaryMarket(NFTOwner = nftOwner, secondaryMarketID = secondaryMarketId)
                tx <- masterTransactionMakeOrderTransactions.Utility.transaction(nftID = createData.nftId, nftOwner = nftOwner, endTimeEpoch = createData.endEpoch, price = createData.price, buyerAccountId = None, secondaryMarketId = secondaryMarketId, gasPrice = constants.Blockchain.DefaultGasPrice, ecKey = activeKey.getECKey(createData.password).get)
              } yield tx
            } else errors.head.throwFutureBaseException()
          }

          //          def sendNotification(collection: Collection) = utilitiesNotification.send(loginState.username, notification = constants.Notification.SECONDARY_MARKET_CREATION, collection.name)(s"'${collection.id}'")

          (for {
            nftOwner <- nftOwner
            activeKey <- activeKey
            collection <- collection(nftOwner.collectionId)
            tx <- addToSecondaryMarket(nftOwner = nftOwner, collection = collection, activeKey = activeKey)
            //            _ <- sendNotification(collection)
            _ <- collectionsAnalysis.Utility.onCreateSecondaryMarket(collection.id, totalListed = 1, listingPrice = createData.price)
          } yield PartialContent(views.html.secondaryMarket.createSuccessful())
            ).recover {
            case baseException: BaseException => BadRequest(views.html.secondaryMarket.createSecondaryMarket(CreateSecondaryMarket.form.withGlobalError(baseException.failure.message), createData.nftId))
          }
        }
      )
  }


}
