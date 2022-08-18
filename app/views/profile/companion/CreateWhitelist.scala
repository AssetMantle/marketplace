package views.profile.companion

import play.api.data.Form
import play.api.data.Forms.mapping

object CreateWhitelist {

  val form: Form[Data] = Form(
    mapping(
      constants.FormField.WHITELIST_NAME.mapping,
      constants.FormField.WHITELIST_MAX_MEMBERS.mapping,
    )(Data.apply)(Data.unapply))

  case class Data(whitelistName: String, whitelistMembers: Int)

}