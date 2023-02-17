// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: cosmos/distribution/v1beta1/genesis.proto

package com.cosmos.distribution.v1beta1;

/**
 * <pre>
 * DelegatorStartingInfoRecord used for import / export via genesis json.
 * </pre>
 *
 * Protobuf type {@code cosmos.distribution.v1beta1.DelegatorStartingInfoRecord}
 */
public final class DelegatorStartingInfoRecord extends
    com.google.protobuf.GeneratedMessageV3 implements
    // @@protoc_insertion_point(message_implements:cosmos.distribution.v1beta1.DelegatorStartingInfoRecord)
    DelegatorStartingInfoRecordOrBuilder {
private static final long serialVersionUID = 0L;
  // Use DelegatorStartingInfoRecord.newBuilder() to construct.
  private DelegatorStartingInfoRecord(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
    super(builder);
  }
  private DelegatorStartingInfoRecord() {
    delegatorAddress_ = "";
    validatorAddress_ = "";
  }

  @java.lang.Override
  @SuppressWarnings({"unused"})
  protected java.lang.Object newInstance(
      UnusedPrivateParameter unused) {
    return new DelegatorStartingInfoRecord();
  }

  public static final com.google.protobuf.Descriptors.Descriptor
      getDescriptor() {
    return com.cosmos.distribution.v1beta1.GenesisProto.internal_static_cosmos_distribution_v1beta1_DelegatorStartingInfoRecord_descriptor;
  }

  @java.lang.Override
  protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internalGetFieldAccessorTable() {
    return com.cosmos.distribution.v1beta1.GenesisProto.internal_static_cosmos_distribution_v1beta1_DelegatorStartingInfoRecord_fieldAccessorTable
        .ensureFieldAccessorsInitialized(
            com.cosmos.distribution.v1beta1.DelegatorStartingInfoRecord.class, com.cosmos.distribution.v1beta1.DelegatorStartingInfoRecord.Builder.class);
  }

  public static final int DELEGATOR_ADDRESS_FIELD_NUMBER = 1;
  @SuppressWarnings("serial")
  private volatile java.lang.Object delegatorAddress_ = "";
  /**
   * <pre>
   * delegator_address is the address of the delegator.
   * </pre>
   *
   * <code>string delegator_address = 1 [json_name = "delegatorAddress", (.gogoproto.moretags) = "yaml:&#92;"delegator_address&#92;""];</code>
   * @return The delegatorAddress.
   */
  @java.lang.Override
  public java.lang.String getDelegatorAddress() {
    java.lang.Object ref = delegatorAddress_;
    if (ref instanceof java.lang.String) {
      return (java.lang.String) ref;
    } else {
      com.google.protobuf.ByteString bs = 
          (com.google.protobuf.ByteString) ref;
      java.lang.String s = bs.toStringUtf8();
      delegatorAddress_ = s;
      return s;
    }
  }
  /**
   * <pre>
   * delegator_address is the address of the delegator.
   * </pre>
   *
   * <code>string delegator_address = 1 [json_name = "delegatorAddress", (.gogoproto.moretags) = "yaml:&#92;"delegator_address&#92;""];</code>
   * @return The bytes for delegatorAddress.
   */
  @java.lang.Override
  public com.google.protobuf.ByteString
      getDelegatorAddressBytes() {
    java.lang.Object ref = delegatorAddress_;
    if (ref instanceof java.lang.String) {
      com.google.protobuf.ByteString b = 
          com.google.protobuf.ByteString.copyFromUtf8(
              (java.lang.String) ref);
      delegatorAddress_ = b;
      return b;
    } else {
      return (com.google.protobuf.ByteString) ref;
    }
  }

  public static final int VALIDATOR_ADDRESS_FIELD_NUMBER = 2;
  @SuppressWarnings("serial")
  private volatile java.lang.Object validatorAddress_ = "";
  /**
   * <pre>
   * validator_address is the address of the validator.
   * </pre>
   *
   * <code>string validator_address = 2 [json_name = "validatorAddress", (.gogoproto.moretags) = "yaml:&#92;"validator_address&#92;""];</code>
   * @return The validatorAddress.
   */
  @java.lang.Override
  public java.lang.String getValidatorAddress() {
    java.lang.Object ref = validatorAddress_;
    if (ref instanceof java.lang.String) {
      return (java.lang.String) ref;
    } else {
      com.google.protobuf.ByteString bs = 
          (com.google.protobuf.ByteString) ref;
      java.lang.String s = bs.toStringUtf8();
      validatorAddress_ = s;
      return s;
    }
  }
  /**
   * <pre>
   * validator_address is the address of the validator.
   * </pre>
   *
   * <code>string validator_address = 2 [json_name = "validatorAddress", (.gogoproto.moretags) = "yaml:&#92;"validator_address&#92;""];</code>
   * @return The bytes for validatorAddress.
   */
  @java.lang.Override
  public com.google.protobuf.ByteString
      getValidatorAddressBytes() {
    java.lang.Object ref = validatorAddress_;
    if (ref instanceof java.lang.String) {
      com.google.protobuf.ByteString b = 
          com.google.protobuf.ByteString.copyFromUtf8(
              (java.lang.String) ref);
      validatorAddress_ = b;
      return b;
    } else {
      return (com.google.protobuf.ByteString) ref;
    }
  }

  public static final int STARTING_INFO_FIELD_NUMBER = 3;
  private com.cosmos.distribution.v1beta1.DelegatorStartingInfo startingInfo_;
  /**
   * <pre>
   * starting_info defines the starting info of a delegator.
   * </pre>
   *
   * <code>.cosmos.distribution.v1beta1.DelegatorStartingInfo starting_info = 3 [json_name = "startingInfo", (.gogoproto.nullable) = false, (.gogoproto.moretags) = "yaml:&#92;"starting_info&#92;""];</code>
   * @return Whether the startingInfo field is set.
   */
  @java.lang.Override
  public boolean hasStartingInfo() {
    return startingInfo_ != null;
  }
  /**
   * <pre>
   * starting_info defines the starting info of a delegator.
   * </pre>
   *
   * <code>.cosmos.distribution.v1beta1.DelegatorStartingInfo starting_info = 3 [json_name = "startingInfo", (.gogoproto.nullable) = false, (.gogoproto.moretags) = "yaml:&#92;"starting_info&#92;""];</code>
   * @return The startingInfo.
   */
  @java.lang.Override
  public com.cosmos.distribution.v1beta1.DelegatorStartingInfo getStartingInfo() {
    return startingInfo_ == null ? com.cosmos.distribution.v1beta1.DelegatorStartingInfo.getDefaultInstance() : startingInfo_;
  }
  /**
   * <pre>
   * starting_info defines the starting info of a delegator.
   * </pre>
   *
   * <code>.cosmos.distribution.v1beta1.DelegatorStartingInfo starting_info = 3 [json_name = "startingInfo", (.gogoproto.nullable) = false, (.gogoproto.moretags) = "yaml:&#92;"starting_info&#92;""];</code>
   */
  @java.lang.Override
  public com.cosmos.distribution.v1beta1.DelegatorStartingInfoOrBuilder getStartingInfoOrBuilder() {
    return startingInfo_ == null ? com.cosmos.distribution.v1beta1.DelegatorStartingInfo.getDefaultInstance() : startingInfo_;
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
    if (!com.google.protobuf.GeneratedMessageV3.isStringEmpty(delegatorAddress_)) {
      com.google.protobuf.GeneratedMessageV3.writeString(output, 1, delegatorAddress_);
    }
    if (!com.google.protobuf.GeneratedMessageV3.isStringEmpty(validatorAddress_)) {
      com.google.protobuf.GeneratedMessageV3.writeString(output, 2, validatorAddress_);
    }
    if (startingInfo_ != null) {
      output.writeMessage(3, getStartingInfo());
    }
    getUnknownFields().writeTo(output);
  }

  @java.lang.Override
  public int getSerializedSize() {
    int size = memoizedSize;
    if (size != -1) return size;

    size = 0;
    if (!com.google.protobuf.GeneratedMessageV3.isStringEmpty(delegatorAddress_)) {
      size += com.google.protobuf.GeneratedMessageV3.computeStringSize(1, delegatorAddress_);
    }
    if (!com.google.protobuf.GeneratedMessageV3.isStringEmpty(validatorAddress_)) {
      size += com.google.protobuf.GeneratedMessageV3.computeStringSize(2, validatorAddress_);
    }
    if (startingInfo_ != null) {
      size += com.google.protobuf.CodedOutputStream
        .computeMessageSize(3, getStartingInfo());
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
    if (!(obj instanceof com.cosmos.distribution.v1beta1.DelegatorStartingInfoRecord)) {
      return super.equals(obj);
    }
    com.cosmos.distribution.v1beta1.DelegatorStartingInfoRecord other = (com.cosmos.distribution.v1beta1.DelegatorStartingInfoRecord) obj;

    if (!getDelegatorAddress()
        .equals(other.getDelegatorAddress())) return false;
    if (!getValidatorAddress()
        .equals(other.getValidatorAddress())) return false;
    if (hasStartingInfo() != other.hasStartingInfo()) return false;
    if (hasStartingInfo()) {
      if (!getStartingInfo()
          .equals(other.getStartingInfo())) return false;
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
    hash = (37 * hash) + DELEGATOR_ADDRESS_FIELD_NUMBER;
    hash = (53 * hash) + getDelegatorAddress().hashCode();
    hash = (37 * hash) + VALIDATOR_ADDRESS_FIELD_NUMBER;
    hash = (53 * hash) + getValidatorAddress().hashCode();
    if (hasStartingInfo()) {
      hash = (37 * hash) + STARTING_INFO_FIELD_NUMBER;
      hash = (53 * hash) + getStartingInfo().hashCode();
    }
    hash = (29 * hash) + getUnknownFields().hashCode();
    memoizedHashCode = hash;
    return hash;
  }

  public static com.cosmos.distribution.v1beta1.DelegatorStartingInfoRecord parseFrom(
      java.nio.ByteBuffer data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static com.cosmos.distribution.v1beta1.DelegatorStartingInfoRecord parseFrom(
      java.nio.ByteBuffer data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static com.cosmos.distribution.v1beta1.DelegatorStartingInfoRecord parseFrom(
      com.google.protobuf.ByteString data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static com.cosmos.distribution.v1beta1.DelegatorStartingInfoRecord parseFrom(
      com.google.protobuf.ByteString data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static com.cosmos.distribution.v1beta1.DelegatorStartingInfoRecord parseFrom(byte[] data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static com.cosmos.distribution.v1beta1.DelegatorStartingInfoRecord parseFrom(
      byte[] data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static com.cosmos.distribution.v1beta1.DelegatorStartingInfoRecord parseFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static com.cosmos.distribution.v1beta1.DelegatorStartingInfoRecord parseFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input, extensionRegistry);
  }
  public static com.cosmos.distribution.v1beta1.DelegatorStartingInfoRecord parseDelimitedFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input);
  }
  public static com.cosmos.distribution.v1beta1.DelegatorStartingInfoRecord parseDelimitedFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
  }
  public static com.cosmos.distribution.v1beta1.DelegatorStartingInfoRecord parseFrom(
      com.google.protobuf.CodedInputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static com.cosmos.distribution.v1beta1.DelegatorStartingInfoRecord parseFrom(
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
  public static Builder newBuilder(com.cosmos.distribution.v1beta1.DelegatorStartingInfoRecord prototype) {
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
   * DelegatorStartingInfoRecord used for import / export via genesis json.
   * </pre>
   *
   * Protobuf type {@code cosmos.distribution.v1beta1.DelegatorStartingInfoRecord}
   */
  public static final class Builder extends
      com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
      // @@protoc_insertion_point(builder_implements:cosmos.distribution.v1beta1.DelegatorStartingInfoRecord)
      com.cosmos.distribution.v1beta1.DelegatorStartingInfoRecordOrBuilder {
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return com.cosmos.distribution.v1beta1.GenesisProto.internal_static_cosmos_distribution_v1beta1_DelegatorStartingInfoRecord_descriptor;
    }

    @java.lang.Override
    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return com.cosmos.distribution.v1beta1.GenesisProto.internal_static_cosmos_distribution_v1beta1_DelegatorStartingInfoRecord_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              com.cosmos.distribution.v1beta1.DelegatorStartingInfoRecord.class, com.cosmos.distribution.v1beta1.DelegatorStartingInfoRecord.Builder.class);
    }

    // Construct using com.cosmos.distribution.v1beta1.DelegatorStartingInfoRecord.newBuilder()
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
      delegatorAddress_ = "";
      validatorAddress_ = "";
      startingInfo_ = null;
      if (startingInfoBuilder_ != null) {
        startingInfoBuilder_.dispose();
        startingInfoBuilder_ = null;
      }
      return this;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.Descriptor
        getDescriptorForType() {
      return com.cosmos.distribution.v1beta1.GenesisProto.internal_static_cosmos_distribution_v1beta1_DelegatorStartingInfoRecord_descriptor;
    }

    @java.lang.Override
    public com.cosmos.distribution.v1beta1.DelegatorStartingInfoRecord getDefaultInstanceForType() {
      return com.cosmos.distribution.v1beta1.DelegatorStartingInfoRecord.getDefaultInstance();
    }

    @java.lang.Override
    public com.cosmos.distribution.v1beta1.DelegatorStartingInfoRecord build() {
      com.cosmos.distribution.v1beta1.DelegatorStartingInfoRecord result = buildPartial();
      if (!result.isInitialized()) {
        throw newUninitializedMessageException(result);
      }
      return result;
    }

    @java.lang.Override
    public com.cosmos.distribution.v1beta1.DelegatorStartingInfoRecord buildPartial() {
      com.cosmos.distribution.v1beta1.DelegatorStartingInfoRecord result = new com.cosmos.distribution.v1beta1.DelegatorStartingInfoRecord(this);
      if (bitField0_ != 0) { buildPartial0(result); }
      onBuilt();
      return result;
    }

    private void buildPartial0(com.cosmos.distribution.v1beta1.DelegatorStartingInfoRecord result) {
      int from_bitField0_ = bitField0_;
      if (((from_bitField0_ & 0x00000001) != 0)) {
        result.delegatorAddress_ = delegatorAddress_;
      }
      if (((from_bitField0_ & 0x00000002) != 0)) {
        result.validatorAddress_ = validatorAddress_;
      }
      if (((from_bitField0_ & 0x00000004) != 0)) {
        result.startingInfo_ = startingInfoBuilder_ == null
            ? startingInfo_
            : startingInfoBuilder_.build();
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
      if (other instanceof com.cosmos.distribution.v1beta1.DelegatorStartingInfoRecord) {
        return mergeFrom((com.cosmos.distribution.v1beta1.DelegatorStartingInfoRecord)other);
      } else {
        super.mergeFrom(other);
        return this;
      }
    }

    public Builder mergeFrom(com.cosmos.distribution.v1beta1.DelegatorStartingInfoRecord other) {
      if (other == com.cosmos.distribution.v1beta1.DelegatorStartingInfoRecord.getDefaultInstance()) return this;
      if (!other.getDelegatorAddress().isEmpty()) {
        delegatorAddress_ = other.delegatorAddress_;
        bitField0_ |= 0x00000001;
        onChanged();
      }
      if (!other.getValidatorAddress().isEmpty()) {
        validatorAddress_ = other.validatorAddress_;
        bitField0_ |= 0x00000002;
        onChanged();
      }
      if (other.hasStartingInfo()) {
        mergeStartingInfo(other.getStartingInfo());
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
              delegatorAddress_ = input.readStringRequireUtf8();
              bitField0_ |= 0x00000001;
              break;
            } // case 10
            case 18: {
              validatorAddress_ = input.readStringRequireUtf8();
              bitField0_ |= 0x00000002;
              break;
            } // case 18
            case 26: {
              input.readMessage(
                  getStartingInfoFieldBuilder().getBuilder(),
                  extensionRegistry);
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

    private java.lang.Object delegatorAddress_ = "";
    /**
     * <pre>
     * delegator_address is the address of the delegator.
     * </pre>
     *
     * <code>string delegator_address = 1 [json_name = "delegatorAddress", (.gogoproto.moretags) = "yaml:&#92;"delegator_address&#92;""];</code>
     * @return The delegatorAddress.
     */
    public java.lang.String getDelegatorAddress() {
      java.lang.Object ref = delegatorAddress_;
      if (!(ref instanceof java.lang.String)) {
        com.google.protobuf.ByteString bs =
            (com.google.protobuf.ByteString) ref;
        java.lang.String s = bs.toStringUtf8();
        delegatorAddress_ = s;
        return s;
      } else {
        return (java.lang.String) ref;
      }
    }
    /**
     * <pre>
     * delegator_address is the address of the delegator.
     * </pre>
     *
     * <code>string delegator_address = 1 [json_name = "delegatorAddress", (.gogoproto.moretags) = "yaml:&#92;"delegator_address&#92;""];</code>
     * @return The bytes for delegatorAddress.
     */
    public com.google.protobuf.ByteString
        getDelegatorAddressBytes() {
      java.lang.Object ref = delegatorAddress_;
      if (ref instanceof String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8(
                (java.lang.String) ref);
        delegatorAddress_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }
    /**
     * <pre>
     * delegator_address is the address of the delegator.
     * </pre>
     *
     * <code>string delegator_address = 1 [json_name = "delegatorAddress", (.gogoproto.moretags) = "yaml:&#92;"delegator_address&#92;""];</code>
     * @param value The delegatorAddress to set.
     * @return This builder for chaining.
     */
    public Builder setDelegatorAddress(
        java.lang.String value) {
      if (value == null) { throw new NullPointerException(); }
      delegatorAddress_ = value;
      bitField0_ |= 0x00000001;
      onChanged();
      return this;
    }
    /**
     * <pre>
     * delegator_address is the address of the delegator.
     * </pre>
     *
     * <code>string delegator_address = 1 [json_name = "delegatorAddress", (.gogoproto.moretags) = "yaml:&#92;"delegator_address&#92;""];</code>
     * @return This builder for chaining.
     */
    public Builder clearDelegatorAddress() {
      delegatorAddress_ = getDefaultInstance().getDelegatorAddress();
      bitField0_ = (bitField0_ & ~0x00000001);
      onChanged();
      return this;
    }
    /**
     * <pre>
     * delegator_address is the address of the delegator.
     * </pre>
     *
     * <code>string delegator_address = 1 [json_name = "delegatorAddress", (.gogoproto.moretags) = "yaml:&#92;"delegator_address&#92;""];</code>
     * @param value The bytes for delegatorAddress to set.
     * @return This builder for chaining.
     */
    public Builder setDelegatorAddressBytes(
        com.google.protobuf.ByteString value) {
      if (value == null) { throw new NullPointerException(); }
      checkByteStringIsUtf8(value);
      delegatorAddress_ = value;
      bitField0_ |= 0x00000001;
      onChanged();
      return this;
    }

    private java.lang.Object validatorAddress_ = "";
    /**
     * <pre>
     * validator_address is the address of the validator.
     * </pre>
     *
     * <code>string validator_address = 2 [json_name = "validatorAddress", (.gogoproto.moretags) = "yaml:&#92;"validator_address&#92;""];</code>
     * @return The validatorAddress.
     */
    public java.lang.String getValidatorAddress() {
      java.lang.Object ref = validatorAddress_;
      if (!(ref instanceof java.lang.String)) {
        com.google.protobuf.ByteString bs =
            (com.google.protobuf.ByteString) ref;
        java.lang.String s = bs.toStringUtf8();
        validatorAddress_ = s;
        return s;
      } else {
        return (java.lang.String) ref;
      }
    }
    /**
     * <pre>
     * validator_address is the address of the validator.
     * </pre>
     *
     * <code>string validator_address = 2 [json_name = "validatorAddress", (.gogoproto.moretags) = "yaml:&#92;"validator_address&#92;""];</code>
     * @return The bytes for validatorAddress.
     */
    public com.google.protobuf.ByteString
        getValidatorAddressBytes() {
      java.lang.Object ref = validatorAddress_;
      if (ref instanceof String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8(
                (java.lang.String) ref);
        validatorAddress_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }
    /**
     * <pre>
     * validator_address is the address of the validator.
     * </pre>
     *
     * <code>string validator_address = 2 [json_name = "validatorAddress", (.gogoproto.moretags) = "yaml:&#92;"validator_address&#92;""];</code>
     * @param value The validatorAddress to set.
     * @return This builder for chaining.
     */
    public Builder setValidatorAddress(
        java.lang.String value) {
      if (value == null) { throw new NullPointerException(); }
      validatorAddress_ = value;
      bitField0_ |= 0x00000002;
      onChanged();
      return this;
    }
    /**
     * <pre>
     * validator_address is the address of the validator.
     * </pre>
     *
     * <code>string validator_address = 2 [json_name = "validatorAddress", (.gogoproto.moretags) = "yaml:&#92;"validator_address&#92;""];</code>
     * @return This builder for chaining.
     */
    public Builder clearValidatorAddress() {
      validatorAddress_ = getDefaultInstance().getValidatorAddress();
      bitField0_ = (bitField0_ & ~0x00000002);
      onChanged();
      return this;
    }
    /**
     * <pre>
     * validator_address is the address of the validator.
     * </pre>
     *
     * <code>string validator_address = 2 [json_name = "validatorAddress", (.gogoproto.moretags) = "yaml:&#92;"validator_address&#92;""];</code>
     * @param value The bytes for validatorAddress to set.
     * @return This builder for chaining.
     */
    public Builder setValidatorAddressBytes(
        com.google.protobuf.ByteString value) {
      if (value == null) { throw new NullPointerException(); }
      checkByteStringIsUtf8(value);
      validatorAddress_ = value;
      bitField0_ |= 0x00000002;
      onChanged();
      return this;
    }

    private com.cosmos.distribution.v1beta1.DelegatorStartingInfo startingInfo_;
    private com.google.protobuf.SingleFieldBuilderV3<
        com.cosmos.distribution.v1beta1.DelegatorStartingInfo, com.cosmos.distribution.v1beta1.DelegatorStartingInfo.Builder, com.cosmos.distribution.v1beta1.DelegatorStartingInfoOrBuilder> startingInfoBuilder_;
    /**
     * <pre>
     * starting_info defines the starting info of a delegator.
     * </pre>
     *
     * <code>.cosmos.distribution.v1beta1.DelegatorStartingInfo starting_info = 3 [json_name = "startingInfo", (.gogoproto.nullable) = false, (.gogoproto.moretags) = "yaml:&#92;"starting_info&#92;""];</code>
     * @return Whether the startingInfo field is set.
     */
    public boolean hasStartingInfo() {
      return ((bitField0_ & 0x00000004) != 0);
    }
    /**
     * <pre>
     * starting_info defines the starting info of a delegator.
     * </pre>
     *
     * <code>.cosmos.distribution.v1beta1.DelegatorStartingInfo starting_info = 3 [json_name = "startingInfo", (.gogoproto.nullable) = false, (.gogoproto.moretags) = "yaml:&#92;"starting_info&#92;""];</code>
     * @return The startingInfo.
     */
    public com.cosmos.distribution.v1beta1.DelegatorStartingInfo getStartingInfo() {
      if (startingInfoBuilder_ == null) {
        return startingInfo_ == null ? com.cosmos.distribution.v1beta1.DelegatorStartingInfo.getDefaultInstance() : startingInfo_;
      } else {
        return startingInfoBuilder_.getMessage();
      }
    }
    /**
     * <pre>
     * starting_info defines the starting info of a delegator.
     * </pre>
     *
     * <code>.cosmos.distribution.v1beta1.DelegatorStartingInfo starting_info = 3 [json_name = "startingInfo", (.gogoproto.nullable) = false, (.gogoproto.moretags) = "yaml:&#92;"starting_info&#92;""];</code>
     */
    public Builder setStartingInfo(com.cosmos.distribution.v1beta1.DelegatorStartingInfo value) {
      if (startingInfoBuilder_ == null) {
        if (value == null) {
          throw new NullPointerException();
        }
        startingInfo_ = value;
      } else {
        startingInfoBuilder_.setMessage(value);
      }
      bitField0_ |= 0x00000004;
      onChanged();
      return this;
    }
    /**
     * <pre>
     * starting_info defines the starting info of a delegator.
     * </pre>
     *
     * <code>.cosmos.distribution.v1beta1.DelegatorStartingInfo starting_info = 3 [json_name = "startingInfo", (.gogoproto.nullable) = false, (.gogoproto.moretags) = "yaml:&#92;"starting_info&#92;""];</code>
     */
    public Builder setStartingInfo(
        com.cosmos.distribution.v1beta1.DelegatorStartingInfo.Builder builderForValue) {
      if (startingInfoBuilder_ == null) {
        startingInfo_ = builderForValue.build();
      } else {
        startingInfoBuilder_.setMessage(builderForValue.build());
      }
      bitField0_ |= 0x00000004;
      onChanged();
      return this;
    }
    /**
     * <pre>
     * starting_info defines the starting info of a delegator.
     * </pre>
     *
     * <code>.cosmos.distribution.v1beta1.DelegatorStartingInfo starting_info = 3 [json_name = "startingInfo", (.gogoproto.nullable) = false, (.gogoproto.moretags) = "yaml:&#92;"starting_info&#92;""];</code>
     */
    public Builder mergeStartingInfo(com.cosmos.distribution.v1beta1.DelegatorStartingInfo value) {
      if (startingInfoBuilder_ == null) {
        if (((bitField0_ & 0x00000004) != 0) &&
          startingInfo_ != null &&
          startingInfo_ != com.cosmos.distribution.v1beta1.DelegatorStartingInfo.getDefaultInstance()) {
          getStartingInfoBuilder().mergeFrom(value);
        } else {
          startingInfo_ = value;
        }
      } else {
        startingInfoBuilder_.mergeFrom(value);
      }
      bitField0_ |= 0x00000004;
      onChanged();
      return this;
    }
    /**
     * <pre>
     * starting_info defines the starting info of a delegator.
     * </pre>
     *
     * <code>.cosmos.distribution.v1beta1.DelegatorStartingInfo starting_info = 3 [json_name = "startingInfo", (.gogoproto.nullable) = false, (.gogoproto.moretags) = "yaml:&#92;"starting_info&#92;""];</code>
     */
    public Builder clearStartingInfo() {
      bitField0_ = (bitField0_ & ~0x00000004);
      startingInfo_ = null;
      if (startingInfoBuilder_ != null) {
        startingInfoBuilder_.dispose();
        startingInfoBuilder_ = null;
      }
      onChanged();
      return this;
    }
    /**
     * <pre>
     * starting_info defines the starting info of a delegator.
     * </pre>
     *
     * <code>.cosmos.distribution.v1beta1.DelegatorStartingInfo starting_info = 3 [json_name = "startingInfo", (.gogoproto.nullable) = false, (.gogoproto.moretags) = "yaml:&#92;"starting_info&#92;""];</code>
     */
    public com.cosmos.distribution.v1beta1.DelegatorStartingInfo.Builder getStartingInfoBuilder() {
      bitField0_ |= 0x00000004;
      onChanged();
      return getStartingInfoFieldBuilder().getBuilder();
    }
    /**
     * <pre>
     * starting_info defines the starting info of a delegator.
     * </pre>
     *
     * <code>.cosmos.distribution.v1beta1.DelegatorStartingInfo starting_info = 3 [json_name = "startingInfo", (.gogoproto.nullable) = false, (.gogoproto.moretags) = "yaml:&#92;"starting_info&#92;""];</code>
     */
    public com.cosmos.distribution.v1beta1.DelegatorStartingInfoOrBuilder getStartingInfoOrBuilder() {
      if (startingInfoBuilder_ != null) {
        return startingInfoBuilder_.getMessageOrBuilder();
      } else {
        return startingInfo_ == null ?
            com.cosmos.distribution.v1beta1.DelegatorStartingInfo.getDefaultInstance() : startingInfo_;
      }
    }
    /**
     * <pre>
     * starting_info defines the starting info of a delegator.
     * </pre>
     *
     * <code>.cosmos.distribution.v1beta1.DelegatorStartingInfo starting_info = 3 [json_name = "startingInfo", (.gogoproto.nullable) = false, (.gogoproto.moretags) = "yaml:&#92;"starting_info&#92;""];</code>
     */
    private com.google.protobuf.SingleFieldBuilderV3<
        com.cosmos.distribution.v1beta1.DelegatorStartingInfo, com.cosmos.distribution.v1beta1.DelegatorStartingInfo.Builder, com.cosmos.distribution.v1beta1.DelegatorStartingInfoOrBuilder> 
        getStartingInfoFieldBuilder() {
      if (startingInfoBuilder_ == null) {
        startingInfoBuilder_ = new com.google.protobuf.SingleFieldBuilderV3<
            com.cosmos.distribution.v1beta1.DelegatorStartingInfo, com.cosmos.distribution.v1beta1.DelegatorStartingInfo.Builder, com.cosmos.distribution.v1beta1.DelegatorStartingInfoOrBuilder>(
                getStartingInfo(),
                getParentForChildren(),
                isClean());
        startingInfo_ = null;
      }
      return startingInfoBuilder_;
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


    // @@protoc_insertion_point(builder_scope:cosmos.distribution.v1beta1.DelegatorStartingInfoRecord)
  }

  // @@protoc_insertion_point(class_scope:cosmos.distribution.v1beta1.DelegatorStartingInfoRecord)
  private static final com.cosmos.distribution.v1beta1.DelegatorStartingInfoRecord DEFAULT_INSTANCE;
  static {
    DEFAULT_INSTANCE = new com.cosmos.distribution.v1beta1.DelegatorStartingInfoRecord();
  }

  public static com.cosmos.distribution.v1beta1.DelegatorStartingInfoRecord getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }

  private static final com.google.protobuf.Parser<DelegatorStartingInfoRecord>
      PARSER = new com.google.protobuf.AbstractParser<DelegatorStartingInfoRecord>() {
    @java.lang.Override
    public DelegatorStartingInfoRecord parsePartialFrom(
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

  public static com.google.protobuf.Parser<DelegatorStartingInfoRecord> parser() {
    return PARSER;
  }

  @java.lang.Override
  public com.google.protobuf.Parser<DelegatorStartingInfoRecord> getParserForType() {
    return PARSER;
  }

  @java.lang.Override
  public com.cosmos.distribution.v1beta1.DelegatorStartingInfoRecord getDefaultInstanceForType() {
    return DEFAULT_INSTANCE;
  }

}

