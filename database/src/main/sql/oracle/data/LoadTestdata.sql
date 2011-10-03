insert into TestEntity values (1, 'Text 1');
insert into TestBubble values (1, 'Text 1');
insert into TestBubble values (2, 'Text 2');

insert into TableSequence (tableName, nextFreeNumber) values ('GLOBAL_SEQUENCE',999999);

insert into TestAKode values(1, 'A1');
insert into TestAKode values(2, 'A');
insert into TestAKodeLoc values(1, 'b', 'Kodebeskrivelse for A1 bokmål');
insert into TestAKodeLoc values(1, 'n', 'Kodebeskrivelse for A1 nynorsk');
insert into TestAKodeLoc values(2, 'b', 'Kodebeskrivelse for A bokmål');
insert into TestAKodeLoc values(2, 'n', 'Kodebeskrivelse for A nynorsk');

insert into TestBKode values(1, 'B1');
insert into TestBKode values(2, 'B2');
insert into TestBKodeLoc values(1, 'b', 'Kodebeskrivelse for B1 bokmål');
insert into TestBKodeLoc values(1, 'n', 'Kodebeskrivelse for B1 nynorsk');
insert into TestBKodeLoc values(2, 'b', 'Kodebeskrivelse for B2 bokmål');
insert into TestBKodeLoc values(2, 'n', 'Kodebeskrivelse for B2 nynorsk');

insert into TestCKode values(1, 'C1A', 'TestC1DbKode');
insert into TestCKode values(2, 'C1B', 'TestC1DbKode');
insert into TestCKode values(10, 'C2A', 'TestC2DbKode');
insert into TestCKode values(11, 'C2B', 'TestC2DbKode');
insert into TestCKodeLoc values(1, 'b', 'Kodebeskrivelse for C1A bokmål');
insert into TestCKodeLoc values(1, 'n', 'Kodebeskrivelse for C1A nynorsk');
insert into TestCKodeLoc values(2, 'b', 'Kodebeskrivelse for C1B bokmål');
insert into TestCKodeLoc values(2, 'n', 'Kodebeskrivelse for C1B nynorsk');
insert into TestCKodeLoc values(10, 'b', 'Kodebeskrivelse for C2A bokmål');
insert into TestCKodeLoc values(10, 'n', 'Kodebeskrivelse for C2A nynorsk');
insert into TestCKodeLoc values(11, 'b', 'Kodebeskrivelse for C2B bokmål');
insert into TestCKodeLoc values(11, 'n', 'Kodebeskrivelse for C2B nynorsk');

insert into Kodeliste values(10001, 'TestAKode', 'no.statkart.skif.storetest.domain.kodeliste.TestADbKode');
insert into Kodeliste values(10002, 'TestBKode', 'no.statkart.skif.storetest.domain.kodeliste.TestBDbKode');
insert into Kodeliste values(10003, 'TestC1Kode', 'no.statkart.skif.storetest.domain.kodeliste.TestC1DbKode');
insert into Kodeliste values(10004, 'TestC2Kode', 'no.statkart.skif.storetest.domain.kodeliste.TestC2DbKode');

insert into KodelisteLoc values(10001, 'b', 'Kodelistebeskrivelse for TestAKode bokmål');
insert into KodelisteLoc values(10001, 'n', 'Kodelistebeskrivelse for TestAKode nynorsk');
insert into KodelisteLoc values(10002, 'b', 'Kodelistebeskrivelse for TestBKode bokmål');
insert into KodelisteLoc values(10002, 'n', 'Kodelistebeskrivelse for TestBKode nynorsk');
insert into KodelisteLoc values(10003, 'b', 'Kodelistebeskrivelse for TestC1Kode bokmål');
insert into KodelisteLoc values(10003, 'n', 'Kodelistebeskrivelse for TestC1Kode nynorsk');
insert into KodelisteLoc values(10004, 'b', 'Kodelistebeskrivelse for TestC2Kode bokmål');
insert into KodelisteLoc values(10004, 'n', 'Kodelistebeskrivelse for TestC2Kode nynorsk');

insert into FOO_H (ID,TBEGIN,TEND,TVERSION,A,B) values (100,to_timestamp('02.10.2011 08.00.00,000000000','DD.MM.RRRR HH24.MI.SS,FF'),to_timestamp('02.10.2011 08.01.00,000000000','DD.MM.RRRR HH24.MI.SS,FF'),1,10,'A1');
insert into FOO_H (ID,TBEGIN,TEND,TVERSION,A,B) values (100,to_timestamp('02.10.2011 08.01.00,000000000','DD.MM.RRRR HH24.MI.SS,FF'),to_timestamp('02.10.2011 08.02.00,000000000','DD.MM.RRRR HH24.MI.SS,FF'),2,11,'A1');
insert into FOO_H (ID,TBEGIN,TEND,TVERSION,A,B) values (100,to_timestamp('02.10.2011 08.02.00,000000000','DD.MM.RRRR HH24.MI.SS,FF'),to_timestamp('02.10.2011 08.03.00,000000000','DD.MM.RRRR HH24.MI.SS,FF'),3,12,'A3');
insert into FOO_H (ID,TBEGIN,TEND,TVERSION,A,B) values (100,to_timestamp('02.10.2011 08.03.00,000000000','DD.MM.RRRR HH24.MI.SS,FF'),to_timestamp('02.10.2011 08.04.00,000000000','DD.MM.RRRR HH24.MI.SS,FF'),4,13,'A1');
insert into FOO_H (ID,TBEGIN,TEND,TVERSION,A,B) values (100,to_timestamp('02.10.2011 08.04.00,000000000','DD.MM.RRRR HH24.MI.SS,FF'),to_timestamp('02.10.2011 08.05.00,000000000','DD.MM.RRRR HH24.MI.SS,FF'),5,13,'B52');
insert into FOO_H (ID,TBEGIN,TEND,TVERSION,A,B) values (100,to_timestamp('02.10.2011 08.05.00,000000000','DD.MM.RRRR HH24.MI.SS,FF'),to_timestamp('02.10.2011 08.06.00,000000000','DD.MM.RRRR HH24.MI.SS,FF'),6,17,'17');
insert into FOO_H (ID,TBEGIN,TEND,TVERSION,A,B) values (100,to_timestamp('02.10.2011 08.06.00,000000000','DD.MM.RRRR HH24.MI.SS,FF'),to_timestamp('01.01.9999 00.00.00,000000000','DD.MM.RRRR HH24.MI.SS,FF'),7,21,'C1');
