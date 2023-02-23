// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: modules/metas/internal/genesis/genesis.v1.proto

package com.metas;

/**
 * Protobuf type {@code metas.Genesis}
 */
public final class Genesis extends
    com.google.protobuf.GeneratedMessageV3 implements
    // @@protoc_insertion_point(message_implements:metas.Genesis)
    GenesisOrBuilder {
private static final long serialVersionUID = 0L;
  // Use Genesis.newBuilder() to construct.
  private Genesis(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
    super(builder);
  }
  private Genesis() {
    mappables_ = java.util.Collections.emptyList();
    parameters_ = java.util.Collections.emptyList();
  }

  @java.lang.Override
  @SuppressWarnings({"unused"})
  protected java.lang.Object newInstance(
      UnusedPrivateParameter unused) {
    return new Genesis();
  }

  public static final com.google.protobuf.Descriptors.Descriptor
      getDescriptor() {
    return com.metas.GenesisV1Proto.internal_static_metas_Genesis_descriptor;
  }

  @java.lang.Override
  protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internalGetFieldAccessorTable() {
    return com.metas.GenesisV1Proto.internal_static_metas_Genesis_fieldAccessorTable
        .ensureFieldAccessorsInitialized(
            com.metas.Genesis.class, com.metas.Genesis.Builder.class);
  }

  public static final int MAPPABLES_FIELD_NUMBER = 1;
  @SuppressWarnings("serial")
  private java.util.List<com.metas.Mappable> mappables_;
  /**
   * <code>repeated .metas.Mappable mappables = 1 [json_name = "mappables"];</code>
   */
  @java.lang.Override
  public java.util.List<com.metas.Mappable> getMappablesList() {
    return mappables_;
  }
  /**
   * <code>repeated .metas.Mappable mappables = 1 [json_name = "mappables"];</code>
   */
  @java.lang.Override
  public java.util.List<? extends com.metas.MappableOrBuilder> 
      getMappablesOrBuilderList() {
    return mappables_;
  }
  /**
   * <code>repeated .metas.Mappable mappables = 1 [json_name = "mappables"];</code>
   */
  @java.lang.Override
  public int getMappablesCount() {
    return mappables_.size();
  }
  /**
   * <code>repeated .metas.Mappable mappables = 1 [json_name = "mappables"];</code>
   */
  @java.lang.Override
  public com.metas.Mappable getMappables(int index) {
    return mappables_.get(index);
  }
  /**
   * <code>repeated .metas.Mappable mappables = 1 [json_name = "mappables"];</code>
   */
  @java.lang.Override
  public com.metas.MappableOrBuilder getMappablesOrBuilder(
      int index) {
    return mappables_.get(index);
  }

  public static final int PARAMETERS_FIELD_NUMBER = 2;
  @SuppressWarnings("serial")
  private java.util.List<com.parameters.Parameter> parameters_;
  /**
   * <code>repeated .parameters.Parameter parameters = 2 [json_name = "parameters"];</code>
   */
  @java.lang.Override
  public java.util.List<com.parameters.Parameter> getParametersList() {
    return parameters_;
  }
  /**
   * <code>repeated .parameters.Parameter parameters = 2 [json_name = "parameters"];</code>
   */
  @java.lang.Override
  public java.util.List<? extends com.parameters.ParameterOrBuilder> 
      getParametersOrBuilderList() {
    return parameters_;
  }
  /**
   * <code>repeated .parameters.Parameter parameters = 2 [json_name = "parameters"];</code>
   */
  @java.lang.Override
  public int getParametersCount() {
    return parameters_.size();
  }
  /**
   * <code>repeated .parameters.Parameter parameters = 2 [json_name = "parameters"];</code>
   */
  @java.lang.Override
  public com.parameters.Parameter getParameters(int index) {
    return parameters_.get(index);
  }
  /**
   * <code>repeated .parameters.Parameter parameters = 2 [json_name = "parameters"];</code>
   */
  @java.lang.Override
  public com.parameters.ParameterOrBuilder getParametersOrBuilder(
      int index) {
    return parameters_.get(index);
  }

  private byte memoizedIsInitialized = -1;
  @java.lang.Override
  public final boolean isInitialized() {
    byte isInitialized = memoizedIsInitialized;
    if (isInitialized == 1) return true;
    if (isInitialized == 0) return false;

    memoizedIsInitialized = 1;
    return true;
  }

  @java.lang.Override
  public void writeTo(com.google.protobuf.CodedOutputStream output)
                      throws java.io.IOException {
    for (int i = 0; i < mappables_.size(); i++) {
      output.writeMessage(1, mappables_.get(i));
    }
    for (int i = 0; i < parameters_.size(); i++) {
      output.writeMessage(2, parameters_.get(i));
    }
    getUnknownFields().writeTo(output);
  }

  @java.lang.Override
  public int getSerializedSize() {
    int size = memoizedSize;
    if (size != -1) return size;

    size = 0;
    for (int i = 0; i < mappables_.size(); i++) {
      size += com.google.protobuf.CodedOutputStream
        .computeMessageSize(1, mappables_.get(i));
    }
    for (int i = 0; i < parameters_.size(); i++) {
      size += com.google.protobuf.CodedOutputStream
        .computeMessageSize(2, parameters_.get(i));
    }
    size += getUnknownFields().getSerializedSize();
    memoizedSize = size;
    return size;
  }

  @java.lang.Override
  public boolean equals(final java.lang.Object obj) {
    if (obj == this) {
     return true;
    }
    if (!(obj instanceof com.metas.Genesis)) {
      return super.equals(obj);
    }
    com.metas.Genesis other = (com.metas.Genesis) obj;

    if (!getMappablesList()
        .equals(other.getMappablesList())) return false;
    if (!getParametersList()
        .equals(other.getParametersList())) return false;
    if (!getUnknownFields().equals(other.getUnknownFields())) return false;
    return true;
  }

  @java.lang.Override
  public int hashCode() {
    if (memoizedHashCode != 0) {
      return memoizedHashCode;
    }
    int hash = 41;
    hash = (19 * hash) + getDescriptor().hashCode();
    if (getMappablesCount() > 0) {
      hash = (37 * hash) + MAPPABLES_FIELD_NUMBER;
      hash = (53 * hash) + getMappablesList().hashCode();
    }
    if (getParametersCount() > 0) {
      hash = (37 * hash) + PARAMETERS_FIELD_NUMBER;
      hash = (53 * hash) + getParametersList().hashCode();
    }
    hash = (29 * hash) + getUnknownFields().hashCode();
    memoizedHashCode = hash;
    return hash;
  }

  public static com.metas.Genesis parseFrom(
      java.nio.ByteBuffer data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static com.metas.Genesis parseFrom(
      java.nio.ByteBuffer data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static com.metas.Genesis parseFrom(
      com.google.protobuf.ByteString data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static com.metas.Genesis parseFrom(
      com.google.protobuf.ByteString data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static com.metas.Genesis parseFrom(byte[] data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static com.metas.Genesis parseFrom(
      byte[] data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static com.metas.Genesis parseFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static com.metas.Genesis parseFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input, extensionRegistry);
  }
  public static com.metas.Genesis parseDelimitedFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input);
  }
  public static com.metas.Genesis parseDelimitedFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
  }
  public static com.metas.Genesis parseFrom(
      com.google.protobuf.CodedInputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static com.metas.Genesis parseFrom(
      com.google.protobuf.CodedInputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input, extensionRegistry);
  }

  @java.lang.Override
  public Builder newBuilderForType() { return newBuilder(); }
  public static Builder newBuilder() {
    return DEFAULT_INSTANCE.toBuilder();
  }
  public static Builder newBuilder(com.metas.Genesis prototype) {
    return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
  }
  @java.lang.Override
  public Builder toBuilder() {
    return this == DEFAULT_INSTANCE
        ? new Builder() : new Builder().mergeFrom(this);
  }

  @java.lang.Override
  protected Builder newBuilderForType(
      com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
    Builder builder = new Builder(parent);
    return builder;
  }
  /**
   * Protobuf type {@code metas.Genesis}
   */
  public static final class Builder extends
      com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
      // @@protoc_insertion_point(builder_implements:metas.Genesis)
      com.metas.GenesisOrBuilder {
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return com.metas.GenesisV1Proto.internal_static_metas_Genesis_descriptor;
    }

    @java.lang.Override
    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return com.metas.GenesisV1Proto.internal_static_metas_Genesis_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              com.metas.Genesis.class, com.metas.Genesis.Builder.class);
    }

    // Construct using com.metas.Genesis.newBuilder()
    private Builder() {

    }

    private Builder(
        com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
      super(parent);

    }
    @java.lang.Override
    public Builder clear() {
      super.clear();
      bitField0_ = 0;
      if (mappablesBuilder_ == null) {
        mappables_ = java.util.Collections.emptyList();
      } else {
        mappables_ = null;
        mappablesBuilder_.clear();
      }
      bitField0_ = (bitField0_ & ~0x00000001);
      if (parametersBuilder_ == null) {
        parameters_ = java.util.Collections.emptyList();
      } else {
        parameters_ = null;
        parametersBuilder_.clear();
      }
      bitField0_ = (bitField0_ & ~0x00000002);
      return this;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.Descriptor
        getDescriptorForType() {
      return com.metas.GenesisV1Proto.internal_static_metas_Genesis_descriptor;
    }

    @java.lang.Override
    public com.metas.Genesis getDefaultInstanceForType() {
      return com.metas.Genesis.getDefaultInstance();
    }

    @java.lang.Override
    public com.metas.Genesis build() {
      com.metas.Genesis result = buildPartial();
      if (!result.isInitialized()) {
        throw newUninitializedMessageException(result);
      }
      return result;
    }

    @java.lang.Override
    public com.metas.Genesis buildPartial() {
      com.metas.Genesis result = new com.metas.Genesis(this);
      buildPartialRepeatedFields(result);
      if (bitField0_ != 0) { buildPartial0(result); }
      onBuilt();
      return result;
    }

    private void buildPartialRepeatedFields(com.metas.Genesis result) {
      if (mappablesBuilder_ == null) {
        if (((bitField0_ & 0x00000001) != 0)) {
          mappables_ = java.util.Collections.unmodifiableList(mappables_);
          bitField0_ = (bitField0_ & ~0x00000001);
        }
        result.mappables_ = mappables_;
      } else {
        result.mappables_ = mappablesBuilder_.build();
      }
      if (parametersBuilder_ == null) {
        if (((bitField0_ & 0x00000002) != 0)) {
          parameters_ = java.util.Collections.unmodifiableList(parameters_);
          bitField0_ = (bitField0_ & ~0x00000002);
        }
        result.parameters_ = parameters_;
      } else {
        result.parameters_ = parametersBuilder_.build();
      }
    }

    private void buildPartial0(com.metas.Genesis result) {
      int from_bitField0_ = bitField0_;
    }

    @java.lang.Override
    public Builder clone() {
      return super.clone();
    }
    @java.lang.Override
    public Builder setField(
        com.google.protobuf.Descriptors.FieldDescriptor field,
        java.lang.Object value) {
      return super.setField(field, value);
    }
    @java.lang.Override
    public Builder clearField(
        com.google.protobuf.Descriptors.FieldDescriptor field) {
      return super.clearField(field);
    }
    @java.lang.Override
    public Builder clearOneof(
        com.google.protobuf.Descriptors.OneofDescriptor oneof) {
      return super.clearOneof(oneof);
    }
    @java.lang.Override
    public Builder setRepeatedField(
        com.google.protobuf.Descriptors.FieldDescriptor field,
        int index, java.lang.Object value) {
      return super.setRepeatedField(field, index, value);
    }
    @java.lang.Override
    public Builder addRepeatedField(
        com.google.protobuf.Descriptors.FieldDescriptor field,
        java.lang.Object value) {
      return super.addRepeatedField(field, value);
    }
    @java.lang.Override
    public Builder mergeFrom(com.google.protobuf.Message other) {
      if (other instanceof com.metas.Genesis) {
        return mergeFrom((com.metas.Genesis)other);
      } else {
        super.mergeFrom(other);
        return this;
      }
    }

    public Builder mergeFrom(com.metas.Genesis other) {
      if (other == com.metas.Genesis.getDefaultInstance()) return this;
      if (mappablesBuilder_ == null) {
        if (!other.mappables_.isEmpty()) {
          if (mappables_.isEmpty()) {
            mappables_ = other.mappables_;
            bitField0_ = (bitField0_ & ~0x00000001);
          } else {
            ensureMappablesIsMutable();
            mappables_.addAll(other.mappables_);
          }
          onChanged();
        }
      } else {
        if (!other.mappables_.isEmpty()) {
          if (mappablesBuilder_.isEmpty()) {
            mappablesBuilder_.dispose();
            mappablesBuilder_ = null;
            mappables_ = other.mappables_;
            bitField0_ = (bitField0_ & ~0x00000001);
            mappablesBuilder_ = 
              com.google.protobuf.GeneratedMessageV3.alwaysUseFieldBuilders ?
                 getMappablesFieldBuilder() : null;
          } else {
            mappablesBuilder_.addAllMessages(other.mappables_);
          }
        }
      }
      if (parametersBuilder_ == null) {
        if (!other.parameters_.isEmpty()) {
          if (parameters_.isEmpty()) {
            parameters_ = other.parameters_;
            bitField0_ = (bitField0_ & ~0x00000002);
          } else {
            ensureParametersIsMutable();
            parameters_.addAll(other.parameters_);
          }
          onChanged();
        }
      } else {
        if (!other.parameters_.isEmpty()) {
          if (parametersBuilder_.isEmpty()) {
            parametersBuilder_.dispose();
            parametersBuilder_ = null;
            parameters_ = other.parameters_;
            bitField0_ = (bitField0_ & ~0x00000002);
            parametersBuilder_ = 
              com.google.protobuf.GeneratedMessageV3.alwaysUseFieldBuilders ?
                 getParametersFieldBuilder() : null;
          } else {
            parametersBuilder_.addAllMessages(other.parameters_);
          }
        }
      }
      this.mergeUnknownFields(other.getUnknownFields());
      onChanged();
      return this;
    }

    @java.lang.Override
    public final boolean isInitialized() {
      return true;
    }

    @java.lang.Override
    public Builder mergeFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      if (extensionRegistry == null) {
        throw new java.lang.NullPointerException();
      }
      try {
        boolean done = false;
        while (!done) {
          int tag = input.readTag();
          switch (tag) {
            case 0:
              done = true;
              break;
            case 10: {
              com.metas.Mappable m =
                  input.readMessage(
                      com.metas.Mappable.parser(),
                      extensionRegistry);
              if (mappablesBuilder_ == null) {
                ensureMappablesIsMutable();
                mappables_.add(m);
              } else {
                mappablesBuilder_.addMessage(m);
              }
              break;
            } // case 10
            case 18: {
              com.parameters.Parameter m =
                  input.readMessage(
                      com.parameters.Parameter.parser(),
                      extensionRegistry);
              if (parametersBuilder_ == null) {
                ensureParametersIsMutable();
                parameters_.add(m);
              } else {
                parametersBuilder_.addMessage(m);
              }
              break;
            } // case 18
            default: {
              if (!super.parseUnknownField(input, extensionRegistry, tag)) {
                done = true; // was an endgroup tag
              }
              break;
            } // default:
          } // switch (tag)
        } // while (!done)
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        throw e.unwrapIOException();
      } finally {
        onChanged();
      } // finally
      return this;
    }
    private int bitField0_;

    private java.util.List<com.metas.Mappable> mappables_ =
      java.util.Collections.emptyList();
    private void ensureMappablesIsMutable() {
      if (!((bitField0_ & 0x00000001) != 0)) {
        mappables_ = new java.util.ArrayList<com.metas.Mappable>(mappables_);
        bitField0_ |= 0x00000001;
       }
    }

    private com.google.protobuf.RepeatedFieldBuilderV3<
        com.metas.Mappable, com.metas.Mappable.Builder, com.metas.MappableOrBuilder> mappablesBuilder_;

    /**
     * <code>repeated .metas.Mappable mappables = 1 [json_name = "mappables"];</code>
     */
    public java.util.List<com.metas.Mappable> getMappablesList() {
      if (mappablesBuilder_ == null) {
        return java.util.Collections.unmodifiableList(mappables_);
      } else {
        return mappablesBuilder_.getMessageList();
      }
    }
    /**
     * <code>repeated .metas.Mappable mappables = 1 [json_name = "mappables"];</code>
     */
    public int getMappablesCount() {
      if (mappablesBuilder_ == null) {
        return mappables_.size();
      } else {
        return mappablesBuilder_.getCount();
      }
    }
    /**
     * <code>repeated .metas.Mappable mappables = 1 [json_name = "mappables"];</code>
     */
    public com.metas.Mappable getMappables(int index) {
      if (mappablesBuilder_ == null) {
        return mappables_.get(index);
      } else {
        return mappablesBuilder_.getMessage(index);
      }
    }
    /**
     * <code>repeated .metas.Mappable mappables = 1 [json_name = "mappables"];</code>
     */
    public Builder setMappables(
        int index, com.metas.Mappable value) {
      if (mappablesBuilder_ == null) {
        if (value == null) {
          throw new NullPointerException();
        }
        ensureMappablesIsMutable();
        mappables_.set(index, value);
        onChanged();
      } else {
        mappablesBuilder_.setMessage(index, value);
      }
      return this;
    }
    /**
     * <code>repeated .metas.Mappable mappables = 1 [json_name = "mappables"];</code>
     */
    public Builder setMappables(
        int index, com.metas.Mappable.Builder builderForValue) {
      if (mappablesBuilder_ == null) {
        ensureMappablesIsMutable();
        mappables_.set(index, builderForValue.build());
        onChanged();
      } else {
        mappablesBuilder_.setMessage(index, builderForValue.build());
      }
      return this;
    }
    /**
     * <code>repeated .metas.Mappable mappables = 1 [json_name = "mappables"];</code>
     */
    public Builder addMappables(com.metas.Mappable value) {
      if (mappablesBuilder_ == null) {
        if (value == null) {
          throw new NullPointerException();
        }
        ensureMappablesIsMutable();
        mappables_.add(value);
        onChanged();
      } else {
        mappablesBuilder_.addMessage(value);
      }
      return this;
    }
    /**
     * <code>repeated .metas.Mappable mappables = 1 [json_name = "mappables"];</code>
     */
    public Builder addMappables(
        int index, com.metas.Mappable value) {
      if (mappablesBuilder_ == null) {
        if (value == null) {
          throw new NullPointerException();
        }
        ensureMappablesIsMutable();
        mappables_.add(index, value);
        onChanged();
      } else {
        mappablesBuilder_.addMessage(index, value);
      }
      return this;
    }
    /**
     * <code>repeated .metas.Mappable mappables = 1 [json_name = "mappables"];</code>
     */
    public Builder addMappables(
        com.metas.Mappable.Builder builderForValue) {
      if (mappablesBuilder_ == null) {
        ensureMappablesIsMutable();
        mappables_.add(builderForValue.build());
        onChanged();
      } else {
        mappablesBuilder_.addMessage(builderForValue.build());
      }
      return this;
    }
    /**
     * <code>repeated .metas.Mappable mappables = 1 [json_name = "mappables"];</code>
     */
    public Builder addMappables(
        int index, com.metas.Mappable.Builder builderForValue) {
      if (mappablesBuilder_ == null) {
        ensureMappablesIsMutable();
        mappables_.add(index, builderForValue.build());
        onChanged();
      } else {
        mappablesBuilder_.addMessage(index, builderForValue.build());
      }
      return this;
    }
    /**
     * <code>repeated .metas.Mappable mappables = 1 [json_name = "mappables"];</code>
     */
    public Builder addAllMappables(
        java.lang.Iterable<? extends com.metas.Mappable> values) {
      if (mappablesBuilder_ == null) {
        ensureMappablesIsMutable();
        com.google.protobuf.AbstractMessageLite.Builder.addAll(
            values, mappables_);
        onChanged();
      } else {
        mappablesBuilder_.addAllMessages(values);
      }
      return this;
    }
    /**
     * <code>repeated .metas.Mappable mappables = 1 [json_name = "mappables"];</code>
     */
    public Builder clearMappables() {
      if (mappablesBuilder_ == null) {
        mappables_ = java.util.Collections.emptyList();
        bitField0_ = (bitField0_ & ~0x00000001);
        onChanged();
      } else {
        mappablesBuilder_.clear();
      }
      return this;
    }
    /**
     * <code>repeated .metas.Mappable mappables = 1 [json_name = "mappables"];</code>
     */
    public Builder removeMappables(int index) {
      if (mappablesBuilder_ == null) {
        ensureMappablesIsMutable();
        mappables_.remove(index);
        onChanged();
      } else {
        mappablesBuilder_.remove(index);
      }
      return this;
    }
    /**
     * <code>repeated .metas.Mappable mappables = 1 [json_name = "mappables"];</code>
     */
    public com.metas.Mappable.Builder getMappablesBuilder(
        int index) {
      return getMappablesFieldBuilder().getBuilder(index);
    }
    /**
     * <code>repeated .metas.Mappable mappables = 1 [json_name = "mappables"];</code>
     */
    public com.metas.MappableOrBuilder getMappablesOrBuilder(
        int index) {
      if (mappablesBuilder_ == null) {
        return mappables_.get(index);  } else {
        return mappablesBuilder_.getMessageOrBuilder(index);
      }
    }
    /**
     * <code>repeated .metas.Mappable mappables = 1 [json_name = "mappables"];</code>
     */
    public java.util.List<? extends com.metas.MappableOrBuilder> 
         getMappablesOrBuilderList() {
      if (mappablesBuilder_ != null) {
        return mappablesBuilder_.getMessageOrBuilderList();
      } else {
        return java.util.Collections.unmodifiableList(mappables_);
      }
    }
    /**
     * <code>repeated .metas.Mappable mappables = 1 [json_name = "mappables"];</code>
     */
    public com.metas.Mappable.Builder addMappablesBuilder() {
      return getMappablesFieldBuilder().addBuilder(
          com.metas.Mappable.getDefaultInstance());
    }
    /**
     * <code>repeated .metas.Mappable mappables = 1 [json_name = "mappables"];</code>
     */
    public com.metas.Mappable.Builder addMappablesBuilder(
        int index) {
      return getMappablesFieldBuilder().addBuilder(
          index, com.metas.Mappable.getDefaultInstance());
    }
    /**
     * <code>repeated .metas.Mappable mappables = 1 [json_name = "mappables"];</code>
     */
    public java.util.List<com.metas.Mappable.Builder> 
         getMappablesBuilderList() {
      return getMappablesFieldBuilder().getBuilderList();
    }
    private com.google.protobuf.RepeatedFieldBuilderV3<
        com.metas.Mappable, com.metas.Mappable.Builder, com.metas.MappableOrBuilder> 
        getMappablesFieldBuilder() {
      if (mappablesBuilder_ == null) {
        mappablesBuilder_ = new com.google.protobuf.RepeatedFieldBuilderV3<
            com.metas.Mappable, com.metas.Mappable.Builder, com.metas.MappableOrBuilder>(
                mappables_,
                ((bitField0_ & 0x00000001) != 0),
                getParentForChildren(),
                isClean());
        mappables_ = null;
      }
      return mappablesBuilder_;
    }

    private java.util.List<com.parameters.Parameter> parameters_ =
      java.util.Collections.emptyList();
    private void ensureParametersIsMutable() {
      if (!((bitField0_ & 0x00000002) != 0)) {
        parameters_ = new java.util.ArrayList<com.parameters.Parameter>(parameters_);
        bitField0_ |= 0x00000002;
       }
    }

    private com.google.protobuf.RepeatedFieldBuilderV3<
        com.parameters.Parameter, com.parameters.Parameter.Builder, com.parameters.ParameterOrBuilder> parametersBuilder_;

    /**
     * <code>repeated .parameters.Parameter parameters = 2 [json_name = "parameters"];</code>
     */
    public java.util.List<com.parameters.Parameter> getParametersList() {
      if (parametersBuilder_ == null) {
        return java.util.Collections.unmodifiableList(parameters_);
      } else {
        return parametersBuilder_.getMessageList();
      }
    }
    /**
     * <code>repeated .parameters.Parameter parameters = 2 [json_name = "parameters"];</code>
     */
    public int getParametersCount() {
      if (parametersBuilder_ == null) {
        return parameters_.size();
      } else {
        return parametersBuilder_.getCount();
      }
    }
    /**
     * <code>repeated .parameters.Parameter parameters = 2 [json_name = "parameters"];</code>
     */
    public com.parameters.Parameter getParameters(int index) {
      if (parametersBuilder_ == null) {
        return parameters_.get(index);
      } else {
        return parametersBuilder_.getMessage(index);
      }
    }
    /**
     * <code>repeated .parameters.Parameter parameters = 2 [json_name = "parameters"];</code>
     */
    public Builder setParameters(
        int index, com.parameters.Parameter value) {
      if (parametersBuilder_ == null) {
        if (value == null) {
          throw new NullPointerException();
        }
        ensureParametersIsMutable();
        parameters_.set(index, value);
        onChanged();
      } else {
        parametersBuilder_.setMessage(index, value);
      }
      return this;
    }
    /**
     * <code>repeated .parameters.Parameter parameters = 2 [json_name = "parameters"];</code>
     */
    public Builder setParameters(
        int index, com.parameters.Parameter.Builder builderForValue) {
      if (parametersBuilder_ == null) {
        ensureParametersIsMutable();
        parameters_.set(index, builderForValue.build());
        onChanged();
      } else {
        parametersBuilder_.setMessage(index, builderForValue.build());
      }
      return this;
    }
    /**
     * <code>repeated .parameters.Parameter parameters = 2 [json_name = "parameters"];</code>
     */
    public Builder addParameters(com.parameters.Parameter value) {
      if (parametersBuilder_ == null) {
        if (value == null) {
          throw new NullPointerException();
        }
        ensureParametersIsMutable();
        parameters_.add(value);
        onChanged();
      } else {
        parametersBuilder_.addMessage(value);
      }
      return this;
    }
    /**
     * <code>repeated .parameters.Parameter parameters = 2 [json_name = "parameters"];</code>
     */
    public Builder addParameters(
        int index, com.parameters.Parameter value) {
      if (parametersBuilder_ == null) {
        if (value == null) {
          throw new NullPointerException();
        }
        ensureParametersIsMutable();
        parameters_.add(index, value);
        onChanged();
      } else {
        parametersBuilder_.addMessage(index, value);
      }
      return this;
    }
    /**
     * <code>repeated .parameters.Parameter parameters = 2 [json_name = "parameters"];</code>
     */
    public Builder addParameters(
        com.parameters.Parameter.Builder builderForValue) {
      if (parametersBuilder_ == null) {
        ensureParametersIsMutable();
        parameters_.add(builderForValue.build());
        onChanged();
      } else {
        parametersBuilder_.addMessage(builderForValue.build());
      }
      return this;
    }
    /**
     * <code>repeated .parameters.Parameter parameters = 2 [json_name = "parameters"];</code>
     */
    public Builder addParameters(
        int index, com.parameters.Parameter.Builder builderForValue) {
      if (parametersBuilder_ == null) {
        ensureParametersIsMutable();
        parameters_.add(index, builderForValue.build());
        onChanged();
      } else {
        parametersBuilder_.addMessage(index, builderForValue.build());
      }
      return this;
    }
    /**
     * <code>repeated .parameters.Parameter parameters = 2 [json_name = "parameters"];</code>
     */
    public Builder addAllParameters(
        java.lang.Iterable<? extends com.parameters.Parameter> values) {
      if (parametersBuilder_ == null) {
        ensureParametersIsMutable();
        com.google.protobuf.AbstractMessageLite.Builder.addAll(
            values, parameters_);
        onChanged();
      } else {
        parametersBuilder_.addAllMessages(values);
      }
      return this;
    }
    /**
     * <code>repeated .parameters.Parameter parameters = 2 [json_name = "parameters"];</code>
     */
    public Builder clearParameters() {
      if (parametersBuilder_ == null) {
        parameters_ = java.util.Collections.emptyList();
        bitField0_ = (bitField0_ & ~0x00000002);
        onChanged();
      } else {
        parametersBuilder_.clear();
      }
      return this;
    }
    /**
     * <code>repeated .parameters.Parameter parameters = 2 [json_name = "parameters"];</code>
     */
    public Builder removeParameters(int index) {
      if (parametersBuilder_ == null) {
        ensureParametersIsMutable();
        parameters_.remove(index);
        onChanged();
      } else {
        parametersBuilder_.remove(index);
      }
      return this;
    }
    /**
     * <code>repeated .parameters.Parameter parameters = 2 [json_name = "parameters"];</code>
     */
    public com.parameters.Parameter.Builder getParametersBuilder(
        int index) {
      return getParametersFieldBuilder().getBuilder(index);
    }
    /**
     * <code>repeated .parameters.Parameter parameters = 2 [json_name = "parameters"];</code>
     */
    public com.parameters.ParameterOrBuilder getParametersOrBuilder(
        int index) {
      if (parametersBuilder_ == null) {
        return parameters_.get(index);  } else {
        return parametersBuilder_.getMessageOrBuilder(index);
      }
    }
    /**
     * <code>repeated .parameters.Parameter parameters = 2 [json_name = "parameters"];</code>
     */
    public java.util.List<? extends com.parameters.ParameterOrBuilder> 
         getParametersOrBuilderList() {
      if (parametersBuilder_ != null) {
        return parametersBuilder_.getMessageOrBuilderList();
      } else {
        return java.util.Collections.unmodifiableList(parameters_);
      }
    }
    /**
     * <code>repeated .parameters.Parameter parameters = 2 [json_name = "parameters"];</code>
     */
    public com.parameters.Parameter.Builder addParametersBuilder() {
      return getParametersFieldBuilder().addBuilder(
          com.parameters.Parameter.getDefaultInstance());
    }
    /**
     * <code>repeated .parameters.Parameter parameters = 2 [json_name = "parameters"];</code>
     */
    public com.parameters.Parameter.Builder addParametersBuilder(
        int index) {
      return getParametersFieldBuilder().addBuilder(
          index, com.parameters.Parameter.getDefaultInstance());
    }
    /**
     * <code>repeated .parameters.Parameter parameters = 2 [json_name = "parameters"];</code>
     */
    public java.util.List<com.parameters.Parameter.Builder> 
         getParametersBuilderList() {
      return getParametersFieldBuilder().getBuilderList();
    }
    private com.google.protobuf.RepeatedFieldBuilderV3<
        com.parameters.Parameter, com.parameters.Parameter.Builder, com.parameters.ParameterOrBuilder> 
        getParametersFieldBuilder() {
      if (parametersBuilder_ == null) {
        parametersBuilder_ = new com.google.protobuf.RepeatedFieldBuilderV3<
            com.parameters.Parameter, com.parameters.Parameter.Builder, com.parameters.ParameterOrBuilder>(
                parameters_,
                ((bitField0_ & 0x00000002) != 0),
                getParentForChildren(),
                isClean());
        parameters_ = null;
      }
      return parametersBuilder_;
    }
    @java.lang.Override
    public final Builder setUnknownFields(
        final com.google.protobuf.UnknownFieldSet unknownFields) {
      return super.setUnknownFields(unknownFields);
    }

    @java.lang.Override
    public final Builder mergeUnknownFields(
        final com.google.protobuf.UnknownFieldSet unknownFields) {
      return super.mergeUnknownFields(unknownFields);
    }


    // @@protoc_insertion_point(builder_scope:metas.Genesis)
  }

  // @@protoc_insertion_point(class_scope:metas.Genesis)
  private static final com.metas.Genesis DEFAULT_INSTANCE;
  static {
    DEFAULT_INSTANCE = new com.metas.Genesis();
  }

  public static com.metas.Genesis getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }

  private static final com.google.protobuf.Parser<Genesis>
      PARSER = new com.google.protobuf.AbstractParser<Genesis>() {
    @java.lang.Override
    public Genesis parsePartialFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      Builder builder = newBuilder();
      try {
        builder.mergeFrom(input, extensionRegistry);
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        throw e.setUnfinishedMessage(builder.buildPartial());
      } catch (com.google.protobuf.UninitializedMessageException e) {
        throw e.asInvalidProtocolBufferException().setUnfinishedMessage(builder.buildPartial());
      } catch (java.io.IOException e) {
        throw new com.google.protobuf.InvalidProtocolBufferException(e)
            .setUnfinishedMessage(builder.buildPartial());
      }
      return builder.buildPartial();
    }
  };

  public static com.google.protobuf.Parser<Genesis> parser() {
    return PARSER;
  }

  @java.lang.Override
  public com.google.protobuf.Parser<Genesis> getParserForType() {
    return PARSER;
  }

  @java.lang.Override
  public com.metas.Genesis getDefaultInstanceForType() {
    return DEFAULT_INSTANCE;
  }

}
