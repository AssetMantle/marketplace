// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: utilities/test/schema/helpers/base/mappable.proto

package com.test;

public final class MappableProto {
  private MappableProto() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistryLite registry) {
  }

  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
    registerAllExtensions(
        (com.google.protobuf.ExtensionRegistryLite) registry);
  }
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_test_TestMappable_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_test_TestMappable_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static  com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n1utilities/test/schema/helpers/base/map" +
      "pable.proto\022\004test\"5\n\014TestMappable\022\017\n\003i_d" +
      "\030\001 \001(\tR\002iD\022\024\n\005value\030\002 \001(\tR\005valueB\214\001\n\010com" +
      ".testB\rMappableProtoP\001ZAgithub.com/Asset" +
      "Mantle/modules/utilities/test/schema/hel" +
      "pers/base\242\002\003TXX\252\002\004Test\312\002\004Test\342\002\020Test\\GPB" +
      "Metadata\352\002\004Testb\006proto3"
    };
    descriptor = com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
        });
    internal_static_test_TestMappable_descriptor =
      getDescriptor().getMessageTypes().get(0);
    internal_static_test_TestMappable_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_test_TestMappable_descriptor,
        new java.lang.String[] { "ID", "Value", });
  }

  // @@protoc_insertion_point(outer_class_scope)
}
