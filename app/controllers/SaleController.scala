package controllers

import controllers.actions._
import exceptions.BaseException
import models.analytics.CollectionsAnalysis
import models.master.{Collection, NFTOwner, Sale}
import models.{blockchain, master, masterTransaction}
import play.api.Logger
import play.api.cache.Cached
import play.api.i18n.I18nSupport
import play.api.mvc._
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
                                collectionsAnalysis: CollectionsAnalysis,
                                blockchainBalances: blockchain.Balances,
                                masterAccounts: master.Accounts,
                                masterKeys: master.Keys,
                                masterWhitelists: master.Whitelists,
                                masterCollections: master.Collections,
                                masterNFTs: master.NFTs,
                                masterNFTOwners: master.NFTOwners,
                                masterSales: master.Sales,
                                masterWhitelistMembers: master.WhitelistMembers,
                                masterTransactionNotifications: masterTransaction.Notifications,
                              )(implicit executionContext: ExecutionContext) extends AbstractController(messagesControllerComponents) with I18nSupport {

  private implicit val logger: Logger = Logger(this.getClass)

  private implicit val module: String = constants.Module.SALE_CONTROLLER

  implicit val callbackOnSessionTimeout: Call = routes.CollectionController.viewCollections(constants.View.DEFAULT_COLLECTION_SECTION)

  def createCollectionSaleForm(whitelistId: Option[String], collectionId: Option[String]): Action[AnyContent] = withLoginActionAsync { implicit loginState =>
    implicit request =>
      val whitelists = masterWhitelists.Service.getIdNameMapForOwner(loginState.username)
      val collections = masterCollections.Service.getByCreator(loginState.username)

      for {
        whitelists <- whitelists
        collections <- collections
      } yield if ((whitelistId.isDefined && whitelists.exists(_._1 == whitelistId.get)) || (collectionId.isDefined && collections.exists(_._1 == whitelistId.get))) Ok(views.html.sale.createCollectionSale(collections = collections.map(x => x._1 -> x._2).toMap, collectionId = collectionId, whitelistId = whitelistId, whitelists = whitelists))
      else BadRequest(constants.Response.COLLECTION_ID_OR_WHITELIST_ID_DOES_NOT_EXIST.message)
  }

  def createCollectionSale(): Action[AnyContent] = withLoginActionAsync { implicit loginState =>
    implicit request =>
      CreateCollectionSale.form.bindFromRequest().fold(
        formWithErrors => {
          val collections = masterCollections.Service.getByCreator(loginState.username)
          val whitelists = masterWhitelists.Service.getIdNameMapForOwner(loginState.username)

          for {
            whitelists <- whitelists
            collections <- collections
          } yield BadRequest(views.html.sale.createCollectionSale(formWithErrors, whitelists = whitelists, whitelistId = formWithErrors.data.get(constants.FormField.SELECT_WHITELIST_ID.name), collectionId = formWithErrors.data.get(constants.FormField.SELECT_COLLECTION_ID.name), collections = collections.map(x => x._1 -> x._1).toMap))
        },
        createData => {
          val collection = masterCollections.Service.tryGet(id = createData.collectionId)
          val whitelistIds = masterWhitelists.Service.getIdsByOwnerId(loginState.username)

          def currentOnSaleIds(whitelistIds: Seq[String]) = masterSales.Service.getIdsCurrentOnSaleByWhitelistIds(whitelistIds)

          def countNFts(currentOnSaleIds: Seq[String]) = masterNFTOwners.Service.countForOwnerNotOnSale(collectionId = createData.collectionId, currentOnSaleIds = currentOnSaleIds, ownerId = loginState.username)

          def updateCreatorFee(collection: Collection) = if (collection.creatorFee != createData.creatorFee) masterCollections.Service.update(collection.copy(creatorFee = createData.creatorFee)) else Future()

          def addToSale(collection: Collection, countNFts: Int, currentOnSaleIds: Seq[String]) = {
            val errors = Seq(
              if (collection.creatorId != loginState.username) Option(constants.Response.NOT_COLLECTION_OWNER) else None,
              if (!collection.public) Option(constants.Response.COLLECTION_NOT_PUBLIC) else None,
              if (createData.nftForSale > countNFts) Option(constants.Response.NOT_ENOUGH_NFTS_IN_COLLECTION) else None,
            ).flatten
            if (errors.isEmpty) {
              for {
                saleId <- masterSales.Service.add(createData.toNewSale)
                _ <- masterNFTOwners.Service.addRandomNFTsToSale(collectionId = collection.id, nfts = createData.nftForSale, ownerId = loginState.username, saleId = saleId, currentOnSaleIds = currentOnSaleIds)
              } yield ()
            } else errors.head.throwFutureBaseException()
          }

          (for {
            collection <- collection
            whitelistIds <- whitelistIds
            currentOnSaleIds <- currentOnSaleIds(whitelistIds)
            countNFts <- countNFts(currentOnSaleIds)
            _ <- updateCreatorFee(collection)
            _ <- addToSale(collection = collection, countNFts = countNFts, currentOnSaleIds = currentOnSaleIds)
            _ <- collectionsAnalysis.Utility.onCreateSale(collection.id)
          } yield PartialContent(views.html.sale.createSuccessful())
            ).recover {
            case baseException: BaseException => {
              try {
                val collections = masterCollections.Service.getByCreator(loginState.username)
                val whitelists = masterWhitelists.Service.getIdNameMapForOwner(loginState.username)

                val result = for {
                  whitelists <- whitelists
                  collections <- collections
                } yield BadRequest(views.html.sale.createCollectionSale(CreateCollectionSale.form.withGlobalError(baseException.failure.message), collectionId = Option(createData.collectionId), whitelists = whitelists, whitelistId = Option(createData.whitelistId), collections = collections.map(x => x._1 -> x._2).toMap))
                Await.result(result, Duration.Inf)
              } catch {
                case baseException: BaseException => BadRequest(views.html.sale.createCollectionSale(CreateCollectionSale.form.withGlobalError(baseException.failure.message), collections = Map(), collectionId = Option(createData.collectionId), whitelistId = Option(createData.whitelistId), whitelists = Map()))
              }
            }
          }
        }
      )
  }

  def buySaleNFTForm(saleId: String, nftId: String): Action[AnyContent] = withoutLoginAction { implicit request =>
    Ok(views.html.sale.buySaleNFT(saleId = saleId, nftId = nftId))
  }

  def buySaleNFT(): Action[AnyContent] = withLoginActionAsync { implicit loginState =>
    implicit request =>
      BuySaleNFT.form.bindFromRequest().fold(
        formWithErrors => {
          Future(BadRequest(views.html.sale.buySaleNFT(formWithErrors, saleId = formWithErrors.data.getOrElse(constants.FormField.SALE_ID.name, ""), nftId = formWithErrors.data.getOrElse(constants.FormField.NFT_FILE_NAME.name, ""))))
        },
        buySaleNFTData => {
          val sale = masterSales.Service.tryGet(buySaleNFTData.saleId)
          val nftOwner = masterNFTOwners.Service.tryGetByNFTAndSaleId(fileName = buySaleNFTData.fileName, saleId = buySaleNFTData.saleId)
          val verifyPassword = masterKeys.Service.validateActiveKeyUsernamePasswordAndGet(username = loginState.username, password = buySaleNFTData.password)

          def isWhitelistMember(sale: Sale) = masterWhitelistMembers.Service.isMember(whitelistId = sale.whitelistId, accountId = loginState.username)

          def validateAndTransfer(nftOwner: NFTOwner, verifyPassword: Boolean, sale: Sale, isWhitelistMember: Boolean) = {
            val errors = Seq(
              if (nftOwner.ownerId == loginState.username) Option(constants.Response.CANNOT_SELL_TO_YOURSELF) else None,
              if (!isWhitelistMember) Option(constants.Response.NOT_MEMBER_OF_WHITELIST) else None,
              if (sale.startTimeEpoch > utilities.Date.currentEpoch) Option(constants.Response.SALE_NOT_STARTED) else None,
              if (utilities.Date.currentEpoch >= sale.endTimeEpoch) Option(constants.Response.SALE_EXPIRED) else None,
              if (!verifyPassword) Option(constants.Response.INVALID_PASSWORD) else None,
            ).flatten
            if (errors.nonEmpty) {
              masterNFTOwners.Service.update(nftOwner.copy(ownerId = loginState.username))
            } else errors.head.throwFutureBaseException()
          }

          (for {
            sale <- sale
            nftOwner <- nftOwner
            (verifyPassword, _) <- verifyPassword
            isWhitelistMember <- isWhitelistMember(sale)
            _ <- validateAndTransfer(nftOwner, verifyPassword, sale, isWhitelistMember)
          } yield PartialContent(views.html.sale.createSuccessful())
            ).recover {
            case baseException: BaseException => BadRequest(views.html.sale.buySaleNFT(BuySaleNFT.form.withGlobalError(baseException.failure.message), saleId = buySaleNFTData.saleId, nftId = buySaleNFTData.fileName))
          }
        }
      )
  }

}
