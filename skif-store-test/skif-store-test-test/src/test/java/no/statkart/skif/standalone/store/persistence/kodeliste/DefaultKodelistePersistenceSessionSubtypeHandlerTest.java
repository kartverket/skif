package no.statkart.skif.standalone.store.persistence.kodeliste;

import no.statkart.skif.standalone.util.testsupport.StandAloneTestHelper;
import no.statkart.skif.store.BubbleModelConfiguration;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.kodeliste.KodeId;
import no.statkart.skif.store.kodeliste.KodelisteId;
import no.statkart.skif.store.persistence.DefaultPersistenceSessionManager;
import no.statkart.skif.store.persistence.DefaultPersistenceSessionStrategy;
import no.statkart.skif.store.persistence.PersistenceSessionManager;
import no.statkart.skif.store.persistence.hibernate.HibernatePersistenceSessionMasterImpl;
import no.statkart.skif.store.persistence.hibernate.HibernateSessionFactoryBuilder;
import no.statkart.skif.store.persistence.hibernate.HibernateSessionFactoryManagerBundle;
import no.statkart.skif.store.persistence.kodeliste.DefaultKodelistePersistenceSessionSubtypeHandler;
import no.statkart.skif.store.persistence.kodeliste.EnumKodelisteManager;
import no.statkart.skif.store.persistence.kodeliste.KodelistePersistenceSessionSubtypeHandler;
import no.statkart.skif.storetest.domain.demo.koder.ADbKode;
import no.statkart.skif.storetest.domain.demo.koder.ADbKodeId;
import no.statkart.skif.storetest.domain.demo.koder.AEnumKode;
import no.statkart.skif.storetest.domain.demo.koder.AEnumKodeId;
import no.statkart.skif.storetest.domain.demo.koder.BDbKode;
import no.statkart.skif.storetest.domain.demo.koder.BDbKodeId;
import no.statkart.skif.storetest.domain.demo.koder.BEnumKode;
import no.statkart.skif.storetest.domain.demo.koder.BEnumKodeId;
import no.statkart.skif.storetest.domain.demo.koder.C1DbKode;
import no.statkart.skif.storetest.domain.demo.koder.C1DbKodeId;
import no.statkart.skif.storetest.domain.demo.koder.C2DbKode;
import no.statkart.skif.storetest.domain.demo.koder.CDbKode;
import no.statkart.skif.storetest.domain.demo.koder.SEnumKode;
import no.statkart.skif.storetest.domain.demo.koder.SEnumKodeId;
import no.statkart.skif.storetest.domain.demo.koder.XStrDbKode;
import no.statkart.skif.storetest.domain.demo.koder.XStrDbKodeId;
import no.statkart.skif.storetest.domain.kodeliste.StoreTestKodelisteLong;
import no.statkart.skif.storetest.domain.kodeliste.StoreTestKodelisteString;
import no.statkart.skif.storetest.domain.koder.HistoriskDbKode;
import no.statkart.skif.storetest.domain.koder.SimpleLocalizedDbKode;
import no.statkart.skif.storetest.domain.koder.SimpleLocalizedDbKodeId;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Collection;
import java.util.Locale;
import java.util.Properties;

import static no.statkart.skif.standalone.util.testsupport.StandAloneTestHelper.createHibernateSessionFactorManagerBundle;
import static no.statkart.skif.standalone.util.testsupport.StandAloneTestHelper.createHibernateSessionFactoryBuilderWithHistory;
import static org.assertj.core.api.Assertions.assertThat;

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
            assertThat(aEnumKodeA.getId()).isEqualTo(AEnumKodeId.KodeAId);
            assertThat(aEnumKodeA.getKodelisteId()).isEqualTo(AEnumKodeId.KODELISTE_ID);
            assertThat(aEnumKodeA.getBeskrivelse().getText(norsk)).isEqualTo("Kode A er den første av AKodene");

            AEnumKode aEnumKodeB = persistenceSessionManager.get(AEnumKodeId.KodeBId);
            assertThat(aEnumKodeB.getId()).isEqualTo(AEnumKodeId.KodeBId);
            assertThat(aEnumKodeB.getKodelisteId()).isEqualTo(AEnumKodeId.KODELISTE_ID);
            assertThat(aEnumKodeB.getBeskrivelse().getText(norsk)).isEqualTo("Kode B er den andre av AKodene");
        } finally {
            persistenceSessionManager.close();
        }
    }

    public void testGetBEnumKode_NO() {
        PersistenceSessionManager persistenceSessionManager = createPersistenceSessionManager();

        try {
            BEnumKode bEnumKodeA = persistenceSessionManager.get(BEnumKodeId.KodeAId);
            assertThat(bEnumKodeA.getId()).isEqualTo(BEnumKodeId.KodeAId);
            assertThat(bEnumKodeA.getKodelisteId()).isEqualTo(BEnumKodeId.KODELISTE_ID);
            assertThat(bEnumKodeA.getBeskrivelse().getText(norsk)).isEqualTo("Kode A er den første av BKodene");

            BEnumKode bEnumKodeB = persistenceSessionManager.get(BEnumKodeId.KodeBId);
            assertThat(bEnumKodeB.getId()).isEqualTo(BEnumKodeId.KodeBId);
            assertThat(bEnumKodeB.getKodelisteId()).isEqualTo(BEnumKodeId.KODELISTE_ID);
            assertThat(bEnumKodeB.getBeskrivelse().getText(norsk)).isEqualTo("Kode B er den andre av BKodene");
        } finally {
            persistenceSessionManager.close();
        }
    }

    public void testGetSEnumKode_NO() {
        PersistenceSessionManager persistenceSessionManager = createPersistenceSessionManager();

        try {
            SEnumKode sEnumKodeA = persistenceSessionManager.get(SEnumKodeId.KodeAId);
            assertThat(sEnumKodeA.getId()).isEqualTo(SEnumKodeId.KodeAId);
            assertThat(sEnumKodeA.getKodelisteId()).isEqualTo(SEnumKodeId.KODELISTE_ID);
            assertThat(sEnumKodeA.getBeskrivelse().getText(norsk)).isEqualTo("Kode A er den første av SKodene");

            SEnumKode sEnumKodeB = persistenceSessionManager.get(SEnumKodeId.KodeBId);
            assertThat(sEnumKodeB.getId()).isEqualTo(SEnumKodeId.KodeBId);
            assertThat(sEnumKodeB.getKodelisteId()).isEqualTo(SEnumKodeId.KODELISTE_ID);
            assertThat(sEnumKodeB.getBeskrivelse().getText(norsk)).isEqualTo("Kode B er den andre av SKodene");
        } finally {
            persistenceSessionManager.close();
        }
    }

    public void testGetEnumKode_NO_Old() {
        PersistenceSessionManager persistenceSessionManager = createPersistenceSessionManager();

        try {
            AEnumKode aEnumKodeA = persistenceSessionManager.get((AEnumKodeId) AEnumKodeId.KodeAId.asSnapshotVersionOld());
            assertThat(aEnumKodeA.getId()).isEqualTo(AEnumKodeId.KodeAId.asSnapshotVersionOld());
            assertThat(aEnumKodeA.getKodelisteId()).isEqualTo(AEnumKodeId.KODELISTE_ID.asSnapshotVersionOld());
            assertThat(aEnumKodeA.getBeskrivelse().getText(norsk)).isEqualTo("Kode A er den første av AKodene");
        } finally {
            persistenceSessionManager.close();
        }
    }

    public void testGetEnumKode_NO_NY() {
        PersistenceSessionManager persistenceSessionManager = createPersistenceSessionManager();

        try {
            AEnumKode aEnumKodeA = persistenceSessionManager.get(AEnumKodeId.KodeAId);
            assertThat(aEnumKodeA.getBeskrivelse().getText(norskNynorsk)).isEqualTo("Kode A er den fyrste av AKodane");

            AEnumKode aEnumKodeB = persistenceSessionManager.get(AEnumKodeId.KodeBId);
            assertThat(aEnumKodeB.getBeskrivelse().getText(norskNynorsk)).isEqualTo("Kode B er den andre av AKodane");
        } finally {
            persistenceSessionManager.close();
        }
    }

    public void testGetKodelisteForEnum_NO() {
        PersistenceSessionManager persistenceSessionManager = createPersistenceSessionManager();

        try {
            StoreTestKodelisteLong kodelisteForAEnumKode = persistenceSessionManager.get(AEnumKodeId.KODELISTE_ID);
            assertThat(kodelisteForAEnumKode.getKodeIdClass()).isEqualTo(AEnumKodeId.class);
            assertThat(kodelisteForAEnumKode.getId()).isEqualTo(AEnumKodeId.KODELISTE_ID);
            assertThat(kodelisteForAEnumKode.getBeskrivelse().getText(norsk)).isEqualTo("Kodeliste for AEnumKode");

            StoreTestKodelisteLong kodelisteForBEnumKode = persistenceSessionManager.get(BEnumKodeId.KODELISTE_ID);
            assertThat(kodelisteForBEnumKode.getKodeIdClass()).isEqualTo(BEnumKodeId.class);
            assertThat(kodelisteForBEnumKode.getId()).isEqualTo(BEnumKodeId.KODELISTE_ID);
            assertThat(kodelisteForBEnumKode.getBeskrivelse().getText(norsk)).isEqualTo("Kodeliste for BEnumKode");

            StoreTestKodelisteString kodelisteForSEnumKode = persistenceSessionManager.get(SEnumKodeId.KODELISTE_ID);
            assertThat(kodelisteForSEnumKode.getKodeIdClass()).isEqualTo(SEnumKodeId.class);
            assertThat(kodelisteForSEnumKode.getId()).isEqualTo(SEnumKodeId.KODELISTE_ID);
            assertThat(kodelisteForSEnumKode.getBeskrivelse().getText(norsk)).isEqualTo("Kodeliste for SEnumKode, som er String-basert");
        } finally {
            persistenceSessionManager.close();
        }
    }

    public void testGetKodelisteForEnum_NO_Old() {
        PersistenceSessionManager persistenceSessionManager = createPersistenceSessionManager();

        try {
            StoreTestKodelisteLong kodelisteForEnumKodeA = persistenceSessionManager.get(AEnumKodeId.KODELISTE_ID.asSnapshotVersionOld());
            assertThat(kodelisteForEnumKodeA.getKodeIdClass()).isEqualTo(AEnumKodeId.class);
            assertThat(kodelisteForEnumKodeA.getId()).isEqualTo(AEnumKodeId.KODELISTE_ID.asSnapshotVersionOld());
            assertThat(kodelisteForEnumKodeA.getKoderIds()).containsExactly(
                    (KodeId<?>)AEnumKodeId.IkkeOppgittId.asSnapshotVersionOld(),
                    (KodeId<?>)AEnumKodeId.KodeAId.asSnapshotVersionOld(),
                    (KodeId<?>)AEnumKodeId.KodeBId.asSnapshotVersionOld()
            );
            assertThat(kodelisteForEnumKodeA.getBeskrivelse().getText(norsk)).isEqualTo("Kodeliste for AEnumKode");
        } finally {
            persistenceSessionManager.close();
        }
    }

    public void testGetKodelisteForEnumKode_NO_NY() {
        PersistenceSessionManager persistenceSessionManager = createPersistenceSessionManager();

        try {
            StoreTestKodelisteLong kodelisteForEnumKodeA = persistenceSessionManager.get(AEnumKodeId.KODELISTE_ID);
            assertThat(kodelisteForEnumKodeA.getBeskrivelse().getText(norskNynorsk)).isEqualTo("Kodeliste for AEnumKode");
        } finally {
            persistenceSessionManager.close();
        }
    }

    public void testGetADbKode_NO() {
        PersistenceSessionManager persistenceSessionManager = createPersistenceSessionManager();

        try {
            ADbKode aDbKodeA1 = persistenceSessionManager.get(ADbKodeId.A1Id);
            assertThat(aDbKodeA1.getId()).isEqualTo(ADbKodeId.A1Id);
            assertThat(aDbKodeA1.getKodelisteId()).isEqualTo(ADbKodeId.KODELISTE_ID);
            assertThat(aDbKodeA1.getBeskrivelse().getText(norsk)).isEqualTo("Kodebeskrivelse for A1 bokmål");

            ADbKode aDbKodeA2 = persistenceSessionManager.get(ADbKodeId.A2Id);
            assertThat(aDbKodeA2.getId()).isEqualTo(ADbKodeId.A2Id);
            assertThat(aDbKodeA2.getKodelisteId()).isEqualTo(ADbKodeId.KODELISTE_ID);
            assertThat(aDbKodeA2.getNavn().getText(norsk)).isEqualTo("A2-navn bokmål");
            assertThat(aDbKodeA2.getBeskrivelse().getText(norsk)).isEqualTo("Kodebeskrivelse for A2 bokmål");
        } finally {
            persistenceSessionManager.close();
        }
    }

    public void testGetBDbKode_NO() {
        PersistenceSessionManager persistenceSessionManager = createPersistenceSessionManager();

        try {
            BDbKode bDbKodeB1 = persistenceSessionManager.get(BDbKodeId.B1Id);
            assertThat(bDbKodeB1.getId()).isEqualTo(BDbKodeId.B1Id);
            assertThat(bDbKodeB1.getKodelisteId()).isEqualTo(BDbKodeId.KODELISTE_ID);
            assertThat(bDbKodeB1.getBeskrivelse().getText(norsk)).isEqualTo("Kodebeskrivelse for B1 bokmål");

            BDbKode bDbKodeB2 = persistenceSessionManager.get(BDbKodeId.B2Id);
            assertThat(bDbKodeB2.getId()).isEqualTo(BDbKodeId.B2Id);
            assertThat(bDbKodeB2.getKodelisteId()).isEqualTo(BDbKodeId.KODELISTE_ID);
            assertThat(bDbKodeB2.getBeskrivelse().getText(norsk)).isEqualTo("Kodebeskrivelse for B2 bokmål");
        } finally {
            persistenceSessionManager.close();
        }
    }

    public void testGetC1DbKode_NO() {
        PersistenceSessionManager persistenceSessionManager = createPersistenceSessionManager();

        try {
            C1DbKode c1DbKodeC1A = persistenceSessionManager.get(C1DbKodeId.C1AId);
            assertThat(c1DbKodeC1A.getId()).isEqualTo(C1DbKodeId.C1AId);
            assertThat(c1DbKodeC1A.getKodelisteId()).isEqualTo(C1DbKodeId.KODELISTE_ID);
            assertThat(c1DbKodeC1A.getBeskrivelse().getText(norsk)).isEqualTo("Kodebeskrivelse for C1A bokmål");

            C1DbKode c1DbKodeC1B = persistenceSessionManager.get(C1DbKodeId.C1BId);
            assertThat(c1DbKodeC1B.getId()).isEqualTo(C1DbKodeId.C1BId);
            assertThat(c1DbKodeC1B.getKodelisteId()).isEqualTo(C1DbKodeId.KODELISTE_ID);
            assertThat(c1DbKodeC1B.getBeskrivelse().getText(norsk)).isEqualTo("Kodebeskrivelse for C1B bokmål");
        } finally {
            persistenceSessionManager.close();
        }
    }


    public void testGetXStrDbKode_NO() {
        PersistenceSessionManager persistenceSessionManager = createPersistenceSessionManager();

        try {
            XStrDbKode xStrDbKodeA = persistenceSessionManager.get(XStrDbKodeId.AId);
            assertThat(xStrDbKodeA.getId()).isEqualTo(XStrDbKodeId.AId);
            assertThat(xStrDbKodeA.getKodelisteId()).isEqualTo(XStrDbKodeId.KODELISTE_ID);
            assertThat(xStrDbKodeA.getBeskrivelse().getText(norsk)).isEqualTo("Kodebeskrivelse for X1 bokmål");

            XStrDbKode xStrDbKodeB = persistenceSessionManager.get(XStrDbKodeId.BId);
            assertThat(xStrDbKodeB.getId()).isEqualTo(XStrDbKodeId.BId);
            assertThat(xStrDbKodeB.getKodelisteId()).isEqualTo(XStrDbKodeId.KODELISTE_ID);
            assertThat(xStrDbKodeB.getBeskrivelse().getText(norsk)).isEqualTo("Kodebeskrivelse for X2 bokmål");
        } finally {
            persistenceSessionManager.close();
        }
    }


    public void testGetKodelisteForDbKode_NO_NY() {
        PersistenceSessionManager persistenceSessionManager = createPersistenceSessionManager();

        try {
            StoreTestKodelisteLong kodelisteForADbKode = persistenceSessionManager.get(ADbKodeId.KODELISTE_ID);
            assertThat(kodelisteForADbKode.getKodeIdClass()).isEqualTo(ADbKodeId.class);
            assertThat(kodelisteForADbKode.getBeskrivelse().getText(norskNynorsk)).isEqualTo("Kodelistebeskrivelse for ADbKode nynorsk");
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
            assertThat(kodelisteIds.size()).isEqualTo(9);
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
            bDbKodeNy.getBeskrivelse().setText(bokmaal, "Kodebeskrivelse ting for ny kode som er inserted. bokmål");
            bDbKodeNy.getNavn().setText(bokmaal, "1234-bokmål");
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
//            Assert.assertThat(aEnumKodeA.getBeskrivelse()).isEqualTo("Kode A");
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
//            Assert.assertThat(aEnumKodeA.getBeskrivelse()).isEqualTo("Kode A");
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
//            Assert.assertThat(aDbKode1.getId().getSnapshotVersion()).isEqualTo(SnapshotVersion.OLD);
//            Assert.assertThat(aDbKode1.getBeskrivelse()).isEqualTo("Kodebeskrivelse for A1 bokmål");
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

