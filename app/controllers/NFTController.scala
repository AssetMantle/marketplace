package controllers

import akka.stream.scaladsl.{Source, StreamConverters}
import akka.util.ByteString
import controllers.actions._
import exceptions.BaseException
import models.master.Collection
import models.{master, masterTransaction}
import play.api.Logger
import play.api.cache.Cached
import play.api.i18n.I18nSupport
import play.api.mvc._
import views.base.companion.UploadFile
import views.nft.companion.{NFTBasicDetail, NFTTags, SetProperties}

import java.nio.file.Files
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class NFTController @Inject()(
                               messagesControllerComponents: MessagesControllerComponents,
                               cached: Cached,
                               withoutLoginActionAsync: WithoutLoginActionAsync,
                               withLoginActionAsync: WithLoginActionAsync,
                               withoutLoginAction: WithoutLoginAction,
                               withLoginAction: WithLoginAction,
                               masterAccounts: master.Accounts,
                               masterCollections: master.Collections,
                               masterNFTs: master.NFTs,
                               masterTransactionNFTDrafts: masterTransaction.NFTDrafts,
                               masterWishLists: master.WishLists,
                               masterCollectionFiles: master.CollectionFiles,
                             )(implicit executionContext: ExecutionContext) extends AbstractController(messagesControllerComponents) with I18nSupport {

  private implicit val logger: Logger = Logger(this.getClass)

  private implicit val module: String = constants.Module.NFT_CONTROLLER

  implicit val callbackOnSessionTimeout: Call = routes.CollectionController.viewCollections("art")

  def viewNFT(nftId: String): EssentialAction = cached.apply(req => req.path + "/" + nftId, constants.CommonConfig.WebAppCacheDuration) {
    withoutLoginActionAsync { implicit loginState =>
      implicit request =>
        val nft = masterNFTs.Service.tryGet(nftId)
        (for {
          nft <- nft
        } yield Ok(views.html.nft.viewNft(nft))
          ).recover {
          case baseException: BaseException => InternalServerError(baseException.failure.message)
        }
    }
  }

  def details(nftId: String): EssentialAction = cached.apply(req => req.path + "/" + nftId, constants.CommonConfig.WebAppCacheDuration) {
    withoutLoginActionAsync { implicit loginState =>
      implicit request =>
        val nft = masterNFTs.Service.tryGet(nftId)
        val liked = loginState.fold[Future[Option[Boolean]]](Future(None))(x => masterWishLists.Service.checkExists(accountId = x.username, nftId = nftId).map(Option(_)))

        def getCollection(collectionID: String) = masterCollections.Service.tryGet(collectionID)

        (for {
          nft <- nft
          liked <- liked
          collection <- getCollection(nft.collectionId)
        } yield Ok(views.html.nft.details(collection, nft, liked))
          ).recover {
          case baseException: BaseException => InternalServerError(baseException.failure.message)
        }
    }
  }

  def info(nftId: String): EssentialAction = cached.apply(req => req.path + "/" + nftId, constants.CommonConfig.WebAppCacheDuration) {
    withoutLoginActionAsync { implicit loginState =>
      implicit request =>
        val nft = masterNFTs.Service.tryGet(nftId)
        val liked = loginState.fold[Future[Option[Boolean]]](Future(None))(x => masterWishLists.Service.checkExists(accountId = x.username, nftId = nftId).map(Option(_)))

        (for {
          nft <- nft
          liked <- liked
        } yield Ok(views.html.nft.info(nft, liked))
          ).recover {
          case baseException: BaseException => InternalServerError(baseException.failure.message)
        }
    }
  }

  def file(nftId: String): Action[AnyContent] = withoutLoginActionAsync { implicit loginState =>
    implicit request =>
      val nft = masterNFTs.Service.tryGet(nftId)

      def getCollection(collectionID: String) = masterCollections.Service.tryGet(collectionID)

      (for {
        nft <- nft
        collection <- getCollection(nft.collectionId)
      } yield {
        val source: Source[ByteString, _] = StreamConverters.fromInputStream(() => utilities.AmazonS3.getFullObject(collection.name + "/nfts/" + nft.fileName).getObjectContent.getDelegateStream)
        Ok.chunked(source, inline = true, Option(nft.fileName))
      }
        ).recover {
        case baseException: BaseException => InternalServerError(baseException.failure.message)
      }
  }

  def likesCounter(nftId: String): EssentialAction = cached.apply(req => req.path + "/" + nftId, constants.CommonConfig.WebAppCacheDuration) {
    withoutLoginActionAsync { implicit loginState =>
      implicit request =>
        val countLikes = masterWishLists.Service.countLikes(nftId)
        (for {
          countLikes <- countLikes
        } yield Ok(countLikes.toString)
          ).recover {
          case baseException: BaseException => BadRequest(baseException.failure.message)
        }
    }
  }

  def selectCollection(): EssentialAction = cached.apply(req => req.path, constants.CommonConfig.WebAppCacheDuration) {
    withLoginActionAsync { implicit loginState =>
      implicit request =>
        val collections = masterCollections.Service.getByCreator(loginState.username)
        (for {
          collections <- collections
        } yield Ok(views.html.nft.selectCollection(collections))
          ).recover {
          case baseException: BaseException => BadRequest(baseException.failure.message)
        }
    }
  }

  def uploadNFTFileForm(collectionId: String): Action[AnyContent] = withLoginActionAsync { implicit loginState =>
    implicit request =>
      val checkCollectionOwner = masterCollections.Service.isOwner(id = collectionId, accountId = loginState.username)
      (for {
        collectionOwner <- checkCollectionOwner
      } yield if (collectionOwner) Ok(views.html.nft.upload(collectionId))
      else constants.Response.NOT_COLLECTION_OWNER.throwBaseException()
        ).recover {
        case baseException: BaseException => BadRequest(baseException.failure.message)
      }
  }

  def storeNFTFile(collectionId: String, documentType: String) = withLoginAction.applyMultipartFormData { implicit loginState =>
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
                utilities.FileOperations.savePartialFile(Files.readAllBytes(file.ref.path), fileUploadInfo, constants.Collection.getNFTFilePath)
                Ok
              } else constants.Response.NOT_COLLECTION_OWNER.throwBaseException()
            }
          } catch {
            case baseException: BaseException => BadRequest(baseException.failure.message)
          }
        }
      )
  }

  def uploadNFTFile(collectionId: String, documentType: String, name: String): Action[AnyContent] = withLoginActionAsync { implicit loginState =>
    implicit request =>
      val oldFilePath = constants.Collection.getNFTFilePath + name
      val fileHash = utilities.FileOperations.getFileHash(oldFilePath)
      val newFileName = fileHash + "." + utilities.FileOperations.fileExtensionFromName(name)
      val awsKey = collectionId + "/nfts/" + newFileName
      val collection = masterCollections.Service.tryGet(id = collectionId)

      def uploadToAws(collection: Collection) = if (collection.creatorId == loginState.username) Future(utilities.AmazonS3.uploadFile(objectKey = awsKey, filePath = oldFilePath))
      else {
        utilities.FileOperations.deleteFile(oldFilePath)
        constants.Response.NOT_COLLECTION_OWNER.throwFutureBaseException()
      }

      def deleteLocalFile() = Future(utilities.FileOperations.deleteFile(oldFilePath))

      def add() = masterTransactionNFTDrafts.Service.add(fileName = newFileName, collectionId = collectionId)

      (for {
        collection <- collection
        _ <- uploadToAws(collection)
        _ <- deleteLocalFile()
        _ <- add()
      } yield Ok(newFileName)
        ).recover {
        case baseException: BaseException => BadRequest(baseException.failure.message)
      }
  }

  def basicDetailsForm(collectionId: String, fileName: String): Action[AnyContent] = withLoginActionAsync { implicit loginState =>
    implicit request =>
      val isOwner = masterCollections.Service.isOwner(id = collectionId, accountId = loginState.username)
      (for {
        isOwner <- isOwner
      } yield if (isOwner) Ok(views.html.nft.nftBasicDetail(collectionId = collectionId, fileName = fileName))
      else constants.Response.NOT_COLLECTION_OWNER.throwBaseException()
        ).recover {
        case baseException: BaseException => BadRequest(baseException.failure.message)
      }
  }

  def basicDetails(): Action[AnyContent] = withLoginActionAsync { implicit loginState =>
    implicit request =>
      NFTBasicDetail.form.bindFromRequest().fold(
        formWithErrors => {
          Future(BadRequest(views.html.nft.nftBasicDetail(formWithErrors, formWithErrors.data.getOrElse(constants.FormField.COLLECTION_ID.name, ""), formWithErrors.data.getOrElse(constants.FormField.NFT_FILE_NAME.name, ""))))
        },
        basicDetailsData => {
          val isOwner = masterCollections.Service.isOwner(id = basicDetailsData.collectionId, accountId = loginState.username)

          def update(isOwner: Boolean) = if (isOwner) masterTransactionNFTDrafts.Service.updateNameDescription(fileName = basicDetailsData.fileName, name = basicDetailsData.name, description = basicDetailsData.description)
          else constants.Response.NOT_COLLECTION_OWNER.throwFutureBaseException()

          (for {
            isOwner <- isOwner
            _ <- update(isOwner)
          } yield Ok
            ).recover {
            case baseException: BaseException => BadRequest(views.html.nft.nftBasicDetail(NFTBasicDetail.form.withGlobalError(baseException.failure.message), collectionId = basicDetailsData.collectionId, fileName = basicDetailsData.fileName))
          }
        }
      )
  }

  def tagsForm(collectionId: String, fileName: String): Action[AnyContent] = withLoginActionAsync { implicit loginState =>
    implicit request =>
      val isOwner = masterCollections.Service.isOwner(id = collectionId, accountId = loginState.username)
      (for {
        isOwner <- isOwner
      } yield if (isOwner) Ok(views.html.nft.tags(collectionId = collectionId, fileName = fileName))
      else constants.Response.NOT_COLLECTION_OWNER.throwBaseException()
        ).recover {
        case baseException: BaseException => BadRequest(baseException.failure.message)
      }
  }

  def tags(): Action[AnyContent] = withLoginActionAsync { implicit loginState =>
    implicit request =>
      NFTTags.form.bindFromRequest().fold(
        formWithErrors => {
          Future(BadRequest(views.html.nft.tags(formWithErrors, formWithErrors.data.getOrElse(constants.FormField.COLLECTION_ID.name, ""), formWithErrors.data.getOrElse(constants.FormField.NFT_FILE_NAME.name, ""))))
        },
        tagsData => {
          val isOwner = masterCollections.Service.isOwner(id = tagsData.collectionId, accountId = loginState.username)

          def update(isOwner: Boolean) = if (isOwner) masterTransactionNFTDrafts.Service.updateHashTags(fileName = tagsData.fileName, hashTags = tagsData.getTags)
          else constants.Response.NOT_COLLECTION_OWNER.throwFutureBaseException()

          (for {
            isOwner <- isOwner
            _ <- update(isOwner)
          } yield Ok
            ).recover {
            case baseException: BaseException => BadRequest(views.html.nft.tags(NFTTags.form.withGlobalError(baseException.failure.message), collectionId = tagsData.collectionId, fileName = tagsData.fileName))
          }
        }
      )
  }

  def setPropertiesForm(collectionId: String, fileName: String): Action[AnyContent] = withLoginActionAsync { implicit loginState =>
    implicit request =>
      val collection = masterCollections.Service.tryGet(id = collectionId)
      (for {
        collection <- collection
      } yield if (collection.creatorId == loginState.username) Ok(views.html.nft.setProperties(collection = collection, fileName = fileName))
      else constants.Response.NOT_COLLECTION_OWNER.throwBaseException()
        ).recover {
        case baseException: BaseException => BadRequest(baseException.failure.message)
      }
  }

  def setProperties(): Action[AnyContent] = withLoginActionAsync { implicit loginState =>
    implicit request =>
      SetProperties.form.bindFromRequest().fold(
        formWithErrors => {
          val collection = masterCollections.Service.tryGet(id = formWithErrors.data.getOrElse(constants.FormField.COLLECTION_ID.name, ""))
          (for {
            collection <- collection
          } yield if (collection.creatorId == loginState.username) BadRequest(views.html.nft.setProperties(formWithErrors, collection, formWithErrors.data.getOrElse(constants.FormField.NFT_FILE_NAME.name, "")))
          else constants.Response.NOT_COLLECTION_OWNER.throwBaseException()
            ).recover {
            case baseException: BaseException => BadRequest(baseException.failure.message)
          }
        },
        setPropertiesData => {
          val isOwner = masterCollections.Service.isOwner(id = setPropertiesData.collectionId, accountId = loginState.username)


          (for {
            isOwner <- isOwner
          } yield Ok
            ).recover {
            case baseException: BaseException => BadRequest(baseException.failure.message)
          }
        }
      )
  }


}