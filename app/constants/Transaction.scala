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
  val DefaultSendCoinGasAmount = 120000
  val DefaultIssueIdentityGasLimit = 120000
  val DefaultDefineAssetGasLimit = 150000
  val DefaultMintAssetGasLimit = 150000
  val DefaultPutOrderGasLimit = 150000
  val DefaultUnwrapGasLimit = 120000
  val DefaultWrapGasLimit = 120000
  val DefaultCancelOrderGasLimit = 150000
  val DefaultGetOrderGasLimit = 150000
  val DefaultGasPrice: BigDecimal = BigDecimal(0)
  val DefaultProvisionGasLimit = 120000
  val DefaultNFTTransferGasLimit = 120000
  val TimeoutHeight = 100

  case class TxUtil(txType: String, gasLimit: Int)

  object Admin {
    object Campaign {
      val MINT_NFT_AIRDROP = "MINT_NFT_AIRDROP"
    }

  }

  // TODO Remove after migration
  object User {
    val PUBLIC_SALE = "PUBLIC_SALE"
    val WHITELIST_SALE = "WHITELIST_SALE"
    val SEND_COIN = "SEND_COIN"
  }

}
