// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: tendermint/abci/types.proto

package com.tendermint.abci;

public interface RequestLoadSnapshotChunkOrBuilder extends
    // @@protoc_insertion_point(interface_extends:tendermint.abci.RequestLoadSnapshotChunk)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <code>uint64 height = 1 [json_name = "height"];</code>
   * @return The height.
   */
  long getHeight();

  /**
   * <code>uint32 format = 2 [json_name = "format"];</code>
   * @return The format.
   */
  int getFormat();

  /**
   * <code>uint32 chunk = 3 [json_name = "chunk"];</code>
   * @return The chunk.
   */
  int getChunk();
}
