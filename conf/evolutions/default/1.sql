# --- !Ups

CREATE SCHEMA IF NOT EXISTS MASTER
    AUTHORIZATION "mantlePlace";
CREATE SCHEMA IF NOT EXISTS MASTER_TRANSACTION
    AUTHORIZATION "mantlePlace";

CREATE TABLE IF NOT EXISTS MASTER."Account"
(
    "id"                VARCHAR NOT NULL,
    "passwordHash"      BYTEA   NOT NULL,
    "salt"              BYTEA   NOT NULL,
    "iterations"        INTEGER NOT NULL,
    "accountType"       VARCHAR,
    "language"          VARCHAR,
    "createdBy"         VARCHAR,
    "createdOn"         TIMESTAMP,
    "createdOnTimeZone" VARCHAR,
    "updatedBy"         VARCHAR,
    "updatedOn"         TIMESTAMP,
    "updatedOnTimeZone" VARCHAR,
    PRIMARY KEY ("id")
);

CREATE TABLE IF NOT EXISTS MASTER."Collection"
(

    "id"                VARCHAR NOT NULL,
    "classificationId"  VARCHAR,
    "name"              VARCHAR NOT NULL UNIQUE,
    "description"       VARCHAR NOT NULL,
    "website"           VARCHAR NOT NULL,
    "socialProfiles"    VARCHAR NOT NULL,
    "createdBy"         VARCHAR,
    "createdOn"         TIMESTAMP,
    "createdOnTimeZone" VARCHAR,
    "updatedBy"         VARCHAR,
    "updatedOn"         TIMESTAMP,
    "updatedOnTimeZone" VARCHAR,
    PRIMARY KEY ("id")
);

CREATE TABLE IF NOT EXISTS MASTER."CollectionFile"
(
    "id"                VARCHAR NOT NULL,
    "documentType"      VARCHAR NOT NULL,
    "fileName"          VARCHAR NOT NULL,
    "file"              BYTEA   NOT NULL,
    "createdBy"         VARCHAR,
    "createdOn"         TIMESTAMP,
    "createdOnTimeZone" VARCHAR,
    "updatedBy"         VARCHAR,
    "updatedOn"         TIMESTAMP,
    "updatedOnTimeZone" VARCHAR,
    PRIMARY KEY ("id", "documentType")
);

CREATE TABLE IF NOT EXISTS MASTER."NFT"
(
    "fileName"          VARCHAR NOT NULL,
    "file"              BYTEA   NOT NULL,
    "collectionId"      VARCHAR NOT NULL,
    "name"              VARCHAR NOT NULL,
    "description"       VARCHAR NOT NULL,
    "properties"        VARCHAR NOT NULL,
    "ipfsLink"          VARCHAR NOT NULL,
    "edition"           INTEGER,
    "createdBy"         VARCHAR,
    "createdOn"         TIMESTAMP,
    "createdOnTimeZone" VARCHAR,
    "updatedBy"         VARCHAR,
    "updatedOn"         TIMESTAMP,
    "updatedOnTimeZone" VARCHAR,
    PRIMARY KEY ("fileName")
);

CREATE TABLE IF NOT EXISTS MASTER."Wallet"
(
    "address"           VARCHAR NOT NULL,
    "partialMnemonics"  VARCHAR NOT NULL,
    "accountId"         VARCHAR NOT NULL,
    "provisioned"       BOOLEAN,
    "verified"          BOOLEAN,
    "preference"        INTEGER NOT NULL,
    "createdBy"         VARCHAR,
    "createdOn"         TIMESTAMP,
    "createdOnTimeZone" VARCHAR,
    "updatedBy"         VARCHAR,
    "updatedOn"         TIMESTAMP,
    "updatedOnTimeZone" VARCHAR,
    PRIMARY KEY ("address")
);

CREATE TABLE IF NOT EXISTS MASTER_TRANSACTION."PushNotificationToken"
(
    "accountId"         VARCHAR NOT NULL,
    "token"             VARCHAR NOT NULL,
    "createdBy"         VARCHAR,
    "createdOn"         TIMESTAMP,
    "createdOnTimeZone" VARCHAR,
    "updatedBy"         VARCHAR,
    "updatedOn"         TIMESTAMP,
    "updatedOnTimeZone" VARCHAR,
    PRIMARY KEY ("accountId")
);

CREATE TABLE IF NOT EXISTS MASTER_TRANSACTION."SessionToken"
(
    "accountId"         VARCHAR NOT NULL,
    "sessionTokenHash"  VARCHAR NOT NULL,
    "sessionTokenTime"  BIGINT  NOT NULL,
    "language"          VARCHAR,
    "createdBy"         VARCHAR,
    "createdOn"         TIMESTAMP,
    "createdOnTimeZone" VARCHAR,
    "updatedBy"         VARCHAR,
    "updatedOn"         TIMESTAMP,
    "updatedOnTimeZone" VARCHAR,
    PRIMARY KEY ("accountId")
);

CREATE TABLE IF NOT EXISTS MASTER_TRANSACTION."NameTransaction"
(
    "username" VARCHAR NOT NULL,
    "email" VARCHAR,
    "idString" VARCHAR NOT NULL,
    "isClaimed" BOOLEAN NOT NULL,
    PRIMARY KEY ("username", "idString")
);

ALTER TABLE MASTER."Wallet"
    ADD CONSTRAINT Wallet_Account_Id FOREIGN KEY ("accountId") REFERENCES MASTER."Account" ("id");
ALTER TABLE MASTER."NFT"
    ADD CONSTRAINT NFT_Collection_Id FOREIGN KEY ("collectionId") REFERENCES MASTER."Collection" ("id");

ALTER TABLE MASTER_TRANSACTION."SessionToken"
    ADD CONSTRAINT Wallet_Account_Id FOREIGN KEY ("accountId") REFERENCES MASTER."Account" ("id");
ALTER TABLE MASTER_TRANSACTION."PushNotificationToken"
    ADD CONSTRAINT PushNotificationToken_Account_Id FOREIGN KEY ("accountId") REFERENCES MASTER."Account" ("id");

/*Triggers*/

CREATE OR REPLACE FUNCTION PUBLIC.INSERT_OR_UPDATE_LOG() RETURNS TRIGGER AS
$$
BEGIN
    IF (TG_OP = 'INSERT') THEN
        new."createdOn" = CURRENT_TIMESTAMP;;
        new."createdOnTimeZone" = CURRENT_SETTING('TIMEZONE');;
        new."createdBy" = CURRENT_USER;;
    ELSEIF (TG_OP = 'UPDATE') THEN
--         values of created needs to be set here otherwise insertOrUpdate of slick will omit created details
        new."createdOn" = old."createdOn";;
        new."createdOnTimeZone" = old."createdOnTimeZone";;
        new."createdBy" = old."createdBy";;
        new."updatedOn" = CURRENT_TIMESTAMP;;
        new."updatedOnTimeZone" = CURRENT_SETTING('TIMEZONE');;
        new."updatedBy" = CURRENT_USER;;
    END IF;;
    RETURN NEW;;
END;;
$$ LANGUAGE PLPGSQL;

CREATE TRIGGER ACCOUNT_LOG
    BEFORE INSERT OR UPDATE
    ON MASTER."Account"
    FOR EACH ROW
EXECUTE PROCEDURE PUBLIC.INSERT_OR_UPDATE_LOG();
CREATE TRIGGER COLLECTION_LOG
    BEFORE INSERT OR UPDATE
    ON MASTER."Collection"
    FOR EACH ROW
EXECUTE PROCEDURE PUBLIC.INSERT_OR_UPDATE_LOG();
CREATE TRIGGER COLLECTION_FILE_LOG
    BEFORE INSERT OR UPDATE
    ON MASTER."CollectionFile"
    FOR EACH ROW
EXECUTE PROCEDURE PUBLIC.INSERT_OR_UPDATE_LOG();
CREATE TRIGGER NFT_LOG
    BEFORE INSERT OR UPDATE
    ON MASTER."NFT"
    FOR EACH ROW
EXECUTE PROCEDURE PUBLIC.INSERT_OR_UPDATE_LOG();
CREATE TRIGGER WALLET_LOG
    BEFORE INSERT OR UPDATE
    ON MASTER."Wallet"
    FOR EACH ROW
EXECUTE PROCEDURE PUBLIC.INSERT_OR_UPDATE_LOG();

CREATE TRIGGER PUSH_NOTIFICATION_TOKEN_TOKEN_LOG
    BEFORE INSERT OR UPDATE
    ON MASTER_TRANSACTION."PushNotificationToken"
    FOR EACH ROW
EXECUTE PROCEDURE PUBLIC.INSERT_OR_UPDATE_LOG();
CREATE TRIGGER SESSION_TOKEN_LOG
    BEFORE INSERT OR UPDATE
    ON MASTER_TRANSACTION."SessionToken"
    FOR EACH ROW
EXECUTE PROCEDURE PUBLIC.INSERT_OR_UPDATE_LOG();


# --- !Downs

DROP TRIGGER IF EXISTS ACCOUNT_LOG ON MASTER."Account" CASCADE;
DROP TRIGGER IF EXISTS COLLECTION_LOG ON MASTER."Collection" CASCADE;
DROP TRIGGER IF EXISTS COLLECTION_FILE_LOG ON MASTER."CollectionFile" CASCADE;
DROP TRIGGER IF EXISTS NFT_LOG ON MASTER."NFT" CASCADE;
DROP TRIGGER IF EXISTS WALLET_LOG ON MASTER."Wallet" CASCADE;

DROP TRIGGER IF EXISTS PUSH_NOTIFICATION_TOKEN_TOKEN_LOG ON MASTER_TRANSACTION."PushNotificationToken" CASCADE;
DROP TRIGGER IF EXISTS SESSION_TOKEN_LOG ON MASTER_TRANSACTION."SessionToken" CASCADE;

DROP TABLE IF EXISTS MASTER."Account" CASCADE;
DROP TABLE IF EXISTS MASTER."Collection" CASCADE;
DROP TABLE IF EXISTS MASTER."CollectionFile" CASCADE;
DROP TABLE IF EXISTS MASTER."NFT" CASCADE;
DROP TABLE IF EXISTS MASTER."Wallet" CASCADE;

DROP TABLE IF EXISTS MASTER_TRANSACTION."PushNotificationToken" CASCADE;
DROP TABLE IF EXISTS MASTER_TRANSACTION."SessionToken" CASCADE;
DROP TABLE IF EXISTS MASTER_TRANSACTION."NameTransaction" CASCADE;

DROP SCHEMA IF EXISTS MASTER CASCADE;
DROP SCHEMA IF EXISTS MASTER_TRANSACTION CASCADE;