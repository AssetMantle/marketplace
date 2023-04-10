package views.profile.whitelist.companion

import play.api.data.Form
import play.api.data.Forms.mapping

object AddFromNFTOwners {

  val form: Form[Data] = Form(
    mapping(
      constants.FormField.WHITELIST_ID.mapping,
      constants.FormField.SELECT_COLLECTION_ID.mapping,
    )(Data.apply)(Data.unapply))

  case class Data(whitelistId: String, collectionId: String)

}
