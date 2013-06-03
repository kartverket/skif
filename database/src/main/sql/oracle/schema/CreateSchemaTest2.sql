-- Oppretter tabeller som brukes i storetest2-testprosjektet. Her skal det brukes mockupdata via mockup-rammeverk.

create table BubbleWithEntityComponents (
    id number(19,0) not null,
    oppdateringsdato timestamp,
    sluttdato timestamp,
    versjonId number(19,0),
    mainEntityComponent number(19,0),
    primary key (id)
);

create table Eiendom (
    id number(19,0) not null,
    eiendomstypeKodeId number(5,0) not null,
    primary key (id)
);

create table EiendomForEier (
    eierId number(19,0) not null,
    eiendomId number(19,0) not null,
    primary key (eierId, eiendomId)
);

create table Eier (
    id number(19,0) not null,
    primary key (id)
);

create table Endring (
    id number(19,0) not null,
    class varchar2(255 char) not null,
    endringstype number(3,0),
    endringstidspunkt timestamp,
    brukernavn varchar2(255 char),
    endretBubbleId number(19,0),
    primary key (id)
);

create table EntityComponent (
    id number(19,0) not null,
    oppdateringsdato timestamp,
    sluttdato timestamp,
    versjonId number(19,0),
    value varchar2(255 char),
    bubbleId number(19,0),
    primary key (id)
);

create table Kodeliste2 (
    id number(19,0) not null,
    kodeTypeNavn varchar2(255 char),
    kodeIdClassname varchar2(255 char),
    primary key (id)
);

create table Kodeliste2Loc (
    id number(19,0) not null,
    navn varchar2(255 char),
    beskrivelse varchar2(255 char),
    lokale varchar2(255 char) not null,
    primary key (id, lokale)
);

create table ListEntityComponent (
    id number(19,0) not null,
    oppdateringsdato timestamp,
    sluttdato timestamp,
    versjonId number(19,0),
    textValue varchar2(255 char),
    listOfEntityComponentsId number(19,0),
    listIndex number(10,0),
    primary key (id)
);

create table ListOfEntityComponents (
    id number(19,0) not null,
    oppdateringsdato timestamp,
    sluttdato timestamp,
    versjonId number(19,0),
    primary key (id)
);

create table Multirefererende (
    id number(19,0) not null,
    primary key (id)
);

create table MultirefererendeKobling (
    multirefererendeId number(19,0) not null,
    rolle varchar2(255 char) not null,
    tekst varchar2(255 char) not null,
    primary key (multirefererendeId, rolle, tekst)
);

create table StoreTest2DbKode (
    id number(19,0) not null,
    class varchar2(255 char) not null,
    primary key (id)
);

create table StoreTest2DbKodeLoc (
    id number(19,0) not null,
    navn varchar2(255 char),
    beskrivelse varchar2(255 char),
    lokale varchar2(255 char) not null,
    primary key (id, lokale)
);

create table SubTypedBubble (
    id number(19,0) not null,
    class varchar2(255 char) not null,
    text varchar2(255 char),
    num number(10,0),
    primary key (id)
);

create table TekstForSubtype (
    subtypedid number(19,0) not null,
    tekst varchar2(255 char) not null,
    primary key (subtypedid, tekst)
);

alter table BubbleWithEntityComponents
    add constraint FK3405730B705AD7C8
    foreign key (mainEntityComponent)
    references EntityComponent;

alter table EiendomForEier
    add constraint FKE3E20945AA81BF14
    foreign key (eierId)
    references Eier;

alter table EiendomForEier
    add constraint FK_Eier_Eiendom
    foreign key (eiendomId)
    references Eiendom;

alter table EntityComponent
    add constraint FK_BUBBLE_WITH_ENTCOMPS
    foreign key (bubbleId)
    references BubbleWithEntityComponents;

alter table Kodeliste2Loc
    add constraint FK77F93E506889CC0F
    foreign key (id)
    references Kodeliste2;

alter table ListEntityComponent
    add constraint FK_LIST_OF_ENTITY_COMPONENTS
    foreign key (listOfEntityComponentsId)
    references ListOfEntityComponents;

alter table MultirefererendeKobling
    add constraint FK_MULTIREFEREREND_KOBLING
    foreign key (multirefererendeId)
    references Multirefererende;

alter table StoreTest2DbKodeLoc
    add constraint FKE2B172DE79AC50C8
    foreign key (id)
    references StoreTest2DbKode;

alter table TekstForSubtype
    add constraint FK31DBF85938AD5F10
    foreign key (subtypedid)
    references SubTypedBubble;
