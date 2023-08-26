// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: tendermint/consensus/types.proto

package com.tendermint.consensus;

/**
 * <pre>
 * NewRoundStep is sent for every step taken in the ConsensusState.
 * For every height/round/step transition
 * </pre>
 *
 * Protobuf type {@code tendermint.consensus.NewRoundStep}
 */
public final class NewRoundStep extends
    com.google.protobuf.GeneratedMessageV3 implements
    // @@protoc_insertion_point(message_implements:tendermint.consensus.NewRoundStep)
    NewRoundStepOrBuilder {
private static final long serialVersionUID = 0L;
  // Use NewRoundStep.newBuilder() to construct.
  private NewRoundStep(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
    super(builder);
  }
  private NewRoundStep() {
  }

  @java.lang.Override
  @SuppressWarnings({"unused"})
  protected java.lang.Object newInstance(
      UnusedPrivateParameter unused) {
    return new NewRoundStep();
  }

  public static final com.google.protobuf.Descriptors.Descriptor
      getDescriptor() {
    return com.tendermint.consensus.TypesProto.internal_static_tendermint_consensus_NewRoundStep_descriptor;
  }

  @java.lang.Override
  protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internalGetFieldAccessorTable() {
    return com.tendermint.consensus.TypesProto.internal_static_tendermint_consensus_NewRoundStep_fieldAccessorTable
        .ensureFieldAccessorsInitialized(
            com.tendermint.consensus.NewRoundStep.class, com.tendermint.consensus.NewRoundStep.Builder.class);
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
  private int step_ = 0;
  /**
   * <code>uint32 step = 3 [json_name = "step"];</code>
   * @return The step.
   */
  @java.lang.Override
  public int getStep() {
    return step_;
  }

  public static final int SECONDS_SINCE_START_TIME_FIELD_NUMBER = 4;
  private long secondsSinceStartTime_ = 0L;
  /**
   * <code>int64 seconds_since_start_time = 4 [json_name = "secondsSinceStartTime"];</code>
   * @return The secondsSinceStartTime.
   */
  @java.lang.Override
  public long getSecondsSinceStartTime() {
    return secondsSinceStartTime_;
  }

  public static final int LAST_COMMIT_ROUND_FIELD_NUMBER = 5;
  private int lastCommitRound_ = 0;
  /**
   * <code>int32 last_commit_round = 5 [json_name = "lastCommitRound"];</code>
   * @return The lastCommitRound.
   */
  @java.lang.Override
  public int getLastCommitRound() {
    return lastCommitRound_;
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
    if (step_ != 0) {
      output.writeUInt32(3, step_);
    }
    if (secondsSinceStartTime_ != 0L) {
      output.writeInt64(4, secondsSinceStartTime_);
    }
    if (lastCommitRound_ != 0) {
      output.writeInt32(5, lastCommitRound_);
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
    if (step_ != 0) {
      size += com.google.protobuf.CodedOutputStream
        .computeUInt32Size(3, step_);
    }
    if (secondsSinceStartTime_ != 0L) {
      size += com.google.protobuf.CodedOutputStream
        .computeInt64Size(4, secondsSinceStartTime_);
    }
    if (lastCommitRound_ != 0) {
      size += com.google.protobuf.CodedOutputStream
        .computeInt32Size(5, lastCommitRound_);
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
    if (!(obj instanceof com.tendermint.consensus.NewRoundStep)) {
      return super.equals(obj);
    }
    com.tendermint.consensus.NewRoundStep other = (com.tendermint.consensus.NewRoundStep) obj;

    if (getHeight()
        != other.getHeight()) return false;
    if (getRound()
        != other.getRound()) return false;
    if (getStep()
        != other.getStep()) return false;
    if (getSecondsSinceStartTime()
        != other.getSecondsSinceStartTime()) return false;
    if (getLastCommitRound()
        != other.getLastCommitRound()) return false;
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
    hash = (53 * hash) + getStep();
    hash = (37 * hash) + SECONDS_SINCE_START_TIME_FIELD_NUMBER;
    hash = (53 * hash) + com.google.protobuf.Internal.hashLong(
        getSecondsSinceStartTime());
    hash = (37 * hash) + LAST_COMMIT_ROUND_FIELD_NUMBER;
    hash = (53 * hash) + getLastCommitRound();
    hash = (29 * hash) + getUnknownFields().hashCode();
    memoizedHashCode = hash;
    return hash;
  }

  public static com.tendermint.consensus.NewRoundStep parseFrom(
      java.nio.ByteBuffer data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static com.tendermint.consensus.NewRoundStep parseFrom(
      java.nio.ByteBuffer data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static com.tendermint.consensus.NewRoundStep parseFrom(
      com.google.protobuf.ByteString data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static com.tendermint.consensus.NewRoundStep parseFrom(
      com.google.protobuf.ByteString data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static com.tendermint.consensus.NewRoundStep parseFrom(byte[] data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static com.tendermint.consensus.NewRoundStep parseFrom(
      byte[] data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static com.tendermint.consensus.NewRoundStep parseFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static com.tendermint.consensus.NewRoundStep parseFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input, extensionRegistry);
  }
  public static com.tendermint.consensus.NewRoundStep parseDelimitedFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input);
  }
  public static com.tendermint.consensus.NewRoundStep parseDelimitedFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
  }
  public static com.tendermint.consensus.NewRoundStep parseFrom(
      com.google.protobuf.CodedInputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static com.tendermint.consensus.NewRoundStep parseFrom(
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
  public static Builder newBuilder(com.tendermint.consensus.NewRoundStep prototype) {
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
   * NewRoundStep is sent for every step taken in the ConsensusState.
   * For every height/round/step transition
   * </pre>
   *
   * Protobuf type {@code tendermint.consensus.NewRoundStep}
   */
  public static final class Builder extends
      com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
      // @@protoc_insertion_point(builder_implements:tendermint.consensus.NewRoundStep)
      com.tendermint.consensus.NewRoundStepOrBuilder {
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return com.tendermint.consensus.TypesProto.internal_static_tendermint_consensus_NewRoundStep_descriptor;
    }

    @java.lang.Override
    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return com.tendermint.consensus.TypesProto.internal_static_tendermint_consensus_NewRoundStep_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              com.tendermint.consensus.NewRoundStep.class, com.tendermint.consensus.NewRoundStep.Builder.class);
    }

    // Construct using com.tendermint.consensus.NewRoundStep.newBuilder()
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
      step_ = 0;
      secondsSinceStartTime_ = 0L;
      lastCommitRound_ = 0;
      return this;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.Descriptor
        getDescriptorForType() {
      return com.tendermint.consensus.TypesProto.internal_static_tendermint_consensus_NewRoundStep_descriptor;
    }

    @java.lang.Override
    public com.tendermint.consensus.NewRoundStep getDefaultInstanceForType() {
      return com.tendermint.consensus.NewRoundStep.getDefaultInstance();
    }

    @java.lang.Override
    public com.tendermint.consensus.NewRoundStep build() {
      com.tendermint.consensus.NewRoundStep result = buildPartial();
      if (!result.isInitialized()) {
        throw newUninitializedMessageException(result);
      }
      return result;
    }

    @java.lang.Override
    public com.tendermint.consensus.NewRoundStep buildPartial() {
      com.tendermint.consensus.NewRoundStep result = new com.tendermint.consensus.NewRoundStep(this);
      if (bitField0_ != 0) { buildPartial0(result); }
      onBuilt();
      return result;
    }

    private void buildPartial0(com.tendermint.consensus.NewRoundStep result) {
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
      if (((from_bitField0_ & 0x00000008) != 0)) {
        result.secondsSinceStartTime_ = secondsSinceStartTime_;
      }
      if (((from_bitField0_ & 0x00000010) != 0)) {
        result.lastCommitRound_ = lastCommitRound_;
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
      if (other instanceof com.tendermint.consensus.NewRoundStep) {
        return mergeFrom((com.tendermint.consensus.NewRoundStep)other);
      } else {
        super.mergeFrom(other);
        return this;
      }
    }

    public Builder mergeFrom(com.tendermint.consensus.NewRoundStep other) {
      if (other == com.tendermint.consensus.NewRoundStep.getDefaultInstance()) return this;
      if (other.getHeight() != 0L) {
        setHeight(other.getHeight());
      }
      if (other.getRound() != 0) {
        setRound(other.getRound());
      }
      if (other.getStep() != 0) {
        setStep(other.getStep());
      }
      if (other.getSecondsSinceStartTime() != 0L) {
        setSecondsSinceStartTime(other.getSecondsSinceStartTime());
      }
      if (other.getLastCommitRound() != 0) {
        setLastCommitRound(other.getLastCommitRound());
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
            case 24: {
              step_ = input.readUInt32();
              bitField0_ |= 0x00000004;
              break;
            } // case 24
            case 32: {
              secondsSinceStartTime_ = input.readInt64();
              bitField0_ |= 0x00000008;
              break;
            } // case 32
            case 40: {
              lastCommitRound_ = input.readInt32();
              bitField0_ |= 0x00000010;
              break;
            } // case 40
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

    private int step_ ;
    /**
     * <code>uint32 step = 3 [json_name = "step"];</code>
     * @return The step.
     */
    @java.lang.Override
    public int getStep() {
      return step_;
    }
    /**
     * <code>uint32 step = 3 [json_name = "step"];</code>
     * @param value The step to set.
     * @return This builder for chaining.
     */
    public Builder setStep(int value) {

      step_ = value;
      bitField0_ |= 0x00000004;
      onChanged();
      return this;
    }
    /**
     * <code>uint32 step = 3 [json_name = "step"];</code>
     * @return This builder for chaining.
     */
    public Builder clearStep() {
      bitField0_ = (bitField0_ & ~0x00000004);
      step_ = 0;
      onChanged();
      return this;
    }

    private long secondsSinceStartTime_ ;
    /**
     * <code>int64 seconds_since_start_time = 4 [json_name = "secondsSinceStartTime"];</code>
     * @return The secondsSinceStartTime.
     */
    @java.lang.Override
    public long getSecondsSinceStartTime() {
      return secondsSinceStartTime_;
    }
    /**
     * <code>int64 seconds_since_start_time = 4 [json_name = "secondsSinceStartTime"];</code>
     * @param value The secondsSinceStartTime to set.
     * @return This builder for chaining.
     */
    public Builder setSecondsSinceStartTime(long value) {

      secondsSinceStartTime_ = value;
      bitField0_ |= 0x00000008;
      onChanged();
      return this;
    }
    /**
     * <code>int64 seconds_since_start_time = 4 [json_name = "secondsSinceStartTime"];</code>
     * @return This builder for chaining.
     */
    public Builder clearSecondsSinceStartTime() {
      bitField0_ = (bitField0_ & ~0x00000008);
      secondsSinceStartTime_ = 0L;
      onChanged();
      return this;
    }

    private int lastCommitRound_ ;
    /**
     * <code>int32 last_commit_round = 5 [json_name = "lastCommitRound"];</code>
     * @return The lastCommitRound.
     */
    @java.lang.Override
    public int getLastCommitRound() {
      return lastCommitRound_;
    }
    /**
     * <code>int32 last_commit_round = 5 [json_name = "lastCommitRound"];</code>
     * @param value The lastCommitRound to set.
     * @return This builder for chaining.
     */
    public Builder setLastCommitRound(int value) {

      lastCommitRound_ = value;
      bitField0_ |= 0x00000010;
      onChanged();
      return this;
    }
    /**
     * <code>int32 last_commit_round = 5 [json_name = "lastCommitRound"];</code>
     * @return This builder for chaining.
     */
    public Builder clearLastCommitRound() {
      bitField0_ = (bitField0_ & ~0x00000010);
      lastCommitRound_ = 0;
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


    // @@protoc_insertion_point(builder_scope:tendermint.consensus.NewRoundStep)
  }

  // @@protoc_insertion_point(class_scope:tendermint.consensus.NewRoundStep)
  private static final com.tendermint.consensus.NewRoundStep DEFAULT_INSTANCE;
  static {
    DEFAULT_INSTANCE = new com.tendermint.consensus.NewRoundStep();
  }

  public static com.tendermint.consensus.NewRoundStep getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }

  private static final com.google.protobuf.Parser<NewRoundStep>
      PARSER = new com.google.protobuf.AbstractParser<NewRoundStep>() {
    @java.lang.Override
    public NewRoundStep parsePartialFrom(
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

  public static com.google.protobuf.Parser<NewRoundStep> parser() {
    return PARSER;
  }

  @java.lang.Override
  public com.google.protobuf.Parser<NewRoundStep> getParserForType() {
    return PARSER;
  }

  @java.lang.Override
  public com.tendermint.consensus.NewRoundStep getDefaultInstanceForType() {
    return DEFAULT_INSTANCE;
  }

}

