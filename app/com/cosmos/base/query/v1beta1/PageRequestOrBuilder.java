// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: cosmos/base/query/v1beta1/pagination.proto

package com.cosmos.base.query.v1beta1;

public interface PageRequestOrBuilder extends
    // @@protoc_insertion_point(interface_extends:cosmos.base.query.v1beta1.PageRequest)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <pre>
   * key is a value returned in PageResponse.next_key to begin
   * querying the next page most efficiently. Only one of offset or key
   * should be set.
   * </pre>
   *
   * <code>bytes key = 1 [json_name = "key"];</code>
   * @return The key.
   */
  com.google.protobuf.ByteString getKey();

  /**
   * <pre>
   * offset is a numeric offset that can be used when key is unavailable.
   * It is less efficient than using key. Only one of offset or key should
   * be set.
   * </pre>
   *
   * <code>uint64 offset = 2 [json_name = "offset"];</code>
   * @return The offset.
   */
  long getOffset();

  /**
   * <pre>
   * limit is the total number of results to be returned in the result page.
   * If left empty it will default to a value to be set by each app.
   * </pre>
   *
   * <code>uint64 limit = 3 [json_name = "limit"];</code>
   * @return The limit.
   */
  long getLimit();

  /**
   * <pre>
   * count_total is set to true  to indicate that the result set should include
   * a count of the total number of items available for pagination in UIs.
   * count_total is only respected when offset is used. It is ignored when key
   * is set.
   * </pre>
   *
   * <code>bool count_total = 4 [json_name = "countTotal"];</code>
   * @return The countTotal.
   */
  boolean getCountTotal();

  /**
   * <pre>
   * reverse is set to true if results are to be returned in the descending order.
   *
   * Since: cosmos-sdk 0.43
   * </pre>
   *
   * <code>bool reverse = 5 [json_name = "reverse"];</code>
   * @return The reverse.
   */
  boolean getReverse();
}