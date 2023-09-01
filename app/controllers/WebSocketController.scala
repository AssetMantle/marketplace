package controllers

import actors.UserWebSocketActor
import play.api.Logger
import play.api.i18n.I18nSupport
import play.api.libs.streams.ActorFlow
import play.api.mvc.{AbstractController, MessagesControllerComponents, WebSocket}

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class WebSocketController @Inject()(
                                     messagesControllerComponents: MessagesControllerComponents,
                                   )(implicit executionContext: ExecutionContext) extends AbstractController(messagesControllerComponents) with I18nSupport {

  private implicit val logger: Logger = Logger(this.getClass)

  private implicit val module: String = constants.Module.WEB_SOCKET_CONTROLLER

  def connect(): WebSocket = WebSocket.acceptOrResult[String, String] { request =>
    Future.successful(request.session.get(constants.Session.USERNAME) match {
      case None => Left(Forbidden)
      case Some(username) => Right(
        ActorFlow.actorRef { out =>
          UserWebSocketActor.props(username = username, addToPublic = false, out = out)
        }(actors.Service.PrimaryActorSystem, actors.Service.materializer)
      )
    })
  }

}
