package constants

import org.bitcoinj.crypto.ChildNumber
import schema.id.base._
import utilities.Wallet

object Transaction {

  val LowGasPrice: Double = CommonConfig.Blockchain.LowGasPrice
  val MediumGasPrice: Double = CommonConfig.Blockchain.MediumGasPrice
  val HighGasPrice: Double = CommonConfig.Blockchain.HighGasPrice
  val IdentityClassificationID: ClassificationID = ClassificationID(utilities.Secrets.base64URLDecode("IoTaHkXLe_NVFxz11-BhmxQZZX52EfmuAq5QM6DBR3k="))
  val FromID: IdentityID = IdentityID(utilities.Secrets.base64URLDecode("YJu7-vpLARPfNs5_yiuDLb8xkKcNYjqH6Yq4IfqubTY="))
  val OrderClassificationID: ClassificationID = ClassificationID(utilities.Secrets.base64URLDecode("9IqAiL2idrARX91QJZVBj89zjQN_y3_3sHc90z1DPTU="))

  val DefaultSendCoinGasAmount = 120000
  val DefaultIssueIdentityGasLimit = 120000
  val DefaultDefineAssetGasLimit = 150000
  val DefaultMintAssetGasLimit = 150000
  val DefaultMakeOrderGasLimit = 150000
  val DefaultUnwrapGasLimit = 120000
  val DefaultWrapGasLimit = 120000
  val DefaultCancelOrderGasLimit = 150000
  val DefaultTakeOrderGasLimit = 150000
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

  object Wallet {
    val seeds = "comfort broccoli urban cheap noise income ensure wheat maze cement panel clinic opinion acoustic select sentence code purchase casual dose brown fish salt coral".split(" ") //utilities.EncryptedKeyStore.getPassphrase("WALLET_SEEDS").split(" ").toSeq
    val IssueIdentityHDPath: Seq[ChildNumber] = Seq(
      new ChildNumber(44, true),
      new ChildNumber(constants.Blockchain.CoinType, true),
      new ChildNumber(0, true),
      new ChildNumber(0, false),
      new ChildNumber(1, false)
    )

    val DefineAssetHDPath: Seq[ChildNumber] = Seq(
      new ChildNumber(44, true),
      new ChildNumber(constants.Blockchain.CoinType, true),
      new ChildNumber(0, true),
      new ChildNumber(0, false),
      new ChildNumber(2, false)
    )

    val MintAssetHDPath: Seq[ChildNumber] = Seq(
      new ChildNumber(44, true),
      new ChildNumber(constants.Blockchain.CoinType, true),
      new ChildNumber(0, true),
      new ChildNumber(0, false),
      new ChildNumber(3, false)
    )

    val FeeCollectorAddress = "mantle19qxy9t064v79wkslptpjxn3nealzhxhdfe3ldd"
    val IssueIdentityWallet: Wallet = utilities.Wallet.getWallet(seeds, hdPath = IssueIdentityHDPath)
    val DefineAssetWallet: Wallet = utilities.Wallet.getWallet(seeds, hdPath = DefineAssetHDPath)
    val MintAssetWallet: Wallet = utilities.Wallet.getWallet(seeds, hdPath = MintAssetHDPath)
  }

  object User {
    val CANCEL_ORDER = "CANCEL_ORDER"
    val MAKE_ORDER = "MAKE_ORDER"
    val NFT_MINTING_FEE = "NFT_MINTING_FEE"
    val NFT_TRANSFER = "NFT_TRANSFER"
    val PROVISION_ADDRESS = "PROVISION_ADDRESS"
    val TAKE_ORDER = "TAKE_ORDER"
    val UNPROVISION_ADDRESS = "UNPROVISION_ADDRESS"
    val UNWRAP = "UNWRAP"
    val WRAP = "WRAP"
    val PUBLIC_SALE = "PUBLIC_SALE"
    val WHITELIST_SALE = "WHITELIST_SALE"
    val SEND_COIN = "SEND_COIN"
  }

}
