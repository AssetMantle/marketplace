package views.account.companion

import play.api.data.Form
import play.api.data.Forms.mapping

object SignUp {

  val form: Form[Data] = Form(
    mapping(
      constants.FormField.USERNAME.mapping,
      constants.FormField.USERNAME_AVAILABLE.mapping,
      constants.FormField.SIGNUP_TERMS_CONDITIONS.mapping,
    )(Data.apply)(Data.unapply).verifying(constants.FormConstraint.signUpConstraint))

  case class Data(username: String, usernameAvailable: Boolean, termsCondition: Boolean)

}
