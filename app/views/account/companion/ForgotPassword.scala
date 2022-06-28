package views.account.companion

import play.api.data.Form
import play.api.data.Forms.mapping

object ForgotPassword {

  val form: Form[Data] = Form(
    mapping(
      constants.FormField.USERNAME.name -> constants.FormField.USERNAME.field,
      constants.FormField.WALLET_ADDRESS.name -> constants.FormField.WALLET_ADDRESS.field,
      constants.FormField.SEED_PHRASE_1.name -> constants.FormField.SEED_PHRASE_1.field,
      constants.FormField.SEED_PHRASE_2.name -> constants.FormField.SEED_PHRASE_2.field,
      constants.FormField.SEED_PHRASE_3.name -> constants.FormField.SEED_PHRASE_3.field,
      constants.FormField.SEED_PHRASE_4.name -> constants.FormField.SEED_PHRASE_4.field,
      constants.FormField.FORGOT_PASSWORD.name -> constants.FormField.FORGOT_PASSWORD.field,
      constants.FormField.FORGOT_CONFIRM_PASSWORD.name -> constants.FormField.FORGOT_CONFIRM_PASSWORD.field,
    )(Data.apply)(Data.unapply).verifying(constants.FormConstraint.forgotPasswordConstraint))

  case class Data(username: String, address: String, phrase1: String, phrase2: String, phrase3: String, phrase4: String, newPassword: String, confirmNewPassword: String)

}
