package controllers.actions

import controllers.logging.WithActionAsyncLoggingFilter
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
                                         withActionAsyncLoggingFilter: WithActionAsyncLoggingFilter,
                                         masterTransactionSessionTokens: masterTransaction.SessionTokens,
                                         masterWallets: master.Wallets,
                                       )(implicit executionContext: ExecutionContext) extends AbstractController(messagesControllerComponents) with I18nSupport {

  private implicit val module: String = constants.Module.ACTIONS_WITHOUT_LOGIN_ACTION_ASYNC

  def apply(f: => Option[LoginState] => Request[AnyContent] => Future[Result])(implicit logger: Logger): Action[AnyContent] = {
    withActionAsyncLoggingFilter.next { implicit request =>
      val username = request.session.get(constants.Session.USERNAME).getOrElse("")
      val sessionToken = request.session.get(constants.Session.TOKEN).getOrElse("")
      val address = request.session.get(constants.Session.ADDRESS).getOrElse("")

      def verifyAndGetResult(username: String, address: String, sessionToken: String) = if (username != "" && address != "" && sessionToken != "") {
        val verify = {
          val token = masterTransactionSessionTokens.Service.tryGet(username)
          val wallet = masterWallets.Service.tryGet(address)

          for {
            token <- token
            wallet <- wallet
          } yield (wallet.accountId == username && token.sessionTokenHash == utilities.Secrets.sha256HashString(sessionToken) && (DateTime.now(DateTimeZone.UTC).getMillis - token.sessionTokenTime < constants.CommonConfig.sessionTokenTimeout))
        }


        def getResult(verify: Boolean, loginState: LoginState) = if (verify) f(Option(loginState))(request)
        else Future(throw new BaseException(constants.Response.INVALID_SESSION))

        for {
          verify <- verify
          result <- f(Option(LoginState(username, address)))(request)
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
