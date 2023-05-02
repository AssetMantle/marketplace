package constants

import org.bitcoinj.crypto.ChildNumber
import utilities.Wallet

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
