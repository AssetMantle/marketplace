// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: orders/transactions/make/message.proto

package com.assetmantle.modules.orders.transactions.make;

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
    internal_static_assetmantle_modules_orders_transactions_make_Message_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_assetmantle_modules_orders_transactions_make_Message_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static  com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n&orders/transactions/make/message.proto" +
      "\022,assetmantle.modules.orders.transaction" +
      "s.make\032 ids/base/classification_id.proto" +
      "\032\032ids/base/identity_id.proto\032\035ids/base/a" +
      "ny_ownable_id.proto\032\036lists/base/property" +
      "_list.proto\032\027types/base/height.proto\"\336\007\n" +
      "\007Message\022\022\n\004from\030\001 \001(\tR\004from\022A\n\010from_i_d" +
      "\030\002 \001(\0132\'.assetmantle.schema.ids.base.Ide" +
      "ntityIDR\006fromID\022[\n\022classification_i_d\030\003 " +
      "\001(\0132-.assetmantle.schema.ids.base.Classi" +
      "ficationIDR\020classificationID\022C\n\ttaker_i_" +
      "d\030\004 \001(\0132\'.assetmantle.schema.ids.base.Id" +
      "entityIDR\007takerID\022T\n\021maker_ownable_i_d\030\005" +
      " \001(\0132).assetmantle.schema.ids.base.AnyOw" +
      "nableIDR\016makerOwnableID\022T\n\021taker_ownable" +
      "_i_d\030\006 \001(\0132).assetmantle.schema.ids.base" +
      ".AnyOwnableIDR\016takerOwnableID\022D\n\nexpires" +
      "_in\030\007 \001(\0132%.assetmantle.schema.types.bas" +
      "e.HeightR\texpiresIn\022.\n\023maker_ownable_spl" +
      "it\030\010 \001(\tR\021makerOwnableSplit\022.\n\023taker_own" +
      "able_split\030\t \001(\tR\021takerOwnableSplit\022g\n\031i" +
      "mmutable_meta_properties\030\n \001(\0132+.assetma" +
      "ntle.schema.lists.base.PropertyListR\027imm" +
      "utableMetaProperties\022^\n\024immutable_proper" +
      "ties\030\013 \001(\0132+.assetmantle.schema.lists.ba" +
      "se.PropertyListR\023immutableProperties\022c\n\027" +
      "mutable_meta_properties\030\014 \001(\0132+.assetman" +
      "tle.schema.lists.base.PropertyListR\025muta" +
      "bleMetaProperties\022Z\n\022mutable_properties\030" +
      "\r \001(\0132+.assetmantle.schema.lists.base.Pr" +
      "opertyListR\021mutablePropertiesB\321\002\n0com.as" +
      "setmantle.modules.orders.transactions.ma" +
      "keB\014MessageProtoP\001Z9github.com/AssetMant" +
      "le/modules/x/orders/transactions/make\242\002\005" +
      "AMOTM\252\002,Assetmantle.Modules.Orders.Trans" +
      "actions.Make\312\002,Assetmantle\\Modules\\Order" +
      "s\\Transactions\\Make\342\0028Assetmantle\\Module" +
      "s\\Orders\\Transactions\\Make\\GPBMetadata\352\002" +
      "0Assetmantle::Modules::Orders::Transacti" +
      "ons::Makeb\006proto3"
    };
    descriptor = com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
          com.assetmantle.schema.ids.base.ClassificationIdProto.getDescriptor(),
          com.assetmantle.schema.ids.base.IdentityIdProto.getDescriptor(),
          com.assetmantle.schema.ids.base.AnyOwnableIdProto.getDescriptor(),
          com.assetmantle.schema.lists.base.PropertyListProto.getDescriptor(),
          com.assetmantle.schema.types.base.HeightProto.getDescriptor(),
        });
    internal_static_assetmantle_modules_orders_transactions_make_Message_descriptor =
      getDescriptor().getMessageTypes().get(0);
    internal_static_assetmantle_modules_orders_transactions_make_Message_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_assetmantle_modules_orders_transactions_make_Message_descriptor,
        new java.lang.String[] { "From", "FromID", "ClassificationID", "TakerID", "MakerOwnableID", "TakerOwnableID", "ExpiresIn", "MakerOwnableSplit", "TakerOwnableSplit", "ImmutableMetaProperties", "ImmutableProperties", "MutableMetaProperties", "MutableProperties", });
    com.assetmantle.schema.ids.base.ClassificationIdProto.getDescriptor();
    com.assetmantle.schema.ids.base.IdentityIdProto.getDescriptor();
    com.assetmantle.schema.ids.base.AnyOwnableIdProto.getDescriptor();
    com.assetmantle.schema.lists.base.PropertyListProto.getDescriptor();
    com.assetmantle.schema.types.base.HeightProto.getDescriptor();
  }

  // @@protoc_insertion_point(outer_class_scope)
}
