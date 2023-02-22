package models.common

import exceptions.BaseException
import models.Abstract.{ProposalContent, PublicKey, TransactionMessage}
import models.common.IBC._
import models.common.Validator._
import play.api.Logger
import play.api.libs.functional.syntax.toFunctionalBuilderOps
import play.api.libs.json._
import utilities.Date.RFC3339
import utilities.MicroNumber

object TransactionMessages {

  private implicit val module: String = constants.Module.COMMON_TRANSACTION_MESSAGE

  private implicit val logger: Logger = Logger(this.getClass)

  case class StdMsg(messageType: String, message: TransactionMessage) {
    def getSigners: Seq[String] = message.getSigners
  }

  implicit val stdMsgReads: Reads[StdMsg] = (
    (JsPath \ "messageType").read[String] and
      (JsPath \ "message").read[JsObject]
    ) (stdMsgApply _)

  implicit val stdMsgWrites: Writes[StdMsg] = Json.writes[StdMsg]

  //auth
  case class CreateVestingAccount(fromAddress: String, toAddress: String, amount: Seq[Coin], endTime: String, delayed: Boolean) extends TransactionMessage {
    def getSigners: Seq[String] = Seq(fromAddress)
  }

  implicit val createVestingAccountReads: Reads[CreateVestingAccount] = Json.reads[CreateVestingAccount]

  implicit val createVestingAccountWrites: OWrites[CreateVestingAccount] = Json.writes[CreateVestingAccount]

  //authz
  case class Grant(authorization: Authz.Authorization, expiration: RFC3339)

  implicit val grantReads: Reads[Grant] = Json.reads[Grant]

  implicit val grantWrites: OWrites[Grant] = Json.writes[Grant]

  case class GrantAuthorization(granter: String, grantee: String, grant: Grant) extends TransactionMessage {
    def getSigners: Seq[String] = Seq(granter)
  }

  implicit val grantAuthorizationReads: Reads[GrantAuthorization] = Json.reads[GrantAuthorization]

  implicit val grantAuthorizationWrites: OWrites[GrantAuthorization] = Json.writes[GrantAuthorization]

  case class RevokeAuthorization(granter: String, grantee: String, messageTypeURL: String) extends TransactionMessage {
    def getSigners: Seq[String] = Seq(granter)
  }

  implicit val revokeAuthorizationReads: Reads[RevokeAuthorization] = Json.reads[RevokeAuthorization]

  implicit val revokeAuthorizationWrites: OWrites[RevokeAuthorization] = Json.writes[RevokeAuthorization]

  case class ExecuteAuthorization(grantee: String, messages: Seq[StdMsg]) extends TransactionMessage {
    def getSigners: Seq[String] = Seq(grantee)
  }

  implicit val executeAuthorizationReads: Reads[ExecuteAuthorization] = Json.reads[ExecuteAuthorization]

  implicit val executeAuthorizationWrites: OWrites[ExecuteAuthorization] = Json.writes[ExecuteAuthorization]

  //bank
  case class Input(address: String, coins: Seq[Coin])

  implicit val inputReads: Reads[Input] = Json.reads[Input]

  implicit val inputWrites: OWrites[Input] = Json.writes[Input]

  case class Output(address: String, coins: Seq[Coin])

  implicit val outputReads: Reads[Output] = Json.reads[Output]

  implicit val outputWrites: OWrites[Output] = Json.writes[Output]

  case class MultiSend(inputs: Seq[Input], outputs: Seq[Output]) extends TransactionMessage {
    def getSigners: Seq[String] = inputs.map(_.address)
  }

  implicit val multiSendReads: Reads[MultiSend] = Json.reads[MultiSend]

  implicit val multiSendWrites: OWrites[MultiSend] = Json.writes[MultiSend]

  case class SendCoin(fromAddress: String, toAddress: String, amount: Seq[Coin]) extends TransactionMessage {
    def getSigners: Seq[String] = Seq(fromAddress)
  }

  implicit val sendCoinReads: Reads[SendCoin] = Json.reads[SendCoin]

  implicit val sendCoinWrites: OWrites[SendCoin] = Json.writes[SendCoin]

  //crisis
  case class VerifyInvariant(sender: String, invariantModuleName: String, invariantRoute: String) extends TransactionMessage {
    def getSigners: Seq[String] = Seq(sender)
  }

  implicit val verifyInvariantReads: Reads[VerifyInvariant] = Json.reads[VerifyInvariant]

  implicit val verifyInvariantWrites: OWrites[VerifyInvariant] = Json.writes[VerifyInvariant]

  //distribution
  case class SetWithdrawAddress(delegatorAddress: String, withdrawAddress: String) extends TransactionMessage {
    def getSigners: Seq[String] = Seq(delegatorAddress)
  }

  implicit val setWithdrawAddressReads: Reads[SetWithdrawAddress] = Json.reads[SetWithdrawAddress]

  implicit val setWithdrawAddressWrites: OWrites[SetWithdrawAddress] = Json.writes[SetWithdrawAddress]

  case class WithdrawDelegatorReward(delegatorAddress: String, validatorAddress: String) extends TransactionMessage {
    def getSigners: Seq[String] = Seq(delegatorAddress)
  }

  implicit val withdrawDelegatorRewardReads: Reads[WithdrawDelegatorReward] = Json.reads[WithdrawDelegatorReward]

  implicit val withdrawDelegatorRewardWrites: OWrites[WithdrawDelegatorReward] = Json.writes[WithdrawDelegatorReward]

  case class WithdrawValidatorCommission(validatorAddress: String) extends TransactionMessage {
    def getSigners: Seq[String] = Seq(utilities.Crypto.convertOperatorAddressToAccountAddress(validatorAddress))
  }

  implicit val withdrawValidatorCommissionReads: Reads[WithdrawValidatorCommission] = Json.reads[WithdrawValidatorCommission]

  implicit val withdrawValidatorCommissionWrites: OWrites[WithdrawValidatorCommission] = Json.writes[WithdrawValidatorCommission]

  case class FundCommunityPool(amount: Seq[Coin], depositor: String) extends TransactionMessage {
    def getSigners: Seq[String] = Seq(depositor)
  }

  implicit val fundCommunityPoolReads: Reads[FundCommunityPool] = Json.reads[FundCommunityPool]

  implicit val fundCommunityPoolWrites: OWrites[FundCommunityPool] = Json.writes[FundCommunityPool]

  //evidence
  case class Equivocation(height: Int, time: RFC3339, power: String, consensusAddress: String)

  implicit val equivocationReads: Reads[Equivocation] = Json.reads[Equivocation]

  implicit val equivocationWrites: OWrites[Equivocation] = Json.writes[Equivocation]

  case class SubmitEvidence(submitter: String, evidence: Equivocation) extends TransactionMessage {
    def getSigners: Seq[String] = Seq(submitter)
  }

  implicit val submitEvidenceReads: Reads[SubmitEvidence] = Json.reads[SubmitEvidence]

  implicit val submitEvidenceWrites: OWrites[SubmitEvidence] = Json.writes[SubmitEvidence]

  //feeGrant
  case class FeeGrantAllowance(granter: String, grantee: String, allowance: FeeGrant.Allowance) extends TransactionMessage {
    def getSigners: Seq[String] = Seq(granter)
  }

  implicit val feeGrantAllowanceReads: Reads[FeeGrantAllowance] = Json.reads[FeeGrantAllowance]

  implicit val feeGrantAllowanceWrites: OWrites[FeeGrantAllowance] = Json.writes[FeeGrantAllowance]

  case class FeeRevokeAllowance(granter: String, grantee: String) extends TransactionMessage {
    def getSigners: Seq[String] = Seq(granter)
  }

  implicit val feeRevokeAllowanceReads: Reads[FeeRevokeAllowance] = Json.reads[FeeRevokeAllowance]

  implicit val feeRevokeAllowanceWrites: OWrites[FeeRevokeAllowance] = Json.writes[FeeRevokeAllowance]

  //gov
  case class Deposit(proposalID: Int, depositor: String, amount: Seq[Coin]) extends TransactionMessage {
    def getSigners: Seq[String] = Seq(depositor)
  }

  implicit val depositReads: Reads[Deposit] = Json.reads[Deposit]

  implicit val depositWrites: OWrites[Deposit] = Json.writes[Deposit]

  case class SubmitProposal(content: ProposalContent, initialDeposit: Seq[Coin], proposer: String) extends TransactionMessage {
    def getSigners: Seq[String] = Seq(proposer)
  }

  implicit val submitProposalReads: Reads[SubmitProposal] = Json.reads[SubmitProposal]

  implicit val submitProposalWrites: OWrites[SubmitProposal] = Json.writes[SubmitProposal]

  case class Vote(proposalID: Int, voter: String, option: String) extends TransactionMessage {
    def getSigners: Seq[String] = Seq(voter)
  }

  implicit val voteReads: Reads[Vote] = Json.reads[Vote]

  implicit val voteWrites: OWrites[Vote] = Json.writes[Vote]

  //slashing
  case class Unjail(validatorAddress: String) extends TransactionMessage {
    def getSigners: Seq[String] = Seq(utilities.Crypto.convertOperatorAddressToAccountAddress(validatorAddress))
  }

  implicit val unjailReads: Reads[Unjail] = Json.reads[Unjail]

  implicit val unjailWrites: OWrites[Unjail] = Json.writes[Unjail]

  //staking
  case class CreateValidator(delegatorAddress: String, validatorAddress: String, publicKey: PublicKey, value: Coin, minSelfDelegation: MicroNumber, commissionRates: CommissionRates, description: Description) extends TransactionMessage {
    def getSigners: Seq[String] = {
      val validatorAccountAddress = utilities.Crypto.convertOperatorAddressToAccountAddress(validatorAddress)
      if (validatorAddress == delegatorAddress) Seq(delegatorAddress) else Seq(delegatorAddress, validatorAccountAddress)
    }
  }

  implicit val createValidatorReads: Reads[CreateValidator] = Json.reads[CreateValidator]

  implicit val createValidatorWrites: OWrites[CreateValidator] = Json.writes[CreateValidator]

  case class EditValidator(validatorAddress: String, commissionRate: Option[BigDecimal], description: Description, minSelfDelegation: Option[MicroNumber]) extends TransactionMessage {
    def getSigners: Seq[String] = Seq(utilities.Crypto.convertOperatorAddressToAccountAddress(validatorAddress))
  }

  implicit val editValidatorReads: Reads[EditValidator] = Json.reads[EditValidator]

  implicit val editValidatorWrites: OWrites[EditValidator] = Json.writes[EditValidator]

  case class Delegate(delegatorAddress: String, validatorAddress: String, amount: Coin) extends TransactionMessage {
    def getSigners: Seq[String] = Seq(delegatorAddress)
  }

  implicit val delegateReads: Reads[Delegate] = Json.reads[Delegate]

  implicit val delegateWrites: OWrites[Delegate] = Json.writes[Delegate]

  case class Redelegate(delegatorAddress: String, validatorSrcAddress: String, validatorDstAddress: String, amount: Coin) extends TransactionMessage {
    def getSigners: Seq[String] = Seq(delegatorAddress)
  }

  implicit val redelegateReads: Reads[Redelegate] = Json.reads[Redelegate]

  implicit val redelegateWrites: OWrites[Redelegate] = Json.writes[Redelegate]

  case class Undelegate(delegatorAddress: String, validatorAddress: String, amount: Coin) extends TransactionMessage {
    def getSigners: Seq[String] = Seq(delegatorAddress)
  }

  implicit val undelegateReads: Reads[Undelegate] = Json.reads[Undelegate]

  implicit val undelegateWrites: OWrites[Undelegate] = Json.writes[Undelegate]

  //ibc-client
  case class CreateClient(signer: String) extends TransactionMessage {
    def getSigners: Seq[String] = Seq(signer)
  }

  implicit val createClientReads: Reads[CreateClient] = Json.reads[CreateClient]

  implicit val createClientWrites: OWrites[CreateClient] = Json.writes[CreateClient]

  case class UpdateClient(clientID: String, signer: String) extends TransactionMessage {
    def getSigners: Seq[String] = Seq(signer)
  }

  implicit val updateClientReads: Reads[UpdateClient] = Json.reads[UpdateClient]

  implicit val updateClientWrites: OWrites[UpdateClient] = Json.writes[UpdateClient]

  case class SubmitMisbehaviour(clientID: String, signer: String) extends TransactionMessage {
    def getSigners: Seq[String] = Seq(signer)
  }

  implicit val submitMisbehaviourReads: Reads[SubmitMisbehaviour] = Json.reads[SubmitMisbehaviour]

  implicit val submitMisbehaviourWrites: OWrites[SubmitMisbehaviour] = Json.writes[SubmitMisbehaviour]

  case class UpgradeClient(clientID: String, signer: String) extends TransactionMessage {
    def getSigners: Seq[String] = Seq(signer)
  }

  implicit val upgradeClientReads: Reads[UpgradeClient] = Json.reads[UpgradeClient]

  implicit val upgradeClientWrites: OWrites[UpgradeClient] = Json.writes[UpgradeClient]

  //ibc-connection
  case class ConnectionOpenInit(clientID: String, counterparty: ConnectionCounterparty, version: Option[Version], delayPeriod: String, signer: String) extends TransactionMessage {
    def getSigners: Seq[String] = Seq(signer)
  }

  implicit val connectionOpenInitReads: Reads[ConnectionOpenInit] = Json.reads[ConnectionOpenInit]

  implicit val connectionOpenInitWrites: OWrites[ConnectionOpenInit] = Json.writes[ConnectionOpenInit]

  case class ConnectionOpenConfirm(connectionID: String, proofHeight: ClientHeight, signer: String) extends TransactionMessage {
    def getSigners: Seq[String] = Seq(signer)
  }

  implicit val connectionOpenConfirmReads: Reads[ConnectionOpenConfirm] = Json.reads[ConnectionOpenConfirm]

  implicit val connectionOpenConfirmWrites: OWrites[ConnectionOpenConfirm] = Json.writes[ConnectionOpenConfirm]

  case class ConnectionOpenAck(connectionID: String, counterpartyConnectionID: String, version: Option[Version], proofHeight: ClientHeight, consensusHeight: ClientHeight, signer: String) extends TransactionMessage {
    def getSigners: Seq[String] = Seq(signer)
  }

  implicit val connectionOpenAckReads: Reads[ConnectionOpenAck] = Json.reads[ConnectionOpenAck]

  implicit val connectionOpenAckWrites: OWrites[ConnectionOpenAck] = Json.writes[ConnectionOpenAck]

  case class ConnectionOpenTry(clientID: String, previousConnectionID: String, counterparty: ConnectionCounterparty, delayPeriod: String, counterpartyVersions: Seq[Version], proofHeight: ClientHeight, consensusHeight: ClientHeight, signer: String) extends TransactionMessage {
    def getSigners: Seq[String] = Seq(signer)
  }

  implicit val connectionOpenTryReads: Reads[ConnectionOpenTry] = Json.reads[ConnectionOpenTry]

  implicit val connectionOpenTryWrites: OWrites[ConnectionOpenTry] = Json.writes[ConnectionOpenTry]

  //ibc-channel
  case class ChannelOpenInit(portID: String, channel: Channel, signer: String) extends TransactionMessage {
    def getSigners: Seq[String] = Seq(signer)
  }

  implicit val channelOpenInitReads: Reads[ChannelOpenInit] = Json.reads[ChannelOpenInit]

  implicit val channelOpenInitWrites: OWrites[ChannelOpenInit] = Json.writes[ChannelOpenInit]

  case class ChannelOpenTry(portID: String, previousChannelID: String, channel: Channel, counterpartyVersion: String, proofHeight: ClientHeight, signer: String) extends TransactionMessage {
    def getSigners: Seq[String] = Seq(signer)
  }

  implicit val channelOpenTryReads: Reads[ChannelOpenTry] = Json.reads[ChannelOpenTry]

  implicit val channelOpenTryWrites: OWrites[ChannelOpenTry] = Json.writes[ChannelOpenTry]

  case class ChannelOpenAck(portID: String, channelID: String, counterpartyChannelID: String, counterpartyVersion: String, proofHeight: ClientHeight, signer: String) extends TransactionMessage {
    def getSigners: Seq[String] = Seq(signer)
  }

  implicit val channelOpenAckReads: Reads[ChannelOpenAck] = Json.reads[ChannelOpenAck]

  implicit val channelOpenAckWrites: OWrites[ChannelOpenAck] = Json.writes[ChannelOpenAck]

  case class ChannelOpenConfirm(portID: String, channelID: String, proofHeight: ClientHeight, signer: String) extends TransactionMessage {
    def getSigners: Seq[String] = Seq(signer)
  }

  implicit val channelOpenConfirmReads: Reads[ChannelOpenConfirm] = Json.reads[ChannelOpenConfirm]

  implicit val channelOpenConfirmWrites: OWrites[ChannelOpenConfirm] = Json.writes[ChannelOpenConfirm]

  case class ChannelCloseInit(portID: String, channelID: String, signer: String) extends TransactionMessage {
    def getSigners: Seq[String] = Seq(signer)
  }

  implicit val channelCloseInitReads: Reads[ChannelCloseInit] = Json.reads[ChannelCloseInit]

  implicit val channelCloseInitWrites: OWrites[ChannelCloseInit] = Json.writes[ChannelCloseInit]

  case class ChannelCloseConfirm(portID: String, channelID: String, proofHeight: ClientHeight, signer: String) extends TransactionMessage {
    def getSigners: Seq[String] = Seq(signer)
  }

  implicit val channelCloseConfirmReads: Reads[ChannelCloseConfirm] = Json.reads[ChannelCloseConfirm]

  implicit val channelCloseConfirmWrites: OWrites[ChannelCloseConfirm] = Json.writes[ChannelCloseConfirm]

  case class RecvPacket(packet: Packet, proofHeight: ClientHeight, signer: String) extends TransactionMessage {
    def getSigners: Seq[String] = Seq(signer)
  }

  implicit val recvPacketReads: Reads[RecvPacket] = Json.reads[RecvPacket]

  implicit val recvPacketWrites: OWrites[RecvPacket] = Json.writes[RecvPacket]

  case class Timeout(packet: Packet, proofHeight: ClientHeight, nextSequenceRecv: String, signer: String) extends TransactionMessage {
    def getSigners: Seq[String] = Seq(signer)
  }

  implicit val timeoutReads: Reads[Timeout] = Json.reads[Timeout]

  implicit val timeoutWrites: OWrites[Timeout] = Json.writes[Timeout]

  case class TimeoutOnClose(packet: Packet, proofHeight: ClientHeight, nextSequenceRecv: String, signer: String) extends TransactionMessage {
    def getSigners: Seq[String] = Seq(signer)
  }

  implicit val timeoutOnCloseReads: Reads[TimeoutOnClose] = Json.reads[TimeoutOnClose]

  implicit val timeoutOnCloseWrites: OWrites[TimeoutOnClose] = Json.writes[TimeoutOnClose]

  case class Acknowledgement(packet: Packet, proofHeight: ClientHeight, signer: String) extends TransactionMessage {
    def getSigners: Seq[String] = Seq(signer)
  }

  implicit val acknowledgementReads: Reads[Acknowledgement] = Json.reads[Acknowledgement]

  implicit val acknowledgementWrites: OWrites[Acknowledgement] = Json.writes[Acknowledgement]

  //ibc-transfer
  case class Transfer(sourcePort: String, sourceChannel: String, token: Coin, sender: String, receiver: String, timeoutHeight: ClientHeight, timeoutTimestamp: String) extends TransactionMessage {
    def getSigners: Seq[String] = Seq(sender)
  }

  implicit val transferReads: Reads[Transfer] = Json.reads[Transfer]

  implicit val transferWrites: OWrites[Transfer] = Json.writes[Transfer]

  def stdMsgApply(msgType: String, value: JsObject): StdMsg = {
    msgType match {
      //auth
      case constants.Blockchain.TransactionMessage.CREATE_VESTING_ACCOUNT => StdMsg(msgType, utilities.JSON.convertJsonStringToObject[CreateVestingAccount](value.toString))
      //authz
      case constants.Blockchain.TransactionMessage.GRANT_AUTHORIZATION => StdMsg(msgType, utilities.JSON.convertJsonStringToObject[GrantAuthorization](value.toString))
      case constants.Blockchain.TransactionMessage.REVOKE_AUTHORIZATION => StdMsg(msgType, utilities.JSON.convertJsonStringToObject[RevokeAuthorization](value.toString))
      case constants.Blockchain.TransactionMessage.EXECUTE_AUTHORIZATION => StdMsg(msgType, utilities.JSON.convertJsonStringToObject[ExecuteAuthorization](value.toString))
      //bank
      case constants.Blockchain.TransactionMessage.SEND_COIN => StdMsg(msgType, utilities.JSON.convertJsonStringToObject[SendCoin](value.toString))
      case constants.Blockchain.TransactionMessage.MULTI_SEND => StdMsg(msgType, utilities.JSON.convertJsonStringToObject[MultiSend](value.toString))
      //crisis
      case constants.Blockchain.TransactionMessage.VERIFY_INVARIANT => StdMsg(msgType, utilities.JSON.convertJsonStringToObject[VerifyInvariant](value.toString))
      //distribution
      case constants.Blockchain.TransactionMessage.SET_WITHDRAW_ADDRESS => StdMsg(msgType, utilities.JSON.convertJsonStringToObject[SetWithdrawAddress](value.toString))
      case constants.Blockchain.TransactionMessage.WITHDRAW_DELEGATOR_REWARD => StdMsg(msgType, utilities.JSON.convertJsonStringToObject[WithdrawDelegatorReward](value.toString))
      case constants.Blockchain.TransactionMessage.WITHDRAW_VALIDATOR_COMMISSION => StdMsg(msgType, utilities.JSON.convertJsonStringToObject[WithdrawValidatorCommission](value.toString))
      case constants.Blockchain.TransactionMessage.FUND_COMMUNITY_POOL => StdMsg(msgType, utilities.JSON.convertJsonStringToObject[FundCommunityPool](value.toString))
      //evidence
      case constants.Blockchain.TransactionMessage.SUBMIT_EVIDENCE => StdMsg(msgType, utilities.JSON.convertJsonStringToObject[SubmitEvidence](value.toString))
      //feeGrant
      case constants.Blockchain.TransactionMessage.FEE_GRANT_ALLOWANCE => StdMsg(msgType, utilities.JSON.convertJsonStringToObject[FeeGrantAllowance](value.toString))
      case constants.Blockchain.TransactionMessage.FEE_REVOKE_ALLOWANCE => StdMsg(msgType, utilities.JSON.convertJsonStringToObject[FeeRevokeAllowance](value.toString))
      //gov
      case constants.Blockchain.TransactionMessage.DEPOSIT => StdMsg(msgType, utilities.JSON.convertJsonStringToObject[Deposit](value.toString))
      case constants.Blockchain.TransactionMessage.SUBMIT_PROPOSAL => StdMsg(msgType, utilities.JSON.convertJsonStringToObject[SubmitProposal](value.toString))
      case constants.Blockchain.TransactionMessage.VOTE => StdMsg(msgType, utilities.JSON.convertJsonStringToObject[Vote](value.toString))
      //slashing
      case constants.Blockchain.TransactionMessage.UNJAIL => StdMsg(msgType, utilities.JSON.convertJsonStringToObject[Unjail](value.toString))
      //staking
      case constants.Blockchain.TransactionMessage.CREATE_VALIDATOR => StdMsg(msgType, utilities.JSON.convertJsonStringToObject[CreateValidator](value.toString))
      case constants.Blockchain.TransactionMessage.EDIT_VALIDATOR => StdMsg(msgType, utilities.JSON.convertJsonStringToObject[EditValidator](value.toString))
      case constants.Blockchain.TransactionMessage.DELEGATE => StdMsg(msgType, utilities.JSON.convertJsonStringToObject[Delegate](value.toString))
      case constants.Blockchain.TransactionMessage.REDELEGATE => StdMsg(msgType, utilities.JSON.convertJsonStringToObject[Redelegate](value.toString))
      case constants.Blockchain.TransactionMessage.UNDELEGATE => StdMsg(msgType, utilities.JSON.convertJsonStringToObject[Undelegate](value.toString))
      //ibc-client
      case constants.Blockchain.TransactionMessage.CREATE_CLIENT => StdMsg(msgType, utilities.JSON.convertJsonStringToObject[CreateClient](value.toString))
      case constants.Blockchain.TransactionMessage.UPDATE_CLIENT => StdMsg(msgType, utilities.JSON.convertJsonStringToObject[UpdateClient](value.toString))
      case constants.Blockchain.TransactionMessage.SUBMIT_MISBEHAVIOUR => StdMsg(msgType, utilities.JSON.convertJsonStringToObject[SubmitMisbehaviour](value.toString))
      case constants.Blockchain.TransactionMessage.UPGRADE_CLIENT => StdMsg(msgType, utilities.JSON.convertJsonStringToObject[UpgradeClient](value.toString))
      //ibc-connection
      case constants.Blockchain.TransactionMessage.CONNECTION_OPEN_INIT => StdMsg(msgType, utilities.JSON.convertJsonStringToObject[ConnectionOpenInit](value.toString))
      case constants.Blockchain.TransactionMessage.CONNECTION_OPEN_CONFIRM => StdMsg(msgType, utilities.JSON.convertJsonStringToObject[ConnectionOpenConfirm](value.toString))
      case constants.Blockchain.TransactionMessage.CONNECTION_OPEN_ACK => StdMsg(msgType, utilities.JSON.convertJsonStringToObject[ConnectionOpenAck](value.toString))
      case constants.Blockchain.TransactionMessage.CONNECTION_OPEN_TRY => StdMsg(msgType, utilities.JSON.convertJsonStringToObject[ConnectionOpenTry](value.toString))
      //ibc-channel
      case constants.Blockchain.TransactionMessage.CHANNEL_OPEN_INIT => StdMsg(msgType, utilities.JSON.convertJsonStringToObject[ChannelOpenInit](value.toString))
      case constants.Blockchain.TransactionMessage.CHANNEL_OPEN_TRY => StdMsg(msgType, utilities.JSON.convertJsonStringToObject[ChannelOpenTry](value.toString))
      case constants.Blockchain.TransactionMessage.CHANNEL_OPEN_ACK => StdMsg(msgType, utilities.JSON.convertJsonStringToObject[ChannelOpenAck](value.toString))
      case constants.Blockchain.TransactionMessage.CHANNEL_OPEN_CONFIRM => StdMsg(msgType, utilities.JSON.convertJsonStringToObject[ChannelOpenConfirm](value.toString))
      case constants.Blockchain.TransactionMessage.CHANNEL_CLOSE_INIT => StdMsg(msgType, utilities.JSON.convertJsonStringToObject[ChannelCloseInit](value.toString))
      case constants.Blockchain.TransactionMessage.CHANNEL_CLOSE_CONFIRM => StdMsg(msgType, utilities.JSON.convertJsonStringToObject[ChannelCloseConfirm](value.toString))
      case constants.Blockchain.TransactionMessage.RECV_PACKET => StdMsg(msgType, utilities.JSON.convertJsonStringToObject[RecvPacket](value.toString))
      case constants.Blockchain.TransactionMessage.TIMEOUT => StdMsg(msgType, utilities.JSON.convertJsonStringToObject[Timeout](value.toString))
      case constants.Blockchain.TransactionMessage.TIMEOUT_ON_CLOSE => StdMsg(msgType, utilities.JSON.convertJsonStringToObject[TimeoutOnClose](value.toString))
      case constants.Blockchain.TransactionMessage.ACKNOWLEDGEMENT => StdMsg(msgType, utilities.JSON.convertJsonStringToObject[Acknowledgement](value.toString))
      //ibc-transfer
      case constants.Blockchain.TransactionMessage.TRANSFER => StdMsg(msgType, utilities.JSON.convertJsonStringToObject[Transfer](value.toString))
      case _ => throw new BaseException(constants.Response.UNKNOWN_TRANSACTION_MESSAGE)
    }
  }

}