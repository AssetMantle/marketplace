// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: identities/transactions/name/service.proto

package com.assetmantle.modules.identities.transactions.name;

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
      "\n*identities/transactions/name/service.p" +
      "roto\0220assetmantle.modules.identities.tra" +
      "nsactions.name\032\034google/api/annotations.p" +
      "roto\032*identities/transactions/name/messa" +
      "ge.proto\0327identities/transactions/name/t" +
      "ransaction_response.proto2\263\001\n\003Msg\022\253\001\n\006Ha" +
      "ndle\0229.assetmantle.modules.identities.tr" +
      "ansactions.name.Message\032E.assetmantle.mo" +
      "dules.identities.transactions.name.Trans" +
      "actionResponse\"\037\202\323\344\223\002\031\"\027/mantle/identiti" +
      "es/nameB\351\002\n4com.assetmantle.modules.iden" +
      "tities.transactions.nameB\014ServiceProtoP\001" +
      "Z=github.com/AssetMantle/modules/x/ident" +
      "ities/transactions/name\242\002\005AMITN\252\0020Assetm" +
      "antle.Modules.Identities.Transactions.Na" +
      "me\312\0020Assetmantle\\Modules\\Identities\\Tran" +
      "sactions\\Name\342\002<Assetmantle\\Modules\\Iden" +
      "tities\\Transactions\\Name\\GPBMetadata\352\0024A" +
      "ssetmantle::Modules::Identities::Transac" +
      "tions::Nameb\006proto3"
    };
    descriptor = com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
          com.google.api.AnnotationsProto.getDescriptor(),
          com.assetmantle.modules.identities.transactions.name.MessageProto.getDescriptor(),
          com.assetmantle.modules.identities.transactions.name.TransactionResponseProto.getDescriptor(),
        });
    com.google.protobuf.ExtensionRegistry registry =
        com.google.protobuf.ExtensionRegistry.newInstance();
    registry.add(com.google.api.AnnotationsProto.http);
    com.google.protobuf.Descriptors.FileDescriptor
        .internalUpdateFileDescriptor(descriptor, registry);
    com.google.api.AnnotationsProto.getDescriptor();
    com.assetmantle.modules.identities.transactions.name.MessageProto.getDescriptor();
    com.assetmantle.modules.identities.transactions.name.TransactionResponseProto.getDescriptor();
  }

  // @@protoc_insertion_point(outer_class_scope)
}
