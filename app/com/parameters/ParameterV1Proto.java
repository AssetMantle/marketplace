// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: schema/parameters/base/parameter.v1.proto

package com.parameters;

public final class ParameterV1Proto {
  private ParameterV1Proto() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistryLite registry) {
  }

  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
    registerAllExtensions(
        (com.google.protobuf.ExtensionRegistryLite) registry);
  }
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_parameters_Parameter_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_parameters_Parameter_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static  com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n)schema/parameters/base/parameter.v1.pr" +
      "oto\022\nparameters\032\024gogoproto/gogo.proto\032,s" +
      "chema/properties/base/metaProperty.v1.pr" +
      "oto\"P\n\tParameter\022=\n\rmeta_property\030\001 \001(\0132" +
      "\030.properties.MetaPropertyR\014metaProperty:" +
      "\004\210\240\037\000B\241\001\n\016com.parametersB\020ParameterV1Pro" +
      "toP\001Z5github.com/AssetMantle/modules/sch" +
      "ema/parameters/base\242\002\003PXX\252\002\nParameters\312\002" +
      "\nParameters\342\002\026Parameters\\GPBMetadata\352\002\nP" +
      "arametersb\006proto3"
    };
    descriptor = com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
          com.gogoproto.GogoProto.getDescriptor(),
          com.properties.MetaPropertyV1Proto.getDescriptor(),
        });
    internal_static_parameters_Parameter_descriptor =
      getDescriptor().getMessageTypes().get(0);
    internal_static_parameters_Parameter_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_parameters_Parameter_descriptor,
        new java.lang.String[] { "MetaProperty", });
    com.google.protobuf.ExtensionRegistry registry =
        com.google.protobuf.ExtensionRegistry.newInstance();
    registry.add(com.gogoproto.GogoProto.goprotoGetters);
    com.google.protobuf.Descriptors.FileDescriptor
        .internalUpdateFileDescriptor(descriptor, registry);
    com.gogoproto.GogoProto.getDescriptor();
    com.properties.MetaPropertyV1Proto.getDescriptor();
  }

  // @@protoc_insertion_point(outer_class_scope)
}