// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: schema/properties/base/mesaProperty.v1.proto

package com.properties;

public interface MesaPropertyOrBuilder extends
    // @@protoc_insertion_point(interface_extends:properties.MesaProperty)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <code>.ids.PropertyID id = 1 [json_name = "id"];</code>
   * @return Whether the id field is set.
   */
  boolean hasId();
  /**
   * <code>.ids.PropertyID id = 1 [json_name = "id"];</code>
   * @return The id.
   */
  com.ids.PropertyID getId();
  /**
   * <code>.ids.PropertyID id = 1 [json_name = "id"];</code>
   */
  com.ids.PropertyIDOrBuilder getIdOrBuilder();

  /**
   * <code>.ids.DataID data_i_d = 2 [json_name = "dataID"];</code>
   * @return Whether the dataID field is set.
   */
  boolean hasDataID();
  /**
   * <code>.ids.DataID data_i_d = 2 [json_name = "dataID"];</code>
   * @return The dataID.
   */
  com.ids.DataID getDataID();
  /**
   * <code>.ids.DataID data_i_d = 2 [json_name = "dataID"];</code>
   */
  com.ids.DataIDOrBuilder getDataIDOrBuilder();
}
