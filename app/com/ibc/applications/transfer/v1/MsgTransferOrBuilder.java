// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: ibc/applications/transfer/v1/tx.proto

package com.ibc.applications.transfer.v1;

public interface MsgTransferOrBuilder extends
    // @@protoc_insertion_point(interface_extends:ibc.applications.transfer.v1.MsgTransfer)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <pre>
   * the port on which the packet will be sent
   * </pre>
   *
   * <code>string source_port = 1 [json_name = "sourcePort", (.gogoproto.moretags) = "yaml:&#92;"source_port&#92;""];</code>
   * @return The sourcePort.
   */
  java.lang.String getSourcePort();
  /**
   * <pre>
   * the port on which the packet will be sent
   * </pre>
   *
   * <code>string source_port = 1 [json_name = "sourcePort", (.gogoproto.moretags) = "yaml:&#92;"source_port&#92;""];</code>
   * @return The bytes for sourcePort.
   */
  com.google.protobuf.ByteString
      getSourcePortBytes();

  /**
   * <pre>
   * the channel by which the packet will be sent
   * </pre>
   *
   * <code>string source_channel = 2 [json_name = "sourceChannel", (.gogoproto.moretags) = "yaml:&#92;"source_channel&#92;""];</code>
   * @return The sourceChannel.
   */
  java.lang.String getSourceChannel();
  /**
   * <pre>
   * the channel by which the packet will be sent
   * </pre>
   *
   * <code>string source_channel = 2 [json_name = "sourceChannel", (.gogoproto.moretags) = "yaml:&#92;"source_channel&#92;""];</code>
   * @return The bytes for sourceChannel.
   */
  com.google.protobuf.ByteString
      getSourceChannelBytes();

  /**
   * <pre>
   * the tokens to be transferred
   * </pre>
   *
   * <code>.cosmos.base.v1beta1.Coin token = 3 [json_name = "token", (.gogoproto.nullable) = false];</code>
   * @return Whether the token field is set.
   */
  boolean hasToken();
  /**
   * <pre>
   * the tokens to be transferred
   * </pre>
   *
   * <code>.cosmos.base.v1beta1.Coin token = 3 [json_name = "token", (.gogoproto.nullable) = false];</code>
   * @return The token.
   */
  com.cosmos.base.v1beta1.Coin getToken();
  /**
   * <pre>
   * the tokens to be transferred
   * </pre>
   *
   * <code>.cosmos.base.v1beta1.Coin token = 3 [json_name = "token", (.gogoproto.nullable) = false];</code>
   */
  com.cosmos.base.v1beta1.CoinOrBuilder getTokenOrBuilder();

  /**
   * <pre>
   * the sender address
   * </pre>
   *
   * <code>string sender = 4 [json_name = "sender"];</code>
   * @return The sender.
   */
  java.lang.String getSender();
  /**
   * <pre>
   * the sender address
   * </pre>
   *
   * <code>string sender = 4 [json_name = "sender"];</code>
   * @return The bytes for sender.
   */
  com.google.protobuf.ByteString
      getSenderBytes();

  /**
   * <pre>
   * the recipient address on the destination chain
   * </pre>
   *
   * <code>string receiver = 5 [json_name = "receiver"];</code>
   * @return The receiver.
   */
  java.lang.String getReceiver();
  /**
   * <pre>
   * the recipient address on the destination chain
   * </pre>
   *
   * <code>string receiver = 5 [json_name = "receiver"];</code>
   * @return The bytes for receiver.
   */
  com.google.protobuf.ByteString
      getReceiverBytes();

  /**
   * <pre>
   * Timeout height relative to the current block height.
   * The timeout is disabled when set to 0.
   * </pre>
   *
   * <code>.ibc.core.client.v1.Height timeout_height = 6 [json_name = "timeoutHeight", (.gogoproto.nullable) = false, (.gogoproto.moretags) = "yaml:&#92;"timeout_height&#92;""];</code>
   * @return Whether the timeoutHeight field is set.
   */
  boolean hasTimeoutHeight();
  /**
   * <pre>
   * Timeout height relative to the current block height.
   * The timeout is disabled when set to 0.
   * </pre>
   *
   * <code>.ibc.core.client.v1.Height timeout_height = 6 [json_name = "timeoutHeight", (.gogoproto.nullable) = false, (.gogoproto.moretags) = "yaml:&#92;"timeout_height&#92;""];</code>
   * @return The timeoutHeight.
   */
  com.ibc.core.client.v1.Height getTimeoutHeight();
  /**
   * <pre>
   * Timeout height relative to the current block height.
   * The timeout is disabled when set to 0.
   * </pre>
   *
   * <code>.ibc.core.client.v1.Height timeout_height = 6 [json_name = "timeoutHeight", (.gogoproto.nullable) = false, (.gogoproto.moretags) = "yaml:&#92;"timeout_height&#92;""];</code>
   */
  com.ibc.core.client.v1.HeightOrBuilder getTimeoutHeightOrBuilder();

  /**
   * <pre>
   * Timeout timestamp in absolute nanoseconds since unix epoch.
   * The timeout is disabled when set to 0.
   * </pre>
   *
   * <code>uint64 timeout_timestamp = 7 [json_name = "timeoutTimestamp", (.gogoproto.moretags) = "yaml:&#92;"timeout_timestamp&#92;""];</code>
   * @return The timeoutTimestamp.
   */
  long getTimeoutTimestamp();
}
