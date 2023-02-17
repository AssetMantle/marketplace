// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: cosmos/authz/v1beta1/query.proto

package com.cosmos.authz.v1beta1;

/**
 * <pre>
 * QueryGranterGrantsResponse is the response type for the Query/GranterGrants RPC method.
 * </pre>
 *
 * Protobuf type {@code cosmos.authz.v1beta1.QueryGranterGrantsResponse}
 */
public final class QueryGranterGrantsResponse extends
    com.google.protobuf.GeneratedMessageV3 implements
    // @@protoc_insertion_point(message_implements:cosmos.authz.v1beta1.QueryGranterGrantsResponse)
    QueryGranterGrantsResponseOrBuilder {
private static final long serialVersionUID = 0L;
  // Use QueryGranterGrantsResponse.newBuilder() to construct.
  private QueryGranterGrantsResponse(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
    super(builder);
  }
  private QueryGranterGrantsResponse() {
    grants_ = java.util.Collections.emptyList();
  }

  @java.lang.Override
  @SuppressWarnings({"unused"})
  protected java.lang.Object newInstance(
      UnusedPrivateParameter unused) {
    return new QueryGranterGrantsResponse();
  }

  public static final com.google.protobuf.Descriptors.Descriptor
      getDescriptor() {
    return com.cosmos.authz.v1beta1.QueryProto.internal_static_cosmos_authz_v1beta1_QueryGranterGrantsResponse_descriptor;
  }

  @java.lang.Override
  protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internalGetFieldAccessorTable() {
    return com.cosmos.authz.v1beta1.QueryProto.internal_static_cosmos_authz_v1beta1_QueryGranterGrantsResponse_fieldAccessorTable
        .ensureFieldAccessorsInitialized(
            com.cosmos.authz.v1beta1.QueryGranterGrantsResponse.class, com.cosmos.authz.v1beta1.QueryGranterGrantsResponse.Builder.class);
  }

  public static final int GRANTS_FIELD_NUMBER = 1;
  @SuppressWarnings("serial")
  private java.util.List<com.cosmos.authz.v1beta1.GrantAuthorization> grants_;
  /**
   * <pre>
   * grants is a list of grants granted by the granter.
   * </pre>
   *
   * <code>repeated .cosmos.authz.v1beta1.GrantAuthorization grants = 1 [json_name = "grants"];</code>
   */
  @java.lang.Override
  public java.util.List<com.cosmos.authz.v1beta1.GrantAuthorization> getGrantsList() {
    return grants_;
  }
  /**
   * <pre>
   * grants is a list of grants granted by the granter.
   * </pre>
   *
   * <code>repeated .cosmos.authz.v1beta1.GrantAuthorization grants = 1 [json_name = "grants"];</code>
   */
  @java.lang.Override
  public java.util.List<? extends com.cosmos.authz.v1beta1.GrantAuthorizationOrBuilder> 
      getGrantsOrBuilderList() {
    return grants_;
  }
  /**
   * <pre>
   * grants is a list of grants granted by the granter.
   * </pre>
   *
   * <code>repeated .cosmos.authz.v1beta1.GrantAuthorization grants = 1 [json_name = "grants"];</code>
   */
  @java.lang.Override
  public int getGrantsCount() {
    return grants_.size();
  }
  /**
   * <pre>
   * grants is a list of grants granted by the granter.
   * </pre>
   *
   * <code>repeated .cosmos.authz.v1beta1.GrantAuthorization grants = 1 [json_name = "grants"];</code>
   */
  @java.lang.Override
  public com.cosmos.authz.v1beta1.GrantAuthorization getGrants(int index) {
    return grants_.get(index);
  }
  /**
   * <pre>
   * grants is a list of grants granted by the granter.
   * </pre>
   *
   * <code>repeated .cosmos.authz.v1beta1.GrantAuthorization grants = 1 [json_name = "grants"];</code>
   */
  @java.lang.Override
  public com.cosmos.authz.v1beta1.GrantAuthorizationOrBuilder getGrantsOrBuilder(
      int index) {
    return grants_.get(index);
  }

  public static final int PAGINATION_FIELD_NUMBER = 2;
  private com.cosmos.base.query.v1beta1.PageResponse pagination_;
  /**
   * <pre>
   * pagination defines an pagination for the response.
   * </pre>
   *
   * <code>.cosmos.base.query.v1beta1.PageResponse pagination = 2 [json_name = "pagination"];</code>
   * @return Whether the pagination field is set.
   */
  @java.lang.Override
  public boolean hasPagination() {
    return pagination_ != null;
  }
  /**
   * <pre>
   * pagination defines an pagination for the response.
   * </pre>
   *
   * <code>.cosmos.base.query.v1beta1.PageResponse pagination = 2 [json_name = "pagination"];</code>
   * @return The pagination.
   */
  @java.lang.Override
  public com.cosmos.base.query.v1beta1.PageResponse getPagination() {
    return pagination_ == null ? com.cosmos.base.query.v1beta1.PageResponse.getDefaultInstance() : pagination_;
  }
  /**
   * <pre>
   * pagination defines an pagination for the response.
   * </pre>
   *
   * <code>.cosmos.base.query.v1beta1.PageResponse pagination = 2 [json_name = "pagination"];</code>
   */
  @java.lang.Override
  public com.cosmos.base.query.v1beta1.PageResponseOrBuilder getPaginationOrBuilder() {
    return pagination_ == null ? com.cosmos.base.query.v1beta1.PageResponse.getDefaultInstance() : pagination_;
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
    for (int i = 0; i < grants_.size(); i++) {
      output.writeMessage(1, grants_.get(i));
    }
    if (pagination_ != null) {
      output.writeMessage(2, getPagination());
    }
    getUnknownFields().writeTo(output);
  }

  @java.lang.Override
  public int getSerializedSize() {
    int size = memoizedSize;
    if (size != -1) return size;

    size = 0;
    for (int i = 0; i < grants_.size(); i++) {
      size += com.google.protobuf.CodedOutputStream
        .computeMessageSize(1, grants_.get(i));
    }
    if (pagination_ != null) {
      size += com.google.protobuf.CodedOutputStream
        .computeMessageSize(2, getPagination());
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
    if (!(obj instanceof com.cosmos.authz.v1beta1.QueryGranterGrantsResponse)) {
      return super.equals(obj);
    }
    com.cosmos.authz.v1beta1.QueryGranterGrantsResponse other = (com.cosmos.authz.v1beta1.QueryGranterGrantsResponse) obj;

    if (!getGrantsList()
        .equals(other.getGrantsList())) return false;
    if (hasPagination() != other.hasPagination()) return false;
    if (hasPagination()) {
      if (!getPagination()
          .equals(other.getPagination())) return false;
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
    if (getGrantsCount() > 0) {
      hash = (37 * hash) + GRANTS_FIELD_NUMBER;
      hash = (53 * hash) + getGrantsList().hashCode();
    }
    if (hasPagination()) {
      hash = (37 * hash) + PAGINATION_FIELD_NUMBER;
      hash = (53 * hash) + getPagination().hashCode();
    }
    hash = (29 * hash) + getUnknownFields().hashCode();
    memoizedHashCode = hash;
    return hash;
  }

  public static com.cosmos.authz.v1beta1.QueryGranterGrantsResponse parseFrom(
      java.nio.ByteBuffer data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static com.cosmos.authz.v1beta1.QueryGranterGrantsResponse parseFrom(
      java.nio.ByteBuffer data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static com.cosmos.authz.v1beta1.QueryGranterGrantsResponse parseFrom(
      com.google.protobuf.ByteString data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static com.cosmos.authz.v1beta1.QueryGranterGrantsResponse parseFrom(
      com.google.protobuf.ByteString data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static com.cosmos.authz.v1beta1.QueryGranterGrantsResponse parseFrom(byte[] data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static com.cosmos.authz.v1beta1.QueryGranterGrantsResponse parseFrom(
      byte[] data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static com.cosmos.authz.v1beta1.QueryGranterGrantsResponse parseFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static com.cosmos.authz.v1beta1.QueryGranterGrantsResponse parseFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input, extensionRegistry);
  }
  public static com.cosmos.authz.v1beta1.QueryGranterGrantsResponse parseDelimitedFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input);
  }
  public static com.cosmos.authz.v1beta1.QueryGranterGrantsResponse parseDelimitedFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
  }
  public static com.cosmos.authz.v1beta1.QueryGranterGrantsResponse parseFrom(
      com.google.protobuf.CodedInputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static com.cosmos.authz.v1beta1.QueryGranterGrantsResponse parseFrom(
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
  public static Builder newBuilder(com.cosmos.authz.v1beta1.QueryGranterGrantsResponse prototype) {
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
   * QueryGranterGrantsResponse is the response type for the Query/GranterGrants RPC method.
   * </pre>
   *
   * Protobuf type {@code cosmos.authz.v1beta1.QueryGranterGrantsResponse}
   */
  public static final class Builder extends
      com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
      // @@protoc_insertion_point(builder_implements:cosmos.authz.v1beta1.QueryGranterGrantsResponse)
      com.cosmos.authz.v1beta1.QueryGranterGrantsResponseOrBuilder {
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return com.cosmos.authz.v1beta1.QueryProto.internal_static_cosmos_authz_v1beta1_QueryGranterGrantsResponse_descriptor;
    }

    @java.lang.Override
    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return com.cosmos.authz.v1beta1.QueryProto.internal_static_cosmos_authz_v1beta1_QueryGranterGrantsResponse_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              com.cosmos.authz.v1beta1.QueryGranterGrantsResponse.class, com.cosmos.authz.v1beta1.QueryGranterGrantsResponse.Builder.class);
    }

    // Construct using com.cosmos.authz.v1beta1.QueryGranterGrantsResponse.newBuilder()
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
      if (grantsBuilder_ == null) {
        grants_ = java.util.Collections.emptyList();
      } else {
        grants_ = null;
        grantsBuilder_.clear();
      }
      bitField0_ = (bitField0_ & ~0x00000001);
      pagination_ = null;
      if (paginationBuilder_ != null) {
        paginationBuilder_.dispose();
        paginationBuilder_ = null;
      }
      return this;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.Descriptor
        getDescriptorForType() {
      return com.cosmos.authz.v1beta1.QueryProto.internal_static_cosmos_authz_v1beta1_QueryGranterGrantsResponse_descriptor;
    }

    @java.lang.Override
    public com.cosmos.authz.v1beta1.QueryGranterGrantsResponse getDefaultInstanceForType() {
      return com.cosmos.authz.v1beta1.QueryGranterGrantsResponse.getDefaultInstance();
    }

    @java.lang.Override
    public com.cosmos.authz.v1beta1.QueryGranterGrantsResponse build() {
      com.cosmos.authz.v1beta1.QueryGranterGrantsResponse result = buildPartial();
      if (!result.isInitialized()) {
        throw newUninitializedMessageException(result);
      }
      return result;
    }

    @java.lang.Override
    public com.cosmos.authz.v1beta1.QueryGranterGrantsResponse buildPartial() {
      com.cosmos.authz.v1beta1.QueryGranterGrantsResponse result = new com.cosmos.authz.v1beta1.QueryGranterGrantsResponse(this);
      buildPartialRepeatedFields(result);
      if (bitField0_ != 0) { buildPartial0(result); }
      onBuilt();
      return result;
    }

    private void buildPartialRepeatedFields(com.cosmos.authz.v1beta1.QueryGranterGrantsResponse result) {
      if (grantsBuilder_ == null) {
        if (((bitField0_ & 0x00000001) != 0)) {
          grants_ = java.util.Collections.unmodifiableList(grants_);
          bitField0_ = (bitField0_ & ~0x00000001);
        }
        result.grants_ = grants_;
      } else {
        result.grants_ = grantsBuilder_.build();
      }
    }

    private void buildPartial0(com.cosmos.authz.v1beta1.QueryGranterGrantsResponse result) {
      int from_bitField0_ = bitField0_;
      if (((from_bitField0_ & 0x00000002) != 0)) {
        result.pagination_ = paginationBuilder_ == null
            ? pagination_
            : paginationBuilder_.build();
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
      if (other instanceof com.cosmos.authz.v1beta1.QueryGranterGrantsResponse) {
        return mergeFrom((com.cosmos.authz.v1beta1.QueryGranterGrantsResponse)other);
      } else {
        super.mergeFrom(other);
        return this;
      }
    }

    public Builder mergeFrom(com.cosmos.authz.v1beta1.QueryGranterGrantsResponse other) {
      if (other == com.cosmos.authz.v1beta1.QueryGranterGrantsResponse.getDefaultInstance()) return this;
      if (grantsBuilder_ == null) {
        if (!other.grants_.isEmpty()) {
          if (grants_.isEmpty()) {
            grants_ = other.grants_;
            bitField0_ = (bitField0_ & ~0x00000001);
          } else {
            ensureGrantsIsMutable();
            grants_.addAll(other.grants_);
          }
          onChanged();
        }
      } else {
        if (!other.grants_.isEmpty()) {
          if (grantsBuilder_.isEmpty()) {
            grantsBuilder_.dispose();
            grantsBuilder_ = null;
            grants_ = other.grants_;
            bitField0_ = (bitField0_ & ~0x00000001);
            grantsBuilder_ = 
              com.google.protobuf.GeneratedMessageV3.alwaysUseFieldBuilders ?
                 getGrantsFieldBuilder() : null;
          } else {
            grantsBuilder_.addAllMessages(other.grants_);
          }
        }
      }
      if (other.hasPagination()) {
        mergePagination(other.getPagination());
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
              com.cosmos.authz.v1beta1.GrantAuthorization m =
                  input.readMessage(
                      com.cosmos.authz.v1beta1.GrantAuthorization.parser(),
                      extensionRegistry);
              if (grantsBuilder_ == null) {
                ensureGrantsIsMutable();
                grants_.add(m);
              } else {
                grantsBuilder_.addMessage(m);
              }
              break;
            } // case 10
            case 18: {
              input.readMessage(
                  getPaginationFieldBuilder().getBuilder(),
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

    private java.util.List<com.cosmos.authz.v1beta1.GrantAuthorization> grants_ =
      java.util.Collections.emptyList();
    private void ensureGrantsIsMutable() {
      if (!((bitField0_ & 0x00000001) != 0)) {
        grants_ = new java.util.ArrayList<com.cosmos.authz.v1beta1.GrantAuthorization>(grants_);
        bitField0_ |= 0x00000001;
       }
    }

    private com.google.protobuf.RepeatedFieldBuilderV3<
        com.cosmos.authz.v1beta1.GrantAuthorization, com.cosmos.authz.v1beta1.GrantAuthorization.Builder, com.cosmos.authz.v1beta1.GrantAuthorizationOrBuilder> grantsBuilder_;

    /**
     * <pre>
     * grants is a list of grants granted by the granter.
     * </pre>
     *
     * <code>repeated .cosmos.authz.v1beta1.GrantAuthorization grants = 1 [json_name = "grants"];</code>
     */
    public java.util.List<com.cosmos.authz.v1beta1.GrantAuthorization> getGrantsList() {
      if (grantsBuilder_ == null) {
        return java.util.Collections.unmodifiableList(grants_);
      } else {
        return grantsBuilder_.getMessageList();
      }
    }
    /**
     * <pre>
     * grants is a list of grants granted by the granter.
     * </pre>
     *
     * <code>repeated .cosmos.authz.v1beta1.GrantAuthorization grants = 1 [json_name = "grants"];</code>
     */
    public int getGrantsCount() {
      if (grantsBuilder_ == null) {
        return grants_.size();
      } else {
        return grantsBuilder_.getCount();
      }
    }
    /**
     * <pre>
     * grants is a list of grants granted by the granter.
     * </pre>
     *
     * <code>repeated .cosmos.authz.v1beta1.GrantAuthorization grants = 1 [json_name = "grants"];</code>
     */
    public com.cosmos.authz.v1beta1.GrantAuthorization getGrants(int index) {
      if (grantsBuilder_ == null) {
        return grants_.get(index);
      } else {
        return grantsBuilder_.getMessage(index);
      }
    }
    /**
     * <pre>
     * grants is a list of grants granted by the granter.
     * </pre>
     *
     * <code>repeated .cosmos.authz.v1beta1.GrantAuthorization grants = 1 [json_name = "grants"];</code>
     */
    public Builder setGrants(
        int index, com.cosmos.authz.v1beta1.GrantAuthorization value) {
      if (grantsBuilder_ == null) {
        if (value == null) {
          throw new NullPointerException();
        }
        ensureGrantsIsMutable();
        grants_.set(index, value);
        onChanged();
      } else {
        grantsBuilder_.setMessage(index, value);
      }
      return this;
    }
    /**
     * <pre>
     * grants is a list of grants granted by the granter.
     * </pre>
     *
     * <code>repeated .cosmos.authz.v1beta1.GrantAuthorization grants = 1 [json_name = "grants"];</code>
     */
    public Builder setGrants(
        int index, com.cosmos.authz.v1beta1.GrantAuthorization.Builder builderForValue) {
      if (grantsBuilder_ == null) {
        ensureGrantsIsMutable();
        grants_.set(index, builderForValue.build());
        onChanged();
      } else {
        grantsBuilder_.setMessage(index, builderForValue.build());
      }
      return this;
    }
    /**
     * <pre>
     * grants is a list of grants granted by the granter.
     * </pre>
     *
     * <code>repeated .cosmos.authz.v1beta1.GrantAuthorization grants = 1 [json_name = "grants"];</code>
     */
    public Builder addGrants(com.cosmos.authz.v1beta1.GrantAuthorization value) {
      if (grantsBuilder_ == null) {
        if (value == null) {
          throw new NullPointerException();
        }
        ensureGrantsIsMutable();
        grants_.add(value);
        onChanged();
      } else {
        grantsBuilder_.addMessage(value);
      }
      return this;
    }
    /**
     * <pre>
     * grants is a list of grants granted by the granter.
     * </pre>
     *
     * <code>repeated .cosmos.authz.v1beta1.GrantAuthorization grants = 1 [json_name = "grants"];</code>
     */
    public Builder addGrants(
        int index, com.cosmos.authz.v1beta1.GrantAuthorization value) {
      if (grantsBuilder_ == null) {
        if (value == null) {
          throw new NullPointerException();
        }
        ensureGrantsIsMutable();
        grants_.add(index, value);
        onChanged();
      } else {
        grantsBuilder_.addMessage(index, value);
      }
      return this;
    }
    /**
     * <pre>
     * grants is a list of grants granted by the granter.
     * </pre>
     *
     * <code>repeated .cosmos.authz.v1beta1.GrantAuthorization grants = 1 [json_name = "grants"];</code>
     */
    public Builder addGrants(
        com.cosmos.authz.v1beta1.GrantAuthorization.Builder builderForValue) {
      if (grantsBuilder_ == null) {
        ensureGrantsIsMutable();
        grants_.add(builderForValue.build());
        onChanged();
      } else {
        grantsBuilder_.addMessage(builderForValue.build());
      }
      return this;
    }
    /**
     * <pre>
     * grants is a list of grants granted by the granter.
     * </pre>
     *
     * <code>repeated .cosmos.authz.v1beta1.GrantAuthorization grants = 1 [json_name = "grants"];</code>
     */
    public Builder addGrants(
        int index, com.cosmos.authz.v1beta1.GrantAuthorization.Builder builderForValue) {
      if (grantsBuilder_ == null) {
        ensureGrantsIsMutable();
        grants_.add(index, builderForValue.build());
        onChanged();
      } else {
        grantsBuilder_.addMessage(index, builderForValue.build());
      }
      return this;
    }
    /**
     * <pre>
     * grants is a list of grants granted by the granter.
     * </pre>
     *
     * <code>repeated .cosmos.authz.v1beta1.GrantAuthorization grants = 1 [json_name = "grants"];</code>
     */
    public Builder addAllGrants(
        java.lang.Iterable<? extends com.cosmos.authz.v1beta1.GrantAuthorization> values) {
      if (grantsBuilder_ == null) {
        ensureGrantsIsMutable();
        com.google.protobuf.AbstractMessageLite.Builder.addAll(
            values, grants_);
        onChanged();
      } else {
        grantsBuilder_.addAllMessages(values);
      }
      return this;
    }
    /**
     * <pre>
     * grants is a list of grants granted by the granter.
     * </pre>
     *
     * <code>repeated .cosmos.authz.v1beta1.GrantAuthorization grants = 1 [json_name = "grants"];</code>
     */
    public Builder clearGrants() {
      if (grantsBuilder_ == null) {
        grants_ = java.util.Collections.emptyList();
        bitField0_ = (bitField0_ & ~0x00000001);
        onChanged();
      } else {
        grantsBuilder_.clear();
      }
      return this;
    }
    /**
     * <pre>
     * grants is a list of grants granted by the granter.
     * </pre>
     *
     * <code>repeated .cosmos.authz.v1beta1.GrantAuthorization grants = 1 [json_name = "grants"];</code>
     */
    public Builder removeGrants(int index) {
      if (grantsBuilder_ == null) {
        ensureGrantsIsMutable();
        grants_.remove(index);
        onChanged();
      } else {
        grantsBuilder_.remove(index);
      }
      return this;
    }
    /**
     * <pre>
     * grants is a list of grants granted by the granter.
     * </pre>
     *
     * <code>repeated .cosmos.authz.v1beta1.GrantAuthorization grants = 1 [json_name = "grants"];</code>
     */
    public com.cosmos.authz.v1beta1.GrantAuthorization.Builder getGrantsBuilder(
        int index) {
      return getGrantsFieldBuilder().getBuilder(index);
    }
    /**
     * <pre>
     * grants is a list of grants granted by the granter.
     * </pre>
     *
     * <code>repeated .cosmos.authz.v1beta1.GrantAuthorization grants = 1 [json_name = "grants"];</code>
     */
    public com.cosmos.authz.v1beta1.GrantAuthorizationOrBuilder getGrantsOrBuilder(
        int index) {
      if (grantsBuilder_ == null) {
        return grants_.get(index);  } else {
        return grantsBuilder_.getMessageOrBuilder(index);
      }
    }
    /**
     * <pre>
     * grants is a list of grants granted by the granter.
     * </pre>
     *
     * <code>repeated .cosmos.authz.v1beta1.GrantAuthorization grants = 1 [json_name = "grants"];</code>
     */
    public java.util.List<? extends com.cosmos.authz.v1beta1.GrantAuthorizationOrBuilder> 
         getGrantsOrBuilderList() {
      if (grantsBuilder_ != null) {
        return grantsBuilder_.getMessageOrBuilderList();
      } else {
        return java.util.Collections.unmodifiableList(grants_);
      }
    }
    /**
     * <pre>
     * grants is a list of grants granted by the granter.
     * </pre>
     *
     * <code>repeated .cosmos.authz.v1beta1.GrantAuthorization grants = 1 [json_name = "grants"];</code>
     */
    public com.cosmos.authz.v1beta1.GrantAuthorization.Builder addGrantsBuilder() {
      return getGrantsFieldBuilder().addBuilder(
          com.cosmos.authz.v1beta1.GrantAuthorization.getDefaultInstance());
    }
    /**
     * <pre>
     * grants is a list of grants granted by the granter.
     * </pre>
     *
     * <code>repeated .cosmos.authz.v1beta1.GrantAuthorization grants = 1 [json_name = "grants"];</code>
     */
    public com.cosmos.authz.v1beta1.GrantAuthorization.Builder addGrantsBuilder(
        int index) {
      return getGrantsFieldBuilder().addBuilder(
          index, com.cosmos.authz.v1beta1.GrantAuthorization.getDefaultInstance());
    }
    /**
     * <pre>
     * grants is a list of grants granted by the granter.
     * </pre>
     *
     * <code>repeated .cosmos.authz.v1beta1.GrantAuthorization grants = 1 [json_name = "grants"];</code>
     */
    public java.util.List<com.cosmos.authz.v1beta1.GrantAuthorization.Builder> 
         getGrantsBuilderList() {
      return getGrantsFieldBuilder().getBuilderList();
    }
    private com.google.protobuf.RepeatedFieldBuilderV3<
        com.cosmos.authz.v1beta1.GrantAuthorization, com.cosmos.authz.v1beta1.GrantAuthorization.Builder, com.cosmos.authz.v1beta1.GrantAuthorizationOrBuilder> 
        getGrantsFieldBuilder() {
      if (grantsBuilder_ == null) {
        grantsBuilder_ = new com.google.protobuf.RepeatedFieldBuilderV3<
            com.cosmos.authz.v1beta1.GrantAuthorization, com.cosmos.authz.v1beta1.GrantAuthorization.Builder, com.cosmos.authz.v1beta1.GrantAuthorizationOrBuilder>(
                grants_,
                ((bitField0_ & 0x00000001) != 0),
                getParentForChildren(),
                isClean());
        grants_ = null;
      }
      return grantsBuilder_;
    }

    private com.cosmos.base.query.v1beta1.PageResponse pagination_;
    private com.google.protobuf.SingleFieldBuilderV3<
        com.cosmos.base.query.v1beta1.PageResponse, com.cosmos.base.query.v1beta1.PageResponse.Builder, com.cosmos.base.query.v1beta1.PageResponseOrBuilder> paginationBuilder_;
    /**
     * <pre>
     * pagination defines an pagination for the response.
     * </pre>
     *
     * <code>.cosmos.base.query.v1beta1.PageResponse pagination = 2 [json_name = "pagination"];</code>
     * @return Whether the pagination field is set.
     */
    public boolean hasPagination() {
      return ((bitField0_ & 0x00000002) != 0);
    }
    /**
     * <pre>
     * pagination defines an pagination for the response.
     * </pre>
     *
     * <code>.cosmos.base.query.v1beta1.PageResponse pagination = 2 [json_name = "pagination"];</code>
     * @return The pagination.
     */
    public com.cosmos.base.query.v1beta1.PageResponse getPagination() {
      if (paginationBuilder_ == null) {
        return pagination_ == null ? com.cosmos.base.query.v1beta1.PageResponse.getDefaultInstance() : pagination_;
      } else {
        return paginationBuilder_.getMessage();
      }
    }
    /**
     * <pre>
     * pagination defines an pagination for the response.
     * </pre>
     *
     * <code>.cosmos.base.query.v1beta1.PageResponse pagination = 2 [json_name = "pagination"];</code>
     */
    public Builder setPagination(com.cosmos.base.query.v1beta1.PageResponse value) {
      if (paginationBuilder_ == null) {
        if (value == null) {
          throw new NullPointerException();
        }
        pagination_ = value;
      } else {
        paginationBuilder_.setMessage(value);
      }
      bitField0_ |= 0x00000002;
      onChanged();
      return this;
    }
    /**
     * <pre>
     * pagination defines an pagination for the response.
     * </pre>
     *
     * <code>.cosmos.base.query.v1beta1.PageResponse pagination = 2 [json_name = "pagination"];</code>
     */
    public Builder setPagination(
        com.cosmos.base.query.v1beta1.PageResponse.Builder builderForValue) {
      if (paginationBuilder_ == null) {
        pagination_ = builderForValue.build();
      } else {
        paginationBuilder_.setMessage(builderForValue.build());
      }
      bitField0_ |= 0x00000002;
      onChanged();
      return this;
    }
    /**
     * <pre>
     * pagination defines an pagination for the response.
     * </pre>
     *
     * <code>.cosmos.base.query.v1beta1.PageResponse pagination = 2 [json_name = "pagination"];</code>
     */
    public Builder mergePagination(com.cosmos.base.query.v1beta1.PageResponse value) {
      if (paginationBuilder_ == null) {
        if (((bitField0_ & 0x00000002) != 0) &&
          pagination_ != null &&
          pagination_ != com.cosmos.base.query.v1beta1.PageResponse.getDefaultInstance()) {
          getPaginationBuilder().mergeFrom(value);
        } else {
          pagination_ = value;
        }
      } else {
        paginationBuilder_.mergeFrom(value);
      }
      bitField0_ |= 0x00000002;
      onChanged();
      return this;
    }
    /**
     * <pre>
     * pagination defines an pagination for the response.
     * </pre>
     *
     * <code>.cosmos.base.query.v1beta1.PageResponse pagination = 2 [json_name = "pagination"];</code>
     */
    public Builder clearPagination() {
      bitField0_ = (bitField0_ & ~0x00000002);
      pagination_ = null;
      if (paginationBuilder_ != null) {
        paginationBuilder_.dispose();
        paginationBuilder_ = null;
      }
      onChanged();
      return this;
    }
    /**
     * <pre>
     * pagination defines an pagination for the response.
     * </pre>
     *
     * <code>.cosmos.base.query.v1beta1.PageResponse pagination = 2 [json_name = "pagination"];</code>
     */
    public com.cosmos.base.query.v1beta1.PageResponse.Builder getPaginationBuilder() {
      bitField0_ |= 0x00000002;
      onChanged();
      return getPaginationFieldBuilder().getBuilder();
    }
    /**
     * <pre>
     * pagination defines an pagination for the response.
     * </pre>
     *
     * <code>.cosmos.base.query.v1beta1.PageResponse pagination = 2 [json_name = "pagination"];</code>
     */
    public com.cosmos.base.query.v1beta1.PageResponseOrBuilder getPaginationOrBuilder() {
      if (paginationBuilder_ != null) {
        return paginationBuilder_.getMessageOrBuilder();
      } else {
        return pagination_ == null ?
            com.cosmos.base.query.v1beta1.PageResponse.getDefaultInstance() : pagination_;
      }
    }
    /**
     * <pre>
     * pagination defines an pagination for the response.
     * </pre>
     *
     * <code>.cosmos.base.query.v1beta1.PageResponse pagination = 2 [json_name = "pagination"];</code>
     */
    private com.google.protobuf.SingleFieldBuilderV3<
        com.cosmos.base.query.v1beta1.PageResponse, com.cosmos.base.query.v1beta1.PageResponse.Builder, com.cosmos.base.query.v1beta1.PageResponseOrBuilder> 
        getPaginationFieldBuilder() {
      if (paginationBuilder_ == null) {
        paginationBuilder_ = new com.google.protobuf.SingleFieldBuilderV3<
            com.cosmos.base.query.v1beta1.PageResponse, com.cosmos.base.query.v1beta1.PageResponse.Builder, com.cosmos.base.query.v1beta1.PageResponseOrBuilder>(
                getPagination(),
                getParentForChildren(),
                isClean());
        pagination_ = null;
      }
      return paginationBuilder_;
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


    // @@protoc_insertion_point(builder_scope:cosmos.authz.v1beta1.QueryGranterGrantsResponse)
  }

  // @@protoc_insertion_point(class_scope:cosmos.authz.v1beta1.QueryGranterGrantsResponse)
  private static final com.cosmos.authz.v1beta1.QueryGranterGrantsResponse DEFAULT_INSTANCE;
  static {
    DEFAULT_INSTANCE = new com.cosmos.authz.v1beta1.QueryGranterGrantsResponse();
  }

  public static com.cosmos.authz.v1beta1.QueryGranterGrantsResponse getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }

  private static final com.google.protobuf.Parser<QueryGranterGrantsResponse>
      PARSER = new com.google.protobuf.AbstractParser<QueryGranterGrantsResponse>() {
    @java.lang.Override
    public QueryGranterGrantsResponse parsePartialFrom(
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

  public static com.google.protobuf.Parser<QueryGranterGrantsResponse> parser() {
    return PARSER;
  }

  @java.lang.Override
  public com.google.protobuf.Parser<QueryGranterGrantsResponse> getParserForType() {
    return PARSER;
  }

  @java.lang.Override
  public com.cosmos.authz.v1beta1.QueryGranterGrantsResponse getDefaultInstanceForType() {
    return DEFAULT_INSTANCE;
  }

}

