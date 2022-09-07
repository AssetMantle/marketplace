package utilities

import cosmos.tx.v1beta1.TxOuterClass

import scala.jdk.CollectionConverters.IterableHasAsJava
import com.google.protobuf.{ByteString, Any => protoBufAny}
import cosmos.crypto.secp256k1.Keys
import models.common.Coin
import org.bitcoinj.core.ECKey

object BlockchainTransaction {

  def getTxHashAndRawHex(messages: Seq[protoBufAny], fee: Coin, gasLimit: Int, account: models.blockchain.Account, ecKey: ECKey, memo: String): (String, String) = {
    val txBody = TxOuterClass.TxBody.newBuilder().addAllMessages(messages.asJava).setMemo(memo).build()

    val signerInfo = TxOuterClass.SignerInfo.newBuilder()
      .setSequence(account.sequence)
      .setPublicKey(com.google.protobuf.Any.newBuilder().setTypeUrl(constants.Blockchain.PublicKey.SINGLE).setValue(Keys.PubKey.newBuilder().setKey(ByteString.copyFrom(ecKey.getPubKey)).build().toByteString).build())
      .setModeInfo(TxOuterClass.ModeInfo.newBuilder().setSingle(TxOuterClass.ModeInfo.Single.newBuilder().setModeValue(1).build()).build())
      .build()

    val authInfo = TxOuterClass.AuthInfo.newBuilder()
      .addSignerInfos(signerInfo)
      .setFee(TxOuterClass.Fee.newBuilder().addAmount(fee.toProtoCoin).setGasLimit(gasLimit).build())
      .build()

    val signDoc = TxOuterClass.SignDoc.newBuilder()
      .setBodyBytes(txBody.toByteString)
      .setAuthInfoBytes(authInfo.toByteString)
      .setChainId(constants.Blockchain.ChainId)
      .setAccountNumber(account.accountNumber)
      .build()

    val txRaw = TxOuterClass.TxRaw.newBuilder()
      .setBodyBytes(txBody.toByteString)
      .setAuthInfoBytes(authInfo.toByteString)
      .addSignatures(ByteString.copyFrom(Wallet.ecdsaSign(utilities.Secrets.sha256Hash(signDoc.toByteArray), ecKey)))
      .build()

    (utilities.Secrets.sha256Hash(txRaw.toByteArray).map("%02x".format(_)).mkString.toUpperCase, txRaw.toByteArray.map("%02x".format(_)).mkString.toUpperCase)
  }

}
