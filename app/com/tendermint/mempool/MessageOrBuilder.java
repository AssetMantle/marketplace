// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: tendermint/mempool/types.proto

package com.tendermint.mempool;

public interface MessageOrBuilder extends
    // @@protoc_insertion_point(interface_extends:tendermint.mempool.Message)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <code>.tendermint.mempool.Txs txs = 1 [json_name = "txs"];</code>
   * @return Whether the txs field is set.
   */
  boolean hasTxs();
  /**
   * <code>.tendermint.mempool.Txs txs = 1 [json_name = "txs"];</code>
   * @return The txs.
   */
  com.tendermint.mempool.Txs getTxs();
  /**
   * <code>.tendermint.mempool.Txs txs = 1 [json_name = "txs"];</code>
   */
  com.tendermint.mempool.TxsOrBuilder getTxsOrBuilder();

  com.tendermint.mempool.Message.SumCase getSumCase();
}
