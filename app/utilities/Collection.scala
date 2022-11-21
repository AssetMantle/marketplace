package utilities

object Collection {

  private val others = "/others/"
  private val nfts = "/nfts/"

  def getFilePath(collectionId: String): String = utilities.FileOperations.checkAndCreateDirectory(constants.Collection.File.AllCollectionsPath + collectionId + others)

  def getNFTFilePath(collectionId: String): String = utilities.FileOperations.checkAndCreateDirectory(constants.Collection.File.AllCollectionsPath + collectionId + nfts)

  def getFileAwsKey(collectionId: String, fileName: String): String = collectionId + others + fileName

  def getNFTFileAwsKey(collectionId: String, fileName: String): String = collectionId + nfts + fileName
}
