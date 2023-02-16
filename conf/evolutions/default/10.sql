# --- !Ups

CREATE TABLE IF NOT EXISTS BLOCKCHAIN_TRANSACTION."SecondaryMarketTransfer"
(
    "txHash"               VARCHAR NOT NULL,
    "txRawBytes"           BYTEA   NOT NULL,
    "fromAddress"          VARCHAR NOT NULL,
    "toAddress"            VARCHAR NOT NULL,
    "amount"               VARCHAR NOT NULL,
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

CREATE TABLE IF NOT EXISTS HISTORY."MasterSecondaryMarket"
(
    "id"                   VARCHAR NOT NULL,
    "nftId"                VARCHAR NOT NULL,
    "collectionId"         VARCHAR NOT NULL,
    "price"                NUMERIC NOT NULL,
    "denom"                VARCHAR NOT NULL,
    "endTimeEpoch"         BIGINT  NOT NULL,
    "createdBy"            VARCHAR,
    "createdOnMillisEpoch" BIGINT,
    "updatedBy"            VARCHAR,
    "updatedOnMillisEpoch" BIGINT,
    "deletedBy"            VARCHAR,
    "deletedOnMillisEpoch" BIGINT,
    PRIMARY KEY ("id")
);

CREATE TABLE IF NOT EXISTS MASTER."SecondaryMarket"
(
    "id"                   VARCHAR NOT NULL,
    "nftId"                VARCHAR NOT NULL,
    "collectionId"         VARCHAR NOT NULL,
    "price"                NUMERIC NOT NULL,
    "denom"                VARCHAR NOT NULL,
    "endTimeEpoch"         BIGINT  NOT NULL,
    "createdBy"            VARCHAR,
    "createdOnMillisEpoch" BIGINT,
    "updatedBy"            VARCHAR,
    "updatedOnMillisEpoch" BIGINT,
    PRIMARY KEY ("id")
);

CREATE TABLE IF NOT EXISTS MASTER_TRANSACTION."secondaryMarketTransferTransactions"
(
    "txHash"               VARCHAR NOT NULL,
    "nftId"                VARCHAR NOT NULL,
    "buyerAccountId"       VARCHAR NOT NULL,
    "sellerAccountId"      VARCHAR NOT NULL,
    "secondaryMarketId"    VARCHAR NOT NULL,
    "status"               BOOLEAN,
    "createdBy"            VARCHAR,
    "createdOnMillisEpoch" BIGINT,
    "updatedBy"            VARCHAR,
    "updatedOnMillisEpoch" BIGINT,
    PRIMARY KEY ("txHash", "nftId"),
    UNIQUE ("buyerAccountId", "sellerAccountId", "txHash", "nftId")
);

ALTER TABLE MASTER."NFTOwner"
    ADD COLUMN IF NOT EXISTS "secondaryMarketId" VARCHAR DEFAULT NULL;
ALTER TABLE MASTER."NFTOwner"
    ADD CONSTRAINT NFTOwner_SecondaryMarket FOREIGN KEY ("secondaryMarketId") REFERENCES MASTER."SecondaryMarket" ("id");

ALTER TABLE MASTER."SecondaryMarket"
    ADD CONSTRAINT SecondaryMarket_NFTId FOREIGN KEY ("nftId") REFERENCES MASTER."NFT" ("id");
ALTER TABLE MASTER."SecondaryMarket"
    ADD CONSTRAINT SecondaryMarket_collectionId FOREIGN KEY ("collectionId") REFERENCES MASTER."Collection" ("id");

ALTER TABLE MASTER_TRANSACTION."secondaryMarketTransferTransactions"
    ADD CONSTRAINT secondaryMarketTransferTransactions_BuyerAccountId FOREIGN KEY ("buyerAccountId") REFERENCES MASTER."Account" ("id");
ALTER TABLE MASTER_TRANSACTION."secondaryMarketTransferTransactions"
    ADD CONSTRAINT secondaryMarketTransferTransactions_sellerAccountId FOREIGN KEY ("sellerAccountId") REFERENCES MASTER."Account" ("id");
ALTER TABLE MASTER_TRANSACTION."secondaryMarketTransferTransactions"
    ADD CONSTRAINT secondaryMarketTransferTransactions_TxHash FOREIGN KEY ("txHash") REFERENCES BLOCKCHAIN_TRANSACTION."SecondaryMarketTransfer" ("txHash");

CREATE TRIGGER BT_SECONDARY_MARKET_SALE_LOG
    BEFORE INSERT OR UPDATE
    ON BLOCKCHAIN_TRANSACTION."SecondaryMarketTransfer"
    FOR EACH ROW
EXECUTE PROCEDURE PUBLIC.INSERT_OR_UPDATE_EPOCH_LOG();

CREATE TRIGGER SECONDARY_MARKET_HISTORY_LOG
    BEFORE INSERT OR UPDATE
    ON HISTORY."MasterSecondaryMarket"
    FOR EACH ROW
EXECUTE PROCEDURE PUBLIC.INSERT_OR_UPDATE_HISTORY_EPOCH_LOG();

CREATE TRIGGER SECONDARY_MARKET_LOG
    BEFORE INSERT OR UPDATE
    ON MASTER."SecondaryMarket"
    FOR EACH ROW
EXECUTE PROCEDURE PUBLIC.INSERT_OR_UPDATE_EPOCH_LOG();

CREATE TRIGGER SECONDARY_MARKET_SALE_TX_LOG
    BEFORE INSERT OR UPDATE
    ON MASTER_TRANSACTION."secondaryMarketTransferTransactions"
    FOR EACH ROW
EXECUTE PROCEDURE PUBLIC.INSERT_OR_UPDATE_EPOCH_LOG();

# --- !Downs
DROP TRIGGER IF EXISTS BT_SECONDARY_MARKET_SALE_LOG ON BLOCKCHAIN_TRANSACTION."SecondaryMarketTransfer" CASCADE;
DROP TRIGGER IF EXISTS SECONDARY_MARKET_HISTORY_LOG ON HISTORY."MasterSecondaryMarket" CASCADE;
DROP TRIGGER IF EXISTS SECONDARY_MARKET_SALE_TX_LOG ON MASTER_TRANSACTION."secondaryMarketTransferTransactions" CASCADE;
DROP TRIGGER IF EXISTS SECONDARY_MARKET_LOG ON MASTER."SecondaryMarket" CASCADE;

DROP TABLE IF EXISTS BLOCKCHAIN_TRANSACTION."SecondaryMarketTransfer" CASCADE;
DROP TABLE IF EXISTS HISTORY."MasterSecondaryMarket" CASCADE;
DROP TABLE IF EXISTS MASTER_TRANSACTION."secondaryMarketTransferTransactions" CASCADE;
DROP TABLE IF EXISTS MASTER."SecondaryMarket" CASCADE;
