// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: maintainers/queries/maintainers/query_request.proto

package com.assetmantle.modules.maintainers.queries.maintainers;

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
    internal_static_assetmantle_modules_maintainers_queries_maintainers_QueryRequest_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_assetmantle_modules_maintainers_queries_maintainers_QueryRequest_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static  com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n3maintainers/queries/maintainers/query_" +
      "request.proto\0223assetmantle.modules.maint" +
      "ainers.queries.maintainers\032\031maintainers/" +
      "key/key.proto\"`\n\014QueryRequest\022:\n\003key\030\001 \001" +
      "(\0132(.assetmantle.modules.maintainers.key" +
      ".KeyR\003key\022\024\n\005limit\030\002 \001(\005R\005limitB\200\003\n7com." +
      "assetmantle.modules.maintainers.queries." +
      "maintainersB\021QueryRequestProtoP\001Z@github" +
      ".com/AssetMantle/modules/x/maintainers/q" +
      "ueries/maintainers\242\002\005AMMQM\252\0023Assetmantle" +
      ".Modules.Maintainers.Queries.Maintainers" +
      "\312\0023Assetmantle\\Modules\\Maintainers\\Queri" +
      "es\\Maintainers\342\002?Assetmantle\\Modules\\Mai" +
      "ntainers\\Queries\\Maintainers\\GPBMetadata" +
      "\352\0027Assetmantle::Modules::Maintainers::Qu" +
      "eries::Maintainersb\006proto3"
    };
    descriptor = com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
          com.assetmantle.modules.maintainers.key.KeyProto.getDescriptor(),
        });
    internal_static_assetmantle_modules_maintainers_queries_maintainers_QueryRequest_descriptor =
      getDescriptor().getMessageTypes().get(0);
    internal_static_assetmantle_modules_maintainers_queries_maintainers_QueryRequest_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_assetmantle_modules_maintainers_queries_maintainers_QueryRequest_descriptor,
        new java.lang.String[] { "Key", "Limit", });
    com.assetmantle.modules.maintainers.key.KeyProto.getDescriptor();
  }

  // @@protoc_insertion_point(outer_class_scope)
}