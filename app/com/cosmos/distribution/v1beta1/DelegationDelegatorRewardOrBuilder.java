// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: cosmos/distribution/v1beta1/distribution.proto

package com.cosmos.distribution.v1beta1;

public interface DelegationDelegatorRewardOrBuilder extends
    // @@protoc_insertion_point(interface_extends:cosmos.distribution.v1beta1.DelegationDelegatorReward)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <code>string validator_address = 1 [json_name = "validatorAddress", (.gogoproto.moretags) = "yaml:&#92;"validator_address&#92;""];</code>
   * @return The validatorAddress.
   */
  java.lang.String getValidatorAddress();
  /**
   * <code>string validator_address = 1 [json_name = "validatorAddress", (.gogoproto.moretags) = "yaml:&#92;"validator_address&#92;""];</code>
   * @return The bytes for validatorAddress.
   */
  com.google.protobuf.ByteString
      getValidatorAddressBytes();

  /**
   * <code>repeated .cosmos.base.v1beta1.DecCoin reward = 2 [json_name = "reward", (.gogoproto.nullable) = false, (.gogoproto.castrepeated) = "github.com/cosmos/cosmos-sdk/types.DecCoins"];</code>
   */
  java.util.List<com.cosmos.base.v1beta1.DecCoin> 
      getRewardList();
  /**
   * <code>repeated .cosmos.base.v1beta1.DecCoin reward = 2 [json_name = "reward", (.gogoproto.nullable) = false, (.gogoproto.castrepeated) = "github.com/cosmos/cosmos-sdk/types.DecCoins"];</code>
   */
  com.cosmos.base.v1beta1.DecCoin getReward(int index);
  /**
   * <code>repeated .cosmos.base.v1beta1.DecCoin reward = 2 [json_name = "reward", (.gogoproto.nullable) = false, (.gogoproto.castrepeated) = "github.com/cosmos/cosmos-sdk/types.DecCoins"];</code>
   */
  int getRewardCount();
  /**
   * <code>repeated .cosmos.base.v1beta1.DecCoin reward = 2 [json_name = "reward", (.gogoproto.nullable) = false, (.gogoproto.castrepeated) = "github.com/cosmos/cosmos-sdk/types.DecCoins"];</code>
   */
  java.util.List<? extends com.cosmos.base.v1beta1.DecCoinOrBuilder> 
      getRewardOrBuilderList();
  /**
   * <code>repeated .cosmos.base.v1beta1.DecCoin reward = 2 [json_name = "reward", (.gogoproto.nullable) = false, (.gogoproto.castrepeated) = "github.com/cosmos/cosmos-sdk/types.DecCoins"];</code>
   */
  com.cosmos.base.v1beta1.DecCoinOrBuilder getRewardOrBuilder(
      int index);
}