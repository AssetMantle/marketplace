// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: modules/orders/internal/transactions/modify/transactionResponse.v1.proto

package com.orders.transactions.modify;

public interface TransactionResponseOrBuilder extends
    // @@protoc_insertion_point(interface_extends:orders.transactions.modify.TransactionResponse)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <code>bool success = 1 [json_name = "success"];</code>
   * @return The success.
   */
  boolean getSuccess();

  /**
   * <pre>
   * TODO define error object
   * </pre>
   *
   * <code>string error = 2 [json_name = "error"];</code>
   * @return The error.
   */
  java.lang.String getError();
  /**
   * <pre>
   * TODO define error object
   * </pre>
   *
   * <code>string error = 2 [json_name = "error"];</code>
   * @return The bytes for error.
   */
  com.google.protobuf.ByteString
      getErrorBytes();
}
