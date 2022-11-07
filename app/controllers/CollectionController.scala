package controllers

import controllers.actions._
import exceptions.BaseException
import models.master.{Account, Collection}
import models.masterTransaction.CollectionDraft
import models.{master, masterTransaction}
import play.api.Logger
import play.api.cache.Cached
import play.api.i18n.I18nSupport
import play.api.mvc._
import views.base.companion.UploadFile
import views.collection.companion._

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
                                      masterTransactionCollectionDrafts: masterTransaction.CollectionDrafts,
                                      masterNFTs: master.NFTs,
                                      masterTransactionNFTDrafts: masterTransaction.NFTDrafts,
                                      masterCollectionFiles: master.CollectionFiles,
                                      masterWishLists: master.WishLists,
                                      utilitiesNotification: utilities.Notification,
                                    )(implicit executionContext: ExecutionContext) extends AbstractController(messagesControllerComponents) with I18nSupport {

  private implicit val logger: Logger = Logger(this.getClass)

  private implicit val module: String = constants.Module.COLLECTION_CONTROLLER

  implicit val callbackOnSessionTimeout: Call = routes.CollectionController.viewCollections("art")

  def viewCollections(category: String): EssentialAction = cached.apply(req => req.path + "/" + category, constants.CommonConfig.WebAppCacheDuration) {
    withoutLoginActionAsync { implicit loginState =>
      implicit request =>
        Future(Ok(views.html.collection.viewCollections(category)))
    }
  }

  def viewCollection(id: String): EssentialAction = cached.apply(req => req.path + "/" + id + "/" + req.session.get(constants.Session.USERNAME).getOrElse("") + "/" + req.session.get(constants.Session.TOKEN).getOrElse(""), constants.CommonConfig.WebAppCacheDuration) {
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

  def collectionsSection(category: String): EssentialAction = cached.apply(req => req.path + "/" + category, constants.CommonConfig.WebAppCacheDuration) {
    withoutLoginActionAsync { implicit loginState =>
      implicit request =>
        Future(Ok(views.html.collection.explore.collectionsSection(category)))
    }
  }

  def collectionList(category: String): EssentialAction = cached.apply(req => req.path + "/" + category, constants.CommonConfig.WebAppCacheDuration) {
    withoutLoginActionAsync { implicit loginState =>
      implicit request =>
        val totalCollections = masterCollections.Service.total(category)

        (for {
          totalCollections <- totalCollections
        } yield Ok(views.html.collection.explore.collectionList(category, totalCollections))
          ).recover {
          case baseException: BaseException => InternalServerError(baseException.failure.message)
        }
    }
  }

  def collectionsPerPage(category: String, pageNumber: Int): EssentialAction = cached.apply(req => req.path + "/" + category + "/" + pageNumber.toString, constants.CommonConfig.WebAppCacheDuration) {
    withoutLoginActionAsync { implicit loginState =>
      implicit request =>
        val collections = if (pageNumber < 1) Future(throw new BaseException(constants.Response.INVALID_PAGE_NUMBER))
        else masterCollections.Service.getByPageNumber(category, pageNumber)

        (for {
          collections <- collections
        } yield Ok(views.html.collection.explore.collectionsPerPage(collections))
          ).recover {
          case baseException: BaseException => InternalServerError(baseException.failure.message)
        }
    }
  }

  def collectionNFTs(id: String): EssentialAction = cached.apply(req => req.path + "/" + id + "/" + req.session.get(constants.Session.USERNAME).getOrElse("") + "/" + req.session.get(constants.Session.TOKEN).getOrElse(""), constants.CommonConfig.WebAppCacheDuration) {
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

  def collectionNFTsPerPage(id: String, pageNumber: Int): EssentialAction = cached.apply(req => req.path + "/" + id + "/" + pageNumber.toString + "/" + req.session.get(constants.Session.USERNAME).getOrElse("") + "/" + req.session.get(constants.Session.TOKEN).getOrElse(""), constants.CommonConfig.WebAppCacheDuration) {
    withoutLoginActionAsync { implicit loginState =>
      implicit request =>
        val collection = if (pageNumber < 1) Future(throw new BaseException(constants.Response.INVALID_PAGE_NUMBER))
        else masterCollections.Service.tryGet(id)
        val nfts = masterNFTs.Service.getByPageNumber(id, pageNumber)

        val likedNFTs = loginState.fold[Future[Seq[String]]](Future(Seq()))(x => masterWishLists.Service.getByCollection(accountId = x.username, collectionId = id))

        def nftDrafts(collection: Collection) = if (loginState.fold("")(_.username) == collection.creatorId) masterTransactionNFTDrafts.Service.getAllForCollection(id) else Future(Seq())

        (for {
          collection <- collection
          nfts <- nfts
          nftDrafts <- nftDrafts(collection)
          likedNFTs <- likedNFTs
        } yield Ok(views.html.collection.details.nftsPerPage(collection, nfts, likedNFTs, nftDrafts))
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

  // createdSection: Do not do caching here as it will then show draft collections to non-owners
  def createdSection(accountId: String): Action[AnyContent] = withoutLoginActionAsync { implicit loginState =>
    implicit request =>
      if (loginState.isDefined && loginState.get.username == accountId) {
        val account = masterAccounts.Service.tryGet(loginState.get.username)
        for {
          account <- account
        } yield Ok(views.html.profile.created.createdSection(accountId, account.accountType == constants.Account.Type.CREATOR))
      } else Future(Ok(views.html.profile.created.createdSection(accountId, false)))

  }

  // createdCollectionPerPage: Do not do caching here as it will then show draft collections to non-owners
  def createdCollectionPerPage(accountId: String, pageNumber: Int): Action[AnyContent] = withoutLoginActionAsync { implicit optionalLoginState =>
    implicit request =>
      val collections = masterCollections.Service.getByCreatorAndPage(accountId, pageNumber)
      val totalCreated = masterCollections.Service.totalCreated(accountId)
      val drafts = if (pageNumber == 1 && optionalLoginState.fold("")(_.username) == accountId) masterTransactionCollectionDrafts.Service.getByCreatorAndPage(accountId, pageNumber)
      else Future(Seq())

      (for {
        totalCreated <- totalCreated
        collections <- collections
        drafts <- drafts
      } yield Ok(views.html.profile.created.collectionPerPage(collections, drafts = drafts, totalCollections = totalCreated))
        ).recover {
        case baseException: BaseException => InternalServerError(baseException.failure.message)
      }
  }

  def createForm(): Action[AnyContent] = withLoginActionAsync { implicit loginState =>
    implicit request =>
      val totalDrafts = masterTransactionCollectionDrafts.Service.totalDrafts(loginState.username)
      for {
        totalDrafts <- totalDrafts
      } yield Ok(views.html.collection.create(totalDrafts = totalDrafts))
  }

  def create(): Action[AnyContent] = withLoginActionAsync { implicit loginState =>
    implicit request =>
      Create.form.bindFromRequest().fold(
        formWithErrors => {
          Future(BadRequest(views.html.collection.create(formWithErrors, 0)))
        },
        createData => {
          val account = masterAccounts.Service.tryGet(loginState.username)

          def collectionDraft(account: Account) = if (account.accountType == constants.Account.Type.GENESIS_CREATOR) masterTransactionCollectionDrafts.Service.add(name = createData.name, description = createData.description, socialProfiles = createData.getSocialProfiles, category = constants.Collection.Category.ART, creatorId = loginState.username, nsfw = createData.nsfw)
          else constants.Response.NOT_GENESIS_CREATOR.throwFutureBaseException()

          (for {
            account <- account
            collectionDraft <- collectionDraft(account)
          } yield PartialContent(views.html.collection.uploadFile(collectionDraft = collectionDraft))
            ).recover {
            case baseException: BaseException => BadRequest(views.html.collection.create(Create.form.withGlobalError(baseException.failure.message), 0))
          }
        }
      )
  }

  def editForm(id: String): Action[AnyContent] = withLoginActionAsync { implicit loginState =>
    implicit request =>
      val collectionDraft = masterTransactionCollectionDrafts.Service.tryGet(id)
      (for {
        collectionDraft <- collectionDraft
      } yield if (collectionDraft.creatorId == loginState.username) Ok(views.html.collection.edit(collectionDraft = Option(collectionDraft))) else constants.Response.NOT_COLLECTION_OWNER.throwBaseException()
        ).recover {
        case baseException: BaseException => BadRequest(baseException.failure.message)
      }
  }

  def edit(): Action[AnyContent] = withLoginActionAsync { implicit loginState =>
    implicit request =>
      Edit.form.bindFromRequest().fold(
        formWithErrors => {
          val collectionDraft = masterTransactionCollectionDrafts.Service.tryGet(formWithErrors.data.getOrElse(constants.FormField.COLLECTION_ID.name, ""))
          (for {
            collectionDraft <- collectionDraft
          } yield if (collectionDraft.creatorId == loginState.username) BadRequest(views.html.collection.edit(formWithErrors, Option(collectionDraft))) else constants.Response.NOT_COLLECTION_OWNER.throwBaseException()
            ).recover {
            case baseException: BaseException => BadRequest(baseException.failure.message)
          }
        },
        editData => {
          val update = masterTransactionCollectionDrafts.Service.checkOwnerAndUpdate(id = editData.collectionId, name = editData.name, description = editData.description, socialProfiles = editData.getSocialProfiles, category = constants.Collection.Category.ART, creatorId = loginState.username, nsfw = editData.nsfw)

          (for {
            collectionDraft <- update
          } yield PartialContent(views.html.collection.uploadFile(collectionDraft = collectionDraft))
            ).recover {
            case baseException: BaseException => BadRequest(views.html.collection.edit(Edit.form.withGlobalError(baseException.failure.message), None))
          }
        }
      )
  }

  def uploadCollectionFilesForm(id: String): Action[AnyContent] = withLoginActionAsync { implicit loginState =>
    implicit request =>
      val collectionDraft = masterTransactionCollectionDrafts.Service.tryGet(id)
      (for {
        collectionDraft <- collectionDraft
      } yield if (collectionDraft.creatorId == loginState.username) Ok(views.html.collection.uploadFile(collectionDraft = collectionDraft)) else constants.Response.NOT_COLLECTION_OWNER.throwBaseException()
        ).recover {
        case baseException: BaseException => BadRequest(baseException.failure.message)
      }
  }

  def uploadCollectionFileForm(id: String, documentType: String): Action[AnyContent] = withLoginActionAsync { implicit loginState =>
    implicit request =>
      val checkCollectionOwner = masterTransactionCollectionDrafts.Service.isOwner(id = id, accountId = loginState.username)
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
                utilities.FileOperations.savePartialFile(Files.readAllBytes(file.ref.path), fileUploadInfo, constants.Collection.getFilePath)
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
      val collectionDraft = masterTransactionCollectionDrafts.Service.tryGet(id = id)
      val oldFilePath = constants.Collection.getFilePath + name
      val fileHash = utilities.FileOperations.getFileHash(oldFilePath)
      val newFileName = fileHash + "." + utilities.FileOperations.fileExtensionFromName(name)
      val awsKey = id + "/others/" + newFileName

      def uploadToAws(collectionDraft: CollectionDraft) = if (collectionDraft.creatorId == loginState.username) {
        val uploadLatest = Future(utilities.AmazonS3.uploadFile(objectKey = awsKey, filePath = oldFilePath))

        def deleteOldAws() = Future(documentType match {
          case constants.Collection.File.PROFILE => collectionDraft.profileFileName.map(x => utilities.AmazonS3.deleteObject(id + "/others/" + x))
          case constants.Collection.File.COVER => collectionDraft.coverFileName.map(x => utilities.AmazonS3.deleteObject(id + "/others/" + x))
          case _ => constants.Response.INVALID_DOCUMENT_TYPE.throwBaseException()
        })

        for {
          _ <- uploadLatest
          _ <- deleteOldAws()
        } yield ()
      } else {
        utilities.FileOperations.deleteFile(oldFilePath)
        constants.Response.NOT_COLLECTION_OWNER.throwFutureBaseException()
      }

      def deleteLocalFile() = Future(utilities.FileOperations.deleteFile(oldFilePath))

      def add = documentType match {
        case constants.Collection.File.PROFILE => masterTransactionCollectionDrafts.Service.updateProfile(id = id, fileName = newFileName)
        case constants.Collection.File.COVER => masterTransactionCollectionDrafts.Service.updateCover(id = id, fileName = newFileName)
        case _ => constants.Response.INVALID_DOCUMENT_TYPE.throwFutureBaseException()
      }

      (for {
        collectionDraft <- collectionDraft
        _ <- uploadToAws(collectionDraft)
        _ <- deleteLocalFile()
        _ <- add
      } yield Ok(constants.CommonConfig.AmazonS3.s3BucketURL + awsKey)
        ).recover {
        case baseException: BaseException => BadRequest(baseException.failure.message)
      }
  }

  def definePropertiesForm(id: String): Action[AnyContent] = withLoginActionAsync { implicit loginState =>
    implicit request =>
      val collectionDraft = masterTransactionCollectionDrafts.Service.tryGet(id)
      (for {
        collectionDraft <- collectionDraft
      } yield if (collectionDraft.creatorId == loginState.username) Ok(views.html.collection.defineProperties(collectionDraft = Option(collectionDraft))) else constants.Response.NOT_COLLECTION_OWNER.throwBaseException()
        ).recover {
        case baseException: BaseException => BadRequest(baseException.failure.message)
      }
  }

  def defineProperties(): Action[AnyContent] = withLoginActionAsync { implicit loginState =>
    implicit request =>
      DefineProperties.form.bindFromRequest().fold(
        formWithErrors => {
          val collectionDraft = masterTransactionCollectionDrafts.Service.tryGet(formWithErrors.data.getOrElse(constants.FormField.COLLECTION_ID.name, ""))
          (for {
            collectionDraft <- collectionDraft
          } yield if (collectionDraft.creatorId == loginState.username) BadRequest(views.html.collection.defineProperties(formWithErrors, Option(collectionDraft))) else constants.Response.NOT_COLLECTION_OWNER.throwBaseException()
            ).recover {
            case baseException: BaseException => BadRequest(baseException.failure.message)
          }
        },
        definePropertiesData => {
          val update = masterTransactionCollectionDrafts.Service.updateProperties(definePropertiesData.collectionId, definePropertiesData.getSerializableProperties)

          def saveCollection(collectionDraft: CollectionDraft) = if (!definePropertiesData.saveAsDraft) {
            val add = masterCollections.Service.add(collectionDraft.toCollection())

            def deleteDraft() = masterTransactionCollectionDrafts.Service.deleteById(collectionDraft.id)

            for {
              _ <- add
              _ <- deleteDraft()
              _ <- utilitiesNotification.send(accountID = loginState.username, notification = constants.Notification.COLLECTION_CREATED, collectionDraft.name)(collectionDraft.id)
            } yield ()
          } else Future("")

          (for {
            collectionDraft <- update
            _ <- saveCollection(collectionDraft)
          } yield PartialContent(views.html.collection.createSuccessful(collectionDraft, definePropertiesData.saveAsDraft))
            ).recover {
            case baseException: BaseException => BadRequest(views.html.collection.defineProperties(DefineProperties.form.withGlobalError(baseException.failure.message), None))
          }
        }
      )
  }

  def deleteDraftForm(collectionId: String): Action[AnyContent] = withoutLoginAction { implicit request =>
    Ok(views.html.collection.deleteDraft(collectionId))
  }

  def deleteDraft(): Action[AnyContent] = withLoginActionAsync { implicit loginState =>
    implicit request =>
      DeleteDraft.form.bindFromRequest().fold(
        formWithErrors => {
          Future(BadRequest)
        },
        deleteDraftData => {
          val delete = masterTransactionCollectionDrafts.Service.checkOwnerAndDelete(id = deleteDraftData.collectionId, accountId = loginState.username)

          (for {
            _ <- delete
          } yield Ok
            ).recover {
            case baseException: BaseException => BadRequest(baseException.failure.message)
          }
        }
      )
  }

}