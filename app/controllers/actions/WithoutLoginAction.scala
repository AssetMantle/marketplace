package controllers.actions

import controllers.logging.WithActionLoggingFilter
import exceptions.BaseException
import play.api.Logger
import play.api.i18n.I18nSupport
import play.api.mvc._

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class WithoutLoginAction @Inject()(messagesControllerComponents: MessagesControllerComponents,
                                   withActionLoggingFilter: WithActionLoggingFilter
                                  )(implicit executionContext: ExecutionContext) extends AbstractController(messagesControllerComponents) with I18nSupport {

  def apply(f: => Request[AnyContent] => Result)(implicit logger: Logger): Action[AnyContent] = {
    withActionLoggingFilter.next { implicit request =>
      try {
        f(request)
      } catch {
        case baseException: BaseException => Results.InternalServerError(views.html.index(failures = Seq(baseException.failure))).withNewSession
      }
    }
  }
}
