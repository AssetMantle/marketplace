package views.account.companion

import play.api.data.Form
import play.api.data.Forms.mapping

object SignUpBootstrap {

  val form: Form[Data] = Form(
    mapping(
      constants.FormField.USERNAME.name -> constants.FormField.USERNAME.field,
      constants.FormField.USERNAME_AVAILABLE.name -> constants.FormField.USERNAME_AVAILABLE.field,
      constants.FormField.SIGNUP_PASSWORD.name -> constants.FormField.SIGNUP_PASSWORD.field,
      constants.FormField.SIGNUP_CONFIRM_PASSWORD.name -> constants.FormField.SIGNUP_CONFIRM_PASSWORD.field,
    )(Data.apply)(Data.unapply))

  case class Data(username: String, usernameAvailable: Boolean, password: String, confirmPassword: String)

}
