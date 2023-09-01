package controllers.logging

import exceptions.BaseException
import play.api.Logger
import play.api.i18n.{I18nSupport, Lang, MessagesApi}
import play.api.libs.Files.TemporaryFile
import play.api.mvc._

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class WithMultipartFormActionAsyncLogging @Inject()(messagesControllerComponents: MessagesControllerComponents, messagesApi: MessagesApi)(implicit executionContext: ExecutionContext) extends AbstractController(messagesControllerComponents) with I18nSupport {

  private implicit val lang: Lang = constants.CommonConfig.LogLang

  def apply(f: => Request[MultipartFormData[TemporaryFile]] => Future[Result])(implicit logger: Logger): Action[MultipartFormData[TemporaryFile]] = Action(parse.multipartFormData).async { implicit request =>
    val startTime = System.currentTimeMillis()
    val remoteIp = request.headers.get(constants.Session.FORWARDED_IP_ADDRESS_HEADER).getOrElse(request.remoteAddress)
    logger.info(messagesApi(constants.Session.Log.CONTROLLERS_REQUEST, request.method, request.uri, remoteIp, request.session.get(constants.Session.USERNAME).getOrElse(constants.View.UNKNOWN)))
    val result = f(request)
    (for {
      result <- result
    } yield {
      val endTime = System.currentTimeMillis()
      logger.info(messagesApi(constants.Session.Log.CONTROLLERS_RESPONSE, request.method, request.uri, remoteIp, request.session.get(constants.Session.USERNAME).getOrElse(constants.View.UNKNOWN), result.header.status, endTime - startTime))
      result
    }).recover {
      case baseException: BaseException =>
        val endTime = System.currentTimeMillis()
        logger.info(messagesApi(constants.Session.Log.CONTROLLERS_RESPONSE, request.method, request.uri, remoteIp, request.session.get(constants.Session.USERNAME).getOrElse(constants.View.UNKNOWN), Results.InternalServerError.header.status, endTime - startTime))
        Results.InternalServerError(views.html.index())
    }
  }
}