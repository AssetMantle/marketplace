package models.traits

trait BlockchainTransaction {

  val txHash: String
  val status: Option[Boolean]
  val memo: Option[String]
  val log: Option[String]

  def getTxRawAsHexString(txRawBytes: Array[Byte]): String = txRawBytes.map("%02x".format(_)).mkString.toUpperCase
}
