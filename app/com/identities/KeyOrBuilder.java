// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: modules/identities/internal/key/key.v1.proto

package com.identities;

public interface KeyOrBuilder extends
    // @@protoc_insertion_point(interface_extends:identities.Key)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <code>.ids.IdentityID identity_i_d = 1 [json_name = "identityID"];</code>
   * @return Whether the identityID field is set.
   */
  boolean hasIdentityID();
  /**
   * <code>.ids.IdentityID identity_i_d = 1 [json_name = "identityID"];</code>
   * @return The identityID.
   */
  com.ids.IdentityID getIdentityID();
  /**
   * <code>.ids.IdentityID identity_i_d = 1 [json_name = "identityID"];</code>
   */
  com.ids.IdentityIDOrBuilder getIdentityIDOrBuilder();
}
