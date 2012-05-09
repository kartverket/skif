
create table FilteredBubble (
  id number(19,0) not null,
  Text Varchar2(255),
  filter number(1,0),
  FilterText Varchar2(255),
  Primary Key (Id)
);


create table ParrentBubble (
  id number(19,0) not null,
  Text Varchar2(255),
  Primary Key (Id)
);

CREATE TABLE CHILDFORPARRENT(
  id number (19,0) not null,
  parrentBubbleId number(19,0) not null,
  childBubbleId number(19,0) not null,
  Primary Key (id)
);

create table ChildBubble(
  id number(19,0) not null,
  TEXT VARCHAR2(255),
  TESTBUBBLEID number(19,0),
  PRIMARY KEY (ID)
) ;

alter table ChildForParrent add constraint FK23723BEA16AF2FAB foreign key (parrentBubbleId) references ParrentBubble;
ALTER TABLE CHILDFORPARRENT ADD CONSTRAINT FK_CHILDFORPARRENT_CHILD FOREIGN KEY (CHILDBUBBLEID) REFERENCES CHILDBUBBLE;





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
create table AKodeLoc ( id number(19,0) not null, lokale varchar2(10) not null, navn varchar2(64) not null, beskrivelse varchar2(255) not null, primary key (id, lokale));
alter table AKodeLoc add constraint FK_AKodeLoc foreign key (id) references AKode;

create table BKode ( id number(19,0) not null, kodeVerdi varchar2(10) not null, primary key (id) );
create table BKodeLoc ( id number(19,0) not null, lokale varchar2(10) not null, navn varchar2(64) not null, beskrivelse varchar2(255) not null, primary key (id, lokale));
alter table BKodeLoc add constraint FK_BKodeLoc foreign key (id) references BKode;

create table CKode ( id number(19,0) not null, kodeVerdi varchar2(10) not null, class varchar2(64) not null, primary key (id) );
create table CKodeLoc ( id number(19,0) not null, lokale varchar2(10) not null, navn varchar2(64) not null, beskrivelse varchar2(255) not null, primary key (id, lokale));
alter table CKodeLoc add constraint FK_CKodeLoc foreign key (id) references CKode;

create table XStrKode ( id varchar2(10) not null, kodeVerdi varchar2(10) not null, primary key (id) );
create table XStrKodeLoc ( id varchar2(10) not null, lokale varchar2(10) not null, navn varchar2(64) not null, beskrivelse varchar2(255) not null, primary key (id, lokale));
alter table XStrKodeLoc add constraint FK_XStrKodeLoc foreign key (id) references XStrKode;

create table YStrKode ( id varchar2(10) not null, kodeVerdi varchar2(10) not null, primary key (id) );
create table YStrKodeLoc ( id varchar2(10) not null, lokale varchar2(10) not null, navn varchar2(64) not null, beskrivelse varchar2(255) not null, primary key (id, lokale));
alter table YStrKodeLoc add constraint FK_YStrKodeLoc foreign key (id) references YStrKode;

create table Kodeliste( id number(19,0) not null, kodeTypeNavn varchar2(64), kodeIdClassname varchar2(255), primary key(id));
create table KodelisteLoc ( id number(19,0) not null, lokale varchar2(10) not null, navn varchar2(64) not null, beskrivelse varchar2(255) not null, primary key (id, lokale));
alter table KodelisteLoc add constraint FK_TestKodelisteLoc foreign key (id) references Kodeliste;

create global temporary table SNAPSHOT_TRANS (v Timestamp) on commit delete rows;

CREATE OR REPLACE PACKAGE snapshot_time
As
    Function Get_T_CURRENT Return Timestamp;
    Function Get_T Return Timestamp;
    Function Set_T(Newvalue In Timestamp) Return Timestamp;
    Function Get_T_Trans Return Timestamp;
    Function To_T(timestampAsString IN VARCHAR2) Return Timestamp;
    Function T_Between(tBegin In Timestamp, tEnd In Timestamp) Return Number;
END snapshot_time;
/

CREATE OR REPLACE PACKAGE BODY snapshot_time
As
    T_CURRENT TIMESTAMP := snapshot_time.to_t('9999-01-01 00:00:00.00');

    t Timestamp;
    t_Trans Timestamp := T_CURRENT;

    Function Get_T_CURRENT
    RETURN Timestamp
    IS
    BEGIN
      Return T_CURRENT;
    End Get_T_CURRENT;

    Function Get_T
    RETURN Timestamp
    IS
    BEGIN
      Return t;
    End Get_T;

    Function Set_T(Newvalue In Timestamp)
    RETURN timestamp
    IS
    Begin
      t:= newValue;
      Return t;
    End Set_T;

    Function Get_T_Trans
    RETURN Timestamp
    IS
    tVal TIMESTAMP;
    BEGIN
      BEGIN
        select v into t_Trans from SNAPSHOT_TRANS;
        exception
        when NO_DATA_FOUND THEN
           t_Trans := NULL;
       END;
       IF t_Trans is NULL THEN
         t_Trans := LOCALTIMESTAMP;
         insert into SNAPSHOT_TRANS values(t_Trans);
       END IF;
      Return t_Trans;
    End Get_T_Trans;

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
                IF (TBEGIN<=T AND (T<TEND OR TEND=T_CURRENT))
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
t_Trans TIMESTAMP := snapshot_time.Get_T_Trans();
t_End TIMESTAMP := snapshot_time.Get_T_CURRENT();
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
    testAEnumKodeId      number(10,0) not null,
    testC2DbKodeId       number(19,0) not null,
    PRIMARY KEY (id)
);

-- Denne tabell har heller ikke historikk
CREATE TABLE Raz (
    id                   NUMBER(19,0) NOT NULL ENABLE,
    text                 VARCHAR2(255 BYTE),
    compText             VARCHAR2(255 BYTE),
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
t_Trans TIMESTAMP := snapshot_time.Get_T_Trans();
t_End TIMESTAMP := snapshot_time.Get_T_CURRENT();
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
t_Trans TIMESTAMP := snapshot_time.Get_T_Trans();
t_End TIMESTAMP := snapshot_time.Get_T_CURRENT();
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
t_Trans TIMESTAMP := snapshot_time.Get_T_Trans();
t_End TIMESTAMP := snapshot_time.Get_T_CURRENT();
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


CREATE TABLE GEOMETRICELEMENT_H (
  id                  NUMBER(19,0),
  point               MDSYS.SDO_GEOMETRY,
  polygon             MDSYS.SDO_GEOMETRY,
  tBegin              timestamp(6) not null,
  tEnd                timestamp(6) not null,
  tVersion            number (19,0) not null,
  primary key(id, tBegin)
);
create view GEOMETRICELEMENT as select * from GEOMETRICELEMENT_H where snapshot_time.t_between(tBegin, tEnd)=1;

CREATE OR REPLACE TRIGGER T_GEOMETRICELEMENT INSTEAD OF INSERT OR UPDATE OR DELETE ON GEOMETRICELEMENT
FOR EACH ROW
DECLARE
t_Trans TIMESTAMP := snapshot_time.Get_T_Trans();
t_End TIMESTAMP := snapshot_time.Get_T_CURRENT();
BEGIN
  IF INSERTING THEN
    INSERT INTO GEOMETRICELEMENT_H
        VALUES (:new.ID, point, polygon, t_Now, t_End, 1);

  ELSIF UPDATING THEN
     IF :old.tBegin < t_Now THEN
        INSERT INTO GEOMETRICELEMENT_H VALUES (:old.ID, :new.POINT, :new.POLYGON, :old.TBEGIN, t_Now, :old.TVERSION);
        UPDATE GEOMETRICELEMENT_H SET TVERSION = :old.TVERSION+1
        WHERE id = :new.id and tEnd = t_End;
     END IF;
     UPDATE TEIG_H SET ID=:new.ID, POINT=:new.POINT, POLYGON=:new.POLYGON, TBEGIN=t_Now, TEND=t_End, TVERSION=TVERSION
        WHERE id = :new.id and tEnd = t_End;

  ELSIF DELETING THEN
     IF :old.tBegin < t_Now THEN
        INSERT INTO GEOMETRICELEMENT_H
           VALUES (:old.ID, :old.POINT, :old.POLYGON, :old.TBEGIN, t_Now, :old.TVERSION);
     END IF;
     DELETE FROM GEOMETRICELEMENT_H
        WHERE id = :old.id AND tEnd = t_End;

  END IF;
END T_GEOMETRICELEMENT;
/

INSERT INTO USER_SDO_GEOM_METADATA VALUES ('GEOMETRICELEMENT_h', 'polygon', MDSYS.SDO_DIM_ARRAY( MDSYS.SDO_DIM_ELEMENT('X', 257000, 1352000, 0.0005), MDSYS.SDO_DIM_ELEMENT('Y', 6320000, 8050000, 0.0005)), NULL);
INSERT INTO USER_SDO_GEOM_METADATA VALUES ('GEOMETRICELEMENT_h', 'point', MDSYS.SDO_DIM_ARRAY( MDSYS.SDO_DIM_ELEMENT('X', 257000, 1352000, 0.0005), MDSYS.SDO_DIM_ELEMENT('Y', 6320000, 8050000, 0.0005)), NULL);

ALTER SESSION SET SORT_AREA_SIZE = 20000000;

CREATE INDEX geometricentity_spatial_idx ON geometricelement_H(polygon) INDEXTYPE IS MDSYS.SPATIAL_INDEX PARAMETERS ('layer_gtype=POLYGON');
CREATE INDEX geometricentity2_spatial_idx ON geometricelement_H(point) INDEXTYPE IS MDSYS.SPATIAL_INDEX PARAMETERS ('layer_gtype=POINT');

create table AggregertObjektMeta_h (
  id number(19,0) not null,
  sistOppdatertAv varchar2(255 char),
  sistOppdatert timestamp,
  tBegin              timestamp(6) not null,
  tEnd                timestamp(6) not null,
  tVersion            number (19,0) not null,
  primary key (id, tend)
);
create table AggregertObjekt_H (
  id number(19,0) not null,
  tekst varchar2(255 char),
  tBegin              timestamp(6) not null,
  tEnd                timestamp(6) not null,
  tVersion            number (19,0) not null,
  primary key (id, tend)
);
create table AggregertKomponent_h (
  id number(19,0) not null,
  indeks number(10,0) not null,
  noe varchar2(255 char),
  annet number(10,0),
  tBegin              timestamp(6) not null,
  tEnd                timestamp(6) not null,
  tVersion            number (19,0) not null,
  primary key (id, indeks, tend)
);

create view AggregertObjektMeta as select * from AggregertObjektMeta_H where snapshot_time.t_between(tBegin, tEnd)=1;
CREATE OR REPLACE TRIGGER T_AggregertObjektMeta INSTEAD OF INSERT OR UPDATE OR DELETE ON AggregertObjektMeta
FOR EACH ROW
DECLARE
t_Trans TIMESTAMP := snapshot_time.Get_T_Trans();
t_End TIMESTAMP := snapshot_time.Get_T_CURRENT();
BEGIN
  IF INSERTING THEN
    INSERT INTO AggregertObjektMeta_H
        VALUES (:new.id, :new.sistoppdatertav, :new.sistoppdatert, t_Trans, t_End, 1);
  ELSIF UPDATING THEN
     IF :old.tBegin < t_Trans THEN
        INSERT INTO AggregertObjektMeta_H VALUES (:old.id, :old.sistoppdatertav, :old.sistoppdatert, :old.TBEGIN, t_Trans, :old.TVERSION);
        UPDATE AggregertObjektMeta_H SET TVERSION = :old.TVERSION+1
        WHERE id = :new.id and tEnd = t_End;
     END IF;
     UPDATE AggregertObjektMeta_H SET ID=:new.ID, sistoppdatertav=:new.sistoppdatertav, sistoppdatert=:new.sistoppdatert, TBEGIN=t_Trans, TEND=t_End, TVERSION=TVERSION
        WHERE id = :new.id and tEnd = t_End;
  ELSIF DELETING THEN
     IF :old.tBegin < t_Trans THEN
        INSERT INTO AggregertObjektMeta_H
           VALUES (:old.ID, :old.sistoppdatertav, :old.sistoppdatert, :old.TBEGIN, t_Trans, :old.TVERSION);
     END IF;
     DELETE FROM AggregertObjektMeta_H
        WHERE id = :old.id AND tEnd = t_End;
  END IF;
END T_AggregertObjektMeta;
/

create view AggregertObjekt as select * from AggregertObjekt_H where snapshot_time.t_between(tBegin, tEnd)=1;
CREATE OR REPLACE TRIGGER T_AggregertObjekt INSTEAD OF INSERT OR UPDATE OR DELETE ON AggregertObjekt
FOR EACH ROW
DECLARE
t_Trans TIMESTAMP := snapshot_time.Get_T_Trans();
t_End TIMESTAMP := snapshot_time.Get_T_CURRENT();
BEGIN
  IF INSERTING THEN
    INSERT INTO AggregertObjekt_H
        VALUES (:new.id, :new.tekst, t_Trans, t_End, 1);

  ELSIF UPDATING THEN
     IF :old.tBegin < t_Trans THEN
        INSERT INTO AggregertObjekt_H VALUES (:old.id, :old.tekst, :old.TBEGIN, t_Trans, :old.TVERSION);
        UPDATE AggregertObjekt_H SET TVERSION = :old.TVERSION+1
        WHERE id = :new.id and tEnd = t_End;
     END IF;
     UPDATE AggregertObjekt_H SET ID=:new.ID, tekst=:new.tekst, TBEGIN=t_Trans, TEND=t_End, TVERSION=TVERSION
        WHERE id = :new.id and tEnd = t_End;

  ELSIF DELETING THEN
     IF :old.tBegin < t_Trans THEN
        INSERT INTO AggregertObjekt_H
           VALUES (:old.ID, :old.tekst, :old.TBEGIN, t_Trans, :old.TVERSION);
     END IF;
     DELETE FROM AggregertObjekt_H
        WHERE id = :old.id AND tEnd = t_End;

  END IF;
END T_AggregertObjekt;
/

create view AggregertKomponent as select * from AggregertKomponent_H where snapshot_time.t_between(tBegin, tEnd)=1;
CREATE OR REPLACE TRIGGER T_AggregertKomponent INSTEAD OF INSERT OR UPDATE OR DELETE ON AggregertKomponent
FOR EACH ROW
DECLARE
t_Trans TIMESTAMP := snapshot_time.Get_T_Trans();
t_End TIMESTAMP := snapshot_time.Get_T_CURRENT();
BEGIN
  IF INSERTING THEN
    INSERT INTO AggregertKomponent_H
        VALUES (:new.id, :new.indeks, :new.noe, :new.annet, t_Trans, t_End, 1);

  ELSIF UPDATING THEN
     IF :old.tBegin < t_Trans THEN
        INSERT INTO AggregertKomponent_H VALUES (:old.id, :old.indeks, :old.noe, :old.annet, :old.TBEGIN, t_Trans, :old.TVERSION);
        UPDATE AggregertKomponent_H SET TVERSION = :old.TVERSION+1
        WHERE id = :new.id and indeks = :new.indeks and tEnd = t_End;
     END IF;
     UPDATE AggregertKomponent_H SET ID=:new.ID, indeks=:new.indeks, noe=:new.noe, annet=:new.annet, TBEGIN=t_Trans, TEND=t_End, TVERSION=TVERSION
        WHERE id = :new.id AND indeks = :new.indeks and tEnd = t_End;

  ELSIF DELETING THEN
     IF :old.tBegin < t_Trans THEN
        INSERT INTO AggregertKomponent_H
           VALUES (:old.ID, :old.indeks, :old.noe, :old.annet, :old.TBEGIN, t_Trans, :old.TVERSION);
     END IF;
     DELETE FROM AggregertKomponent_H
        WHERE id = :old.id AND tEnd = t_End;

  END IF;
END T_AggregertKomponent;
/


-- Multikobling tabeller

create table Person (
id number(19,0) not null,
Primary Key (Id)
);

create table Rettsstiftelse (
id number(19,0) not null,
class varchar2(60) not null,
Primary Key (Id)
);

create table Rettsstiftelse_Person_Kobling (
rettsstiftelseId number(19,0) not null,
rolle varchar2(30) not null,
personId number(19,0) not null,
Primary Key (rettsstiftelseId, rolle, personId)
);

-- Tinglysing-tabeller

create table RettstypeKode (
id number(19,0) not null,
kodeVerdi varchar2(10) not null,
primary key (id)
);

create table RettstypeKodeLoc (
id number(19,0) not null,
lokale varchar2(10) not null,
navn varchar2(64) not null,
beskrivelse varchar2(255) not null,
primary key (id, lokale)
);
alter table RettstypeKodeLoc add constraint FK_RettstypeKodeLoc foreign key (id) references RettstypeKode;

create table RettsstiftelsestypeKode (
id number(19,0) not null,
rettstypeKodeId number(19,0) not null,
kodeVerdi varchar2(10) not null,
primary key (id)
);
ALTER TABLE RettsstiftelsestypeKode ADD CONSTRAINT FK_RetKode_RettstypeKode FOREIGN KEY (rettstypeKodeId) REFERENCES RettstypeKode;

create table RettsstiftelsestypeKodeLoc (
id number(19,0) not null,
lokale varchar2(10) not null,
navn varchar2(64) not null,
beskrivelse varchar2(255) not null,
primary key (id, lokale)
);
alter table RettsstiftelsestypeKodeLoc add constraint FK_RettsstiftelsestypeKodeLoc foreign key (id) references RettsstiftelsestypeKode;

create table MatrikkelenhetsnivaaKode (
id number(19,0) not null,
kodeVerdi varchar2(10) not null,
primary key (id)
);

create table MatrikkelenhetsnivaaKodeLoc (
id number(19,0) not null,
lokale varchar2(10) not null,
navn varchar2(64) not null,
beskrivelse varchar2(255) not null,
primary key (id, lokale)
);
alter table MatrikkelenhetsnivaaKode add constraint FK_MatrikkelenhetsnivaaKode foreign key (id) references MatrikkelenhetsnivaaKode;

create table OmsetningstypeKode (
id number(19,0) not null,
kodeVerdi varchar2(10) not null,
primary key (id)
);

create table OmsetningstypeKodeLoc (
id number(19,0) not null,
lokale varchar2(10) not null,
navn varchar2(64) not null,
beskrivelse varchar2(255) not null,
primary key (id, lokale)
);
alter table OmsetningstypeKodeLoc add constraint FK_OmsetningstypeKodeLoc foreign key (id) references OmsetningstypeKode;

create table Person_t (
id number(19,0) not null,
ident number(11,0) not null,
identtype varchar2(60) not null,
navn varchar2(60),
Primary Key (Id)
);

create table Embete (
id number(19,0) not null,
embetenummer varchar2(4) not null,
Primary Key (Id)
);

create table Kommune (
id number(19,0) not null,
kommunenummer varchar2(4) not null,
navn varchar2(60) not null,
embeteId number(19,0) not null,
Primary Key (Id)
);
ALTER TABLE Kommune ADD CONSTRAINT FK_Kommune_Embete FOREIGN KEY (embeteId) REFERENCES Embete;

create table Matrikkelenhet (
id number(19,0) not null,
kommuneId number(19,0) not null,
gaardsnummer number(10,0) not null,
bruksnummer number(5,0) not null,
festenummer number(5,0),
seksjonsnummer number(5,0),
Primary Key (Id)
);
alter table Matrikkelenhet add constraint FK_Matrikkelenhet_Kommune foreign key (kommuneId) references Kommune;

create table NivaaIMatrikkelenhet (
id number(19,0) not null,
matrikkelenhetId number(19,0) not null,
matrikkelenhetsnivaaKodeId number(19,0) not null,
Primary Key (Id)
);
alter table NivaaIMatrikkelenhet add constraint FK_Nivaa_Matrikkelenhet foreign key (matrikkelenhetId) references Matrikkelenhet;
alter table NivaaIMatrikkelenhet add constraint FK_Nivaa_NivaaKode foreign key (matrikkelenhetsnivaaKodeId) references MatrikkelenhetsnivaaKode;

create table AndelIMatrikkelenhet (
id number(19,0) not null,
teller number(10,0) not null,
nevner number(10,0) not null,
aktiv number(1,0) not null,
nivaaIMatrikkelenhetId number(19,0) not null,
andelseierPersonId number(19,0),
andelseierMatrikkelenhetId number(19,0),
Primary Key (Id)
);
ALTER TABLE AndelIMatrikkelenhet ADD CONSTRAINT FK_Andel_Nivaa FOREIGN KEY (nivaaIMatrikkelenhetId) REFERENCES NivaaIMatrikkelenhet;
ALTER TABLE AndelIMatrikkelenhet ADD CONSTRAINT FK_Andel_EierPerson FOREIGN KEY (andelseierPersonId) REFERENCES Person_t;
ALTER TABLE AndelIMatrikkelenhet ADD CONSTRAINT FK_Andel_EierMatr FOREIGN KEY (andelseierMatrikkelenhetId) REFERENCES Matrikkelenhet;

create table Dokument (
id number(19,0) not null,
dokumentaar number(5,0),
dokumentnummer number(10,0) not null,
embeteId number(19,0) not null,
status varchar2(30) not null,
Primary Key (Id)
);
ALTER TABLE Dokument ADD CONSTRAINT FK_Dokument_Embete FOREIGN KEY (embeteId) REFERENCES Embete;

create table Rettsstiftelse_t (
id number(19,0) not null,
class varchar2(60) not null,
rettsstiftelsesnummer number(5,0) not null,
aktiv number(1,0) not null,
dokumentId number(19,0) not null,
rettsstiftelsestypeKodeId number(19,0) not null,
omsetningstypeKodeId number(19,0),
Primary Key (Id)
);
ALTER TABLE Rettsstiftelse_t ADD CONSTRAINT FK_Rettsstift_Dokument FOREIGN KEY (dokumentId) REFERENCES Dokument;
ALTER TABLE Rettsstiftelse_t ADD CONSTRAINT FK_Rettsstift_RtypeKode FOREIGN KEY (rettsstiftelsestypeKodeId) REFERENCES RettsstiftelsestypeKode;
ALTER TABLE Rettsstiftelse_t ADD CONSTRAINT FK_Rettsstift_OmsetKode FOREIGN KEY (omsetningstypeKodeId) REFERENCES OmsetningstypeKode;

create table Rettsstiftelse_Andel_Kobling (
rettsstiftelseId number(19,0) not null,
rolle varchar2(30) not null,
andelId number(19,0) not null,
Primary Key (rettsstiftelseId, rolle, andelId)
);
ALTER TABLE Rettsstiftelse_Andel_Kobling ADD CONSTRAINT FK_Rettsstift_Andel FOREIGN KEY (andelId) REFERENCES AndelIMatrikkelenhet;

create table RettsstiftelseRelasjon (
id number(19,0) not null,
rettsstiftelseId number(19,0) not null,
Primary Key (Id)
);

create table Relasjon_Andel_Kobling (
rettsstiftelseId number(19,0) not null,
rolle varchar2(30) not null,
andelId number(19,0) not null,
Primary Key (rettsstiftelseId, rolle, andelId)
);
ALTER TABLE Relasjon_Andel_Kobling ADD CONSTRAINT FK_Relasjon_Andel FOREIGN KEY (andelId) REFERENCES AndelIMatrikkelenhet;

create table Relasjon_Nivaa_Kobling (
rettsstiftelseId number(19,0) not null,
rolle varchar2(30) not null,
nivaaId number(19,0) not null,
Primary Key (rettsstiftelseId, rolle, nivaaId)
);
ALTER TABLE Relasjon_Nivaa_Kobling ADD CONSTRAINT FK_Relasjon_Nivaa FOREIGN KEY (nivaaId) REFERENCES NivaaIMatrikkelenhet;

create table Rettsst_Relasjon_Kobling (
rettsstiftelseId number(19,0) not null,
rolle varchar2(30) not null,
relasjonId number(19,0) not null,
Primary Key (rettsstiftelseId, rolle, relasjonId)
);
ALTER TABLE Rettsst_Relasjon_Kobling ADD CONSTRAINT FK_Rettsstift_Relasjon FOREIGN KEY (relasjonId) REFERENCES RettsstiftelseRelasjon;

