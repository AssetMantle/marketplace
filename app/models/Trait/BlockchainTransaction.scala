package models.Trait

trait BlockchainTransaction {

  val txHash: String
  val txRawBytes: Array[Byte]
  val broadcasted: Boolean
  val status: Option[Boolean]
  val memo: Option[String]
  val log: Option[String]

  def getTxRawAsHexString: String = this.txRawBytes.map("%02x".format(_)).mkString.toUpperCase
}
