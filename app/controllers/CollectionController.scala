package controllers

import controllers.actions._
import exceptions.BaseException
import models.master
import play.api.Logger
import play.api.cache.Cached
import play.api.i18n.I18nSupport
import play.api.mvc._
import views.base.companion.UploadFile
import views.collection.companion.Create

import java.nio.file.Files
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class CollectionController @Inject()(
                                      messagesControllerComponents: MessagesControllerComponents,
                                      cached: Cached,
                                      withoutLoginAction: WithoutLoginAction,
                                      withoutLoginActionAsync: WithoutLoginActionAsync,
                                      withLoginActionAsync: WithLoginActionAsync,
                                      withLoginAction: WithLoginAction,
                                      masterAccounts: master.Accounts,
                                      masterCollections: master.Collections,
                                      masterNFTs: master.NFTs,
                                      masterCollectionFiles: master.CollectionFiles,
                                      masterWishLists: master.WishLists,
                                    )(implicit executionContext: ExecutionContext) extends AbstractController(messagesControllerComponents) with I18nSupport {

  private implicit val logger: Logger = Logger(this.getClass)

  private implicit val module: String = constants.Module.COLLECTION_CONTROLLER

  implicit val callbackOnSessionTimeout: Call = routes.CollectionController.viewCollections("art")

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

      def allCollectionFiles(collectionIds: Seq[String]) = masterCollectionFiles.Service.get(collectionIds)

      (for {
        collections <- collections
        collectionFiles <- allCollectionFiles(collections.map(_.id))
      } yield Ok(views.html.collection.explore.collectionsPerPage(collections, collectionFiles))
        ).recover {
        case baseException: BaseException => InternalServerError(baseException.failure.message)
      }
  }

  def collectionNFTs(id: String): EssentialAction = cached.apply(req => req.path + "/" + id, constants.CommonConfig.WebAppCacheDuration) {
    withoutLoginActionAsync { implicit loginState =>
      implicit request =>
        val collection = masterCollections.Service.tryGet(id)
        val collectionCover = masterCollectionFiles.Service.get(id = id, documentType = constants.Collection.File.COVER)

        (for {
          collection <- collection
          collectionCover <- collectionCover
        } yield Ok(views.html.collection.details.collectionNFTs(collection, collectionCover.fold[Option[String]](None)(x => Option(x.fileName))))
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

  def createForm(): Action[AnyContent] = withoutLoginAction { implicit request =>
    Ok(views.html.collection.create())
  }

  def create(): Action[AnyContent] = withLoginActionAsync { implicit loginState =>
    implicit request =>
      Create.form.bindFromRequest().fold(
        formWithErrors => {
          Future(BadRequest(views.html.collection.create(formWithErrors)))
        },
        createData => {
          val collectionId = masterCollections.Service.add(name = createData.name, description = createData.description, website = "", socialProfiles = createData.getSocialProfiles, category = createData.category, creatorId = loginState.username)

          (for {
            collectionId <- collectionId
          } yield PartialContent(views.html.collection.uploadFile(id = collectionId))
            ).recover {
            case baseException: BaseException => BadRequest(views.html.collection.create(Create.form.withGlobalError(baseException.failure.message)))
          }
        }
      )
  }

  def uploadCollectionFileForm(id: String, documentType: String): Action[AnyContent] = withLoginActionAsync { implicit loginState =>
    implicit request =>
      val checkCollectionOwner = masterCollections.Service.isOwner(id = id, accountId = loginState.username)
      (for {
        collectionOwner <- checkCollectionOwner
      } yield if (collectionOwner) Ok(views.html.base.commonUploadFile(constants.File.COLLECTION_FILE_FORM, id = id, documentType = documentType))
      else constants.Response.NOT_COLLECTION_OWNER.throwBaseException()
        ).recover {
        case baseException: BaseException => BadRequest(baseException.failure.message)
      }
  }

  def storeCollectionFile(id: String, documentType: String) = withLoginAction.applyMultipartFormData { implicit loginState =>
    implicit request =>
      UploadFile.form.bindFromRequest.fold(
        formWithErrors => {
          BadRequest(constants.View.BAD_REQUEST)
        },
        fileUploadInfo => {
          try {
            request.body.file(constants.File.KEY_FILE) match {
              case None => BadRequest(constants.View.BAD_REQUEST)
              case Some(file) => if (fileUploadInfo.resumableTotalSize <= constants.File.COLLECTION_FILE_FORM.maxFileSize) {
                utilities.FileOperations.savePartialFile(Files.readAllBytes(file.ref.path), fileUploadInfo, constants.Collection.getFilePath(documentType))
                Ok
              } else constants.Response.NOT_COLLECTION_OWNER.throwBaseException()
            }
          } catch {
            case baseException: BaseException => BadRequest(baseException.failure.message)
          }
        }
      )
  }

  def uploadCollectionFile(id: String, documentType: String, name: String): Action[AnyContent] = withLoginActionAsync { implicit loginState =>
    implicit request =>
      val oldFilePath = constants.Collection.getFilePath(documentType) + name
      val fileHash = utilities.FileOperations.getFileHash(oldFilePath)
      val newFileName = fileHash + "." + utilities.FileOperations.fileExtensionFromName(name)
      val awsKey = id + "/others/" + newFileName
      val checkCollectionOwner = masterCollections.Service.isOwner(id = id, accountId = loginState.username)

      def uploadToAws(collectionOwner: Boolean) = if (collectionOwner) Future(utilities.AmazonS3.uploadFile(objectKey = awsKey, filePath = oldFilePath))
      else {
        utilities.FileOperations.deleteFile(oldFilePath)
        constants.Response.NOT_COLLECTION_OWNER.throwFutureBaseException()
      }

      def add = masterCollectionFiles.Service.add(id = id, documentType = documentType, fileName = newFileName)

      (for {
        collectionOwner <- checkCollectionOwner
        _ <- uploadToAws(collectionOwner)
        _ <- add
      } yield Ok
        ).recover {
        case baseException: BaseException => BadRequest(baseException.failure.message)
      }
  }


}