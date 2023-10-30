// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: ibc/applications/fee/v1/tx.proto

package com.ibc.applications.fee.v1;

/**
 * <pre>
 * MsgPayPacketFeeAsync defines the request type for the PayPacketFeeAsync rpc
 * This Msg can be used to pay for a packet at a specified sequence (instead of the next sequence send)
 * </pre>
 *
 * Protobuf type {@code ibc.applications.fee.v1.MsgPayPacketFeeAsync}
 */
public final class MsgPayPacketFeeAsync extends
    com.google.protobuf.GeneratedMessageV3 implements
    // @@protoc_insertion_point(message_implements:ibc.applications.fee.v1.MsgPayPacketFeeAsync)
    MsgPayPacketFeeAsyncOrBuilder {
private static final long serialVersionUID = 0L;
  // Use MsgPayPacketFeeAsync.newBuilder() to construct.
  private MsgPayPacketFeeAsync(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
    super(builder);
  }
  private MsgPayPacketFeeAsync() {
  }

  @java.lang.Override
  @SuppressWarnings({"unused"})
  protected java.lang.Object newInstance(
      UnusedPrivateParameter unused) {
    return new MsgPayPacketFeeAsync();
  }

  public static final com.google.protobuf.Descriptors.Descriptor
      getDescriptor() {
    return com.ibc.applications.fee.v1.TxProto.internal_static_ibc_applications_fee_v1_MsgPayPacketFeeAsync_descriptor;
  }

  @java.lang.Override
  protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internalGetFieldAccessorTable() {
    return com.ibc.applications.fee.v1.TxProto.internal_static_ibc_applications_fee_v1_MsgPayPacketFeeAsync_fieldAccessorTable
        .ensureFieldAccessorsInitialized(
            com.ibc.applications.fee.v1.MsgPayPacketFeeAsync.class, com.ibc.applications.fee.v1.MsgPayPacketFeeAsync.Builder.class);
  }

  public static final int PACKET_ID_FIELD_NUMBER = 1;
  private com.ibc.core.channel.v1.PacketId packetId_;
  /**
   * <pre>
   * unique packet identifier comprised of the channel ID, port ID and sequence
   * </pre>
   *
   * <code>.ibc.core.channel.v1.PacketId packet_id = 1 [json_name = "packetId", (.gogoproto.nullable) = false, (.gogoproto.moretags) = "yaml:&#92;"packet_id&#92;""];</code>
   * @return Whether the packetId field is set.
   */
  @java.lang.Override
  public boolean hasPacketId() {
    return packetId_ != null;
  }
  /**
   * <pre>
   * unique packet identifier comprised of the channel ID, port ID and sequence
   * </pre>
   *
   * <code>.ibc.core.channel.v1.PacketId packet_id = 1 [json_name = "packetId", (.gogoproto.nullable) = false, (.gogoproto.moretags) = "yaml:&#92;"packet_id&#92;""];</code>
   * @return The packetId.
   */
  @java.lang.Override
  public com.ibc.core.channel.v1.PacketId getPacketId() {
    return packetId_ == null ? com.ibc.core.channel.v1.PacketId.getDefaultInstance() : packetId_;
  }
  /**
   * <pre>
   * unique packet identifier comprised of the channel ID, port ID and sequence
   * </pre>
   *
   * <code>.ibc.core.channel.v1.PacketId packet_id = 1 [json_name = "packetId", (.gogoproto.nullable) = false, (.gogoproto.moretags) = "yaml:&#92;"packet_id&#92;""];</code>
   */
  @java.lang.Override
  public com.ibc.core.channel.v1.PacketIdOrBuilder getPacketIdOrBuilder() {
    return packetId_ == null ? com.ibc.core.channel.v1.PacketId.getDefaultInstance() : packetId_;
  }

  public static final int PACKET_FEE_FIELD_NUMBER = 2;
  private com.ibc.applications.fee.v1.PacketFee packetFee_;
  /**
   * <pre>
   * the packet fee associated with a particular IBC packet
   * </pre>
   *
   * <code>.ibc.applications.fee.v1.PacketFee packet_fee = 2 [json_name = "packetFee", (.gogoproto.nullable) = false, (.gogoproto.moretags) = "yaml:&#92;"packet_fee&#92;""];</code>
   * @return Whether the packetFee field is set.
   */
  @java.lang.Override
  public boolean hasPacketFee() {
    return packetFee_ != null;
  }
  /**
   * <pre>
   * the packet fee associated with a particular IBC packet
   * </pre>
   *
   * <code>.ibc.applications.fee.v1.PacketFee packet_fee = 2 [json_name = "packetFee", (.gogoproto.nullable) = false, (.gogoproto.moretags) = "yaml:&#92;"packet_fee&#92;""];</code>
   * @return The packetFee.
   */
  @java.lang.Override
  public com.ibc.applications.fee.v1.PacketFee getPacketFee() {
    return packetFee_ == null ? com.ibc.applications.fee.v1.PacketFee.getDefaultInstance() : packetFee_;
  }
  /**
   * <pre>
   * the packet fee associated with a particular IBC packet
   * </pre>
   *
   * <code>.ibc.applications.fee.v1.PacketFee packet_fee = 2 [json_name = "packetFee", (.gogoproto.nullable) = false, (.gogoproto.moretags) = "yaml:&#92;"packet_fee&#92;""];</code>
   */
  @java.lang.Override
  public com.ibc.applications.fee.v1.PacketFeeOrBuilder getPacketFeeOrBuilder() {
    return packetFee_ == null ? com.ibc.applications.fee.v1.PacketFee.getDefaultInstance() : packetFee_;
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
    if (packetId_ != null) {
      output.writeMessage(1, getPacketId());
    }
    if (packetFee_ != null) {
      output.writeMessage(2, getPacketFee());
    }
    getUnknownFields().writeTo(output);
  }

  @java.lang.Override
  public int getSerializedSize() {
    int size = memoizedSize;
    if (size != -1) return size;

    size = 0;
    if (packetId_ != null) {
      size += com.google.protobuf.CodedOutputStream
        .computeMessageSize(1, getPacketId());
    }
    if (packetFee_ != null) {
      size += com.google.protobuf.CodedOutputStream
        .computeMessageSize(2, getPacketFee());
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
    if (!(obj instanceof com.ibc.applications.fee.v1.MsgPayPacketFeeAsync)) {
      return super.equals(obj);
    }
    com.ibc.applications.fee.v1.MsgPayPacketFeeAsync other = (com.ibc.applications.fee.v1.MsgPayPacketFeeAsync) obj;

    if (hasPacketId() != other.hasPacketId()) return false;
    if (hasPacketId()) {
      if (!getPacketId()
          .equals(other.getPacketId())) return false;
    }
    if (hasPacketFee() != other.hasPacketFee()) return false;
    if (hasPacketFee()) {
      if (!getPacketFee()
          .equals(other.getPacketFee())) return false;
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
    if (hasPacketId()) {
      hash = (37 * hash) + PACKET_ID_FIELD_NUMBER;
      hash = (53 * hash) + getPacketId().hashCode();
    }
    if (hasPacketFee()) {
      hash = (37 * hash) + PACKET_FEE_FIELD_NUMBER;
      hash = (53 * hash) + getPacketFee().hashCode();
    }
    hash = (29 * hash) + getUnknownFields().hashCode();
    memoizedHashCode = hash;
    return hash;
  }

  public static com.ibc.applications.fee.v1.MsgPayPacketFeeAsync parseFrom(
      java.nio.ByteBuffer data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static com.ibc.applications.fee.v1.MsgPayPacketFeeAsync parseFrom(
      java.nio.ByteBuffer data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static com.ibc.applications.fee.v1.MsgPayPacketFeeAsync parseFrom(
      com.google.protobuf.ByteString data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static com.ibc.applications.fee.v1.MsgPayPacketFeeAsync parseFrom(
      com.google.protobuf.ByteString data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static com.ibc.applications.fee.v1.MsgPayPacketFeeAsync parseFrom(byte[] data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static com.ibc.applications.fee.v1.MsgPayPacketFeeAsync parseFrom(
      byte[] data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static com.ibc.applications.fee.v1.MsgPayPacketFeeAsync parseFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static com.ibc.applications.fee.v1.MsgPayPacketFeeAsync parseFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input, extensionRegistry);
  }
  public static com.ibc.applications.fee.v1.MsgPayPacketFeeAsync parseDelimitedFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input);
  }
  public static com.ibc.applications.fee.v1.MsgPayPacketFeeAsync parseDelimitedFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
  }
  public static com.ibc.applications.fee.v1.MsgPayPacketFeeAsync parseFrom(
      com.google.protobuf.CodedInputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static com.ibc.applications.fee.v1.MsgPayPacketFeeAsync parseFrom(
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
  public static Builder newBuilder(com.ibc.applications.fee.v1.MsgPayPacketFeeAsync prototype) {
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
   * MsgPayPacketFeeAsync defines the request type for the PayPacketFeeAsync rpc
   * This Msg can be used to pay for a packet at a specified sequence (instead of the next sequence send)
   * </pre>
   *
   * Protobuf type {@code ibc.applications.fee.v1.MsgPayPacketFeeAsync}
   */
  public static final class Builder extends
      com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
      // @@protoc_insertion_point(builder_implements:ibc.applications.fee.v1.MsgPayPacketFeeAsync)
      com.ibc.applications.fee.v1.MsgPayPacketFeeAsyncOrBuilder {
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return com.ibc.applications.fee.v1.TxProto.internal_static_ibc_applications_fee_v1_MsgPayPacketFeeAsync_descriptor;
    }

    @java.lang.Override
    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return com.ibc.applications.fee.v1.TxProto.internal_static_ibc_applications_fee_v1_MsgPayPacketFeeAsync_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              com.ibc.applications.fee.v1.MsgPayPacketFeeAsync.class, com.ibc.applications.fee.v1.MsgPayPacketFeeAsync.Builder.class);
    }

    // Construct using com.ibc.applications.fee.v1.MsgPayPacketFeeAsync.newBuilder()
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
      packetId_ = null;
      if (packetIdBuilder_ != null) {
        packetIdBuilder_.dispose();
        packetIdBuilder_ = null;
      }
      packetFee_ = null;
      if (packetFeeBuilder_ != null) {
        packetFeeBuilder_.dispose();
        packetFeeBuilder_ = null;
      }
      return this;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.Descriptor
        getDescriptorForType() {
      return com.ibc.applications.fee.v1.TxProto.internal_static_ibc_applications_fee_v1_MsgPayPacketFeeAsync_descriptor;
    }

    @java.lang.Override
    public com.ibc.applications.fee.v1.MsgPayPacketFeeAsync getDefaultInstanceForType() {
      return com.ibc.applications.fee.v1.MsgPayPacketFeeAsync.getDefaultInstance();
    }

    @java.lang.Override
    public com.ibc.applications.fee.v1.MsgPayPacketFeeAsync build() {
      com.ibc.applications.fee.v1.MsgPayPacketFeeAsync result = buildPartial();
      if (!result.isInitialized()) {
        throw newUninitializedMessageException(result);
      }
      return result;
    }

    @java.lang.Override
    public com.ibc.applications.fee.v1.MsgPayPacketFeeAsync buildPartial() {
      com.ibc.applications.fee.v1.MsgPayPacketFeeAsync result = new com.ibc.applications.fee.v1.MsgPayPacketFeeAsync(this);
      if (bitField0_ != 0) { buildPartial0(result); }
      onBuilt();
      return result;
    }

    private void buildPartial0(com.ibc.applications.fee.v1.MsgPayPacketFeeAsync result) {
      int from_bitField0_ = bitField0_;
      if (((from_bitField0_ & 0x00000001) != 0)) {
        result.packetId_ = packetIdBuilder_ == null
            ? packetId_
            : packetIdBuilder_.build();
      }
      if (((from_bitField0_ & 0x00000002) != 0)) {
        result.packetFee_ = packetFeeBuilder_ == null
            ? packetFee_
            : packetFeeBuilder_.build();
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
      if (other instanceof com.ibc.applications.fee.v1.MsgPayPacketFeeAsync) {
        return mergeFrom((com.ibc.applications.fee.v1.MsgPayPacketFeeAsync)other);
      } else {
        super.mergeFrom(other);
        return this;
      }
    }

    public Builder mergeFrom(com.ibc.applications.fee.v1.MsgPayPacketFeeAsync other) {
      if (other == com.ibc.applications.fee.v1.MsgPayPacketFeeAsync.getDefaultInstance()) return this;
      if (other.hasPacketId()) {
        mergePacketId(other.getPacketId());
      }
      if (other.hasPacketFee()) {
        mergePacketFee(other.getPacketFee());
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
                  getPacketIdFieldBuilder().getBuilder(),
                  extensionRegistry);
              bitField0_ |= 0x00000001;
              break;
            } // case 10
            case 18: {
              input.readMessage(
                  getPacketFeeFieldBuilder().getBuilder(),
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

    private com.ibc.core.channel.v1.PacketId packetId_;
    private com.google.protobuf.SingleFieldBuilderV3<
        com.ibc.core.channel.v1.PacketId, com.ibc.core.channel.v1.PacketId.Builder, com.ibc.core.channel.v1.PacketIdOrBuilder> packetIdBuilder_;
    /**
     * <pre>
     * unique packet identifier comprised of the channel ID, port ID and sequence
     * </pre>
     *
     * <code>.ibc.core.channel.v1.PacketId packet_id = 1 [json_name = "packetId", (.gogoproto.nullable) = false, (.gogoproto.moretags) = "yaml:&#92;"packet_id&#92;""];</code>
     * @return Whether the packetId field is set.
     */
    public boolean hasPacketId() {
      return ((bitField0_ & 0x00000001) != 0);
    }
    /**
     * <pre>
     * unique packet identifier comprised of the channel ID, port ID and sequence
     * </pre>
     *
     * <code>.ibc.core.channel.v1.PacketId packet_id = 1 [json_name = "packetId", (.gogoproto.nullable) = false, (.gogoproto.moretags) = "yaml:&#92;"packet_id&#92;""];</code>
     * @return The packetId.
     */
    public com.ibc.core.channel.v1.PacketId getPacketId() {
      if (packetIdBuilder_ == null) {
        return packetId_ == null ? com.ibc.core.channel.v1.PacketId.getDefaultInstance() : packetId_;
      } else {
        return packetIdBuilder_.getMessage();
      }
    }
    /**
     * <pre>
     * unique packet identifier comprised of the channel ID, port ID and sequence
     * </pre>
     *
     * <code>.ibc.core.channel.v1.PacketId packet_id = 1 [json_name = "packetId", (.gogoproto.nullable) = false, (.gogoproto.moretags) = "yaml:&#92;"packet_id&#92;""];</code>
     */
    public Builder setPacketId(com.ibc.core.channel.v1.PacketId value) {
      if (packetIdBuilder_ == null) {
        if (value == null) {
          throw new NullPointerException();
        }
        packetId_ = value;
      } else {
        packetIdBuilder_.setMessage(value);
      }
      bitField0_ |= 0x00000001;
      onChanged();
      return this;
    }
    /**
     * <pre>
     * unique packet identifier comprised of the channel ID, port ID and sequence
     * </pre>
     *
     * <code>.ibc.core.channel.v1.PacketId packet_id = 1 [json_name = "packetId", (.gogoproto.nullable) = false, (.gogoproto.moretags) = "yaml:&#92;"packet_id&#92;""];</code>
     */
    public Builder setPacketId(
        com.ibc.core.channel.v1.PacketId.Builder builderForValue) {
      if (packetIdBuilder_ == null) {
        packetId_ = builderForValue.build();
      } else {
        packetIdBuilder_.setMessage(builderForValue.build());
      }
      bitField0_ |= 0x00000001;
      onChanged();
      return this;
    }
    /**
     * <pre>
     * unique packet identifier comprised of the channel ID, port ID and sequence
     * </pre>
     *
     * <code>.ibc.core.channel.v1.PacketId packet_id = 1 [json_name = "packetId", (.gogoproto.nullable) = false, (.gogoproto.moretags) = "yaml:&#92;"packet_id&#92;""];</code>
     */
    public Builder mergePacketId(com.ibc.core.channel.v1.PacketId value) {
      if (packetIdBuilder_ == null) {
        if (((bitField0_ & 0x00000001) != 0) &&
          packetId_ != null &&
          packetId_ != com.ibc.core.channel.v1.PacketId.getDefaultInstance()) {
          getPacketIdBuilder().mergeFrom(value);
        } else {
          packetId_ = value;
        }
      } else {
        packetIdBuilder_.mergeFrom(value);
      }
      bitField0_ |= 0x00000001;
      onChanged();
      return this;
    }
    /**
     * <pre>
     * unique packet identifier comprised of the channel ID, port ID and sequence
     * </pre>
     *
     * <code>.ibc.core.channel.v1.PacketId packet_id = 1 [json_name = "packetId", (.gogoproto.nullable) = false, (.gogoproto.moretags) = "yaml:&#92;"packet_id&#92;""];</code>
     */
    public Builder clearPacketId() {
      bitField0_ = (bitField0_ & ~0x00000001);
      packetId_ = null;
      if (packetIdBuilder_ != null) {
        packetIdBuilder_.dispose();
        packetIdBuilder_ = null;
      }
      onChanged();
      return this;
    }
    /**
     * <pre>
     * unique packet identifier comprised of the channel ID, port ID and sequence
     * </pre>
     *
     * <code>.ibc.core.channel.v1.PacketId packet_id = 1 [json_name = "packetId", (.gogoproto.nullable) = false, (.gogoproto.moretags) = "yaml:&#92;"packet_id&#92;""];</code>
     */
    public com.ibc.core.channel.v1.PacketId.Builder getPacketIdBuilder() {
      bitField0_ |= 0x00000001;
      onChanged();
      return getPacketIdFieldBuilder().getBuilder();
    }
    /**
     * <pre>
     * unique packet identifier comprised of the channel ID, port ID and sequence
     * </pre>
     *
     * <code>.ibc.core.channel.v1.PacketId packet_id = 1 [json_name = "packetId", (.gogoproto.nullable) = false, (.gogoproto.moretags) = "yaml:&#92;"packet_id&#92;""];</code>
     */
    public com.ibc.core.channel.v1.PacketIdOrBuilder getPacketIdOrBuilder() {
      if (packetIdBuilder_ != null) {
        return packetIdBuilder_.getMessageOrBuilder();
      } else {
        return packetId_ == null ?
            com.ibc.core.channel.v1.PacketId.getDefaultInstance() : packetId_;
      }
    }
    /**
     * <pre>
     * unique packet identifier comprised of the channel ID, port ID and sequence
     * </pre>
     *
     * <code>.ibc.core.channel.v1.PacketId packet_id = 1 [json_name = "packetId", (.gogoproto.nullable) = false, (.gogoproto.moretags) = "yaml:&#92;"packet_id&#92;""];</code>
     */
    private com.google.protobuf.SingleFieldBuilderV3<
        com.ibc.core.channel.v1.PacketId, com.ibc.core.channel.v1.PacketId.Builder, com.ibc.core.channel.v1.PacketIdOrBuilder> 
        getPacketIdFieldBuilder() {
      if (packetIdBuilder_ == null) {
        packetIdBuilder_ = new com.google.protobuf.SingleFieldBuilderV3<
            com.ibc.core.channel.v1.PacketId, com.ibc.core.channel.v1.PacketId.Builder, com.ibc.core.channel.v1.PacketIdOrBuilder>(
                getPacketId(),
                getParentForChildren(),
                isClean());
        packetId_ = null;
      }
      return packetIdBuilder_;
    }

    private com.ibc.applications.fee.v1.PacketFee packetFee_;
    private com.google.protobuf.SingleFieldBuilderV3<
        com.ibc.applications.fee.v1.PacketFee, com.ibc.applications.fee.v1.PacketFee.Builder, com.ibc.applications.fee.v1.PacketFeeOrBuilder> packetFeeBuilder_;
    /**
     * <pre>
     * the packet fee associated with a particular IBC packet
     * </pre>
     *
     * <code>.ibc.applications.fee.v1.PacketFee packet_fee = 2 [json_name = "packetFee", (.gogoproto.nullable) = false, (.gogoproto.moretags) = "yaml:&#92;"packet_fee&#92;""];</code>
     * @return Whether the packetFee field is set.
     */
    public boolean hasPacketFee() {
      return ((bitField0_ & 0x00000002) != 0);
    }
    /**
     * <pre>
     * the packet fee associated with a particular IBC packet
     * </pre>
     *
     * <code>.ibc.applications.fee.v1.PacketFee packet_fee = 2 [json_name = "packetFee", (.gogoproto.nullable) = false, (.gogoproto.moretags) = "yaml:&#92;"packet_fee&#92;""];</code>
     * @return The packetFee.
     */
    public com.ibc.applications.fee.v1.PacketFee getPacketFee() {
      if (packetFeeBuilder_ == null) {
        return packetFee_ == null ? com.ibc.applications.fee.v1.PacketFee.getDefaultInstance() : packetFee_;
      } else {
        return packetFeeBuilder_.getMessage();
      }
    }
    /**
     * <pre>
     * the packet fee associated with a particular IBC packet
     * </pre>
     *
     * <code>.ibc.applications.fee.v1.PacketFee packet_fee = 2 [json_name = "packetFee", (.gogoproto.nullable) = false, (.gogoproto.moretags) = "yaml:&#92;"packet_fee&#92;""];</code>
     */
    public Builder setPacketFee(com.ibc.applications.fee.v1.PacketFee value) {
      if (packetFeeBuilder_ == null) {
        if (value == null) {
          throw new NullPointerException();
        }
        packetFee_ = value;
      } else {
        packetFeeBuilder_.setMessage(value);
      }
      bitField0_ |= 0x00000002;
      onChanged();
      return this;
    }
    /**
     * <pre>
     * the packet fee associated with a particular IBC packet
     * </pre>
     *
     * <code>.ibc.applications.fee.v1.PacketFee packet_fee = 2 [json_name = "packetFee", (.gogoproto.nullable) = false, (.gogoproto.moretags) = "yaml:&#92;"packet_fee&#92;""];</code>
     */
    public Builder setPacketFee(
        com.ibc.applications.fee.v1.PacketFee.Builder builderForValue) {
      if (packetFeeBuilder_ == null) {
        packetFee_ = builderForValue.build();
      } else {
        packetFeeBuilder_.setMessage(builderForValue.build());
      }
      bitField0_ |= 0x00000002;
      onChanged();
      return this;
    }
    /**
     * <pre>
     * the packet fee associated with a particular IBC packet
     * </pre>
     *
     * <code>.ibc.applications.fee.v1.PacketFee packet_fee = 2 [json_name = "packetFee", (.gogoproto.nullable) = false, (.gogoproto.moretags) = "yaml:&#92;"packet_fee&#92;""];</code>
     */
    public Builder mergePacketFee(com.ibc.applications.fee.v1.PacketFee value) {
      if (packetFeeBuilder_ == null) {
        if (((bitField0_ & 0x00000002) != 0) &&
          packetFee_ != null &&
          packetFee_ != com.ibc.applications.fee.v1.PacketFee.getDefaultInstance()) {
          getPacketFeeBuilder().mergeFrom(value);
        } else {
          packetFee_ = value;
        }
      } else {
        packetFeeBuilder_.mergeFrom(value);
      }
      bitField0_ |= 0x00000002;
      onChanged();
      return this;
    }
    /**
     * <pre>
     * the packet fee associated with a particular IBC packet
     * </pre>
     *
     * <code>.ibc.applications.fee.v1.PacketFee packet_fee = 2 [json_name = "packetFee", (.gogoproto.nullable) = false, (.gogoproto.moretags) = "yaml:&#92;"packet_fee&#92;""];</code>
     */
    public Builder clearPacketFee() {
      bitField0_ = (bitField0_ & ~0x00000002);
      packetFee_ = null;
      if (packetFeeBuilder_ != null) {
        packetFeeBuilder_.dispose();
        packetFeeBuilder_ = null;
      }
      onChanged();
      return this;
    }
    /**
     * <pre>
     * the packet fee associated with a particular IBC packet
     * </pre>
     *
     * <code>.ibc.applications.fee.v1.PacketFee packet_fee = 2 [json_name = "packetFee", (.gogoproto.nullable) = false, (.gogoproto.moretags) = "yaml:&#92;"packet_fee&#92;""];</code>
     */
    public com.ibc.applications.fee.v1.PacketFee.Builder getPacketFeeBuilder() {
      bitField0_ |= 0x00000002;
      onChanged();
      return getPacketFeeFieldBuilder().getBuilder();
    }
    /**
     * <pre>
     * the packet fee associated with a particular IBC packet
     * </pre>
     *
     * <code>.ibc.applications.fee.v1.PacketFee packet_fee = 2 [json_name = "packetFee", (.gogoproto.nullable) = false, (.gogoproto.moretags) = "yaml:&#92;"packet_fee&#92;""];</code>
     */
    public com.ibc.applications.fee.v1.PacketFeeOrBuilder getPacketFeeOrBuilder() {
      if (packetFeeBuilder_ != null) {
        return packetFeeBuilder_.getMessageOrBuilder();
      } else {
        return packetFee_ == null ?
            com.ibc.applications.fee.v1.PacketFee.getDefaultInstance() : packetFee_;
      }
    }
    /**
     * <pre>
     * the packet fee associated with a particular IBC packet
     * </pre>
     *
     * <code>.ibc.applications.fee.v1.PacketFee packet_fee = 2 [json_name = "packetFee", (.gogoproto.nullable) = false, (.gogoproto.moretags) = "yaml:&#92;"packet_fee&#92;""];</code>
     */
    private com.google.protobuf.SingleFieldBuilderV3<
        com.ibc.applications.fee.v1.PacketFee, com.ibc.applications.fee.v1.PacketFee.Builder, com.ibc.applications.fee.v1.PacketFeeOrBuilder> 
        getPacketFeeFieldBuilder() {
      if (packetFeeBuilder_ == null) {
        packetFeeBuilder_ = new com.google.protobuf.SingleFieldBuilderV3<
            com.ibc.applications.fee.v1.PacketFee, com.ibc.applications.fee.v1.PacketFee.Builder, com.ibc.applications.fee.v1.PacketFeeOrBuilder>(
                getPacketFee(),
                getParentForChildren(),
                isClean());
        packetFee_ = null;
      }
      return packetFeeBuilder_;
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


    // @@protoc_insertion_point(builder_scope:ibc.applications.fee.v1.MsgPayPacketFeeAsync)
  }

  // @@protoc_insertion_point(class_scope:ibc.applications.fee.v1.MsgPayPacketFeeAsync)
  private static final com.ibc.applications.fee.v1.MsgPayPacketFeeAsync DEFAULT_INSTANCE;
  static {
    DEFAULT_INSTANCE = new com.ibc.applications.fee.v1.MsgPayPacketFeeAsync();
  }

  public static com.ibc.applications.fee.v1.MsgPayPacketFeeAsync getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }

  private static final com.google.protobuf.Parser<MsgPayPacketFeeAsync>
      PARSER = new com.google.protobuf.AbstractParser<MsgPayPacketFeeAsync>() {
    @java.lang.Override
    public MsgPayPacketFeeAsync parsePartialFrom(
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

  public static com.google.protobuf.Parser<MsgPayPacketFeeAsync> parser() {
    return PARSER;
  }

  @java.lang.Override
  public com.google.protobuf.Parser<MsgPayPacketFeeAsync> getParserForType() {
    return PARSER;
  }

  @java.lang.Override
  public com.ibc.applications.fee.v1.MsgPayPacketFeeAsync getDefaultInstanceForType() {
    return DEFAULT_INSTANCE;
  }

}
