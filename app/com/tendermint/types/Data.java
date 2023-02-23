// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: tendermint/types/types.proto

package com.tendermint.types;

/**
 * <pre>
 * Data contains the set of transactions included in the block
 * </pre>
 *
 * Protobuf type {@code tendermint.types.Data}
 */
public final class Data extends
    com.google.protobuf.GeneratedMessageV3 implements
    // @@protoc_insertion_point(message_implements:tendermint.types.Data)
    DataOrBuilder {
private static final long serialVersionUID = 0L;
  // Use Data.newBuilder() to construct.
  private Data(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
    super(builder);
  }
  private Data() {
    txs_ = java.util.Collections.emptyList();
  }

  @java.lang.Override
  @SuppressWarnings({"unused"})
  protected java.lang.Object newInstance(
      UnusedPrivateParameter unused) {
    return new Data();
  }

  public static final com.google.protobuf.Descriptors.Descriptor
      getDescriptor() {
    return com.tendermint.types.TypesProto.internal_static_tendermint_types_Data_descriptor;
  }

  @java.lang.Override
  protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internalGetFieldAccessorTable() {
    return com.tendermint.types.TypesProto.internal_static_tendermint_types_Data_fieldAccessorTable
        .ensureFieldAccessorsInitialized(
            com.tendermint.types.Data.class, com.tendermint.types.Data.Builder.class);
  }

  public static final int TXS_FIELD_NUMBER = 1;
  @SuppressWarnings("serial")
  private java.util.List<com.google.protobuf.ByteString> txs_;
  /**
   * <pre>
   * Txs that will be applied by state &#64; block.Height+1.
   * NOTE: not all txs here are valid.  We're just agreeing on the order first.
   * This means that block.AppHash does not include these txs.
   * </pre>
   *
   * <code>repeated bytes txs = 1 [json_name = "txs"];</code>
   * @return A list containing the txs.
   */
  @java.lang.Override
  public java.util.List<com.google.protobuf.ByteString>
      getTxsList() {
    return txs_;
  }
  /**
   * <pre>
   * Txs that will be applied by state &#64; block.Height+1.
   * NOTE: not all txs here are valid.  We're just agreeing on the order first.
   * This means that block.AppHash does not include these txs.
   * </pre>
   *
   * <code>repeated bytes txs = 1 [json_name = "txs"];</code>
   * @return The count of txs.
   */
  public int getTxsCount() {
    return txs_.size();
  }
  /**
   * <pre>
   * Txs that will be applied by state &#64; block.Height+1.
   * NOTE: not all txs here are valid.  We're just agreeing on the order first.
   * This means that block.AppHash does not include these txs.
   * </pre>
   *
   * <code>repeated bytes txs = 1 [json_name = "txs"];</code>
   * @param index The index of the element to return.
   * @return The txs at the given index.
   */
  public com.google.protobuf.ByteString getTxs(int index) {
    return txs_.get(index);
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
    for (int i = 0; i < txs_.size(); i++) {
      output.writeBytes(1, txs_.get(i));
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
      for (int i = 0; i < txs_.size(); i++) {
        dataSize += com.google.protobuf.CodedOutputStream
          .computeBytesSizeNoTag(txs_.get(i));
      }
      size += dataSize;
      size += 1 * getTxsList().size();
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
    if (!(obj instanceof com.tendermint.types.Data)) {
      return super.equals(obj);
    }
    com.tendermint.types.Data other = (com.tendermint.types.Data) obj;

    if (!getTxsList()
        .equals(other.getTxsList())) return false;
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
    if (getTxsCount() > 0) {
      hash = (37 * hash) + TXS_FIELD_NUMBER;
      hash = (53 * hash) + getTxsList().hashCode();
    }
    hash = (29 * hash) + getUnknownFields().hashCode();
    memoizedHashCode = hash;
    return hash;
  }

  public static com.tendermint.types.Data parseFrom(
      java.nio.ByteBuffer data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static com.tendermint.types.Data parseFrom(
      java.nio.ByteBuffer data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static com.tendermint.types.Data parseFrom(
      com.google.protobuf.ByteString data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static com.tendermint.types.Data parseFrom(
      com.google.protobuf.ByteString data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static com.tendermint.types.Data parseFrom(byte[] data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static com.tendermint.types.Data parseFrom(
      byte[] data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static com.tendermint.types.Data parseFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static com.tendermint.types.Data parseFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input, extensionRegistry);
  }
  public static com.tendermint.types.Data parseDelimitedFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input);
  }
  public static com.tendermint.types.Data parseDelimitedFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
  }
  public static com.tendermint.types.Data parseFrom(
      com.google.protobuf.CodedInputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static com.tendermint.types.Data parseFrom(
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
  public static Builder newBuilder(com.tendermint.types.Data prototype) {
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
   * Data contains the set of transactions included in the block
   * </pre>
   *
   * Protobuf type {@code tendermint.types.Data}
   */
  public static final class Builder extends
      com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
      // @@protoc_insertion_point(builder_implements:tendermint.types.Data)
      com.tendermint.types.DataOrBuilder {
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return com.tendermint.types.TypesProto.internal_static_tendermint_types_Data_descriptor;
    }

    @java.lang.Override
    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return com.tendermint.types.TypesProto.internal_static_tendermint_types_Data_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              com.tendermint.types.Data.class, com.tendermint.types.Data.Builder.class);
    }

    // Construct using com.tendermint.types.Data.newBuilder()
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
      txs_ = java.util.Collections.emptyList();
      return this;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.Descriptor
        getDescriptorForType() {
      return com.tendermint.types.TypesProto.internal_static_tendermint_types_Data_descriptor;
    }

    @java.lang.Override
    public com.tendermint.types.Data getDefaultInstanceForType() {
      return com.tendermint.types.Data.getDefaultInstance();
    }

    @java.lang.Override
    public com.tendermint.types.Data build() {
      com.tendermint.types.Data result = buildPartial();
      if (!result.isInitialized()) {
        throw newUninitializedMessageException(result);
      }
      return result;
    }

    @java.lang.Override
    public com.tendermint.types.Data buildPartial() {
      com.tendermint.types.Data result = new com.tendermint.types.Data(this);
      buildPartialRepeatedFields(result);
      if (bitField0_ != 0) { buildPartial0(result); }
      onBuilt();
      return result;
    }

    private void buildPartialRepeatedFields(com.tendermint.types.Data result) {
      if (((bitField0_ & 0x00000001) != 0)) {
        txs_ = java.util.Collections.unmodifiableList(txs_);
        bitField0_ = (bitField0_ & ~0x00000001);
      }
      result.txs_ = txs_;
    }

    private void buildPartial0(com.tendermint.types.Data result) {
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
      if (other instanceof com.tendermint.types.Data) {
        return mergeFrom((com.tendermint.types.Data)other);
      } else {
        super.mergeFrom(other);
        return this;
      }
    }

    public Builder mergeFrom(com.tendermint.types.Data other) {
      if (other == com.tendermint.types.Data.getDefaultInstance()) return this;
      if (!other.txs_.isEmpty()) {
        if (txs_.isEmpty()) {
          txs_ = other.txs_;
          bitField0_ = (bitField0_ & ~0x00000001);
        } else {
          ensureTxsIsMutable();
          txs_.addAll(other.txs_);
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
              com.google.protobuf.ByteString v = input.readBytes();
              ensureTxsIsMutable();
              txs_.add(v);
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

    private java.util.List<com.google.protobuf.ByteString> txs_ = java.util.Collections.emptyList();
    private void ensureTxsIsMutable() {
      if (!((bitField0_ & 0x00000001) != 0)) {
        txs_ = new java.util.ArrayList<com.google.protobuf.ByteString>(txs_);
        bitField0_ |= 0x00000001;
      }
    }
    /**
     * <pre>
     * Txs that will be applied by state &#64; block.Height+1.
     * NOTE: not all txs here are valid.  We're just agreeing on the order first.
     * This means that block.AppHash does not include these txs.
     * </pre>
     *
     * <code>repeated bytes txs = 1 [json_name = "txs"];</code>
     * @return A list containing the txs.
     */
    public java.util.List<com.google.protobuf.ByteString>
        getTxsList() {
      return ((bitField0_ & 0x00000001) != 0) ?
               java.util.Collections.unmodifiableList(txs_) : txs_;
    }
    /**
     * <pre>
     * Txs that will be applied by state &#64; block.Height+1.
     * NOTE: not all txs here are valid.  We're just agreeing on the order first.
     * This means that block.AppHash does not include these txs.
     * </pre>
     *
     * <code>repeated bytes txs = 1 [json_name = "txs"];</code>
     * @return The count of txs.
     */
    public int getTxsCount() {
      return txs_.size();
    }
    /**
     * <pre>
     * Txs that will be applied by state &#64; block.Height+1.
     * NOTE: not all txs here are valid.  We're just agreeing on the order first.
     * This means that block.AppHash does not include these txs.
     * </pre>
     *
     * <code>repeated bytes txs = 1 [json_name = "txs"];</code>
     * @param index The index of the element to return.
     * @return The txs at the given index.
     */
    public com.google.protobuf.ByteString getTxs(int index) {
      return txs_.get(index);
    }
    /**
     * <pre>
     * Txs that will be applied by state &#64; block.Height+1.
     * NOTE: not all txs here are valid.  We're just agreeing on the order first.
     * This means that block.AppHash does not include these txs.
     * </pre>
     *
     * <code>repeated bytes txs = 1 [json_name = "txs"];</code>
     * @param index The index to set the value at.
     * @param value The txs to set.
     * @return This builder for chaining.
     */
    public Builder setTxs(
        int index, com.google.protobuf.ByteString value) {
      if (value == null) { throw new NullPointerException(); }
      ensureTxsIsMutable();
      txs_.set(index, value);
      onChanged();
      return this;
    }
    /**
     * <pre>
     * Txs that will be applied by state &#64; block.Height+1.
     * NOTE: not all txs here are valid.  We're just agreeing on the order first.
     * This means that block.AppHash does not include these txs.
     * </pre>
     *
     * <code>repeated bytes txs = 1 [json_name = "txs"];</code>
     * @param value The txs to add.
     * @return This builder for chaining.
     */
    public Builder addTxs(com.google.protobuf.ByteString value) {
      if (value == null) { throw new NullPointerException(); }
      ensureTxsIsMutable();
      txs_.add(value);
      onChanged();
      return this;
    }
    /**
     * <pre>
     * Txs that will be applied by state &#64; block.Height+1.
     * NOTE: not all txs here are valid.  We're just agreeing on the order first.
     * This means that block.AppHash does not include these txs.
     * </pre>
     *
     * <code>repeated bytes txs = 1 [json_name = "txs"];</code>
     * @param values The txs to add.
     * @return This builder for chaining.
     */
    public Builder addAllTxs(
        java.lang.Iterable<? extends com.google.protobuf.ByteString> values) {
      ensureTxsIsMutable();
      com.google.protobuf.AbstractMessageLite.Builder.addAll(
          values, txs_);
      onChanged();
      return this;
    }
    /**
     * <pre>
     * Txs that will be applied by state &#64; block.Height+1.
     * NOTE: not all txs here are valid.  We're just agreeing on the order first.
     * This means that block.AppHash does not include these txs.
     * </pre>
     *
     * <code>repeated bytes txs = 1 [json_name = "txs"];</code>
     * @return This builder for chaining.
     */
    public Builder clearTxs() {
      txs_ = java.util.Collections.emptyList();
      bitField0_ = (bitField0_ & ~0x00000001);
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


    // @@protoc_insertion_point(builder_scope:tendermint.types.Data)
  }

  // @@protoc_insertion_point(class_scope:tendermint.types.Data)
  private static final com.tendermint.types.Data DEFAULT_INSTANCE;
  static {
    DEFAULT_INSTANCE = new com.tendermint.types.Data();
  }

  public static com.tendermint.types.Data getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }

  private static final com.google.protobuf.Parser<Data>
      PARSER = new com.google.protobuf.AbstractParser<Data>() {
    @java.lang.Override
    public Data parsePartialFrom(
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

  public static com.google.protobuf.Parser<Data> parser() {
    return PARSER;
  }

  @java.lang.Override
  public com.google.protobuf.Parser<Data> getParserForType() {
    return PARSER;
  }

  @java.lang.Override
  public com.tendermint.types.Data getDefaultInstanceForType() {
    return DEFAULT_INSTANCE;
  }

}
