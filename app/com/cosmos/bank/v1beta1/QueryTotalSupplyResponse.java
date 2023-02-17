// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: cosmos/bank/v1beta1/query.proto

package com.cosmos.bank.v1beta1;

/**
 * <pre>
 * QueryTotalSupplyResponse is the response type for the Query/TotalSupply RPC
 * method
 * </pre>
 *
 * Protobuf type {@code cosmos.bank.v1beta1.QueryTotalSupplyResponse}
 */
public final class QueryTotalSupplyResponse extends
    com.google.protobuf.GeneratedMessageV3 implements
    // @@protoc_insertion_point(message_implements:cosmos.bank.v1beta1.QueryTotalSupplyResponse)
    QueryTotalSupplyResponseOrBuilder {
private static final long serialVersionUID = 0L;
  // Use QueryTotalSupplyResponse.newBuilder() to construct.
  private QueryTotalSupplyResponse(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
    super(builder);
  }
  private QueryTotalSupplyResponse() {
    supply_ = java.util.Collections.emptyList();
  }

  @java.lang.Override
  @SuppressWarnings({"unused"})
  protected java.lang.Object newInstance(
      UnusedPrivateParameter unused) {
    return new QueryTotalSupplyResponse();
  }

  public static final com.google.protobuf.Descriptors.Descriptor
      getDescriptor() {
    return com.cosmos.bank.v1beta1.QueryProto.internal_static_cosmos_bank_v1beta1_QueryTotalSupplyResponse_descriptor;
  }

  @java.lang.Override
  protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internalGetFieldAccessorTable() {
    return com.cosmos.bank.v1beta1.QueryProto.internal_static_cosmos_bank_v1beta1_QueryTotalSupplyResponse_fieldAccessorTable
        .ensureFieldAccessorsInitialized(
            com.cosmos.bank.v1beta1.QueryTotalSupplyResponse.class, com.cosmos.bank.v1beta1.QueryTotalSupplyResponse.Builder.class);
  }

  public static final int SUPPLY_FIELD_NUMBER = 1;
  @SuppressWarnings("serial")
  private java.util.List<com.cosmos.base.v1beta1.Coin> supply_;
  /**
   * <pre>
   * supply is the supply of the coins
   * </pre>
   *
   * <code>repeated .cosmos.base.v1beta1.Coin supply = 1 [json_name = "supply", (.gogoproto.nullable) = false, (.gogoproto.castrepeated) = "github.com/cosmos/cosmos-sdk/types.Coins"];</code>
   */
  @java.lang.Override
  public java.util.List<com.cosmos.base.v1beta1.Coin> getSupplyList() {
    return supply_;
  }
  /**
   * <pre>
   * supply is the supply of the coins
   * </pre>
   *
   * <code>repeated .cosmos.base.v1beta1.Coin supply = 1 [json_name = "supply", (.gogoproto.nullable) = false, (.gogoproto.castrepeated) = "github.com/cosmos/cosmos-sdk/types.Coins"];</code>
   */
  @java.lang.Override
  public java.util.List<? extends com.cosmos.base.v1beta1.CoinOrBuilder> 
      getSupplyOrBuilderList() {
    return supply_;
  }
  /**
   * <pre>
   * supply is the supply of the coins
   * </pre>
   *
   * <code>repeated .cosmos.base.v1beta1.Coin supply = 1 [json_name = "supply", (.gogoproto.nullable) = false, (.gogoproto.castrepeated) = "github.com/cosmos/cosmos-sdk/types.Coins"];</code>
   */
  @java.lang.Override
  public int getSupplyCount() {
    return supply_.size();
  }
  /**
   * <pre>
   * supply is the supply of the coins
   * </pre>
   *
   * <code>repeated .cosmos.base.v1beta1.Coin supply = 1 [json_name = "supply", (.gogoproto.nullable) = false, (.gogoproto.castrepeated) = "github.com/cosmos/cosmos-sdk/types.Coins"];</code>
   */
  @java.lang.Override
  public com.cosmos.base.v1beta1.Coin getSupply(int index) {
    return supply_.get(index);
  }
  /**
   * <pre>
   * supply is the supply of the coins
   * </pre>
   *
   * <code>repeated .cosmos.base.v1beta1.Coin supply = 1 [json_name = "supply", (.gogoproto.nullable) = false, (.gogoproto.castrepeated) = "github.com/cosmos/cosmos-sdk/types.Coins"];</code>
   */
  @java.lang.Override
  public com.cosmos.base.v1beta1.CoinOrBuilder getSupplyOrBuilder(
      int index) {
    return supply_.get(index);
  }

  public static final int PAGINATION_FIELD_NUMBER = 2;
  private com.cosmos.base.query.v1beta1.PageResponse pagination_;
  /**
   * <pre>
   * pagination defines the pagination in the response.
   *
   * Since: cosmos-sdk 0.43
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
   * pagination defines the pagination in the response.
   *
   * Since: cosmos-sdk 0.43
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
   * pagination defines the pagination in the response.
   *
   * Since: cosmos-sdk 0.43
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
    for (int i = 0; i < supply_.size(); i++) {
      output.writeMessage(1, supply_.get(i));
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
    for (int i = 0; i < supply_.size(); i++) {
      size += com.google.protobuf.CodedOutputStream
        .computeMessageSize(1, supply_.get(i));
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
    if (!(obj instanceof com.cosmos.bank.v1beta1.QueryTotalSupplyResponse)) {
      return super.equals(obj);
    }
    com.cosmos.bank.v1beta1.QueryTotalSupplyResponse other = (com.cosmos.bank.v1beta1.QueryTotalSupplyResponse) obj;

    if (!getSupplyList()
        .equals(other.getSupplyList())) return false;
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
    if (getSupplyCount() > 0) {
      hash = (37 * hash) + SUPPLY_FIELD_NUMBER;
      hash = (53 * hash) + getSupplyList().hashCode();
    }
    if (hasPagination()) {
      hash = (37 * hash) + PAGINATION_FIELD_NUMBER;
      hash = (53 * hash) + getPagination().hashCode();
    }
    hash = (29 * hash) + getUnknownFields().hashCode();
    memoizedHashCode = hash;
    return hash;
  }

  public static com.cosmos.bank.v1beta1.QueryTotalSupplyResponse parseFrom(
      java.nio.ByteBuffer data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static com.cosmos.bank.v1beta1.QueryTotalSupplyResponse parseFrom(
      java.nio.ByteBuffer data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static com.cosmos.bank.v1beta1.QueryTotalSupplyResponse parseFrom(
      com.google.protobuf.ByteString data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static com.cosmos.bank.v1beta1.QueryTotalSupplyResponse parseFrom(
      com.google.protobuf.ByteString data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static com.cosmos.bank.v1beta1.QueryTotalSupplyResponse parseFrom(byte[] data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static com.cosmos.bank.v1beta1.QueryTotalSupplyResponse parseFrom(
      byte[] data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static com.cosmos.bank.v1beta1.QueryTotalSupplyResponse parseFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static com.cosmos.bank.v1beta1.QueryTotalSupplyResponse parseFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input, extensionRegistry);
  }
  public static com.cosmos.bank.v1beta1.QueryTotalSupplyResponse parseDelimitedFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input);
  }
  public static com.cosmos.bank.v1beta1.QueryTotalSupplyResponse parseDelimitedFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
  }
  public static com.cosmos.bank.v1beta1.QueryTotalSupplyResponse parseFrom(
      com.google.protobuf.CodedInputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static com.cosmos.bank.v1beta1.QueryTotalSupplyResponse parseFrom(
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
  public static Builder newBuilder(com.cosmos.bank.v1beta1.QueryTotalSupplyResponse prototype) {
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
   * QueryTotalSupplyResponse is the response type for the Query/TotalSupply RPC
   * method
   * </pre>
   *
   * Protobuf type {@code cosmos.bank.v1beta1.QueryTotalSupplyResponse}
   */
  public static final class Builder extends
      com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
      // @@protoc_insertion_point(builder_implements:cosmos.bank.v1beta1.QueryTotalSupplyResponse)
      com.cosmos.bank.v1beta1.QueryTotalSupplyResponseOrBuilder {
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return com.cosmos.bank.v1beta1.QueryProto.internal_static_cosmos_bank_v1beta1_QueryTotalSupplyResponse_descriptor;
    }

    @java.lang.Override
    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return com.cosmos.bank.v1beta1.QueryProto.internal_static_cosmos_bank_v1beta1_QueryTotalSupplyResponse_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              com.cosmos.bank.v1beta1.QueryTotalSupplyResponse.class, com.cosmos.bank.v1beta1.QueryTotalSupplyResponse.Builder.class);
    }

    // Construct using com.cosmos.bank.v1beta1.QueryTotalSupplyResponse.newBuilder()
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
      if (supplyBuilder_ == null) {
        supply_ = java.util.Collections.emptyList();
      } else {
        supply_ = null;
        supplyBuilder_.clear();
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
      return com.cosmos.bank.v1beta1.QueryProto.internal_static_cosmos_bank_v1beta1_QueryTotalSupplyResponse_descriptor;
    }

    @java.lang.Override
    public com.cosmos.bank.v1beta1.QueryTotalSupplyResponse getDefaultInstanceForType() {
      return com.cosmos.bank.v1beta1.QueryTotalSupplyResponse.getDefaultInstance();
    }

    @java.lang.Override
    public com.cosmos.bank.v1beta1.QueryTotalSupplyResponse build() {
      com.cosmos.bank.v1beta1.QueryTotalSupplyResponse result = buildPartial();
      if (!result.isInitialized()) {
        throw newUninitializedMessageException(result);
      }
      return result;
    }

    @java.lang.Override
    public com.cosmos.bank.v1beta1.QueryTotalSupplyResponse buildPartial() {
      com.cosmos.bank.v1beta1.QueryTotalSupplyResponse result = new com.cosmos.bank.v1beta1.QueryTotalSupplyResponse(this);
      buildPartialRepeatedFields(result);
      if (bitField0_ != 0) { buildPartial0(result); }
      onBuilt();
      return result;
    }

    private void buildPartialRepeatedFields(com.cosmos.bank.v1beta1.QueryTotalSupplyResponse result) {
      if (supplyBuilder_ == null) {
        if (((bitField0_ & 0x00000001) != 0)) {
          supply_ = java.util.Collections.unmodifiableList(supply_);
          bitField0_ = (bitField0_ & ~0x00000001);
        }
        result.supply_ = supply_;
      } else {
        result.supply_ = supplyBuilder_.build();
      }
    }

    private void buildPartial0(com.cosmos.bank.v1beta1.QueryTotalSupplyResponse result) {
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
      if (other instanceof com.cosmos.bank.v1beta1.QueryTotalSupplyResponse) {
        return mergeFrom((com.cosmos.bank.v1beta1.QueryTotalSupplyResponse)other);
      } else {
        super.mergeFrom(other);
        return this;
      }
    }

    public Builder mergeFrom(com.cosmos.bank.v1beta1.QueryTotalSupplyResponse other) {
      if (other == com.cosmos.bank.v1beta1.QueryTotalSupplyResponse.getDefaultInstance()) return this;
      if (supplyBuilder_ == null) {
        if (!other.supply_.isEmpty()) {
          if (supply_.isEmpty()) {
            supply_ = other.supply_;
            bitField0_ = (bitField0_ & ~0x00000001);
          } else {
            ensureSupplyIsMutable();
            supply_.addAll(other.supply_);
          }
          onChanged();
        }
      } else {
        if (!other.supply_.isEmpty()) {
          if (supplyBuilder_.isEmpty()) {
            supplyBuilder_.dispose();
            supplyBuilder_ = null;
            supply_ = other.supply_;
            bitField0_ = (bitField0_ & ~0x00000001);
            supplyBuilder_ = 
              com.google.protobuf.GeneratedMessageV3.alwaysUseFieldBuilders ?
                 getSupplyFieldBuilder() : null;
          } else {
            supplyBuilder_.addAllMessages(other.supply_);
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
              com.cosmos.base.v1beta1.Coin m =
                  input.readMessage(
                      com.cosmos.base.v1beta1.Coin.parser(),
                      extensionRegistry);
              if (supplyBuilder_ == null) {
                ensureSupplyIsMutable();
                supply_.add(m);
              } else {
                supplyBuilder_.addMessage(m);
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

    private java.util.List<com.cosmos.base.v1beta1.Coin> supply_ =
      java.util.Collections.emptyList();
    private void ensureSupplyIsMutable() {
      if (!((bitField0_ & 0x00000001) != 0)) {
        supply_ = new java.util.ArrayList<com.cosmos.base.v1beta1.Coin>(supply_);
        bitField0_ |= 0x00000001;
       }
    }

    private com.google.protobuf.RepeatedFieldBuilderV3<
        com.cosmos.base.v1beta1.Coin, com.cosmos.base.v1beta1.Coin.Builder, com.cosmos.base.v1beta1.CoinOrBuilder> supplyBuilder_;

    /**
     * <pre>
     * supply is the supply of the coins
     * </pre>
     *
     * <code>repeated .cosmos.base.v1beta1.Coin supply = 1 [json_name = "supply", (.gogoproto.nullable) = false, (.gogoproto.castrepeated) = "github.com/cosmos/cosmos-sdk/types.Coins"];</code>
     */
    public java.util.List<com.cosmos.base.v1beta1.Coin> getSupplyList() {
      if (supplyBuilder_ == null) {
        return java.util.Collections.unmodifiableList(supply_);
      } else {
        return supplyBuilder_.getMessageList();
      }
    }
    /**
     * <pre>
     * supply is the supply of the coins
     * </pre>
     *
     * <code>repeated .cosmos.base.v1beta1.Coin supply = 1 [json_name = "supply", (.gogoproto.nullable) = false, (.gogoproto.castrepeated) = "github.com/cosmos/cosmos-sdk/types.Coins"];</code>
     */
    public int getSupplyCount() {
      if (supplyBuilder_ == null) {
        return supply_.size();
      } else {
        return supplyBuilder_.getCount();
      }
    }
    /**
     * <pre>
     * supply is the supply of the coins
     * </pre>
     *
     * <code>repeated .cosmos.base.v1beta1.Coin supply = 1 [json_name = "supply", (.gogoproto.nullable) = false, (.gogoproto.castrepeated) = "github.com/cosmos/cosmos-sdk/types.Coins"];</code>
     */
    public com.cosmos.base.v1beta1.Coin getSupply(int index) {
      if (supplyBuilder_ == null) {
        return supply_.get(index);
      } else {
        return supplyBuilder_.getMessage(index);
      }
    }
    /**
     * <pre>
     * supply is the supply of the coins
     * </pre>
     *
     * <code>repeated .cosmos.base.v1beta1.Coin supply = 1 [json_name = "supply", (.gogoproto.nullable) = false, (.gogoproto.castrepeated) = "github.com/cosmos/cosmos-sdk/types.Coins"];</code>
     */
    public Builder setSupply(
        int index, com.cosmos.base.v1beta1.Coin value) {
      if (supplyBuilder_ == null) {
        if (value == null) {
          throw new NullPointerException();
        }
        ensureSupplyIsMutable();
        supply_.set(index, value);
        onChanged();
      } else {
        supplyBuilder_.setMessage(index, value);
      }
      return this;
    }
    /**
     * <pre>
     * supply is the supply of the coins
     * </pre>
     *
     * <code>repeated .cosmos.base.v1beta1.Coin supply = 1 [json_name = "supply", (.gogoproto.nullable) = false, (.gogoproto.castrepeated) = "github.com/cosmos/cosmos-sdk/types.Coins"];</code>
     */
    public Builder setSupply(
        int index, com.cosmos.base.v1beta1.Coin.Builder builderForValue) {
      if (supplyBuilder_ == null) {
        ensureSupplyIsMutable();
        supply_.set(index, builderForValue.build());
        onChanged();
      } else {
        supplyBuilder_.setMessage(index, builderForValue.build());
      }
      return this;
    }
    /**
     * <pre>
     * supply is the supply of the coins
     * </pre>
     *
     * <code>repeated .cosmos.base.v1beta1.Coin supply = 1 [json_name = "supply", (.gogoproto.nullable) = false, (.gogoproto.castrepeated) = "github.com/cosmos/cosmos-sdk/types.Coins"];</code>
     */
    public Builder addSupply(com.cosmos.base.v1beta1.Coin value) {
      if (supplyBuilder_ == null) {
        if (value == null) {
          throw new NullPointerException();
        }
        ensureSupplyIsMutable();
        supply_.add(value);
        onChanged();
      } else {
        supplyBuilder_.addMessage(value);
      }
      return this;
    }
    /**
     * <pre>
     * supply is the supply of the coins
     * </pre>
     *
     * <code>repeated .cosmos.base.v1beta1.Coin supply = 1 [json_name = "supply", (.gogoproto.nullable) = false, (.gogoproto.castrepeated) = "github.com/cosmos/cosmos-sdk/types.Coins"];</code>
     */
    public Builder addSupply(
        int index, com.cosmos.base.v1beta1.Coin value) {
      if (supplyBuilder_ == null) {
        if (value == null) {
          throw new NullPointerException();
        }
        ensureSupplyIsMutable();
        supply_.add(index, value);
        onChanged();
      } else {
        supplyBuilder_.addMessage(index, value);
      }
      return this;
    }
    /**
     * <pre>
     * supply is the supply of the coins
     * </pre>
     *
     * <code>repeated .cosmos.base.v1beta1.Coin supply = 1 [json_name = "supply", (.gogoproto.nullable) = false, (.gogoproto.castrepeated) = "github.com/cosmos/cosmos-sdk/types.Coins"];</code>
     */
    public Builder addSupply(
        com.cosmos.base.v1beta1.Coin.Builder builderForValue) {
      if (supplyBuilder_ == null) {
        ensureSupplyIsMutable();
        supply_.add(builderForValue.build());
        onChanged();
      } else {
        supplyBuilder_.addMessage(builderForValue.build());
      }
      return this;
    }
    /**
     * <pre>
     * supply is the supply of the coins
     * </pre>
     *
     * <code>repeated .cosmos.base.v1beta1.Coin supply = 1 [json_name = "supply", (.gogoproto.nullable) = false, (.gogoproto.castrepeated) = "github.com/cosmos/cosmos-sdk/types.Coins"];</code>
     */
    public Builder addSupply(
        int index, com.cosmos.base.v1beta1.Coin.Builder builderForValue) {
      if (supplyBuilder_ == null) {
        ensureSupplyIsMutable();
        supply_.add(index, builderForValue.build());
        onChanged();
      } else {
        supplyBuilder_.addMessage(index, builderForValue.build());
      }
      return this;
    }
    /**
     * <pre>
     * supply is the supply of the coins
     * </pre>
     *
     * <code>repeated .cosmos.base.v1beta1.Coin supply = 1 [json_name = "supply", (.gogoproto.nullable) = false, (.gogoproto.castrepeated) = "github.com/cosmos/cosmos-sdk/types.Coins"];</code>
     */
    public Builder addAllSupply(
        java.lang.Iterable<? extends com.cosmos.base.v1beta1.Coin> values) {
      if (supplyBuilder_ == null) {
        ensureSupplyIsMutable();
        com.google.protobuf.AbstractMessageLite.Builder.addAll(
            values, supply_);
        onChanged();
      } else {
        supplyBuilder_.addAllMessages(values);
      }
      return this;
    }
    /**
     * <pre>
     * supply is the supply of the coins
     * </pre>
     *
     * <code>repeated .cosmos.base.v1beta1.Coin supply = 1 [json_name = "supply", (.gogoproto.nullable) = false, (.gogoproto.castrepeated) = "github.com/cosmos/cosmos-sdk/types.Coins"];</code>
     */
    public Builder clearSupply() {
      if (supplyBuilder_ == null) {
        supply_ = java.util.Collections.emptyList();
        bitField0_ = (bitField0_ & ~0x00000001);
        onChanged();
      } else {
        supplyBuilder_.clear();
      }
      return this;
    }
    /**
     * <pre>
     * supply is the supply of the coins
     * </pre>
     *
     * <code>repeated .cosmos.base.v1beta1.Coin supply = 1 [json_name = "supply", (.gogoproto.nullable) = false, (.gogoproto.castrepeated) = "github.com/cosmos/cosmos-sdk/types.Coins"];</code>
     */
    public Builder removeSupply(int index) {
      if (supplyBuilder_ == null) {
        ensureSupplyIsMutable();
        supply_.remove(index);
        onChanged();
      } else {
        supplyBuilder_.remove(index);
      }
      return this;
    }
    /**
     * <pre>
     * supply is the supply of the coins
     * </pre>
     *
     * <code>repeated .cosmos.base.v1beta1.Coin supply = 1 [json_name = "supply", (.gogoproto.nullable) = false, (.gogoproto.castrepeated) = "github.com/cosmos/cosmos-sdk/types.Coins"];</code>
     */
    public com.cosmos.base.v1beta1.Coin.Builder getSupplyBuilder(
        int index) {
      return getSupplyFieldBuilder().getBuilder(index);
    }
    /**
     * <pre>
     * supply is the supply of the coins
     * </pre>
     *
     * <code>repeated .cosmos.base.v1beta1.Coin supply = 1 [json_name = "supply", (.gogoproto.nullable) = false, (.gogoproto.castrepeated) = "github.com/cosmos/cosmos-sdk/types.Coins"];</code>
     */
    public com.cosmos.base.v1beta1.CoinOrBuilder getSupplyOrBuilder(
        int index) {
      if (supplyBuilder_ == null) {
        return supply_.get(index);  } else {
        return supplyBuilder_.getMessageOrBuilder(index);
      }
    }
    /**
     * <pre>
     * supply is the supply of the coins
     * </pre>
     *
     * <code>repeated .cosmos.base.v1beta1.Coin supply = 1 [json_name = "supply", (.gogoproto.nullable) = false, (.gogoproto.castrepeated) = "github.com/cosmos/cosmos-sdk/types.Coins"];</code>
     */
    public java.util.List<? extends com.cosmos.base.v1beta1.CoinOrBuilder> 
         getSupplyOrBuilderList() {
      if (supplyBuilder_ != null) {
        return supplyBuilder_.getMessageOrBuilderList();
      } else {
        return java.util.Collections.unmodifiableList(supply_);
      }
    }
    /**
     * <pre>
     * supply is the supply of the coins
     * </pre>
     *
     * <code>repeated .cosmos.base.v1beta1.Coin supply = 1 [json_name = "supply", (.gogoproto.nullable) = false, (.gogoproto.castrepeated) = "github.com/cosmos/cosmos-sdk/types.Coins"];</code>
     */
    public com.cosmos.base.v1beta1.Coin.Builder addSupplyBuilder() {
      return getSupplyFieldBuilder().addBuilder(
          com.cosmos.base.v1beta1.Coin.getDefaultInstance());
    }
    /**
     * <pre>
     * supply is the supply of the coins
     * </pre>
     *
     * <code>repeated .cosmos.base.v1beta1.Coin supply = 1 [json_name = "supply", (.gogoproto.nullable) = false, (.gogoproto.castrepeated) = "github.com/cosmos/cosmos-sdk/types.Coins"];</code>
     */
    public com.cosmos.base.v1beta1.Coin.Builder addSupplyBuilder(
        int index) {
      return getSupplyFieldBuilder().addBuilder(
          index, com.cosmos.base.v1beta1.Coin.getDefaultInstance());
    }
    /**
     * <pre>
     * supply is the supply of the coins
     * </pre>
     *
     * <code>repeated .cosmos.base.v1beta1.Coin supply = 1 [json_name = "supply", (.gogoproto.nullable) = false, (.gogoproto.castrepeated) = "github.com/cosmos/cosmos-sdk/types.Coins"];</code>
     */
    public java.util.List<com.cosmos.base.v1beta1.Coin.Builder> 
         getSupplyBuilderList() {
      return getSupplyFieldBuilder().getBuilderList();
    }
    private com.google.protobuf.RepeatedFieldBuilderV3<
        com.cosmos.base.v1beta1.Coin, com.cosmos.base.v1beta1.Coin.Builder, com.cosmos.base.v1beta1.CoinOrBuilder> 
        getSupplyFieldBuilder() {
      if (supplyBuilder_ == null) {
        supplyBuilder_ = new com.google.protobuf.RepeatedFieldBuilderV3<
            com.cosmos.base.v1beta1.Coin, com.cosmos.base.v1beta1.Coin.Builder, com.cosmos.base.v1beta1.CoinOrBuilder>(
                supply_,
                ((bitField0_ & 0x00000001) != 0),
                getParentForChildren(),
                isClean());
        supply_ = null;
      }
      return supplyBuilder_;
    }

    private com.cosmos.base.query.v1beta1.PageResponse pagination_;
    private com.google.protobuf.SingleFieldBuilderV3<
        com.cosmos.base.query.v1beta1.PageResponse, com.cosmos.base.query.v1beta1.PageResponse.Builder, com.cosmos.base.query.v1beta1.PageResponseOrBuilder> paginationBuilder_;
    /**
     * <pre>
     * pagination defines the pagination in the response.
     *
     * Since: cosmos-sdk 0.43
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
     * pagination defines the pagination in the response.
     *
     * Since: cosmos-sdk 0.43
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
     * pagination defines the pagination in the response.
     *
     * Since: cosmos-sdk 0.43
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
     * pagination defines the pagination in the response.
     *
     * Since: cosmos-sdk 0.43
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
     * pagination defines the pagination in the response.
     *
     * Since: cosmos-sdk 0.43
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
     * pagination defines the pagination in the response.
     *
     * Since: cosmos-sdk 0.43
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
     * pagination defines the pagination in the response.
     *
     * Since: cosmos-sdk 0.43
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
     * pagination defines the pagination in the response.
     *
     * Since: cosmos-sdk 0.43
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
     * pagination defines the pagination in the response.
     *
     * Since: cosmos-sdk 0.43
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


    // @@protoc_insertion_point(builder_scope:cosmos.bank.v1beta1.QueryTotalSupplyResponse)
  }

  // @@protoc_insertion_point(class_scope:cosmos.bank.v1beta1.QueryTotalSupplyResponse)
  private static final com.cosmos.bank.v1beta1.QueryTotalSupplyResponse DEFAULT_INSTANCE;
  static {
    DEFAULT_INSTANCE = new com.cosmos.bank.v1beta1.QueryTotalSupplyResponse();
  }

  public static com.cosmos.bank.v1beta1.QueryTotalSupplyResponse getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }

  private static final com.google.protobuf.Parser<QueryTotalSupplyResponse>
      PARSER = new com.google.protobuf.AbstractParser<QueryTotalSupplyResponse>() {
    @java.lang.Override
    public QueryTotalSupplyResponse parsePartialFrom(
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

  public static com.google.protobuf.Parser<QueryTotalSupplyResponse> parser() {
    return PARSER;
  }

  @java.lang.Override
  public com.google.protobuf.Parser<QueryTotalSupplyResponse> getParserForType() {
    return PARSER;
  }

  @java.lang.Override
  public com.cosmos.bank.v1beta1.QueryTotalSupplyResponse getDefaultInstanceForType() {
    return DEFAULT_INSTANCE;
  }

}

