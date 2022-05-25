package controllers

import controllers.actions.{WithoutLoginAction, WithoutLoginActionAsync}
import play.api.Logger
import play.api.cache.Cached
import play.api.i18n.I18nSupport
import play.api.mvc.{AbstractController, Action, AnyContent, MessagesControllerComponents}

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class AccountController @Inject()(
                                   messagesControllerComponents: MessagesControllerComponents,
                                   cached: Cached,
                                   withoutLoginActionAsync: WithoutLoginActionAsync,
                                   withoutLoginAction: WithoutLoginAction
                                 )(implicit executionContext: ExecutionContext) extends AbstractController(messagesControllerComponents) with I18nSupport {

  private implicit val logger: Logger = Logger(this.getClass)

  private implicit val module: String = constants.Module.ACCOUNT_CONTROLLER

  def signUpForm(): Action[AnyContent] = withoutLoginAction { implicit loginState =>
    implicit request =>
      Ok(views.html.component.master.account.signUp())
  }

}