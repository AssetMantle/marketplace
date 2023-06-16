package utilities

import schema.id.base.{AssetID, ClassificationID}
import schema.qualified.Immutables

object NFT {

  def getAWSKey(fileName: String): String = "nft/" + fileName

  def getAssetID(classificationID: ClassificationID, immutables: Immutables): AssetID = schema.utilities.ID.getAssetID(classificationID, immutables)

}
