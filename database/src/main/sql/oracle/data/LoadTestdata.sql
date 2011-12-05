insert into TestEntity values (1, 'Text 1');
insert into TestBubble values (1, 'Text 1');
insert into TestBubble values (2, 'Text 2');

insert into TableSequence (tableName, nextFreeNumber) values ('GLOBAL_SEQUENCE',999999);

insert into AKode values(1, 'A1');
insert into AKode values(2, 'A2');
insert into AKodeLoc values(1, 'no_NO',    'Kodebeskrivelse for A1 bokmål');
insert into AKodeLoc values(1, 'no_NO_NY', 'Kodebeskrivelse for A1 nynorsk');
insert into AKodeLoc values(2, 'no_NO',    'Kodebeskrivelse for A bokmål');
insert into AKodeLoc values(2, 'no_NO_NY', 'Kodebeskrivelse for A nynorsk');

insert into BKode values(1, 'B1');
insert into BKode values(2, 'B2');
insert into BKodeLoc values(1, 'no_NO',    'Kodebeskrivelse for B1 bokmål');
insert into BKodeLoc values(1, 'no_NO_NY', 'Kodebeskrivelse for B1 nynorsk');
insert into BKodeLoc values(2, 'no_NO',    'Kodebeskrivelse for B2 bokmål');
insert into BKodeLoc values(2, 'no_NO_NY', 'Kodebeskrivelse for B2 nynorsk');

insert into CKode values(1, 'C1A', 'C1DbKode');
insert into CKode values(2, 'C1B', 'C1DbKode');
insert into CKode values(10, 'C2A', 'C2DbKode');
insert into CKode values(11, 'C2B', 'C2DbKode');
insert into CKodeLoc values(1, 'no_NO',    'Kodebeskrivelse for C1A bokmål');
insert into CKodeLoc values(1, 'no_NO_NY', 'Kodebeskrivelse for C1A nynorsk');
insert into CKodeLoc values(2, 'no_NO',    'Kodebeskrivelse for C1B bokmål');
insert into CKodeLoc values(2, 'no_NO_NY', 'Kodebeskrivelse for C1B nynorsk');
insert into CKodeLoc values(10, 'no_NO',    'Kodebeskrivelse for C2A bokmål');
insert into CKodeLoc values(10, 'no_NO_NY', 'Kodebeskrivelse for C2A nynorsk');
insert into CKodeLoc values(11, 'no_NO',    'Kodebeskrivelse for C2B bokmål');
insert into CKodeLoc values(11, 'no_NO_NY', 'Kodebeskrivelse for C2B nynorsk');

insert into XstrKode values('A', 'X1');
insert into XstrKode values('B', 'X2');
insert into XstrKodeLoc values('A', 'no_NO',    'Kodebeskrivelse for X1 bokmål');
insert into XstrKodeLoc values('A', 'no_NO_NY', 'Kodebeskrivelse for X1 nynorsk');
insert into XstrKodeLoc values('B', 'no_NO',    'Kodebeskrivelse for X2 bokmål');
insert into XstrKodeLoc values('B', 'no_NO_NY', 'Kodebeskrivelse for X2 nynorsk');

insert into YstrKode values('A', 'Y1');
insert into YstrKode values('B', 'Y2');
insert into YstrKodeLoc values('A', 'no_NO',    'Kodebeskrivelse for Y1 bokmål');
insert into YstrKodeLoc values('A', 'no_NO_NY', 'Kodebeskrivelse for Y1 nynorsk');
insert into YstrKodeLoc values('B', 'no_NO',    'Kodebeskrivelse for Y2 bokmål');
insert into YstrKodeLoc values('B', 'no_NO_NY', 'Kodebeskrivelse for Y2 nynorsk');

insert into Kodeliste values(10001, 'AKode', 'no.statkart.skif.storetest.domain.demo.koder.ADbKode');
insert into Kodeliste values(10002, 'BKode', 'no.statkart.skif.storetest.domain.demo.koder.BDbKode');
insert into Kodeliste values(10003, 'C1Kode', 'no.statkart.skif.storetest.domain.demo.koder.C1DbKode');
insert into Kodeliste values(10004, 'C2Kode', 'no.statkart.skif.storetest.domain.demo.koder.C2DbKode');
insert into Kodeliste values(10005, 'XStrDbKode', 'no.statkart.skif.storetest.domain.demo.koder.XStrDbKode');

insert into KodelisteLoc values(10001, 'no_NO',    'Kodelistebeskrivelse for AKode bokmål');
insert into KodelisteLoc values(10001, 'no_NO_NY', 'Kodelistebeskrivelse for AKode nynorsk');
insert into KodelisteLoc values(10002, 'no_NO',    'Kodelistebeskrivelse for BKode bokmål');
insert into KodelisteLoc values(10002, 'no_NO_NY', 'Kodelistebeskrivelse for BKode nynorsk');
insert into KodelisteLoc values(10003, 'no_NO',    'Kodelistebeskrivelse for C1Kode bokmål');
insert into KodelisteLoc values(10003, 'no_NO_NY', 'Kodelistebeskrivelse for C1Kode nynorsk');
insert into KodelisteLoc values(10004, 'no_NO',    'Kodelistebeskrivelse for C2Kode bokmål');
insert into KodelisteLoc values(10004, 'no_NO_NY', 'Kodelistebeskrivelse for C2Kode nynorsk');
insert into KodelisteLoc values(10005, 'no_NO',    'Kodelistebeskrivelse for XStrDbKode bokmål');
insert into KodelisteLoc values(10005, 'no_NO_NY', 'Kodelistebeskrivelse for XStrDbKode nynorsk');

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

insert into GEOMETRICELEMENT_H(id, point, polygon, TBEGIN, TEND, TVERSION) values (1, MDSYS.SDO_GEOMETRY(2001,null,MDSYS.SDO_POINT_TYPE(608000,6650000,null),null,null), MDSYS.SDO_GEOMETRY(2003,null,null,MDSYS.SDO_ELEM_INFO_ARRAY(1,1003,1),MDSYS.SDO_ORDINATE_ARRAY(607900, 6649900, 608100, 6649900, 608100, 6650100, 607900, 6650100, 607900, 6649900)), snapshot_time.to_t('2011-10-02 08:00:31.00'),snapshot_time.to_t('2011-10-02 08:03:31.00'),1);
insert into GEOMETRICELEMENT_H(id, point, polygon, TBEGIN, TEND, TVERSION) values (1, MDSYS.SDO_GEOMETRY(2001,null,MDSYS.SDO_POINT_TYPE(608010,6650010,null),null,null), MDSYS.SDO_GEOMETRY(2003,null,null,MDSYS.SDO_ELEM_INFO_ARRAY(1,1003,1),MDSYS.SDO_ORDINATE_ARRAY(607910, 6649910, 608110, 6649910, 608110, 6650110, 607910, 6650110, 607910, 6649910)), snapshot_time.to_t('2011-10-02 08:03:31.00'),snapshot_time.to_t('2011-10-02 08:05:31.00'),2);
insert into GEOMETRICELEMENT_H(id, point, polygon, TBEGIN, TEND, TVERSION) values (1, MDSYS.SDO_GEOMETRY(2001,null,MDSYS.SDO_POINT_TYPE(608020,6650020,null),null,null), MDSYS.SDO_GEOMETRY(2003,null,null,MDSYS.SDO_ELEM_INFO_ARRAY(1,1003,1),MDSYS.SDO_ORDINATE_ARRAY(607920, 6649920, 608100, 6649920, 608120, 6650120, 607920, 6650120, 607920, 6649920)), snapshot_time.to_t('2011-10-02 08:05:31.00'),snapshot_time.to_t('9999-01-01 00:00:00.00'),3);