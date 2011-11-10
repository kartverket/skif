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

CREATE OR REPLACE PACKAGE snapshot_time
As
    Function Get_T Return Timestamp;
    Function Set_T(Newvalue In Timestamp) Return Timestamp;
    Function To_T(timestampAsString IN VARCHAR2) Return Timestamp;
    Function T_Between(tBegin In Timestamp, tEnd In Timestamp) Return Number;
END snapshot_time;
/

CREATE OR REPLACE PACKAGE BODY snapshot_time
As
    T Timestamp;
    t_End TIMESTAMP := snapshot_time.to_t('9999-01-01 00:00:00.00');
    Function Get_T
    RETURN Timestamp
    IS
    BEGIN
      Return T;
    End Get_T;

    Function Set_T(Newvalue In Timestamp)
    RETURN timestamp
    IS
    Begin
      T:= newValue;
      Return T;
    End Set_T;

    Function To_T(timestampAsString In Varchar2)
    Return Timestamp
    Is
    Begin
      Return To_Timestamp (Timestampasstring, 'YYYY-MM-DD HH24:MI:SS.FF');
    end To_T;

    Function T_Between(Tbegin In Timestamp, Tend In Timestamp)
    Return Number
    Is
    retval NUMBER;
    BEGIN
                IF (TBEGIN<=T AND (T<TEND OR TEND=t_END))
                THEN
                    retVal := 1;
                ELSE
                    retVal := 0;
                END IF;
                RETURN retVal;
    End T_Between;
END snapshot_time;
/

CREATE TABLE FOO_H (
    id                   NUMBER(19,0) NOT NULL ENABLE,
    tBegin               timestamp(6) not null,
    tEnd                 timestamp(6) not null,
    tVersion             number (19,0) not null,
    nr                   number(10,0),
    navn                 VARCHAR2(255 BYTE),
    PRIMARY KEY (ID, tEnd)
);
create view FOO as select * from FOO_H  where snapshot_time.t_between(tBegin, tEnd)=1;
