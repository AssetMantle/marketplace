package actors

import actors.Message._
import akka.actor.{Actor, ActorRef, ActorSystem}
import akka.dispatch.{PriorityGenerator, UnboundedStablePriorityMailbox}
import com.typesafe.config.Config
import play.api.Logger

import javax.inject.Singleton

@Singleton
class AppWebSocketActor extends Actor {

  private implicit val logger: Logger = Logger(this.getClass)

  private implicit val module: String = constants.Actor.ACTOR_APP_WEB_SOCKET

  def getSender: ActorRef = sender()

  def receive = {
    case addActor: AddActor => Service.addOrUpdateUserActor(addActor.userWebSocketActor)
    case removeActor: RemoveActor => Service.closeUserActor(removeActor.username)
    case _ => logger.error(constants.Actor.UNKNOWN_MESSAGE_TYPE)
  }
}

class AppWebSocketActorMailBox(settings: ActorSystem.Settings, config: Config) extends UnboundedStablePriorityMailbox(
  PriorityGenerator {
    case _: RemoveActor => 0
    case _: AddActor => 1
    case _ => 2
  })