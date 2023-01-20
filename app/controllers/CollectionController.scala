package controllers

import controllers.actions._
import exceptions.BaseException
import models.analytics.CollectionsAnalysis
import models.master.{Collection, PublicListing}
import models.masterTransaction.CollectionDraft
import models.{master, masterTransaction}
import play.api.Logger
import play.api.cache.Cached
import play.api.i18n.I18nSupport
import play.api.mvc._
import utilities.MicroNumber
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
                                      collectionsAnalysis: CollectionsAnalysis,
                                      masterAccounts: master.Accounts,
                                      masterCollections: master.Collections,
                                      masterTransactionCollectionDrafts: masterTransaction.CollectionDrafts,
                                      masterTransactionPublicListingNFTTransactions: masterTransaction.PublicListingNFTTransactions,
                                      masterTransactionSaleNFTTransactions: masterTransaction.SaleNFTTransactions,
                                      masterNFTs: master.NFTs,
                                      masterSales: master.Sales,
                                      masterPublicListings: master.PublicListings,
                                      masterNFTOwners: master.NFTOwners,
                                      masterTransactionNFTDrafts: masterTransaction.NFTDrafts,
                                      masterWhitelists: master.Whitelists,
                                      masterWhitelistMembers: master.WhitelistMembers,
                                      masterWishLists: master.WishLists,
                                      utilitiesNotification: utilities.Notification,
                                    )(implicit executionContext: ExecutionContext) extends AbstractController(messagesControllerComponents) with I18nSupport {

  private implicit val logger: Logger = Logger(this.getClass)

  private implicit val module: String = constants.Module.COLLECTION_CONTROLLER

  implicit val callbackOnSessionTimeout: Call = routes.CollectionController.viewCollections(constants.View.DEFAULT_COLLECTION_SECTION)

  def viewCollections(category: String): EssentialAction = cached(req => utilities.Session.getSessionCachingKey(req), constants.CommonConfig.WebAppCacheDuration) {
    withoutLoginActionAsync { implicit loginState =>
      implicit request =>
        Future(Ok(views.html.collection.viewCollections(category)))
    }
  }

  def viewPublicListedCollections(): EssentialAction = cached(req => utilities.Session.getSessionCachingKey(req), constants.CommonConfig.WebAppCacheDuration) {
    withoutLoginActionAsync { implicit loginState =>
      implicit request =>
        Future(Ok(views.html.collection.viewPublicListedCollections()))
    }
  }

  def viewWhitelistSaleCollections(): EssentialAction = cached(req => utilities.Session.getSessionCachingKey(req), constants.CommonConfig.WebAppCacheDuration) {
    withoutLoginActionAsync { implicit loginState =>
      implicit request =>
        Future(Ok(views.html.collection.viewWhitelistSaleCollections()))
    }
  }

  def viewCollection(id: String, showPublicListing: Boolean): EssentialAction = cached(req => utilities.Session.getSessionCachingKey(req), constants.CommonConfig.WebAppCacheDuration) {
    withoutLoginActionAsync { implicit loginState =>
      implicit request =>
        val collection = masterCollections.Service.tryGet(id)
        (for {
          collection <- collection
        } yield Ok(views.html.collection.viewCollection(collection, showPublicListing))
          ).recover {
          case baseException: BaseException => InternalServerError(baseException.failure.message)
        }
    }
  }

  def collectionsSection(category: String): EssentialAction = cached(req => utilities.Session.getSessionCachingKey(req), constants.CommonConfig.WebAppCacheDuration) {
    withoutLoginActionAsync { implicit loginState =>
      implicit request =>
        Future(Ok(views.html.collection.all.collectionsSection(category)))
    }
  }

  def collectionList(category: String): EssentialAction = cached(req => utilities.Session.getSessionCachingKey(req), constants.CommonConfig.WebAppCacheDuration) {
    withoutLoginActionAsync { implicit loginState =>
      implicit request =>
        val totalCollections = masterCollections.Service.total(category)

        (for {
          totalCollections <- totalCollections
        } yield Ok(views.html.collection.all.collectionList(category, totalCollections))
          ).recover {
          case baseException: BaseException => BadRequest(baseException.failure.message)
        }
    }
  }

  def collectionsPerPage(category: String, pageNumber: Int): EssentialAction = cached(req => utilities.Session.getSessionCachingKey(req), constants.CommonConfig.WebAppCacheDuration) {
    withoutLoginActionAsync { implicit loginState =>
      implicit request =>
        val collections = if (pageNumber < 1) Future(throw new BaseException(constants.Response.INVALID_PAGE_NUMBER))
        else masterCollections.Service.getByPageNumber(category, pageNumber)

        (for {
          collections <- collections
        } yield Ok(views.html.collection.all.collectionsPerPage(collections))
          ).recover {
          case baseException: BaseException => BadRequest(baseException.failure.message)
        }
    }
  }

  def publicListedCollectionsSection(): EssentialAction = cached(req => utilities.Session.getSessionCachingKey(req), constants.CommonConfig.WebAppCacheDuration) {
    withoutLoginActionAsync { implicit loginState =>
      implicit request =>
        val totalCollections = masterPublicListings.Service.total
        (for {
          totalCollections <- totalCollections
        } yield Ok(views.html.collection.publicListed.collectionsSection(totalCollections))
          ).recover {
          case baseException: BaseException => BadRequest(baseException.failure.message)
        }
    }
  }

  def publicListedCollectionsPerPage(pageNumber: Int): EssentialAction = cached(req => utilities.Session.getSessionCachingKey(req), constants.CommonConfig.WebAppCacheDuration) {
    withoutLoginActionAsync { implicit loginState =>
      implicit request =>
        val publicListings = if (pageNumber < 1) Future(throw new BaseException(constants.Response.INVALID_PAGE_NUMBER))
        else masterPublicListings.Service.getByPageNumber(pageNumber)

        def collections(ids: Seq[String]) = masterCollections.Service.getCollections(ids)

        (for {
          publicListings <- publicListings
          collections <- collections(publicListings.map(_.collectionId))
        } yield Ok(views.html.collection.publicListed.collectionsPerPage(collections))
          ).recover {
          case baseException: BaseException => InternalServerError(baseException.failure.message)
        }
    }
  }

  def whitelistSaleCollectionsSection(): EssentialAction = cached(req => utilities.Session.getSessionCachingKey(req), constants.CommonConfig.WebAppCacheDuration) {
    withoutLoginActionAsync { implicit loginState =>
      implicit request =>
        val totalCollections = masterSales.Service.total
        (for {
          totalCollections <- totalCollections
        } yield Ok(views.html.collection.whitelistSale.collectionsSection(totalCollections))
          ).recover {
          case baseException: BaseException => BadRequest(baseException.failure.message)
        }
    }
  }

  def whitelistSaleCollectionsPerPage(pageNumber: Int): EssentialAction = cached(req => utilities.Session.getSessionCachingKey(req), constants.CommonConfig.WebAppCacheDuration) {
    withoutLoginActionAsync { implicit loginState =>
      implicit request =>
        val sales = if (pageNumber < 1) Future(throw new BaseException(constants.Response.INVALID_PAGE_NUMBER))
        else masterSales.Service.getByPageNumber(pageNumber)

        def collections(ids: Seq[String]) = masterCollections.Service.getCollections(ids)

        (for {
          sales <- sales
          collections <- collections(sales.map(_.collectionId))
        } yield Ok(views.html.collection.whitelistSale.collectionsPerPage(collections))
          ).recover {
          case baseException: BaseException => InternalServerError(baseException.failure.message)
        }
    }
  }


  def collectionNFTs(id: String): EssentialAction = cached(req => utilities.Session.getSessionCachingKey(req), constants.CommonConfig.WebAppCacheDuration) {
    withoutLoginActionAsync { implicit loginState =>
      implicit request =>
        val collection = masterCollections.Service.tryGet(id)
        val sale = masterSales.Service.getSaleByCollectionId(id)
        val publicListing = masterPublicListings.Service.getPublicListingByCollectionId(id)

        def randomNFTs(get: Boolean) = if (get) masterNFTs.Service.getRandomNFTs(id, 5, Seq.empty) else Future(Seq())

        (for {
          collection <- collection
          sale <- sale
          publicListing <- publicListing
          randomNFTs <- randomNFTs(sale.nonEmpty || publicListing.nonEmpty)
        } yield Ok(views.html.collection.details.collectionNFTs(collection, sale, publicListing, randomNFTs))
          ).recover {
          case baseException: BaseException => InternalServerError(baseException.failure.message)
        }
    }
  }

  def collectionNFTsPerPage(id: String, pageNumber: Int): EssentialAction = cached(req => utilities.Session.getSessionCachingKey(req), constants.CommonConfig.WebAppCacheDuration) {
    withoutLoginActionAsync { implicit optionalLoginState =>
      implicit request =>
        val collection = if (pageNumber < 1) Future(throw new BaseException(constants.Response.INVALID_PAGE_NUMBER))
        else masterCollections.Service.tryGet(id)
        val likedNFTs = optionalLoginState.fold[Future[Seq[String]]](Future(Seq()))(x => masterWishLists.Service.getByCollection(accountId = x.username, collectionId = id))

        def getNFTs(creatorId: String) = if (optionalLoginState.fold("")(_.username) == creatorId || pageNumber == 1) masterNFTs.Service.getByPageNumber(id, pageNumber) else Future(Seq())

        def nftDrafts(collection: Collection) = if (optionalLoginState.fold("")(_.username) == collection.creatorId) masterTransactionNFTDrafts.Service.getAllForCollection(id) else Future(Seq())

        (for {
          collection <- collection
          nfts <- getNFTs(collection.creatorId)
          nftDrafts <- nftDrafts(collection)
          likedNFTs <- likedNFTs
        } yield Ok(views.html.collection.details.nftsPerPage(collection, nfts, likedNFTs, nftDrafts, pageNumber))
          ).recover {
          case baseException: BaseException => InternalServerError(baseException.failure.message)
        }
    }
  }

  def info(id: String): EssentialAction = cached(req => utilities.Session.getSessionCachingKey(req), constants.CommonConfig.WebAppCacheDuration) {
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

  def topRightCard(id: String, showPublicListing: Boolean): Action[AnyContent] = withoutLoginActionAsync { implicit optionalLoginState =>
    implicit request =>
      val collectionAnalysis = collectionsAnalysis.Service.tryGet(id)
      val collection = masterCollections.Service.tryGet(id)
      val publicListing = if (showPublicListing) masterPublicListings.Service.getPublicListingByCollectionId(id) else Future(None)

      def getTotalPublicListingSold(publicListingId: Option[String]): Future[Long] = if (publicListingId.isDefined) masterTransactionPublicListingNFTTransactions.Service.getTotalPublicListingSold(publicListingId.get).map(_.toLong) else Future(0L)

      def getTotalWhitelistSaleSold(saleId: Option[String]): Future[Long] = if (saleId.isDefined) masterTransactionSaleNFTTransactions.Service.getTotalWhitelistSaleSold(saleId.get).map(_.toLong) else Future(0L)

      val getSalesInfo = if (optionalLoginState.isDefined) {
        val sale = if (!showPublicListing) masterSales.Service.getSaleByCollectionId(id) else Future(None)

        def isMember(whitelistId: Option[String]) = if (whitelistId.isDefined) masterWhitelistMembers.Service.isMember(whitelistId.get, optionalLoginState.get.username) else Future(false)

        for {
          sale <- sale
          isMember <- isMember(sale.map(_.whitelistId))
        } yield (sale, isMember)
      } else Future(None, false)

      (for {
        collectionAnalysis <- collectionAnalysis
        collection <- collection
        publicListing <- publicListing
        totalPublicListingSold <- getTotalPublicListingSold(publicListing.map(_.id))
        (sale, isMember) <- getSalesInfo
        totalWhitelistSaleSold <- getTotalWhitelistSaleSold(sale.map(_.id))
      } yield Ok(views.html.collection.details.topRightCard(collectionAnalysis = collectionAnalysis, collection = collection, sale = sale, publicListing = publicListing, isMember = isMember, publicListingSold = totalPublicListingSold, whitelistSaleSold = totalWhitelistSaleSold))
        ).recover {
        case baseException: BaseException => BadRequest(baseException.failure.message)
      }
  }

  def commonCardInfo(id: String): EssentialAction = cached(req => utilities.Session.getSessionCachingKey(req), constants.CommonConfig.WebAppCacheDuration) {
    withoutLoginActionAsync { implicit loginState =>
      implicit request =>
        val collectionAnalysis = collectionsAnalysis.Service.tryGet(id)
        val publicListing = masterPublicListings.Service.getPublicListingByCollectionId(id)

        def publicListingStatus(publicListing: Option[PublicListing]) = if (publicListing.isDefined) masterTransactionPublicListingNFTTransactions.Service.getTotalPublicListingSold(publicListing.get.id).map(x => publicListing.get.getStatus(x).id) else Future(0)


        (for {
          collectionAnalysis <- collectionAnalysis
          publicListing <- publicListing
          publicListingStatus <- publicListingStatus(publicListing)
        } yield Ok(s"${collectionAnalysis.totalNFTs.toString}|${publicListing.fold(MicroNumber.zero)(_.price).toString}|${publicListingStatus}")
          ).recover {
          case baseException: BaseException => BadRequest(baseException.failure.message)
        }
    }
  }

  def createdSection(accountId: String): EssentialAction = cached(req => utilities.Session.getSessionCachingKey(req), constants.CommonConfig.WebAppCacheDuration) {
    withoutLoginActionAsync { implicit loginState =>
      implicit request =>
        Future(Ok(views.html.profile.created.createdSection(accountId, allowCreateCollection = loginState.fold("")(_.username) == accountId)))
    }
  }

  def createdCollectionPerPage(accountId: String, pageNumber: Int): EssentialAction = cached(req => utilities.Session.getSessionCachingKey(req), constants.CommonConfig.WebAppCacheDuration) {
    withoutLoginActionAsync { implicit optionalLoginState =>
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
          val collectionDraft = masterTransactionCollectionDrafts.Service.add(name = createData.name, description = createData.description, socialProfiles = createData.getSocialProfiles, category = constants.Collection.Category.ART, creatorId = loginState.username, nsfw = createData.nsfw)

          (for {
            collectionDraft <- collectionDraft
          } yield PartialContent(views.html.collection.uploadDraftFile(collectionDraft = collectionDraft))
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
          } yield PartialContent(views.html.collection.uploadDraftFile(collectionDraft = collectionDraft))
            ).recover {
            case baseException: BaseException => BadRequest(views.html.collection.edit(Edit.form.withGlobalError(baseException.failure.message), None))
          }
        }
      )
  }

  def uploadCollectionDraftFilesForm(id: String): Action[AnyContent] = withLoginActionAsync { implicit loginState =>
    implicit request =>
      val collectionDraft = masterTransactionCollectionDrafts.Service.tryGet(id)
      (for {
        collectionDraft <- collectionDraft
      } yield if (collectionDraft.creatorId == loginState.username) Ok(views.html.collection.uploadDraftFile(collectionDraft = collectionDraft)) else constants.Response.NOT_COLLECTION_OWNER.throwBaseException()
        ).recover {
        case baseException: BaseException => BadRequest(baseException.failure.message)
      }
  }

  def uploadCollectionDraftFileForm(id: String, documentType: String): Action[AnyContent] = withLoginActionAsync { implicit loginState =>
    implicit request =>
      val checkCollectionOwner = masterTransactionCollectionDrafts.Service.isOwner(id = id, accountId = loginState.username)
      (for {
        collectionOwner <- checkCollectionOwner
      } yield if (collectionOwner) Ok(views.html.base.commonUploadFile(constants.File.COLLECTION_DRAFT_FILE_FORM, id = id, documentType = documentType))
      else constants.Response.NOT_COLLECTION_OWNER.throwBaseException()
        ).recover {
        case baseException: BaseException => BadRequest(baseException.failure.message)
      }
  }

  def storeCollectionDraftFile(id: String, documentType: String) = withLoginAction.applyMultipartFormData { implicit loginState =>
    implicit request =>
      UploadFile.form.bindFromRequest().fold(
        formWithErrors => {
          BadRequest(constants.View.BAD_REQUEST)
        },
        fileUploadInfo => {
          try {
            request.body.file(constants.File.KEY_FILE) match {
              case None => BadRequest(constants.View.BAD_REQUEST)
              case Some(file) => if (fileUploadInfo.resumableTotalSize <= constants.File.COLLECTION_DRAFT_FILE_FORM.maxFileSize) {
                utilities.FileOperations.savePartialFile(Files.readAllBytes(file.ref.path), fileUploadInfo, utilities.Collection.getFilePath(id))
                Ok
              } else constants.Response.NOT_COLLECTION_OWNER.throwBaseException()
            }
          } catch {
            case baseException: BaseException => BadRequest(baseException.failure.message)
          }
        }
      )
  }

  def uploadCollectionDraftFile(id: String, documentType: String, name: String): Action[AnyContent] = withLoginActionAsync { implicit loginState =>
    implicit request =>
      val collectionDraft = masterTransactionCollectionDrafts.Service.tryGet(id = id)
      val oldFilePath = utilities.Collection.getFilePath(id) + name
      val newFileName = utilities.FileOperations.getFileHash(oldFilePath) + "." + utilities.FileOperations.fileExtensionFromName(name)
      val awsKey = utilities.Collection.getOthersFileAwsKey(collectionId = id, fileName = newFileName)

      def uploadToAws(collectionDraft: CollectionDraft) = if (collectionDraft.creatorId == loginState.username) {
        val uploadLatest = Future(utilities.AmazonS3.uploadFile(objectKey = awsKey, filePath = oldFilePath))

        def deleteOldAws() = Future(documentType match {
          case constants.Collection.File.PROFILE => collectionDraft.profileFileName.map(x => utilities.AmazonS3.deleteObject(utilities.Collection.getOthersFileAwsKey(collectionId = id, fileName = x)))
          case constants.Collection.File.COVER => collectionDraft.coverFileName.map(x => utilities.AmazonS3.deleteObject(utilities.Collection.getOthersFileAwsKey(collectionId = id, fileName = x)))
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
      } yield if (collectionDraft.creatorId == loginState.username) Ok(views.html.collection.defineProperties(collectionDraftId = id, collectionDraft = Option(collectionDraft))) else constants.Response.NOT_COLLECTION_OWNER.throwBaseException()
        ).recover {
        case baseException: BaseException => BadRequest(baseException.failure.message)
      }
  }

  def defineProperties(): Action[AnyContent] = withLoginActionAsync { implicit loginState =>
    implicit request =>
      DefineProperties.form.bindFromRequest().fold(
        formWithErrors => {
          val collectionDraftId = formWithErrors.data.getOrElse(constants.FormField.COLLECTION_ID.name, "")
          val collectionDraft = masterTransactionCollectionDrafts.Service.tryGet(collectionDraftId)
          (for {
            collectionDraft <- collectionDraft
          } yield if (collectionDraft.creatorId == loginState.username) BadRequest(views.html.collection.defineProperties(formWithErrors, collectionDraftId, Option(collectionDraft))) else constants.Response.NOT_COLLECTION_OWNER.throwBaseException()
            ).recover {
            case baseException: BaseException => BadRequest(baseException.failure.message)
          }
        },
        definePropertiesData => {
          val update = masterTransactionCollectionDrafts.Service.updateProperties(definePropertiesData.collectionId, definePropertiesData.getSerializableProperties)

          def saveCollection(collectionDraft: CollectionDraft) = if (!definePropertiesData.saveAsDraft) {
            val add = masterCollections.Service.add(collectionDraft.toCollection())

            def deleteDraft() = masterTransactionCollectionDrafts.Service.delete(collectionDraft.id)

            def updateAccountToCreator() = masterAccounts.Service.updateAccountToCreator(loginState.username)

            for {
              _ <- add
              _ <- updateAccountToCreator()
              _ <- deleteDraft()
              _ <- collectionsAnalysis.Utility.onNewCollection(collectionDraft.id)
              _ <- utilitiesNotification.send(accountID = loginState.username, notification = constants.Notification.COLLECTION_CREATED, collectionDraft.name)(s"'${collectionDraft.id}'")
            } yield ()
          } else Future("")

          (for {
            collectionDraft <- update
            _ <- saveCollection(collectionDraft)
          } yield PartialContent(views.html.collection.createSuccessful(collectionDraft, definePropertiesData.saveAsDraft))
            ).recover {
            case baseException: BaseException => BadRequest(views.html.collection.defineProperties(DefineProperties.form.withGlobalError(baseException.failure.message), definePropertiesData.collectionId, None))
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

  def countForCreatorNotForSell(collectionId: String, accountId: String): EssentialAction = cached(req => utilities.Session.getSessionCachingKey(req), constants.CommonConfig.WebAppCacheDuration) {
    withoutLoginActionAsync { implicit loginState =>
      implicit request =>
        val countNFTs = masterNFTOwners.Service.countForCreatorNotForSell(collectionId = collectionId, creatorId = accountId)

        (for {
          countNFTs <- countNFTs
        } yield Ok(if (countNFTs <= 50) countNFTs.toString else (countNFTs / 10).toString)
          ).recover {
          case _: BaseException => BadRequest("0")
        }
    }
  }

  def uploadCollectionFilesForm(id: String): Action[AnyContent] = withLoginActionAsync { implicit loginState =>
    implicit request =>
      val collection = masterCollections.Service.tryGet(id)
      (for {
        collection <- collection
      } yield if (collection.creatorId == loginState.username) Ok(views.html.collection.uploadFile(collection = collection)) else constants.Response.NOT_COLLECTION_OWNER.throwBaseException()
        ).recover {
        case baseException: BaseException => BadRequest(baseException.failure.message)
      }
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
      UploadFile.form.bindFromRequest().fold(
        formWithErrors => {
          BadRequest(constants.View.BAD_REQUEST)
        },
        fileUploadInfo => {
          try {
            request.body.file(constants.File.KEY_FILE) match {
              case None => BadRequest(constants.View.BAD_REQUEST)
              case Some(file) => if (fileUploadInfo.resumableTotalSize <= constants.File.COLLECTION_FILE_FORM.maxFileSize) {
                utilities.FileOperations.savePartialFile(Files.readAllBytes(file.ref.path), fileUploadInfo, utilities.Collection.getFilePath(id))
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
      val collection = masterCollections.Service.tryGet(id = id)
      val oldFilePath = utilities.Collection.getFilePath(id) + name
      val newFileName = utilities.FileOperations.getFileHash(oldFilePath) + "." + utilities.FileOperations.fileExtensionFromName(name)
      val awsKey = utilities.Collection.getOthersFileAwsKey(collectionId = id, fileName = newFileName)

      def uploadToAws(collection: Collection) = if (collection.creatorId == loginState.username) {
        val uploadLatest = Future(utilities.AmazonS3.uploadFile(objectKey = awsKey, filePath = oldFilePath))

        def deleteOldAws() = Future(documentType match {
          case constants.Collection.File.PROFILE => collection.profileFileName.map(x => utilities.AmazonS3.deleteObject(utilities.Collection.getOthersFileAwsKey(collectionId = id, fileName = x)))
          case constants.Collection.File.COVER => collection.coverFileName.map(x => utilities.AmazonS3.deleteObject(utilities.Collection.getOthersFileAwsKey(collectionId = id, fileName = x)))
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

      def updateFile = documentType match {
        case constants.Collection.File.PROFILE => masterCollections.Service.updateProfile(id = id, fileName = newFileName)
        case constants.Collection.File.COVER => masterCollections.Service.updateCover(id = id, fileName = newFileName)
        case _ => constants.Response.INVALID_DOCUMENT_TYPE.throwFutureBaseException()
      }

      (for {
        collection <- collection
        _ <- uploadToAws(collection)
        _ <- deleteLocalFile()
        _ <- updateFile
      } yield Ok(constants.CommonConfig.AmazonS3.s3BucketURL + awsKey)
        ).recover {
        case baseException: BaseException => BadRequest(baseException.failure.message)
      }
  }

  def genesisTypeForm(): Action[AnyContent] = withLoginActionAsync { implicit loginState =>
    implicit request =>
      Future(Ok(views.html.base.genesisTypeForm()))
  }
}