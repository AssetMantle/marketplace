// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: tendermint/types/events.proto

package com.tendermint.types;

/**
 * Protobuf type {@code tendermint.types.EventDataRoundState}
 */
public final class EventDataRoundState extends
    com.google.protobuf.GeneratedMessageV3 implements
    // @@protoc_insertion_point(message_implements:tendermint.types.EventDataRoundState)
    EventDataRoundStateOrBuilder {
private static final long serialVersionUID = 0L;
  // Use EventDataRoundState.newBuilder() to construct.
  private EventDataRoundState(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
    super(builder);
  }
  private EventDataRoundState() {
    step_ = "";
  }

  @java.lang.Override
  @SuppressWarnings({"unused"})
  protected java.lang.Object newInstance(
      UnusedPrivateParameter unused) {
    return new EventDataRoundState();
  }

  public static final com.google.protobuf.Descriptors.Descriptor
      getDescriptor() {
    return com.tendermint.types.EventsProto.internal_static_tendermint_types_EventDataRoundState_descriptor;
  }

  @java.lang.Override
  protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internalGetFieldAccessorTable() {
    return com.tendermint.types.EventsProto.internal_static_tendermint_types_EventDataRoundState_fieldAccessorTable
        .ensureFieldAccessorsInitialized(
            com.tendermint.types.EventDataRoundState.class, com.tendermint.types.EventDataRoundState.Builder.class);
  }

  public static final int HEIGHT_FIELD_NUMBER = 1;
  private long height_ = 0L;
  /**
   * <code>int64 height = 1 [json_name = "height"];</code>
   * @return The height.
   */
  @java.lang.Override
  public long getHeight() {
    return height_;
  }

  public static final int ROUND_FIELD_NUMBER = 2;
  private int round_ = 0;
  /**
   * <code>int32 round = 2 [json_name = "round"];</code>
   * @return The round.
   */
  @java.lang.Override
  public int getRound() {
    return round_;
  }

  public static final int STEP_FIELD_NUMBER = 3;
  @SuppressWarnings("serial")
  private volatile java.lang.Object step_ = "";
  /**
   * <code>string step = 3 [json_name = "step"];</code>
   * @return The step.
   */
  @java.lang.Override
  public java.lang.String getStep() {
    java.lang.Object ref = step_;
    if (ref instanceof java.lang.String) {
      return (java.lang.String) ref;
    } else {
      com.google.protobuf.ByteString bs = 
          (com.google.protobuf.ByteString) ref;
      java.lang.String s = bs.toStringUtf8();
      step_ = s;
      return s;
    }
  }
  /**
   * <code>string step = 3 [json_name = "step"];</code>
   * @return The bytes for step.
   */
  @java.lang.Override
  public com.google.protobuf.ByteString
      getStepBytes() {
    java.lang.Object ref = step_;
    if (ref instanceof java.lang.String) {
      com.google.protobuf.ByteString b = 
          com.google.protobuf.ByteString.copyFromUtf8(
              (java.lang.String) ref);
      step_ = b;
      return b;
    } else {
      return (com.google.protobuf.ByteString) ref;
    }
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
    if (height_ != 0L) {
      output.writeInt64(1, height_);
    }
    if (round_ != 0) {
      output.writeInt32(2, round_);
    }
    if (!com.google.protobuf.GeneratedMessageV3.isStringEmpty(step_)) {
      com.google.protobuf.GeneratedMessageV3.writeString(output, 3, step_);
    }
    getUnknownFields().writeTo(output);
  }

  @java.lang.Override
  public int getSerializedSize() {
    int size = memoizedSize;
    if (size != -1) return size;

    size = 0;
    if (height_ != 0L) {
      size += com.google.protobuf.CodedOutputStream
        .computeInt64Size(1, height_);
    }
    if (round_ != 0) {
      size += com.google.protobuf.CodedOutputStream
        .computeInt32Size(2, round_);
    }
    if (!com.google.protobuf.GeneratedMessageV3.isStringEmpty(step_)) {
      size += com.google.protobuf.GeneratedMessageV3.computeStringSize(3, step_);
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
    if (!(obj instanceof com.tendermint.types.EventDataRoundState)) {
      return super.equals(obj);
    }
    com.tendermint.types.EventDataRoundState other = (com.tendermint.types.EventDataRoundState) obj;

    if (getHeight()
        != other.getHeight()) return false;
    if (getRound()
        != other.getRound()) return false;
    if (!getStep()
        .equals(other.getStep())) return false;
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
    hash = (37 * hash) + HEIGHT_FIELD_NUMBER;
    hash = (53 * hash) + com.google.protobuf.Internal.hashLong(
        getHeight());
    hash = (37 * hash) + ROUND_FIELD_NUMBER;
    hash = (53 * hash) + getRound();
    hash = (37 * hash) + STEP_FIELD_NUMBER;
    hash = (53 * hash) + getStep().hashCode();
    hash = (29 * hash) + getUnknownFields().hashCode();
    memoizedHashCode = hash;
    return hash;
  }

  public static com.tendermint.types.EventDataRoundState parseFrom(
      java.nio.ByteBuffer data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static com.tendermint.types.EventDataRoundState parseFrom(
      java.nio.ByteBuffer data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static com.tendermint.types.EventDataRoundState parseFrom(
      com.google.protobuf.ByteString data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static com.tendermint.types.EventDataRoundState parseFrom(
      com.google.protobuf.ByteString data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static com.tendermint.types.EventDataRoundState parseFrom(byte[] data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static com.tendermint.types.EventDataRoundState parseFrom(
      byte[] data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static com.tendermint.types.EventDataRoundState parseFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static com.tendermint.types.EventDataRoundState parseFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input, extensionRegistry);
  }
  public static com.tendermint.types.EventDataRoundState parseDelimitedFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input);
  }
  public static com.tendermint.types.EventDataRoundState parseDelimitedFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
  }
  public static com.tendermint.types.EventDataRoundState parseFrom(
      com.google.protobuf.CodedInputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static com.tendermint.types.EventDataRoundState parseFrom(
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
  public static Builder newBuilder(com.tendermint.types.EventDataRoundState prototype) {
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
   * Protobuf type {@code tendermint.types.EventDataRoundState}
   */
  public static final class Builder extends
      com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
      // @@protoc_insertion_point(builder_implements:tendermint.types.EventDataRoundState)
      com.tendermint.types.EventDataRoundStateOrBuilder {
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return com.tendermint.types.EventsProto.internal_static_tendermint_types_EventDataRoundState_descriptor;
    }

    @java.lang.Override
    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return com.tendermint.types.EventsProto.internal_static_tendermint_types_EventDataRoundState_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              com.tendermint.types.EventDataRoundState.class, com.tendermint.types.EventDataRoundState.Builder.class);
    }

    // Construct using com.tendermint.types.EventDataRoundState.newBuilder()
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
      height_ = 0L;
      round_ = 0;
      step_ = "";
      return this;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.Descriptor
        getDescriptorForType() {
      return com.tendermint.types.EventsProto.internal_static_tendermint_types_EventDataRoundState_descriptor;
    }

    @java.lang.Override
    public com.tendermint.types.EventDataRoundState getDefaultInstanceForType() {
      return com.tendermint.types.EventDataRoundState.getDefaultInstance();
    }

    @java.lang.Override
    public com.tendermint.types.EventDataRoundState build() {
      com.tendermint.types.EventDataRoundState result = buildPartial();
      if (!result.isInitialized()) {
        throw newUninitializedMessageException(result);
      }
      return result;
    }

    @java.lang.Override
    public com.tendermint.types.EventDataRoundState buildPartial() {
      com.tendermint.types.EventDataRoundState result = new com.tendermint.types.EventDataRoundState(this);
      if (bitField0_ != 0) { buildPartial0(result); }
      onBuilt();
      return result;
    }

    private void buildPartial0(com.tendermint.types.EventDataRoundState result) {
      int from_bitField0_ = bitField0_;
      if (((from_bitField0_ & 0x00000001) != 0)) {
        result.height_ = height_;
      }
      if (((from_bitField0_ & 0x00000002) != 0)) {
        result.round_ = round_;
      }
      if (((from_bitField0_ & 0x00000004) != 0)) {
        result.step_ = step_;
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
      if (other instanceof com.tendermint.types.EventDataRoundState) {
        return mergeFrom((com.tendermint.types.EventDataRoundState)other);
      } else {
        super.mergeFrom(other);
        return this;
      }
    }

    public Builder mergeFrom(com.tendermint.types.EventDataRoundState other) {
      if (other == com.tendermint.types.EventDataRoundState.getDefaultInstance()) return this;
      if (other.getHeight() != 0L) {
        setHeight(other.getHeight());
      }
      if (other.getRound() != 0) {
        setRound(other.getRound());
      }
      if (!other.getStep().isEmpty()) {
        step_ = other.step_;
        bitField0_ |= 0x00000004;
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
            case 8: {
              height_ = input.readInt64();
              bitField0_ |= 0x00000001;
              break;
            } // case 8
            case 16: {
              round_ = input.readInt32();
              bitField0_ |= 0x00000002;
              break;
            } // case 16
            case 26: {
              step_ = input.readStringRequireUtf8();
              bitField0_ |= 0x00000004;
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

    private long height_ ;
    /**
     * <code>int64 height = 1 [json_name = "height"];</code>
     * @return The height.
     */
    @java.lang.Override
    public long getHeight() {
      return height_;
    }
    /**
     * <code>int64 height = 1 [json_name = "height"];</code>
     * @param value The height to set.
     * @return This builder for chaining.
     */
    public Builder setHeight(long value) {

      height_ = value;
      bitField0_ |= 0x00000001;
      onChanged();
      return this;
    }
    /**
     * <code>int64 height = 1 [json_name = "height"];</code>
     * @return This builder for chaining.
     */
    public Builder clearHeight() {
      bitField0_ = (bitField0_ & ~0x00000001);
      height_ = 0L;
      onChanged();
      return this;
    }

    private int round_ ;
    /**
     * <code>int32 round = 2 [json_name = "round"];</code>
     * @return The round.
     */
    @java.lang.Override
    public int getRound() {
      return round_;
    }
    /**
     * <code>int32 round = 2 [json_name = "round"];</code>
     * @param value The round to set.
     * @return This builder for chaining.
     */
    public Builder setRound(int value) {

      round_ = value;
      bitField0_ |= 0x00000002;
      onChanged();
      return this;
    }
    /**
     * <code>int32 round = 2 [json_name = "round"];</code>
     * @return This builder for chaining.
     */
    public Builder clearRound() {
      bitField0_ = (bitField0_ & ~0x00000002);
      round_ = 0;
      onChanged();
      return this;
    }

    private java.lang.Object step_ = "";
    /**
     * <code>string step = 3 [json_name = "step"];</code>
     * @return The step.
     */
    public java.lang.String getStep() {
      java.lang.Object ref = step_;
      if (!(ref instanceof java.lang.String)) {
        com.google.protobuf.ByteString bs =
            (com.google.protobuf.ByteString) ref;
        java.lang.String s = bs.toStringUtf8();
        step_ = s;
        return s;
      } else {
        return (java.lang.String) ref;
      }
    }
    /**
     * <code>string step = 3 [json_name = "step"];</code>
     * @return The bytes for step.
     */
    public com.google.protobuf.ByteString
        getStepBytes() {
      java.lang.Object ref = step_;
      if (ref instanceof String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8(
                (java.lang.String) ref);
        step_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }
    /**
     * <code>string step = 3 [json_name = "step"];</code>
     * @param value The step to set.
     * @return This builder for chaining.
     */
    public Builder setStep(
        java.lang.String value) {
      if (value == null) { throw new NullPointerException(); }
      step_ = value;
      bitField0_ |= 0x00000004;
      onChanged();
      return this;
    }
    /**
     * <code>string step = 3 [json_name = "step"];</code>
     * @return This builder for chaining.
     */
    public Builder clearStep() {
      step_ = getDefaultInstance().getStep();
      bitField0_ = (bitField0_ & ~0x00000004);
      onChanged();
      return this;
    }
    /**
     * <code>string step = 3 [json_name = "step"];</code>
     * @param value The bytes for step to set.
     * @return This builder for chaining.
     */
    public Builder setStepBytes(
        com.google.protobuf.ByteString value) {
      if (value == null) { throw new NullPointerException(); }
      checkByteStringIsUtf8(value);
      step_ = value;
      bitField0_ |= 0x00000004;
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


    // @@protoc_insertion_point(builder_scope:tendermint.types.EventDataRoundState)
  }

  // @@protoc_insertion_point(class_scope:tendermint.types.EventDataRoundState)
  private static final com.tendermint.types.EventDataRoundState DEFAULT_INSTANCE;
  static {
    DEFAULT_INSTANCE = new com.tendermint.types.EventDataRoundState();
  }

  public static com.tendermint.types.EventDataRoundState getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }

  private static final com.google.protobuf.Parser<EventDataRoundState>
      PARSER = new com.google.protobuf.AbstractParser<EventDataRoundState>() {
    @java.lang.Override
    public EventDataRoundState parsePartialFrom(
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

  public static com.google.protobuf.Parser<EventDataRoundState> parser() {
    return PARSER;
  }

  @java.lang.Override
  public com.google.protobuf.Parser<EventDataRoundState> getParserForType() {
    return PARSER;
  }

  @java.lang.Override
  public com.tendermint.types.EventDataRoundState getDefaultInstanceForType() {
    return DEFAULT_INSTANCE;
  }

}
