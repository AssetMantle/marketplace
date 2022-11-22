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
                                         masterAccounts: master.Accounts,
                                         masterKeys: master.Keys,
                                       )(implicit executionContext: ExecutionContext) extends AbstractController(messagesControllerComponents) with I18nSupport {

  private implicit val module: String = constants.Module.ASYNC_ACTIONS_WITHOUT_LOGIN

  def apply(f: => Option[LoginState] => Request[AnyContent] => Future[Result])(implicit logger: Logger): Action[AnyContent] = {
    withActionAsyncLogging { implicit request =>
      val username = request.session.get(constants.Session.USERNAME).getOrElse("")
      val currentSessionToken = request.session.get(constants.Session.TOKEN).getOrElse("")
      val address = request.session.get(constants.Session.ADDRESS).getOrElse("")

      def verifyAndGetResult(username: String, address: String, currentSessionToken: String) = if (username != "" && address != "" && currentSessionToken != "") {
        val verify = {
          val storedSessionToken = masterTransactionSessionTokens.Service.tryGet(username)
          val key = masterKeys.Service.tryGetActive(username)
          val account = masterAccounts.Service.tryGet(username)

          for {
            storedSessionToken <- storedSessionToken
            key <- key
            account <- account
          } yield (utilities.Session.verify(username = username, key = key, address = address, storedSessionToken = storedSessionToken, currentSessionToken = currentSessionToken), account)
        }

        def getResult(verify: Boolean, loginState: LoginState) = if (verify) f(Option(loginState))(request)
        else constants.Response.INVALID_SESSION.throwFutureBaseException()

        for {
          (verify, account) <- verify
          result <- getResult(verify, LoginState(username, address, isCreator = account.isCreator, isVerifiedCreator = account.isVerifiedCreator))
        } yield result
      } else f(None)(request)

      (for {
        result <- verifyAndGetResult(username = username, address = address, currentSessionToken = currentSessionToken)
      } yield result).recover {
        case baseException: BaseException => Results.InternalServerError(views.html.index(failures = Seq(baseException.failure))).withNewSession
      }
    }
  }
}
