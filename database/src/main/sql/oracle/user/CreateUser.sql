--***************************************************************************
--
-- Kjores som systembruker
--
-- Oppretter bruker ('@db_username@')
--
--***************************************************************************

CREATE USER "@db_username@" IDENTIFIED BY "@db_password@"
  PROFILE "DEFAULT" DEFAULT TABLESPACE "@db_tablespace@"
  TEMPORARY TABLESPACE "TEMP"
  ACCOUNT UNLOCK;

GRANT UNLIMITED TABLESPACE TO "@db_username@";
GRANT ALTER SESSION TO "@db_username@";
GRANT CREATE SESSION TO "@db_username@";

-- trenger noen privilegier for opprettelse og patching av schema
-- todo: vurdere om en ønsker å revoke disse i byggesystemet etter at schema er opprettet og patchet opp.
GRANT CREATE SEQUENCE TO "@db_username@";
GRANT CREATE TABLE TO "@db_username@";
GRANT CREATE VIEW TO "@db_username@";
GRANT QUERY REWRITE TO "@db_username@";
GRANT CREATE TRIGGER TO "@db_username@";
GRANT CREATE TYPE TO "@db_username@";
GRANT CREATE PROCEDURE TO "@db_username@";
