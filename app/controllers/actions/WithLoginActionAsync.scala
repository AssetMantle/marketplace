package controllers.actions

import controllers.logging.WithActionAsyncLoggingFilter
import exceptions.BaseException
import models.{master, masterTransaction}
import org.joda.time.{DateTime, DateTimeZone}
import play.api.i18n.I18nSupport
import play.api.mvc._
import play.api.{Configuration, Logger}

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class WithLoginActionAsync @Inject()(
                                      messagesControllerComponents: MessagesControllerComponents,
                                      withActionAsyncLoggingFilter: WithActionAsyncLoggingFilter,
                                      masterKeys: master.Keys,
                                      masterTransactionSessionTokens: masterTransaction.SessionTokens,
                                    )(implicit executionContext: ExecutionContext) extends AbstractController(messagesControllerComponents) with I18nSupport {

  private implicit val module: String = constants.Module.ASYNC_ACTIONS_WITH_LOGIN

  def apply(f: => LoginState => Request[AnyContent] => Future[Result])(implicit logger: Logger, callbackOnSessionTimeout: Call): Action[AnyContent] = {
    withActionAsyncLoggingFilter.next { implicit request =>

      val username = Future(request.session.get(constants.Session.USERNAME).getOrElse(throw new BaseException(constants.Response.USERNAME_NOT_FOUND)))
      val address = Future(request.session.get(constants.Session.ADDRESS).getOrElse(throw new BaseException(constants.Response.ADDRESS_NOT_FOUND)))
      val token = Future(request.session.get(constants.Session.TOKEN).getOrElse(throw new BaseException(constants.Response.TOKEN_NOT_FOUND)))

      def verify(username: String, address: String, sessionToken: String) = {
        val token = masterTransactionSessionTokens.Service.tryGet(username)
        val key = masterKeys.Service.tryGetActive(username)

        for {
          token <- token
          key <- key
        } yield (key.accountId == username && key.address == address && token.sessionTokenHash == utilities.Secrets.sha256HashString(sessionToken) && (DateTime.now(DateTimeZone.UTC).getMillis - token.sessionTokenTime < constants.CommonConfig.sessionTokenTimeout))
      }

      def getResult(verify: Boolean, loginState: LoginState): Future[Result] = if (verify) f(loginState)(request)
      else throw new BaseException(constants.Response.INVALID_SESSION)

      (for {
        username <- username
        address <- address
        token <- token
        verify <- verify(username, address, token)
        result <- getResult(verify, LoginState(username = username, address = address))
      } yield result).recover {
        case baseException: BaseException => logger.info(baseException.failure.message, baseException)
          Results.Unauthorized(views.html.indexWithLoginFormPopup(callbackOnSessionTimeout.url)).withNewSession
      }
    }
  }
}
