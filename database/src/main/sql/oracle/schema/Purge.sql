PROMPT purging views for user @db_username@
BEGIN
  FOR i IN (SELECT object_name FROM all_objects where owner = '@db_username@' AND object_type = 'VIEW')
    LOOP
        EXECUTE IMMEDIATE('DROP VIEW @db_schema@.' || i.object_name);
    END LOOP;
END;
/

COMMIT;

PROMPT purging tables for user @db_username@
BEGIN
  FOR i IN (SELECT table_name FROM all_tables where owner = '@db_username@' and table_name not like '%$%')
    LOOP
        EXECUTE IMMEDIATE('DROP TABLE @db_schema@.' || i.table_name || ' CASCADE CONSTRAINTS PURGE');
    END LOOP;
END;
/

PROMPT purging packages for user @db_username@
BEGIN
  FOR i IN (SELECT object_name FROM all_objects where owner = '@db_username@' AND object_type = 'PACKAGE')
    LOOP
        EXECUTE IMMEDIATE('DROP PACKAGE @db_schema@.' || i.object_name);
    END LOOP;
END;
/

PROMPT purging functions for user @db_username@
BEGIN
  FOR i IN (SELECT object_name FROM all_objects where owner = '@db_username@' AND object_type = 'FUNCTION')
    LOOP
        EXECUTE IMMEDIATE('DROP FUNCTION @db_schema@.' || i.object_name);
    END LOOP;
END;
/

PROMPT purging synonyms for user @db_username@
BEGIN
  FOR i IN (SELECT object_name FROM all_objects where owner = '@db_username@' AND object_type = 'SYNONYM')
    LOOP
        EXECUTE IMMEDIATE('DROP SYNONYM @db_schema@.' || i.object_name);
    END LOOP;
END;
/

PROMPT purging sdo_metadata for user @oracle_db_username@
BEGIN
  DELETE FROM USER_SDO_GEOM_METADATA;
END;
/

PROMPT purge successfull!;
