package controllers

import controllers.actions._
import exceptions.BaseException
import models.analytics.CollectionsAnalysis
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
                               collectionsAnalysis: CollectionsAnalysis,
                               masterAccounts: master.Accounts,
                               masterCollections: master.Collections,
                               masterNFTs: master.NFTs,
                               masterNFTOwners: master.NFTOwners,
                               masterNFTProperties: master.NFTProperties,
                               masterTransactionNFTDrafts: masterTransaction.NFTDrafts,
                               masterWishLists: master.WishLists,
                               masterWhitelists: master.Whitelists,
                               masterWhitelistMembers: master.WhitelistMembers,
                               masterCollectionFiles: master.CollectionFiles,
                               utilitiesNotification: utilities.Notification,
                             )(implicit executionContext: ExecutionContext) extends AbstractController(messagesControllerComponents) with I18nSupport {

  private implicit val logger: Logger = Logger(this.getClass)

  private implicit val module: String = constants.Module.NFT_CONTROLLER

  implicit val callbackOnSessionTimeout: Call = routes.CollectionController.viewCollections(constants.View.DEFAULT_COLLECTION_SECTION)

  def viewNFT(nftId: String): EssentialAction = cached(req => utilities.Session.getSessionCachingKey(req), constants.CommonConfig.WebAppCacheDuration) {
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

  def details(nftId: String): EssentialAction = cached(req => utilities.Session.getSessionCachingKey(req), constants.CommonConfig.WebAppCacheDuration) {
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

  def info(nftId: String): EssentialAction = cached(req => utilities.Session.getSessionCachingKey(req), constants.CommonConfig.WebAppCacheDuration) {
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

  def collectionInfo(nftId: String): EssentialAction = cached(req => utilities.Session.getSessionCachingKey(req), constants.CommonConfig.WebAppCacheDuration) {
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

  def likesCounter(nftId: String): EssentialAction = cached(req => utilities.Session.getSessionCachingKey(req), constants.CommonConfig.WebAppCacheDuration) {
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
              case Some(file) => if (fileUploadInfo.resumableTotalSize <= constants.File.COLLECTION_DRAFT_FILE_FORM.maxFileSize) {
                utilities.FileOperations.savePartialFile(Files.readAllBytes(file.ref.path), fileUploadInfo, constants.Collection.getNFTFilePath(collectionId))
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
      val oldFilePath = constants.Collection.getNFTFilePath(collectionId) + name
      val newFileName = utilities.FileOperations.getFileHash(oldFilePath) + "." + utilities.FileOperations.fileExtensionFromName(name)
      val awsKey = constants.NFT.getAwsKey(collectionId = collectionId, fileName = newFileName)
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
          } yield PartialContent(views.html.nft.tags(collectionId = basicDetailsData.collectionId, fileName = nftDraft.fileName, tags = nftDraft.tagNames.getOrElse(Seq.empty[String])))
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
      } yield if (isOwner) Ok(views.html.nft.tags(collectionId = collectionId, fileName = fileName, tags = nftDraft.fold[Seq[String]](Seq())(_.tagNames.getOrElse(Seq.empty[String]))))
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

          def update(collection: Collection) = if (collection.creatorId == loginState.username) masterTransactionNFTDrafts.Service.updateTagNames(fileName = tagsData.fileName, tagNames = tagsData.getTags)
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
              val add = masterNFTs.Service.add(nftDraft.toNFT(loginState.username))

              def addProperties() = masterNFTProperties.Service.addMultiple(nftDraft.getNFTProperties)

              def delete = masterTransactionNFTDrafts.Service.deleteNFT(nftDraft.fileName)

              for {
                _ <- add
                _ <- addProperties()
                _ <- delete
                _ <- collectionsAnalysis.Utility.onNewNFT(collection.id)
                _ <- utilitiesNotification.send(accountID = loginState.username, notification = constants.Notification.NFT_CREATED, nftDraft.name.getOrElse(""))(nftDraft.fileName)
              } yield ()
            } else Future()

            for {
              draft <- updateDraft
              _ <- addToNFT(draft)
            } yield draft.toNFT(loginState.username)
          } else constants.Response.NOT_COLLECTION_OWNER.throwFutureBaseException()

          (for {
            collection <- collection
            nft <- update(collection)
          } yield PartialContent(views.html.nft.createSuccessful(nft, setPropertiesData.saveNFTDraft))
            ).recover {
            case baseException: BaseException => BadRequest(views.html.base.modal.errorModal(heading = constants.View.CREATE_NFT_FAILED, subHeading = baseException.failure.message))
          }
        }
      )
  }

  def deleteDraftForm(fileName: String, fileHash: String): Action[AnyContent] = withoutLoginAction { implicit request =>
    Ok(views.html.nft.deleteDraft(fileName, fileHash))
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

          def delete(isOwner: Boolean) = masterTransactionNFTDrafts.Service.delete(deleteDraftData.nftFileName)

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


  def ownedSection(accountId: String): EssentialAction = cached(req => utilities.Session.getSessionCachingKey(req), constants.CommonConfig.WebAppCacheDuration) {
    withoutLoginActionAsync { implicit optionalLoginState =>
      implicit request =>
        Future(Ok(views.html.profile.owned.ownedSection(accountId)))
    }
  }

  def ownedNFTsPerPage(accountId: String, pageNumber: Int): EssentialAction = cached(req => utilities.Session.getSessionCachingKey(req), constants.CommonConfig.WebAppCacheDuration) {
    withoutLoginActionAsync { implicit optionalLoginState =>
      implicit request =>
        val nftIds = masterNFTOwners.Service.getByOwnerIdAndPageNumber(accountId, pageNumber)

        def nfts(nftIds: Seq[String]) = masterNFTs.Service.getByIds(nftIds)

        def collections(collectionIds: Seq[String]) = masterCollections.Service.getCollections(collectionIds)

        def wishLists(nftIds: Seq[String]) = masterWishLists.Service.get(accountId = accountId, nftIds = nftIds)

        (for {
          nftIds <- nftIds
          nfts <- nfts(nftIds)
          collections <- collections(nfts.map(_.collectionId))
          wishLists <- wishLists(nfts.map(_.fileName))
        } yield Ok(views.html.profile.owned.ownedNFTsPerPage(nfts, collections, wishLists))
          ).recover {
          case baseException: BaseException => InternalServerError(baseException.failure.message)
        }
    }
  }

}