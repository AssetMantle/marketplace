# --- !Ups

CREATE TABLE IF NOT EXISTS MASTER."Key"
(
    "accountId"           VARCHAR NOT NULL,
    "address"             VARCHAR NOT NULL,
    "passwordHash"        BYTEA   NOT NULL,
    "salt"                BYTEA   NOT NULL,
    "iterations"          INTEGER NOT NULL,
    "encryptedPrivateKey" BYTEA,
    "partialMnemonics"    VARCHAR,
    "retryCounter"        INTEGER NOT NULL,
    "active"              BOOLEAN NOT NULL,
    "backupUsed"          BOOLEAN NOT NULL,
    "verified"            BOOLEAN NOT NULL,
    "createdBy"           VARCHAR,
    "createdOn"           TIMESTAMP,
    "createdOnTimeZone"   VARCHAR,
    "updatedBy"           VARCHAR,
    "updatedOn"           TIMESTAMP,
    "updatedOnTimeZone"   VARCHAR,
    PRIMARY KEY ("accountId", "address")
);

ALTER TABLE MASTER."Key"
    ADD CONSTRAINT Key_Account_Id FOREIGN KEY ("accountId") REFERENCES MASTER."Account" ("id");

CREATE TRIGGER KEY_LOG
    BEFORE INSERT OR UPDATE
    ON MASTER."Key"
    FOR EACH ROW
EXECUTE PROCEDURE PUBLIC.INSERT_OR_UPDATE_LOG();

# --- !Downs

DROP TRIGGER IF EXISTS KEY_LOG ON MASTER."Key" CASCADE;

DROP TABLE IF EXISTS MASTER."Key" CASCADE;