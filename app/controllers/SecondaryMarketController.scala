package controllers

import controllers.actions._
import exceptions.BaseException
import models.analytics.CollectionsAnalysis
import models.master._
import models.masterTransaction.MakeOrderTransaction
import models.{blockchain, master, masterTransaction}
import org.bitcoinj.core.ECKey
import play.api.Logger
import play.api.cache.Cached
import play.api.i18n.I18nSupport
import play.api.mvc._
import utilities.MicroNumber
import views.secondaryMarket.companion.{Buy, Cancel, CreateSecondaryMarket}

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

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

  private implicit val logger: Logger = Logger(this.getClass)

  private implicit val module: String = constants.Module.SECONDARY_MARKET_CONTROLLER

  implicit val callbackOnSessionTimeout: Call = routes.SecondaryMarketController.viewSecondaryMarket()

  def viewSecondaryMarket(): EssentialAction = cached(req => utilities.Session.getSessionCachingKey(req), constants.CommonConfig.WebAppCacheDuration) {
    withoutLoginActionAsync { implicit loginState =>
      implicit request =>
        Future(Ok(views.html.secondaryMarket.viewSecondaryMarkets()))
    }
  }

  def collectionsSection(): EssentialAction = cached(req => utilities.Session.getSessionCachingKey(req), constants.CommonConfig.WebAppCacheDuration) {
    withoutLoginActionAsync { implicit loginState =>
      implicit request =>
        val totalCollections = masterSecondaryMarkets.Service.total
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
        val secondaryMarkets = if (pageNumber < 1) Future(throw new BaseException(constants.Response.INVALID_PAGE_NUMBER))
        else masterSecondaryMarkets.Service.getByPageNumber(pageNumber)

        def collections(ids: Seq[String]) = masterCollections.Service.getCollections(ids)

        (for {
          secondaryMarkets <- secondaryMarkets
          collections <- collections(secondaryMarkets.map(_.collectionId))
        } yield Ok(views.html.secondaryMarket.collectionsPerPage(collections))
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
        val collection = if (pageNumber < 1) Future(throw new BaseException(constants.Response.INVALID_PAGE_NUMBER))
        else masterCollections.Service.tryGet(id)
        val likedNFTs = optionalLoginState.fold[Future[Seq[String]]](Future(Seq()))(x => masterWishLists.Service.getByCollection(accountId = x.username, collectionId = id))
        val nftIds = masterNFTOwners.Service.getByMarketIDs(id, pageNumber)

        def nfts(nftIds: Seq[String]) = masterNFTs.Service.getByIds(nftIds)

        (for {
          collection <- collection
          nftIds <- nftIds
          nfts <- nfts(nftIds)
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
      Future(Ok(views.html.secondaryMarket.createSecondaryMarket(nftId = nftId)))
  }

  def create(): Action[AnyContent] = withLoginActionAsync { implicit loginState =>
    implicit request =>
      CreateSecondaryMarket.form.bindFromRequest().fold(
        formWithErrors => {
          val nftID = formWithErrors.data.getOrElse(constants.FormField.NFT_ID.name, "")
          Future(BadRequest(views.html.secondaryMarket.createSecondaryMarket(formWithErrors, nftID)))
        },
        createData => {
          val nft = masterNFTs.Service.tryGet(createData.nftId)
          val nftOwner = masterNFTOwners.Service.tryGet(nftId = createData.nftId, ownerId = loginState.username)
          val activeKey = masterKeys.Service.tryGetActive(loginState.username)
          // TODO
          val quantity = 1

          def collection(id: String) = masterCollections.Service.tryGet(id)

          def addToSecondaryMarket(nft: NFT, nftOwner: NFTOwner, collection: Collection, activeKey: Key) = {
            val errors = Seq(
              if (!nft.isMinted.getOrElse(false)) Option(constants.Response.NFT_NOT_MINTED) else None,
              if (nftOwner.creatorId == "Mint.E") Option(constants.Response.SELLING_MINT_E_COLLECTIONS_NOT_ALLOWED) else None,
              if (nftOwner.ownerId != loginState.username) Option(constants.Response.NOT_NFT_OWNER) else None,
              if (nftOwner.creatorId == loginState.username) Option(constants.Response.NFT_CREATOR_NOT_ALLOWED_SECONDARY_SALE) else None,
              if (nftOwner.saleId.isDefined || nftOwner.publicListingId.isDefined || nftOwner.secondaryMarketId.isDefined) Option(constants.Response.NFT_ALREADY_ON_SALE) else None,
              if (!collection.public) Option(constants.Response.COLLECTION_NOT_PUBLIC) else None,
            ).flatten
            if (errors.isEmpty) {
              for {
                secondaryMarketId <- masterSecondaryMarkets.Service.add(createData.toNewSecondaryMarket(collectionId = nftOwner.collectionId, sellerId = loginState.username, quantity = quantity))
                _ <- masterNFTOwners.Service.listNFTOnSecondaryMarket(NFTOwner = nftOwner, secondaryMarketID = secondaryMarketId)
                tx <- masterTransactionMakeOrderTransactions.Utility.transaction(nftID = createData.nftId, nftOwner = nftOwner, fromAddress = loginState.address, quantity = quantity, endHours = createData.endHours, price = createData.price, buyerId = None, secondaryMarketId = secondaryMarketId, gasPrice = constants.Blockchain.DefaultGasPrice, ecKey = activeKey.getECKey(createData.password).get)
              } yield tx
            } else errors.head.throwFutureBaseException()
          }

          (for {
            nft <- nft
            nftOwner <- nftOwner
            activeKey <- activeKey
            collection <- collection(nftOwner.collectionId)
            tx <- addToSecondaryMarket(nft = nft, nftOwner = nftOwner, collection = collection, activeKey = activeKey)
          } yield PartialContent(views.html.blockchainTransaction.transactionSuccessful(tx))
            ).recover {
            case baseException: BaseException => BadRequest(views.html.secondaryMarket.createSecondaryMarket(CreateSecondaryMarket.form.withGlobalError(baseException.failure.message), createData.nftId))
          }
        }
      )
  }

  def cancelForm(nftId: String, secondaryMarketId: String): Action[AnyContent] = withLoginActionAsync { implicit loginState =>
    implicit request =>
      Future(Ok(views.html.secondaryMarket.cancel(nftId = nftId, secondaryMarketId = secondaryMarketId)))
  }


  def buyForm(nftId: String, secondaryMarketId: String): Action[AnyContent] = withLoginActionAsync { implicit loginState =>
    implicit request =>
      Future(Ok(views.html.secondaryMarket.buy(nftId = nftId, secondaryMarketId = secondaryMarketId)))
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
          val nftOwner = masterNFTOwners.Service.tryGetBySecondaryMarketId(cancelData.secondaryMarketId)
          val checkAlreadySold = masterTransactionTakeOrderTransactions.Service.checkAlreadySold(nftId = cancelData.nftId, secondaryMarketId = cancelData.secondaryMarketId)
          // TODO
          val quantity = 1

          def validateAndTransfer(nftOwner: NFTOwner, makeOrderTx: MakeOrderTransaction, verifyPassword: Boolean, secondaryMarket: SecondaryMarket, buyerKey: Key, checkAlreadySold: Boolean) = {
            val errors = Seq(
              if (nftOwner.ownerId != loginState.username) Option(constants.Response.NFT_OWNER_NOT_FOUND) else None,
              if (!verifyPassword) Option(constants.Response.INVALID_PASSWORD) else None,
              if (checkAlreadySold) Option(constants.Response.NFT_ALREADY_SOLD) else None,
              if (makeOrderTx.secondaryMarketId != secondaryMarket.id || makeOrderTx.nftId != cancelData.nftId || makeOrderTx.sellerId != nftOwner.ownerId || secondaryMarket.sellerId != loginState.username) Option(constants.Response.INVALID_ORDER) else None,
            ).flatten
            if (errors.isEmpty) {
              masterTransactionCancelOrderTransactions.Utility.transaction(
                secondaryMarket = secondaryMarket,
                fromAddress = buyerKey.address,
                gasPrice = constants.Blockchain.DefaultGasPrice,
                ecKey = ECKey.fromPrivate(utilities.Secrets.decryptData(buyerKey.encryptedPrivateKey, cancelData.password))
              )
            } else errors.head.throwFutureBaseException()
          }

          (for {
            secondaryMarket <- secondaryMarket
            makeOrderTx <- makeOrderTx
            nftOwner <- nftOwner
            (verifyPassword, buyerKey) <- verifyPassword
            checkAlreadySold <- checkAlreadySold
            blockchainTransaction <- validateAndTransfer(nftOwner = nftOwner, makeOrderTx = makeOrderTx, verifyPassword = verifyPassword, secondaryMarket = secondaryMarket, buyerKey = buyerKey, checkAlreadySold = checkAlreadySold)
          } yield PartialContent(views.html.blockchainTransaction.transactionSuccessful(blockchainTransaction))
            ).recover {
            case baseException: BaseException => BadRequest(views.html.secondaryMarket.cancel(Cancel.form.withGlobalError(baseException.failure.message), nftId = cancelData.nftId, secondaryMarketId = cancelData.secondaryMarketId))
          }
        }
      )
  }

  def buy(): Action[AnyContent] = withLoginActionAsync { implicit loginState =>
    implicit request =>
      Buy.form.bindFromRequest().fold(
        formWithErrors => {
          Future(BadRequest(views.html.secondaryMarket.buy(formWithErrors, nftId = formWithErrors.data.getOrElse(constants.FormField.NFT_ID.name, ""), secondaryMarketId = formWithErrors.data.getOrElse(constants.FormField.SECONDARY_MARKET_ID.name, ""))))
        },
        buyData => {
          val secondaryMarket = masterSecondaryMarkets.Service.tryGet(buyData.secondaryMarketId)
          val makeOrderTx = masterTransactionMakeOrderTransactions.Service.tryGetByNFTAndSecondaryMarket(nftId = buyData.nftId, secondaryMarketId = buyData.secondaryMarketId)
          val verifyPassword = masterKeys.Service.validateActiveKeyUsernamePasswordAndGet(username = loginState.username, password = buyData.password)
          val balance = blockchainBalances.Service.getTokenBalance(loginState.address)
          val nftOwner = masterNFTOwners.Service.tryGetBySecondaryMarketId(buyData.secondaryMarketId)
          val checkAlreadySold = masterTransactionTakeOrderTransactions.Service.checkAlreadySold(nftId = buyData.nftId, secondaryMarketId = buyData.secondaryMarketId)
          // TODO
          val quantity = 1

          def validateAndTransfer(nftOwner: NFTOwner, makeOrderTx: MakeOrderTransaction, verifyPassword: Boolean, secondaryMarket: SecondaryMarket, buyerKey: Key, balance: MicroNumber, checkAlreadySold: Boolean) = {
            val errors = Seq(
              if (nftOwner.ownerId == loginState.username) Option(constants.Response.CANNOT_SELL_TO_YOURSELF) else None,
              if (balance == MicroNumber.zero || balance <= (secondaryMarket.price * quantity)) Option(constants.Response.INSUFFICIENT_BALANCE) else None,
              if (!verifyPassword) Option(constants.Response.INVALID_PASSWORD) else None,
              if (checkAlreadySold) Option(constants.Response.NFT_ALREADY_SOLD) else None,
              if (makeOrderTx.secondaryMarketId != secondaryMarket.id || makeOrderTx.nftId != buyData.nftId || makeOrderTx.sellerId != nftOwner.ownerId) Option(constants.Response.INVALID_ORDER) else None,
            ).flatten
            if (errors.isEmpty) {
              masterTransactionTakeOrderTransactions.Utility.transaction(
                nftID = buyData.nftId,
                nftOwner = nftOwner,
                quantity = 1,
                buyerId = loginState.username,
                fromAddress = buyerKey.address,
                secondaryMarket = secondaryMarket,
                gasPrice = constants.Blockchain.DefaultGasPrice,
                ecKey = ECKey.fromPrivate(utilities.Secrets.decryptData(buyerKey.encryptedPrivateKey, buyData.password))
              )
            } else errors.head.throwFutureBaseException()
          }

          (for {
            secondaryMarket <- secondaryMarket
            makeOrderTx <- makeOrderTx
            nftOwner <- nftOwner
            (verifyPassword, buyerKey) <- verifyPassword
            balance <- balance
            checkAlreadySold <- checkAlreadySold
            blockchainTransaction <- validateAndTransfer(nftOwner = nftOwner, makeOrderTx = makeOrderTx, verifyPassword = verifyPassword, secondaryMarket = secondaryMarket, buyerKey = buyerKey, balance = balance, checkAlreadySold = checkAlreadySold)
          } yield PartialContent(views.html.blockchainTransaction.transactionSuccessful(blockchainTransaction))
            ).recover {
            case baseException: BaseException => BadRequest(views.html.secondaryMarket.buy(Buy.form.withGlobalError(baseException.failure.message), nftId = buyData.nftId, secondaryMarketId = buyData.secondaryMarketId))
          }
        }
      )
  }

}
