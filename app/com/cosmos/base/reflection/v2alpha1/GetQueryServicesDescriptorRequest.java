// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: cosmos/base/reflection/v2alpha1/reflection.proto

package com.cosmos.base.reflection.v2alpha1;

/**
 * <pre>
 * GetQueryServicesDescriptorRequest is the request used for the GetQueryServicesDescriptor RPC
 * </pre>
 *
 * Protobuf type {@code cosmos.base.reflection.v2alpha1.GetQueryServicesDescriptorRequest}
 */
public final class GetQueryServicesDescriptorRequest extends
    com.google.protobuf.GeneratedMessageV3 implements
    // @@protoc_insertion_point(message_implements:cosmos.base.reflection.v2alpha1.GetQueryServicesDescriptorRequest)
    GetQueryServicesDescriptorRequestOrBuilder {
private static final long serialVersionUID = 0L;
  // Use GetQueryServicesDescriptorRequest.newBuilder() to construct.
  private GetQueryServicesDescriptorRequest(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
    super(builder);
  }
  private GetQueryServicesDescriptorRequest() {
  }

  @java.lang.Override
  @SuppressWarnings({"unused"})
  protected java.lang.Object newInstance(
      UnusedPrivateParameter unused) {
    return new GetQueryServicesDescriptorRequest();
  }

  public static final com.google.protobuf.Descriptors.Descriptor
      getDescriptor() {
    return com.cosmos.base.reflection.v2alpha1.ReflectionProto.internal_static_cosmos_base_reflection_v2alpha1_GetQueryServicesDescriptorRequest_descriptor;
  }

  @java.lang.Override
  protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internalGetFieldAccessorTable() {
    return com.cosmos.base.reflection.v2alpha1.ReflectionProto.internal_static_cosmos_base_reflection_v2alpha1_GetQueryServicesDescriptorRequest_fieldAccessorTable
        .ensureFieldAccessorsInitialized(
            com.cosmos.base.reflection.v2alpha1.GetQueryServicesDescriptorRequest.class, com.cosmos.base.reflection.v2alpha1.GetQueryServicesDescriptorRequest.Builder.class);
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
    getUnknownFields().writeTo(output);
  }

  @java.lang.Override
  public int getSerializedSize() {
    int size = memoizedSize;
    if (size != -1) return size;

    size = 0;
    size += getUnknownFields().getSerializedSize();
    memoizedSize = size;
    return size;
  }

  @java.lang.Override
  public boolean equals(final java.lang.Object obj) {
    if (obj == this) {
     return true;
    }
    if (!(obj instanceof com.cosmos.base.reflection.v2alpha1.GetQueryServicesDescriptorRequest)) {
      return super.equals(obj);
    }
    com.cosmos.base.reflection.v2alpha1.GetQueryServicesDescriptorRequest other = (com.cosmos.base.reflection.v2alpha1.GetQueryServicesDescriptorRequest) obj;

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
    hash = (29 * hash) + getUnknownFields().hashCode();
    memoizedHashCode = hash;
    return hash;
  }

  public static com.cosmos.base.reflection.v2alpha1.GetQueryServicesDescriptorRequest parseFrom(
      java.nio.ByteBuffer data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static com.cosmos.base.reflection.v2alpha1.GetQueryServicesDescriptorRequest parseFrom(
      java.nio.ByteBuffer data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static com.cosmos.base.reflection.v2alpha1.GetQueryServicesDescriptorRequest parseFrom(
      com.google.protobuf.ByteString data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static com.cosmos.base.reflection.v2alpha1.GetQueryServicesDescriptorRequest parseFrom(
      com.google.protobuf.ByteString data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static com.cosmos.base.reflection.v2alpha1.GetQueryServicesDescriptorRequest parseFrom(byte[] data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static com.cosmos.base.reflection.v2alpha1.GetQueryServicesDescriptorRequest parseFrom(
      byte[] data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static com.cosmos.base.reflection.v2alpha1.GetQueryServicesDescriptorRequest parseFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static com.cosmos.base.reflection.v2alpha1.GetQueryServicesDescriptorRequest parseFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input, extensionRegistry);
  }
  public static com.cosmos.base.reflection.v2alpha1.GetQueryServicesDescriptorRequest parseDelimitedFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input);
  }
  public static com.cosmos.base.reflection.v2alpha1.GetQueryServicesDescriptorRequest parseDelimitedFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
  }
  public static com.cosmos.base.reflection.v2alpha1.GetQueryServicesDescriptorRequest parseFrom(
      com.google.protobuf.CodedInputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static com.cosmos.base.reflection.v2alpha1.GetQueryServicesDescriptorRequest parseFrom(
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
  public static Builder newBuilder(com.cosmos.base.reflection.v2alpha1.GetQueryServicesDescriptorRequest prototype) {
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
   * GetQueryServicesDescriptorRequest is the request used for the GetQueryServicesDescriptor RPC
   * </pre>
   *
   * Protobuf type {@code cosmos.base.reflection.v2alpha1.GetQueryServicesDescriptorRequest}
   */
  public static final class Builder extends
      com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
      // @@protoc_insertion_point(builder_implements:cosmos.base.reflection.v2alpha1.GetQueryServicesDescriptorRequest)
      com.cosmos.base.reflection.v2alpha1.GetQueryServicesDescriptorRequestOrBuilder {
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return com.cosmos.base.reflection.v2alpha1.ReflectionProto.internal_static_cosmos_base_reflection_v2alpha1_GetQueryServicesDescriptorRequest_descriptor;
    }

    @java.lang.Override
    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return com.cosmos.base.reflection.v2alpha1.ReflectionProto.internal_static_cosmos_base_reflection_v2alpha1_GetQueryServicesDescriptorRequest_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              com.cosmos.base.reflection.v2alpha1.GetQueryServicesDescriptorRequest.class, com.cosmos.base.reflection.v2alpha1.GetQueryServicesDescriptorRequest.Builder.class);
    }

    // Construct using com.cosmos.base.reflection.v2alpha1.GetQueryServicesDescriptorRequest.newBuilder()
    private Builder() {

    }

    private Builder(
        com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
      super(parent);

    }
    @java.lang.Override
    public Builder clear() {
      super.clear();
      return this;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.Descriptor
        getDescriptorForType() {
      return com.cosmos.base.reflection.v2alpha1.ReflectionProto.internal_static_cosmos_base_reflection_v2alpha1_GetQueryServicesDescriptorRequest_descriptor;
    }

    @java.lang.Override
    public com.cosmos.base.reflection.v2alpha1.GetQueryServicesDescriptorRequest getDefaultInstanceForType() {
      return com.cosmos.base.reflection.v2alpha1.GetQueryServicesDescriptorRequest.getDefaultInstance();
    }

    @java.lang.Override
    public com.cosmos.base.reflection.v2alpha1.GetQueryServicesDescriptorRequest build() {
      com.cosmos.base.reflection.v2alpha1.GetQueryServicesDescriptorRequest result = buildPartial();
      if (!result.isInitialized()) {
        throw newUninitializedMessageException(result);
      }
      return result;
    }

    @java.lang.Override
    public com.cosmos.base.reflection.v2alpha1.GetQueryServicesDescriptorRequest buildPartial() {
      com.cosmos.base.reflection.v2alpha1.GetQueryServicesDescriptorRequest result = new com.cosmos.base.reflection.v2alpha1.GetQueryServicesDescriptorRequest(this);
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
      if (other instanceof com.cosmos.base.reflection.v2alpha1.GetQueryServicesDescriptorRequest) {
        return mergeFrom((com.cosmos.base.reflection.v2alpha1.GetQueryServicesDescriptorRequest)other);
      } else {
        super.mergeFrom(other);
        return this;
      }
    }

    public Builder mergeFrom(com.cosmos.base.reflection.v2alpha1.GetQueryServicesDescriptorRequest other) {
      if (other == com.cosmos.base.reflection.v2alpha1.GetQueryServicesDescriptorRequest.getDefaultInstance()) return this;
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


    // @@protoc_insertion_point(builder_scope:cosmos.base.reflection.v2alpha1.GetQueryServicesDescriptorRequest)
  }

  // @@protoc_insertion_point(class_scope:cosmos.base.reflection.v2alpha1.GetQueryServicesDescriptorRequest)
  private static final com.cosmos.base.reflection.v2alpha1.GetQueryServicesDescriptorRequest DEFAULT_INSTANCE;
  static {
    DEFAULT_INSTANCE = new com.cosmos.base.reflection.v2alpha1.GetQueryServicesDescriptorRequest();
  }

  public static com.cosmos.base.reflection.v2alpha1.GetQueryServicesDescriptorRequest getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }

  private static final com.google.protobuf.Parser<GetQueryServicesDescriptorRequest>
      PARSER = new com.google.protobuf.AbstractParser<GetQueryServicesDescriptorRequest>() {
    @java.lang.Override
    public GetQueryServicesDescriptorRequest parsePartialFrom(
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

  public static com.google.protobuf.Parser<GetQueryServicesDescriptorRequest> parser() {
    return PARSER;
  }

  @java.lang.Override
  public com.google.protobuf.Parser<GetQueryServicesDescriptorRequest> getParserForType() {
    return PARSER;
  }

  @java.lang.Override
  public com.cosmos.base.reflection.v2alpha1.GetQueryServicesDescriptorRequest getDefaultInstanceForType() {
    return DEFAULT_INSTANCE;
  }

}
