// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: schema/parameters/base/parameterList.v1.proto

package com.parameters;

public interface ParameterListOrBuilder extends
    // @@protoc_insertion_point(interface_extends:parameters.ParameterList)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <code>repeated .parameters.Parameter parameters = 1 [json_name = "parameters"];</code>
   */
  java.util.List<com.parameters.Parameter> 
      getParametersList();
  /**
   * <code>repeated .parameters.Parameter parameters = 1 [json_name = "parameters"];</code>
   */
  com.parameters.Parameter getParameters(int index);
  /**
   * <code>repeated .parameters.Parameter parameters = 1 [json_name = "parameters"];</code>
   */
  int getParametersCount();
  /**
   * <code>repeated .parameters.Parameter parameters = 1 [json_name = "parameters"];</code>
   */
  java.util.List<? extends com.parameters.ParameterOrBuilder> 
      getParametersOrBuilderList();
  /**
   * <code>repeated .parameters.Parameter parameters = 1 [json_name = "parameters"];</code>
   */
  com.parameters.ParameterOrBuilder getParametersOrBuilder(
      int index);
}
