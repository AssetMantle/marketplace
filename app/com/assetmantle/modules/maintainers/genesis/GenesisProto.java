// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: maintainers/genesis/genesis.proto

package com.assetmantle.modules.maintainers.genesis;

public final class GenesisProto {
  private GenesisProto() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistryLite registry) {
  }

  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
    registerAllExtensions(
        (com.google.protobuf.ExtensionRegistryLite) registry);
  }
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_assetmantle_modules_maintainers_genesis_Genesis_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_assetmantle_modules_maintainers_genesis_Genesis_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static  com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n!maintainers/genesis/genesis.proto\022\'ass" +
      "etmantle.modules.maintainers.genesis\032\024go" +
      "goproto/gogo.proto\032#maintainers/mappable" +
      "/mappable.proto\032$parameters/base/paramet" +
      "er_list.proto\"\273\001\n\007Genesis\022P\n\tmappables\030\001" +
      " \003(\01322.assetmantle.modules.maintainers.m" +
      "appable.MappableR\tmappables\022X\n\016parameter" +
      "_list\030\002 \001(\01321.assetmantle.schema.paramet" +
      "ers.base.ParameterListR\rparameterList:\004\210" +
      "\240\037\000B\261\002\n+com.assetmantle.modules.maintain" +
      "ers.genesisB\014GenesisProtoP\001Z4github.com/" +
      "AssetMantle/modules/x/maintainers/genesi" +
      "s\242\002\004AMMG\252\002\'Assetmantle.Modules.Maintaine" +
      "rs.Genesis\312\002\'Assetmantle\\Modules\\Maintai" +
      "ners\\Genesis\342\0023Assetmantle\\Modules\\Maint" +
      "ainers\\Genesis\\GPBMetadata\352\002*Assetmantle" +
      "::Modules::Maintainers::Genesisb\006proto3"
    };
    descriptor = com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
          com.gogoproto.GogoProto.getDescriptor(),
          com.assetmantle.modules.maintainers.mappable.MappableProto.getDescriptor(),
          com.assetmantle.schema.parameters.base.ParameterListProto.getDescriptor(),
        });
    internal_static_assetmantle_modules_maintainers_genesis_Genesis_descriptor =
      getDescriptor().getMessageTypes().get(0);
    internal_static_assetmantle_modules_maintainers_genesis_Genesis_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_assetmantle_modules_maintainers_genesis_Genesis_descriptor,
        new java.lang.String[] { "Mappables", "ParameterList", });
    com.google.protobuf.ExtensionRegistry registry =
        com.google.protobuf.ExtensionRegistry.newInstance();
    registry.add(com.gogoproto.GogoProto.goprotoGetters);
    com.google.protobuf.Descriptors.FileDescriptor
        .internalUpdateFileDescriptor(descriptor, registry);
    com.gogoproto.GogoProto.getDescriptor();
    com.assetmantle.modules.maintainers.mappable.MappableProto.getDescriptor();
    com.assetmantle.schema.parameters.base.ParameterListProto.getDescriptor();
  }

  // @@protoc_insertion_point(outer_class_scope)
}
