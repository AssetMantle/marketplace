package views.account.companion

import play.api.data.Form
import play.api.data.Forms.mapping

object ForgotPasswordNewPassword {

  val form: Form[Data] = Form(
    mapping(
      constants.FormField.FORGOT_PASSWORD.name -> constants.FormField.FORGOT_PASSWORD.field,
      constants.FormField.FORGOT_CONFIRM_PASSWORD.name -> constants.FormField.FORGOT_CONFIRM_PASSWORD.field,
    )(Data.apply)(Data.unapply))

  case class Data(password: String, confirmPassword: String)

}
