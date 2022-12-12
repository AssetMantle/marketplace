package utilities

import com.google.protobuf.{ByteString, Any => protoBufAny}
import cosmos.bank.v1beta1.Tx
import cosmos.crypto.secp256k1.Keys
import cosmos.tx.v1beta1.TxOuterClass
import models.common.Coin
import models.master.{NFT, NFTProperty}
import org.bitcoinj.core.ECKey

import scala.jdk.CollectionConverters.IterableHasAsJava

object BlockchainTransaction {

  def getTxRawBytes(messages: Seq[protoBufAny], fee: Coin, gasLimit: Int, account: models.blockchain.Account, ecKey: ECKey, memo: String): Array[Byte] = {
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
    txRaw.toByteArray
  }

  def getFee(gasPrice: Double, gasLimit: Int): Coin = Coin(denom = constants.Blockchain.StakingToken, amount = MicroNumber((gasPrice * gasLimit) / MicroNumber.factor))

  def getSendCoinMsgAsAny(fromAddress: String, toAddress: String, amount: Seq[Coin]): protoBufAny = protoBufAny.newBuilder()
    .setTypeUrl(constants.Blockchain.TransactionMessage.SEND_COIN)
    .setValue(Tx
      .MsgSend.newBuilder()
      .setFromAddress(fromAddress)
      .setToAddress(toAddress)
      .addAllAmount(amount.map(_.toProtoCoin).asJava)
      .build().toByteString)
    .build()

  def getMintMsgAsAny(fromAddress: String, fromId: String, toId: String, classificationId: String) : protoBufAny = protoBufAny.newBuilder()
//    .setTypeUrl(constants.Blockchain.TransactionMessage.MINT)
//    .setValue(Tx
//      .MsgSend.newBuilder()
//      .setFromAddress(fromAddress)
//      .setToAddress(toAddress)
//      .addAllAmount(amount.map(_.toProtoCoin).asJava)
//      .build().toByteString)
    .build()

  def getNubMsgAsAny(fromAddress: String, fromId: String, toId: String, classificationId: String): protoBufAny = protoBufAny.newBuilder()
    //    .setTypeUrl(constants.Blockchain.TransactionMessage.MINT)
    //    .setValue(Tx
    //      .MsgSend.newBuilder()
    //      .setFromAddress(fromAddress)
    //      .setToAddress(toAddress)
    //      .addAllAmount(amount.map(_.toProtoCoin).asJava)
    //      .build().toByteString)
    .build()

}
