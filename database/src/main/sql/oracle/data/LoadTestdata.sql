insert into TestEntity values (1, 'Text 1');
insert into TestBubble values (1, 'Text 1');
insert into TestBubble values (2, 'Text 2');

insert into TableSequence (tableName, nextFreeNumber) values ('GLOBAL_SEQUENCE',999999);

insert into AKode values(1, 'A1');
insert into AKode values(2, 'A2');
insert into AKodeLoc values(1, 'b', 'Kodebeskrivelse for A1 bokmål');
insert into AKodeLoc values(1, 'n', 'Kodebeskrivelse for A1 nynorsk');
insert into AKodeLoc values(2, 'b', 'Kodebeskrivelse for A bokmål');
insert into AKodeLoc values(2, 'n', 'Kodebeskrivelse for A nynorsk');

insert into BKode values(1, 'B1');
insert into BKode values(2, 'B2');
insert into BKodeLoc values(1, 'b', 'Kodebeskrivelse for B1 bokmål');
insert into BKodeLoc values(1, 'n', 'Kodebeskrivelse for B1 nynorsk');
insert into BKodeLoc values(2, 'b', 'Kodebeskrivelse for B2 bokmål');
insert into BKodeLoc values(2, 'n', 'Kodebeskrivelse for B2 nynorsk');

insert into CKode values(1, 'C1A', 'C1DbKode');
insert into CKode values(2, 'C1B', 'C1DbKode');
insert into CKode values(10, 'C2A', 'C2DbKode');
insert into CKode values(11, 'C2B', 'C2DbKode');
insert into CKodeLoc values(1, 'b', 'Kodebeskrivelse for C1A bokmål');
insert into CKodeLoc values(1, 'n', 'Kodebeskrivelse for C1A nynorsk');
insert into CKodeLoc values(2, 'b', 'Kodebeskrivelse for C1B bokmål');
insert into CKodeLoc values(2, 'n', 'Kodebeskrivelse for C1B nynorsk');
insert into CKodeLoc values(10, 'b', 'Kodebeskrivelse for C2A bokmål');
insert into CKodeLoc values(10, 'n', 'Kodebeskrivelse for C2A nynorsk');
insert into CKodeLoc values(11, 'b', 'Kodebeskrivelse for C2B bokmål');
insert into CKodeLoc values(11, 'n', 'Kodebeskrivelse for C2B nynorsk');

insert into XstrKode values('A', 'X1');
insert into XstrKode values('B', 'X2');
insert into XstrKodeLoc values('A', 'b', 'Kodebeskrivelse for X1 bokmål');
insert into XstrKodeLoc values('A', 'n', 'Kodebeskrivelse for X1 nynorsk');
insert into XstrKodeLoc values('B', 'b', 'Kodebeskrivelse for X2 bokmål');
insert into XstrKodeLoc values('B', 'n', 'Kodebeskrivelse for X2 nynorsk');

insert into YstrKode values('A', 'Y1');
insert into YstrKode values('B', 'Y2');
insert into YstrKodeLoc values('A', 'b', 'Kodebeskrivelse for Y1 bokmål');
insert into YstrKodeLoc values('A', 'n', 'Kodebeskrivelse for Y1 nynorsk');
insert into YstrKodeLoc values('B', 'b', 'Kodebeskrivelse for Y2 bokmål');
insert into YstrKodeLoc values('B', 'n', 'Kodebeskrivelse for Y2 nynorsk');

insert into Kodeliste values(10001, 'AKode', 'no.statkart.skif.storetest.domain.demo.koder.ADbKode');
insert into Kodeliste values(10002, 'BKode', 'no.statkart.skif.storetest.domain.demo.koder.BDbKode');
insert into Kodeliste values(10003, 'C1Kode', 'no.statkart.skif.storetest.domain.demo.koder.C1DbKode');
insert into Kodeliste values(10004, 'C2Kode', 'no.statkart.skif.storetest.domain.demo.koder.C2DbKode');
--insert into Kodeliste values(10005, 'XStrKode', 'no.statkart.skif.storetest.domain.demo.koder.XStrKode');

insert into KodelisteLoc values(10001, 'b', 'Kodelistebeskrivelse for AKode bokmål');
insert into KodelisteLoc values(10001, 'n', 'Kodelistebeskrivelse for AKode nynorsk');
insert into KodelisteLoc values(10002, 'b', 'Kodelistebeskrivelse for BKode bokmål');
insert into KodelisteLoc values(10002, 'n', 'Kodelistebeskrivelse for BKode nynorsk');
insert into KodelisteLoc values(10003, 'b', 'Kodelistebeskrivelse for C1Kode bokmål');
insert into KodelisteLoc values(10003, 'n', 'Kodelistebeskrivelse for C1Kode nynorsk');
insert into KodelisteLoc values(10004, 'b', 'Kodelistebeskrivelse for C2Kode bokmål');
insert into KodelisteLoc values(10004, 'n', 'Kodelistebeskrivelse for C2Kode nynorsk');
--insert into KodelisteLoc values(10005, 'b', 'Kodelistebeskrivelse for XstrKode bokmål');
--insert into KodelisteLoc values(10005, 'n', 'Kodelistebeskrivelse for XstrKode bokmål');

insert into FOO_H (ID,TBEGIN,TEND,TVERSION,NR,NAVN) values (100,snapshot_time.to_t('2011-10-02 08:00:00.00'),snapshot_time.to_t('2011-10-02 08:01:00.00'),1,2200,'KARTGATA');
insert into FOO_H (ID,TBEGIN,TEND,TVERSION,NR,NAVN) values (100,snapshot_time.to_t('2011-10-02 08:01:00.00'),snapshot_time.to_t('2011-10-02 08:02:00.00'),2,2200,'KARTVEGEN');
insert into FOO_H (ID,TBEGIN,TEND,TVERSION,NR,NAVN) values (100,snapshot_time.to_t('2011-10-02 08:02:00.00'),snapshot_time.to_t('2011-10-02 08:03:00.00'),3,2200,'KARTVEIEN');
insert into FOO_H (ID,TBEGIN,TEND,TVERSION,NR,NAVN) values (100,snapshot_time.to_t('2011-10-02 08:03:00.00'),snapshot_time.to_t('2011-10-02 08:04:00.00'),4,2200,'KART-VEIEN');
insert into FOO_H (ID,TBEGIN,TEND,TVERSION,NR,NAVN) values (100,snapshot_time.to_t('2011-10-02 08:04:00.00'),snapshot_time.to_t('9999-01-01 00:00:00.00'),5,2200,'KARTVEIEN');

insert into FOO_H (ID,TBEGIN,TEND,TVERSION,NR,NAVN) values (101,snapshot_time.to_t('2011-10-02 08:03:00.00'),snapshot_time.to_t('2011-10-02 08:04:00.00'),1,2201,'GAMMEL-VEIEN');
insert into FOO_H (ID,TBEGIN,TEND,TVERSION,NR,NAVN) values (101,snapshot_time.to_t('2011-10-02 08:04:00.00'),snapshot_time.to_t('9999-01-01 00:00:00.00'),2,2201,'GAMMELVEIEN');

insert into BAZ (ID, TEXT, FOOID, TESTAENUMKODEID, TESTC2DBKODEID) values (501, 'Baz 1', 100, 0, 10);
insert into BAZ (ID, TEXT, FOOID, TESTAENUMKODEID, TESTC2DBKODEID) values (502, 'Baz 2', 100, 1, 11);
insert into BAZ (ID, TEXT, FOOID, TESTAENUMKODEID, TESTC2DBKODEID) values (503, 'Baz 3', 101, 2, 10);

insert into RAZ (ID, TEXT,COMPTEXT, FOOID) values (601, 'Raz 1','CompRaz 1', 100);
insert into RAZ (ID, TEXT,COMPTEXT, FOOID) values (602, 'Raz 2','CompRaz 2', 100);
insert into RAZ (ID, TEXT,COMPTEXT, FOOID) values (603, 'Raz 3','CompRaz 3', 101);


insert into BAR_H (ID,TBEGIN,TEND,TVERSION,HUSNR,BOKSTAV,FOOID, BAZID) values (1001,snapshot_time.to_t('2011-10-02 08:00:30.00'),snapshot_time.to_t('2011-10-02 08:03:30.00'),1,105,NULL,100, 501);
insert into BAR_H (ID,TBEGIN,TEND,TVERSION,HUSNR,BOKSTAV,FOOID, BAZID) values (1001,snapshot_time.to_t('2011-10-02 08:03:30.00'),snapshot_time.to_t('9999-01-01 00:00:00.00'),2,106,NULL,100, 502);
insert into BAR_H (ID,TBEGIN,TEND,TVERSION,HUSNR,BOKSTAV,FOOID, BAZID) values (1002,snapshot_time.to_t('2011-10-02 08:00:30.00'),snapshot_time.to_t('2011-10-02 08:03:30.00'),1,205,NULL,100, 503);
insert into BAR_H (ID,TBEGIN,TEND,TVERSION,HUSNR,BOKSTAV,FOOID, BAZID) values (1002,snapshot_time.to_t('2011-10-02 08:03:30.00'),snapshot_time.to_t('9999-01-01 00:00:00.00'),2,206,NULL,100, 503);


insert into BARFOOS_H (ID,TBEGIN,TEND,TVERSION,TEXT,BARID) values (2001,snapshot_time.to_t('2011-10-02 08:00:30.00'),snapshot_time.to_t('2011-10-02 08:03:30.00'),1,'Text 1',1001);
insert into BARFOOS_H (ID,TBEGIN,TEND,TVERSION,TEXT,BARID) values (2001,snapshot_time.to_t('2011-10-02 08:03:30.00'),snapshot_time.to_t('9999-01-01 00:00:00.00'),2,'Text 2',1001);
insert into BARFOOS_H (ID,TBEGIN,TEND,TVERSION,TEXT,BARID) values (2002,snapshot_time.to_t('2011-10-02 08:00:30.00'),snapshot_time.to_t('2011-10-02 08:03:30.00'),1,'Text 1',1002);
insert into BARFOOS_H (ID,TBEGIN,TEND,TVERSION,TEXT,BARID) values (2002,snapshot_time.to_t('2011-10-02 08:03:30.00'),snapshot_time.to_t('9999-01-01 00:00:00.00'),2,'Text 2',1002);


insert into FooForBarFoos_H (BarFoosId, fooId, TBEGIN,TEND) values (2001, 100, snapshot_time.to_t('2011-10-02 08:03:30.00'),snapshot_time.to_t('9999-01-01 00:00:00.00'));
insert into FooForBarFoos_H (BarFoosId, fooId, TBEGIN,TEND) values (2001, 101, snapshot_time.to_t('2011-10-02 08:03:30.00'),snapshot_time.to_t('9999-01-01 00:00:00.00'));

