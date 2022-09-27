package controllers

import controllers.actions._
import exceptions.BaseException
import models.master
import play.api.Logger
import play.api.cache.Cached
import play.api.i18n.I18nSupport
import play.api.mvc._

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
                                  )(implicit executionContext: ExecutionContext) extends AbstractController(messagesControllerComponents) with I18nSupport {

  private implicit val logger: Logger = Logger(this.getClass)

  private implicit val module: String = constants.Module.WISHLIST_CONTROLLER

  implicit val callbackOnSessionTimeout: Call = routes.ProfileController.viewDefaultProfile()

  def wishlistSection(accountId: String): EssentialAction = cached.apply(req => req.path + accountId, constants.CommonConfig.WebAppCacheDuration) {
    withoutLoginActionAsync { implicit loginState =>
      implicit request =>
        Future(Ok(views.html.profile.wishlist.wishlistSection(accountId)))
    }
  }

  def collectionPerPage(accountId: String, pageNumber: Int): EssentialAction = cached.apply(req => req.path + accountId + pageNumber.toString, constants.CommonConfig.WebAppCacheDuration) {
    withoutLoginActionAsync { implicit loginState =>
      implicit request =>
        val allCollectionIds = masterWishLists.Service.getCollections(accountId)

        def allCollections(collectionIds: Seq[String]) = if (pageNumber < 1) Future(throw new BaseException(constants.Response.INVALID_PAGE_NUMBER))
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

  def viewCollectionNFTs(accountId: String, collectionId: String): EssentialAction = cached.apply(req => req.path + accountId + collectionId, constants.CommonConfig.WebAppCacheDuration) {
    withoutLoginActionAsync { implicit loginState =>
      implicit request =>
        Future(Ok(views.html.profile.wishlist.viewCollectionNFTs(accountId, collectionId)))
    }
  }

  def collectionNFTs(accountId: String, collectionId: String): EssentialAction = cached.apply(req => req.path + accountId + collectionId, constants.CommonConfig.WebAppCacheDuration) {
    withoutLoginActionAsync { implicit loginState =>
      implicit request =>
        val collection = masterCollections.Service.tryGet(collectionId)
        (for {
          collection <- collection
        } yield Ok(views.html.profile.wishlist.collectionNFTs(accountId, collection))
          ).recover {
          case baseException: BaseException => InternalServerError(baseException.failure.message)
        }
    }
  }

  def collectionNFTsPerPage(accountId: String, collectionId: String, pageNumber: Int): EssentialAction = cached.apply(req => req.path + accountId + collectionId + pageNumber.toString, constants.CommonConfig.WebAppCacheDuration) {
    withoutLoginActionAsync { implicit loginState =>
      implicit request =>
        val collection = if (pageNumber < 1) Future(throw new BaseException(constants.Response.INVALID_PAGE_NUMBER))
        else masterCollections.Service.tryGet(collectionId)
        val nftIds = masterWishLists.Service.getByCollectionAndPageNumber(accountId = accountId, collectionId = collectionId, pageNumber = pageNumber, perPage = constants.CommonConfig.Pagination.NFTsPerPage)

        def getNFTs(nftIds: Seq[String]) = masterNFTs.Service.getByIds(nftIds)

        (for {
          collection <- collection
          nftIds <- nftIds
          nfts <- getNFTs(nftIds)
        } yield Ok(views.html.collection.details.nftsPerPage(collection, nfts, nfts.map(_.fileName)))
          ).recover {
          case baseException: BaseException => InternalServerError(baseException.failure.message)
        }
    }
  }
}
