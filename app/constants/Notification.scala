package constants

object Notification {

  val PUSH_NOTIFICATION_PREFIX = "PUSH_NOTIFICATION"
  val SUBJECT_SUFFIX = "SUBJECT"
  val MESSAGE_SUFFIX = "MESSAGE"
  val TITLE_SUFFIX = "TITLE"

  class PushNotification(private val notificationType: String) {
    val title: String = Seq(PUSH_NOTIFICATION_PREFIX, notificationType, TITLE_SUFFIX).mkString(".")
    val message: String = Seq(PUSH_NOTIFICATION_PREFIX, notificationType, MESSAGE_SUFFIX).mkString(".")
  }


}
