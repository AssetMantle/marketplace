// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: cosmos/staking/v1beta1/staking.proto

package com.cosmos.staking.v1beta1;

public interface ValidatorUpdatesOrBuilder extends
    // @@protoc_insertion_point(interface_extends:cosmos.staking.v1beta1.ValidatorUpdates)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <code>repeated .tendermint.abci.ValidatorUpdate updates = 1 [json_name = "updates", (.gogoproto.nullable) = false];</code>
   */
  java.util.List<com.tendermint.abci.ValidatorUpdate> 
      getUpdatesList();
  /**
   * <code>repeated .tendermint.abci.ValidatorUpdate updates = 1 [json_name = "updates", (.gogoproto.nullable) = false];</code>
   */
  com.tendermint.abci.ValidatorUpdate getUpdates(int index);
  /**
   * <code>repeated .tendermint.abci.ValidatorUpdate updates = 1 [json_name = "updates", (.gogoproto.nullable) = false];</code>
   */
  int getUpdatesCount();
  /**
   * <code>repeated .tendermint.abci.ValidatorUpdate updates = 1 [json_name = "updates", (.gogoproto.nullable) = false];</code>
   */
  java.util.List<? extends com.tendermint.abci.ValidatorUpdateOrBuilder> 
      getUpdatesOrBuilderList();
  /**
   * <code>repeated .tendermint.abci.ValidatorUpdate updates = 1 [json_name = "updates", (.gogoproto.nullable) = false];</code>
   */
  com.tendermint.abci.ValidatorUpdateOrBuilder getUpdatesOrBuilder(
      int index);
}
