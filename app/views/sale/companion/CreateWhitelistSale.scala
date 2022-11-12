package views.sale.companion

import models.master.NFTWhitelistSale
import play.api.data.Form
import play.api.data.Forms.mapping
import utilities.MicroNumber

object CreateWhitelistSale {
  val form: Form[Data] = Form(
    mapping(
      constants.FormField.COLLECTION_ID.mapping,
      constants.FormField.WHITELIST_SALE_NFT_FILE_NAMES.optionalMapping,
      constants.FormField.SELECT_WHITELIST_ID.mapping,
      constants.FormField.NFT_SALE_RATE.mapping,
      constants.FormField.NFT_SALE_CREATOR_FEE.mapping,
      constants.FormField.NFT_SALE_START_EPOCH.mapping,
      constants.FormField.NFT_SALE_END_EPOCH.mapping,
    )(Data.apply)(Data.unapply))

  case class Data(collectionId: String, nftFileNames: Option[String], whitelistId: String, rate: MicroNumber, creatorFee: BigDecimal, startEpoch: Int, endEpoch: Int) {

    def toNFTWhitelistSales(fileNames: Seq[String], quantity: Long, denom: String): Seq[NFTWhitelistSale] = {
      fileNames.map(x => NFTWhitelistSale(id = utilities.IdGenerator.getRandomHexadecimal, fileName = x, whitelistId = whitelistId, quantity = quantity, rate = rate, denom = denom, creatorFee = creatorFee, startTimeEpoch = startEpoch, endTimeEpoch = endEpoch))
    }

  }
}
