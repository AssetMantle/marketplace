# --- !Ups

CREATE TABLE IF NOT EXISTS MASTER."WishList"
(
    "accountId"         VARCHAR NOT NULL,
    "nftId"             VARCHAR NOT NULL,
    "createdBy"         VARCHAR,
    "createdOn"         TIMESTAMP,
    "createdOnTimeZone" VARCHAR,
    "updatedBy"         VARCHAR,
    "updatedOn"         TIMESTAMP,
    "updatedOnTimeZone" VARCHAR,
    PRIMARY KEY ("accountId", "nftId")
);

ALTER TABLE MASTER."WishList"
    ADD CONSTRAINT WishList_Account_Id FOREIGN KEY ("accountId") REFERENCES MASTER."Account" ("id");
ALTER TABLE MASTER."WishList"
    ADD CONSTRAINT WishList_NFT_Id FOREIGN KEY ("nftId") REFERENCES MASTER."NFT" ("fileName");

CREATE TRIGGER WISHLIST_LOG
    BEFORE INSERT OR UPDATE
    ON MASTER."WishList"
    FOR EACH ROW
EXECUTE PROCEDURE PUBLIC.INSERT_OR_UPDATE_LOG();

# --- !Downs

DROP TRIGGER IF EXISTS WISHLIST_LOG ON MASTER."WishList" CASCADE;

DROP TABLE IF EXISTS MASTER."WishList" CASCADE;