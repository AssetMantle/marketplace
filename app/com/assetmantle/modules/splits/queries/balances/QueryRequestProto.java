// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: splits/queries/balances/query_request.proto

package com.assetmantle.modules.splits.queries.balances;

public final class QueryRequestProto {
  private QueryRequestProto() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistryLite registry) {
  }

  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
    registerAllExtensions(
        (com.google.protobuf.ExtensionRegistryLite) registry);
  }
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_assetmantle_modules_splits_queries_balances_QueryRequest_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_assetmantle_modules_splits_queries_balances_QueryRequest_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static  com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n+splits/queries/balances/query_request." +
      "proto\022+assetmantle.modules.splits.querie" +
      "s.balances\032\032ids/base/identity_id.proto\032\024" +
      "splits/key/key.proto\"\246\001\n\014QueryRequest\0225\n" +
      "\003key\030\001 \001(\0132#.assetmantle.modules.splits." +
      "key.KeyR\003key\022\024\n\005limit\030\002 \001(\005R\005limit\022I\n\014id" +
      "entity_i_d\030\003 \001(\0132\'.assetmantle.schema.id" +
      "s.base.IdentityIDR\nidentityIDB\320\002\n/com.as" +
      "setmantle.modules.splits.queries.balance" +
      "sB\021QueryRequestProtoP\001Z8github.com/Asset" +
      "Mantle/modules/x/splits/queries/balances" +
      "\242\002\005AMSQB\252\002+Assetmantle.Modules.Splits.Qu" +
      "eries.Balances\312\002+Assetmantle\\Modules\\Spl" +
      "its\\Queries\\Balances\342\0027Assetmantle\\Modul" +
      "es\\Splits\\Queries\\Balances\\GPBMetadata\352\002" +
      "/Assetmantle::Modules::Splits::Queries::" +
      "Balancesb\006proto3"
    };
    descriptor = com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
          com.assetmantle.schema.ids.base.IdentityIdProto.getDescriptor(),
          com.assetmantle.modules.splits.key.KeyProto.getDescriptor(),
        });
    internal_static_assetmantle_modules_splits_queries_balances_QueryRequest_descriptor =
      getDescriptor().getMessageTypes().get(0);
    internal_static_assetmantle_modules_splits_queries_balances_QueryRequest_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_assetmantle_modules_splits_queries_balances_QueryRequest_descriptor,
        new java.lang.String[] { "Key", "Limit", "IdentityID", });
    com.assetmantle.schema.ids.base.IdentityIdProto.getDescriptor();
    com.assetmantle.modules.splits.key.KeyProto.getDescriptor();
  }

  // @@protoc_insertion_point(outer_class_scope)
}
