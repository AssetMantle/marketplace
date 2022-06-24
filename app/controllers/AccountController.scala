package controllers

import controllers.actions.{WithoutLoginAction, WithoutLoginActionAsync}
import exceptions.BaseException
import models.{master}
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
        } yield PartialContent(views.html.account.createWalletSuccess(address = wallet.address, partialMnemonics = wallet.mnemonics.takeRight(constants.Blockchain.MnemonicShown)))
          ).recover {
          case baseException: BaseException => BadRequest(views.html.account.signUp(SignUp.form.withGlobalError(baseException.failure.message)))
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
        Future(Ok(views.html.index()))
      }
    )
  }


}