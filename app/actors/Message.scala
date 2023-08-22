package actors

import akka.actor.ActorRef
import play.api.libs.json.{Json, OWrites, Writes}

object Message {

  case class PushNotification(token: String, pushNotification: constants.Notification.PushNotification, messageParameters: Seq[String])

  case class AddActor(username: String, addToPublic: Boolean, actorRef: ActorRef)

  case class RemoveActor(username: String)

  abstract class MessageValue {
    def getMessageType: String

    def toClientMessageString: String
  }

  case class ClientMessage(messageType: String, messageValue: MessageValue)

  abstract class PublicMessage extends MessageValue {
    def toClientMessageString: String = Json.toJson(ClientMessage(messageType = constants.Actor.MessageType.PUBLIC_MESSAGE, messageValue = this)).toString()
  }

  abstract class PrivateMessage extends MessageValue {
    val toUser: String

    def toClientMessageString: String = Json.toJson(ClientMessage(messageType = this.getMessageType, messageValue = this)).toString()
  }

  case class Chat(toUser: String, chatID: String, chatMessage: String) extends PrivateMessage {
    def getMessageType: String = constants.Actor.MessageType.CHAT
  }

  implicit val chatWrites: OWrites[Chat] = Json.writes[Chat]

  case class Asset(toUser: String, nftId: String, collectionId: String, operation: String) extends PrivateMessage {
    def getMessageType: String = constants.Actor.MessageType.ASSET
  }

  implicit val assetWrites: OWrites[Asset] = Json.writes[Asset]

  case class Notification(toUser: String, id: String, message: String, title: String, notificationType: String) extends PrivateMessage {
    def getMessageType: String = constants.Actor.MessageType.NOTIFICATION
  }

  implicit val notificationWrites: OWrites[Notification] = Json.writes[Notification]

  implicit val messageValueWrites: Writes[MessageValue] = {
    case chat: Chat => Json.toJson(chat)
    case asset: Asset => Json.toJson(asset)
    case notification: Notification => Json.toJson(notification)
    case _ => throw new IllegalArgumentException("UNKNOWN_ACTOR_MESSAGE_TYPE")
  }

  implicit val clientMessageWrites: OWrites[ClientMessage] = Json.writes[ClientMessage]

}
