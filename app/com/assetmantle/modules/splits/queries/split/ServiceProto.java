// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: splits/queries/split/service.proto

package com.assetmantle.modules.splits.queries.split;

public final class ServiceProto {
  private ServiceProto() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistryLite registry) {
  }

  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
    registerAllExtensions(
        (com.google.protobuf.ExtensionRegistryLite) registry);
  }

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static  com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n\"splits/queries/split/service.proto\022(as" +
      "setmantle.modules.splits.queries.split\032\034" +
      "google/api/annotations.proto\032(splits/que" +
      "ries/split/query_request.proto\032)splits/q" +
      "ueries/split/query_response.proto2\251\001\n\005Qu" +
      "ery\022\237\001\n\006Handle\0226.assetmantle.modules.spl" +
      "its.queries.split.QueryRequest\0327.assetma" +
      "ntle.modules.splits.queries.split.QueryR" +
      "esponse\"$\202\323\344\223\002\036\022\034/mantle/splits/v1beta1/" +
      "splitB\271\002\n,com.assetmantle.modules.splits" +
      ".queries.splitB\014ServiceProtoP\001Z5github.c" +
      "om/AssetMantle/modules/x/splits/queries/" +
      "split\242\002\005AMSQS\252\002(Assetmantle.Modules.Spli" +
      "ts.Queries.Split\312\002(Assetmantle\\Modules\\S" +
      "plits\\Queries\\Split\342\0024Assetmantle\\Module" +
      "s\\Splits\\Queries\\Split\\GPBMetadata\352\002,Ass" +
      "etmantle::Modules::Splits::Queries::Spli" +
      "tb\006proto3"
    };
    descriptor = com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
          com.google.api.AnnotationsProto.getDescriptor(),
          com.assetmantle.modules.splits.queries.split.QueryRequestProto.getDescriptor(),
          com.assetmantle.modules.splits.queries.split.QueryResponseProto.getDescriptor(),
        });
    com.google.protobuf.ExtensionRegistry registry =
        com.google.protobuf.ExtensionRegistry.newInstance();
    registry.add(com.google.api.AnnotationsProto.http);
    com.google.protobuf.Descriptors.FileDescriptor
        .internalUpdateFileDescriptor(descriptor, registry);
    com.google.api.AnnotationsProto.getDescriptor();
    com.assetmantle.modules.splits.queries.split.QueryRequestProto.getDescriptor();
    com.assetmantle.modules.splits.queries.split.QueryResponseProto.getDescriptor();
  }

  // @@protoc_insertion_point(outer_class_scope)
}
