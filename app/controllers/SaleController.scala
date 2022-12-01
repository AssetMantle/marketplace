package controllers

import controllers.actions._
import exceptions.BaseException
import models.analytics.CollectionsAnalysis
import models.master._
import models.{blockchain, blockchainTransaction, master}
import org.bitcoinj.core.ECKey
import play.api.Logger
import play.api.cache.Cached
import play.api.i18n.I18nSupport
import play.api.mvc._
import utilities.MicroNumber
import views.sale.companion._

import javax.inject.{Inject, Singleton}
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.util.Try

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
                                masterNFTProperties: master.NFTProperties,
                                masterWhitelistMembers: master.WhitelistMembers,
                                blockchainTransactionBuyAssetWithoutMints: blockchainTransaction.BuyAssetWithoutMints,
                                blockchainTransactionBuyAndMintAssets: blockchainTransaction.BuyAndMintAssets,
                                utilitiesNotification: utilities.Notification,
                                utilitiesOperations: utilities.Operations,
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
          val whitelistMembers = masterWhitelistMembers.Service.getAllMembers(createData.whitelistId)

          def currentOnSaleIds(whitelistIds: Seq[String]) = masterSales.Service.getIdsCurrentOnSaleByWhitelistIds(whitelistIds)

          def countNFts(currentOnSaleIds: Seq[String]) = masterNFTOwners.Service.countForOwnerNotOnSale(collectionId = createData.collectionId, currentOnSaleIds = currentOnSaleIds, ownerId = loginState.username)

          def addToSale(collection: Collection, countNFts: Int, currentOnSaleIds: Seq[String]) = {
            val errors = Seq(
              if (!loginState.isGenesisCreator) Option(constants.Response.NOT_GENESIS_CREATOR) else None,
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

          def sendNotifications(whitelistMembers: Seq[String], collectionName: String): Unit = whitelistMembers.foreach { member =>
            utilitiesNotification.send(accountID = member, constants.Notification.SALE_ON_WHITELIST, collectionName)(s"'$member', '${constants.View.WHITELIST}'")
          }

          (for {
            collection <- collection
            whitelistIds <- whitelistIds
            whitelistMembers <- whitelistMembers
            currentOnSaleIds <- currentOnSaleIds(whitelistIds)
            countNFts <- countNFts(currentOnSaleIds)
            _ <- addToSale(collection = collection, countNFts = countNFts, currentOnSaleIds = currentOnSaleIds)
            _ <- collectionsAnalysis.Utility.onCreateSale(collection.id)
          } yield {
            sendNotifications(whitelistMembers, collection.name)
            PartialContent(views.html.sale.createSuccessful())
          }
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

  def buySaleNFTForm(saleId: String, nftId: String, mintNft: Boolean): Action[AnyContent] = withoutLoginAction { implicit request =>
    Ok(views.html.sale.buySaleNFT(saleId = saleId, nftId = nftId, mintNft = mintNft))
  }

  def buySaleNFT(): Action[AnyContent] = withLoginActionAsync { implicit loginState =>
    implicit request =>
      BuySaleNFT.form.bindFromRequest().fold(
        formWithErrors => {
          Future(BadRequest(views.html.sale.buySaleNFT(formWithErrors, saleId = formWithErrors.data.getOrElse(constants.FormField.SALE_ID.name, ""), nftId = formWithErrors.data.getOrElse(constants.FormField.NFT_ID.name, ""), mintNft = Try(formWithErrors.data.getOrElse(constants.FormField.MINT_NFT.name, "false").toBoolean).getOrElse(false))))
        },
        buySaleNFTData => {
          val sale = masterSales.Service.tryGet(buySaleNFTData.saleId)
          val nftOwner = masterNFTOwners.Service.tryGetByNFTAndSaleId(nftId = buySaleNFTData.nftId, saleId = buySaleNFTData.saleId)
          val verifyPassword = masterKeys.Service.validateActiveKeyUsernamePasswordAndGet(username = loginState.username, password = buySaleNFTData.password)
          val balance = blockchainBalances.Service.getTokenBalance(loginState.address)
          val nft = masterNFTs.Service.tryGet(buySaleNFTData.nftId)
          val nftProperties = masterNFTProperties.Service.getForNFT(buySaleNFTData.nftId)
          val countBuyerNFTsFromSale = blockchainTransactionBuyAssetWithoutMints.Service.countBuyerNFTsFromSale(buyerAccountId = loginState.username, saleId = buySaleNFTData.saleId)

          def sellerKey(NFTOwner: NFTOwner) = masterKeys.Service.tryGetActive(NFTOwner.ownerId)

          def isWhitelistMember(sale: Sale) = masterWhitelistMembers.Service.isMember(whitelistId = sale.whitelistId, accountId = loginState.username)

          def validateAndTransfer(nftOwner: NFTOwner, verifyPassword: Boolean, sale: Sale, isWhitelistMember: Boolean, buyerKey: Key, sellerKey: Key, balance: MicroNumber, nft: NFT, nftProperties: Seq[NFTProperty], countBuyerNFTsFromSale: Int) = {
            val errors = Seq(
              if (nftOwner.ownerId == loginState.username) Option(constants.Response.CANNOT_SELL_TO_YOURSELF) else None,
              if (!isWhitelistMember) Option(constants.Response.NOT_MEMBER_OF_WHITELIST) else None,
              if (sale.startTimeEpoch > utilities.Date.currentEpoch) Option(constants.Response.SALE_NOT_STARTED) else None,
              if (utilities.Date.currentEpoch >= sale.endTimeEpoch) Option(constants.Response.SALE_EXPIRED) else None,
              if (balance == MicroNumber.zero || balance <= sale.price) Option(constants.Response.INSUFFICIENT_BALANCE) else None,
              if (buySaleNFTData.mintNFT && (balance - (sale.price + constants.Blockchain.AssetPropertyRate * (nftProperties.length + constants.Collection.DefaultProperty.list.length)) <= MicroNumber.zero)) Option(constants.Response.INSUFFICIENT_BALANCE) else None,
              if (countBuyerNFTsFromSale >= sale.maxMintPerAccount) Option(constants.Response.MAXIMUM_NFT_MINT_PER_ACCOUNT_REACHED) else None,
              if (buySaleNFTData.mintNFT && nft.isMinted) Option(constants.Response.NFT_ALREADY_MINTED) else None,
              if (!verifyPassword) Option(constants.Response.INVALID_PASSWORD) else None,
            ).flatten
            if (errors.isEmpty) {
              if (!buySaleNFTData.mintNFT) {
                blockchainTransactionBuyAssetWithoutMints.Utility.transaction(
                  buyerAccountId = loginState.username,
                  sellerAccountId = sellerKey.accountId,
                  saleId = sale.id,
                  nftId = buySaleNFTData.nftId,
                  fromAddress = buyerKey.address,
                  toAddress = sellerKey.address,
                  amount = sale.price,
                  gasLimit = buySaleNFTData.gasAmount,
                  gasPrice = buySaleNFTData.gasPrice,
                  ecKey = ECKey.fromPrivate(utilities.Secrets.decryptData(buyerKey.encryptedPrivateKey, buySaleNFTData.password))
                )
              } else {
                blockchainTransactionBuyAndMintAssets.Utility.transaction(
                  buyerAccountId = loginState.username,
                  sellerAccountId = sellerKey.accountId,
                  fromId = loginState.getIdentityId,
                  toId = loginState.getIdentityId,
                  assetId = "",
                  classificationId = "",
                  saleId = sale.id,
                  nftId = buySaleNFTData.nftId,
                  fromAddress = buyerKey.address,
                  toAddress = sellerKey.address,
                  amount = sale.price,
                  gasLimit = buySaleNFTData.gasAmount,
                  gasPrice = buySaleNFTData.gasPrice,
                  ecKey = ECKey.fromPrivate(utilities.Secrets.decryptData(buyerKey.encryptedPrivateKey, buySaleNFTData.password)),
                )
              }
            } else errors.head.throwFutureBaseException()
          }

          (for {
            sale <- sale
            nftOwner <- nftOwner
            nft <- nft
            nftProperties <- nftProperties
            sellerKey <- sellerKey(nftOwner)
            (verifyPassword, buyerKey) <- verifyPassword
            isWhitelistMember <- isWhitelistMember(sale)
            balance <- balance
            countBuyerNFTsFromSale <- countBuyerNFTsFromSale
            blockchainTransaction <- validateAndTransfer(nftOwner = nftOwner, verifyPassword = verifyPassword, sale = sale, isWhitelistMember = isWhitelistMember, buyerKey = buyerKey, sellerKey = sellerKey, balance = balance, nft = nft, nftProperties = nftProperties, countBuyerNFTsFromSale = countBuyerNFTsFromSale)
          } yield PartialContent(views.html.blockchainTransaction.transactionSuccessful(blockchainTransaction))
            ).recover {
            case baseException: BaseException => BadRequest(views.html.sale.buySaleNFT(BuySaleNFT.form.withGlobalError(baseException.failure.message), saleId = buySaleNFTData.saleId, nftId = buySaleNFTData.nftId, mintNft = buySaleNFTData.mintNFT))
          }
        }
      )
  }

}
