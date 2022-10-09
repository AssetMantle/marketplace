package controllers

import controllers.actions._
import controllers.result.WithUsernameToken
import exceptions.BaseException
import models.{blockchain, master, masterTransaction}
import play.api.Logger
import play.api.cache.Cached
import play.api.i18n.I18nSupport
import play.api.mvc.{AbstractController, Action, AnyContent, MessagesControllerComponents}
import views.setting.companion._
import play.api.mvc._
import utilities.MicroNumber

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class SettingController @Inject()(
                                   messagesControllerComponents: MessagesControllerComponents,
                                   cached: Cached,
                                   blockchainBalances: blockchain.Balances,
                                   withoutLoginActionAsync: WithoutLoginActionAsync,
                                   withoutLoginAction: WithoutLoginAction,
                                   masterAccounts: master.Accounts,
                                   masterKeys: master.Keys,
                                   masterTransactionSessionTokens: masterTransaction.SessionTokens,
                                   masterTransactionPushNotificationTokens: masterTransaction.PushNotificationTokens,
                                   withUsernameToken: WithUsernameToken,
                                   withLoginActionAsync: WithLoginActionAsync,
                                   withLoginAction: WithLoginAction
                                 )(implicit executionContext: ExecutionContext) extends AbstractController(messagesControllerComponents) with I18nSupport {

  private implicit val logger: Logger = Logger(this.getClass)

  private implicit val module: String = constants.Module.SETTING_CONTROLLER

  implicit val callbackOnSessionTimeout: Call = routes.SettingController.viewSettings()

  def viewSettings(): EssentialAction = cached.apply(req => req.path + "/" + req.session.get(constants.Session.USERNAME).getOrElse("") + "/" + req.session.get(constants.Session.TOKEN).getOrElse(""), constants.CommonConfig.WebAppCacheDuration) {
    withLoginAction { implicit loginState =>
      implicit request =>
        Ok(views.html.setting.viewSettings())
    }
  }

  def settings(): EssentialAction = cached.apply(req => req.path + "/" + req.session.get(constants.Session.USERNAME).getOrElse("") + "/" + req.session.get(constants.Session.TOKEN).getOrElse(""), constants.CommonConfig.WebAppCacheDuration) {
    withLoginActionAsync { implicit loginState =>
      implicit request =>
        val keys = masterKeys.Service.getAll(loginState.username)
        (for {
          keys <- keys
        } yield Ok(views.html.setting.settings(keys))
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
    Ok(views.html.setting.addNewKey())
  }

  def addManagedKeyForm(): Action[AnyContent] = withoutLoginAction { implicit request =>
    Ok(views.html.setting.addManagedKey())
  }

  def addManagedKey(): Action[AnyContent] = withLoginActionAsync { implicit loginState =>
    implicit request =>
      AddManagedKey.form.bindFromRequest().fold(
        formWithErrors => {
          Future(BadRequest(views.html.setting.addManagedKey(formWithErrors)))
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
          } yield PartialContent(views.html.setting.keyAddedOrUpdatedSuccessfully(address = wallet.address, name = addManagedKeyData.keyName))
            ).recover {
            case baseException: BaseException => BadRequest(views.html.setting.addManagedKey(AddManagedKey.form.withGlobalError(baseException.failure.message)))
          }
        }
      )
  }

  def addUnmanagedKeyForm(): Action[AnyContent] = withoutLoginAction { implicit request =>
    Ok(views.html.setting.addUnmanagedKey())
  }

  def addUnmanagedKey(): Action[AnyContent] = withLoginActionAsync { implicit loginState =>
    implicit request =>
      AddUnmanagedKey.form.bindFromRequest().fold(
        formWithErrors => {
          Future(BadRequest(views.html.setting.addUnmanagedKey(formWithErrors)))
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
          } yield PartialContent(views.html.setting.keyAddedOrUpdatedSuccessfully(address = addUnmanagedKeyData.address, name = addUnmanagedKeyData.keyName))
            ).recover {
            case baseException: BaseException => BadRequest(views.html.setting.addUnmanagedKey(AddUnmanagedKey.form.withGlobalError(baseException.failure.message)))
          }
        }
      )
  }

  def changeKeyNameForm(address: String): Action[AnyContent] = withoutLoginAction { implicit request =>
    Ok(views.html.setting.changeKeyName(address = address))
  }

  def changeKeyName: Action[AnyContent] = withLoginActionAsync { implicit loginState =>
    implicit request =>
      ChangeKeyName.form.bindFromRequest().fold(
        formWithErrors => {
          Future(BadRequest(views.html.setting.changeKeyName(formWithErrors, formWithErrors.data.getOrElse(constants.FormField.CHANGE_KEY_ADDRESS.name, ""))))
        },
        changeKeyNameData => {
          val update = masterKeys.Service.updateKeyName(accountId = loginState.username, address = changeKeyNameData.address, keyName = changeKeyNameData.keyName)

          (for {
            _ <- update
          } yield PartialContent(views.html.setting.changeKeyNameSuccessfully(address = changeKeyNameData.address, name = changeKeyNameData.keyName))
            ).recover {
            case baseException: BaseException => BadRequest(views.html.setting.changeKeyName(ChangeKeyName.form.withGlobalError(baseException.failure.message), changeKeyNameData.address))
          }
        }
      )
  }

  def viewMnemonicsForm(address: String): Action[AnyContent] = withoutLoginAction { implicit request =>
    Ok(views.html.setting.viewMnemonics(address = address))
  }

  def viewMnemonics(): Action[AnyContent] = withLoginActionAsync { implicit loginState =>
    implicit request =>
      ViewMnemonics.form.bindFromRequest().fold(
        formWithErrors => {
          Future(BadRequest(views.html.setting.viewMnemonics(formWithErrors, formWithErrors.data.getOrElse(constants.FormField.WALLET_ADDRESS.name, ""))))
        },
        viewMnemonicsData => {
          val validateAndGetKey = masterKeys.Service.validateUsernamePasswordAndGetKey(username = loginState.username, address = viewMnemonicsData.address, password = viewMnemonicsData.password)

          def update(validated: Boolean, key: master.Key) = if (validated) masterKeys.Service.updateKey(key.copy(backupUsed = true)) else constants.Response.INVALID_PASSWORD.throwFutureBaseException()

          (for {
            (validated, key) <- validateAndGetKey
            _ <- update(validated, key)
          } yield if (validated) PartialContent(views.html.setting.seedPhrase(key.partialMnemonics.getOrElse(constants.Response.SEEDS_NOT_FOUND.throwBaseException()))) else constants.Response.INVALID_PASSWORD.throwBaseException()
            ).recover {
            case baseException: BaseException => BadRequest(views.html.setting.viewMnemonics(ViewMnemonics.form.withGlobalError(baseException.failure.message), viewMnemonicsData.address))
          }
        }
      )
  }

  def deleteKeyForm(address: String): Action[AnyContent] = withoutLoginAction { implicit request =>
    Ok(views.html.setting.deleteKey(address = address))
  }

  def deleteKey(): Action[AnyContent] = withLoginActionAsync { implicit loginState =>
    implicit request =>
      DeleteKey.form.bindFromRequest().fold(
        formWithErrors => {
          Future(BadRequest(views.html.setting.deleteKey(formWithErrors, formWithErrors.data.getOrElse(constants.FormField.WALLET_ADDRESS.name, ""))))
        },
        deleteKeyData => {
          val validateAndGetKey = masterKeys.Service.validateUsernamePasswordAndGetKey(username = loginState.username, address = deleteKeyData.address, password = deleteKeyData.password)

          def delete(validated: Boolean, key: master.Key) = if (!key.active) {
            if (validated) masterKeys.Service.deleteKey(accountId = key.accountId, address = key.address) else constants.Response.INVALID_PASSWORD.throwFutureBaseException()
          } else constants.Response.CANNOT_DELETE_ACTIVE_KEY.throwFutureBaseException()

          (for {
            (validated, key) <- validateAndGetKey
            _ <- delete(validated, key)
          } yield PartialContent(views.html.setting.keyDeletedSuccessfully())
            ).recover {
            case baseException: BaseException => BadRequest(views.html.setting.deleteKey(DeleteKey.form.withGlobalError(baseException.failure.message), deleteKeyData.address))
          }
        }
      )
  }

  def changeManagedToUnmanagedForm(address: String): Action[AnyContent] = withoutLoginAction { implicit request =>
    Ok(views.html.setting.changeManagedToUnmanaged(address = address))
  }

  def changeManagedToUnmanaged: Action[AnyContent] = withLoginActionAsync { implicit loginState =>
    implicit request =>
      ChangeManagedToUnmanaged.form.bindFromRequest().fold(
        formWithErrors => {
          Future(BadRequest(views.html.setting.changeManagedToUnmanaged(formWithErrors, formWithErrors.data.getOrElse(constants.FormField.MANAGED_KEY_ADDRESS.name, ""))))
        },
        changeManagedToUnmanagedData => {
          val validate = masterKeys.Service.changeManagedToUnmanaged(accountId = loginState.username, address = changeManagedToUnmanagedData.address, password = changeManagedToUnmanagedData.password)

          (for {
            _ <- validate
          } yield PartialContent(views.html.setting.changeManagedToUnmanaged(address = changeManagedToUnmanagedData.address))
            ).recover {
            case baseException: BaseException => BadRequest(views.html.setting.changeManagedToUnmanaged(ChangeManagedToUnmanaged.form.withGlobalError(baseException.failure.message), changeManagedToUnmanagedData.address))
          }

        }
      )
  }

  def walletBalance(address: String): EssentialAction = cached.apply(req => req.path + "/" + address, constants.CommonConfig.WebAppCacheDuration) {
    withoutLoginActionAsync { implicit loginState =>
      implicit request =>
        val balance = blockchainBalances.Service.tryGet(address)
        (for {
          balance <- balance
        } yield Ok(views.html.base.info.commonMicroNumber(balance.coins.find(_.denom == constants.Blockchain.StakingToken).fold(MicroNumber.zero)(_.amount), constants.View.STAKING_TOKEN_UNITS))
          ).recover {
          case _: BaseException => BadRequest(views.html.base.info.commonMicroNumber(MicroNumber.zero, constants.View.STAKING_TOKEN_UNITS))
        }
    }
  }

  def notificationPopup(): EssentialAction = cached.apply(req => req.path + "/" + req.session.get(constants.Session.USERNAME).getOrElse("") + "/" + req.session.get(constants.Session.TOKEN).getOrElse(""), constants.CommonConfig.WebAppCacheDuration) {
    withLoginAction { implicit loginState =>
      implicit request =>
        Ok(views.html.base.commonNotificationPopup())
    }
  }

}