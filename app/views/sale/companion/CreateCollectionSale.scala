package views.sale.companion

import models.master.Sale
import play.api.data.Form
import play.api.data.Forms.mapping
import utilities.MicroNumber

object CreateCollectionSale {
  val form: Form[Data] = Form(
    mapping(
      constants.FormField.SELECT_COLLECTION_ID.mapping,
      constants.FormField.SALE_NFT_NUMBER.mapping,
      constants.FormField.SALE_MAX_MINT_PER_ACCOUNT.mapping,
      constants.FormField.SELECT_WHITELIST_ID.mapping,
      constants.FormField.NFT_WHITELIST_SALE_PRICE.mapping,
      constants.FormField.NFT_SALE_START_EPOCH.mapping,
      constants.FormField.NFT_SALE_END_EPOCH.mapping,
    )(Data.apply)(Data.unapply).verifying(constants.FormConstraint.createSale))

  case class Data(collectionId: String, nftForSale: Int, maxMintPerAccount: Int, whitelistId: String, price: MicroNumber, startEpoch: Int, endEpoch: Int) {

    def toNewSale: Sale = Sale(id = utilities.IdGenerator.getRandomHexadecimal, whitelistId = whitelistId, collectionId = collectionId, numberOfNFTs = nftForSale, maxMintPerAccount = maxMintPerAccount, price = price, denom = constants.Blockchain.StakingToken, startTimeEpoch = startEpoch, endTimeEpoch = endEpoch, soldOut = false)
  }
}
