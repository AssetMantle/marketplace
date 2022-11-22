package models.Trait

trait BlockchainTransaction {

  val txHash: String
  val txRawBytes: Array[Byte]
  val broadcasted: Boolean
  val status: Option[Boolean]
  val memo: Option[String]
  val log: Option[String]
}
