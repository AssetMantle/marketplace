// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: modules/orders/internal/transactions/modify/service.v1.proto

package com.orders.transactions.modify;

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
      "\n<modules/orders/internal/transactions/m" +
      "odify/service.v1.proto\022\032orders.transacti" +
      "ons.modify\032\034google/api/annotations.proto" +
      "\032<modules/orders/internal/transactions/m" +
      "odify/message.v1.proto\032=modules/orders/i" +
      "nternal/transactions/modify/response.v1." +
      "proto2}\n\007Service\022r\n\006Handle\022#.orders.tran" +
      "sactions.modify.Message\032$.orders.transac" +
      "tions.modify.Response\"\035\202\323\344\223\002\027\"\025/mantle/o" +
      "rders/modifyB\206\002\n\036com.orders.transactions" +
      ".modifyB\016ServiceV1ProtoP\001ZJgithub.com/As" +
      "setMantle/modules/modules/orders/interna" +
      "l/transactions/modify\242\002\003OTM\252\002\032Orders.Tra" +
      "nsactions.Modify\312\002\032Orders\\Transactions\\M" +
      "odify\342\002&Orders\\Transactions\\Modify\\GPBMe" +
      "tadata\352\002\034Orders::Transactions::Modifyb\006p" +
      "roto3"
    };
    descriptor = com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
          com.google.api.AnnotationsProto.getDescriptor(),
          com.orders.transactions.modify.MessageV1Proto.getDescriptor(),
          com.orders.transactions.modify.ResponseV1Proto.getDescriptor(),
        });
    com.google.protobuf.ExtensionRegistry registry =
        com.google.protobuf.ExtensionRegistry.newInstance();
    registry.add(com.google.api.AnnotationsProto.http);
    com.google.protobuf.Descriptors.FileDescriptor
        .internalUpdateFileDescriptor(descriptor, registry);
    com.google.api.AnnotationsProto.getDescriptor();
    com.orders.transactions.modify.MessageV1Proto.getDescriptor();
    com.orders.transactions.modify.ResponseV1Proto.getDescriptor();
  }

  // @@protoc_insertion_point(outer_class_scope)
}
