// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: modules/identities/internal/queries/identity/service.v1.proto

package com.identities.queries.identity;

public final class ServiceV1Proto {
  private ServiceV1Proto() {}
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
      "\n=modules/identities/internal/queries/id" +
      "entity/service.v1.proto\022\033identities.quer" +
      "ies.identity\032\034google/api/annotations.pro" +
      "to\032Bmodules/identities/internal/queries/" +
      "identity/queryRequest.v1.proto\032Cmodules/" +
      "identities/internal/queries/identity/que" +
      "ryResponse.v1.proto2\230\001\n\007Service\022\214\001\n\006Hand" +
      "le\022).identities.queries.identity.QueryRe" +
      "quest\032*.identities.queries.identity.Quer" +
      "yResponse\"+\202\323\344\223\002%\022#/mantle/identities/v1" +
      "beta1/identityB\214\002\n\037com.identities.querie" +
      "s.identityB\016ServiceV1ProtoP\001ZKgithub.com" +
      "/AssetMantle/modules/modules/identities/" +
      "internal/queries/identity\242\002\003IQI\252\002\033Identi" +
      "ties.Queries.Identity\312\002\033Identities\\Queri" +
      "es\\Identity\342\002\'Identities\\Queries\\Identit" +
      "y\\GPBMetadata\352\002\035Identities::Queries::Ide" +
      "ntityb\006proto3"
    };
    descriptor = com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
          com.google.api.AnnotationsProto.getDescriptor(),
          com.identities.queries.identity.QueryRequestV1Proto.getDescriptor(),
          com.identities.queries.identity.QueryResponseV1Proto.getDescriptor(),
        });
    com.google.protobuf.ExtensionRegistry registry =
        com.google.protobuf.ExtensionRegistry.newInstance();
    registry.add(com.google.api.AnnotationsProto.http);
    com.google.protobuf.Descriptors.FileDescriptor
        .internalUpdateFileDescriptor(descriptor, registry);
    com.google.api.AnnotationsProto.getDescriptor();
    com.identities.queries.identity.QueryRequestV1Proto.getDescriptor();
    com.identities.queries.identity.QueryResponseV1Proto.getDescriptor();
  }

  // @@protoc_insertion_point(outer_class_scope)
}
