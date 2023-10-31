package controllers

import controllers.actions._
import exceptions.BaseException
import models.analytics.{CollectionAnalysis, CollectionsAnalysis}
import models.master._
import models.{blockchain, master, masterTransaction}
import org.bitcoinj.core.ECKey
import play.api.Logger
import play.api.cache.Cached
import play.api.i18n.I18nSupport
import play.api.mvc._
import utilities.MicroNumber
import views.publicListing.companion._

import javax.inject.{Inject, Singleton}
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}

@Singleton
class PublicListingController @Inject()(
                                         messagesControllerComponents: MessagesControllerComponents,
                                         cached: Cached,
                                         withoutLoginActionAsync: WithoutLoginActionAsync,
                                         withLoginAction: WithLoginAction,
                                         withLoginActionAsync: WithLoginActionAsync,
                                         withoutLoginAction: WithoutLoginAction,
                                         collectionsAnalysis: CollectionsAnalysis,
                                         blockchainBalances: blockchain.Balances,
                                         masterAccounts: master.Accounts,
                                         masterKeys: master.Keys,
                                         masterCollections: master.Collections,
                                         masterNFTs: master.NFTs,
                                         masterPublicListings: master.PublicListings,
                                         masterNFTOwners: master.NFTOwners,
                                         masterNFTProperties: master.NFTProperties,
                                         masterTransactionPublicListingNFTTransactions: masterTransaction.PublicListingNFTTransactions,
                                         utilitiesNotification: utilities.Notification,
                                         utilitiesOperations: utilities.Operations,
                                         masterTransactionTokenPrices: masterTransaction.TokenPrices,
                                       )(implicit executionContext: ExecutionContext) extends AbstractController(messagesControllerComponents) with I18nSupport {

  implicit val logger: Logger = Logger(this.getClass)

  implicit val module: String = constants.Module.PUBLIC_LISTING_CONTROLLER

  implicit val callbackOnSessionTimeout: Call = routes.PublicListingController.viewCollections()

  def viewCollections(): EssentialAction = cached(req => utilities.Session.getSessionCachingKey(req), constants.CommonConfig.WebAppCacheDuration) {
    withoutLoginActionAsync { implicit loginState =>
      implicit request =>
        Future(Ok(views.html.publicListing.viewCollections()))
    }
  }

  def collectionsSection(): EssentialAction = cached(req => utilities.Session.getSessionCachingKey(req), constants.CommonConfig.WebAppCacheDuration) {
    withoutLoginActionAsync { implicit loginState =>
      implicit request =>
        val totalCollections = masterPublicListings.Service.total
        (for {
          totalCollections <- totalCollections
        } yield Ok(views.html.publicListing.collectionsSection(totalCollections))
          ).recover {
          case baseException: BaseException => BadRequest(baseException.failure.message)
        }
    }
  }

  def collectionsPerPage(pageNumber: Int): EssentialAction = cached(req => utilities.Session.getSessionCachingKey(req), constants.CommonConfig.WebAppCacheDuration) {
    withoutLoginActionAsync { implicit loginState =>
      implicit request =>
        val publicListings = if (pageNumber < 1) constants.Response.INVALID_PAGE_NUMBER.throwBaseException()
        else masterPublicListings.Service.getByPageNumber(pageNumber)

        def collections(ids: Seq[String]) = masterCollections.Service.getCollections(ids)

        (for {
          publicListings <- publicListings
          collections <- collections(publicListings.map(_.collectionId))
        } yield Ok(views.html.publicListing.collectionsPerPage(collections))
          ).recover {
          case baseException: BaseException => InternalServerError(baseException.failure.message)
        }
    }
  }

  def viewCollection(id: String): EssentialAction = cached(req => utilities.Session.getSessionCachingKey(req), constants.CommonConfig.WebAppCacheDuration) {
    withoutLoginActionAsync { implicit loginState =>
      implicit request =>
        val collection = masterCollections.Service.tryGet(id)
        (for {
          collection <- collection
        } yield Ok(views.html.publicListing.viewCollection(collection))
          ).recover {
          case baseException: BaseException => InternalServerError(baseException.failure.message)
        }
    }
  }

  def collectionNFTs(id: String): EssentialAction = cached(req => utilities.Session.getSessionCachingKey(req), constants.CommonConfig.WebAppCacheDuration) {
    withoutLoginActionAsync { implicit loginState =>
      implicit request =>
        val collection = masterCollections.Service.tryGet(id)
        val nfts = masterNFTs.Service.getRandomNFTs(id, constants.CommonConfig.Pagination.NFTsPerPage, Seq())

        (for {
          collection <- collection
          nfts <- nfts
        } yield Ok(views.html.publicListing.collection.collectionNFTs(collection, nfts))
          ).recover {
          case baseException: BaseException => InternalServerError(baseException.failure.message)
        }
    }
  }

  def collectionTopRightCard(id: String): EssentialAction = cached(req => utilities.Session.getSessionCachingKey(req), constants.CommonConfig.WebAppCacheDuration) {
    withoutLoginActionAsync { implicit optionalLoginState =>
      implicit request =>
        val collectionAnalysis = collectionsAnalysis.Service.tryGet(id)
        val collection = masterCollections.Service.tryGet(id)
        val publicListing = masterPublicListings.Service.tryGetPublicListingByCollectionId(id)

        def getTotalPublicListingSold(publicListingId: String): Future[Long] = masterTransactionPublicListingNFTTransactions.Service.getTotalPublicListingSold(publicListingId).map(_.toLong)

        (for {
          collectionAnalysis <- collectionAnalysis
          collection <- collection
          publicListing <- publicListing
          totalPublicListingSold <- getTotalPublicListingSold(publicListing.id)
        } yield Ok(views.html.publicListing.collection.topRightCard(collectionAnalysis, collection, publicListing, totalPublicListingSold, masterTransactionTokenPrices.Service.getLatestPrice))
          ).recover {
          case baseException: BaseException => InternalServerError(baseException.failure.message)
        }
    }
  }

  def createPublicListingForm(collectionId: Option[String]): Action[AnyContent] = withLoginActionAsync { implicit loginState =>
    implicit request =>
      val collections = masterCollections.Service.getByCreator(loginState.username)

      for {
        collections <- collections
      } yield if (collectionId.isDefined && collections.exists(_._1 == collectionId.get)) Ok(views.html.publicListing.createPublicListing(collections = collections.map(x => x._1 -> x._2).toMap, collectionId = collectionId))
      else BadRequest(constants.Response.COLLECTION_ID_OR_WHITELIST_ID_DOES_NOT_EXIST.message)
  }

  def createPublicListing(): Action[AnyContent] = withLoginActionAsync { implicit loginState =>
    implicit request =>
      CreatePublicListing.form.bindFromRequest().fold(
        formWithErrors => {
          val collections = masterCollections.Service.getByCreator(loginState.username)

          for {
            collections <- collections
          } yield BadRequest(views.html.publicListing.createPublicListing(formWithErrors, collectionId = formWithErrors.data.get(constants.FormField.SELECT_COLLECTION_ID.name), collections = collections.map(x => x._1 -> x._2).toMap))
        },
        createData => {
          val collection = masterCollections.Service.tryGet(id = createData.collectionId)
          val collectionAnalysis = collectionsAnalysis.Service.tryGet(createData.collectionId)
          val unmintedNFTs = masterNFTs.Service.getUnmintedNFTIDs(createData.collectionId)

          def countNFts(unmintedNFTs: Seq[String]) = masterNFTOwners.Service.countForCreatorForPrimarySale(collectionId = createData.collectionId, creatorId = loginState.username, unmintedNFTs = unmintedNFTs)

          def addToPublicListing(countNFts: Int, collection: Collection, collectionAnalysis: CollectionAnalysis, unmintedNFTs: Seq[String]) = {
            val maxSellNumber: Int = if (collectionAnalysis.totalNFTs <= 50) collectionAnalysis.totalNFTs.toInt else collectionAnalysis.totalNFTs.toInt / 10
            val errors = Seq(
              if (!loginState.isVerifiedCreator) Option(constants.Response.NOT_VERIFIED_CREATOR) else None,
              if (collection.creatorId != loginState.username) Option(constants.Response.NOT_COLLECTION_OWNER) else None,
              if (collection.isFractionalized) Option(constants.Response.FRACTIONALIZED_COLLECTION_NOT_ALLOWED) else None,
              if (!collection.public) Option(constants.Response.COLLECTION_NOT_PUBLIC) else None,
              if (createData.numberOfNFTs > maxSellNumber.min(countNFts)) Option(constants.Response.CANNOT_SELL_MORE_THAN_ALLOWED_LIMIT) else None,
              if (createData.numberOfNFTs > countNFts) Option(constants.Response.NOT_ENOUGH_NFTS_IN_COLLECTION) else None,
            ).flatten
            if (errors.isEmpty) {
              for {
                publicListingId <- masterPublicListings.Service.add(createData.toNewPublicListing)
                _ <- masterNFTOwners.Service.publicListRandomNFTs(collectionId = collection.id, nfts = createData.numberOfNFTs, creatorId = loginState.username, publicListingId = publicListingId, unmintedNFTs = unmintedNFTs)
              } yield ()
            } else errors.head.throwBaseException()
          }

          def sendNotification(collectionName: String) = utilitiesNotification.send(loginState.username, notification = constants.Notification.PUBLIC_LISTING_ON_COLLECTION, collectionName)(s"'${createData.collectionId}'")

          (for {
            collection <- collection
            collectionAnalysis <- collectionAnalysis
            unmintedNFTs <- unmintedNFTs
            countNFts <- countNFts(unmintedNFTs)
            _ <- addToPublicListing(countNFts = countNFts, collection = collection, collectionAnalysis = collectionAnalysis, unmintedNFTs = unmintedNFTs)
            _ <- sendNotification(collection.name)
            _ <- collectionsAnalysis.Utility.onCreatePublicListing(collection.id, listingPrice = createData.price)
          } yield PartialContent(views.html.publicListing.createSuccessful())
            ).recover {
            case baseException: BaseException => {
              try {
                val collections = masterCollections.Service.getByCreator(loginState.username)

                val result = for {
                  collections <- collections
                } yield BadRequest(views.html.publicListing.createPublicListing(CreatePublicListing.form.withGlobalError(baseException.failure.message), collectionId = Option(createData.collectionId), collections = collections.map(x => x._1 -> x._2).toMap))
                Await.result(result, Duration.Inf)
              } catch {
                case baseException: BaseException => BadRequest(views.html.publicListing.createPublicListing(CreatePublicListing.form.withGlobalError(baseException.failure.message), collections = Map(), collectionId = Option(createData.collectionId)))
              }
            }
          }
        }
      )
  }

  def stopForm(publicListingId: String): Action[AnyContent] = withLoginActionAsync { implicit loginState =>
    implicit request =>
      val publicListing = masterPublicListings.Service.tryGet(publicListingId)

      (for {
        publicListing <- publicListing
      } yield Ok(views.html.publicListing.stop(publicListing = publicListing))
        ).recover {
        case baseException: BaseException => BadRequest(baseException.failure.message)
      }
  }

  def stop(): Action[AnyContent] = withLoginActionAsync { implicit loginState =>
    implicit request =>
      StopPublicListing.form.bindFromRequest().fold(
        formWithErrors => {
          val publicListing = masterPublicListings.Service.tryGet(formWithErrors.data.getOrElse(constants.FormField.PUBLIC_LISTING_ID.name, ""))

          for {
            publicListing <- publicListing
          } yield BadRequest(views.html.publicListing.stop(formWithErrors, publicListing))
        },
        stopData => {
          val publicListing = masterPublicListings.Service.tryGet(stopData.publicListingId)

          def collection(id: String) = masterCollections.Service.tryGet(id = id)

          def stopPublicListing(collection: Collection, publicListing: PublicListing) = {
            val errors = Seq(
              if (collection.creatorId != loginState.username) Option(constants.Response.NOT_COLLECTION_OWNER) else None,
              if (!collection.public) Option(constants.Response.COLLECTION_NOT_PUBLIC) else None,
              if (utilities.Date.currentEpoch >= publicListing.endTimeEpoch) Option(constants.Response.SALE_EXPIRED) else None,
            ).flatten
            if (errors.isEmpty) {
              for {
                _ <- masterPublicListings.Service.markStop(stopData.publicListingId)
              } yield ()
            } else errors.head.throwBaseException()
          }

          (for {
            publicListing <- publicListing
            collection <- collection(publicListing.collectionId)
            _ <- stopPublicListing(collection, publicListing)
          } yield PartialContent(views.html.publicListing.stopSuccessful())
            ).recover {
            case baseException: BaseException => {
              try {
                val publicListing = masterPublicListings.Service.tryGet(stopData.publicListingId)

                val result = for {
                  publicListing <- publicListing
                } yield BadRequest(views.html.publicListing.stop(StopPublicListing.form.withGlobalError(baseException.failure.message), publicListing))
                Await.result(result, Duration.Inf)
              } catch {
                case baseException: BaseException => BadRequest(baseException.failure.message)
              }
            }
          }
        }
      )
  }

  def buyNFTForm(publicListingId: String): EssentialAction = cached(req => utilities.Session.getSessionCachingKey(req), constants.CommonConfig.WebAppCacheDuration) {
    withoutLoginActionAsync { implicit loginState =>
      implicit request =>
        val publicListing = masterPublicListings.Service.tryGet(publicListingId)

        def collection(id: String) = masterCollections.Service.tryGet(id)

        (for {
          publicListing <- publicListing
          collection <- collection(publicListing.collectionId)
        } yield Ok(views.html.publicListing.buyNFT(publicListing = publicListing, collection = collection))
          ).recover {
          case _: BaseException => BadRequest
        }
    }
  }

  def buyNFT(): Action[AnyContent] = withLoginActionAsync { implicit loginState =>
    implicit request =>
      BuyNFT.form.bindFromRequest().fold(
        formWithErrors => {
          val publicListing = masterPublicListings.Service.tryGet(formWithErrors.data.getOrElse(constants.FormField.PUBLIC_LISTING_ID.name, ""))

          def collection(id: String) = masterCollections.Service.tryGet(id)

          (for {
            publicListing <- publicListing
            collection <- collection(publicListing.collectionId)
          } yield BadRequest(views.html.publicListing.buyNFT(formWithErrors, publicListing, collection))
            ).recover {
            case _: BaseException => BadRequest
          }
        },
        buyNFTData => {
          val publicListing = masterPublicListings.Service.tryGet(buyNFTData.publicListingId)
          val verifyPassword = masterKeys.Service.validateActiveKeyUsernamePasswordAndGet(username = loginState.username, password = buyNFTData.password)
          val balance = blockchainBalances.Service.getTokenBalance(loginState.address)
          val countBuyerNFTsFromPublicListing = masterTransactionPublicListingNFTTransactions.Service.countBuyerNFTsFromPublicListing(buyerAccountId = loginState.username, publicListingId = buyNFTData.publicListingId)

          def collection(id: String) = masterCollections.Service.tryGet(id)

          def nftOwners(collection: Collection) = masterNFTOwners.Service.getRandomNFTsByPublicListingId(publicListingId = buyNFTData.publicListingId, take = buyNFTData.buyNFTs, creatorId = collection.creatorId)

          def checkAlreadySold(nftIds: Seq[String]) = masterTransactionPublicListingNFTTransactions.Service.checkAlreadySold(publicListingId = buyNFTData.publicListingId, nftIds = nftIds)

          def nfts(nftIds: Seq[String]) = masterNFTs.Service.getByIds(nftIds)

          def sellerKey(ownerId: String) = masterKeys.Service.tryGetActive(ownerId)

          def validateAndTransfer(nftOwners: Seq[NFTOwner], verifyPassword: Boolean, publicListing: PublicListing, buyerKey: Key, collection: Collection, sellerKey: Key, balance: MicroNumber, nfts: Seq[NFT], countBuyerNFTsFromPublicListing: Int, checkAlreadySold: Boolean) = {
            val errors = Seq(
              if (nftOwners.map(_.ownerId).distinct.contains(loginState.username)) Option(constants.Response.CANNOT_SELL_TO_YOURSELF) else None,
              if (publicListing.startTimeEpoch > utilities.Date.currentEpoch) Option(constants.Response.EARLY_ACCESS_NOT_STARTED) else None,
              if (utilities.Date.currentEpoch >= publicListing.endTimeEpoch) Option(constants.Response.EARLY_ACCESS_ENDED) else None,
              if (balance == MicroNumber.zero || balance <= (publicListing.price * buyNFTData.buyNFTs)) Option(constants.Response.INSUFFICIENT_BALANCE) else None,
              if (buyNFTData.mintOnSuccess && (balance - nfts.map(_.getBondAmount(collection)).sum <= MicroNumber.zero)) Option(constants.Response.INSUFFICIENT_BALANCE) else None,
              if ((countBuyerNFTsFromPublicListing + buyNFTData.buyNFTs) > publicListing.maxMintPerAccount) Option(constants.Response.MAXIMUM_NFT_MINT_PER_ACCOUNT_REACHED) else None,
              if (buyNFTData.mintOnSuccess && nfts.map(_.isMinted.getOrElse(true)).exists(identity)) Option(constants.Response.NFT_ALREADY_MINTED) else None,
              if (nfts.exists(_.isMinted.getOrElse(true))) Option(constants.Response.NFT_ALREADY_MINTED) else None,
              if (!verifyPassword) Option(constants.Response.INVALID_PASSWORD) else None,
              if (checkAlreadySold) Option(constants.Response.NFT_ALREADY_SOLD) else None,
              if (publicListing.isOver) Option(constants.Response.SALE_STOPPED_OR_OVER) else None,
            ).flatten
            if (errors.isEmpty) {
              masterTransactionPublicListingNFTTransactions.Utility.transaction(
                buyerAccountId = loginState.username,
                sellerAccountId = sellerKey.accountId,
                publicListingId = publicListing.id,
                mintOnSuccess = buyNFTData.mintOnSuccess,
                nfts = nfts,
                fromAddress = buyerKey.address,
                collection = collection,
                toAddress = sellerKey.address,
                amount = publicListing.price * buyNFTData.buyNFTs,
                gasPrice = constants.Transaction.DefaultGasPrice,
                ecKey = ECKey.fromPrivate(utilities.Secrets.decryptData(buyerKey.encryptedPrivateKey, buyNFTData.password))
              )
            } else errors.head.throwBaseException()
          }

          (for {
            publicListing <- publicListing
            collection <- collection(publicListing.collectionId)
            nftOwners <- nftOwners(collection)
            nfts <- nfts(nftOwners.map(_.nftId))
            sellerKey <- sellerKey(collection.creatorId)
            (verifyPassword, buyerKey) <- verifyPassword
            balance <- balance
            countBuyerNFTsFromPublicListing <- countBuyerNFTsFromPublicListing
            checkAlreadySold <- checkAlreadySold(nftOwners.map(_.nftId))
            blockchainTransaction <- validateAndTransfer(nftOwners = nftOwners, verifyPassword = verifyPassword, publicListing = publicListing, buyerKey = buyerKey, collection = collection, sellerKey = sellerKey, balance = balance, nfts = nfts, countBuyerNFTsFromPublicListing = countBuyerNFTsFromPublicListing, checkAlreadySold = checkAlreadySold)
          } yield {
            val tweetURI = if (collection.getTwitterUsername.isDefined) Option(s"https://twitter.com/intent/tweet?text=Just bought ${collection.name} NFTs via Early Access Sale on MantlePlace. @${collection.getTwitterUsername.get} @AssetMantle Check here &url=https://marketplace.assetmantle.one/collection/${collection.id}&hashtags=NFT") else None
            PartialContent(views.html.transactionSuccessful(blockchainTransaction, tweetURI, nfts))
          }
            ).recover {
            case baseException: BaseException => {
              val badResult = {
                val publicListing = masterPublicListings.Service.tryGet(buyNFTData.publicListingId)

                def collection(id: String) = masterCollections.Service.tryGet(id)

                (for {
                  publicListing <- publicListing
                  collection <- collection(publicListing.collectionId)
                } yield BadRequest(views.html.publicListing.buyNFT(BuyNFT.form.withGlobalError(baseException.failure.message), publicListing, collection))
                  ).recover {
                  case _: BaseException => BadRequest
                }
              }
              Await.result(badResult, Duration.Inf)
            }
          }
        }
      )
  }

}
