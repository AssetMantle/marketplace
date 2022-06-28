package actors

import actors.Message._
import akka.actor.{Actor, ActorLogging}
import play.api.Logger

import javax.inject.Singleton
import scala.concurrent.ExecutionContext

@Singleton
class PushNotificationActor() extends Actor with ActorLogging {

  private implicit val executionContext: ExecutionContext = context.dispatcher

  private implicit val logger: Logger = Logger(this.getClass)

  private implicit val module: String = constants.Actor.ACTOR_PUSH_NOTIFICATION

  def receive: PartialFunction[Any, Unit] = {
    case pushNotification: PushNotification => logger.info(pushNotification.token)
  }

}