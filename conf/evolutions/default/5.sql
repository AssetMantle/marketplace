# --- !Ups

ALTER TABLE MASTER."Collection"
    ADD COLUMN IF NOT EXISTS "category" VARCHAR NOT NULL default 'ART';

# --- !Downs