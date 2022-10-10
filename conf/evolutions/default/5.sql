# --- !Ups

CREATE TABLE IF NOT EXISTS MASTER."CollectionProperty"
(
    "id"                   VARCHAR NOT NULL,
    "propertyName"         VARCHAR NOT NULL,
    "propertyType"         VARCHAR NOT NULL,
    "required"             BOOLEAN NOT NULL,
    "mutable"              BOOLEAN NOT NULL,
    "hideValue"            BOOLEAN NOT NULL,
    "fixedValue"           VARCHAR,
    "createdBy"            VARCHAR,
    "createdOnMillisEpoch" BIGINT,
    "updatedBy"            VARCHAR,
    "updatedOnMillisEpoch" BIGINT,
    PRIMARY KEY ("id", "propertyName")
);

CREATE TABLE IF NOT EXISTS MASTER_TRANSACTION."Notification"
(
    "id"                   VARCHAR NOT NULL,
    "accountID"            VARCHAR,
    "title"                VARCHAR NOT NULL,
    "messageParameters"    VARCHAR NOT NULL,
    "jsRoute"              VARCHAR,
    "read"                 BOOLEAN NOT NULL,
    "createdBy"            VARCHAR,
    "createdOnMillisEpoch" BIGINT,
    "updatedBy"            VARCHAR,
    "updatedOnMillisEpoch" BIGINT,
    PRIMARY KEY ("id")
);

ALTER TABLE MASTER."Collection"
    ADD COLUMN IF NOT EXISTS "category" VARCHAR NOT NULL default 'ART';
ALTER TABLE MASTER."Collection"
    ADD COLUMN IF NOT EXISTS "nsfw" BOOLEAN NOT NULL default false;

ALTER TABLE MASTER."CollectionProperty"
    ADD CONSTRAINT CollectionProperty_Collection_Id FOREIGN KEY ("id") REFERENCES MASTER."Collection" ("id");
ALTER TABLE MASTER_TRANSACTION."Notification"
    ADD CONSTRAINT Notification_Account_Id FOREIGN KEY ("accountID") REFERENCES MASTER."Account" ("id");

CREATE OR REPLACE FUNCTION PUBLIC.INSERT_OR_UPDATE_EPOCH_LOG() RETURNS TRIGGER AS
$$
BEGIN
    IF (TG_OP = 'INSERT') THEN
        new."createdOnMillisEpoch" = FLOOR(EXTRACT(EPOCH FROM NOW()) * 1000);;
        new."createdBy" = CURRENT_USER;;
    ELSEIF (TG_OP = 'UPDATE') THEN
--         values of created needs to be set here otherwise insertOrUpdate of slick will omit created details
        new."createdOnMillisEpoch" = old."createdOnMillisEpoch";;
        new."createdBy" = old."createdBy";;
        new."updatedOnMillisEpoch" = FLOOR(EXTRACT(EPOCH FROM NOW()) * 1000);;
        new."updatedBy" = CURRENT_USER;;
    END IF;;
    RETURN NEW;;
END;;
$$ LANGUAGE PLPGSQL;

CREATE TRIGGER COLLECTION_PROPERTY_LOG
    BEFORE INSERT OR UPDATE
    ON MASTER."CollectionProperty"
    FOR EACH ROW
EXECUTE PROCEDURE PUBLIC.INSERT_OR_UPDATE_EPOCH_LOG();
CREATE TRIGGER NOTIFICATION_LOG
    BEFORE INSERT OR UPDATE
    ON MASTER_TRANSACTION."Notification"
    FOR EACH ROW
EXECUTE PROCEDURE PUBLIC.INSERT_OR_UPDATE_EPOCH_LOG();

# --- !Downs
DROP TRIGGER IF EXISTS COLLECTION_PROPERTY_LOG ON MASTER."CollectionProperty" CASCADE;
DROP TRIGGER IF EXISTS NOTIFICATION_LOG ON MASTER_TRANSACTION."Notification" CASCADE;

DROP TABLE IF EXISTS MASTER."CollectionProperty" CASCADE;
DROP TABLE IF EXISTS MASTER_TRANSACTION."Notification" CASCADE;