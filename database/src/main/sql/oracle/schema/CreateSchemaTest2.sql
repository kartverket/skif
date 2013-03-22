-- Oppretter tabeller som brukes i storetest2-testprosjektet. Her skal det brukes mockupdata via mockup-rammeverk.

create table Eiendom (
    id number(19,0) not null,
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
    endringstype number(10,0),
    endringstidspunkt timestamp,
    brukernavn varchar2(255 char),
    endretBubbleId number(19,0),
    primary key (id)
);

alter table EiendomForEier
    add constraint FKE3E20945AA81BF14
    foreign key (eierId)
    references Eier;

alter table EiendomForEier
    add constraint FK_Eier_Eiendom
    foreign key (eiendomId)
    references Eiendom;
