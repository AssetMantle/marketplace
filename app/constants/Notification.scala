package constants

import constants.Notification._
import play.api.routing.JavaScriptReverseRoute

case class Notification(name: String, sendEmail: Boolean, sendPushNotification: Boolean, sendSMS: Boolean, route: Option[JavaScriptReverseRoute] = None) {

  def email: Option[Email] = if (sendEmail) Option(Email(name)) else None

  def pushNotification: Option[PushNotification] = if (sendPushNotification) Option(PushNotification(name)) else None

  def sms: Option[SMS] = if (sendSMS) Option(SMS(name)) else None

}

object Notification {

  case class SMS(name: String) {
    def message: String = Seq("SMS", name, "MESSAGE").mkString(".")
  }

  case class PushNotification(name: String) {
    def title: String = Seq("PUSH_NOTIFICATION", name, "TITLE").mkString(".")

    def message: String = Seq("PUSH_NOTIFICATION", name, "MESSAGE").mkString(".")
  }

  case class Email(name: String) {
    def subject: String = Seq("EMAIL", name, "SUBJECT").mkString(".")

    def message: String = Seq("EMAIL", name, "MESSAGE").mkString(".")
  }

  val LOGIN: Notification = Notification("LOGIN", sendEmail = false, sendPushNotification = true, sendSMS = false)
  val LOG_OUT: Notification = Notification("LOG_OUT", sendEmail = false, sendPushNotification = true, sendSMS = false)

}
