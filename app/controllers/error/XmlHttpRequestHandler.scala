package controllers.error


import play.api._
import play.api.http.DefaultHttpErrorHandler
import play.api.i18n.MessagesApi
import play.api.i18n.{Lang, Langs, MessagesImpl, MessagesProvider}
import play.api.mvc.Results.{BadRequest, Status}
import play.api.mvc._
import play.api.routing.Router

import javax.inject._
import scala.concurrent._

@Singleton
class XmlHttpRequestHandler @Inject()(
                                       environment: Environment,
                                       configuration: Configuration,
                                       sourceMapper: OptionalSourceMapper,
                                       router: Provider[Router],
                                       langs: Langs,
                                       messagesApi: MessagesApi
                                     ) extends DefaultHttpErrorHandler(environment, configuration, sourceMapper, router) {

  override def onClientError( request: RequestHeader, statusCode: Int, message: String): Future[Result] = {


    implicit val messagesProvider: MessagesProvider = {
      MessagesImpl(request.transientLang().getOrElse(Lang("en")), messagesApi)
    }


    Future.successful(
      BadRequest(views.html.pageNotFound()(request, messagesProvider))
    )


  }

}
