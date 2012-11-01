insert into TestEntity values (1, 'Text 1');
insert into TestBubble values (1, 'Text 1');
insert into TestBubble values (2, 'Text 2');



insert into ParrentBubble values (1, 'Parrent 1');
insert into ParrentBubble values (2, 'Parrent 2');
insert into ParrentBubble values (3, 'Parrent 3');

insert into ChildBubble values (1, 'Child 1.1',null);
insert into ChildBubble values (2, 'Child 1.2',null);
insert into ChildBubble values (3, 'Child 1.3',null);
insert into ChildBubble values (4, 'Child 2.1',null);
insert into ChildBubble values (5, 'Child 2.2',1);

insert into ChildForParrent values(1,1,1);
insert into ChildForParrent values(2,1,2);
insert into ChildForParrent values(3,1,3);
insert into ChildForParrent values(4,2,1);
insert into ChildForParrent values(5,2,2);






insert into FilteredBubble values(1, 'Orginal 1',0,'Ufilterert');
insert into FilteredBubble values(2, 'Orginal 2',1,'Filterert');



insert into TableSequence (tableName, nextFreeNumber) values ('GLOBAL_SEQUENCE',999999);
insert into TableSequence (tableName, nextFreeNumber) values ('TEST_NUMBER',1);

insert into AKode values(1, 'A1');
insert into AKode values(2, 'A2');
insert into AKodeLoc values(1, 'no_NO',    'A1-navn bokmål',  'Kodebeskrivelse for A1 bokmål');
insert into AKodeLoc values(1, 'no_NO_NY', 'A1-navn nynorsk', 'Kodebeskrivelse for A1 nynorsk');
insert into AKodeLoc values(2, 'no_NO',    'A2-navn bokmål',  'Kodebeskrivelse for A2 bokmål');
insert into AKodeLoc values(2, 'no_NO_NY', 'A2-navn nynorsk', 'Kodebeskrivelse for A2 nynorsk');

insert into BKode values(1, 'B1');
insert into BKode values(2, 'B2');
insert into BKodeLoc values(1, 'no_NO',    'B1-navn bokmål',  'Kodebeskrivelse for B1 bokmål');
insert into BKodeLoc values(1, 'no_NO_NY', 'B1-navn nynorsk', 'Kodebeskrivelse for B1 nynorsk');
insert into BKodeLoc values(2, 'no_NO',    'B2-navn bokmål',  'Kodebeskrivelse for B2 bokmål');
insert into BKodeLoc values(2, 'no_NO_NY', 'B2-navn nynorsk', 'Kodebeskrivelse for B2 nynorsk');

insert into CKode values(1, 'C1A',  'C1DbKode');
insert into CKode values(2, 'C1B',  'C1DbKode');
insert into CKode values(10, 'C2A', 'C2DbKode');
insert into CKode values(11, 'C2B', 'C2DbKode');
insert into CKodeLoc values(1, 'no_NO',     'C1A-navn bokmål',  'Kodebeskrivelse for C1A bokmål');
insert into CKodeLoc values(1, 'no_NO_NY',  'C1A-navn nynorsk', 'Kodebeskrivelse for C1A nynorsk');
insert into CKodeLoc values(2, 'no_NO',     'C1B-navn bokmål',  'Kodebeskrivelse for C1B bokmål');
insert into CKodeLoc values(2, 'no_NO_NY',  'C1B-navn nynorsk', 'Kodebeskrivelse for C1B nynorsk');
insert into CKodeLoc values(10, 'no_NO',    'C2A-navn bokmål',  'Kodebeskrivelse for C2A bokmål');
insert into CKodeLoc values(10, 'no_NO_NY', 'C2A-navn nynorsk', 'Kodebeskrivelse for C2A nynorsk');
insert into CKodeLoc values(11, 'no_NO',    'C2B-navn bokmål',  'Kodebeskrivelse for C2B bokmål');
insert into CKodeLoc values(11, 'no_NO_NY', 'C2B-navn nynorsk', 'Kodebeskrivelse for C2B nynorsk');

insert into XstrKode values('A', 'X1');
insert into XstrKode values('B', 'X2');
insert into XstrKodeLoc values('A', 'no_NO',    'X1-navn bokmål',  'Kodebeskrivelse for X1 bokmål');
insert into XstrKodeLoc values('A', 'no_NO_NY', 'X1-navn nynorsk', 'Kodebeskrivelse for X1 nynorsk');
insert into XstrKodeLoc values('B', 'no_NO',    'X2-navn bokmål',  'Kodebeskrivelse for X2 bokmål');
insert into XstrKodeLoc values('B', 'no_NO_NY', 'X2-navn nynorsk', 'Kodebeskrivelse for X2 nynorsk');

insert into YstrKode values('A', 'Y1');
insert into YstrKode values('B', 'Y2');
insert into YstrKodeLoc values('A', 'no_NO',    'Y1-navn bokmål',  'Kodebeskrivelse for Y1 bokmål');
insert into YstrKodeLoc values('A', 'no_NO_NY', 'Y1-navn nynorsk', 'Kodebeskrivelse for Y1 nynorsk');
insert into YstrKodeLoc values('B', 'no_NO',    'Y2-navn bokmål',  'Kodebeskrivelse for Y2 bokmål');
insert into YstrKodeLoc values('B', 'no_NO_NY', 'Y2-navn nynorsk', 'Kodebeskrivelse for Y2 nynorsk');

insert into Kodeliste values(10001, 'AKode', 'no.statkart.skif.storetest.domain.demo.koder.ADbKodeId');
insert into Kodeliste values(10002, 'BKode', 'no.statkart.skif.storetest.domain.demo.koder.BDbKodeId');
insert into Kodeliste values(10003, 'C1Kode', 'no.statkart.skif.storetest.domain.demo.koder.C1DbKodeId');
insert into Kodeliste values(10004, 'C2Kode', 'no.statkart.skif.storetest.domain.demo.koder.C2DbKodeId');
insert into Kodeliste values(10005, 'XStrDbKode', 'no.statkart.skif.storetest.domain.demo.koder.XStrDbKodeId');

insert into KodelisteLoc values(10001, 'no_NO',    'ADbKode-navn bokmål', 'Kodelistebeskrivelse for ADbKode bokmål');
insert into KodelisteLoc values(10001, 'no_NO_NY', 'ADbKode-navn nynorsk', 'Kodelistebeskrivelse for ADbKode nynorsk');
insert into KodelisteLoc values(10002, 'no_NO',    'BDbKode-navn bokmål', 'Kodelistebeskrivelse for BDbKode bokmål');
insert into KodelisteLoc values(10002, 'no_NO_NY', 'BDbKode-navn nynorsk', 'Kodelistebeskrivelse for BDbKode nynorsk');
insert into KodelisteLoc values(10003, 'no_NO',    'C1DbKode-navn bokmål', 'Kodelistebeskrivelse for C1DbKode bokmål');
insert into KodelisteLoc values(10003, 'no_NO_NY', 'C1DbKode-navn nynorsk', 'Kodelistebeskrivelse for C1DbKode nynorsk');
insert into KodelisteLoc values(10004, 'no_NO',    'C2DbKode-navn bokmål', 'Kodelistebeskrivelse for C2DbKode bokmål');
insert into KodelisteLoc values(10004, 'no_NO_NY', 'C2DbKode-navn nynorsk', 'Kodelistebeskrivelse for C2DbKode nynorsk');
insert into KodelisteLoc values(10005, 'no_NO',    'XStrDbKode-navn bokmål', 'Kodelistebeskrivelse for XStrDbKode bokmål');
insert into KodelisteLoc values(10005, 'no_NO_NY', 'XStrDbKode-navn nynorsk', 'Kodelistebeskrivelse for XStrDbKode nynorsk');


insert into FOO_H (ID,oppdateringsdato,sluttdato,versjonId,NR,NAVN) values (100,snapshot_time.to_t('2011-10-02 08:00:00.00'),snapshot_time.to_t('2011-10-02 08:01:00.00'),1,2200,'KARTGATA');
insert into FOO_H (ID,oppdateringsdato,sluttdato,versjonId,NR,NAVN) values (100,snapshot_time.to_t('2011-10-02 08:01:00.00'),snapshot_time.to_t('2011-10-02 08:02:00.00'),2,2200,'KARTVEGEN');
insert into FOO_H (ID,oppdateringsdato,sluttdato,versjonId,NR,NAVN) values (100,snapshot_time.to_t('2011-10-02 08:02:00.00'),snapshot_time.to_t('2011-10-02 08:03:00.00'),3,2200,'KARTVEIEN');
insert into FOO_H (ID,oppdateringsdato,sluttdato,versjonId,NR,NAVN) values (100,snapshot_time.to_t('2011-10-02 08:03:00.00'),snapshot_time.to_t('2011-10-02 08:04:00.00'),4,2200,'KART-VEIEN');
insert into FOO_H (ID,oppdateringsdato,sluttdato,versjonId,NR,NAVN) values (100,snapshot_time.to_t('2011-10-02 08:04:00.00'),snapshot_time.to_t('9999-01-01 00:00:00.00'),5,2200,'KARTVEIEN');

insert into FOO_H (ID,oppdateringsdato,sluttdato,versjonId,NR,NAVN) values (101,snapshot_time.to_t('2011-10-02 08:03:00.00'),snapshot_time.to_t('2011-10-02 08:04:00.00'),1,2201,'GAMMEL-VEIEN');
insert into FOO_H (ID,oppdateringsdato,sluttdato,versjonId,NR,NAVN) values (101,snapshot_time.to_t('2011-10-02 08:04:00.00'),snapshot_time.to_t('9999-01-01 00:00:00.00'),2,2201,'GAMMELVEIEN');

insert into BAZ (ID, TEXT, FOOID, TESTAENUMKODEID, TESTC2DBKODEID) values (501, 'Baz 1', 100, 0, 10);
insert into BAZ (ID, TEXT, FOOID, TESTAENUMKODEID, TESTC2DBKODEID) values (502, 'Baz 2', 100, 1, 11);
insert into BAZ (ID, TEXT, FOOID, TESTAENUMKODEID, TESTC2DBKODEID) values (503, 'Baz 3', 101, 2, 10);

insert into RAZ (ID, TEXT,COMPTEXT, FOOID, RAZENTITYCOMPONENTID) values (601, 'Raz 1','CompRaz 1', 100, 610 );
insert into RAZ (ID, TEXT,COMPTEXT, FOOID, RAZENTITYCOMPONENTID) values (602, 'Raz 2','CompRaz 2', 100, NULL);
insert into RAZ (ID, TEXT,COMPTEXT, FOOID, RAZENTITYCOMPONENTID) values (603, 'Raz 3','CompRaz 3', 101, NULL);

insert into RAZENTITYCOMPONENT (ID, COMPONENTNAME) values (610, 'Raz 1');


insert into BAR_H (ID,oppdateringsdato,sluttdato,versjonId,HUSNR,BOKSTAV,FOOID, BAZID) values (1001,snapshot_time.to_t('2011-10-02 08:00:30.00'),snapshot_time.to_t('2011-10-02 08:03:30.00'),1,105,NULL,100, 501);
insert into BAR_H (ID,oppdateringsdato,sluttdato,versjonId,HUSNR,BOKSTAV,FOOID, BAZID) values (1001,snapshot_time.to_t('2011-10-02 08:03:30.00'),snapshot_time.to_t('9999-01-01 00:00:00.00'),2,106,NULL,100, 502);
insert into BAR_H (ID,oppdateringsdato,sluttdato,versjonId,HUSNR,BOKSTAV,FOOID, BAZID) values (1002,snapshot_time.to_t('2011-10-02 08:00:30.00'),snapshot_time.to_t('2011-10-02 08:03:30.00'),1,205,NULL,100, 503);
insert into BAR_H (ID,oppdateringsdato,sluttdato,versjonId,HUSNR,BOKSTAV,FOOID, BAZID) values (1002,snapshot_time.to_t('2011-10-02 08:03:30.00'),snapshot_time.to_t('9999-01-01 00:00:00.00'),2,206,NULL,100, 503);


insert into BARFOOS_H (ID,oppdateringsdato,sluttdato,versjonId,TEXT,BARID) values (2001,snapshot_time.to_t('2011-10-02 08:00:30.00'),snapshot_time.to_t('2011-10-02 08:03:30.00'),1,'Text 1',1001);
insert into BARFOOS_H (ID,oppdateringsdato,sluttdato,versjonId,TEXT,BARID) values (2001,snapshot_time.to_t('2011-10-02 08:03:30.00'),snapshot_time.to_t('9999-01-01 00:00:00.00'),2,'Text 2',1001);
insert into BARFOOS_H (ID,oppdateringsdato,sluttdato,versjonId,TEXT,BARID) values (2002,snapshot_time.to_t('2011-10-02 08:00:30.00'),snapshot_time.to_t('2011-10-02 08:03:30.00'),1,'Text 1',1002);
insert into BARFOOS_H (ID,oppdateringsdato,sluttdato,versjonId,TEXT,BARID) values (2002,snapshot_time.to_t('2011-10-02 08:03:30.00'),snapshot_time.to_t('9999-01-01 00:00:00.00'),2,'Text 2',1002);


insert into FooForBarFoos_H (BarFoosId, fooId, oppdateringsdato,sluttdato) values (2001, 100, snapshot_time.to_t('2011-10-02 08:03:30.00'),snapshot_time.to_t('9999-01-01 00:00:00.00'));
insert into FooForBarFoos_H (BarFoosId, fooId, oppdateringsdato,sluttdato) values (2001, 101, snapshot_time.to_t('2011-10-02 08:03:30.00'),snapshot_time.to_t('9999-01-01 00:00:00.00'));

insert into GEOMETRICELEMENT_H(id, point, polygon, oppdateringsdato, sluttdato, versjonId) values (1, MDSYS.SDO_GEOMETRY(2001,null,MDSYS.SDO_POINT_TYPE(608000,6650000,null),null,null), MDSYS.SDO_GEOMETRY(2003,null,null,MDSYS.SDO_ELEM_INFO_ARRAY(1,1003,1),MDSYS.SDO_ORDINATE_ARRAY(607900, 6649900, 608100, 6649900, 608100, 6650100, 607900, 6650100, 607900, 6649900)), snapshot_time.to_t('2011-10-02 08:00:31.00'),snapshot_time.to_t('2011-10-02 08:03:31.00'),1);
insert into GEOMETRICELEMENT_H(id, point, polygon, oppdateringsdato, sluttdato, versjonId) values (1, MDSYS.SDO_GEOMETRY(2001,null,MDSYS.SDO_POINT_TYPE(608010,6650010,null),null,null), MDSYS.SDO_GEOMETRY(2003,null,null,MDSYS.SDO_ELEM_INFO_ARRAY(1,1003,1),MDSYS.SDO_ORDINATE_ARRAY(607910, 6649910, 608110, 6649910, 608110, 6650110, 607910, 6650110, 607910, 6649910)), snapshot_time.to_t('2011-10-02 08:03:31.00'),snapshot_time.to_t('2011-10-02 08:05:31.00'),2);
insert into GEOMETRICELEMENT_H(id, point, polygon, oppdateringsdato, sluttdato, versjonId) values (1, MDSYS.SDO_GEOMETRY(2001,null,MDSYS.SDO_POINT_TYPE(608020,6650020,null),null,null), MDSYS.SDO_GEOMETRY(2003,null,null,MDSYS.SDO_ELEM_INFO_ARRAY(1,1003,1),MDSYS.SDO_ORDINATE_ARRAY(607920, 6649920, 608100, 6649920, 608120, 6650120, 607920, 6650120, 607920, 6649920)), snapshot_time.to_t('2011-10-02 08:05:31.00'),snapshot_time.to_t('9999-01-01 00:00:00.00'),3);


-- Multikobling

insert into Person(id) values (1000);
insert into Person(id) values (1001);
insert into Person(id) values (1002);
insert into Person(id) values (1003);
insert into Person(id) values (1004);
insert into Person(id) values (1005);

insert into Rettsstiftelse(id, class) values (2001, 'Servitutt');
insert into Rettsstiftelse(id, class) values (2000, 'Servitutt');
insert into Rettsstiftelse(id, class) values (2002, 'Servitutt');
insert into Rettsstiftelse(id, class) values (2003, 'Servitutt');
insert into Rettsstiftelse(id, class) values (2004, 'Servitutt');
insert into Rettsstiftelse(id, class) values (2005, 'Servitutt');

insert into Rettsstiftelse(id, class) values (2101, 'Pengeheftelse');
insert into Rettsstiftelse(id, class) values (2100, 'Pengeheftelse');
insert into Rettsstiftelse(id, class) values (2102, 'Pengeheftelse');
insert into Rettsstiftelse(id, class) values (2103, 'Pengeheftelse');
insert into Rettsstiftelse(id, class) values (2104, 'Pengeheftelse');
insert into Rettsstiftelse(id, class) values (2105, 'Pengeheftelse');

insert into Rettsstiftelse_Person_Kobling(rettsstiftelseId, rolle, personId) values (2001, 'RETTIGHETSHAVER_AKTIV',1001 );

insert into BubbleWithList values (2201, 'text', null, null, null);
insert into BubbleWithList values (2204, 'text2', null, null, null);

insert into BwlForBwl values (2204, 2201, null, null, null);

insert into BubbleWithListComponent values (2202, 'component for 2201', 2201, 1, null, null, null);

insert into BubbleWithListComponent2 values (2203, 'component for 2201', 2201, null, null, null);
