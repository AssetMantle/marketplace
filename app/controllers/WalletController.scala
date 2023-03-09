package controllers

import controllers.actions._
import exceptions.BaseException
import models.blockchain.Split
import models.master.Key
import models.{blockchain, master, masterTransaction}
import org.bitcoinj.core.ECKey
import play.api.Logger
import play.api.cache.Cached
import play.api.i18n.I18nSupport
import play.api.mvc._
import views.wallet.companion.UnwrapToken

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
                                  masterKeys: master.Keys,
                                  masterTransactionUnwrapTransactions: masterTransaction.UnwrapTransactions,
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
          } yield PartialContent(views.html.blockchainTransaction.transactionSuccessful(blockchainTransaction))
            ).recover {
            case baseException: BaseException => BadRequest(views.html.wallet.unwrapToken(UnwrapToken.form.withGlobalError(baseException.failure.message)))
          }
        }
      )
  }


}
