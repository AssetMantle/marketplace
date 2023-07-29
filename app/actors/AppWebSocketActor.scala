package actors

import actors.Message._
import akka.actor.{Actor, ActorRef, ActorSystem, PoisonPill}
import akka.dispatch.{PriorityGenerator, UnboundedStablePriorityMailbox}
import akka.routing.{BroadcastRoutingLogic, Router}
import com.typesafe.config.Config
import play.api.Logger
import play.api.libs.json.{Json, OWrites}

import javax.inject.Singleton

@Singleton
class AppWebSocketActor extends Actor {

  private implicit val logger: Logger = Logger(this.getClass)

  private implicit val module: String = constants.Actor.ACTOR_APP_WEB_SOCKET

  case class Message(messageType: String, messageValue: MessageValue) {
    def toJsonString: String = Json.toJson(Message(messageType = messageType, messageValue = messageValue)).toString
  }

  implicit val messageWrites: OWrites[Message] = Json.writes[Message]

  private var publicRouter: Router = {
    val routees = Vector.empty
    Router(BroadcastRoutingLogic(), routees)
  }

  private var privateActorMap = Map[String, ActorRef]()

  private def broadcastToAll(message: Message): Unit = publicRouter.route(message.toJsonString, sender())

  private def broadcastToUser(username: String, messageValue: MessageValue, messageType: String): Unit = privateActorMap.get(username) match {
    case Some(actorRef) => actorRef ! Message(messageType, messageValue).toJsonString
    case None => logger.info(username + ": " + constants.Actor.ACTOR_NOT_FOUND)
  }

  private def closeUserActor(username: String): Unit = {
    privateActorMap.get(username).foreach(userActor => {
      userActor ! PoisonPill
      publicRouter = publicRouter.removeRoutee(userActor)
    })
    privateActorMap -= username
  }

  private def addOrUpdateUserActor(addActor: AddActor): Unit = {
    closeUserActor(addActor.username)
    if (addActor.addToPublic) publicRouter = publicRouter.addRoutee(addActor.actorRef)
    privateActorMap += (addActor.username -> addActor.actorRef)
  }

  def receive = {
    case chat: Chat => broadcastToUser(username = chat.toUser, messageValue = chat, messageType = constants.Actor.MessageType.CHAT)
    case asset: Asset => broadcastToUser(username = asset.toUser, messageValue = asset, messageType = constants.Actor.MessageType.ASSET)
    case notification: Notification => broadcastToUser(username = notification.toUser, messageValue = notification, messageType = constants.Actor.MessageType.NOTIFICATION)
    case addActor: AddActor => addOrUpdateUserActor(addActor)
    case removeActor: RemoveActor => closeUserActor(removeActor.username)
    case _ => logger.error(constants.Actor.UNKNOWN_MESSAGE_TYPE)
  }
}

class AppWebSocketActorMailBox(settings: ActorSystem.Settings, config: Config) extends UnboundedStablePriorityMailbox(
  PriorityGenerator {
    case _: RemoveActor => 0
    case _: AddActor => 1
    case _: Notification => 2
    case _ => 3
  })