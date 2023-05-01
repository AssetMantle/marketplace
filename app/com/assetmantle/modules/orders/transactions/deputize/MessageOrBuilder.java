// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: orders/transactions/deputize/message.proto

package com.assetmantle.modules.orders.transactions.deputize;

public interface MessageOrBuilder extends
    // @@protoc_insertion_point(interface_extends:assetmantle.modules.orders.transactions.deputize.Message)
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
   * <code>.assetmantle.schema.ids.base.IdentityID from_i_d = 2 [json_name = "fromID"];</code>
   * @return Whether the fromID field is set.
   */
  boolean hasFromID();
  /**
   * <code>.assetmantle.schema.ids.base.IdentityID from_i_d = 2 [json_name = "fromID"];</code>
   * @return The fromID.
   */
  com.assetmantle.schema.ids.base.IdentityID getFromID();
  /**
   * <code>.assetmantle.schema.ids.base.IdentityID from_i_d = 2 [json_name = "fromID"];</code>
   */
  com.assetmantle.schema.ids.base.IdentityIDOrBuilder getFromIDOrBuilder();

  /**
   * <code>.assetmantle.schema.ids.base.IdentityID to_i_d = 3 [json_name = "toID"];</code>
   * @return Whether the toID field is set.
   */
  boolean hasToID();
  /**
   * <code>.assetmantle.schema.ids.base.IdentityID to_i_d = 3 [json_name = "toID"];</code>
   * @return The toID.
   */
  com.assetmantle.schema.ids.base.IdentityID getToID();
  /**
   * <code>.assetmantle.schema.ids.base.IdentityID to_i_d = 3 [json_name = "toID"];</code>
   */
  com.assetmantle.schema.ids.base.IdentityIDOrBuilder getToIDOrBuilder();

  /**
   * <code>.assetmantle.schema.ids.base.ClassificationID classification_i_d = 4 [json_name = "classificationID"];</code>
   * @return Whether the classificationID field is set.
   */
  boolean hasClassificationID();
  /**
   * <code>.assetmantle.schema.ids.base.ClassificationID classification_i_d = 4 [json_name = "classificationID"];</code>
   * @return The classificationID.
   */
  com.assetmantle.schema.ids.base.ClassificationID getClassificationID();
  /**
   * <code>.assetmantle.schema.ids.base.ClassificationID classification_i_d = 4 [json_name = "classificationID"];</code>
   */
  com.assetmantle.schema.ids.base.ClassificationIDOrBuilder getClassificationIDOrBuilder();

  /**
   * <code>.assetmantle.schema.lists.base.PropertyList maintained_properties = 5 [json_name = "maintainedProperties"];</code>
   * @return Whether the maintainedProperties field is set.
   */
  boolean hasMaintainedProperties();
  /**
   * <code>.assetmantle.schema.lists.base.PropertyList maintained_properties = 5 [json_name = "maintainedProperties"];</code>
   * @return The maintainedProperties.
   */
  com.assetmantle.schema.lists.base.PropertyList getMaintainedProperties();
  /**
   * <code>.assetmantle.schema.lists.base.PropertyList maintained_properties = 5 [json_name = "maintainedProperties"];</code>
   */
  com.assetmantle.schema.lists.base.PropertyListOrBuilder getMaintainedPropertiesOrBuilder();

  /**
   * <code>bool can_mint_asset = 6 [json_name = "canMintAsset"];</code>
   * @return The canMintAsset.
   */
  boolean getCanMintAsset();

  /**
   * <code>bool can_burn_asset = 7 [json_name = "canBurnAsset"];</code>
   * @return The canBurnAsset.
   */
  boolean getCanBurnAsset();

  /**
   * <code>bool can_renumerate_asset = 8 [json_name = "canRenumerateAsset"];</code>
   * @return The canRenumerateAsset.
   */
  boolean getCanRenumerateAsset();

  /**
   * <code>bool can_add_maintainer = 9 [json_name = "canAddMaintainer"];</code>
   * @return The canAddMaintainer.
   */
  boolean getCanAddMaintainer();

  /**
   * <code>bool can_remove_maintainer = 10 [json_name = "canRemoveMaintainer"];</code>
   * @return The canRemoveMaintainer.
   */
  boolean getCanRemoveMaintainer();

  /**
   * <code>bool can_mutate_maintainer = 11 [json_name = "canMutateMaintainer"];</code>
   * @return The canMutateMaintainer.
   */
  boolean getCanMutateMaintainer();
}
