package utilities

import com.assets.transactions.{define => assetDefine, mint => mintAsset}
import com.cosmos.bank.v1beta1.MsgSend
import com.cosmos.crypto.secp256k1.PubKey
import com.cosmos.tx.v1beta1._
import com.google.protobuf.{ByteString, Any => protoBufAny}
import com.identities.transactions.issue
import com.orders.transactions.{define => ordersDefine, make => ordersMake}
import models.common.Coin
import org.bitcoinj.core.ECKey
import schema.id.OwnableID
import schema.id.base.{ClassificationID, IdentityID}
import schema.list.PropertyList
import schema.property.base.{MesaProperty, MetaProperty}
import schema.types.Height

import scala.jdk.CollectionConverters.IterableHasAsJava

object BlockchainTransaction {

  private def getTxRawBytes(messages: Seq[protoBufAny], fee: Coin, gasLimit: Int, account: models.blockchain.Account, ecKey: ECKey, memo: String, timeoutHeight: Int): Array[Byte] = {
    val txBody = TxBody.newBuilder().addAllMessages(messages.asJava).setMemo(memo).setTimeoutHeight(timeoutHeight.toLong).build()

    val signerInfo = SignerInfo.newBuilder()
      .setSequence(account.sequence)
      .setPublicKey(com.google.protobuf.Any.newBuilder().setTypeUrl(constants.Blockchain.PublicKey.SINGLE).setValue(PubKey.newBuilder().setKey(ByteString.copyFrom(ecKey.getPubKey)).build().toByteString).build())
      .setModeInfo(ModeInfo.newBuilder().setSingle(ModeInfo.Single.newBuilder().setModeValue(1).build()).build())
      .build()

    val authInfo = AuthInfo.newBuilder()
      .addSignerInfos(signerInfo)
      .setFee(Fee.newBuilder().addAmount(fee.toProtoCoin).setGasLimit(gasLimit).build())
      .build()

    val signDoc = SignDoc.newBuilder()
      .setBodyBytes(txBody.toByteString)
      .setAuthInfoBytes(authInfo.toByteString)
      .setChainId(constants.Blockchain.ChainId)
      .setAccountNumber(account.accountNumber)
      .build()

    val txRaw = TxRaw.newBuilder()
      .setBodyBytes(txBody.toByteString)
      .setAuthInfoBytes(authInfo.toByteString)
      .addSignatures(ByteString.copyFrom(Wallet.ecdsaSign(utilities.Secrets.sha256Hash(signDoc.toByteArray), ecKey)))
      .build()
    txRaw.toByteArray
  }

  def getTxRawBytesWithSignedMemo(messages: Seq[protoBufAny], fee: Coin, gasLimit: Int, account: models.blockchain.Account, ecKey: ECKey, timeoutHeight: Int): (Array[Byte], String) = {
    val txRawBytesWithoutMemo = getTxRawBytes(
      messages = messages,
      fee = fee,
      gasLimit = gasLimit,
      account = account,
      ecKey = ecKey,
      memo = "",
      timeoutHeight = timeoutHeight)
    val memo = utilities.Secrets.base64URLEncoder(utilities.Wallet.ecdsaSign(utilities.Secrets.sha256Hash(txRawBytesWithoutMemo), ECKey.fromPrivate(constants.CommonConfig.MemoSignerWallet.privateKey)))
    (getTxRawBytes(
      messages = messages,
      fee = fee,
      gasLimit = gasLimit,
      account = account,
      ecKey = ecKey,
      memo = memo,
      timeoutHeight = timeoutHeight), memo)
  }

  def memoGenerator(memoPrefix: String): String = utilities.Secrets.base64URLEncoder(utilities.Wallet.hashAndEcdsaSign(memoPrefix, ECKey.fromPrivate(constants.CommonConfig.MemoSignerWallet.privateKey)))

  def getFee(gasPrice: BigDecimal, gasLimit: Int): Coin = Coin(denom = constants.Blockchain.StakingToken, amount = MicroNumber((gasPrice * gasLimit) / MicroNumber.factor))

  def getSendCoinMsgAsAny(fromAddress: String, toAddress: String, amount: Seq[Coin]): protoBufAny = protoBufAny.newBuilder()
    .setTypeUrl(constants.Blockchain.TransactionMessage.SEND_COIN)
    .setValue(MsgSend
      .newBuilder()
      .setFromAddress(fromAddress)
      .setToAddress(toAddress)
      .addAllAmount(amount.map(_.toProtoCoin).asJava)
      .build().toByteString)
    .build()


  def getMantlePlaceIssueIdentityMsg(id: String, fromAddress: String, fromID: IdentityID, toAddress: String, classificationID: ClassificationID): protoBufAny = protoBufAny.newBuilder()
    .setTypeUrl(constants.Blockchain.TransactionMessage.IDENTITY_ISSUE)
    .setValue(issue
      .Message.newBuilder()
      .setFrom(fromAddress)
      .setFromID(fromID.asProtoIdentityID)
      .setTo(toAddress)
      .setClassificationID(classificationID.asProtoClassificationID)
      .setImmutableMetaProperties(PropertyList(Seq(utilities.Identity.getOriginMetaProperty, utilities.Identity.getBondAmountMetaProperty, utilities.Identity.getIDMetaProperty(id))).asProtoPropertyList)
      .setImmutableProperties(PropertyList(Seq(utilities.Identity.getExtraMesaProperty(""))).asProtoPropertyList)
      .setMutableMetaProperties(PropertyList(Seq(utilities.Identity.getTwitterMetaProperty(""), utilities.Identity.getNote1MetaProperty(""))).asProtoPropertyList)
      .setMutableProperties(PropertyList(Seq(utilities.Identity.getNote2MesaProperty(""))).asProtoPropertyList)
      .build().toByteString)
    .build()

  def getDefineAssetMsg(fromAddress: String, fromID: IdentityID, immutableMetas: Seq[MetaProperty], immutables: Seq[MesaProperty], mutableMetas: Seq[MetaProperty], mutables: Seq[MesaProperty]): protoBufAny = protoBufAny.newBuilder()
    .setTypeUrl(constants.Blockchain.TransactionMessage.ASSET_DEFINE)
    .setValue(assetDefine
      .Message.newBuilder()
      .setFrom(fromAddress)
      .setFromID(fromID.asProtoIdentityID)
      .setImmutableMetaProperties(PropertyList(immutableMetas).asProtoPropertyList)
      .setImmutableProperties(PropertyList(immutables).asProtoPropertyList)
      .setMutableMetaProperties(PropertyList(mutableMetas).asProtoPropertyList)
      .setMutableProperties(PropertyList(mutables).asProtoPropertyList)
      .build().toByteString)
    .build()

  def getDefineOrderMsg(fromAddress: String, fromID: IdentityID, immutableMetas: Seq[MetaProperty], immutables: Seq[MesaProperty], mutableMetas: Seq[MetaProperty], mutables: Seq[MesaProperty]): protoBufAny = protoBufAny.newBuilder()
    .setTypeUrl(constants.Blockchain.TransactionMessage.ORDER_DEFINE)
    .setValue(ordersDefine
      .Message.newBuilder()
      .setFrom(fromAddress)
      .setFromID(fromID.asProtoIdentityID)
      .setImmutableMetaProperties(PropertyList(immutableMetas).asProtoPropertyList)
      .setImmutableProperties(PropertyList(immutables).asProtoPropertyList)
      .setMutableMetaProperties(PropertyList(mutableMetas).asProtoPropertyList)
      .setMutableProperties(PropertyList(mutables).asProtoPropertyList)
      .build().toByteString)
    .build()

  def getMintAssetMsg(fromAddress: String, fromID: IdentityID, classificationID: ClassificationID, toID: IdentityID, immutableMetas: Seq[MetaProperty], immutables: Seq[MesaProperty], mutableMetas: Seq[MetaProperty], mutables: Seq[MesaProperty]): protoBufAny = protoBufAny.newBuilder()
    .setTypeUrl(constants.Blockchain.TransactionMessage.ASSET_MINT)
    .setValue(mintAsset
      .Message.newBuilder()
      .setFrom(fromAddress)
      .setFromID(fromID.asProtoIdentityID)
      .setToID(toID.asProtoIdentityID)
      .setClassificationID(classificationID.asProtoClassificationID)
      .setImmutableMetaProperties(PropertyList(immutableMetas).asProtoPropertyList)
      .setImmutableProperties(PropertyList(immutables).asProtoPropertyList)
      .setMutableMetaProperties(PropertyList(mutableMetas).asProtoPropertyList)
      .setMutableProperties(PropertyList(mutables).asProtoPropertyList)
      .build().toByteString)
    .build()

  def getMakeOrderMsg(fromAddress: String, fromID: IdentityID, classificationID: ClassificationID, takerID: IdentityID, makerOwnableID: OwnableID, makerOwnableSplit: BigDecimal, expiryHeight: Long, takerOwnableID: OwnableID, takerOwnableSplit: BigDecimal, immutableMetas: Seq[MetaProperty], immutables: Seq[MesaProperty], mutableMetas: Seq[MetaProperty], mutables: Seq[MesaProperty]): protoBufAny = protoBufAny.newBuilder()
    .setTypeUrl(constants.Blockchain.TransactionMessage.ASSET_MINT)
    .setValue(ordersMake
      .Message.newBuilder()
      .setFrom(fromAddress)
      .setFromID(fromID.asProtoIdentityID)
      .setClassificationID(classificationID.asProtoClassificationID)
      .setClassificationID(classificationID.asProtoClassificationID)
      .setTakerID(takerID.asProtoIdentityID)
      .setMakerOwnableID(makerOwnableID.toAnyOwnableID)
      .setTakerOwnableID(takerOwnableID.toAnyOwnableID)
      .setExpiresIn(Height(expiryHeight).asProtoHeight)
      .setMakerOwnableSplit(makerOwnableSplit.toString())
      .setTakerOwnableSplit(takerOwnableSplit.toString())
      .setImmutableMetaProperties(PropertyList(immutableMetas).asProtoPropertyList)
      .setImmutableProperties(PropertyList(immutables).asProtoPropertyList)
      .setMutableMetaProperties(PropertyList(mutableMetas).asProtoPropertyList)
      .setMutableProperties(PropertyList(mutables).asProtoPropertyList)
      .build().toByteString)
    .build()

}
