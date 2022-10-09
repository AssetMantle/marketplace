package controllers.actions

import controllers.logging.{WithActionAsyncLogging, WithMultipartFormActionAsyncLogging}
import exceptions.BaseException
import models.{master, masterTransaction}
import org.joda.time.{DateTime, DateTimeZone}
import play.api.i18n.I18nSupport
import play.api.libs.Files.TemporaryFile
import play.api.mvc._
import play.api.{Configuration, Logger}

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class WithLoginAction @Inject()(
                                 messagesControllerComponents: MessagesControllerComponents,
                                 withActionAsyncLogging: WithActionAsyncLogging,
                                 withMultipartFormActionAsyncLogging: WithMultipartFormActionAsyncLogging,
                                 masterKeys: master.Keys,
                                 masterTransactionSessionTokens: masterTransaction.SessionTokens,
                               )(implicit executionContext: ExecutionContext) extends AbstractController(messagesControllerComponents) with I18nSupport {

  private implicit val module: String = constants.Module.ACTIONS_WITH_LOGIN

  private def verifyAndGetLoginState(request: Request[_])(implicit logger: Logger) = {
    val username = Future(request.session.get(constants.Session.USERNAME).getOrElse(throw new BaseException(constants.Response.USERNAME_NOT_FOUND)))
    val address = Future(request.session.get(constants.Session.ADDRESS).getOrElse(throw new BaseException(constants.Response.ADDRESS_NOT_FOUND)))
    val token = Future(request.session.get(constants.Session.TOKEN).getOrElse(throw new BaseException(constants.Response.TOKEN_NOT_FOUND)))

    def verify(username: String, address: String, sessionToken: String) = {
      val token = masterTransactionSessionTokens.Service.tryGet(username)
      val key = masterKeys.Service.tryGetActive(username)

      for {
        token <- token
        key <- key
      } yield key.accountId == username && key.address == address && token.sessionTokenHash == utilities.Secrets.sha256HashString(sessionToken) && (DateTime.now(DateTimeZone.UTC).getMillis - token.sessionTokenTime < constants.CommonConfig.SessionTokenTimeout)
    }

    (for {
      username <- username
      address <- address
      token <- token
      verify <- verify(username, address, token)
    } yield (verify, LoginState(username = username, address = address))).recover {
      case _: BaseException => (false, LoginState(username = "", address = ""))
    }
  }

  def apply(f: => LoginState => Request[AnyContent] => Result)(implicit logger: Logger, callbackOnSessionTimeout: Call): Action[AnyContent] = {
    withActionAsyncLogging { implicit request =>

      def getResult(verified: Boolean, loginState: LoginState): Result = if (verified) f(loginState)(request)
      else constants.Response.INVALID_SESSION.throwBaseException()

      (for {
        (verified, loginState) <- verifyAndGetLoginState(request)
      } yield getResult(verified, loginState)
        ).recover {
        case baseException: BaseException => logger.info(baseException.failure.message, baseException)
          Results.Unauthorized(views.html.indexWithLoginFormPopup(callbackOnSessionTimeout.url)).withNewSession
      }
    }
  }

  def applyMultipartFormData(f: => LoginState => Request[MultipartFormData[TemporaryFile]] => Result)(implicit logger: Logger, callbackOnSessionTimeout: Call): Action[MultipartFormData[TemporaryFile]] = {
    withMultipartFormActionAsyncLogging { implicit request =>

      def getResult(verified: Boolean, loginState: LoginState): Result = if (verified) f(loginState)(request)
      else constants.Response.INVALID_SESSION.throwBaseException()

      (for {
        (verified, loginState) <- verifyAndGetLoginState(request)
      } yield getResult(verified, loginState)
        ).recover {
        case baseException: BaseException => logger.info(baseException.failure.message, baseException)
          Results.Unauthorized(views.html.indexWithLoginFormPopup(callbackOnSessionTimeout.url)).withNewSession
      }
    }
  }
}
