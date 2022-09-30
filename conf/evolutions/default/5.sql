# --- !Ups

CREATE TABLE IF NOT EXISTS MASTER."CollectionProperty"
(
    "id"                VARCHAR NOT NULL,
    "propertyName"      VARCHAR NOT NULL,
    "propertyType"      VARCHAR NOT NULL,
    "required"          BOOLEAN NOT NULL,
    "mutable"           BOOLEAN NOT NULL,
    "hideValue"         BOOLEAN NOT NULL,
    "fixedValue"        VARCHAR,
    "createdBy"         VARCHAR,
    "createdOn"         TIMESTAMP,
    "createdOnTimeZone" VARCHAR,
    "updatedBy"         VARCHAR,
    "updatedOn"         TIMESTAMP,
    "updatedOnTimeZone" VARCHAR,
    PRIMARY KEY ("id", "propertyName")
);

ALTER TABLE MASTER."Collection"
    ADD COLUMN IF NOT EXISTS "category" VARCHAR NOT NULL default 'ART';
ALTER TABLE MASTER."Collection"
    ADD COLUMN IF NOT EXISTS "nsfw" BOOLEAN NOT NULL default false;

ALTER TABLE MASTER."CollectionProperty"
    ADD CONSTRAINT CollectionProperty_Collection_Id FOREIGN KEY ("id") REFERENCES MASTER."Collection" ("id");

CREATE TRIGGER COLLECTION_PROPERTY_LOG
    BEFORE INSERT OR UPDATE
    ON MASTER."CollectionProperty"
    FOR EACH ROW
EXECUTE PROCEDURE PUBLIC.INSERT_OR_UPDATE_LOG();
# --- !Downs