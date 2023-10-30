// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: tendermint/types/block.proto

package com.tendermint.types;

public interface BlockOrBuilder extends
    // @@protoc_insertion_point(interface_extends:tendermint.types.Block)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <code>.tendermint.types.Header header = 1 [json_name = "header", (.gogoproto.nullable) = false];</code>
   * @return Whether the header field is set.
   */
  boolean hasHeader();
  /**
   * <code>.tendermint.types.Header header = 1 [json_name = "header", (.gogoproto.nullable) = false];</code>
   * @return The header.
   */
  com.tendermint.types.Header getHeader();
  /**
   * <code>.tendermint.types.Header header = 1 [json_name = "header", (.gogoproto.nullable) = false];</code>
   */
  com.tendermint.types.HeaderOrBuilder getHeaderOrBuilder();

  /**
   * <code>.tendermint.types.Data data = 2 [json_name = "data", (.gogoproto.nullable) = false];</code>
   * @return Whether the data field is set.
   */
  boolean hasData();
  /**
   * <code>.tendermint.types.Data data = 2 [json_name = "data", (.gogoproto.nullable) = false];</code>
   * @return The data.
   */
  com.tendermint.types.Data getData();
  /**
   * <code>.tendermint.types.Data data = 2 [json_name = "data", (.gogoproto.nullable) = false];</code>
   */
  com.tendermint.types.DataOrBuilder getDataOrBuilder();

  /**
   * <code>.tendermint.types.EvidenceList evidence = 3 [json_name = "evidence", (.gogoproto.nullable) = false];</code>
   * @return Whether the evidence field is set.
   */
  boolean hasEvidence();
  /**
   * <code>.tendermint.types.EvidenceList evidence = 3 [json_name = "evidence", (.gogoproto.nullable) = false];</code>
   * @return The evidence.
   */
  com.tendermint.types.EvidenceList getEvidence();
  /**
   * <code>.tendermint.types.EvidenceList evidence = 3 [json_name = "evidence", (.gogoproto.nullable) = false];</code>
   */
  com.tendermint.types.EvidenceListOrBuilder getEvidenceOrBuilder();

  /**
   * <code>.tendermint.types.Commit last_commit = 4 [json_name = "lastCommit"];</code>
   * @return Whether the lastCommit field is set.
   */
  boolean hasLastCommit();
  /**
   * <code>.tendermint.types.Commit last_commit = 4 [json_name = "lastCommit"];</code>
   * @return The lastCommit.
   */
  com.tendermint.types.Commit getLastCommit();
  /**
   * <code>.tendermint.types.Commit last_commit = 4 [json_name = "lastCommit"];</code>
   */
  com.tendermint.types.CommitOrBuilder getLastCommitOrBuilder();
}