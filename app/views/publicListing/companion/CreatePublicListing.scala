package views.publicListing.companion

import models.master.PublicListing
import play.api.data.Form
import play.api.data.Forms.mapping
import utilities.MicroNumber

object CreatePublicListing {
  val form: Form[Data] = Form(
    mapping(
      constants.FormField.SELECT_COLLECTION_ID.mapping,
      constants.FormField.PUBLIC_LISTING_NFT_NUMBER.mapping,
      constants.FormField.PUBLIC_LISTING_MAX_MINT_PER_ACCOUNT.mapping,
      constants.FormField.PUBLIC_LISTING_PRICE.mapping,
      constants.FormField.PUBLIC_LISTING_START_EPOCH.mapping,
      constants.FormField.PUBLIC_LISTING_END_EPOCH.mapping,
    )(Data.apply)(Data.unapply).verifying(constants.FormConstraint.createPublicListing))

  case class Data(collectionId: String, numberOfNFTs: Int, maxMintPerAccount: Int, price: MicroNumber, startEpoch: Int, endEpoch: Int) {

    def toNewPublicListing: PublicListing = PublicListing(id = utilities.IdGenerator.getRandomHexadecimal, collectionId = collectionId, numberOfNFTs = numberOfNFTs, maxMintPerAccount = maxMintPerAccount, price = price, denom = constants.Blockchain.StakingToken, startTimeEpoch = startEpoch, endTimeEpoch = endEpoch)
  }
}
