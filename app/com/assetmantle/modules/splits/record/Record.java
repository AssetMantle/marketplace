// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: splits/record/record.proto

package com.assetmantle.modules.splits.record;

/**
 * Protobuf type {@code assetmantle.modules.splits.record.Record}
 */
public final class Record extends
    com.google.protobuf.GeneratedMessageV3 implements
    // @@protoc_insertion_point(message_implements:assetmantle.modules.splits.record.Record)
    RecordOrBuilder {
private static final long serialVersionUID = 0L;
  // Use Record.newBuilder() to construct.
  private Record(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
    super(builder);
  }
  private Record() {
  }

  @java.lang.Override
  @SuppressWarnings({"unused"})
  protected java.lang.Object newInstance(
      UnusedPrivateParameter unused) {
    return new Record();
  }

  public static final com.google.protobuf.Descriptors.Descriptor
      getDescriptor() {
    return com.assetmantle.modules.splits.record.RecordProto.internal_static_assetmantle_modules_splits_record_Record_descriptor;
  }

  @java.lang.Override
  protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internalGetFieldAccessorTable() {
    return com.assetmantle.modules.splits.record.RecordProto.internal_static_assetmantle_modules_splits_record_Record_fieldAccessorTable
        .ensureFieldAccessorsInitialized(
            com.assetmantle.modules.splits.record.Record.class, com.assetmantle.modules.splits.record.Record.Builder.class);
  }

  public static final int KEY_FIELD_NUMBER = 1;
  private com.assetmantle.modules.splits.key.Key key_;
  /**
   * <code>.assetmantle.modules.splits.key.Key key = 1 [json_name = "key"];</code>
   * @return Whether the key field is set.
   */
  @java.lang.Override
  public boolean hasKey() {
    return key_ != null;
  }
  /**
   * <code>.assetmantle.modules.splits.key.Key key = 1 [json_name = "key"];</code>
   * @return The key.
   */
  @java.lang.Override
  public com.assetmantle.modules.splits.key.Key getKey() {
    return key_ == null ? com.assetmantle.modules.splits.key.Key.getDefaultInstance() : key_;
  }
  /**
   * <code>.assetmantle.modules.splits.key.Key key = 1 [json_name = "key"];</code>
   */
  @java.lang.Override
  public com.assetmantle.modules.splits.key.KeyOrBuilder getKeyOrBuilder() {
    return key_ == null ? com.assetmantle.modules.splits.key.Key.getDefaultInstance() : key_;
  }

  public static final int MAPPABLE_FIELD_NUMBER = 2;
  private com.assetmantle.modules.splits.mappable.Mappable mappable_;
  /**
   * <code>.assetmantle.modules.splits.mappable.Mappable mappable = 2 [json_name = "mappable"];</code>
   * @return Whether the mappable field is set.
   */
  @java.lang.Override
  public boolean hasMappable() {
    return mappable_ != null;
  }
  /**
   * <code>.assetmantle.modules.splits.mappable.Mappable mappable = 2 [json_name = "mappable"];</code>
   * @return The mappable.
   */
  @java.lang.Override
  public com.assetmantle.modules.splits.mappable.Mappable getMappable() {
    return mappable_ == null ? com.assetmantle.modules.splits.mappable.Mappable.getDefaultInstance() : mappable_;
  }
  /**
   * <code>.assetmantle.modules.splits.mappable.Mappable mappable = 2 [json_name = "mappable"];</code>
   */
  @java.lang.Override
  public com.assetmantle.modules.splits.mappable.MappableOrBuilder getMappableOrBuilder() {
    return mappable_ == null ? com.assetmantle.modules.splits.mappable.Mappable.getDefaultInstance() : mappable_;
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
    if (key_ != null) {
      output.writeMessage(1, getKey());
    }
    if (mappable_ != null) {
      output.writeMessage(2, getMappable());
    }
    getUnknownFields().writeTo(output);
  }

  @java.lang.Override
  public int getSerializedSize() {
    int size = memoizedSize;
    if (size != -1) return size;

    size = 0;
    if (key_ != null) {
      size += com.google.protobuf.CodedOutputStream
        .computeMessageSize(1, getKey());
    }
    if (mappable_ != null) {
      size += com.google.protobuf.CodedOutputStream
        .computeMessageSize(2, getMappable());
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
    if (!(obj instanceof com.assetmantle.modules.splits.record.Record)) {
      return super.equals(obj);
    }
    com.assetmantle.modules.splits.record.Record other = (com.assetmantle.modules.splits.record.Record) obj;

    if (hasKey() != other.hasKey()) return false;
    if (hasKey()) {
      if (!getKey()
          .equals(other.getKey())) return false;
    }
    if (hasMappable() != other.hasMappable()) return false;
    if (hasMappable()) {
      if (!getMappable()
          .equals(other.getMappable())) return false;
    }
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
    if (hasKey()) {
      hash = (37 * hash) + KEY_FIELD_NUMBER;
      hash = (53 * hash) + getKey().hashCode();
    }
    if (hasMappable()) {
      hash = (37 * hash) + MAPPABLE_FIELD_NUMBER;
      hash = (53 * hash) + getMappable().hashCode();
    }
    hash = (29 * hash) + getUnknownFields().hashCode();
    memoizedHashCode = hash;
    return hash;
  }

  public static com.assetmantle.modules.splits.record.Record parseFrom(
      java.nio.ByteBuffer data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static com.assetmantle.modules.splits.record.Record parseFrom(
      java.nio.ByteBuffer data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static com.assetmantle.modules.splits.record.Record parseFrom(
      com.google.protobuf.ByteString data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static com.assetmantle.modules.splits.record.Record parseFrom(
      com.google.protobuf.ByteString data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static com.assetmantle.modules.splits.record.Record parseFrom(byte[] data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static com.assetmantle.modules.splits.record.Record parseFrom(
      byte[] data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static com.assetmantle.modules.splits.record.Record parseFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static com.assetmantle.modules.splits.record.Record parseFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input, extensionRegistry);
  }
  public static com.assetmantle.modules.splits.record.Record parseDelimitedFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input);
  }
  public static com.assetmantle.modules.splits.record.Record parseDelimitedFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
  }
  public static com.assetmantle.modules.splits.record.Record parseFrom(
      com.google.protobuf.CodedInputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static com.assetmantle.modules.splits.record.Record parseFrom(
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
  public static Builder newBuilder(com.assetmantle.modules.splits.record.Record prototype) {
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
   * Protobuf type {@code assetmantle.modules.splits.record.Record}
   */
  public static final class Builder extends
      com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
      // @@protoc_insertion_point(builder_implements:assetmantle.modules.splits.record.Record)
      com.assetmantle.modules.splits.record.RecordOrBuilder {
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return com.assetmantle.modules.splits.record.RecordProto.internal_static_assetmantle_modules_splits_record_Record_descriptor;
    }

    @java.lang.Override
    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return com.assetmantle.modules.splits.record.RecordProto.internal_static_assetmantle_modules_splits_record_Record_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              com.assetmantle.modules.splits.record.Record.class, com.assetmantle.modules.splits.record.Record.Builder.class);
    }

    // Construct using com.assetmantle.modules.splits.record.Record.newBuilder()
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
      key_ = null;
      if (keyBuilder_ != null) {
        keyBuilder_.dispose();
        keyBuilder_ = null;
      }
      mappable_ = null;
      if (mappableBuilder_ != null) {
        mappableBuilder_.dispose();
        mappableBuilder_ = null;
      }
      return this;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.Descriptor
        getDescriptorForType() {
      return com.assetmantle.modules.splits.record.RecordProto.internal_static_assetmantle_modules_splits_record_Record_descriptor;
    }

    @java.lang.Override
    public com.assetmantle.modules.splits.record.Record getDefaultInstanceForType() {
      return com.assetmantle.modules.splits.record.Record.getDefaultInstance();
    }

    @java.lang.Override
    public com.assetmantle.modules.splits.record.Record build() {
      com.assetmantle.modules.splits.record.Record result = buildPartial();
      if (!result.isInitialized()) {
        throw newUninitializedMessageException(result);
      }
      return result;
    }

    @java.lang.Override
    public com.assetmantle.modules.splits.record.Record buildPartial() {
      com.assetmantle.modules.splits.record.Record result = new com.assetmantle.modules.splits.record.Record(this);
      if (bitField0_ != 0) { buildPartial0(result); }
      onBuilt();
      return result;
    }

    private void buildPartial0(com.assetmantle.modules.splits.record.Record result) {
      int from_bitField0_ = bitField0_;
      if (((from_bitField0_ & 0x00000001) != 0)) {
        result.key_ = keyBuilder_ == null
            ? key_
            : keyBuilder_.build();
      }
      if (((from_bitField0_ & 0x00000002) != 0)) {
        result.mappable_ = mappableBuilder_ == null
            ? mappable_
            : mappableBuilder_.build();
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
      if (other instanceof com.assetmantle.modules.splits.record.Record) {
        return mergeFrom((com.assetmantle.modules.splits.record.Record)other);
      } else {
        super.mergeFrom(other);
        return this;
      }
    }

    public Builder mergeFrom(com.assetmantle.modules.splits.record.Record other) {
      if (other == com.assetmantle.modules.splits.record.Record.getDefaultInstance()) return this;
      if (other.hasKey()) {
        mergeKey(other.getKey());
      }
      if (other.hasMappable()) {
        mergeMappable(other.getMappable());
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
              input.readMessage(
                  getKeyFieldBuilder().getBuilder(),
                  extensionRegistry);
              bitField0_ |= 0x00000001;
              break;
            } // case 10
            case 18: {
              input.readMessage(
                  getMappableFieldBuilder().getBuilder(),
                  extensionRegistry);
              bitField0_ |= 0x00000002;
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

    private com.assetmantle.modules.splits.key.Key key_;
    private com.google.protobuf.SingleFieldBuilderV3<
        com.assetmantle.modules.splits.key.Key, com.assetmantle.modules.splits.key.Key.Builder, com.assetmantle.modules.splits.key.KeyOrBuilder> keyBuilder_;
    /**
     * <code>.assetmantle.modules.splits.key.Key key = 1 [json_name = "key"];</code>
     * @return Whether the key field is set.
     */
    public boolean hasKey() {
      return ((bitField0_ & 0x00000001) != 0);
    }
    /**
     * <code>.assetmantle.modules.splits.key.Key key = 1 [json_name = "key"];</code>
     * @return The key.
     */
    public com.assetmantle.modules.splits.key.Key getKey() {
      if (keyBuilder_ == null) {
        return key_ == null ? com.assetmantle.modules.splits.key.Key.getDefaultInstance() : key_;
      } else {
        return keyBuilder_.getMessage();
      }
    }
    /**
     * <code>.assetmantle.modules.splits.key.Key key = 1 [json_name = "key"];</code>
     */
    public Builder setKey(com.assetmantle.modules.splits.key.Key value) {
      if (keyBuilder_ == null) {
        if (value == null) {
          throw new NullPointerException();
        }
        key_ = value;
      } else {
        keyBuilder_.setMessage(value);
      }
      bitField0_ |= 0x00000001;
      onChanged();
      return this;
    }
    /**
     * <code>.assetmantle.modules.splits.key.Key key = 1 [json_name = "key"];</code>
     */
    public Builder setKey(
        com.assetmantle.modules.splits.key.Key.Builder builderForValue) {
      if (keyBuilder_ == null) {
        key_ = builderForValue.build();
      } else {
        keyBuilder_.setMessage(builderForValue.build());
      }
      bitField0_ |= 0x00000001;
      onChanged();
      return this;
    }
    /**
     * <code>.assetmantle.modules.splits.key.Key key = 1 [json_name = "key"];</code>
     */
    public Builder mergeKey(com.assetmantle.modules.splits.key.Key value) {
      if (keyBuilder_ == null) {
        if (((bitField0_ & 0x00000001) != 0) &&
          key_ != null &&
          key_ != com.assetmantle.modules.splits.key.Key.getDefaultInstance()) {
          getKeyBuilder().mergeFrom(value);
        } else {
          key_ = value;
        }
      } else {
        keyBuilder_.mergeFrom(value);
      }
      bitField0_ |= 0x00000001;
      onChanged();
      return this;
    }
    /**
     * <code>.assetmantle.modules.splits.key.Key key = 1 [json_name = "key"];</code>
     */
    public Builder clearKey() {
      bitField0_ = (bitField0_ & ~0x00000001);
      key_ = null;
      if (keyBuilder_ != null) {
        keyBuilder_.dispose();
        keyBuilder_ = null;
      }
      onChanged();
      return this;
    }
    /**
     * <code>.assetmantle.modules.splits.key.Key key = 1 [json_name = "key"];</code>
     */
    public com.assetmantle.modules.splits.key.Key.Builder getKeyBuilder() {
      bitField0_ |= 0x00000001;
      onChanged();
      return getKeyFieldBuilder().getBuilder();
    }
    /**
     * <code>.assetmantle.modules.splits.key.Key key = 1 [json_name = "key"];</code>
     */
    public com.assetmantle.modules.splits.key.KeyOrBuilder getKeyOrBuilder() {
      if (keyBuilder_ != null) {
        return keyBuilder_.getMessageOrBuilder();
      } else {
        return key_ == null ?
            com.assetmantle.modules.splits.key.Key.getDefaultInstance() : key_;
      }
    }
    /**
     * <code>.assetmantle.modules.splits.key.Key key = 1 [json_name = "key"];</code>
     */
    private com.google.protobuf.SingleFieldBuilderV3<
        com.assetmantle.modules.splits.key.Key, com.assetmantle.modules.splits.key.Key.Builder, com.assetmantle.modules.splits.key.KeyOrBuilder> 
        getKeyFieldBuilder() {
      if (keyBuilder_ == null) {
        keyBuilder_ = new com.google.protobuf.SingleFieldBuilderV3<
            com.assetmantle.modules.splits.key.Key, com.assetmantle.modules.splits.key.Key.Builder, com.assetmantle.modules.splits.key.KeyOrBuilder>(
                getKey(),
                getParentForChildren(),
                isClean());
        key_ = null;
      }
      return keyBuilder_;
    }

    private com.assetmantle.modules.splits.mappable.Mappable mappable_;
    private com.google.protobuf.SingleFieldBuilderV3<
        com.assetmantle.modules.splits.mappable.Mappable, com.assetmantle.modules.splits.mappable.Mappable.Builder, com.assetmantle.modules.splits.mappable.MappableOrBuilder> mappableBuilder_;
    /**
     * <code>.assetmantle.modules.splits.mappable.Mappable mappable = 2 [json_name = "mappable"];</code>
     * @return Whether the mappable field is set.
     */
    public boolean hasMappable() {
      return ((bitField0_ & 0x00000002) != 0);
    }
    /**
     * <code>.assetmantle.modules.splits.mappable.Mappable mappable = 2 [json_name = "mappable"];</code>
     * @return The mappable.
     */
    public com.assetmantle.modules.splits.mappable.Mappable getMappable() {
      if (mappableBuilder_ == null) {
        return mappable_ == null ? com.assetmantle.modules.splits.mappable.Mappable.getDefaultInstance() : mappable_;
      } else {
        return mappableBuilder_.getMessage();
      }
    }
    /**
     * <code>.assetmantle.modules.splits.mappable.Mappable mappable = 2 [json_name = "mappable"];</code>
     */
    public Builder setMappable(com.assetmantle.modules.splits.mappable.Mappable value) {
      if (mappableBuilder_ == null) {
        if (value == null) {
          throw new NullPointerException();
        }
        mappable_ = value;
      } else {
        mappableBuilder_.setMessage(value);
      }
      bitField0_ |= 0x00000002;
      onChanged();
      return this;
    }
    /**
     * <code>.assetmantle.modules.splits.mappable.Mappable mappable = 2 [json_name = "mappable"];</code>
     */
    public Builder setMappable(
        com.assetmantle.modules.splits.mappable.Mappable.Builder builderForValue) {
      if (mappableBuilder_ == null) {
        mappable_ = builderForValue.build();
      } else {
        mappableBuilder_.setMessage(builderForValue.build());
      }
      bitField0_ |= 0x00000002;
      onChanged();
      return this;
    }
    /**
     * <code>.assetmantle.modules.splits.mappable.Mappable mappable = 2 [json_name = "mappable"];</code>
     */
    public Builder mergeMappable(com.assetmantle.modules.splits.mappable.Mappable value) {
      if (mappableBuilder_ == null) {
        if (((bitField0_ & 0x00000002) != 0) &&
          mappable_ != null &&
          mappable_ != com.assetmantle.modules.splits.mappable.Mappable.getDefaultInstance()) {
          getMappableBuilder().mergeFrom(value);
        } else {
          mappable_ = value;
        }
      } else {
        mappableBuilder_.mergeFrom(value);
      }
      bitField0_ |= 0x00000002;
      onChanged();
      return this;
    }
    /**
     * <code>.assetmantle.modules.splits.mappable.Mappable mappable = 2 [json_name = "mappable"];</code>
     */
    public Builder clearMappable() {
      bitField0_ = (bitField0_ & ~0x00000002);
      mappable_ = null;
      if (mappableBuilder_ != null) {
        mappableBuilder_.dispose();
        mappableBuilder_ = null;
      }
      onChanged();
      return this;
    }
    /**
     * <code>.assetmantle.modules.splits.mappable.Mappable mappable = 2 [json_name = "mappable"];</code>
     */
    public com.assetmantle.modules.splits.mappable.Mappable.Builder getMappableBuilder() {
      bitField0_ |= 0x00000002;
      onChanged();
      return getMappableFieldBuilder().getBuilder();
    }
    /**
     * <code>.assetmantle.modules.splits.mappable.Mappable mappable = 2 [json_name = "mappable"];</code>
     */
    public com.assetmantle.modules.splits.mappable.MappableOrBuilder getMappableOrBuilder() {
      if (mappableBuilder_ != null) {
        return mappableBuilder_.getMessageOrBuilder();
      } else {
        return mappable_ == null ?
            com.assetmantle.modules.splits.mappable.Mappable.getDefaultInstance() : mappable_;
      }
    }
    /**
     * <code>.assetmantle.modules.splits.mappable.Mappable mappable = 2 [json_name = "mappable"];</code>
     */
    private com.google.protobuf.SingleFieldBuilderV3<
        com.assetmantle.modules.splits.mappable.Mappable, com.assetmantle.modules.splits.mappable.Mappable.Builder, com.assetmantle.modules.splits.mappable.MappableOrBuilder> 
        getMappableFieldBuilder() {
      if (mappableBuilder_ == null) {
        mappableBuilder_ = new com.google.protobuf.SingleFieldBuilderV3<
            com.assetmantle.modules.splits.mappable.Mappable, com.assetmantle.modules.splits.mappable.Mappable.Builder, com.assetmantle.modules.splits.mappable.MappableOrBuilder>(
                getMappable(),
                getParentForChildren(),
                isClean());
        mappable_ = null;
      }
      return mappableBuilder_;
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


    // @@protoc_insertion_point(builder_scope:assetmantle.modules.splits.record.Record)
  }

  // @@protoc_insertion_point(class_scope:assetmantle.modules.splits.record.Record)
  private static final com.assetmantle.modules.splits.record.Record DEFAULT_INSTANCE;
  static {
    DEFAULT_INSTANCE = new com.assetmantle.modules.splits.record.Record();
  }

  public static com.assetmantle.modules.splits.record.Record getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }

  private static final com.google.protobuf.Parser<Record>
      PARSER = new com.google.protobuf.AbstractParser<Record>() {
    @java.lang.Override
    public Record parsePartialFrom(
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

  public static com.google.protobuf.Parser<Record> parser() {
    return PARSER;
  }

  @java.lang.Override
  public com.google.protobuf.Parser<Record> getParserForType() {
    return PARSER;
  }

  @java.lang.Override
  public com.assetmantle.modules.splits.record.Record getDefaultInstanceForType() {
    return DEFAULT_INSTANCE;
  }

}
