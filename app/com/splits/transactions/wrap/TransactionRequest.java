// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: modules/splits/internal/transactions/wrap/transactionRequest.v1.proto

package com.splits.transactions.wrap;

/**
 * Protobuf type {@code splits.transactions.wrap.TransactionRequest}
 */
public final class TransactionRequest extends
    com.google.protobuf.GeneratedMessageV3 implements
    // @@protoc_insertion_point(message_implements:splits.transactions.wrap.TransactionRequest)
    TransactionRequestOrBuilder {
private static final long serialVersionUID = 0L;
  // Use TransactionRequest.newBuilder() to construct.
  private TransactionRequest(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
    super(builder);
  }
  private TransactionRequest() {
    from_ = "";
    fromID_ = "";
    coins_ = "";
  }

  @java.lang.Override
  @SuppressWarnings({"unused"})
  protected java.lang.Object newInstance(
      UnusedPrivateParameter unused) {
    return new TransactionRequest();
  }

  @java.lang.Override
  public final com.google.protobuf.UnknownFieldSet
  getUnknownFields() {
    return this.unknownFields;
  }
  public static final com.google.protobuf.Descriptors.Descriptor
      getDescriptor() {
    return com.splits.transactions.wrap.TransactionRequestV1Proto.internal_static_splits_transactions_wrap_TransactionRequest_descriptor;
  }

  @java.lang.Override
  protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internalGetFieldAccessorTable() {
    return com.splits.transactions.wrap.TransactionRequestV1Proto.internal_static_splits_transactions_wrap_TransactionRequest_fieldAccessorTable
        .ensureFieldAccessorsInitialized(
            com.splits.transactions.wrap.TransactionRequest.class, com.splits.transactions.wrap.TransactionRequest.Builder.class);
  }

  public static final int FROM_FIELD_NUMBER = 1;
  private volatile java.lang.Object from_;
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
  private volatile java.lang.Object fromID_;
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

  public static final int COINS_FIELD_NUMBER = 3;
  private volatile java.lang.Object coins_;
  /**
   * <code>string coins = 3 [json_name = "coins"];</code>
   * @return The coins.
   */
  @java.lang.Override
  public java.lang.String getCoins() {
    java.lang.Object ref = coins_;
    if (ref instanceof java.lang.String) {
      return (java.lang.String) ref;
    } else {
      com.google.protobuf.ByteString bs = 
          (com.google.protobuf.ByteString) ref;
      java.lang.String s = bs.toStringUtf8();
      coins_ = s;
      return s;
    }
  }
  /**
   * <code>string coins = 3 [json_name = "coins"];</code>
   * @return The bytes for coins.
   */
  @java.lang.Override
  public com.google.protobuf.ByteString
      getCoinsBytes() {
    java.lang.Object ref = coins_;
    if (ref instanceof java.lang.String) {
      com.google.protobuf.ByteString b = 
          com.google.protobuf.ByteString.copyFromUtf8(
              (java.lang.String) ref);
      coins_ = b;
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
    if (!com.google.protobuf.GeneratedMessageV3.isStringEmpty(coins_)) {
      com.google.protobuf.GeneratedMessageV3.writeString(output, 3, coins_);
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
    if (!com.google.protobuf.GeneratedMessageV3.isStringEmpty(coins_)) {
      size += com.google.protobuf.GeneratedMessageV3.computeStringSize(3, coins_);
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
    if (!(obj instanceof com.splits.transactions.wrap.TransactionRequest)) {
      return super.equals(obj);
    }
    com.splits.transactions.wrap.TransactionRequest other = (com.splits.transactions.wrap.TransactionRequest) obj;

    if (!getFrom()
        .equals(other.getFrom())) return false;
    if (!getFromID()
        .equals(other.getFromID())) return false;
    if (!getCoins()
        .equals(other.getCoins())) return false;
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
    hash = (37 * hash) + COINS_FIELD_NUMBER;
    hash = (53 * hash) + getCoins().hashCode();
    hash = (29 * hash) + getUnknownFields().hashCode();
    memoizedHashCode = hash;
    return hash;
  }

  public static com.splits.transactions.wrap.TransactionRequest parseFrom(
      java.nio.ByteBuffer data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static com.splits.transactions.wrap.TransactionRequest parseFrom(
      java.nio.ByteBuffer data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static com.splits.transactions.wrap.TransactionRequest parseFrom(
      com.google.protobuf.ByteString data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static com.splits.transactions.wrap.TransactionRequest parseFrom(
      com.google.protobuf.ByteString data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static com.splits.transactions.wrap.TransactionRequest parseFrom(byte[] data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static com.splits.transactions.wrap.TransactionRequest parseFrom(
      byte[] data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static com.splits.transactions.wrap.TransactionRequest parseFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static com.splits.transactions.wrap.TransactionRequest parseFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input, extensionRegistry);
  }
  public static com.splits.transactions.wrap.TransactionRequest parseDelimitedFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input);
  }
  public static com.splits.transactions.wrap.TransactionRequest parseDelimitedFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
  }
  public static com.splits.transactions.wrap.TransactionRequest parseFrom(
      com.google.protobuf.CodedInputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static com.splits.transactions.wrap.TransactionRequest parseFrom(
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
  public static Builder newBuilder(com.splits.transactions.wrap.TransactionRequest prototype) {
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
   * Protobuf type {@code splits.transactions.wrap.TransactionRequest}
   */
  public static final class Builder extends
      com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
      // @@protoc_insertion_point(builder_implements:splits.transactions.wrap.TransactionRequest)
      com.splits.transactions.wrap.TransactionRequestOrBuilder {
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return com.splits.transactions.wrap.TransactionRequestV1Proto.internal_static_splits_transactions_wrap_TransactionRequest_descriptor;
    }

    @java.lang.Override
    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return com.splits.transactions.wrap.TransactionRequestV1Proto.internal_static_splits_transactions_wrap_TransactionRequest_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              com.splits.transactions.wrap.TransactionRequest.class, com.splits.transactions.wrap.TransactionRequest.Builder.class);
    }

    // Construct using com.splits.transactions.wrap.TransactionRequest.newBuilder()
    private Builder() {

    }

    private Builder(
        com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
      super(parent);

    }
    @java.lang.Override
    public Builder clear() {
      super.clear();
      from_ = "";

      fromID_ = "";

      coins_ = "";

      return this;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.Descriptor
        getDescriptorForType() {
      return com.splits.transactions.wrap.TransactionRequestV1Proto.internal_static_splits_transactions_wrap_TransactionRequest_descriptor;
    }

    @java.lang.Override
    public com.splits.transactions.wrap.TransactionRequest getDefaultInstanceForType() {
      return com.splits.transactions.wrap.TransactionRequest.getDefaultInstance();
    }

    @java.lang.Override
    public com.splits.transactions.wrap.TransactionRequest build() {
      com.splits.transactions.wrap.TransactionRequest result = buildPartial();
      if (!result.isInitialized()) {
        throw newUninitializedMessageException(result);
      }
      return result;
    }

    @java.lang.Override
    public com.splits.transactions.wrap.TransactionRequest buildPartial() {
      com.splits.transactions.wrap.TransactionRequest result = new com.splits.transactions.wrap.TransactionRequest(this);
      result.from_ = from_;
      result.fromID_ = fromID_;
      result.coins_ = coins_;
      onBuilt();
      return result;
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
      if (other instanceof com.splits.transactions.wrap.TransactionRequest) {
        return mergeFrom((com.splits.transactions.wrap.TransactionRequest)other);
      } else {
        super.mergeFrom(other);
        return this;
      }
    }

    public Builder mergeFrom(com.splits.transactions.wrap.TransactionRequest other) {
      if (other == com.splits.transactions.wrap.TransactionRequest.getDefaultInstance()) return this;
      if (!other.getFrom().isEmpty()) {
        from_ = other.from_;
        onChanged();
      }
      if (!other.getFromID().isEmpty()) {
        fromID_ = other.fromID_;
        onChanged();
      }
      if (!other.getCoins().isEmpty()) {
        coins_ = other.coins_;
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

              break;
            } // case 10
            case 18: {
              fromID_ = input.readStringRequireUtf8();

              break;
            } // case 18
            case 26: {
              coins_ = input.readStringRequireUtf8();

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
      if (value == null) {
    throw new NullPointerException();
  }
  
      from_ = value;
      onChanged();
      return this;
    }
    /**
     * <code>string from = 1 [json_name = "from"];</code>
     * @return This builder for chaining.
     */
    public Builder clearFrom() {
      
      from_ = getDefaultInstance().getFrom();
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
      if (value == null) {
    throw new NullPointerException();
  }
  checkByteStringIsUtf8(value);
      
      from_ = value;
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
      if (value == null) {
    throw new NullPointerException();
  }
  
      fromID_ = value;
      onChanged();
      return this;
    }
    /**
     * <code>string from_i_d = 2 [json_name = "fromID"];</code>
     * @return This builder for chaining.
     */
    public Builder clearFromID() {
      
      fromID_ = getDefaultInstance().getFromID();
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
      if (value == null) {
    throw new NullPointerException();
  }
  checkByteStringIsUtf8(value);
      
      fromID_ = value;
      onChanged();
      return this;
    }

    private java.lang.Object coins_ = "";
    /**
     * <code>string coins = 3 [json_name = "coins"];</code>
     * @return The coins.
     */
    public java.lang.String getCoins() {
      java.lang.Object ref = coins_;
      if (!(ref instanceof java.lang.String)) {
        com.google.protobuf.ByteString bs =
            (com.google.protobuf.ByteString) ref;
        java.lang.String s = bs.toStringUtf8();
        coins_ = s;
        return s;
      } else {
        return (java.lang.String) ref;
      }
    }
    /**
     * <code>string coins = 3 [json_name = "coins"];</code>
     * @return The bytes for coins.
     */
    public com.google.protobuf.ByteString
        getCoinsBytes() {
      java.lang.Object ref = coins_;
      if (ref instanceof String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8(
                (java.lang.String) ref);
        coins_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }
    /**
     * <code>string coins = 3 [json_name = "coins"];</code>
     * @param value The coins to set.
     * @return This builder for chaining.
     */
    public Builder setCoins(
        java.lang.String value) {
      if (value == null) {
    throw new NullPointerException();
  }
  
      coins_ = value;
      onChanged();
      return this;
    }
    /**
     * <code>string coins = 3 [json_name = "coins"];</code>
     * @return This builder for chaining.
     */
    public Builder clearCoins() {
      
      coins_ = getDefaultInstance().getCoins();
      onChanged();
      return this;
    }
    /**
     * <code>string coins = 3 [json_name = "coins"];</code>
     * @param value The bytes for coins to set.
     * @return This builder for chaining.
     */
    public Builder setCoinsBytes(
        com.google.protobuf.ByteString value) {
      if (value == null) {
    throw new NullPointerException();
  }
  checkByteStringIsUtf8(value);
      
      coins_ = value;
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


    // @@protoc_insertion_point(builder_scope:splits.transactions.wrap.TransactionRequest)
  }

  // @@protoc_insertion_point(class_scope:splits.transactions.wrap.TransactionRequest)
  private static final com.splits.transactions.wrap.TransactionRequest DEFAULT_INSTANCE;
  static {
    DEFAULT_INSTANCE = new com.splits.transactions.wrap.TransactionRequest();
  }

  public static com.splits.transactions.wrap.TransactionRequest getDefaultInstance() {
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
  public com.splits.transactions.wrap.TransactionRequest getDefaultInstanceForType() {
    return DEFAULT_INSTANCE;
  }

}

