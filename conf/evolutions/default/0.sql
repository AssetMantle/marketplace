-- Create database and user scripts. To be run before evolutions are started.

CREATE USER "mantlePlace" WITH PASSWORD 'mantlePlace';

CREATE DATABASE "mantlePlace" WITH OWNER = "mantlePlace";

ALTER USER "mantlePlace" SET SEARCH_PATH = "$user", MASTER, MASTER_TRANSACTION, BLOCKCHAIN, BLOCKCHAIN_TRANSACTION;

-- Should be implemented on explorer db

CREATE ROLE "readAccess";

GRANT CONNECT ON DATABASE "assetMantle" TO "readAccess";

GRANT USAGE ON SCHEMA public TO "readAccess";

GRANT SELECT ON ALL TABLES IN SCHEMA public TO "readAccess";

CREATE USER "assetMantleRead" WITH PASSWORD 'assetMantleRead';

GRANT "readAccess" TO "assetMantleRead";