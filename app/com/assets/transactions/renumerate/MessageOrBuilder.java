// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: modules/assets/internal/transactions/renumerate/message.v1.proto

package com.assets.transactions.renumerate;

public interface MessageOrBuilder extends
    // @@protoc_insertion_point(interface_extends:assets.transactions.renumerate.Message)
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
   * <code>.ids.AssetID asset_i_d = 3 [json_name = "assetID"];</code>
   * @return Whether the assetID field is set.
   */
  boolean hasAssetID();
  /**
   * <code>.ids.AssetID asset_i_d = 3 [json_name = "assetID"];</code>
   * @return The assetID.
   */
  com.ids.AssetID getAssetID();
  /**
   * <code>.ids.AssetID asset_i_d = 3 [json_name = "assetID"];</code>
   */
  com.ids.AssetIDOrBuilder getAssetIDOrBuilder();
}