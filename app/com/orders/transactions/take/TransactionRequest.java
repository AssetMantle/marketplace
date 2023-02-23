// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: modules/orders/internal/transactions/take/transactionRequest.v1.proto

package com.orders.transactions.take;

/**
 * Protobuf type {@code orders.transactions.take.TransactionRequest}
 */
public final class TransactionRequest extends
    com.google.protobuf.GeneratedMessageV3 implements
    // @@protoc_insertion_point(message_implements:orders.transactions.take.TransactionRequest)
    TransactionRequestOrBuilder {
private static final long serialVersionUID = 0L;
  // Use TransactionRequest.newBuilder() to construct.
  private TransactionRequest(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
    super(builder);
  }
  private TransactionRequest() {
    from_ = "";
    fromID_ = "";
    takerOwnableSplit_ = "";
    orderID_ = "";
  }

  @java.lang.Override
  @SuppressWarnings({"unused"})
  protected java.lang.Object newInstance(
      UnusedPrivateParameter unused) {
    return new TransactionRequest();
  }

  public static final com.google.protobuf.Descriptors.Descriptor
      getDescriptor() {
    return com.orders.transactions.take.TransactionRequestV1Proto.internal_static_orders_transactions_take_TransactionRequest_descriptor;
  }

  @java.lang.Override
  protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internalGetFieldAccessorTable() {
    return com.orders.transactions.take.TransactionRequestV1Proto.internal_static_orders_transactions_take_TransactionRequest_fieldAccessorTable
        .ensureFieldAccessorsInitialized(
            com.orders.transactions.take.TransactionRequest.class, com.orders.transactions.take.TransactionRequest.Builder.class);
  }

  public static final int FROM_FIELD_NUMBER = 1;
  @SuppressWarnings("serial")
  private volatile java.lang.Object from_ = "";
  /**
   * <code>string from = 1 [json_name = "from"];</code>
   * @return The from.
   */
  @java.lang.Override
  public java.lang.String getFrom() {
    java.lang.Object ref = from_;
    if (ref instanceof java.lang.String) {
      return (java.lang.String) ref;
    } else {
      com.google.protobuf.ByteString bs = 
          (com.google.protobuf.ByteString) ref;
      java.lang.String s = bs.toStringUtf8();
      from_ = s;
      return s;
    }
  }
  /**
   * <code>string from = 1 [json_name = "from"];</code>
   * @return The bytes for from.
   */
  @java.lang.Override
  public com.google.protobuf.ByteString
      getFromBytes() {
    java.lang.Object ref = from_;
    if (ref instanceof java.lang.String) {
      com.google.protobuf.ByteString b = 
          com.google.protobuf.ByteString.copyFromUtf8(
              (java.lang.String) ref);
      from_ = b;
      return b;
    } else {
      return (com.google.protobuf.ByteString) ref;
    }
  }

  public static final int FROM_I_D_FIELD_NUMBER = 2;
  @SuppressWarnings("serial")
  private volatile java.lang.Object fromID_ = "";
  /**
   * <code>string from_i_d = 2 [json_name = "fromID"];</code>
   * @return The fromID.
   */
  @java.lang.Override
  public java.lang.String getFromID() {
    java.lang.Object ref = fromID_;
    if (ref instanceof java.lang.String) {
      return (java.lang.String) ref;
    } else {
      com.google.protobuf.ByteString bs = 
          (com.google.protobuf.ByteString) ref;
      java.lang.String s = bs.toStringUtf8();
      fromID_ = s;
      return s;
    }
  }
  /**
   * <code>string from_i_d = 2 [json_name = "fromID"];</code>
   * @return The bytes for fromID.
   */
  @java.lang.Override
  public com.google.protobuf.ByteString
      getFromIDBytes() {
    java.lang.Object ref = fromID_;
    if (ref instanceof java.lang.String) {
      com.google.protobuf.ByteString b = 
          com.google.protobuf.ByteString.copyFromUtf8(
              (java.lang.String) ref);
      fromID_ = b;
      return b;
    } else {
      return (com.google.protobuf.ByteString) ref;
    }
  }

  public static final int TAKER_OWNABLE_SPLIT_FIELD_NUMBER = 3;
  @SuppressWarnings("serial")
  private volatile java.lang.Object takerOwnableSplit_ = "";
  /**
   * <code>string taker_ownable_split = 3 [json_name = "takerOwnableSplit"];</code>
   * @return The takerOwnableSplit.
   */
  @java.lang.Override
  public java.lang.String getTakerOwnableSplit() {
    java.lang.Object ref = takerOwnableSplit_;
    if (ref instanceof java.lang.String) {
      return (java.lang.String) ref;
    } else {
      com.google.protobuf.ByteString bs = 
          (com.google.protobuf.ByteString) ref;
      java.lang.String s = bs.toStringUtf8();
      takerOwnableSplit_ = s;
      return s;
    }
  }
  /**
   * <code>string taker_ownable_split = 3 [json_name = "takerOwnableSplit"];</code>
   * @return The bytes for takerOwnableSplit.
   */
  @java.lang.Override
  public com.google.protobuf.ByteString
      getTakerOwnableSplitBytes() {
    java.lang.Object ref = takerOwnableSplit_;
    if (ref instanceof java.lang.String) {
      com.google.protobuf.ByteString b = 
          com.google.protobuf.ByteString.copyFromUtf8(
              (java.lang.String) ref);
      takerOwnableSplit_ = b;
      return b;
    } else {
      return (com.google.protobuf.ByteString) ref;
    }
  }

  public static final int ORDER_I_D_FIELD_NUMBER = 4;
  @SuppressWarnings("serial")
  private volatile java.lang.Object orderID_ = "";
  /**
   * <code>string order_i_d = 4 [json_name = "orderID"];</code>
   * @return The orderID.
   */
  @java.lang.Override
  public java.lang.String getOrderID() {
    java.lang.Object ref = orderID_;
    if (ref instanceof java.lang.String) {
      return (java.lang.String) ref;
    } else {
      com.google.protobuf.ByteString bs = 
          (com.google.protobuf.ByteString) ref;
      java.lang.String s = bs.toStringUtf8();
      orderID_ = s;
      return s;
    }
  }
  /**
   * <code>string order_i_d = 4 [json_name = "orderID"];</code>
   * @return The bytes for orderID.
   */
  @java.lang.Override
  public com.google.protobuf.ByteString
      getOrderIDBytes() {
    java.lang.Object ref = orderID_;
    if (ref instanceof java.lang.String) {
      com.google.protobuf.ByteString b = 
          com.google.protobuf.ByteString.copyFromUtf8(
              (java.lang.String) ref);
      orderID_ = b;
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
    if (!com.google.protobuf.GeneratedMessageV3.isStringEmpty(from_)) {
      com.google.protobuf.GeneratedMessageV3.writeString(output, 1, from_);
    }
    if (!com.google.protobuf.GeneratedMessageV3.isStringEmpty(fromID_)) {
      com.google.protobuf.GeneratedMessageV3.writeString(output, 2, fromID_);
    }
    if (!com.google.protobuf.GeneratedMessageV3.isStringEmpty(takerOwnableSplit_)) {
      com.google.protobuf.GeneratedMessageV3.writeString(output, 3, takerOwnableSplit_);
    }
    if (!com.google.protobuf.GeneratedMessageV3.isStringEmpty(orderID_)) {
      com.google.protobuf.GeneratedMessageV3.writeString(output, 4, orderID_);
    }
    getUnknownFields().writeTo(output);
  }

  @java.lang.Override
  public int getSerializedSize() {
    int size = memoizedSize;
    if (size != -1) return size;

    size = 0;
    if (!com.google.protobuf.GeneratedMessageV3.isStringEmpty(from_)) {
      size += com.google.protobuf.GeneratedMessageV3.computeStringSize(1, from_);
    }
    if (!com.google.protobuf.GeneratedMessageV3.isStringEmpty(fromID_)) {
      size += com.google.protobuf.GeneratedMessageV3.computeStringSize(2, fromID_);
    }
    if (!com.google.protobuf.GeneratedMessageV3.isStringEmpty(takerOwnableSplit_)) {
      size += com.google.protobuf.GeneratedMessageV3.computeStringSize(3, takerOwnableSplit_);
    }
    if (!com.google.protobuf.GeneratedMessageV3.isStringEmpty(orderID_)) {
      size += com.google.protobuf.GeneratedMessageV3.computeStringSize(4, orderID_);
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
    if (!(obj instanceof com.orders.transactions.take.TransactionRequest)) {
      return super.equals(obj);
    }
    com.orders.transactions.take.TransactionRequest other = (com.orders.transactions.take.TransactionRequest) obj;

    if (!getFrom()
        .equals(other.getFrom())) return false;
    if (!getFromID()
        .equals(other.getFromID())) return false;
    if (!getTakerOwnableSplit()
        .equals(other.getTakerOwnableSplit())) return false;
    if (!getOrderID()
        .equals(other.getOrderID())) return false;
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
    hash = (37 * hash) + FROM_FIELD_NUMBER;
    hash = (53 * hash) + getFrom().hashCode();
    hash = (37 * hash) + FROM_I_D_FIELD_NUMBER;
    hash = (53 * hash) + getFromID().hashCode();
    hash = (37 * hash) + TAKER_OWNABLE_SPLIT_FIELD_NUMBER;
    hash = (53 * hash) + getTakerOwnableSplit().hashCode();
    hash = (37 * hash) + ORDER_I_D_FIELD_NUMBER;
    hash = (53 * hash) + getOrderID().hashCode();
    hash = (29 * hash) + getUnknownFields().hashCode();
    memoizedHashCode = hash;
    return hash;
  }

  public static com.orders.transactions.take.TransactionRequest parseFrom(
      java.nio.ByteBuffer data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static com.orders.transactions.take.TransactionRequest parseFrom(
      java.nio.ByteBuffer data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static com.orders.transactions.take.TransactionRequest parseFrom(
      com.google.protobuf.ByteString data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static com.orders.transactions.take.TransactionRequest parseFrom(
      com.google.protobuf.ByteString data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static com.orders.transactions.take.TransactionRequest parseFrom(byte[] data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static com.orders.transactions.take.TransactionRequest parseFrom(
      byte[] data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static com.orders.transactions.take.TransactionRequest parseFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static com.orders.transactions.take.TransactionRequest parseFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input, extensionRegistry);
  }
  public static com.orders.transactions.take.TransactionRequest parseDelimitedFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input);
  }
  public static com.orders.transactions.take.TransactionRequest parseDelimitedFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
  }
  public static com.orders.transactions.take.TransactionRequest parseFrom(
      com.google.protobuf.CodedInputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static com.orders.transactions.take.TransactionRequest parseFrom(
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
  public static Builder newBuilder(com.orders.transactions.take.TransactionRequest prototype) {
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
   * Protobuf type {@code orders.transactions.take.TransactionRequest}
   */
  public static final class Builder extends
      com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
      // @@protoc_insertion_point(builder_implements:orders.transactions.take.TransactionRequest)
      com.orders.transactions.take.TransactionRequestOrBuilder {
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return com.orders.transactions.take.TransactionRequestV1Proto.internal_static_orders_transactions_take_TransactionRequest_descriptor;
    }

    @java.lang.Override
    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return com.orders.transactions.take.TransactionRequestV1Proto.internal_static_orders_transactions_take_TransactionRequest_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              com.orders.transactions.take.TransactionRequest.class, com.orders.transactions.take.TransactionRequest.Builder.class);
    }

    // Construct using com.orders.transactions.take.TransactionRequest.newBuilder()
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
      from_ = "";
      fromID_ = "";
      takerOwnableSplit_ = "";
      orderID_ = "";
      return this;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.Descriptor
        getDescriptorForType() {
      return com.orders.transactions.take.TransactionRequestV1Proto.internal_static_orders_transactions_take_TransactionRequest_descriptor;
    }

    @java.lang.Override
    public com.orders.transactions.take.TransactionRequest getDefaultInstanceForType() {
      return com.orders.transactions.take.TransactionRequest.getDefaultInstance();
    }

    @java.lang.Override
    public com.orders.transactions.take.TransactionRequest build() {
      com.orders.transactions.take.TransactionRequest result = buildPartial();
      if (!result.isInitialized()) {
        throw newUninitializedMessageException(result);
      }
      return result;
    }

    @java.lang.Override
    public com.orders.transactions.take.TransactionRequest buildPartial() {
      com.orders.transactions.take.TransactionRequest result = new com.orders.transactions.take.TransactionRequest(this);
      if (bitField0_ != 0) { buildPartial0(result); }
      onBuilt();
      return result;
    }

    private void buildPartial0(com.orders.transactions.take.TransactionRequest result) {
      int from_bitField0_ = bitField0_;
      if (((from_bitField0_ & 0x00000001) != 0)) {
        result.from_ = from_;
      }
      if (((from_bitField0_ & 0x00000002) != 0)) {
        result.fromID_ = fromID_;
      }
      if (((from_bitField0_ & 0x00000004) != 0)) {
        result.takerOwnableSplit_ = takerOwnableSplit_;
      }
      if (((from_bitField0_ & 0x00000008) != 0)) {
        result.orderID_ = orderID_;
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
      if (other instanceof com.orders.transactions.take.TransactionRequest) {
        return mergeFrom((com.orders.transactions.take.TransactionRequest)other);
      } else {
        super.mergeFrom(other);
        return this;
      }
    }

    public Builder mergeFrom(com.orders.transactions.take.TransactionRequest other) {
      if (other == com.orders.transactions.take.TransactionRequest.getDefaultInstance()) return this;
      if (!other.getFrom().isEmpty()) {
        from_ = other.from_;
        bitField0_ |= 0x00000001;
        onChanged();
      }
      if (!other.getFromID().isEmpty()) {
        fromID_ = other.fromID_;
        bitField0_ |= 0x00000002;
        onChanged();
      }
      if (!other.getTakerOwnableSplit().isEmpty()) {
        takerOwnableSplit_ = other.takerOwnableSplit_;
        bitField0_ |= 0x00000004;
        onChanged();
      }
      if (!other.getOrderID().isEmpty()) {
        orderID_ = other.orderID_;
        bitField0_ |= 0x00000008;
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
              from_ = input.readStringRequireUtf8();
              bitField0_ |= 0x00000001;
              break;
            } // case 10
            case 18: {
              fromID_ = input.readStringRequireUtf8();
              bitField0_ |= 0x00000002;
              break;
            } // case 18
            case 26: {
              takerOwnableSplit_ = input.readStringRequireUtf8();
              bitField0_ |= 0x00000004;
              break;
            } // case 26
            case 34: {
              orderID_ = input.readStringRequireUtf8();
              bitField0_ |= 0x00000008;
              break;
            } // case 34
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

    private java.lang.Object from_ = "";
    /**
     * <code>string from = 1 [json_name = "from"];</code>
     * @return The from.
     */
    public java.lang.String getFrom() {
      java.lang.Object ref = from_;
      if (!(ref instanceof java.lang.String)) {
        com.google.protobuf.ByteString bs =
            (com.google.protobuf.ByteString) ref;
        java.lang.String s = bs.toStringUtf8();
        from_ = s;
        return s;
      } else {
        return (java.lang.String) ref;
      }
    }
    /**
     * <code>string from = 1 [json_name = "from"];</code>
     * @return The bytes for from.
     */
    public com.google.protobuf.ByteString
        getFromBytes() {
      java.lang.Object ref = from_;
      if (ref instanceof String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8(
                (java.lang.String) ref);
        from_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }
    /**
     * <code>string from = 1 [json_name = "from"];</code>
     * @param value The from to set.
     * @return This builder for chaining.
     */
    public Builder setFrom(
        java.lang.String value) {
      if (value == null) { throw new NullPointerException(); }
      from_ = value;
      bitField0_ |= 0x00000001;
      onChanged();
      return this;
    }
    /**
     * <code>string from = 1 [json_name = "from"];</code>
     * @return This builder for chaining.
     */
    public Builder clearFrom() {
      from_ = getDefaultInstance().getFrom();
      bitField0_ = (bitField0_ & ~0x00000001);
      onChanged();
      return this;
    }
    /**
     * <code>string from = 1 [json_name = "from"];</code>
     * @param value The bytes for from to set.
     * @return This builder for chaining.
     */
    public Builder setFromBytes(
        com.google.protobuf.ByteString value) {
      if (value == null) { throw new NullPointerException(); }
      checkByteStringIsUtf8(value);
      from_ = value;
      bitField0_ |= 0x00000001;
      onChanged();
      return this;
    }

    private java.lang.Object fromID_ = "";
    /**
     * <code>string from_i_d = 2 [json_name = "fromID"];</code>
     * @return The fromID.
     */
    public java.lang.String getFromID() {
      java.lang.Object ref = fromID_;
      if (!(ref instanceof java.lang.String)) {
        com.google.protobuf.ByteString bs =
            (com.google.protobuf.ByteString) ref;
        java.lang.String s = bs.toStringUtf8();
        fromID_ = s;
        return s;
      } else {
        return (java.lang.String) ref;
      }
    }
    /**
     * <code>string from_i_d = 2 [json_name = "fromID"];</code>
     * @return The bytes for fromID.
     */
    public com.google.protobuf.ByteString
        getFromIDBytes() {
      java.lang.Object ref = fromID_;
      if (ref instanceof String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8(
                (java.lang.String) ref);
        fromID_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }
    /**
     * <code>string from_i_d = 2 [json_name = "fromID"];</code>
     * @param value The fromID to set.
     * @return This builder for chaining.
     */
    public Builder setFromID(
        java.lang.String value) {
      if (value == null) { throw new NullPointerException(); }
      fromID_ = value;
      bitField0_ |= 0x00000002;
      onChanged();
      return this;
    }
    /**
     * <code>string from_i_d = 2 [json_name = "fromID"];</code>
     * @return This builder for chaining.
     */
    public Builder clearFromID() {
      fromID_ = getDefaultInstance().getFromID();
      bitField0_ = (bitField0_ & ~0x00000002);
      onChanged();
      return this;
    }
    /**
     * <code>string from_i_d = 2 [json_name = "fromID"];</code>
     * @param value The bytes for fromID to set.
     * @return This builder for chaining.
     */
    public Builder setFromIDBytes(
        com.google.protobuf.ByteString value) {
      if (value == null) { throw new NullPointerException(); }
      checkByteStringIsUtf8(value);
      fromID_ = value;
      bitField0_ |= 0x00000002;
      onChanged();
      return this;
    }

    private java.lang.Object takerOwnableSplit_ = "";
    /**
     * <code>string taker_ownable_split = 3 [json_name = "takerOwnableSplit"];</code>
     * @return The takerOwnableSplit.
     */
    public java.lang.String getTakerOwnableSplit() {
      java.lang.Object ref = takerOwnableSplit_;
      if (!(ref instanceof java.lang.String)) {
        com.google.protobuf.ByteString bs =
            (com.google.protobuf.ByteString) ref;
        java.lang.String s = bs.toStringUtf8();
        takerOwnableSplit_ = s;
        return s;
      } else {
        return (java.lang.String) ref;
      }
    }
    /**
     * <code>string taker_ownable_split = 3 [json_name = "takerOwnableSplit"];</code>
     * @return The bytes for takerOwnableSplit.
     */
    public com.google.protobuf.ByteString
        getTakerOwnableSplitBytes() {
      java.lang.Object ref = takerOwnableSplit_;
      if (ref instanceof String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8(
                (java.lang.String) ref);
        takerOwnableSplit_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }
    /**
     * <code>string taker_ownable_split = 3 [json_name = "takerOwnableSplit"];</code>
     * @param value The takerOwnableSplit to set.
     * @return This builder for chaining.
     */
    public Builder setTakerOwnableSplit(
        java.lang.String value) {
      if (value == null) { throw new NullPointerException(); }
      takerOwnableSplit_ = value;
      bitField0_ |= 0x00000004;
      onChanged();
      return this;
    }
    /**
     * <code>string taker_ownable_split = 3 [json_name = "takerOwnableSplit"];</code>
     * @return This builder for chaining.
     */
    public Builder clearTakerOwnableSplit() {
      takerOwnableSplit_ = getDefaultInstance().getTakerOwnableSplit();
      bitField0_ = (bitField0_ & ~0x00000004);
      onChanged();
      return this;
    }
    /**
     * <code>string taker_ownable_split = 3 [json_name = "takerOwnableSplit"];</code>
     * @param value The bytes for takerOwnableSplit to set.
     * @return This builder for chaining.
     */
    public Builder setTakerOwnableSplitBytes(
        com.google.protobuf.ByteString value) {
      if (value == null) { throw new NullPointerException(); }
      checkByteStringIsUtf8(value);
      takerOwnableSplit_ = value;
      bitField0_ |= 0x00000004;
      onChanged();
      return this;
    }

    private java.lang.Object orderID_ = "";
    /**
     * <code>string order_i_d = 4 [json_name = "orderID"];</code>
     * @return The orderID.
     */
    public java.lang.String getOrderID() {
      java.lang.Object ref = orderID_;
      if (!(ref instanceof java.lang.String)) {
        com.google.protobuf.ByteString bs =
            (com.google.protobuf.ByteString) ref;
        java.lang.String s = bs.toStringUtf8();
        orderID_ = s;
        return s;
      } else {
        return (java.lang.String) ref;
      }
    }
    /**
     * <code>string order_i_d = 4 [json_name = "orderID"];</code>
     * @return The bytes for orderID.
     */
    public com.google.protobuf.ByteString
        getOrderIDBytes() {
      java.lang.Object ref = orderID_;
      if (ref instanceof String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8(
                (java.lang.String) ref);
        orderID_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }
    /**
     * <code>string order_i_d = 4 [json_name = "orderID"];</code>
     * @param value The orderID to set.
     * @return This builder for chaining.
     */
    public Builder setOrderID(
        java.lang.String value) {
      if (value == null) { throw new NullPointerException(); }
      orderID_ = value;
      bitField0_ |= 0x00000008;
      onChanged();
      return this;
    }
    /**
     * <code>string order_i_d = 4 [json_name = "orderID"];</code>
     * @return This builder for chaining.
     */
    public Builder clearOrderID() {
      orderID_ = getDefaultInstance().getOrderID();
      bitField0_ = (bitField0_ & ~0x00000008);
      onChanged();
      return this;
    }
    /**
     * <code>string order_i_d = 4 [json_name = "orderID"];</code>
     * @param value The bytes for orderID to set.
     * @return This builder for chaining.
     */
    public Builder setOrderIDBytes(
        com.google.protobuf.ByteString value) {
      if (value == null) { throw new NullPointerException(); }
      checkByteStringIsUtf8(value);
      orderID_ = value;
      bitField0_ |= 0x00000008;
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


    // @@protoc_insertion_point(builder_scope:orders.transactions.take.TransactionRequest)
  }

  // @@protoc_insertion_point(class_scope:orders.transactions.take.TransactionRequest)
  private static final com.orders.transactions.take.TransactionRequest DEFAULT_INSTANCE;
  static {
    DEFAULT_INSTANCE = new com.orders.transactions.take.TransactionRequest();
  }

  public static com.orders.transactions.take.TransactionRequest getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }

  private static final com.google.protobuf.Parser<TransactionRequest>
      PARSER = new com.google.protobuf.AbstractParser<TransactionRequest>() {
    @java.lang.Override
    public TransactionRequest parsePartialFrom(
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

  public static com.google.protobuf.Parser<TransactionRequest> parser() {
    return PARSER;
  }

  @java.lang.Override
  public com.google.protobuf.Parser<TransactionRequest> getParserForType() {
    return PARSER;
  }

  @java.lang.Override
  public com.orders.transactions.take.TransactionRequest getDefaultInstanceForType() {
    return DEFAULT_INSTANCE;
  }

}
