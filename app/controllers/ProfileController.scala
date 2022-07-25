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
import views.profile.companion._
import play.api.mvc._

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ProfileController @Inject()(
                                   messagesControllerComponents: MessagesControllerComponents,
                                   cached: Cached,
                                   withoutLoginActionAsync: WithoutLoginActionAsync,
                                   withoutLoginAction: WithoutLoginAction,
                                   masterAccounts: master.Accounts,
                                   masterKeys: master.Keys,
                                   masterTransactionSessionTokens: masterTransaction.SessionTokens,
                                   masterTransactionPushNotificationTokens: masterTransaction.PushNotificationTokens,
                                   withUsernameToken: WithUsernameToken,
                                   uploadCollections: UploadCollections,
                                   withLoginActionAsync: WithLoginActionAsync,
                                 )(implicit executionContext: ExecutionContext) extends AbstractController(messagesControllerComponents) with I18nSupport {

  private implicit val logger: Logger = Logger(this.getClass)

  private implicit val module: String = constants.Module.ACCOUNT_CONTROLLER

  def viewProfile(): EssentialAction = cached.apply(req => req.path, constants.CommonConfig.WebAppCacheDuration) {
    withLoginActionAsync { implicit loginState =>
      implicit request =>
        implicit val optionalLoginState: Option[LoginState] = Option(loginState)
        Future(Ok(views.html.profile.viewProfile()))
    }
  }

  def settings(): EssentialAction = cached.apply(req => req.path, constants.CommonConfig.WebAppCacheDuration) {
    withLoginActionAsync { implicit loginState =>
      implicit request =>
        val keys = masterKeys.Service.getAll(loginState.username)
        (for {
          keys <- keys
        } yield Ok(views.html.profile.settings(keys))
          ).recover {
          case baseException: BaseException => InternalServerError(baseException.failure.message)
        }
    }
  }

  def addNewKey(): Action[AnyContent] = withoutLoginAction { implicit request =>
    Ok(views.html.profile.addNewKey())
  }

  def addManagedKeyForm(): Action[AnyContent] = withoutLoginAction { implicit request =>
    Ok(views.html.profile.addManagedKey())
  }

  def addManagedKey(): Action[AnyContent] = withLoginActionAsync { implicit loginState =>
    implicit request =>
      AddManagedKey.form.bindFromRequest().fold(
        formWithErrors => {
          Future(BadRequest(views.html.profile.addManagedKey(formWithErrors)))
        },
        addManagedKeyData => {
          val wallet = utilities.Wallet.getWallet(addManagedKeyData.seeds.split(" "))
          val validatePassword = masterKeys.Service.validateUsernamePasswordAndGetKey(username = loginState.username, address = loginState.address, password = addManagedKeyData.password)

          def validateAndAdd(validatePassword: Boolean) = if (validatePassword && wallet.address == addManagedKeyData.address) {
            masterKeys.Service.addManagedKey(
              accountId = loginState.username,
              address = wallet.address,
              hdPath = wallet.hdPath,
              password = addManagedKeyData.password,
              privateKey = wallet.privateKey,
              partialMnemonics = Option(wallet.mnemonics.take(wallet.mnemonics.length - constants.Blockchain.MnemonicShown)),
              name = addManagedKeyData.keyName,
              retryCounter = 0,
              backupUsed = true,
              active = false,
              verified = Option(true)
            )
          } else constants.Response.INVALID_SEEDS_OR_ADDRESS.throwFutureBaseException()

          (for {
            (validatePassword, _) <- validatePassword
            _ <- validateAndAdd(validatePassword)
          } yield PartialContent(views.html.profile.keyAddedOrUpdatedSuccessfully(address = wallet.address, name = addManagedKeyData.keyName))
            ).recover {
            case baseException: BaseException => BadRequest(views.html.profile.addManagedKey(AddManagedKey.form.withGlobalError(baseException.failure.message)))
          }
        }
      )
  }

  def addUnmanagedKeyForm(): Action[AnyContent] = withoutLoginAction { implicit request =>
    Ok(views.html.profile.addUnmanagedKey())
  }

  def addUnmanagedKey(): Action[AnyContent] = withLoginActionAsync { implicit loginState =>
    implicit request =>
      AddUnmanagedKey.form.bindFromRequest().fold(
        formWithErrors => {
          Future(BadRequest(views.html.profile.addUnmanagedKey(formWithErrors)))
        },
        addUnmanagedKeyData => {
          val validatePassword = masterKeys.Service.validateUsernamePasswordAndGetKey(username = loginState.username, address = loginState.address, password = addUnmanagedKeyData.password)

          def validateAndAdd(validatePassword: Boolean) = if (validatePassword) {
            masterKeys.Service.addUnmanagedKey(
              accountId = loginState.username,
              address = addUnmanagedKeyData.address,
              password = addUnmanagedKeyData.password,
              name = addUnmanagedKeyData.keyName,
              retryCounter = 0,
              backupUsed = true,
              active = false,
              verified = Option(true)
            )
          } else constants.Response.INVALID_SEEDS_OR_ADDRESS.throwFutureBaseException()

          (for {
            (validatePassword, _) <- validatePassword
            _ <- validateAndAdd(validatePassword)
          } yield PartialContent(views.html.profile.keyAddedOrUpdatedSuccessfully(address = addUnmanagedKeyData.address, name = addUnmanagedKeyData.keyName))
            ).recover {
            case baseException: BaseException => BadRequest(views.html.profile.addUnmanagedKey(AddUnmanagedKey.form.withGlobalError(baseException.failure.message)))
          }
        }
      )
  }

  def changeKeyNameForm(address: String): Action[AnyContent] = withoutLoginAction { implicit request =>
    Ok(views.html.profile.changeKeyName(address = address))
  }

  def changeKeyName: Action[AnyContent] = withLoginActionAsync { implicit loginState =>
    implicit request =>
      ChangeKeyName.form.bindFromRequest().fold(
        formWithErrors => {
          Future(BadRequest(views.html.profile.changeKeyName(formWithErrors, formWithErrors.get.address)))
        },
        changeKeyNameData => {
          val update = masterKeys.Service.updateKeyName(accountId = loginState.username, address = changeKeyNameData.address, keyName = changeKeyNameData.keyName)

          (for {
            _ <- update
          } yield PartialContent(views.html.profile.keyAddedOrUpdatedSuccessfully(address = changeKeyNameData.address, name = changeKeyNameData.keyName))
            ).recover {
            case baseException: BaseException => BadRequest(views.html.profile.changeKeyName(ChangeKeyName.form.withGlobalError(baseException.failure.message), changeKeyNameData.address))
          }
        }
      )
  }

  def viewMnemonicsForm(): Action[AnyContent] = withoutLoginAction { implicit request =>
    Ok(views.html.profile.viewMnemonics())
  }

  def viewMnemonics: Action[AnyContent] = withoutLoginActionAsync { implicit loginState =>
    implicit request =>
      ViewMnemonics.form.bindFromRequest().fold(
        formWithErrors => {
          Future(BadRequest(views.html.profile.viewMnemonics(formWithErrors)))
        },
        viewMnemonicsData => {
          Future(Ok(views.html.index(successes = Seq(constants.Response.SIGN_UP_SUCCESSFUL))))
        }
      )
  }

  def deleteAccountForm(): Action[AnyContent] = withoutLoginAction { implicit request =>
    Ok(views.html.profile.deleteAccount())
  }

  def deleteAccount: Action[AnyContent] = withoutLoginActionAsync { implicit loginState =>
    implicit request =>
      DeleteAccount.form.bindFromRequest().fold(
        formWithErrors => {
          Future(BadRequest(views.html.profile.deleteAccount(formWithErrors)))
        },
        deleteAccountData => {
          Future(Ok(views.html.index(successes = Seq(constants.Response.SIGN_UP_SUCCESSFUL))))
        }
      )
  }

  def changeManagedToUnmanagedForm(address: String): Action[AnyContent] = withoutLoginAction { implicit request =>
    Ok(views.html.profile.changeManagedToUnmanaged(address = address))
  }

  def changeManagedToUnmanaged: Action[AnyContent] = withLoginActionAsync { implicit loginState =>
    implicit request =>
      ChangeManagedToUnmanaged.form.bindFromRequest().fold(
        formWithErrors => {
          Future(BadRequest(views.html.profile.changeManagedToUnmanaged(formWithErrors, formWithErrors.get.address)))
        },
        changeManagedToUnmanagedData => {
          val validate = masterKeys.Service.changeManagedToUnmanaged(accountId = loginState.username, address = changeManagedToUnmanagedData.address, password = changeManagedToUnmanagedData.password)

          (for {
            _ <- validate
          } yield PartialContent(views.html.profile.changeManagedToUnmanaged(address = changeManagedToUnmanagedData.address))
            ).recover {
            case baseException: BaseException => BadRequest(views.html.profile.changeManagedToUnmanaged(ChangeManagedToUnmanaged.form.withGlobalError(baseException.failure.message), changeManagedToUnmanagedData.address))
          }

        }
      )
  }

}