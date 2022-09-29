# --- !Ups

ALTER TABLE MASTER."Collection"
    ADD COLUMN IF NOT EXISTS "category" VARCHAR NOT NULL default 'ART';
ALTER TABLE MASTER."Collection"
    ADD COLUMN IF NOT EXISTS "nsfw" BOOLEAN NOT NULL default false;

# --- !Downs