package views.whiteList.companion

import play.api.data.Form
import play.api.data.Forms.mapping

object Create {

  val form: Form[Data] = Form(
    mapping(
      constants.FormField.WHITE_LIST_NAME.mapping,
      constants.FormField.WHITE_LIST_DESCRIPTION.mapping,
      constants.FormField.WHITE_LIST_MAX_MEMBERS.mapping,
    )(Data.apply)(Data.unapply))

  case class Data(name: String, description: String, maxMembers: Int)

}
