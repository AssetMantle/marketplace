// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: ibc/applications/fee/v1/fee.proto

package com.ibc.applications.fee.v1;

public interface PacketFeeOrBuilder extends
    // @@protoc_insertion_point(interface_extends:ibc.applications.fee.v1.PacketFee)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <pre>
   * fee encapsulates the recv, ack and timeout fees associated with an IBC packet
   * </pre>
   *
   * <code>.ibc.applications.fee.v1.Fee fee = 1 [json_name = "fee", (.gogoproto.nullable) = false];</code>
   * @return Whether the fee field is set.
   */
  boolean hasFee();
  /**
   * <pre>
   * fee encapsulates the recv, ack and timeout fees associated with an IBC packet
   * </pre>
   *
   * <code>.ibc.applications.fee.v1.Fee fee = 1 [json_name = "fee", (.gogoproto.nullable) = false];</code>
   * @return The fee.
   */
  com.ibc.applications.fee.v1.Fee getFee();
  /**
   * <pre>
   * fee encapsulates the recv, ack and timeout fees associated with an IBC packet
   * </pre>
   *
   * <code>.ibc.applications.fee.v1.Fee fee = 1 [json_name = "fee", (.gogoproto.nullable) = false];</code>
   */
  com.ibc.applications.fee.v1.FeeOrBuilder getFeeOrBuilder();

  /**
   * <pre>
   * the refund address for unspent fees
   * </pre>
   *
   * <code>string refund_address = 2 [json_name = "refundAddress", (.gogoproto.moretags) = "yaml:&#92;"refund_address&#92;""];</code>
   * @return The refundAddress.
   */
  java.lang.String getRefundAddress();
  /**
   * <pre>
   * the refund address for unspent fees
   * </pre>
   *
   * <code>string refund_address = 2 [json_name = "refundAddress", (.gogoproto.moretags) = "yaml:&#92;"refund_address&#92;""];</code>
   * @return The bytes for refundAddress.
   */
  com.google.protobuf.ByteString
      getRefundAddressBytes();

  /**
   * <pre>
   * optional list of relayers permitted to receive fees
   * </pre>
   *
   * <code>repeated string relayers = 3 [json_name = "relayers"];</code>
   * @return A list containing the relayers.
   */
  java.util.List<java.lang.String>
      getRelayersList();
  /**
   * <pre>
   * optional list of relayers permitted to receive fees
   * </pre>
   *
   * <code>repeated string relayers = 3 [json_name = "relayers"];</code>
   * @return The count of relayers.
   */
  int getRelayersCount();
  /**
   * <pre>
   * optional list of relayers permitted to receive fees
   * </pre>
   *
   * <code>repeated string relayers = 3 [json_name = "relayers"];</code>
   * @param index The index of the element to return.
   * @return The relayers at the given index.
   */
  java.lang.String getRelayers(int index);
  /**
   * <pre>
   * optional list of relayers permitted to receive fees
   * </pre>
   *
   * <code>repeated string relayers = 3 [json_name = "relayers"];</code>
   * @param index The index of the value to return.
   * @return The bytes of the relayers at the given index.
   */
  com.google.protobuf.ByteString
      getRelayersBytes(int index);
}