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

CREATE TABLE IF NOT EXISTS MASTER."NFTWhitelistSale"
(
    "id"                   VARCHAR NOT NULL,
    "fileName"             VARCHAR NOT NULL,
    "whitelistId"          VARCHAR NOT NULL,
    "quantity"             BIGINT  NOT NULL,
    "price"                NUMERIC NOT NULL,
    "denom"                VARCHAR NOT NULL,
    "creatorFee"           NUMERIC NOT NULL,
    "startTimeEpoch"       BIGINT  NOT NULL,
    "endTimeEpoch"         BIGINT  NOT NULL,
    "createdBy"            VARCHAR,
    "createdOnMillisEpoch" BIGINT,
    "updatedBy"            VARCHAR,
    "updatedOnMillisEpoch" BIGINT,
    PRIMARY KEY ("id"),
    UNIQUE ("fileName", "whitelistId")
);

ALTER TABLE MASTER."Collection"
    DROP COLUMN IF EXISTS "website";
ALTER TABLE MASTER."NFT"
    DROP COLUMN IF EXISTS "properties";
ALTER TABLE MASTER."NFT"
    ADD COLUMN IF NOT EXISTS "ownerId" VARCHAR default null;
ALTER TABLE MASTER."NFT"
    ADD COLUMN IF NOT EXISTS "supply" BIGINT NOT NULL default 1;

ALTER TABLE ANALYTICS."CollectionAnalysis"
    ADD CONSTRAINT CollectionAnalysis_id FOREIGN KEY ("id") REFERENCES MASTER."Collection" ("id");

ALTER TABLE MASTER."NFTWhitelistSale"
    ADD CONSTRAINT NFTWhitelistSale_fileName FOREIGN KEY ("fileName") REFERENCES MASTER."NFT" ("fileName");
ALTER TABLE MASTER."NFTWhitelistSale"
    ADD CONSTRAINT NFTWhitelistSale_whitelistId FOREIGN KEY ("whitelistId") REFERENCES MASTER."Whitelist" ("id");
ALTER TABLE MASTER."NFT"
    ADD CONSTRAINT NFT_ownerId FOREIGN KEY ("ownerId") REFERENCES MASTER."Account" ("id");

CREATE TRIGGER COLLECTION_ANALYSIS_LOG
    BEFORE INSERT OR UPDATE
    ON ANALYTICS."CollectionAnalysis"
    FOR EACH ROW
EXECUTE PROCEDURE PUBLIC.INSERT_OR_UPDATE_EPOCH_LOG();

CREATE TRIGGER NFT_WHITELIST_SALE_LOG
    BEFORE INSERT OR UPDATE
    ON MASTER."NFTWhitelistSale"
    FOR EACH ROW
EXECUTE PROCEDURE PUBLIC.INSERT_OR_UPDATE_EPOCH_LOG();

# --- !Downs
DROP TRIGGER IF EXISTS COLLECTION_ANALYSIS_LOG ON ANALYTICS."CollectionAnalysis" CASCADE;
DROP TRIGGER IF EXISTS NFT_WHITELIST_SALE_LOG ON MASTER."NFTWhitelistSale" CASCADE;

DROP TABLE IF EXISTS ANALYTICS."CollectionAnalysis" CASCADE;
DROP TABLE IF EXISTS MASTER."NFTWhitelistSale" CASCADE;

DROP SCHEMA IF EXISTS ANALYTICS CASCADE;