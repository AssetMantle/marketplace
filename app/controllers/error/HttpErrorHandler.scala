package controllers.error


import play.api._
import play.api.http.DefaultHttpErrorHandler
import play.api.i18n._
import play.api.mvc.Results.BadRequest
import play.api.mvc._
import play.api.routing.Router

import javax.inject._
import scala.concurrent._

@Singleton
class HttpErrorHandler @Inject()(
                                       environment: Environment,
                                       configuration: Configuration,
                                       sourceMapper: OptionalSourceMapper,
                                       router: Provider[Router],
                                       messagesApi: MessagesApi,
                                     ) extends DefaultHttpErrorHandler(environment, configuration, sourceMapper, router) {

  override def onClientError(request: RequestHeader, statusCode: Int, message: String): Future[Result] = {
    implicit val messagesProvider: MessagesProvider = MessagesImpl(request.transientLang().getOrElse(Lang("en")), messagesApi)
    Future.successful(BadRequest(views.html.clientError()(request, messagesProvider)))
  }

  override def onServerError(request: RequestHeader, exception: Throwable): Future[Result] = {
    implicit val messagesProvider: MessagesProvider = MessagesImpl(request.transientLang().getOrElse(Lang("en")), messagesApi)
    Future.successful(BadRequest(views.html.serverError()(request, messagesProvider)))
  }

}
