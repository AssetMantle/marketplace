// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: ibc/core/client/v1/query.proto

package com.ibc.core.client.v1;

/**
 * <pre>
 * QueryClientStateResponse is the response type for the Query/ClientState RPC
 * method. Besides the client state, it includes a proof and the height from
 * which the proof was retrieved.
 * </pre>
 *
 * Protobuf type {@code ibc.core.client.v1.QueryClientStateResponse}
 */
public final class QueryClientStateResponse extends
    com.google.protobuf.GeneratedMessageV3 implements
    // @@protoc_insertion_point(message_implements:ibc.core.client.v1.QueryClientStateResponse)
    QueryClientStateResponseOrBuilder {
private static final long serialVersionUID = 0L;
  // Use QueryClientStateResponse.newBuilder() to construct.
  private QueryClientStateResponse(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
    super(builder);
  }
  private QueryClientStateResponse() {
    proof_ = com.google.protobuf.ByteString.EMPTY;
  }

  @java.lang.Override
  @SuppressWarnings({"unused"})
  protected java.lang.Object newInstance(
      UnusedPrivateParameter unused) {
    return new QueryClientStateResponse();
  }

  public static final com.google.protobuf.Descriptors.Descriptor
      getDescriptor() {
    return com.ibc.core.client.v1.QueryProto.internal_static_ibc_core_client_v1_QueryClientStateResponse_descriptor;
  }

  @java.lang.Override
  protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internalGetFieldAccessorTable() {
    return com.ibc.core.client.v1.QueryProto.internal_static_ibc_core_client_v1_QueryClientStateResponse_fieldAccessorTable
        .ensureFieldAccessorsInitialized(
            com.ibc.core.client.v1.QueryClientStateResponse.class, com.ibc.core.client.v1.QueryClientStateResponse.Builder.class);
  }

  public static final int CLIENT_STATE_FIELD_NUMBER = 1;
  private com.google.protobuf.Any clientState_;
  /**
   * <pre>
   * client state associated with the request identifier
   * </pre>
   *
   * <code>.google.protobuf.Any client_state = 1 [json_name = "clientState"];</code>
   * @return Whether the clientState field is set.
   */
  @java.lang.Override
  public boolean hasClientState() {
    return clientState_ != null;
  }
  /**
   * <pre>
   * client state associated with the request identifier
   * </pre>
   *
   * <code>.google.protobuf.Any client_state = 1 [json_name = "clientState"];</code>
   * @return The clientState.
   */
  @java.lang.Override
  public com.google.protobuf.Any getClientState() {
    return clientState_ == null ? com.google.protobuf.Any.getDefaultInstance() : clientState_;
  }
  /**
   * <pre>
   * client state associated with the request identifier
   * </pre>
   *
   * <code>.google.protobuf.Any client_state = 1 [json_name = "clientState"];</code>
   */
  @java.lang.Override
  public com.google.protobuf.AnyOrBuilder getClientStateOrBuilder() {
    return clientState_ == null ? com.google.protobuf.Any.getDefaultInstance() : clientState_;
  }

  public static final int PROOF_FIELD_NUMBER = 2;
  private com.google.protobuf.ByteString proof_ = com.google.protobuf.ByteString.EMPTY;
  /**
   * <pre>
   * merkle proof of existence
   * </pre>
   *
   * <code>bytes proof = 2 [json_name = "proof"];</code>
   * @return The proof.
   */
  @java.lang.Override
  public com.google.protobuf.ByteString getProof() {
    return proof_;
  }

  public static final int PROOF_HEIGHT_FIELD_NUMBER = 3;
  private com.ibc.core.client.v1.Height proofHeight_;
  /**
   * <pre>
   * height at which the proof was retrieved
   * </pre>
   *
   * <code>.ibc.core.client.v1.Height proof_height = 3 [json_name = "proofHeight", (.gogoproto.nullable) = false];</code>
   * @return Whether the proofHeight field is set.
   */
  @java.lang.Override
  public boolean hasProofHeight() {
    return proofHeight_ != null;
  }
  /**
   * <pre>
   * height at which the proof was retrieved
   * </pre>
   *
   * <code>.ibc.core.client.v1.Height proof_height = 3 [json_name = "proofHeight", (.gogoproto.nullable) = false];</code>
   * @return The proofHeight.
   */
  @java.lang.Override
  public com.ibc.core.client.v1.Height getProofHeight() {
    return proofHeight_ == null ? com.ibc.core.client.v1.Height.getDefaultInstance() : proofHeight_;
  }
  /**
   * <pre>
   * height at which the proof was retrieved
   * </pre>
   *
   * <code>.ibc.core.client.v1.Height proof_height = 3 [json_name = "proofHeight", (.gogoproto.nullable) = false];</code>
   */
  @java.lang.Override
  public com.ibc.core.client.v1.HeightOrBuilder getProofHeightOrBuilder() {
    return proofHeight_ == null ? com.ibc.core.client.v1.Height.getDefaultInstance() : proofHeight_;
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
    if (clientState_ != null) {
      output.writeMessage(1, getClientState());
    }
    if (!proof_.isEmpty()) {
      output.writeBytes(2, proof_);
    }
    if (proofHeight_ != null) {
      output.writeMessage(3, getProofHeight());
    }
    getUnknownFields().writeTo(output);
  }

  @java.lang.Override
  public int getSerializedSize() {
    int size = memoizedSize;
    if (size != -1) return size;

    size = 0;
    if (clientState_ != null) {
      size += com.google.protobuf.CodedOutputStream
        .computeMessageSize(1, getClientState());
    }
    if (!proof_.isEmpty()) {
      size += com.google.protobuf.CodedOutputStream
        .computeBytesSize(2, proof_);
    }
    if (proofHeight_ != null) {
      size += com.google.protobuf.CodedOutputStream
        .computeMessageSize(3, getProofHeight());
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
    if (!(obj instanceof com.ibc.core.client.v1.QueryClientStateResponse)) {
      return super.equals(obj);
    }
    com.ibc.core.client.v1.QueryClientStateResponse other = (com.ibc.core.client.v1.QueryClientStateResponse) obj;

    if (hasClientState() != other.hasClientState()) return false;
    if (hasClientState()) {
      if (!getClientState()
          .equals(other.getClientState())) return false;
    }
    if (!getProof()
        .equals(other.getProof())) return false;
    if (hasProofHeight() != other.hasProofHeight()) return false;
    if (hasProofHeight()) {
      if (!getProofHeight()
          .equals(other.getProofHeight())) return false;
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
    if (hasClientState()) {
      hash = (37 * hash) + CLIENT_STATE_FIELD_NUMBER;
      hash = (53 * hash) + getClientState().hashCode();
    }
    hash = (37 * hash) + PROOF_FIELD_NUMBER;
    hash = (53 * hash) + getProof().hashCode();
    if (hasProofHeight()) {
      hash = (37 * hash) + PROOF_HEIGHT_FIELD_NUMBER;
      hash = (53 * hash) + getProofHeight().hashCode();
    }
    hash = (29 * hash) + getUnknownFields().hashCode();
    memoizedHashCode = hash;
    return hash;
  }

  public static com.ibc.core.client.v1.QueryClientStateResponse parseFrom(
      java.nio.ByteBuffer data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static com.ibc.core.client.v1.QueryClientStateResponse parseFrom(
      java.nio.ByteBuffer data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static com.ibc.core.client.v1.QueryClientStateResponse parseFrom(
      com.google.protobuf.ByteString data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static com.ibc.core.client.v1.QueryClientStateResponse parseFrom(
      com.google.protobuf.ByteString data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static com.ibc.core.client.v1.QueryClientStateResponse parseFrom(byte[] data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static com.ibc.core.client.v1.QueryClientStateResponse parseFrom(
      byte[] data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static com.ibc.core.client.v1.QueryClientStateResponse parseFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static com.ibc.core.client.v1.QueryClientStateResponse parseFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input, extensionRegistry);
  }
  public static com.ibc.core.client.v1.QueryClientStateResponse parseDelimitedFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input);
  }
  public static com.ibc.core.client.v1.QueryClientStateResponse parseDelimitedFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
  }
  public static com.ibc.core.client.v1.QueryClientStateResponse parseFrom(
      com.google.protobuf.CodedInputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static com.ibc.core.client.v1.QueryClientStateResponse parseFrom(
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
  public static Builder newBuilder(com.ibc.core.client.v1.QueryClientStateResponse prototype) {
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
   * QueryClientStateResponse is the response type for the Query/ClientState RPC
   * method. Besides the client state, it includes a proof and the height from
   * which the proof was retrieved.
   * </pre>
   *
   * Protobuf type {@code ibc.core.client.v1.QueryClientStateResponse}
   */
  public static final class Builder extends
      com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
      // @@protoc_insertion_point(builder_implements:ibc.core.client.v1.QueryClientStateResponse)
      com.ibc.core.client.v1.QueryClientStateResponseOrBuilder {
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return com.ibc.core.client.v1.QueryProto.internal_static_ibc_core_client_v1_QueryClientStateResponse_descriptor;
    }

    @java.lang.Override
    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return com.ibc.core.client.v1.QueryProto.internal_static_ibc_core_client_v1_QueryClientStateResponse_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              com.ibc.core.client.v1.QueryClientStateResponse.class, com.ibc.core.client.v1.QueryClientStateResponse.Builder.class);
    }

    // Construct using com.ibc.core.client.v1.QueryClientStateResponse.newBuilder()
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
      clientState_ = null;
      if (clientStateBuilder_ != null) {
        clientStateBuilder_.dispose();
        clientStateBuilder_ = null;
      }
      proof_ = com.google.protobuf.ByteString.EMPTY;
      proofHeight_ = null;
      if (proofHeightBuilder_ != null) {
        proofHeightBuilder_.dispose();
        proofHeightBuilder_ = null;
      }
      return this;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.Descriptor
        getDescriptorForType() {
      return com.ibc.core.client.v1.QueryProto.internal_static_ibc_core_client_v1_QueryClientStateResponse_descriptor;
    }

    @java.lang.Override
    public com.ibc.core.client.v1.QueryClientStateResponse getDefaultInstanceForType() {
      return com.ibc.core.client.v1.QueryClientStateResponse.getDefaultInstance();
    }

    @java.lang.Override
    public com.ibc.core.client.v1.QueryClientStateResponse build() {
      com.ibc.core.client.v1.QueryClientStateResponse result = buildPartial();
      if (!result.isInitialized()) {
        throw newUninitializedMessageException(result);
      }
      return result;
    }

    @java.lang.Override
    public com.ibc.core.client.v1.QueryClientStateResponse buildPartial() {
      com.ibc.core.client.v1.QueryClientStateResponse result = new com.ibc.core.client.v1.QueryClientStateResponse(this);
      if (bitField0_ != 0) { buildPartial0(result); }
      onBuilt();
      return result;
    }

    private void buildPartial0(com.ibc.core.client.v1.QueryClientStateResponse result) {
      int from_bitField0_ = bitField0_;
      if (((from_bitField0_ & 0x00000001) != 0)) {
        result.clientState_ = clientStateBuilder_ == null
            ? clientState_
            : clientStateBuilder_.build();
      }
      if (((from_bitField0_ & 0x00000002) != 0)) {
        result.proof_ = proof_;
      }
      if (((from_bitField0_ & 0x00000004) != 0)) {
        result.proofHeight_ = proofHeightBuilder_ == null
            ? proofHeight_
            : proofHeightBuilder_.build();
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
      if (other instanceof com.ibc.core.client.v1.QueryClientStateResponse) {
        return mergeFrom((com.ibc.core.client.v1.QueryClientStateResponse)other);
      } else {
        super.mergeFrom(other);
        return this;
      }
    }

    public Builder mergeFrom(com.ibc.core.client.v1.QueryClientStateResponse other) {
      if (other == com.ibc.core.client.v1.QueryClientStateResponse.getDefaultInstance()) return this;
      if (other.hasClientState()) {
        mergeClientState(other.getClientState());
      }
      if (other.getProof() != com.google.protobuf.ByteString.EMPTY) {
        setProof(other.getProof());
      }
      if (other.hasProofHeight()) {
        mergeProofHeight(other.getProofHeight());
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
                  getClientStateFieldBuilder().getBuilder(),
                  extensionRegistry);
              bitField0_ |= 0x00000001;
              break;
            } // case 10
            case 18: {
              proof_ = input.readBytes();
              bitField0_ |= 0x00000002;
              break;
            } // case 18
            case 26: {
              input.readMessage(
                  getProofHeightFieldBuilder().getBuilder(),
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

    private com.google.protobuf.Any clientState_;
    private com.google.protobuf.SingleFieldBuilderV3<
        com.google.protobuf.Any, com.google.protobuf.Any.Builder, com.google.protobuf.AnyOrBuilder> clientStateBuilder_;
    /**
     * <pre>
     * client state associated with the request identifier
     * </pre>
     *
     * <code>.google.protobuf.Any client_state = 1 [json_name = "clientState"];</code>
     * @return Whether the clientState field is set.
     */
    public boolean hasClientState() {
      return ((bitField0_ & 0x00000001) != 0);
    }
    /**
     * <pre>
     * client state associated with the request identifier
     * </pre>
     *
     * <code>.google.protobuf.Any client_state = 1 [json_name = "clientState"];</code>
     * @return The clientState.
     */
    public com.google.protobuf.Any getClientState() {
      if (clientStateBuilder_ == null) {
        return clientState_ == null ? com.google.protobuf.Any.getDefaultInstance() : clientState_;
      } else {
        return clientStateBuilder_.getMessage();
      }
    }
    /**
     * <pre>
     * client state associated with the request identifier
     * </pre>
     *
     * <code>.google.protobuf.Any client_state = 1 [json_name = "clientState"];</code>
     */
    public Builder setClientState(com.google.protobuf.Any value) {
      if (clientStateBuilder_ == null) {
        if (value == null) {
          throw new NullPointerException();
        }
        clientState_ = value;
      } else {
        clientStateBuilder_.setMessage(value);
      }
      bitField0_ |= 0x00000001;
      onChanged();
      return this;
    }
    /**
     * <pre>
     * client state associated with the request identifier
     * </pre>
     *
     * <code>.google.protobuf.Any client_state = 1 [json_name = "clientState"];</code>
     */
    public Builder setClientState(
        com.google.protobuf.Any.Builder builderForValue) {
      if (clientStateBuilder_ == null) {
        clientState_ = builderForValue.build();
      } else {
        clientStateBuilder_.setMessage(builderForValue.build());
      }
      bitField0_ |= 0x00000001;
      onChanged();
      return this;
    }
    /**
     * <pre>
     * client state associated with the request identifier
     * </pre>
     *
     * <code>.google.protobuf.Any client_state = 1 [json_name = "clientState"];</code>
     */
    public Builder mergeClientState(com.google.protobuf.Any value) {
      if (clientStateBuilder_ == null) {
        if (((bitField0_ & 0x00000001) != 0) &&
          clientState_ != null &&
          clientState_ != com.google.protobuf.Any.getDefaultInstance()) {
          getClientStateBuilder().mergeFrom(value);
        } else {
          clientState_ = value;
        }
      } else {
        clientStateBuilder_.mergeFrom(value);
      }
      bitField0_ |= 0x00000001;
      onChanged();
      return this;
    }
    /**
     * <pre>
     * client state associated with the request identifier
     * </pre>
     *
     * <code>.google.protobuf.Any client_state = 1 [json_name = "clientState"];</code>
     */
    public Builder clearClientState() {
      bitField0_ = (bitField0_ & ~0x00000001);
      clientState_ = null;
      if (clientStateBuilder_ != null) {
        clientStateBuilder_.dispose();
        clientStateBuilder_ = null;
      }
      onChanged();
      return this;
    }
    /**
     * <pre>
     * client state associated with the request identifier
     * </pre>
     *
     * <code>.google.protobuf.Any client_state = 1 [json_name = "clientState"];</code>
     */
    public com.google.protobuf.Any.Builder getClientStateBuilder() {
      bitField0_ |= 0x00000001;
      onChanged();
      return getClientStateFieldBuilder().getBuilder();
    }
    /**
     * <pre>
     * client state associated with the request identifier
     * </pre>
     *
     * <code>.google.protobuf.Any client_state = 1 [json_name = "clientState"];</code>
     */
    public com.google.protobuf.AnyOrBuilder getClientStateOrBuilder() {
      if (clientStateBuilder_ != null) {
        return clientStateBuilder_.getMessageOrBuilder();
      } else {
        return clientState_ == null ?
            com.google.protobuf.Any.getDefaultInstance() : clientState_;
      }
    }
    /**
     * <pre>
     * client state associated with the request identifier
     * </pre>
     *
     * <code>.google.protobuf.Any client_state = 1 [json_name = "clientState"];</code>
     */
    private com.google.protobuf.SingleFieldBuilderV3<
        com.google.protobuf.Any, com.google.protobuf.Any.Builder, com.google.protobuf.AnyOrBuilder> 
        getClientStateFieldBuilder() {
      if (clientStateBuilder_ == null) {
        clientStateBuilder_ = new com.google.protobuf.SingleFieldBuilderV3<
            com.google.protobuf.Any, com.google.protobuf.Any.Builder, com.google.protobuf.AnyOrBuilder>(
                getClientState(),
                getParentForChildren(),
                isClean());
        clientState_ = null;
      }
      return clientStateBuilder_;
    }

    private com.google.protobuf.ByteString proof_ = com.google.protobuf.ByteString.EMPTY;
    /**
     * <pre>
     * merkle proof of existence
     * </pre>
     *
     * <code>bytes proof = 2 [json_name = "proof"];</code>
     * @return The proof.
     */
    @java.lang.Override
    public com.google.protobuf.ByteString getProof() {
      return proof_;
    }
    /**
     * <pre>
     * merkle proof of existence
     * </pre>
     *
     * <code>bytes proof = 2 [json_name = "proof"];</code>
     * @param value The proof to set.
     * @return This builder for chaining.
     */
    public Builder setProof(com.google.protobuf.ByteString value) {
      if (value == null) { throw new NullPointerException(); }
      proof_ = value;
      bitField0_ |= 0x00000002;
      onChanged();
      return this;
    }
    /**
     * <pre>
     * merkle proof of existence
     * </pre>
     *
     * <code>bytes proof = 2 [json_name = "proof"];</code>
     * @return This builder for chaining.
     */
    public Builder clearProof() {
      bitField0_ = (bitField0_ & ~0x00000002);
      proof_ = getDefaultInstance().getProof();
      onChanged();
      return this;
    }

    private com.ibc.core.client.v1.Height proofHeight_;
    private com.google.protobuf.SingleFieldBuilderV3<
        com.ibc.core.client.v1.Height, com.ibc.core.client.v1.Height.Builder, com.ibc.core.client.v1.HeightOrBuilder> proofHeightBuilder_;
    /**
     * <pre>
     * height at which the proof was retrieved
     * </pre>
     *
     * <code>.ibc.core.client.v1.Height proof_height = 3 [json_name = "proofHeight", (.gogoproto.nullable) = false];</code>
     * @return Whether the proofHeight field is set.
     */
    public boolean hasProofHeight() {
      return ((bitField0_ & 0x00000004) != 0);
    }
    /**
     * <pre>
     * height at which the proof was retrieved
     * </pre>
     *
     * <code>.ibc.core.client.v1.Height proof_height = 3 [json_name = "proofHeight", (.gogoproto.nullable) = false];</code>
     * @return The proofHeight.
     */
    public com.ibc.core.client.v1.Height getProofHeight() {
      if (proofHeightBuilder_ == null) {
        return proofHeight_ == null ? com.ibc.core.client.v1.Height.getDefaultInstance() : proofHeight_;
      } else {
        return proofHeightBuilder_.getMessage();
      }
    }
    /**
     * <pre>
     * height at which the proof was retrieved
     * </pre>
     *
     * <code>.ibc.core.client.v1.Height proof_height = 3 [json_name = "proofHeight", (.gogoproto.nullable) = false];</code>
     */
    public Builder setProofHeight(com.ibc.core.client.v1.Height value) {
      if (proofHeightBuilder_ == null) {
        if (value == null) {
          throw new NullPointerException();
        }
        proofHeight_ = value;
      } else {
        proofHeightBuilder_.setMessage(value);
      }
      bitField0_ |= 0x00000004;
      onChanged();
      return this;
    }
    /**
     * <pre>
     * height at which the proof was retrieved
     * </pre>
     *
     * <code>.ibc.core.client.v1.Height proof_height = 3 [json_name = "proofHeight", (.gogoproto.nullable) = false];</code>
     */
    public Builder setProofHeight(
        com.ibc.core.client.v1.Height.Builder builderForValue) {
      if (proofHeightBuilder_ == null) {
        proofHeight_ = builderForValue.build();
      } else {
        proofHeightBuilder_.setMessage(builderForValue.build());
      }
      bitField0_ |= 0x00000004;
      onChanged();
      return this;
    }
    /**
     * <pre>
     * height at which the proof was retrieved
     * </pre>
     *
     * <code>.ibc.core.client.v1.Height proof_height = 3 [json_name = "proofHeight", (.gogoproto.nullable) = false];</code>
     */
    public Builder mergeProofHeight(com.ibc.core.client.v1.Height value) {
      if (proofHeightBuilder_ == null) {
        if (((bitField0_ & 0x00000004) != 0) &&
          proofHeight_ != null &&
          proofHeight_ != com.ibc.core.client.v1.Height.getDefaultInstance()) {
          getProofHeightBuilder().mergeFrom(value);
        } else {
          proofHeight_ = value;
        }
      } else {
        proofHeightBuilder_.mergeFrom(value);
      }
      bitField0_ |= 0x00000004;
      onChanged();
      return this;
    }
    /**
     * <pre>
     * height at which the proof was retrieved
     * </pre>
     *
     * <code>.ibc.core.client.v1.Height proof_height = 3 [json_name = "proofHeight", (.gogoproto.nullable) = false];</code>
     */
    public Builder clearProofHeight() {
      bitField0_ = (bitField0_ & ~0x00000004);
      proofHeight_ = null;
      if (proofHeightBuilder_ != null) {
        proofHeightBuilder_.dispose();
        proofHeightBuilder_ = null;
      }
      onChanged();
      return this;
    }
    /**
     * <pre>
     * height at which the proof was retrieved
     * </pre>
     *
     * <code>.ibc.core.client.v1.Height proof_height = 3 [json_name = "proofHeight", (.gogoproto.nullable) = false];</code>
     */
    public com.ibc.core.client.v1.Height.Builder getProofHeightBuilder() {
      bitField0_ |= 0x00000004;
      onChanged();
      return getProofHeightFieldBuilder().getBuilder();
    }
    /**
     * <pre>
     * height at which the proof was retrieved
     * </pre>
     *
     * <code>.ibc.core.client.v1.Height proof_height = 3 [json_name = "proofHeight", (.gogoproto.nullable) = false];</code>
     */
    public com.ibc.core.client.v1.HeightOrBuilder getProofHeightOrBuilder() {
      if (proofHeightBuilder_ != null) {
        return proofHeightBuilder_.getMessageOrBuilder();
      } else {
        return proofHeight_ == null ?
            com.ibc.core.client.v1.Height.getDefaultInstance() : proofHeight_;
      }
    }
    /**
     * <pre>
     * height at which the proof was retrieved
     * </pre>
     *
     * <code>.ibc.core.client.v1.Height proof_height = 3 [json_name = "proofHeight", (.gogoproto.nullable) = false];</code>
     */
    private com.google.protobuf.SingleFieldBuilderV3<
        com.ibc.core.client.v1.Height, com.ibc.core.client.v1.Height.Builder, com.ibc.core.client.v1.HeightOrBuilder> 
        getProofHeightFieldBuilder() {
      if (proofHeightBuilder_ == null) {
        proofHeightBuilder_ = new com.google.protobuf.SingleFieldBuilderV3<
            com.ibc.core.client.v1.Height, com.ibc.core.client.v1.Height.Builder, com.ibc.core.client.v1.HeightOrBuilder>(
                getProofHeight(),
                getParentForChildren(),
                isClean());
        proofHeight_ = null;
      }
      return proofHeightBuilder_;
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


    // @@protoc_insertion_point(builder_scope:ibc.core.client.v1.QueryClientStateResponse)
  }

  // @@protoc_insertion_point(class_scope:ibc.core.client.v1.QueryClientStateResponse)
  private static final com.ibc.core.client.v1.QueryClientStateResponse DEFAULT_INSTANCE;
  static {
    DEFAULT_INSTANCE = new com.ibc.core.client.v1.QueryClientStateResponse();
  }

  public static com.ibc.core.client.v1.QueryClientStateResponse getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }

  private static final com.google.protobuf.Parser<QueryClientStateResponse>
      PARSER = new com.google.protobuf.AbstractParser<QueryClientStateResponse>() {
    @java.lang.Override
    public QueryClientStateResponse parsePartialFrom(
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

  public static com.google.protobuf.Parser<QueryClientStateResponse> parser() {
    return PARSER;
  }

  @java.lang.Override
  public com.google.protobuf.Parser<QueryClientStateResponse> getParserForType() {
    return PARSER;
  }

  @java.lang.Override
  public com.ibc.core.client.v1.QueryClientStateResponse getDefaultInstanceForType() {
    return DEFAULT_INSTANCE;
  }

}

