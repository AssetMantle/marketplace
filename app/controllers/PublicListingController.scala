package controllers

import controllers.actions._
import exceptions.BaseException
import models.analytics.CollectionsAnalysis
import models.master._
import models.{blockchain, blockchainTransaction, master, masterTransaction}
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
                                         blockchainTransactionNFTPublicListings: blockchainTransaction.NFTPublicListings,
                                         masterNFTOwners: master.NFTOwners,
                                         masterNFTProperties: master.NFTProperties,
                                         masterTransactionPublicListingNFTTransactions: masterTransaction.PublicListingNFTTransactions,
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
          val totalNFTs = masterNFTOwners.Service.countOwnedAndCreatedNFTsForCollection(accountId = loginState.username, collectionId = createData.collectionId)
          val countNFts = masterNFTOwners.Service.countForCreatorNotForSell(collectionId = createData.collectionId, creatorId = loginState.username)

          def addToPublicListing(countNFts: Int, collection: Collection, totalNFTs: Int) = {
            val maxSellNumber: Int = if (totalNFTs <= 50) totalNFTs else totalNFTs / 10
            val errors = Seq(
              if (!loginState.isGenesisCreator) Option(constants.Response.NOT_GENESIS_CREATOR) else None,
              if (collection.creatorId != loginState.username) Option(constants.Response.NOT_COLLECTION_OWNER) else None,
              if (!collection.public) Option(constants.Response.COLLECTION_NOT_PUBLIC) else None,
              if (createData.numberOfNFTs > maxSellNumber) Option(constants.Response.CANNOT_SELL_MORE_THAN_ALLOWED_LIMIT) else None,
              if (createData.numberOfNFTs > countNFts) Option(constants.Response.NOT_ENOUGH_NFTS_IN_COLLECTION) else None,
            ).flatten
            if (errors.isEmpty) {
              for {
                publicListingId <- masterPublicListings.Service.add(createData.toNewPublicListing)
                _ <- masterNFTOwners.Service.publicListRandomNFTs(collectionId = collection.id, nfts = createData.numberOfNFTs, creatorId = loginState.username, publicListingId = publicListingId)
              } yield ()
            } else errors.head.throwFutureBaseException()
          }

          def sendNotification(collectionName: String) = utilitiesNotification.send(loginState.username, notification = constants.Notification.PUBLIC_LISTING_ON_COLLECTION, collectionName)(s"'${createData.collectionId}'")

          (for {
            collection <- collection
            countNFts <- countNFts
            totalNFTs <- totalNFTs
            _ <- addToPublicListing(countNFts = countNFts, collection = collection, totalNFTs = totalNFTs)
            _ <- sendNotification(collection.name)
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

  def buyNFTForm(publicListingId: String, mintNft: Boolean): Action[AnyContent] = withoutLoginAction { implicit request =>
    Ok(views.html.publicListing.buyNFT(publicListingId = publicListingId))
  }

  def buyNFT(): Action[AnyContent] = withLoginActionAsync { implicit loginState =>
    implicit request =>
      BuyNFT.form.bindFromRequest().fold(
        formWithErrors => {
          Future(BadRequest(views.html.publicListing.buyNFT(formWithErrors, publicListingId = formWithErrors.data.getOrElse(constants.FormField.PUBLIC_LISTING_ID.name, ""))))
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

          //          def nftProperties(nftId: String) = masterNFTProperties.Service.getForNFT(nftId)

          def sellerKey(ownerId: String) = masterKeys.Service.tryGetActive(ownerId)

          def validateAndTransfer(nftOwners: Seq[NFTOwner], verifyPassword: Boolean, publicListing: PublicListing, buyerKey: Key, sellerKey: Key, balance: MicroNumber, nfts: Seq[NFT], countBuyerNFTsFromPublicListing: Int, checkAlreadySold: Boolean) = {
            val errors = Seq(
              if (nftOwners.map(_.ownerId).distinct.contains(loginState.username)) Option(constants.Response.CANNOT_SELL_TO_YOURSELF) else None,
              if (publicListing.startTimeEpoch > utilities.Date.currentEpoch) Option(constants.Response.EARLY_ACCESS_NOT_STARTED) else None,
              if (utilities.Date.currentEpoch >= publicListing.endTimeEpoch) Option(constants.Response.EARLY_ACCESS_ENDED) else None,
              if (balance == MicroNumber.zero || balance <= (publicListing.price * buyNFTData.buyNFTs)) Option(constants.Response.INSUFFICIENT_BALANCE) else None,
              //              if (buyNFTData.mintNFT && (balance - (publicListing.price + constants.Blockchain.AssetPropertyRate * (nftProperties.length + constants.Collection.DefaultProperty.list.length)) <= MicroNumber.zero)) Option(constants.Response.INSUFFICIENT_BALANCE) else None,
              if ((countBuyerNFTsFromPublicListing + buyNFTData.buyNFTs) > publicListing.maxMintPerAccount) Option(constants.Response.MAXIMUM_NFT_MINT_PER_ACCOUNT_REACHED) else None,
              //              if (buyNFTData.mintNFT && nft.isMinted) Option(constants.Response.NFT_ALREADY_MINTED) else None,
              if (nfts.exists(_.isMinted)) Option(constants.Response.NFT_ALREADY_MINTED) else None,
              if (!verifyPassword) Option(constants.Response.INVALID_PASSWORD) else None,
              if (checkAlreadySold) Option(constants.Response.NFT_ALREADY_SOLD) else None,
            ).flatten
            if (errors.isEmpty) {
              masterTransactionPublicListingNFTTransactions.Utility.transaction(
                buyerAccountId = loginState.username,
                sellerAccountId = sellerKey.accountId,
                publicListingId = publicListing.id,
                nftIds = nftOwners.map(_.nftId),
                fromAddress = buyerKey.address,
                toAddress = sellerKey.address,
                amount = publicListing.price * buyNFTData.buyNFTs,
                gasLimit = buyNFTData.gasAmount,
                gasPrice = buyNFTData.gasPrice,
                ecKey = ECKey.fromPrivate(utilities.Secrets.decryptData(buyerKey.encryptedPrivateKey, buyNFTData.password))
              )
            } else errors.head.throwFutureBaseException()
          }

          (for {
            publicListing <- publicListing
            collection <- collection(publicListing.collectionId)
            nftOwners <- nftOwners(collection)
            nfts <- nfts(nftOwners.map(_.nftId))
            //            nftProperties <- nftProperties(nftOwner.nftId)
            sellerKey <- sellerKey(collection.creatorId)
            (verifyPassword, buyerKey) <- verifyPassword
            balance <- balance
            countBuyerNFTsFromPublicListing <- countBuyerNFTsFromPublicListing
            checkAlreadySold <- checkAlreadySold(nftOwners.map(_.nftId))
            blockchainTransaction <- validateAndTransfer(nftOwners = nftOwners, verifyPassword = verifyPassword, publicListing = publicListing, buyerKey = buyerKey, sellerKey = sellerKey, balance = balance, nfts = nfts, countBuyerNFTsFromPublicListing = countBuyerNFTsFromPublicListing, checkAlreadySold = checkAlreadySold)
          } yield PartialContent(views.html.blockchainTransaction.transactionSuccessful(blockchainTransaction))
            ).recover {
            case baseException: BaseException => BadRequest(views.html.publicListing.buyNFT(BuyNFT.form.withGlobalError(baseException.failure.message), publicListingId = buyNFTData.publicListingId))
          }
        }
      )
  }

}
