// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: ibc/core/client/v1/tx.proto

package com.ibc.core.client.v1;

public interface MsgUpdateClientOrBuilder extends
    // @@protoc_insertion_point(interface_extends:ibc.core.client.v1.MsgUpdateClient)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <pre>
   * client unique identifier
   * </pre>
   *
   * <code>string client_id = 1 [json_name = "clientId", (.gogoproto.moretags) = "yaml:&#92;"client_id&#92;""];</code>
   * @return The clientId.
   */
  java.lang.String getClientId();
  /**
   * <pre>
   * client unique identifier
   * </pre>
   *
   * <code>string client_id = 1 [json_name = "clientId", (.gogoproto.moretags) = "yaml:&#92;"client_id&#92;""];</code>
   * @return The bytes for clientId.
   */
  com.google.protobuf.ByteString
      getClientIdBytes();

  /**
   * <pre>
   * header to update the light client
   * </pre>
   *
   * <code>.google.protobuf.Any header = 2 [json_name = "header"];</code>
   * @return Whether the header field is set.
   */
  boolean hasHeader();
  /**
   * <pre>
   * header to update the light client
   * </pre>
   *
   * <code>.google.protobuf.Any header = 2 [json_name = "header"];</code>
   * @return The header.
   */
  com.google.protobuf.Any getHeader();
  /**
   * <pre>
   * header to update the light client
   * </pre>
   *
   * <code>.google.protobuf.Any header = 2 [json_name = "header"];</code>
   */
  com.google.protobuf.AnyOrBuilder getHeaderOrBuilder();

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
