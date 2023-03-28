package views.publicListing.companion

import play.api.data.Form
import play.api.data.Forms.mapping

object BuyNFT {
  val form: Form[Data] = Form(
    mapping(
      constants.FormField.PUBLIC_LISTING_ID.mapping,
      constants.FormField.PUBLIC_LISTING_BUY_NFT_NUMBER.mapping,
      constants.FormField.MINT_NFT.mapping,
      constants.FormField.PASSWORD.mapping
    )(Data.apply)(Data.unapply))

  case class Data(publicListingId: String, buyNFTs: Int, mintOnSuccess: Boolean, password: String)
}
