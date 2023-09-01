package utilities

import schema.id.base.ClassificationID
import schema.qualified.{Immutables, Mutables}

object Collection {

  private val others = "/others/"
  private val nfts = "/nfts/"

  def getFilePath(collectionId: String): String = utilities.FileOperations.checkAndCreateDirectory(constants.Collection.File.AllCollectionsPath + collectionId + others)

  def getNFTFilePath(collectionId: String): String = utilities.FileOperations.checkAndCreateDirectory(constants.Collection.File.AllCollectionsPath + collectionId + nfts)

  def getOthersFileAwsKey(collectionId: String, fileName: String): String = collectionId + others + fileName

  def getOldNFTFileAwsKey(collectionId: String, fileName: String): String = collectionId + nfts + fileName

  def getTotalBondAmount(immutables: Immutables, mutables: Mutables, bondRate: Int): BigInt = {
    val totalWeight = mutables.propertyList.properties.map(_.getBondedWeight).sum + immutables.propertyList.properties.map(_.getBondedWeight).sum
    BigInt(totalWeight * bondRate)
  }

  // TODO BondRate from parameters
  def getClassificationID(immutables: Immutables, mutables: Mutables): ClassificationID = schema.utilities.ID.getClassificationID(immutables = immutables, mutables = mutables)
}
