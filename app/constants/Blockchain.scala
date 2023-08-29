package constants

import org.bitcoinj.crypto.ChildNumber
import schema.id.base._

object Blockchain {
  val AccountPrefix = "mantle"
  val ValidatorPrefix: String = AccountPrefix + "valoper"
  val ValidatorConsensusPublicPrefix: String = AccountPrefix + "valconspub"
  val CoinType = 118
  val MnemonicShown = 4
  val MaximumProperties = 22
  val ChainId: String = CommonConfig.Blockchain.ChainId
  val StakingToken: String = CommonConfig.Blockchain.StakingToken
  val StakingTokenAssetID: AssetID = schema.document.CoinAsset.getCoinAssetID(StakingToken)
  val UpgradeHeight: Int = CommonConfig.Blockchain.UpgradeHeight
  val DefaultHDPath: Seq[ChildNumber] = Seq(
    new ChildNumber(44, true),
    new ChildNumber(CoinType, true),
    new ChildNumber(0, true),
    new ChildNumber(0, false),
    new ChildNumber(0, false)
  )
  // TODO BondRate from parameters
  val BondRate = 1
  val MaxOrderExpiry: Int = 43210
  val MaxOrderHours = 72
  val EmptyIdentityID: IdentityID = IdentityID(Array[Byte]())

  val RestEndPoint: String = CommonConfig.Blockchain.RestEndPoint
  val RPCEndPoint: String = CommonConfig.Blockchain.RPCEndPoint


  object Account {
    val BASE = "/cosmos.auth.v1beta1.BaseAccount"
    val CONTINUOUS_VESTING = "/cosmos.vesting.v1beta1.ContinuousVestingAccount"
    val DELAYED_VESTING = "/cosmos.vesting.v1beta1.DelayedVestingAccount"
    val MODULE = "/cosmos.auth.v1beta1.ModuleAccount"
    val PERIODIC_VESTING = "/cosmos.vesting.v1beta1.PeriodicVestingAccount"
  }


  object Proposal {
    val PARAMETER_CHANGE = "/cosmos.params.v1beta1.ParameterChangeProposal"
    val TEXT = "/cosmos.gov.v1beta1.TextProposal"
    val COMMUNITY_POOL_SPEND = "/cosmos.distribution.v1beta1.CommunityPoolSpendProposal"
    val SOFTWARE_UPGRADE = "/cosmos.upgrade.v1beta1.SoftwareUpgradeProposal"
    val CANCEL_SOFTWARE_UPGRADE = "/cosmos.upgrade.v1beta1.CancelSoftwareUpgradeProposal"
  }

  object PublicKey {
    val MULTI_SIG = "/cosmos.crypto.multisig.LegacyAminoPubKey"
    val SINGLE = "/cosmos.crypto.secp256k1.PubKey"
    val VALIDATOR = "/cosmos.crypto.ed25519.PubKey"
  }

  object ParameterType {
    val AUTH = "auth"
    val BANK = "bank"
    val CRISIS = "crisis"
    val DISTRIBUTION = "distribution"
    val GOVERNANCE = "gov"
    val HALVING = "halving"
    val IBC = "ibc"
    val MINT = "mint"
    val SLASHING = "slashing"
    val STAKING = "staking"
    val TRANSFER = "transfer"
  }

  object Authz {
    val SEND_AUTHORIZATION = "/cosmos.bank.v1beta1.SendAuthorization"
    val GENERIC_AUTHORIZATION = "/cosmos.authz.v1beta1.GenericAuthorization"
    val STAKE_AUTHORIZATION = "/cosmos.staking.v1beta1.StakeAuthorization"

    object StakeAuthorization {
      val AUTHORIZATION_TYPE_DELEGATE = "AUTHORIZATION_TYPE_DELEGATE"
      val AUTHORIZATION_TYPE_UNDELEGATE = "AUTHORIZATION_TYPE_UNDELEGATE"
      val AUTHORIZATION_TYPE_REDELEGATE = "AUTHORIZATION_TYPE_REDELEGATE"
    }
  }

  object FeeGrant {
    val BASIC_ALLOWANCE = "/cosmos.feegrant.v1beta1.BasicAllowance"
    val PERIODIC_ALLOWANCE = "/cosmos.feegrant.v1beta1.PeriodicAllowance"
    val ALLOWED_MSG_ALLOWANCE = "/cosmos.feegrant.v1beta1.AllowedMsgAllowance"
  }

  object TransactionMessage {
    //auth
    val CREATE_VESTING_ACCOUNT = "/cosmos.vesting.v1beta1.MsgCreateVestingAccount"
    //authz
    val GRANT_AUTHORIZATION = "/cosmos.authz.v1beta1.MsgGrant"
    val REVOKE_AUTHORIZATION = "/cosmos.authz.v1beta1.MsgRevoke"
    val EXECUTE_AUTHORIZATION = "/cosmos.authz.v1beta1.MsgExec"
    //bank
    val SEND_COIN = "/cosmos.bank.v1beta1.MsgSend"
    val MULTI_SEND = "/cosmos.bank.v1beta1.MsgMultiSend"
    //crisis
    val VERIFY_INVARIANT = "/cosmos.crisis.v1beta1.MsgVerifyInvariant"
    //distribution
    val SET_WITHDRAW_ADDRESS = "/cosmos.distribution.v1beta1.MsgSetWithdrawAddress"
    val WITHDRAW_DELEGATOR_REWARD = "/cosmos.distribution.v1beta1.MsgWithdrawDelegatorReward"
    val WITHDRAW_VALIDATOR_COMMISSION = "/cosmos.distribution.v1beta1.MsgWithdrawValidatorCommission"
    val FUND_COMMUNITY_POOL = "/cosmos.distribution.v1beta1.MsgFundCommunityPool"
    //evidence
    val SUBMIT_EVIDENCE = "/cosmos.evidence.v1beta1.MsgSubmitEvidence"
    //feeGrant
    val FEE_GRANT_ALLOWANCE = "/cosmos.feegrant.v1beta1.MsgGrantAllowance"
    val FEE_REVOKE_ALLOWANCE = "/cosmos.feegrant.v1beta1.MsgRevokeAllowance"
    //gov
    val DEPOSIT = "/cosmos.gov.v1beta1.MsgDeposit"
    val SUBMIT_PROPOSAL = "/cosmos.gov.v1beta1.MsgSubmitProposal"
    val VOTE = "/cosmos.gov.v1beta1.MsgVote"
    //slashing
    val UNJAIL = "/cosmos.slashing.v1beta1.MsgUnjail"
    //staking
    val CREATE_VALIDATOR = "/cosmos.staking.v1beta1.MsgCreateValidator"
    val EDIT_VALIDATOR = "/cosmos.staking.v1beta1.MsgEditValidator"
    val DELEGATE = "/cosmos.staking.v1beta1.MsgDelegate"
    val REDELEGATE = "/cosmos.staking.v1beta1.MsgBeginRedelegate"
    val UNDELEGATE = "/cosmos.staking.v1beta1.MsgUndelegate"
    //ibc-client
    val CREATE_CLIENT = "/ibc.core.client.v1.MsgCreateClient"
    val UPDATE_CLIENT = "/ibc.core.client.v1.MsgUpdateClient"
    val UPGRADE_CLIENT = "/ibc.core.client.v1.MsgUpgradeClient"
    val SUBMIT_MISBEHAVIOUR = "/ibc.core.client.v1.MsgSubmitMisbehaviour"
    //ibc-connection
    val CONNECTION_OPEN_INIT = "/ibc.core.connection.v1.MsgConnectionOpenInit"
    val CONNECTION_OPEN_TRY = "/ibc.core.connection.v1.MsgConnectionOpenTry"
    val CONNECTION_OPEN_ACK = "/ibc.core.connection.v1.MsgConnectionOpenAck"
    val CONNECTION_OPEN_CONFIRM = "/ibc.core.connection.v1.MsgConnectionOpenConfirm"
    //ibc-channel
    val CHANNEL_OPEN_INIT = "/ibc.core.channel.v1.MsgChannelOpenInit"
    val CHANNEL_OPEN_TRY = "/ibc.core.channel.v1.MsgChannelOpenTry"
    val CHANNEL_OPEN_ACK = "/ibc.core.channel.v1.MsgChannelOpenAck"
    val CHANNEL_OPEN_CONFIRM = "/ibc.core.channel.v1.MsgChannelOpenConfirm"
    val CHANNEL_CLOSE_INIT = "/ibc.core.channel.v1.MsgChannelCloseInit"
    val CHANNEL_CLOSE_CONFIRM = "/ibc.core.channel.v1.MsgChannelCloseConfirm"
    val RECV_PACKET = "/ibc.core.channel.v1.MsgRecvPacket"
    val TIMEOUT = "/ibc.core.channel.v1.MsgTimeout"
    val TIMEOUT_ON_CLOSE = "/ibc.core.channel.v1.MsgTimeoutOnClose"
    val ACKNOWLEDGEMENT = "/ibc.core.channel.v1.MsgAcknowledgement"
    //ibc-transfer
    val TRANSFER = "/ibc.applications.transfer.v1.MsgTransfer"
    //asset
    val ASSET_BURN = "/assetmantle.modules.assets.transactions.burn.Message"
    val ASSET_DEFINE = "/assetmantle.modules.assets.transactions.define.Message"
    val ASSET_DEPUTIZE = "/assetmantle.modules.assets.transactions.deputize.Message"
    val ASSET_MINT = "/assetmantle.modules.assets.transactions.mint.Message"
    val ASSET_MUTATE = "/assetmantle.modules.assets.transactions.mutate.Message"
    val ASSET_RENUMERATE = "/assets.transactions.renumerate.Message"
    val ASSET_REVOKE = "/assets.transactions.revoke.Message"
    //identity
    val IDENTITY_DEFINE = "/assetmantle.modules.identities.transactions.define.Message"
    val IDENTITY_DEPUTIZE = "/assetmantle.modules.identities.transactions.deputize.Message"
    val IDENTITY_ISSUE = "/assetmantle.modules.identities.transactions.issue.Message"
    val IDENTITY_MUTATE = "/assetmantle.modules.identities.transactions.mutate.Message"
    val IDENTITY_NUB = "/assetmantle.modules.identities.transactions.nub.Message"
    val IDENTITY_PROVISION = "/assetmantle.modules.identities.transactions.provision.Message"
    val IDENTITY_QUASH = "/assetmantle.modules.identities.transactions.quash.Message"
    val IDENTITY_REVOKE = "/assetmantle.modules.identities.transactions.revoke.Message"
    val IDENTITY_UNPROVISION = "/assetmantle.modules.identities.transactions.unprovision.Message"
    //split
    val SPLIT_SEND = "/assetmantle.modules.splits.transactions.send.Message"
    val SPLIT_WRAP = "/assetmantle.modules.splits.transactions.wrap.Message"
    val SPLIT_UNWRAP = "/assetmantle.modules.splits.transactions.unwrap.Message"
    //order
    val ORDER_CANCEL = "/assetmantle.modules.orders.transactions.cancel.Message"
    val ORDER_DEFINE = "/assetmantle.modules.orders.transactions.define.Message"
    val ORDER_DEPUTIZE = "/assetmantle.modules.orders.transactions.deputize.Message"
    val ORDER_IMMEDIATE = "/assetmantle.modules.orders.transactions.immediate.Message"
    val ORDER_MAKE = "/assetmantle.modules.orders.transactions.make.Message"
    val ORDER_MODIFY = "/assetmantle.modules.orders.transactions.modify.Message"
    val ORDER_REVOKE = "/assetmantle.modules.orders.transactions.revoke.Message"
    val ORDER_TAKE = "/assetmantle.modules.orders.transactions.take.Message"
    //meta
    val META_REVEAL = "/assetmantle.modules.metas.transactions.reveal.Message"

  }
}
