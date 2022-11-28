package controllers

import controllers.actions._
import controllers.result.WithUsernameToken
import exceptions.BaseException
import models.common.Coin
import models.master.{NFT, NFTProperty}
import models.{blockchain, blockchainTransaction, master, masterTransaction}
import org.bitcoinj.core.ECKey
import play.api.Logger
import play.api.cache.Cached
import play.api.i18n.I18nSupport
import play.api.mvc._
import utilities.MicroNumber
import views.blockchainTransaction.companion._

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class BlockchainTransactionController @Inject()(
                                                 messagesControllerComponents: MessagesControllerComponents,
                                                 cached: Cached,
                                                 withoutLoginActionAsync: WithoutLoginActionAsync,
                                                 withoutLoginAction: WithoutLoginAction,
                                                 masterAccounts: master.Accounts,
                                                 masterKeys: master.Keys,
                                                 masterNFTs: master.NFTs,
                                                 masterNFTProperties: master.NFTProperties,
                                                 blockchainBalances: blockchain.Balances,
                                                 blockchainTransactionSendCoins: blockchainTransaction.SendCoins,
                                                 blockchainTransactionMints: blockchainTransaction.Mints,
                                                 masterTransactionTokenPrices: masterTransaction.TokenPrices,
                                                 withUsernameToken: WithUsernameToken,
                                                 withLoginActionAsync: WithLoginActionAsync,
                                               )(implicit executionContext: ExecutionContext) extends AbstractController(messagesControllerComponents) with I18nSupport {

  private implicit val logger: Logger = Logger(this.getClass)

  private implicit val module: String = constants.Module.BLOCKCHAIN_TRANSACTION_CONTROLLER

  implicit val callbackOnSessionTimeout: Call = routes.ProfileController.viewDefaultProfile()

  def gasTokenPrice: EssentialAction = cached(req => utilities.Session.getSessionCachingKey(req), constants.CommonConfig.WebAppCacheDuration) {
    withoutLoginActionAsync { implicit loginState =>
      implicit request =>
        val tokenPrice = masterTransactionTokenPrices.Service.tryGetLatestTokenPrice(constants.Blockchain.StakingToken)
        (for {
          tokenPrice <- tokenPrice
        } yield Ok(tokenPrice.price.toString)
          ).recover {
          case _: BaseException => BadRequest
        }
    }
  }

  def balance(address: String): EssentialAction = cached(req => utilities.Session.getSessionCachingKey(req), constants.CommonConfig.WebAppCacheDuration) {
    withoutLoginActionAsync { implicit loginState =>
      implicit request =>
        val balance = blockchainBalances.Service.getTokenBalance(address)
        (for {
          balance <- balance
        } yield Ok(balance.toString)
          ).recover {
          case _: BaseException => BadRequest("0")
        }
    }
  }

  def sendCoinForm(fromAddress: String): Action[AnyContent] = withoutLoginActionAsync { implicit loginState =>
    implicit request =>
      val balance = blockchainBalances.Service.get(fromAddress)
      (for {
        balance <- balance
      } yield Ok(views.html.blockchainTransaction.sendCoin(fromAddress = fromAddress, balance = balance.fold(MicroNumber.zero)(_.coins.find(_.denom == constants.Blockchain.StakingToken).fold(MicroNumber.zero)(_.amount))))
        ).recover {
        case _: BaseException => Ok(views.html.blockchainTransaction.sendCoin(SendCoin.form.withGlobalError(constants.Response.BALANCE_FETCH_FAILED.message), fromAddress = fromAddress, balance = MicroNumber.zero))
      }
  }

  def sendCoin(): Action[AnyContent] = withLoginActionAsync { implicit loginState =>
    implicit request =>
      SendCoin.form.bindFromRequest().fold(
        formWithErrors => {
          Future(BadRequest(views.html.blockchainTransaction.sendCoin(formWithErrors, formWithErrors.data.getOrElse(constants.FormField.FROM_ADDRESS.name, ""), formWithErrors.data.get(constants.FormField.SEND_COIN_AMOUNT.name).fold(MicroNumber.zero)(MicroNumber(_)))))
        },
        sendCoinData => {
          val balance = blockchainBalances.Service.getTokenBalance(sendCoinData.fromAddress)
          val validateAndKey = masterKeys.Service.validateUsernamePasswordAndGetKey(username = loginState.username, address = sendCoinData.fromAddress, password = sendCoinData.password)

          def validateAndBroadcast(balance: MicroNumber, validatePassword: Boolean, key: master.Key) = {
            val errors = Seq(
              if (balance == MicroNumber.zero || balance <= sendCoinData.sendCoinAmount) Option(constants.Response.INSUFFICIENT_BALANCE) else None,
              if (!validatePassword) Option(constants.Response.INVALID_PASSWORD) else None
            ).flatten
            if (errors.isEmpty) {
              blockchainTransactionSendCoins.Utility.transaction(
                accountId = loginState.username,
                fromAddress = sendCoinData.fromAddress,
                toAddress = sendCoinData.toAddress,
                amount = Seq(Coin(denom = constants.Blockchain.StakingToken, amount = sendCoinData.sendCoinAmount)),
                gasLimit = sendCoinData.gasAmount,
                gasPrice = sendCoinData.gasPrice,
                ecKey = ECKey.fromPrivate(utilities.Secrets.decryptData(key.encryptedPrivateKey, sendCoinData.password)),
                memo = None
              )
            } else errors.head.throwFutureBaseException()
          }

          (for {
            balance <- balance
            (validatePassword, key) <- validateAndKey
            blockchainTransaction <- validateAndBroadcast(balance, validatePassword, key)
          } yield PartialContent(views.html.blockchainTransaction.transactionSuccessful(blockchainTransaction))
            ).recover {
            case baseException: BaseException => BadRequest(views.html.blockchainTransaction.sendCoin(SendCoin.form.withGlobalError(baseException.failure.message), sendCoinData.fromAddress, sendCoinData.sendCoinAmount))
          }
        }
      )
  }

  def mintForm(nftId: String): Action[AnyContent] = withLoginActionAsync { implicit loginState =>
    implicit request =>
      Future(Ok(views.html.blockchainTransaction.mint(nftId = nftId)))
  }

  def mint(): Action[AnyContent] = withLoginActionAsync { implicit loginState =>
    implicit request =>
      Mint.form.bindFromRequest().fold(
        formWithErrors => {
          Future(BadRequest(views.html.blockchainTransaction.mint(formWithErrors, nftId = formWithErrors.data.getOrElse(constants.FormField.NFT_ID.name, ""))))
        },
        mintData => {
          val balance = blockchainBalances.Service.getTokenBalance(loginState.address)
          val validateAndKey = masterKeys.Service.validateUsernamePasswordAndGetKey(username = loginState.username, address = loginState.address, password = mintData.password)
          val nft = masterNFTs.Service.tryGet(mintData.nftId)
          val nftProperties = masterNFTProperties.Service.getForNFT(mintData.nftId)

          def validateAndBroadcast(balance: MicroNumber, validatePassword: Boolean, key: master.Key, nft: NFT, nftProperties: Seq[NFTProperty]) = {
            val errors = Seq(
              if (balance == MicroNumber.zero || balance <= (constants.Blockchain.AssetPropertyRate * (nftProperties.length + constants.Collection.DefaultProperty.list.length))) Option(constants.Response.INSUFFICIENT_BALANCE) else None,
              if (!validatePassword) Option(constants.Response.INVALID_PASSWORD) else None,
              if (nft.isMinted) Option(constants.Response.NFT_ALREADY_MINTED) else None
            ).flatten
            if (errors.isEmpty) {
              blockchainTransactionMints.Utility.transaction(
                fromAccountId = loginState.username,
                fromAddress = loginState.address,
                fromId = loginState.getIdentityId,
                toId = loginState.getIdentityId,
                assetId = "",
                classificationId = "",
                nftId = nft.id,
                gasLimit = mintData.gasAmount,
                gasPrice = mintData.gasPrice,
                ecKey = ECKey.fromPrivate(utilities.Secrets.decryptData(key.encryptedPrivateKey, mintData.password)),
              )
            } else errors.head.throwFutureBaseException()
          }


          (for {
            balance <- balance
            (validatePassword, key) <- validateAndKey
            nft <- nft
            nftProperties <- nftProperties
            blockchainTransaction <- validateAndBroadcast(balance, validatePassword, key, nft, nftProperties)
          } yield PartialContent(views.html.blockchainTransaction.transactionSuccessful(blockchainTransaction))
            ).recover {
            case baseException: BaseException => BadRequest(views.html.blockchainTransaction.mint(Mint.form.withGlobalError(baseException.failure.message), mintData.nftId))
          }
        }
      )
  }

}