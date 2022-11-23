# --- !Ups

CREATE SCHEMA IF NOT EXISTS ANALYTICS
    AUTHORIZATION "mantlePlace";

CREATE TABLE IF NOT EXISTS ANALYTICS."CollectionAnalysis"
(
    "id"                   VARCHAR NOT NULL,
    "totalNFTs"            BIGINT  NOT NULL,
    "totalSold"            BIGINT  NOT NULL,
    "totalTraded"          BIGINT  NOT NULL,
    "floorPrice"           NUMERIC NOT NULL,
    "totalVolume"          NUMERIC NOT NULL,
    "bestOffer"            NUMERIC NOT NULL,
    "listed"               BIGINT  NOT NULL,
    "owners"               BIGINT  NOT NULL,
    "uniqueOwners"         BIGINT  NOT NULL,
    "createdBy"            VARCHAR,
    "createdOnMillisEpoch" BIGINT,
    "updatedBy"            VARCHAR,
    "updatedOnMillisEpoch" BIGINT,
    PRIMARY KEY ("id")
);

CREATE TABLE IF NOT EXISTS BLOCKCHAIN_TRANSACTION."BuyAssetWithoutMint"
(
    "sellerAccountId"      VARCHAR NOT NULL,
    "buyerAccountId"       VARCHAR NOT NULL,
    "txHash"               VARCHAR NOT NULL,
    "txRawBytes"           BYTEA   NOT NULL,
    "nftId"                VARCHAR NOT NULL,
    "saleId"               VARCHAR NOT NULL,
    "fromAddress"          VARCHAR NOT NULL,
    "toAddress"            VARCHAR NOT NULL,
    "amount"               VARCHAR NOT NULL,
    "broadcasted"          BOOLEAN NOT NULL,
    "status"               BOOLEAN,
    "memo"                 VARCHAR,
    "log"                  VARCHAR,
    "createdBy"            VARCHAR,
    "createdOnMillisEpoch" BIGINT,
    "updatedBy"            VARCHAR,
    "updatedOnMillisEpoch" BIGINT,
    PRIMARY KEY ("buyerAccountId", "sellerAccountId", "txHash"),
    UNIQUE ("buyerAccountId", "sellerAccountId", "saleId", "txHash")
);

CREATE TABLE IF NOT EXISTS MASTER."NFTOwner"
(
    "nftId"                VARCHAR NOT NULL,
    "ownerId"              VARCHAR NOT NULL,
    "creatorId"            VARCHAR NOT NULL,
    "collectionId"         VARCHAR NOT NULL,
    "quantity"             BIGINT  NOT NULL,
    "saleId"               VARCHAR,
    "createdBy"            VARCHAR,
    "createdOnMillisEpoch" BIGINT,
    "updatedBy"            VARCHAR,
    "updatedOnMillisEpoch" BIGINT,
    PRIMARY KEY ("nftId", "ownerId"),
    UNIQUE ("nftId", "ownerId", "saleId")
);

CREATE TABLE IF NOT EXISTS MASTER."Sale"
(
    "id"                   VARCHAR NOT NULL,
    "whitelistId"          VARCHAR NOT NULL,
    "collectionId"         VARCHAR NOT NULL,
    "numberOfNFTs"         BIGINT  NOT NULL,
    "maxMintPerAccount"    BIGINT  NOT NULL,
    "price"                NUMERIC NOT NULL,
    "denom"                VARCHAR NOT NULL,
    "startTimeEpoch"       BIGINT  NOT NULL,
    "endTimeEpoch"         BIGINT  NOT NULL,
    "createdBy"            VARCHAR,
    "createdOnMillisEpoch" BIGINT,
    "updatedBy"            VARCHAR,
    "updatedOnMillisEpoch" BIGINT,
    PRIMARY KEY ("id"),
    UNIQUE ("whitelistId", "collectionId")
);

ALTER TABLE BLOCKCHAIN_TRANSACTION."SendCoin"
    ADD COLUMN IF NOT EXISTS "memo" VARCHAR DEFAULT null;
ALTER TABLE BLOCKCHAIN_TRANSACTION."SendCoin"
    ADD COLUMN IF NOT EXISTS "txRawBytes" BYTEA NOT NULL DEFAULT '[]';
UPDATE BLOCKCHAIN_TRANSACTION."SendCoin"
SET "txRawBytes" = decode("txRawHex", 'hex')
WHERE "txRawBytes" = '[]';
ALTER TABLE BLOCKCHAIN_TRANSACTION."SendCoin"
    DROP COLUMN IF EXISTS "txRawHex";

ALTER TABLE MASTER."WishList"
    DROP CONSTRAINT IF EXISTS WishList_NFT_Id;
ALTER TABLE MASTER."NFTProperty"
    DROP CONSTRAINT IF EXISTS NFTProperty_fileName;
ALTER TABLE MASTER."NFTTag"
    DROP CONSTRAINT IF EXISTS NFTTag_fileName;

ALTER TABLE MASTER_TRANSACTION."NFTDraft"
    ADD COLUMN IF NOT EXISTS "id" VARCHAR NOT NULL DEFAULT '';
ALTER TABLE MASTER_TRANSACTION."NFTDraft"
    ADD COLUMN IF NOT EXISTS "fileExtension" VARCHAR NOT NULL DEFAULT '';
UPDATE MASTER_TRANSACTION."NFTDraft"
SET "id"            = split_part("fileName", '.', 1),
    "fileExtension" = split_part("fileName", '.', 2)
WHERE "id" = '';
ALTER TABLE MASTER_TRANSACTION."NFTDraft"
    DROP COLUMN IF EXISTS "fileName";
ALTER TABLE MASTER_TRANSACTION."NFTDraft"
    DROP CONSTRAINT IF EXISTS "NFTDraft_pkey";
ALTER TABLE MASTER_TRANSACTION."NFTDraft"
    ADD PRIMARY KEY ("id");

ALTER TABLE MASTER."NFTTag"
    ADD COLUMN IF NOT EXISTS "nftId" VARCHAR NOT NULL DEFAULT '';
UPDATE MASTER."NFTTag"
SET "nftId" = split_part("fileName", '.', 1)
WHERE "nftId" = '';
ALTER TABLE MASTER."NFTTag"
    DROP COLUMN IF EXISTS "fileName";
ALTER TABLE MASTER."NFTTag"
    DROP CONSTRAINT IF EXISTS "NFTTag_pkey";
ALTER TABLE MASTER."NFTTag"
    ADD PRIMARY KEY ("tagName", "nftId");

ALTER TABLE MASTER."NFTProperty"
    ADD COLUMN IF NOT EXISTS "nftId" VARCHAR NOT NULL DEFAULT '';
UPDATE MASTER."NFTProperty"
SET "nftId" = split_part("fileName", '.', 1)
WHERE "nftId" = '';
ALTER TABLE MASTER."NFTProperty"
    DROP COLUMN IF EXISTS "fileName";
ALTER TABLE MASTER."NFTProperty"
    DROP CONSTRAINT IF EXISTS "NFTProperty_pkey";
ALTER TABLE MASTER."NFTProperty"
    ADD PRIMARY KEY ("nftId", "name", "type");

UPDATE MASTER."WishList"
SET "nftId" = split_part("nftId", '.', 1)
WHERE "nftId" != '';

ALTER TABLE MASTER."NFT"
    ADD COLUMN IF NOT EXISTS "id" VARCHAR NOT NULL DEFAULT '';
ALTER TABLE MASTER."NFT"
    ADD COLUMN IF NOT EXISTS "fileExtension" VARCHAR NOT NULL DEFAULT '';
UPDATE MASTER."NFT"
SET "id"            = split_part("fileName", '.', 1),
    "fileExtension" = split_part("fileName", '.', 2)
WHERE "id" = '';
ALTER TABLE MASTER."NFT"
    DROP CONSTRAINT IF EXISTS "NFT_pkey";
ALTER TABLE MASTER."NFT"
    ADD PRIMARY KEY ("id");
ALTER TABLE MASTER."WishList"
    DROP CONSTRAINT IF EXISTS wishlist_nft_id;
ALTER TABLE MASTER."NFTProperty"
    DROP CONSTRAINT IF EXISTS NFTProperty_fileName;
ALTER TABLE MASTER."NFTTag"
    DROP CONSTRAINT IF EXISTS NFTTag_fileName;
ALTER TABLE MASTER."NFT"
    DROP COLUMN IF EXISTS "fileName";
ALTER TABLE MASTER."WishList"
    ADD CONSTRAINT WishList_NFTId FOREIGN KEY ("nftId") REFERENCES MASTER."NFT" ("id");
ALTER TABLE MASTER."NFTProperty"
    ADD CONSTRAINT NFTProperty_NFTId FOREIGN KEY ("nftId") REFERENCES MASTER."NFT" ("id");
ALTER TABLE MASTER."NFTTag"
    ADD CONSTRAINT NFTTag_NFTId FOREIGN KEY ("nftId") REFERENCES MASTER."NFT" ("id");


ALTER TABLE MASTER."Collection"
    DROP COLUMN IF EXISTS "website";
ALTER TABLE MASTER."Collection"
    ADD COLUMN IF NOT EXISTS "creatorFee" NUMERIC NOT NULL DEFAULT 0.0;
ALTER TABLE MASTER."NFT"
    DROP COLUMN IF EXISTS "properties";
ALTER TABLE MASTER."NFT"
    ADD COLUMN IF NOT EXISTS "totalSupply" BIGINT NOT NULL DEFAULT 1;
ALTER TABLE MASTER."NFT"
    ADD COLUMN IF NOT EXISTS "isMinted" BOOLEAN NOT NULL DEFAULT false;
ALTER TABLE MASTER."NFT"
    ADD CONSTRAINT uniqueNFTIdCollectionId UNIQUE ("id", "collectionId");

ALTER TABLE ANALYTICS."CollectionAnalysis"
    ADD CONSTRAINT CollectionAnalysis_id FOREIGN KEY ("id") REFERENCES MASTER."Collection" ("id");

ALTER TABLE BLOCKCHAIN_TRANSACTION."BuyAssetWithoutMint"
    ADD CONSTRAINT BuyAssetWithoutMint_sellerAccountId FOREIGN KEY ("sellerAccountId") REFERENCES MASTER."Account" ("id");
ALTER TABLE BLOCKCHAIN_TRANSACTION."BuyAssetWithoutMint"
    ADD CONSTRAINT BuyAssetWithoutMint_buyerAccountId FOREIGN KEY ("buyerAccountId") REFERENCES MASTER."Account" ("id");
ALTER TABLE BLOCKCHAIN_TRANSACTION."BuyAssetWithoutMint"
    ADD CONSTRAINT BuyAssetWithoutMint_saleId FOREIGN KEY ("saleId") REFERENCES MASTER."Sale" ("id");
ALTER TABLE BLOCKCHAIN_TRANSACTION."BuyAssetWithoutMint"
    ADD CONSTRAINT BuyAssetWithoutMint_nftId FOREIGN KEY ("nftId") REFERENCES MASTER."NFT" ("id");


ALTER TABLE MASTER."Sale"
    ADD CONSTRAINT Sale_collectionId FOREIGN KEY ("collectionId") REFERENCES MASTER."Collection" ("id");
ALTER TABLE MASTER."NFTOwner"
    ADD CONSTRAINT NFTOwner_ownerId FOREIGN KEY ("ownerId") REFERENCES MASTER."Account" ("id");
ALTER TABLE MASTER."NFTOwner"
    ADD CONSTRAINT NFTOwner_saleId FOREIGN KEY ("saleId") REFERENCES MASTER."Sale" ("id");
ALTER TABLE MASTER."NFTOwner"
    ADD CONSTRAINT NFTOwner_nftId FOREIGN KEY ("nftId") REFERENCES MASTER."NFT" ("id");
ALTER TABLE MASTER."NFTOwner"
    ADD CONSTRAINT NFTOwner_collectionId FOREIGN KEY ("collectionId") REFERENCES MASTER."Collection" ("id");
ALTER TABLE MASTER."Sale"
    ADD CONSTRAINT Sale_WhitelistId FOREIGN KEY ("whitelistId") REFERENCES MASTER."Whitelist" ("id");

CREATE TRIGGER COLLECTION_ANALYSIS_LOG
    BEFORE INSERT OR UPDATE
    ON ANALYTICS."CollectionAnalysis"
    FOR EACH ROW
EXECUTE PROCEDURE PUBLIC.INSERT_OR_UPDATE_EPOCH_LOG();

CREATE TRIGGER BUY_ASSET_WITHOUT_MINT_LOG
    BEFORE INSERT OR UPDATE
    ON BLOCKCHAIN_TRANSACTION."BuyAssetWithoutMint"
    FOR EACH ROW
EXECUTE PROCEDURE PUBLIC.INSERT_OR_UPDATE_EPOCH_LOG();

CREATE TRIGGER NFT_OWNER_LOG
    BEFORE INSERT OR UPDATE
    ON MASTER."NFTOwner"
    FOR EACH ROW
EXECUTE PROCEDURE PUBLIC.INSERT_OR_UPDATE_EPOCH_LOG();
CREATE TRIGGER SALE_LOG
    BEFORE INSERT OR UPDATE
    ON MASTER."Sale"
    FOR EACH ROW
EXECUTE PROCEDURE PUBLIC.INSERT_OR_UPDATE_EPOCH_LOG();

# --- !Downs
DROP TRIGGER IF EXISTS COLLECTION_ANALYSIS_LOG ON ANALYTICS."CollectionAnalysis" CASCADE;

DROP TRIGGER IF EXISTS BUY_ASSET_WITHOUT_MINT_LOG ON BLOCKCHAIN_TRANSACTION."BuyAssetWithoutMint" CASCADE;

DROP TRIGGER IF EXISTS NFT_OWNER_LOG ON MASTER."NFTOwner" CASCADE;
DROP TRIGGER IF EXISTS SALE_LOG ON MASTER."Sale" CASCADE;

DROP TABLE IF EXISTS ANALYTICS."CollectionAnalysis" CASCADE;
DROP TABLE IF EXISTS BLOCKCHAIN_TRANSACTION."BuyAssetWithoutMint" CASCADE;
DROP TABLE IF EXISTS MASTER."NFTOwner" CASCADE;
DROP TABLE IF EXISTS MASTER."Sale" CASCADE;

DROP SCHEMA IF EXISTS ANALYTICS CASCADE;