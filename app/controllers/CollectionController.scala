package controllers

import controllers.actions.{WithLoginActionAsync, WithoutLoginAction, WithoutLoginActionAsync}
import exceptions.BaseException
import models.master
import play.api.Logger
import play.api.cache.Cached
import play.api.i18n.I18nSupport
import play.api.mvc._

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class CollectionController @Inject()(
                                      messagesControllerComponents: MessagesControllerComponents,
                                      cached: Cached,
                                      withoutLoginAction: WithoutLoginAction,
                                      withoutLoginActionAsync: WithoutLoginActionAsync,
                                      withLoginActionAsync: WithLoginActionAsync,
                                      masterAccounts: master.Accounts,
                                      masterWallets: master.Wallets,
                                      masterCollections: master.Collections,
                                      masterNFTs: master.NFTs,
                                      masterCollectionFiles: master.CollectionFiles,
                                    )(implicit executionContext: ExecutionContext) extends AbstractController(messagesControllerComponents) with I18nSupport {

  private implicit val logger: Logger = Logger(this.getClass)

  private implicit val module: String = constants.Module.COLLECTION_CONTROLLER

  def viewCollections(): EssentialAction = cached.apply(req => req.path, constants.CommonConfig.WebAppCacheDuration) {
    withoutLoginActionAsync { implicit loginState =>
      implicit request =>
        Future(Ok(views.html.collection.collections(None)))
    }
  }

  def viewCollection(id: String): EssentialAction = cached.apply(req => req.path, constants.CommonConfig.WebAppCacheDuration) {
    withoutLoginActionAsync { implicit loginState =>
      implicit request =>
        val collection = masterCollections.Service.get(id)
        (for {
          collection <- collection
        } yield Ok(views.html.collection.collections(collection))
          ).recover {
          case baseException: BaseException => InternalServerError(baseException.failure.message)
        }
    }
  }

  def all(): EssentialAction = cached.apply(req => req.path, constants.CommonConfig.WebAppCacheDuration) {
    withoutLoginActionAsync { implicit loginState =>
      implicit request =>
        val allCollections = masterCollections.Service.fetchAll()
        (for {
          allCollections <- allCollections
        } yield Ok(views.html.collection.all(allCollections))
          ).recover {
          case baseException: BaseException => InternalServerError(baseException.failure.message)
        }
    }
  }

  def collection(id: String): EssentialAction = cached.apply(req => req.path, constants.CommonConfig.WebAppCacheDuration) {
    withoutLoginActionAsync { implicit loginState =>
      implicit request =>
        val collection = masterCollections.Service.tryGet(id)
        val allNFTs = masterNFTs.Service.getAllForCollection(id)
        (for {
          collection <- collection
          allNFTs <- allNFTs
        } yield Ok(views.html.collection.collection(collection, allNFTs))
          ).recover {
          case baseException: BaseException => InternalServerError(baseException.failure.message)
        }
    }
  }

  def collectionFile(id: String, documentType: String): Action[AnyContent] = withoutLoginActionAsync { implicit loginState =>
    implicit request =>
      val collectionFile = masterCollectionFiles.Service.get(id = id, documentType = documentType)
      (for {
        collectionFile <- collectionFile
      } yield {
        documentType match {
          case constants.File.Collection.COVER_IMAGE => Ok(views.html.base.collection.commonCollectionCoverImage(collectionFile))
          case constants.File.Collection.PROFILE_IMAGE => Ok(views.html.base.collection.commonCollectionProfileImage(collectionFile))
          case _ => throw new BaseException(constants.Response.FILE_TYPE_NOT_FOUND)
        }
      }
        ).recover {
        case baseException: BaseException => InternalServerError(baseException.failure.message)
      }
  }

}