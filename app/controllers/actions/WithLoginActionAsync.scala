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
                                      masterWallets: master.Wallets,
                                      masterTransactionSessionTokens: masterTransaction.SessionTokens,
                                    )(implicit executionContext: ExecutionContext, configuration: Configuration) extends AbstractController(messagesControllerComponents) with I18nSupport {

  private implicit val module: String = constants.Module.ACTIONS_WITH_LOGIN_ACTION

  def apply(f: ⇒ LoginState => Request[AnyContent] => Future[Result])(implicit logger: Logger): Action[AnyContent] = {
    withActionAsyncLoggingFilter.next { implicit request ⇒

      val username = Future(request.session.get(constants.Session.USERNAME).getOrElse(throw new BaseException(constants.Response.USERNAME_NOT_FOUND)))
      val address = Future(request.session.get(constants.Session.ADDRESS).getOrElse(throw new BaseException(constants.Response.ADDRESS_NOT_FOUND)))
      val token = Future(request.session.get(constants.Session.TOKEN).getOrElse(throw new BaseException(constants.Response.TOKEN_NOT_FOUND)))

      def verifyAndRefresh(username: String, address: String, sessionToken: String) = {
        val token = masterTransactionSessionTokens.Service.tryGet(username)
        val wallet = masterWallets.Service.tryGet(address)

        def checkAndRefresh(wallet: master.Wallet, token: masterTransaction.SessionToken) = if (wallet.accountId == username && token.sessionTokenHash == utilities.Secrets.sha256HashString(sessionToken) && (DateTime.now(DateTimeZone.UTC).getMillis - token.sessionTokenTime < constants.CommonConfig.sessionTokenTimeout)) {
          masterTransactionSessionTokens.Service.refresh(username)
        } else Future(throw new BaseException(constants.Response.INVALID_SESSION))

        for {
          token <- token
          wallet <- wallet
          _ <- checkAndRefresh(wallet, token)
        } yield ()
      }

      def getResult(loginState: LoginState): Future[Result] = f(loginState)(request)

      (for {
        username <- username
        address <- address
        token <- token
        _ <- verifyAndRefresh(username, address, token)
        result <- getResult(LoginState(username = username, address = address))
      } yield {
        result
      }).recover {
        case baseException: BaseException =>
          logger.info(baseException.failure.message, baseException)
          Results.Unauthorized(views.html.index()).withNewSession
      }
    }
  }
}
