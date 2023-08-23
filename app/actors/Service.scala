package actors

import actors.Message.{PrivateMessage, PublicMessage}
import akka.actor.{ActorRef, ActorSystem, PoisonPill, Props}
import akka.routing.{BroadcastRoutingLogic, Router}
import akka.stream.Materializer
import com.typesafe.config.ConfigFactory
import play.api.Logger

object Service {

  implicit val PrimaryActorSystem: ActorSystem = ActorSystem(constants.Actor.PRIMARY_ACTOR_SYSTEM, ConfigFactory.load)

  implicit val logger: Logger = Logger(this.getClass)

  implicit val materializer: Materializer = Materializer(PrimaryActorSystem)

  val PushNotificationActor: ActorRef = PrimaryActorSystem.actorOf(props = Props[PushNotificationActor]().withDispatcher("akka.actor.default-mailbox"), name = constants.Actor.ACTOR_PUSH_NOTIFICATION)

  val AppWebSocketActor: ActorRef = PrimaryActorSystem.actorOf(props = Props[AppWebSocketActor]().withDispatcher("akka.actor.appWebSocketActorMailBox"), name = constants.Actor.ACTOR_APP_WEB_SOCKET)

  private var publicRouter: Router = Router(BroadcastRoutingLogic(), Vector.empty)

  private var privateActorMap = Map[String, UserWebSocketActor]()

  // Should always be called using AppWebSocketActor mechanism otherwise queuing is lost
  def closeUserActor(username: String): Unit = {
    logger.debug("Closing actor for: " + username)
    privateActorMap.get(username).foreach(userWebSocketActor => {
      userWebSocketActor.self ! PoisonPill
      publicRouter = publicRouter.removeRoutee(userWebSocketActor.self)
    })
    privateActorMap -= username
  }

  // Should always be called using AppWebSocketActor mechanism otherwise queuing is lost
  def addOrUpdateUserActor(userWebSocketActor: UserWebSocketActor): Unit = {
    logger.debug("Adding actor for: " + userWebSocketActor.getUsername)
    closeUserActor(userWebSocketActor.getUsername)
    if (userWebSocketActor.getAddToPublic) publicRouter = publicRouter.addRoutee(userWebSocketActor.self)
    privateActorMap += (userWebSocketActor.getUsername -> userWebSocketActor)
  }

  // Can be handled via AppWebSocketActor but is there any advantage?
  def sendPrivateMessage(privateMessage: PrivateMessage): Unit = privateActorMap.get(privateMessage.toUser) match {
    case Some(userWebSocketActor) => userWebSocketActor.self ! privateMessage
    case None => logger.info(privateMessage.toUser + ": " + constants.Actor.ACTOR_NOT_FOUND)
  }

  // Can be handled via AppWebSocketActor but is there any advantage?
  def broadcastToAll(publicMessage: PublicMessage, sender: ActorRef = AppWebSocketActor): Unit = publicRouter.route(publicMessage, sender)

}