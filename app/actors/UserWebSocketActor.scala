package actors

import actors.Message.{PrivateMessage, PublicMessage}
import akka.actor._

object UserWebSocketActor {
  def props(username: String, addToPublic: Boolean, out: ActorRef): Props = Props(new UserWebSocketActor(username, addToPublic, out))
}

class UserWebSocketActor(username: String, addToPublic: Boolean, out: ActorRef) extends Actor {

  def getUsername: String = this.username

  def getAddToPublic: Boolean = this.addToPublic

  def receive = {
    case msg: String => if (msg == "START") Service.AppWebSocketActor ! Message.AddActor(this)
    case publicMessage: PublicMessage => this.out ! publicMessage.toClientMessageString
    case privateMessage: PrivateMessage => this.out ! privateMessage.toClientMessageString
  }

  override def postStop(): Unit = Service.AppWebSocketActor ! Message.RemoveActor(this.username)
}
