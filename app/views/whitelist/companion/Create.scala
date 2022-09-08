package views.whitelist.companion

import play.api.data.Form
import play.api.data.Forms.mapping

object Create {

  val form: Form[Data] = Form(
    mapping(
      constants.FormField.WHITELIST_NAME.mapping,
      constants.FormField.WHITELIST_DESCRIPTION.mapping,
      constants.FormField.WHITELIST_MAX_MEMBERS.mapping,
      constants.FormField.WHITELIST_INVITE_START_EPOCH.mapping,
      constants.FormField.WHITELIST_INVITE_END_EPOCH.mapping,
    )(Data.apply)(Data.unapply))

  case class Data(name: String, description: String, maxMembers: Int, startEpoch: Int, endEpoch: Int)

}
