// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: tendermint/p2p/types.proto

package com.tendermint.p2p;

public interface NetAddressOrBuilder extends
    // @@protoc_insertion_point(interface_extends:tendermint.p2p.NetAddress)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <code>string id = 1 [json_name = "id", (.gogoproto.customname) = "ID"];</code>
   * @return The id.
   */
  java.lang.String getId();
  /**
   * <code>string id = 1 [json_name = "id", (.gogoproto.customname) = "ID"];</code>
   * @return The bytes for id.
   */
  com.google.protobuf.ByteString
      getIdBytes();

  /**
   * <code>string ip = 2 [json_name = "ip", (.gogoproto.customname) = "IP"];</code>
   * @return The ip.
   */
  java.lang.String getIp();
  /**
   * <code>string ip = 2 [json_name = "ip", (.gogoproto.customname) = "IP"];</code>
   * @return The bytes for ip.
   */
  com.google.protobuf.ByteString
      getIpBytes();

  /**
   * <code>uint32 port = 3 [json_name = "port"];</code>
   * @return The port.
   */
  int getPort();
}