// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: cosmos/staking/v1beta1/query.proto

package com.cosmos.staking.v1beta1;

public interface QueryRedelegationsResponseOrBuilder extends
    // @@protoc_insertion_point(interface_extends:cosmos.staking.v1beta1.QueryRedelegationsResponse)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <code>repeated .cosmos.staking.v1beta1.RedelegationResponse redelegation_responses = 1 [json_name = "redelegationResponses", (.gogoproto.nullable) = false];</code>
   */
  java.util.List<com.cosmos.staking.v1beta1.RedelegationResponse> 
      getRedelegationResponsesList();
  /**
   * <code>repeated .cosmos.staking.v1beta1.RedelegationResponse redelegation_responses = 1 [json_name = "redelegationResponses", (.gogoproto.nullable) = false];</code>
   */
  com.cosmos.staking.v1beta1.RedelegationResponse getRedelegationResponses(int index);
  /**
   * <code>repeated .cosmos.staking.v1beta1.RedelegationResponse redelegation_responses = 1 [json_name = "redelegationResponses", (.gogoproto.nullable) = false];</code>
   */
  int getRedelegationResponsesCount();
  /**
   * <code>repeated .cosmos.staking.v1beta1.RedelegationResponse redelegation_responses = 1 [json_name = "redelegationResponses", (.gogoproto.nullable) = false];</code>
   */
  java.util.List<? extends com.cosmos.staking.v1beta1.RedelegationResponseOrBuilder> 
      getRedelegationResponsesOrBuilderList();
  /**
   * <code>repeated .cosmos.staking.v1beta1.RedelegationResponse redelegation_responses = 1 [json_name = "redelegationResponses", (.gogoproto.nullable) = false];</code>
   */
  com.cosmos.staking.v1beta1.RedelegationResponseOrBuilder getRedelegationResponsesOrBuilder(
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