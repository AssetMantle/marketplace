// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: orders/queries/orders/service.proto

package com.assetmantle.modules.orders.queries.orders;

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
      "\n#orders/queries/orders/service.proto\022)a" +
      "ssetmantle.modules.orders.queries.orders" +
      "\032\034google/api/annotations.proto\032)orders/q" +
      "ueries/orders/query_request.proto\032*order" +
      "s/queries/orders/query_response.proto2\254\001" +
      "\n\005Query\022\242\001\n\006Handle\0227.assetmantle.modules" +
      ".orders.queries.orders.QueryRequest\0328.as" +
      "setmantle.modules.orders.queries.orders." +
      "QueryResponse\"%\202\323\344\223\002\037\022\035/mantle/orders/v1" +
      "beta1/ordersB\277\002\n-com.assetmantle.modules" +
      ".orders.queries.ordersB\014ServiceProtoP\001Z6" +
      "github.com/AssetMantle/modules/x/orders/" +
      "queries/orders\242\002\005AMOQO\252\002)Assetmantle.Mod" +
      "ules.Orders.Queries.Orders\312\002)Assetmantle" +
      "\\Modules\\Orders\\Queries\\Orders\342\0025Assetma" +
      "ntle\\Modules\\Orders\\Queries\\Orders\\GPBMe" +
      "tadata\352\002-Assetmantle::Modules::Orders::Q" +
      "ueries::Ordersb\006proto3"
    };
    descriptor = com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
          com.google.api.AnnotationsProto.getDescriptor(),
          com.assetmantle.modules.orders.queries.orders.QueryRequestProto.getDescriptor(),
          com.assetmantle.modules.orders.queries.orders.QueryResponseProto.getDescriptor(),
        });
    com.google.protobuf.ExtensionRegistry registry =
        com.google.protobuf.ExtensionRegistry.newInstance();
    registry.add(com.google.api.AnnotationsProto.http);
    com.google.protobuf.Descriptors.FileDescriptor
        .internalUpdateFileDescriptor(descriptor, registry);
    com.google.api.AnnotationsProto.getDescriptor();
    com.assetmantle.modules.orders.queries.orders.QueryRequestProto.getDescriptor();
    com.assetmantle.modules.orders.queries.orders.QueryResponseProto.getDescriptor();
  }

  // @@protoc_insertion_point(outer_class_scope)
}
