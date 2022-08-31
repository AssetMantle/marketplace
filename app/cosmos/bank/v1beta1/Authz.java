// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: cosmos/bank/v1beta1/authz.proto

package cosmos.bank.v1beta1;

public final class Authz {
  private Authz() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistryLite registry) {
  }

  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
    registerAllExtensions(
        (com.google.protobuf.ExtensionRegistryLite) registry);
  }
  public interface SendAuthorizationOrBuilder extends
      // @@protoc_insertion_point(interface_extends:cosmos.bank.v1beta1.SendAuthorization)
      com.google.protobuf.MessageOrBuilder {

    /**
     * <code>repeated .cosmos.base.v1beta1.Coin spend_limit = 1 [(.gogoproto.nullable) = false, (.gogoproto.castrepeated) = "github.com/cosmos/cosmos-sdk/types.Coins"];</code>
     */
    java.util.List<cosmos.base.v1beta1.CoinOuterClass.Coin> 
        getSpendLimitList();
    /**
     * <code>repeated .cosmos.base.v1beta1.Coin spend_limit = 1 [(.gogoproto.nullable) = false, (.gogoproto.castrepeated) = "github.com/cosmos/cosmos-sdk/types.Coins"];</code>
     */
    cosmos.base.v1beta1.CoinOuterClass.Coin getSpendLimit(int index);
    /**
     * <code>repeated .cosmos.base.v1beta1.Coin spend_limit = 1 [(.gogoproto.nullable) = false, (.gogoproto.castrepeated) = "github.com/cosmos/cosmos-sdk/types.Coins"];</code>
     */
    int getSpendLimitCount();
    /**
     * <code>repeated .cosmos.base.v1beta1.Coin spend_limit = 1 [(.gogoproto.nullable) = false, (.gogoproto.castrepeated) = "github.com/cosmos/cosmos-sdk/types.Coins"];</code>
     */
    java.util.List<? extends cosmos.base.v1beta1.CoinOuterClass.CoinOrBuilder> 
        getSpendLimitOrBuilderList();
    /**
     * <code>repeated .cosmos.base.v1beta1.Coin spend_limit = 1 [(.gogoproto.nullable) = false, (.gogoproto.castrepeated) = "github.com/cosmos/cosmos-sdk/types.Coins"];</code>
     */
    cosmos.base.v1beta1.CoinOuterClass.CoinOrBuilder getSpendLimitOrBuilder(
        int index);
  }
  /**
   * <pre>
   * SendAuthorization allows the grantee to spend up to spend_limit coins from
   * the granter's account.
   * Since: cosmos-sdk 0.43
   * </pre>
   *
   * Protobuf type {@code cosmos.bank.v1beta1.SendAuthorization}
   */
  public static final class SendAuthorization extends
      com.google.protobuf.GeneratedMessageV3 implements
      // @@protoc_insertion_point(message_implements:cosmos.bank.v1beta1.SendAuthorization)
      SendAuthorizationOrBuilder {
  private static final long serialVersionUID = 0L;
    // Use SendAuthorization.newBuilder() to construct.
    private SendAuthorization(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
      super(builder);
    }
    private SendAuthorization() {
      spendLimit_ = java.util.Collections.emptyList();
    }

    @java.lang.Override
    @SuppressWarnings({"unused"})
    protected java.lang.Object newInstance(
        UnusedPrivateParameter unused) {
      return new SendAuthorization();
    }

    @java.lang.Override
    public final com.google.protobuf.UnknownFieldSet
    getUnknownFields() {
      return this.unknownFields;
    }
    private SendAuthorization(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      this();
      if (extensionRegistry == null) {
        throw new java.lang.NullPointerException();
      }
      int mutable_bitField0_ = 0;
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
              if (!((mutable_bitField0_ & 0x00000001) != 0)) {
                spendLimit_ = new java.util.ArrayList<cosmos.base.v1beta1.CoinOuterClass.Coin>();
                mutable_bitField0_ |= 0x00000001;
              }
              spendLimit_.add(
                  input.readMessage(cosmos.base.v1beta1.CoinOuterClass.Coin.parser(), extensionRegistry));
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
        if (((mutable_bitField0_ & 0x00000001) != 0)) {
          spendLimit_ = java.util.Collections.unmodifiableList(spendLimit_);
        }
        this.unknownFields = unknownFields.build();
        makeExtensionsImmutable();
      }
    }
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return cosmos.bank.v1beta1.Authz.internal_static_cosmos_bank_v1beta1_SendAuthorization_descriptor;
    }

    @java.lang.Override
    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return cosmos.bank.v1beta1.Authz.internal_static_cosmos_bank_v1beta1_SendAuthorization_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              cosmos.bank.v1beta1.Authz.SendAuthorization.class, cosmos.bank.v1beta1.Authz.SendAuthorization.Builder.class);
    }

    public static final int SPEND_LIMIT_FIELD_NUMBER = 1;
    private java.util.List<cosmos.base.v1beta1.CoinOuterClass.Coin> spendLimit_;
    /**
     * <code>repeated .cosmos.base.v1beta1.Coin spend_limit = 1 [(.gogoproto.nullable) = false, (.gogoproto.castrepeated) = "github.com/cosmos/cosmos-sdk/types.Coins"];</code>
     */
    @java.lang.Override
    public java.util.List<cosmos.base.v1beta1.CoinOuterClass.Coin> getSpendLimitList() {
      return spendLimit_;
    }
    /**
     * <code>repeated .cosmos.base.v1beta1.Coin spend_limit = 1 [(.gogoproto.nullable) = false, (.gogoproto.castrepeated) = "github.com/cosmos/cosmos-sdk/types.Coins"];</code>
     */
    @java.lang.Override
    public java.util.List<? extends cosmos.base.v1beta1.CoinOuterClass.CoinOrBuilder> 
        getSpendLimitOrBuilderList() {
      return spendLimit_;
    }
    /**
     * <code>repeated .cosmos.base.v1beta1.Coin spend_limit = 1 [(.gogoproto.nullable) = false, (.gogoproto.castrepeated) = "github.com/cosmos/cosmos-sdk/types.Coins"];</code>
     */
    @java.lang.Override
    public int getSpendLimitCount() {
      return spendLimit_.size();
    }
    /**
     * <code>repeated .cosmos.base.v1beta1.Coin spend_limit = 1 [(.gogoproto.nullable) = false, (.gogoproto.castrepeated) = "github.com/cosmos/cosmos-sdk/types.Coins"];</code>
     */
    @java.lang.Override
    public cosmos.base.v1beta1.CoinOuterClass.Coin getSpendLimit(int index) {
      return spendLimit_.get(index);
    }
    /**
     * <code>repeated .cosmos.base.v1beta1.Coin spend_limit = 1 [(.gogoproto.nullable) = false, (.gogoproto.castrepeated) = "github.com/cosmos/cosmos-sdk/types.Coins"];</code>
     */
    @java.lang.Override
    public cosmos.base.v1beta1.CoinOuterClass.CoinOrBuilder getSpendLimitOrBuilder(
        int index) {
      return spendLimit_.get(index);
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
      for (int i = 0; i < spendLimit_.size(); i++) {
        output.writeMessage(1, spendLimit_.get(i));
      }
      unknownFields.writeTo(output);
    }

    @java.lang.Override
    public int getSerializedSize() {
      int size = memoizedSize;
      if (size != -1) return size;

      size = 0;
      for (int i = 0; i < spendLimit_.size(); i++) {
        size += com.google.protobuf.CodedOutputStream
          .computeMessageSize(1, spendLimit_.get(i));
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
      if (!(obj instanceof cosmos.bank.v1beta1.Authz.SendAuthorization)) {
        return super.equals(obj);
      }
      cosmos.bank.v1beta1.Authz.SendAuthorization other = (cosmos.bank.v1beta1.Authz.SendAuthorization) obj;

      if (!getSpendLimitList()
          .equals(other.getSpendLimitList())) return false;
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
      if (getSpendLimitCount() > 0) {
        hash = (37 * hash) + SPEND_LIMIT_FIELD_NUMBER;
        hash = (53 * hash) + getSpendLimitList().hashCode();
      }
      hash = (29 * hash) + unknownFields.hashCode();
      memoizedHashCode = hash;
      return hash;
    }

    public static cosmos.bank.v1beta1.Authz.SendAuthorization parseFrom(
        java.nio.ByteBuffer data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static cosmos.bank.v1beta1.Authz.SendAuthorization parseFrom(
        java.nio.ByteBuffer data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static cosmos.bank.v1beta1.Authz.SendAuthorization parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static cosmos.bank.v1beta1.Authz.SendAuthorization parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static cosmos.bank.v1beta1.Authz.SendAuthorization parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static cosmos.bank.v1beta1.Authz.SendAuthorization parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static cosmos.bank.v1beta1.Authz.SendAuthorization parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static cosmos.bank.v1beta1.Authz.SendAuthorization parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input, extensionRegistry);
    }
    public static cosmos.bank.v1beta1.Authz.SendAuthorization parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input);
    }
    public static cosmos.bank.v1beta1.Authz.SendAuthorization parseDelimitedFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }
    public static cosmos.bank.v1beta1.Authz.SendAuthorization parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static cosmos.bank.v1beta1.Authz.SendAuthorization parseFrom(
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
    public static Builder newBuilder(cosmos.bank.v1beta1.Authz.SendAuthorization prototype) {
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
     * SendAuthorization allows the grantee to spend up to spend_limit coins from
     * the granter's account.
     * Since: cosmos-sdk 0.43
     * </pre>
     *
     * Protobuf type {@code cosmos.bank.v1beta1.SendAuthorization}
     */
    public static final class Builder extends
        com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
        // @@protoc_insertion_point(builder_implements:cosmos.bank.v1beta1.SendAuthorization)
        cosmos.bank.v1beta1.Authz.SendAuthorizationOrBuilder {
      public static final com.google.protobuf.Descriptors.Descriptor
          getDescriptor() {
        return cosmos.bank.v1beta1.Authz.internal_static_cosmos_bank_v1beta1_SendAuthorization_descriptor;
      }

      @java.lang.Override
      protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
          internalGetFieldAccessorTable() {
        return cosmos.bank.v1beta1.Authz.internal_static_cosmos_bank_v1beta1_SendAuthorization_fieldAccessorTable
            .ensureFieldAccessorsInitialized(
                cosmos.bank.v1beta1.Authz.SendAuthorization.class, cosmos.bank.v1beta1.Authz.SendAuthorization.Builder.class);
      }

      // Construct using cosmos.bank.v1beta1.Authz.SendAuthorization.newBuilder()
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
          getSpendLimitFieldBuilder();
        }
      }
      @java.lang.Override
      public Builder clear() {
        super.clear();
        if (spendLimitBuilder_ == null) {
          spendLimit_ = java.util.Collections.emptyList();
          bitField0_ = (bitField0_ & ~0x00000001);
        } else {
          spendLimitBuilder_.clear();
        }
        return this;
      }

      @java.lang.Override
      public com.google.protobuf.Descriptors.Descriptor
          getDescriptorForType() {
        return cosmos.bank.v1beta1.Authz.internal_static_cosmos_bank_v1beta1_SendAuthorization_descriptor;
      }

      @java.lang.Override
      public cosmos.bank.v1beta1.Authz.SendAuthorization getDefaultInstanceForType() {
        return cosmos.bank.v1beta1.Authz.SendAuthorization.getDefaultInstance();
      }

      @java.lang.Override
      public cosmos.bank.v1beta1.Authz.SendAuthorization build() {
        cosmos.bank.v1beta1.Authz.SendAuthorization result = buildPartial();
        if (!result.isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return result;
      }

      @java.lang.Override
      public cosmos.bank.v1beta1.Authz.SendAuthorization buildPartial() {
        cosmos.bank.v1beta1.Authz.SendAuthorization result = new cosmos.bank.v1beta1.Authz.SendAuthorization(this);
        int from_bitField0_ = bitField0_;
        if (spendLimitBuilder_ == null) {
          if (((bitField0_ & 0x00000001) != 0)) {
            spendLimit_ = java.util.Collections.unmodifiableList(spendLimit_);
            bitField0_ = (bitField0_ & ~0x00000001);
          }
          result.spendLimit_ = spendLimit_;
        } else {
          result.spendLimit_ = spendLimitBuilder_.build();
        }
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
        if (other instanceof cosmos.bank.v1beta1.Authz.SendAuthorization) {
          return mergeFrom((cosmos.bank.v1beta1.Authz.SendAuthorization)other);
        } else {
          super.mergeFrom(other);
          return this;
        }
      }

      public Builder mergeFrom(cosmos.bank.v1beta1.Authz.SendAuthorization other) {
        if (other == cosmos.bank.v1beta1.Authz.SendAuthorization.getDefaultInstance()) return this;
        if (spendLimitBuilder_ == null) {
          if (!other.spendLimit_.isEmpty()) {
            if (spendLimit_.isEmpty()) {
              spendLimit_ = other.spendLimit_;
              bitField0_ = (bitField0_ & ~0x00000001);
            } else {
              ensureSpendLimitIsMutable();
              spendLimit_.addAll(other.spendLimit_);
            }
            onChanged();
          }
        } else {
          if (!other.spendLimit_.isEmpty()) {
            if (spendLimitBuilder_.isEmpty()) {
              spendLimitBuilder_.dispose();
              spendLimitBuilder_ = null;
              spendLimit_ = other.spendLimit_;
              bitField0_ = (bitField0_ & ~0x00000001);
              spendLimitBuilder_ = 
                com.google.protobuf.GeneratedMessageV3.alwaysUseFieldBuilders ?
                   getSpendLimitFieldBuilder() : null;
            } else {
              spendLimitBuilder_.addAllMessages(other.spendLimit_);
            }
          }
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
        cosmos.bank.v1beta1.Authz.SendAuthorization parsedMessage = null;
        try {
          parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
        } catch (com.google.protobuf.InvalidProtocolBufferException e) {
          parsedMessage = (cosmos.bank.v1beta1.Authz.SendAuthorization) e.getUnfinishedMessage();
          throw e.unwrapIOException();
        } finally {
          if (parsedMessage != null) {
            mergeFrom(parsedMessage);
          }
        }
        return this;
      }
      private int bitField0_;

      private java.util.List<cosmos.base.v1beta1.CoinOuterClass.Coin> spendLimit_ =
        java.util.Collections.emptyList();
      private void ensureSpendLimitIsMutable() {
        if (!((bitField0_ & 0x00000001) != 0)) {
          spendLimit_ = new java.util.ArrayList<cosmos.base.v1beta1.CoinOuterClass.Coin>(spendLimit_);
          bitField0_ |= 0x00000001;
         }
      }

      private com.google.protobuf.RepeatedFieldBuilderV3<
          cosmos.base.v1beta1.CoinOuterClass.Coin, cosmos.base.v1beta1.CoinOuterClass.Coin.Builder, cosmos.base.v1beta1.CoinOuterClass.CoinOrBuilder> spendLimitBuilder_;

      /**
       * <code>repeated .cosmos.base.v1beta1.Coin spend_limit = 1 [(.gogoproto.nullable) = false, (.gogoproto.castrepeated) = "github.com/cosmos/cosmos-sdk/types.Coins"];</code>
       */
      public java.util.List<cosmos.base.v1beta1.CoinOuterClass.Coin> getSpendLimitList() {
        if (spendLimitBuilder_ == null) {
          return java.util.Collections.unmodifiableList(spendLimit_);
        } else {
          return spendLimitBuilder_.getMessageList();
        }
      }
      /**
       * <code>repeated .cosmos.base.v1beta1.Coin spend_limit = 1 [(.gogoproto.nullable) = false, (.gogoproto.castrepeated) = "github.com/cosmos/cosmos-sdk/types.Coins"];</code>
       */
      public int getSpendLimitCount() {
        if (spendLimitBuilder_ == null) {
          return spendLimit_.size();
        } else {
          return spendLimitBuilder_.getCount();
        }
      }
      /**
       * <code>repeated .cosmos.base.v1beta1.Coin spend_limit = 1 [(.gogoproto.nullable) = false, (.gogoproto.castrepeated) = "github.com/cosmos/cosmos-sdk/types.Coins"];</code>
       */
      public cosmos.base.v1beta1.CoinOuterClass.Coin getSpendLimit(int index) {
        if (spendLimitBuilder_ == null) {
          return spendLimit_.get(index);
        } else {
          return spendLimitBuilder_.getMessage(index);
        }
      }
      /**
       * <code>repeated .cosmos.base.v1beta1.Coin spend_limit = 1 [(.gogoproto.nullable) = false, (.gogoproto.castrepeated) = "github.com/cosmos/cosmos-sdk/types.Coins"];</code>
       */
      public Builder setSpendLimit(
          int index, cosmos.base.v1beta1.CoinOuterClass.Coin value) {
        if (spendLimitBuilder_ == null) {
          if (value == null) {
            throw new NullPointerException();
          }
          ensureSpendLimitIsMutable();
          spendLimit_.set(index, value);
          onChanged();
        } else {
          spendLimitBuilder_.setMessage(index, value);
        }
        return this;
      }
      /**
       * <code>repeated .cosmos.base.v1beta1.Coin spend_limit = 1 [(.gogoproto.nullable) = false, (.gogoproto.castrepeated) = "github.com/cosmos/cosmos-sdk/types.Coins"];</code>
       */
      public Builder setSpendLimit(
          int index, cosmos.base.v1beta1.CoinOuterClass.Coin.Builder builderForValue) {
        if (spendLimitBuilder_ == null) {
          ensureSpendLimitIsMutable();
          spendLimit_.set(index, builderForValue.build());
          onChanged();
        } else {
          spendLimitBuilder_.setMessage(index, builderForValue.build());
        }
        return this;
      }
      /**
       * <code>repeated .cosmos.base.v1beta1.Coin spend_limit = 1 [(.gogoproto.nullable) = false, (.gogoproto.castrepeated) = "github.com/cosmos/cosmos-sdk/types.Coins"];</code>
       */
      public Builder addSpendLimit(cosmos.base.v1beta1.CoinOuterClass.Coin value) {
        if (spendLimitBuilder_ == null) {
          if (value == null) {
            throw new NullPointerException();
          }
          ensureSpendLimitIsMutable();
          spendLimit_.add(value);
          onChanged();
        } else {
          spendLimitBuilder_.addMessage(value);
        }
        return this;
      }
      /**
       * <code>repeated .cosmos.base.v1beta1.Coin spend_limit = 1 [(.gogoproto.nullable) = false, (.gogoproto.castrepeated) = "github.com/cosmos/cosmos-sdk/types.Coins"];</code>
       */
      public Builder addSpendLimit(
          int index, cosmos.base.v1beta1.CoinOuterClass.Coin value) {
        if (spendLimitBuilder_ == null) {
          if (value == null) {
            throw new NullPointerException();
          }
          ensureSpendLimitIsMutable();
          spendLimit_.add(index, value);
          onChanged();
        } else {
          spendLimitBuilder_.addMessage(index, value);
        }
        return this;
      }
      /**
       * <code>repeated .cosmos.base.v1beta1.Coin spend_limit = 1 [(.gogoproto.nullable) = false, (.gogoproto.castrepeated) = "github.com/cosmos/cosmos-sdk/types.Coins"];</code>
       */
      public Builder addSpendLimit(
          cosmos.base.v1beta1.CoinOuterClass.Coin.Builder builderForValue) {
        if (spendLimitBuilder_ == null) {
          ensureSpendLimitIsMutable();
          spendLimit_.add(builderForValue.build());
          onChanged();
        } else {
          spendLimitBuilder_.addMessage(builderForValue.build());
        }
        return this;
      }
      /**
       * <code>repeated .cosmos.base.v1beta1.Coin spend_limit = 1 [(.gogoproto.nullable) = false, (.gogoproto.castrepeated) = "github.com/cosmos/cosmos-sdk/types.Coins"];</code>
       */
      public Builder addSpendLimit(
          int index, cosmos.base.v1beta1.CoinOuterClass.Coin.Builder builderForValue) {
        if (spendLimitBuilder_ == null) {
          ensureSpendLimitIsMutable();
          spendLimit_.add(index, builderForValue.build());
          onChanged();
        } else {
          spendLimitBuilder_.addMessage(index, builderForValue.build());
        }
        return this;
      }
      /**
       * <code>repeated .cosmos.base.v1beta1.Coin spend_limit = 1 [(.gogoproto.nullable) = false, (.gogoproto.castrepeated) = "github.com/cosmos/cosmos-sdk/types.Coins"];</code>
       */
      public Builder addAllSpendLimit(
          java.lang.Iterable<? extends cosmos.base.v1beta1.CoinOuterClass.Coin> values) {
        if (spendLimitBuilder_ == null) {
          ensureSpendLimitIsMutable();
          com.google.protobuf.AbstractMessageLite.Builder.addAll(
              values, spendLimit_);
          onChanged();
        } else {
          spendLimitBuilder_.addAllMessages(values);
        }
        return this;
      }
      /**
       * <code>repeated .cosmos.base.v1beta1.Coin spend_limit = 1 [(.gogoproto.nullable) = false, (.gogoproto.castrepeated) = "github.com/cosmos/cosmos-sdk/types.Coins"];</code>
       */
      public Builder clearSpendLimit() {
        if (spendLimitBuilder_ == null) {
          spendLimit_ = java.util.Collections.emptyList();
          bitField0_ = (bitField0_ & ~0x00000001);
          onChanged();
        } else {
          spendLimitBuilder_.clear();
        }
        return this;
      }
      /**
       * <code>repeated .cosmos.base.v1beta1.Coin spend_limit = 1 [(.gogoproto.nullable) = false, (.gogoproto.castrepeated) = "github.com/cosmos/cosmos-sdk/types.Coins"];</code>
       */
      public Builder removeSpendLimit(int index) {
        if (spendLimitBuilder_ == null) {
          ensureSpendLimitIsMutable();
          spendLimit_.remove(index);
          onChanged();
        } else {
          spendLimitBuilder_.remove(index);
        }
        return this;
      }
      /**
       * <code>repeated .cosmos.base.v1beta1.Coin spend_limit = 1 [(.gogoproto.nullable) = false, (.gogoproto.castrepeated) = "github.com/cosmos/cosmos-sdk/types.Coins"];</code>
       */
      public cosmos.base.v1beta1.CoinOuterClass.Coin.Builder getSpendLimitBuilder(
          int index) {
        return getSpendLimitFieldBuilder().getBuilder(index);
      }
      /**
       * <code>repeated .cosmos.base.v1beta1.Coin spend_limit = 1 [(.gogoproto.nullable) = false, (.gogoproto.castrepeated) = "github.com/cosmos/cosmos-sdk/types.Coins"];</code>
       */
      public cosmos.base.v1beta1.CoinOuterClass.CoinOrBuilder getSpendLimitOrBuilder(
          int index) {
        if (spendLimitBuilder_ == null) {
          return spendLimit_.get(index);  } else {
          return spendLimitBuilder_.getMessageOrBuilder(index);
        }
      }
      /**
       * <code>repeated .cosmos.base.v1beta1.Coin spend_limit = 1 [(.gogoproto.nullable) = false, (.gogoproto.castrepeated) = "github.com/cosmos/cosmos-sdk/types.Coins"];</code>
       */
      public java.util.List<? extends cosmos.base.v1beta1.CoinOuterClass.CoinOrBuilder> 
           getSpendLimitOrBuilderList() {
        if (spendLimitBuilder_ != null) {
          return spendLimitBuilder_.getMessageOrBuilderList();
        } else {
          return java.util.Collections.unmodifiableList(spendLimit_);
        }
      }
      /**
       * <code>repeated .cosmos.base.v1beta1.Coin spend_limit = 1 [(.gogoproto.nullable) = false, (.gogoproto.castrepeated) = "github.com/cosmos/cosmos-sdk/types.Coins"];</code>
       */
      public cosmos.base.v1beta1.CoinOuterClass.Coin.Builder addSpendLimitBuilder() {
        return getSpendLimitFieldBuilder().addBuilder(
            cosmos.base.v1beta1.CoinOuterClass.Coin.getDefaultInstance());
      }
      /**
       * <code>repeated .cosmos.base.v1beta1.Coin spend_limit = 1 [(.gogoproto.nullable) = false, (.gogoproto.castrepeated) = "github.com/cosmos/cosmos-sdk/types.Coins"];</code>
       */
      public cosmos.base.v1beta1.CoinOuterClass.Coin.Builder addSpendLimitBuilder(
          int index) {
        return getSpendLimitFieldBuilder().addBuilder(
            index, cosmos.base.v1beta1.CoinOuterClass.Coin.getDefaultInstance());
      }
      /**
       * <code>repeated .cosmos.base.v1beta1.Coin spend_limit = 1 [(.gogoproto.nullable) = false, (.gogoproto.castrepeated) = "github.com/cosmos/cosmos-sdk/types.Coins"];</code>
       */
      public java.util.List<cosmos.base.v1beta1.CoinOuterClass.Coin.Builder> 
           getSpendLimitBuilderList() {
        return getSpendLimitFieldBuilder().getBuilderList();
      }
      private com.google.protobuf.RepeatedFieldBuilderV3<
          cosmos.base.v1beta1.CoinOuterClass.Coin, cosmos.base.v1beta1.CoinOuterClass.Coin.Builder, cosmos.base.v1beta1.CoinOuterClass.CoinOrBuilder> 
          getSpendLimitFieldBuilder() {
        if (spendLimitBuilder_ == null) {
          spendLimitBuilder_ = new com.google.protobuf.RepeatedFieldBuilderV3<
              cosmos.base.v1beta1.CoinOuterClass.Coin, cosmos.base.v1beta1.CoinOuterClass.Coin.Builder, cosmos.base.v1beta1.CoinOuterClass.CoinOrBuilder>(
                  spendLimit_,
                  ((bitField0_ & 0x00000001) != 0),
                  getParentForChildren(),
                  isClean());
          spendLimit_ = null;
        }
        return spendLimitBuilder_;
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


      // @@protoc_insertion_point(builder_scope:cosmos.bank.v1beta1.SendAuthorization)
    }

    // @@protoc_insertion_point(class_scope:cosmos.bank.v1beta1.SendAuthorization)
    private static final cosmos.bank.v1beta1.Authz.SendAuthorization DEFAULT_INSTANCE;
    static {
      DEFAULT_INSTANCE = new cosmos.bank.v1beta1.Authz.SendAuthorization();
    }

    public static cosmos.bank.v1beta1.Authz.SendAuthorization getDefaultInstance() {
      return DEFAULT_INSTANCE;
    }

    private static final com.google.protobuf.Parser<SendAuthorization>
        PARSER = new com.google.protobuf.AbstractParser<SendAuthorization>() {
      @java.lang.Override
      public SendAuthorization parsePartialFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws com.google.protobuf.InvalidProtocolBufferException {
        return new SendAuthorization(input, extensionRegistry);
      }
    };

    public static com.google.protobuf.Parser<SendAuthorization> parser() {
      return PARSER;
    }

    @java.lang.Override
    public com.google.protobuf.Parser<SendAuthorization> getParserForType() {
      return PARSER;
    }

    @java.lang.Override
    public cosmos.bank.v1beta1.Authz.SendAuthorization getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
    }

  }

  private static final com.google.protobuf.Descriptors.Descriptor
    internal_static_cosmos_bank_v1beta1_SendAuthorization_descriptor;
  private static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_cosmos_bank_v1beta1_SendAuthorization_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static  com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n\037cosmos/bank/v1beta1/authz.proto\022\023cosmo" +
      "s.bank.v1beta1\032\024gogoproto/gogo.proto\032\031co" +
      "smos_proto/cosmos.proto\032\036cosmos/base/v1b" +
      "eta1/coin.proto\"\210\001\n\021SendAuthorization\022`\n" +
      "\013spend_limit\030\001 \003(\0132\031.cosmos.base.v1beta1" +
      ".CoinB0\310\336\037\000\252\337\037(github.com/cosmos/cosmos-" +
      "sdk/types.Coins:\021\322\264-\rAuthorizationB+Z)gi" +
      "thub.com/cosmos/cosmos-sdk/x/bank/typesb" +
      "\006proto3"
    };
    descriptor = com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
          com.google.protobuf.GoGoProtos.getDescriptor(),
          cosmos_proto.Cosmos.getDescriptor(),
          cosmos.base.v1beta1.CoinOuterClass.getDescriptor(),
        });
    internal_static_cosmos_bank_v1beta1_SendAuthorization_descriptor =
      getDescriptor().getMessageTypes().get(0);
    internal_static_cosmos_bank_v1beta1_SendAuthorization_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_cosmos_bank_v1beta1_SendAuthorization_descriptor,
        new java.lang.String[] { "SpendLimit", });
    com.google.protobuf.ExtensionRegistry registry =
        com.google.protobuf.ExtensionRegistry.newInstance();
    registry.add(cosmos_proto.Cosmos.implementsInterface);
    registry.add(com.google.protobuf.GoGoProtos.castrepeated);
    registry.add(com.google.protobuf.GoGoProtos.nullable);
    com.google.protobuf.Descriptors.FileDescriptor
        .internalUpdateFileDescriptor(descriptor, registry);
    com.google.protobuf.GoGoProtos.getDescriptor();
    cosmos_proto.Cosmos.getDescriptor();
    cosmos.base.v1beta1.CoinOuterClass.getDescriptor();
  }

  // @@protoc_insertion_point(outer_class_scope)
}
