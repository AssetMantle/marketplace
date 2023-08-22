package actors

import actors.Message._
import akka.actor._

object UserWebSocketActor {
  def props(username: String, addToPublic: Boolean, out: ActorRef): Props = Props(new UserWebSocketActor(username, addToPublic, out))
}

class UserWebSocketActor(username: String, addToPublic: Boolean, out: ActorRef) extends Actor {

  def receive = {
    case msg: String => if (msg == "START") actors.Service.addOrUpdateUserActor(AddActor(username = username, addToPublic = addToPublic, actorRef = out))
  }

  override def postStop(): Unit = actors.Service.closeUserActor(username)
}
