package views.account.companion

import play.api.data.Form
import play.api.data.Forms.mapping

object ForgotPassword {

  val form: Form[Data] = Form(
    mapping(
      constants.FormField.USERNAME.mapping,
      constants.FormField.WALLET_ADDRESS.mapping,
      constants.FormField.SEED_PHRASE_1.mapping,
      constants.FormField.SEED_PHRASE_2.mapping,
      constants.FormField.SEED_PHRASE_3.mapping,
      constants.FormField.SEED_PHRASE_4.mapping,
      constants.FormField.FORGOT_PASSWORD.mapping,
      constants.FormField.FORGOT_CONFIRM_PASSWORD.mapping,
    )(Data.apply)(Data.unapply).verifying(constants.FormConstraint.forgotPasswordConstraint))

  case class Data(username: String, address: String, phrase1: String, phrase2: String, phrase3: String, phrase4: String, newPassword: String, confirmNewPassword: String)

}
