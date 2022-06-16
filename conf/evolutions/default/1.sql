# --- !Ups

CREATE SCHEMA IF NOT EXISTS MASTER
    AUTHORIZATION "mantlePlace";
CREATE SCHEMA IF NOT EXISTS MASTER_TRANSACTION
    AUTHORIZATION "mantlePlace";

CREATE TABLE IF NOT EXISTS MASTER."Account"
(
    "id"                VARCHAR NOT NULL,
    "passwordHash"      VARCHAR NOT NULL,
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

CREATE TABLE IF NOT EXISTS MASTER."Wallet"
(
    "address"           VARCHAR NOT NULL,
    "mnemonics"         VARCHAR NOT NULL,
    "accountID"         VARCHAR NOT NULL,
    "provisioned"       BOOLEAN,
    "createdBy"         VARCHAR,
    "createdOn"         TIMESTAMP,
    "createdOnTimeZone" VARCHAR,
    "updatedBy"         VARCHAR,
    "updatedOn"         TIMESTAMP,
    "updatedOnTimeZone" VARCHAR,
    PRIMARY KEY ("address")
);

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
CREATE TRIGGER WALLET_LOG
    BEFORE INSERT OR UPDATE
    ON MASTER."Wallet"
    FOR EACH ROW
EXECUTE PROCEDURE PUBLIC.INSERT_OR_UPDATE_LOG();


# --- !Downs

DROP TRIGGER IF EXISTS ACCOUNT_LOG ON MASTER."Account" CASCADE;
DROP TRIGGER IF EXISTS WALLET_LOG ON MASTER."Wallet" CASCADE;

DROP TABLE IF EXISTS MASTER."Account" CASCADE;
DROP TABLE IF EXISTS MASTER."Wallet" CASCADE;

DROP SCHEMA IF EXISTS MASTER CASCADE;
DROP SCHEMA IF EXISTS MASTER_TRANSACTION CASCADE;