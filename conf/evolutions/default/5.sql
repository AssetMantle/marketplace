# --- !Ups

CREATE TABLE IF NOT EXISTS MASTER."NFTTag"
(
    "hashTag"              VARCHAR NOT NULL,
    "fileName"             VARCHAR NOT NULL,
    "createdBy"            VARCHAR,
    "createdOnMillisEpoch" BIGINT,
    "updatedBy"            VARCHAR,
    "updatedOnMillisEpoch" BIGINT,
    PRIMARY KEY ("hashTag", "fileName")
);

CREATE TABLE IF NOT EXISTS MASTER_TRANSACTION."CollectionDraft"
(
    "id"                   VARCHAR NOT NULL,
    "creatorId"            VARCHAR NOT NULL,
    "name"                 VARCHAR NOT NULL,
    "description"          VARCHAR NOT NULL,
    "socialProfiles"       VARCHAR NOT NULL,
    "category"             VARCHAR NOT NULL,
    "nsfw"                 BOOLEAN NOT NULL,
    "properties"           VARCHAR NOT NULL,
    "profileFileName"      VARCHAR,
    "coverFileName"        VARCHAR,
    "createdBy"            VARCHAR,
    "createdOnMillisEpoch" BIGINT,
    "updatedBy"            VARCHAR,
    "updatedOnMillisEpoch" BIGINT,
    PRIMARY KEY ("id")
);

CREATE TABLE IF NOT EXISTS MASTER_TRANSACTION."NFTDraft"
(
    "fileName"             VARCHAR NOT NULL,
    "collectionId"         VARCHAR NOT NULL,
    "name"                 VARCHAR,
    "description"          VARCHAR,
    "properties"           VARCHAR,
    "hashTags"             VARCHAR,
    "createdBy"            VARCHAR,
    "createdOnMillisEpoch" BIGINT,
    "updatedBy"            VARCHAR,
    "updatedOnMillisEpoch" BIGINT,
    PRIMARY KEY ("fileName")
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
    ADD COLUMN IF NOT EXISTS "properties" VARCHAR default null;
ALTER TABLE MASTER."Collection"
    ADD COLUMN IF NOT EXISTS "profileFileName" VARCHAR default null;
ALTER TABLE MASTER."Collection"
    ADD COLUMN IF NOT EXISTS "coverFileName" VARCHAR default null;
ALTER TABLE MASTER."Collection"
    ADD COLUMN IF NOT EXISTS "public" BOOLEAN NOT NULL default true;
ALTER TABLE MASTER."Collection"
    ADD COLUMN IF NOT EXISTS "category" VARCHAR NOT NULL default 'ART';
ALTER TABLE MASTER."Collection"
    ADD COLUMN IF NOT EXISTS "nsfw" BOOLEAN NOT NULL default false;
ALTER TABLE MASTER."NFT"
    DROP COLUMN IF EXISTS "file";
ALTER TABLE MASTER."NFT"
    ADD COLUMN IF NOT EXISTS "baseProperties" VARCHAR NOT NULL default '[]';
ALTER TABLE MASTER."Collection"
    DROP CONSTRAINT IF EXISTS "Collection_name_key";
ALTER TABLE MASTER."Collection"
    ADD CONSTRAINT uniqueClassificationId UNIQUE ("classificationId");

ALTER TABLE MASTER."NFTTag"
    ADD CONSTRAINT NFTTag_fileName FOREIGN KEY ("fileName") REFERENCES MASTER."NFT" ("fileName");

ALTER TABLE MASTER_TRANSACTION."CollectionDraft"
    ADD CONSTRAINT CollectionDraft_Creator_Id FOREIGN KEY ("creatorId") REFERENCES MASTER."Account" ("id");
ALTER TABLE MASTER_TRANSACTION."NFTDraft"
    ADD CONSTRAINT NFTDraft_Collection_Id FOREIGN KEY ("collectionId") REFERENCES MASTER."Collection" ("id");
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

CREATE TRIGGER NFT_TAG_LOG
    BEFORE INSERT OR UPDATE
    ON MASTER."NFTTag"
    FOR EACH ROW
EXECUTE PROCEDURE PUBLIC.INSERT_OR_UPDATE_EPOCH_LOG();

CREATE TRIGGER COLLECTION_DRAFT_LOG
    BEFORE INSERT OR UPDATE
    ON MASTER_TRANSACTION."CollectionDraft"
    FOR EACH ROW
EXECUTE PROCEDURE PUBLIC.INSERT_OR_UPDATE_EPOCH_LOG();
CREATE TRIGGER NOTIFICATION_LOG
    BEFORE INSERT OR UPDATE
    ON MASTER_TRANSACTION."Notification"
    FOR EACH ROW
EXECUTE PROCEDURE PUBLIC.INSERT_OR_UPDATE_EPOCH_LOG();
CREATE TRIGGER NFT_DRAFT_LOG
    BEFORE INSERT OR UPDATE
    ON MASTER_TRANSACTION."NFTDraft"
    FOR EACH ROW
EXECUTE PROCEDURE PUBLIC.INSERT_OR_UPDATE_EPOCH_LOG();

# --- !Downs
DROP TRIGGER IF EXISTS NFT_TAG_LOG ON MASTER."NFTTag" CASCADE;

DROP TRIGGER IF EXISTS COLLECTION_DRAFT_LOG ON MASTER_TRANSACTION."CollectionDraft" CASCADE;
DROP TRIGGER IF EXISTS NOTIFICATION_LOG ON MASTER_TRANSACTION."Notification" CASCADE;
DROP TRIGGER IF EXISTS NFT_DRAFT_LOG ON MASTER_TRANSACTION."NFTDraft" CASCADE;

DROP TABLE IF EXISTS MASTER."NFTTag" CASCADE;

DROP TABLE IF EXISTS MASTER_TRANSACTION."CollectionDraft" CASCADE;
DROP TABLE IF EXISTS MASTER_TRANSACTION."Notification" CASCADE;
DROP TABLE IF EXISTS MASTER_TRANSACTION."NFTDraft" CASCADE;