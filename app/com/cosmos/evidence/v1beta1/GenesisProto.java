// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: cosmos/evidence/v1beta1/genesis.proto

package com.cosmos.evidence.v1beta1;

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
    internal_static_cosmos_evidence_v1beta1_GenesisState_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_cosmos_evidence_v1beta1_GenesisState_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static  com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n%cosmos/evidence/v1beta1/genesis.proto\022" +
      "\027cosmos.evidence.v1beta1\032\031google/protobu" +
      "f/any.proto\"@\n\014GenesisState\0220\n\010evidence\030" +
      "\001 \003(\0132\024.google.protobuf.AnyR\010evidenceB\330\001" +
      "\n\033com.cosmos.evidence.v1beta1B\014GenesisPr" +
      "otoP\001Z-github.com/cosmos/cosmos-sdk/x/ev" +
      "idence/types\242\002\003CEX\252\002\027Cosmos.Evidence.V1b" +
      "eta1\312\002\027Cosmos\\Evidence\\V1beta1\342\002#Cosmos\\" +
      "Evidence\\V1beta1\\GPBMetadata\352\002\031Cosmos::E" +
      "vidence::V1beta1b\006proto3"
    };
    descriptor = com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
          com.google.protobuf.AnyProto.getDescriptor(),
        });
    internal_static_cosmos_evidence_v1beta1_GenesisState_descriptor =
      getDescriptor().getMessageTypes().get(0);
    internal_static_cosmos_evidence_v1beta1_GenesisState_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_cosmos_evidence_v1beta1_GenesisState_descriptor,
        new java.lang.String[] { "Evidence", });
    com.google.protobuf.AnyProto.getDescriptor();
  }

  // @@protoc_insertion_point(outer_class_scope)
}