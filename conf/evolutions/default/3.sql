# --- !Ups

CREATE SCHEMA IF NOT EXISTS BLOCKCHAIN_TRANSACTION
    AUTHORIZATION "mantlePlace";

CREATE TABLE IF NOT EXISTS BLOCKCHAIN_TRANSACTION."SendCoin"
(
    "accountId"         VARCHAR NOT NULL,
    "txHash"            VARCHAR NOT NULL,
    "txRawHex"          VARCHAR NOT NULL,
    "fromAddress"       VARCHAR NOT NULL,
    "toAddress"         VARCHAR NOT NULL,
    "amount"            VARCHAR NOT NULL,
    "broadcasted"       BOOLEAN NOT NULL,
    "status"            BOOLEAN,
    "log"               VARCHAR,
    "createdBy"         VARCHAR,
    "createdOn"         TIMESTAMP,
    "createdOnTimeZone" VARCHAR,
    "updatedBy"         VARCHAR,
    "updatedOn"         TIMESTAMP,
    "updatedOnTimeZone" VARCHAR,
    PRIMARY KEY ("accountId", "txHash")
);

ALTER TABLE BLOCKCHAIN_TRANSACTION."SendCoin"
    ADD CONSTRAINT SendCoin_Account_Id FOREIGN KEY ("accountId") REFERENCES MASTER."Account" ("id");

CREATE TRIGGER SEND_COIN_LOG
    BEFORE INSERT OR UPDATE
    ON BLOCKCHAIN_TRANSACTION."SendCoin"
    FOR EACH ROW
EXECUTE PROCEDURE PUBLIC.INSERT_OR_UPDATE_LOG();

# --- !Downs

DROP TRIGGER IF EXISTS SEND_COIN_LOG ON BLOCKCHAIN_TRANSACTION."SendCoin" CASCADE;

DROP TABLE IF EXISTS BLOCKCHAIN_TRANSACTION."SendCoin" CASCADE;

DROP SCHEMA IF EXISTS BLOCKCHAIN_TRANSACTION CASCADE;