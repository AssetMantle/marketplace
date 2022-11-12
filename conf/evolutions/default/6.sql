# --- !Ups

CREATE TABLE IF NOT EXISTS MASTER."NFTWhitelistSale"
(
    "id"                   VARCHAR NOT NULL,
    "fileName"             VARCHAR NOT NULL,
    "whitelistId"          VARCHAR NOT NULL,
    "quantity"             BIGINT  NOT NULL,
    "rate"                 NUMERIC NOT NULL,
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
    DROP COLUMN "website";
ALTER TABLE MASTER."NFT"
    DROP COLUMN "properties";
ALTER TABLE MASTER."NFT"
    ADD COLUMN IF NOT EXISTS "ownerId" VARCHAR default null;
ALTER TABLE MASTER."NFT"
    ADD COLUMN IF NOT EXISTS "supply" BIGINT NOT NULL default 1;


ALTER TABLE MASTER."NFTWhitelistSale"
    ADD CONSTRAINT NFTWhitelistSale_fileName FOREIGN KEY ("fileName") REFERENCES MASTER."NFT" ("fileName");
ALTER TABLE MASTER."NFTWhitelistSale"
    ADD CONSTRAINT NFTWhitelistSale_whitelistId FOREIGN KEY ("whitelistId") REFERENCES MASTER."Whitelist" ("id");
ALTER TABLE MASTER."NFT"
    ADD CONSTRAINT NFT_ownerId FOREIGN KEY ("ownerId") REFERENCES MASTER."Account" ("id");

CREATE TRIGGER NFT_WHITELIST_SALE_LOG
    BEFORE INSERT OR UPDATE
    ON MASTER."NFTWhitelistSale"
    FOR EACH ROW
EXECUTE PROCEDURE PUBLIC.INSERT_OR_UPDATE_EPOCH_LOG();

# --- !Downs
DROP TRIGGER IF EXISTS NFT_WHITELIST_SALE_LOG ON MASTER."NFTWhitelistSale" CASCADE;

DROP TABLE IF EXISTS MASTER."NFTWhitelistSale" CASCADE;