package actors

import akka.actor._

object UserWebSocketActor {
  def props(username: String, addToPublic: Boolean, out: ActorRef): Props = Props(new UserWebSocketActor(username, addToPublic, out))
}

class UserWebSocketActor(username: String, addToPublic: Boolean, out: ActorRef) extends Actor {

  def getUsername: String = this.username

  def getAddToPublic: Boolean = this.addToPublic

  def getOutActorRef: ActorRef = this.out

  def receive = {
    case msg: String => if (msg == "START") actors.Service.addOrUpdateUserActor(this)
  }

  override def postStop(): Unit = actors.Service.closeUserActor(username)
}
