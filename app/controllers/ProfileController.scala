package controllers

import controllers.actions._
import controllers.result.WithUsernameToken
import exceptions.BaseException
import models.{master, masterTransaction}
import play.api.Logger
import play.api.cache.Cached
import play.api.i18n.I18nSupport
import play.api.mvc.{AbstractController, Action, AnyContent, MessagesControllerComponents}
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
                                   withLoginActionAsync: WithLoginActionAsync,
                                 )(implicit executionContext: ExecutionContext) extends AbstractController(messagesControllerComponents) with I18nSupport {

  private implicit val logger: Logger = Logger(this.getClass)

  private implicit val module: String = constants.Module.PROFILE_CONTROLLER

  def viewProfile(): EssentialAction = cached.apply(req => req.path + "/" + req.session.get(constants.Session.USERNAME).getOrElse("") + "/" + req.session.get(constants.Session.TOKEN).getOrElse(""), constants.CommonConfig.WebAppCacheDuration) {
    withLoginActionAsync { implicit loginState =>
      implicit request =>
        implicit val optionalLoginState: Option[LoginState] = Option(loginState)
        Future(Ok(views.html.profile.viewProfile()))
    }
  }

  def settings(): EssentialAction = cached.apply(req => req.path + "/" + req.session.get(constants.Session.USERNAME).getOrElse("") + "/" + req.session.get(constants.Session.TOKEN).getOrElse(""), constants.CommonConfig.WebAppCacheDuration) {
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

  def walletPopup(): EssentialAction = cached.apply(req => req.path + "/" + req.session.get(constants.Session.USERNAME).getOrElse("") + "/" + req.session.get(constants.Session.TOKEN).getOrElse(""), constants.CommonConfig.WebAppCacheDuration) {
    withLoginActionAsync { implicit loginState =>
      implicit request =>
        val keys = masterKeys.Service.getAll(loginState.username)
        (for {
          keys <- keys
        } yield Ok(views.html.base.commonWalletPopup(keys))
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
          } else constants.Response.INVALID_SEEDS_OR_ADDRESS_OR_PASSWORD.throwFutureBaseException()

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
          } else constants.Response.INVALID_PASSWORD.throwFutureBaseException()

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
          Future(BadRequest(views.html.profile.changeKeyName(formWithErrors, formWithErrors.data.getOrElse(constants.FormField.CHANGE_KEY_ADDRESS.name, ""))))
        },
        changeKeyNameData => {
          val update = masterKeys.Service.updateKeyName(accountId = loginState.username, address = changeKeyNameData.address, keyName = changeKeyNameData.keyName)

          (for {
            _ <- update
          } yield PartialContent(views.html.profile.changeKeyNameSuccessfully(address = changeKeyNameData.address, name = changeKeyNameData.keyName))
            ).recover {
            case baseException: BaseException => BadRequest(views.html.profile.changeKeyName(ChangeKeyName.form.withGlobalError(baseException.failure.message), changeKeyNameData.address))
          }
        }
      )
  }

  def viewMnemonicsForm(address: String): Action[AnyContent] = withoutLoginAction { implicit request =>
    Ok(views.html.profile.viewMnemonics(address = address))
  }

  def viewMnemonics(): Action[AnyContent] = withLoginActionAsync { implicit loginState =>
    implicit request =>
      ViewMnemonics.form.bindFromRequest().fold(
        formWithErrors => {
          Future(BadRequest(views.html.profile.viewMnemonics(formWithErrors, formWithErrors.data.getOrElse(constants.FormField.WALLET_ADDRESS.name, ""))))
        },
        viewMnemonicsData => {
          val validateAndGetKey = masterKeys.Service.validateUsernamePasswordAndGetKey(username = loginState.username, address = viewMnemonicsData.address, password = viewMnemonicsData.password)

          def update(validated: Boolean, key: master.Key) = if (validated) masterKeys.Service.updateKey(key.copy(backupUsed = true)) else constants.Response.INVALID_PASSWORD.throwFutureBaseException()

          (for {
            (validated, key) <- validateAndGetKey
            _ <- update(validated, key)
          } yield if (validated) PartialContent(views.html.profile.seedPhrase(key.partialMnemonics.getOrElse(constants.Response.SEEDS_NOT_FOUND.throwBaseException()))) else constants.Response.INVALID_PASSWORD.throwBaseException()
            ).recover {
            case baseException: BaseException => BadRequest(views.html.profile.viewMnemonics(ViewMnemonics.form.withGlobalError(baseException.failure.message), viewMnemonicsData.address))
          }
        }
      )
  }

  def deleteKeyForm(address: String): Action[AnyContent] = withoutLoginAction { implicit request =>
    Ok(views.html.profile.deleteKey(address = address))
  }

  def deleteKey(): Action[AnyContent] = withLoginActionAsync { implicit loginState =>
    implicit request =>
      DeleteKey.form.bindFromRequest().fold(
        formWithErrors => {
          Future(BadRequest(views.html.profile.deleteKey(formWithErrors, formWithErrors.data.getOrElse(constants.FormField.WALLET_ADDRESS.name, ""))))
        },
        deleteKeyData => {
          val validateAndGetKey = masterKeys.Service.validateUsernamePasswordAndGetKey(username = loginState.username, address = deleteKeyData.address, password = deleteKeyData.password)

          def delete(validated: Boolean, key: master.Key) = if (!key.active) {
            if (validated) masterKeys.Service.deleteKey(accountId = key.accountId, address = key.address) else constants.Response.INVALID_PASSWORD.throwFutureBaseException()
          } else constants.Response.CANNOT_DELETE_ACTIVE_KEY.throwFutureBaseException()

          (for {
            (validated, key) <- validateAndGetKey
            _ <- delete(validated, key)
          } yield PartialContent(views.html.profile.keyDeletedSuccessfully())
            ).recover {
            case baseException: BaseException => BadRequest(views.html.profile.deleteKey(DeleteKey.form.withGlobalError(baseException.failure.message), deleteKeyData.address))
          }
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
          Future(BadRequest(views.html.profile.changeManagedToUnmanaged(formWithErrors, formWithErrors.data.getOrElse(constants.FormField.MANAGED_KEY_ADDRESS.name, ""))))
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

  def viewWishList(): EssentialAction = cached.apply(req => req.path, constants.CommonConfig.WebAppCacheDuration) {
    withoutLoginActionAsync { implicit loginState =>
      implicit request =>
        Future(Ok(views.html.profile.wishList.viewWishList()))
    }
  }

  def wishList(): EssentialAction = cached.apply(req => req.path, constants.CommonConfig.WebAppCacheDuration) {
    withoutLoginAction { implicit request =>
      Ok(views.html.profile.wishList.wishList())
    }
  }

  def wishListNFTs(): EssentialAction = cached.apply(req => req.path, constants.CommonConfig.WebAppCacheDuration) {
    withoutLoginAction { implicit request =>
      Ok(views.html.profile.wishList.wishListNFTs())
    }
  }
}