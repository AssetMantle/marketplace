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

  def getOldNFTFileAwsKey(collectionId: String, fileName: String): String = collectionId + nfts + fileName

  def getTotalBondAmount(immutables: Immutables, mutables: Mutables, bondRate: Int): Long = {
    val totalWeight = mutables.propertyList.properties.map(_.getBondedWeight).sum + immutables.propertyList.properties.map(_.getBondedWeight).sum
    (totalWeight * bondRate).toLong
  }

  // TODO BondRate from parameters
  def getClassificationID(immutables: Immutables, mutables: Mutables, bondRate: Int): ClassificationID = {
    val totalWeight = mutables.propertyList.properties.map(_.getBondedWeight).sum + immutables.propertyList.properties.map(_.getBondedWeight).sum

    val updatedImmutables = Immutables(PropertyList(immutables.propertyList.properties ++ Seq(schema.constants.Properties.BondAmountProperty.copy(data = NumberData(totalWeight * bondRate)))))
    schema.utilities.ID.getClassificationID(immutables = updatedImmutables, mutables = mutables)
  }
}
