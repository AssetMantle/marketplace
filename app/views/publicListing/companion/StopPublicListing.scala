package views.publicListing.companion

import play.api.data.Form
import play.api.data.Forms.mapping

object StopPublicListing {
  val form: Form[Data] = Form(
    mapping(
      constants.FormField.PUBLIC_LISTING_ID.mapping,
    )(Data.apply)(Data.unapply))

  case class Data(publicListingId: String)
}
