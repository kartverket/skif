-- Tabeller som tilhører standalone pakken og som ikke brukes via mockup-rammeverket

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

CREATE TABLE TestBubbleWithHistory_H (
  id NUMBER(19,0) NOT NULL ENABLE,
  oppdateringsdato timestamp(6) not null,
  sluttdato timestamp(6) not null,
  versjonId number (19,0) not null,
  nr number(10,0),
  text VARCHAR2(255 BYTE),
  PRIMARY KEY (ID, sluttdato)
);
create view TestBubbleWithHistory as select * from TestBubbleWithHistory_H  where snapshot_time.t_between(oppdateringsdato, sluttdato)=1;

CREATE OR REPLACE TRIGGER TestBubbleWithHistory_TRIGGER
INSTEAD OF INSERT OR UPDATE OR DELETE ON TestBubbleWithHistory
FOR EACH ROW
DECLARE
t_Trans TIMESTAMP := snapshot_time.Get_T_Trans();
t_End TIMESTAMP := snapshot_time.Get_T_CURRENT();
BEGIN
  IF UPDATING THEN
    IF :old.oppdateringsdato < t_Trans THEN
        INSERT INTO TestBubbleWithHistory_H
        VALUES (:old.id, :old.oppdateringsdato, t_Trans, :old.versjonId, :old.nr, :old.text);

        UPDATE TestBubbleWithHistory_H SET versjonId = :old.versjonId + 1 WHERE id= :new.id and sluttdato = t_End;
    END IF;
    UPDATE TestBubbleWithHistory_H
    SET id = :new.id, oppdateringsdato = t_Trans, nr = :new.nr, text = :new.text
    WHERE id = :new.id and sluttdato = t_End;
  ELSIF INSERTING THEN
    INSERT INTO TestBubbleWithHistory_H
        VALUES (:new.id, t_Trans,t_End, 1, :new.nr, :new.text);
  ELSIF DELETING THEN
    IF :old.oppdateringsdato < t_Trans THEN
        INSERT INTO TestBubbleWithHistory_H
        VALUES (:old.id, :old.oppdateringsdato, t_Trans, :old.versjonId, :old.nr, :old.text);
    END IF;
    delete from TestBubbleWithHistory_H
    WHERE id = :old.id and sluttdato = t_End;
  END IF;
END TestBubbleWithHistory_TRIGGER;
/

create table SelfBubble (
id number(19,0) not null,
text varchar2(255),
refId number(19,0),

Primary Key (Id)
);
alter table SelfBubble add constraint Self_FK foreign key (refId) references SelfBubble;

create table FilteredBubble (
  id number(19,0) not null,
  Text Varchar2(255),
  filter number(1,0),
  FilterText Varchar2(255),
  Primary Key (Id)
);


create table ParentBubble (
  id number(19,0) not null,
  Text Varchar2(255),
  Primary Key (Id)
);

CREATE TABLE CHILDFORPARENT(
  id number (19,0) not null,
  parentBubbleId number(19,0) not null,
  childBubbleId number(19,0) not null,
  Primary Key (id)
);

create table ChildBubble(
  id number(19,0) not null,
  TEXT VARCHAR2(255),
  TESTBUBBLEID number(19,0),
  PRIMARY KEY (ID)
) ;

alter table ChildForParent add constraint FK23723BEA16AF2FAB foreign key (parentBubbleId) references ParentBubble;
ALTER TABLE CHILDFORPARENT ADD CONSTRAINT FK_CHILDFORPARENT_CHILD FOREIGN KEY (CHILDBUBBLEID) REFERENCES CHILDBUBBLE;
