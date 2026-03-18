--***************************************************************************
--
-- Kjores som systembruker
--
-- Oppretter
--    Tablespace for mockupdata
--***************************************************************************

--Create tablespace
CREATE TABLESPACE "@db_tablespace@"
DATAFILE '@db_dataroot_dir@/@db_tablespace@.DBF' SIZE 100M
LOGGING
EXTENT MANAGEMENT LOCAL SEGMENT SPACE MANAGEMENT AUTO;
