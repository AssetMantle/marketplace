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
                                   masterAccounts: master.Accounts
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
        val addAccount = masterAccounts.Service.create(username = signUpData.username, password = signUpData.password, language = request.lang, userType = constants.User.USER)

        (for {
          _ <- addAccount
        } yield Ok(views.html.index(successes = Seq(constants.Response.SIGN_UP_SUCCESSFUL)))
          ).recover {
          case baseException: BaseException => BadRequest(views.html.account.signUp(SignUp.form.withGlobalError(baseException.failure.message)))
        }
      }
    )
  }

  def createWalletForm(): Action[AnyContent] = withoutLoginAction { implicit request =>
    Ok(views.html.account.createWallet())
  }

  def createWalletSeedPhraseForm(): Action[AnyContent] = withoutLoginAction { implicit request =>
    Ok(views.html.account.createWalletSeedPhrase())
  }

  def createWalletSeedPhrase: Action[AnyContent] = withoutLoginActionAsync { implicit request =>
    CreateWalletSeedPhrase.form.bindFromRequest().fold(
      formWithErrors => {
        Future(BadRequest(views.html.account.createWalletSeedPhrase(formWithErrors)))
      },
      createWalletSeedPhraseData => {
        Future(Ok(views.html.index(successes = Seq(constants.Response.SIGN_UP_SUCCESSFUL))))
      }
    )
  }

  def createWalletSuccessForm(): Action[AnyContent] = withoutLoginAction { implicit request =>
    Ok(views.html.account.createWalletSuccess())
  }

  def createWalletErrorForm(): Action[AnyContent] = withoutLoginAction { implicit request =>
    Ok(views.html.account.createWalletError())
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
        Future(Ok(views.html.index(successes = Seq(constants.Response.SIGN_UP_SUCCESSFUL))))
      }
    )
  }

  def forgotPasswordUsernameForm(): Action[AnyContent] = withoutLoginAction { implicit request =>
    Ok(views.html.account.forgotPasswordUsername())
  }

  def forgotPasswordUsername: Action[AnyContent] = withoutLoginActionAsync { implicit request =>
    ForgotPasswordUsername.form.bindFromRequest().fold(
      formWithErrors => {
        Future(BadRequest(views.html.account.forgotPasswordUsername(formWithErrors)))
      },
      forgotPasswordUsernameData => {
        Future(Ok(views.html.index(successes = Seq(constants.Response.SIGN_UP_SUCCESSFUL))))
      }
    )
  }

  def forgotPasswordSeedPhraseForm(): Action[AnyContent] = withoutLoginAction { implicit request =>
    Ok(views.html.account.forgotPasswordSeedPhrase())
  }

  def forgotPasswordSeedPhrase: Action[AnyContent] = withoutLoginActionAsync { implicit request =>
    ForgotPasswordSeedPhrase.form.bindFromRequest().fold(
      formWithErrors => {
        Future(BadRequest(views.html.account.forgotPasswordSeedPhrase(formWithErrors)))
      },
      forgotPasswordSeedPhraseData => {
        Future(Ok(views.html.index(successes = Seq(constants.Response.SIGN_UP_SUCCESSFUL))))
      }
    )
  }

  def forgotPasswordNewPasswordForm(): Action[AnyContent] = withoutLoginAction { implicit request =>
    Ok(views.html.account.forgotPasswordNewPassword())
  }

  def forgotPasswordNewPassword: Action[AnyContent] = withoutLoginActionAsync { implicit request =>
    ForgotPasswordNewPassword.form.bindFromRequest().fold(
      formWithErrors => {
        Future(BadRequest(views.html.account.forgotPasswordNewPassword(formWithErrors)))
      },
      forgotPasswordNewPasswordData => {
        Future(Ok(views.html.index(successes = Seq(constants.Response.SIGN_UP_SUCCESSFUL))))
      }
    )
  }

  def forgotPasswordSuccessForm(): Action[AnyContent] = withoutLoginAction { implicit request =>
    Ok(views.html.account.forgotPasswordSuccess())
  }


  // Bootstrap SignUp
  def signUpBootstrapForm(): Action[AnyContent] = withoutLoginAction { implicit request =>
    Ok(views.html.account.signUpBootstrap())
  }

  def signUpBootstrap: Action[AnyContent] = withoutLoginActionAsync { implicit request =>
    SignUpBootstrap.form.bindFromRequest().fold(
      formWithErrors => {
        Future(BadRequest(views.html.account.signUpBootstrap(formWithErrors)))
      },
      signUpBootstrapData => {
        Future(Ok(views.html.index(successes = Seq(constants.Response.SIGN_UP_SUCCESSFUL))))
      }
    )
  }
}