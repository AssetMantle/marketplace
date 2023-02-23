// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: cosmos/distribution/v1beta1/query.proto

package com.cosmos.distribution.v1beta1;

public interface QueryDelegatorValidatorsRequestOrBuilder extends
    // @@protoc_insertion_point(interface_extends:cosmos.distribution.v1beta1.QueryDelegatorValidatorsRequest)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <pre>
   * delegator_address defines the delegator address to query for.
   * </pre>
   *
   * <code>string delegator_address = 1 [json_name = "delegatorAddress"];</code>
   * @return The delegatorAddress.
   */
  java.lang.String getDelegatorAddress();
  /**
   * <pre>
   * delegator_address defines the delegator address to query for.
   * </pre>
   *
   * <code>string delegator_address = 1 [json_name = "delegatorAddress"];</code>
   * @return The bytes for delegatorAddress.
   */
  com.google.protobuf.ByteString
      getDelegatorAddressBytes();
}