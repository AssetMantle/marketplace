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

CREATE TABLE IF NOT EXISTS MASTER."NFTOwner"
(
    "fileName"             VARCHAR NOT NULL,
    "ownerId"              VARCHAR NOT NULL,
    "isCreator"            BOOLEAN NOT NULL,
    "collectionId"         VARCHAR NOT NULL,
    "quantity"             BIGINT  NOT NULL,
    "saleId"               VARCHAR,
    "createdBy"            VARCHAR,
    "createdOnMillisEpoch" BIGINT,
    "updatedBy"            VARCHAR,
    "updatedOnMillisEpoch" BIGINT,
    PRIMARY KEY ("fileName", "ownerId")
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

ALTER TABLE MASTER."Collection"
    DROP COLUMN IF EXISTS "website";
ALTER TABLE MASTER."Collection"
    ADD COLUMN IF NOT EXISTS "creatorFee" NUMERIC NOT NULL default 0.0;
ALTER TABLE MASTER."NFT"
    DROP COLUMN IF EXISTS "properties";
ALTER TABLE MASTER."NFT"
    ADD COLUMN IF NOT EXISTS "totalSupply" BIGINT NOT NULL default 1;
ALTER TABLE MASTER."NFT"
    ADD COLUMN IF NOT EXISTS "isMinted" BOOLEAN NOT NULL default false;
ALTER TABLE MASTER."NFT"
    ADD CONSTRAINT uniqueNFTIdCollectionId UNIQUE ("fileName", "collectionId");

ALTER TABLE ANALYTICS."CollectionAnalysis"
    ADD CONSTRAINT CollectionAnalysis_id FOREIGN KEY ("id") REFERENCES MASTER."Collection" ("id");

ALTER TABLE MASTER."Sale"
    ADD CONSTRAINT Sale_collectionId FOREIGN KEY ("collectionId") REFERENCES MASTER."Collection" ("id");
ALTER TABLE MASTER."NFTOwner"
    ADD CONSTRAINT NFTOwner_ownerId FOREIGN KEY ("ownerId") REFERENCES MASTER."Account" ("id");
ALTER TABLE MASTER."NFTOwner"
    ADD CONSTRAINT NFTOwner_saleId FOREIGN KEY ("saleId") REFERENCES MASTER."Sale" ("id");
ALTER TABLE MASTER."NFTOwner"
    ADD CONSTRAINT NFTOwner_collectionId FOREIGN KEY ("collectionId") REFERENCES MASTER."Collection" ("id");
ALTER TABLE MASTER."Sale"
    ADD CONSTRAINT Sale_WhitelistId FOREIGN KEY ("whitelistId") REFERENCES MASTER."Whitelist" ("id");

CREATE TRIGGER COLLECTION_ANALYSIS_LOG
    BEFORE INSERT OR UPDATE
    ON ANALYTICS."CollectionAnalysis"
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
DROP TRIGGER IF EXISTS NFT_OWNER_LOG ON MASTER."NFTOwner" CASCADE;
DROP TRIGGER IF EXISTS SALE_LOG ON MASTER."Sale" CASCADE;

DROP TABLE IF EXISTS ANALYTICS."CollectionAnalysis" CASCADE;
DROP TABLE IF EXISTS MASTER."NFTOwner" CASCADE;
DROP TABLE IF EXISTS MASTER."Sale" CASCADE;

DROP SCHEMA IF EXISTS ANALYTICS CASCADE;