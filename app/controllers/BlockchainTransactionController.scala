package controllers

import controllers.actions._
import controllers.result.WithUsernameToken
import exceptions.BaseException
import models.common.Coin
import models.{master, masterTransaction}
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
                                                 blockchainBalances: models.blockchain.Balances,
                                                 blockchainTransactionSendCoins: models.blockchainTransaction.SendCoins,
                                                 masterTransactionTokenPrices: masterTransaction.TokenPrices,
                                                 withUsernameToken: WithUsernameToken,
                                                 withLoginActionAsync: WithLoginActionAsync,
                                               )(implicit executionContext: ExecutionContext) extends AbstractController(messagesControllerComponents) with I18nSupport {

  private implicit val logger: Logger = Logger(this.getClass)

  private implicit val module: String = constants.Module.BLOCKCHAIN_TRANSACTION_CONTROLLER

  implicit val callbackOnSessionTimeout: Call = routes.ProfileController.viewDefaultProfile()

  def gasTokenPrice: EssentialAction = cached.apply(req => req.path, constants.CommonConfig.WebAppCacheDuration) {
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
          val balance = blockchainBalances.Service.get(sendCoinData.fromAddress)
          val validateAndKey = masterKeys.Service.validateUsernamePasswordAndGetKey(username = loginState.username, address = sendCoinData.fromAddress, password = sendCoinData.password)

          def checkBalanceAndBroadcast(balance: Option[models.blockchain.Balance], validatePassword: Boolean, key: master.Key) = if (balance.fold(MicroNumber.zero)(_.coins.find(_.denom == constants.Blockchain.StakingToken).fold(MicroNumber.zero)(_.amount)) == MicroNumber.zero) {
            constants.Response.INSUFFICIENT_BALANCE.throwFutureBaseException()
          } else if (!validatePassword) constants.Response.INVALID_PASSWORD.throwFutureBaseException()
          else {
            blockchainTransactionSendCoins.Utility.transaction(
              accountId = loginState.username,
              fromAddress = sendCoinData.fromAddress,
              toAddress = sendCoinData.toAddress,
              amount = Seq(Coin(denom = constants.Blockchain.StakingToken, amount = sendCoinData.sendCoinAmount)),
              gasLimit = sendCoinData.gasAmount,
              gasPrice = sendCoinData.gasPrice.toDouble,
              ecKey = ECKey.fromPrivate(utilities.Secrets.decryptData(key.encryptedPrivateKey, sendCoinData.password)),
            )
          }


          (for {
            balance <- balance
            (validatePassword, key) <- validateAndKey
            sendCoin <- checkBalanceAndBroadcast(balance, validatePassword, key)
          } yield PartialContent(views.html.blockchainTransaction.transactionSuccessful(broadcasted = sendCoin.broadcasted, status = sendCoin.status, txHash = sendCoin.txHash, log = sendCoin.log.getOrElse("")))
            ).recover {
            case baseException: BaseException => BadRequest(views.html.blockchainTransaction.sendCoin(SendCoin.form.withGlobalError(baseException.failure.message), sendCoinData.fromAddress, sendCoinData.sendCoinAmount))
          }
        }
      )
  }
       def redelegateForm(redelegationAmount: String): Action[AnyContent] = withoutLoginAction { implicit request => 
     Ok(views.html.blockchainTransaction.redelegate(redelegationAmount = redelegationAmount))}



    redelegateData => {

  }
  def checkBalanceAndRedelegate(balance: Option[models.blockchain.Balance], validatePassword: Boolean, key: master.Key) = if (balance.fold(MicroNumber.zero)(_.coins.find(_.denom == constants.Blockchain.StakingToken).fold(MicroNumber.zero)(_.amount)) == MicroNumber.zero) {
            constants.Response.INSUFFICIENT_BALANCE.throwFutureBaseException()
          } else if (!validatePassword) constants.Response.INVALID_PASSWORD.throwFutureBaseException()
  }

  


}