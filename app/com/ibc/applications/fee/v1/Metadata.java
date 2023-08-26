// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: ibc/applications/fee/v1/metadata.proto

package com.ibc.applications.fee.v1;

/**
 * <pre>
 * Metadata defines the ICS29 channel specific metadata encoded into the channel version bytestring
 * See ICS004: https://github.com/cosmos/ibc/tree/master/spec/core/ics-004-channel-and-packet-semantics#Versioning
 * </pre>
 *
 * Protobuf type {@code ibc.applications.fee.v1.Metadata}
 */
public final class Metadata extends
    com.google.protobuf.GeneratedMessageV3 implements
    // @@protoc_insertion_point(message_implements:ibc.applications.fee.v1.Metadata)
    MetadataOrBuilder {
private static final long serialVersionUID = 0L;
  // Use Metadata.newBuilder() to construct.
  private Metadata(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
    super(builder);
  }
  private Metadata() {
    feeVersion_ = "";
    appVersion_ = "";
  }

  @java.lang.Override
  @SuppressWarnings({"unused"})
  protected java.lang.Object newInstance(
      UnusedPrivateParameter unused) {
    return new Metadata();
  }

  public static final com.google.protobuf.Descriptors.Descriptor
      getDescriptor() {
    return com.ibc.applications.fee.v1.MetadataProto.internal_static_ibc_applications_fee_v1_Metadata_descriptor;
  }

  @java.lang.Override
  protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internalGetFieldAccessorTable() {
    return com.ibc.applications.fee.v1.MetadataProto.internal_static_ibc_applications_fee_v1_Metadata_fieldAccessorTable
        .ensureFieldAccessorsInitialized(
            com.ibc.applications.fee.v1.Metadata.class, com.ibc.applications.fee.v1.Metadata.Builder.class);
  }

  public static final int FEE_VERSION_FIELD_NUMBER = 1;
  @SuppressWarnings("serial")
  private volatile java.lang.Object feeVersion_ = "";
  /**
   * <pre>
   * fee_version defines the ICS29 fee version
   * </pre>
   *
   * <code>string fee_version = 1 [json_name = "feeVersion", (.gogoproto.moretags) = "yaml:&#92;"fee_version&#92;""];</code>
   * @return The feeVersion.
   */
  @java.lang.Override
  public java.lang.String getFeeVersion() {
    java.lang.Object ref = feeVersion_;
    if (ref instanceof java.lang.String) {
      return (java.lang.String) ref;
    } else {
      com.google.protobuf.ByteString bs = 
          (com.google.protobuf.ByteString) ref;
      java.lang.String s = bs.toStringUtf8();
      feeVersion_ = s;
      return s;
    }
  }
  /**
   * <pre>
   * fee_version defines the ICS29 fee version
   * </pre>
   *
   * <code>string fee_version = 1 [json_name = "feeVersion", (.gogoproto.moretags) = "yaml:&#92;"fee_version&#92;""];</code>
   * @return The bytes for feeVersion.
   */
  @java.lang.Override
  public com.google.protobuf.ByteString
      getFeeVersionBytes() {
    java.lang.Object ref = feeVersion_;
    if (ref instanceof java.lang.String) {
      com.google.protobuf.ByteString b = 
          com.google.protobuf.ByteString.copyFromUtf8(
              (java.lang.String) ref);
      feeVersion_ = b;
      return b;
    } else {
      return (com.google.protobuf.ByteString) ref;
    }
  }

  public static final int APP_VERSION_FIELD_NUMBER = 2;
  @SuppressWarnings("serial")
  private volatile java.lang.Object appVersion_ = "";
  /**
   * <pre>
   * app_version defines the underlying application version, which may or may not be a JSON encoded bytestring
   * </pre>
   *
   * <code>string app_version = 2 [json_name = "appVersion", (.gogoproto.moretags) = "yaml:&#92;"app_version&#92;""];</code>
   * @return The appVersion.
   */
  @java.lang.Override
  public java.lang.String getAppVersion() {
    java.lang.Object ref = appVersion_;
    if (ref instanceof java.lang.String) {
      return (java.lang.String) ref;
    } else {
      com.google.protobuf.ByteString bs = 
          (com.google.protobuf.ByteString) ref;
      java.lang.String s = bs.toStringUtf8();
      appVersion_ = s;
      return s;
    }
  }
  /**
   * <pre>
   * app_version defines the underlying application version, which may or may not be a JSON encoded bytestring
   * </pre>
   *
   * <code>string app_version = 2 [json_name = "appVersion", (.gogoproto.moretags) = "yaml:&#92;"app_version&#92;""];</code>
   * @return The bytes for appVersion.
   */
  @java.lang.Override
  public com.google.protobuf.ByteString
      getAppVersionBytes() {
    java.lang.Object ref = appVersion_;
    if (ref instanceof java.lang.String) {
      com.google.protobuf.ByteString b = 
          com.google.protobuf.ByteString.copyFromUtf8(
              (java.lang.String) ref);
      appVersion_ = b;
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
    if (!com.google.protobuf.GeneratedMessageV3.isStringEmpty(feeVersion_)) {
      com.google.protobuf.GeneratedMessageV3.writeString(output, 1, feeVersion_);
    }
    if (!com.google.protobuf.GeneratedMessageV3.isStringEmpty(appVersion_)) {
      com.google.protobuf.GeneratedMessageV3.writeString(output, 2, appVersion_);
    }
    getUnknownFields().writeTo(output);
  }

  @java.lang.Override
  public int getSerializedSize() {
    int size = memoizedSize;
    if (size != -1) return size;

    size = 0;
    if (!com.google.protobuf.GeneratedMessageV3.isStringEmpty(feeVersion_)) {
      size += com.google.protobuf.GeneratedMessageV3.computeStringSize(1, feeVersion_);
    }
    if (!com.google.protobuf.GeneratedMessageV3.isStringEmpty(appVersion_)) {
      size += com.google.protobuf.GeneratedMessageV3.computeStringSize(2, appVersion_);
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
    if (!(obj instanceof com.ibc.applications.fee.v1.Metadata)) {
      return super.equals(obj);
    }
    com.ibc.applications.fee.v1.Metadata other = (com.ibc.applications.fee.v1.Metadata) obj;

    if (!getFeeVersion()
        .equals(other.getFeeVersion())) return false;
    if (!getAppVersion()
        .equals(other.getAppVersion())) return false;
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
    hash = (37 * hash) + FEE_VERSION_FIELD_NUMBER;
    hash = (53 * hash) + getFeeVersion().hashCode();
    hash = (37 * hash) + APP_VERSION_FIELD_NUMBER;
    hash = (53 * hash) + getAppVersion().hashCode();
    hash = (29 * hash) + getUnknownFields().hashCode();
    memoizedHashCode = hash;
    return hash;
  }

  public static com.ibc.applications.fee.v1.Metadata parseFrom(
      java.nio.ByteBuffer data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static com.ibc.applications.fee.v1.Metadata parseFrom(
      java.nio.ByteBuffer data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static com.ibc.applications.fee.v1.Metadata parseFrom(
      com.google.protobuf.ByteString data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static com.ibc.applications.fee.v1.Metadata parseFrom(
      com.google.protobuf.ByteString data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static com.ibc.applications.fee.v1.Metadata parseFrom(byte[] data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static com.ibc.applications.fee.v1.Metadata parseFrom(
      byte[] data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static com.ibc.applications.fee.v1.Metadata parseFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static com.ibc.applications.fee.v1.Metadata parseFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input, extensionRegistry);
  }
  public static com.ibc.applications.fee.v1.Metadata parseDelimitedFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input);
  }
  public static com.ibc.applications.fee.v1.Metadata parseDelimitedFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
  }
  public static com.ibc.applications.fee.v1.Metadata parseFrom(
      com.google.protobuf.CodedInputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static com.ibc.applications.fee.v1.Metadata parseFrom(
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
  public static Builder newBuilder(com.ibc.applications.fee.v1.Metadata prototype) {
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
   * Metadata defines the ICS29 channel specific metadata encoded into the channel version bytestring
   * See ICS004: https://github.com/cosmos/ibc/tree/master/spec/core/ics-004-channel-and-packet-semantics#Versioning
   * </pre>
   *
   * Protobuf type {@code ibc.applications.fee.v1.Metadata}
   */
  public static final class Builder extends
      com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
      // @@protoc_insertion_point(builder_implements:ibc.applications.fee.v1.Metadata)
      com.ibc.applications.fee.v1.MetadataOrBuilder {
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return com.ibc.applications.fee.v1.MetadataProto.internal_static_ibc_applications_fee_v1_Metadata_descriptor;
    }

    @java.lang.Override
    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return com.ibc.applications.fee.v1.MetadataProto.internal_static_ibc_applications_fee_v1_Metadata_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              com.ibc.applications.fee.v1.Metadata.class, com.ibc.applications.fee.v1.Metadata.Builder.class);
    }

    // Construct using com.ibc.applications.fee.v1.Metadata.newBuilder()
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
      feeVersion_ = "";
      appVersion_ = "";
      return this;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.Descriptor
        getDescriptorForType() {
      return com.ibc.applications.fee.v1.MetadataProto.internal_static_ibc_applications_fee_v1_Metadata_descriptor;
    }

    @java.lang.Override
    public com.ibc.applications.fee.v1.Metadata getDefaultInstanceForType() {
      return com.ibc.applications.fee.v1.Metadata.getDefaultInstance();
    }

    @java.lang.Override
    public com.ibc.applications.fee.v1.Metadata build() {
      com.ibc.applications.fee.v1.Metadata result = buildPartial();
      if (!result.isInitialized()) {
        throw newUninitializedMessageException(result);
      }
      return result;
    }

    @java.lang.Override
    public com.ibc.applications.fee.v1.Metadata buildPartial() {
      com.ibc.applications.fee.v1.Metadata result = new com.ibc.applications.fee.v1.Metadata(this);
      if (bitField0_ != 0) { buildPartial0(result); }
      onBuilt();
      return result;
    }

    private void buildPartial0(com.ibc.applications.fee.v1.Metadata result) {
      int from_bitField0_ = bitField0_;
      if (((from_bitField0_ & 0x00000001) != 0)) {
        result.feeVersion_ = feeVersion_;
      }
      if (((from_bitField0_ & 0x00000002) != 0)) {
        result.appVersion_ = appVersion_;
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
      if (other instanceof com.ibc.applications.fee.v1.Metadata) {
        return mergeFrom((com.ibc.applications.fee.v1.Metadata)other);
      } else {
        super.mergeFrom(other);
        return this;
      }
    }

    public Builder mergeFrom(com.ibc.applications.fee.v1.Metadata other) {
      if (other == com.ibc.applications.fee.v1.Metadata.getDefaultInstance()) return this;
      if (!other.getFeeVersion().isEmpty()) {
        feeVersion_ = other.feeVersion_;
        bitField0_ |= 0x00000001;
        onChanged();
      }
      if (!other.getAppVersion().isEmpty()) {
        appVersion_ = other.appVersion_;
        bitField0_ |= 0x00000002;
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
              feeVersion_ = input.readStringRequireUtf8();
              bitField0_ |= 0x00000001;
              break;
            } // case 10
            case 18: {
              appVersion_ = input.readStringRequireUtf8();
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

    private java.lang.Object feeVersion_ = "";
    /**
     * <pre>
     * fee_version defines the ICS29 fee version
     * </pre>
     *
     * <code>string fee_version = 1 [json_name = "feeVersion", (.gogoproto.moretags) = "yaml:&#92;"fee_version&#92;""];</code>
     * @return The feeVersion.
     */
    public java.lang.String getFeeVersion() {
      java.lang.Object ref = feeVersion_;
      if (!(ref instanceof java.lang.String)) {
        com.google.protobuf.ByteString bs =
            (com.google.protobuf.ByteString) ref;
        java.lang.String s = bs.toStringUtf8();
        feeVersion_ = s;
        return s;
      } else {
        return (java.lang.String) ref;
      }
    }
    /**
     * <pre>
     * fee_version defines the ICS29 fee version
     * </pre>
     *
     * <code>string fee_version = 1 [json_name = "feeVersion", (.gogoproto.moretags) = "yaml:&#92;"fee_version&#92;""];</code>
     * @return The bytes for feeVersion.
     */
    public com.google.protobuf.ByteString
        getFeeVersionBytes() {
      java.lang.Object ref = feeVersion_;
      if (ref instanceof String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8(
                (java.lang.String) ref);
        feeVersion_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }
    /**
     * <pre>
     * fee_version defines the ICS29 fee version
     * </pre>
     *
     * <code>string fee_version = 1 [json_name = "feeVersion", (.gogoproto.moretags) = "yaml:&#92;"fee_version&#92;""];</code>
     * @param value The feeVersion to set.
     * @return This builder for chaining.
     */
    public Builder setFeeVersion(
        java.lang.String value) {
      if (value == null) { throw new NullPointerException(); }
      feeVersion_ = value;
      bitField0_ |= 0x00000001;
      onChanged();
      return this;
    }
    /**
     * <pre>
     * fee_version defines the ICS29 fee version
     * </pre>
     *
     * <code>string fee_version = 1 [json_name = "feeVersion", (.gogoproto.moretags) = "yaml:&#92;"fee_version&#92;""];</code>
     * @return This builder for chaining.
     */
    public Builder clearFeeVersion() {
      feeVersion_ = getDefaultInstance().getFeeVersion();
      bitField0_ = (bitField0_ & ~0x00000001);
      onChanged();
      return this;
    }
    /**
     * <pre>
     * fee_version defines the ICS29 fee version
     * </pre>
     *
     * <code>string fee_version = 1 [json_name = "feeVersion", (.gogoproto.moretags) = "yaml:&#92;"fee_version&#92;""];</code>
     * @param value The bytes for feeVersion to set.
     * @return This builder for chaining.
     */
    public Builder setFeeVersionBytes(
        com.google.protobuf.ByteString value) {
      if (value == null) { throw new NullPointerException(); }
      checkByteStringIsUtf8(value);
      feeVersion_ = value;
      bitField0_ |= 0x00000001;
      onChanged();
      return this;
    }

    private java.lang.Object appVersion_ = "";
    /**
     * <pre>
     * app_version defines the underlying application version, which may or may not be a JSON encoded bytestring
     * </pre>
     *
     * <code>string app_version = 2 [json_name = "appVersion", (.gogoproto.moretags) = "yaml:&#92;"app_version&#92;""];</code>
     * @return The appVersion.
     */
    public java.lang.String getAppVersion() {
      java.lang.Object ref = appVersion_;
      if (!(ref instanceof java.lang.String)) {
        com.google.protobuf.ByteString bs =
            (com.google.protobuf.ByteString) ref;
        java.lang.String s = bs.toStringUtf8();
        appVersion_ = s;
        return s;
      } else {
        return (java.lang.String) ref;
      }
    }
    /**
     * <pre>
     * app_version defines the underlying application version, which may or may not be a JSON encoded bytestring
     * </pre>
     *
     * <code>string app_version = 2 [json_name = "appVersion", (.gogoproto.moretags) = "yaml:&#92;"app_version&#92;""];</code>
     * @return The bytes for appVersion.
     */
    public com.google.protobuf.ByteString
        getAppVersionBytes() {
      java.lang.Object ref = appVersion_;
      if (ref instanceof String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8(
                (java.lang.String) ref);
        appVersion_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }
    /**
     * <pre>
     * app_version defines the underlying application version, which may or may not be a JSON encoded bytestring
     * </pre>
     *
     * <code>string app_version = 2 [json_name = "appVersion", (.gogoproto.moretags) = "yaml:&#92;"app_version&#92;""];</code>
     * @param value The appVersion to set.
     * @return This builder for chaining.
     */
    public Builder setAppVersion(
        java.lang.String value) {
      if (value == null) { throw new NullPointerException(); }
      appVersion_ = value;
      bitField0_ |= 0x00000002;
      onChanged();
      return this;
    }
    /**
     * <pre>
     * app_version defines the underlying application version, which may or may not be a JSON encoded bytestring
     * </pre>
     *
     * <code>string app_version = 2 [json_name = "appVersion", (.gogoproto.moretags) = "yaml:&#92;"app_version&#92;""];</code>
     * @return This builder for chaining.
     */
    public Builder clearAppVersion() {
      appVersion_ = getDefaultInstance().getAppVersion();
      bitField0_ = (bitField0_ & ~0x00000002);
      onChanged();
      return this;
    }
    /**
     * <pre>
     * app_version defines the underlying application version, which may or may not be a JSON encoded bytestring
     * </pre>
     *
     * <code>string app_version = 2 [json_name = "appVersion", (.gogoproto.moretags) = "yaml:&#92;"app_version&#92;""];</code>
     * @param value The bytes for appVersion to set.
     * @return This builder for chaining.
     */
    public Builder setAppVersionBytes(
        com.google.protobuf.ByteString value) {
      if (value == null) { throw new NullPointerException(); }
      checkByteStringIsUtf8(value);
      appVersion_ = value;
      bitField0_ |= 0x00000002;
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


    // @@protoc_insertion_point(builder_scope:ibc.applications.fee.v1.Metadata)
  }

  // @@protoc_insertion_point(class_scope:ibc.applications.fee.v1.Metadata)
  private static final com.ibc.applications.fee.v1.Metadata DEFAULT_INSTANCE;
  static {
    DEFAULT_INSTANCE = new com.ibc.applications.fee.v1.Metadata();
  }

  public static com.ibc.applications.fee.v1.Metadata getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }

  private static final com.google.protobuf.Parser<Metadata>
      PARSER = new com.google.protobuf.AbstractParser<Metadata>() {
    @java.lang.Override
    public Metadata parsePartialFrom(
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

  public static com.google.protobuf.Parser<Metadata> parser() {
    return PARSER;
  }

  @java.lang.Override
  public com.google.protobuf.Parser<Metadata> getParserForType() {
    return PARSER;
  }

  @java.lang.Override
  public com.ibc.applications.fee.v1.Metadata getDefaultInstanceForType() {
    return DEFAULT_INSTANCE;
  }

}

