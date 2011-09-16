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

create table TestAKode ( id number(19,0) not null, kodeVerdi varchar2(10) not null, primary key (id) );
create table TestAKodeLoc ( id number(19,0) not null, lokale varchar2(10) not null, beskrivelse varchar2(255) not null, primary key (id, lokale));
alter table TestAKodeLoc add constraint FK_TestAKodeLoc foreign key (id) references TestAKode;

create table TestBKode ( id number(19,0) not null, kodeVerdi varchar2(10) not null, primary key (id) );
create table TestBKodeLoc ( id number(19,0) not null, lokale varchar2(10) not null, beskrivelse varchar2(255) not null, primary key (id, lokale));
alter table TestBKodeLoc add constraint FK_TestBKodeLoc foreign key (id) references TestBKode;

create table TestCKode ( id number(19,0) not null, class varchar2(64) not null, kodeVerdi varchar2(10) not null, primary key (id) );
create table TestCKodeLoc ( id number(19,0) not null, lokale varchar2(10) not null, beskrivelse varchar2(255) not null, primary key (id, lokale));
alter table TestCKodeLoc add constraint FK_TestCKodeLoc foreign key (id) references TestCKode;

create table Kodeliste( id number(19,0) not null, navn varchar2(64), kodeClassname varchar2(255), primary key(id));
create table KodelisteLoc ( id number(19,0) not null, lokale varchar2(10) not null, beskrivelse varchar2(255) not null, primary key (id, lokale));
alter table KodelisteLoc add constraint FK_TestKodelisteLoc foreign key (id) references Kodeliste;
