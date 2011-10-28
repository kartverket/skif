---som system-bruker
---=================

drop user @db_history_schema_name@ cascade;
create user @db_history_schema_name@ identified by @db_history_schema_name@;
grant CREATE SESSION to @db_history_schema_name@;
grant create table to @db_history_schema_name@;
GRANT RESOURCE TO @db_history_schema_name@;


