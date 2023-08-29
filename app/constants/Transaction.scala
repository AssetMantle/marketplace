package constants

import schema.id.base._

object Transaction {

  val LowGasPrice: Double = CommonConfig.Blockchain.LowGasPrice
  val MediumGasPrice: Double = CommonConfig.Blockchain.MediumGasPrice
  val HighGasPrice: Double = CommonConfig.Blockchain.HighGasPrice
  val IdentityClassificationID: ClassificationID = ClassificationID(utilities.Secrets.base64URLDecode("IoTaHkXLe_NVFxz11-BhmxQZZX52EfmuAq5QM6DBR3k="))
  val FromID: IdentityID = IdentityID(utilities.Secrets.base64URLDecode("4lbk0EzCO2uIabkLYh9O50uwFwcZcKFQFbhYnupH0zo="))

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

  object Admin {
    object Campaign {
      val MINT_NFT_AIRDROP = "MINT_NFT_AIRDROP"
    }

    val DEFINE_ASSET = "DEFINE_ASSET"
    val MINT_ASSET = "MINT_ASSET"
    val ISSUE_IDENTITY = "ISSUE_IDENTITY"
  }

  object User {
    val CANCEL_ORDER = "CANCEL_ORDER"
    val NFT_MINTING_FEE = "NFT_MINTING_FEE"
    val NFT_TRANSFER = "NFT_TRANSFER"
    val PROVISION_ADDRESS = "PROVISION_ADDRESS"
    val UNPROVISION_ADDRESS = "UNPROVISION_ADDRESS"
    val UNWRAP = "UNWRAP"
    val WRAP = "WRAP"
    val PUBLIC_SALE = "PUBLIC_SALE"
    val WHITELIST_SALE = "WHITELIST_SALE"
    val SEND_COIN = "SEND_COIN"
    val SECONDARY_MARKET_SELL = "SECONDARY_MARKET_SELL"
    val SECONDARY_MARKET_BUY = "SECONDARY_MARKET_BUY"
  }

}
