package utilities

import schema.data.base.NumberData
import schema.id.base.ClassificationID
import schema.list.PropertyList
import schema.qualified.{Immutables, Mutables}

object Collection {

  private val others = "/others/"
  private val nfts = "/nfts/"

  def getFilePath(collectionId: String): String = utilities.FileOperations.checkAndCreateDirectory(constants.Collection.File.AllCollectionsPath + collectionId + others)

  def getNFTFilePath(collectionId: String): String = utilities.FileOperations.checkAndCreateDirectory(constants.Collection.File.AllCollectionsPath + collectionId + nfts)

  def getOthersFileAwsKey(collectionId: String, fileName: String): String = collectionId + others + fileName

  def getNFTFileAwsKey(collectionId: String, fileName: String): String = collectionId + nfts + fileName

  def getTotalBondAmount(immutables: Immutables, mutables: Mutables, bondRate: Int): Long = {
    val totalWeight = mutables.propertyList.propertyList.map(_.getBondedWeight).sum + immutables.propertyList.propertyList.map(_.getBondedWeight).sum
    (totalWeight * bondRate).toLong
  }

  // TODO BondRate from parameters
  def getClassificationID(immutables: Immutables, mutables: Mutables, bondRate: Int): ClassificationID = {
    val totalWeight = mutables.propertyList.propertyList.map(_.getBondedWeight).sum + immutables.propertyList.propertyList.map(_.getBondedWeight).sum

    val updatedImmutables = Immutables(PropertyList(immutables.propertyList.propertyList ++ Seq(constants.Blockchain.BondAmountProperty.copy(data = NumberData(totalWeight * bondRate).toAnyData))))
    utilities.ID.getClassificationID(immutables = updatedImmutables, mutables = mutables)
  }
}
