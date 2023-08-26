# --- !Ups
CREATE SCHEMA IF NOT EXISTS CAMPAIGN
    AUTHORIZATION "mantlePlace";

ALTER TABLE BLOCKCHAIN_TRANSACTION."NFTPublicListing"
    DROP COLUMN IF EXISTS "txRawBytes";
ALTER TABLE BLOCKCHAIN_TRANSACTION."NFTSale"
    DROP COLUMN IF EXISTS "txRawBytes";
ALTER TABLE BLOCKCHAIN_TRANSACTION."SendCoin"
    DROP COLUMN IF EXISTS "txRawBytes";

ALTER TABLE MASTER_TRANSACTION."PublicListingNFTTransaction"
    DROP CONSTRAINT IF EXISTS "PublicListingNFTTransaction_buyerAccountId_sellerAccountId__key";

ALTER TABLE MASTER_TRANSACTION."SaleNFTTransaction"
    DROP CONSTRAINT IF EXISTS "BuyNFTTransaction_buyerAccountId_sellerAccountId_txHash_nft_key";

CREATE TABLE IF NOT EXISTS BLOCKCHAIN_TRANSACTION."AdminTransaction"
(
    "txHash"               VARCHAR NOT NULL,
    "fromAddress"          VARCHAR NOT NULL,
    "status"               BOOLEAN,
    "memo"                 VARCHAR,
    "timeoutHeight"        INTEGER NOT NULL,
    "log"                  VARCHAR,
    "txHeight"             INTEGER,
    "txType"               VARCHAR NOT NULL,
    "createdBy"            VARCHAR,
    "createdOnMillisEpoch" BIGINT,
    "updatedBy"            VARCHAR,
    "updatedOnMillisEpoch" BIGINT,
    PRIMARY KEY ("txHash")
);

CREATE TABLE IF NOT EXISTS BLOCKCHAIN_TRANSACTION."UserTransaction"
(
    "txHash"               VARCHAR NOT NULL,
    "accountId"            VARCHAR NOT NULL,
    "fromAddress"          VARCHAR NOT NULL,
    "status"               BOOLEAN,
    "memo"                 VARCHAR,
    "timeoutHeight"        INTEGER NOT NULL,
    "log"                  VARCHAR,
    "txHeight"             INTEGER,
    "txType"               VARCHAR NOT NULL,
    "createdBy"            VARCHAR,
    "createdOnMillisEpoch" BIGINT,
    "updatedBy"            VARCHAR,
    "updatedOnMillisEpoch" BIGINT,
    PRIMARY KEY ("txHash"),
    UNIQUE ("accountId", "txHash")
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
    "status"               BOOLEAN,
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
    "status"               BOOLEAN,
    "createdBy"            VARCHAR,
    "createdOnMillisEpoch" BIGINT,
    "updatedBy"            VARCHAR,
    "updatedOnMillisEpoch" BIGINT,
    PRIMARY KEY ("id"),
    UNIQUE ("nftId", "id"),
    UNIQUE ("sellerId", "id")
);

CREATE TABLE IF NOT EXISTS MASTER."Secret"
(
    "id"                   VARCHAR NOT NULL,
    "secret"               VARCHAR NOT NULL,
    "salt"                 VARCHAR NOT NULL,
    "createdBy"            VARCHAR,
    "createdOnMillisEpoch" BIGINT,
    "updatedBy"            VARCHAR,
    "updatedOnMillisEpoch" BIGINT,
    PRIMARY KEY ("id")
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
    PRIMARY KEY ("txHash")
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
    "assetId"                VARCHAR NOT NULL,
    "currentOwnerIdentityId" VARCHAR NOT NULL,
    "nftId"                  VARCHAR NOT NULL,
    "lastOwnerId"            VARCHAR NOT NULL,
    "collectionId"           VARCHAR NOT NULL,
    "amount"                 NUMERIC NOT NULL,
    "createdBy"              VARCHAR,
    "createdOnMillisEpoch"   BIGINT,
    "updatedBy"              VARCHAR,
    "updatedOnMillisEpoch"   BIGINT,
    PRIMARY KEY ("assetId", "currentOwnerIdentityId")
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
    PRIMARY KEY ("txHash"),
    UNIQUE ("nftId", "secondaryMarketId")
);

CREATE TABLE IF NOT EXISTS MASTER_TRANSACTION."MintAssetTransaction"
(
    "txHash"               VARCHAR NOT NULL,
    "nftID"                VARCHAR NOT NULL,
    "toAccountID"          VARCHAR NOT NULL,
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
    PRIMARY KEY ("txHash")
);

CREATE TABLE IF NOT EXISTS MASTER_TRANSACTION."NFTTransferTransaction"
(
    "txHash"               VARCHAR NOT NULL,
    "nftId"                VARCHAR NOT NULL,
    "fromId"               VARCHAR NOT NULL,
    "quantity"             INTEGER NOT NULL,
    "toIdentityId"         VARCHAR NOT NULL,
    "toAccountId"          VARCHAR,
    "status"               BOOLEAN,
    "createdBy"            VARCHAR,
    "createdOnMillisEpoch" BIGINT,
    "updatedBy"            VARCHAR,
    "updatedOnMillisEpoch" BIGINT,
    PRIMARY KEY ("txHash")
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
    PRIMARY KEY ("txHash")
);

CREATE TABLE IF NOT EXISTS MASTER_TRANSACTION."SendCoinTransaction"
(
    "txHash"               VARCHAR NOT NULL,
    "fromAccountId"        VARCHAR NOT NULL,
    "toAddress"            VARCHAR NOT NULL,
    "amount"               VARCHAR NOT NULL,
    "toAccountId"          VARCHAR,
    "status"               BOOLEAN,
    "createdBy"            VARCHAR,
    "createdOnMillisEpoch" BIGINT,
    "updatedBy"            VARCHAR,
    "updatedOnMillisEpoch" BIGINT,
    PRIMARY KEY ("txHash")
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
    PRIMARY KEY ("txHash")
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
    PRIMARY KEY ("txHash")
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
    PRIMARY KEY ("txHash")
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
    PRIMARY KEY ("txHash")
);

ALTER TABLE ANALYTICS."CollectionAnalysis"
    ADD COLUMN IF NOT EXISTS "totalBurnt" BIGINT NOT NULL DEFAULT 0;

ALTER TABLE CAMPAIGN."MintNFTAirDrop"
    ADD CONSTRAINT MintNFTAirDrop_AccountId FOREIGN KEY ("accountId") REFERENCES MASTER."Account" ("id");
ALTER TABLE CAMPAIGN."MintNFTAirDrop"
    ADD CONSTRAINT MintNFTAirDrop_AirDropTxHash FOREIGN KEY ("airdropTxHash") REFERENCES BLOCKCHAIN_TRANSACTION."AdminTransaction" ("txHash");
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

ALTER TABLE BLOCKCHAIN_TRANSACTION."UserTransaction"
    ADD CONSTRAINT UserTransaction_AccountId FOREIGN KEY ("accountId") REFERENCES MASTER."Account" ("id");

ALTER TABLE MASTER."BurntNFT"
    ADD CONSTRAINT BurntNFT_CollectionId FOREIGN KEY ("collectionId") REFERENCES MASTER."Collection" ("id");

ALTER TABLE MASTER."SecondaryMarket"
    ADD CONSTRAINT SecondaryMarket_nftId FOREIGN KEY ("nftId") REFERENCES MASTER."NFT" ("id");
ALTER TABLE MASTER."SecondaryMarket"
    ADD CONSTRAINT SecondaryMarket_collectionId FOREIGN KEY ("collectionId") REFERENCES MASTER."Collection" ("id");
ALTER TABLE MASTER."SecondaryMarket"
    ADD CONSTRAINT SecondaryMarket_sellerId FOREIGN KEY ("sellerId") REFERENCES MASTER."Account" ("id");

ALTER TABLE MASTER_TRANSACTION."CancelOrderTransaction"
    ADD CONSTRAINT CancelOrderTransaction_TxHash FOREIGN KEY ("txHash") REFERENCES BLOCKCHAIN_TRANSACTION."UserTransaction" ("txHash");
ALTER TABLE MASTER_TRANSACTION."CancelOrderTransaction"
    ADD CONSTRAINT CancelOrderTransaction_SellerId FOREIGN KEY ("sellerId") REFERENCES MASTER."Account" ("id");

ALTER TABLE MASTER_TRANSACTION."DefineAssetTransaction"
    ADD CONSTRAINT DefineAssetTransaction_TxHash FOREIGN KEY ("txHash") REFERENCES BLOCKCHAIN_TRANSACTION."AdminTransaction" ("txHash");
ALTER TABLE MASTER_TRANSACTION."DefineAssetTransaction"
    ADD CONSTRAINT DefineAssetTransaction_CollectionId FOREIGN KEY ("collectionId") REFERENCES MASTER."Collection" ("id");

ALTER TABLE MASTER_TRANSACTION."ExternalAsset"
    ADD CONSTRAINT ExternalAsset_NFTId FOREIGN KEY ("nftId") REFERENCES MASTER."NFT" ("id");
ALTER TABLE MASTER_TRANSACTION."ExternalAsset"
    ADD CONSTRAINT ExternalAsset_LastOwnerId FOREIGN KEY ("lastOwnerId") REFERENCES MASTER."Account" ("id");
ALTER TABLE MASTER_TRANSACTION."ExternalAsset"
    ADD CONSTRAINT ExternalAsset_CollectionId FOREIGN KEY ("collectionId") REFERENCES MASTER."Collection" ("id");

ALTER TABLE MASTER_TRANSACTION."IssueIdentityTransaction"
    ADD CONSTRAINT IssueIdentityTransaction_TxHash FOREIGN KEY ("txHash") REFERENCES BLOCKCHAIN_TRANSACTION."AdminTransaction" ("txHash");
ALTER TABLE MASTER_TRANSACTION."IssueIdentityTransaction"
    ADD CONSTRAINT IssueIdentityTransaction_AccountId FOREIGN KEY ("accountId") REFERENCES MASTER."Account" ("id");

ALTER TABLE MASTER_TRANSACTION."MintAssetTransaction"
    ADD CONSTRAINT MintAssetTransaction_TxHash FOREIGN KEY ("txHash") REFERENCES BLOCKCHAIN_TRANSACTION."AdminTransaction" ("txHash");
ALTER TABLE MASTER_TRANSACTION."MintAssetTransaction"
    ADD CONSTRAINT MintAssetTransaction_NFTId FOREIGN KEY ("nftID") REFERENCES MASTER."NFT" ("id");
ALTER TABLE MASTER_TRANSACTION."MintAssetTransaction"
    ADD CONSTRAINT MintAssetTransaction_ToAccountID FOREIGN KEY ("toAccountID") REFERENCES MASTER."Account" ("id");

ALTER TABLE MASTER_TRANSACTION."NFTMintingFeeTransaction"
    ADD CONSTRAINT NFTMintingFeeTransaction_TxHash FOREIGN KEY ("txHash") REFERENCES BLOCKCHAIN_TRANSACTION."UserTransaction" ("txHash");
ALTER TABLE MASTER_TRANSACTION."NFTMintingFeeTransaction"
    ADD CONSTRAINT NFTMintingFeeTransaction_AccountId FOREIGN KEY ("accountId") REFERENCES MASTER."Account" ("id");

ALTER TABLE MASTER_TRANSACTION."ProvisionAddressTransaction"
    ADD CONSTRAINT ProvisionAddressTransaction_TxHash FOREIGN KEY ("txHash") REFERENCES BLOCKCHAIN_TRANSACTION."UserTransaction" ("txHash");
ALTER TABLE MASTER_TRANSACTION."ProvisionAddressTransaction"
    ADD CONSTRAINT ProvisionAddressTransaction_AccountId FOREIGN KEY ("accountId") REFERENCES MASTER."Account" ("id");

ALTER TABLE MASTER_TRANSACTION."UnprovisionAddressTransaction"
    ADD CONSTRAINT UnprovisionAddressTransaction_TxHash FOREIGN KEY ("txHash") REFERENCES BLOCKCHAIN_TRANSACTION."UserTransaction" ("txHash");
ALTER TABLE MASTER_TRANSACTION."UnprovisionAddressTransaction"
    ADD CONSTRAINT UnprovisionAddressTransaction_AccountId FOREIGN KEY ("accountId") REFERENCES MASTER."Account" ("id");

ALTER TABLE MASTER_TRANSACTION."MakeOrderTransaction"
    ADD CONSTRAINT MakeOrderTransaction_BuyerAccountId FOREIGN KEY ("buyerId") REFERENCES MASTER."Account" ("id");
ALTER TABLE MASTER_TRANSACTION."MakeOrderTransaction"
    ADD CONSTRAINT MakeOrderTransaction_sellerId FOREIGN KEY ("sellerId") REFERENCES MASTER."Account" ("id");
ALTER TABLE MASTER_TRANSACTION."MakeOrderTransaction"
    ADD CONSTRAINT MakeOrderTransaction_TxHash FOREIGN KEY ("txHash") REFERENCES BLOCKCHAIN_TRANSACTION."UserTransaction" ("txHash");

ALTER TABLE MASTER_TRANSACTION."NFTTransferTransaction"
    ADD CONSTRAINT NFTTransferTransaction_OwnerId FOREIGN KEY ("fromId") REFERENCES MASTER."Account" ("id");
ALTER TABLE MASTER_TRANSACTION."NFTTransferTransaction"
    ADD CONSTRAINT NFTTransferTransaction_ToAccountId FOREIGN KEY ("toAccountId") REFERENCES MASTER."Account" ("id");
ALTER TABLE MASTER_TRANSACTION."NFTTransferTransaction"
    ADD CONSTRAINT NFTTransferTransaction_TxHash FOREIGN KEY ("txHash") REFERENCES BLOCKCHAIN_TRANSACTION."UserTransaction" ("txHash");

ALTER TABLE MASTER_TRANSACTION."SendCoinTransaction"
    ADD CONSTRAINT SendCoinTransaction_FromAccountId FOREIGN KEY ("fromAccountId") REFERENCES MASTER."Account" ("id");
ALTER TABLE MASTER_TRANSACTION."SendCoinTransaction"
    ADD CONSTRAINT SendCoinTransaction_ToAccountId FOREIGN KEY ("toAccountId") REFERENCES MASTER."Account" ("id");
ALTER TABLE MASTER_TRANSACTION."SendCoinTransaction"
    ADD CONSTRAINT SendCoinTransaction_TxHash FOREIGN KEY ("txHash") REFERENCES BLOCKCHAIN_TRANSACTION."UserTransaction" ("txHash");

ALTER TABLE MASTER_TRANSACTION."TakeOrderTransaction"
    ADD CONSTRAINT TakeOrderTransaction_BuyerAccountId FOREIGN KEY ("buyerId") REFERENCES MASTER."Account" ("id");
ALTER TABLE MASTER_TRANSACTION."TakeOrderTransaction"
    ADD CONSTRAINT TakeOrderTransaction_TxHash FOREIGN KEY ("txHash") REFERENCES BLOCKCHAIN_TRANSACTION."UserTransaction" ("txHash");

ALTER TABLE MASTER_TRANSACTION."UnwrapTransaction"
    ADD CONSTRAINT UnwrapTransaction_AccountId FOREIGN KEY ("accountId") REFERENCES MASTER."Account" ("id");
ALTER TABLE MASTER_TRANSACTION."UnwrapTransaction"
    ADD CONSTRAINT UnwrapTransaction_TxHash FOREIGN KEY ("txHash") REFERENCES BLOCKCHAIN_TRANSACTION."UserTransaction" ("txHash");

ALTER TABLE MASTER_TRANSACTION."WrapTransaction"
    ADD CONSTRAINT WrapTransaction_AccountId FOREIGN KEY ("accountId") REFERENCES MASTER."Account" ("id");
ALTER TABLE MASTER_TRANSACTION."WrapTransaction"
    ADD CONSTRAINT WrapTransaction_TxHash FOREIGN KEY ("txHash") REFERENCES BLOCKCHAIN_TRANSACTION."UserTransaction" ("txHash");

CREATE TRIGGER BT_ADMIN_TRANSACTION_LOG
    BEFORE INSERT OR UPDATE
    ON BLOCKCHAIN_TRANSACTION."AdminTransaction"
    FOR EACH ROW
EXECUTE PROCEDURE PUBLIC.INSERT_OR_UPDATE_EPOCH_LOG();
CREATE TRIGGER BT_USER_TRANSACTION_LOG
    BEFORE INSERT OR UPDATE
    ON BLOCKCHAIN_TRANSACTION."UserTransaction"
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
CREATE TRIGGER SECRET_LOG
    BEFORE INSERT OR UPDATE
    ON MASTER."Secret"
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
CREATE TRIGGER SEND_COIN_TRANSACTION_LOG
    BEFORE INSERT OR UPDATE
    ON MASTER_TRANSACTION."SendCoinTransaction"
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
        IF (new."active" = true AND
            EXISTS(SELECT * FROM MASTER."Key" WHERE "accountId" = new."accountId" AND "active" = true)) THEN
            RAISE EXCEPTION 'MULTIPLE_ACTIVE_KEYS';;
        END IF;;
    ELSEIF (TG_OP = 'UPDATE') THEN
        -- allow all keys to be in false state to change active state
        IF (new."active" = true AND
            EXISTS(SELECT *
                   FROM MASTER."Key"
                   WHERE "accountId" = new."accountId"
                     AND "address" != new."address"
                     AND "active" = true)) THEN
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

INSERT INTO MASTER."Secret" ("id", "secret", "salt")
VALUES ('MEMO_SIGNER',
        '6MSNoTkLjY4uDONEIzGkvScwI7a6Et5P7QGIayQOBXjiwGDVHoUIdeYR3ow6fFTOE/dnXquNvShPy1FrSnkaeOtlG1lpUPe1Xxi105JybO+eiN4/eCTqNpUR2mA6NHPypGCozfnjlXgmIk4hMzfGGZbPwHDcrhjSMwYD6u5AHhDJwoRzMtS2izEUj/Wq/Dih/nIepC5NnUhcQMFLKwxDOs24q4b49J4VZOrwQcNRKi4=',
        'YSFEa1dvQE4qUl54akY2dkNAbkFMQHphRDI0V3FIZmJUWg==');

INSERT INTO MASTER."Secret" ("id", "secret", "salt")
VALUES ('MANTLE_PLACE',
        '6MSNoTkLjY4uDONEIzGkvScwI7a6Et5P7QGIayQOBXjiwGDVHoUIdeYR3ow6fFTOE/dnXquNvShPy1FrSnkaeOtlG1lpUPe1Xxi105JybO+eiN4/eCTqNpUR2mA6NHPypGCozfnjlXgmIk4hMzfGGZbPwHDcrhjSMwYD6u5AHhDJwoRzMtS2izEUj/Wq/Dih/nIepC5NnUhcQMFLKwxDOs24q4b49J4VZOrwQcNRKi4=',
        'YSFEa1dvQE4qUl54akY2dkNAbkFMQHphRDI0V3FIZmJUWg==');


# --- !Downs
DROP TRIGGER IF EXISTS BT_ADMIN_TRANSACTION_LOG ON BLOCKCHAIN_TRANSACTION."AdminTransaction" CASCADE;
DROP TRIGGER IF EXISTS BT_USER_TRANSACTION_LOG ON BLOCKCHAIN_TRANSACTION."UserTransaction" CASCADE;

DROP TRIGGER IF EXISTS MINT_NFT_AIRDROP_LOG ON CAMPAIGN."MintNFTAirDrop" CASCADE;
DROP TRIGGER IF EXISTS INELIGIBLE_MINT_NFT_AIRDROP_LOG ON CAMPAIGN."IneligibleMintNFTAirDrop" CASCADE;

DROP TRIGGER IF EXISTS SECONDARY_MARKET_HISTORY_LOG ON HISTORY."MasterSecondaryMarket" CASCADE;

DROP TRIGGER IF EXISTS BURNT_NFT_LOG ON MASTER."BurntNFT" CASCADE;
DROP TRIGGER IF EXISTS SECONDARY_MARKET_LOG ON MASTER."SecondaryMarket" CASCADE;
DROP TRIGGER IF EXISTS SECRET_LOG ON MASTER."Secret" CASCADE;

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
DROP TRIGGER IF EXISTS SEND_COIN_TRANSACTION_LOG ON MASTER_TRANSACTION."SendCoinTransaction" CASCADE;
DROP TRIGGER IF EXISTS TAKE_ORDER_TRANSACTION_LOG ON MASTER_TRANSACTION."TakeOrderTransaction" CASCADE;
DROP TRIGGER IF EXISTS UNWRAP_TRANSACTION_LOG ON MASTER_TRANSACTION."UnwrapTransaction" CASCADE;
DROP TRIGGER IF EXISTS WRAP_TRANSACTION_LOG ON MASTER_TRANSACTION."WrapTransaction" CASCADE;

DROP TABLE IF EXISTS BLOCKCHAIN_TRANSACTION."AdminTransaction" CASCADE;
DROP TABLE IF EXISTS BLOCKCHAIN_TRANSACTION."UserTransaction" CASCADE;

DROP TABLE IF EXISTS CAMPAIGN."MintNFTAirDrop" CASCADE;
DROP TABLE IF EXISTS CAMPAIGN."IneligibleMintNFTAirDrop" CASCADE;

DROP TABLE IF EXISTS HISTORY."MasterSecondaryMarket" CASCADE;

DROP TABLE IF EXISTS MASTER."BurntNFT" CASCADE;
DROP TABLE IF EXISTS MASTER."SecondaryMarket" CASCADE;
DROP TABLE IF EXISTS MASTER."Secret" CASCADE;

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
DROP TABLE IF EXISTS MASTER_TRANSACTION."SendCoinTransaction" CASCADE;
DROP TABLE IF EXISTS MASTER_TRANSACTION."TakeOrderTransaction" CASCADE;
DROP TABLE IF EXISTS MASTER_TRANSACTION."UnprovisionAddressTransaction" CASCADE;
DROP TABLE IF EXISTS MASTER_TRANSACTION."UnwrapTransaction" CASCADE;
DROP TABLE IF EXISTS MASTER_TRANSACTION."WrapTransaction" CASCADE;

DROP SCHEMA IF EXISTS CAMPAIGN CASCADE;
