// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: identities/queries/identity/service.proto

package com.assetmantle.modules.identities.queries.identity;

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
      "\n)identities/queries/identity/service.pr" +
      "oto\022/assetmantle.modules.identities.quer" +
      "ies.identity\032\034google/api/annotations.pro" +
      "to\032/identities/queries/identity/query_re" +
      "quest.proto\0320identities/queries/identity" +
      "/query_response.proto2\276\001\n\005Query\022\264\001\n\006Hand" +
      "le\022=.assetmantle.modules.identities.quer" +
      "ies.identity.QueryRequest\032>.assetmantle." +
      "modules.identities.queries.identity.Quer" +
      "yResponse\"+\202\323\344\223\002%\022#/mantle/identities/v1" +
      "beta1/identityB\343\002\n3com.assetmantle.modul" +
      "es.identities.queries.identityB\014ServiceP" +
      "rotoP\001Z<github.com/AssetMantle/modules/x" +
      "/identities/queries/identity\242\002\005AMIQI\252\002/A" +
      "ssetmantle.Modules.Identities.Queries.Id" +
      "entity\312\002/Assetmantle\\Modules\\Identities\\" +
      "Queries\\Identity\342\002;Assetmantle\\Modules\\I" +
      "dentities\\Queries\\Identity\\GPBMetadata\352\002" +
      "3Assetmantle::Modules::Identities::Queri" +
      "es::Identityb\006proto3"
    };
    descriptor = com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
          com.google.api.AnnotationsProto.getDescriptor(),
          com.assetmantle.modules.identities.queries.identity.QueryRequestProto.getDescriptor(),
          com.assetmantle.modules.identities.queries.identity.QueryResponseProto.getDescriptor(),
        });
    com.google.protobuf.ExtensionRegistry registry =
        com.google.protobuf.ExtensionRegistry.newInstance();
    registry.add(com.google.api.AnnotationsProto.http);
    com.google.protobuf.Descriptors.FileDescriptor
        .internalUpdateFileDescriptor(descriptor, registry);
    com.google.api.AnnotationsProto.getDescriptor();
    com.assetmantle.modules.identities.queries.identity.QueryRequestProto.getDescriptor();
    com.assetmantle.modules.identities.queries.identity.QueryResponseProto.getDescriptor();
  }

  // @@protoc_insertion_point(outer_class_scope)
}
