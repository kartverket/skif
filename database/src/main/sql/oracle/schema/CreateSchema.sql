
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

create table SelfBubble (
id number(19,0) not null,
text varchar2(255),
refId number(19,0),

Primary Key (Id)
);
alter table SelfBubble add constraint Self_FK foreign key (refId) references SelfBubble;



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
    Function T_Between(oppdateringsdato In Timestamp, sluttdato In Timestamp) Return Number;
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

    Function T_Between(oppdateringsdato In Timestamp, sluttdato In Timestamp)
    Return Number
    Is
    retval NUMBER;
    BEGIN
                IF (oppdateringsdato<=T AND (T<sluttdato OR sluttdato=T_CURRENT))
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
    oppdateringsdato               timestamp(6) not null,
    sluttdato                 timestamp(6) not null,
    versjonId             number (19,0) not null,
    nr                   number(10,0),
    navn                 VARCHAR2(255 BYTE),
    PRIMARY KEY (ID, sluttdato)
);
create view FOO as select * from FOO_H  where snapshot_time.t_between(oppdateringsdato, sluttdato)=1;

CREATE OR REPLACE TRIGGER FOO_TRIGGER
INSTEAD OF INSERT OR UPDATE OR DELETE ON FOO
FOR EACH ROW
DECLARE
t_Trans TIMESTAMP := snapshot_time.Get_T_Trans();
t_End TIMESTAMP := snapshot_time.Get_T_CURRENT();
BEGIN
  IF UPDATING THEN
    IF :old.oppdateringsdato < t_Trans THEN
        INSERT INTO FOO_H
        VALUES (:old.id, :old.oppdateringsdato, t_Trans, :old.versjonId, :old.nr, :old.navn);

        UPDATE FOO_H SET versjonId = :old.versjonId + 1 WHERE id= :new.id and sluttdato = t_End;
    END IF;
    UPDATE FOO_H
    SET id = :new.id, oppdateringsdato = t_Trans, nr = :new.nr, navn = :new.navn
    WHERE id = :new.id and sluttdato = t_End;
  ELSIF INSERTING THEN
    INSERT INTO FOO_H
        VALUES (:new.id, t_Trans,t_End, 1, :new.nr, :new.navn);
  ELSIF DELETING THEN
    IF :old.oppdateringsdato < t_Trans THEN
        INSERT INTO FOO_H
        VALUES (:old.id, :old.oppdateringsdato, t_Trans, :old.versjonId, :old.nr, :old.navn);
    END IF;
    delete from FOO_H
    WHERE id = :old.id and sluttdato = t_End;
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
    RazEntityComponentId number(19,0),
    PRIMARY KEY (id)
);

-- Denne tabell har heller ikke historikk
CREATE TABLE RazEntityComponent (
    id                   NUMBER(19,0) NOT NULL ENABLE,
    componentName        VARCHAR2(255 BYTE),
    PRIMARY KEY (id)
);

CREATE TABLE BAR_H (
    id                   NUMBER(19,0) NOT NULL ENABLE,
    oppdateringsdato               timestamp(6) not null,
    sluttdato                 timestamp(6) not null,
    versjonId             number (19,0) not null,
    husnr                number(10,0),
    bokstav              VARCHAR2(255 BYTE),
    fooId                number(19,0) not null,
    bazId                number(19,0),
    PRIMARY KEY (ID, sluttdato)
);
create view BAR as select * from BAR_H  where snapshot_time.t_between(oppdateringsdato, sluttdato)=1;

CREATE OR REPLACE TRIGGER BAR_TRIGGER
INSTEAD OF INSERT OR UPDATE OR DELETE ON BAR
FOR EACH ROW
DECLARE
t_Trans TIMESTAMP := snapshot_time.Get_T_Trans();
t_End TIMESTAMP := snapshot_time.Get_T_CURRENT();
BEGIN
  IF UPDATING THEN
    IF :old.oppdateringsdato < t_Trans THEN
        INSERT INTO BAR_H
        VALUES (:old.id, :old.oppdateringsdato, t_Trans, :old.versjonId, :old.husnr, :old.bokstav, :old.fooId, :old.bazId);

        UPDATE BAR_H SET versjonId = :old.versjonId + 1 WHERE id= :new.id and sluttdato = t_End;
    END IF;
    UPDATE BAR_H
    SET id = :new.id, oppdateringsdato = t_Trans, nr = :new.husnr, navn = :new.bokstav, fooId = :new.fooId, bazId = :new.bazId
    WHERE id = :new.id and sluttdato = t_End;
  ELSIF INSERTING THEN
    INSERT INTO BAR_H
        VALUES (:new.id, t_Trans,t_End, 1, :new.husnr, :new.bokstav, :new.fooId, :new.bazId);
  ELSIF DELETING THEN
    IF :old.oppdateringsdato < t_Trans THEN
        INSERT INTO BAR_H
        VALUES (:old.id, :old.oppdateringsdato, t_Trans, :old.versjonId, :old.husnr, :old.bokstav, :old.fooId, :old.bazId);
    END IF;
    delete from BAR_H
    WHERE id = :old.id and sluttdato = t_End;
  END IF;
END BAR_TRIGGER;
/

CREATE TABLE BARFOOS_H (
    id                   NUMBER(19,0) NOT NULL ENABLE,
    oppdateringsdato               timestamp(6) not null,
    sluttdato                 timestamp(6) not null,
    versjonId             number (19,0) not null,
    text                 VARCHAR2(255 BYTE),
    barId                number(19,0) not null,
    PRIMARY KEY (ID, sluttdato)
);
create view BARFOOS as select * from BARFOOS_H  where snapshot_time.t_between(oppdateringsdato, sluttdato)=1;

CREATE OR REPLACE TRIGGER BARFOOS_TRIGGER
INSTEAD OF INSERT OR UPDATE OR DELETE ON BARFOOS
FOR EACH ROW
DECLARE
t_Trans TIMESTAMP := snapshot_time.Get_T_Trans();
t_End TIMESTAMP := snapshot_time.Get_T_CURRENT();
BEGIN
  IF UPDATING THEN
    IF :old.oppdateringsdato < t_Trans THEN
        INSERT INTO BARFOOS_H
        VALUES (:old.id, :old.oppdateringsdato, t_Trans, :old.versjonId, :old.text, :old.barId);

        UPDATE BARFOOS_H SET versjonId = :old.versjonId + 1 WHERE id= :new.id and sluttdato = t_End;
    END IF;
    UPDATE BARFOOS_H
    SET id = :new.id, oppdateringsdato = t_Trans, text = :new.text, barId = :new.barId
    WHERE id = :new.id and sluttdato = t_End;
  ELSIF INSERTING THEN
    INSERT INTO BARFOOS_H
        VALUES (:new.id, t_Trans,t_End, 1, :new.text, :new.barId);
  ELSIF DELETING THEN
    IF :old.oppdateringsdato < t_Trans THEN
        INSERT INTO BARFOOS_H
        VALUES (:old.id, :old.oppdateringsdato, t_Trans, :old.versjonId, :old.text, :old.barId);
    END IF;
    delete from BARFOOS_H
    WHERE id = :old.id and sluttdato = t_End;
  END IF;
END BARFOOS_TRIGGER;
/

CREATE TABLE FooForBarFoos_H (
    barFoosId             NUMBER(19,0) not null,
    fooId                NUMBER(19,0) not null,
    oppdateringsdato               timestamp(6) not null,
    sluttdato                 timestamp(6) not null,
    PRIMARY KEY (barFoosId, fooId, sluttdato)
);
create view FooForBarFoos as select * from FooForBarFoos_H where snapshot_time.t_between(oppdateringsdato, sluttdato)=1;

CREATE OR REPLACE TRIGGER FooForBarFoos_TRIGGER
INSTEAD OF INSERT OR UPDATE OR DELETE ON FooForBarFoos
FOR EACH ROW
DECLARE
t_Trans TIMESTAMP := snapshot_time.Get_T_Trans();
t_End TIMESTAMP := snapshot_time.Get_T_CURRENT();
BEGIN
  IF UPDATING THEN
    IF :old.oppdateringsdato < t_Trans THEN
        INSERT INTO FooForBarFoos_H
        VALUES (:old.barFoosId, :old.fooId, :old.oppdateringsdato, t_Trans);
    END IF;
    UPDATE FooForBarFoos_H
    SET barFoosId = :new.barFoosId, fooId = :new.fooId and oppdateringsdato = t_Trans
    WHERE barFoosId = :new.barFoosId and fooId = :new.fooId and sluttdato = t_End;
  ELSIF INSERTING THEN
    INSERT INTO FooForBarFoos_H
        VALUES (:new.barFoosId, :new.barId, t_Trans,t_End);
  ELSIF DELETING THEN
    IF :old.oppdateringsdato < t_Trans THEN
        INSERT INTO FooForBarFoos_H
        VALUES (:old.barFoosId, :old.barId, :old.oppdateringsdato, t_Trans);
    END IF;
    delete from FooForBarFoos_H
    WHERE barFoosId = :old.barFoosId and fooId = :old.fooId and sluttdato = t_End;
  END IF;
END BARFOOS_TRIGGER;
/


CREATE TABLE GEOMETRICELEMENT_H (
  id                  NUMBER(19,0),
  point               MDSYS.SDO_GEOMETRY,
  polygon             MDSYS.SDO_GEOMETRY,
  oppdateringsdato              timestamp(6) not null,
  sluttdato                timestamp(6) not null,
  versjonId            number (19,0) not null,
  primary key(id, oppdateringsdato)
);
create view GEOMETRICELEMENT as select * from GEOMETRICELEMENT_H where snapshot_time.t_between(oppdateringsdato, sluttdato)=1;

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
     IF :old.oppdateringsdato < t_Now THEN
        INSERT INTO GEOMETRICELEMENT_H VALUES (:old.ID, :new.POINT, :new.POLYGON, :old.oppdateringsdato, t_Now, :old.versjonId);
        UPDATE GEOMETRICELEMENT_H SET versjonId = :old.versjonId+1
        WHERE id = :new.id and sluttdato = t_End;
     END IF;
     UPDATE TEIG_H SET ID=:new.ID, POINT=:new.POINT, POLYGON=:new.POLYGON, oppdateringsdato=t_Now, sluttdato=t_End, versjonId=versjonId
        WHERE id = :new.id and sluttdato = t_End;

  ELSIF DELETING THEN
     IF :old.oppdateringsdato < t_Now THEN
        INSERT INTO GEOMETRICELEMENT_H
           VALUES (:old.ID, :old.POINT, :old.POLYGON, :old.oppdateringsdato, t_Now, :old.versjonId);
     END IF;
     DELETE FROM GEOMETRICELEMENT_H
        WHERE id = :old.id AND sluttdato = t_End;

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
  oppdateringsdato              timestamp(6) not null,
  sluttdato                timestamp(6) not null,
  versjonId            number (19,0) not null,
  primary key (id, sluttdato)
);
create table AggregertObjekt_H (
  id number(19,0) not null,
  tekst varchar2(255 char),
  oppdateringsdato              timestamp(6) not null,
  sluttdato                timestamp(6) not null,
  versjonId            number (19,0) not null,
  primary key (id, sluttdato)
);
create table AggregertKomponent_h (
  id number(19,0) not null,
  indeks number(10,0) not null,
  noe varchar2(255 char),
  annet number(10,0),
  oppdateringsdato              timestamp(6) not null,
  sluttdato                timestamp(6) not null,
  versjonId            number (19,0) not null,
  primary key (id, indeks, sluttdato)
);

create view AggregertObjektMeta as select * from AggregertObjektMeta_H where snapshot_time.t_between(oppdateringsdato, sluttdato)=1;
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
     IF :old.oppdateringsdato < t_Trans THEN
        INSERT INTO AggregertObjektMeta_H VALUES (:old.id, :old.sistoppdatertav, :old.sistoppdatert, :old.oppdateringsdato, t_Trans, :old.versjonId);
        UPDATE AggregertObjektMeta_H SET versjonId = :old.versjonId+1
        WHERE id = :new.id and sluttdato = t_End;
     END IF;
     UPDATE AggregertObjektMeta_H SET ID=:new.ID, sistoppdatertav=:new.sistoppdatertav, sistoppdatert=:new.sistoppdatert, oppdateringsdato=t_Trans, sluttdato=t_End, versjonId=versjonId
        WHERE id = :new.id and sluttdato = t_End;
  ELSIF DELETING THEN
     IF :old.oppdateringsdato < t_Trans THEN
        INSERT INTO AggregertObjektMeta_H
           VALUES (:old.ID, :old.sistoppdatertav, :old.sistoppdatert, :old.oppdateringsdato, t_Trans, :old.versjonId);
     END IF;
     DELETE FROM AggregertObjektMeta_H
        WHERE id = :old.id AND sluttdato = t_End;
  END IF;
END T_AggregertObjektMeta;
/

create view AggregertObjekt as select * from AggregertObjekt_H where snapshot_time.t_between(oppdateringsdato, sluttdato)=1;
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
     IF :old.oppdateringsdato < t_Trans THEN
        INSERT INTO AggregertObjekt_H VALUES (:old.id, :old.tekst, :old.oppdateringsdato, t_Trans, :old.versjonId);
        UPDATE AggregertObjekt_H SET versjonId = :old.versjonId+1
        WHERE id = :new.id and sluttdato = t_End;
     END IF;
     UPDATE AggregertObjekt_H SET ID=:new.ID, tekst=:new.tekst, oppdateringsdato=t_Trans, sluttdato=t_End, versjonId=versjonId
        WHERE id = :new.id and sluttdato = t_End;

  ELSIF DELETING THEN
     IF :old.oppdateringsdato < t_Trans THEN
        INSERT INTO AggregertObjekt_H
           VALUES (:old.ID, :old.tekst, :old.oppdateringsdato, t_Trans, :old.versjonId);
     END IF;
     DELETE FROM AggregertObjekt_H
        WHERE id = :old.id AND sluttdato = t_End;

  END IF;
END T_AggregertObjekt;
/

create view AggregertKomponent as select * from AggregertKomponent_H where snapshot_time.t_between(oppdateringsdato, sluttdato)=1;
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
     IF :old.oppdateringsdato < t_Trans THEN
        INSERT INTO AggregertKomponent_H VALUES (:old.id, :old.indeks, :old.noe, :old.annet, :old.oppdateringsdato, t_Trans, :old.versjonId);
        UPDATE AggregertKomponent_H SET versjonId = :old.versjonId+1
        WHERE id = :new.id and indeks = :new.indeks and sluttdato = t_End;
     END IF;
     UPDATE AggregertKomponent_H SET ID=:new.ID, indeks=:new.indeks, noe=:new.noe, annet=:new.annet, oppdateringsdato=t_Trans, sluttdato=t_End, versjonId=versjonId
        WHERE id = :new.id AND indeks = :new.indeks and sluttdato = t_End;

  ELSIF DELETING THEN
     IF :old.oppdateringsdato < t_Trans THEN
        INSERT INTO AggregertKomponent_H
           VALUES (:old.ID, :old.indeks, :old.noe, :old.annet, :old.oppdateringsdato, t_Trans, :old.versjonId);
     END IF;
     DELETE FROM AggregertKomponent_H
        WHERE id = :old.id AND sluttdato = t_End;

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

create table BubbleWithList_h (
  id number(19, 0) not null,
  text                varchar2(30),
  oppdateringsdato              timestamp(6) not null,
  sluttdato                timestamp(6) not null,
  versjonId            number (19,0) not null,
  primary key (id, sluttdato)
);

create view BubbleWithList as select * from BubbleWithList_h where snapshot_time.t_between(oppdateringsdato, sluttdato)=1;
CREATE OR REPLACE TRIGGER T_BubbleWithList INSTEAD OF INSERT OR UPDATE OR DELETE ON BubbleWithList
FOR EACH ROW
DECLARE
t_Trans TIMESTAMP := snapshot_time.Get_T_Trans();
t_End TIMESTAMP := snapshot_time.Get_T_CURRENT();
BEGIN
  IF INSERTING THEN
    INSERT INTO BubbleWithList_h
        VALUES (:new.id, :new.text, t_Trans, t_End, 1);

  ELSIF UPDATING THEN
     IF :old.oppdateringsdato < t_Trans THEN
        INSERT INTO BubbleWithList_h VALUES (:old.id, :old.text, :old.oppdateringsdato, t_Trans, :old.versjonId);
        UPDATE BubbleWithList_h SET versjonId = :old.versjonId+1
        WHERE id = :new.id and sluttdato = t_End;
     END IF;
     UPDATE BubbleWithList_h SET ID=:new.ID, TEXT=:new.text, oppdateringsdato=t_Trans, sluttdato=t_End, versjonId=versjonId
        WHERE id = :new.id and sluttdato = t_End;

  ELSIF DELETING THEN
     IF :old.oppdateringsdato < t_Trans THEN
        INSERT INTO BubbleWithList_h
           VALUES (:old.ID, :old.TEXT, :old.oppdateringsdato, t_Trans, :old.versjonId);
     END IF;
     DELETE FROM BubbleWithList_h
        WHERE id = :old.id AND sluttdato = t_End;

  END IF;
END T_BubbleWithList;
/

create table BWLForBWL_h(
  underBWLId number(19,0) not null,
  bwlId number(19,0) not null,
  oppdateringsdato              timestamp(6) not null,
  sluttdato                timestamp(6) not null,
  versjonId            number (19,0) not null,
  primary key(underBWLId, bwlId)
);

create view BWLForBWL as select * from BWLForBWL_h where snapshot_time.t_between(oppdateringsdato, sluttdato)=1;
CREATE OR REPLACE TRIGGER T_BWLForBWL INSTEAD OF INSERT OR UPDATE OR DELETE ON BWLForBWL
FOR EACH ROW
DECLARE
t_Trans TIMESTAMP := snapshot_time.Get_T_Trans();
t_End TIMESTAMP := snapshot_time.Get_T_CURRENT();
BEGIN
  IF INSERTING THEN
    INSERT INTO BWLForBWL_h
        VALUES (:new.underBWLId, :new.bwlId, t_Trans, t_End, 1);

  ELSIF UPDATING THEN
     IF :old.oppdateringsdato < t_Trans THEN
        INSERT INTO BWLForBWL_h VALUES (:old.underBWLId, :old.bwlId, :old.oppdateringsdato, t_Trans, :old.versjonId);
        UPDATE BWLForBWL_h SET versjonId = :old.versjonId+1
        WHERE underBWLId = :new.underBWLId and bwlId = :new.bwlId and sluttdato = t_End;
     END IF;
     UPDATE BWLForBWL_h SET UNDERBWLID=:new.underBWLId, bwlId=:new.bwlId, oppdateringsdato=t_Trans, sluttdato=t_End, versjonId=versjonId
        WHERE underBWLId = :new.underBWLId and bwlId = :new.bwlId and sluttdato = t_End;

  ELSIF DELETING THEN
     IF :old.oppdateringsdato < t_Trans THEN
        INSERT INTO BWLForBWL_h
           VALUES (:old.underBWLId, :old.bwlId, :old.oppdateringsdato, t_Trans, :old.versjonId);
     END IF;
     DELETE FROM BWLForBWL_h
        WHERE underBWLId = :old.underBWLId and bwlId = :old.bwlId AND sluttdato = t_End;

  END IF;
END T_BWLForBWL;
/

create table BubbleWithListComponent_h (
  id number(19, 0) not null,
  componentName varchar2(255 BYTE),
  bubblewithlistid number(19, 0),
  aEnumKodeId number(19, 0),
  oppdateringsdato              timestamp(6) not null,
  sluttdato                timestamp(6) not null,
  versjonId            number (19,0) not null,
  primary key (id, sluttdato)
);

create view BubbleWithListComponent as select * from BubbleWithListComponent_h where snapshot_time.t_between(oppdateringsdato, sluttdato)=1;
CREATE OR REPLACE TRIGGER T_BubbleWithListComponent INSTEAD OF INSERT OR UPDATE OR DELETE ON BubbleWithListComponent
FOR EACH ROW
DECLARE
t_Trans TIMESTAMP := snapshot_time.Get_T_Trans();
t_End TIMESTAMP := snapshot_time.Get_T_CURRENT();
BEGIN
  IF INSERTING THEN
    INSERT INTO BubbleWithListComponent_h
        VALUES (:new.id, :new.componentname, :new.bubblewithlistid, :new.aEnumKodeId, t_Trans, t_End, 1);

  ELSIF UPDATING THEN
     IF :old.oppdateringsdato < t_Trans THEN
        INSERT INTO BubbleWithListComponent_h VALUES (:old.id, :old.componentname, :old.bubblewithlistid, :old.aEnumKodeId, :old.oppdateringsdato, t_Trans, :old.versjonId);
        UPDATE BubbleWithListComponent_h SET versjonId = :old.versjonId+1
        WHERE id = :new.id and sluttdato = t_End;
     END IF;
     UPDATE BubbleWithListComponent_h SET ID=:new.ID, componentname=:new.componentname, bubblewithlistid=:new.bubblewithlistid, aEnumKodeId=:new.aEnumKodeId, oppdateringsdato=t_Trans, sluttdato=t_End, versjonId=versjonId
        WHERE id = :new.id and sluttdato = t_End;

  ELSIF DELETING THEN
     IF :old.oppdateringsdato < t_Trans THEN
        INSERT INTO BubbleWithListComponent_h
           VALUES (:old.ID, :old.componentname, :old.bubblewithlistid, :old.aEnumKodeId, :old.oppdateringsdato, t_Trans, :old.versjonId);
     END IF;
     DELETE FROM BubbleWithListComponent_h
        WHERE id = :old.id AND sluttdato = t_End;

  END IF;
END T_BubbleWithListComponent;
/

create table BubbleWithListComponent2_h (
  id number(19, 0) not null,
  componentName varchar2(255 BYTE),
  bubblewithlistid number(19, 0),
  oppdateringsdato              timestamp(6) not null,
  sluttdato                timestamp(6) not null,
  versjonId            number (19,0) not null,
  primary key (id, sluttdato)
);

create view BubbleWithListComponent2 as select * from BubbleWithListComponent2_h where snapshot_time.t_between(oppdateringsdato, sluttdato)=1;
CREATE OR REPLACE TRIGGER T_BubbleWithListComponent2 INSTEAD OF INSERT OR UPDATE OR DELETE ON BubbleWithListComponent2
FOR EACH ROW
DECLARE
t_Trans TIMESTAMP := snapshot_time.Get_T_Trans();
t_End TIMESTAMP := snapshot_time.Get_T_CURRENT();
BEGIN
  IF INSERTING THEN
    INSERT INTO BubbleWithListComponent2_h
        VALUES (:new.id, :new.componentname, :new.bubblewithlistid, t_Trans, t_End, 1);

  ELSIF UPDATING THEN
     IF :old.oppdateringsdato < t_Trans THEN
        INSERT INTO BubbleWithListComponent2_h VALUES (:old.id, :old.componentname, :old.bubblewithlistid, :old.oppdateringsdato, t_Trans, :old.versjonId);
        UPDATE BubbleWithListComponent2_h SET versjonId = :old.versjonId+1
        WHERE id = :new.id and sluttdato = t_End;
     END IF;
     UPDATE BubbleWithListComponent2_h SET ID=:new.ID, componentname=:new.componentname, bubblewithlistid=:new.bubblewithlistid, oppdateringsdato=t_Trans, sluttdato=t_End, versjonId=versjonId
        WHERE id = :new.id and sluttdato = t_End;

  ELSIF DELETING THEN
     IF :old.oppdateringsdato < t_Trans THEN
        INSERT INTO BubbleWithListComponent2_h
           VALUES (:old.ID, :old.componentname, :old.bubblewithlistid, :old.oppdateringsdato, t_Trans, :old.versjonId);
     END IF;
     DELETE FROM BubbleWithListComponent2_h
        WHERE id = :old.id AND sluttdato = t_End;

  END IF;
END T_BubbleWithListComponent2;
/

create table SubTypedBubble_h (
  id number(19, 0) not null,
  class varchar2(255 byte),
  text varchar2(255 byte),
  num number(10,0),
  oppdateringsdato timestamp(6) not null,
  sluttdato timestamp(6) not null,
  versjonId number (19,0) not null,
  primary key (id, sluttdato)
);

create view SubTypedBubble as select * from SubTypedBubble_h where snapshot_time.t_between(oppdateringsdato, sluttdato)=1;
CREATE OR REPLACE TRIGGER T_SubTypedBubble INSTEAD OF INSERT OR UPDATE OR DELETE ON SubTypedBubble
FOR EACH ROW
DECLARE
t_Trans TIMESTAMP := snapshot_time.Get_T_Trans();
t_End TIMESTAMP := snapshot_time.Get_T_CURRENT();
BEGIN
  IF INSERTING THEN
    INSERT INTO SubTypedBubble_h
        VALUES (:new.id, :new.class, :new.text, :new.num, t_Trans, t_End, 1);

  ELSIF UPDATING THEN
     IF :old.oppdateringsdato < t_Trans THEN
        INSERT INTO SubTypedBubble_h VALUES (:old.id, :old.class, :old.text, :old.num, :old.oppdateringsdato, t_Trans, :old.versjonId);
        UPDATE SubTypedBubble_h SET versjonId = :old.versjonId+1
        WHERE id = :new.id and sluttdato = t_End;
     END IF;
     UPDATE SubTypedBubble_h SET ID=:new.ID, class=:new.class, text=:new.text, num=:new.num, oppdateringsdato=t_Trans, sluttdato=t_End, versjonId=versjonId
        WHERE id = :new.id and sluttdato = t_End;

  ELSIF DELETING THEN
     IF :old.oppdateringsdato < t_Trans THEN
        INSERT INTO SubTypedBubble_h
           VALUES (:old.ID, :old.class, :old.text, :old.num, :old.oppdateringsdato, t_Trans, :old.versjonId);
     END IF;
     DELETE FROM SubTypedBubble_h
        WHERE id = :old.id AND sluttdato = t_End;

  END IF;
END T_SubTypedBubble;
/

create table AKodeForSubtype_h (
  subtypedid number(19, 0) not null,
  kodeid number(19, 0) not null,
  oppdateringsdato timestamp(6) not null,
  sluttdato timestamp(6) not null,
  versjonId number (19,0) not null,
  primary key (subtypedid, kodeid, sluttdato)
);

create view AKodeForSubtype as select * from AKodeForSubtype_h where snapshot_time.t_between(oppdateringsdato, sluttdato)=1;
CREATE OR REPLACE TRIGGER T_AKodeForSubtype INSTEAD OF INSERT OR UPDATE OR DELETE ON AKodeForSubtype
FOR EACH ROW
DECLARE
t_Trans TIMESTAMP := snapshot_time.Get_T_Trans();
t_End TIMESTAMP := snapshot_time.Get_T_CURRENT();
BEGIN
  IF INSERTING THEN
    INSERT INTO AKodeForSubtype_h
        VALUES (:new.subtypedid, :new.kodeid, t_Trans, t_End, 1);

  ELSIF UPDATING THEN
     IF :old.oppdateringsdato < t_Trans THEN
        INSERT INTO AKodeForSubtype_h VALUES (:old.subtypedid, :old.kodeid, :old.oppdateringsdato, t_Trans, :old.versjonId);
        UPDATE AKodeForSubtype_h SET versjonId = :old.versjonId+1
        WHERE subtypedid = :new.subtypedid and kodeid = :new.kodeid and sluttdato = t_End;
     END IF;
     UPDATE AKodeForSubtype_h SET subtypedid=:new.subtypedid, kodeId=:new.kodeid, oppdateringsdato=t_Trans, sluttdato=t_End, versjonId=versjonId
        WHERE subtypedid = :new.subtypedid and kodeid = :new.kodeid and sluttdato = t_End;

  ELSIF DELETING THEN
     IF :old.oppdateringsdato < t_Trans THEN
        INSERT INTO AKodeForSubtype_h
           VALUES (:old.subtypedid, :old.kodeid, :old.oppdateringsdato, t_Trans, :old.versjonId);
     END IF;
     DELETE FROM AKodeForSubtype_h
        WHERE subtypedid = :old.subtypedid AND kodeid = :old.kodeid AND sluttdato = t_End;

  END IF;
END T_AKodeForSubtype;
/
