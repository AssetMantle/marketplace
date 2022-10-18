package views.profile.wishlist.companion

import play.api.data.Form
import play.api.data.Forms.mapping

object AddOrRemove {

  val form: Form[Data] = Form(
    mapping(
      constants.FormField.NFT_FILE_NAME.mapping,
    )(Data.apply)(Data.unapply))

  case class Data(nftFileName: String)

}
