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
class CollectedController @Inject()(
                                     messagesControllerComponents: MessagesControllerComponents,
                                     cached: Cached,
                                     withoutLoginActionAsync: WithoutLoginActionAsync,
                                     withLoginAction: WithLoginAction,
                                     withLoginActionAsync: WithLoginActionAsync,
                                     withoutLoginAction: WithoutLoginAction,
                                     masterAccounts: master.Accounts,
                                     masterCollections: master.Collections,
                                     masterNFTs: master.NFTs,
                                     masterNFTOwners: master.NFTOwners,
                                   )(implicit executionContext: ExecutionContext) extends AbstractController(messagesControllerComponents) with I18nSupport {

  private implicit val logger: Logger = Logger(this.getClass)

  private implicit val module: String = constants.Module.COLLECTED_CONTROLLER

  implicit val callbackOnSessionTimeout: Call = routes.ProfileController.viewDefaultProfile()

  def collectedSection(accountId: String): EssentialAction = cached(req => utilities.Session.getSessionCachingKey(req), constants.CommonConfig.WebAppCacheDuration) {
    withoutLoginActionAsync { implicit loginState =>
      implicit request =>
        Future(Ok(views.html.profile.collected.collectedSection(accountId)))
    }
  }

  def collectionPerPage(accountId: String, pageNumber: Int): EssentialAction = cached(req => utilities.Session.getSessionCachingKey(req), constants.CommonConfig.WebAppCacheDuration) {
    withoutLoginActionAsync { implicit loginState =>
      implicit request =>
        val allCollectionIds = masterNFTOwners.Service.getCollectedCollection(accountId)

        def allCollections(collectionIds: Seq[String]) = if (pageNumber < 1) Future(throw new BaseException(constants.Response.INVALID_PAGE_NUMBER))
        else masterCollections.Service.getCollectionsByPage(collectionIds, pageNumber)

        (for {
          collectionIds <- allCollectionIds
          collections <- allCollections(collectionIds)
        } yield Ok(views.html.profile.collected.collectionPerPage(accountId, collections, totalCollections = collectionIds.length))
          ).recover {
          case baseException: BaseException => InternalServerError(baseException.failure.message)
        }
    }
  }

  def viewCollectionNFTs(accountId: String, collectionId: String): EssentialAction = cached(req => utilities.Session.getSessionCachingKey(req), constants.CommonConfig.WebAppCacheDuration) {
    withoutLoginActionAsync { implicit loginState =>
      implicit request =>
        Future(Ok(views.html.profile.collected.viewCollectionNFTs(accountId, collectionId)))
    }
  }

  def collectionNFTs(accountId: String, collectionId: String): EssentialAction = cached(req => utilities.Session.getSessionCachingKey(req), constants.CommonConfig.WebAppCacheDuration) {
    withoutLoginActionAsync { implicit loginState =>
      implicit request =>
        val collection = masterCollections.Service.tryGet(collectionId)

        (for {
          collection <- collection
        } yield Ok(views.html.profile.collected.collectionNFTs(accountId, collection, collection.coverFileName))
          ).recover {
          case baseException: BaseException => InternalServerError(baseException.failure.message)
        }
    }
  }

  def collectionNFTsPerPage(accountId: String, collectionId: String, pageNumber: Int): EssentialAction = cached(req => utilities.Session.getSessionCachingKey(req), constants.CommonConfig.WebAppCacheDuration) {
    withoutLoginActionAsync { implicit loginState =>
      implicit request =>
        val collection = if (pageNumber < 1) Future(throw new BaseException(constants.Response.INVALID_PAGE_NUMBER))
        else masterCollections.Service.tryGet(collectionId)
        val nftIds = masterNFTOwners.Service.getByCollectionAndPageNumber(accountId = accountId, collectionId = collectionId, pageNumber = pageNumber, perPage = constants.CommonConfig.Pagination.NFTsPerPage)

        def getNFTs(nftIds: Seq[String]) = masterNFTs.Service.getByIds(nftIds)

        (for {
          collection <- collection
          nftIds <- nftIds
          nfts <- getNFTs(nftIds)
        } yield Ok(views.html.collection.details.nftsPerPage(collection, nfts, nfts.map(_.id), Seq(), pageNumber))
          ).recover {
          case baseException: BaseException => InternalServerError(baseException.failure.message)
        }
    }
  }

  def commonCardInfo(collectionID: String, accountID: String): EssentialAction = cached(req => utilities.Session.getSessionCachingKey(req), constants.CommonConfig.WebAppCacheDuration) {
    withoutLoginActionAsync { implicit loginState =>
      implicit request =>
        val totalOwned = masterNFTOwners.Service.countCollectionOwnedNFTs(accountId = accountID, collectionID = collectionID)

        (for {
          totalOwned <- totalOwned
        } yield {
          Ok(s"${totalOwned.toString}|N/A")
        }
          ).recover {
          case baseException: BaseException => BadRequest(baseException.failure.message)
        }
    }
  }

  def topRightCard(collectionID: String, accountID: String): Action[AnyContent] = withoutLoginActionAsync { implicit optionalLoginState =>
    implicit request =>
      val totalOwned = masterNFTOwners.Service.countCollectionOwnedNFTs(accountId = accountID, collectionID = collectionID)

      (for {
        totalOwned <- totalOwned
      } yield Ok(views.html.profile.collected.topRightCard(totalOwned = totalOwned))
        ).recover {
        case baseException: BaseException => BadRequest(baseException.failure.message)
      }
  }

}
