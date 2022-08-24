# --- !Ups

CREATE TABLE IF NOT EXISTS MASTER."WhiteList"
(
    "id"                VARCHAR NOT NULL,
    "ownerId"           VARCHAR NOT NULL,
    "name"              VARCHAR NOT NULL,
    "description"       VARCHAR NOT NULL,
    "maxMembers"        INTEGER NOT NULL,
    "createdBy"         VARCHAR,
    "createdOn"         TIMESTAMP,
    "createdOnTimeZone" VARCHAR,
    "updatedBy"         VARCHAR,
    "updatedOn"         TIMESTAMP,
    "updatedOnTimeZone" VARCHAR,
    PRIMARY KEY ("id")
);

CREATE TABLE IF NOT EXISTS MASTER."WhiteListMember"
(
    "whiteListId"       VARCHAR NOT NULL,
    "accountId"         VARCHAR NOT NULL,
    "createdBy"         VARCHAR,
    "createdOn"         TIMESTAMP,
    "createdOnTimeZone" VARCHAR,
    "updatedBy"         VARCHAR,
    "updatedOn"         TIMESTAMP,
    "updatedOnTimeZone" VARCHAR,
    PRIMARY KEY ("whiteListId", "accountId")
);

CREATE TABLE IF NOT EXISTS MASTER_TRANSACTION."WhiteListInvite"
(
    "id"                VARCHAR NOT NULL,
    "whiteListId"       VARCHAR NOT NULL,
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

ALTER TABLE MASTER."WhiteList"
    ADD CONSTRAINT WhiteList_Account_Id FOREIGN KEY ("ownerId") REFERENCES MASTER."Account" ("id");
ALTER TABLE MASTER."WhiteListMember"
    ADD CONSTRAINT WhiteListMember_WhiteList_Id FOREIGN KEY ("whiteListId") REFERENCES MASTER."WhiteList" ("id");
ALTER TABLE MASTER."WhiteListMember"
    ADD CONSTRAINT WhiteListMember_Account_Id FOREIGN KEY ("accountId") REFERENCES MASTER."Account" ("id");

ALTER TABLE MASTER_TRANSACTION."WhiteListInvite"
    ADD CONSTRAINT WhiteListInvite_WhiteList_Id FOREIGN KEY ("whiteListId") REFERENCES MASTER."WhiteList" ("id");

CREATE TRIGGER WHITE_LIST_LOG
    BEFORE INSERT OR UPDATE
    ON MASTER."WhiteList"
    FOR EACH ROW
EXECUTE PROCEDURE PUBLIC.INSERT_OR_UPDATE_LOG();
CREATE TRIGGER WHITE_LIST_MEMBER_LOG
    BEFORE INSERT OR UPDATE
    ON MASTER."WhiteListMember"
    FOR EACH ROW
EXECUTE PROCEDURE PUBLIC.INSERT_OR_UPDATE_LOG();

CREATE TRIGGER WHITE_LIST_INVITE_MEMBER_LOG
    BEFORE INSERT OR UPDATE
    ON MASTER_TRANSACTION."WhiteListInvite"
    FOR EACH ROW
EXECUTE PROCEDURE PUBLIC.INSERT_OR_UPDATE_LOG();

# --- !Downs

DROP TRIGGER IF EXISTS WHITE_LIST_MEMBER_LOG ON MASTER."WhiteListMember" CASCADE;
DROP TRIGGER IF EXISTS WHITE_LIST_LOG ON MASTER."WhiteList" CASCADE;

DROP TRIGGER IF EXISTS WHITE_LIST_INVITE_MEMBER_LOG ON MASTER_TRANSACTION."WhiteListInvite" CASCADE;

DROP TABLE IF EXISTS MASTER."WhiteListMember" CASCADE;
DROP TABLE IF EXISTS MASTER."WhiteList" CASCADE;

DROP TABLE IF EXISTS MASTER_TRANSACTION."WhiteListInvite" CASCADE;