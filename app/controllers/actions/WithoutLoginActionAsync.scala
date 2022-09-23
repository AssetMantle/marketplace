package controllers.actions

import controllers.logging.WithActionAsyncLogging
import exceptions.BaseException
import models.{master, masterTransaction}
import org.joda.time.{DateTime, DateTimeZone}
import play.api.Logger
import play.api.i18n.I18nSupport
import play.api.mvc._

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class WithoutLoginActionAsync @Inject()(
                                         messagesControllerComponents: MessagesControllerComponents,
                                         withActionAsyncLogging: WithActionAsyncLogging,
                                         masterTransactionSessionTokens: masterTransaction.SessionTokens,
                                         masterKeys: master.Keys,
                                       )(implicit executionContext: ExecutionContext) extends AbstractController(messagesControllerComponents) with I18nSupport {

  private implicit val module: String = constants.Module.ASYNC_ACTIONS_WITHOUT_LOGIN

  def apply(f: => Option[LoginState] => Request[AnyContent] => Future[Result])(implicit logger: Logger): Action[AnyContent] = {
    withActionAsyncLogging { implicit request =>
      val username = request.session.get(constants.Session.USERNAME).getOrElse("")
      val sessionToken = request.session.get(constants.Session.TOKEN).getOrElse("")
      val address = request.session.get(constants.Session.ADDRESS).getOrElse("")

      def verifyAndGetResult(username: String, address: String, sessionToken: String) = if (username != "" && address != "" && sessionToken != "") {
        val verify = {
          val token = masterTransactionSessionTokens.Service.tryGet(username)
          val key = masterKeys.Service.tryGetActive(username)

          for {
            token <- token
            key <- key
          } yield key.accountId == username && key.address == address && token.sessionTokenHash == utilities.Secrets.sha256HashString(sessionToken) && (DateTime.now(DateTimeZone.UTC).getMillis - token.sessionTokenTime < constants.CommonConfig.sessionTokenTimeout)
        }

        def getResult(verify: Boolean, loginState: LoginState) = if (verify) f(Option(loginState))(request)
        else constants.Response.INVALID_SESSION.throwFutureBaseException()

        for {
          verify <- verify
          result <- getResult(verify, LoginState(username, address))
        } yield result
      } else f(None)(request)

      (for {
        result <- verifyAndGetResult(username = username, address = address, sessionToken = sessionToken)
      } yield result).recover {
        case baseException: BaseException => Results.InternalServerError(views.html.index(failures = Seq(baseException.failure))).withNewSession
      }
    }
  }
}
