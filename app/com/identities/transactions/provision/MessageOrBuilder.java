// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: modules/identities/internal/transactions/provision/message.v1.proto

package com.identities.transactions.provision;

public interface MessageOrBuilder extends
    // @@protoc_insertion_point(interface_extends:identities.transactions.provision.Message)
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
   * <code>string to = 2 [json_name = "to"];</code>
   * @return The to.
   */
  java.lang.String getTo();
  /**
   * <code>string to = 2 [json_name = "to"];</code>
   * @return The bytes for to.
   */
  com.google.protobuf.ByteString
      getToBytes();

  /**
   * <code>.ids.IdentityID identity_i_d = 3 [json_name = "identityID"];</code>
   * @return Whether the identityID field is set.
   */
  boolean hasIdentityID();
  /**
   * <code>.ids.IdentityID identity_i_d = 3 [json_name = "identityID"];</code>
   * @return The identityID.
   */
  com.ids.IdentityID getIdentityID();
  /**
   * <code>.ids.IdentityID identity_i_d = 3 [json_name = "identityID"];</code>
   */
  com.ids.IdentityIDOrBuilder getIdentityIDOrBuilder();
}