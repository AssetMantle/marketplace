package controllers

import controllers.actions._
import controllers.result.WithUsernameToken
import play.api.Logger
import play.api.cache.Cached
import play.api.i18n.I18nSupport
import play.api.mvc._
import service.UploadCollections

import javax.inject._
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class IndexController @Inject()(
                                 messagesControllerComponents: MessagesControllerComponents,
                                 cached: Cached,
                                 withoutLoginActionAsync: WithoutLoginActionAsync,
                                 withoutLoginAction: WithoutLoginAction,
                                 withUsernameToken: WithUsernameToken,
                                 uploadCollections: UploadCollections
                               )(implicit executionContext: ExecutionContext) extends AbstractController(messagesControllerComponents) with I18nSupport {

  private implicit val logger: Logger = Logger(this.getClass)

  private implicit val module: String = constants.Module.HOME_CONTROLLER

  def index: EssentialAction = cached.apply(req => req.path, constants.CommonConfig.webAppCacheDuration) {
    withoutLoginActionAsync { implicit loginState =>
      implicit request =>
        loginState match {
          case Some(loginState) =>
            implicit val loginStateImplicit: LoginState = loginState
            withUsernameToken.Ok(views.html.collections())
          case None => Future(Ok(views.html.index()))
        }

    }
  }

  uploadCollections.start()
}
