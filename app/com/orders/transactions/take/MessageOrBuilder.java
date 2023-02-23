// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: modules/orders/internal/transactions/take/message.v1.proto

package com.orders.transactions.take;

public interface MessageOrBuilder extends
    // @@protoc_insertion_point(interface_extends:orders.transactions.take.Message)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <code>string from = 1 [json_name = "from"];</code>
   * @return The from.
   */
  java.lang.String getFrom();
  /**
   * <code>string from = 1 [json_name = "from"];</code>
   * @return The bytes for from.
   */
  com.google.protobuf.ByteString
      getFromBytes();

  /**
   * <code>.ids.IdentityID from_i_d = 2 [json_name = "fromID"];</code>
   * @return Whether the fromID field is set.
   */
  boolean hasFromID();
  /**
   * <code>.ids.IdentityID from_i_d = 2 [json_name = "fromID"];</code>
   * @return The fromID.
   */
  com.ids.IdentityID getFromID();
  /**
   * <code>.ids.IdentityID from_i_d = 2 [json_name = "fromID"];</code>
   */
  com.ids.IdentityIDOrBuilder getFromIDOrBuilder();

  /**
   * <code>string taker_ownable_split = 3 [json_name = "takerOwnableSplit", (.gogoproto.nullable) = false, (.gogoproto.customtype) = "github.com/cosmos/cosmos-sdk/types.Dec"];</code>
   * @return The takerOwnableSplit.
   */
  java.lang.String getTakerOwnableSplit();
  /**
   * <code>string taker_ownable_split = 3 [json_name = "takerOwnableSplit", (.gogoproto.nullable) = false, (.gogoproto.customtype) = "github.com/cosmos/cosmos-sdk/types.Dec"];</code>
   * @return The bytes for takerOwnableSplit.
   */
  com.google.protobuf.ByteString
      getTakerOwnableSplitBytes();

  /**
   * <code>.ids.OrderID order_i_d = 4 [json_name = "orderID"];</code>
   * @return Whether the orderID field is set.
   */
  boolean hasOrderID();
  /**
   * <code>.ids.OrderID order_i_d = 4 [json_name = "orderID"];</code>
   * @return The orderID.
   */
  com.ids.OrderID getOrderID();
  /**
   * <code>.ids.OrderID order_i_d = 4 [json_name = "orderID"];</code>
   */
  com.ids.OrderIDOrBuilder getOrderIDOrBuilder();
}