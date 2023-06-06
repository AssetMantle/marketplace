# --- !Ups
CREATE SCHEMA IF NOT EXISTS CAMPAIGN
    AUTHORIZATION "mantlePlace";

ALTER TABLE BLOCKCHAIN_TRANSACTION."NFTPublicListing"
    DROP COLUMN IF EXISTS "txRawBytes";
ALTER TABLE BLOCKCHAIN_TRANSACTION."NFTSale"
    DROP COLUMN IF EXISTS "txRawBytes";
ALTER TABLE BLOCKCHAIN_TRANSACTION."SendCoin"
    DROP COLUMN IF EXISTS "txRawBytes";

CREATE TABLE IF NOT EXISTS BLOCKCHAIN_TRANSACTION."CampaignSendCoin"
(
    "txHash"               VARCHAR NOT NULL,
    "fromAddress"          VARCHAR NOT NULL,
    "status"               BOOLEAN,
    "memo"                 VARCHAR,
    "timeoutHeight"        INTEGER NOT NULL,
    "log"                  VARCHAR,
    "createdBy"            VARCHAR,
    "createdOnMillisEpoch" BIGINT,
    "updatedBy"            VARCHAR,
    "updatedOnMillisEpoch" BIGINT,
    PRIMARY KEY ("txHash")
);

CREATE TABLE IF NOT EXISTS BLOCKCHAIN_TRANSACTION."CancelOrder"
(
    "txHash"               VARCHAR NOT NULL,
    "fromAddress"          VARCHAR NOT NULL,
    "status"               BOOLEAN,
    "memo"                 VARCHAR,
    "timeoutHeight"        INTEGER NOT NULL,
    "log"                  VARCHAR,
    "createdBy"            VARCHAR,
    "createdOnMillisEpoch" BIGINT,
    "updatedBy"            VARCHAR,
    "updatedOnMillisEpoch" BIGINT,
    PRIMARY KEY ("txHash")
);

CREATE TABLE IF NOT EXISTS BLOCKCHAIN_TRANSACTION."DefineAsset"
(
    "txHash"               VARCHAR NOT NULL,
    "fromAddress"          VARCHAR NOT NULL,
    "status"               BOOLEAN,
    "memo"                 VARCHAR,
    "timeoutHeight"        INTEGER NOT NULL,
    "log"                  VARCHAR,
    "createdBy"            VARCHAR,
    "createdOnMillisEpoch" BIGINT,
    "updatedBy"            VARCHAR,
    "updatedOnMillisEpoch" BIGINT,
    PRIMARY KEY ("txHash")
);

CREATE TABLE IF NOT EXISTS BLOCKCHAIN_TRANSACTION."IssueIdentity"
(
    "txHash"               VARCHAR NOT NULL,
    "fromAddress"          VARCHAR NOT NULL,
    "status"               BOOLEAN,
    "memo"                 VARCHAR,
    "timeoutHeight"        INTEGER NOT NULL,
    "log"                  VARCHAR,
    "createdBy"            VARCHAR,
    "createdOnMillisEpoch" BIGINT,
    "updatedBy"            VARCHAR,
    "updatedOnMillisEpoch" BIGINT,
    PRIMARY KEY ("txHash")
);

CREATE TABLE IF NOT EXISTS BLOCKCHAIN_TRANSACTION."MakeOrder"
(
    "txHash"               VARCHAR NOT NULL,
    "fromAddress"          VARCHAR NOT NULL,
    "status"               BOOLEAN,
    "memo"                 VARCHAR,
    "timeoutHeight"        INTEGER NOT NULL,
    "log"                  VARCHAR,
    "createdBy"            VARCHAR,
    "createdOnMillisEpoch" BIGINT,
    "updatedBy"            VARCHAR,
    "updatedOnMillisEpoch" BIGINT,
    PRIMARY KEY ("txHash")
);

CREATE TABLE IF NOT EXISTS BLOCKCHAIN_TRANSACTION."MintAsset"
(
    "txHash"               VARCHAR NOT NULL,
    "fromAddress"          VARCHAR NOT NULL,
    "status"               BOOLEAN,
    "memo"                 VARCHAR,
    "timeoutHeight"        INTEGER NOT NULL,
    "log"                  VARCHAR,
    "createdBy"            VARCHAR,
    "createdOnMillisEpoch" BIGINT,
    "updatedBy"            VARCHAR,
    "updatedOnMillisEpoch" BIGINT,
    PRIMARY KEY ("txHash")
);

CREATE TABLE IF NOT EXISTS BLOCKCHAIN_TRANSACTION."NFTMintingFee"
(
    "txHash"               VARCHAR NOT NULL,
    "fromAddress"          VARCHAR NOT NULL,
    "status"               BOOLEAN,
    "memo"                 VARCHAR,
    "timeoutHeight"        INTEGER NOT NULL,
    "log"                  VARCHAR,
    "createdBy"            VARCHAR,
    "createdOnMillisEpoch" BIGINT,
    "updatedBy"            VARCHAR,
    "updatedOnMillisEpoch" BIGINT,
    PRIMARY KEY ("txHash")
);

CREATE TABLE IF NOT EXISTS BLOCKCHAIN_TRANSACTION."ProvisionAddress"
(
    "txHash"               VARCHAR NOT NULL,
    "fromAddress"          VARCHAR NOT NULL,
    "status"               BOOLEAN,
    "memo"                 VARCHAR,
    "timeoutHeight"        INTEGER NOT NULL,
    "log"                  VARCHAR,
    "createdBy"            VARCHAR,
    "createdOnMillisEpoch" BIGINT,
    "updatedBy"            VARCHAR,
    "updatedOnMillisEpoch" BIGINT,
    PRIMARY KEY ("txHash")
);

CREATE TABLE IF NOT EXISTS BLOCKCHAIN_TRANSACTION."SplitSend"
(
    "txHash"               VARCHAR NOT NULL,
    "fromAddress"          VARCHAR NOT NULL,
    "status"               BOOLEAN,
    "memo"                 VARCHAR,
    "timeoutHeight"        INTEGER NOT NULL,
    "log"                  VARCHAR,
    "createdBy"            VARCHAR,
    "createdOnMillisEpoch" BIGINT,
    "updatedBy"            VARCHAR,
    "updatedOnMillisEpoch" BIGINT,
    PRIMARY KEY ("txHash")
);

CREATE TABLE IF NOT EXISTS BLOCKCHAIN_TRANSACTION."TakeOrder"
(
    "txHash"               VARCHAR NOT NULL,
    "fromAddress"          VARCHAR NOT NULL,
    "status"               BOOLEAN,
    "memo"                 VARCHAR,
    "timeoutHeight"        INTEGER NOT NULL,
    "log"                  VARCHAR,
    "createdBy"            VARCHAR,
    "createdOnMillisEpoch" BIGINT,
    "updatedBy"            VARCHAR,
    "updatedOnMillisEpoch" BIGINT,
    PRIMARY KEY ("txHash")
);

CREATE TABLE IF NOT EXISTS BLOCKCHAIN_TRANSACTION."UnprovisionAddress"
(
    "txHash"               VARCHAR NOT NULL,
    "fromAddress"          VARCHAR NOT NULL,
    "status"               BOOLEAN,
    "memo"                 VARCHAR,
    "timeoutHeight"        INTEGER NOT NULL,
    "log"                  VARCHAR,
    "createdBy"            VARCHAR,
    "createdOnMillisEpoch" BIGINT,
    "updatedBy"            VARCHAR,
    "updatedOnMillisEpoch" BIGINT,
    PRIMARY KEY ("txHash")
);

CREATE TABLE IF NOT EXISTS BLOCKCHAIN_TRANSACTION."Unwrap"
(
    "txHash"               VARCHAR NOT NULL,
    "fromAddress"          VARCHAR NOT NULL,
    "status"               BOOLEAN,
    "memo"                 VARCHAR,
    "timeoutHeight"        INTEGER NOT NULL,
    "log"                  VARCHAR,
    "createdBy"            VARCHAR,
    "createdOnMillisEpoch" BIGINT,
    "updatedBy"            VARCHAR,
    "updatedOnMillisEpoch" BIGINT,
    PRIMARY KEY ("txHash")
);

CREATE TABLE IF NOT EXISTS BLOCKCHAIN_TRANSACTION."Wrap"
(
    "txHash"               VARCHAR NOT NULL,
    "fromAddress"          VARCHAR NOT NULL,
    "status"               BOOLEAN,
    "memo"                 VARCHAR,
    "timeoutHeight"        INTEGER NOT NULL,
    "log"                  VARCHAR,
    "createdBy"            VARCHAR,
    "createdOnMillisEpoch" BIGINT,
    "updatedBy"            VARCHAR,
    "updatedOnMillisEpoch" BIGINT,
    PRIMARY KEY ("txHash")
);

CREATE TABLE IF NOT EXISTS CAMPAIGN."MintNFTAirDrop"
(
    "accountId"            VARCHAR NOT NULL,
    "address"              VARCHAR NOT NULL,
    "eligibilityTxHash"    VARCHAR NOT NULL,
    "airdropTxHash"        VARCHAR,
    "status"               BOOLEAN NOT NULL,
    "log"                  VARCHAR,
    "createdBy"            VARCHAR,
    "createdOnMillisEpoch" BIGINT,
    "updatedBy"            VARCHAR,
    "updatedOnMillisEpoch" BIGINT,
    PRIMARY KEY ("accountId")
);

CREATE TABLE IF NOT EXISTS CAMPAIGN."IneligibleMintNFTAirDrop"
(
    "nftID"                VARCHAR NOT NULL,
    "createdBy"            VARCHAR,
    "createdOnMillisEpoch" BIGINT,
    "updatedBy"            VARCHAR,
    "updatedOnMillisEpoch" BIGINT,
    PRIMARY KEY ("nftID")
);

CREATE TABLE IF NOT EXISTS HISTORY."MasterSecondaryMarket"
(
    "id"                   VARCHAR NOT NULL,
    "orderId"              VARCHAR UNIQUE,
    "nftId"                VARCHAR NOT NULL,
    "collectionId"         VARCHAR NOT NULL,
    "sellerId"             VARCHAR NOT NULL,
    "quantity"             NUMERIC NOT NULL,
    "price"                NUMERIC NOT NULL,
    "denom"                VARCHAR NOT NULL,
    "endHours"             INTEGER NOT NULL,
    "externallyMade"       BOOLEAN NOT NULL,
    "completed"            BOOLEAN NOT NULL,
    "cancelled"            BOOLEAN NOT NULL,
    "expired"              BOOLEAN NOT NULL,
    "failed"               BOOLEAN NOT NULL,
    "createdBy"            VARCHAR,
    "createdOnMillisEpoch" BIGINT,
    "updatedBy"            VARCHAR,
    "updatedOnMillisEpoch" BIGINT,
    "deletedBy"            VARCHAR,
    "deletedOnMillisEpoch" BIGINT,
    PRIMARY KEY ("id")
);

CREATE TABLE IF NOT EXISTS MASTER."BurntNFT"
(
    "nftId"                VARCHAR NOT NULL,
    "txHash"               VARCHAR NOT NULL UNIQUE,
    "collectionId"         VARCHAR NOT NULL,
    "assetId"              VARCHAR NOT NULL,
    "classificationId"     VARCHAR NOT NULL,
    "supply"               NUMERIC NOT NULL,
    "name"                 VARCHAR NOT NULL,
    "description"          VARCHAR NOT NULL,
    "properties"           VARCHAR NOT NULL,
    "fileExtension"        VARCHAR NOT NULL,
    "createdBy"            VARCHAR,
    "createdOnMillisEpoch" BIGINT,
    "updatedBy"            VARCHAR,
    "updatedOnMillisEpoch" BIGINT,
    PRIMARY KEY ("nftId", "txHash"),
    UNIQUE ("assetId", "txHash")
);

CREATE TABLE IF NOT EXISTS MASTER."SecondaryMarket"
(
    "id"                   VARCHAR NOT NULL,
    "orderId"              VARCHAR,
    "nftId"                VARCHAR NOT NULL,
    "collectionId"         VARCHAR NOT NULL,
    "sellerId"             VARCHAR NOT NULL,
    "quantity"             NUMERIC NOT NULL,
    "price"                NUMERIC NOT NULL,
    "denom"                VARCHAR NOT NULL,
    "endHours"             INTEGER NOT NULL,
    "externallyMade"       BOOLEAN NOT NULL,
    "completed"            BOOLEAN NOT NULL,
    "cancelled"            BOOLEAN NOT NULL,
    "expired"              BOOLEAN NOT NULL,
    "failed"               BOOLEAN NOT NULL,
    "createdBy"            VARCHAR,
    "createdOnMillisEpoch" BIGINT,
    "updatedBy"            VARCHAR,
    "updatedOnMillisEpoch" BIGINT,
    PRIMARY KEY ("id"),
    UNIQUE ("nftId", "sellerId"),
    UNIQUE ("id", "nftId")
);

CREATE TABLE IF NOT EXISTS MASTER_TRANSACTION."CancelOrderTransaction"
(
    "txHash"               VARCHAR NOT NULL,
    "secondaryMarketId"    VARCHAR NOT NULL,
    "sellerId"             VARCHAR NOT NULL,
    "status"               BOOLEAN,
    "createdBy"            VARCHAR,
    "createdOnMillisEpoch" BIGINT,
    "updatedBy"            VARCHAR,
    "updatedOnMillisEpoch" BIGINT,
    PRIMARY KEY ("txHash", "secondaryMarketId"),
    UNIQUE ("txHash", "sellerId")
);

CREATE TABLE IF NOT EXISTS MASTER_TRANSACTION."DefineAssetTransaction"
(
    "txHash"               VARCHAR NOT NULL,
    "collectionId"         VARCHAR NOT NULL,
    "status"               BOOLEAN,
    "createdBy"            VARCHAR,
    "createdOnMillisEpoch" BIGINT,
    "updatedBy"            VARCHAR,
    "updatedOnMillisEpoch" BIGINT,
    PRIMARY KEY ("txHash", "collectionId")
);

CREATE TABLE IF NOT EXISTS MASTER_TRANSACTION."ExternalAsset"
(
    "nftId"                  VARCHAR NOT NULL,
    "lastOwnerId"            VARCHAR NOT NULL,
    "assetId"                VARCHAR NOT NULL,
    "currentOwnerIdentityId" VARCHAR NOT NULL,
    "collectionId"           VARCHAR NOT NULL,
    "amount"                 NUMERIC NOT NULL,
    "createdBy"              VARCHAR,
    "createdOnMillisEpoch"   BIGINT,
    "updatedBy"              VARCHAR,
    "updatedOnMillisEpoch"   BIGINT,
    PRIMARY KEY ("nftId", "lastOwnerId"),
    UNIQUE ("assetId", "currentOwnerIdentityId")
);

CREATE TABLE IF NOT EXISTS MASTER_TRANSACTION."IssueIdentityTransaction"
(
    "txHash"               VARCHAR NOT NULL,
    "accountId"            VARCHAR NOT NULL,
    "status"               BOOLEAN,
    "createdBy"            VARCHAR,
    "createdOnMillisEpoch" BIGINT,
    "updatedBy"            VARCHAR,
    "updatedOnMillisEpoch" BIGINT,
    PRIMARY KEY ("txHash", "accountId")
);

CREATE TABLE IF NOT EXISTS MASTER_TRANSACTION."LatestBlock"
(
    "height"               BIGINT,
    "createdBy"            VARCHAR,
    "createdOnMillisEpoch" BIGINT,
    "updatedBy"            VARCHAR,
    "updatedOnMillisEpoch" BIGINT,
    PRIMARY KEY ("height")
);

CREATE TABLE IF NOT EXISTS MASTER_TRANSACTION."MakeOrderTransaction"
(
    "txHash"               VARCHAR NOT NULL,
    "nftId"                VARCHAR NOT NULL,
    "sellerId"             VARCHAR NOT NULL,
    "buyerId"              VARCHAR,
    "denom"                VARCHAR NOT NULL,
    "makerOwnableSplit"    NUMERIC NOT NULL,
    "expiresIn"            BIGINT  NOT NULL,
    "takerOwnableSplit"    NUMERIC NOT NULL,
    "secondaryMarketId"    VARCHAR NOT NULL,
    "status"               BOOLEAN,
    "creationHeight"       INTEGER,
    "createdBy"            VARCHAR,
    "createdOnMillisEpoch" BIGINT,
    "updatedBy"            VARCHAR,
    "updatedOnMillisEpoch" BIGINT,
    PRIMARY KEY ("txHash", "nftId", "sellerId"),
    UNIQUE ("nftId", "secondaryMarketId")
);

CREATE TABLE IF NOT EXISTS MASTER_TRANSACTION."MintAssetTransaction"
(
    "txHash"               VARCHAR NOT NULL,
    "nftID"                VARCHAR NOT NULL,
    "minterAccountID"      VARCHAR NOT NULL,
    "status"               BOOLEAN,
    "createdBy"            VARCHAR,
    "createdOnMillisEpoch" BIGINT,
    "updatedBy"            VARCHAR,
    "updatedOnMillisEpoch" BIGINT,
    PRIMARY KEY ("txHash", "nftID")
);

CREATE TABLE IF NOT EXISTS MASTER_TRANSACTION."NFTMintingFeeTransaction"
(
    "txHash"               VARCHAR NOT NULL,
    "nftId"                VARCHAR NOT NULL,
    "collectionId"         VARCHAR NOT NULL,
    "accountId"            VARCHAR NOT NULL,
    "status"               BOOLEAN,
    "createdBy"            VARCHAR,
    "createdOnMillisEpoch" BIGINT,
    "updatedBy"            VARCHAR,
    "updatedOnMillisEpoch" BIGINT,
    PRIMARY KEY ("txHash", "nftId")
);

CREATE TABLE IF NOT EXISTS MASTER_TRANSACTION."ProvisionAddressTransaction"
(
    "txHash"               VARCHAR NOT NULL,
    "accountId"            VARCHAR NOT NULL,
    "toAddress"            VARCHAR NOT NULL,
    "status"               BOOLEAN,
    "createdBy"            VARCHAR,
    "createdOnMillisEpoch" BIGINT,
    "updatedBy"            VARCHAR,
    "updatedOnMillisEpoch" BIGINT,
    PRIMARY KEY ("txHash", "accountId")
);

CREATE TABLE IF NOT EXISTS MASTER_TRANSACTION."NFTTransferTransaction"
(
    "txHash"               VARCHAR NOT NULL,
    "nftId"                VARCHAR NOT NULL,
    "ownerId"              VARCHAR NOT NULL,
    "quantity"             INTEGER NOT NULL,
    "toIdentityId"         VARCHAR NOT NULL,
    "toAccountId"          VARCHAR,
    "status"               BOOLEAN,
    "createdBy"            VARCHAR,
    "createdOnMillisEpoch" BIGINT,
    "updatedBy"            VARCHAR,
    "updatedOnMillisEpoch" BIGINT,
    PRIMARY KEY ("txHash", "nftId", "ownerId")
);

CREATE TABLE IF NOT EXISTS MASTER_TRANSACTION."TakeOrderTransaction"
(
    "txHash"               VARCHAR NOT NULL,
    "nftId"                VARCHAR NOT NULL,
    "buyerId"              VARCHAR NOT NULL,
    "quantity"             NUMERIC NOT NULL,
    "secondaryMarketId"    VARCHAR NOT NULL,
    "status"               BOOLEAN,
    "createdBy"            VARCHAR,
    "createdOnMillisEpoch" BIGINT,
    "updatedBy"            VARCHAR,
    "updatedOnMillisEpoch" BIGINT,
    PRIMARY KEY ("txHash", "nftId", "buyerId")
);

CREATE TABLE IF NOT EXISTS MASTER_TRANSACTION."UnprovisionAddressTransaction"
(
    "txHash"               VARCHAR NOT NULL,
    "accountId"            VARCHAR NOT NULL,
    "toAddress"            VARCHAR NOT NULL,
    "status"               BOOLEAN,
    "createdBy"            VARCHAR,
    "createdOnMillisEpoch" BIGINT,
    "updatedBy"            VARCHAR,
    "updatedOnMillisEpoch" BIGINT,
    PRIMARY KEY ("txHash", "accountId")
);

CREATE TABLE IF NOT EXISTS MASTER_TRANSACTION."UnwrapTransaction"
(
    "txHash"               VARCHAR NOT NULL,
    "accountId"            VARCHAR NOT NULL,
    "ownableId"            VARCHAR NOT NULL,
    "isCoin"               BOOLEAN NOT NULL,
    "amount"               NUMERIC NOT NULL,
    "status"               BOOLEAN,
    "createdBy"            VARCHAR,
    "createdOnMillisEpoch" BIGINT,
    "updatedBy"            VARCHAR,
    "updatedOnMillisEpoch" BIGINT,
    PRIMARY KEY ("txHash", "accountId")
);

CREATE TABLE IF NOT EXISTS MASTER_TRANSACTION."WrapTransaction"
(
    "txHash"               VARCHAR NOT NULL,
    "accountId"            VARCHAR NOT NULL,
    "ownableId"            VARCHAR NOT NULL,
    "isCoin"               BOOLEAN NOT NULL,
    "amount"               NUMERIC NOT NULL,
    "status"               BOOLEAN,
    "createdBy"            VARCHAR,
    "createdOnMillisEpoch" BIGINT,
    "updatedBy"            VARCHAR,
    "updatedOnMillisEpoch" BIGINT,
    PRIMARY KEY ("txHash", "accountId")
);

ALTER TABLE ANALYTICS."CollectionAnalysis"
    ADD COLUMN IF NOT EXISTS "totalBurnt" BIGINT NOT NULL DEFAULT 0;

ALTER TABLE CAMPAIGN."MintNFTAirDrop"
    ADD CONSTRAINT MintNFTAirDrop_AccountId FOREIGN KEY ("accountId") REFERENCES MASTER."Account" ("id");
ALTER TABLE CAMPAIGN."MintNFTAirDrop"
    ADD CONSTRAINT MintNFTAirDrop_AirDropTxHash FOREIGN KEY ("airdropTxHash") REFERENCES BLOCKCHAIN_TRANSACTION."CampaignSendCoin" ("txHash");
ALTER TABLE CAMPAIGN."IneligibleMintNFTAirDrop"
    ADD CONSTRAINT IneligibleMintNFTAirDrop_NFT_ID FOREIGN KEY ("nftID") REFERENCES MASTER."NFT" ("id");

INSERT INTO CAMPAIGN."IneligibleMintNFTAirDrop"("nftID")
SELECT "nftId"
FROM MASTER."NFTOwner"
WHERE "ownerId" != "creatorId"
  AND "creatorId" != 'Mint.E';

ALTER TABLE MASTER."Account"
    ADD COLUMN IF NOT EXISTS "identityId" VARCHAR DEFAULT NULL UNIQUE;

ALTER TABLE MASTER."Collection"
    ADD COLUMN IF NOT EXISTS "royalty" NUMERIC NOT NULL DEFAULT 0.02;
ALTER TABLE MASTER."Collection"
    ADD COLUMN IF NOT EXISTS "isDefined" BOOLEAN DEFAULT false;
ALTER TABLE MASTER."Collection"
    ADD COLUMN IF NOT EXISTS "defineAsset" BOOLEAN NOT NULL DEFAULT false;
ALTER TABLE MASTER."Collection"
    ADD COLUMN IF NOT EXISTS "rank" INTEGER NOT NULL DEFAULT 2147483647;
ALTER TABLE MASTER."Collection"
    ADD COLUMN IF NOT EXISTS "onSecondaryMarket" BOOLEAN NOT NULL DEFAULT false;
ALTER TABLE MASTER."Collection"
    DROP COLUMN IF EXISTS "category";
ALTER TABLE MASTER."Collection"
    ADD COLUMN IF NOT EXISTS "showAll" BOOLEAN NOT NULL DEFAULT false;

ALTER TABLE MASTER."NFT"
    ALTER COLUMN "isMinted" DROP NOT NULL;
ALTER TABLE MASTER."Key"
    ADD COLUMN IF NOT EXISTS "identityIssued" BOOLEAN DEFAULT false;
ALTER TABLE MASTER."NFT"
    ADD COLUMN IF NOT EXISTS "assetId" VARCHAR DEFAULT NULL UNIQUE;
ALTER TABLE MASTER."NFT"
    ADD COLUMN IF NOT EXISTS "mintReady" BOOLEAN NOT NULL DEFAULT false;
ALTER TABLE MASTER."NFT"
    DROP COLUMN IF EXISTS "edition";
ALTER TABLE MASTER."NFT"
    DROP COLUMN IF EXISTS "ipfsLink";
ALTER TABLE MASTER."NFT"
    ALTER COLUMN "totalSupply" TYPE NUMERIC;

ALTER TABLE MASTER."NFTOwner"
    ALTER COLUMN "quantity" TYPE NUMERIC;

ALTER TABLE MASTER_TRANSACTION."CollectionDraft"
    ADD COLUMN IF NOT EXISTS "royalty" NUMERIC NOT NULL DEFAULT 0.0;
ALTER TABLE MASTER_TRANSACTION."CollectionDraft"
    DROP COLUMN IF EXISTS "category";

ALTER TABLE MASTER_TRANSACTION."PublicListingNFTTransaction"
    ADD COLUMN IF NOT EXISTS "mintOnSuccess" BOOLEAN NOT NULL DEFAULT false;
ALTER TABLE MASTER_TRANSACTION."SaleNFTTransaction"
    ADD COLUMN IF NOT EXISTS "mintOnSuccess" BOOLEAN NOT NULL DEFAULT false;

ALTER TABLE MASTER."BurntNFT"
    ADD CONSTRAINT BurntNFT_CollectionId FOREIGN KEY ("collectionId") REFERENCES MASTER."Collection" ("id");

ALTER TABLE MASTER."SecondaryMarket"
    ADD CONSTRAINT SecondaryMarket_nftId FOREIGN KEY ("nftId") REFERENCES MASTER."NFT" ("id");
ALTER TABLE MASTER."SecondaryMarket"
    ADD CONSTRAINT SecondaryMarket_collectionId FOREIGN KEY ("collectionId") REFERENCES MASTER."Collection" ("id");
ALTER TABLE MASTER."SecondaryMarket"
    ADD CONSTRAINT SecondaryMarket_sellerId FOREIGN KEY ("sellerId") REFERENCES MASTER."Account" ("id");

ALTER TABLE MASTER_TRANSACTION."CancelOrderTransaction"
    ADD CONSTRAINT CancelOrderTransaction_TxHash FOREIGN KEY ("txHash") REFERENCES BLOCKCHAIN_TRANSACTION."CancelOrder" ("txHash");
ALTER TABLE MASTER_TRANSACTION."CancelOrderTransaction"
    ADD CONSTRAINT CancelOrderTransaction_SellerId FOREIGN KEY ("sellerId") REFERENCES MASTER."Account" ("id");

ALTER TABLE MASTER_TRANSACTION."DefineAssetTransaction"
    ADD CONSTRAINT DefineAssetTransaction_TxHash FOREIGN KEY ("txHash") REFERENCES BLOCKCHAIN_TRANSACTION."DefineAsset" ("txHash");
ALTER TABLE MASTER_TRANSACTION."DefineAssetTransaction"
    ADD CONSTRAINT DefineAssetTransaction_CollectionId FOREIGN KEY ("collectionId") REFERENCES MASTER."Collection" ("id");

ALTER TABLE MASTER_TRANSACTION."ExternalAsset"
    ADD CONSTRAINT ExternalAsset_NFTId FOREIGN KEY ("nftId") REFERENCES MASTER."NFT" ("id");
ALTER TABLE MASTER_TRANSACTION."ExternalAsset"
    ADD CONSTRAINT ExternalAsset_LastOwnerId FOREIGN KEY ("lastOwnerId") REFERENCES MASTER."Account" ("id");
ALTER TABLE MASTER_TRANSACTION."ExternalAsset"
    ADD CONSTRAINT ExternalAsset_CollectionId FOREIGN KEY ("collectionId") REFERENCES MASTER."Collection" ("id");

ALTER TABLE MASTER_TRANSACTION."IssueIdentityTransaction"
    ADD CONSTRAINT IssueIdentityTransaction_TxHash FOREIGN KEY ("txHash") REFERENCES BLOCKCHAIN_TRANSACTION."IssueIdentity" ("txHash");
ALTER TABLE MASTER_TRANSACTION."IssueIdentityTransaction"
    ADD CONSTRAINT IssueIdentityTransaction_AccountId FOREIGN KEY ("accountId") REFERENCES MASTER."Account" ("id");

ALTER TABLE MASTER_TRANSACTION."MintAssetTransaction"
    ADD CONSTRAINT MintAssetTransaction_TxHash FOREIGN KEY ("txHash") REFERENCES BLOCKCHAIN_TRANSACTION."MintAsset" ("txHash");
ALTER TABLE MASTER_TRANSACTION."MintAssetTransaction"
    ADD CONSTRAINT MintAssetTransaction_NFTId FOREIGN KEY ("nftID") REFERENCES MASTER."NFT" ("id");
ALTER TABLE MASTER_TRANSACTION."MintAssetTransaction"
    ADD CONSTRAINT MintAssetTransaction_MinterAccountId FOREIGN KEY ("minterAccountID") REFERENCES MASTER."Account" ("id");

ALTER TABLE MASTER_TRANSACTION."NFTMintingFeeTransaction"
    ADD CONSTRAINT NFTMintingFeeTransaction_TxHash FOREIGN KEY ("txHash") REFERENCES BLOCKCHAIN_TRANSACTION."NFTMintingFee" ("txHash");
ALTER TABLE MASTER_TRANSACTION."NFTMintingFeeTransaction"
    ADD CONSTRAINT NFTMintingFeeTransaction_AccountId FOREIGN KEY ("accountId") REFERENCES MASTER."Account" ("id");

ALTER TABLE MASTER_TRANSACTION."ProvisionAddressTransaction"
    ADD CONSTRAINT ProvisionAddressTransaction_TxHash FOREIGN KEY ("txHash") REFERENCES BLOCKCHAIN_TRANSACTION."ProvisionAddress" ("txHash");
ALTER TABLE MASTER_TRANSACTION."ProvisionAddressTransaction"
    ADD CONSTRAINT ProvisionAddressTransaction_AccountId FOREIGN KEY ("accountId") REFERENCES MASTER."Account" ("id");

ALTER TABLE MASTER_TRANSACTION."UnprovisionAddressTransaction"
    ADD CONSTRAINT UnprovisionAddressTransaction_TxHash FOREIGN KEY ("txHash") REFERENCES BLOCKCHAIN_TRANSACTION."UnprovisionAddress" ("txHash");
ALTER TABLE MASTER_TRANSACTION."UnprovisionAddressTransaction"
    ADD CONSTRAINT UnprovisionAddressTransaction_AccountId FOREIGN KEY ("accountId") REFERENCES MASTER."Account" ("id");

ALTER TABLE MASTER_TRANSACTION."MakeOrderTransaction"
    ADD CONSTRAINT MakeOrderTransaction_BuyerAccountId FOREIGN KEY ("buyerId") REFERENCES MASTER."Account" ("id");
ALTER TABLE MASTER_TRANSACTION."MakeOrderTransaction"
    ADD CONSTRAINT MakeOrderTransaction_sellerId FOREIGN KEY ("sellerId") REFERENCES MASTER."Account" ("id");
ALTER TABLE MASTER_TRANSACTION."MakeOrderTransaction"
    ADD CONSTRAINT MakeOrderTransaction_TxHash FOREIGN KEY ("txHash") REFERENCES BLOCKCHAIN_TRANSACTION."MakeOrder" ("txHash");

ALTER TABLE MASTER_TRANSACTION."NFTTransferTransaction"
    ADD CONSTRAINT NFTTransferTransaction_OwnerId FOREIGN KEY ("ownerId") REFERENCES MASTER."Account" ("id");
ALTER TABLE MASTER_TRANSACTION."NFTTransferTransaction"
    ADD CONSTRAINT NFTTransferTransaction_ToAccountId FOREIGN KEY ("toAccountId") REFERENCES MASTER."Account" ("id");
ALTER TABLE MASTER_TRANSACTION."NFTTransferTransaction"
    ADD CONSTRAINT NFTTransferTransaction_TxHash FOREIGN KEY ("txHash") REFERENCES BLOCKCHAIN_TRANSACTION."SplitSend" ("txHash");

ALTER TABLE MASTER_TRANSACTION."TakeOrderTransaction"
    ADD CONSTRAINT TakeOrderTransaction_BuyerAccountId FOREIGN KEY ("buyerId") REFERENCES MASTER."Account" ("id");
ALTER TABLE MASTER_TRANSACTION."TakeOrderTransaction"
    ADD CONSTRAINT TakeOrderTransaction_TxHash FOREIGN KEY ("txHash") REFERENCES BLOCKCHAIN_TRANSACTION."TakeOrder" ("txHash");

ALTER TABLE MASTER_TRANSACTION."UnwrapTransaction"
    ADD CONSTRAINT UnwrapTransaction_AccountId FOREIGN KEY ("accountId") REFERENCES MASTER."Account" ("id");
ALTER TABLE MASTER_TRANSACTION."UnwrapTransaction"
    ADD CONSTRAINT UnwrapTransaction_TxHash FOREIGN KEY ("txHash") REFERENCES BLOCKCHAIN_TRANSACTION."Unwrap" ("txHash");

ALTER TABLE MASTER_TRANSACTION."WrapTransaction"
    ADD CONSTRAINT WrapTransaction_AccountId FOREIGN KEY ("accountId") REFERENCES MASTER."Account" ("id");
ALTER TABLE MASTER_TRANSACTION."WrapTransaction"
    ADD CONSTRAINT WrapTransaction_TxHash FOREIGN KEY ("txHash") REFERENCES BLOCKCHAIN_TRANSACTION."Wrap" ("txHash");

CREATE TRIGGER BT_CANCEL_ORDER_LOG
    BEFORE INSERT OR UPDATE
    ON BLOCKCHAIN_TRANSACTION."CancelOrder"
    FOR EACH ROW
EXECUTE PROCEDURE PUBLIC.INSERT_OR_UPDATE_EPOCH_LOG();
CREATE TRIGGER BT_CAMPAIGN_SEND_COIN_LOG
    BEFORE INSERT OR UPDATE
    ON BLOCKCHAIN_TRANSACTION."CampaignSendCoin"
    FOR EACH ROW
EXECUTE PROCEDURE PUBLIC.INSERT_OR_UPDATE_EPOCH_LOG();
CREATE TRIGGER BT_DEFINE_ASSET_LOG
    BEFORE INSERT OR UPDATE
    ON BLOCKCHAIN_TRANSACTION."DefineAsset"
    FOR EACH ROW
EXECUTE PROCEDURE PUBLIC.INSERT_OR_UPDATE_EPOCH_LOG();
CREATE TRIGGER BT_ISSUE_IDENTITY_LOG
    BEFORE INSERT OR UPDATE
    ON BLOCKCHAIN_TRANSACTION."IssueIdentity"
    FOR EACH ROW
EXECUTE PROCEDURE PUBLIC.INSERT_OR_UPDATE_EPOCH_LOG();
CREATE TRIGGER BT_MINT_ASSET_LOG
    BEFORE INSERT OR UPDATE
    ON BLOCKCHAIN_TRANSACTION."MintAsset"
    FOR EACH ROW
EXECUTE PROCEDURE PUBLIC.INSERT_OR_UPDATE_EPOCH_LOG();
CREATE TRIGGER BT_NFT_MINTING_FEE_LOG
    BEFORE INSERT OR UPDATE
    ON BLOCKCHAIN_TRANSACTION."NFTMintingFee"
    FOR EACH ROW
EXECUTE PROCEDURE PUBLIC.INSERT_OR_UPDATE_EPOCH_LOG();
CREATE TRIGGER BT_PROVOSION_ADDRESS_LOG
    BEFORE INSERT OR UPDATE
    ON BLOCKCHAIN_TRANSACTION."ProvisionAddress"
    FOR EACH ROW
EXECUTE PROCEDURE PUBLIC.INSERT_OR_UPDATE_EPOCH_LOG();
CREATE TRIGGER BT_MAKE_ORDER_LOG
    BEFORE INSERT OR UPDATE
    ON BLOCKCHAIN_TRANSACTION."MakeOrder"
    FOR EACH ROW
EXECUTE PROCEDURE PUBLIC.INSERT_OR_UPDATE_EPOCH_LOG();
CREATE TRIGGER BT_SPLIT_SEND_LOG
    BEFORE INSERT OR UPDATE
    ON BLOCKCHAIN_TRANSACTION."SplitSend"
    FOR EACH ROW
EXECUTE PROCEDURE PUBLIC.INSERT_OR_UPDATE_EPOCH_LOG();
CREATE TRIGGER BT_TAKE_ORDER_LOG
    BEFORE INSERT OR UPDATE
    ON BLOCKCHAIN_TRANSACTION."TakeOrder"
    FOR EACH ROW
EXECUTE PROCEDURE PUBLIC.INSERT_OR_UPDATE_EPOCH_LOG();
CREATE TRIGGER BT_UNPROVOSION_ADDRESS_LOG
    BEFORE INSERT OR UPDATE
    ON BLOCKCHAIN_TRANSACTION."UnprovisionAddress"
    FOR EACH ROW
EXECUTE PROCEDURE PUBLIC.INSERT_OR_UPDATE_EPOCH_LOG();
CREATE TRIGGER BT_UNWRAP_LOG
    BEFORE INSERT OR UPDATE
    ON BLOCKCHAIN_TRANSACTION."Unwrap"
    FOR EACH ROW
EXECUTE PROCEDURE PUBLIC.INSERT_OR_UPDATE_EPOCH_LOG();
CREATE TRIGGER BT_WRAP_LOG
    BEFORE INSERT OR UPDATE
    ON BLOCKCHAIN_TRANSACTION."Wrap"
    FOR EACH ROW
EXECUTE PROCEDURE PUBLIC.INSERT_OR_UPDATE_EPOCH_LOG();

CREATE TRIGGER MINT_NFT_AIRDROP_LOG
    BEFORE INSERT OR UPDATE
    ON CAMPAIGN."MintNFTAirDrop"
    FOR EACH ROW
EXECUTE PROCEDURE PUBLIC.INSERT_OR_UPDATE_EPOCH_LOG();
CREATE TRIGGER INELIGIBLE_MINT_NFT_AIRDROP_LOG
    BEFORE INSERT OR UPDATE
    ON CAMPAIGN."IneligibleMintNFTAirDrop"
    FOR EACH ROW
EXECUTE PROCEDURE PUBLIC.INSERT_OR_UPDATE_EPOCH_LOG();

CREATE TRIGGER SECONDARY_MARKET_HISTORY_LOG
    BEFORE INSERT OR UPDATE
    ON HISTORY."MasterSecondaryMarket"
    FOR EACH ROW
EXECUTE PROCEDURE PUBLIC.INSERT_OR_UPDATE_HISTORY_EPOCH_LOG();

CREATE TRIGGER BURNT_NFT_LOG
    BEFORE INSERT OR UPDATE
    ON MASTER."BurntNFT"
    FOR EACH ROW
EXECUTE PROCEDURE PUBLIC.INSERT_OR_UPDATE_EPOCH_LOG();
CREATE TRIGGER SECONDARY_MARKET_LOG
    BEFORE INSERT OR UPDATE
    ON MASTER."SecondaryMarket"
    FOR EACH ROW
EXECUTE PROCEDURE PUBLIC.INSERT_OR_UPDATE_EPOCH_LOG();

CREATE TRIGGER CANCEL_ORDER_TRANSACTION_LOG
    BEFORE INSERT OR UPDATE
    ON MASTER_TRANSACTION."CancelOrderTransaction"
    FOR EACH ROW
EXECUTE PROCEDURE PUBLIC.INSERT_OR_UPDATE_EPOCH_LOG();
CREATE TRIGGER DEFINE_ASSET_TRANSACTION_LOG
    BEFORE INSERT OR UPDATE
    ON MASTER_TRANSACTION."DefineAssetTransaction"
    FOR EACH ROW
EXECUTE PROCEDURE PUBLIC.INSERT_OR_UPDATE_EPOCH_LOG();
CREATE TRIGGER EXTERNAL_ASSET_LOG
    BEFORE INSERT OR UPDATE
    ON MASTER_TRANSACTION."ExternalAsset"
    FOR EACH ROW
EXECUTE PROCEDURE PUBLIC.INSERT_OR_UPDATE_EPOCH_LOG();
CREATE TRIGGER ISSUE_IDENTITY_TRANSACTION_LOG
    BEFORE INSERT OR UPDATE
    ON MASTER_TRANSACTION."IssueIdentityTransaction"
    FOR EACH ROW
EXECUTE PROCEDURE PUBLIC.INSERT_OR_UPDATE_EPOCH_LOG();
CREATE TRIGGER LATEST_BLOCK_LOG
    BEFORE INSERT OR UPDATE
    ON MASTER_TRANSACTION."LatestBlock"
    FOR EACH ROW
EXECUTE PROCEDURE PUBLIC.INSERT_OR_UPDATE_EPOCH_LOG();
CREATE TRIGGER MINT_ASSET_TRANSACTION_LOG
    BEFORE INSERT OR UPDATE
    ON MASTER_TRANSACTION."MintAssetTransaction"
    FOR EACH ROW
EXECUTE PROCEDURE PUBLIC.INSERT_OR_UPDATE_EPOCH_LOG();
CREATE TRIGGER MAKE_ORDER_TRANSACTION_LOG
    BEFORE INSERT OR UPDATE
    ON MASTER_TRANSACTION."MakeOrderTransaction"
    FOR EACH ROW
EXECUTE PROCEDURE PUBLIC.INSERT_OR_UPDATE_EPOCH_LOG();
CREATE TRIGGER NFT_MINTING_FEE_TRANSACTION_LOG
    BEFORE INSERT OR UPDATE
    ON MASTER_TRANSACTION."NFTMintingFeeTransaction"
    FOR EACH ROW
EXECUTE PROCEDURE PUBLIC.INSERT_OR_UPDATE_EPOCH_LOG();
CREATE TRIGGER NFT_TRANSFER_TRANSACTION_LOG
    BEFORE INSERT OR UPDATE
    ON MASTER_TRANSACTION."NFTTransferTransaction"
    FOR EACH ROW
EXECUTE PROCEDURE PUBLIC.INSERT_OR_UPDATE_EPOCH_LOG();
CREATE TRIGGER PROVISION_ADDRESS_TRANSACTION_LOG
    BEFORE INSERT OR UPDATE
    ON MASTER_TRANSACTION."ProvisionAddressTransaction"
    FOR EACH ROW
EXECUTE PROCEDURE PUBLIC.INSERT_OR_UPDATE_EPOCH_LOG();
CREATE TRIGGER TAKE_ORDER_TRANSACTION_LOG
    BEFORE INSERT OR UPDATE
    ON MASTER_TRANSACTION."TakeOrderTransaction"
    FOR EACH ROW
EXECUTE PROCEDURE PUBLIC.INSERT_OR_UPDATE_EPOCH_LOG();
CREATE TRIGGER UNPROVISION_ADDRESS_TRANSACTION_LOG
    BEFORE INSERT OR UPDATE
    ON MASTER_TRANSACTION."UnprovisionAddressTransaction"
    FOR EACH ROW
EXECUTE PROCEDURE PUBLIC.INSERT_OR_UPDATE_EPOCH_LOG();
CREATE TRIGGER UNWRAP_TRANSACTION_LOG
    BEFORE INSERT OR UPDATE
    ON MASTER_TRANSACTION."UnwrapTransaction"
    FOR EACH ROW
EXECUTE PROCEDURE PUBLIC.INSERT_OR_UPDATE_EPOCH_LOG();
CREATE TRIGGER WRAP_TRANSACTION_LOG
    BEFORE INSERT OR UPDATE
    ON MASTER_TRANSACTION."WrapTransaction"
    FOR EACH ROW
EXECUTE PROCEDURE PUBLIC.INSERT_OR_UPDATE_EPOCH_LOG();

CREATE OR REPLACE FUNCTION MASTER.KEY_VALIDATE() RETURNS TRIGGER AS
$$
BEGIN
    IF (TG_OP = 'INSERT') THEN
        IF (EXISTS(SELECT * FROM MASTER."Key" WHERE "accountId" = new."accountId" AND "active" = true)
            AND new."active" = true) THEN
            RAISE EXCEPTION 'MULTIPLE_ACTIVE_KEYS';;
        END IF;;
    ELSEIF (TG_OP = 'UPDATE') THEN
        -- allow all keys to be in false state to change active state
        IF (EXISTS(SELECT *
                   FROM MASTER."Key"
                   WHERE "accountId" = new."accountId"
                     AND "address" = new."address"
                     AND "active" = false) AND
            new."active" = true AND
            EXISTS(SELECT * FROM MASTER."Key" WHERE "accountId" = new."accountId" AND "active" = true)) THEN
            RAISE EXCEPTION 'MULTIPLE_ACTIVE_KEYS';;
        END IF;;
    END IF;;
    RETURN NEW;;
END;;
$$ LANGUAGE PLPGSQL;

CREATE TRIGGER KEY_VALID
    BEFORE INSERT OR UPDATE
    ON MASTER."Key"
    FOR EACH ROW
EXECUTE PROCEDURE MASTER.KEY_VALIDATE();

# --- !Downs
DROP TRIGGER IF EXISTS BT_CANCEL_ORDER_LOG ON BLOCKCHAIN_TRANSACTION."CancelOrder" CASCADE;
DROP TRIGGER IF EXISTS BT_CAMPAIGN_SEND_COIN_LOG ON BLOCKCHAIN_TRANSACTION."CampaignSendCoin" CASCADE;
DROP TRIGGER IF EXISTS BT_DEFINE_ASSET_LOG ON BLOCKCHAIN_TRANSACTION."DefineAsset" CASCADE;
DROP TRIGGER IF EXISTS BT_ISSUE_IDENTITY_LOG ON BLOCKCHAIN_TRANSACTION."IssueIdentity" CASCADE;
DROP TRIGGER IF EXISTS BT_MINT_ASSET_LOG ON BLOCKCHAIN_TRANSACTION."MintAsset" CASCADE;
DROP TRIGGER IF EXISTS BT_PROVOSION_ADDRESS_LOG ON BLOCKCHAIN_TRANSACTION."ProvisionAddress" CASCADE;
DROP TRIGGER IF EXISTS BT_NFT_MINTING_FEE_LOG ON BLOCKCHAIN_TRANSACTION."NFTMintingFee" CASCADE;
DROP TRIGGER IF EXISTS BT_MAKE_ORDER_LOG ON BLOCKCHAIN_TRANSACTION."MakeOrder" CASCADE;
DROP TRIGGER IF EXISTS BT_SPLIT_SEND_LOG ON BLOCKCHAIN_TRANSACTION."SplitSend" CASCADE;
DROP TRIGGER IF EXISTS BT_TAKE_ORDER_LOG ON BLOCKCHAIN_TRANSACTION."TakeOrder" CASCADE;
DROP TRIGGER IF EXISTS BT_UNPROVOSION_ADDRESS_LOG ON BLOCKCHAIN_TRANSACTION."UnprovisionAddress" CASCADE;
DROP TRIGGER IF EXISTS BT_UNWRAP_LOG ON BLOCKCHAIN_TRANSACTION."Unwrap" CASCADE;
DROP TRIGGER IF EXISTS BT_WRAP_LOG ON BLOCKCHAIN_TRANSACTION."Wrap" CASCADE;

DROP TRIGGER IF EXISTS MINT_NFT_AIRDROP_LOG ON CAMPAIGN."MintNFTAirDrop" CASCADE;
DROP TRIGGER IF EXISTS INELIGIBLE_MINT_NFT_AIRDROP_LOG ON CAMPAIGN."IneligibleMintNFTAirDrop" CASCADE;

DROP TRIGGER IF EXISTS SECONDARY_MARKET_HISTORY_LOG ON HISTORY."MasterSecondaryMarket" CASCADE;

DROP TRIGGER IF EXISTS SECONDARY_MARKET_LOG ON MASTER."SecondaryMarket" CASCADE;
DROP TRIGGER IF EXISTS BURNT_NFT_LOG ON MASTER."BurntNFT" CASCADE;

DROP TRIGGER IF EXISTS DEFINE_ASSET_TRANSACTION_LOG ON MASTER_TRANSACTION."CancelOrderTransaction" CASCADE;
DROP TRIGGER IF EXISTS DEFINE_ASSET_TRANSACTION_LOG ON MASTER_TRANSACTION."DefineAssetTransaction" CASCADE;
DROP TRIGGER IF EXISTS EXTERNAL_ASSET_LOG ON MASTER_TRANSACTION."ExternalAsset" CASCADE;
DROP TRIGGER IF EXISTS ISSUE_IDENTITY_TRANSACTION_LOG ON MASTER_TRANSACTION."IssueIdentityTransaction" CASCADE;
DROP TRIGGER IF EXISTS LATEST_BLOCK_LOG ON MASTER_TRANSACTION."LatestBlock" CASCADE;
DROP TRIGGER IF EXISTS MINT_ASSET_TRANSACTION_LOG ON MASTER_TRANSACTION."MintAssetTransaction" CASCADE;
DROP TRIGGER IF EXISTS NFT_MINTING_FEE_TRANSACTION_LOG ON MASTER_TRANSACTION."NFTMintingFeeTransaction" CASCADE;
DROP TRIGGER IF EXISTS NFT_TRANSFER_TRANSACTION_LOG ON MASTER_TRANSACTION."NFTTransferTransaction" CASCADE;
DROP TRIGGER IF EXISTS PROVISION_ADDRESS_TRANSACTION_LOG ON MASTER_TRANSACTION."ProvisionAddressTransaction" CASCADE;
DROP TRIGGER IF EXISTS UNPROVISION_ADDRESS_TRANSACTION_LOG ON MASTER_TRANSACTION."UnprovisionAddressTransaction" CASCADE;
DROP TRIGGER IF EXISTS MAKE_ORDER_TRANSACTION_LOG ON MASTER_TRANSACTION."MakeOrderTransaction" CASCADE;
DROP TRIGGER IF EXISTS TAKE_ORDER_TRANSACTION_LOG ON MASTER_TRANSACTION."TakeOrderTransaction" CASCADE;
DROP TRIGGER IF EXISTS UNWRAP_TRANSACTION_LOG ON MASTER_TRANSACTION."UnwrapTransaction" CASCADE;
DROP TRIGGER IF EXISTS WRAP_TRANSACTION_LOG ON MASTER_TRANSACTION."WrapTransaction" CASCADE;

DROP TABLE IF EXISTS BLOCKCHAIN_TRANSACTION."CancelOrder" CASCADE;
DROP TABLE IF EXISTS BLOCKCHAIN_TRANSACTION."CampaignSendCoin" CASCADE;
DROP TABLE IF EXISTS BLOCKCHAIN_TRANSACTION."DefineAsset" CASCADE;
DROP TABLE IF EXISTS BLOCKCHAIN_TRANSACTION."IssueIdentity" CASCADE;
DROP TABLE IF EXISTS BLOCKCHAIN_TRANSACTION."MintAsset" CASCADE;
DROP TABLE IF EXISTS BLOCKCHAIN_TRANSACTION."NFTMintingFee" CASCADE;
DROP TABLE IF EXISTS BLOCKCHAIN_TRANSACTION."ProvisionAddress" CASCADE;
DROP TABLE IF EXISTS BLOCKCHAIN_TRANSACTION."SplitSend" CASCADE;
DROP TABLE IF EXISTS BLOCKCHAIN_TRANSACTION."TakeOrder" CASCADE;
DROP TABLE IF EXISTS BLOCKCHAIN_TRANSACTION."UnprovisionAddress" CASCADE;
DROP TABLE IF EXISTS BLOCKCHAIN_TRANSACTION."Unwrap" CASCADE;
DROP TABLE IF EXISTS BLOCKCHAIN_TRANSACTION."Wrap" CASCADE;

DROP TABLE IF EXISTS CAMPAIGN."MintNFTAirDrop" CASCADE;
DROP TABLE IF EXISTS CAMPAIGN."IneligibleMintNFTAirDrop" CASCADE;

DROP TABLE IF EXISTS HISTORY."MasterSecondaryMarket" CASCADE;

DROP TABLE IF EXISTS MASTER."BurntNFT" CASCADE;
DROP TABLE IF EXISTS MASTER."SecondaryMarket" CASCADE;

DROP TABLE IF EXISTS MASTER_TRANSACTION."CancelOrderTransaction" CASCADE;
DROP TABLE IF EXISTS MASTER_TRANSACTION."DefineAssetTransaction" CASCADE;
DROP TABLE IF EXISTS MASTER_TRANSACTION."ExternalAsset" CASCADE;
DROP TABLE IF EXISTS MASTER_TRANSACTION."LatestBlock" CASCADE;
DROP TABLE IF EXISTS MASTER_TRANSACTION."MakeOrderTransaction" CASCADE;
DROP TABLE IF EXISTS MASTER_TRANSACTION."MintAssetTransaction" CASCADE;
DROP TABLE IF EXISTS MASTER_TRANSACTION."NFTMintingFeeTransaction" CASCADE;
DROP TABLE IF EXISTS MASTER_TRANSACTION."NFTTransferTransaction" CASCADE;
DROP TABLE IF EXISTS MASTER_TRANSACTION."IssueIdentityTransaction" CASCADE;
DROP TABLE IF EXISTS MASTER_TRANSACTION."ProvisionAddressTransaction" CASCADE;
DROP TABLE IF EXISTS MASTER_TRANSACTION."TakeOrderTransaction" CASCADE;
DROP TABLE IF EXISTS MASTER_TRANSACTION."UnprovisionAddressTransaction" CASCADE;
DROP TABLE IF EXISTS MASTER_TRANSACTION."UnwrapTransaction" CASCADE;
DROP TABLE IF EXISTS MASTER_TRANSACTION."WrapTransaction" CASCADE;

DROP SCHEMA IF EXISTS CAMPAIGN CASCADE;
