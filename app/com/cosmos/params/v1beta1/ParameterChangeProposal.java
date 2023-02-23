// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: cosmos/params/v1beta1/params.proto

package com.cosmos.params.v1beta1;

/**
 * <pre>
 * ParameterChangeProposal defines a proposal to change one or more parameters.
 * </pre>
 *
 * Protobuf type {@code cosmos.params.v1beta1.ParameterChangeProposal}
 */
public final class ParameterChangeProposal extends
    com.google.protobuf.GeneratedMessageV3 implements
    // @@protoc_insertion_point(message_implements:cosmos.params.v1beta1.ParameterChangeProposal)
    ParameterChangeProposalOrBuilder {
private static final long serialVersionUID = 0L;
  // Use ParameterChangeProposal.newBuilder() to construct.
  private ParameterChangeProposal(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
    super(builder);
  }
  private ParameterChangeProposal() {
    title_ = "";
    description_ = "";
    changes_ = java.util.Collections.emptyList();
  }

  @java.lang.Override
  @SuppressWarnings({"unused"})
  protected java.lang.Object newInstance(
      UnusedPrivateParameter unused) {
    return new ParameterChangeProposal();
  }

  public static final com.google.protobuf.Descriptors.Descriptor
      getDescriptor() {
    return com.cosmos.params.v1beta1.ParamsProto.internal_static_cosmos_params_v1beta1_ParameterChangeProposal_descriptor;
  }

  @java.lang.Override
  protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internalGetFieldAccessorTable() {
    return com.cosmos.params.v1beta1.ParamsProto.internal_static_cosmos_params_v1beta1_ParameterChangeProposal_fieldAccessorTable
        .ensureFieldAccessorsInitialized(
            com.cosmos.params.v1beta1.ParameterChangeProposal.class, com.cosmos.params.v1beta1.ParameterChangeProposal.Builder.class);
  }

  public static final int TITLE_FIELD_NUMBER = 1;
  @SuppressWarnings("serial")
  private volatile java.lang.Object title_ = "";
  /**
   * <code>string title = 1 [json_name = "title"];</code>
   * @return The title.
   */
  @java.lang.Override
  public java.lang.String getTitle() {
    java.lang.Object ref = title_;
    if (ref instanceof java.lang.String) {
      return (java.lang.String) ref;
    } else {
      com.google.protobuf.ByteString bs = 
          (com.google.protobuf.ByteString) ref;
      java.lang.String s = bs.toStringUtf8();
      title_ = s;
      return s;
    }
  }
  /**
   * <code>string title = 1 [json_name = "title"];</code>
   * @return The bytes for title.
   */
  @java.lang.Override
  public com.google.protobuf.ByteString
      getTitleBytes() {
    java.lang.Object ref = title_;
    if (ref instanceof java.lang.String) {
      com.google.protobuf.ByteString b = 
          com.google.protobuf.ByteString.copyFromUtf8(
              (java.lang.String) ref);
      title_ = b;
      return b;
    } else {
      return (com.google.protobuf.ByteString) ref;
    }
  }

  public static final int DESCRIPTION_FIELD_NUMBER = 2;
  @SuppressWarnings("serial")
  private volatile java.lang.Object description_ = "";
  /**
   * <code>string description = 2 [json_name = "description"];</code>
   * @return The description.
   */
  @java.lang.Override
  public java.lang.String getDescription() {
    java.lang.Object ref = description_;
    if (ref instanceof java.lang.String) {
      return (java.lang.String) ref;
    } else {
      com.google.protobuf.ByteString bs = 
          (com.google.protobuf.ByteString) ref;
      java.lang.String s = bs.toStringUtf8();
      description_ = s;
      return s;
    }
  }
  /**
   * <code>string description = 2 [json_name = "description"];</code>
   * @return The bytes for description.
   */
  @java.lang.Override
  public com.google.protobuf.ByteString
      getDescriptionBytes() {
    java.lang.Object ref = description_;
    if (ref instanceof java.lang.String) {
      com.google.protobuf.ByteString b = 
          com.google.protobuf.ByteString.copyFromUtf8(
              (java.lang.String) ref);
      description_ = b;
      return b;
    } else {
      return (com.google.protobuf.ByteString) ref;
    }
  }

  public static final int CHANGES_FIELD_NUMBER = 3;
  @SuppressWarnings("serial")
  private java.util.List<com.cosmos.params.v1beta1.ParamChange> changes_;
  /**
   * <code>repeated .cosmos.params.v1beta1.ParamChange changes = 3 [json_name = "changes", (.gogoproto.nullable) = false];</code>
   */
  @java.lang.Override
  public java.util.List<com.cosmos.params.v1beta1.ParamChange> getChangesList() {
    return changes_;
  }
  /**
   * <code>repeated .cosmos.params.v1beta1.ParamChange changes = 3 [json_name = "changes", (.gogoproto.nullable) = false];</code>
   */
  @java.lang.Override
  public java.util.List<? extends com.cosmos.params.v1beta1.ParamChangeOrBuilder> 
      getChangesOrBuilderList() {
    return changes_;
  }
  /**
   * <code>repeated .cosmos.params.v1beta1.ParamChange changes = 3 [json_name = "changes", (.gogoproto.nullable) = false];</code>
   */
  @java.lang.Override
  public int getChangesCount() {
    return changes_.size();
  }
  /**
   * <code>repeated .cosmos.params.v1beta1.ParamChange changes = 3 [json_name = "changes", (.gogoproto.nullable) = false];</code>
   */
  @java.lang.Override
  public com.cosmos.params.v1beta1.ParamChange getChanges(int index) {
    return changes_.get(index);
  }
  /**
   * <code>repeated .cosmos.params.v1beta1.ParamChange changes = 3 [json_name = "changes", (.gogoproto.nullable) = false];</code>
   */
  @java.lang.Override
  public com.cosmos.params.v1beta1.ParamChangeOrBuilder getChangesOrBuilder(
      int index) {
    return changes_.get(index);
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
    if (!com.google.protobuf.GeneratedMessageV3.isStringEmpty(title_)) {
      com.google.protobuf.GeneratedMessageV3.writeString(output, 1, title_);
    }
    if (!com.google.protobuf.GeneratedMessageV3.isStringEmpty(description_)) {
      com.google.protobuf.GeneratedMessageV3.writeString(output, 2, description_);
    }
    for (int i = 0; i < changes_.size(); i++) {
      output.writeMessage(3, changes_.get(i));
    }
    getUnknownFields().writeTo(output);
  }

  @java.lang.Override
  public int getSerializedSize() {
    int size = memoizedSize;
    if (size != -1) return size;

    size = 0;
    if (!com.google.protobuf.GeneratedMessageV3.isStringEmpty(title_)) {
      size += com.google.protobuf.GeneratedMessageV3.computeStringSize(1, title_);
    }
    if (!com.google.protobuf.GeneratedMessageV3.isStringEmpty(description_)) {
      size += com.google.protobuf.GeneratedMessageV3.computeStringSize(2, description_);
    }
    for (int i = 0; i < changes_.size(); i++) {
      size += com.google.protobuf.CodedOutputStream
        .computeMessageSize(3, changes_.get(i));
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
    if (!(obj instanceof com.cosmos.params.v1beta1.ParameterChangeProposal)) {
      return super.equals(obj);
    }
    com.cosmos.params.v1beta1.ParameterChangeProposal other = (com.cosmos.params.v1beta1.ParameterChangeProposal) obj;

    if (!getTitle()
        .equals(other.getTitle())) return false;
    if (!getDescription()
        .equals(other.getDescription())) return false;
    if (!getChangesList()
        .equals(other.getChangesList())) return false;
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
    hash = (37 * hash) + TITLE_FIELD_NUMBER;
    hash = (53 * hash) + getTitle().hashCode();
    hash = (37 * hash) + DESCRIPTION_FIELD_NUMBER;
    hash = (53 * hash) + getDescription().hashCode();
    if (getChangesCount() > 0) {
      hash = (37 * hash) + CHANGES_FIELD_NUMBER;
      hash = (53 * hash) + getChangesList().hashCode();
    }
    hash = (29 * hash) + getUnknownFields().hashCode();
    memoizedHashCode = hash;
    return hash;
  }

  public static com.cosmos.params.v1beta1.ParameterChangeProposal parseFrom(
      java.nio.ByteBuffer data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static com.cosmos.params.v1beta1.ParameterChangeProposal parseFrom(
      java.nio.ByteBuffer data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static com.cosmos.params.v1beta1.ParameterChangeProposal parseFrom(
      com.google.protobuf.ByteString data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static com.cosmos.params.v1beta1.ParameterChangeProposal parseFrom(
      com.google.protobuf.ByteString data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static com.cosmos.params.v1beta1.ParameterChangeProposal parseFrom(byte[] data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static com.cosmos.params.v1beta1.ParameterChangeProposal parseFrom(
      byte[] data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static com.cosmos.params.v1beta1.ParameterChangeProposal parseFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static com.cosmos.params.v1beta1.ParameterChangeProposal parseFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input, extensionRegistry);
  }
  public static com.cosmos.params.v1beta1.ParameterChangeProposal parseDelimitedFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input);
  }
  public static com.cosmos.params.v1beta1.ParameterChangeProposal parseDelimitedFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
  }
  public static com.cosmos.params.v1beta1.ParameterChangeProposal parseFrom(
      com.google.protobuf.CodedInputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static com.cosmos.params.v1beta1.ParameterChangeProposal parseFrom(
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
  public static Builder newBuilder(com.cosmos.params.v1beta1.ParameterChangeProposal prototype) {
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
   * <pre>
   * ParameterChangeProposal defines a proposal to change one or more parameters.
   * </pre>
   *
   * Protobuf type {@code cosmos.params.v1beta1.ParameterChangeProposal}
   */
  public static final class Builder extends
      com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
      // @@protoc_insertion_point(builder_implements:cosmos.params.v1beta1.ParameterChangeProposal)
      com.cosmos.params.v1beta1.ParameterChangeProposalOrBuilder {
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return com.cosmos.params.v1beta1.ParamsProto.internal_static_cosmos_params_v1beta1_ParameterChangeProposal_descriptor;
    }

    @java.lang.Override
    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return com.cosmos.params.v1beta1.ParamsProto.internal_static_cosmos_params_v1beta1_ParameterChangeProposal_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              com.cosmos.params.v1beta1.ParameterChangeProposal.class, com.cosmos.params.v1beta1.ParameterChangeProposal.Builder.class);
    }

    // Construct using com.cosmos.params.v1beta1.ParameterChangeProposal.newBuilder()
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
      title_ = "";
      description_ = "";
      if (changesBuilder_ == null) {
        changes_ = java.util.Collections.emptyList();
      } else {
        changes_ = null;
        changesBuilder_.clear();
      }
      bitField0_ = (bitField0_ & ~0x00000004);
      return this;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.Descriptor
        getDescriptorForType() {
      return com.cosmos.params.v1beta1.ParamsProto.internal_static_cosmos_params_v1beta1_ParameterChangeProposal_descriptor;
    }

    @java.lang.Override
    public com.cosmos.params.v1beta1.ParameterChangeProposal getDefaultInstanceForType() {
      return com.cosmos.params.v1beta1.ParameterChangeProposal.getDefaultInstance();
    }

    @java.lang.Override
    public com.cosmos.params.v1beta1.ParameterChangeProposal build() {
      com.cosmos.params.v1beta1.ParameterChangeProposal result = buildPartial();
      if (!result.isInitialized()) {
        throw newUninitializedMessageException(result);
      }
      return result;
    }

    @java.lang.Override
    public com.cosmos.params.v1beta1.ParameterChangeProposal buildPartial() {
      com.cosmos.params.v1beta1.ParameterChangeProposal result = new com.cosmos.params.v1beta1.ParameterChangeProposal(this);
      buildPartialRepeatedFields(result);
      if (bitField0_ != 0) { buildPartial0(result); }
      onBuilt();
      return result;
    }

    private void buildPartialRepeatedFields(com.cosmos.params.v1beta1.ParameterChangeProposal result) {
      if (changesBuilder_ == null) {
        if (((bitField0_ & 0x00000004) != 0)) {
          changes_ = java.util.Collections.unmodifiableList(changes_);
          bitField0_ = (bitField0_ & ~0x00000004);
        }
        result.changes_ = changes_;
      } else {
        result.changes_ = changesBuilder_.build();
      }
    }

    private void buildPartial0(com.cosmos.params.v1beta1.ParameterChangeProposal result) {
      int from_bitField0_ = bitField0_;
      if (((from_bitField0_ & 0x00000001) != 0)) {
        result.title_ = title_;
      }
      if (((from_bitField0_ & 0x00000002) != 0)) {
        result.description_ = description_;
      }
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
      if (other instanceof com.cosmos.params.v1beta1.ParameterChangeProposal) {
        return mergeFrom((com.cosmos.params.v1beta1.ParameterChangeProposal)other);
      } else {
        super.mergeFrom(other);
        return this;
      }
    }

    public Builder mergeFrom(com.cosmos.params.v1beta1.ParameterChangeProposal other) {
      if (other == com.cosmos.params.v1beta1.ParameterChangeProposal.getDefaultInstance()) return this;
      if (!other.getTitle().isEmpty()) {
        title_ = other.title_;
        bitField0_ |= 0x00000001;
        onChanged();
      }
      if (!other.getDescription().isEmpty()) {
        description_ = other.description_;
        bitField0_ |= 0x00000002;
        onChanged();
      }
      if (changesBuilder_ == null) {
        if (!other.changes_.isEmpty()) {
          if (changes_.isEmpty()) {
            changes_ = other.changes_;
            bitField0_ = (bitField0_ & ~0x00000004);
          } else {
            ensureChangesIsMutable();
            changes_.addAll(other.changes_);
          }
          onChanged();
        }
      } else {
        if (!other.changes_.isEmpty()) {
          if (changesBuilder_.isEmpty()) {
            changesBuilder_.dispose();
            changesBuilder_ = null;
            changes_ = other.changes_;
            bitField0_ = (bitField0_ & ~0x00000004);
            changesBuilder_ = 
              com.google.protobuf.GeneratedMessageV3.alwaysUseFieldBuilders ?
                 getChangesFieldBuilder() : null;
          } else {
            changesBuilder_.addAllMessages(other.changes_);
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
              title_ = input.readStringRequireUtf8();
              bitField0_ |= 0x00000001;
              break;
            } // case 10
            case 18: {
              description_ = input.readStringRequireUtf8();
              bitField0_ |= 0x00000002;
              break;
            } // case 18
            case 26: {
              com.cosmos.params.v1beta1.ParamChange m =
                  input.readMessage(
                      com.cosmos.params.v1beta1.ParamChange.parser(),
                      extensionRegistry);
              if (changesBuilder_ == null) {
                ensureChangesIsMutable();
                changes_.add(m);
              } else {
                changesBuilder_.addMessage(m);
              }
              break;
            } // case 26
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

    private java.lang.Object title_ = "";
    /**
     * <code>string title = 1 [json_name = "title"];</code>
     * @return The title.
     */
    public java.lang.String getTitle() {
      java.lang.Object ref = title_;
      if (!(ref instanceof java.lang.String)) {
        com.google.protobuf.ByteString bs =
            (com.google.protobuf.ByteString) ref;
        java.lang.String s = bs.toStringUtf8();
        title_ = s;
        return s;
      } else {
        return (java.lang.String) ref;
      }
    }
    /**
     * <code>string title = 1 [json_name = "title"];</code>
     * @return The bytes for title.
     */
    public com.google.protobuf.ByteString
        getTitleBytes() {
      java.lang.Object ref = title_;
      if (ref instanceof String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8(
                (java.lang.String) ref);
        title_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }
    /**
     * <code>string title = 1 [json_name = "title"];</code>
     * @param value The title to set.
     * @return This builder for chaining.
     */
    public Builder setTitle(
        java.lang.String value) {
      if (value == null) { throw new NullPointerException(); }
      title_ = value;
      bitField0_ |= 0x00000001;
      onChanged();
      return this;
    }
    /**
     * <code>string title = 1 [json_name = "title"];</code>
     * @return This builder for chaining.
     */
    public Builder clearTitle() {
      title_ = getDefaultInstance().getTitle();
      bitField0_ = (bitField0_ & ~0x00000001);
      onChanged();
      return this;
    }
    /**
     * <code>string title = 1 [json_name = "title"];</code>
     * @param value The bytes for title to set.
     * @return This builder for chaining.
     */
    public Builder setTitleBytes(
        com.google.protobuf.ByteString value) {
      if (value == null) { throw new NullPointerException(); }
      checkByteStringIsUtf8(value);
      title_ = value;
      bitField0_ |= 0x00000001;
      onChanged();
      return this;
    }

    private java.lang.Object description_ = "";
    /**
     * <code>string description = 2 [json_name = "description"];</code>
     * @return The description.
     */
    public java.lang.String getDescription() {
      java.lang.Object ref = description_;
      if (!(ref instanceof java.lang.String)) {
        com.google.protobuf.ByteString bs =
            (com.google.protobuf.ByteString) ref;
        java.lang.String s = bs.toStringUtf8();
        description_ = s;
        return s;
      } else {
        return (java.lang.String) ref;
      }
    }
    /**
     * <code>string description = 2 [json_name = "description"];</code>
     * @return The bytes for description.
     */
    public com.google.protobuf.ByteString
        getDescriptionBytes() {
      java.lang.Object ref = description_;
      if (ref instanceof String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8(
                (java.lang.String) ref);
        description_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }
    /**
     * <code>string description = 2 [json_name = "description"];</code>
     * @param value The description to set.
     * @return This builder for chaining.
     */
    public Builder setDescription(
        java.lang.String value) {
      if (value == null) { throw new NullPointerException(); }
      description_ = value;
      bitField0_ |= 0x00000002;
      onChanged();
      return this;
    }
    /**
     * <code>string description = 2 [json_name = "description"];</code>
     * @return This builder for chaining.
     */
    public Builder clearDescription() {
      description_ = getDefaultInstance().getDescription();
      bitField0_ = (bitField0_ & ~0x00000002);
      onChanged();
      return this;
    }
    /**
     * <code>string description = 2 [json_name = "description"];</code>
     * @param value The bytes for description to set.
     * @return This builder for chaining.
     */
    public Builder setDescriptionBytes(
        com.google.protobuf.ByteString value) {
      if (value == null) { throw new NullPointerException(); }
      checkByteStringIsUtf8(value);
      description_ = value;
      bitField0_ |= 0x00000002;
      onChanged();
      return this;
    }

    private java.util.List<com.cosmos.params.v1beta1.ParamChange> changes_ =
      java.util.Collections.emptyList();
    private void ensureChangesIsMutable() {
      if (!((bitField0_ & 0x00000004) != 0)) {
        changes_ = new java.util.ArrayList<com.cosmos.params.v1beta1.ParamChange>(changes_);
        bitField0_ |= 0x00000004;
       }
    }

    private com.google.protobuf.RepeatedFieldBuilderV3<
        com.cosmos.params.v1beta1.ParamChange, com.cosmos.params.v1beta1.ParamChange.Builder, com.cosmos.params.v1beta1.ParamChangeOrBuilder> changesBuilder_;

    /**
     * <code>repeated .cosmos.params.v1beta1.ParamChange changes = 3 [json_name = "changes", (.gogoproto.nullable) = false];</code>
     */
    public java.util.List<com.cosmos.params.v1beta1.ParamChange> getChangesList() {
      if (changesBuilder_ == null) {
        return java.util.Collections.unmodifiableList(changes_);
      } else {
        return changesBuilder_.getMessageList();
      }
    }
    /**
     * <code>repeated .cosmos.params.v1beta1.ParamChange changes = 3 [json_name = "changes", (.gogoproto.nullable) = false];</code>
     */
    public int getChangesCount() {
      if (changesBuilder_ == null) {
        return changes_.size();
      } else {
        return changesBuilder_.getCount();
      }
    }
    /**
     * <code>repeated .cosmos.params.v1beta1.ParamChange changes = 3 [json_name = "changes", (.gogoproto.nullable) = false];</code>
     */
    public com.cosmos.params.v1beta1.ParamChange getChanges(int index) {
      if (changesBuilder_ == null) {
        return changes_.get(index);
      } else {
        return changesBuilder_.getMessage(index);
      }
    }
    /**
     * <code>repeated .cosmos.params.v1beta1.ParamChange changes = 3 [json_name = "changes", (.gogoproto.nullable) = false];</code>
     */
    public Builder setChanges(
        int index, com.cosmos.params.v1beta1.ParamChange value) {
      if (changesBuilder_ == null) {
        if (value == null) {
          throw new NullPointerException();
        }
        ensureChangesIsMutable();
        changes_.set(index, value);
        onChanged();
      } else {
        changesBuilder_.setMessage(index, value);
      }
      return this;
    }
    /**
     * <code>repeated .cosmos.params.v1beta1.ParamChange changes = 3 [json_name = "changes", (.gogoproto.nullable) = false];</code>
     */
    public Builder setChanges(
        int index, com.cosmos.params.v1beta1.ParamChange.Builder builderForValue) {
      if (changesBuilder_ == null) {
        ensureChangesIsMutable();
        changes_.set(index, builderForValue.build());
        onChanged();
      } else {
        changesBuilder_.setMessage(index, builderForValue.build());
      }
      return this;
    }
    /**
     * <code>repeated .cosmos.params.v1beta1.ParamChange changes = 3 [json_name = "changes", (.gogoproto.nullable) = false];</code>
     */
    public Builder addChanges(com.cosmos.params.v1beta1.ParamChange value) {
      if (changesBuilder_ == null) {
        if (value == null) {
          throw new NullPointerException();
        }
        ensureChangesIsMutable();
        changes_.add(value);
        onChanged();
      } else {
        changesBuilder_.addMessage(value);
      }
      return this;
    }
    /**
     * <code>repeated .cosmos.params.v1beta1.ParamChange changes = 3 [json_name = "changes", (.gogoproto.nullable) = false];</code>
     */
    public Builder addChanges(
        int index, com.cosmos.params.v1beta1.ParamChange value) {
      if (changesBuilder_ == null) {
        if (value == null) {
          throw new NullPointerException();
        }
        ensureChangesIsMutable();
        changes_.add(index, value);
        onChanged();
      } else {
        changesBuilder_.addMessage(index, value);
      }
      return this;
    }
    /**
     * <code>repeated .cosmos.params.v1beta1.ParamChange changes = 3 [json_name = "changes", (.gogoproto.nullable) = false];</code>
     */
    public Builder addChanges(
        com.cosmos.params.v1beta1.ParamChange.Builder builderForValue) {
      if (changesBuilder_ == null) {
        ensureChangesIsMutable();
        changes_.add(builderForValue.build());
        onChanged();
      } else {
        changesBuilder_.addMessage(builderForValue.build());
      }
      return this;
    }
    /**
     * <code>repeated .cosmos.params.v1beta1.ParamChange changes = 3 [json_name = "changes", (.gogoproto.nullable) = false];</code>
     */
    public Builder addChanges(
        int index, com.cosmos.params.v1beta1.ParamChange.Builder builderForValue) {
      if (changesBuilder_ == null) {
        ensureChangesIsMutable();
        changes_.add(index, builderForValue.build());
        onChanged();
      } else {
        changesBuilder_.addMessage(index, builderForValue.build());
      }
      return this;
    }
    /**
     * <code>repeated .cosmos.params.v1beta1.ParamChange changes = 3 [json_name = "changes", (.gogoproto.nullable) = false];</code>
     */
    public Builder addAllChanges(
        java.lang.Iterable<? extends com.cosmos.params.v1beta1.ParamChange> values) {
      if (changesBuilder_ == null) {
        ensureChangesIsMutable();
        com.google.protobuf.AbstractMessageLite.Builder.addAll(
            values, changes_);
        onChanged();
      } else {
        changesBuilder_.addAllMessages(values);
      }
      return this;
    }
    /**
     * <code>repeated .cosmos.params.v1beta1.ParamChange changes = 3 [json_name = "changes", (.gogoproto.nullable) = false];</code>
     */
    public Builder clearChanges() {
      if (changesBuilder_ == null) {
        changes_ = java.util.Collections.emptyList();
        bitField0_ = (bitField0_ & ~0x00000004);
        onChanged();
      } else {
        changesBuilder_.clear();
      }
      return this;
    }
    /**
     * <code>repeated .cosmos.params.v1beta1.ParamChange changes = 3 [json_name = "changes", (.gogoproto.nullable) = false];</code>
     */
    public Builder removeChanges(int index) {
      if (changesBuilder_ == null) {
        ensureChangesIsMutable();
        changes_.remove(index);
        onChanged();
      } else {
        changesBuilder_.remove(index);
      }
      return this;
    }
    /**
     * <code>repeated .cosmos.params.v1beta1.ParamChange changes = 3 [json_name = "changes", (.gogoproto.nullable) = false];</code>
     */
    public com.cosmos.params.v1beta1.ParamChange.Builder getChangesBuilder(
        int index) {
      return getChangesFieldBuilder().getBuilder(index);
    }
    /**
     * <code>repeated .cosmos.params.v1beta1.ParamChange changes = 3 [json_name = "changes", (.gogoproto.nullable) = false];</code>
     */
    public com.cosmos.params.v1beta1.ParamChangeOrBuilder getChangesOrBuilder(
        int index) {
      if (changesBuilder_ == null) {
        return changes_.get(index);  } else {
        return changesBuilder_.getMessageOrBuilder(index);
      }
    }
    /**
     * <code>repeated .cosmos.params.v1beta1.ParamChange changes = 3 [json_name = "changes", (.gogoproto.nullable) = false];</code>
     */
    public java.util.List<? extends com.cosmos.params.v1beta1.ParamChangeOrBuilder> 
         getChangesOrBuilderList() {
      if (changesBuilder_ != null) {
        return changesBuilder_.getMessageOrBuilderList();
      } else {
        return java.util.Collections.unmodifiableList(changes_);
      }
    }
    /**
     * <code>repeated .cosmos.params.v1beta1.ParamChange changes = 3 [json_name = "changes", (.gogoproto.nullable) = false];</code>
     */
    public com.cosmos.params.v1beta1.ParamChange.Builder addChangesBuilder() {
      return getChangesFieldBuilder().addBuilder(
          com.cosmos.params.v1beta1.ParamChange.getDefaultInstance());
    }
    /**
     * <code>repeated .cosmos.params.v1beta1.ParamChange changes = 3 [json_name = "changes", (.gogoproto.nullable) = false];</code>
     */
    public com.cosmos.params.v1beta1.ParamChange.Builder addChangesBuilder(
        int index) {
      return getChangesFieldBuilder().addBuilder(
          index, com.cosmos.params.v1beta1.ParamChange.getDefaultInstance());
    }
    /**
     * <code>repeated .cosmos.params.v1beta1.ParamChange changes = 3 [json_name = "changes", (.gogoproto.nullable) = false];</code>
     */
    public java.util.List<com.cosmos.params.v1beta1.ParamChange.Builder> 
         getChangesBuilderList() {
      return getChangesFieldBuilder().getBuilderList();
    }
    private com.google.protobuf.RepeatedFieldBuilderV3<
        com.cosmos.params.v1beta1.ParamChange, com.cosmos.params.v1beta1.ParamChange.Builder, com.cosmos.params.v1beta1.ParamChangeOrBuilder> 
        getChangesFieldBuilder() {
      if (changesBuilder_ == null) {
        changesBuilder_ = new com.google.protobuf.RepeatedFieldBuilderV3<
            com.cosmos.params.v1beta1.ParamChange, com.cosmos.params.v1beta1.ParamChange.Builder, com.cosmos.params.v1beta1.ParamChangeOrBuilder>(
                changes_,
                ((bitField0_ & 0x00000004) != 0),
                getParentForChildren(),
                isClean());
        changes_ = null;
      }
      return changesBuilder_;
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


    // @@protoc_insertion_point(builder_scope:cosmos.params.v1beta1.ParameterChangeProposal)
  }

  // @@protoc_insertion_point(class_scope:cosmos.params.v1beta1.ParameterChangeProposal)
  private static final com.cosmos.params.v1beta1.ParameterChangeProposal DEFAULT_INSTANCE;
  static {
    DEFAULT_INSTANCE = new com.cosmos.params.v1beta1.ParameterChangeProposal();
  }

  public static com.cosmos.params.v1beta1.ParameterChangeProposal getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }

  private static final com.google.protobuf.Parser<ParameterChangeProposal>
      PARSER = new com.google.protobuf.AbstractParser<ParameterChangeProposal>() {
    @java.lang.Override
    public ParameterChangeProposal parsePartialFrom(
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

  public static com.google.protobuf.Parser<ParameterChangeProposal> parser() {
    return PARSER;
  }

  @java.lang.Override
  public com.google.protobuf.Parser<ParameterChangeProposal> getParserForType() {
    return PARSER;
  }

  @java.lang.Override
  public com.cosmos.params.v1beta1.ParameterChangeProposal getDefaultInstanceForType() {
    return DEFAULT_INSTANCE;
  }

}
