// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: cosmos/base/store/v1beta1/listening.proto

package cosmos.base.store.v1beta1;

public final class Listening {
  private Listening() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistryLite registry) {
  }

  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
    registerAllExtensions(
        (com.google.protobuf.ExtensionRegistryLite) registry);
  }
  public interface StoreKVPairOrBuilder extends
      // @@protoc_insertion_point(interface_extends:cosmos.base.store.v1beta1.StoreKVPair)
      com.google.protobuf.MessageOrBuilder {

    /**
     * <pre>
     * the store key for the KVStore this pair originates from
     * </pre>
     *
     * <code>string store_key = 1;</code>
     * @return The storeKey.
     */
    java.lang.String getStoreKey();
    /**
     * <pre>
     * the store key for the KVStore this pair originates from
     * </pre>
     *
     * <code>string store_key = 1;</code>
     * @return The bytes for storeKey.
     */
    com.google.protobuf.ByteString
        getStoreKeyBytes();

    /**
     * <pre>
     * true indicates a delete operation, false indicates a set operation
     * </pre>
     *
     * <code>bool delete = 2;</code>
     * @return The delete.
     */
    boolean getDelete();

    /**
     * <code>bytes key = 3;</code>
     * @return The key.
     */
    com.google.protobuf.ByteString getKey();

    /**
     * <code>bytes value = 4;</code>
     * @return The value.
     */
    com.google.protobuf.ByteString getValue();
  }
  /**
   * <pre>
   * StoreKVPair is a KVStore KVPair used for listening to state changes (Sets and Deletes)
   * It optionally includes the StoreKey for the originating KVStore and a Boolean flag to distinguish between Sets and
   * Deletes
   * Since: cosmos-sdk 0.43
   * </pre>
   *
   * Protobuf type {@code cosmos.base.store.v1beta1.StoreKVPair}
   */
  public static final class StoreKVPair extends
      com.google.protobuf.GeneratedMessageV3 implements
      // @@protoc_insertion_point(message_implements:cosmos.base.store.v1beta1.StoreKVPair)
      StoreKVPairOrBuilder {
  private static final long serialVersionUID = 0L;
    // Use StoreKVPair.newBuilder() to construct.
    private StoreKVPair(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
      super(builder);
    }
    private StoreKVPair() {
      storeKey_ = "";
      key_ = com.google.protobuf.ByteString.EMPTY;
      value_ = com.google.protobuf.ByteString.EMPTY;
    }

    @java.lang.Override
    @SuppressWarnings({"unused"})
    protected java.lang.Object newInstance(
        UnusedPrivateParameter unused) {
      return new StoreKVPair();
    }

    @java.lang.Override
    public final com.google.protobuf.UnknownFieldSet
    getUnknownFields() {
      return this.unknownFields;
    }
    private StoreKVPair(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      this();
      if (extensionRegistry == null) {
        throw new java.lang.NullPointerException();
      }
      com.google.protobuf.UnknownFieldSet.Builder unknownFields =
          com.google.protobuf.UnknownFieldSet.newBuilder();
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

              storeKey_ = s;
              break;
            }
            case 16: {

              delete_ = input.readBool();
              break;
            }
            case 26: {

              key_ = input.readBytes();
              break;
            }
            case 34: {

              value_ = input.readBytes();
              break;
            }
            default: {
              if (!parseUnknownField(
                  input, unknownFields, extensionRegistry, tag)) {
                done = true;
              }
              break;
            }
          }
        }
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        throw e.setUnfinishedMessage(this);
      } catch (java.io.IOException e) {
        throw new com.google.protobuf.InvalidProtocolBufferException(
            e).setUnfinishedMessage(this);
      } finally {
        this.unknownFields = unknownFields.build();
        makeExtensionsImmutable();
      }
    }
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return cosmos.base.store.v1beta1.Listening.internal_static_cosmos_base_store_v1beta1_StoreKVPair_descriptor;
    }

    @java.lang.Override
    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return cosmos.base.store.v1beta1.Listening.internal_static_cosmos_base_store_v1beta1_StoreKVPair_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              cosmos.base.store.v1beta1.Listening.StoreKVPair.class, cosmos.base.store.v1beta1.Listening.StoreKVPair.Builder.class);
    }

    public static final int STORE_KEY_FIELD_NUMBER = 1;
    private volatile java.lang.Object storeKey_;
    /**
     * <pre>
     * the store key for the KVStore this pair originates from
     * </pre>
     *
     * <code>string store_key = 1;</code>
     * @return The storeKey.
     */
    @java.lang.Override
    public java.lang.String getStoreKey() {
      java.lang.Object ref = storeKey_;
      if (ref instanceof java.lang.String) {
        return (java.lang.String) ref;
      } else {
        com.google.protobuf.ByteString bs = 
            (com.google.protobuf.ByteString) ref;
        java.lang.String s = bs.toStringUtf8();
        storeKey_ = s;
        return s;
      }
    }
    /**
     * <pre>
     * the store key for the KVStore this pair originates from
     * </pre>
     *
     * <code>string store_key = 1;</code>
     * @return The bytes for storeKey.
     */
    @java.lang.Override
    public com.google.protobuf.ByteString
        getStoreKeyBytes() {
      java.lang.Object ref = storeKey_;
      if (ref instanceof java.lang.String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8(
                (java.lang.String) ref);
        storeKey_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }

    public static final int DELETE_FIELD_NUMBER = 2;
    private boolean delete_;
    /**
     * <pre>
     * true indicates a delete operation, false indicates a set operation
     * </pre>
     *
     * <code>bool delete = 2;</code>
     * @return The delete.
     */
    @java.lang.Override
    public boolean getDelete() {
      return delete_;
    }

    public static final int KEY_FIELD_NUMBER = 3;
    private com.google.protobuf.ByteString key_;
    /**
     * <code>bytes key = 3;</code>
     * @return The key.
     */
    @java.lang.Override
    public com.google.protobuf.ByteString getKey() {
      return key_;
    }

    public static final int VALUE_FIELD_NUMBER = 4;
    private com.google.protobuf.ByteString value_;
    /**
     * <code>bytes value = 4;</code>
     * @return The value.
     */
    @java.lang.Override
    public com.google.protobuf.ByteString getValue() {
      return value_;
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
      if (!com.google.protobuf.GeneratedMessageV3.isStringEmpty(storeKey_)) {
        com.google.protobuf.GeneratedMessageV3.writeString(output, 1, storeKey_);
      }
      if (delete_ != false) {
        output.writeBool(2, delete_);
      }
      if (!key_.isEmpty()) {
        output.writeBytes(3, key_);
      }
      if (!value_.isEmpty()) {
        output.writeBytes(4, value_);
      }
      unknownFields.writeTo(output);
    }

    @java.lang.Override
    public int getSerializedSize() {
      int size = memoizedSize;
      if (size != -1) return size;

      size = 0;
      if (!com.google.protobuf.GeneratedMessageV3.isStringEmpty(storeKey_)) {
        size += com.google.protobuf.GeneratedMessageV3.computeStringSize(1, storeKey_);
      }
      if (delete_ != false) {
        size += com.google.protobuf.CodedOutputStream
          .computeBoolSize(2, delete_);
      }
      if (!key_.isEmpty()) {
        size += com.google.protobuf.CodedOutputStream
          .computeBytesSize(3, key_);
      }
      if (!value_.isEmpty()) {
        size += com.google.protobuf.CodedOutputStream
          .computeBytesSize(4, value_);
      }
      size += unknownFields.getSerializedSize();
      memoizedSize = size;
      return size;
    }

    @java.lang.Override
    public boolean equals(final java.lang.Object obj) {
      if (obj == this) {
       return true;
      }
      if (!(obj instanceof cosmos.base.store.v1beta1.Listening.StoreKVPair)) {
        return super.equals(obj);
      }
      cosmos.base.store.v1beta1.Listening.StoreKVPair other = (cosmos.base.store.v1beta1.Listening.StoreKVPair) obj;

      if (!getStoreKey()
          .equals(other.getStoreKey())) return false;
      if (getDelete()
          != other.getDelete()) return false;
      if (!getKey()
          .equals(other.getKey())) return false;
      if (!getValue()
          .equals(other.getValue())) return false;
      if (!unknownFields.equals(other.unknownFields)) return false;
      return true;
    }

    @java.lang.Override
    public int hashCode() {
      if (memoizedHashCode != 0) {
        return memoizedHashCode;
      }
      int hash = 41;
      hash = (19 * hash) + getDescriptor().hashCode();
      hash = (37 * hash) + STORE_KEY_FIELD_NUMBER;
      hash = (53 * hash) + getStoreKey().hashCode();
      hash = (37 * hash) + DELETE_FIELD_NUMBER;
      hash = (53 * hash) + com.google.protobuf.Internal.hashBoolean(
          getDelete());
      hash = (37 * hash) + KEY_FIELD_NUMBER;
      hash = (53 * hash) + getKey().hashCode();
      hash = (37 * hash) + VALUE_FIELD_NUMBER;
      hash = (53 * hash) + getValue().hashCode();
      hash = (29 * hash) + unknownFields.hashCode();
      memoizedHashCode = hash;
      return hash;
    }

    public static cosmos.base.store.v1beta1.Listening.StoreKVPair parseFrom(
        java.nio.ByteBuffer data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static cosmos.base.store.v1beta1.Listening.StoreKVPair parseFrom(
        java.nio.ByteBuffer data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static cosmos.base.store.v1beta1.Listening.StoreKVPair parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static cosmos.base.store.v1beta1.Listening.StoreKVPair parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static cosmos.base.store.v1beta1.Listening.StoreKVPair parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static cosmos.base.store.v1beta1.Listening.StoreKVPair parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static cosmos.base.store.v1beta1.Listening.StoreKVPair parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static cosmos.base.store.v1beta1.Listening.StoreKVPair parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input, extensionRegistry);
    }
    public static cosmos.base.store.v1beta1.Listening.StoreKVPair parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input);
    }
    public static cosmos.base.store.v1beta1.Listening.StoreKVPair parseDelimitedFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }
    public static cosmos.base.store.v1beta1.Listening.StoreKVPair parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static cosmos.base.store.v1beta1.Listening.StoreKVPair parseFrom(
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
    public static Builder newBuilder(cosmos.base.store.v1beta1.Listening.StoreKVPair prototype) {
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
     * StoreKVPair is a KVStore KVPair used for listening to state changes (Sets and Deletes)
     * It optionally includes the StoreKey for the originating KVStore and a Boolean flag to distinguish between Sets and
     * Deletes
     * Since: cosmos-sdk 0.43
     * </pre>
     *
     * Protobuf type {@code cosmos.base.store.v1beta1.StoreKVPair}
     */
    public static final class Builder extends
        com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
        // @@protoc_insertion_point(builder_implements:cosmos.base.store.v1beta1.StoreKVPair)
        cosmos.base.store.v1beta1.Listening.StoreKVPairOrBuilder {
      public static final com.google.protobuf.Descriptors.Descriptor
          getDescriptor() {
        return cosmos.base.store.v1beta1.Listening.internal_static_cosmos_base_store_v1beta1_StoreKVPair_descriptor;
      }

      @java.lang.Override
      protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
          internalGetFieldAccessorTable() {
        return cosmos.base.store.v1beta1.Listening.internal_static_cosmos_base_store_v1beta1_StoreKVPair_fieldAccessorTable
            .ensureFieldAccessorsInitialized(
                cosmos.base.store.v1beta1.Listening.StoreKVPair.class, cosmos.base.store.v1beta1.Listening.StoreKVPair.Builder.class);
      }

      // Construct using cosmos.base.store.v1beta1.Listening.StoreKVPair.newBuilder()
      private Builder() {
        maybeForceBuilderInitialization();
      }

      private Builder(
          com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
        super(parent);
        maybeForceBuilderInitialization();
      }
      private void maybeForceBuilderInitialization() {
        if (com.google.protobuf.GeneratedMessageV3
                .alwaysUseFieldBuilders) {
        }
      }
      @java.lang.Override
      public Builder clear() {
        super.clear();
        storeKey_ = "";

        delete_ = false;

        key_ = com.google.protobuf.ByteString.EMPTY;

        value_ = com.google.protobuf.ByteString.EMPTY;

        return this;
      }

      @java.lang.Override
      public com.google.protobuf.Descriptors.Descriptor
          getDescriptorForType() {
        return cosmos.base.store.v1beta1.Listening.internal_static_cosmos_base_store_v1beta1_StoreKVPair_descriptor;
      }

      @java.lang.Override
      public cosmos.base.store.v1beta1.Listening.StoreKVPair getDefaultInstanceForType() {
        return cosmos.base.store.v1beta1.Listening.StoreKVPair.getDefaultInstance();
      }

      @java.lang.Override
      public cosmos.base.store.v1beta1.Listening.StoreKVPair build() {
        cosmos.base.store.v1beta1.Listening.StoreKVPair result = buildPartial();
        if (!result.isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return result;
      }

      @java.lang.Override
      public cosmos.base.store.v1beta1.Listening.StoreKVPair buildPartial() {
        cosmos.base.store.v1beta1.Listening.StoreKVPair result = new cosmos.base.store.v1beta1.Listening.StoreKVPair(this);
        result.storeKey_ = storeKey_;
        result.delete_ = delete_;
        result.key_ = key_;
        result.value_ = value_;
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
        if (other instanceof cosmos.base.store.v1beta1.Listening.StoreKVPair) {
          return mergeFrom((cosmos.base.store.v1beta1.Listening.StoreKVPair)other);
        } else {
          super.mergeFrom(other);
          return this;
        }
      }

      public Builder mergeFrom(cosmos.base.store.v1beta1.Listening.StoreKVPair other) {
        if (other == cosmos.base.store.v1beta1.Listening.StoreKVPair.getDefaultInstance()) return this;
        if (!other.getStoreKey().isEmpty()) {
          storeKey_ = other.storeKey_;
          onChanged();
        }
        if (other.getDelete() != false) {
          setDelete(other.getDelete());
        }
        if (other.getKey() != com.google.protobuf.ByteString.EMPTY) {
          setKey(other.getKey());
        }
        if (other.getValue() != com.google.protobuf.ByteString.EMPTY) {
          setValue(other.getValue());
        }
        this.mergeUnknownFields(other.unknownFields);
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
        cosmos.base.store.v1beta1.Listening.StoreKVPair parsedMessage = null;
        try {
          parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
        } catch (com.google.protobuf.InvalidProtocolBufferException e) {
          parsedMessage = (cosmos.base.store.v1beta1.Listening.StoreKVPair) e.getUnfinishedMessage();
          throw e.unwrapIOException();
        } finally {
          if (parsedMessage != null) {
            mergeFrom(parsedMessage);
          }
        }
        return this;
      }

      private java.lang.Object storeKey_ = "";
      /**
       * <pre>
       * the store key for the KVStore this pair originates from
       * </pre>
       *
       * <code>string store_key = 1;</code>
       * @return The storeKey.
       */
      public java.lang.String getStoreKey() {
        java.lang.Object ref = storeKey_;
        if (!(ref instanceof java.lang.String)) {
          com.google.protobuf.ByteString bs =
              (com.google.protobuf.ByteString) ref;
          java.lang.String s = bs.toStringUtf8();
          storeKey_ = s;
          return s;
        } else {
          return (java.lang.String) ref;
        }
      }
      /**
       * <pre>
       * the store key for the KVStore this pair originates from
       * </pre>
       *
       * <code>string store_key = 1;</code>
       * @return The bytes for storeKey.
       */
      public com.google.protobuf.ByteString
          getStoreKeyBytes() {
        java.lang.Object ref = storeKey_;
        if (ref instanceof String) {
          com.google.protobuf.ByteString b = 
              com.google.protobuf.ByteString.copyFromUtf8(
                  (java.lang.String) ref);
          storeKey_ = b;
          return b;
        } else {
          return (com.google.protobuf.ByteString) ref;
        }
      }
      /**
       * <pre>
       * the store key for the KVStore this pair originates from
       * </pre>
       *
       * <code>string store_key = 1;</code>
       * @param value The storeKey to set.
       * @return This builder for chaining.
       */
      public Builder setStoreKey(
          java.lang.String value) {
        if (value == null) {
    throw new NullPointerException();
  }
  
        storeKey_ = value;
        onChanged();
        return this;
      }
      /**
       * <pre>
       * the store key for the KVStore this pair originates from
       * </pre>
       *
       * <code>string store_key = 1;</code>
       * @return This builder for chaining.
       */
      public Builder clearStoreKey() {
        
        storeKey_ = getDefaultInstance().getStoreKey();
        onChanged();
        return this;
      }
      /**
       * <pre>
       * the store key for the KVStore this pair originates from
       * </pre>
       *
       * <code>string store_key = 1;</code>
       * @param value The bytes for storeKey to set.
       * @return This builder for chaining.
       */
      public Builder setStoreKeyBytes(
          com.google.protobuf.ByteString value) {
        if (value == null) {
    throw new NullPointerException();
  }
  checkByteStringIsUtf8(value);
        
        storeKey_ = value;
        onChanged();
        return this;
      }

      private boolean delete_ ;
      /**
       * <pre>
       * true indicates a delete operation, false indicates a set operation
       * </pre>
       *
       * <code>bool delete = 2;</code>
       * @return The delete.
       */
      @java.lang.Override
      public boolean getDelete() {
        return delete_;
      }
      /**
       * <pre>
       * true indicates a delete operation, false indicates a set operation
       * </pre>
       *
       * <code>bool delete = 2;</code>
       * @param value The delete to set.
       * @return This builder for chaining.
       */
      public Builder setDelete(boolean value) {
        
        delete_ = value;
        onChanged();
        return this;
      }
      /**
       * <pre>
       * true indicates a delete operation, false indicates a set operation
       * </pre>
       *
       * <code>bool delete = 2;</code>
       * @return This builder for chaining.
       */
      public Builder clearDelete() {
        
        delete_ = false;
        onChanged();
        return this;
      }

      private com.google.protobuf.ByteString key_ = com.google.protobuf.ByteString.EMPTY;
      /**
       * <code>bytes key = 3;</code>
       * @return The key.
       */
      @java.lang.Override
      public com.google.protobuf.ByteString getKey() {
        return key_;
      }
      /**
       * <code>bytes key = 3;</code>
       * @param value The key to set.
       * @return This builder for chaining.
       */
      public Builder setKey(com.google.protobuf.ByteString value) {
        if (value == null) {
    throw new NullPointerException();
  }
  
        key_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>bytes key = 3;</code>
       * @return This builder for chaining.
       */
      public Builder clearKey() {
        
        key_ = getDefaultInstance().getKey();
        onChanged();
        return this;
      }

      private com.google.protobuf.ByteString value_ = com.google.protobuf.ByteString.EMPTY;
      /**
       * <code>bytes value = 4;</code>
       * @return The value.
       */
      @java.lang.Override
      public com.google.protobuf.ByteString getValue() {
        return value_;
      }
      /**
       * <code>bytes value = 4;</code>
       * @param value The value to set.
       * @return This builder for chaining.
       */
      public Builder setValue(com.google.protobuf.ByteString value) {
        if (value == null) {
    throw new NullPointerException();
  }
  
        value_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>bytes value = 4;</code>
       * @return This builder for chaining.
       */
      public Builder clearValue() {
        
        value_ = getDefaultInstance().getValue();
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


      // @@protoc_insertion_point(builder_scope:cosmos.base.store.v1beta1.StoreKVPair)
    }

    // @@protoc_insertion_point(class_scope:cosmos.base.store.v1beta1.StoreKVPair)
    private static final cosmos.base.store.v1beta1.Listening.StoreKVPair DEFAULT_INSTANCE;
    static {
      DEFAULT_INSTANCE = new cosmos.base.store.v1beta1.Listening.StoreKVPair();
    }

    public static cosmos.base.store.v1beta1.Listening.StoreKVPair getDefaultInstance() {
      return DEFAULT_INSTANCE;
    }

    private static final com.google.protobuf.Parser<StoreKVPair>
        PARSER = new com.google.protobuf.AbstractParser<StoreKVPair>() {
      @java.lang.Override
      public StoreKVPair parsePartialFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws com.google.protobuf.InvalidProtocolBufferException {
        return new StoreKVPair(input, extensionRegistry);
      }
    };

    public static com.google.protobuf.Parser<StoreKVPair> parser() {
      return PARSER;
    }

    @java.lang.Override
    public com.google.protobuf.Parser<StoreKVPair> getParserForType() {
      return PARSER;
    }

    @java.lang.Override
    public cosmos.base.store.v1beta1.Listening.StoreKVPair getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
    }

  }

  private static final com.google.protobuf.Descriptors.Descriptor
    internal_static_cosmos_base_store_v1beta1_StoreKVPair_descriptor;
  private static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_cosmos_base_store_v1beta1_StoreKVPair_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static  com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n)cosmos/base/store/v1beta1/listening.pr" +
      "oto\022\031cosmos.base.store.v1beta1\"L\n\013StoreK" +
      "VPair\022\021\n\tstore_key\030\001 \001(\t\022\016\n\006delete\030\002 \001(\010" +
      "\022\013\n\003key\030\003 \001(\014\022\r\n\005value\030\004 \001(\014B*Z(github.c" +
      "om/cosmos/cosmos-sdk/store/typesb\006proto3"
    };
    descriptor = com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
        });
    internal_static_cosmos_base_store_v1beta1_StoreKVPair_descriptor =
      getDescriptor().getMessageTypes().get(0);
    internal_static_cosmos_base_store_v1beta1_StoreKVPair_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_cosmos_base_store_v1beta1_StoreKVPair_descriptor,
        new java.lang.String[] { "StoreKey", "Delete", "Key", "Value", });
  }

  // @@protoc_insertion_point(outer_class_scope)
}
