# --- !Ups

CREATE TABLE IF NOT EXISTS MASTER."NFTSale"
(
    "fileName"             VARCHAR NOT NULL,
    "price"                NUMERIC NOT NULL,
    "denom"                VARCHAR NOT NULL,
    "creatorFee"           NUMERIC NOT NULL,
    "public"               BOOLEAN NOT NULL,
    "marketplace"          BOOLEAN NOT NULL,
    "startTimeEpoch"       BIGINT  NOT NULL,
    "endTimeEpoch"         BIGINT  NOT NULL,
    "createdBy"            VARCHAR,
    "createdOnMillisEpoch" BIGINT,
    "updatedBy"            VARCHAR,
    "updatedOnMillisEpoch" BIGINT,
    PRIMARY KEY ("fileName")
);

ALTER TABLE MASTER."NFTSale"
    ADD CONSTRAINT NFTSale_fileName FOREIGN KEY ("fileName") REFERENCES MASTER."NFT" ("fileName");

CREATE TRIGGER NFT_SALE_LOG
    BEFORE INSERT OR UPDATE
    ON MASTER."NFTSale"
    FOR EACH ROW
EXECUTE PROCEDURE PUBLIC.INSERT_OR_UPDATE_EPOCH_LOG();

# --- !Downs
DROP TRIGGER IF EXISTS NFT_SALE_LOG ON MASTER."NFTSale" CASCADE;

DROP TABLE IF EXISTS MASTER."NFTSale" CASCADE;