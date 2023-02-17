// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: cosmos/slashing/v1beta1/genesis.proto

package com.cosmos.slashing.v1beta1;

/**
 * <pre>
 * MissedBlock contains height and missed status as boolean.
 * </pre>
 *
 * Protobuf type {@code cosmos.slashing.v1beta1.MissedBlock}
 */
public final class MissedBlock extends
    com.google.protobuf.GeneratedMessageV3 implements
    // @@protoc_insertion_point(message_implements:cosmos.slashing.v1beta1.MissedBlock)
    MissedBlockOrBuilder {
private static final long serialVersionUID = 0L;
  // Use MissedBlock.newBuilder() to construct.
  private MissedBlock(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
    super(builder);
  }
  private MissedBlock() {
  }

  @java.lang.Override
  @SuppressWarnings({"unused"})
  protected java.lang.Object newInstance(
      UnusedPrivateParameter unused) {
    return new MissedBlock();
  }

  public static final com.google.protobuf.Descriptors.Descriptor
      getDescriptor() {
    return com.cosmos.slashing.v1beta1.GenesisProto.internal_static_cosmos_slashing_v1beta1_MissedBlock_descriptor;
  }

  @java.lang.Override
  protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internalGetFieldAccessorTable() {
    return com.cosmos.slashing.v1beta1.GenesisProto.internal_static_cosmos_slashing_v1beta1_MissedBlock_fieldAccessorTable
        .ensureFieldAccessorsInitialized(
            com.cosmos.slashing.v1beta1.MissedBlock.class, com.cosmos.slashing.v1beta1.MissedBlock.Builder.class);
  }

  public static final int INDEX_FIELD_NUMBER = 1;
  private long index_ = 0L;
  /**
   * <pre>
   * index is the height at which the block was missed.
   * </pre>
   *
   * <code>int64 index = 1 [json_name = "index"];</code>
   * @return The index.
   */
  @java.lang.Override
  public long getIndex() {
    return index_;
  }

  public static final int MISSED_FIELD_NUMBER = 2;
  private boolean missed_ = false;
  /**
   * <pre>
   * missed is the missed status.
   * </pre>
   *
   * <code>bool missed = 2 [json_name = "missed"];</code>
   * @return The missed.
   */
  @java.lang.Override
  public boolean getMissed() {
    return missed_;
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
    if (index_ != 0L) {
      output.writeInt64(1, index_);
    }
    if (missed_ != false) {
      output.writeBool(2, missed_);
    }
    getUnknownFields().writeTo(output);
  }

  @java.lang.Override
  public int getSerializedSize() {
    int size = memoizedSize;
    if (size != -1) return size;

    size = 0;
    if (index_ != 0L) {
      size += com.google.protobuf.CodedOutputStream
        .computeInt64Size(1, index_);
    }
    if (missed_ != false) {
      size += com.google.protobuf.CodedOutputStream
        .computeBoolSize(2, missed_);
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
    if (!(obj instanceof com.cosmos.slashing.v1beta1.MissedBlock)) {
      return super.equals(obj);
    }
    com.cosmos.slashing.v1beta1.MissedBlock other = (com.cosmos.slashing.v1beta1.MissedBlock) obj;

    if (getIndex()
        != other.getIndex()) return false;
    if (getMissed()
        != other.getMissed()) return false;
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
    hash = (37 * hash) + INDEX_FIELD_NUMBER;
    hash = (53 * hash) + com.google.protobuf.Internal.hashLong(
        getIndex());
    hash = (37 * hash) + MISSED_FIELD_NUMBER;
    hash = (53 * hash) + com.google.protobuf.Internal.hashBoolean(
        getMissed());
    hash = (29 * hash) + getUnknownFields().hashCode();
    memoizedHashCode = hash;
    return hash;
  }

  public static com.cosmos.slashing.v1beta1.MissedBlock parseFrom(
      java.nio.ByteBuffer data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static com.cosmos.slashing.v1beta1.MissedBlock parseFrom(
      java.nio.ByteBuffer data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static com.cosmos.slashing.v1beta1.MissedBlock parseFrom(
      com.google.protobuf.ByteString data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static com.cosmos.slashing.v1beta1.MissedBlock parseFrom(
      com.google.protobuf.ByteString data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static com.cosmos.slashing.v1beta1.MissedBlock parseFrom(byte[] data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static com.cosmos.slashing.v1beta1.MissedBlock parseFrom(
      byte[] data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static com.cosmos.slashing.v1beta1.MissedBlock parseFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static com.cosmos.slashing.v1beta1.MissedBlock parseFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input, extensionRegistry);
  }
  public static com.cosmos.slashing.v1beta1.MissedBlock parseDelimitedFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input);
  }
  public static com.cosmos.slashing.v1beta1.MissedBlock parseDelimitedFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
  }
  public static com.cosmos.slashing.v1beta1.MissedBlock parseFrom(
      com.google.protobuf.CodedInputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static com.cosmos.slashing.v1beta1.MissedBlock parseFrom(
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
  public static Builder newBuilder(com.cosmos.slashing.v1beta1.MissedBlock prototype) {
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
   * MissedBlock contains height and missed status as boolean.
   * </pre>
   *
   * Protobuf type {@code cosmos.slashing.v1beta1.MissedBlock}
   */
  public static final class Builder extends
      com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
      // @@protoc_insertion_point(builder_implements:cosmos.slashing.v1beta1.MissedBlock)
      com.cosmos.slashing.v1beta1.MissedBlockOrBuilder {
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return com.cosmos.slashing.v1beta1.GenesisProto.internal_static_cosmos_slashing_v1beta1_MissedBlock_descriptor;
    }

    @java.lang.Override
    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return com.cosmos.slashing.v1beta1.GenesisProto.internal_static_cosmos_slashing_v1beta1_MissedBlock_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              com.cosmos.slashing.v1beta1.MissedBlock.class, com.cosmos.slashing.v1beta1.MissedBlock.Builder.class);
    }

    // Construct using com.cosmos.slashing.v1beta1.MissedBlock.newBuilder()
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
      index_ = 0L;
      missed_ = false;
      return this;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.Descriptor
        getDescriptorForType() {
      return com.cosmos.slashing.v1beta1.GenesisProto.internal_static_cosmos_slashing_v1beta1_MissedBlock_descriptor;
    }

    @java.lang.Override
    public com.cosmos.slashing.v1beta1.MissedBlock getDefaultInstanceForType() {
      return com.cosmos.slashing.v1beta1.MissedBlock.getDefaultInstance();
    }

    @java.lang.Override
    public com.cosmos.slashing.v1beta1.MissedBlock build() {
      com.cosmos.slashing.v1beta1.MissedBlock result = buildPartial();
      if (!result.isInitialized()) {
        throw newUninitializedMessageException(result);
      }
      return result;
    }

    @java.lang.Override
    public com.cosmos.slashing.v1beta1.MissedBlock buildPartial() {
      com.cosmos.slashing.v1beta1.MissedBlock result = new com.cosmos.slashing.v1beta1.MissedBlock(this);
      if (bitField0_ != 0) { buildPartial0(result); }
      onBuilt();
      return result;
    }

    private void buildPartial0(com.cosmos.slashing.v1beta1.MissedBlock result) {
      int from_bitField0_ = bitField0_;
      if (((from_bitField0_ & 0x00000001) != 0)) {
        result.index_ = index_;
      }
      if (((from_bitField0_ & 0x00000002) != 0)) {
        result.missed_ = missed_;
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
      if (other instanceof com.cosmos.slashing.v1beta1.MissedBlock) {
        return mergeFrom((com.cosmos.slashing.v1beta1.MissedBlock)other);
      } else {
        super.mergeFrom(other);
        return this;
      }
    }

    public Builder mergeFrom(com.cosmos.slashing.v1beta1.MissedBlock other) {
      if (other == com.cosmos.slashing.v1beta1.MissedBlock.getDefaultInstance()) return this;
      if (other.getIndex() != 0L) {
        setIndex(other.getIndex());
      }
      if (other.getMissed() != false) {
        setMissed(other.getMissed());
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
            case 8: {
              index_ = input.readInt64();
              bitField0_ |= 0x00000001;
              break;
            } // case 8
            case 16: {
              missed_ = input.readBool();
              bitField0_ |= 0x00000002;
              break;
            } // case 16
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

    private long index_ ;
    /**
     * <pre>
     * index is the height at which the block was missed.
     * </pre>
     *
     * <code>int64 index = 1 [json_name = "index"];</code>
     * @return The index.
     */
    @java.lang.Override
    public long getIndex() {
      return index_;
    }
    /**
     * <pre>
     * index is the height at which the block was missed.
     * </pre>
     *
     * <code>int64 index = 1 [json_name = "index"];</code>
     * @param value The index to set.
     * @return This builder for chaining.
     */
    public Builder setIndex(long value) {

      index_ = value;
      bitField0_ |= 0x00000001;
      onChanged();
      return this;
    }
    /**
     * <pre>
     * index is the height at which the block was missed.
     * </pre>
     *
     * <code>int64 index = 1 [json_name = "index"];</code>
     * @return This builder for chaining.
     */
    public Builder clearIndex() {
      bitField0_ = (bitField0_ & ~0x00000001);
      index_ = 0L;
      onChanged();
      return this;
    }

    private boolean missed_ ;
    /**
     * <pre>
     * missed is the missed status.
     * </pre>
     *
     * <code>bool missed = 2 [json_name = "missed"];</code>
     * @return The missed.
     */
    @java.lang.Override
    public boolean getMissed() {
      return missed_;
    }
    /**
     * <pre>
     * missed is the missed status.
     * </pre>
     *
     * <code>bool missed = 2 [json_name = "missed"];</code>
     * @param value The missed to set.
     * @return This builder for chaining.
     */
    public Builder setMissed(boolean value) {

      missed_ = value;
      bitField0_ |= 0x00000002;
      onChanged();
      return this;
    }
    /**
     * <pre>
     * missed is the missed status.
     * </pre>
     *
     * <code>bool missed = 2 [json_name = "missed"];</code>
     * @return This builder for chaining.
     */
    public Builder clearMissed() {
      bitField0_ = (bitField0_ & ~0x00000002);
      missed_ = false;
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


    // @@protoc_insertion_point(builder_scope:cosmos.slashing.v1beta1.MissedBlock)
  }

  // @@protoc_insertion_point(class_scope:cosmos.slashing.v1beta1.MissedBlock)
  private static final com.cosmos.slashing.v1beta1.MissedBlock DEFAULT_INSTANCE;
  static {
    DEFAULT_INSTANCE = new com.cosmos.slashing.v1beta1.MissedBlock();
  }

  public static com.cosmos.slashing.v1beta1.MissedBlock getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }

  private static final com.google.protobuf.Parser<MissedBlock>
      PARSER = new com.google.protobuf.AbstractParser<MissedBlock>() {
    @java.lang.Override
    public MissedBlock parsePartialFrom(
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

  public static com.google.protobuf.Parser<MissedBlock> parser() {
    return PARSER;
  }

  @java.lang.Override
  public com.google.protobuf.Parser<MissedBlock> getParserForType() {
    return PARSER;
  }

  @java.lang.Override
  public com.cosmos.slashing.v1beta1.MissedBlock getDefaultInstanceForType() {
    return DEFAULT_INSTANCE;
  }

}

