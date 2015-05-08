-- Oppretter tabeller som brukes storetest-testprosjektet og som bruker mockup-rammeverket for opprettelse av testdatasett

create table Kodeliste(
    id number(19,0) not null,
    kodeTypeNavn varchar2(64),
    kodeIdClassname varchar2(255),
    primary key(id)
);
create table KodelisteLoc (
    id number(19,0) not null,
    locale varchar2(10) not null,
    name varchar2(64) not null,
    value varchar2(255) not null,
    primary key (id, locale, name)
);
alter table KodelisteLoc add constraint FK_KodelisteLoc foreign key (id) references Kodeliste;

CREATE TABLE Endring (
    ID NUMBER(19,0) NOT NULL,
    CLASS VARCHAR2(255 CHAR) NOT NULL,
    ENDRINGSTYPE NUMBER(3,0),
    ENDRINGSTIDSPUNKT TIMESTAMP(6),
    BRUKERNAVN VARCHAR2(255 CHAR),
    ENDRETBUBBLEID NUMBER(19,0),
    CONSTRAINT PK_ENDRING PRIMARY KEY (ID)
) organization index;

create table HistoriskDbKode (
    id number(19,0) not null,
    class varchar2(255) not null,
    kodeverdi varchar2(10) not null,
    primary key(id)
);
create table HistoriskDbKodeLoc (
    id number(19,0) not null,
    locale varchar2(10) not null,
    name varchar2(64) not null,
    value varchar2(255) not null,
    primary key (id, locale, name)
);

create table Simple (
    id number(19,0) not null,
    nr number(10,0),
    text varchar2(255),
    primary key (id)
);

create table BubbleWithAnyBubbleRef (
    id number(19,0) not null,
    nr number(10,0),
    anyId number(19,0),
    anyIdclass varchar2(255),
    someIdentValue varchar2(255),
    someIdentclass varchar2(255),
    primary key (id)
);

create table BubbleWithRelation (
    id number(19,0) not null,
    nr number(10,0),
    text varchar2(255),
    simpleId number(19,0),
    primary key (id)
);
alter table BubbleWithRelation add constraint FK_BubbleWithRelation_simpleId foreign key (simpleId) references Simple;

create table BubbleWithFilter (
    id number(19,0) not null,
    nr number(10,0),
    filter number(1,0),
    filterText varchar2(255),
    text varchar2(255),
    primary key (id)
);

create table BubbleWithKode (
    id number(19,0) not null,
    nr number(10,0),
    text varchar2(255),
    testAEnumKodeId number(10,0) not null,
    testC2DbKodeId number(19,0) not null,
    primary key (id)
);

create table BubbleWithLocalDate (
    id number(19,0) not null,
    dato date not null,
    primary key (id)
);

create table BubbleWithLocalDateTime (
    id number(19,0) not null,
    tidspunkt timestamp not null,
    primary key (id)
);

CREATE TABLE SubTypedBubble (
    ID NUMBER(19,0) NOT NULL,
    CLASS VARCHAR2(255 CHAR) NOT NULL,
    TEXT VARCHAR2(255 CHAR),
    NUM NUMBER(10,0),
    CONSTRAINT PK_SUBTYPEDBUBBLE PRIMARY KEY (ID)
);

--todo: fobedret navn
CREATE TABLE TekstForSubtype (
    SUBTYPEDID NUMBER(19,0) NOT NULL,
    TEKST VARCHAR2(255 CHAR) NOT NULL,
    CONSTRAINT PK_TEKSTFORSUBTYPE PRIMARY KEY (SUBTYPEDID, TEKST)
);

create table HistSimple (
    id number(19,0) not null,
    nr number(10,0),
    text varchar2(255),
    testSetNumber number(10,0) not null,
    CONSTRAINT PK_HISTSIMPLE PRIMARY KEY (ID)
);

create table HistWithRelation (
    id number(19,0) not null,
    nr number(10,0),
    text varchar2(255),
    testSetNumber number(10,0) not null,
    histSimpleId number(19,0),
    CONSTRAINT PK_HISTWITHRELATION PRIMARY KEY (ID)
);

create table BubbleWithValueObject (
    id number(19,0) not null,
    nr number(10,0),
    text varchar2(255),
    a_valuta varchar2(255),
    a_verdi number(19,0),
    a_kommentar varchar2(255),
    b_valuta varchar2(255),
    b_verdi number(19,0),
    b_kommentar varchar2(255),
    primary key (id)
);

create table BeloepVOSet (
    ownerId number(19,0) not null,
    valuta varchar2(255) not null,
    verdi number(19,0) not null,
    kommentar varchar2(255),
    primary key (ownerId, valuta)
);

alter table BeloepVOSet add constraint FK_BeloepVOSet_ownerId foreign key (ownerId) references BubbleWithValueObject;

create table BubbleWithCompositeComponent (
    id number(19,0) not null,
    nr number(10,0),
    text varchar2(255),
    l1_text varchar2(255),
    l1_valuta varchar2(255),
    l1_verdi number(19,0),
    l1_kommentar varchar2(255),
    l2_text varchar2(255),
    l2_valuta varchar2(255),
    l2_verdi number(19,0),
    l2_kommentar varchar2(255),
    primary key (id)
);

create table CompositeL1BeloepVOSet (
    ownerId number(19,0) not null,
    valuta varchar2(255) not null,
    verdi number(19,0) not null,
    kommentar varchar2(255),
    primary key (ownerId, valuta)
);

alter table CompositeL1BeloepVOSet add constraint FK_BubbleWithComp_Component_1 foreign key (ownerId) references BubbleWithCompositeComponent;

create table CompositeL2BeloepVOSet (
    ownerId number(19,0) not null,
    valuta varchar2(255) not null,
    verdi number(19,0) not null,
    kommentar varchar2(255),
    primary key (ownerId, valuta)
);

alter table CompositeL2BeloepVOSet add constraint FK_CompositeL2BeloepVOSet_owId foreign key (ownerId) references BubbleWithCompositeComponent;

create table BubbleWithEntityComponent (
    id number(19,0) not null,
    nr number(10,0),
    level1ComponentId number(19,0),
    text varchar2(255),
    nestedComponentId number(19,0),
    primary key (id)
);

create table Level1EntityComponent (
    id number(19,0) not null,
    ownerId number(19,0) constraint Level1EComponent_ownerId_null not null initially deferred,
    text varchar2(255),
    valuta varchar2(255),
    verdi number(19,0),
    kommentar varchar2(255),
    level2ComponentId number(19,0),
    primary key (id)
);
alter table BubbleWithEntityComponent add constraint FK_BWithEntity_level1CompId foreign key (level1ComponentId) references Level1EntityComponent;

create table EntityL1BeloepVOSet (
    ownerId number(19,0) not null,
    valuta varchar2(255) not null,
    verdi number(19,0) not null,
    kommentar varchar2(255),
    primary key (ownerId, valuta)
);
alter table EntityL1BeloepVOSet add constraint FK_EntityL1BeloepVOSet_ownerId foreign key (ownerId) references Level1EntityComponent;

create table Level2EntityComponent (
    id number(19,0) not null,
    ownerId number(19,0) constraint Level2EComponent_ownerId_null not null initially deferred,
    text varchar2(255),
    valuta varchar2(255),
    verdi number(19,0),
    kommentar varchar2(255),
    primary key (id)
);

alter table Level1EntityComponent add constraint FK_Level1Entity_level2CompId foreign key (level2ComponentId) references Level2EntityComponent;

create table EntityL2BeloepVOSet (
    ownerId number(19,0) not null,
    valuta varchar2(255) not null,
    verdi number(19,0) not null,
    kommentar varchar2(255),
    primary key (ownerId, valuta)
);
alter table EntityL2BeloepVOSet add constraint FK_EntityL2BeloepVOSet_ownerId foreign key (ownerId) references Level2EntityComponent;

create table SetAaEntityComp (
  id number(19,0) not null,
  ownerId number(19,0) constraint SetAaEntityComp_ownerId_null not null initially deferred,
  ident number(10,0) not null,
  text varchar2(255),
  level1ComponentId number(19,0),
  nestedComponentId number(19,0),
  primary key (id)
);
alter table SetAaEntityComp add constraint FK_SetAaEntityComp_ownerId foreign key (ownerId) references BubbleWithEntityComponent;

create table SetAaEntityBeloepVOSet (
  ownerId number(19,0) not null,
  valuta varchar2(255) not null,
  verdi number(19,0) not null,
  kommentar varchar2(255),
  primary key (ownerId, valuta)
);
alter table SetAaEntityBeloepVOSet add constraint FK_SetAaEntBeloepVOSet_ownerId foreign key (ownerId) references SetAaEntityComp;

create table SetAaL1EntityComp (
  id number(19,0) not null,
  ownerId number(19,0) constraint SetAaL1EntityComp_ownerId_null not null initially deferred,
  text varchar2(255),
  level2ComponentId number(19,0),
  primary key (id)
);
alter table SetAaL1EntityComp add constraint FK_SetAaL1EntComp_ownerId foreign key (ownerId) references SetAaEntityComp;
alter table SetAaEntityComp add constraint FK_SetAaEntityComp_level1Id foreign key (level1ComponentId) references SetAaL1EntityComp;

create table SetAaL1EntBeloepVOSet (
  ownerId number(19,0) not null,
  valuta varchar2(255) not null,
  verdi number(19,0) not null,
  kommentar varchar2(255),
  primary key (ownerId, valuta)
);
alter table SetAaL1EntBeloepVOSet add constraint FK_SetAaL1EntBeloepVOSet_ownId foreign key (ownerId) references SetAaL1EntityComp;

create table SetAaL2EntityComp (
  id number(19,0) not null,
  ownerId number(19,0) constraint SetAaL2EntityComp_ownerId_null not null initially deferred,
  text varchar2(255),
  primary key (id)
);
alter table SetAaL2EntityComp add constraint FK_SetAaL2EntComp_ownerId foreign key (ownerId) references SetAaL1EntityComp;
alter table SetAaL1EntityComp add constraint FK_SetAaL1EntComp_level2Id foreign key (level2ComponentId) references SetAaL2EntityComp;

create table SetAaL2EntBeloepVOSet (
  ownerId number(19,0) not null,
  valuta varchar2(255) not null,
  verdi number(19,0) not null,
  kommentar varchar2(255),
  primary key (ownerId, valuta)
);
alter table SetAaL2EntBeloepVOSet add constraint FK_SetAaL2EntBeloepVOSet_ownId foreign key (ownerId) references SetAaL2EntityComp;

create table NestedEntityComp (
  id number(19,0) not null,
  ownerId number(19,0), -- denne er null hvis objektet blir pekt på direkte fra en annet objekt istedet for å inngå i et sett
  text varchar2(255),
  nestedComponentId number(19,0),
  primary key (id)
);
alter table NestedEntityComp add constraint FK_NestedEntityComp_nestedId foreign key (nestedComponentId) references NestedEntityComp;
alter table NestedEntityComp add constraint FK_NestedEntityComp_ownerId foreign key (ownerId) references NestedEntityComp;


create table BubbleWithEntInCompComponent (
  id number(19,0) not null,
  nr number(10,0),
  text varchar2(255),
  l1_text varchar2(255),
  l1_entityId number(19,0),
  l2_text varchar2(255),
  l2_entityId number(19,0),
  primary key (id)
);

create table L1EntInCompComponent (
  id number(19,0) not null,
  ownerId number(19,0) constraint L1EntInComp_ownerId_null not null initially deferred,
  text varchar2(255),
  primary key (id)
);
alter table L1EntInCompComponent add constraint FK_L1EntInComp_ownerId foreign key (ownerId) references BubbleWithEntInCompComponent;
alter table BubbleWithEntInCompComponent add constraint FK_BubbleWithEntInComp_l1eId foreign key (l1_entityId) references L1EntInCompComponent;

create table L1SetEntInCompComponent (
  id number(19,0) not null,
  ownerId number(19,0) constraint L1SetEntInComp_ownerId_null not null initially deferred,  -- Hibernate setter feltet til null før sletting. Kan derfor ikke bruke 'not null' direkte
  text varchar2(255),
  primary key (id)
);
alter table L1SetEntInCompComponent add constraint FK_L1SetEntInComp_ownerId foreign key (ownerId) references BubbleWithEntInCompComponent;

create table L2EntInCompComponent (
  id number(19,0) not null,
  ownerId number(19,0) constraint L2EntInComp_ownerId_null not null initially deferred,
  text varchar2(255),
  primary key (id)
);
alter table BubbleWithEntInCompComponent add constraint FK_BubbleWithEntInComp_l2eId foreign key (l2_entityId) references L2EntInCompComponent;

create table L2SetEntInCompComponent (
  id number(19,0) not null,
  ownerId number(19,0) constraint L2SetEntInComp_ownerId_null not null initially deferred,  -- Hibernate setter feltet til null før sletting. Kan derfor ikke bruke 'not null' direkte
  text varchar2(255),
  primary key (id)
);
alter table L2SetEntInCompComponent add constraint FK_L2SetEntInComp_ownerId foreign key (ownerId) references BubbleWithEntInCompComponent;


-- Noen enklere bobler med entitycomponents som har historikk

CREATE TABLE HIST_ENTITY_COMPONENT (
    ID NUMBER(19,0) NOT NULL,
    OPPDATERINGSDATO TIMESTAMP(6),
    SLUTTDATO TIMESTAMP(6),
    VERSJONID NUMBER(19,0),
    VALUE VARCHAR2(255 CHAR),
    BUBBLEID NUMBER(19,0),
    CONSTRAINT PK_HIST_ENTITY_COMPONENT PRIMARY KEY (ID)
);
COMMENT ON TABLE HIST_ENTITY_COMPONENT IS 'Komponent for bobble med historikk';

CREATE TABLE HIST_BUBBLE_W_ENTITIES (
    ID NUMBER(19,0) NOT NULL,
    OPPDATERINGSDATO TIMESTAMP(6),
    SLUTTDATO TIMESTAMP(6),
    VERSJONID NUMBER(19,0),
    MAINENTITYCOMPONENT NUMBER(19,0) CONSTRAINT FK_HIST_BUBBLE_W_ENTITIES_1 REFERENCES HIST_ENTITY_COMPONENT(ID),
    CONSTRAINT PK_HIST_BUBBLE_W_ENTITIES PRIMARY KEY (ID)
);
COMMENT ON TABLE HIST_BUBBLE_W_ENTITIES IS 'Objekt med flere relasjoner til samme type EntityComponent og historikk';

ALTER TABLE HIST_ENTITY_COMPONENT ADD (
  CONSTRAINT FK_HIST_ENTITY_COMPONENT_1 FOREIGN KEY (BUBBLEID) REFERENCES HIST_BUBBLE_W_ENTITIES(ID)
);


CREATE TABLE HIST_BUBBLE_W_LENTITIES (
    ID NUMBER(19,0) NOT NULL,
    OPPDATERINGSDATO TIMESTAMP(6),
    SLUTTDATO TIMESTAMP(6),
    VERSJONID NUMBER(19,0),
    CONSTRAINT PK_HIST_BUBBLE_W_LENTITIES PRIMARY KEY (ID)
);
COMMENT ON TABLE HIST_BUBBLE_W_LENTITIES IS 'Bobble med historikk og liste av mange EntityComponent';

CREATE TABLE HIST_LENTITY_COMPONENT (
    ID NUMBER(19,0) NOT NULL,
    OPPDATERINGSDATO TIMESTAMP(6),
    SLUTTDATO TIMESTAMP(6),
    VERSJONID NUMBER(19,0),
    TEXTVALUE VARCHAR2(255 CHAR),
    LISTOFENTITYCOMPONENTSID NUMBER(19,0) CONSTRAINT FK_HIST_LENTITY_COMPONENT_1 REFERENCES HIST_BUBBLE_W_LENTITIES(ID),
    LISTINDEX NUMBER(10,0),
    CONSTRAINT PK_HIST_LENTITY_COMPONENT PRIMARY KEY (ID)
);
COMMENT ON TABLE HIST_LENTITY_COMPONENT IS 'Element i liste av komponenter for bobble med historikk';


CREATE TABLE Multirefererende (
    ID NUMBER(19,0) NOT NULL,
    CONSTRAINT PK_MULTIREFERERENDE PRIMARY KEY (ID)
);

--todo: forbedret navn
CREATE TABLE MultirefererendeKobling (
    MULTIREFERERENDEID NUMBER(19,0) NOT NULL,
    ROLLE VARCHAR2(255 CHAR) NOT NULL,
    TEKST VARCHAR2(255 CHAR) NOT NULL,
    CONSTRAINT PK_MULTIREFERERENDEKOBLING PRIMARY KEY (MULTIREFERERENDEID, ROLLE, TEKST)
);

ALTER TABLE MULTIREFERERENDEKOBLING ADD (
  CONSTRAINT FK_MULTIREFERERENDEKOBLING_1 FOREIGN KEY (MULTIREFERERENDEID) REFERENCES MULTIREFERERENDE(ID)
);


-- Tabeller for relasjonstesting

create table X1BBOne (
  id number(19,0) not null,
  nr number(10,0),
  text varchar2(255),
  primary key (id)
);

create table X1AA (
  id number(19,0) not null,
  nr number(10,0),
  text varchar2(255),
  someBBId number(19,0),
  uniqueOnX1AA varchar2(255),
  nonUniqueOnX1AA varchar2(255),
  primary key (id)
);
alter table X1AA add constraint FK_X1AA_someBBId foreign key (someBBId) references X1BBOne;

create table X1CCMany (
  id number(19,0) not null,
  nr number(10,0),
  text varchar2(255),
  ownerId number(19,0),
  primary key (id)
);
alter table X1CCMany add constraint FK_X1CCMany_ownerId foreign key (ownerId) references X1AA;

create table X2BBOne (
  id number(19,0) not null,
  nr number(10,0),
  text varchar2(255),
  primary key (id)
);

create table X2EntityComponentOne (
  id number(19,0) not null,
  ownerId number(19,0),
  text varchar2(255),
  someBBId number(19,0),
  primary key (id)
);
alter table X2EntityComponentOne add constraint FK_X2EntityCompOne_someBBId foreign key (someBBId) references X2BBOne;


create table X2AAWithEntComp (
  id number(19,0) not null,
  nr number(10,0),
  text varchar2(255),
  entityComponentOneId number(19,0),
  primary key (id)
);
alter table X2AAWithEntComp add constraint FK_X2AA_entityComponentOneId foreign key (entityComponentOneId) references X2EntityComponentOne;


create table X2SetEntityComp (
  id number(19,0) not null,
  ownerId number(19,0) constraint X2SetEntityComp_ownerId_null not null initially deferred,
  text varchar2(255),
  role1BBOneId number(19,0),
  primary key (id)
);
alter table X2SetEntityComp add constraint FK_X2SetEntityComp_ownerId foreign key (ownerId) references X2AAWithEntComp;
alter table X2SetEntityComp add constraint FK_X2SetEntityComp_bbOneId foreign key (role1BBOneId) references X2BBOne;


create table X2CCMany (
  id number(19,0) not null,
  nr number(10,0),
  text varchar2(255),
  ownerId number(19,0),
  primary key (id)
);
alter table X2CCMany add constraint FK_X2CCMany_ownerId foreign key (ownerId) references X2EntityComponentOne;

-- Denne map tabell brukes av StoreTest1ServiceTest
create table TestMap (
k varchar2(255) not null,
v Varchar2(255),
Primary Key (k)
);
--


-- Gammel kode her fra ---


create table BubbleWithComponents (
  id number(19,0) not null,
  Primary Key (Id)
);

create table BubbleWithComponentsComponent (
  id number(19,0) not null,
  bubbleId number(19,0),
  Text Varchar2(255),
  Primary Key (Id)
);
ALTER TABLE BubbleWithComponentsComponent ADD CONSTRAINT FK_BWCComponent_Component FOREIGN KEY (bubbleId) REFERENCES BubbleWithComponents;
create index IDX_BWCComponent_bubbleId on BubbleWithComponentsComponent(bubbleId);





create table AKode ( id number(19,0) not null, kodeVerdi varchar2(10) not null, primary key (id) );
create table AKodeLoc ( id number(19,0) not null, locale varchar2(10) not null, name varchar2(64) not null, value varchar2(255) not null, primary key (id, locale, name));
alter table AKodeLoc add constraint FK_AKodeLoc foreign key (id) references AKode;

create table BKode ( id number(19,0) not null, kodeVerdi varchar2(10) not null, primary key (id) );
create table BKodeLoc ( id number(19,0) not null, locale varchar2(10) not null, name varchar2(64) not null, value varchar2(255) not null, primary key (id, locale, name));
alter table BKodeLoc add constraint FK_BKodeLoc foreign key (id) references BKode;

create table CKode ( id number(19,0) not null, kodeVerdi varchar2(10) not null, class varchar2(64) not null, primary key (id) );
create table CKodeLoc ( id number(19,0) not null, locale varchar2(10) not null, name varchar2(64) not null, value varchar2(255) not null, primary key (id, locale, name));
alter table CKodeLoc add constraint FK_CKodeLoc foreign key (id) references CKode;

create table XStrKode ( id varchar2(10) not null, kodeVerdi varchar2(10) not null, primary key (id) );
create table XStrKodeLoc ( id varchar2(10) not null, locale varchar2(10) not null, name varchar2(64) not null, value varchar2(255) not null, primary key (id, locale, name));
alter table XStrKodeLoc add constraint FK_XStrKodeLoc foreign key (id) references XStrKode;

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


