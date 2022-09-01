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

CREATE TABLE IF NOT EXISTS MASTER."WishList"
(
    "accountId"         VARCHAR NOT NULL,
    "nftId"             VARCHAR NOT NULL,
    "collectionId"      VARCHAR NOT NULL,
    "createdBy"         VARCHAR,
    "createdOn"         TIMESTAMP,
    "createdOnTimeZone" VARCHAR,
    "updatedBy"         VARCHAR,
    "updatedOn"         TIMESTAMP,
    "updatedOnTimeZone" VARCHAR,
    PRIMARY KEY ("accountId", "nftId")
);

ALTER TABLE BLOCKCHAIN_TRANSACTION."SendCoin"
    ADD CONSTRAINT SendCoin_Account_Id FOREIGN KEY ("accountId") REFERENCES MASTER."Account" ("id");

ALTER TABLE MASTER."WishList"
    ADD CONSTRAINT WishList_Account_Id FOREIGN KEY ("accountId") REFERENCES MASTER."Account" ("id");
ALTER TABLE MASTER."WishList"
    ADD CONSTRAINT WishList_NFT_Id FOREIGN KEY ("nftId") REFERENCES MASTER."NFT" ("fileName");
ALTER TABLE MASTER."WishList"
    ADD CONSTRAINT WishList_NFT_Id FOREIGN KEY ("collectionId") REFERENCES MASTER."Collection" ("id");

CREATE TRIGGER SEND_COIN_LOG
    BEFORE INSERT OR UPDATE
    ON BLOCKCHAIN_TRANSACTION."SendCoin"
    FOR EACH ROW
EXECUTE PROCEDURE PUBLIC.INSERT_OR_UPDATE_LOG();

CREATE TRIGGER WISHLIST_LOG
    BEFORE INSERT OR UPDATE
    ON MASTER."WishList"
    FOR EACH ROW
EXECUTE PROCEDURE PUBLIC.INSERT_OR_UPDATE_LOG();

# --- !Downs

DROP TRIGGER IF EXISTS SEND_COIN_LOG ON BLOCKCHAIN_TRANSACTION."SendCoin" CASCADE;
DROP TRIGGER IF EXISTS WISHLIST_LOG ON MASTER."WishList" CASCADE;

DROP TABLE IF EXISTS BLOCKCHAIN_TRANSACTION."SendCoin" CASCADE;
DROP TABLE IF EXISTS MASTER."WishList" CASCADE;

DROP SCHEMA IF EXISTS BLOCKCHAIN_TRANSACTION CASCADE;