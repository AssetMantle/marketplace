package controllers.actions

import controllers.logging.WithActionAsyncLoggingFilter
import exceptions.BaseException
import play.api.Logger
import play.api.i18n.I18nSupport
import play.api.mvc._

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class WithoutLoginActionAsync @Inject()(messagesControllerComponents: MessagesControllerComponents,
                                        withActionAsyncLoggingFilter: WithActionAsyncLoggingFilter
                                       )(implicit executionContext: ExecutionContext) extends AbstractController(messagesControllerComponents) with I18nSupport {

  def apply(f: => Request[AnyContent] => Future[Result])(implicit logger: Logger): Action[AnyContent] = {
    withActionAsyncLoggingFilter.next { implicit request ⇒
      (for {
        result <- f(request)
      } yield result).recover {
        case baseException: BaseException => Results.InternalServerError(views.html.index(failures = Seq(baseException.failure))).withNewSession
      }
    }
  }
}
