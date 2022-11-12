package views.sale.companion

import play.api.data.Form
import play.api.data.Forms.mapping
import utilities.MicroNumber

object CreateWhitelistSale {
  val form: Form[Data] = Form(
    mapping(
      constants.FormField.SELECT_COLLECTION_ID.mapping,
      constants.FormField.WHITELIST_SALE_NFT_NUMBER.mapping,
      constants.FormField.SELECT_WHITELIST_ID.mapping,
      constants.FormField.NFT_WHITELIST_SALE_PRICE.mapping,
      constants.FormField.NFT_SALE_CREATOR_FEE.mapping,
      constants.FormField.NFT_SALE_START_EPOCH.mapping,
      constants.FormField.NFT_SALE_END_EPOCH.mapping,
    )(Data.apply)(Data.unapply))

  case class Data(collectionId: String, nftForSale: Int, whitelistId: String, price: MicroNumber, creatorFee: BigDecimal, startEpoch: Int, endEpoch: Int)
}
