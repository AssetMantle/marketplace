package constants

import org.bitcoinj.crypto.ChildNumber
import schema.data.base._
import schema.id.base._
import schema.list.PropertyList
import schema.property.base.{MesaProperty, MetaProperty}
import schema.qualified._
import schema.types.Height
import utilities.{MicroNumber, Wallet}

object Blockchain {
  val AccountPrefix = "mantle"
  val ValidatorPrefix: String = AccountPrefix + "valoper"
  val ValidatorConsensusPublicPrefix: String = AccountPrefix + "valconspub"
  val CoinType = 118
  val MnemonicShown = 4
  val MaximumProperties = 22
  val ChainId: String = CommonConfig.Blockchain.ChainId
  val StakingToken: String = CommonConfig.Blockchain.StakingToken
  val StakingTokenCoinID: CoinID = CoinID(StringID(StakingToken))
  val DefaultSendCoinGasAmount = 120000
  val DefaultIssueIdentityGasLimit = 120000
  val DefaultDefineAssetGasLimit = 150000
  val DefaultMintAssetGasLimit = 150000
  val DefaultMakeOrderGasLimit = 150000
  val DefaultUnwrapGasLimit = 120000
  val DefaultCancelOrderGasLimit = 150000
  val DefaultTakeOrderGasLimit = 150000
  val DefaultGasPrice: BigDecimal = BigDecimal(0)
  val DefaultProvisionGasLimit = 120000
  val DefaultNFTTransferGasLimit = 120000
  val TxTimeoutHeight: Int = 100
  val DefaultHDPath: Seq[ChildNumber] = Seq(
    new ChildNumber(44, true),
    new ChildNumber(CoinType, true),
    new ChildNumber(0, true),
    new ChildNumber(0, false),
    new ChildNumber(0, false)
  )
  val AssetPropertyRate: MicroNumber = CommonConfig.Blockchain.AssetPropertyRate
  val LowGasPrice: Double = CommonConfig.Blockchain.LowGasPrice
  val MediumGasPrice: Double = CommonConfig.Blockchain.MediumGasPrice
  val HighGasPrice: Double = CommonConfig.Blockchain.HighGasPrice
  val MantlePlaceFeeCollectorAddress = "mantle19qxy9t064v79wkslptpjxn3nealzhxhdfe3ldd"
  val MantleNodeMaintainerWallet: Wallet = utilities.Wallet.getWallet("comfort broccoli urban cheap noise income ensure wheat maze cement panel clinic opinion acoustic select sentence code purchase casual dose brown fish salt coral".split(" "))
  val MantlePlaceMaintainerAddress = "mantle19qxy9t064v79wkslptpjxn3nealzhxhdfe3ldd"
  val MantlePlaceIdentityClassificationID: ClassificationID = ClassificationID(utilities.Secrets.base64URLDecode("OXt-qbauoEO41FQzh5oSwcJcCTbxyATh85ufYqevmbs="))
  val MantlePlaceFromID: IdentityID = IdentityID(utilities.Secrets.base64URLDecode("MuFGjnQuCNHHVP7u6HfAJ3tqd3Yc-EpOqT2IT4QetdU="))
  val MantlePlaceOrderClassificationID: ClassificationID = ClassificationID(utilities.Secrets.base64URLDecode("iFQV9507FNXJv__IqxEIffjf4AxXUScgGIe-HUKQ3ek="))
  val IDSeparator = "."
  val OneDec: BigDecimal = BigDecimal("1.000000000000000000")
  val ZeroDec: BigDecimal = BigDecimal("0.0")
  val SmallestDec: BigDecimal = BigDecimal("0.000000000000000001")
  val SmallestDecReciprocal: BigDecimal = 1 / SmallestDec
  // TODO BondRate from parameters
  val BondRate = 1
  val MaxOrderExpiry: Int = (43210 * 6) / 6
  val MaxOrderHours = 72

  val EmptyIdentityID: IdentityID = IdentityID(Array[Byte]())

  val Mint: StringID = StringID("mint")
  val Burn: StringID = StringID("burn")
  val Renumerate: StringID = StringID("renumerate")
  val Add: StringID = StringID("add")
  val Remove: StringID = StringID("remove")
  val Mutate: StringID = StringID("mutate")

  val AuthenticationProperty: MetaProperty = MetaProperty(id = PropertyID(keyID = StringID("authentication"), typeID = constants.Data.ListDataTypeID), data = ListData(Seq()).toAnyData)
  val BondAmountProperty: MetaProperty = MetaProperty(id = PropertyID(keyID = StringID("bondAmount"), typeID = constants.Data.NumberDataTypeID), data = NumberData(0).toAnyData)
  val BondRateProperty: MetaProperty = MetaProperty(id = PropertyID(keyID = StringID("bondRate"), typeID = constants.Data.NumberDataTypeID), data = NumberData(0).toAnyData)
  val BurnHeightProperty: MetaProperty = MetaProperty(id = PropertyID(keyID = StringID("burnHeight"), typeID = constants.Data.HeightDataTypeID), data = HeightData(Height(-1)).toAnyData)
  val ExpiryHeightProperty: MetaProperty = MetaProperty(id = PropertyID(keyID = StringID("expiryHeight"), typeID = constants.Data.HeightDataTypeID), data = HeightData(Height(-1)).toAnyData)
  val ExchangeRateProperty: MetaProperty = MetaProperty(id = PropertyID(keyID = StringID("exchangeRate"), typeID = constants.Data.DecDataTypeID), data = DecData(SmallestDec.toString()).toAnyData)
  val CreationHeightProperty: MetaProperty = MetaProperty(id = PropertyID(keyID = StringID("creationHeight"), typeID = constants.Data.HeightDataTypeID), data = HeightData(Height(-1)).toAnyData)
  val IdentityIDProperty: MetaProperty = MetaProperty(id = PropertyID(keyID = StringID("identityID"), typeID = constants.Data.IDDataTypeID), data = IDData(ClassificationID(HashID(Array[Byte]())).toAnyID).toAnyData)
  val LockProperty: MetaProperty = MetaProperty(id = PropertyID(keyID = StringID("lock"), typeID = constants.Data.HeightDataTypeID), data = HeightData(Height(-1)).toAnyData)
  val MaintainedPropertiesProperty: MetaProperty = MetaProperty(id = PropertyID(keyID = StringID("maintainedProperties"), typeID = constants.Data.ListDataTypeID), data = ListData(Seq()).toAnyData)
  val MaintainedClassificationIDProperty: MetaProperty = MetaProperty(id = PropertyID(keyID = StringID("maintainedClassificationID"), typeID = constants.Data.IDDataTypeID), data = IDData(ClassificationID(HashID(Array[Byte]())).toAnyID).toAnyData)
  val MakerOwnableIDProperty: MetaProperty = MetaProperty(id = PropertyID(keyID = StringID("makerOwnableID"), typeID = constants.Data.IDDataTypeID), data = IDData(StringID("").toAnyID).toAnyData)
  val MakerIDProperty: MetaProperty = MetaProperty(id = PropertyID(keyID = StringID("makerID"), typeID = constants.Data.IDDataTypeID), data = IDData(StringID("").toAnyID).toAnyData)
  val MakerOwnableSplitProperty: MetaProperty = MetaProperty(id = PropertyID(keyID = StringID("makerOwnableSplit"), typeID = constants.Data.DecDataTypeID), data = DecData(SmallestDec.toString()).toAnyData)
  val MaxOrderLifeProperty: MetaProperty = MetaProperty(id = PropertyID(keyID = StringID("maxOrderLife"), typeID = constants.Data.HeightDataTypeID), data = HeightData(Height(-1)).toAnyData)
  val MaxPropertyCountProperty: MetaProperty = MetaProperty(id = PropertyID(keyID = StringID("maxPropertyCount"), typeID = constants.Data.NumberDataTypeID), data = NumberData(0).toAnyData)
  val MaxProvisionAddressCountProperty: MetaProperty = MetaProperty(id = PropertyID(keyID = StringID("maxProvisionAddressCount"), typeID = constants.Data.NumberDataTypeID), data = NumberData(0).toAnyData)
  val NubProperty: MetaProperty = MetaProperty(id = PropertyID(keyID = StringID("nubID"), typeID = constants.Data.IDDataTypeID), data = IDData(StringID("").toAnyID).toAnyData)
  val PermissionsProperty: MetaProperty = MetaProperty(id = PropertyID(keyID = StringID("permissions"), typeID = constants.Data.ListDataTypeID), data = ListData(Seq()).toAnyData)
  val SupplyProperty: MetaProperty = MetaProperty(id = PropertyID(keyID = StringID("supply"), typeID = constants.Data.DecDataTypeID), data = DecData(SmallestDec.toString()).toAnyData)
  val TakerOwnableIDProperty: MetaProperty = MetaProperty(id = PropertyID(keyID = StringID("takerOwnableID"), typeID = constants.Data.IDDataTypeID), data = IDData(StringID("").toAnyID).toAnyData)
  val TakerIDProperty: MetaProperty = MetaProperty(id = PropertyID(keyID = StringID("takerID"), typeID = constants.Data.IDDataTypeID), data = IDData(StringID("").toAnyID).toAnyData)
  val WrapAllowedCoinsProperty: MetaProperty = MetaProperty(id = PropertyID(keyID = StringID("wrapAllowedCoins"), typeID = constants.Data.ListDataTypeID), data = ListData(Seq()).toAnyData)

  val NubClassificationID: ClassificationID = utilities.ID.getClassificationID(Immutables(PropertyList(Seq(NubProperty))), Mutables(PropertyList(Seq(AuthenticationProperty))))
  val OrderIdentityID: IdentityID = utilities.ID.getIdentityID(NubClassificationID, Immutables(PropertyList(Seq(MesaProperty(id = NubProperty.id, dataID = StringData("orders").getDataID)))))

  val Name: String = CommonConfig.Blockchain.name
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
    val ASSET_BURN = "/assets.transactions.burn.Message"
    val ASSET_DEFINE = "/assets.transactions.define.Message"
    val ASSET_DEPUTIZE = "/assets.transactions.deputize.Message"
    val ASSET_MINT = "/assets.transactions.mint.Message"
    val ASSET_MUTATE = "/assets.transactions.mutate.Message"
    val ASSET_RENUMERATE = "/assets.transactions.renumerate.Message"
    val ASSET_REVOKE = "/assets.transactions.revoke.Message"
    //identity
    val IDENTITY_DEFINE = "/identities.transactions.define.Message"
    val IDENTITY_DEPUTIZE = "/identities.transactions.deputize.Message"
    val IDENTITY_ISSUE = "/identities.transactions.issue.Message"
    val IDENTITY_MUTATE = "/identities.transactions.mutate.Message"
    val IDENTITY_NUB = "/identities.transactions.nub.Message"
    val IDENTITY_PROVISION = "/identities.transactions.provision.Message"
    val IDENTITY_QUASH = "/identities.transactions.quash.Message"
    val IDENTITY_REVOKE = "/identities.transactions.revoke.Message"
    val IDENTITY_UNPROVISION = "/identities.transactions.unprovision.Message"
    //split
    val SPLIT_SEND = "/splits.transactions.send.Message"
    val SPLIT_WRAP = "/splits.transactions.wrap.Message"
    val SPLIT_UNWRAP = "/splits.transactions.unwrap.Message"
    //order
    val ORDER_CANCEL = "/orders.transactions.cancel.Message"
    val ORDER_DEFINE = "/orders.transactions.define.Message"
    val ORDER_DEPUTIZE = "/orders.transactions.deputize.Message"
    val ORDER_IMMEDIATE = "/orders.transactions.immediate.Message"
    val ORDER_MAKE = "/orders.transactions.make.Message"
    val ORDER_MODIFY = "/orders.transactions.modify.Message"
    val ORDER_REVOKE = "/orders.transactions.revoke.Message"
    val ORDER_TAKE = "/orders.transactions.take.Message"
    //metaList
    val META_REVEAL = "/metas.transactions.reveal.Message"
  }
}
