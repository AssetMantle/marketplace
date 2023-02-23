// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: cosmos/vesting/v1beta1/vesting.proto

package com.cosmos.vesting.v1beta1;

public final class VestingProto {
  private VestingProto() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistryLite registry) {
  }

  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
    registerAllExtensions(
        (com.google.protobuf.ExtensionRegistryLite) registry);
  }
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_cosmos_vesting_v1beta1_BaseVestingAccount_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_cosmos_vesting_v1beta1_BaseVestingAccount_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_cosmos_vesting_v1beta1_ContinuousVestingAccount_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_cosmos_vesting_v1beta1_ContinuousVestingAccount_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_cosmos_vesting_v1beta1_DelayedVestingAccount_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_cosmos_vesting_v1beta1_DelayedVestingAccount_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_cosmos_vesting_v1beta1_Period_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_cosmos_vesting_v1beta1_Period_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_cosmos_vesting_v1beta1_PeriodicVestingAccount_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_cosmos_vesting_v1beta1_PeriodicVestingAccount_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_cosmos_vesting_v1beta1_PermanentLockedAccount_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_cosmos_vesting_v1beta1_PermanentLockedAccount_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static  com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n$cosmos/vesting/v1beta1/vesting.proto\022\026" +
      "cosmos.vesting.v1beta1\032\024gogoproto/gogo.p" +
      "roto\032\036cosmos/base/v1beta1/coin.proto\032\036co" +
      "smos/auth/v1beta1/auth.proto\"\322\004\n\022BaseVes" +
      "tingAccount\022I\n\014base_account\030\001 \001(\0132 .cosm" +
      "os.auth.v1beta1.BaseAccountB\004\320\336\037\001R\013baseA" +
      "ccount\022\221\001\n\020original_vesting\030\002 \003(\0132\031.cosm" +
      "os.base.v1beta1.CoinBK\310\336\037\000\362\336\037\027yaml:\"orig" +
      "inal_vesting\"\252\337\037(github.com/cosmos/cosmo" +
      "s-sdk/types.CoinsR\017originalVesting\022\213\001\n\016d" +
      "elegated_free\030\003 \003(\0132\031.cosmos.base.v1beta" +
      "1.CoinBI\310\336\037\000\362\336\037\025yaml:\"delegated_free\"\252\337\037" +
      "(github.com/cosmos/cosmos-sdk/types.Coin" +
      "sR\rdelegatedFree\022\224\001\n\021delegated_vesting\030\004" +
      " \003(\0132\031.cosmos.base.v1beta1.CoinBL\310\336\037\000\362\336\037" +
      "\030yaml:\"delegated_vesting\"\252\337\037(github.com/" +
      "cosmos/cosmos-sdk/types.CoinsR\020delegated" +
      "Vesting\022.\n\010end_time\030\005 \001(\003B\023\362\336\037\017yaml:\"end" +
      "_time\"R\007endTime:\010\210\240\037\000\230\240\037\000\"\276\001\n\030Continuous" +
      "VestingAccount\022b\n\024base_vesting_account\030\001" +
      " \001(\0132*.cosmos.vesting.v1beta1.BaseVestin" +
      "gAccountB\004\320\336\037\001R\022baseVestingAccount\0224\n\nst" +
      "art_time\030\002 \001(\003B\025\362\336\037\021yaml:\"start_time\"R\ts" +
      "tartTime:\010\210\240\037\000\230\240\037\000\"\205\001\n\025DelayedVestingAcc" +
      "ount\022b\n\024base_vesting_account\030\001 \001(\0132*.cos" +
      "mos.vesting.v1beta1.BaseVestingAccountB\004" +
      "\320\336\037\001R\022baseVestingAccount:\010\210\240\037\000\230\240\037\000\"\213\001\n\006P" +
      "eriod\022\026\n\006length\030\001 \001(\003R\006length\022c\n\006amount\030" +
      "\002 \003(\0132\031.cosmos.base.v1beta1.CoinB0\310\336\037\000\252\337" +
      "\037(github.com/cosmos/cosmos-sdk/types.Coi" +
      "nsR\006amount:\004\230\240\037\000\"\245\002\n\026PeriodicVestingAcco" +
      "unt\022b\n\024base_vesting_account\030\001 \001(\0132*.cosm" +
      "os.vesting.v1beta1.BaseVestingAccountB\004\320" +
      "\336\037\001R\022baseVestingAccount\0224\n\nstart_time\030\002 " +
      "\001(\003B\025\362\336\037\021yaml:\"start_time\"R\tstartTime\022g\n" +
      "\017vesting_periods\030\003 \003(\0132\036.cosmos.vesting." +
      "v1beta1.PeriodB\036\310\336\037\000\362\336\037\026yaml:\"vesting_pe" +
      "riods\"R\016vestingPeriods:\010\210\240\037\000\230\240\037\000\"\206\001\n\026Per" +
      "manentLockedAccount\022b\n\024base_vesting_acco" +
      "unt\030\001 \001(\0132*.cosmos.vesting.v1beta1.BaseV" +
      "estingAccountB\004\320\336\037\001R\022baseVestingAccount:" +
      "\010\210\240\037\000\230\240\037\000B\352\001\n\032com.cosmos.vesting.v1beta1" +
      "B\014VestingProtoP\001ZDgithub.com/AssetMantle" +
      "/modules/cosmos/vesting/v1beta1;vestingv" +
      "1beta1\242\002\003CVX\252\002\026Cosmos.Vesting.V1beta1\312\002\026" +
      "Cosmos\\Vesting\\V1beta1\342\002\"Cosmos\\Vesting\\" +
      "V1beta1\\GPBMetadata\352\002\030Cosmos::Vesting::V" +
      "1beta1b\006proto3"
    };
    descriptor = com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
          com.gogoproto.GogoProto.getDescriptor(),
          com.cosmos.base.v1beta1.CoinProto.getDescriptor(),
          com.cosmos.auth.v1beta1.AuthProto.getDescriptor(),
        });
    internal_static_cosmos_vesting_v1beta1_BaseVestingAccount_descriptor =
      getDescriptor().getMessageTypes().get(0);
    internal_static_cosmos_vesting_v1beta1_BaseVestingAccount_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_cosmos_vesting_v1beta1_BaseVestingAccount_descriptor,
        new java.lang.String[] { "BaseAccount", "OriginalVesting", "DelegatedFree", "DelegatedVesting", "EndTime", });
    internal_static_cosmos_vesting_v1beta1_ContinuousVestingAccount_descriptor =
      getDescriptor().getMessageTypes().get(1);
    internal_static_cosmos_vesting_v1beta1_ContinuousVestingAccount_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_cosmos_vesting_v1beta1_ContinuousVestingAccount_descriptor,
        new java.lang.String[] { "BaseVestingAccount", "StartTime", });
    internal_static_cosmos_vesting_v1beta1_DelayedVestingAccount_descriptor =
      getDescriptor().getMessageTypes().get(2);
    internal_static_cosmos_vesting_v1beta1_DelayedVestingAccount_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_cosmos_vesting_v1beta1_DelayedVestingAccount_descriptor,
        new java.lang.String[] { "BaseVestingAccount", });
    internal_static_cosmos_vesting_v1beta1_Period_descriptor =
      getDescriptor().getMessageTypes().get(3);
    internal_static_cosmos_vesting_v1beta1_Period_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_cosmos_vesting_v1beta1_Period_descriptor,
        new java.lang.String[] { "Length", "Amount", });
    internal_static_cosmos_vesting_v1beta1_PeriodicVestingAccount_descriptor =
      getDescriptor().getMessageTypes().get(4);
    internal_static_cosmos_vesting_v1beta1_PeriodicVestingAccount_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_cosmos_vesting_v1beta1_PeriodicVestingAccount_descriptor,
        new java.lang.String[] { "BaseVestingAccount", "StartTime", "VestingPeriods", });
    internal_static_cosmos_vesting_v1beta1_PermanentLockedAccount_descriptor =
      getDescriptor().getMessageTypes().get(5);
    internal_static_cosmos_vesting_v1beta1_PermanentLockedAccount_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_cosmos_vesting_v1beta1_PermanentLockedAccount_descriptor,
        new java.lang.String[] { "BaseVestingAccount", });
    com.google.protobuf.ExtensionRegistry registry =
        com.google.protobuf.ExtensionRegistry.newInstance();
    registry.add(com.gogoproto.GogoProto.castrepeated);
    registry.add(com.gogoproto.GogoProto.embed);
    registry.add(com.gogoproto.GogoProto.goprotoGetters);
    registry.add(com.gogoproto.GogoProto.goprotoStringer);
    registry.add(com.gogoproto.GogoProto.moretags);
    registry.add(com.gogoproto.GogoProto.nullable);
    com.google.protobuf.Descriptors.FileDescriptor
        .internalUpdateFileDescriptor(descriptor, registry);
    com.gogoproto.GogoProto.getDescriptor();
    com.cosmos.base.v1beta1.CoinProto.getDescriptor();
    com.cosmos.auth.v1beta1.AuthProto.getDescriptor();
  }

  // @@protoc_insertion_point(outer_class_scope)
}