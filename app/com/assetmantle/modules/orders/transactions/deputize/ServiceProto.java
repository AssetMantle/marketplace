// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: orders/transactions/deputize/service.proto

package com.assetmantle.modules.orders.transactions.deputize;

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
      "\n*orders/transactions/deputize/service.p" +
      "roto\0220assetmantle.modules.orders.transac" +
      "tions.deputize\032\034google/api/annotations.p" +
      "roto\032*orders/transactions/deputize/messa" +
      "ge.proto\0327orders/transactions/deputize/t" +
      "ransaction_response.proto2\263\001\n\003Msg\022\253\001\n\006Ha" +
      "ndle\0229.assetmantle.modules.orders.transa" +
      "ctions.deputize.Message\032E.assetmantle.mo" +
      "dules.orders.transactions.deputize.Trans" +
      "actionResponse\"\037\202\323\344\223\002\031\"\027/mantle/orders/d" +
      "eputizeB\351\002\n4com.assetmantle.modules.orde" +
      "rs.transactions.deputizeB\014ServiceProtoP\001" +
      "Z=github.com/AssetMantle/modules/x/order" +
      "s/transactions/deputize\242\002\005AMOTD\252\0020Assetm" +
      "antle.Modules.Orders.Transactions.Deputi" +
      "ze\312\0020Assetmantle\\Modules\\Orders\\Transact" +
      "ions\\Deputize\342\002<Assetmantle\\Modules\\Orde" +
      "rs\\Transactions\\Deputize\\GPBMetadata\352\0024A" +
      "ssetmantle::Modules::Orders::Transaction" +
      "s::Deputizeb\006proto3"
    };
    descriptor = com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
          com.google.api.AnnotationsProto.getDescriptor(),
          com.assetmantle.modules.orders.transactions.deputize.MessageProto.getDescriptor(),
          com.assetmantle.modules.orders.transactions.deputize.TransactionResponseProto.getDescriptor(),
        });
    com.google.protobuf.ExtensionRegistry registry =
        com.google.protobuf.ExtensionRegistry.newInstance();
    registry.add(com.google.api.AnnotationsProto.http);
    com.google.protobuf.Descriptors.FileDescriptor
        .internalUpdateFileDescriptor(descriptor, registry);
    com.google.api.AnnotationsProto.getDescriptor();
    com.assetmantle.modules.orders.transactions.deputize.MessageProto.getDescriptor();
    com.assetmantle.modules.orders.transactions.deputize.TransactionResponseProto.getDescriptor();
  }

  // @@protoc_insertion_point(outer_class_scope)
}
