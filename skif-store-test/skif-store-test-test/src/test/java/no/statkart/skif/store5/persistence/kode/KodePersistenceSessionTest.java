package no.statkart.skif.store5.persistence.kode;

import no.statkart.skif.ConfigurationConverter;
import no.statkart.skif.config.Configuration;
import no.statkart.skif.config.PropertiesConfiguration;
import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.service.DefaultServiceContext;
import no.statkart.skif.service.ServiceContext;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.kodeliste.Kode;
import no.statkart.skif.store.kodeliste.KodeId;
import no.statkart.skif.store5.persistence.DefaultPersistenceSessionManager;
import no.statkart.skif.store5.persistence.DefaultPersistenceSessionStrategy;
import no.statkart.skif.store5.persistence.PersistenceSessionManager;
import no.statkart.skif.store5.persistence.hibernate.DefaultHibernatePersistenceSession;
import no.statkart.skif.store5.persistence.hibernate.HibernateSessionFactoryBuilder;
import no.statkart.skif.store5.persistence.hibernate.HibernateSessionFactoryManager;
import no.statkart.skif.store5.persistence.hibernate.HibernateSessionFactoryManagerBundle;
import no.statkart.skif.storetest.domain.demo.koder.*;
import no.statkart.skif.storetest.util.DemoKodeMsg;
import no.statkart.skif.util.KodeMsg;
import org.junit.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.*;

import static no.statkart.skif.storetest.TestHelper.createHibernateSessionFactorManagerBundle;
import static no.statkart.skif.storetest.TestHelper.createHibernateSessionFactoryBuilderWithHistory;
import static org.fest.assertions.Assertions.assertThat;

/**
 * Test av {@link KodePersistenceSession}.
 *
 * @author Henrik Fredholm
 * @since 2.1
 */
@Test
public class KodePersistenceSessionTest {
    Properties hibernateProperties;

    HibernateSessionFactoryManager sessionFactoryManager;
    HibernateSessionFactoryManagerBundle sessionFactoryManagerBundle;

    public KodePersistenceSessionTest() {
        Configuration cfg = new PropertiesConfiguration("no/statkart/skif/storetest/config/persistence/skiftest-hibernate-singlevm.properties");
        hibernateProperties = ConfigurationConverter.getProperties(cfg);
    }

    @BeforeClass
    public void setUp() {
        HibernateSessionFactoryBuilder sessionFactoryBuilder = createHibernateSessionFactoryBuilderWithHistory();
//        sessionFactoryBuilder.addResourceUsingRelativePath("kodeliste", ADbKode.class);
        sessionFactoryBuilder.addResource(ADbKode.class);
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
        EnumKodeManager enumKodeManager = new EnumKodeManager();
        enumKodeManager.installStatic(AEnumKodeId.class);
        enumKodeManager.installStatic(BEnumKodeId.class);
        enumKodeManager.installStatic(CEnumKodeId.class);

        KodeMsg kodeMsg = new DemoKodeMsg();

        DefaultHibernatePersistenceSession masterCurrent = new DefaultHibernatePersistenceSession(
                sessionFactoryManagerBundle.getBundle().get(0)
        );
        DefaultHibernatePersistenceSession masterOld = new DefaultHibernatePersistenceSession(
                sessionFactoryManagerBundle.getBundle().get(1)
        );

        return new DefaultPersistenceSessionManager(
                new DefaultPersistenceSessionStrategy(
                        masterCurrent,
                        new DefaultKodePersistenceSession(masterCurrent, enumKodeManager, kodeMsg, context)
                ),
                new DefaultPersistenceSessionStrategy(
                        masterOld,
                        new DefaultKodePersistenceSession(masterOld, enumKodeManager, kodeMsg, context)
                )
        );
    }

    @Test
    public void testNbKode() {
        ServiceContext context = new DefaultServiceContext();
        context.setLocale(new Locale("no", "NO"));
        PersistenceSessionManager persistenceSessionManager = createPersistenceSessionManager(context);

        try {
            AEnumKode aEnumKodeA = persistenceSessionManager.get(AEnumKodeId.KodeAId);
            Assert.assertEquals("Kode A", aEnumKodeA.getBeskrivelse());

            AEnumKode aEnumKodeB = persistenceSessionManager.get(AEnumKodeId.KodeBId);
            Assert.assertEquals("Kode B", aEnumKodeB.getBeskrivelse());

            ADbKode aDbKode1 = persistenceSessionManager.get(new ADbKodeId(1L, SnapshotVersion.CURRENT));
            Assert.assertEquals("Kodebeskrivelse for A1 bokmål", aDbKode1.getBeskrivelse());
        } finally {
            persistenceSessionManager.close();
        }
    }


    @Test
    public void testNyKode() {
        ServiceContext context = new DefaultServiceContext();
        context.setLocale(new Locale("no", "NO", "NY"));
        PersistenceSessionManager persistenceSessionManager = createPersistenceSessionManager(context);

        try {
            AEnumKode aEnumKodeA = persistenceSessionManager.get(AEnumKodeId.KodeAId);
            Assert.assertEquals("Kode A(nynorsk)", aEnumKodeA.getBeskrivelse());

            AEnumKode aEnumKodeB = persistenceSessionManager.get(AEnumKodeId.KodeBId);
            Assert.assertEquals("Kode B(nynorsk)", aEnumKodeB.getBeskrivelse());

            ADbKode aDbKode1 = persistenceSessionManager.get(new ADbKodeId(1L, SnapshotVersion.CURRENT));
            Assert.assertEquals("Kodebeskrivelse for A1 nynorsk", aDbKode1.getBeskrivelse());
        } finally {
            persistenceSessionManager.close();
        }
    }

    @Test
    public void testUnikEnumKode() {
        PersistenceSessionManager persistenceSessionManager = createPersistenceSessionManager();

        try {
            // NB: Det er ikke noe krav at de skal være forskjellige, det er bare slik at implementasjonen pt er.
            AEnumKode aEnumKode1 = persistenceSessionManager.get(AEnumKodeId.KodeAId);
            AEnumKode aEnumKode2 = persistenceSessionManager.get(AEnumKodeId.KodeAId);

            Assert.assertFalse("Fikk samme kodeobjekt", aEnumKode1 == aEnumKode2);
        } finally {
            persistenceSessionManager.close();
        }
    }

    @Test
    public void testMixed() {
        PersistenceSessionManager persistenceSessionManager = createPersistenceSessionManager();
        try {
            List<KodeId<? extends Kode>> ids = Arrays.<KodeId<? extends Kode>>asList(AEnumKodeId.KodeAId, new ADbKodeId(1L, SnapshotVersion.CURRENT), new ADbKodeId(2L, SnapshotVersion.CURRENT));
            Collection<? extends Kode> koder = persistenceSessionManager.get(ids);
            assertThat(koder).onProperty("id").containsOnly(AEnumKodeId.KodeAId, ADbKodeId.A1Id, ADbKodeId.A2Id);


        } finally {
            persistenceSessionManager.close();
        }
    }

    @Test
    public void testGetImplementation() {
        PersistenceSessionManager persistenceSessionManager = createPersistenceSessionManager();

        KodePersistenceSession implementation = persistenceSessionManager.getForSnapshotVersion(SnapshotVersion.CURRENT).getImplementation(KodePersistenceSession.class);
        try {
            AEnumKode aEnumKodeA = implementation.get(AEnumKodeId.KodeAId);
            Assert.assertEquals("Kode A", aEnumKodeA.getBeskrivelse());
        } finally {
            persistenceSessionManager.close();
        }
    }


    @Test(expectedExceptions = ImplementationException.class)
    public void testOldEnumKode() {
        PersistenceSessionManager persistenceSessionManager = createPersistenceSessionManager();

        try {
            AEnumKode aEnumKodeA = persistenceSessionManager.get(AEnumKodeId.KodeAId.asReplicaVersion(SnapshotVersion.OLD));
            Assert.assertEquals("Kode A", aEnumKodeA.getBeskrivelse());
        } finally {
            persistenceSessionManager.close();
        }
    }

    public void testOldDbKode() {
        PersistenceSessionManager persistenceSessionManager = createPersistenceSessionManager();

        try {
            ADbKode aDbKode1 = persistenceSessionManager.get(new ADbKodeId(1L, SnapshotVersion.OLD));
            Assert.assertEquals(aDbKode1.getId().getSnapshotVersion(), SnapshotVersion.OLD);
            Assert.assertEquals("Kodebeskrivelse for A1 bokmål", aDbKode1.getBeskrivelse());
        } finally {
            persistenceSessionManager.close();
        }
    }

    @Test
    public void testMixedCurrentAndOld() {
        PersistenceSessionManager persistenceSessionManager = createPersistenceSessionManager();
        try {
            List<KodeId<? extends Kode>> ids = Arrays.<KodeId<? extends Kode>>asList(
                    AEnumKodeId.KodeAId, new ADbKodeId(1L, SnapshotVersion.CURRENT), new ADbKodeId(2L, SnapshotVersion.CURRENT),
                    new ADbKodeId(1L, SnapshotVersion.OLD), new ADbKodeId(2L, SnapshotVersion.OLD));
            Collection<? extends Kode> koder = persistenceSessionManager.get(ids);
            assertThat(koder).onProperty("id").containsOnly(AEnumKodeId.KodeAId, ADbKodeId.A1Id, ADbKodeId.A2Id, ADbKodeId.A1Id.asReplicaVersion(SnapshotVersion.OLD), ADbKodeId.A2Id.asReplicaVersion(SnapshotVersion.OLD ));
        } finally {
            persistenceSessionManager.close();
        }
    }

}

