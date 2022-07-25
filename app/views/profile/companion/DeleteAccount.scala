package views.profile.companion

import play.api.data.Form
import play.api.data.Forms.mapping

object DeleteAccount {

  val form: Form[Data] = Form(
    mapping(
      constants.FormField.PASSWORD.name-> constants.FormField.PASSWORD.field,
    )(Data.apply)(Data.unapply))

  case class Data(password: String)

}