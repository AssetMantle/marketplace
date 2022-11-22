-- Create database and user scripts. To be run before evolutions are started.

CREATE USER "mantlePlace" WITH PASSWORD 'mantlePlace';

CREATE DATABASE "mantlePlace" WITH OWNER = "mantlePlace";

ALTER USER "mantlePlace" SET SEARCH_PATH = "$user", MASTER, MASTER_TRANSACTION, BLOCKCHAIN, BLOCKCHAIN_TRANSACTION, ANALYTICS;

