// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: cosmos/mint/v1beta1/genesis.proto

package com.cosmos.mint.v1beta1;

public final class GenesisProto {
  private GenesisProto() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistryLite registry) {
  }

  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
    registerAllExtensions(
        (com.google.protobuf.ExtensionRegistryLite) registry);
  }
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_cosmos_mint_v1beta1_GenesisState_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_cosmos_mint_v1beta1_GenesisState_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static  com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n!cosmos/mint/v1beta1/genesis.proto\022\023cos" +
      "mos.mint.v1beta1\032\024gogoproto/gogo.proto\032\036" +
      "cosmos/mint/v1beta1/mint.proto\"\204\001\n\014Genes" +
      "isState\0229\n\006minter\030\001 \001(\0132\033.cosmos.mint.v1" +
      "beta1.MinterB\004\310\336\037\000R\006minter\0229\n\006params\030\002 \001" +
      "(\0132\033.cosmos.mint.v1beta1.ParamsB\004\310\336\037\000R\006p" +
      "aramsB\300\001\n\027com.cosmos.mint.v1beta1B\014Genes" +
      "isProtoP\001Z)github.com/cosmos/cosmos-sdk/" +
      "x/mint/types\242\002\003CMX\252\002\023Cosmos.Mint.V1beta1" +
      "\312\002\023Cosmos\\Mint\\V1beta1\342\002\037Cosmos\\Mint\\V1b" +
      "eta1\\GPBMetadata\352\002\025Cosmos::Mint::V1beta1" +
      "b\006proto3"
    };
    descriptor = com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
          com.gogoproto.GogoProto.getDescriptor(),
          com.cosmos.mint.v1beta1.MintProto.getDescriptor(),
        });
    internal_static_cosmos_mint_v1beta1_GenesisState_descriptor =
      getDescriptor().getMessageTypes().get(0);
    internal_static_cosmos_mint_v1beta1_GenesisState_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_cosmos_mint_v1beta1_GenesisState_descriptor,
        new java.lang.String[] { "Minter", "Params", });
    com.google.protobuf.ExtensionRegistry registry =
        com.google.protobuf.ExtensionRegistry.newInstance();
    registry.add(com.gogoproto.GogoProto.nullable);
    com.google.protobuf.Descriptors.FileDescriptor
        .internalUpdateFileDescriptor(descriptor, registry);
    com.gogoproto.GogoProto.getDescriptor();
    com.cosmos.mint.v1beta1.MintProto.getDescriptor();
  }

  // @@protoc_insertion_point(outer_class_scope)
}
