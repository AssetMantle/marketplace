package views.profile.companion

import play.api.data.Form
import play.api.data.Forms.mapping

object WalletMnemonics {

  val form: Form[Data] = Form(
    mapping(
      constants.FormField.SEED_PHRASE_1.name-> constants.FormField.SEED_PHRASE_1.field,
      constants.FormField.SEED_PHRASE_2.name-> constants.FormField.SEED_PHRASE_2.field,
  )(Data.apply)(Data.unapply))

  case class Data(seed1: String, seed2: String)

}