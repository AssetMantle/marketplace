package controllers.logging

import exceptions.BaseException
import play.api.i18n.{I18nSupport, Lang, MessagesApi}
import play.api.mvc._
import play.api.{Configuration, Logger}

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class WithActionLogging @Inject()(messagesControllerComponents: MessagesControllerComponents, messagesApi: MessagesApi)(implicit executionContext: ExecutionContext) extends AbstractController(messagesControllerComponents) with I18nSupport {

  private implicit val lang: Lang = constants.CommonConfig.LogLang

  def apply(f: => Request[AnyContent] => Result)(implicit logger: Logger): Action[AnyContent] = Action { implicit request =>
    val startTime = System.currentTimeMillis()
    val remoteIp = request.headers.get(constants.Session.FORWARDED_IP_ADDRESS_HEADER).getOrElse("0.0.0.0")
    try {
      logger.info(messagesApi(constants.Log.Info.CONTROLLERS_REQUEST, request.method, request.uri, remoteIp, request.session.get(constants.Session.USERNAME).getOrElse(constants.View.UNKNOWN)))
      val result = f(request)
      val endTime = System.currentTimeMillis()
      logger.info(messagesApi(constants.Log.Info.CONTROLLERS_RESPONSE, request.method, request.uri, remoteIp, request.session.get(constants.Session.USERNAME).getOrElse(constants.View.UNKNOWN), result.header.status, endTime - startTime))
      result
    } catch {
      case baseException: BaseException =>
        val endTime = System.currentTimeMillis()
        logger.info(messagesApi(constants.Log.Info.CONTROLLERS_RESPONSE, request.method, request.uri, remoteIp, request.session.get(constants.Session.USERNAME).getOrElse(constants.View.UNKNOWN), Results.InternalServerError.header.status, endTime - startTime))
        Results.InternalServerError(views.html.index(failures = Seq(baseException.failure)))
    }

  }
}