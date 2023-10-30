// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: cosmos/base/reflection/v1beta1/reflection.proto

package com.cosmos.base.reflection.v1beta1;

public interface ListAllInterfacesResponseOrBuilder extends
    // @@protoc_insertion_point(interface_extends:cosmos.base.reflection.v1beta1.ListAllInterfacesResponse)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <pre>
   * interface_names is an array of all the registered interfaces.
   * </pre>
   *
   * <code>repeated string interface_names = 1 [json_name = "interfaceNames"];</code>
   * @return A list containing the interfaceNames.
   */
  java.util.List<java.lang.String>
      getInterfaceNamesList();
  /**
   * <pre>
   * interface_names is an array of all the registered interfaces.
   * </pre>
   *
   * <code>repeated string interface_names = 1 [json_name = "interfaceNames"];</code>
   * @return The count of interfaceNames.
   */
  int getInterfaceNamesCount();
  /**
   * <pre>
   * interface_names is an array of all the registered interfaces.
   * </pre>
   *
   * <code>repeated string interface_names = 1 [json_name = "interfaceNames"];</code>
   * @param index The index of the element to return.
   * @return The interfaceNames at the given index.
   */
  java.lang.String getInterfaceNames(int index);
  /**
   * <pre>
   * interface_names is an array of all the registered interfaces.
   * </pre>
   *
   * <code>repeated string interface_names = 1 [json_name = "interfaceNames"];</code>
   * @param index The index of the value to return.
   * @return The bytes of the interfaceNames at the given index.
   */
  com.google.protobuf.ByteString
      getInterfaceNamesBytes(int index);
}