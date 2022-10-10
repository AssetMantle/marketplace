package views.profile.whitelist.companion

import play.api.data.Form
import play.api.data.Forms.mapping

object AcceptOrLeave {

  val form: Form[Data] = Form(
    mapping(
      constants.FormField.WHITELIST_ID.mapping,
    )(Data.apply)(Data.unapply))

  case class Data(whitelistId: String)

}
