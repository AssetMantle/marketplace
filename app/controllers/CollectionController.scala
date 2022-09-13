package controllers

import akka.stream.scaladsl.{Source, StreamConverters}
import akka.util.ByteString
import controllers.actions.{LoginState, WithLoginActionAsync, WithoutLoginAction, WithoutLoginActionAsync}
import exceptions.BaseException
import models.master
import play.api.Logger
import play.api.cache.Cached
import play.api.i18n.I18nSupport
import play.api.mvc._

import java.io.{File, FileInputStream}
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
                                      masterCollections: master.Collections,
                                      masterNFTs: master.NFTs,
                                      masterCollectionFiles: master.CollectionFiles,
                                      masterWishLists: master.WishLists,
                                    )(implicit executionContext: ExecutionContext) extends AbstractController(messagesControllerComponents) with I18nSupport {

  private implicit val logger: Logger = Logger(this.getClass)

  private implicit val module: String = constants.Module.COLLECTION_CONTROLLER

  def viewCollections(section: String): EssentialAction = cached.apply(req => req.path, constants.CommonConfig.WebAppCacheDuration) {
    withoutLoginActionAsync { implicit loginState =>
      implicit request =>
        Future(Ok(views.html.collection.viewCollections(section)))
    }
  }

  def viewCollection(id: String): EssentialAction = cached.apply(req => req.path + "/" + id, constants.CommonConfig.WebAppCacheDuration) {
    withoutLoginActionAsync { implicit loginState =>
      implicit request =>
        val collection = masterCollections.Service.tryGet(id)
        (for {
          collection <- collection
        } yield Ok(views.html.collection.viewCollection(collection))
          ).recover {
          case baseException: BaseException => InternalServerError(baseException.failure.message)
        }
    }
  }

  def collectionsList(section: String): EssentialAction = cached.apply(req => req.path, constants.CommonConfig.WebAppCacheDuration) {
    withoutLoginActionAsync { implicit loginState =>
      implicit request =>
        Future(Ok(views.html.collection.explore.collectionsList(section)))
    }
  }

  def collectionsPerPage(pageNumber: Int): Action[AnyContent] = withoutLoginActionAsync { implicit loginState =>
    implicit request =>
      val collections = if (pageNumber < 1) Future(throw new BaseException(constants.Response.INVALID_PAGE_NUMBER))
      else masterCollections.Service.getByPageNumber(pageNumber)
      (for {
        collections <- collections
      } yield Ok(views.html.collection.explore.collectionsPerPage(collections))
        ).recover {
        case baseException: BaseException => InternalServerError(baseException.failure.message)
      }
  }

  def collectionFile(id: String, documentType: String, compress: Boolean): Action[AnyContent] = withoutLoginActionAsync { implicit loginState =>
    implicit request =>
      val collectionFile = masterCollectionFiles.Service.get(id = id, documentType = documentType)
      val collection = masterCollections.Service.tryGet(id)
      (for {
        collectionFile <- collectionFile
        collection <- collection
      } yield {
        if (compress) {
          documentType match {
            case constants.Collection.File.COVER => Ok(views.html.collection.collectionCardCoverImage(collectionFile))
            case constants.Collection.File.PROFILE => Ok(views.html.collection.collectionCardProfileImage(collectionFile))
            case _ => throw new BaseException(constants.Response.FILE_TYPE_NOT_FOUND)
          }
        } else {
          collectionFile.fold {
            documentType match {
              case constants.Collection.File.COVER => Ok.chunked(StreamConverters.fromInputStream(() => new FileInputStream(new File(constants.CommonConfig.DefaultPublicFolder + "/images/defaultCollectionCover.png"))))
              case constants.Collection.File.PROFILE => Ok.chunked(StreamConverters.fromInputStream(() => new FileInputStream(new File(constants.CommonConfig.DefaultPublicFolder + "/images/defaultProfileCover.png"))))
              case _ => throw new BaseException(constants.Response.FILE_TYPE_NOT_FOUND)
            }
          }(x => {
            val source: Source[ByteString, _] = StreamConverters.fromInputStream(() => utilities.AmazonS3.getFullObject(collection.name + "/others/" + x.fileName).getObjectContent.getDelegateStream)
            Ok.chunked(source, inline = true, Option(x.fileName))
          })
        }
      }
        ).recover {
        case baseException: BaseException => InternalServerError(baseException.failure.message)
      }
  }

  def collectionNFTs(id: String): EssentialAction = cached.apply(req => req.path + "/" + id, constants.CommonConfig.WebAppCacheDuration) {
    withoutLoginActionAsync { implicit loginState =>
      implicit request =>
        val collection = masterCollections.Service.tryGet(id)
        (for {
          collection <- collection
        } yield Ok(views.html.collection.details.collectionNFTs(collection))
          ).recover {
          case baseException: BaseException => InternalServerError(baseException.failure.message)
        }
    }
  }

  def collectionNFTsPerPage(id: String, pageNumber: Int): EssentialAction = cached.apply(req => req.path + "/" + id + "/" + pageNumber.toString, constants.CommonConfig.WebAppCacheDuration) {
    withoutLoginActionAsync { implicit loginState =>
      implicit request =>
        val collection = if (pageNumber < 1) Future(throw new BaseException(constants.Response.INVALID_PAGE_NUMBER))
        else masterCollections.Service.tryGet(id)
        val nfts = masterNFTs.Service.getByPageNumber(id, pageNumber)

        val likedNFTs = loginState.fold[Future[Seq[String]]](Future(Seq()))(x => masterWishLists.Service.getByCollection(accountId = x.username, collectionId = id))

        (for {
          collection <- collection
          nfts <- nfts
          likedNFTs <- likedNFTs
        } yield Ok(views.html.collection.details.nftsPerPage(collection, nfts, likedNFTs))
          ).recover {
          case baseException: BaseException => InternalServerError(baseException.failure.message)
        }
    }
  }

  def info(id: String): EssentialAction = cached.apply(req => req.path + "/" + id, constants.CommonConfig.WebAppCacheDuration) {
    withoutLoginActionAsync { implicit loginState =>
      implicit request =>
        val collection = masterCollections.Service.tryGet(id)
        (for {
          collection <- collection
        } yield Ok(views.html.collection.details.collectionInfo(collection))
          ).recover {
          case baseException: BaseException => InternalServerError(baseException.failure.message)
        }
    }
  }
  def wishListCollectionPerPage(pageNumber: Int): EssentialAction = cached.apply(req => req.path + "/" + req.session.get(constants.Session.USERNAME).getOrElse("") + "/" + pageNumber.toString, constants.CommonConfig.WebAppCacheDuration) {
    withLoginActionAsync { implicit loginState =>
      implicit request =>
        val allCollectionIds = masterWishLists.Service.getCollections(loginState.username)

        def allCollections(collectionIds: Seq[String]) = if (pageNumber < 1) Future(throw new BaseException(constants.Response.INVALID_PAGE_NUMBER))
        else masterCollections.Service.getCollectionsByPage(collectionIds, pageNumber)

        (for {
          collectionIds <- allCollectionIds
          collections <- allCollections(collectionIds)
        } yield Ok(views.html.collection.explore.wishListCollectionPerPage(collections))
          ).recover {
          case baseException: BaseException => InternalServerError(baseException.failure.message)
        }
    }
  }

  def wishListCollectionNFTs(id: String): EssentialAction = cached.apply(req => req.path + "/" + req.session.get(constants.Session.USERNAME).getOrElse("") + "/" + id, constants.CommonConfig.WebAppCacheDuration) {
    withLoginActionAsync { implicit loginState =>
      implicit request =>
        val collection = masterCollections.Service.tryGet(id)
        (for {
          collection <- collection
        } yield Ok(views.html.collection.details.wishListCollectionNFTs(collection))
          ).recover {
          case baseException: BaseException => InternalServerError(baseException.failure.message)
        }
    }
  }

  def wishListCollectionNFTsPerPage(id: String, pageNumber: Int): EssentialAction = cached.apply(req => req.path + "/" + req.session.get(constants.Session.USERNAME).getOrElse("") + "/" + id + "/" + pageNumber.toString, constants.CommonConfig.WebAppCacheDuration) {
    withLoginActionAsync { implicit loginState =>
      implicit request =>
        val collection = if (pageNumber < 1) Future(throw new BaseException(constants.Response.INVALID_PAGE_NUMBER))
        else masterCollections.Service.tryGet(id)
        val nftIds = masterWishLists.Service.getByCollectionAndPageNumber(accountId = loginState.username, collectionId = id, pageNumber = pageNumber, perPage = constants.CommonConfig.Pagination.NFTsPerPage)

        def getNFTs(nftIds: Seq[String]) = masterNFTs.Service.getByIds(nftIds)

        (for {
          collection <- collection
          nftIds <- nftIds
          nfts <- getNFTs(nftIds)
        } yield {
          implicit val implicitLoginState: Option[LoginState] = Option(loginState)
          Ok(views.html.collection.details.nftsPerPage(collection, nfts, nfts.map(_.fileName)))
        }
          ).recover {
          case baseException: BaseException => InternalServerError(baseException.failure.message)
        }
    }
  }
}