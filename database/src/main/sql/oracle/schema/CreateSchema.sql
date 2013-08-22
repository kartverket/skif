-- Brukes for å sende array av Number eller String til databasen basert på Oracle Array extension for JDBC
CREATE TYPE NUMBER_LIST_TYPE AS TABLE OF NUMBER(19,0);
CREATE TYPE STRING_LIST_TYPE AS TABLE OF VARCHAR2(256 CHAR);

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
