package controllers

import controllers.actions._
import play.api.Logger
import play.api.cache.Cached
import play.api.i18n.I18nSupport
import play.api.mvc._

import javax.inject._
import scala.concurrent.ExecutionContext

@Singleton
class IndexController @Inject()(
                                messagesControllerComponents: MessagesControllerComponents,
                                cached: Cached,
                                withoutLoginActionAsync: WithoutLoginActionAsync,
                                withoutLoginAction: WithoutLoginAction
                              )(implicit executionContext: ExecutionContext) extends AbstractController(messagesControllerComponents) with I18nSupport {

  private implicit val logger: Logger = Logger(this.getClass)

  private implicit val module: String = constants.Module.HOME_CONTROLLER

  def index: EssentialAction = cached.apply(req => req.path, constants.CommonConfig.webAppCacheDuration) {
    withoutLoginAction { implicit request =>
      Ok(views.html.index())
    }
  }
}
