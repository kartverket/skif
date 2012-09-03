GRANT CREATE TYPE TO FREHEN;
GRANT CREATE PROCEDURE TO FREHEN;
GRANT CREATE ANY TRIGGER TO FREHEN;

-- trenger noen privilegier for opprettelse og patching av schema
-- todo: vurdere om en ønsker å revoke disse i byggesystemet etter at schema er opprettet og patchet opp.
GRANT CREATE SEQUENCE TO "FREHEN";
GRANT CREATE SESSION TO "FREHEN";
GRANT CREATE TABLE TO "FREHEN";
GRANT CREATE VIEW TO "FREHEN";
