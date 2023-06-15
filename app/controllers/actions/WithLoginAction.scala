package controllers.actions

import controllers.logging.{WithActionAsyncLogging, WithMultipartFormActionAsyncLogging}
import exceptions.BaseException
import models.{master, masterTransaction}
import play.api.Logger
import play.api.i18n.I18nSupport
import play.api.libs.Files.TemporaryFile
import play.api.mvc._

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class WithLoginAction @Inject()(
                                 messagesControllerComponents: MessagesControllerComponents,
                                 withActionAsyncLogging: WithActionAsyncLogging,
                                 withMultipartFormActionAsyncLogging: WithMultipartFormActionAsyncLogging,
                                 masterAccounts: master.Accounts,
                                 masterKeys: master.Keys,
                                 masterTransactionSessionTokens: masterTransaction.SessionTokens,
                               )(implicit executionContext: ExecutionContext) extends AbstractController(messagesControllerComponents) with I18nSupport {

  implicit val module: String = constants.Module.ACTIONS_WITH_LOGIN

  private def verifyAndGetLoginState(request: Request[_])(implicit logger: Logger) = {
    val username = Future(request.session.get(constants.Session.USERNAME).getOrElse(constants.Response.USERNAME_NOT_FOUND.throwBaseException()))
    val address = Future(request.session.get(constants.Session.ADDRESS).getOrElse(constants.Response.ADDRESS_NOT_FOUND.throwBaseException()))
    val currentSessionToken = Future(request.session.get(constants.Session.TOKEN).getOrElse(constants.Response.TOKEN_NOT_FOUND.throwBaseException()))

    def verify(username: String, address: String, currentSessionToken: String) = {
      val storedSessionToken = masterTransactionSessionTokens.Service.tryGet(username)
      val key = masterKeys.Service.tryGetActive(username)
      val account = masterAccounts.Service.tryGet(username)

      for {
        storedSessionToken <- storedSessionToken
        key <- key
        account <- account
      } yield (utilities.Session.verify(username = username, key = key, address = address, storedSessionToken = storedSessionToken, currentSessionToken = currentSessionToken), account)
    }

    (for {
      username <- username
      address <- address
      currentSessionToken <- currentSessionToken
      (verify, account) <- verify(username, address, currentSessionToken)
    } yield (verify, LoginState(username = username, address = address, accountType = account.accountType))).recover {
      case _: BaseException => (false, LoginState(username = "", address = "", accountType = ""))
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
        case exception: Exception => logger.error(exception.getLocalizedMessage)
          BadRequest
      }
    }
  }
}
