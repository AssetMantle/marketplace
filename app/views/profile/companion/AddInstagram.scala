package views.profile.companion

import play.api.data.Form
import play.api.data.Forms.mapping

object AddInstagram {

  val form: Form[Data] = Form(
    mapping(
      constants.FormField.INSTAGRAM_USERNAME.mapping,
    )(Data.apply)(Data.unapply))

  case class Data(bio: String)

}