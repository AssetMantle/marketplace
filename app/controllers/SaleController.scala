package controllers

import controllers.actions._
import exceptions.BaseException
import models.analytics.{CollectionAnalysis, CollectionsAnalysis}
import models.master._
import models.{blockchain, blockchainTransaction, master, masterTransaction}
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
                                masterTransactionTokenPrices: masterTransaction.TokenPrices,
                                masterWhitelistMembers: master.WhitelistMembers,
                                masterTransactionSaleNFTTransactions: masterTransaction.SaleNFTTransactions,
                                blockchainTransactionNFTSales: blockchainTransaction.NFTSales,
                                utilitiesNotification: utilities.Notification,
                                utilitiesOperations: utilities.Operations,
                              )(implicit executionContext: ExecutionContext) extends AbstractController(messagesControllerComponents) with I18nSupport {

  private implicit val logger: Logger = Logger(this.getClass)

  private implicit val module: String = constants.Module.SALE_CONTROLLER

  implicit val callbackOnSessionTimeout: Call = routes.SaleController.viewCollections()

  def viewCollections(): EssentialAction = cached(req => utilities.Session.getSessionCachingKey(req), constants.CommonConfig.WebAppCacheDuration) {
    withoutLoginActionAsync { implicit loginState =>
      implicit request =>
        Future(Ok(views.html.sale.viewCollections()))
    }
  }

  def collectionsSection(): EssentialAction = cached(req => utilities.Session.getSessionCachingKey(req), constants.CommonConfig.WebAppCacheDuration) {
    withoutLoginActionAsync { implicit loginState =>
      implicit request =>
        val totalCollections = masterSales.Service.total
        (for {
          totalCollections <- totalCollections
        } yield Ok(views.html.sale.collectionsSection(totalCollections))
          ).recover {
          case baseException: BaseException => BadRequest(baseException.failure.message)
        }
    }
  }

  def collectionsPerPage(pageNumber: Int): EssentialAction = cached(req => utilities.Session.getSessionCachingKey(req), constants.CommonConfig.WebAppCacheDuration) {
    withoutLoginActionAsync { implicit loginState =>
      implicit request =>
        val sales = if (pageNumber < 1) Future(throw new BaseException(constants.Response.INVALID_PAGE_NUMBER))
        else masterSales.Service.getByPageNumber(pageNumber)

        def collections(ids: Seq[String]) = masterCollections.Service.getCollections(ids)

        (for {
          sales <- sales
          collections <- collections(sales.map(_.collectionId))
        } yield Ok(views.html.sale.collectionsPerPage(collections))
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
        } yield Ok(views.html.sale.viewCollection(collection))
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
        } yield Ok(views.html.sale.collection.collectionNFTs(collection, nfts))
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

        def getTotalWhitelistSaleSold(saleId: String): Future[Long] = masterTransactionSaleNFTTransactions.Service.getTotalWhitelistSaleSold(saleId).map(_.toLong)

        val getSalesInfo = {
          val sale = masterSales.Service.tryGetSaleByCollectionId(id)

          def isMember(whitelistId: String) = if (optionalLoginState.isDefined) masterWhitelistMembers.Service.isMember(whitelistId, optionalLoginState.get.username) else Future(false)

          for {
            sale <- sale
            isMember <- isMember(sale.whitelistId)
          } yield (sale, isMember)
        }

        (for {
          collectionAnalysis <- collectionAnalysis
          collection <- collection
          (sale, isMember) <- getSalesInfo
          totalWhitelistSaleSold <- getTotalWhitelistSaleSold(sale.id)
        } yield Ok(views.html.sale.collection.topRightCard(collectionAnalysis, collection, sale, isMember, totalWhitelistSaleSold, masterTransactionTokenPrices.Service.getLatestPrice))
          ).recover {
          case baseException: BaseException => InternalServerError(baseException.failure.message)
        }
    }
  }

  def createCollectionSaleForm(whitelistId: Option[String], collectionId: Option[String]): Action[AnyContent] = withLoginActionAsync { implicit loginState =>
    implicit request =>
      val whitelists = masterWhitelists.Service.getIdNameMapForOwner(loginState.username)
      val collections = masterCollections.Service.getByCreator(loginState.username)

      for {
        whitelists <- whitelists
        collections <- collections
      } yield if ((whitelistId.isDefined && whitelists.exists(_._1 == whitelistId.get)) || (collectionId.isDefined && collections.exists(_._1 == collectionId.get))) Ok(views.html.sale.createCollectionSale(collections = collections.map(x => x._1 -> x._2).toMap, collectionId = collectionId, whitelistId = whitelistId, whitelists = whitelists))
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
          } yield BadRequest(views.html.sale.createCollectionSale(formWithErrors, whitelists = whitelists, whitelistId = formWithErrors.data.get(constants.FormField.SELECT_WHITELIST_ID.name), collectionId = formWithErrors.data.get(constants.FormField.SELECT_COLLECTION_ID.name), collections = collections.map(x => x._1 -> x._2).toMap))
        },
        createData => {
          val collection = masterCollections.Service.tryGet(id = createData.collectionId)
          val whitelistMembers = masterWhitelistMembers.Service.getAllMembers(createData.whitelistId)
          val collectionAnalysis = collectionsAnalysis.Service.tryGet(createData.collectionId)
          val saleExistOnCollection = masterSales.Service.getSaleByCollectionId(createData.collectionId).map(_.nonEmpty)
          val unmintedNFTs = masterNFTs.Service.getUnmintedNFTs(createData.collectionId)

          def countNFts(unmintedNFTs: Seq[String]) = masterNFTOwners.Service.countForCreatorForPrimarySale(collectionId = createData.collectionId, creatorId = loginState.username, unmintedNFTs = unmintedNFTs)

          def addToSale(collection: Collection, countNFts: Int, saleExistOnCollection: Boolean, collectionAnalysis: CollectionAnalysis, unmintedNFTs: Seq[NFT]) = {
            val maxSellNumber: Int = if (collectionAnalysis.totalNFTs <= 50) collectionAnalysis.totalNFTs.toInt else collectionAnalysis.totalNFTs.toInt / 10
            val errors = Seq(
              if (!loginState.isGenesisCreator) Option(constants.Response.NOT_GENESIS_CREATOR) else None,
              if (collection.creatorId != loginState.username) Option(constants.Response.NOT_COLLECTION_OWNER) else None,
              if (!collection.public) Option(constants.Response.COLLECTION_NOT_PUBLIC) else None,
              if (saleExistOnCollection) Option(constants.Response.CANNOT_CREATE_MORE_THAN_ONE_SALE) else None,
              if (createData.nftForSale > maxSellNumber.min(countNFts)) Option(constants.Response.CANNOT_SELL_MORE_THAN_ALLOWED_LIMIT) else None,
              if (createData.nftForSale > countNFts) Option(constants.Response.NOT_ENOUGH_NFTS_IN_COLLECTION) else None,
            ).flatten
            if (errors.isEmpty) {
              for {
                saleId <- masterSales.Service.add(createData.toNewSale)
                _ <- masterNFTOwners.Service.whitelistSaleRandomNFTs(collectionId = collection.id, nfts = createData.nftForSale, creatorId = loginState.username, saleId = saleId, unmintedNFTs = unmintedNFTs.map(_.id))
              } yield ()
            } else errors.head.throwFutureBaseException()
          }

          def sendNotifications(whitelistMembers: Seq[String], collectionName: String): Unit = whitelistMembers.foreach { member =>
            utilitiesNotification.send(accountID = member, constants.Notification.SALE_ON_WHITELIST, collectionName)(s"'$member', '${constants.View.WHITELIST}'")
          }

          (for {
            collection <- collection
            whitelistMembers <- whitelistMembers
            saleExistOnCollection <- saleExistOnCollection
            collectionAnalysis <- collectionAnalysis
            unmintedNFTs <- unmintedNFTs
            countNFts <- countNFts(unmintedNFTs.map(_.id))
            _ <- addToSale(collection = collection, countNFts = countNFts, saleExistOnCollection = saleExistOnCollection, collectionAnalysis = collectionAnalysis, unmintedNFTs = unmintedNFTs)
            _ <- collectionsAnalysis.Utility.onCreateSale(collection.id, totalListed = createData.nftForSale, salePrice = createData.price)
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

  def buySaleNFTForm(saleId: String): EssentialAction = cached(req => utilities.Session.getSessionCachingKey(req), constants.CommonConfig.WebAppCacheDuration) {
    withoutLoginActionAsync { implicit loginState =>
      implicit request =>
        val sale = masterSales.Service.tryGet(saleId)

        def collection(id: String) = masterCollections.Service.tryGet(id)

        (for {
          sale <- sale
          collection <- collection(sale.collectionId)
        } yield Ok(views.html.sale.buySaleNFT(sale = sale, collection = collection))
          ).recover {
          case _: Exception => BadRequest
        }
    }
  }

  def buySaleNFT(): Action[AnyContent] = withLoginActionAsync { implicit loginState =>
    implicit request =>
      BuySaleNFT.form.bindFromRequest().fold(
        formWithErrors => {
          val sale = masterSales.Service.tryGet(formWithErrors.data.getOrElse(constants.FormField.SALE_ID.name, ""))

          def collection(id: String) = masterCollections.Service.tryGet(id)

          (for {
            sale <- sale
            collection <- collection(sale.collectionId)
          } yield BadRequest(views.html.sale.buySaleNFT(formWithErrors, sale = sale, collection = collection))
            ).recover {
            case _: Exception => BadRequest
          }
        },
        buySaleNFTData => {
          val sale = masterSales.Service.tryGet(buySaleNFTData.saleId)
          val verifyPassword = masterKeys.Service.validateActiveKeyUsernamePasswordAndGet(username = loginState.username, password = buySaleNFTData.password)
          val balance = blockchainBalances.Service.getTokenBalance(loginState.address)
          val countBuyerNFTsFromSale = masterTransactionSaleNFTTransactions.Service.countBuyerNFTsFromSale(buyerAccountId = loginState.username, saleId = buySaleNFTData.saleId)

          def collection(id: String) = masterCollections.Service.tryGet(id)

          def nftOwners(collection: Collection) = masterNFTOwners.Service.getRandomNFTsBySaleId(saleId = buySaleNFTData.saleId, take = buySaleNFTData.buyNFTs, creatorId = collection.creatorId)

          def checkAlreadySold(nftIds: Seq[String]) = masterTransactionSaleNFTTransactions.Service.checkAlreadySold(saleId = buySaleNFTData.saleId, nftIds = nftIds)

          def nfts(nftIds: Seq[String]) = masterNFTs.Service.getByIds(nftIds)

          //          def nftProperties(nftId: String) = masterNFTProperties.Service.getForNFT(nftId)

          def sellerKey(ownerId: String) = masterKeys.Service.tryGetActive(ownerId)

          def isWhitelistMember(sale: Sale) = masterWhitelistMembers.Service.isMember(whitelistId = sale.whitelistId, accountId = loginState.username)

          def validateAndTransfer(nftOwners: Seq[NFTOwner], verifyPassword: Boolean, sale: Sale, isWhitelistMember: Boolean, buyerKey: Key, sellerKey: Key, balance: MicroNumber, nfts: Seq[NFT], countBuyerNFTsFromSale: Int, checkAlreadySold: Boolean) = {
            val errors = Seq(
              if (nftOwners.map(_.ownerId).distinct.contains(loginState.username)) Option(constants.Response.CANNOT_SELL_TO_YOURSELF) else None,
              if (!isWhitelistMember) Option(constants.Response.NOT_MEMBER_OF_WHITELIST) else None,
              if (sale.startTimeEpoch > utilities.Date.currentEpoch) Option(constants.Response.SALE_NOT_STARTED) else None,
              if (utilities.Date.currentEpoch >= sale.endTimeEpoch) Option(constants.Response.SALE_EXPIRED) else None,
              if (balance == MicroNumber.zero || balance <= (sale.price * buySaleNFTData.buyNFTs)) Option(constants.Response.INSUFFICIENT_BALANCE) else None,
              //              if (buySaleNFTData.mintNFT && (balance - (sale.price + constants.Blockchain.AssetPropertyRate * (nftProperties.length + constants.Collection.DefaultProperty.list.length)) <= MicroNumber.zero)) Option(constants.Response.INSUFFICIENT_BALANCE) else None,
              if ((countBuyerNFTsFromSale + buySaleNFTData.buyNFTs) > sale.maxMintPerAccount) Option(constants.Response.MAXIMUM_NFT_MINT_PER_ACCOUNT_REACHED) else None,
              //              if (buySaleNFTData.mintNFT && nft.isMinted) Option(constants.Response.NFT_ALREADY_MINTED) else None,
              if (nfts.exists(_.isMinted.getOrElse(true))) Option(constants.Response.NFT_ALREADY_MINTED) else None,
              if (!verifyPassword) Option(constants.Response.INVALID_PASSWORD) else None,
              if (checkAlreadySold) Option(constants.Response.NFT_ALREADY_SOLD) else None,
            ).flatten
            if (errors.isEmpty) {
              masterTransactionSaleNFTTransactions.Utility.transaction(
                buyerAccountId = loginState.username,
                sellerAccountId = sellerKey.accountId,
                saleId = sale.id,
                mintOnSuccess = buySaleNFTData.mintOnSuccess,
                nftIds = nftOwners.map(_.nftId),
                fromAddress = buyerKey.address,
                toAddress = sellerKey.address,
                amount = sale.price * buySaleNFTData.buyNFTs,
                gasLimit = constants.Blockchain.DefaultSendCoinGasAmount,
                gasPrice = constants.Blockchain.DefaultGasPrice,
                ecKey = ECKey.fromPrivate(utilities.Secrets.decryptData(buyerKey.encryptedPrivateKey, buySaleNFTData.password))
              )
            } else errors.head.throwFutureBaseException()
          }

          (for {
            sale <- sale
            collection <- collection(sale.collectionId)
            nftOwners <- nftOwners(collection)
            nfts <- nfts(nftOwners.map(_.nftId))
            //            nftProperties <- nftProperties(nftOwner.nftId)
            sellerKey <- sellerKey(collection.creatorId)
            (verifyPassword, buyerKey) <- verifyPassword
            isWhitelistMember <- isWhitelistMember(sale)
            balance <- balance
            countBuyerNFTsFromSale <- countBuyerNFTsFromSale
            checkAlreadySold <- checkAlreadySold(nftOwners.map(_.nftId))
            blockchainTransaction <- validateAndTransfer(nftOwners = nftOwners, verifyPassword = verifyPassword, sale = sale, isWhitelistMember = isWhitelistMember, buyerKey = buyerKey, sellerKey = sellerKey, balance = balance, nfts = nfts, countBuyerNFTsFromSale = countBuyerNFTsFromSale, checkAlreadySold = checkAlreadySold)
          } yield {
            val tweetURI = if (collection.getTwitterUsername.isDefined) Option(s"https://twitter.com/intent/tweet?text=Just bought ${collection.name} NFTs at a huge discount via Launchpad Sale on MantlePlace. @${collection.getTwitterUsername.get} @AssetMantle Check here &url=https://marketplace.assetmantle.one/collection/${collection.id}&hashtags=NFT") else None
            PartialContent(views.html.blockchainTransaction.transactionSuccessful(blockchainTransaction, tweetURI))
          }
            ).recover {
            case baseException: BaseException => {
              val badResult = {
                val sale = masterSales.Service.tryGet(buySaleNFTData.saleId)

                def collection(id: String) = masterCollections.Service.tryGet(id)

                (for {
                  sale <- sale
                  collection <- collection(sale.collectionId)
                } yield BadRequest(views.html.sale.buySaleNFT(BuySaleNFT.form.withGlobalError(baseException.failure.message), sale = sale, collection = collection))
                  ).recover {
                  case _: Exception => BadRequest
                }
              }
              Await.result(badResult, Duration.Inf)
            }
          }
        }
      )
  }

}
