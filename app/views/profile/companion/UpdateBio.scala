package views.profile.companion

import play.api.data.Form
import play.api.data.Forms.mapping

object UpdateBio {

  val form: Form[Data] = Form(
    mapping(
      constants.FormField.USER_BIO.mapping,
    )(Data.apply)(Data.unapply))

  case class Data(bio: String)

}