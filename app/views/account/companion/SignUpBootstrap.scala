package views.account.companion

import play.api.data.Form
import play.api.data.Forms.mapping

object SignUpBootstrap {

  val form: Form[Data] = Form(
    mapping(
      constants.FormField.USERNAME.mapping,
      constants.FormField.USERNAME_AVAILABLE.mapping,
      constants.FormField.SIGNUP_PASSWORD.mapping,
      constants.FormField.SIGNUP_CONFIRM_PASSWORD.mapping,
    )(Data.apply)(Data.unapply))

  case class Data(username: String, usernameAvailable: Boolean, password: String, confirmPassword: String)

}
