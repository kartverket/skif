-- Oppretter tabeller som brukes i storetest2-testprosjektet. Her skal det brukes mockupdata via mockup-rammeverk.

create table BubbleWithEntityComponents (
    id number(19,0) not null,
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
    value varchar2(255 char),
    bubbleId number(19,0),
    primary key (id)
);

create table ListEntityComponent (
    id number(19,0) not null,
    textValue varchar2(255 char),
    listOfEntityComponentsId number(19,0),
    listIndex number(10,0),
    primary key (id)
);

create table ListOfEntityComponents (
    id number(19,0) not null,
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

alter table ListEntityComponent
    add constraint FK_LIST_OF_ENTITY_COMPONENTS
    foreign key (listOfEntityComponentsId)
    references ListOfEntityComponents;

alter table MultirefererendeKobling
    add constraint FK_MULTIREFEREREND_KOBLING
    foreign key (multirefererendeId)
    references Multirefererende;

alter table TekstForSubtype
    add constraint FK31DBF85938AD5F10
    foreign key (subtypedid)
    references SubTypedBubble;
