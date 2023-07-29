package actors

import akka.actor.ActorRef
import play.api.libs.json.{Json, OWrites, Writes}

object Message {

  case class PushNotification(token: String, pushNotification: constants.Notification.PushNotification, messageParameters: Seq[String])

  case class AddActor(username: String, addToPublic: Boolean, actorRef: ActorRef)

  case class RemoveActor(username: String)

  abstract class MessageValue

  abstract class PrivateMessageContent extends MessageValue {
    val toUser: String
  }

  case class Chat(toUser: String, chatID: String, chatMessage: String) extends PrivateMessageContent

  implicit val chatWrites: OWrites[Chat] = Json.writes[Chat]

  case class Asset(toUser: String, nftId: String, collectionId: String, operation: String) extends PrivateMessageContent

  implicit val assetWrites: OWrites[Asset] = Json.writes[Asset]

  case class Notification(toUser: String, id: String, message: String, `type`: String) extends PrivateMessageContent

  implicit val notificationWrites: OWrites[Notification] = Json.writes[Notification]

  implicit val messageValueWrites: Writes[MessageValue] = {
    case chat: Chat => Json.toJson(chat)
    case asset: Asset => Json.toJson(asset)
    case notification: Notification => Json.toJson(notification)
  }

}
