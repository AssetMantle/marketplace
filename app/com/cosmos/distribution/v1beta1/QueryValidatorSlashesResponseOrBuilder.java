// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: cosmos/distribution/v1beta1/query.proto

package com.cosmos.distribution.v1beta1;

public interface QueryValidatorSlashesResponseOrBuilder extends
    // @@protoc_insertion_point(interface_extends:cosmos.distribution.v1beta1.QueryValidatorSlashesResponse)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <pre>
   * slashes defines the slashes the validator received.
   * </pre>
   *
   * <code>repeated .cosmos.distribution.v1beta1.ValidatorSlashEvent slashes = 1 [json_name = "slashes", (.gogoproto.nullable) = false];</code>
   */
  java.util.List<com.cosmos.distribution.v1beta1.ValidatorSlashEvent> 
      getSlashesList();
  /**
   * <pre>
   * slashes defines the slashes the validator received.
   * </pre>
   *
   * <code>repeated .cosmos.distribution.v1beta1.ValidatorSlashEvent slashes = 1 [json_name = "slashes", (.gogoproto.nullable) = false];</code>
   */
  com.cosmos.distribution.v1beta1.ValidatorSlashEvent getSlashes(int index);
  /**
   * <pre>
   * slashes defines the slashes the validator received.
   * </pre>
   *
   * <code>repeated .cosmos.distribution.v1beta1.ValidatorSlashEvent slashes = 1 [json_name = "slashes", (.gogoproto.nullable) = false];</code>
   */
  int getSlashesCount();
  /**
   * <pre>
   * slashes defines the slashes the validator received.
   * </pre>
   *
   * <code>repeated .cosmos.distribution.v1beta1.ValidatorSlashEvent slashes = 1 [json_name = "slashes", (.gogoproto.nullable) = false];</code>
   */
  java.util.List<? extends com.cosmos.distribution.v1beta1.ValidatorSlashEventOrBuilder> 
      getSlashesOrBuilderList();
  /**
   * <pre>
   * slashes defines the slashes the validator received.
   * </pre>
   *
   * <code>repeated .cosmos.distribution.v1beta1.ValidatorSlashEvent slashes = 1 [json_name = "slashes", (.gogoproto.nullable) = false];</code>
   */
  com.cosmos.distribution.v1beta1.ValidatorSlashEventOrBuilder getSlashesOrBuilder(
      int index);

  /**
   * <pre>
   * pagination defines the pagination in the response.
   * </pre>
   *
   * <code>.cosmos.base.query.v1beta1.PageResponse pagination = 2 [json_name = "pagination"];</code>
   * @return Whether the pagination field is set.
   */
  boolean hasPagination();
  /**
   * <pre>
   * pagination defines the pagination in the response.
   * </pre>
   *
   * <code>.cosmos.base.query.v1beta1.PageResponse pagination = 2 [json_name = "pagination"];</code>
   * @return The pagination.
   */
  com.cosmos.base.query.v1beta1.PageResponse getPagination();
  /**
   * <pre>
   * pagination defines the pagination in the response.
   * </pre>
   *
   * <code>.cosmos.base.query.v1beta1.PageResponse pagination = 2 [json_name = "pagination"];</code>
   */
  com.cosmos.base.query.v1beta1.PageResponseOrBuilder getPaginationOrBuilder();
}
