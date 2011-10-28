---som hist_brukeren
---=================


create table TestHistoricEntity (
id number(19,0) not null,
Text Varchar2(255),
Primary Key (Id)
);

CREATE TABLE TESTHISTORICBUBBLE
 (	"ID" NUMBER(19,0) NOT NULL,
  "TEXT" VARCHAR2(255 BYTE),
   PRIMARY KEY ("ID")
 );

GRANT select ON TestHistoricEntity TO @db_non_history_schema_name@;
GRANT insert ON TestHistoricEntity TO @db_non_history_schema_name@;
GRANT update ON TestHistoricEntity TO @db_non_history_schema_name@;
GRANT delete ON TestHistoricEntity TO @db_non_history_schema_name@;

GRANT select ON TestHistoricBubble TO @db_non_history_schema_name@;
GRANT insert ON TestHistoricBubble TO @db_non_history_schema_name@;
GRANT update ON TestHistoricBubble TO @db_non_history_schema_name@;
GRANT delete ON TestHistoricBubble TO @db_non_history_schema_name@;

insert into TestHistoricEntity values (1, 'Test');
insert into TestHistoricBubble values (1, 'Test');