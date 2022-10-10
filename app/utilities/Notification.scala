package utilities

import exceptions.BaseException
import models.{master, masterTransaction}
import play.api.i18n.{Lang, MessagesApi}
import play.api.libs.json.{Json, OWrites}
import play.api.libs.ws.WSClient
import play.api.{Configuration, Logger}

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class Notification @Inject()(masterTransactionNotifications: masterTransaction.Notifications,
                             masterTransactionPushNotificationTokens: masterTransaction.PushNotificationTokens,
                             wsClient: WSClient,
                             masterAccounts: master.Accounts,
                             messagesApi: MessagesApi,
                            )(implicit executionContext: ExecutionContext, configuration: Configuration) {

  private implicit val module: String = constants.Module.UTILITIES_NOTIFICATION

  private implicit val logger: Logger = Logger(this.getClass)

  private val CONTENT_TYPE = "Content-Type"
  private val AUTHORIZATION = "Authorization"
  private val APPLICATION_JSON = "application/json"

  private case class Notification(title: String, body: String)

  private case class Data(to: String, notification: Notification)

  private implicit val notificationWrites: OWrites[Notification] = Json.writes[Notification]

  private implicit val dataWrites: OWrites[Data] = Json.writes[Data]

  private def sendPushNotification(accountID: String, pushNotification: constants.Notification.PushNotification, messageParameters: String*)(implicit lang: Lang): Future[Unit] = {
    val pushNotificationToken = masterTransactionPushNotificationTokens.Service.getPushNotificationToken(accountID)

    val title = messagesApi(pushNotification.title)
    val message = messagesApi(pushNotification.message, messageParameters: _*)

    def post(title: String, message: String, pushNotificationToken: String) = try {
      wsClient.url(constants.CommonConfig.PushNotificationURL).withHttpHeaders(CONTENT_TYPE -> APPLICATION_JSON).withHttpHeaders(AUTHORIZATION -> ("key=" + constants.CommonConfig.PushNotificationAuthorizationKey)).post(Json.toJson(Data(pushNotificationToken, Notification(title, message))))
    } catch {
      case exception: Exception => logger.error(exception.getLocalizedMessage)
        Future()
    }

    (for {
      pushNotificationToken <- pushNotificationToken
      _ <- pushNotificationToken.map(token => post(title, message, token)).getOrElse(Future())
    } yield ()
      ).recover {
      case baseException: BaseException => logger.info(baseException.failure.message, baseException)
    }
  }

  def send(accountID: String, notification: constants.Notification, messagesParameters: String*)(routeParameters: String*): Future[Unit] = {
    val language = masterAccounts.Service.getLanguage(accountID)
    val notificationID = masterTransactionNotifications.Service.add(accountID = accountID, notification = notification, messagesParameters: _*)(routeParameters: _*)

    def pushNotification(implicit language: Lang): Future[Unit] = notification.pushNotification.fold(Future())(pushNotification => sendPushNotification(accountID = accountID, pushNotification = pushNotification, messageParameters = messagesParameters: _*))

    (for {
      language <- language
      notificationID <- notificationID
//      _ <- pushNotification(language)
    } yield ()
      ).recover {
      case baseException: BaseException => logger.error(baseException.failure.message, baseException)
    }
  }

}
