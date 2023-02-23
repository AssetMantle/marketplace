// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: cosmos/base/tendermint/v1beta1/query.proto

package com.cosmos.base.tendermint.v1beta1;

public final class QueryProto {
  private QueryProto() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistryLite registry) {
  }

  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
    registerAllExtensions(
        (com.google.protobuf.ExtensionRegistryLite) registry);
  }
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_cosmos_base_tendermint_v1beta1_GetValidatorSetByHeightRequest_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_cosmos_base_tendermint_v1beta1_GetValidatorSetByHeightRequest_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_cosmos_base_tendermint_v1beta1_GetValidatorSetByHeightResponse_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_cosmos_base_tendermint_v1beta1_GetValidatorSetByHeightResponse_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_cosmos_base_tendermint_v1beta1_GetLatestValidatorSetRequest_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_cosmos_base_tendermint_v1beta1_GetLatestValidatorSetRequest_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_cosmos_base_tendermint_v1beta1_GetLatestValidatorSetResponse_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_cosmos_base_tendermint_v1beta1_GetLatestValidatorSetResponse_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_cosmos_base_tendermint_v1beta1_Validator_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_cosmos_base_tendermint_v1beta1_Validator_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_cosmos_base_tendermint_v1beta1_GetBlockByHeightRequest_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_cosmos_base_tendermint_v1beta1_GetBlockByHeightRequest_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_cosmos_base_tendermint_v1beta1_GetBlockByHeightResponse_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_cosmos_base_tendermint_v1beta1_GetBlockByHeightResponse_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_cosmos_base_tendermint_v1beta1_GetLatestBlockRequest_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_cosmos_base_tendermint_v1beta1_GetLatestBlockRequest_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_cosmos_base_tendermint_v1beta1_GetLatestBlockResponse_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_cosmos_base_tendermint_v1beta1_GetLatestBlockResponse_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_cosmos_base_tendermint_v1beta1_GetSyncingRequest_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_cosmos_base_tendermint_v1beta1_GetSyncingRequest_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_cosmos_base_tendermint_v1beta1_GetSyncingResponse_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_cosmos_base_tendermint_v1beta1_GetSyncingResponse_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_cosmos_base_tendermint_v1beta1_GetNodeInfoRequest_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_cosmos_base_tendermint_v1beta1_GetNodeInfoRequest_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_cosmos_base_tendermint_v1beta1_GetNodeInfoResponse_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_cosmos_base_tendermint_v1beta1_GetNodeInfoResponse_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_cosmos_base_tendermint_v1beta1_VersionInfo_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_cosmos_base_tendermint_v1beta1_VersionInfo_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_cosmos_base_tendermint_v1beta1_Module_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_cosmos_base_tendermint_v1beta1_Module_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static  com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n*cosmos/base/tendermint/v1beta1/query.p" +
      "roto\022\036cosmos.base.tendermint.v1beta1\032\024go" +
      "goproto/gogo.proto\032\031google/protobuf/any." +
      "proto\032\034google/api/annotations.proto\032\032ten" +
      "dermint/p2p/types.proto\032\034tendermint/type" +
      "s/block.proto\032\034tendermint/types/types.pr" +
      "oto\032*cosmos/base/query/v1beta1/paginatio" +
      "n.proto\"\200\001\n\036GetValidatorSetByHeightReque" +
      "st\022\026\n\006height\030\001 \001(\003R\006height\022F\n\npagination" +
      "\030\002 \001(\0132&.cosmos.base.query.v1beta1.PageR" +
      "equestR\npagination\"\330\001\n\037GetValidatorSetBy" +
      "HeightResponse\022!\n\014block_height\030\001 \001(\003R\013bl" +
      "ockHeight\022I\n\nvalidators\030\002 \003(\0132).cosmos.b" +
      "ase.tendermint.v1beta1.ValidatorR\nvalida" +
      "tors\022G\n\npagination\030\003 \001(\0132\'.cosmos.base.q" +
      "uery.v1beta1.PageResponseR\npagination\"f\n" +
      "\034GetLatestValidatorSetRequest\022F\n\npaginat" +
      "ion\030\001 \001(\0132&.cosmos.base.query.v1beta1.Pa" +
      "geRequestR\npagination\"\326\001\n\035GetLatestValid" +
      "atorSetResponse\022!\n\014block_height\030\001 \001(\003R\013b" +
      "lockHeight\022I\n\nvalidators\030\002 \003(\0132).cosmos." +
      "base.tendermint.v1beta1.ValidatorR\nvalid" +
      "ators\022G\n\npagination\030\003 \001(\0132\'.cosmos.base." +
      "query.v1beta1.PageResponseR\npagination\"\244" +
      "\001\n\tValidator\022\030\n\007address\030\001 \001(\tR\007address\022-" +
      "\n\007pub_key\030\002 \001(\0132\024.google.protobuf.AnyR\006p" +
      "ubKey\022!\n\014voting_power\030\003 \001(\003R\013votingPower" +
      "\022+\n\021proposer_priority\030\004 \001(\003R\020proposerPri" +
      "ority\"1\n\027GetBlockByHeightRequest\022\026\n\006heig" +
      "ht\030\001 \001(\003R\006height\"\177\n\030GetBlockByHeightResp" +
      "onse\0224\n\010block_id\030\001 \001(\0132\031.tendermint.type" +
      "s.BlockIDR\007blockId\022-\n\005block\030\002 \001(\0132\027.tend" +
      "ermint.types.BlockR\005block\"\027\n\025GetLatestBl" +
      "ockRequest\"}\n\026GetLatestBlockResponse\0224\n\010" +
      "block_id\030\001 \001(\0132\031.tendermint.types.BlockI" +
      "DR\007blockId\022-\n\005block\030\002 \001(\0132\027.tendermint.t" +
      "ypes.BlockR\005block\"\023\n\021GetSyncingRequest\"." +
      "\n\022GetSyncingResponse\022\030\n\007syncing\030\001 \001(\010R\007s" +
      "yncing\"\024\n\022GetNodeInfoRequest\"\300\001\n\023GetNode" +
      "InfoResponse\022K\n\021default_node_info\030\001 \001(\0132" +
      "\037.tendermint.p2p.DefaultNodeInfoR\017defaul" +
      "tNodeInfo\022\\\n\023application_version\030\002 \001(\0132+" +
      ".cosmos.base.tendermint.v1beta1.VersionI" +
      "nfoR\022applicationVersion\"\250\002\n\013VersionInfo\022" +
      "\022\n\004name\030\001 \001(\tR\004name\022\031\n\010app_name\030\002 \001(\tR\007a" +
      "ppName\022\030\n\007version\030\003 \001(\tR\007version\022\035\n\ngit_" +
      "commit\030\004 \001(\tR\tgitCommit\022\035\n\nbuild_tags\030\005 " +
      "\001(\tR\tbuildTags\022\035\n\ngo_version\030\006 \001(\tR\tgoVe" +
      "rsion\022E\n\nbuild_deps\030\007 \003(\0132&.cosmos.base." +
      "tendermint.v1beta1.ModuleR\tbuildDeps\022,\n\022" +
      "cosmos_sdk_version\030\010 \001(\tR\020cosmosSdkVersi" +
      "on\"H\n\006Module\022\022\n\004path\030\001 \001(\tR\004path\022\030\n\007vers" +
      "ion\030\002 \001(\tR\007version\022\020\n\003sum\030\003 \001(\tR\003sum2\210\t\n" +
      "\007Service\022\251\001\n\013GetNodeInfo\0222.cosmos.base.t" +
      "endermint.v1beta1.GetNodeInfoRequest\0323.c" +
      "osmos.base.tendermint.v1beta1.GetNodeInf" +
      "oResponse\"1\202\323\344\223\002+\022)/cosmos/base/tendermi" +
      "nt/v1beta1/node_info\022\244\001\n\nGetSyncing\0221.co" +
      "smos.base.tendermint.v1beta1.GetSyncingR" +
      "equest\0322.cosmos.base.tendermint.v1beta1." +
      "GetSyncingResponse\"/\202\323\344\223\002)\022\'/cosmos/base" +
      "/tendermint/v1beta1/syncing\022\266\001\n\016GetLates" +
      "tBlock\0225.cosmos.base.tendermint.v1beta1." +
      "GetLatestBlockRequest\0326.cosmos.base.tend" +
      "ermint.v1beta1.GetLatestBlockResponse\"5\202" +
      "\323\344\223\002/\022-/cosmos/base/tendermint/v1beta1/b" +
      "locks/latest\022\276\001\n\020GetBlockByHeight\0227.cosm" +
      "os.base.tendermint.v1beta1.GetBlockByHei" +
      "ghtRequest\0328.cosmos.base.tendermint.v1be" +
      "ta1.GetBlockByHeightResponse\"7\202\323\344\223\0021\022//c" +
      "osmos/base/tendermint/v1beta1/blocks/{he" +
      "ight}\022\322\001\n\025GetLatestValidatorSet\022<.cosmos" +
      ".base.tendermint.v1beta1.GetLatestValida" +
      "torSetRequest\032=.cosmos.base.tendermint.v" +
      "1beta1.GetLatestValidatorSetResponse\"<\202\323" +
      "\344\223\0026\0224/cosmos/base/tendermint/v1beta1/va" +
      "lidatorsets/latest\022\332\001\n\027GetValidatorSetBy" +
      "Height\022>.cosmos.base.tendermint.v1beta1." +
      "GetValidatorSetByHeightRequest\032?.cosmos." +
      "base.tendermint.v1beta1.GetValidatorSetB" +
      "yHeightResponse\">\202\323\344\223\0028\0226/cosmos/base/te" +
      "ndermint/v1beta1/validatorsets/{height}B" +
      "\234\002\n\"com.cosmos.base.tendermint.v1beta1B\n" +
      "QueryProtoP\001ZOgithub.com/AssetMantle/mod" +
      "ules/cosmos/base/tendermint/v1beta1;tend" +
      "ermintv1beta1\242\002\003CBT\252\002\036Cosmos.Base.Tender" +
      "mint.V1beta1\312\002\036Cosmos\\Base\\Tendermint\\V1" +
      "beta1\342\002*Cosmos\\Base\\Tendermint\\V1beta1\\G" +
      "PBMetadata\352\002!Cosmos::Base::Tendermint::V" +
      "1beta1b\006proto3"
    };
    descriptor = com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
          com.gogoproto.GogoProto.getDescriptor(),
          com.google.protobuf.AnyProto.getDescriptor(),
          com.google.api.AnnotationsProto.getDescriptor(),
          com.tendermint.p2p.TypesProto.getDescriptor(),
          com.tendermint.types.BlockProto.getDescriptor(),
          com.tendermint.types.TypesProto.getDescriptor(),
          com.cosmos.base.query.v1beta1.PaginationProto.getDescriptor(),
        });
    internal_static_cosmos_base_tendermint_v1beta1_GetValidatorSetByHeightRequest_descriptor =
      getDescriptor().getMessageTypes().get(0);
    internal_static_cosmos_base_tendermint_v1beta1_GetValidatorSetByHeightRequest_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_cosmos_base_tendermint_v1beta1_GetValidatorSetByHeightRequest_descriptor,
        new java.lang.String[] { "Height", "Pagination", });
    internal_static_cosmos_base_tendermint_v1beta1_GetValidatorSetByHeightResponse_descriptor =
      getDescriptor().getMessageTypes().get(1);
    internal_static_cosmos_base_tendermint_v1beta1_GetValidatorSetByHeightResponse_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_cosmos_base_tendermint_v1beta1_GetValidatorSetByHeightResponse_descriptor,
        new java.lang.String[] { "BlockHeight", "Validators", "Pagination", });
    internal_static_cosmos_base_tendermint_v1beta1_GetLatestValidatorSetRequest_descriptor =
      getDescriptor().getMessageTypes().get(2);
    internal_static_cosmos_base_tendermint_v1beta1_GetLatestValidatorSetRequest_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_cosmos_base_tendermint_v1beta1_GetLatestValidatorSetRequest_descriptor,
        new java.lang.String[] { "Pagination", });
    internal_static_cosmos_base_tendermint_v1beta1_GetLatestValidatorSetResponse_descriptor =
      getDescriptor().getMessageTypes().get(3);
    internal_static_cosmos_base_tendermint_v1beta1_GetLatestValidatorSetResponse_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_cosmos_base_tendermint_v1beta1_GetLatestValidatorSetResponse_descriptor,
        new java.lang.String[] { "BlockHeight", "Validators", "Pagination", });
    internal_static_cosmos_base_tendermint_v1beta1_Validator_descriptor =
      getDescriptor().getMessageTypes().get(4);
    internal_static_cosmos_base_tendermint_v1beta1_Validator_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_cosmos_base_tendermint_v1beta1_Validator_descriptor,
        new java.lang.String[] { "Address", "PubKey", "VotingPower", "ProposerPriority", });
    internal_static_cosmos_base_tendermint_v1beta1_GetBlockByHeightRequest_descriptor =
      getDescriptor().getMessageTypes().get(5);
    internal_static_cosmos_base_tendermint_v1beta1_GetBlockByHeightRequest_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_cosmos_base_tendermint_v1beta1_GetBlockByHeightRequest_descriptor,
        new java.lang.String[] { "Height", });
    internal_static_cosmos_base_tendermint_v1beta1_GetBlockByHeightResponse_descriptor =
      getDescriptor().getMessageTypes().get(6);
    internal_static_cosmos_base_tendermint_v1beta1_GetBlockByHeightResponse_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_cosmos_base_tendermint_v1beta1_GetBlockByHeightResponse_descriptor,
        new java.lang.String[] { "BlockId", "Block", });
    internal_static_cosmos_base_tendermint_v1beta1_GetLatestBlockRequest_descriptor =
      getDescriptor().getMessageTypes().get(7);
    internal_static_cosmos_base_tendermint_v1beta1_GetLatestBlockRequest_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_cosmos_base_tendermint_v1beta1_GetLatestBlockRequest_descriptor,
        new java.lang.String[] { });
    internal_static_cosmos_base_tendermint_v1beta1_GetLatestBlockResponse_descriptor =
      getDescriptor().getMessageTypes().get(8);
    internal_static_cosmos_base_tendermint_v1beta1_GetLatestBlockResponse_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_cosmos_base_tendermint_v1beta1_GetLatestBlockResponse_descriptor,
        new java.lang.String[] { "BlockId", "Block", });
    internal_static_cosmos_base_tendermint_v1beta1_GetSyncingRequest_descriptor =
      getDescriptor().getMessageTypes().get(9);
    internal_static_cosmos_base_tendermint_v1beta1_GetSyncingRequest_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_cosmos_base_tendermint_v1beta1_GetSyncingRequest_descriptor,
        new java.lang.String[] { });
    internal_static_cosmos_base_tendermint_v1beta1_GetSyncingResponse_descriptor =
      getDescriptor().getMessageTypes().get(10);
    internal_static_cosmos_base_tendermint_v1beta1_GetSyncingResponse_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_cosmos_base_tendermint_v1beta1_GetSyncingResponse_descriptor,
        new java.lang.String[] { "Syncing", });
    internal_static_cosmos_base_tendermint_v1beta1_GetNodeInfoRequest_descriptor =
      getDescriptor().getMessageTypes().get(11);
    internal_static_cosmos_base_tendermint_v1beta1_GetNodeInfoRequest_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_cosmos_base_tendermint_v1beta1_GetNodeInfoRequest_descriptor,
        new java.lang.String[] { });
    internal_static_cosmos_base_tendermint_v1beta1_GetNodeInfoResponse_descriptor =
      getDescriptor().getMessageTypes().get(12);
    internal_static_cosmos_base_tendermint_v1beta1_GetNodeInfoResponse_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_cosmos_base_tendermint_v1beta1_GetNodeInfoResponse_descriptor,
        new java.lang.String[] { "DefaultNodeInfo", "ApplicationVersion", });
    internal_static_cosmos_base_tendermint_v1beta1_VersionInfo_descriptor =
      getDescriptor().getMessageTypes().get(13);
    internal_static_cosmos_base_tendermint_v1beta1_VersionInfo_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_cosmos_base_tendermint_v1beta1_VersionInfo_descriptor,
        new java.lang.String[] { "Name", "AppName", "Version", "GitCommit", "BuildTags", "GoVersion", "BuildDeps", "CosmosSdkVersion", });
    internal_static_cosmos_base_tendermint_v1beta1_Module_descriptor =
      getDescriptor().getMessageTypes().get(14);
    internal_static_cosmos_base_tendermint_v1beta1_Module_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_cosmos_base_tendermint_v1beta1_Module_descriptor,
        new java.lang.String[] { "Path", "Version", "Sum", });
    com.google.protobuf.ExtensionRegistry registry =
        com.google.protobuf.ExtensionRegistry.newInstance();
    registry.add(com.google.api.AnnotationsProto.http);
    com.google.protobuf.Descriptors.FileDescriptor
        .internalUpdateFileDescriptor(descriptor, registry);
    com.gogoproto.GogoProto.getDescriptor();
    com.google.protobuf.AnyProto.getDescriptor();
    com.google.api.AnnotationsProto.getDescriptor();
    com.tendermint.p2p.TypesProto.getDescriptor();
    com.tendermint.types.BlockProto.getDescriptor();
    com.tendermint.types.TypesProto.getDescriptor();
    com.cosmos.base.query.v1beta1.PaginationProto.getDescriptor();
  }

  // @@protoc_insertion_point(outer_class_scope)
}