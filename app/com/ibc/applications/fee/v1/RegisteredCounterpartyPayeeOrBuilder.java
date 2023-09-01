// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: ibc/applications/fee/v1/genesis.proto

package com.ibc.applications.fee.v1;

public interface RegisteredCounterpartyPayeeOrBuilder extends
    // @@protoc_insertion_point(interface_extends:ibc.applications.fee.v1.RegisteredCounterpartyPayee)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <pre>
   * unique channel identifier
   * </pre>
   *
   * <code>string channel_id = 1 [json_name = "channelId", (.gogoproto.moretags) = "yaml:&#92;"channel_id&#92;""];</code>
   * @return The channelId.
   */
  java.lang.String getChannelId();
  /**
   * <pre>
   * unique channel identifier
   * </pre>
   *
   * <code>string channel_id = 1 [json_name = "channelId", (.gogoproto.moretags) = "yaml:&#92;"channel_id&#92;""];</code>
   * @return The bytes for channelId.
   */
  com.google.protobuf.ByteString
      getChannelIdBytes();

  /**
   * <pre>
   * the relayer address
   * </pre>
   *
   * <code>string relayer = 2 [json_name = "relayer"];</code>
   * @return The relayer.
   */
  java.lang.String getRelayer();
  /**
   * <pre>
   * the relayer address
   * </pre>
   *
   * <code>string relayer = 2 [json_name = "relayer"];</code>
   * @return The bytes for relayer.
   */
  com.google.protobuf.ByteString
      getRelayerBytes();

  /**
   * <pre>
   * the counterparty payee address
   * </pre>
   *
   * <code>string counterparty_payee = 3 [json_name = "counterpartyPayee", (.gogoproto.moretags) = "yaml:&#92;"counterparty_payee&#92;""];</code>
   * @return The counterpartyPayee.
   */
  java.lang.String getCounterpartyPayee();
  /**
   * <pre>
   * the counterparty payee address
   * </pre>
   *
   * <code>string counterparty_payee = 3 [json_name = "counterpartyPayee", (.gogoproto.moretags) = "yaml:&#92;"counterparty_payee&#92;""];</code>
   * @return The bytes for counterpartyPayee.
   */
  com.google.protobuf.ByteString
      getCounterpartyPayeeBytes();
}
