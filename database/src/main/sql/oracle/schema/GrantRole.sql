GRANT CREATE TYPE TO "@db_username@";
GRANT CREATE PROCEDURE TO "@db_username@";
GRANT CREATE ANY TRIGGER TO "@db_username@";

-- trenger noen privilegier for opprettelse og patching av schema
-- todo: vurdere om en ønsker å revoke disse i byggesystemet etter at schema er opprettet og patchet opp.
GRANT CREATE SEQUENCE TO "@db_username@";
GRANT CREATE SESSION TO "@db_username@";
GRANT CREATE TABLE TO "@db_username@";
GRANT CREATE VIEW TO "@db_username@";
