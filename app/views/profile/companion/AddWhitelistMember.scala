package views.profile.companion

import play.api.data.Form
import play.api.data.Forms.mapping

object AddWhitelistMember {

  val form: Form[Data] = Form(
    mapping(
      constants.FormField.WHITELIST_MEMBER_NAME.mapping,
    )(Data.apply)(Data.unapply))

  case class Data(whitelistName: String)

}