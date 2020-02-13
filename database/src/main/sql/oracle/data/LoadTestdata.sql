insert into TestEntity values (1, 'Text 1');

insert into TestBubble values (1, 'Text 1');
insert into TestBubble values (2, 'Text 2');

insert into TestBubbleWithHistory_H (ID, oppdateringsdato, sluttdato, versjonId, nr, text) values (10, snapshot_time.to_t('2011-10-02 08:00:00.00'), snapshot_time.to_t('2011-10-02 08:01:00.00'), 1, 2200, 'KARTGATA');
insert into TestBubbleWithHistory_H (ID, oppdateringsdato, sluttdato, versjonId, nr, text) values (10, snapshot_time.to_t('2011-10-02 08:01:00.00'), snapshot_time.to_t('2011-10-02 08:02:00.00'), 2, 2200, 'KARTVEGEN');
insert into TestBubbleWithHistory_H (ID, oppdateringsdato, sluttdato, versjonId, nr, text) values (10, snapshot_time.to_t('2011-10-02 08:02:00.00'), snapshot_time.to_t('2011-10-02 08:03:00.00'), 3, 2200, 'KARTVEIEN');
insert into TestBubbleWithHistory_H (ID, oppdateringsdato, sluttdato, versjonId, nr, text) values (10, snapshot_time.to_t('2011-10-02 08:03:00.00'), snapshot_time.to_t('2011-10-02 08:04:00.00'), 4, 2200, 'KART-VEIEN');
insert into TestBubbleWithHistory_H (ID, oppdateringsdato, sluttdato, versjonId, nr, text) values (10, snapshot_time.to_t('2011-10-02 08:04:00.00'), snapshot_time.to_t('9999-01-01 00:00:00.00'), 5, 2200, 'KARTVEIEN');
insert into TestBubbleWithHistory_H (ID, oppdateringsdato, sluttdato, versjonId, nr, text) values (11, snapshot_time.to_t('2011-10-02 08:03:00.00'),snapshot_time.to_t('2011-10-02 08:04:00.00'), 1, 2201, 'GAMMEL-VEIEN');
insert into TestBubbleWithHistory_H (ID, oppdateringsdato, sluttdato, versjonId, nr, text) values (11, snapshot_time.to_t('2011-10-02 08:04:00.00'),snapshot_time.to_t('9999-01-01 00:00:00.00'), 2, 2201, 'GAMMELVEIEN');

insert into FilteredBubble values(1, 'Orginal 1',0,'Ufilterert');
insert into FilteredBubble values(2, 'Orginal 2',1,'Filterert');

insert into ParentBubble values (1, 'Parent 1');
insert into ParentBubble values (2, 'Parent 2');
insert into ParentBubble values (3, 'Parent 3');


insert into ChildBubble values (1, 'Child 1.1',null);
insert into ChildBubble values (2, 'Child 1.2',null);
insert into ChildBubble values (3, 'Child 1.3',null);
insert into ChildBubble values (4, 'Child 2.1',null);
insert into ChildBubble values (5, 'Child 2.2',1);

insert into ChildForParent (id, parentBubbleId, childBubbleId) values(1,1,1);
insert into ChildForParent (id, parentBubbleId, childBubbleId) values(2,1,2);
insert into ChildForParent (id, parentBubbleId, childBubbleId) values(3,1,3);
insert into ChildForParent (id, parentBubbleId, childBubbleId) values(4,2,4);
insert into ChildForParent (id, parentBubbleId, childBubbleId) values(5,2,5);

insert into AKode values(1, 'A1');
insert into AKode values(2, 'A2');
insert into AKodeLoc values(1, 'no_NO', 'navn', unistr('A1-navn bokm\00E5l'));
insert into AKodeLoc values(1, 'no_NO', 'beskrivelse', unistr('Kodebeskrivelse for A1 bokm\00E5l'));
insert into AKodeLoc values(1, 'no_NO_NY', 'navn', 'A1-navn nynorsk');
insert into AKodeLoc values(1, 'no_NO_NY', 'beskrivelse', 'Kodebeskrivelse for A1 nynorsk');
insert into AKodeLoc values(2, 'no_NO', 'navn', unistr('A2-navn bokm\00E5l'));
insert into AKodeLoc values(2, 'no_NO', 'beskrivelse', unistr('Kodebeskrivelse for A2 bokm\00E5l'));
insert into AKodeLoc values(2, 'no_NO_NY', 'navn', 'A2-navn nynorsk');
insert into AKodeLoc values(2, 'no_NO_NY', 'beskrivelse', 'Kodebeskrivelse for A2 nynorsk');

insert into BKode values(1, 'B1');
insert into BKode values(2, 'B2');
insert into BKodeLoc values(1, 'no_NO', 'navn', unistr('B1-navn bokm\00E5l'));
insert into BKodeLoc values(1, 'no_NO', 'beskrivelse', unistr('Kodebeskrivelse for B1 bokm\00E5l'));
insert into BKodeLoc values(1, 'no_NO_NY', 'navn', 'B1-navn nynorsk');
insert into BKodeLoc values(1, 'no_NO_NY', 'beskrivelse', 'Kodebeskrivelse for B1 nynorsk');
insert into BKodeLoc values(2, 'no_NO', 'navn', unistr('B2-navn bokm\00E5l'));
insert into BKodeLoc values(2, 'no_NO', 'beskrivelse', unistr('Kodebeskrivelse for B2 bokm\00E5l'));
insert into BKodeLoc values(2, 'no_NO_NY', 'navn', 'B2-navn nynorsk');
insert into BKodeLoc values(2, 'no_NO_NY', 'beskrivelse', 'Kodebeskrivelse for B2 nynorsk');

insert into CKode values(1, 'C1A',  'C1DbKode');
insert into CKode values(2, 'C1B',  'C1DbKode');
insert into CKode values(10, 'C2A', 'C2DbKode');
insert into CKode values(11, 'C2B', 'C2DbKode');
insert into CKodeLoc values(1, 'no_NO', 'navn', unistr('C1A-navn bokm\00E5l'));
insert into CKodeLoc values(1, 'no_NO', 'beskrivelse', unistr('Kodebeskrivelse for C1A bokm\00E5l'));
insert into CKodeLoc values(1, 'no_NO_NY', 'navn', 'C1A-navn nynorsk');
insert into CKodeLoc values(1, 'no_NO_NY', 'beskrivelse', 'Kodebeskrivelse for C1A nynorsk');
insert into CKodeLoc values(2, 'no_NO', 'navn', unistr('C1B-navn bokm\00E5l'));
insert into CKodeLoc values(2, 'no_NO', 'beskrivelse', unistr('Kodebeskrivelse for C1B bokm\00E5l'));
insert into CKodeLoc values(2, 'no_NO_NY', 'navn', 'C1B-navn nynorsk');
insert into CKodeLoc values(2, 'no_NO_NY', 'beskrivelse', 'Kodebeskrivelse for C1B nynorsk');
insert into CKodeLoc values(10, 'no_NO',    unistr('C2A-navn bokm\00E5l'),  unistr('Kodebeskrivelse for C2A bokm\00E5l'));
insert into CKodeLoc values(10, 'no_NO_NY', 'C2A-navn nynorsk', 'Kodebeskrivelse for C2A nynorsk');
insert into CKodeLoc values(11, 'no_NO',    unistr('C2B-navn bokm\00E5l'),  unistr('Kodebeskrivelse for C2B bokm\00E5l'));
insert into CKodeLoc values(11, 'no_NO_NY', 'C2B-navn nynorsk', 'Kodebeskrivelse for C2B nynorsk');

insert into XstrKode values('A', 'X1');
insert into XstrKode values('B', 'X2');
insert into XstrKodeLoc values('A', 'no_NO', 'navn', unistr('X1-navn bokm\00E5l'));
insert into XstrKodeLoc values('A', 'no_NO', 'beskrivelse', unistr('Kodebeskrivelse for X1 bokm\00E5l'));
insert into XstrKodeLoc values('A', 'no_NO_NY', 'navn', 'X1-navn nynorsk');
insert into XstrKodeLoc values('A', 'no_NO_NY', 'beskrivelse', 'Kodebeskrivelse for X1 nynorsk');
insert into XstrKodeLoc values('B', 'no_NO', 'navn', unistr('X2-navn bokm\00E5l'));
insert into XstrKodeLoc values('B', 'no_NO', 'beskrivelse', unistr('Kodebeskrivelse for X2 bokm\00E5l'));
insert into XstrKodeLoc values('B', 'no_NO_NY', 'navn', 'X2-navn nynorsk');
insert into XstrKodeLoc values('B', 'no_NO_NY', 'beskrivelse', 'Kodebeskrivelse for X2 nynorsk');

insert into Kodeliste values(10001, 'AKode', 'no.statkart.skif.storetest.domain.demo.koder.ADbKodeId');
insert into Kodeliste values(10002, 'BKode', 'no.statkart.skif.storetest.domain.demo.koder.BDbKodeId');
insert into Kodeliste values(10003, 'C1Kode', 'no.statkart.skif.storetest.domain.demo.koder.C1DbKodeId');
insert into Kodeliste values(10004, 'C2Kode', 'no.statkart.skif.storetest.domain.demo.koder.C2DbKodeId');
insert into Kodeliste values(10005, 'XStrDbKode', 'no.statkart.skif.storetest.domain.demo.koder.XStrDbKodeId');

--insert into Kodeliste values(10011, 'SimpleLocalizedDbKode', 'no.statkart.skif.storetest.domain.koder.SimpleLocalizedDbKodeId');

insert into KodelisteLoc values(10001, 'no_NO', 'navn', unistr('ADbKode-navn bokm\00E5l'));
insert into KodelisteLoc values(10001, 'no_NO', 'beskrivelse', unistr('Kodelistebeskrivelse for ADbKode bokm\00E5l'));
insert into KodelisteLoc values(10001, 'no_NO_NY', 'navn', 'ADbKode-navn nynorsk');
insert into KodelisteLoc values(10001, 'no_NO_NY', 'beskrivelse', 'Kodelistebeskrivelse for ADbKode nynorsk');
insert into KodelisteLoc values(10002, 'no_NO', 'navn', unistr('BDbKode-navn bokm\00E5l'));
insert into KodelisteLoc values(10002, 'no_NO', 'beskrivelse', unistr('Kodelistebeskrivelse for BDbKode bokm\00E5l'));
insert into KodelisteLoc values(10002, 'no_NO_NY', 'navn', 'BDbKode-navn nynorsk');
insert into KodelisteLoc values(10002, 'no_NO_NY', 'beskrivelse', 'Kodelistebeskrivelse for BDbKode nynorsk');
insert into KodelisteLoc values(10003, 'no_NO', 'navn', unistr('C1DbKode-navn bokm\00E5l'));
insert into KodelisteLoc values(10003, 'no_NO', 'beskrivelse', unistr('Kodelistebeskrivelse for C1DbKode bokm\00E5l'));
insert into KodelisteLoc values(10003, 'no_NO_NY', 'navn', 'C1DbKode-navn nynorsk');
insert into KodelisteLoc values(10003, 'no_NO_NY', 'beskrivelse', 'Kodelistebeskrivelse for C1DbKode nynorsk');
insert into KodelisteLoc values(10004, 'no_NO', 'navn', unistr('C2DbKode-navn bokm\00E5l'));
insert into KodelisteLoc values(10004, 'no_NO', 'beskrivelse', unistr('Kodelistebeskrivelse for C2DbKode bokm\00E5l'));
insert into KodelisteLoc values(10004, 'no_NO_NY', 'navn', 'C2DbKode-navn nynorsk');
insert into KodelisteLoc values(10004, 'no_NO_NY', 'beskrivelse', 'Kodelistebeskrivelse for C2DbKode nynorsk');
insert into KodelisteLoc values(10005, 'no_NO', 'navn', unistr('XStrDbKode-navn bokm\00E5l'));
insert into KodelisteLoc values(10005, 'no_NO', 'beskrivelse', unistr('Kodelistebeskrivelse for XStrDbKode bokm\00E5l'));
insert into KodelisteLoc values(10005, 'no_NO_NY', 'navn', 'XStrDbKode-navn nynorsk');
insert into KodelisteLoc values(10005, 'no_NO_NY', 'beskrivelse', 'Kodelistebeskrivelse for XStrDbKode nynorsk');


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
