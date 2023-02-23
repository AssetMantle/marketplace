// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: modules/maintainers/internal/queries/maintainer/queryResponse.v1.proto

package com.maintainers.queries.maintainer;

public interface QueryResponseOrBuilder extends
    // @@protoc_insertion_point(interface_extends:maintainers.queries.maintainer.QueryResponse)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <code>bool success = 1 [json_name = "success"];</code>
   * @return The success.
   */
  boolean getSuccess();

  /**
   * <code>string error = 2 [json_name = "error"];</code>
   * @return The error.
   */
  java.lang.String getError();
  /**
   * <code>string error = 2 [json_name = "error"];</code>
   * @return The bytes for error.
   */
  com.google.protobuf.ByteString
      getErrorBytes();

  /**
   * <code>repeated .maintainers.Mappable list = 3 [json_name = "list"];</code>
   */
  java.util.List<com.maintainers.Mappable> 
      getListList();
  /**
   * <code>repeated .maintainers.Mappable list = 3 [json_name = "list"];</code>
   */
  com.maintainers.Mappable getList(int index);
  /**
   * <code>repeated .maintainers.Mappable list = 3 [json_name = "list"];</code>
   */
  int getListCount();
  /**
   * <code>repeated .maintainers.Mappable list = 3 [json_name = "list"];</code>
   */
  java.util.List<? extends com.maintainers.MappableOrBuilder> 
      getListOrBuilderList();
  /**
   * <code>repeated .maintainers.Mappable list = 3 [json_name = "list"];</code>
   */
  com.maintainers.MappableOrBuilder getListOrBuilder(
      int index);
}