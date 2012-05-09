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

-- Tinglysing

insert into MatrikkelenhetsnivaaKode values(1, 'G');
insert into MatrikkelenhetsnivaaKode values(2, 'F');
insert into MatrikkelenhetsnivaaKode values(3, 'F1');
insert into MatrikkelenhetsnivaaKode values(4, 'F2');
insert into MatrikkelenhetsnivaaKode values(5, 'F3');
insert into MatrikkelenhetsnivaaKode values(6, 'F4');
insert into MatrikkelenhetsnivaaKode values(7, 'F5');
insert into MatrikkelenhetsnivaaKode values(8, 'F6');
insert into MatrikkelenhetsnivaaKode values(9, 'F7');
insert into MatrikkelenhetsnivaaKode values(10, 'F8');
insert into MatrikkelenhetsnivaaKode values(11, 'F9');

insert into MatrikkelenhetsnivaaKodeLoc values(1, 'no_NO', 'Grunn',  'Grunn');
insert into MatrikkelenhetsnivaaKodeLoc values(2, 'no_NO', 'Feste',  'Feste');
insert into MatrikkelenhetsnivaaKodeLoc values(3, 'no_NO', 'Framfeste 1',  'Framfeste 1');
insert into MatrikkelenhetsnivaaKodeLoc values(4, 'no_NO', 'Framfeste 2',  'Framfeste 2');
insert into MatrikkelenhetsnivaaKodeLoc values(5, 'no_NO', 'Framfeste 3',  'Framfeste 3');
insert into MatrikkelenhetsnivaaKodeLoc values(6, 'no_NO', 'Framfeste 4',  'Framfeste 4');
insert into MatrikkelenhetsnivaaKodeLoc values(7, 'no_NO', 'Framfeste 5',  'Framfeste 5');
insert into MatrikkelenhetsnivaaKodeLoc values(8, 'no_NO', 'Framfeste 6',  'Framfeste 6');
insert into MatrikkelenhetsnivaaKodeLoc values(9, 'no_NO', 'Framfeste 7',  'Framfeste 7');
insert into MatrikkelenhetsnivaaKodeLoc values(10, 'no_NO', 'Framfeste 8',  'Framfeste 8');
insert into MatrikkelenhetsnivaaKodeLoc values(11, 'no_NO', 'Framfeste 9',  'Framfeste 9');


insert into RettstypeKode values (1, 'AF'); insert into RettstypeKodeLoc values (1,'no_NO','Arealoverføring','Arealoverføring');
insert into RettstypeKode values (2, 'DI'); insert into RettstypeKodeLoc values (2,'no_NO','Diverse påtegning','Diverse påtegning');
insert into RettstypeKode values (3, 'EI'); insert into RettstypeKodeLoc values (3,'no_NO','Eiendomsforvalter','Eiendomsforvalter');
insert into RettstypeKode values (4, 'EN'); insert into RettstypeKodeLoc values (4,'no_NO','Påtegning på andel','Påtegning på andel');
insert into RettstypeKode values (5, 'ET'); insert into RettstypeKodeLoc values (5,'no_NO','Tvangspåt. på ID','Tvangspåt. på ID');
insert into RettstypeKode values (6, 'FA'); insert into RettstypeKodeLoc values (6,'no_NO','Realsameie','Realsameie');
insert into RettstypeKode values (7, 'FB'); insert into RettstypeKodeLoc values (7,'no_NO','Overføring fra tidligere festenummer','Overføring fra tidligere festenummer');
insert into RettstypeKode values (8, 'FE'); insert into RettstypeKodeLoc values (8,'no_NO','Festekontrakt','Festekontrakt');
insert into RettstypeKode values (9, 'FL'); insert into RettstypeKodeLoc values (9,'no_NO','Fremleieavtale','Fremleieavtale');
insert into RettstypeKode values (10, 'FN'); insert into RettstypeKodeLoc values (10,'no_NO','Realsameie - endring','Realsameie - endring');
insert into RettstypeKode values (11, 'FO'); insert into RettstypeKodeLoc values (11,'no_NO','Forhøyelse obligasjon','Forhøyelse obligasjon');
insert into RettstypeKode values (12, 'FR'); insert into RettstypeKodeLoc values (12,'no_NO','Fradeling','Fradeling');
insert into RettstypeKode values (13, 'FS'); insert into RettstypeKodeLoc values (13,'no_NO','Opphør av realsameie','Opphør av realsameie');
insert into RettstypeKode values (14, 'HI'); insert into RettstypeKodeLoc values (14,'no_NO','Historisk matrikkelenhet','Historisk matrikkelenhet');
insert into RettstypeKode values (15, 'HJ'); insert into RettstypeKodeLoc values (15,'no_NO','Hjemmelsovergang','Hjemmelsovergang');
insert into RettstypeKode values (16, 'HL'); insert into RettstypeKodeLoc values (16,'no_NO','Heftelse i rettighet','Heftelse i rettighet');
insert into RettstypeKode values (17, 'ID'); insert into RettstypeKodeLoc values (17,'no_NO','ID endring','ID endring');
insert into RettstypeKode values (18, 'JS'); insert into RettstypeKodeLoc values (18,'no_NO','Jordsameie','Jordsameie');
insert into RettstypeKode values (19, 'KA'); insert into RettstypeKodeLoc values (19,'no_NO','Kartforretning','Kartforretning');
insert into RettstypeKode values (20, 'KL'); insert into RettstypeKodeLoc values (20,'no_NO','Registrering anke','Registrering anke');
insert into RettstypeKode values (21, 'NE'); insert into RettstypeKodeLoc values (21,'no_NO','Nedkvittering','Nedkvittering');
insert into RettstypeKode values (22, 'PR'); insert into RettstypeKodeLoc values (22,'no_NO','Prioritet for dokumentnummer','Prioritet for dokumentnummer');
insert into RettstypeKode values (23, 'PU'); insert into RettstypeKodeLoc values (23,'no_NO','Pantutvidelse','Pantutvidelse');
insert into RettstypeKode values (24, 'RE'); insert into RettstypeKodeLoc values (24,'no_NO','Rettighet','Rettighet');
insert into RettstypeKode values (25, 'SA'); insert into RettstypeKodeLoc values (25,'no_NO','Sammenslåing','Sammenslåing');
insert into RettstypeKode values (26, 'SB'); insert into RettstypeKodeLoc values (26,'no_NO','Endring av sameiebrøk','Endring av sameiebrøk');
insert into RettstypeKode values (27, 'SE'); insert into RettstypeKodeLoc values (27,'no_NO','Seksjonering','Seksjonering');
insert into RettstypeKode values (28, 'SF'); insert into RettstypeKodeLoc values (28,'no_NO','Fjerning av seksjoner','Fjerning av seksjoner');
insert into RettstypeKode values (29, 'SH'); insert into RettstypeKodeLoc values (29,'no_NO','Oppheving av seksjoner','Oppheving av seksjoner');
insert into RettstypeKode values (30, 'SI'); insert into RettstypeKodeLoc values (30,'no_NO','Sletting av eiendomsforvalter','Sletting av eiendomsforvalter');
insert into RettstypeKode values (31, 'SL'); insert into RettstypeKodeLoc values (31,'no_NO','Sletting','Sletting');
insert into RettstypeKode values (32, 'SO'); insert into RettstypeKodeLoc values (32,'no_NO','Oppdeling av seksjoner','Oppdeling av seksjoner');
insert into RettstypeKode values (33, 'SP'); insert into RettstypeKodeLoc values (33,'no_NO','Servitutt pengeheftelse','Servitutt pengeheftelse');
insert into RettstypeKode values (34, 'SR'); insert into RettstypeKodeLoc values (34,'no_NO','Servitutt','Servitutt');
insert into RettstypeKode values (35, 'SS'); insert into RettstypeKodeLoc values (35,'no_NO','Sammenslåing av seksjoner','Sammenslåing av seksjoner');
insert into RettstypeKode values (36, 'ST'); insert into RettstypeKodeLoc values (36,'no_NO','Tilleggsseksjonering','Tilleggsseksjonering');
insert into RettstypeKode values (37, 'TE'); insert into RettstypeKodeLoc values (37,'no_NO','Transport av rettighet','Transport av rettighet');
insert into RettstypeKode values (38, 'TF'); insert into RettstypeKodeLoc values (38,'no_NO','Transport av feste','Transport av feste');
insert into RettstypeKode values (39, 'TI'); insert into RettstypeKodeLoc values (39,'no_NO','Uregistrert grunn','Uregistrert grunn');
insert into RettstypeKode values (40, 'TL'); insert into RettstypeKodeLoc values (40,'no_NO','Tvangsforretning i rettighet','Tvangsforretning i rettighet');
insert into RettstypeKode values (41, 'TN'); insert into RettstypeKodeLoc values (41,'no_NO','Tinglysing på ny','Tinglysing på ny');
insert into RettstypeKode values (42, 'TP'); insert into RettstypeKodeLoc values (42,'no_NO','Transport av leie','Transport av leie');
insert into RettstypeKode values (43, 'TR'); insert into RettstypeKodeLoc values (43,'no_NO','Transport av panthaver','Transport av panthaver');
insert into RettstypeKode values (44, 'TV'); insert into RettstypeKodeLoc values (44,'no_NO','Tvangsforretning','Tvangsforretning');
insert into RettstypeKode values (45, 'OB'); insert into RettstypeKodeLoc values (45,'no_NO','Obligasjon','Obligasjon');
insert into RettstypeKode values (46, 'OM'); insert into RettstypeKodeLoc values (46,'no_NO','Ommatrikulering','Ommatrikulering');
insert into RettstypeKode values (47, 'PA'); insert into RettstypeKodeLoc values (47,'no_NO','Pantefrafall','Pantefrafall');
insert into RettstypeKode values (48, 'PB'); insert into RettstypeKodeLoc values (48,'no_NO','Prioritet ikke tinglyst dokument','Prioritet ikke tinglyst dokument');
insert into RettstypeKode values (49, 'PE'); insert into RettstypeKodeLoc values (49,'no_NO','Leieavtale','Leieavtale');
insert into RettstypeKode values (50, 'PI'); insert into RettstypeKodeLoc values (50,'no_NO','Pantefrafall ikke tinglyst matrikkelenhet','Pantefrafall ikke tinglyst matrikkelenhet');
insert into RettstypeKode values (51, 'VF'); insert into RettstypeKodeLoc values (51,'no_NO','Vilkår i feste','Vilkår i feste');
insert into RettstypeKode values (52, 'VL'); insert into RettstypeKodeLoc values (52,'no_NO','Nye vilkår i leie','Nye vilkår i leie');
insert into RettstypeKode values (53, 'VN'); insert into RettstypeKodeLoc values (53,'no_NO','Nye vilkår i festeavtale','Nye vilkår i festeavtale');
insert into RettstypeKode values (54, 'JE'); insert into RettstypeKodeLoc values (54,'no_NO','Endring av jordsameie','Endring av jordsameie');
insert into RettstypeKode values (55, 'JO'); insert into RettstypeKodeLoc values (55,'no_NO','Opphør av jordsameie','Opphør av jordsameie');

insert into RettstypeKodeLoc values (1,'no_NO_NY','Arealoverføring ','Arealoverføring ');
insert into RettstypeKodeLoc values (2,'no_NO_NY','Diverse påteikning','Diverse påteikning');
insert into RettstypeKodeLoc values (3,'no_NO_NY','Eigedomsforvaltar','Eigedomsforvaltar');
insert into RettstypeKodeLoc values (4,'no_NO_NY','Påteikning på andel','Påteikning på andel');
insert into RettstypeKodeLoc values (5,'no_NO_NY','Tvangspåteikn. på ID','Tvangspåteikn. på ID');
insert into RettstypeKodeLoc values (6,'no_NO_NY','Realsameige','Realsameige');
insert into RettstypeKodeLoc values (7,'no_NO_NY','Overføring frå tidlegare festenummer','Overføring frå tidlegare festenummer');
insert into RettstypeKodeLoc values (8,'no_NO_NY','Festekontrakt','Festekontrakt');
insert into RettstypeKodeLoc values (9,'no_NO_NY','Framleigeavtale','Framleigeavtale');
insert into RettstypeKodeLoc values (10,'no_NO_NY','Realsameige - endring','Realsameige - endring');
insert into RettstypeKodeLoc values (11,'no_NO_NY','Auke obligasjon','Auke obligasjon');
insert into RettstypeKodeLoc values (12,'no_NO_NY','Frådeling','Frådeling');
insert into RettstypeKodeLoc values (13,'no_NO_NY','Opphøyr av realsameige','Opphøyr av realsameige');
insert into RettstypeKodeLoc values (14,'no_NO_NY','Historisk matrikkeleining','Historisk matrikkeleining');
insert into RettstypeKodeLoc values (15,'no_NO_NY','Overgang av heimel','Overgang av heimel');
insert into RettstypeKodeLoc values (16,'no_NO_NY','Hefte i rett','Hefte i rett');
insert into RettstypeKodeLoc values (17,'no_NO_NY','ID endring','ID endring');
insert into RettstypeKodeLoc values (18,'no_NO_NY','Jordsameige','Jordsameige');
insert into RettstypeKodeLoc values (19,'no_NO_NY','Kartforretning','Kartforretning');
insert into RettstypeKodeLoc values (20,'no_NO_NY','Registrering anke','Registrering anke');
insert into RettstypeKodeLoc values (21,'no_NO_NY','Nedkvittering','Nedkvittering');
insert into RettstypeKodeLoc values (22,'no_NO_NY','Prioritet for dokumentnummer','Prioritet for dokumentnummer');
insert into RettstypeKodeLoc values (23,'no_NO_NY','Utviding av pant','Utviding av pant');
insert into RettstypeKodeLoc values (24,'no_NO_NY','Rett','Rett');
insert into RettstypeKodeLoc values (25,'no_NO_NY','Samanslåing','Samanslåing');
insert into RettstypeKodeLoc values (26,'no_NO_NY','Endring av sameigebrøk','Endring av sameigebrøk');
insert into RettstypeKodeLoc values (27,'no_NO_NY','Seksjonering','Seksjonering');
insert into RettstypeKodeLoc values (28,'no_NO_NY','Fjerning av seksjonar','Fjerning av seksjonar');
insert into RettstypeKodeLoc values (29,'no_NO_NY','Oppheving av seksjonar','Oppheving av seksjonar');
insert into RettstypeKodeLoc values (30,'no_NO_NY','Sletting av eigendomsforvaltar','Sletting av eigendomsforvaltar');
insert into RettstypeKodeLoc values (31,'no_NO_NY','Sletting','Sletting');
insert into RettstypeKodeLoc values (32,'no_NO_NY','Oppdeling av seksjonar','Oppdeling av seksjonar');
insert into RettstypeKodeLoc values (33,'no_NO_NY','Servitutt pengehefte','Servitutt pengehefte');
insert into RettstypeKodeLoc values (34,'no_NO_NY','Servitutt','Servitutt');
insert into RettstypeKodeLoc values (35,'no_NO_NY','Samanslåing av seksjonar','Samanslåing av seksjonar');
insert into RettstypeKodeLoc values (36,'no_NO_NY','Tilleggsseksjonering','Tilleggsseksjonering');
insert into RettstypeKodeLoc values (37,'no_NO_NY','Transport av rett','Transport av rett');
insert into RettstypeKodeLoc values (38,'no_NO_NY','Transport av feste','Transport av feste');
insert into RettstypeKodeLoc values (39,'no_NO_NY','Uregistrert grunn','Uregistrert grunn');
insert into RettstypeKodeLoc values (40,'no_NO_NY','Tvangsforretning i rett','Tvangsforretning i rett');
insert into RettstypeKodeLoc values (41,'no_NO_NY','Tinglysing på nytt','Tinglysing på nytt');
insert into RettstypeKodeLoc values (42,'no_NO_NY','Transport av leige','Transport av leige');
insert into RettstypeKodeLoc values (43,'no_NO_NY','Transport av panthavar','Transport av panthavar');
insert into RettstypeKodeLoc values (44,'no_NO_NY','Tvangsforretning','Tvangsforretning');
insert into RettstypeKodeLoc values (45,'no_NO_NY','Obligasjon','Obligasjon');
insert into RettstypeKodeLoc values (46,'no_NO_NY','Ommatrikulering','Ommatrikulering');
insert into RettstypeKodeLoc values (47,'no_NO_NY','Pantefråfall','Pantefråfall');
insert into RettstypeKodeLoc values (48,'no_NO_NY','Prioritet ikkje tinglyst dokument','Prioritet ikkje tinglyst dokument');
insert into RettstypeKodeLoc values (49,'no_NO_NY','Leigeavtale','Leigeavtale');
insert into RettstypeKodeLoc values (50,'no_NO_NY','Pantefråfall ikkje tinglyst matrikkeleining','Pantefråfall ikkje tinglyst matrikkeleining');
insert into RettstypeKodeLoc values (51,'no_NO_NY','Vilkår i feste','Vilkår i feste');
insert into RettstypeKodeLoc values (52,'no_NO_NY','Nye vilkår i leige','Nye vilkår i leige');
insert into RettstypeKodeLoc values (53,'no_NO_NY','Nye vilkår i festeavtale','Nye vilkår i festeavtale');
insert into RettstypeKodeLoc values (54,'no_NO_NY','Endring av jordsameige','Endring av jordsameige');
insert into RettstypeKodeLoc values (55,'no_NO_NY','Opphøyr av jordsameige','Opphøyr av jordsameige');


