---som system-bruker
---=================

drop user @db_history_schema_name@ cascade;
create user @db_history_schema_name@ identified by @db_history_schema_name@;
grant CREATE SESSION to @db_history_schema_name@;
grant create table to @db_history_schema_name@;
grant create view to @db_history_schema_name@;
GRANT RESOURCE TO @db_history_schema_name@;

drop user @db_non_history_schema_name@ cascade;
create user @db_non_history_schema_name@ identified by @db_non_history_schema_name@;
grant CREATE SESSION to @db_non_history_schema_name@;
grant create table to @db_non_history_schema_name@;
grant create view to @db_non_history_schema_name@;
GRANT RESOURCE TO @db_non_history_schema_name@;

