package views.notification.companion

import play.api.data.Form
import play.api.data.Forms.mapping


object MarkNotificationsRead {

  val form: Form[Data] = Form(
    mapping(
      constants.FormField.NOTIFICATION_ID.optionalMapping,
      constants.FormField.MARK_ALL_NOTIFICATION_READ.mapping,
    )(Data.apply)(Data.unapply))

  case class Data(notificationId: Option[String], markAllRead: Boolean)

}
