// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: cosmos/base/kv/v1beta1/kv.proto

package com.cosmos.base.kv.v1beta1;

public final class KvProto {
  private KvProto() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistryLite registry) {
  }

  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
    registerAllExtensions(
        (com.google.protobuf.ExtensionRegistryLite) registry);
  }
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_cosmos_base_kv_v1beta1_Pairs_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_cosmos_base_kv_v1beta1_Pairs_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_cosmos_base_kv_v1beta1_Pair_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_cosmos_base_kv_v1beta1_Pair_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static  com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n\037cosmos/base/kv/v1beta1/kv.proto\022\026cosmo" +
      "s.base.kv.v1beta1\032\024gogoproto/gogo.proto\"" +
      "A\n\005Pairs\0228\n\005pairs\030\001 \003(\0132\034.cosmos.base.kv" +
      ".v1beta1.PairB\004\310\336\037\000R\005pairs\".\n\004Pair\022\020\n\003ke" +
      "y\030\001 \001(\014R\003key\022\024\n\005value\030\002 \001(\014R\005valueB\307\001\n\032c" +
      "om.cosmos.base.kv.v1beta1B\007KvProtoP\001Z%gi" +
      "thub.com/cosmos/cosmos-sdk/types/kv\242\002\003CB" +
      "K\252\002\026Cosmos.Base.Kv.V1beta1\312\002\026Cosmos\\Base" +
      "\\Kv\\V1beta1\342\002\"Cosmos\\Base\\Kv\\V1beta1\\GPB" +
      "Metadata\352\002\031Cosmos::Base::Kv::V1beta1b\006pr" +
      "oto3"
    };
    descriptor = com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
          com.gogoproto.GogoProto.getDescriptor(),
        });
    internal_static_cosmos_base_kv_v1beta1_Pairs_descriptor =
      getDescriptor().getMessageTypes().get(0);
    internal_static_cosmos_base_kv_v1beta1_Pairs_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_cosmos_base_kv_v1beta1_Pairs_descriptor,
        new java.lang.String[] { "Pairs", });
    internal_static_cosmos_base_kv_v1beta1_Pair_descriptor =
      getDescriptor().getMessageTypes().get(1);
    internal_static_cosmos_base_kv_v1beta1_Pair_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_cosmos_base_kv_v1beta1_Pair_descriptor,
        new java.lang.String[] { "Key", "Value", });
    com.google.protobuf.ExtensionRegistry registry =
        com.google.protobuf.ExtensionRegistry.newInstance();
    registry.add(com.gogoproto.GogoProto.nullable);
    com.google.protobuf.Descriptors.FileDescriptor
        .internalUpdateFileDescriptor(descriptor, registry);
    com.gogoproto.GogoProto.getDescriptor();
  }

  // @@protoc_insertion_point(outer_class_scope)
}