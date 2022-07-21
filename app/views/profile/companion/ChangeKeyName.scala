package views.profile.companion

import play.api.data.Form
import play.api.data.Forms.mapping

object ChangeKeyName {

  val form: Form[Data] = Form(
    mapping(
      constants.FormField.CHANGE_KEY_ADDRESS.mapping,
      constants.FormField.CHANGE_KEY_ADDRESS.mapping
    )(Data.apply)(Data.unapply))

  case class Data(address: String, keyName: String)

}