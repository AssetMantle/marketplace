// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: identities/transactions/define/transaction_response.proto

package com.assetmantle.modules.identities.transactions.define;

public final class TransactionResponseProto {
  private TransactionResponseProto() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistryLite registry) {
  }

  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
    registerAllExtensions(
        (com.google.protobuf.ExtensionRegistryLite) registry);
  }
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_assetmantle_modules_identities_transactions_define_TransactionResponse_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_assetmantle_modules_identities_transactions_define_TransactionResponse_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static  com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n9identities/transactions/define/transac" +
      "tion_response.proto\0222assetmantle.modules" +
      ".identities.transactions.define\032 ids/bas" +
      "e/classification_id.proto\"r\n\023Transaction" +
      "Response\022[\n\022classification_i_d\030\001 \001(\0132-.a" +
      "ssetmantle.schema.ids.base.Classificatio" +
      "nIDR\020classificationIDB\201\003\n6com.assetmantl" +
      "e.modules.identities.transactions.define" +
      "B\030TransactionResponseProtoP\001Z?github.com" +
      "/AssetMantle/modules/x/identities/transa" +
      "ctions/define\242\002\005AMITD\252\0022Assetmantle.Modu" +
      "les.Identities.Transactions.Define\312\0022Ass" +
      "etmantle\\Modules\\Identities\\Transactions" +
      "\\Define\342\002>Assetmantle\\Modules\\Identities" +
      "\\Transactions\\Define\\GPBMetadata\352\0026Asset" +
      "mantle::Modules::Identities::Transaction" +
      "s::Defineb\006proto3"
    };
    descriptor = com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
          com.assetmantle.schema.ids.base.ClassificationIdProto.getDescriptor(),
        });
    internal_static_assetmantle_modules_identities_transactions_define_TransactionResponse_descriptor =
      getDescriptor().getMessageTypes().get(0);
    internal_static_assetmantle_modules_identities_transactions_define_TransactionResponse_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_assetmantle_modules_identities_transactions_define_TransactionResponse_descriptor,
        new java.lang.String[] { "ClassificationID", });
    com.assetmantle.schema.ids.base.ClassificationIdProto.getDescriptor();
  }

  // @@protoc_insertion_point(outer_class_scope)
}