# --- !Ups

CREATE TABLE IF NOT EXISTS MASTER."Whitelist"
(
    "id"                VARCHAR NOT NULL,
    "ownerId"           VARCHAR NOT NULL,
    "name"              VARCHAR NOT NULL,
    "description"       VARCHAR NOT NULL,
    "maxMembers"        INTEGER NOT NULL,
    "startEpoch"        BIGINT  NOT NULL,
    "endEpoch"          BIGINT  NOT NULL,
    "createdBy"         VARCHAR,
    "createdOn"         TIMESTAMP,
    "createdOnTimeZone" VARCHAR,
    "updatedBy"         VARCHAR,
    "updatedOn"         TIMESTAMP,
    "updatedOnTimeZone" VARCHAR,
    PRIMARY KEY ("id")
);

CREATE TABLE IF NOT EXISTS MASTER."WhitelistMember"
(
    "whitelistId"       VARCHAR NOT NULL,
    "accountId"         VARCHAR NOT NULL,
    "createdBy"         VARCHAR,
    "createdOn"         TIMESTAMP,
    "createdOnTimeZone" VARCHAR,
    "updatedBy"         VARCHAR,
    "updatedOn"         TIMESTAMP,
    "updatedOnTimeZone" VARCHAR,
    PRIMARY KEY ("whitelistId", "accountId")
);

ALTER TABLE MASTER."Whitelist"
    ADD CONSTRAINT Whitelist_Account_Id FOREIGN KEY ("ownerId") REFERENCES MASTER."Account" ("id");
ALTER TABLE MASTER."WhitelistMember"
    ADD CONSTRAINT WhitelistMember_Whitelist_Id FOREIGN KEY ("whitelistId") REFERENCES MASTER."Whitelist" ("id");
ALTER TABLE MASTER."WhitelistMember"
    ADD CONSTRAINT WhitelistMember_Account_Id FOREIGN KEY ("accountId") REFERENCES MASTER."Account" ("id");

ALTER TABLE MASTER."CollectionFile"
    ADD CONSTRAINT CollectionFile_Collection_Id FOREIGN KEY ("id") REFERENCES MASTER."Collection" ("id");
ALTER TABLE MASTER."Collection"
    ADD COLUMN IF NOT EXISTS "creatorId" VARCHAR NOT NULL default 'abhinav95';
ALTER TABLE MASTER."Collection"
    ADD CONSTRAINT Collection_Account_Id FOREIGN KEY ("creatorId") REFERENCES MASTER."Account" ("id");

CREATE TRIGGER WHITE_LIST_LOG
    BEFORE INSERT OR UPDATE
    ON MASTER."Whitelist"
    FOR EACH ROW
EXECUTE PROCEDURE PUBLIC.INSERT_OR_UPDATE_LOG();
CREATE TRIGGER WHITE_LIST_MEMBER_LOG
    BEFORE INSERT OR UPDATE
    ON MASTER."WhitelistMember"
    FOR EACH ROW
EXECUTE PROCEDURE PUBLIC.INSERT_OR_UPDATE_LOG();

# --- !Downs

DROP TRIGGER IF EXISTS WHITE_LIST_LOG ON MASTER."Whitelist" CASCADE;
DROP TRIGGER IF EXISTS WHITE_LIST_MEMBER_LOG ON MASTER."WhitelistMember" CASCADE;

DROP TABLE IF EXISTS MASTER."Whitelist" CASCADE;
DROP TABLE IF EXISTS MASTER."WhitelistMember" CASCADE;