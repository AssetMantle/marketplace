package controllers

import controllers.actions._
import controllers.result.WithUsernameToken
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
                                   masterWallets: master.Wallets,
                                   masterTransactionSessionTokens: masterTransaction.SessionTokens,
                                   masterTransactionPushNotificationTokens: masterTransaction.PushNotificationTokens,
                                   withUsernameToken: WithUsernameToken,
                                   uploadCollections: UploadCollections,
                                   withLoginActionAsync: WithLoginActionAsync,
                                 )(implicit executionContext: ExecutionContext) extends AbstractController(messagesControllerComponents) with I18nSupport {

  private implicit val logger: Logger = Logger(this.getClass)

  private implicit val module: String = constants.Module.ACCOUNT_CONTROLLER

  def viewSettings(): EssentialAction = cached.apply(req => req.path, constants.CommonConfig.WebAppCacheDuration) {
    withoutLoginActionAsync { implicit loginState =>
      implicit request =>
        Future(Ok(views.html.profile.viewSettings()))
    }
  }
  def settingsPage(): EssentialAction = cached.apply(req => req.path, constants.CommonConfig.WebAppCacheDuration) {
    withoutLoginAction { implicit request =>
      Ok(views.html.profile.user.settingsPage())
    }
  }

  def addNewWallet(): Action[AnyContent] = withoutLoginAction { implicit request =>
    Ok(views.html.profile.addNewWallet())
  }

  // GET Request
  def managedAddressForm(): Action[AnyContent] = withoutLoginAction { implicit request =>
    Ok(views.html.profile.managedAddress())
//    Ok(views.html.profile.walletAddSuccess())
  }

  // POST Request
  def managedAddress: Action[AnyContent] = withoutLoginActionAsync { implicit loginState =>
    implicit request =>
      ManagedAddress.form.bindFromRequest().fold(
        formWithErrors => {
          Future(BadRequest(views.html.profile.managedAddress(formWithErrors)))
        },
        managedAddressData => {
          Future(Ok(views.html.index(successes = Seq(constants.Response.SIGN_UP_SUCCESSFUL))))
        }
      )
  }

  // GET Request
  def unmanagedAddressForm(): Action[AnyContent] = withoutLoginAction { implicit request =>
    Ok(views.html.profile.unmanagedAddress())
  }
  // POST Request
  def unmanagedAddress: Action[AnyContent] = withoutLoginActionAsync { implicit loginState =>
    implicit request =>
      UnmanagedAddress.form.bindFromRequest().fold(
        formWithErrors => {
          Future(BadRequest(views.html.profile.unmanagedAddress(formWithErrors)))
        },
        unmanagedAddressData => {
          Future(Ok(views.html.index(successes = Seq(constants.Response.SIGN_UP_SUCCESSFUL))))
        }
      )
  }

  // GET Request
  def changeWalletNameForm(): Action[AnyContent] = withoutLoginAction { implicit request =>
    Ok(views.html.profile.changeWalletName())
  }

  // POST Request
  def changeWalletName: Action[AnyContent] = withoutLoginActionAsync { implicit loginState =>
    implicit request =>
      ChangeWalletName.form.bindFromRequest().fold(
        formWithErrors => {
          Future(BadRequest(views.html.profile.changeWalletName(formWithErrors)))
        },
        changeWalletNameData => {
          Future(Ok(views.html.index(successes = Seq(constants.Response.SIGN_UP_SUCCESSFUL))))
        }
      )
  }
}