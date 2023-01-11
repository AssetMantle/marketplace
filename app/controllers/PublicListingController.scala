package controllers

import controllers.actions._
import exceptions.BaseException
import models.analytics.CollectionsAnalysis
import models.master._
import models.{blockchain, blockchainTransaction, master, masterTransaction}
import play.api.Logger
import play.api.cache.Cached
import play.api.i18n.I18nSupport
import play.api.mvc._
import views.publicListing.companion._

import javax.inject.{Inject, Singleton}
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext}

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
                                         masterWhitelistMembers: master.WhitelistMembers,
                                         masterTransactionBuyNFTTransactions: masterTransaction.BuyNFTTransactions,
                                         blockchainTransactionBuyNFTs: blockchainTransaction.BuyNFTs,
                                         utilitiesNotification: utilities.Notification,
                                         utilitiesOperations: utilities.Operations,
                                       )(implicit executionContext: ExecutionContext) extends AbstractController(messagesControllerComponents) with I18nSupport {

  private implicit val logger: Logger = Logger(this.getClass)

  private implicit val module: String = constants.Module.PUBLIC_LISTING_CONTROLLER

  implicit val callbackOnSessionTimeout: Call = routes.CollectionController.viewCollections(constants.View.DEFAULT_COLLECTION_SECTION)

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
          } yield BadRequest(views.html.publicListing.createPublicListing(formWithErrors, collectionId = formWithErrors.data.get(constants.FormField.SELECT_COLLECTION_ID.name), collections = collections.map(x => x._1 -> x._1).toMap))
        },
        createData => {
          val collection = masterCollections.Service.tryGet(id = createData.collectionId)
          val countNFts = masterNFTOwners.Service.countForCreatorNotForSell(collectionId = createData.collectionId, creatorId = loginState.username)

          def addToPublicListing(collection: Collection, countNFts: Int) = {
            val errors = Seq(
              if (!loginState.isGenesisCreator) Option(constants.Response.NOT_GENESIS_CREATOR) else None,
              if (collection.creatorId != loginState.username) Option(constants.Response.NOT_COLLECTION_OWNER) else None,
              if (!collection.public) Option(constants.Response.COLLECTION_NOT_PUBLIC) else None,
              if (createData.numberOfNFTs > countNFts) Option(constants.Response.NOT_ENOUGH_NFTS_IN_COLLECTION) else None,
            ).flatten
            if (errors.isEmpty) {
              for {
                publicListingId <- masterPublicListings.Service.add(createData.toNewPublicListing)
                _ <- masterNFTOwners.Service.publicListRandomNFTs(collectionId = collection.id, nfts = createData.numberOfNFTs, creatorId = loginState.username, publicListingId = publicListingId)
              } yield ()
            } else errors.head.throwFutureBaseException()
          }

          (for {
            collection <- collection
            countNFts <- countNFts
            _ <- addToPublicListing(collection = collection, countNFts = countNFts)
            _ <- collectionsAnalysis.Utility.onCreatePublicListing(collection.id, totalListed = createData.numberOfNFTs, listingPrice = createData.price)
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
  //
  //  def buySaleNFTForm(saleId: String, mintNft: Boolean): Action[AnyContent] = withoutLoginAction { implicit request =>
  //    Ok(views.html.sale.buySaleNFT(saleId = saleId))
  //  }
  //
  //  def buySaleNFT(): Action[AnyContent] = withLoginActionAsync { implicit loginState =>
  //    implicit request =>
  //      BuySaleNFT.form.bindFromRequest().fold(
  //        formWithErrors => {
  //          Future(BadRequest(views.html.sale.buySaleNFT(formWithErrors, saleId = formWithErrors.data.getOrElse(constants.FormField.SALE_ID.name, ""))))
  //        },
  //        buySaleNFTData => {
  //          val sale = masterSales.Service.tryGet(buySaleNFTData.saleId)
  //          val verifyPassword = masterKeys.Service.validateActiveKeyUsernamePasswordAndGet(username = loginState.username, password = buySaleNFTData.password)
  //          val balance = blockchainBalances.Service.getTokenBalance(loginState.address)
  //          val countBuyerNFTsFromSale = masterTransactionBuyNFTTransactions.Service.countBuyerNFTsFromSale(buyerAccountId = loginState.username, saleId = buySaleNFTData.saleId)
  //
  //          def collection(id: String) = masterCollections.Service.tryGet(id)
  //
  //          def nftOwners(collection: Collection) = masterNFTOwners.Service.getRandomNFTsBySaleId(saleId = buySaleNFTData.saleId, take = buySaleNFTData.buyNFTs, creatorId = collection.creatorId)
  //
  //          def checkAlreadySold(nftIds: Seq[String]) = masterTransactionBuyNFTTransactions.Service.checkAlreadySold(saleId = buySaleNFTData.saleId, nftIds = nftIds)
  //
  //          def nfts(nftIds: Seq[String]) = masterNFTs.Service.getByIds(nftIds)
  //
  //          //          def nftProperties(nftId: String) = masterNFTProperties.Service.getForNFT(nftId)
  //
  //          def sellerKey(ownerId: String) = masterKeys.Service.tryGetActive(ownerId)
  //
  //          def isWhitelistMember(sale: Sale) = masterWhitelistMembers.Service.isMember(whitelistId = sale.whitelistId, accountId = loginState.username)
  //
  //          def validateAndTransfer(nftOwners: Seq[NFTOwner], verifyPassword: Boolean, sale: Sale, isWhitelistMember: Boolean, buyerKey: Key, sellerKey: Key, balance: MicroNumber, nfts: Seq[NFT], countBuyerNFTsFromSale: Int, checkAlreadySold: Boolean) = {
  //            val errors = Seq(
  //              if (nftOwners.map(_.ownerId).distinct.contains(loginState.username)) Option(constants.Response.CANNOT_SELL_TO_YOURSELF) else None,
  //              if (!isWhitelistMember) Option(constants.Response.NOT_MEMBER_OF_WHITELIST) else None,
  //              if (sale.startTimeEpoch > utilities.Date.currentEpoch) Option(constants.Response.SALE_NOT_STARTED) else None,
  //              if (utilities.Date.currentEpoch >= sale.endTimeEpoch) Option(constants.Response.SALE_EXPIRED) else None,
  //              if (balance == MicroNumber.zero || balance <= (sale.price * buySaleNFTData.buyNFTs)) Option(constants.Response.INSUFFICIENT_BALANCE) else None,
  //              //              if (buySaleNFTData.mintNFT && (balance - (sale.price + constants.Blockchain.AssetPropertyRate * (nftProperties.length + constants.Collection.DefaultProperty.list.length)) <= MicroNumber.zero)) Option(constants.Response.INSUFFICIENT_BALANCE) else None,
  //              if ((countBuyerNFTsFromSale + buySaleNFTData.buyNFTs) > sale.maxMintPerAccount) Option(constants.Response.MAXIMUM_NFT_MINT_PER_ACCOUNT_REACHED) else None,
  //              //              if (buySaleNFTData.mintNFT && nft.isMinted) Option(constants.Response.NFT_ALREADY_MINTED) else None,
  //              if (nfts.exists(_.isMinted)) Option(constants.Response.NFT_ALREADY_MINTED) else None,
  //              if (!verifyPassword) Option(constants.Response.INVALID_PASSWORD) else None,
  //              if (checkAlreadySold) Option(constants.Response.NFT_ALREADY_SOLD) else None,
  //            ).flatten
  //            if (errors.isEmpty) {
  //              blockchainTransactionBuyNFTs.Utility.transaction(
  //                buyerAccountId = loginState.username,
  //                sellerAccountId = sellerKey.accountId,
  //                saleId = sale.id,
  //                nftIds = nftOwners.map(_.nftId),
  //                fromAddress = buyerKey.address,
  //                toAddress = sellerKey.address,
  //                amount = sale.price * buySaleNFTData.buyNFTs,
  //                gasLimit = buySaleNFTData.gasAmount,
  //                gasPrice = buySaleNFTData.gasPrice,
  //                ecKey = ECKey.fromPrivate(utilities.Secrets.decryptData(buyerKey.encryptedPrivateKey, buySaleNFTData.password))
  //              )
  //            } else errors.head.throwFutureBaseException()
  //          }
  //
  //          (for {
  //            sale <- sale
  //            collection <- collection(sale.collectionId)
  //            nftOwners <- nftOwners(collection)
  //            nfts <- nfts(nftOwners.map(_.nftId))
  //            //            nftProperties <- nftProperties(nftOwner.nftId)
  //            sellerKey <- sellerKey(collection.creatorId)
  //            (verifyPassword, buyerKey) <- verifyPassword
  //            isWhitelistMember <- isWhitelistMember(sale)
  //            balance <- balance
  //            countBuyerNFTsFromSale <- countBuyerNFTsFromSale
  //            checkAlreadySold <- checkAlreadySold(nftOwners.map(_.nftId))
  //            blockchainTransaction <- validateAndTransfer(nftOwners = nftOwners, verifyPassword = verifyPassword, sale = sale, isWhitelistMember = isWhitelistMember, buyerKey = buyerKey, sellerKey = sellerKey, balance = balance, nfts = nfts, countBuyerNFTsFromSale = countBuyerNFTsFromSale, checkAlreadySold = checkAlreadySold)
  //          } yield PartialContent(views.html.blockchainTransaction.transactionSuccessful(blockchainTransaction))
  //            ).recover {
  //            case baseException: BaseException => BadRequest(views.html.sale.buySaleNFT(BuySaleNFT.form.withGlobalError(baseException.failure.message), saleId = buySaleNFTData.saleId))
  //          }
  //        }
  //      )
  //  }

}
