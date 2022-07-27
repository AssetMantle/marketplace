package controllers

import controllers.actions._
import controllers.result.WithUsernameToken
import exceptions.BaseException
import models.{master, masterTransaction}
import play.api.Logger
import play.api.cache.Cached
import play.api.i18n.I18nSupport
import play.api.mvc.{AbstractController, Action, AnyContent, MessagesControllerComponents, Result}
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
                                   masterKeys: master.Keys,
                                   masterTransactionSessionTokens: masterTransaction.SessionTokens,
                                   masterTransactionPushNotificationTokens: masterTransaction.PushNotificationTokens,
                                   withUsernameToken: WithUsernameToken,
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
          val addAccount = masterAccounts.Service.upsertOnSignUp(username = signUpData.username, language = request.lang, accountType = constants.User.USER)
          val wallet = utilities.Wallet.getRandomWallet

          def addKey() = masterKeys.Service.addOnSignUp(
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
          Future(BadRequest(views.html.account.verifyWalletMnemonics(formWithErrors, formWithErrors.data.getOrElse(constants.FormField.CONFIRM_USERNAME.name, ""), formWithErrors.data.getOrElse(constants.FormField.WALLET_ADDRESS.name, ""))))
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
          } else constants.Response.INVALID_ACTIVE_KEY.throwFutureBaseException()

          (for {
            key <- key
            result <- updateAndGetResult(key)
          } yield result
            ).recover {
            case baseException: BaseException => BadRequest(views.html.account.verifyWalletMnemonics(walletMnemonicsForm = VerifyMnemonics.form.withGlobalError(baseException.failure.message), username = walletMnemonicsData.username, address = walletMnemonicsData.walletAddress))
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
          val masterAccount = masterAccounts.Service.tryGet(signInData.username)
          val masterWallet = masterWallets.Service.getByAccountId(signInData.username)
          val masterKey = masterKeys.Service.getActiveByAccountId(signInData.username)

          def verifyUpdateAndGetResult(account: master.Account, key: Option[master.Key], wallet: Option[master.Wallet]): Future[Result] = if (key.isEmpty) {
            if (utilities.Secrets.verifyPassword(password = signInData.password, passwordHash = account.passwordHash, salt = account.salt, iterations = account.iterations)) {
              val addKey = masterKeys.Service.addOnMigration(
                accountId = signInData.username,
                address = wallet.get.address,
                hdPath = constants.Blockchain.DefaultHDPath,
                partialMnemonics = wallet.get.partialMnemonics,
                passwordHash = account.passwordHash,
                salt = account.salt,
                iterations = account.iterations,
                name = constants.Key.DEFAULT_NAME,
                retryCounter = 0,
                active = true,
                backupUsed = false,
                verified = wallet.get.verified)

              def updateMasterAccount() = if (account.passwordHash.nonEmpty) masterAccounts.Service.updateAccount(account.copy(passwordHash = Array[Byte](), salt = Array[Byte](), iterations = 0)) else Future()

              for {
                _ <- addKey
                _ <- updateMasterAccount()
              } yield PartialContent(views.html.account.migrateWalletToKey(MigrateWalletToKey.form, username = signInData.username, address = wallet.get.address))
            } else constants.Response.INVALID_USERNAME_OR_PASSWORD.throwFutureBaseException()

          } else {
            val validPassword = utilities.Secrets.verifyPassword(password = signInData.password, passwordHash = key.get.passwordHash, salt = key.get.salt, iterations = key.get.iterations)

            if (validPassword) {
              if (key.get.encryptedPrivateKey.isEmpty) Future(PartialContent(views.html.account.migrateWalletToKey(username = signInData.username, address = key.get.address)))
              else {
                if (!key.get.verified.getOrElse(false)) {
                  val wallet = utilities.Wallet.getRandomWallet
                  val addKey = masterKeys.Service.addOnSignUp(
                    accountId = signInData.username,
                    address = wallet.address,
                    hdPath = wallet.hdPath,
                    partialMnemonics = wallet.mnemonics.take(wallet.mnemonics.length - constants.Blockchain.MnemonicShown),
                    name = constants.Key.DEFAULT_NAME,
                    retryCounter = 0,
                    active = true,
                    backupUsed = false,
                    verified = None)

                  def deleteUnverifiedKey() = masterKeys.Service.deleteKey(accountId = key.get.accountId, address = key.get.address)

                  for {
                    _ <- addKey
                    _ <- deleteUnverifiedKey()
                  } yield PartialContent(views.html.account.showWalletMnemonics(username = signInData.username, address = wallet.address, partialMnemonics = wallet.mnemonics.takeRight(constants.Blockchain.MnemonicShown)))
                } else {
                  implicit val optionalLoginState: Option[LoginState] = Option(LoginState(username = signInData.username, address = key.get.address))
                  implicit val loginState: LoginState = LoginState(username = signInData.username, address = key.get.address)
                  val pushNotificationTokenUpdate = masterTransactionPushNotificationTokens.Service.upsert(id = loginState.username, token = signInData.pushNotificationToken)

                  for {
                    _ <- pushNotificationTokenUpdate
                    result <- withUsernameToken.Ok(views.html.collection.viewCollections())
                  } yield result
                }
              }
            } else constants.Response.INVALID_USERNAME_OR_PASSWORD.throwFutureBaseException()
          }

          (for {
            masterAccount <- masterAccount
            masterWallet <- masterWallet
            masterKey <- masterKey
            result <- verifyUpdateAndGetResult(masterAccount, masterKey, masterWallet)
          } yield result
            ).recover {
            case baseException: BaseException => NotFound(views.html.account.signIn(SignIn.form.withGlobalError(baseException.failure.message)))
          }
        }
      )
  }

  def migrateWalletToKeyForm(): Action[AnyContent] = withoutLoginAction { implicit request =>
    BadRequest
  }

  def migrateWalletToKey: Action[AnyContent] = withoutLoginActionAsync { implicit loginState =>
    implicit request =>
      MigrateWalletToKey.form.bindFromRequest().fold(
        formWithErrors => {
          Future(BadRequest(views.html.account.migrateWalletToKey(formWithErrors, formWithErrors.get.username, formWithErrors.get.walletAddress)))
        },
        migrateWalletToKeyData => {
          val validateAndKey = masterKeys.Service.validateActiveKeyUsernamePasswordAndGet(migrateWalletToKeyData.username, password = migrateWalletToKeyData.password)

          def update(validated: Boolean, key: master.Key) = if (validated) {
            val wallet = Future(utilities.Wallet.getWallet(key.partialMnemonics.get ++ Seq(migrateWalletToKeyData.seed1, migrateWalletToKeyData.seed2, migrateWalletToKeyData.seed3, migrateWalletToKeyData.seed4), hdPath = key.hdPath.getOrElse(constants.Response.HD_PATH_NOT_FOUND.throwBaseException())))

            def updateKey(wallet: utilities.Wallet) = masterKeys.Service.updateOnMigration(key = key, password = migrateWalletToKeyData.password, privateKey = wallet.privateKey)

            def getResult(username: String, address: String) = {
              implicit val optionalLoginState: Option[LoginState] = Option(LoginState(username = username, address = address))
              implicit val loginState: LoginState = LoginState(username = username, address = address)
              withUsernameToken.Ok(views.html.collection.viewCollections())
            }

            for {
              wallet <- wallet
              _ <- updateKey(wallet)
              result <- getResult(username = migrateWalletToKeyData.username, address = wallet.address)
            } yield result
          } else constants.Response.INVALID_USERNAME_OR_PASSWORD.throwFutureBaseException()

          (for {
            (validated, key) <- validateAndKey
            result <- update(validated, key)
          } yield result
            ).recover {
            case baseException: BaseException => BadRequest(views.html.account.migrateWalletToKey(MigrateWalletToKey.form.withGlobalError(baseException.failure.message), migrateWalletToKeyData.username, migrateWalletToKeyData.walletAddress))
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
      val verifiedMnemonicExists = masterKeys.Service.checkVerifiedKeyExists(username)
      for {
        verifiedMnemonicExists <- verifiedMnemonicExists
      } yield if (!verifiedMnemonicExists) Ok else NoContent
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
          val update = masterKeys.Service.updateOnForgotPassword(accountId = forgetPasswordData.username, address = forgetPasswordData.address, lastWords = lastWords, newPassword = forgetPasswordData.newPassword)

          (for {
            _ <- update
          } yield PartialContent(views.html.account.successfullPasswordChange())
            ).recover {
            case baseException: BaseException => BadRequest(views.html.account.forgetPassword(ForgotPassword.form.withGlobalError(baseException.failure.message)))
          }
        }
      )
  }

  def changePasswordForm(): Action[AnyContent] = withoutLoginAction { implicit request =>
    Ok(views.html.account.changePassword())
  }

  def changePassword: Action[AnyContent] = withLoginActionAsync { implicit loginState =>
    implicit request =>
      ChangePassword.form.bindFromRequest().fold(
        formWithErrors => {
          Future(BadRequest(views.html.account.changePassword(formWithErrors)))
        },
        changePasswordData => {
          val changePassword = masterKeys.Service.changePassword(accountId = loginState.username, address = loginState.address, oldPassword = changePasswordData.oldPassword, newPassword = changePasswordData.newPassword)
          (for {
            _ <- changePassword
          } yield PartialContent(views.html.account.successfullPasswordChange())
            ).recover {
            case baseException: BaseException => BadRequest(views.html.account.changePassword(ChangePassword.form.withGlobalError(baseException.failure.message)))
          }
        }
      )
  }

  def changeActiveKey(address: String): Action[AnyContent] = withLoginActionAsync { implicit loginState =>
    implicit request =>
      val changeActive = masterKeys.Service.changeActive(accountId = loginState.username, oldAddress = loginState.address, newAddress = address)
      (for {
        _ <- changeActive
      } yield Ok
        ).recover {
        case _: BaseException => NoContent
      }
  }

}