package controllers

import controllers.actions._
import exceptions.BaseException
import models.master
import play.api.Logger
import play.api.cache.Cached
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, _}
import play.api.mvc.{AbstractController, MessagesControllerComponents}

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}
import views.profile.whitelist.companion._

@Singleton
class ProfileController @Inject()(
                                   messagesControllerComponents: MessagesControllerComponents,
                                   cached: Cached,
                                   withoutLoginActionAsync: WithoutLoginActionAsync,
                                   withLoginAction: WithLoginAction,
                                   withLoginActionAsync: WithLoginActionAsync,
                                   withoutLoginAction: WithoutLoginAction,
                                   masterAccounts: master.Accounts,
                                   masterWhitelists: master.Whitelists,
                                   masterCollections: master.Collections,
                                   masterWhitelistMembers: master.WhitelistMembers,
                                 )(implicit executionContext: ExecutionContext) extends AbstractController(messagesControllerComponents) with I18nSupport {

  private implicit val logger: Logger = Logger(this.getClass)

  private implicit val module: String = constants.Module.PROFILE_CONTROLLER

  implicit val callbackOnSessionTimeout: Call = routes.ProfileController.viewDefaultProfile()

  def viewDefaultProfile(): Action[AnyContent] = withLoginActionAsync { implicit loginState =>
    implicit request =>
      implicit val optionalLoginState: Option[LoginState] = Option(loginState)
      Future(Ok(views.html.profile.viewProfile(loginState.username)))
  }

  def viewProfile(accountId: String): Action[AnyContent] = withoutLoginActionAsync { implicit loginState =>
    implicit request =>
      Future(Ok(views.html.profile.viewProfile(accountId)))
  }

  def profile(accountId: String): Action[AnyContent] = withoutLoginActionAsync { implicit loginState =>
    implicit request =>
      val isCreator = if (loginState.isDefined && loginState.fold("")(_.username) == accountId) masterCollections.Service.isCreator(accountId) else Future(false)
      val hasWhitelist = if (loginState.isDefined && loginState.fold("")(_.username) == accountId) masterWhitelists.Service.hasWhitelist(accountId) else Future(false)

      (for {
        isCreator <- isCreator
        hasWhitelist <- hasWhitelist
      } yield Ok(views.html.profile.profile(accountId: String, isCreator = isCreator, hasWhitelist = hasWhitelist))
        ).recover {
        case baseException: BaseException => InternalServerError(baseException.failure.message)
      }

  }
}
