package utilities

import models.master.Collection
import schema.data.base.NumberData
import schema.id.base.ClassificationID
import schema.qualified.{Immutables, Mutables}

object Collection {

  private val others = "/others/"
  private val nfts = "/nfts/"

  def getFilePath(collectionId: String): String = utilities.FileOperations.checkAndCreateDirectory(constants.Collection.File.AllCollectionsPath + collectionId + others)

  def getNFTFilePath(collectionId: String): String = utilities.FileOperations.checkAndCreateDirectory(constants.Collection.File.AllCollectionsPath + collectionId + nfts)

  def getOthersFileAwsKey(collectionId: String, fileName: String): String = collectionId + others + fileName

  def getOldNFTFileAwsKey(collectionId: String, fileName: String): String = collectionId + nfts + fileName

  // TODO BondRate from parameters
  def getClassificationID(immutables: Immutables, mutables: Mutables): ClassificationID = schema.utilities.ID.getClassificationID(immutables = immutables, mutables = mutables)

  def generateClassificationID(collection: Collection): ClassificationID = getClassificationID(immutables = collection.getImmutables, mutables = collection.getMutables.remove(Seq(schema.constants.Properties.BondAmountProperty)).add(Seq(schema.constants.Properties.BondAmountProperty.copy(data = NumberData(collection.getMinimumBondAmount.value)))))

}
