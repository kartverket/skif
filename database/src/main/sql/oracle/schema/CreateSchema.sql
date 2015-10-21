-- Brukes for å sende array av Number, String, (Number,Number), (Number,String), eller (String,String) til databasen basert på Oracle Array extension for JDBC
CREATE TYPE NUMBER_LIST_TYPE AS TABLE OF NUMBER(19,0);
CREATE TYPE STRING_LIST_TYPE AS TABLE OF VARCHAR2(256 CHAR);
CREATE TYPE NUMBER_NUMBER_TYPE AS OBJECT (value1 NUMBER, value2 NUMBER);
CREATE TYPE NUMBER_NUMBER_LIST_TYPE AS TABLE OF NUMBER_NUMBER_TYPE;
CREATE TYPE NUMBER_STRING_TYPE AS OBJECT (value NUMBER, valueClass VARCHAR2(255 CHAR));
CREATE TYPE NUMBER_STRING_LIST_TYPE AS TABLE OF NUMBER_STRING_TYPE;
CREATE TYPE STRING_STRING_TYPE AS OBJECT (value VARCHAR2(255 CHAR), valueClass VARCHAR2(255 CHAR));
CREATE TYPE STRING_STRING_LIST_TYPE AS TABLE OF STRING_STRING_TYPE;

------------------------------------------------------------------------------------------------------------------------
-- Applikasjonsspesifike tabeller
------------------------------------------------------------------------------------------------------------------------

CREATE TABLE TableSequence (
   TABLENAME VARCHAR2(255 CHAR) NOT NULL,
   NEXTFREENUMBER NUMBER(19,0),
   CONSTRAINT PK_TABLESEQUENCE PRIMARY KEY (TABLENAME)
);

CREATE TABLE Lockinfo (
   ID NUMBER(19,0) NOT NULL,
   CLASS VARCHAR2(255 CHAR) NOT NULL,
   OWNER VARCHAR2(255 CHAR) NOT NULL,
   EXPIRES TIMESTAMP NOT NULL,
   CONSTRAINT PK_LOCKINFO PRIMARY KEY (ID, CLASS)
);
