package actors

import akka.actor.{ActorRef, ActorSystem, Props}
import akka.stream.Materializer
import com.typesafe.config.ConfigFactory
import play.api.Logger

object Service {

  implicit val PrimaryActorSystem: ActorSystem = ActorSystem(constants.Actor.PRIMARY_ACTOR_SYSTEM, ConfigFactory.load)

  implicit val logger: Logger = Logger(this.getClass)

  implicit val materializer: Materializer = Materializer(PrimaryActorSystem)

  val PushNotificationActor: ActorRef = PrimaryActorSystem.actorOf(props = Props[PushNotificationActor]().withDispatcher("akka.actor.default-mailbox"), name = constants.Actor.ACTOR_PUSH_NOTIFICATION)

  val AppWebSocketActor: ActorRef = PrimaryActorSystem.actorOf(props = Props[AppWebSocketActor]().withDispatcher("akka.actor.appWebSocketActorMailBox"), name = constants.Actor.ACTOR_APP_WEB_SOCKET)

}