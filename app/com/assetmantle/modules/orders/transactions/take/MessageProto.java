// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: orders/transactions/take/message.proto

package com.assetmantle.modules.orders.transactions.take;

public final class MessageProto {
  private MessageProto() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistryLite registry) {
  }

  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
    registerAllExtensions(
        (com.google.protobuf.ExtensionRegistryLite) registry);
  }
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_assetmantle_modules_orders_transactions_take_Message_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_assetmantle_modules_orders_transactions_take_Message_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static  com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n&orders/transactions/take/message.proto" +
      "\022,assetmantle.modules.orders.transaction" +
      "s.take\032\032ids/base/identity_id.proto\032\027ids/" +
      "base/order_id.proto\"\322\001\n\007Message\022\022\n\004from\030" +
      "\001 \001(\tR\004from\022A\n\010from_i_d\030\002 \001(\0132\'.assetman" +
      "tle.schema.ids.base.IdentityIDR\006fromID\022." +
      "\n\023taker_ownable_split\030\003 \001(\tR\021takerOwnabl" +
      "eSplit\022@\n\torder_i_d\030\004 \001(\0132$.assetmantle." +
      "schema.ids.base.OrderIDR\007orderIDB\321\002\n0com" +
      ".assetmantle.modules.orders.transactions" +
      ".takeB\014MessageProtoP\001Z9github.com/AssetM" +
      "antle/modules/x/orders/transactions/take" +
      "\242\002\005AMOTT\252\002,Assetmantle.Modules.Orders.Tr" +
      "ansactions.Take\312\002,Assetmantle\\Modules\\Or" +
      "ders\\Transactions\\Take\342\0028Assetmantle\\Mod" +
      "ules\\Orders\\Transactions\\Take\\GPBMetadat" +
      "a\352\0020Assetmantle::Modules::Orders::Transa" +
      "ctions::Takeb\006proto3"
    };
    descriptor = com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
          com.assetmantle.schema.ids.base.IdentityIdProto.getDescriptor(),
          com.assetmantle.schema.ids.base.OrderIdProto.getDescriptor(),
        });
    internal_static_assetmantle_modules_orders_transactions_take_Message_descriptor =
      getDescriptor().getMessageTypes().get(0);
    internal_static_assetmantle_modules_orders_transactions_take_Message_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_assetmantle_modules_orders_transactions_take_Message_descriptor,
        new java.lang.String[] { "From", "FromID", "TakerOwnableSplit", "OrderID", });
    com.assetmantle.schema.ids.base.IdentityIdProto.getDescriptor();
    com.assetmantle.schema.ids.base.OrderIdProto.getDescriptor();
  }

  // @@protoc_insertion_point(outer_class_scope)
}
