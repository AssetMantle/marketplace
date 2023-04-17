package controllers

import controllers.actions._
import exceptions.BaseException
import models.analytics.CollectionsAnalysis
import models.master.{Collection, Key, NFT, NFTOwner}
import models.masterTransaction.NFTDraft
import models.{blockchain, master, masterTransaction}
import org.bitcoinj.core.ECKey
import play.api.Logger
import play.api.cache.Cached
import play.api.i18n.I18nSupport
import play.api.mvc._
import utilities.MicroNumber
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
                               masterKeys: master.Keys,
                               blockchainBalances: blockchain.Balances,
                               masterNFTs: master.NFTs,
                               masterSales: master.Sales,
                               masterNFTOwners: master.NFTOwners,
                               masterNFTProperties: master.NFTProperties,
                               masterNFTTags: master.NFTTags,
                               masterTransactionNFTDrafts: masterTransaction.NFTDrafts,
                               masterWishLists: master.WishLists,
                               masterWhitelists: master.Whitelists,
                               masterWhitelistMembers: master.WhitelistMembers,
                               masterSecondaryMarkets: master.SecondaryMarkets,
                               masterTransactionTokenPrices: masterTransaction.TokenPrices,
                               utilitiesNotification: utilities.Notification,
                               masterTransactionNFTMintingFeeTransactions: masterTransaction.NFTMintingFeeTransactions,
                               masterTransactionNFTTransferTransactions: masterTransaction.NFTTransferTransactions,
                             )(implicit executionContext: ExecutionContext) extends AbstractController(messagesControllerComponents) with I18nSupport {

  private implicit val logger: Logger = Logger(this.getClass)

  private implicit val module: String = constants.Module.NFT_CONTROLLER

  implicit val callbackOnSessionTimeout: Call = routes.CollectionController.viewCollections()

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
        } yield Ok(views.html.nft.detail.view(collection, nft, nftProperties, liked))
          ).recover {
          case baseException: BaseException => InternalServerError(baseException.failure.message)
        }
    }
  }

  def detailViewLeftCards(nftId: String): EssentialAction = cached(req => utilities.Session.getSessionCachingKey(req), constants.CommonConfig.WebAppCacheDuration) {
    withoutLoginAction {
      implicit request =>
        Ok(views.html.nft.detail.leftCards(nftId))
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
        } yield Ok(views.html.nft.detail.info(nft, liked))
          ).recover {
          case baseException: BaseException => InternalServerError(baseException.failure.message)
        }
    }
  }

  def detailViewRightCards(nftId: String): EssentialAction = cached(req => utilities.Session.getSessionCachingKey(req), constants.CommonConfig.WebAppCacheDuration) {
    withoutLoginActionAsync { implicit loginState =>
      implicit request =>
        val lowestPriceSecondaryMarket = masterSecondaryMarkets.Service.getByNFTIdAndLowestPrice(nftId)
        val userSecondaryMarket = if (loginState.isDefined) {
          masterSecondaryMarkets.Service.getByNFTIdAndSellerId(nftId = nftId, sellerId = loginState.get.username)
        } else Future(None)
        val nft = masterNFTs.Service.tryGet(nftId)
        val nftOwner: Future[Option[NFTOwner]] = if (loginState.isDefined) {
          masterNFTOwners.Service.get(nftId = nftId, ownerId = loginState.get.username)
        } else Future(None)

        def collection(collectionId: String) = masterCollections.Service.tryGet(collectionId)

        def collectionAnalysis(collectionId: String) = collectionsAnalysis.Service.tryGet(collectionId)

        (for {
          nft <- nft
          lowestPriceSecondaryMarket <- lowestPriceSecondaryMarket
          userSecondaryMarket <- userSecondaryMarket
          nftOwner <- nftOwner
          collection <- collection(nft.collectionId)
          collectionAnalysis <- collectionAnalysis(nft.collectionId)
        } yield Ok(views.html.nft.detail.rightCards(collection, collectionAnalysis, nft, userNFTOwner = nftOwner, lowestPriceSecondaryMarket = lowestPriceSecondaryMarket, userSecondaryMarket = userSecondaryMarket, tokenPrice = masterTransactionTokenPrices.Service.getLatestPrice))
          ).recover {
          case baseException: BaseException => InternalServerError(baseException.failure.message)
        }
    }
  }

  def collectionInfo(nftId: String): EssentialAction = cached(req => utilities.Session.getSessionCachingKey(req), constants.CommonConfig.WebAppCacheDuration) {
    withoutLoginActionAsync { implicit loginState =>
      implicit request =>
        val nft = masterNFTs.Service.tryGet(nftId)
        val countOwners = masterNFTOwners.Service.countOwners(nftId)

        def nftOwner(countOwners: Int): Future[Option[NFTOwner]] = if (countOwners == 1) {
          masterNFTOwners.Service.getByNFTID(nftId = nftId).map(Option(_))
        } else Future(None)

        def collection(collectionId: String) = masterCollections.Service.tryGet(collectionId)

        (for {
          nft <- nft
          countOwners <- countOwners
          nftOwner <- nftOwner(countOwners)
          collection <- collection(nft.collectionId)
        } yield Ok(views.html.nft.detail.collectionInfo(nft, collection, nftOwner, countOwners))
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
      UploadFile.form.bindFromRequest().fold(
        formWithErrors => {
          BadRequest(constants.View.BAD_REQUEST)
        },
        fileUploadInfo => {
          try {
            request.body.file(constants.File.KEY_FILE) match {
              case None => BadRequest(constants.View.BAD_REQUEST)
              case Some(file) => if (fileUploadInfo.resumableTotalSize <= constants.File.COLLECTION_DRAFT_FILE_FORM.maxFileSize) {
                utilities.FileOperations.savePartialFile(Files.readAllBytes(file.ref.path), fileUploadInfo, utilities.Collection.getNFTFilePath(collectionId))
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
      val oldFilePath = utilities.Collection.getNFTFilePath(collectionId) + name
      val nftId = utilities.FileOperations.getFileHash(oldFilePath)
      val fileExtension = utilities.FileOperations.fileExtensionFromName(name)
      val newFileName = nftId + "." + fileExtension
      val awsKey = utilities.NFT.getAWSKey(fileName = newFileName)
      val collection = masterCollections.Service.tryGet(id = collectionId)

      def uploadToAws(collection: Collection) = if (collection.creatorId == loginState.username) Future(utilities.AmazonS3.uploadFile(objectKey = awsKey, filePath = oldFilePath))
      else {
        utilities.FileOperations.deleteFile(oldFilePath)
        constants.Response.NOT_COLLECTION_OWNER.throwFutureBaseException()
      }

      def deleteLocalFile() = Future(utilities.FileOperations.deleteFile(oldFilePath))

      def add() = masterTransactionNFTDrafts.Service.add(id = nftId, fileExtension = utilities.FileOperations.fileExtensionFromName(name), collectionId = collectionId)

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

  def basicDetailsForm(collectionId: String, nftId: String): Action[AnyContent] = withLoginActionAsync { implicit loginState =>
    implicit request =>
      val isOwner = masterCollections.Service.isOwner(id = collectionId, accountId = loginState.username)

      def optionalNFTDraft(isOwner: Boolean) = if (isOwner) masterTransactionNFTDrafts.Service.get(nftId)
      else constants.Response.NOT_COLLECTION_OWNER.throwFutureBaseException()

      (for {
        isOwner <- isOwner
        optionalNFTDraft <- optionalNFTDraft(isOwner)
      } yield Ok(views.html.nft.nftBasicDetail(collectionId = collectionId, nftId = nftId, optionalNFTDraft = optionalNFTDraft))
        ).recover {
        case baseException: BaseException => BadRequest(baseException.failure.message)
      }
  }

  def basicDetails(): Action[AnyContent] = withLoginActionAsync { implicit loginState =>
    implicit request =>
      NFTBasicDetail.form.bindFromRequest().fold(
        formWithErrors => {
          val nftId = formWithErrors.data.getOrElse(constants.FormField.NFT_ID.name, "")
          val optionalNFTDraft = masterTransactionNFTDrafts.Service.get(nftId)

          (for {
            optionalNFTDraft <- optionalNFTDraft
          } yield BadRequest(views.html.nft.nftBasicDetail(formWithErrors, formWithErrors.data.getOrElse(constants.FormField.COLLECTION_ID.name, ""), nftId, optionalNFTDraft))
            ).recover {
            case baseException: BaseException => BadRequest(baseException.failure.message)
          }

        },
        basicDetailsData => {
          val isOwner = masterCollections.Service.isOwner(id = basicDetailsData.collectionId, accountId = loginState.username)

          def update(isOwner: Boolean) = if (isOwner) masterTransactionNFTDrafts.Service.updateNameDescription(id = basicDetailsData.nftId, name = basicDetailsData.name, description = basicDetailsData.description)
          else constants.Response.NOT_COLLECTION_OWNER.throwFutureBaseException()

          (for {
            isOwner <- isOwner
            nftDraft <- update(isOwner)
          } yield PartialContent(views.html.nft.tags(collectionId = basicDetailsData.collectionId, nftId = nftDraft.id, tags = nftDraft.tagNames.getOrElse(Seq.empty[String])))
            ).recover {
            case baseException: BaseException => BadRequest(views.html.nft.nftBasicDetail(NFTBasicDetail.form.withGlobalError(baseException.failure.message), collectionId = basicDetailsData.collectionId, nftId = basicDetailsData.nftId, None))
          }
        }
      )
  }

  def tagsForm(collectionId: String, nftId: String): Action[AnyContent] = withLoginActionAsync { implicit loginState =>
    implicit request =>
      val isOwner = masterCollections.Service.isOwner(id = collectionId, accountId = loginState.username)
      val nftDraft = masterTransactionNFTDrafts.Service.get(nftId)
      (for {
        isOwner <- isOwner
        nftDraft <- nftDraft
      } yield if (isOwner) Ok(views.html.nft.tags(collectionId = collectionId, nftId = nftId, tags = nftDraft.fold[Seq[String]](Seq())(_.tagNames.getOrElse(Seq.empty[String]))))
      else constants.Response.NOT_COLLECTION_OWNER.throwBaseException()
        ).recover {
        case baseException: BaseException => BadRequest(baseException.failure.message)
      }
  }

  def tags(): Action[AnyContent] = withLoginActionAsync { implicit loginState =>
    implicit request =>
      NFTTags.form.bindFromRequest().fold(
        formWithErrors => {
          Future(BadRequest(views.html.nft.tags(formWithErrors, formWithErrors.data.getOrElse(constants.FormField.COLLECTION_ID.name, ""), formWithErrors.data.getOrElse(constants.FormField.NFT_ID.name, ""), formWithErrors.data.getOrElse(constants.FormField.NFT_TAGS.name, "").split(","))))
        },
        tagsData => {
          val collection = masterCollections.Service.tryGet(id = tagsData.collectionId)

          def update(collection: Collection) = if (collection.creatorId == loginState.username) masterTransactionNFTDrafts.Service.updateTagNames(id = tagsData.nftId, tagNames = tagsData.getTags)
          else constants.Response.NOT_COLLECTION_OWNER.throwFutureBaseException()

          (for {
            collection <- collection
            _ <- update(collection)
          } yield PartialContent(views.html.nft.setProperties(collection = collection, nftId = tagsData.nftId))
            ).recover {
            case baseException: BaseException => BadRequest(views.html.nft.tags(NFTTags.form.withGlobalError(baseException.failure.message), collectionId = tagsData.collectionId, nftId = tagsData.nftId, tags = tagsData.getTags))
          }
        }
      )
  }

  def setPropertiesForm(collectionId: String, nftId: String): Action[AnyContent] = withLoginActionAsync { implicit loginState =>
    implicit request =>
      val collection = masterCollections.Service.tryGet(id = collectionId)
      (for {
        collection <- collection
      } yield if (collection.creatorId == loginState.username) Ok(views.html.nft.setProperties(collection = collection, nftId = nftId))
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
          } yield if (collection.creatorId == loginState.username) BadRequest(views.html.nft.setProperties(formWithErrors, collection, formWithErrors.data.getOrElse(constants.FormField.NFT_ID.name, "")))
          else constants.Response.NOT_COLLECTION_OWNER.throwBaseException()
            ).recover {
            case baseException: BaseException => BadRequest(baseException.failure.message)
          }
        },
        setPropertiesData => {
          val collection = masterCollections.Service.tryGet(id = setPropertiesData.collectionId)

          def update(collection: Collection) = if (collection.creatorId == loginState.username) {
            val updateDraft = masterTransactionNFTDrafts.Service.updateProperties(setPropertiesData.nftId, setPropertiesData.getNFTProperties(collection.properties.getOrElse(Seq())))

            def addToNFT(nftDraft: NFTDraft) = if (!setPropertiesData.saveNFTDraft) {
              val add = masterNFTs.Service.add(nftDraft.toNFT(collection = collection))

              def addProperties() = masterNFTProperties.Service.addMultiple(nftDraft.getNFTProperties)

              def addOwner(nftOwner: master.NFTOwner) = masterNFTOwners.Service.add(nftOwner)

              def addTags() = if (nftDraft.getTags.nonEmpty) masterNFTTags.Service.add(nftDraft.getTags) else Future()

              def deleteDraft() = masterTransactionNFTDrafts.Service.deleteNFT(nftDraft.id)

              for {
                nft <- add
                _ <- addProperties()
                _ <- addOwner(nftDraft.toNFTOwner(ownerID = collection.creatorId, creatorId = collection.creatorId))
                _ <- addTags()
                _ <- deleteDraft()
                _ <- collectionsAnalysis.Utility.onNewNFT(collection.id)
                _ <- utilitiesNotification.send(accountID = loginState.username, notification = constants.Notification.NFT_CREATED, nftDraft.name.getOrElse(""))(s"'${nftDraft.id}'")
              } yield ()
            } else Future()

            for {
              draft <- updateDraft
              _ <- addToNFT(draft)
            } yield draft.toNFT(collection = collection)
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

  def deleteDraftForm(nftId: String, fileHash: String): Action[AnyContent] = withoutLoginAction { implicit request =>
    Ok(views.html.nft.deleteDraft(nftId, fileHash))
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

  def price(nftId: String): EssentialAction = cached(req => utilities.Session.getSessionCachingKey(req, cacheWithUsername = false), constants.CommonConfig.WebAppCacheDuration) {
    withoutLoginActionAsync { implicit optionalLoginState =>
      implicit request =>
        val saleId = masterNFTOwners.Service.getSaleId(nftId)

        def price(saleId: Option[String]) = saleId.fold(Future("--"))(x => masterSales.Service.tryGet(x).map(_.price.toString))

        (for {
          saleId <- saleId
          price <- price(saleId)
        } yield Ok(price)
          ).recover {
          case _: BaseException => BadRequest("--")
        }
    }
  }

  def mintForm(nftId: String): Action[AnyContent] = withoutLoginAction { implicit request =>
    Ok(views.html.nft.mint(nftId = nftId))
  }

  def mint(): Action[AnyContent] = withLoginActionAsync { implicit loginState =>
    implicit request =>
      Mint.form.bindFromRequest().fold(
        formWithErrors => {
          Future(BadRequest(views.html.nft.mint(formWithErrors, formWithErrors.data.getOrElse(constants.FormField.NFT_ID.name, ""))))
        },
        mintData => {
          val nft = masterNFTs.Service.tryGet(mintData.nftId)
          val nftOwner = masterNFTOwners.Service.tryGet(nftId = mintData.nftId, ownerId = loginState.username)
          val verifyPassword = masterKeys.Service.validateActiveKeyUsernamePasswordAndGet(username = loginState.username, password = mintData.password)
          val balance = blockchainBalances.Service.getTokenBalance(loginState.address)

          def collection(collectionId: String) = masterCollections.Service.tryGet(collectionId)

          def verifyAndTx(verifyPassword: Boolean, balance: MicroNumber, key: Key, nft: NFT, nftOwner: NFTOwner, collection: Collection) = {
            val errors = Seq(
              if (balance <= collection.getBondAmount) Option(constants.Response.INSUFFICIENT_BALANCE) else None,
              if (!verifyPassword) Option(constants.Response.INVALID_PASSWORD) else None,
              if (nft.isMinted.getOrElse(true)) Option(constants.Response.NFT_ALREADY_MINTED) else None,
              if (nft.totalSupply != nftOwner.quantity) Option(constants.Response.NFT_TOTAL_SUPPLY_AND_OWNED_DIFFERENT) else None,
            ).flatten
            if (errors.isEmpty) {
              masterTransactionNFTMintingFeeTransactions.Utility.transaction(
                accountId = loginState.username,
                nft = nft,
                fromAddress = loginState.address,
                amount = collection.getBondAmount,
                gasPrice = constants.Blockchain.DefaultGasPrice,
                ecKey = ECKey.fromPrivate(utilities.Secrets.decryptData(key.encryptedPrivateKey, mintData.password))
              )
            } else errors.head.throwBaseException()
          }

          (for {
            nft <- nft
            collection <- collection(nft.collectionId)
            nftOwner <- nftOwner
            (verified, key) <- verifyPassword
            balance <- balance
          } yield Ok
            ).recover {
            case baseException: BaseException => BadRequest(baseException.failure.message)
          }
        }
      )
  }

  def transferForm(nftId: String): Action[AnyContent] = withoutLoginAction { implicit request =>
    Ok(views.html.nft.transfer(nftId = nftId))
  }

  def transfer(): Action[AnyContent] = withLoginActionAsync { implicit loginState =>
    implicit request =>
      Transfer.form.bindFromRequest().fold(
        formWithErrors => {
          Future(BadRequest(views.html.nft.transfer(formWithErrors, formWithErrors.data.getOrElse(constants.FormField.NFT_ID.name, ""))))
        },
        transferData => {
          val quantity = 1 // TODO
          val nft = masterNFTs.Service.tryGet(transferData.nftId)
          val nftOwner = masterNFTOwners.Service.tryGet(nftId = transferData.nftId, ownerId = loginState.username)
          val verifyPassword = masterKeys.Service.validateActiveKeyUsernamePasswordAndGet(username = loginState.username, password = transferData.password)
          val balance = blockchainBalances.Service.getTokenBalance(loginState.address)
          val toAccountExists = masterKeys.Service.checkVerifiedKeyExists(transferData.toAccountId)

          def verifyAndTx(verifyPassword: Boolean, balance: MicroNumber, key: Key, nft: NFT, nftOwner: NFTOwner, toAccountExists: Boolean) = {
            val errors = Seq(
              if (balance == MicroNumber.zero) Option(constants.Response.INSUFFICIENT_BALANCE) else None,
              if (!verifyPassword) Option(constants.Response.INVALID_PASSWORD) else None,
              if (!nft.isMinted.getOrElse(false)) Option(constants.Response.NFT_NOT_MINTED) else None,
              if (quantity > nftOwner.quantity) Option(constants.Response.INSUFFICIENT_NFT_BALANCE) else None,
              if (!toAccountExists) Option(constants.Response.TO_ACCOUNT_ID_DOES_NOT_EXISTS) else None,
            ).flatten

            if (errors.isEmpty) {
              masterTransactionNFTTransferTransactions.Utility.transaction(
                nft = nft,
                ownerId = loginState.username,
                quantity = quantity,
                fromAddress = key.address,
                toAccountId = transferData.toAccountId,
                gasPrice = constants.Blockchain.DefaultGasPrice,
                ecKey = ECKey.fromPrivate(utilities.Secrets.decryptData(key.encryptedPrivateKey, transferData.password))
              )
            } else errors.head.throwBaseException()
          }

          (for {
            nft <- nft
            (verified, key) <- verifyPassword
            balance <- balance
            nftOwner <- nftOwner
            toAccountExists <- toAccountExists
            blockchainTransaction <- verifyAndTx(verifyPassword = verified, balance = balance, key = key, nft = nft, nftOwner = nftOwner, toAccountExists = toAccountExists)
          } yield PartialContent(views.html.transactionSuccessful(blockchainTransaction))
            ).recover {
            case baseException: BaseException => BadRequest(views.html.nft.transfer(Transfer.form.withGlobalError(baseException.failure.message), transferData.nftId))
          }
        }
      )
  }

}