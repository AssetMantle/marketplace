// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: maintainers/queries/maintainer/query_response.proto

package com.assetmantle.modules.maintainers.queries.maintainer;

public interface QueryResponseOrBuilder extends
    // @@protoc_insertion_point(interface_extends:assetmantle.modules.maintainers.queries.maintainer.QueryResponse)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <code>.assetmantle.modules.maintainers.record.Record record = 1 [json_name = "record"];</code>
   * @return Whether the record field is set.
   */
  boolean hasRecord();
  /**
   * <code>.assetmantle.modules.maintainers.record.Record record = 1 [json_name = "record"];</code>
   * @return The record.
   */
  com.assetmantle.modules.maintainers.record.Record getRecord();
  /**
   * <code>.assetmantle.modules.maintainers.record.Record record = 1 [json_name = "record"];</code>
   */
  com.assetmantle.modules.maintainers.record.RecordOrBuilder getRecordOrBuilder();
}
