# --- !Ups

ALTER TABLE MASTER_TRANSACTION."SessionToken"
    ADD COLUMN IF NOT EXISTS "createdOnMillisEpoch" BIGINT DEFAULT null;
ALTER TABLE MASTER_TRANSACTION."SessionToken"
    ADD COLUMN IF NOT EXISTS "updatedOnMillisEpoch" BIGINT DEFAULT null;
DROP TRIGGER IF EXISTS SESSION_TOKEN_LOG ON MASTER_TRANSACTION."SessionToken";
UPDATE MASTER_TRANSACTION."SessionToken"
SET "createdOnMillisEpoch" = FLOOR(EXTRACT(EPOCH FROM "createdOn" AT TIME ZONE "createdOnTimeZone") * 1000)
WHERE "createdOn" IS NOT NULL;
UPDATE MASTER_TRANSACTION."SessionToken"
SET "updatedOnMillisEpoch" = FLOOR(EXTRACT(EPOCH FROM "updatedOn" AT TIME ZONE "updatedOnTimeZone") * 1000)
WHERE "updatedOn" IS NOT NULL;
ALTER TABLE MASTER_TRANSACTION."SessionToken"
    DROP COLUMN IF EXISTS "createdOn";
ALTER TABLE MASTER_TRANSACTION."SessionToken"
    DROP COLUMN IF EXISTS "createdOnTimeZone";
ALTER TABLE MASTER_TRANSACTION."SessionToken"
    DROP COLUMN IF EXISTS "updatedOn";
ALTER TABLE MASTER_TRANSACTION."SessionToken"
    DROP COLUMN IF EXISTS "updatedOnTimeZone";
CREATE TRIGGER SESSION_TOKEN_LOG
    BEFORE INSERT OR UPDATE
    ON MASTER_TRANSACTION."SessionToken"
    FOR EACH ROW
EXECUTE PROCEDURE PUBLIC.INSERT_OR_UPDATE_EPOCH_LOG();

ALTER TABLE MASTER."Sale"
    ADD CONSTRAINT uniqueCollectionId UNIQUE ("collectionId");

# --- !Downs
