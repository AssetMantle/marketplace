// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: ids/base/any_id.proto

package com.assetmantle.schema.ids.base;

public final class AnyIdProto {
  private AnyIdProto() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistryLite registry) {
  }

  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
    registerAllExtensions(
        (com.google.protobuf.ExtensionRegistryLite) registry);
  }
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_assetmantle_schema_ids_base_AnyID_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_assetmantle_schema_ids_base_AnyID_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static  com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n\025ids/base/any_id.proto\022\033assetmantle.sch" +
      "ema.ids.base\032\024gogoproto/gogo.proto\032\027ids/" +
      "base/asset_id.proto\032 ids/base/classifica" +
      "tion_id.proto\032\026ids/base/data_id.proto\032\026i" +
      "ds/base/hash_id.proto\032\032ids/base/identity" +
      "_id.proto\032\034ids/base/maintainer_id.proto\032" +
      "\027ids/base/order_id.proto\032\032ids/base/prope" +
      "rty_id.proto\032\027ids/base/split_id.proto\032\030i" +
      "ds/base/string_id.proto\"\366\005\n\005AnyID\022B\n\tass" +
      "et_i_d\030\001 \001(\0132$.assetmantle.schema.ids.ba" +
      "se.AssetIDH\000R\007assetID\022]\n\022classification_" +
      "i_d\030\002 \001(\0132-.assetmantle.schema.ids.base." +
      "ClassificationIDH\000R\020classificationID\022?\n\010" +
      "data_i_d\030\003 \001(\0132#.assetmantle.schema.ids." +
      "base.DataIDH\000R\006dataID\022?\n\010hash_i_d\030\004 \001(\0132" +
      "#.assetmantle.schema.ids.base.HashIDH\000R\006" +
      "hashID\022K\n\014identity_i_d\030\005 \001(\0132\'.assetmant" +
      "le.schema.ids.base.IdentityIDH\000R\nidentit" +
      "yID\022Q\n\016maintainer_i_d\030\006 \001(\0132).assetmantl" +
      "e.schema.ids.base.MaintainerIDH\000R\014mainta" +
      "inerID\022B\n\torder_i_d\030\007 \001(\0132$.assetmantle." +
      "schema.ids.base.OrderIDH\000R\007orderID\022K\n\014pr" +
      "operty_i_d\030\010 \001(\0132\'.assetmantle.schema.id" +
      "s.base.PropertyIDH\000R\npropertyID\022B\n\tsplit" +
      "_i_d\030\t \001(\0132$.assetmantle.schema.ids.base" +
      ".SplitIDH\000R\007splitID\022E\n\nstring_i_d\030\n \001(\0132" +
      "%.assetmantle.schema.ids.base.StringIDH\000" +
      "R\010stringID:\004\210\240\037\000B\006\n\004implB\350\001\n\037com.assetma" +
      "ntle.schema.ids.baseB\nAnyIdProtoP\001Z)gith" +
      "ub.com/AssetMantle/schema/go/ids/base\242\002\004" +
      "ASIB\252\002\033Assetmantle.Schema.Ids.Base\312\002\033Ass" +
      "etmantle\\Schema\\Ids\\Base\342\002\'Assetmantle\\S" +
      "chema\\Ids\\Base\\GPBMetadata\352\002\036Assetmantle" +
      "::Schema::Ids::Baseb\006proto3"
    };
    descriptor = com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
          com.gogoproto.GogoProto.getDescriptor(),
          com.assetmantle.schema.ids.base.AssetIdProto.getDescriptor(),
          com.assetmantle.schema.ids.base.ClassificationIdProto.getDescriptor(),
          com.assetmantle.schema.ids.base.DataIdProto.getDescriptor(),
          com.assetmantle.schema.ids.base.HashIdProto.getDescriptor(),
          com.assetmantle.schema.ids.base.IdentityIdProto.getDescriptor(),
          com.assetmantle.schema.ids.base.MaintainerIdProto.getDescriptor(),
          com.assetmantle.schema.ids.base.OrderIdProto.getDescriptor(),
          com.assetmantle.schema.ids.base.PropertyIdProto.getDescriptor(),
          com.assetmantle.schema.ids.base.SplitIdProto.getDescriptor(),
          com.assetmantle.schema.ids.base.StringIdProto.getDescriptor(),
        });
    internal_static_assetmantle_schema_ids_base_AnyID_descriptor =
      getDescriptor().getMessageTypes().get(0);
    internal_static_assetmantle_schema_ids_base_AnyID_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_assetmantle_schema_ids_base_AnyID_descriptor,
        new java.lang.String[] { "AssetID", "ClassificationID", "DataID", "HashID", "IdentityID", "MaintainerID", "OrderID", "PropertyID", "SplitID", "StringID", "Impl", });
    com.google.protobuf.ExtensionRegistry registry =
        com.google.protobuf.ExtensionRegistry.newInstance();
    registry.add(com.gogoproto.GogoProto.goprotoGetters);
    com.google.protobuf.Descriptors.FileDescriptor
        .internalUpdateFileDescriptor(descriptor, registry);
    com.gogoproto.GogoProto.getDescriptor();
    com.assetmantle.schema.ids.base.AssetIdProto.getDescriptor();
    com.assetmantle.schema.ids.base.ClassificationIdProto.getDescriptor();
    com.assetmantle.schema.ids.base.DataIdProto.getDescriptor();
    com.assetmantle.schema.ids.base.HashIdProto.getDescriptor();
    com.assetmantle.schema.ids.base.IdentityIdProto.getDescriptor();
    com.assetmantle.schema.ids.base.MaintainerIdProto.getDescriptor();
    com.assetmantle.schema.ids.base.OrderIdProto.getDescriptor();
    com.assetmantle.schema.ids.base.PropertyIdProto.getDescriptor();
    com.assetmantle.schema.ids.base.SplitIdProto.getDescriptor();
    com.assetmantle.schema.ids.base.StringIdProto.getDescriptor();
  }

  // @@protoc_insertion_point(outer_class_scope)
}
