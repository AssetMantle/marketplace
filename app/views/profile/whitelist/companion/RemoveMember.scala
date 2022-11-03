package views.profile.whitelist.companion

import play.api.data.Form
import play.api.data.Forms.mapping

object RemoveMember {

  val form: Form[Data] = Form(
    mapping(
      constants.FormField.WHITELIST_ID.mapping,
      constants.FormField.USERNAME.mapping,
    )(Data.apply)(Data.unapply))

  case class Data(whitelistId: String, username: String)

}
