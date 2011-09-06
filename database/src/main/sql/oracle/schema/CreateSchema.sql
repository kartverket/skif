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
