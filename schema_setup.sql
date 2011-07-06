drop table TestEntity;
drom table TestBubble;

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

insert into TestEntity values (1, "Text 1");
insert into TestBubble values (1, "Text 1");
