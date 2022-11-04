package controllers

import akka.stream.scaladsl.{Source, StreamConverters}
import akka.util.ByteString
import controllers.actions._
import exceptions.BaseException
import models.master.Collection
import models.masterTransaction.NFTDraft
import models.{master, masterTransaction}
import play.api.Logger
import play.api.cache.Cached
import play.api.i18n.I18nSupport
import play.api.mvc._
import views.base.companion.UploadFile
import views.nft.companion._

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
                               masterNFTProperties: master.NFTProperties,
                               masterTransactionNFTDrafts: masterTransaction.NFTDrafts,
                               masterWishLists: master.WishLists,
                               masterCollectionFiles: master.CollectionFiles,
                             )(implicit executionContext: ExecutionContext) extends AbstractController(messagesControllerComponents) with I18nSupport {

  private implicit val logger: Logger = Logger(this.getClass)

  private implicit val module: String = constants.Module.NFT_CONTROLLER

  implicit val callbackOnSessionTimeout: Call = routes.CollectionController.viewCollections(constants.View.DEFAULT_COLLECTION_SECTION)

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
        val nftProperties = masterNFTProperties.Service.getForNFT(nftId)
        val liked = loginState.fold[Future[Option[Boolean]]](Future(None))(x => masterWishLists.Service.checkExists(accountId = x.username, nftId = nftId).map(Option(_)))

        def getCollection(collectionID: String) = masterCollections.Service.tryGet(collectionID)

        (for {
          nft <- nft
          nftProperties <- nftProperties
          liked <- liked
          collection <- getCollection(nft.collectionId)
        } yield Ok(views.html.nft.details(collection, nft, nftProperties, liked))
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

  def collectionInfo(nftId: String): EssentialAction = cached.apply(req => req.path + "/" + nftId, constants.CommonConfig.WebAppCacheDuration) {
    withoutLoginActionAsync { implicit loginState =>
      implicit request =>
        val nft = masterNFTs.Service.tryGet(nftId)

        def collection(collectionId: String) = masterCollections.Service.tryGet(collectionId)

        (for {
          nft <- nft
          collection <- collection(nft.collectionId)
        } yield Ok(views.html.nft.collectionInfo(nft, collection))
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

  def selectCollection(): Action[AnyContent] = withLoginActionAsync { implicit loginState =>
    implicit request =>
      val collections = masterCollections.Service.getByCreator(loginState.username)
      (for {
        collections <- collections
      } yield Ok(views.html.nft.selectCollection(collections))
        ).recover {
        case baseException: BaseException => BadRequest(baseException.failure.message)
      }
  }

  def uploadNFTFileForm(collectionId: String): Action[AnyContent] = withLoginActionAsync { implicit loginState =>
    implicit request =>
      val checkCollectionOwner = masterCollections.Service.isOwner(id = collectionId, accountId = loginState.username)
      val totalNftDrafts = masterTransactionNFTDrafts.Service.countAllForCollection(collectionId)
      (for {
        collectionOwner <- checkCollectionOwner
        totalNftDrafts <- totalNftDrafts
      } yield if (collectionOwner) Ok(views.html.nft.upload(collectionId, totalNftDrafts))
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
      } yield Ok(constants.CommonConfig.AmazonS3.s3BucketURL + awsKey)
        ).recover {
        case baseException: BaseException => BadRequest(messagesApi(baseException.failure.message)(request.lang))
      }
  }

  def basicDetailsForm(collectionId: String, fileName: String): Action[AnyContent] = withLoginActionAsync { implicit loginState =>
    implicit request =>
      val isOwner = masterCollections.Service.isOwner(id = collectionId, accountId = loginState.username)

      def optionalNFTDraft(isOwner: Boolean) = if (isOwner) masterTransactionNFTDrafts.Service.get(fileName)
      else constants.Response.NOT_COLLECTION_OWNER.throwFutureBaseException()

      (for {
        isOwner <- isOwner
        optionalNFTDraft <- optionalNFTDraft(isOwner)
      } yield Ok(views.html.nft.nftBasicDetail(collectionId = collectionId, fileName = fileName, optionalNFTDraft = optionalNFTDraft))
        ).recover {
        case baseException: BaseException => BadRequest(baseException.failure.message)
      }
  }

  def basicDetails(): Action[AnyContent] = withLoginActionAsync { implicit loginState =>
    implicit request =>
      NFTBasicDetail.form.bindFromRequest().fold(
        formWithErrors => {
          val fileName = formWithErrors.data.getOrElse(constants.FormField.NFT_FILE_NAME.name, "")
          val optionalNFTDraft = masterTransactionNFTDrafts.Service.get(fileName)

          (for {
            optionalNFTDraft <- optionalNFTDraft
          } yield BadRequest(views.html.nft.nftBasicDetail(formWithErrors, formWithErrors.data.getOrElse(constants.FormField.COLLECTION_ID.name, ""), fileName, optionalNFTDraft))
            ).recover {
            case baseException: BaseException => BadRequest(baseException.failure.message)
          }

        },
        basicDetailsData => {
          val isOwner = masterCollections.Service.isOwner(id = basicDetailsData.collectionId, accountId = loginState.username)

          def update(isOwner: Boolean) = if (isOwner) masterTransactionNFTDrafts.Service.updateNameDescription(fileName = basicDetailsData.fileName, name = basicDetailsData.name, description = basicDetailsData.description)
          else constants.Response.NOT_COLLECTION_OWNER.throwFutureBaseException()

          (for {
            isOwner <- isOwner
            nftDraft <- update(isOwner)
          } yield PartialContent(views.html.nft.tags(collectionId = basicDetailsData.collectionId, fileName = nftDraft.fileName, tags = Seq()))
            ).recover {
            case baseException: BaseException => BadRequest(views.html.nft.nftBasicDetail(NFTBasicDetail.form.withGlobalError(baseException.failure.message), collectionId = basicDetailsData.collectionId, fileName = basicDetailsData.fileName, None))
          }
        }
      )
  }

  def tagsForm(collectionId: String, fileName: String): Action[AnyContent] = withLoginActionAsync { implicit loginState =>
    implicit request =>
      val isOwner = masterCollections.Service.isOwner(id = collectionId, accountId = loginState.username)
      val nftDraft = masterTransactionNFTDrafts.Service.get(fileName)
      (for {
        isOwner <- isOwner
        nftDraft <- nftDraft
      } yield if (isOwner) Ok(views.html.nft.tags(collectionId = collectionId, fileName = fileName, tags = nftDraft.fold[Seq[String]](Seq())(_.hashTags.getOrElse(Seq.empty[String]))))
      else constants.Response.NOT_COLLECTION_OWNER.throwBaseException()
        ).recover {
        case baseException: BaseException => BadRequest(baseException.failure.message)
      }
  }

  def tags(): Action[AnyContent] = withLoginActionAsync { implicit loginState =>
    implicit request =>
      NFTTags.form.bindFromRequest().fold(
        formWithErrors => {
          Future(BadRequest(views.html.nft.tags(formWithErrors, formWithErrors.data.getOrElse(constants.FormField.COLLECTION_ID.name, ""), formWithErrors.data.getOrElse(constants.FormField.NFT_FILE_NAME.name, ""), formWithErrors.data.getOrElse(constants.FormField.NFT_TAGS.name, "").split(","))))
        },
        tagsData => {
          val collection = masterCollections.Service.tryGet(id = tagsData.collectionId)

          def update(collection: Collection) = if (collection.creatorId == loginState.username) masterTransactionNFTDrafts.Service.updateHashTags(fileName = tagsData.fileName, hashTags = tagsData.getTags)
          else constants.Response.NOT_COLLECTION_OWNER.throwFutureBaseException()

          (for {
            collection <- collection
            _ <- update(collection)
          } yield PartialContent(views.html.nft.setProperties(collection = collection, fileName = tagsData.fileName))
            ).recover {
            case baseException: BaseException => BadRequest(views.html.nft.tags(NFTTags.form.withGlobalError(baseException.failure.message), collectionId = tagsData.collectionId, fileName = tagsData.fileName, tags = tagsData.getTags))
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
          val collection = masterCollections.Service.tryGet(id = setPropertiesData.collectionId)

          def update(collection: Collection) = if (collection.creatorId == loginState.username) {
            val updateDraft = masterTransactionNFTDrafts.Service.updateProperties(setPropertiesData.fileName, setPropertiesData.getNFTProperties(collection.properties.getOrElse(Seq())))

            def addToNFT(nftDraft: NFTDraft) = if (!setPropertiesData.saveNFTDraft) {
              val add = masterNFTs.Service.add(nftDraft.toNFT)

              def addProperties = masterNFTProperties.Service.addMultiple(nftDraft.getNFTProperties)

              def delete = masterTransactionNFTDrafts.Service.deleteNFT(nftDraft.fileName)

              for {
                _ <- add
                _ <- addProperties
                _ <- delete
              } yield ()
            } else Future()

            for {
              draft <- updateDraft
              _ <- addToNFT(draft)
            } yield draft.toNFT
          } else constants.Response.NOT_COLLECTION_OWNER.throwFutureBaseException()

          (for {
            collection <- collection
            nft <- update(collection)
          } yield PartialContent(views.html.nft.createSuccessful(nft, setPropertiesData.saveNFTDraft))
            ).recover {
            case baseException: BaseException => BadRequest(baseException.failure.message)
          }
        }
      )
  }

  def deleteDraft(): Action[AnyContent] = withLoginActionAsync { implicit loginState =>
    implicit request =>
      DeleteDraft.form.bindFromRequest().fold(
        formWithErrors => {
          Future(BadRequest)
        },
        deleteDraftData => {
          val nftDraft = masterTransactionNFTDrafts.Service.tryGet(deleteDraftData.nftFileName)

          def collection(collectionId: String) = masterCollections.Service.tryGet(id = collectionId)

          def delete(isOwner: Boolean) = masterTransactionNFTDrafts.Service.deleteById(deleteDraftData.nftFileName)

          (for {
            nftDraft <- nftDraft
            collection <- collection(nftDraft.collectionId)
            _ <- delete(collection.creatorId == loginState.username)
          } yield Ok
            ).recover {
            case baseException: BaseException => BadRequest(baseException.failure.message)
          }
        }
      )
  }


}