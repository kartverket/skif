package no.statkart.skif.store.persistence.kodeliste;

import no.statkart.skif.ConfigurationConverter;
import no.statkart.skif.config.Configuration;
import no.statkart.skif.config.PropertiesConfiguration;
import no.statkart.skif.service.DefaultServiceContext;
import no.statkart.skif.service.ServiceContext;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.kodeliste.Kode;
import no.statkart.skif.store.kodeliste.KodeId;
import no.statkart.skif.store.kodeliste.Kodeliste;
import no.statkart.skif.store.kodeliste.KodelisteId;
import no.statkart.skif.store.persistence.DefaultPersistenceSessionManager;
import no.statkart.skif.store.persistence.DefaultPersistenceSessionStrategy;
import no.statkart.skif.store.persistence.PersistenceSessionManager;
import no.statkart.skif.store.persistence.hibernate.HibernatePersistenceSessionMasterImpl;
import no.statkart.skif.store.persistence.hibernate.HibernateSessionFactoryBuilder;
import no.statkart.skif.store.persistence.hibernate.HibernateSessionFactoryManager;
import no.statkart.skif.store.persistence.hibernate.HibernateSessionFactoryManagerBundle;
import no.statkart.skif.storetest.domain.demo.koder.*;
import no.statkart.skif.storetest.domain.kodeliste.StoreTestKodelisteLong;
import no.statkart.skif.storetest.domain.kodeliste.StoreTestKodelisteString;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.*;

import static no.statkart.skif.storetest.TestHelper.createHibernateSessionFactorManagerBundle;
import static no.statkart.skif.storetest.TestHelper.createHibernateSessionFactoryBuilderWithHistory;
import static org.fest.assertions.api.Assertions.*;
import static org.testng.Assert.assertEquals;

/**
 * @author Henrik Fredholm
 * @since 2.1
 */
@Test
public class DefaultKodelistePersistenceSessionSubtypeHandlerTest {
    Properties hibernateProperties;

    HibernateSessionFactoryManager sessionFactoryManager;
    HibernateSessionFactoryManagerBundle sessionFactoryManagerBundle;

    public DefaultKodelistePersistenceSessionSubtypeHandlerTest() {
        Configuration cfg = new PropertiesConfiguration("no/statkart/skif/storetest/config/persistence/skiftest-hibernate-singlevm.properties");
        hibernateProperties = ConfigurationConverter.getProperties(cfg);
    }

    @BeforeClass
    public void setUp() {
        HibernateSessionFactoryBuilder sessionFactoryBuilder = createHibernateSessionFactoryBuilderWithHistory();
//        sessionFactoryBuilder.addResourceUsingRelativePath("kodeliste", ADbKode.class);
        sessionFactoryBuilder.addResource(ADbKode.class);
        sessionFactoryBuilder.addResource(BDbKode.class);
        sessionFactoryBuilder.addResource(XStrDbKode.class);
        sessionFactoryBuilder.addResourceWithSubclasses(CDbKode.class, C1DbKode.class, C2DbKode.class);
        sessionFactoryBuilder.addResource(StoreTestKodelisteLong.class);


        sessionFactoryManagerBundle = createHibernateSessionFactorManagerBundle(sessionFactoryBuilder, hibernateProperties);
    }

    @AfterClass
    void tearDown() {
        sessionFactoryManagerBundle.close();
    }

    private PersistenceSessionManager createPersistenceSessionManager() {
        ServiceContext context = new DefaultServiceContext();
        context.setLocale(new Locale("no", "NO"));

        return createPersistenceSessionManager(context);
    }


    private PersistenceSessionManager createPersistenceSessionManager(ServiceContext context) {
        EnumKodelisteManager enumKodelisteManager = new EnumKodelisteManager();
        enumKodelisteManager.installStatic(AEnumKodeId.class);
        enumKodelisteManager.installStatic(BEnumKodeId.class);
        enumKodelisteManager.installStatic(SEnumKodeId.class);

        HibernatePersistenceSessionMasterImpl masterCurrent = new HibernatePersistenceSessionMasterImpl(
                sessionFactoryManagerBundle.getBundle().get(0)
        );
        HibernatePersistenceSessionMasterImpl masterOld = new HibernatePersistenceSessionMasterImpl(
                sessionFactoryManagerBundle.getBundle().get(1)
        );

        Collection<Class<? extends Kodeliste>> kodelisteClasses = new ArrayList<Class<? extends Kodeliste>>();
        kodelisteClasses.add(StoreTestKodelisteLong.class);
        kodelisteClasses.add(StoreTestKodelisteString.class);

        return new DefaultPersistenceSessionManager(
                new DefaultPersistenceSessionStrategy(
                        masterCurrent,
                        new DefaultKodelistePersistenceSessionSubtypeHandler(masterCurrent, enumKodelisteManager, kodelisteClasses, context)
                ),
                new DefaultPersistenceSessionStrategy(
                        masterOld,
                        new DefaultKodelistePersistenceSessionSubtypeHandler(masterOld, enumKodelisteManager, kodelisteClasses, context)
                )
        );
    }

    public void testGetAEnumKode_NO() {
        ServiceContext context = new DefaultServiceContext();
        context.setLocale(new Locale("no", "NO"));
        PersistenceSessionManager persistenceSessionManager = createPersistenceSessionManager(context);

        try {
            AEnumKode aEnumKodeA = persistenceSessionManager.get(AEnumKodeId.KodeAId);
            assertEquals(aEnumKodeA.getId(), AEnumKodeId.KodeAId);
            assertEquals(aEnumKodeA.getKodelisteId(), AEnumKodeId.KODELISTE_ID);
            assertEquals(aEnumKodeA.getBeskrivelse(), "AEnumKode.KodeA.beskrivelse (no_NO)");

            AEnumKode aEnumKodeB = persistenceSessionManager.get(AEnumKodeId.KodeBId);
            assertEquals(aEnumKodeB.getId(), AEnumKodeId.KodeBId);
            assertEquals(aEnumKodeB.getKodelisteId(), AEnumKodeId.KODELISTE_ID);
            assertEquals(aEnumKodeB.getBeskrivelse(), "AEnumKode.KodeB.beskrivelse (no_NO)");
        } finally {
            persistenceSessionManager.close();
        }
    }

    public void testGetBEnumKode_NO() {
        ServiceContext context = new DefaultServiceContext();
        context.setLocale(new Locale("no", "NO"));
        PersistenceSessionManager persistenceSessionManager = createPersistenceSessionManager(context);

        try {
            BEnumKode bEnumKodeA = persistenceSessionManager.get(BEnumKodeId.KodeAId);
            assertEquals(bEnumKodeA.getId(), BEnumKodeId.KodeAId);
            assertEquals(bEnumKodeA.getKodelisteId(), BEnumKodeId.KODELISTE_ID);
            assertEquals(bEnumKodeA.getBeskrivelse(), "BEnumKode.KodeA.beskrivelse (no_NO)");

            BEnumKode bEnumKodeB = persistenceSessionManager.get(BEnumKodeId.KodeBId);
            assertEquals(bEnumKodeB.getId(), BEnumKodeId.KodeBId);
            assertEquals(bEnumKodeB.getKodelisteId(), BEnumKodeId.KODELISTE_ID);
            assertEquals(bEnumKodeB.getBeskrivelse(), "BEnumKode.KodeB.beskrivelse (no_NO)");
        } finally {
            persistenceSessionManager.close();
        }
    }

    public void testGetSEnumKode_NO() {
        ServiceContext context = new DefaultServiceContext();
        context.setLocale(new Locale("no", "NO"));
        PersistenceSessionManager persistenceSessionManager = createPersistenceSessionManager(context);

        try {
            SEnumKode sEnumKodeA = persistenceSessionManager.get(SEnumKodeId.KodeAId);
            assertEquals(sEnumKodeA.getId(), SEnumKodeId.KodeAId);
            assertEquals(sEnumKodeA.getKodelisteId(), SEnumKodeId.KODELISTE_ID);
            assertEquals(sEnumKodeA.getBeskrivelse(), "SEnumKode.KodeA.beskrivelse (no_NO)");

            SEnumKode sEnumKodeB = persistenceSessionManager.get(SEnumKodeId.KodeBId);
            assertEquals(sEnumKodeA.getId(), SEnumKodeId.KodeAId);
            assertEquals(sEnumKodeA.getKodelisteId(), SEnumKodeId.KODELISTE_ID);
            assertEquals(sEnumKodeA.getBeskrivelse(), "SEnumKode.KodeA.beskrivelse (no_NO)");
        } finally {
            persistenceSessionManager.close();
        }
    }

    public void testGetEnumKode_NO_Old() {
        ServiceContext context = new DefaultServiceContext();
        context.setLocale(new Locale("no", "NO"));
        PersistenceSessionManager persistenceSessionManager = createPersistenceSessionManager(context);

        try {
            AEnumKode aEnumKodeA = persistenceSessionManager.get(AEnumKodeId.KodeAId.asSnapshotVersionOld());
            assertEquals(aEnumKodeA.getId(), AEnumKodeId.KodeAId.asSnapshotVersionOld());
            assertEquals(aEnumKodeA.getKodelisteId(), AEnumKodeId.KODELISTE_ID.asSnapshotVersionOld());
            assertEquals(aEnumKodeA.getBeskrivelse(), "AEnumKode.KodeA.beskrivelse (no_NO)");
        } finally {
            persistenceSessionManager.close();
        }
    }

    public void testGetEnumKode_NO_NY() {
        ServiceContext context = new DefaultServiceContext();
        context.setLocale(new Locale("no", "NO", "NY"));
        PersistenceSessionManager persistenceSessionManager = createPersistenceSessionManager(context);

        try {
            AEnumKode aEnumKodeA = persistenceSessionManager.get(AEnumKodeId.KodeAId);
            assertEquals(aEnumKodeA.getBeskrivelse(), "AEnumKode.KodeA.beskrivelse (no_NO_NY)");

            AEnumKode aEnumKodeB = persistenceSessionManager.get(AEnumKodeId.KodeBId);
            assertEquals(aEnumKodeB.getBeskrivelse(), "AEnumKode.KodeB.beskrivelse (no_NO_NY)");
        } finally {
            persistenceSessionManager.close();
        }
    }

    public void testGetKodelisteForEnum_NO() {
        ServiceContext context = new DefaultServiceContext();
        PersistenceSessionManager persistenceSessionManager = createPersistenceSessionManager(context);
        context.setLocale(new Locale("no", "NO"));

        try {
            Kodeliste kodelisteForAEnumKode = persistenceSessionManager.get(AEnumKodeId.KODELISTE_ID);
            assertEquals(kodelisteForAEnumKode.getKodeIdClass(), AEnumKodeId.class);
            assertEquals(kodelisteForAEnumKode.getId(), AEnumKodeId.KODELISTE_ID);
            assertEquals(kodelisteForAEnumKode.getBeskrivelse(), "AEnumKode.kodeliste.beskrivelse (no_NO)");

            Kodeliste kodelisteForBEnumKode = persistenceSessionManager.get(BEnumKodeId.KODELISTE_ID);
            assertEquals(kodelisteForBEnumKode.getKodeIdClass(), BEnumKodeId.class);
            assertEquals(kodelisteForBEnumKode.getId(), BEnumKodeId.KODELISTE_ID);
            assertEquals(kodelisteForBEnumKode.getBeskrivelse(), "BEnumKode.kodeliste.beskrivelse (no_NO)");

            Kodeliste kodelisteForSEnumKode = persistenceSessionManager.get(SEnumKodeId.KODELISTE_ID);
            assertEquals(kodelisteForSEnumKode.getKodeIdClass(), SEnumKodeId.class);
            assertEquals(kodelisteForSEnumKode.getId(), SEnumKodeId.KODELISTE_ID);
            assertEquals(kodelisteForSEnumKode.getBeskrivelse(), "SEnumKode.kodeliste.beskrivelse (no_NO)");
        } finally {
            persistenceSessionManager.close();
        }
    }

    public void testGetKodelisteForEnum_NO_Old() {
        ServiceContext context = new DefaultServiceContext();
        PersistenceSessionManager persistenceSessionManager = createPersistenceSessionManager(context);
        context.setLocale(new Locale("no", "NO"));

        try {
            Kodeliste kodelisteForEnumKodeA = persistenceSessionManager.get(AEnumKodeId.KODELISTE_ID.asSnapshotVersionOld());
            assertEquals(kodelisteForEnumKodeA.getKodeIdClass(), AEnumKodeId.class);
            assertEquals(kodelisteForEnumKodeA.getId(), AEnumKodeId.KODELISTE_ID.asSnapshotVersionOld());
            assertThat(kodelisteForEnumKodeA.getKodeIds()).containsExactly(
                    (KodeId<?>)AEnumKodeId.IkkeOppgittId.asSnapshotVersionOld(),
                    (KodeId<?>)AEnumKodeId.KodeAId.asSnapshotVersionOld(),
                    (KodeId<?>)AEnumKodeId.KodeBId.asSnapshotVersionOld()
            );
            assertEquals(kodelisteForEnumKodeA.getBeskrivelse(), "AEnumKode.kodeliste.beskrivelse (no_NO)");
        } finally {
            persistenceSessionManager.close();
        }
    }

    public void testGetKodelisteForEnumKode_NO_NY() {
        ServiceContext context = new DefaultServiceContext();
        PersistenceSessionManager persistenceSessionManager = createPersistenceSessionManager(context);
        context.setLocale(new Locale("no", "NO", "NY"));

        try {
            Kodeliste kodelisteForEnumKodeA = persistenceSessionManager.get(AEnumKodeId.KODELISTE_ID);
            assertEquals(kodelisteForEnumKodeA.getBeskrivelse(), "AEnumKode.kodeliste.beskrivelse (no_NO_NY)");
        } finally {
            persistenceSessionManager.close();
        }
    }

    public void testGetADbKode_NO() {
        ServiceContext context = new DefaultServiceContext();
        context.setLocale(new Locale("no", "NO"));
        PersistenceSessionManager persistenceSessionManager = createPersistenceSessionManager(context);

        try {
            ADbKode aDbKodeA1 = persistenceSessionManager.get(ADbKodeId.A1Id);
            assertEquals(aDbKodeA1.getId(), ADbKodeId.A1Id);
            assertEquals(aDbKodeA1.getKodelisteId(), ADbKodeId.KODELISTE_ID);
            assertEquals(aDbKodeA1.getBeskrivelse(), "Kodebeskrivelse for A1 bokmål");

            ADbKode aDbKodeA2 = persistenceSessionManager.get(ADbKodeId.A2Id);
            assertEquals(aDbKodeA2.getId(), ADbKodeId.A2Id);
            assertEquals(aDbKodeA2.getKodelisteId(), ADbKodeId.KODELISTE_ID);
            assertEquals(aDbKodeA2.getNavn(), "A2-navn bokmål");
            assertEquals(aDbKodeA2.getBeskrivelse(), "Kodebeskrivelse for A2 bokmål");
        } finally {
            persistenceSessionManager.close();
        }
    }

    public void testGetBDbKode_NO() {
        ServiceContext context = new DefaultServiceContext();
        context.setLocale(new Locale("no", "NO"));
        PersistenceSessionManager persistenceSessionManager = createPersistenceSessionManager(context);

        try {
            BDbKode bDbKodeB1 = persistenceSessionManager.get(BDbKodeId.B1Id);
            assertEquals(bDbKodeB1.getId(), BDbKodeId.B1Id);
            assertEquals(bDbKodeB1.getKodelisteId(), BDbKodeId.KODELISTE_ID);
            assertEquals(bDbKodeB1.getBeskrivelse(), "Kodebeskrivelse for B1 bokmål");

            BDbKode bDbKodeB2 = persistenceSessionManager.get(BDbKodeId.B2Id);
            assertEquals(bDbKodeB2.getId(), BDbKodeId.B2Id);
            assertEquals(bDbKodeB2.getKodelisteId(), BDbKodeId.KODELISTE_ID);
            assertEquals(bDbKodeB2.getBeskrivelse(), "Kodebeskrivelse for B2 bokmål");
        } finally {
            persistenceSessionManager.close();
        }
    }

    public void testGetC1DbKode_NO() {
        ServiceContext context = new DefaultServiceContext();
        context.setLocale(new Locale("no", "NO"));
        PersistenceSessionManager persistenceSessionManager = createPersistenceSessionManager(context);

        try {
            C1DbKode c1DbKodeC1A = persistenceSessionManager.get(C1DbKodeId.C1AId);
            assertEquals(c1DbKodeC1A.getId(), C1DbKodeId.C1AId);
            assertEquals(c1DbKodeC1A.getKodelisteId(), C1DbKodeId.KODELISTE_ID);
            assertEquals(c1DbKodeC1A.getBeskrivelse(), "Kodebeskrivelse for C1A bokmål");

            C1DbKode c1DbKodeC1B = persistenceSessionManager.get(C1DbKodeId.C1BId);
            assertEquals(c1DbKodeC1B.getId(), C1DbKodeId.C1BId);
            assertEquals(c1DbKodeC1B.getKodelisteId(), C1DbKodeId.KODELISTE_ID);
            assertEquals(c1DbKodeC1B.getBeskrivelse(), "Kodebeskrivelse for C1B bokmål");
        } finally {
            persistenceSessionManager.close();
        }
    }


    public void testGetXStrDbKode_NO() {
        ServiceContext context = new DefaultServiceContext();
        context.setLocale(new Locale("no", "NO"));
        PersistenceSessionManager persistenceSessionManager = createPersistenceSessionManager(context);

        try {
            XStrDbKode xStrDbKodeA = persistenceSessionManager.get(XStrDbKodeId.AId);
            assertEquals(xStrDbKodeA.getId(), XStrDbKodeId.AId);
            assertEquals(xStrDbKodeA.getKodelisteId(), XStrDbKodeId.KODELISTE_ID);
            assertEquals(xStrDbKodeA.getBeskrivelse(), "Kodebeskrivelse for X1 bokmål");

            XStrDbKode xStrDbKodeB = persistenceSessionManager.get(XStrDbKodeId.BId);
            assertEquals(xStrDbKodeB.getId(), XStrDbKodeId.BId);
            assertEquals(xStrDbKodeB.getKodelisteId(), XStrDbKodeId.KODELISTE_ID);
            assertEquals(xStrDbKodeB.getBeskrivelse(), "Kodebeskrivelse for X2 bokmål");
        } finally {
            persistenceSessionManager.close();
        }
    }


    public void testGetKodelisteForDbKode_NO_NY() {
        ServiceContext context = new DefaultServiceContext();
        PersistenceSessionManager persistenceSessionManager = createPersistenceSessionManager(context);
        context.setLocale(new Locale("no", "NO", "NY"));

        try {
            Kodeliste kodelisteForADbKode = persistenceSessionManager.get(ADbKodeId.KODELISTE_ID);
            assertEquals(kodelisteForADbKode.getKodeIdClass(), ADbKodeId.class);
            assertEquals(kodelisteForADbKode.getBeskrivelse(), "Kodelistebeskrivelse for ADbKode nynorsk");
            assertThat(kodelisteForADbKode.getKodeIds()).contains(ADbKodeId.A1Id, ADbKodeId.A2Id);
        } finally {
            persistenceSessionManager.close();
        }
    }

    public void testGetKodelister() {
        ServiceContext context = new DefaultServiceContext();
        PersistenceSessionManager persistenceSessionManager = createPersistenceSessionManager(context);
        context.setLocale(new Locale("no", "NO", "NY"));

        try {
            KodelistePersistenceSessionSubtypeHandler kodelisteSubtypeHandler = persistenceSessionManager.getForSnapshotVersion(SnapshotVersion.CURRENT).getImplementation(KodelistePersistenceSessionSubtypeHandler.class);
            Collection<KodelisteId<?>> kodelisteIds = kodelisteSubtypeHandler.getKodelisteIds();
        } finally {
            persistenceSessionManager.close();
        }

    }

    public void testInsertDbKode() {
        ServiceContext context = new DefaultServiceContext();
        context.setLocale(new Locale("no", "NO"));
        PersistenceSessionManager persistenceSessionManager = createPersistenceSessionManager(context);

        try {
            BDbKode bDbKodeNy = new BDbKode();
            bDbKodeNy.setId(new BDbKodeId(1234L, SnapshotVersion.CURRENT));
            bDbKodeNy.setKodeverdi("AAD12");
            HashMap<String, Kode.LocalizedFields> localizedFieldsMap = new HashMap<String, Kode.LocalizedFields>();
            Kode.LocalizedFields bokmaal = new Kode.LocalizedFields();
            bokmaal.beskrivelse = "Kodebeskrivelse ting for ny kode som er inserted. BOKMÅL";
            bokmaal.navn = "1234-Bokmål";
            localizedFieldsMap.put("no_NO", bokmaal);
            bDbKodeNy.setLocalizedFieldsMap(localizedFieldsMap);
            persistenceSessionManager.beginTransaction();
            persistenceSessionManager.insert(bDbKodeNy);
            persistenceSessionManager.commit();
        } finally {
            persistenceSessionManager.close();
        }

    }

    @Test(dependsOnMethods = "testInsertDbKode")
    public void testUpdateDbKode() {
        ServiceContext context = new DefaultServiceContext();
        context.setLocale(new Locale("no", "NO"));
        PersistenceSessionManager persistenceSessionManager = createPersistenceSessionManager(context);

        try {
            BDbKode dbKode = persistenceSessionManager.get(new BDbKodeId(1234L, SnapshotVersion.CURRENT));
            Kode.LocalizedFields nynorsk = new Kode.LocalizedFields();
            nynorsk.beskrivelse = "Kodebeskrivelse ting for ny kode som er inserted. NYNORSK";
            nynorsk.navn = "1234-Nynorsk";
            dbKode.getLocalizedFieldsMap().put("no_NO_NY", nynorsk);
            persistenceSessionManager.beginTransaction();
            persistenceSessionManager.update(dbKode);
            persistenceSessionManager.commit();
        } finally {
            persistenceSessionManager.close();
        }
    }

    @Test(dependsOnMethods = "testUpdateDbKode")
    public void testDeleteDbKode() {
        ServiceContext context = new DefaultServiceContext();
        context.setLocale(new Locale("no", "NO"));
        PersistenceSessionManager persistenceSessionManager = createPersistenceSessionManager(context);

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

