// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: ids/base/hash_id.proto

package com.assetmantle.schema.ids.base;

public final class HashIdProto {
  private HashIdProto() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistryLite registry) {
  }

  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
    registerAllExtensions(
        (com.google.protobuf.ExtensionRegistryLite) registry);
  }
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_assetmantle_schema_ids_base_HashID_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_assetmantle_schema_ids_base_HashID_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static  com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n\026ids/base/hash_id.proto\022\033assetmantle.sc" +
      "hema.ids.base\032\024gogoproto/gogo.proto\"*\n\006H" +
      "ashID\022\032\n\ti_d_bytes\030\001 \001(\014R\007iDBytes:\004\210\240\037\000B" +
      "\351\001\n\037com.assetmantle.schema.ids.baseB\013Has" +
      "hIdProtoP\001Z)github.com/AssetMantle/schem" +
      "a/go/ids/base\242\002\004ASIB\252\002\033Assetmantle.Schem" +
      "a.Ids.Base\312\002\033Assetmantle\\Schema\\Ids\\Base" +
      "\342\002\'Assetmantle\\Schema\\Ids\\Base\\GPBMetada" +
      "ta\352\002\036Assetmantle::Schema::Ids::Baseb\006pro" +
      "to3"
    };
    descriptor = com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
          com.gogoproto.GogoProto.getDescriptor(),
        });
    internal_static_assetmantle_schema_ids_base_HashID_descriptor =
      getDescriptor().getMessageTypes().get(0);
    internal_static_assetmantle_schema_ids_base_HashID_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_assetmantle_schema_ids_base_HashID_descriptor,
        new java.lang.String[] { "IDBytes", });
    com.google.protobuf.ExtensionRegistry registry =
        com.google.protobuf.ExtensionRegistry.newInstance();
    registry.add(com.gogoproto.GogoProto.goprotoGetters);
    com.google.protobuf.Descriptors.FileDescriptor
        .internalUpdateFileDescriptor(descriptor, registry);
    com.gogoproto.GogoProto.getDescriptor();
  }

  // @@protoc_insertion_point(outer_class_scope)
}