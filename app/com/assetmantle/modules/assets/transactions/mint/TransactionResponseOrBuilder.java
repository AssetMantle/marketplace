// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: assets/transactions/mint/transaction_response.proto

package com.assetmantle.modules.assets.transactions.mint;

public interface TransactionResponseOrBuilder extends
    // @@protoc_insertion_point(interface_extends:assetmantle.modules.assets.transactions.mint.TransactionResponse)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <code>.assetmantle.schema.ids.base.AssetID asset_i_d = 1 [json_name = "assetID"];</code>
   * @return Whether the assetID field is set.
   */
  boolean hasAssetID();
  /**
   * <code>.assetmantle.schema.ids.base.AssetID asset_i_d = 1 [json_name = "assetID"];</code>
   * @return The assetID.
   */
  com.assetmantle.schema.ids.base.AssetID getAssetID();
  /**
   * <code>.assetmantle.schema.ids.base.AssetID asset_i_d = 1 [json_name = "assetID"];</code>
   */
  com.assetmantle.schema.ids.base.AssetIDOrBuilder getAssetIDOrBuilder();
}