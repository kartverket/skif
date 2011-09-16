insert into TestEntity values (1, 'Text 1');
insert into TestBubble values (1, 'Text 1');

insert into TableSequence (tableName, nextFreeNumber) values ('GLOBAL_SEQUENCE',999999);

insert into TestAKode values(1, 'A1');
insert into TestAKode values(2, 'A2');
insert into TestAKodeLoc values(1, 'b', 'Kodebeskrivelse for A1 bokmål');
insert into TestAKodeLoc values(1, 'n', 'Kodebeskrivelse for A1 nynorks');
insert into TestAKodeLoc values(2, 'b', 'Kodebeskrivelse for A2 bokmål');
insert into TestAKodeLoc values(2, 'n', 'Kodebeskrivelse for A2 nynorks');

insert into TestBKode values(1, 'B1');
insert into TestBKode values(2, 'B2');
insert into TestBKodeLoc values(1, 'b', 'Kodebeskrivelse for B1 bokmål');
insert into TestBKodeLoc values(1, 'n', 'Kodebeskrivelse for B1 nynorks');
insert into TestBKodeLoc values(2, 'b', 'Kodebeskrivelse for B2 bokmål');
insert into TestBKodeLoc values(2, 'n', 'Kodebeskrivelse for B2 nynorks');

insert into TestCKode values(1, 'C1A', 'TestC1DbKode');
insert into TestCKode values(2, 'C1B', 'TestC1DbKode');
insert into TestCKode values(10, 'C2A', 'TestC2DbKode');
insert into TestCKode values(11, 'C2B', 'TestC2DbKode');
insert into TestCKodeLoc values(1, 'b', 'Kodebeskrivelse for C1A bokmål');
insert into TestCKodeLoc values(1, 'n', 'Kodebeskrivelse for C1A nynorks');
insert into TestCKodeLoc values(2, 'b', 'Kodebeskrivelse for C1B bokmål');
insert into TestCKodeLoc values(2, 'n', 'Kodebeskrivelse for C1B nynorks');
insert into TestCKodeLoc values(10, 'b', 'Kodebeskrivelse for C2A bokmål');
insert into TestCKodeLoc values(10, 'n', 'Kodebeskrivelse for C2A nynorks');
insert into TestCKodeLoc values(11, 'b', 'Kodebeskrivelse for C2B bokmål');
insert into TestCKodeLoc values(11, 'n', 'Kodebeskrivelse for C2B nynorks');

insert into Kodeliste values(10001, 'TestAKode', 'no.statkart.skif.storetest.domain.kodeliste.TestADbKode');
insert into Kodeliste values(10002, 'TestBKode', 'no.statkart.skif.storetest.domain.kodeliste.TestBDbKode');
insert into Kodeliste values(10003, 'TestC1Kode', 'no.statkart.skif.storetest.domain.kodeliste.TestC1DbKode');
insert into Kodeliste values(10004, 'TestC2Kode', 'no.statkart.skif.storetest.domain.kodeliste.TestC2DbKode');

insert into KodelisteLoc values(10001, 'b', 'Kodelistebeskrivelse for TestAKode bokmål');
insert into KodelisteLoc values(10001, 'n', 'Kodelistebeskrivelse for TestAKode nynorks');
insert into KodelisteLoc values(10002, 'b', 'Kodelistebeskrivelse for TestBKode bokmål');
insert into KodelisteLoc values(10002, 'n', 'Kodelistebeskrivelse for TestBKode nynorks');
insert into KodelisteLoc values(10003, 'b', 'Kodelistebeskrivelse for TestC1Kode bokmål');
insert into KodelisteLoc values(10003, 'n', 'Kodelistebeskrivelse for TestC1Kode nynorks');
insert into KodelisteLoc values(10004, 'b', 'Kodelistebeskrivelse for TestC2Kode bokmål');
insert into KodelisteLoc values(10004, 'n', 'Kodelistebeskrivelse for TestC2Kode nynorks');
