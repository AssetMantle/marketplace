package views.account.companion

import play.api.data.Form
import play.api.data.Forms.mapping

object ForgotPasswordSeedPhrase {

  val form: Form[Data] = Form(
    mapping(
      constants.FormField.SEED_PHRASE_1.name -> constants.FormField.SEED_PHRASE_1.field,
      constants.FormField.SEED_PHRASE_2.name -> constants.FormField.SEED_PHRASE_2.field,
      constants.FormField.SEED_PHRASE_3.name -> constants.FormField.SEED_PHRASE_3.field,
      constants.FormField.SEED_PHRASE_4.name -> constants.FormField.SEED_PHRASE_4.field,
    )(Data.apply)(Data.unapply))

  case class Data(phrase1: String, phrase2: String, phrase3: String, phrase4: String)

}
