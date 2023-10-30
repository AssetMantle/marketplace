// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: tendermint/blockchain/types.proto

package com.tendermint.blockchain;

public final class TypesProto {
  private TypesProto() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistryLite registry) {
  }

  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
    registerAllExtensions(
        (com.google.protobuf.ExtensionRegistryLite) registry);
  }
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_tendermint_blockchain_BlockRequest_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_tendermint_blockchain_BlockRequest_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_tendermint_blockchain_NoBlockResponse_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_tendermint_blockchain_NoBlockResponse_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_tendermint_blockchain_BlockResponse_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_tendermint_blockchain_BlockResponse_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_tendermint_blockchain_StatusRequest_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_tendermint_blockchain_StatusRequest_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_tendermint_blockchain_StatusResponse_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_tendermint_blockchain_StatusResponse_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_tendermint_blockchain_Message_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_tendermint_blockchain_Message_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static  com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n!tendermint/blockchain/types.proto\022\025ten" +
      "dermint.blockchain\032\034tendermint/types/blo" +
      "ck.proto\"&\n\014BlockRequest\022\026\n\006height\030\001 \001(\003" +
      "R\006height\")\n\017NoBlockResponse\022\026\n\006height\030\001 " +
      "\001(\003R\006height\">\n\rBlockResponse\022-\n\005block\030\001 " +
      "\001(\0132\027.tendermint.types.BlockR\005block\"\017\n\rS" +
      "tatusRequest\"<\n\016StatusResponse\022\026\n\006height" +
      "\030\001 \001(\003R\006height\022\022\n\004base\030\002 \001(\003R\004base\"\242\003\n\007M" +
      "essage\022J\n\rblock_request\030\001 \001(\0132#.tendermi" +
      "nt.blockchain.BlockRequestH\000R\014blockReque" +
      "st\022T\n\021no_block_response\030\002 \001(\0132&.tendermi" +
      "nt.blockchain.NoBlockResponseH\000R\017noBlock" +
      "Response\022M\n\016block_response\030\003 \001(\0132$.tende" +
      "rmint.blockchain.BlockResponseH\000R\rblockR" +
      "esponse\022M\n\016status_request\030\004 \001(\0132$.tender" +
      "mint.blockchain.StatusRequestH\000R\rstatusR" +
      "equest\022P\n\017status_response\030\005 \001(\0132%.tender" +
      "mint.blockchain.StatusResponseH\000R\016status" +
      "ResponseB\005\n\003sumB\332\001\n\031com.tendermint.block" +
      "chainB\nTypesProtoP\001Z<github.com/tendermi" +
      "nt/tendermint/proto/tendermint/blockchai" +
      "n\242\002\003TBX\252\002\025Tendermint.Blockchain\312\002\025Tender" +
      "mint\\Blockchain\342\002!Tendermint\\Blockchain\\" +
      "GPBMetadata\352\002\026Tendermint::Blockchainb\006pr" +
      "oto3"
    };
    descriptor = com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
          com.tendermint.types.BlockProto.getDescriptor(),
        });
    internal_static_tendermint_blockchain_BlockRequest_descriptor =
      getDescriptor().getMessageTypes().get(0);
    internal_static_tendermint_blockchain_BlockRequest_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_tendermint_blockchain_BlockRequest_descriptor,
        new java.lang.String[] { "Height", });
    internal_static_tendermint_blockchain_NoBlockResponse_descriptor =
      getDescriptor().getMessageTypes().get(1);
    internal_static_tendermint_blockchain_NoBlockResponse_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_tendermint_blockchain_NoBlockResponse_descriptor,
        new java.lang.String[] { "Height", });
    internal_static_tendermint_blockchain_BlockResponse_descriptor =
      getDescriptor().getMessageTypes().get(2);
    internal_static_tendermint_blockchain_BlockResponse_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_tendermint_blockchain_BlockResponse_descriptor,
        new java.lang.String[] { "Block", });
    internal_static_tendermint_blockchain_StatusRequest_descriptor =
      getDescriptor().getMessageTypes().get(3);
    internal_static_tendermint_blockchain_StatusRequest_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_tendermint_blockchain_StatusRequest_descriptor,
        new java.lang.String[] { });
    internal_static_tendermint_blockchain_StatusResponse_descriptor =
      getDescriptor().getMessageTypes().get(4);
    internal_static_tendermint_blockchain_StatusResponse_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_tendermint_blockchain_StatusResponse_descriptor,
        new java.lang.String[] { "Height", "Base", });
    internal_static_tendermint_blockchain_Message_descriptor =
      getDescriptor().getMessageTypes().get(5);
    internal_static_tendermint_blockchain_Message_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_tendermint_blockchain_Message_descriptor,
        new java.lang.String[] { "BlockRequest", "NoBlockResponse", "BlockResponse", "StatusRequest", "StatusResponse", "Sum", });
    com.tendermint.types.BlockProto.getDescriptor();
  }

  // @@protoc_insertion_point(outer_class_scope)
}