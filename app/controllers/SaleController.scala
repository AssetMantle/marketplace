package controllers

import controllers.actions._
import exceptions.BaseException
import models.{master, masterTransaction}
import play.api.Logger
import play.api.cache.Cached
import play.api.i18n.I18nSupport
import play.api.mvc._
import views.sale.companion._

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class SaleController @Inject()(
                                messagesControllerComponents: MessagesControllerComponents,
                                cached: Cached,
                                withoutLoginActionAsync: WithoutLoginActionAsync,
                                withLoginAction: WithLoginAction,
                                withLoginActionAsync: WithLoginActionAsync,
                                withoutLoginAction: WithoutLoginAction,
                                masterAccounts: master.Accounts,
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

//  def acceptWhitelistSaleOfferForm(id: String): Action[AnyContent] = withLoginActionAsync { implicit loginState =>
//    implicit request =>
//      val whitelistSale = masterNFTWhitelistSales.Service.tryGet(id)
//      def nft(fileName: String) = masterNFTs.Service.tryGet(fileName)
//      def collection(id: String) = masterCollections.Service.tryGet(id)
//
//      for {
//        whitelistSale <- whitelistSale
//        nft <- nft(whitelistSale.fileName)
//        collection <- collection(nft.collectionId)
//      } yield Ok(views.html.sale.acceptWhitelistSaleOffer(collections = collections.toMap, whitelistId = whitelistId, whitelists = whitelists))
//  }


}
