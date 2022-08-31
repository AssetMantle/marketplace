package models.Abstract

import models.common.TransactionMessages._
import play.api.libs.json.{Json, Writes}

abstract class TransactionMessage {
  def getSigners: Seq[String]
}

object TransactionMessage {
  implicit val transactionMessageWrites: Writes[TransactionMessage] = {
    //auth
    case createVestingAccount: CreateVestingAccount => Json.toJson(createVestingAccount)
    //authz
    case grantAuthorization: GrantAuthorization => Json.toJson(grantAuthorization)
    case revokeAuthorization: RevokeAuthorization => Json.toJson(revokeAuthorization)
    case executeAuthorization: ExecuteAuthorization => Json.toJson(executeAuthorization)
    //bank
    case sendCoin: SendCoin => Json.toJson(sendCoin)
    case multiSend: MultiSend => Json.toJson(multiSend)
    //crisis
    case verifyInvariant: VerifyInvariant => Json.toJson(verifyInvariant)
    //distribution
    case setWithdrawAddress: SetWithdrawAddress => Json.toJson(setWithdrawAddress)
    case withdrawDelegatorReward: WithdrawDelegatorReward => Json.toJson(withdrawDelegatorReward)
    case withdrawValidatorCommission: WithdrawValidatorCommission => Json.toJson(withdrawValidatorCommission)
    case fundCommunityPool: FundCommunityPool => Json.toJson(fundCommunityPool)
    //evidence
    case submitEvidence: SubmitEvidence => Json.toJson(submitEvidence)
    //feeGrant
    case feeGrantAllowance: FeeGrantAllowance => Json.toJson(feeGrantAllowance)
    case feeRevokeAllowance: FeeRevokeAllowance => Json.toJson(feeRevokeAllowance)
    //gov
    case deposit: Deposit => Json.toJson(deposit)
    case submitProposal: SubmitProposal => Json.toJson(submitProposal)
    case vote: Vote => Json.toJson(vote)
    //slashing
    case unjail: Unjail => Json.toJson(unjail)
    //staking
    case createValidator: CreateValidator => Json.toJson(createValidator)
    case editValidator: EditValidator => Json.toJson(editValidator)
    case delegate: Delegate => Json.toJson(delegate)
    case redelegate: Redelegate => Json.toJson(redelegate)
    case undelegate: Undelegate => Json.toJson(undelegate)
    //ibc-client
    case createClient: CreateClient => Json.toJson(createClient)
    case updateClient: UpdateClient => Json.toJson(updateClient)
    case submitMisbehaviour: SubmitMisbehaviour => Json.toJson(submitMisbehaviour)
    case upgradeClient: UpgradeClient => Json.toJson(upgradeClient)
    //ibc-connection
    case connectionOpenInit: ConnectionOpenInit => Json.toJson(connectionOpenInit)
    case connectionOpenConfirm: ConnectionOpenConfirm => Json.toJson(connectionOpenConfirm)
    case connectionOpenAck: ConnectionOpenAck => Json.toJson(connectionOpenAck)
    case connectionOpenTry: ConnectionOpenTry => Json.toJson(connectionOpenTry)
    //ibc-channel
    case channelOpenInit: ChannelOpenInit => Json.toJson(channelOpenInit)
    case channelOpenConfirm: ChannelOpenConfirm => Json.toJson(channelOpenConfirm)
    case channelOpenAck: ChannelOpenAck => Json.toJson(channelOpenAck)
    case channelOpenTry: ChannelOpenTry => Json.toJson(channelOpenTry)
    case channelCloseInit: ChannelCloseInit => Json.toJson(channelCloseInit)
    case channelCloseConfirm: ChannelCloseConfirm => Json.toJson(channelCloseConfirm)
    case recvPacket: RecvPacket => Json.toJson(recvPacket)
    case timeout: Timeout => Json.toJson(timeout)
    case timeoutOnClose: TimeoutOnClose => Json.toJson(timeoutOnClose)
    case acknowledgement: Acknowledgement => Json.toJson(acknowledgement)
    //ibc-transfer
    case transfer: Transfer => Json.toJson(transfer)
    case x: Any => Json.toJson(x.toString)
  }
}
