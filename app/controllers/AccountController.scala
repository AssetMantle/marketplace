package controllers

import controllers.actions.{WithoutLoginAction, WithoutLoginActionAsync}
import exceptions.BaseException
import models.master
import play.api.Logger
import play.api.cache.Cached
import play.api.i18n.I18nSupport
import play.api.mvc.{AbstractController, Action, AnyContent, MessagesControllerComponents}
import views.account.companion._

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class AccountController @Inject()(
                                   messagesControllerComponents: MessagesControllerComponents,
                                   cached: Cached,
                                   withoutLoginActionAsync: WithoutLoginActionAsync,
                                   withoutLoginAction: WithoutLoginAction,
                                   masterAccounts: master.Accounts,
                                   masterWallets: master.Wallets,
                                 )(implicit executionContext: ExecutionContext) extends AbstractController(messagesControllerComponents) with I18nSupport {

  private implicit val logger: Logger = Logger(this.getClass)

  private implicit val module: String = constants.Module.ACCOUNT_CONTROLLER

  def signUpForm(): Action[AnyContent] = withoutLoginAction { implicit request =>
    Ok(views.html.account.signUp())
  }

  def signUp: Action[AnyContent] = withoutLoginActionAsync { implicit request =>
    SignUp.form.bindFromRequest().fold(
      formWithErrors => {
        Future(BadRequest(views.html.account.signUp(formWithErrors)))
      },
      signUpData => {
        val addAccount = masterAccounts.Service.add(username = signUpData.username, password = signUpData.password, language = request.lang, accountType = constants.User.USER)
        val wallet = utilities.Wallet.getRandomWallet

        def addWallet() = masterWallets.Service.add(address = wallet.address, partialMnemonics = wallet.mnemonics.take(wallet.mnemonics.length - constants.Blockchain.MnemonicShown), accountId = signUpData.username, provisioned = None)

        (for {
          _ <- addAccount
          _ <- addWallet()
        } yield PartialContent(views.html.account.showWalletMnemonics(username = signUpData.username, address = wallet.address, partialMnemonics = wallet.mnemonics.takeRight(constants.Blockchain.MnemonicShown)))
          ).recover {
          case baseException: BaseException => BadRequest(views.html.account.signUp(SignUp.form.withGlobalError(baseException.failure.message)))
        }
      }
    )
  }

  def verifyWalletMnemonicsForm(): Action[AnyContent] = withoutLoginAction { implicit request =>
    BadRequest
  }

  def verifyWalletMnemonics: Action[AnyContent] = withoutLoginActionAsync { implicit request =>
    WalletMnemonics.form.bindFromRequest().fold(
      formWithErrors => {
        Future(BadRequest(views.html.account.verifyWalletMnemonics(formWithErrors, formWithErrors.get.username, formWithErrors.get.walletAddress, Seq(formWithErrors.get.seed1, formWithErrors.get.seed2, formWithErrors.get.seed3, formWithErrors.get.seed4))))
      },
      walletMnemonicsData => {
        val wallet = masterWallets.Service.tryGet(walletMnemonicsData.walletAddress)

        (for {
          wallet <- wallet
        } yield {
          val mnemonics = wallet.partialMnemonics ++ Seq(walletMnemonicsData.seed1, walletMnemonicsData.seed2, walletMnemonicsData.seed3, walletMnemonicsData.seed4)
          if (utilities.Wallet.getWallet(mnemonics).address == walletMnemonicsData.walletAddress && wallet.address == walletMnemonicsData.walletAddress && wallet.accountId == walletMnemonicsData.username) {
            PartialContent(views.html.account.walletSuccess(username = wallet.accountId, address = walletMnemonicsData.walletAddress))
          } else throw new BaseException(constants.Response.ACCOUNT_NOT_FOUND)
        }
          ).recover {
          case baseException: BaseException => NotFound(views.html.account.verifyWalletMnemonics(walletMnemonicsForm = WalletMnemonics.form.withGlobalError(baseException.failure.message), username = walletMnemonicsData.username, address = walletMnemonicsData.walletAddress, partialMnemonics = Seq(walletMnemonicsData.seed1, walletMnemonicsData.seed2, walletMnemonicsData.seed3, walletMnemonicsData.seed4)))
        }
      }
    )
  }

  def signInForm(): Action[AnyContent] = withoutLoginAction { implicit request =>
    Ok(views.html.account.signIn())
  }

  def signIn: Action[AnyContent] = withoutLoginActionAsync { implicit request =>
    SignIn.form.bindFromRequest().fold(
      formWithErrors => {
        Future(BadRequest(views.html.account.signIn(formWithErrors)))
      },
      signInData => {
        Future(Ok(views.html.collections()))
      }
    )
  }

  def checkUsernameAvailable(username: String): Action[AnyContent] = withoutLoginActionAsync { implicit request =>
    val checkUsernameAvailable = masterAccounts.Service.checkUsernameAvailable(username)
    for {
      checkUsernameAvailable <- checkUsernameAvailable
    } yield if (checkUsernameAvailable) Ok else NoContent
  }


}