package constants

import schema.id.base._

object Transaction {

  val LowGasPrice: Double = CommonConfig.Blockchain.LowGasPrice
  val MediumGasPrice: Double = CommonConfig.Blockchain.MediumGasPrice
  val HighGasPrice: Double = CommonConfig.Blockchain.HighGasPrice
  val IdentityClassificationID: ClassificationID = ClassificationID(utilities.Secrets.base64URLDecode("YvVd7sePlUspu2jGtfSiCcclBZgrd1uZBrz8vUN25fs="))
  val MantlePlaceIdentityID: IdentityID = schema.document.NameIdentity.getNameIdentityID("MantlePlace")

  val AdminTxGasPrice = 0.0001
  val Commission: BigDecimal = 0.00
  val DefaultGasPrice: BigDecimal = BigDecimal(0)
  val TimeoutHeight = 100

  case class TxUtil(txType: String, gasLimit: Int)

  // TODO Remove after migration
  object User {
    val PUBLIC_SALE = "PUBLIC_SALE"
    val WHITELIST_SALE = "WHITELIST_SALE"
    val SEND_COIN = "SEND_COIN"
  }

}
