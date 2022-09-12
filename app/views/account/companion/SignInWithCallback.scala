package views.account.companion

import play.api.data.Form
import play.api.data.Forms.mapping

object SignInWithCallback {

  val form: Form[Data] = Form(
    mapping(
      constants.FormField.USERNAME.mapping,
      constants.FormField.PASSWORD.mapping,
      constants.FormField.PUSH_NOTIFICATION_TOKEN.mapping,
      constants.FormField.CALLBACK_URL.mapping,
    )(Data.apply)(Data.unapply))

  case class Data(username: String, password: String, pushNotificationToken: String, callbackUrl: String)

}
