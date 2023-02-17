// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: ibc/core/commitment/v1/commitment.proto

package com.ibc.core.commitment.v1;

/**
 * <pre>
 * MerklePath is the path used to verify commitment proofs, which can be an
 * arbitrary structured object (defined by a commitment type).
 * MerklePath is represented from root-to-leaf
 * </pre>
 *
 * Protobuf type {@code ibc.core.commitment.v1.MerklePath}
 */
public final class MerklePath extends
    com.google.protobuf.GeneratedMessageV3 implements
    // @@protoc_insertion_point(message_implements:ibc.core.commitment.v1.MerklePath)
    MerklePathOrBuilder {
private static final long serialVersionUID = 0L;
  // Use MerklePath.newBuilder() to construct.
  private MerklePath(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
    super(builder);
  }
  private MerklePath() {
    keyPath_ = com.google.protobuf.LazyStringArrayList.EMPTY;
  }

  @java.lang.Override
  @SuppressWarnings({"unused"})
  protected java.lang.Object newInstance(
      UnusedPrivateParameter unused) {
    return new MerklePath();
  }

  public static final com.google.protobuf.Descriptors.Descriptor
      getDescriptor() {
    return com.ibc.core.commitment.v1.CommitmentProto.internal_static_ibc_core_commitment_v1_MerklePath_descriptor;
  }

  @java.lang.Override
  protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internalGetFieldAccessorTable() {
    return com.ibc.core.commitment.v1.CommitmentProto.internal_static_ibc_core_commitment_v1_MerklePath_fieldAccessorTable
        .ensureFieldAccessorsInitialized(
            com.ibc.core.commitment.v1.MerklePath.class, com.ibc.core.commitment.v1.MerklePath.Builder.class);
  }

  public static final int KEY_PATH_FIELD_NUMBER = 1;
  @SuppressWarnings("serial")
  private com.google.protobuf.LazyStringList keyPath_;
  /**
   * <code>repeated string key_path = 1 [json_name = "keyPath", (.gogoproto.moretags) = "yaml:&#92;"key_path&#92;""];</code>
   * @return A list containing the keyPath.
   */
  public com.google.protobuf.ProtocolStringList
      getKeyPathList() {
    return keyPath_;
  }
  /**
   * <code>repeated string key_path = 1 [json_name = "keyPath", (.gogoproto.moretags) = "yaml:&#92;"key_path&#92;""];</code>
   * @return The count of keyPath.
   */
  public int getKeyPathCount() {
    return keyPath_.size();
  }
  /**
   * <code>repeated string key_path = 1 [json_name = "keyPath", (.gogoproto.moretags) = "yaml:&#92;"key_path&#92;""];</code>
   * @param index The index of the element to return.
   * @return The keyPath at the given index.
   */
  public java.lang.String getKeyPath(int index) {
    return keyPath_.get(index);
  }
  /**
   * <code>repeated string key_path = 1 [json_name = "keyPath", (.gogoproto.moretags) = "yaml:&#92;"key_path&#92;""];</code>
   * @param index The index of the value to return.
   * @return The bytes of the keyPath at the given index.
   */
  public com.google.protobuf.ByteString
      getKeyPathBytes(int index) {
    return keyPath_.getByteString(index);
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
    for (int i = 0; i < keyPath_.size(); i++) {
      com.google.protobuf.GeneratedMessageV3.writeString(output, 1, keyPath_.getRaw(i));
    }
    getUnknownFields().writeTo(output);
  }

  @java.lang.Override
  public int getSerializedSize() {
    int size = memoizedSize;
    if (size != -1) return size;

    size = 0;
    {
      int dataSize = 0;
      for (int i = 0; i < keyPath_.size(); i++) {
        dataSize += computeStringSizeNoTag(keyPath_.getRaw(i));
      }
      size += dataSize;
      size += 1 * getKeyPathList().size();
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
    if (!(obj instanceof com.ibc.core.commitment.v1.MerklePath)) {
      return super.equals(obj);
    }
    com.ibc.core.commitment.v1.MerklePath other = (com.ibc.core.commitment.v1.MerklePath) obj;

    if (!getKeyPathList()
        .equals(other.getKeyPathList())) return false;
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
    if (getKeyPathCount() > 0) {
      hash = (37 * hash) + KEY_PATH_FIELD_NUMBER;
      hash = (53 * hash) + getKeyPathList().hashCode();
    }
    hash = (29 * hash) + getUnknownFields().hashCode();
    memoizedHashCode = hash;
    return hash;
  }

  public static com.ibc.core.commitment.v1.MerklePath parseFrom(
      java.nio.ByteBuffer data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static com.ibc.core.commitment.v1.MerklePath parseFrom(
      java.nio.ByteBuffer data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static com.ibc.core.commitment.v1.MerklePath parseFrom(
      com.google.protobuf.ByteString data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static com.ibc.core.commitment.v1.MerklePath parseFrom(
      com.google.protobuf.ByteString data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static com.ibc.core.commitment.v1.MerklePath parseFrom(byte[] data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static com.ibc.core.commitment.v1.MerklePath parseFrom(
      byte[] data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static com.ibc.core.commitment.v1.MerklePath parseFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static com.ibc.core.commitment.v1.MerklePath parseFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input, extensionRegistry);
  }
  public static com.ibc.core.commitment.v1.MerklePath parseDelimitedFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input);
  }
  public static com.ibc.core.commitment.v1.MerklePath parseDelimitedFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
  }
  public static com.ibc.core.commitment.v1.MerklePath parseFrom(
      com.google.protobuf.CodedInputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static com.ibc.core.commitment.v1.MerklePath parseFrom(
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
  public static Builder newBuilder(com.ibc.core.commitment.v1.MerklePath prototype) {
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
   * MerklePath is the path used to verify commitment proofs, which can be an
   * arbitrary structured object (defined by a commitment type).
   * MerklePath is represented from root-to-leaf
   * </pre>
   *
   * Protobuf type {@code ibc.core.commitment.v1.MerklePath}
   */
  public static final class Builder extends
      com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
      // @@protoc_insertion_point(builder_implements:ibc.core.commitment.v1.MerklePath)
      com.ibc.core.commitment.v1.MerklePathOrBuilder {
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return com.ibc.core.commitment.v1.CommitmentProto.internal_static_ibc_core_commitment_v1_MerklePath_descriptor;
    }

    @java.lang.Override
    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return com.ibc.core.commitment.v1.CommitmentProto.internal_static_ibc_core_commitment_v1_MerklePath_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              com.ibc.core.commitment.v1.MerklePath.class, com.ibc.core.commitment.v1.MerklePath.Builder.class);
    }

    // Construct using com.ibc.core.commitment.v1.MerklePath.newBuilder()
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
      keyPath_ = com.google.protobuf.LazyStringArrayList.EMPTY;
      bitField0_ = (bitField0_ & ~0x00000001);
      return this;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.Descriptor
        getDescriptorForType() {
      return com.ibc.core.commitment.v1.CommitmentProto.internal_static_ibc_core_commitment_v1_MerklePath_descriptor;
    }

    @java.lang.Override
    public com.ibc.core.commitment.v1.MerklePath getDefaultInstanceForType() {
      return com.ibc.core.commitment.v1.MerklePath.getDefaultInstance();
    }

    @java.lang.Override
    public com.ibc.core.commitment.v1.MerklePath build() {
      com.ibc.core.commitment.v1.MerklePath result = buildPartial();
      if (!result.isInitialized()) {
        throw newUninitializedMessageException(result);
      }
      return result;
    }

    @java.lang.Override
    public com.ibc.core.commitment.v1.MerklePath buildPartial() {
      com.ibc.core.commitment.v1.MerklePath result = new com.ibc.core.commitment.v1.MerklePath(this);
      buildPartialRepeatedFields(result);
      if (bitField0_ != 0) { buildPartial0(result); }
      onBuilt();
      return result;
    }

    private void buildPartialRepeatedFields(com.ibc.core.commitment.v1.MerklePath result) {
      if (((bitField0_ & 0x00000001) != 0)) {
        keyPath_ = keyPath_.getUnmodifiableView();
        bitField0_ = (bitField0_ & ~0x00000001);
      }
      result.keyPath_ = keyPath_;
    }

    private void buildPartial0(com.ibc.core.commitment.v1.MerklePath result) {
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
      if (other instanceof com.ibc.core.commitment.v1.MerklePath) {
        return mergeFrom((com.ibc.core.commitment.v1.MerklePath)other);
      } else {
        super.mergeFrom(other);
        return this;
      }
    }

    public Builder mergeFrom(com.ibc.core.commitment.v1.MerklePath other) {
      if (other == com.ibc.core.commitment.v1.MerklePath.getDefaultInstance()) return this;
      if (!other.keyPath_.isEmpty()) {
        if (keyPath_.isEmpty()) {
          keyPath_ = other.keyPath_;
          bitField0_ = (bitField0_ & ~0x00000001);
        } else {
          ensureKeyPathIsMutable();
          keyPath_.addAll(other.keyPath_);
        }
        onChanged();
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
              java.lang.String s = input.readStringRequireUtf8();
              ensureKeyPathIsMutable();
              keyPath_.add(s);
              break;
            } // case 10
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

    private com.google.protobuf.LazyStringList keyPath_ = com.google.protobuf.LazyStringArrayList.EMPTY;
    private void ensureKeyPathIsMutable() {
      if (!((bitField0_ & 0x00000001) != 0)) {
        keyPath_ = new com.google.protobuf.LazyStringArrayList(keyPath_);
        bitField0_ |= 0x00000001;
       }
    }
    /**
     * <code>repeated string key_path = 1 [json_name = "keyPath", (.gogoproto.moretags) = "yaml:&#92;"key_path&#92;""];</code>
     * @return A list containing the keyPath.
     */
    public com.google.protobuf.ProtocolStringList
        getKeyPathList() {
      return keyPath_.getUnmodifiableView();
    }
    /**
     * <code>repeated string key_path = 1 [json_name = "keyPath", (.gogoproto.moretags) = "yaml:&#92;"key_path&#92;""];</code>
     * @return The count of keyPath.
     */
    public int getKeyPathCount() {
      return keyPath_.size();
    }
    /**
     * <code>repeated string key_path = 1 [json_name = "keyPath", (.gogoproto.moretags) = "yaml:&#92;"key_path&#92;""];</code>
     * @param index The index of the element to return.
     * @return The keyPath at the given index.
     */
    public java.lang.String getKeyPath(int index) {
      return keyPath_.get(index);
    }
    /**
     * <code>repeated string key_path = 1 [json_name = "keyPath", (.gogoproto.moretags) = "yaml:&#92;"key_path&#92;""];</code>
     * @param index The index of the value to return.
     * @return The bytes of the keyPath at the given index.
     */
    public com.google.protobuf.ByteString
        getKeyPathBytes(int index) {
      return keyPath_.getByteString(index);
    }
    /**
     * <code>repeated string key_path = 1 [json_name = "keyPath", (.gogoproto.moretags) = "yaml:&#92;"key_path&#92;""];</code>
     * @param index The index to set the value at.
     * @param value The keyPath to set.
     * @return This builder for chaining.
     */
    public Builder setKeyPath(
        int index, java.lang.String value) {
      if (value == null) { throw new NullPointerException(); }
      ensureKeyPathIsMutable();
      keyPath_.set(index, value);
      onChanged();
      return this;
    }
    /**
     * <code>repeated string key_path = 1 [json_name = "keyPath", (.gogoproto.moretags) = "yaml:&#92;"key_path&#92;""];</code>
     * @param value The keyPath to add.
     * @return This builder for chaining.
     */
    public Builder addKeyPath(
        java.lang.String value) {
      if (value == null) { throw new NullPointerException(); }
      ensureKeyPathIsMutable();
      keyPath_.add(value);
      onChanged();
      return this;
    }
    /**
     * <code>repeated string key_path = 1 [json_name = "keyPath", (.gogoproto.moretags) = "yaml:&#92;"key_path&#92;""];</code>
     * @param values The keyPath to add.
     * @return This builder for chaining.
     */
    public Builder addAllKeyPath(
        java.lang.Iterable<java.lang.String> values) {
      ensureKeyPathIsMutable();
      com.google.protobuf.AbstractMessageLite.Builder.addAll(
          values, keyPath_);
      onChanged();
      return this;
    }
    /**
     * <code>repeated string key_path = 1 [json_name = "keyPath", (.gogoproto.moretags) = "yaml:&#92;"key_path&#92;""];</code>
     * @return This builder for chaining.
     */
    public Builder clearKeyPath() {
      keyPath_ = com.google.protobuf.LazyStringArrayList.EMPTY;
      bitField0_ = (bitField0_ & ~0x00000001);
      onChanged();
      return this;
    }
    /**
     * <code>repeated string key_path = 1 [json_name = "keyPath", (.gogoproto.moretags) = "yaml:&#92;"key_path&#92;""];</code>
     * @param value The bytes of the keyPath to add.
     * @return This builder for chaining.
     */
    public Builder addKeyPathBytes(
        com.google.protobuf.ByteString value) {
      if (value == null) { throw new NullPointerException(); }
      checkByteStringIsUtf8(value);
      ensureKeyPathIsMutable();
      keyPath_.add(value);
      onChanged();
      return this;
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


    // @@protoc_insertion_point(builder_scope:ibc.core.commitment.v1.MerklePath)
  }

  // @@protoc_insertion_point(class_scope:ibc.core.commitment.v1.MerklePath)
  private static final com.ibc.core.commitment.v1.MerklePath DEFAULT_INSTANCE;
  static {
    DEFAULT_INSTANCE = new com.ibc.core.commitment.v1.MerklePath();
  }

  public static com.ibc.core.commitment.v1.MerklePath getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }

  private static final com.google.protobuf.Parser<MerklePath>
      PARSER = new com.google.protobuf.AbstractParser<MerklePath>() {
    @java.lang.Override
    public MerklePath parsePartialFrom(
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

  public static com.google.protobuf.Parser<MerklePath> parser() {
    return PARSER;
  }

  @java.lang.Override
  public com.google.protobuf.Parser<MerklePath> getParserForType() {
    return PARSER;
  }

  @java.lang.Override
  public com.ibc.core.commitment.v1.MerklePath getDefaultInstanceForType() {
    return DEFAULT_INSTANCE;
  }

}

