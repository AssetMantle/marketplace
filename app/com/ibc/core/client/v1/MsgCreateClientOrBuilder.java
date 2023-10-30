// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: ibc/core/client/v1/tx.proto

package com.ibc.core.client.v1;

public interface MsgCreateClientOrBuilder extends
    // @@protoc_insertion_point(interface_extends:ibc.core.client.v1.MsgCreateClient)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <pre>
   * light client state
   * </pre>
   *
   * <code>.google.protobuf.Any client_state = 1 [json_name = "clientState", (.gogoproto.moretags) = "yaml:&#92;"client_state&#92;""];</code>
   * @return Whether the clientState field is set.
   */
  boolean hasClientState();
  /**
   * <pre>
   * light client state
   * </pre>
   *
   * <code>.google.protobuf.Any client_state = 1 [json_name = "clientState", (.gogoproto.moretags) = "yaml:&#92;"client_state&#92;""];</code>
   * @return The clientState.
   */
  com.google.protobuf.Any getClientState();
  /**
   * <pre>
   * light client state
   * </pre>
   *
   * <code>.google.protobuf.Any client_state = 1 [json_name = "clientState", (.gogoproto.moretags) = "yaml:&#92;"client_state&#92;""];</code>
   */
  com.google.protobuf.AnyOrBuilder getClientStateOrBuilder();

  /**
   * <pre>
   * consensus state associated with the client that corresponds to a given
   * height.
   * </pre>
   *
   * <code>.google.protobuf.Any consensus_state = 2 [json_name = "consensusState", (.gogoproto.moretags) = "yaml:&#92;"consensus_state&#92;""];</code>
   * @return Whether the consensusState field is set.
   */
  boolean hasConsensusState();
  /**
   * <pre>
   * consensus state associated with the client that corresponds to a given
   * height.
   * </pre>
   *
   * <code>.google.protobuf.Any consensus_state = 2 [json_name = "consensusState", (.gogoproto.moretags) = "yaml:&#92;"consensus_state&#92;""];</code>
   * @return The consensusState.
   */
  com.google.protobuf.Any getConsensusState();
  /**
   * <pre>
   * consensus state associated with the client that corresponds to a given
   * height.
   * </pre>
   *
   * <code>.google.protobuf.Any consensus_state = 2 [json_name = "consensusState", (.gogoproto.moretags) = "yaml:&#92;"consensus_state&#92;""];</code>
   */
  com.google.protobuf.AnyOrBuilder getConsensusStateOrBuilder();

  /**
   * <pre>
   * signer address
   * </pre>
   *
   * <code>string signer = 3 [json_name = "signer"];</code>
   * @return The signer.
   */
  java.lang.String getSigner();
  /**
   * <pre>
   * signer address
   * </pre>
   *
   * <code>string signer = 3 [json_name = "signer"];</code>
   * @return The bytes for signer.
   */
  com.google.protobuf.ByteString
      getSignerBytes();
}