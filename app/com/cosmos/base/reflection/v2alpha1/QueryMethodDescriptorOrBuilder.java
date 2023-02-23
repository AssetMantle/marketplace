// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: cosmos/base/reflection/v2alpha1/reflection.proto

package com.cosmos.base.reflection.v2alpha1;

public interface QueryMethodDescriptorOrBuilder extends
    // @@protoc_insertion_point(interface_extends:cosmos.base.reflection.v2alpha1.QueryMethodDescriptor)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <pre>
   * name is the protobuf name (not fullname) of the method
   * </pre>
   *
   * <code>string name = 1 [json_name = "name"];</code>
   * @return The name.
   */
  java.lang.String getName();
  /**
   * <pre>
   * name is the protobuf name (not fullname) of the method
   * </pre>
   *
   * <code>string name = 1 [json_name = "name"];</code>
   * @return The bytes for name.
   */
  com.google.protobuf.ByteString
      getNameBytes();

  /**
   * <pre>
   * full_query_path is the path that can be used to query
   * this method via tendermint abci.Query
   * </pre>
   *
   * <code>string full_query_path = 2 [json_name = "fullQueryPath"];</code>
   * @return The fullQueryPath.
   */
  java.lang.String getFullQueryPath();
  /**
   * <pre>
   * full_query_path is the path that can be used to query
   * this method via tendermint abci.Query
   * </pre>
   *
   * <code>string full_query_path = 2 [json_name = "fullQueryPath"];</code>
   * @return The bytes for fullQueryPath.
   */
  com.google.protobuf.ByteString
      getFullQueryPathBytes();
}