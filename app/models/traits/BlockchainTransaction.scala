package models.traits

trait BlockchainTransaction[T] {

  val txHash: String
  val status: Option[Boolean]
  val timeoutHeight: Int
  val txHeight: Option[Int]
  val log: Option[String]

  def getTxRawAsHexString(txRawBytes: Array[Byte]): String = txRawBytes.map("%02x".format(_)).mkString.toUpperCase

  def withUpdatedLog(log: Option[String]): T
}
