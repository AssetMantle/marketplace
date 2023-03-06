package views.wallet.companion

import play.api.data.Form
import play.api.data.Forms.mapping

object UnwrapToken {
  val form: Form[Data] = Form(
    mapping(
      constants.FormField.PASSWORD.mapping,
    )(Data.apply)(Data.unapply))

  case class Data(password: String)
}
