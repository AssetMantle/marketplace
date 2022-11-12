package controllers

import controllers.actions._
import exceptions.BaseException
import models.{blockchain, master, masterTransaction}
import play.api.Logger
import play.api.cache.Cached
import play.api.i18n.I18nSupport
import play.api.mvc._
import utilities.MicroNumber
import views.sale.companion._

import javax.inject.{Inject, Singleton}
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}

@Singleton
class SaleController @Inject()(
                                messagesControllerComponents: MessagesControllerComponents,
                                cached: Cached,
                                withoutLoginActionAsync: WithoutLoginActionAsync,
                                withLoginAction: WithLoginAction,
                                withLoginActionAsync: WithLoginActionAsync,
                                withoutLoginAction: WithoutLoginAction,
                                blockchainBalances: blockchain.Balances,
                                masterAccounts: master.Accounts,
                                masterKeys: master.Keys,
                                masterWhitelists: master.Whitelists,
                                masterCollections: master.Collections,
                                masterNFTs: master.NFTs,
                                masterNFTWhitelistSales: master.NFTWhitelistSales,
                                masterWhitelistMembers: master.WhitelistMembers,
                                masterTransactionNotifications: masterTransaction.Notifications,
                              )(implicit executionContext: ExecutionContext) extends AbstractController(messagesControllerComponents) with I18nSupport {

  private implicit val logger: Logger = Logger(this.getClass)

  private implicit val module: String = constants.Module.SALE_CONTROLLER

  implicit val callbackOnSessionTimeout: Call = routes.CollectionController.viewCollections(constants.View.DEFAULT_COLLECTION_SECTION)

  private def checkNFTOwner(fileName: String, accountId: String) = {
    val nft = masterNFTs.Service.tryGet(fileName)

    def collectionOwner(collectionId: String) = masterCollections.Service.isOwner(id = collectionId, accountId = accountId)

    for {
      nft <- nft
      collectionOwner <- collectionOwner(nft.collectionId)
    } yield collectionOwner || nft.ownerId.getOrElse("") == accountId
  }

  def createWhitelistSaleForm(whitelistId: Option[String]): Action[AnyContent] = withLoginActionAsync { implicit loginState =>
    implicit request =>
      val whitelists = masterWhitelists.Service.getAll(loginState.username)
      val collections = masterCollections.Service.getByCreator(loginState.username)

      for {
        whitelists <- whitelists
        collections <- collections
      } yield Ok(views.html.sale.createWhitelistSale(collections = collections.toMap, whitelistId = whitelistId, whitelists = whitelists))
  }

  def createWhitelistSale(): Action[AnyContent] = withLoginActionAsync { implicit loginState =>
    implicit request =>
      CreateWhitelistSale.form.bindFromRequest().fold(
        formWithErrors => {
          val collections = masterCollections.Service.getByCreator(loginState.username)
          val whitelists = masterWhitelists.Service.getAll(loginState.username)

          for {
            whitelists <- whitelists
            collections <- collections
          } yield BadRequest(views.html.sale.createWhitelistSale(formWithErrors, whitelists = whitelists, whitelistId = formWithErrors.data.get(constants.FormField.SELECT_WHITELIST_ID.name), collections = collections.toMap))
        },
        createData => {
          val isCollectionOwner = masterCollections.Service.isOwner(id = createData.collectionId, accountId = loginState.username)
          val verifyNFTsCollection = if (createData.nftFileNames.isDefined && createData.nftFileNames.get.split(",").nonEmpty) masterNFTs.Service.verifyNFTsCollection(fileNames = createData.nftFileNames.get.split(","), collectionId = createData.collectionId) else Future(true)

          def addToSale(isCollectionOwner: Boolean, verifyNFTsCollection: Boolean) = if (isCollectionOwner && verifyNFTsCollection) {
            if (createData.nftFileNames.isDefined && createData.nftFileNames.get.split(",").nonEmpty) masterNFTWhitelistSales.Service.add(createData.toNFTWhitelistSales(quantity = 1, fileNames = createData.nftFileNames.get.split(","), denom = constants.Blockchain.StakingToken))
            else {
              val nftIds = masterNFTs.Service.getIdsAllForCollection(createData.collectionId)
              for {
                nftIds <- nftIds
                _ <- masterNFTWhitelistSales.Service.add(createData.toNFTWhitelistSales(quantity = 1, fileNames = nftIds, denom = constants.Blockchain.StakingToken))
              } yield ()
            }
          }
          else constants.Response.NOT_NFT_OWNER.throwFutureBaseException()

          (for {
            isCollectionOwner <- isCollectionOwner
            verifyNFTsCollection <- verifyNFTsCollection
            _ <- addToSale(isCollectionOwner = isCollectionOwner, verifyNFTsCollection = verifyNFTsCollection)
          } yield PartialContent(views.html.sale.createSuccessful())
            ).recover {
            case baseException: BaseException => BadRequest(views.html.sale.createWhitelistSale(CreateWhitelistSale.form.withGlobalError(baseException.failure.message), Map(), whitelistId = Option(createData.whitelistId), Map()))
          }
        }
      )
  }

  def selectCollectionNFTs(pageNumber: Int, collectionId: String): EssentialAction = cached(req => utilities.Session.getSessionCachingKey(req), constants.CommonConfig.WebAppCacheDuration) {
    withoutLoginActionAsync { implicit loginState =>
      implicit request =>
        val nfts = masterNFTs.Service.getByPageNumber(collectionId, pageNumber)
        (for {
          nfts <- nfts
        } yield Ok(views.html.sale.selectCollectionNFTs(pageNumber, nfts))
          ).recover {
          case baseException: BaseException => InternalServerError(baseException.failure.message)
        }
    }
  }

  def acceptWhitelistSaleOfferForm(id: String): Action[AnyContent] = withLoginActionAsync { implicit loginState =>
    implicit request =>
      val whitelistSale = masterNFTWhitelistSales.Service.tryGet(id)

      for {
        whitelistSale <- whitelistSale
      } yield Ok(views.html.sale.acceptWhitelistSaleOffer(saleId = id, whitelistSale = whitelistSale))
  }

  def acceptWhitelistSaleOffer(): Action[AnyContent] = withLoginActionAsync { implicit loginState =>
    implicit request =>
      AcceptWhitelistSaleOffer.form.bindFromRequest().fold(
        formWithErrors => {
          val saleId = formWithErrors.data.getOrElse(constants.FormField.WHITELIST_SALE_ID.name, "")
          val whitelistSale = masterNFTWhitelistSales.Service.tryGet(saleId)

          for {
            whitelistSale <- whitelistSale
          } yield BadRequest(views.html.sale.acceptWhitelistSaleOffer(formWithErrors, saleId = saleId, whitelistSale = whitelistSale))
        },
        acceptWhitelistSaleOfferData => {
          val keyValidPassword = masterKeys.Service.validateActiveKeyUsernamePasswordAndGet(username = loginState.username, password = acceptWhitelistSaleOfferData.password)
          val whitelistSale = masterNFTWhitelistSales.Service.tryGet(acceptWhitelistSaleOfferData.saleId)
          val balance = blockchainBalances.Service.tryGet(loginState.address)

          def validateAndTransfer(validPassword: Boolean, key: master.Key, whitelistSale: master.NFTWhitelistSale, balance: blockchain.Balance) = {
            if (validPassword) {
              if (balance.coins.find(_.denom == whitelistSale.denom).map(_.amount).getOrElse(MicroNumber.zero) >= whitelistSale.price) {
                Future()
              } else constants.Response.INSUFFICIENT_BALANCE.throwFutureBaseException()
            } else constants.Response.INVALID_PASSWORD.throwFutureBaseException()
          }

          (for {
            whitelistSale <- whitelistSale
            (validPassword, key) <- keyValidPassword
            balance <- balance
            _ <- validateAndTransfer(validPassword, key, whitelistSale, balance)
          } yield PartialContent(views.html.sale.createSuccessful())
            ).recover {
            case baseException: BaseException => {
              try {
                val whitelistSale = Await.result(masterNFTWhitelistSales.Service.tryGet(acceptWhitelistSaleOfferData.saleId), Duration.Inf)
                BadRequest(views.html.sale.acceptWhitelistSaleOffer(AcceptWhitelistSaleOffer.form.withGlobalError(baseException.failure.message), saleId = acceptWhitelistSaleOfferData.saleId, whitelistSale = whitelistSale))
              } catch {
                case baseException: BaseException => BadRequest(baseException.failure.message).withNewSession
              }
            }
          }
        }
      )
  }


}