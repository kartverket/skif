create table TestEntity (
id number(19,0) not null,
Text Varchar2(255),
Primary Key (Id)
);

create table TestBubble(
id number(19,0) not null,
text varchar2(255),
Primary Key (Id)
);

create table TestMap (
k varchar2(255) not null,
v Varchar2(255),
Primary Key (k)
);

create table TableSequence (
   tableName varchar2(255) not null,
   nextFreeNumber number(19,0),
   primary key (tableName)
);

create table LockInfo (
   id number(19,0) not null,
   class varchar2(255) not null,
   owner varchar2(255) not null,
   expires timestamp not null,
   primary key (id, class)
);

create table AKode ( id number(19,0) not null, kodeVerdi varchar2(10) not null, primary key (id) );
create table AKodeLoc ( id number(19,0) not null, lokale varchar2(10) not null, beskrivelse varchar2(255) not null, primary key (id, lokale));
alter table AKodeLoc add constraint FK_AKodeLoc foreign key (id) references AKode;

create table BKode ( id number(19,0) not null, kodeVerdi varchar2(10) not null, primary key (id) );
create table BKodeLoc ( id number(19,0) not null, lokale varchar2(10) not null, beskrivelse varchar2(255) not null, primary key (id, lokale));
alter table BKodeLoc add constraint FK_BKodeLoc foreign key (id) references BKode;

create table CKode ( id number(19,0) not null, kodeVerdi varchar2(10) not null, class varchar2(64) not null, primary key (id) );
create table CKodeLoc ( id number(19,0) not null, lokale varchar2(10) not null, beskrivelse varchar2(255) not null, primary key (id, lokale));
alter table CKodeLoc add constraint FK_CKodeLoc foreign key (id) references CKode;

create table Kodeliste( id number(19,0) not null, navn varchar2(64), kodeClassname varchar2(255), primary key(id));
create table KodelisteLoc ( id number(19,0) not null, lokale varchar2(10) not null, beskrivelse varchar2(255) not null, primary key (id, lokale));
alter table KodelisteLoc add constraint FK_TestKodelisteLoc foreign key (id) references Kodeliste;

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

CREATE OR REPLACE TRIGGER FOO_TRIGGER
INSTEAD OF INSERT OR UPDATE OR DELETE ON FOO
FOR EACH ROW
DECLARE
t_Trans TIMESTAMP := snapshot_time.get_t();
t_End TIMESTAMP := snapshot_time.to_t('9999-01-01 00:00:00.00');
BEGIN
  IF UPDATING THEN
    IF :old.tBegin < t_Trans THEN
        INSERT INTO FOO_H
        VALUES (:old.id, :old.tBegin, t_Trans, :old.tversion, :old.nr, :old.navn);

        UPDATE FOO_H SET tVersion = :old.tVersion + 1 WHERE id= :new.id and tEnd = t_End;
    END IF;
    UPDATE FOO_H
    SET id = :new.id, tBegin = t_Trans, nr = :new.nr, navn = :new.navn
    WHERE id = :new.id and tEnd = t_End;
  ELSIF INSERTING THEN
    INSERT INTO FOO_H
        VALUES (:new.id, t_Trans,t_End, 1, :new.nr, :new.navn);
  ELSIF DELETING THEN
    IF :old.tBegin < t_Trans THEN
        INSERT INTO FOO_H
        VALUES (:old.id, :old.tBegin, t_Trans, :old.tversion, :old.nr, :old.navn);
    END IF;
    delete from FOO_H
    WHERE id = :old.id and tEnd = t_End;
  END IF;
END FOO_TRIGGER;
/

-- Denne tabell har ikke historikk
CREATE TABLE Baz (
    id                   NUMBER(19,0) NOT NULL ENABLE,
    text                 VARCHAR2(255 BYTE),
    fooId                number(19,0) not null,
    PRIMARY KEY (id)
);


CREATE TABLE BAR_H (
    id                   NUMBER(19,0) NOT NULL ENABLE,
    tBegin               timestamp(6) not null,
    tEnd                 timestamp(6) not null,
    tVersion             number (19,0) not null,
    husnr                number(10,0),
    bokstav              VARCHAR2(255 BYTE),
    fooId                number(19,0) not null,
    bazId                number(19,0),
    PRIMARY KEY (ID, tEnd)
);
create view BAR as select * from BAR_H  where snapshot_time.t_between(tBegin, tEnd)=1;

CREATE OR REPLACE TRIGGER BAR_TRIGGER
INSTEAD OF INSERT OR UPDATE OR DELETE ON BAR
FOR EACH ROW
DECLARE
t_Trans TIMESTAMP := snapshot_time.get_t();
t_End TIMESTAMP := snapshot_time.to_t('9999-01-01 00:00:00.00');
BEGIN
  IF UPDATING THEN
    IF :old.tBegin < t_Trans THEN
        INSERT INTO BAR_H
        VALUES (:old.id, :old.tBegin, t_Trans, :old.tversion, :old.husnr, :old.bokstav, :old.fooId, :old.bazId);

        UPDATE BAR_H SET tVersion = :old.tVersion + 1 WHERE id= :new.id and tEnd = t_End;
    END IF;
    UPDATE BAR_H
    SET id = :new.id, tBegin = t_Trans, nr = :new.husnr, navn = :new.bokstav, fooId = :new.fooId, bazId = :new.bazId
    WHERE id = :new.id and tEnd = t_End;
  ELSIF INSERTING THEN
    INSERT INTO BAR_H
        VALUES (:new.id, t_Trans,t_End, 1, :new.husnr, :new.bokstav, :new.fooId, :new.bazId);
  ELSIF DELETING THEN
    IF :old.tBegin < t_Trans THEN
        INSERT INTO BAR_H
        VALUES (:old.id, :old.tBegin, t_Trans, :old.tversion, :old.husnr, :old.bokstav, :old.fooId, :old.bazId);
    END IF;
    delete from BAR_H
    WHERE id = :old.id and tEnd = t_End;
  END IF;
END BAR_TRIGGER;
/

CREATE TABLE BARFOOS_H (
    id                   NUMBER(19,0) NOT NULL ENABLE,
    tBegin               timestamp(6) not null,
    tEnd                 timestamp(6) not null,
    tVersion             number (19,0) not null,
    text                 VARCHAR2(255 BYTE),
    barId                number(19,0) not null,
    PRIMARY KEY (ID, tEnd)
);
create view BARFOOS as select * from BARFOOS_H  where snapshot_time.t_between(tBegin, tEnd)=1;

CREATE OR REPLACE TRIGGER BARFOOS_TRIGGER
INSTEAD OF INSERT OR UPDATE OR DELETE ON BARFOOS
FOR EACH ROW
DECLARE
t_Trans TIMESTAMP := snapshot_time.get_t();
t_End TIMESTAMP := snapshot_time.to_t('9999-01-01 00:00:00.00');
BEGIN
  IF UPDATING THEN
    IF :old.tBegin < t_Trans THEN
        INSERT INTO BARFOOS_H
        VALUES (:old.id, :old.tBegin, t_Trans, :old.tversion, :old.text, :old.barId);

        UPDATE BARFOOS_H SET tVersion = :old.tVersion + 1 WHERE id= :new.id and tEnd = t_End;
    END IF;
    UPDATE BARFOOS_H
    SET id = :new.id, tBegin = t_Trans, text = :new.text, barId = :new.barId
    WHERE id = :new.id and tEnd = t_End;
  ELSIF INSERTING THEN
    INSERT INTO BARFOOS_H
        VALUES (:new.id, t_Trans,t_End, 1, :new.text, :new.barId);
  ELSIF DELETING THEN
    IF :old.tBegin < t_Trans THEN
        INSERT INTO BARFOOS_H
        VALUES (:old.id, :old.tBegin, t_Trans, :old.tversion, :old.text, :old.barId);
    END IF;
    delete from BARFOOS_H
    WHERE id = :old.id and tEnd = t_End;
  END IF;
END BARFOOS_TRIGGER;
/

CREATE TABLE FooForBarFoos_H (
    barFoosId             NUMBER(19,0) not null,
    fooId                NUMBER(19,0) not null,
    tBegin               timestamp(6) not null,
    tEnd                 timestamp(6) not null,
    PRIMARY KEY (barFoosId, fooId, tEnd)
);
create view FooForBarFoos as select * from FooForBarFoos_H where snapshot_time.t_between(tBegin, tEnd)=1;

CREATE OR REPLACE TRIGGER FooForBarFoos_TRIGGER
INSTEAD OF INSERT OR UPDATE OR DELETE ON FooForBarFoos
FOR EACH ROW
DECLARE
t_Trans TIMESTAMP := snapshot_time.get_t();
t_End TIMESTAMP := snapshot_time.to_t('9999-01-01 00:00:00.00');
BEGIN
  IF UPDATING THEN
    IF :old.tBegin < t_Trans THEN
        INSERT INTO FooForBarFoos_H
        VALUES (:old.barFoosId, :old.fooId, :old.tBegin, t_Trans);
    END IF;
    UPDATE FooForBarFoos_H
    SET barFoosId = :new.barFoosId, fooId = :new.fooId and tBegin = t_Trans
    WHERE barFoosId = :new.barFoosId and fooId = :new.fooId and tEnd = t_End;
  ELSIF INSERTING THEN
    INSERT INTO FooForBarFoos_H
        VALUES (:new.barFoosId, :new.barId, t_Trans,t_End);
  ELSIF DELETING THEN
    IF :old.tBegin < t_Trans THEN
        INSERT INTO FooForBarFoos_H
        VALUES (:old.barFoosId, :old.barId, :old.tBegin, t_Trans);
    END IF;
    delete from FooForBarFoos_H
    WHERE barFoosId = :old.barFoosId and fooId = :old.fooId and tEnd = t_End;
  END IF;
END BARFOOS_TRIGGER;
/

