// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: ids/base/any_ownable_id.proto

package com.assetmantle.schema.ids.base;

public interface AnyOwnableIDOrBuilder extends
    // @@protoc_insertion_point(interface_extends:assetmantle.schema.ids.base.AnyOwnableID)
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

  /**
   * <code>.assetmantle.schema.ids.base.CoinID coin_i_d = 2 [json_name = "coinID"];</code>
   * @return Whether the coinID field is set.
   */
  boolean hasCoinID();
  /**
   * <code>.assetmantle.schema.ids.base.CoinID coin_i_d = 2 [json_name = "coinID"];</code>
   * @return The coinID.
   */
  com.assetmantle.schema.ids.base.CoinID getCoinID();
  /**
   * <code>.assetmantle.schema.ids.base.CoinID coin_i_d = 2 [json_name = "coinID"];</code>
   */
  com.assetmantle.schema.ids.base.CoinIDOrBuilder getCoinIDOrBuilder();

  com.assetmantle.schema.ids.base.AnyOwnableID.ImplCase getImplCase();
}
