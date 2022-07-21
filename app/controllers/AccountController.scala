package controllers

import controllers.actions._
import controllers.result.WithUsernameToken
import exceptions.BaseException
import models.{master, masterTransaction}
import play.api.Logger
import play.api.cache.Cached
import play.api.i18n.I18nSupport
import play.api.mvc.{AbstractController, Action, AnyContent, MessagesControllerComponents}
import service.UploadCollections
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
                                   //                                   masterWallets: master.Wallets,
                                   masterKeys: master.Keys,
                                   masterTransactionSessionTokens: masterTransaction.SessionTokens,
                                   masterTransactionPushNotificationTokens: masterTransaction.PushNotificationTokens,
                                   withUsernameToken: WithUsernameToken,
                                   uploadCollections: UploadCollections,
                                   withLoginActionAsync: WithLoginActionAsync,
                                 )(implicit executionContext: ExecutionContext) extends AbstractController(messagesControllerComponents) with I18nSupport {

  private implicit val logger: Logger = Logger(this.getClass)

  private implicit val module: String = constants.Module.ACCOUNT_CONTROLLER

  def signUpForm(): Action[AnyContent] = withoutLoginAction { implicit request =>
    Ok(views.html.account.signUp())
  }

  def signUp: Action[AnyContent] = withoutLoginActionAsync { implicit loginState =>
    implicit request =>
      SignUp.form.bindFromRequest().fold(
        formWithErrors => {
          Future(BadRequest(views.html.account.signUp(formWithErrors)))
        },
        signUpData => {
          val addAccount = masterAccounts.Service.add(username = signUpData.username, language = request.lang, accountType = constants.User.USER)
          val wallet = utilities.Wallet.getRandomWallet

          def addKey() = masterKeys.Service.add(
            accountId = signUpData.username,
            address = wallet.address,
            hdPath = wallet.hdPath,
            partialMnemonics = wallet.mnemonics.take(wallet.mnemonics.length - constants.Blockchain.MnemonicShown),
            name = constants.Key.DEFAULT_NAME,
            retryCounter = 0,
            active = true,
            backupUsed = false,
            verified = None)

          (for {
            _ <- addAccount
            _ <- addKey()
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

  def verifyWalletMnemonics: Action[AnyContent] = withoutLoginActionAsync { implicit loginState =>
    implicit request =>
      VerifyMnemonics.form.bindFromRequest().fold(
        formWithErrors => {
          Future(BadRequest(views.html.account.verifyWalletMnemonics(formWithErrors, formWithErrors.get.username, formWithErrors.get.walletAddress, Seq(formWithErrors.get.seed1, formWithErrors.get.seed2, formWithErrors.get.seed3, formWithErrors.get.seed4))))
        },
        walletMnemonicsData => {
          val key = masterKeys.Service.tryGetActive(walletMnemonicsData.username)

          def updateAndGetResult(key: master.Key) = if (key.partialMnemonics.isDefined) {
            val mnemonics = key.partialMnemonics.get ++ Seq(walletMnemonicsData.seed1, walletMnemonicsData.seed2, walletMnemonicsData.seed3, walletMnemonicsData.seed4)
            val wallet = utilities.Wallet.getWallet(mnemonics)
            if (wallet.address == walletMnemonicsData.walletAddress && key.address == walletMnemonicsData.walletAddress && key.accountId == walletMnemonicsData.username) {
              val updateWallet = masterKeys.Service.updateOnVerifyMnemonics(key, password = walletMnemonicsData.password, privateKey = wallet.privateKey)
              for {
                _ <- updateWallet
              } yield PartialContent(views.html.account.walletSuccess(username = key.accountId, address = walletMnemonicsData.walletAddress))
            } else constants.Response.INVALID_MNEMONICS_OR_USERNAME.throwFutureBaseException()
          } else constants.Response.INVALID_ACTIVE_WALLET.throwFutureBaseException()

          (for {
            key <- key
            result <- updateAndGetResult(key)
          } yield result
            ).recover {
            case baseException: BaseException => BadRequest(views.html.account.verifyWalletMnemonics(walletMnemonicsForm = VerifyMnemonics.form.withGlobalError(baseException.failure.message), username = walletMnemonicsData.username, address = walletMnemonicsData.walletAddress, partialMnemonics = Seq(walletMnemonicsData.seed1, walletMnemonicsData.seed2, walletMnemonicsData.seed3, walletMnemonicsData.seed4)))
          }
        }
      )
  }

  def signInForm(): Action[AnyContent] = withoutLoginAction { implicit request =>
    Ok(views.html.account.signIn())
  }

  def signIn: Action[AnyContent] = withoutLoginActionAsync { implicit loginState =>
    implicit request =>
      SignIn.form.bindFromRequest().fold(
        formWithErrors => {
          Future(BadRequest(views.html.account.signIn(formWithErrors)))
        },
        signInData => {
          val verifyPasswordAndKey = masterKeys.Service.validateActiveKeyUsernamePasswordAndGet(username = signInData.username, password = signInData.password)

          def getResult(passwordVerified: Boolean, key: master.Key) = if (passwordVerified) {
            if (!key.verified.getOrElse(false)) {
              val wallet = utilities.Wallet.getRandomWallet
              val addKey = masterKeys.Service.add(
                accountId = signInData.username,
                address = wallet.address,
                hdPath = wallet.hdPath,
                partialMnemonics = wallet.mnemonics.take(wallet.mnemonics.length - constants.Blockchain.MnemonicShown),
                name = constants.Key.DEFAULT_NAME,
                retryCounter = 0,
                active = true,
                backupUsed = false,
                verified = None)
              val deleteUnverifiedKey = masterKeys.Service.deleteKey(accountId = key.accountId, address = key.address)
              for {
                _ <- addKey
                _ <- deleteUnverifiedKey
              } yield PartialContent(views.html.account.showWalletMnemonics(username = signInData.username, address = wallet.address, partialMnemonics = wallet.mnemonics.takeRight(constants.Blockchain.MnemonicShown)))
            } else {
              implicit val optionalLoginState: Option[LoginState] = Option(LoginState(username = signInData.username, address = key.address))
              implicit val loginState: LoginState = LoginState(username = signInData.username, address = key.address)
              val pushNotificationTokenUpdate = masterTransactionPushNotificationTokens.Service.upsert(id = loginState.username, token = signInData.pushNotificationToken)

              for {
                _ <- pushNotificationTokenUpdate
                result <- withUsernameToken.Ok(views.html.collection.viewCollections())
              } yield result
            }
          } else constants.Response.INVALID_USERNAME_OR_PASSWORD.throwFutureBaseException()

          (for {
            (passwordVerified, key) <- verifyPasswordAndKey
            result <- getResult(passwordVerified, key)
          } yield result
            ).recover {
            case baseException: BaseException => NotFound(views.html.account.signIn(SignIn.form.withGlobalError(baseException.failure.message)))
          }
        }
      )
  }

  def signOutForm: Action[AnyContent] = withoutLoginAction { implicit request =>
    Ok(views.html.account.signOut())
  }

  def signOut: Action[AnyContent] = withLoginActionAsync { implicit loginState =>
    implicit request =>
      SignOut.form.bindFromRequest().fold(
        formWithErrors => {
          Future(BadRequest(views.html.account.signOut(formWithErrors)))
        },
        signOutData => {
          val pushNotificationTokenDelete = if (!signOutData.receiveNotifications) masterTransactionPushNotificationTokens.Service.deleteByID(loginState.username) else Future()
          val deleteSessionToken = masterTransactionSessionTokens.Service.deleteById(loginState.username)

          (for {
            _ <- pushNotificationTokenDelete
            _ <- deleteSessionToken
          } yield Ok(views.html.index(successes = Seq(constants.Response.LOGGED_OUT))).withNewSession
            ).recover {
            case baseException: BaseException => InternalServerError(views.html.index(failures = Seq(baseException.failure)))
          }
        }
      )
  }

  def checkUsernameAvailable(username: String): Action[AnyContent] = withoutLoginActionAsync { implicit loginState =>
    implicit request =>
      val checkUsernameAvailable = masterAccounts.Service.checkUsernameAvailable(username)
      for {
        checkUsernameAvailable <- checkUsernameAvailable
      } yield if (checkUsernameAvailable) Ok else NoContent
  }

  def forgetPasswordForm(): Action[AnyContent] = withoutLoginAction { implicit request =>
    Ok(views.html.account.forgetPassword())
  }

  def forgetPassword: Action[AnyContent] = withoutLoginActionAsync { implicit loginState =>
    implicit request =>
      ForgotPassword.form.bindFromRequest().fold(
        formWithErrors => {
          Future(BadRequest(views.html.account.forgetPassword(formWithErrors)))
        },
        forgetPasswordData => {
          val lastWords = Seq(forgetPasswordData.phrase1, forgetPasswordData.phrase2, forgetPasswordData.phrase3, forgetPasswordData.phrase4)
          val masterKey = masterKeys.Service.tryGetActive(forgetPasswordData.address)

          def verifyAndUpdate(key: master.Key) = if (key.partialMnemonics.isDefined) {
            if (utilities.Wallet.getWallet(key.partialMnemonics.get ++ lastWords).address == key.address && key.accountId == forgetPasswordData.username) {
              masterAccounts.Service.updateOnForgotPassword(accountID = forgetPasswordData.username, newPassword = forgetPasswordData.newPassword)
            } else constants.Response.UNAUTHORIZED.throwFutureBaseException()
          } else constants.Response.INVALID_ACTIVE_WALLET.throwFutureBaseException()

          (for {
            masterKey <- masterKey
            _ <- verifyAndUpdate(masterKey)
          } yield PartialContent(views.html.account.successfullPasswordChange())
            ).recover {
            case baseException: BaseException => BadRequest(views.html.account.forgetPassword(ForgotPassword.form.withGlobalError(baseException.failure.message)))
          }
        }
      )
  }

}