package controllers

import controllers.actions._
import exceptions.BaseException
import models.analytics.CollectionsAnalysis
import models.blockchain.Split
import models.master._
import models.masterTransaction.MakeOrderTransaction
import models.{blockchain, master, masterTransaction}
import org.bitcoinj.core.ECKey
import play.api.Logger
import play.api.cache.Cached
import play.api.i18n.I18nSupport
import play.api.mvc._
import schema.id.base.CoinID
import utilities.MicroNumber
import views.secondaryMarket.companion.{Buy, Cancel, CreateSecondaryMarket}

import javax.inject.{Inject, Singleton}
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}

@Singleton
class SecondaryMarketController @Inject()(
                                           messagesControllerComponents: MessagesControllerComponents,
                                           cached: Cached,
                                           withoutLoginActionAsync: WithoutLoginActionAsync,
                                           withLoginAction: WithLoginAction,
                                           withLoginActionAsync: WithLoginActionAsync,
                                           withoutLoginAction: WithoutLoginAction,
                                           collectionsAnalysis: CollectionsAnalysis,
                                           blockchainBalances: blockchain.Balances,
                                           blockchainSplits: blockchain.Splits,
                                           masterAccounts: master.Accounts,
                                           masterKeys: master.Keys,
                                           masterWishLists: master.WishLists,
                                           masterCollections: master.Collections,
                                           masterNFTs: master.NFTs,
                                           masterSecondaryMarkets: master.SecondaryMarkets,
                                           masterNFTOwners: master.NFTOwners,
                                           masterNFTProperties: master.NFTProperties,
                                           masterTransactionTokenPrices: masterTransaction.TokenPrices,
                                           masterTransactionMakeOrderTransactions: masterTransaction.MakeOrderTransactions,
                                           masterTransactionTakeOrderTransactions: masterTransaction.TakeOrderTransactions,
                                           masterTransactionCancelOrderTransactions: masterTransaction.CancelOrderTransactions,
                                           utilitiesNotification: utilities.Notification,
                                           utilitiesOperations: utilities.Operations,
                                         )(implicit executionContext: ExecutionContext) extends AbstractController(messagesControllerComponents) with I18nSupport {

  implicit val logger: Logger = Logger(this.getClass)

  implicit val module: String = constants.Module.SECONDARY_MARKET_CONTROLLER

  implicit val callbackOnSessionTimeout: Call = routes.SecondaryMarketController.viewCollections()

  def viewCollections(): EssentialAction = cached(req => utilities.Session.getSessionCachingKey(req), constants.CommonConfig.WebAppCacheDuration) {
    withoutLoginActionAsync { implicit loginState =>
      implicit request =>
        Future(Ok(views.html.secondaryMarket.viewCollections()))
    }
  }

  def collectionsSection(): EssentialAction = cached(req => utilities.Session.getSessionCachingKey(req), constants.CommonConfig.WebAppCacheDuration) {
    withoutLoginActionAsync { implicit loginState =>
      implicit request =>
        val totalCollections = masterCollections.Service.totalOnSecondaryMarket
        (for {
          totalCollections <- totalCollections
        } yield Ok(views.html.secondaryMarket.collectionsSection(totalCollections))
          ).recover {
          case baseException: BaseException => BadRequest(baseException.failure.message)
        }
    }
  }

  def collectionsPerPage(pageNumber: Int): EssentialAction = cached(req => utilities.Session.getSessionCachingKey(req), constants.CommonConfig.WebAppCacheDuration) {
    withoutLoginActionAsync { implicit loginState =>
      implicit request =>
        val secondaryMarketCollections = if (pageNumber < 1) constants.Response.INVALID_PAGE_NUMBER.throwBaseException()
        else masterCollections.Service.getSecondaryMarketByPageNumber(pageNumber)

        def collections(ids: Seq[String]) = masterCollections.Service.getCollections(ids)

        (for {
          secondaryMarketCollections <- secondaryMarketCollections
        } yield Ok(views.html.secondaryMarket.collectionsPerPage(secondaryMarketCollections))
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
        } yield Ok(views.html.secondaryMarket.viewCollection(collection))
          ).recover {
          case baseException: BaseException => InternalServerError(baseException.failure.message)
        }
    }
  }

  def collectionNFTs(id: String): EssentialAction = cached(req => utilities.Session.getSessionCachingKey(req), constants.CommonConfig.WebAppCacheDuration) {
    withoutLoginActionAsync { implicit loginState =>
      implicit request =>
        val collection = masterCollections.Service.tryGet(id)

        (for {
          collection <- collection
        } yield Ok(views.html.secondaryMarket.collection.collectionNFTs(collection))
          ).recover {
          case baseException: BaseException => InternalServerError(baseException.failure.message)
        }
    }
  }

  def collectionNFTsPerPage(id: String, pageNumber: Int): EssentialAction = cached(req => utilities.Session.getSessionCachingKey(req), constants.CommonConfig.WebAppCacheDuration) {
    withoutLoginActionAsync { implicit optionalLoginState =>
      implicit request =>
        val collection = if (pageNumber < 1) constants.Response.INVALID_PAGE_NUMBER.throwBaseException()
        else masterCollections.Service.tryGet(id)
        val likedNFTs = optionalLoginState.fold[Future[Seq[String]]](Future(Seq()))(x => masterWishLists.Service.getByCollection(accountId = x.username, collectionId = id))
        val secondaryMarkets = masterSecondaryMarkets.Service.getByCollectionId(id, pageNumber)

        def nfts(nftIds: Seq[String]) = masterNFTs.Service.getByIds(nftIds)

        (for {
          collection <- collection
          secondaryMarkets <- secondaryMarkets
          nfts <- nfts(secondaryMarkets.map(_.nftId))
          likedNFTs <- likedNFTs
        } yield Ok(views.html.secondaryMarket.collection.nftsPerPage(collection, nfts, likedNFTs, pageNumber))
          ).recover {
          case baseException: BaseException => InternalServerError(baseException.failure.message)
        }
    }
  }

  def collectionTopRightCard(id: String): EssentialAction = cached(req => utilities.Session.getSessionCachingKey(req), constants.CommonConfig.WebAppCacheDuration) {
    withoutLoginActionAsync { implicit optionalLoginState =>
      implicit request =>
        val collectionAnalysis = collectionsAnalysis.Service.tryGet(id)
        (for {
          collectionAnalysis <- collectionAnalysis
        } yield Ok(views.html.secondaryMarket.collection.topRightCard(collectionAnalysis))
          ).recover {
          case baseException: BaseException => InternalServerError(baseException.failure.message)
        }
    }
  }

  def createForm(nftId: String): Action[AnyContent] = withLoginActionAsync { implicit loginState =>
    implicit request =>
      val nftOwner = masterNFTOwners.Service.tryGet(nftId = nftId, ownerId = loginState.username)
      (for {
        nftOwner <- nftOwner
      } yield Ok(views.html.secondaryMarket.createSecondaryMarket(nftId = nftId, totalOwned = nftOwner.quantity))
        ).recover {
        case baseException: BaseException => BadRequest(baseException.failure.message)
      }
  }

  def create(): Action[AnyContent] = withLoginActionAsync { implicit loginState =>
    implicit request =>
      CreateSecondaryMarket.form.bindFromRequest().fold(
        formWithErrors => {
          val nftID = formWithErrors.data.getOrElse(constants.FormField.NFT_ID.name, "")
          val nftOwner = masterNFTOwners.Service.tryGet(nftId = nftID, ownerId = loginState.username)
          (for {
            nftOwner <- nftOwner
          } yield BadRequest(views.html.secondaryMarket.createSecondaryMarket(formWithErrors, nftID, totalOwned = nftOwner.quantity))
            ).recover {
            case baseException: BaseException => BadRequest(baseException.failure.message)
          }
        },
        createData => {
          val nft = masterNFTs.Service.tryGet(createData.nftId)
          val nftOwner = masterNFTOwners.Service.tryGet(nftId = createData.nftId, ownerId = loginState.username)
          val activeKey = masterKeys.Service.tryGetActive(loginState.username)

          def collection(id: String) = masterCollections.Service.tryGet(id)

          def addToSecondaryMarket(nft: NFT, nftOwner: NFTOwner, collection: Collection, activeKey: Key) = {
            val errors = Seq(
              if (!nft.isMinted.getOrElse(false)) Option(constants.Response.NFT_NOT_MINTED) else None,
              if (nftOwner.creatorId == "Mint.E") Option(constants.Response.SELLING_MINT_E_COLLECTIONS_NOT_ALLOWED) else None,
              if (nftOwner.ownerId != loginState.username) Option(constants.Response.NOT_NFT_OWNER) else None,
              if (createData.sellQuantity > nftOwner.quantity) Option(constants.Response.NOT_ENOUGH_QUANTITY) else None,
              if (nftOwner.saleId.isDefined || nftOwner.publicListingId.isDefined) Option(constants.Response.NFT_ALREADY_ON_SALE) else None,
              if (!collection.public) Option(constants.Response.COLLECTION_NOT_PUBLIC) else None,
              if (!loginState.isVerifiedCreator) Option(constants.Response.NOT_VERIFIED_CREATOR) else None,
            ).flatten
            if (errors.isEmpty) {
              for {
                secondaryMarketId <- masterSecondaryMarkets.Service.add(createData.toNewSecondaryMarket(collectionId = nftOwner.collectionId, sellerId = loginState.username))
                tx <- masterTransactionMakeOrderTransactions.Utility.transaction(nftID = createData.nftId, nftOwner = nftOwner, fromAddress = loginState.address, quantity = createData.sellQuantity, endHours = createData.endHours, price = createData.price, buyerId = None, secondaryMarketId = secondaryMarketId, gasPrice = constants.Transaction.DefaultGasPrice, ecKey = activeKey.getECKey(createData.password).get)
              } yield tx
            } else errors.head.throwBaseException()
          }

          (for {
            nft <- nft
            nftOwner <- nftOwner
            activeKey <- activeKey
            collection <- collection(nftOwner.collectionId)
            tx <- addToSecondaryMarket(nft = nft, nftOwner = nftOwner, collection = collection, activeKey = activeKey)
          } yield PartialContent(views.html.transactionSuccessful(tx))
            ).recover {
            case baseException: BaseException => try {
              val NFTOwner = Await.result(nftOwner, Duration.Inf)
              BadRequest(views.html.secondaryMarket.createSecondaryMarket(CreateSecondaryMarket.form.withGlobalError(baseException.failure.message), createData.nftId, NFTOwner.quantity))
            } catch {
              case baseException: BaseException => BadRequest(baseException.failure.message)
            }
          }
        }
      )
  }

  def cancelForm(nftId: String, secondaryMarketId: String): Action[AnyContent] = withLoginActionAsync { implicit loginState =>
    implicit request =>
      Future(Ok(views.html.secondaryMarket.cancel(nftId = nftId, secondaryMarketId = secondaryMarketId)))
  }

  def cancel(): Action[AnyContent] = withLoginActionAsync { implicit loginState =>
    implicit request =>
      Cancel.form.bindFromRequest().fold(
        formWithErrors => {
          Future(BadRequest(views.html.secondaryMarket.cancel(formWithErrors, nftId = formWithErrors.data.getOrElse(constants.FormField.NFT_ID.name, ""), secondaryMarketId = formWithErrors.data.getOrElse(constants.FormField.SECONDARY_MARKET_ID.name, ""))))
        },
        cancelData => {
          val secondaryMarket = masterSecondaryMarkets.Service.tryGet(cancelData.secondaryMarketId)
          val makeOrderTx = masterTransactionMakeOrderTransactions.Service.tryGetByNFTAndSecondaryMarket(nftId = cancelData.nftId, secondaryMarketId = cancelData.secondaryMarketId)
          val verifyPassword = masterKeys.Service.validateActiveKeyUsernamePasswordAndGet(username = loginState.username, password = cancelData.password)

          def checkAlreadySold(secondaryMarket: SecondaryMarket) = masterTransactionTakeOrderTransactions.Utility.checkAlreadySold(nftId = cancelData.nftId, secondaryMarket = secondaryMarket)

          def validateAndTransfer(makeOrderTx: MakeOrderTransaction, verifyPassword: Boolean, secondaryMarket: SecondaryMarket, buyerKey: Key, checkAlreadySold: Boolean) = {
            val errors = Seq(
              if (secondaryMarket.sellerId != loginState.username) Option(constants.Response.NOT_NFT_OWNER) else None,
              if (!verifyPassword) Option(constants.Response.INVALID_PASSWORD) else None,
              if (checkAlreadySold) Option(constants.Response.NFT_ALREADY_SOLD) else None,
              if (secondaryMarket.orderId.isEmpty || !makeOrderTx.status.getOrElse(false)) Option(constants.Response.ORDER_NOT_CREATED_ON_BLOCKCHAIN) else None,
              if (makeOrderTx.secondaryMarketId != secondaryMarket.id || makeOrderTx.nftId != cancelData.nftId || makeOrderTx.sellerId != secondaryMarket.sellerId || secondaryMarket.sellerId != loginState.username) Option(constants.Response.INVALID_ORDER) else None,
            ).flatten
            if (errors.isEmpty) {
              masterTransactionCancelOrderTransactions.Utility.transaction(
                secondaryMarket = secondaryMarket,
                fromAddress = buyerKey.address,
                gasPrice = constants.Transaction.DefaultGasPrice,
                ecKey = ECKey.fromPrivate(utilities.Secrets.decryptData(buyerKey.encryptedPrivateKey, cancelData.password))
              )
            } else errors.head.throwBaseException()
          }

          (for {
            secondaryMarket <- secondaryMarket
            makeOrderTx <- makeOrderTx
            (verifyPassword, buyerKey) <- verifyPassword
            checkAlreadySold <- checkAlreadySold(secondaryMarket)
            blockchainTransaction <- validateAndTransfer(makeOrderTx = makeOrderTx, verifyPassword = verifyPassword, secondaryMarket = secondaryMarket, buyerKey = buyerKey, checkAlreadySold = checkAlreadySold)
          } yield PartialContent(views.html.transactionSuccessful(blockchainTransaction))
            ).recover {
            case baseException: BaseException => BadRequest(views.html.secondaryMarket.cancel(Cancel.form.withGlobalError(baseException.failure.message), nftId = cancelData.nftId, secondaryMarketId = cancelData.secondaryMarketId))
          }
        }
      )
  }

  def buyForm(secondaryMarketId: String): Action[AnyContent] = withLoginActionAsync { implicit loginState =>
    implicit request =>
      val secondaryMarket = masterSecondaryMarkets.Service.tryGet(secondaryMarketId)

      def collection(id: String) = masterCollections.Service.tryGet(id)

      (for {
        secondaryMarket <- secondaryMarket
        collection <- collection(secondaryMarket.collectionId)
      } yield Ok(views.html.secondaryMarket.buy(secondaryMarket = secondaryMarket, collection = collection))
        ).recover {
        case baseException: BaseException => BadRequest(baseException.failure.message)
      }
  }

  def buy(): Action[AnyContent] = withLoginActionAsync { implicit loginState =>
    implicit request =>
      Buy.form.bindFromRequest().fold(
        formWithErrors => {
          val secondaryMarket = masterSecondaryMarkets.Service.tryGet(formWithErrors.data.getOrElse(constants.FormField.SECONDARY_MARKET_ID.name, ""))

          def collection(id: String) = masterCollections.Service.tryGet(id)

          (for {
            secondaryMarket <- secondaryMarket
            collection <- collection(secondaryMarket.collectionId)
          } yield BadRequest(views.html.secondaryMarket.buy(formWithErrors, secondaryMarket = secondaryMarket, collection = collection))
            ).recover {
            case _: BaseException => BadRequest
          }
        },
        buyData => {
          val secondaryMarket = masterSecondaryMarkets.Service.tryGet(buyData.secondaryMarketId)
          val makeOrderTx = masterTransactionMakeOrderTransactions.Service.tryGetByNFTAndSecondaryMarket(nftId = buyData.nftId, secondaryMarketId = buyData.secondaryMarketId)
          val verifyPassword = masterKeys.Service.validateActiveKeyUsernamePasswordAndGet(username = loginState.username, password = buyData.password)
          val balance = blockchainBalances.Service.getTokenBalance(loginState.address)
          val split = blockchainSplits.Service.getByOwnerIDAndOwnableID(ownerId = utilities.Identity.getMantlePlaceIdentityID(loginState.username), ownableID = CoinID(constants.Blockchain.StakingToken))

          def collection(secondaryMarket: SecondaryMarket) = masterCollections.Service.tryGet(secondaryMarket.collectionId)

          def creatorAddress(accountId: String) = masterKeys.Service.tryGetActive(accountId).map(_.address)

          def checkAlreadySold(secondaryMarket: SecondaryMarket) = masterTransactionTakeOrderTransactions.Utility.checkAlreadySold(nftId = buyData.nftId, secondaryMarket = secondaryMarket)

          def validateAndTransfer(makeOrderTx: MakeOrderTransaction, verifyPassword: Boolean, secondaryMarket: SecondaryMarket, buyerKey: Key, balance: MicroNumber, split: Option[Split], collection: Collection, checkAlreadySold: Boolean, creatorAddress: String) = {
            val royaltyFees = MicroNumber((secondaryMarket.price * buyData.buyQuantity).toBigDecimal * collection.royalty)
            val errors = Seq(
              if (secondaryMarket.sellerId == loginState.username) Option(constants.Response.CANNOT_SELL_TO_YOURSELF) else None,
              if (balance == MicroNumber.zero || balance <= (secondaryMarket.price * buyData.buyQuantity - split.fold(MicroNumber.zero)(_.getBalanceAsMicroNumber) + royaltyFees)) Option(constants.Response.INSUFFICIENT_BALANCE) else None,
              if (!verifyPassword) Option(constants.Response.INVALID_PASSWORD) else None,
              if (checkAlreadySold) Option(constants.Response.NFT_ALREADY_SOLD) else None,
              if (secondaryMarket.orderId.isEmpty || !makeOrderTx.status.getOrElse(false)) Option(constants.Response.ORDER_NOT_CREATED_ON_BLOCKCHAIN) else None,
              if (makeOrderTx.secondaryMarketId != secondaryMarket.id || makeOrderTx.nftId != buyData.nftId || makeOrderTx.sellerId != secondaryMarket.sellerId) Option(constants.Response.INVALID_ORDER) else None,
            ).flatten
            if (errors.isEmpty) {
              masterTransactionTakeOrderTransactions.Utility.transaction(
                nftID = buyData.nftId,
                quantity = buyData.buyQuantity,
                buyerId = loginState.username,
                fromAddress = buyerKey.address,
                secondaryMarket = secondaryMarket,
                gasPrice = constants.Transaction.DefaultGasPrice,
                ecKey = ECKey.fromPrivate(utilities.Secrets.decryptData(buyerKey.encryptedPrivateKey, buyData.password)),
                split = split,
                royaltyFees = royaltyFees,
                creatorAddress = creatorAddress
              )
            } else errors.head.throwBaseException()
          }

          (for {
            secondaryMarket <- secondaryMarket
            makeOrderTx <- makeOrderTx
            (verifyPassword, buyerKey) <- verifyPassword
            balance <- balance
            split <- split
            collection <- collection(secondaryMarket)
            creatorAddress <- creatorAddress(collection.creatorId)
            checkAlreadySold <- checkAlreadySold(secondaryMarket)
            blockchainTransaction <- validateAndTransfer(makeOrderTx = makeOrderTx, verifyPassword = verifyPassword, secondaryMarket = secondaryMarket, buyerKey = buyerKey, balance = balance, split = split, collection = collection, checkAlreadySold = checkAlreadySold, creatorAddress = creatorAddress)
          } yield PartialContent(views.html.transactionSuccessful(blockchainTransaction))
            ).recover {
            case baseException: BaseException => {
              val badResult = {
                val secondaryMarket = masterSecondaryMarkets.Service.tryGet(buyData.secondaryMarketId)

                def collection(id: String) = masterCollections.Service.tryGet(id)

                (for {
                  secondaryMarket <- secondaryMarket
                  collection <- collection(secondaryMarket.collectionId)
                } yield BadRequest(views.html.secondaryMarket.buy(Buy.form.withGlobalError(baseException.failure.message), secondaryMarket, collection))
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
