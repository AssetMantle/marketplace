package views.publicListing.companion

import play.api.data.Form
import play.api.data.Forms.mapping
import utilities.MicroNumber

object EditPublicListing {
  val form: Form[Data] = Form(
    mapping(
      constants.FormField.PUBLIC_LISTING_ID.mapping,
      constants.FormField.PUBLIC_LISTING_MAX_MINT_PER_ACCOUNT.mapping,
      constants.FormField.PUBLIC_LISTING_PRICE.mapping,
      constants.FormField.PUBLIC_LISTING_START_EPOCH.mapping,
      constants.FormField.PUBLIC_LISTING_END_EPOCH.mapping,
    )(Data.apply)(Data.unapply).verifying(constants.FormConstraint.editPublicListing))

  case class Data(publicListingId: String, maxMintPerAccount: Int, price: MicroNumber, startEpoch: Int, endEpoch: Int)
}
