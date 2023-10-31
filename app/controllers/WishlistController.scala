package controllers

import controllers.actions._
import exceptions.BaseException
import models.{master, masterTransaction}
import play.api.Logger
import play.api.cache.Cached
import play.api.i18n.I18nSupport
import play.api.mvc._
import views.profile.wishlist.companion.AddOrRemove

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class WishlistController @Inject()(
                                    messagesControllerComponents: MessagesControllerComponents,
                                    cached: Cached,
                                    withoutLoginActionAsync: WithoutLoginActionAsync,
                                    withLoginAction: WithLoginAction,
                                    withLoginActionAsync: WithLoginActionAsync,
                                    withoutLoginAction: WithoutLoginAction,
                                    masterAccounts: master.Accounts,
                                    masterWishLists: master.WishLists,
                                    masterCollections: master.Collections,
                                    masterNFTs: master.NFTs,
                                    masterSecondaryMarkets: master.SecondaryMarkets,
                                    masterTransactionTokenPrices: masterTransaction.TokenPrices,
                                  )(implicit executionContext: ExecutionContext) extends AbstractController(messagesControllerComponents) with I18nSupport {

  implicit val logger: Logger = Logger(this.getClass)

  implicit val module: String = constants.Module.WISHLIST_CONTROLLER

  implicit val callbackOnSessionTimeout: Call = routes.ProfileController.viewDefaultProfile()

  def wishlistSection(accountId: String): EssentialAction = cached(req => utilities.Session.getSessionCachingKey(req), constants.CommonConfig.WebAppCacheDuration) {
    withoutLoginActionAsync { implicit loginState =>
      implicit request =>
        Future(Ok(views.html.profile.wishlist.wishlistSection(accountId)))
    }
  }

  def collectionPerPage(accountId: String, pageNumber: Int): EssentialAction = cached(req => utilities.Session.getSessionCachingKey(req), constants.CommonConfig.WebAppCacheDuration) {
    withoutLoginActionAsync { implicit loginState =>
      implicit request =>
        val allCollectionIds = masterWishLists.Service.getCollections(accountId)

        def allCollections(collectionIds: Seq[String]) = if (pageNumber < 1) constants.Response.INVALID_PAGE_NUMBER.throwBaseException()
        else masterCollections.Service.getCollectionsByPage(collectionIds, pageNumber)

        (for {
          collectionIds <- allCollectionIds
          collections <- allCollections(collectionIds)
        } yield Ok(views.html.profile.wishlist.collectionPerPage(accountId, collections, totalCollections = collectionIds.length))
          ).recover {
          case baseException: BaseException => InternalServerError(baseException.failure.message)
        }
    }
  }

  def viewCollectionNFTs(accountId: String, collectionId: String): EssentialAction = cached(req => utilities.Session.getSessionCachingKey(req), constants.CommonConfig.WebAppCacheDuration) {
    withoutLoginActionAsync { implicit loginState =>
      implicit request =>
        Future(Ok(views.html.profile.wishlist.viewCollectionNFTs(accountId, collectionId)))
    }
  }

  def collectionNFTs(accountId: String, collectionId: String): EssentialAction = cached(req => utilities.Session.getSessionCachingKey(req), constants.CommonConfig.WebAppCacheDuration) {
    withoutLoginActionAsync { implicit loginState =>
      implicit request =>
        val collection = masterCollections.Service.tryGet(collectionId)

        (for {
          collection <- collection
        } yield Ok(views.html.profile.wishlist.collectionNFTs(accountId, collection, collection.coverFileName))
          ).recover {
          case baseException: BaseException => InternalServerError(baseException.failure.message)
        }
    }
  }

  def collectionNFTsPerPage(accountId: String, collectionId: String, pageNumber: Int): EssentialAction = cached(req => utilities.Session.getSessionCachingKey(req), constants.CommonConfig.WebAppCacheDuration) {
    withoutLoginActionAsync { implicit loginState =>
      implicit request =>
        val collection = if (pageNumber < 1) constants.Response.INVALID_PAGE_NUMBER.throwBaseException()
        else masterCollections.Service.tryGet(collectionId)
        val nftIds = masterWishLists.Service.getByCollectionAndPageNumber(accountId = accountId, collectionId = collectionId, pageNumber = pageNumber, perPage = constants.CommonConfig.Pagination.NFTsPerPage)
        val tokenPrice = masterTransactionTokenPrices.Service.getLatestPrice

        def secondaryMarkets(nftIds: Seq[String]) = masterSecondaryMarkets.Service.getBySortedNFTIDs(nftIds)

        def getNFTs(nftIds: Seq[String]) = masterNFTs.Service.getByIds(nftIds)

        (for {
          collection <- collection
          nftIds <- nftIds
          nfts <- getNFTs(nftIds)
          secondaryMarkets <- secondaryMarkets(nftIds)
        } yield Ok(views.html.base.commonNFTsPerPage(collection, nfts, Seq(), nftIds, Seq(), pageNumber, secondaryMarkets, showCreatorSection = false, tokenPrice = tokenPrice))
          ).recover {
          case baseException: BaseException => InternalServerError(baseException.failure.message)
        }
    }
  }

  def add(): Action[AnyContent] = withLoginActionAsync { implicit loginState =>
    implicit request =>
      AddOrRemove.form.bindFromRequest().fold(
        formWithErrors => {
          Future(BadRequest(formWithErrors.errors.map(_.message).mkString(" ")))
        },
        addData => {
          implicit val optionalLoginState: Option[LoginState] = Option(loginState)
          (for {
            nft <- masterNFTs.Service.tryGet(addData.nftFileName)
            _ <- masterWishLists.Service.add(accountId = loginState.username, nftId = addData.nftFileName, collectionId = nft.collectionId)
          } yield Ok(views.html.nft.addToWishlist(nft))
            ).recover {
            case baseException: BaseException => BadRequest(baseException.failure.message)
          }
        }
      )
  }

  def delete(): Action[AnyContent] = withLoginActionAsync { implicit loginState =>
    implicit request =>
      AddOrRemove.form.bindFromRequest().fold(
        formWithErrors => {
          Future(BadRequest(formWithErrors.errors.map(_.message).mkString(" ")))
        },
        deleteData => {
          implicit val optionalLoginState: Option[LoginState] = Option(loginState)
          (for {
            nft <- masterNFTs.Service.tryGet(deleteData.nftFileName)
            _ <- masterWishLists.Service.delete(accountId = loginState.username, nftId = deleteData.nftFileName)
          } yield Ok(views.html.nft.deleteFromWishlist(nft))
            ).recover {
            case baseException: BaseException => BadRequest(baseException.failure.message)
          }
        }
      )
  }

}
