// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: cosmos/distribution/v1beta1/query.proto

package com.cosmos.distribution.v1beta1;

/**
 * <pre>
 * QueryDelegatorValidatorsResponse is the response type for the
 * Query/DelegatorValidators RPC method.
 * </pre>
 *
 * Protobuf type {@code cosmos.distribution.v1beta1.QueryDelegatorValidatorsResponse}
 */
public final class QueryDelegatorValidatorsResponse extends
    com.google.protobuf.GeneratedMessageV3 implements
    // @@protoc_insertion_point(message_implements:cosmos.distribution.v1beta1.QueryDelegatorValidatorsResponse)
    QueryDelegatorValidatorsResponseOrBuilder {
private static final long serialVersionUID = 0L;
  // Use QueryDelegatorValidatorsResponse.newBuilder() to construct.
  private QueryDelegatorValidatorsResponse(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
    super(builder);
  }
  private QueryDelegatorValidatorsResponse() {
    validators_ = com.google.protobuf.LazyStringArrayList.EMPTY;
  }

  @java.lang.Override
  @SuppressWarnings({"unused"})
  protected java.lang.Object newInstance(
      UnusedPrivateParameter unused) {
    return new QueryDelegatorValidatorsResponse();
  }

  public static final com.google.protobuf.Descriptors.Descriptor
      getDescriptor() {
    return com.cosmos.distribution.v1beta1.QueryProto.internal_static_cosmos_distribution_v1beta1_QueryDelegatorValidatorsResponse_descriptor;
  }

  @java.lang.Override
  protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internalGetFieldAccessorTable() {
    return com.cosmos.distribution.v1beta1.QueryProto.internal_static_cosmos_distribution_v1beta1_QueryDelegatorValidatorsResponse_fieldAccessorTable
        .ensureFieldAccessorsInitialized(
            com.cosmos.distribution.v1beta1.QueryDelegatorValidatorsResponse.class, com.cosmos.distribution.v1beta1.QueryDelegatorValidatorsResponse.Builder.class);
  }

  public static final int VALIDATORS_FIELD_NUMBER = 1;
  @SuppressWarnings("serial")
  private com.google.protobuf.LazyStringList validators_;
  /**
   * <pre>
   * validators defines the validators a delegator is delegating for.
   * </pre>
   *
   * <code>repeated string validators = 1 [json_name = "validators"];</code>
   * @return A list containing the validators.
   */
  public com.google.protobuf.ProtocolStringList
      getValidatorsList() {
    return validators_;
  }
  /**
   * <pre>
   * validators defines the validators a delegator is delegating for.
   * </pre>
   *
   * <code>repeated string validators = 1 [json_name = "validators"];</code>
   * @return The count of validators.
   */
  public int getValidatorsCount() {
    return validators_.size();
  }
  /**
   * <pre>
   * validators defines the validators a delegator is delegating for.
   * </pre>
   *
   * <code>repeated string validators = 1 [json_name = "validators"];</code>
   * @param index The index of the element to return.
   * @return The validators at the given index.
   */
  public java.lang.String getValidators(int index) {
    return validators_.get(index);
  }
  /**
   * <pre>
   * validators defines the validators a delegator is delegating for.
   * </pre>
   *
   * <code>repeated string validators = 1 [json_name = "validators"];</code>
   * @param index The index of the value to return.
   * @return The bytes of the validators at the given index.
   */
  public com.google.protobuf.ByteString
      getValidatorsBytes(int index) {
    return validators_.getByteString(index);
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
    for (int i = 0; i < validators_.size(); i++) {
      com.google.protobuf.GeneratedMessageV3.writeString(output, 1, validators_.getRaw(i));
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
      for (int i = 0; i < validators_.size(); i++) {
        dataSize += computeStringSizeNoTag(validators_.getRaw(i));
      }
      size += dataSize;
      size += 1 * getValidatorsList().size();
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
    if (!(obj instanceof com.cosmos.distribution.v1beta1.QueryDelegatorValidatorsResponse)) {
      return super.equals(obj);
    }
    com.cosmos.distribution.v1beta1.QueryDelegatorValidatorsResponse other = (com.cosmos.distribution.v1beta1.QueryDelegatorValidatorsResponse) obj;

    if (!getValidatorsList()
        .equals(other.getValidatorsList())) return false;
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
    if (getValidatorsCount() > 0) {
      hash = (37 * hash) + VALIDATORS_FIELD_NUMBER;
      hash = (53 * hash) + getValidatorsList().hashCode();
    }
    hash = (29 * hash) + getUnknownFields().hashCode();
    memoizedHashCode = hash;
    return hash;
  }

  public static com.cosmos.distribution.v1beta1.QueryDelegatorValidatorsResponse parseFrom(
      java.nio.ByteBuffer data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static com.cosmos.distribution.v1beta1.QueryDelegatorValidatorsResponse parseFrom(
      java.nio.ByteBuffer data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static com.cosmos.distribution.v1beta1.QueryDelegatorValidatorsResponse parseFrom(
      com.google.protobuf.ByteString data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static com.cosmos.distribution.v1beta1.QueryDelegatorValidatorsResponse parseFrom(
      com.google.protobuf.ByteString data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static com.cosmos.distribution.v1beta1.QueryDelegatorValidatorsResponse parseFrom(byte[] data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static com.cosmos.distribution.v1beta1.QueryDelegatorValidatorsResponse parseFrom(
      byte[] data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static com.cosmos.distribution.v1beta1.QueryDelegatorValidatorsResponse parseFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static com.cosmos.distribution.v1beta1.QueryDelegatorValidatorsResponse parseFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input, extensionRegistry);
  }
  public static com.cosmos.distribution.v1beta1.QueryDelegatorValidatorsResponse parseDelimitedFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input);
  }
  public static com.cosmos.distribution.v1beta1.QueryDelegatorValidatorsResponse parseDelimitedFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
  }
  public static com.cosmos.distribution.v1beta1.QueryDelegatorValidatorsResponse parseFrom(
      com.google.protobuf.CodedInputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static com.cosmos.distribution.v1beta1.QueryDelegatorValidatorsResponse parseFrom(
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
  public static Builder newBuilder(com.cosmos.distribution.v1beta1.QueryDelegatorValidatorsResponse prototype) {
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
   * QueryDelegatorValidatorsResponse is the response type for the
   * Query/DelegatorValidators RPC method.
   * </pre>
   *
   * Protobuf type {@code cosmos.distribution.v1beta1.QueryDelegatorValidatorsResponse}
   */
  public static final class Builder extends
      com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
      // @@protoc_insertion_point(builder_implements:cosmos.distribution.v1beta1.QueryDelegatorValidatorsResponse)
      com.cosmos.distribution.v1beta1.QueryDelegatorValidatorsResponseOrBuilder {
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return com.cosmos.distribution.v1beta1.QueryProto.internal_static_cosmos_distribution_v1beta1_QueryDelegatorValidatorsResponse_descriptor;
    }

    @java.lang.Override
    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return com.cosmos.distribution.v1beta1.QueryProto.internal_static_cosmos_distribution_v1beta1_QueryDelegatorValidatorsResponse_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              com.cosmos.distribution.v1beta1.QueryDelegatorValidatorsResponse.class, com.cosmos.distribution.v1beta1.QueryDelegatorValidatorsResponse.Builder.class);
    }

    // Construct using com.cosmos.distribution.v1beta1.QueryDelegatorValidatorsResponse.newBuilder()
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
      validators_ = com.google.protobuf.LazyStringArrayList.EMPTY;
      bitField0_ = (bitField0_ & ~0x00000001);
      return this;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.Descriptor
        getDescriptorForType() {
      return com.cosmos.distribution.v1beta1.QueryProto.internal_static_cosmos_distribution_v1beta1_QueryDelegatorValidatorsResponse_descriptor;
    }

    @java.lang.Override
    public com.cosmos.distribution.v1beta1.QueryDelegatorValidatorsResponse getDefaultInstanceForType() {
      return com.cosmos.distribution.v1beta1.QueryDelegatorValidatorsResponse.getDefaultInstance();
    }

    @java.lang.Override
    public com.cosmos.distribution.v1beta1.QueryDelegatorValidatorsResponse build() {
      com.cosmos.distribution.v1beta1.QueryDelegatorValidatorsResponse result = buildPartial();
      if (!result.isInitialized()) {
        throw newUninitializedMessageException(result);
      }
      return result;
    }

    @java.lang.Override
    public com.cosmos.distribution.v1beta1.QueryDelegatorValidatorsResponse buildPartial() {
      com.cosmos.distribution.v1beta1.QueryDelegatorValidatorsResponse result = new com.cosmos.distribution.v1beta1.QueryDelegatorValidatorsResponse(this);
      buildPartialRepeatedFields(result);
      if (bitField0_ != 0) { buildPartial0(result); }
      onBuilt();
      return result;
    }

    private void buildPartialRepeatedFields(com.cosmos.distribution.v1beta1.QueryDelegatorValidatorsResponse result) {
      if (((bitField0_ & 0x00000001) != 0)) {
        validators_ = validators_.getUnmodifiableView();
        bitField0_ = (bitField0_ & ~0x00000001);
      }
      result.validators_ = validators_;
    }

    private void buildPartial0(com.cosmos.distribution.v1beta1.QueryDelegatorValidatorsResponse result) {
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
      if (other instanceof com.cosmos.distribution.v1beta1.QueryDelegatorValidatorsResponse) {
        return mergeFrom((com.cosmos.distribution.v1beta1.QueryDelegatorValidatorsResponse)other);
      } else {
        super.mergeFrom(other);
        return this;
      }
    }

    public Builder mergeFrom(com.cosmos.distribution.v1beta1.QueryDelegatorValidatorsResponse other) {
      if (other == com.cosmos.distribution.v1beta1.QueryDelegatorValidatorsResponse.getDefaultInstance()) return this;
      if (!other.validators_.isEmpty()) {
        if (validators_.isEmpty()) {
          validators_ = other.validators_;
          bitField0_ = (bitField0_ & ~0x00000001);
        } else {
          ensureValidatorsIsMutable();
          validators_.addAll(other.validators_);
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
              ensureValidatorsIsMutable();
              validators_.add(s);
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

    private com.google.protobuf.LazyStringList validators_ = com.google.protobuf.LazyStringArrayList.EMPTY;
    private void ensureValidatorsIsMutable() {
      if (!((bitField0_ & 0x00000001) != 0)) {
        validators_ = new com.google.protobuf.LazyStringArrayList(validators_);
        bitField0_ |= 0x00000001;
       }
    }
    /**
     * <pre>
     * validators defines the validators a delegator is delegating for.
     * </pre>
     *
     * <code>repeated string validators = 1 [json_name = "validators"];</code>
     * @return A list containing the validators.
     */
    public com.google.protobuf.ProtocolStringList
        getValidatorsList() {
      return validators_.getUnmodifiableView();
    }
    /**
     * <pre>
     * validators defines the validators a delegator is delegating for.
     * </pre>
     *
     * <code>repeated string validators = 1 [json_name = "validators"];</code>
     * @return The count of validators.
     */
    public int getValidatorsCount() {
      return validators_.size();
    }
    /**
     * <pre>
     * validators defines the validators a delegator is delegating for.
     * </pre>
     *
     * <code>repeated string validators = 1 [json_name = "validators"];</code>
     * @param index The index of the element to return.
     * @return The validators at the given index.
     */
    public java.lang.String getValidators(int index) {
      return validators_.get(index);
    }
    /**
     * <pre>
     * validators defines the validators a delegator is delegating for.
     * </pre>
     *
     * <code>repeated string validators = 1 [json_name = "validators"];</code>
     * @param index The index of the value to return.
     * @return The bytes of the validators at the given index.
     */
    public com.google.protobuf.ByteString
        getValidatorsBytes(int index) {
      return validators_.getByteString(index);
    }
    /**
     * <pre>
     * validators defines the validators a delegator is delegating for.
     * </pre>
     *
     * <code>repeated string validators = 1 [json_name = "validators"];</code>
     * @param index The index to set the value at.
     * @param value The validators to set.
     * @return This builder for chaining.
     */
    public Builder setValidators(
        int index, java.lang.String value) {
      if (value == null) { throw new NullPointerException(); }
      ensureValidatorsIsMutable();
      validators_.set(index, value);
      onChanged();
      return this;
    }
    /**
     * <pre>
     * validators defines the validators a delegator is delegating for.
     * </pre>
     *
     * <code>repeated string validators = 1 [json_name = "validators"];</code>
     * @param value The validators to add.
     * @return This builder for chaining.
     */
    public Builder addValidators(
        java.lang.String value) {
      if (value == null) { throw new NullPointerException(); }
      ensureValidatorsIsMutable();
      validators_.add(value);
      onChanged();
      return this;
    }
    /**
     * <pre>
     * validators defines the validators a delegator is delegating for.
     * </pre>
     *
     * <code>repeated string validators = 1 [json_name = "validators"];</code>
     * @param values The validators to add.
     * @return This builder for chaining.
     */
    public Builder addAllValidators(
        java.lang.Iterable<java.lang.String> values) {
      ensureValidatorsIsMutable();
      com.google.protobuf.AbstractMessageLite.Builder.addAll(
          values, validators_);
      onChanged();
      return this;
    }
    /**
     * <pre>
     * validators defines the validators a delegator is delegating for.
     * </pre>
     *
     * <code>repeated string validators = 1 [json_name = "validators"];</code>
     * @return This builder for chaining.
     */
    public Builder clearValidators() {
      validators_ = com.google.protobuf.LazyStringArrayList.EMPTY;
      bitField0_ = (bitField0_ & ~0x00000001);
      onChanged();
      return this;
    }
    /**
     * <pre>
     * validators defines the validators a delegator is delegating for.
     * </pre>
     *
     * <code>repeated string validators = 1 [json_name = "validators"];</code>
     * @param value The bytes of the validators to add.
     * @return This builder for chaining.
     */
    public Builder addValidatorsBytes(
        com.google.protobuf.ByteString value) {
      if (value == null) { throw new NullPointerException(); }
      checkByteStringIsUtf8(value);
      ensureValidatorsIsMutable();
      validators_.add(value);
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


    // @@protoc_insertion_point(builder_scope:cosmos.distribution.v1beta1.QueryDelegatorValidatorsResponse)
  }

  // @@protoc_insertion_point(class_scope:cosmos.distribution.v1beta1.QueryDelegatorValidatorsResponse)
  private static final com.cosmos.distribution.v1beta1.QueryDelegatorValidatorsResponse DEFAULT_INSTANCE;
  static {
    DEFAULT_INSTANCE = new com.cosmos.distribution.v1beta1.QueryDelegatorValidatorsResponse();
  }

  public static com.cosmos.distribution.v1beta1.QueryDelegatorValidatorsResponse getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }

  private static final com.google.protobuf.Parser<QueryDelegatorValidatorsResponse>
      PARSER = new com.google.protobuf.AbstractParser<QueryDelegatorValidatorsResponse>() {
    @java.lang.Override
    public QueryDelegatorValidatorsResponse parsePartialFrom(
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

  public static com.google.protobuf.Parser<QueryDelegatorValidatorsResponse> parser() {
    return PARSER;
  }

  @java.lang.Override
  public com.google.protobuf.Parser<QueryDelegatorValidatorsResponse> getParserForType() {
    return PARSER;
  }

  @java.lang.Override
  public com.cosmos.distribution.v1beta1.QueryDelegatorValidatorsResponse getDefaultInstanceForType() {
    return DEFAULT_INSTANCE;
  }

}
