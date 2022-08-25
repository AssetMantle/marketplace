package views.whiteList.companion

import play.api.data.Form
import play.api.data.Forms.mapping

object Invite {

  val form: Form[Data] = Form(
    mapping(
      constants.FormField.WHITE_LIST_ID.mapping,
      constants.FormField.WHITE_LIST_INVITE_START_EPOCH.mapping,
      constants.FormField.WHITE_LIST_INVITE_END_EPOCH.mapping,
    )(Data.apply)(Data.unapply))

  case class Data(whiteListId: String, startEpoch: Int, endEpoch: Int)

}
