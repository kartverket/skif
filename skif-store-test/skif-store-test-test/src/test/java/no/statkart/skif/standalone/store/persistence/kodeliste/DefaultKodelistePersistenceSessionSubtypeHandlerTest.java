package no.statkart.skif.standalone.store.persistence.kodeliste;

import no.statkart.skif.standalone.util.testsupport.StandAloneTestHelper;
import no.statkart.skif.store.BubbleModelConfiguration;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.kodeliste.KodeId;
import no.statkart.skif.store.kodeliste.KodelisteId;
import no.statkart.skif.store.persistence.DefaultPersistenceSessionManager;
import no.statkart.skif.store.persistence.DefaultPersistenceSessionStrategy;
import no.statkart.skif.store.persistence.PersistenceSessionManager;
import no.statkart.skif.store.persistence.hibernate.*;
import no.statkart.skif.store.persistence.kodeliste.DefaultKodelistePersistenceSessionSubtypeHandler;
import no.statkart.skif.store.persistence.kodeliste.EnumKodelisteManager;
import no.statkart.skif.store.persistence.kodeliste.KodelistePersistenceSessionSubtypeHandler;
import no.statkart.skif.storetest.domain.demo.koder.*;
import no.statkart.skif.storetest.domain.kodeliste.StoreTestKodelisteLong;
import no.statkart.skif.storetest.domain.kodeliste.StoreTestKodelisteString;
import no.statkart.skif.storetest.domain.koder.HistoriskDbKode;
import no.statkart.skif.storetest.domain.koder.SimpleLocalizedDbKode;
import no.statkart.skif.storetest.domain.koder.SimpleLocalizedDbKodeId;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.*;

import static no.statkart.skif.standalone.util.testsupport.StandAloneTestHelper.createHibernateSessionFactorManagerBundle;
import static no.statkart.skif.standalone.util.testsupport.StandAloneTestHelper.createHibernateSessionFactoryBuilderWithHistory;
import static org.assertj.core.api.Assertions.*;
import static org.testng.Assert.assertEquals;

/**
 * @author Henrik Fredholm
 * @author Tor Egil R. Strand
 * @since 2.1
 */
@Test(groups = "singlevm-required")
public class DefaultKodelistePersistenceSessionSubtypeHandlerTest {
    private final Locale norsk = new Locale("no", "NO");
    private final Locale norskNynorsk = new Locale("no", "NO", "NY");

    Properties hibernateProperties;

    HibernateSessionFactoryManagerBundle sessionFactoryManagerBundle;

    public DefaultKodelistePersistenceSessionSubtypeHandlerTest() {
        hibernateProperties = StandAloneTestHelper.createHibernatePropertiesSingleVm();
    }

    @BeforeClass
    public void setUp() {
        BubbleModelConfiguration bubbleClasses = new BubbleModelConfiguration();
        bubbleClasses.addBubble(ADbKode.class);
        bubbleClasses.addBubble(BDbKode.class);
        bubbleClasses.addBubbleWithSubclasses(CDbKode.class, C1DbKode.class, C2DbKode.class);
        bubbleClasses.addBubble(XStrDbKode.class);
        bubbleClasses.addBubbleWithSubclasses(HistoriskDbKode.class, SimpleLocalizedDbKode.class);
        bubbleClasses.addBubble(StoreTestKodelisteLong.class);

        HibernateSessionFactoryBuilder sessionFactoryBuilder = createHibernateSessionFactoryBuilderWithHistory();
        sessionFactoryBuilder.addBubbleModel(bubbleClasses);

        sessionFactoryManagerBundle = createHibernateSessionFactorManagerBundle(sessionFactoryBuilder, hibernateProperties);
    }

    @AfterClass
    void tearDown() {
        sessionFactoryManagerBundle.close();
    }

    private PersistenceSessionManager createPersistenceSessionManager() {
        EnumKodelisteManager enumKodelisteManager = new EnumKodelisteManager();
        enumKodelisteManager.installStatic(AEnumKodeId.class);
        enumKodelisteManager.installStatic(BEnumKodeId.class);
        enumKodelisteManager.installStatic(SEnumKodeId.class);

        enumKodelisteManager.installDynamic(SimpleLocalizedDbKodeId.class);

        HibernatePersistenceSessionMasterImpl masterCurrent = new HibernatePersistenceSessionMasterImpl(
                sessionFactoryManagerBundle.getBundle().get(0)
        );
        HibernatePersistenceSessionMasterImpl masterOld = new HibernatePersistenceSessionMasterImpl(
                sessionFactoryManagerBundle.getBundle().get(1)
        );

        return new DefaultPersistenceSessionManager(
                new DefaultPersistenceSessionStrategy(
                        masterCurrent,
                        new DefaultKodelistePersistenceSessionSubtypeHandler(masterCurrent, enumKodelisteManager)
                ),
                new DefaultPersistenceSessionStrategy(
                        masterOld,
                        new DefaultKodelistePersistenceSessionSubtypeHandler(masterOld, enumKodelisteManager)
                )
        );
    }

    public void testGetAEnumKode_NO() {
        PersistenceSessionManager persistenceSessionManager = createPersistenceSessionManager();

        try {
            AEnumKode aEnumKodeA = persistenceSessionManager.get(AEnumKodeId.KodeAId);
            assertEquals(aEnumKodeA.getId(), AEnumKodeId.KodeAId);
            assertEquals(aEnumKodeA.getKodelisteId(), AEnumKodeId.KODELISTE_ID);
            assertEquals(aEnumKodeA.getBeskrivelse().getText(norsk), "Kode A er den første av AKodene");

            AEnumKode aEnumKodeB = persistenceSessionManager.get(AEnumKodeId.KodeBId);
            assertEquals(aEnumKodeB.getId(), AEnumKodeId.KodeBId);
            assertEquals(aEnumKodeB.getKodelisteId(), AEnumKodeId.KODELISTE_ID);
            assertEquals(aEnumKodeB.getBeskrivelse().getText(norsk), "Kode B er den andre av AKodene");
        } finally {
            persistenceSessionManager.close();
        }
    }

    public void testGetBEnumKode_NO() {
        PersistenceSessionManager persistenceSessionManager = createPersistenceSessionManager();

        try {
            BEnumKode bEnumKodeA = persistenceSessionManager.get(BEnumKodeId.KodeAId);
            assertEquals(bEnumKodeA.getId(), BEnumKodeId.KodeAId);
            assertEquals(bEnumKodeA.getKodelisteId(), BEnumKodeId.KODELISTE_ID);
            assertEquals(bEnumKodeA.getBeskrivelse().getText(norsk), "Kode A er den første av BKodene");

            BEnumKode bEnumKodeB = persistenceSessionManager.get(BEnumKodeId.KodeBId);
            assertEquals(bEnumKodeB.getId(), BEnumKodeId.KodeBId);
            assertEquals(bEnumKodeB.getKodelisteId(), BEnumKodeId.KODELISTE_ID);
            assertEquals(bEnumKodeB.getBeskrivelse().getText(norsk), "Kode B er den andre av BKodene");
        } finally {
            persistenceSessionManager.close();
        }
    }

    public void testGetSEnumKode_NO() {
        PersistenceSessionManager persistenceSessionManager = createPersistenceSessionManager();

        try {
            SEnumKode sEnumKodeA = persistenceSessionManager.get(SEnumKodeId.KodeAId);
            assertEquals(sEnumKodeA.getId(), SEnumKodeId.KodeAId);
            assertEquals(sEnumKodeA.getKodelisteId(), SEnumKodeId.KODELISTE_ID);
            assertEquals(sEnumKodeA.getBeskrivelse().getText(norsk), "Kode A er den første av SKodene");

            SEnumKode sEnumKodeB = persistenceSessionManager.get(SEnumKodeId.KodeBId);
            assertEquals(sEnumKodeB.getId(), SEnumKodeId.KodeBId);
            assertEquals(sEnumKodeB.getKodelisteId(), SEnumKodeId.KODELISTE_ID);
            assertEquals(sEnumKodeB.getBeskrivelse().getText(norsk), "Kode B er den andre av SKodene");
        } finally {
            persistenceSessionManager.close();
        }
    }

    public void testGetEnumKode_NO_Old() {
        PersistenceSessionManager persistenceSessionManager = createPersistenceSessionManager();

        try {
            AEnumKode aEnumKodeA = persistenceSessionManager.get((AEnumKodeId) AEnumKodeId.KodeAId.asSnapshotVersionOld());
            assertEquals(aEnumKodeA.getId(), AEnumKodeId.KodeAId.asSnapshotVersionOld());
            assertEquals(aEnumKodeA.getKodelisteId(), AEnumKodeId.KODELISTE_ID.asSnapshotVersionOld());
            assertEquals(aEnumKodeA.getBeskrivelse().getText(norsk), "Kode A er den første av AKodene");
        } finally {
            persistenceSessionManager.close();
        }
    }

    public void testGetEnumKode_NO_NY() {
        PersistenceSessionManager persistenceSessionManager = createPersistenceSessionManager();

        try {
            AEnumKode aEnumKodeA = persistenceSessionManager.get(AEnumKodeId.KodeAId);
            assertEquals(aEnumKodeA.getBeskrivelse().getText(norskNynorsk), "Kode A er den fyrste av AKodane");

            AEnumKode aEnumKodeB = persistenceSessionManager.get(AEnumKodeId.KodeBId);
            assertEquals(aEnumKodeB.getBeskrivelse().getText(norskNynorsk), "Kode B er den andre av AKodane");
        } finally {
            persistenceSessionManager.close();
        }
    }

    public void testGetKodelisteForEnum_NO() {
        PersistenceSessionManager persistenceSessionManager = createPersistenceSessionManager();

        try {
            StoreTestKodelisteLong kodelisteForAEnumKode = persistenceSessionManager.get(AEnumKodeId.KODELISTE_ID);
            assertEquals(kodelisteForAEnumKode.getKodeIdClass(), AEnumKodeId.class);
            assertEquals(kodelisteForAEnumKode.getId(), AEnumKodeId.KODELISTE_ID);
            assertEquals(kodelisteForAEnumKode.getBeskrivelse().getText(norsk), "Kodeliste for AEnumKode");

            StoreTestKodelisteLong kodelisteForBEnumKode = persistenceSessionManager.get(BEnumKodeId.KODELISTE_ID);
            assertEquals(kodelisteForBEnumKode.getKodeIdClass(), BEnumKodeId.class);
            assertEquals(kodelisteForBEnumKode.getId(), BEnumKodeId.KODELISTE_ID);
            assertEquals(kodelisteForBEnumKode.getBeskrivelse().getText(norsk), "Kodeliste for BEnumKode");

            StoreTestKodelisteString kodelisteForSEnumKode = persistenceSessionManager.get(SEnumKodeId.KODELISTE_ID);
            assertEquals(kodelisteForSEnumKode.getKodeIdClass(), SEnumKodeId.class);
            assertEquals(kodelisteForSEnumKode.getId(), SEnumKodeId.KODELISTE_ID);
            assertEquals(kodelisteForSEnumKode.getBeskrivelse().getText(norsk), "Kodeliste for SEnumKode, som er String-basert");
        } finally {
            persistenceSessionManager.close();
        }
    }

    public void testGetKodelisteForEnum_NO_Old() {
        PersistenceSessionManager persistenceSessionManager = createPersistenceSessionManager();

        try {
            StoreTestKodelisteLong kodelisteForEnumKodeA = persistenceSessionManager.get(AEnumKodeId.KODELISTE_ID.asSnapshotVersionOld());
            assertEquals(kodelisteForEnumKodeA.getKodeIdClass(), AEnumKodeId.class);
            assertEquals(kodelisteForEnumKodeA.getId(), AEnumKodeId.KODELISTE_ID.asSnapshotVersionOld());
            assertThat(kodelisteForEnumKodeA.getKoderIds()).containsExactly(
                    (KodeId<?>)AEnumKodeId.IkkeOppgittId.asSnapshotVersionOld(),
                    (KodeId<?>)AEnumKodeId.KodeAId.asSnapshotVersionOld(),
                    (KodeId<?>)AEnumKodeId.KodeBId.asSnapshotVersionOld()
            );
            assertEquals(kodelisteForEnumKodeA.getBeskrivelse().getText(norsk), "Kodeliste for AEnumKode");
        } finally {
            persistenceSessionManager.close();
        }
    }

    public void testGetKodelisteForEnumKode_NO_NY() {
        PersistenceSessionManager persistenceSessionManager = createPersistenceSessionManager();

        try {
            StoreTestKodelisteLong kodelisteForEnumKodeA = persistenceSessionManager.get(AEnumKodeId.KODELISTE_ID);
            assertEquals(kodelisteForEnumKodeA.getBeskrivelse().getText(norskNynorsk), "Kodeliste for AEnumKode");
        } finally {
            persistenceSessionManager.close();
        }
    }

    public void testGetADbKode_NO() {
        PersistenceSessionManager persistenceSessionManager = createPersistenceSessionManager();

        try {
            ADbKode aDbKodeA1 = persistenceSessionManager.get(ADbKodeId.A1Id);
            assertEquals(aDbKodeA1.getId(), ADbKodeId.A1Id);
            assertEquals(aDbKodeA1.getKodelisteId(), ADbKodeId.KODELISTE_ID);
            assertEquals(aDbKodeA1.getBeskrivelse().getText(norsk), "Kodebeskrivelse for A1 bokmål");

            ADbKode aDbKodeA2 = persistenceSessionManager.get(ADbKodeId.A2Id);
            assertEquals(aDbKodeA2.getId(), ADbKodeId.A2Id);
            assertEquals(aDbKodeA2.getKodelisteId(), ADbKodeId.KODELISTE_ID);
            assertEquals(aDbKodeA2.getNavn().getText(norsk), "A2-navn bokmål");
            assertEquals(aDbKodeA2.getBeskrivelse().getText(norsk), "Kodebeskrivelse for A2 bokmål");
        } finally {
            persistenceSessionManager.close();
        }
    }

    public void testGetBDbKode_NO() {
        PersistenceSessionManager persistenceSessionManager = createPersistenceSessionManager();

        try {
            BDbKode bDbKodeB1 = persistenceSessionManager.get(BDbKodeId.B1Id);
            assertEquals(bDbKodeB1.getId(), BDbKodeId.B1Id);
            assertEquals(bDbKodeB1.getKodelisteId(), BDbKodeId.KODELISTE_ID);
            assertEquals(bDbKodeB1.getBeskrivelse().getText(norsk), "Kodebeskrivelse for B1 bokmål");

            BDbKode bDbKodeB2 = persistenceSessionManager.get(BDbKodeId.B2Id);
            assertEquals(bDbKodeB2.getId(), BDbKodeId.B2Id);
            assertEquals(bDbKodeB2.getKodelisteId(), BDbKodeId.KODELISTE_ID);
            assertEquals(bDbKodeB2.getBeskrivelse().getText(norsk), "Kodebeskrivelse for B2 bokmål");
        } finally {
            persistenceSessionManager.close();
        }
    }

    public void testGetC1DbKode_NO() {
        PersistenceSessionManager persistenceSessionManager = createPersistenceSessionManager();

        try {
            C1DbKode c1DbKodeC1A = persistenceSessionManager.get(C1DbKodeId.C1AId);
            assertEquals(c1DbKodeC1A.getId(), C1DbKodeId.C1AId);
            assertEquals(c1DbKodeC1A.getKodelisteId(), C1DbKodeId.KODELISTE_ID);
            assertEquals(c1DbKodeC1A.getBeskrivelse().getText(norsk), "Kodebeskrivelse for C1A bokmål");

            C1DbKode c1DbKodeC1B = persistenceSessionManager.get(C1DbKodeId.C1BId);
            assertEquals(c1DbKodeC1B.getId(), C1DbKodeId.C1BId);
            assertEquals(c1DbKodeC1B.getKodelisteId(), C1DbKodeId.KODELISTE_ID);
            assertEquals(c1DbKodeC1B.getBeskrivelse().getText(norsk), "Kodebeskrivelse for C1B bokmål");
        } finally {
            persistenceSessionManager.close();
        }
    }


    public void testGetXStrDbKode_NO() {
        PersistenceSessionManager persistenceSessionManager = createPersistenceSessionManager();

        try {
            XStrDbKode xStrDbKodeA = persistenceSessionManager.get(XStrDbKodeId.AId);
            assertEquals(xStrDbKodeA.getId(), XStrDbKodeId.AId);
            assertEquals(xStrDbKodeA.getKodelisteId(), XStrDbKodeId.KODELISTE_ID);
            assertEquals(xStrDbKodeA.getBeskrivelse().getText(norsk), "Kodebeskrivelse for X1 bokmål");

            XStrDbKode xStrDbKodeB = persistenceSessionManager.get(XStrDbKodeId.BId);
            assertEquals(xStrDbKodeB.getId(), XStrDbKodeId.BId);
            assertEquals(xStrDbKodeB.getKodelisteId(), XStrDbKodeId.KODELISTE_ID);
            assertEquals(xStrDbKodeB.getBeskrivelse().getText(norsk), "Kodebeskrivelse for X2 bokmål");
        } finally {
            persistenceSessionManager.close();
        }
    }


    public void testGetKodelisteForDbKode_NO_NY() {
        PersistenceSessionManager persistenceSessionManager = createPersistenceSessionManager();

        try {
            StoreTestKodelisteLong kodelisteForADbKode = persistenceSessionManager.get(ADbKodeId.KODELISTE_ID);
            assertEquals(kodelisteForADbKode.getKodeIdClass(), ADbKodeId.class);
            assertEquals(kodelisteForADbKode.getBeskrivelse().getText(norskNynorsk), "Kodelistebeskrivelse for ADbKode nynorsk");
            assertThat(kodelisteForADbKode.getKoderIds()).contains(ADbKodeId.A1Id, ADbKodeId.A2Id);
        } finally {
            persistenceSessionManager.close();
        }
    }

    public void testGetKodelister() {
        PersistenceSessionManager persistenceSessionManager = createPersistenceSessionManager();

        try {
            KodelistePersistenceSessionSubtypeHandler kodelisteSubtypeHandler = persistenceSessionManager.getForSnapshotVersion(SnapshotVersion.CURRENT).getImplementation(KodelistePersistenceSessionSubtypeHandler.class);
            Collection<KodelisteId<?>> kodelisteIds = kodelisteSubtypeHandler.getKodelisteIds();
            assertEquals(kodelisteIds.size(), 9);
        } finally {
            persistenceSessionManager.close();
        }

    }

    public void testInsertDbKode() {
        PersistenceSessionManager persistenceSessionManager = createPersistenceSessionManager();

        try {
            BDbKode bDbKodeNy = new BDbKode();
            bDbKodeNy.setId(new BDbKodeId(1234L, SnapshotVersion.CURRENT));
            bDbKodeNy.setKodeverdi("AAD12");
            Locale bokmaal = norsk;
            bDbKodeNy.getBeskrivelse().setText(bokmaal, "Kodebeskrivelse ting for ny kode som er inserted. BOKMÅL");
            bDbKodeNy.getNavn().setText(bokmaal, "1234-Bokmål");
            persistenceSessionManager.beginTransaction();
            persistenceSessionManager.insert(bDbKodeNy);
            persistenceSessionManager.commit();
        } finally {
            persistenceSessionManager.close();
        }

    }

    @Test(dependsOnMethods = "testInsertDbKode")
    public void testUpdateDbKode() {
        PersistenceSessionManager persistenceSessionManager = createPersistenceSessionManager();

        try {
            BDbKode dbKode = persistenceSessionManager.get(new BDbKodeId(1234L, SnapshotVersion.CURRENT));
            Locale nynorsk = norskNynorsk;
            dbKode.getBeskrivelse().setText(nynorsk, "Kodebeskrivelse ting for ny kode som er inserted. NYNORSK");
            dbKode.getNavn().setText(nynorsk, "1234-Nynorsk");
            persistenceSessionManager.beginTransaction();
            persistenceSessionManager.update(dbKode);
            persistenceSessionManager.commit();
        } finally {
            persistenceSessionManager.close();
        }
    }

    @Test(dependsOnMethods = "testUpdateDbKode")
    public void testDeleteDbKode() {
        PersistenceSessionManager persistenceSessionManager = createPersistenceSessionManager();

        try {
            BDbKode dbKode = persistenceSessionManager.get(new BDbKodeId(1234L, SnapshotVersion.CURRENT));
            persistenceSessionManager.beginTransaction();
            persistenceSessionManager.delete(dbKode);
            persistenceSessionManager.commit();
        } finally {
            persistenceSessionManager.close();
        }
    }

//    public void testMixed() {
//        PersistenceSessionManager persistenceSessionManager = createPersistenceSessionManager();
//        try {
//            List<KodeId<? extends Kode>> ids = Arrays.<KodeId<? extends Kode>>asList(AEnumKodeId.KodeAId, new ADbKodeId(1L, SnapshotVersion.CURRENT), new ADbKodeId(2L, SnapshotVersion.CURRENT));
//            Collection<? extends Kode> koder = persistenceSessionManager.get(ids);
//            assertThat(koder).onProperty("id").containsOnly(AEnumKodeId.KodeAId, ADbKodeId.A1Id, ADbKodeId.A2Id);
//
//
//        } finally {
//            persistenceSessionManager.close();
//        }
//    }
//
//    public void testGetImplementation() {
//        PersistenceSessionManager persistenceSessionManager = createPersistenceSessionManager();
//
//        KodePersistenceSession implementation = persistenceSessionManager.getForSnapshotVersion(SnapshotVersion.CURRENT).getImplementation(KodePersistenceSession.class);
//        try {
//            AEnumKode aEnumKodeA = implementation.get(AEnumKodeId.KodeAId);
//            Assert.assertEquals("Kode A", aEnumKodeA.getBeskrivelse());
//        } finally {
//            persistenceSessionManager.close();
//        }
//    }
//
//
//    @Test(expectedExceptions = ImplementationException.class)
//    public void testOldEnumKode() {
//        PersistenceSessionManager persistenceSessionManager = createPersistenceSessionManager();
//
//        try {
//            AEnumKode aEnumKodeA = persistenceSessionManager.get(AEnumKodeId.KodeAId.asSnapshotVersion(SnapshotVersion.OLD));
//            Assert.assertEquals("Kode A", aEnumKodeA.getBeskrivelse());
//        } finally {
//            persistenceSessionManager.close();
//        }
//    }
//
//    public void testOldDbKode() {
//        PersistenceSessionManager persistenceSessionManager = createPersistenceSessionManager();
//
//        try {
//            ADbKode aDbKode1 = persistenceSessionManager.get(new ADbKodeId(1L, SnapshotVersion.OLD));
//            Assert.assertEquals(aDbKode1.getId().getSnapshotVersion(), SnapshotVersion.OLD);
//            Assert.assertEquals("Kodebeskrivelse for A1 bokmål", aDbKode1.getBeskrivelse());
//        } finally {
//            persistenceSessionManager.close();
//        }
//    }
//
//    public void testMixedCurrentAndOld() {
//        PersistenceSessionManager persistenceSessionManager = createPersistenceSessionManager();
//        try {
//            List<KodeId<? extends Kode>> ids = Arrays.<KodeId<? extends Kode>>asList(
//                    AEnumKodeId.KodeAId, new ADbKodeId(1L, SnapshotVersion.CURRENT), new ADbKodeId(2L, SnapshotVersion.CURRENT),
//                    new ADbKodeId(1L, SnapshotVersion.OLD), new ADbKodeId(2L, SnapshotVersion.OLD));
//            Collection<? extends Kode> koder = persistenceSessionManager.get(ids);
//            assertThat(koder).onProperty("id").containsOnly(AEnumKodeId.KodeAId, ADbKodeId.A1Id, ADbKodeId.A2Id, ADbKodeId.A1Id.asSnapshotVersion(SnapshotVersion.OLD), ADbKodeId.A2Id.asSnapshotVersion(SnapshotVersion.OLD));
//        } finally {
//            persistenceSessionManager.close();
//        }
//    }
//
}

