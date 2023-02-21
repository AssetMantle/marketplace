// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: schema/ids/base/propertyID.v1.proto

package com.ids;

/**
 * Protobuf type {@code ids.PropertyID}
 */
public final class PropertyID extends
    com.google.protobuf.GeneratedMessageV3 implements
    // @@protoc_insertion_point(message_implements:ids.PropertyID)
    PropertyIDOrBuilder {
private static final long serialVersionUID = 0L;
  // Use PropertyID.newBuilder() to construct.
  private PropertyID(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
    super(builder);
  }
  private PropertyID() {
  }

  @java.lang.Override
  @SuppressWarnings({"unused"})
  protected java.lang.Object newInstance(
      UnusedPrivateParameter unused) {
    return new PropertyID();
  }

  public static final com.google.protobuf.Descriptors.Descriptor
      getDescriptor() {
    return com.ids.PropertyIDV1Proto.internal_static_ids_PropertyID_descriptor;
  }

  @java.lang.Override
  protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internalGetFieldAccessorTable() {
    return com.ids.PropertyIDV1Proto.internal_static_ids_PropertyID_fieldAccessorTable
        .ensureFieldAccessorsInitialized(
            com.ids.PropertyID.class, com.ids.PropertyID.Builder.class);
  }

  public static final int KEY_I_D_FIELD_NUMBER = 1;
  private com.ids.StringID keyID_;
  /**
   * <code>.ids.StringID key_i_d = 1 [json_name = "keyID"];</code>
   * @return Whether the keyID field is set.
   */
  @java.lang.Override
  public boolean hasKeyID() {
    return keyID_ != null;
  }
  /**
   * <code>.ids.StringID key_i_d = 1 [json_name = "keyID"];</code>
   * @return The keyID.
   */
  @java.lang.Override
  public com.ids.StringID getKeyID() {
    return keyID_ == null ? com.ids.StringID.getDefaultInstance() : keyID_;
  }
  /**
   * <code>.ids.StringID key_i_d = 1 [json_name = "keyID"];</code>
   */
  @java.lang.Override
  public com.ids.StringIDOrBuilder getKeyIDOrBuilder() {
    return keyID_ == null ? com.ids.StringID.getDefaultInstance() : keyID_;
  }

  public static final int TYPE_I_D_FIELD_NUMBER = 2;
  private com.ids.StringID typeID_;
  /**
   * <code>.ids.StringID type_i_d = 2 [json_name = "typeID"];</code>
   * @return Whether the typeID field is set.
   */
  @java.lang.Override
  public boolean hasTypeID() {
    return typeID_ != null;
  }
  /**
   * <code>.ids.StringID type_i_d = 2 [json_name = "typeID"];</code>
   * @return The typeID.
   */
  @java.lang.Override
  public com.ids.StringID getTypeID() {
    return typeID_ == null ? com.ids.StringID.getDefaultInstance() : typeID_;
  }
  /**
   * <code>.ids.StringID type_i_d = 2 [json_name = "typeID"];</code>
   */
  @java.lang.Override
  public com.ids.StringIDOrBuilder getTypeIDOrBuilder() {
    return typeID_ == null ? com.ids.StringID.getDefaultInstance() : typeID_;
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
    if (keyID_ != null) {
      output.writeMessage(1, getKeyID());
    }
    if (typeID_ != null) {
      output.writeMessage(2, getTypeID());
    }
    getUnknownFields().writeTo(output);
  }

  @java.lang.Override
  public int getSerializedSize() {
    int size = memoizedSize;
    if (size != -1) return size;

    size = 0;
    if (keyID_ != null) {
      size += com.google.protobuf.CodedOutputStream
        .computeMessageSize(1, getKeyID());
    }
    if (typeID_ != null) {
      size += com.google.protobuf.CodedOutputStream
        .computeMessageSize(2, getTypeID());
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
    if (!(obj instanceof com.ids.PropertyID)) {
      return super.equals(obj);
    }
    com.ids.PropertyID other = (com.ids.PropertyID) obj;

    if (hasKeyID() != other.hasKeyID()) return false;
    if (hasKeyID()) {
      if (!getKeyID()
          .equals(other.getKeyID())) return false;
    }
    if (hasTypeID() != other.hasTypeID()) return false;
    if (hasTypeID()) {
      if (!getTypeID()
          .equals(other.getTypeID())) return false;
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
    if (hasKeyID()) {
      hash = (37 * hash) + KEY_I_D_FIELD_NUMBER;
      hash = (53 * hash) + getKeyID().hashCode();
    }
    if (hasTypeID()) {
      hash = (37 * hash) + TYPE_I_D_FIELD_NUMBER;
      hash = (53 * hash) + getTypeID().hashCode();
    }
    hash = (29 * hash) + getUnknownFields().hashCode();
    memoizedHashCode = hash;
    return hash;
  }

  public static com.ids.PropertyID parseFrom(
      java.nio.ByteBuffer data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static com.ids.PropertyID parseFrom(
      java.nio.ByteBuffer data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static com.ids.PropertyID parseFrom(
      com.google.protobuf.ByteString data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static com.ids.PropertyID parseFrom(
      com.google.protobuf.ByteString data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static com.ids.PropertyID parseFrom(byte[] data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static com.ids.PropertyID parseFrom(
      byte[] data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static com.ids.PropertyID parseFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static com.ids.PropertyID parseFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input, extensionRegistry);
  }
  public static com.ids.PropertyID parseDelimitedFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input);
  }
  public static com.ids.PropertyID parseDelimitedFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
  }
  public static com.ids.PropertyID parseFrom(
      com.google.protobuf.CodedInputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static com.ids.PropertyID parseFrom(
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
  public static Builder newBuilder(com.ids.PropertyID prototype) {
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
   * Protobuf type {@code ids.PropertyID}
   */
  public static final class Builder extends
      com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
      // @@protoc_insertion_point(builder_implements:ids.PropertyID)
      com.ids.PropertyIDOrBuilder {
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return com.ids.PropertyIDV1Proto.internal_static_ids_PropertyID_descriptor;
    }

    @java.lang.Override
    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return com.ids.PropertyIDV1Proto.internal_static_ids_PropertyID_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              com.ids.PropertyID.class, com.ids.PropertyID.Builder.class);
    }

    // Construct using com.ids.PropertyID.newBuilder()
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
      keyID_ = null;
      if (keyIDBuilder_ != null) {
        keyIDBuilder_.dispose();
        keyIDBuilder_ = null;
      }
      typeID_ = null;
      if (typeIDBuilder_ != null) {
        typeIDBuilder_.dispose();
        typeIDBuilder_ = null;
      }
      return this;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.Descriptor
        getDescriptorForType() {
      return com.ids.PropertyIDV1Proto.internal_static_ids_PropertyID_descriptor;
    }

    @java.lang.Override
    public com.ids.PropertyID getDefaultInstanceForType() {
      return com.ids.PropertyID.getDefaultInstance();
    }

    @java.lang.Override
    public com.ids.PropertyID build() {
      com.ids.PropertyID result = buildPartial();
      if (!result.isInitialized()) {
        throw newUninitializedMessageException(result);
      }
      return result;
    }

    @java.lang.Override
    public com.ids.PropertyID buildPartial() {
      com.ids.PropertyID result = new com.ids.PropertyID(this);
      if (bitField0_ != 0) { buildPartial0(result); }
      onBuilt();
      return result;
    }

    private void buildPartial0(com.ids.PropertyID result) {
      int from_bitField0_ = bitField0_;
      if (((from_bitField0_ & 0x00000001) != 0)) {
        result.keyID_ = keyIDBuilder_ == null
            ? keyID_
            : keyIDBuilder_.build();
      }
      if (((from_bitField0_ & 0x00000002) != 0)) {
        result.typeID_ = typeIDBuilder_ == null
            ? typeID_
            : typeIDBuilder_.build();
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
      if (other instanceof com.ids.PropertyID) {
        return mergeFrom((com.ids.PropertyID)other);
      } else {
        super.mergeFrom(other);
        return this;
      }
    }

    public Builder mergeFrom(com.ids.PropertyID other) {
      if (other == com.ids.PropertyID.getDefaultInstance()) return this;
      if (other.hasKeyID()) {
        mergeKeyID(other.getKeyID());
      }
      if (other.hasTypeID()) {
        mergeTypeID(other.getTypeID());
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
                  getKeyIDFieldBuilder().getBuilder(),
                  extensionRegistry);
              bitField0_ |= 0x00000001;
              break;
            } // case 10
            case 18: {
              input.readMessage(
                  getTypeIDFieldBuilder().getBuilder(),
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

    private com.ids.StringID keyID_;
    private com.google.protobuf.SingleFieldBuilderV3<
        com.ids.StringID, com.ids.StringID.Builder, com.ids.StringIDOrBuilder> keyIDBuilder_;
    /**
     * <code>.ids.StringID key_i_d = 1 [json_name = "keyID"];</code>
     * @return Whether the keyID field is set.
     */
    public boolean hasKeyID() {
      return ((bitField0_ & 0x00000001) != 0);
    }
    /**
     * <code>.ids.StringID key_i_d = 1 [json_name = "keyID"];</code>
     * @return The keyID.
     */
    public com.ids.StringID getKeyID() {
      if (keyIDBuilder_ == null) {
        return keyID_ == null ? com.ids.StringID.getDefaultInstance() : keyID_;
      } else {
        return keyIDBuilder_.getMessage();
      }
    }
    /**
     * <code>.ids.StringID key_i_d = 1 [json_name = "keyID"];</code>
     */
    public Builder setKeyID(com.ids.StringID value) {
      if (keyIDBuilder_ == null) {
        if (value == null) {
          throw new NullPointerException();
        }
        keyID_ = value;
      } else {
        keyIDBuilder_.setMessage(value);
      }
      bitField0_ |= 0x00000001;
      onChanged();
      return this;
    }
    /**
     * <code>.ids.StringID key_i_d = 1 [json_name = "keyID"];</code>
     */
    public Builder setKeyID(
        com.ids.StringID.Builder builderForValue) {
      if (keyIDBuilder_ == null) {
        keyID_ = builderForValue.build();
      } else {
        keyIDBuilder_.setMessage(builderForValue.build());
      }
      bitField0_ |= 0x00000001;
      onChanged();
      return this;
    }
    /**
     * <code>.ids.StringID key_i_d = 1 [json_name = "keyID"];</code>
     */
    public Builder mergeKeyID(com.ids.StringID value) {
      if (keyIDBuilder_ == null) {
        if (((bitField0_ & 0x00000001) != 0) &&
          keyID_ != null &&
          keyID_ != com.ids.StringID.getDefaultInstance()) {
          getKeyIDBuilder().mergeFrom(value);
        } else {
          keyID_ = value;
        }
      } else {
        keyIDBuilder_.mergeFrom(value);
      }
      bitField0_ |= 0x00000001;
      onChanged();
      return this;
    }
    /**
     * <code>.ids.StringID key_i_d = 1 [json_name = "keyID"];</code>
     */
    public Builder clearKeyID() {
      bitField0_ = (bitField0_ & ~0x00000001);
      keyID_ = null;
      if (keyIDBuilder_ != null) {
        keyIDBuilder_.dispose();
        keyIDBuilder_ = null;
      }
      onChanged();
      return this;
    }
    /**
     * <code>.ids.StringID key_i_d = 1 [json_name = "keyID"];</code>
     */
    public com.ids.StringID.Builder getKeyIDBuilder() {
      bitField0_ |= 0x00000001;
      onChanged();
      return getKeyIDFieldBuilder().getBuilder();
    }
    /**
     * <code>.ids.StringID key_i_d = 1 [json_name = "keyID"];</code>
     */
    public com.ids.StringIDOrBuilder getKeyIDOrBuilder() {
      if (keyIDBuilder_ != null) {
        return keyIDBuilder_.getMessageOrBuilder();
      } else {
        return keyID_ == null ?
            com.ids.StringID.getDefaultInstance() : keyID_;
      }
    }
    /**
     * <code>.ids.StringID key_i_d = 1 [json_name = "keyID"];</code>
     */
    private com.google.protobuf.SingleFieldBuilderV3<
        com.ids.StringID, com.ids.StringID.Builder, com.ids.StringIDOrBuilder> 
        getKeyIDFieldBuilder() {
      if (keyIDBuilder_ == null) {
        keyIDBuilder_ = new com.google.protobuf.SingleFieldBuilderV3<
            com.ids.StringID, com.ids.StringID.Builder, com.ids.StringIDOrBuilder>(
                getKeyID(),
                getParentForChildren(),
                isClean());
        keyID_ = null;
      }
      return keyIDBuilder_;
    }

    private com.ids.StringID typeID_;
    private com.google.protobuf.SingleFieldBuilderV3<
        com.ids.StringID, com.ids.StringID.Builder, com.ids.StringIDOrBuilder> typeIDBuilder_;
    /**
     * <code>.ids.StringID type_i_d = 2 [json_name = "typeID"];</code>
     * @return Whether the typeID field is set.
     */
    public boolean hasTypeID() {
      return ((bitField0_ & 0x00000002) != 0);
    }
    /**
     * <code>.ids.StringID type_i_d = 2 [json_name = "typeID"];</code>
     * @return The typeID.
     */
    public com.ids.StringID getTypeID() {
      if (typeIDBuilder_ == null) {
        return typeID_ == null ? com.ids.StringID.getDefaultInstance() : typeID_;
      } else {
        return typeIDBuilder_.getMessage();
      }
    }
    /**
     * <code>.ids.StringID type_i_d = 2 [json_name = "typeID"];</code>
     */
    public Builder setTypeID(com.ids.StringID value) {
      if (typeIDBuilder_ == null) {
        if (value == null) {
          throw new NullPointerException();
        }
        typeID_ = value;
      } else {
        typeIDBuilder_.setMessage(value);
      }
      bitField0_ |= 0x00000002;
      onChanged();
      return this;
    }
    /**
     * <code>.ids.StringID type_i_d = 2 [json_name = "typeID"];</code>
     */
    public Builder setTypeID(
        com.ids.StringID.Builder builderForValue) {
      if (typeIDBuilder_ == null) {
        typeID_ = builderForValue.build();
      } else {
        typeIDBuilder_.setMessage(builderForValue.build());
      }
      bitField0_ |= 0x00000002;
      onChanged();
      return this;
    }
    /**
     * <code>.ids.StringID type_i_d = 2 [json_name = "typeID"];</code>
     */
    public Builder mergeTypeID(com.ids.StringID value) {
      if (typeIDBuilder_ == null) {
        if (((bitField0_ & 0x00000002) != 0) &&
          typeID_ != null &&
          typeID_ != com.ids.StringID.getDefaultInstance()) {
          getTypeIDBuilder().mergeFrom(value);
        } else {
          typeID_ = value;
        }
      } else {
        typeIDBuilder_.mergeFrom(value);
      }
      bitField0_ |= 0x00000002;
      onChanged();
      return this;
    }
    /**
     * <code>.ids.StringID type_i_d = 2 [json_name = "typeID"];</code>
     */
    public Builder clearTypeID() {
      bitField0_ = (bitField0_ & ~0x00000002);
      typeID_ = null;
      if (typeIDBuilder_ != null) {
        typeIDBuilder_.dispose();
        typeIDBuilder_ = null;
      }
      onChanged();
      return this;
    }
    /**
     * <code>.ids.StringID type_i_d = 2 [json_name = "typeID"];</code>
     */
    public com.ids.StringID.Builder getTypeIDBuilder() {
      bitField0_ |= 0x00000002;
      onChanged();
      return getTypeIDFieldBuilder().getBuilder();
    }
    /**
     * <code>.ids.StringID type_i_d = 2 [json_name = "typeID"];</code>
     */
    public com.ids.StringIDOrBuilder getTypeIDOrBuilder() {
      if (typeIDBuilder_ != null) {
        return typeIDBuilder_.getMessageOrBuilder();
      } else {
        return typeID_ == null ?
            com.ids.StringID.getDefaultInstance() : typeID_;
      }
    }
    /**
     * <code>.ids.StringID type_i_d = 2 [json_name = "typeID"];</code>
     */
    private com.google.protobuf.SingleFieldBuilderV3<
        com.ids.StringID, com.ids.StringID.Builder, com.ids.StringIDOrBuilder> 
        getTypeIDFieldBuilder() {
      if (typeIDBuilder_ == null) {
        typeIDBuilder_ = new com.google.protobuf.SingleFieldBuilderV3<
            com.ids.StringID, com.ids.StringID.Builder, com.ids.StringIDOrBuilder>(
                getTypeID(),
                getParentForChildren(),
                isClean());
        typeID_ = null;
      }
      return typeIDBuilder_;
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


    // @@protoc_insertion_point(builder_scope:ids.PropertyID)
  }

  // @@protoc_insertion_point(class_scope:ids.PropertyID)
  private static final com.ids.PropertyID DEFAULT_INSTANCE;
  static {
    DEFAULT_INSTANCE = new com.ids.PropertyID();
  }

  public static com.ids.PropertyID getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }

  private static final com.google.protobuf.Parser<PropertyID>
      PARSER = new com.google.protobuf.AbstractParser<PropertyID>() {
    @java.lang.Override
    public PropertyID parsePartialFrom(
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

  public static com.google.protobuf.Parser<PropertyID> parser() {
    return PARSER;
  }

  @java.lang.Override
  public com.google.protobuf.Parser<PropertyID> getParserForType() {
    return PARSER;
  }

  @java.lang.Override
  public com.ids.PropertyID getDefaultInstanceForType() {
    return DEFAULT_INSTANCE;
  }

}

