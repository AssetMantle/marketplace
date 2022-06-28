package actors

import akka.actor.{ActorRef, ActorSystem, Props}
import akka.stream.Materializer
import com.typesafe.config.ConfigFactory
import play.api.Logger

object Service {

  implicit val actorSystem: ActorSystem = ActorSystem(constants.Actor.PRIMARY_ACTOR, ConfigFactory.load)

  private implicit val logger: Logger = Logger(this.getClass)

  implicit val materializer: Materializer = Materializer(actorSystem)

  val pushNotificationActor: ActorRef = actorSystem.actorOf(props = Props[PushNotificationActor]().withDispatcher("akka.actor.default-mailbox"), name = constants.Actor.ACTOR_PUSH_NOTIFICATION)

}