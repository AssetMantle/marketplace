package controllers

import controllers.actions._
import exceptions.BaseException
import models.blockchain.Split
import models.common.Coin
import models.master.Key
import models.{blockchain, blockchainTransaction, master, masterTransaction}
import org.bitcoinj.core.ECKey
import play.api.Logger
import play.api.cache.Cached
import play.api.i18n.I18nSupport
import play.api.mvc._
import utilities.MicroNumber
import views.wallet.companion._

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class WalletController @Inject()(
                                  messagesControllerComponents: MessagesControllerComponents,
                                  cached: Cached,
                                  withoutLoginActionAsync: WithoutLoginActionAsync,
                                  withLoginAction: WithLoginAction,
                                  withLoginActionAsync: WithLoginActionAsync,
                                  withoutLoginAction: WithoutLoginAction,
                                  blockchainSplits: blockchain.Splits,
                                  masterAccounts: master.Accounts,
                                  masterNFTs: master.NFTs,
                                  masterNFTProperties: master.NFTProperties,
                                  blockchainBalances: blockchain.Balances,
                                  blockchainTransactionSendCoins: blockchainTransaction.SendCoins,
                                  masterTransactionTokenPrices: masterTransaction.TokenPrices,
                                  masterKeys: master.Keys,
                                  masterTransactionUnwrapTransactions: masterTransaction.UnwrapTransactions,
                                  masterTransactionWrapTransactions: masterTransaction.WrapTransactions,
                                  utilitiesNotification: utilities.Notification,
                                  utilitiesOperations: utilities.Operations,
                                )(implicit executionContext: ExecutionContext) extends AbstractController(messagesControllerComponents) with I18nSupport {

  private implicit val logger: Logger = Logger(this.getClass)

  private implicit val module: String = constants.Module.WALLET_CONTROLLER

  implicit val callbackOnSessionTimeout: Call = routes.ProfileController.viewDefaultProfile()

  def walletPopup(): EssentialAction = cached(req => utilities.Session.getSessionCachingKey(req), constants.CommonConfig.WebAppCacheDuration) {
    withLoginActionAsync { implicit loginState =>
      implicit request =>
        Future(Ok(views.html.base.commonWalletPopup()))
    }
  }

  def walletPopupKeys(): EssentialAction = cached(req => utilities.Session.getSessionCachingKey(req), constants.CommonConfig.WebAppCacheDuration) {
    withLoginActionAsync { implicit loginState =>
      implicit request =>
        val keys = masterKeys.Service.fetchAllForId(loginState.username)
        (for {
          keys <- keys
        } yield Ok(views.html.base.walletPopupKeys(keys))
          ).recover {
          case baseException: BaseException => InternalServerError(baseException.failure.message)
        }
    }
  }

  def gasTokenPrice: EssentialAction = cached(req => utilities.Session.getSessionCachingKey(req), constants.CommonConfig.WebAppCacheDuration) {
    withoutLoginAction { implicit request =>
      Ok(utilities.NumericOperation.formatNumber(masterTransactionTokenPrices.Service.getLatestPrice))
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

  def sendCoinForm(fromAddress: String): Action[AnyContent] = withLoginActionAsync { implicit loginState =>
    implicit request =>
      val balance = blockchainBalances.Service.get(fromAddress)
      (for {
        balance <- balance
      } yield Ok(views.html.wallet.sendCoin(fromAddress = fromAddress, balance = balance.fold(MicroNumber.zero)(_.coins.find(_.denom == constants.Blockchain.StakingToken).fold(MicroNumber.zero)(_.amount))))
        ).recover {
        case _: BaseException => Ok(views.html.wallet.sendCoin(SendCoin.form.withGlobalError(constants.Response.BALANCE_FETCH_FAILED.message), fromAddress = fromAddress, balance = MicroNumber.zero))
      }
  }

  def sendCoin(): Action[AnyContent] = withLoginActionAsync { implicit loginState =>
    implicit request =>
      SendCoin.form.bindFromRequest().fold(
        formWithErrors => {
          Future(BadRequest(views.html.wallet.sendCoin(formWithErrors, formWithErrors.data.getOrElse(constants.FormField.FROM_ADDRESS.name, ""), formWithErrors.data.get(constants.FormField.SEND_COIN_AMOUNT.name).fold(MicroNumber.zero)(MicroNumber(_)))))
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
                gasLimit = constants.Blockchain.DefaultSendCoinGasAmount,
                gasPrice = constants.Blockchain.DefaultGasPrice,
                ecKey = ECKey.fromPrivate(utilities.Secrets.decryptData(key.encryptedPrivateKey, sendCoinData.password)),
              )
            } else errors.head.throwFutureBaseException()
          }

          (for {
            balance <- balance
            (validatePassword, key) <- validateAndKey
            blockchainTransaction <- validateAndBroadcast(balance, validatePassword, key)
          } yield PartialContent(views.html.transactionSuccessful(blockchainTransaction))
            ).recover {
            case baseException: BaseException => BadRequest(views.html.wallet.sendCoin(SendCoin.form.withGlobalError(baseException.failure.message), sendCoinData.fromAddress, sendCoinData.sendCoinAmount))
          }
        }
      )
  }

  def wrappedTokenBalance(): EssentialAction = cached(req => utilities.Session.getSessionCachingKey(req), constants.CommonConfig.WebAppCacheDuration) {
    withLoginActionAsync { implicit loginState =>
      implicit request =>
        val split = blockchainSplits.Service.tryGetByOwnerIDAndOwnableID(ownerId = utilities.Identity.getMantlePlaceIdentityID(loginState.username), ownableID = constants.Blockchain.StakingTokenCoinID)
        (for {
          split <- split
        } yield Ok(s"${utilities.NumericOperation.formatNumber(split.getBalanceAsMicroNumber)} $$MNTL")
          ).recover {
          case _: BaseException => BadRequest("0")
        }
    }
  }

  def wrapCoinForm(): Action[AnyContent] = withLoginActionAsync { implicit loginState =>
    implicit request =>
      val balance = blockchainBalances.Service.get(loginState.address)
      (for {
        balance <- balance
      } yield Ok(views.html.wallet.wrap(balance = balance.fold(MicroNumber.zero)(_.coins.find(_.denom == constants.Blockchain.StakingToken).fold(MicroNumber.zero)(_.amount))))
        ).recover {
        case _: BaseException => Ok(views.html.wallet.wrap(Wrap.form.withGlobalError(constants.Response.BALANCE_FETCH_FAILED.message), balance = MicroNumber.zero))
      }
  }

  def wrapCoin(): Action[AnyContent] = withLoginActionAsync { implicit loginState =>
    implicit request =>
      Wrap.form.bindFromRequest().fold(
        formWithErrors => {
          Future(BadRequest(views.html.wallet.wrap(formWithErrors, formWithErrors.data.get(constants.FormField.SEND_COIN_AMOUNT.name).fold(MicroNumber.zero)(MicroNumber(_)))))
        },
        wrapTokenData => {
          val balance = blockchainBalances.Service.getTokenBalance(loginState.address)
          val verifyPassword = masterKeys.Service.validateActiveKeyUsernamePasswordAndGet(username = loginState.username, password = wrapTokenData.password)

          def validateAndTransfer(balance: MicroNumber, verifyPassword: Boolean, key: Key) = {
            val errors = Seq(
              if (balance <= wrapTokenData.wrapCoinAmount) Option(constants.Response.INSUFFICIENT_BALANCE) else None,
              if (!verifyPassword) Option(constants.Response.INVALID_PASSWORD) else None,
            ).flatten
            if (errors.isEmpty) {
              masterTransactionWrapTransactions.Utility.transaction(
                fromAddress = key.address,
                accountId = loginState.username,
                coin = Coin(denom = constants.Blockchain.StakingToken, amount = wrapTokenData.wrapCoinAmount),
                gasPrice = constants.Blockchain.DefaultGasPrice,
                ecKey = ECKey.fromPrivate(utilities.Secrets.decryptData(key.encryptedPrivateKey, wrapTokenData.password))
              )
            } else errors.head.throwFutureBaseException()
          }

          (for {
            balance <- balance
            (verifyPassword, key) <- verifyPassword
            blockchainTransaction <- validateAndTransfer(balance = balance, verifyPassword = verifyPassword, key = key)
          } yield PartialContent(views.html.transactionSuccessful(blockchainTransaction))
            ).recover {
            case baseException: BaseException => BadRequest(views.html.wallet.wrap(Wrap.form.withGlobalError(baseException.failure.message), wrapTokenData.wrapCoinAmount))
          }
        }
      )
  }

  def unwrapTokenForm(): Action[AnyContent] = withLoginActionAsync { implicit loginState =>
    implicit request =>
      Future(Ok(views.html.wallet.unwrapToken()))
  }

  def unwrapToken(): Action[AnyContent] = withLoginActionAsync { implicit loginState =>
    implicit request =>
      UnwrapToken.form.bindFromRequest().fold(
        formWithErrors => {
          Future(BadRequest(views.html.wallet.unwrapToken(formWithErrors)))
        },
        unwrapTokenData => {
          val split = blockchainSplits.Service.tryGetByOwnerIDAndOwnableID(ownerId = utilities.Identity.getMantlePlaceIdentityID(loginState.username), ownableID = constants.Blockchain.StakingTokenCoinID)
          val verifyPassword = masterKeys.Service.validateActiveKeyUsernamePasswordAndGet(username = loginState.username, password = unwrapTokenData.password)

          def validateAndTransfer(split: Split, verifyPassword: Boolean, key: Key) = {
            val errors = Seq(
              if (!verifyPassword) Option(constants.Response.INVALID_PASSWORD) else None,
            ).flatten
            if (errors.isEmpty) {
              masterTransactionUnwrapTransactions.Utility.transaction(
                fromAddress = key.address,
                accountId = loginState.username,
                amount = split.value,
                ownableId = constants.Blockchain.StakingTokenCoinID,
                gasPrice = constants.Blockchain.DefaultGasPrice,
                ecKey = ECKey.fromPrivate(utilities.Secrets.decryptData(key.encryptedPrivateKey, unwrapTokenData.password))
              )
            } else errors.head.throwFutureBaseException()
          }

          (for {
            split <- split
            (verifyPassword, key) <- verifyPassword
            blockchainTransaction <- validateAndTransfer(split = split, verifyPassword = verifyPassword, key = key)
          } yield PartialContent(views.html.transactionSuccessful(blockchainTransaction))
            ).recover {
            case baseException: BaseException => BadRequest(views.html.wallet.unwrapToken(UnwrapToken.form.withGlobalError(baseException.failure.message)))
          }
        }
      )
  }


}
