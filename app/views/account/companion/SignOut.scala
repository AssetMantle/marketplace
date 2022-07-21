package views.account.companion

import play.api.data.Form
import play.api.data.Forms.mapping

object SignOut {
  val form: Form[Data] = Form(
    mapping(
      constants.FormField.RECEIVE_NOTIFICATIONS.mapping,
    )(Data.apply)(Data.unapply)
  )

  case class Data(receiveNotifications: Boolean)

}
