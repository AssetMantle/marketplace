// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: identities/transactions/name/message.proto

package com.assetmantle.modules.identities.transactions.name;

public interface MessageOrBuilder extends
    // @@protoc_insertion_point(interface_extends:assetmantle.modules.identities.transactions.name.Message)
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
   * <code>.assetmantle.schema.ids.base.StringID name = 2 [json_name = "name"];</code>
   * @return Whether the name field is set.
   */
  boolean hasName();
  /**
   * <code>.assetmantle.schema.ids.base.StringID name = 2 [json_name = "name"];</code>
   * @return The name.
   */
  com.assetmantle.schema.ids.base.StringID getName();
  /**
   * <code>.assetmantle.schema.ids.base.StringID name = 2 [json_name = "name"];</code>
   */
  com.assetmantle.schema.ids.base.StringIDOrBuilder getNameOrBuilder();
}