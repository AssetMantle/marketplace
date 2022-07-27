package controllers.result

import constants.Session
import controllers.actions.LoginState
import models.masterTransaction
import play.api.http.Writeable
import play.api.mvc.{RequestHeader, Result, Results}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class WithUsernameToken @Inject()(masterTransactionSessionTokens: masterTransaction.SessionTokens)(implicit executionContext: ExecutionContext) {

  def Ok[C](content: C)(implicit request: RequestHeader, writeable: Writeable[C], loginState: LoginState): Future[Result] = {
    val newToken = masterTransactionSessionTokens.Service.refresh(loginState.username)
    for {
      newToken <- newToken
    } yield Results.Ok(content).withSession(request.session
      - Session.TOKEN + (Session.TOKEN -> newToken)
      - Session.USERNAME + (Session.USERNAME -> loginState.username)
      - Session.ADDRESS + (Session.ADDRESS -> loginState.address))
  }

  def Ok()(implicit request: RequestHeader, loginState: LoginState): Future[Result] = {
    val newToken = masterTransactionSessionTokens.Service.refresh(loginState.username)
    for {
      newToken <- newToken
    } yield Results.Ok.withSession(request.session
      - Session.TOKEN + (Session.TOKEN -> newToken)
      - Session.USERNAME + (Session.USERNAME -> loginState.username)
      - Session.ADDRESS + (Session.ADDRESS -> loginState.address))
  }

  def PartialContent[C](content: C)(implicit request: RequestHeader, writeable: Writeable[C], loginState: LoginState): Future[Result] = {
    val newToken = masterTransactionSessionTokens.Service.refresh(loginState.username)
    for {
      newToken <- newToken
    } yield Results.PartialContent(content).withSession(request.session
      - Session.TOKEN + (Session.TOKEN -> newToken)
      - Session.USERNAME + (Session.USERNAME -> loginState.username)
      - Session.ADDRESS + (Session.ADDRESS -> loginState.address))
  }
}