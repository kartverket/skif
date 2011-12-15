package no.statkart.skif.store3.persistence.hibernate;

import no.statkart.skif.ConfigurationConverter;
import no.statkart.skif.config.Configuration;
import no.statkart.skif.config.PropertiesConfiguration;
import no.statkart.skif.service.DefaultServiceContext;
import no.statkart.skif.service.ServiceContext;
import no.statkart.skif.store.BubbleObject;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.kodeliste.Kode;
import no.statkart.skif.store.kodeliste.KodeId;
import no.statkart.skif.store3.persistence.kode.EnumKodeManager;
import no.statkart.skif.store3.persistence.kode.KodePersistenceSession;
import no.statkart.skif.store3.persistence.kode.SnapshotManagedKodePersistenceSession;
import no.statkart.skif.storetest.domain.demo.koder.*;
import no.statkart.skif.storetest.util.DemoKodeMsg;
import no.statkart.skif.util.KodeMsg;
import org.junit.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.*;

import static no.statkart.skif.storetest.TestHelper3.createHibernateSessionFactorManagerWithMultipleSessionsWithHistory;
import static no.statkart.skif.storetest.TestHelper3.createHibernateSessionFactoryBuilderWithHistory;

/**
 * Test av {@link SnapshotManagedKodePersistenceSession}.
 *
 * @author Tor Egil R. Strand
 * @since 2.1
 */
@Test
public class SnapshotManagedKodePersistenceSessionTest {
    Properties hibernateProperties;

    HibernateSessionFactoryManager sessionFactoryManager;

    @BeforeClass
    protected void setupFactory() {
        HibernateSessionFactoryBuilder hibernateSessionFactoryBuilderWithHistory = createHibernateSessionFactoryBuilderWithHistory();
        hibernateSessionFactoryBuilderWithHistory.addResourceUsingRelativePath("kodeliste", ADbKode.class);
        sessionFactoryManager = createHibernateSessionFactorManagerWithMultipleSessionsWithHistory(hibernateSessionFactoryBuilderWithHistory, hibernateProperties);
    }

    @AfterClass
    protected void tearDownFactory() {
        sessionFactoryManager.close();
    }

    public SnapshotManagedKodePersistenceSessionTest() {
        Configuration cfg = new PropertiesConfiguration("no/statkart/skif/storetest/config/persistence/skiftest-hibernate-singlevm.properties");
        hibernateProperties = ConfigurationConverter.getProperties(cfg);
    }

    @Test
    public void testNbKode() {
        EnumKodeManager enumKodeManager = new EnumKodeManager();
        enumKodeManager.installStatic(AEnumKodeId.class);
        enumKodeManager.installStatic(BEnumKodeId.class);
        enumKodeManager.installStatic(CEnumKodeId.class);

        KodeMsg kodeMsg = new DemoKodeMsg();

        ServiceContext context = new DefaultServiceContext();
        context.setLocale(new Locale("no", "NO"));

        SnapshotManagedHibernateSession hibernateSessionManager = new SnapshotManagedHibernateSession(sessionFactoryManager);
        try {
            SnapshotManagedKodePersistenceSession snapshotManagedKodePersistenceSession = new SnapshotManagedKodePersistenceSession(hibernateSessionManager, enumKodeManager, kodeMsg, context);

            KodePersistenceSession kodePersistenceSession = null;
            try {
                kodePersistenceSession = snapshotManagedKodePersistenceSession.acquireForSnapshot(SnapshotVersion.CURRENT);

                AEnumKode aEnumKodeA = kodePersistenceSession.get(AEnumKodeId.KodeAId);
                Assert.assertEquals("Kode A", aEnumKodeA.getBeskrivelse());

                AEnumKode aEnumKodeB = kodePersistenceSession.get(AEnumKodeId.KodeBId);
                Assert.assertEquals("Kode B", aEnumKodeB.getBeskrivelse());

                ADbKode aDbKode1 = kodePersistenceSession.get(new ADbKodeId(1L, SnapshotVersion.CURRENT));
                Assert.assertEquals("Kodebeskrivelse for A1 bokmål", aDbKode1.getBeskrivelse());
            } finally {
                snapshotManagedKodePersistenceSession.releaseForSnapshot(kodePersistenceSession);
            }
        } finally {
            hibernateSessionManager.close();
        }
    }

    @Test
    public void testNyKode() {
        EnumKodeManager enumKodeManager = new EnumKodeManager();
        enumKodeManager.installStatic(AEnumKodeId.class);
        enumKodeManager.installStatic(BEnumKodeId.class);
        enumKodeManager.installStatic(CEnumKodeId.class);

        KodeMsg kodeMsg = new DemoKodeMsg();

        ServiceContext context = new DefaultServiceContext();
        context.setLocale(new Locale("no", "NO", "NY"));

        SnapshotManagedHibernateSession hibernateSessionManager = new SnapshotManagedHibernateSession(sessionFactoryManager);
        try {
            SnapshotManagedKodePersistenceSession snapshotManagedKodePersistenceSession = new SnapshotManagedKodePersistenceSession(hibernateSessionManager, enumKodeManager, kodeMsg, context);

            KodePersistenceSession kodePersistenceSession = null;
            try {
                kodePersistenceSession = snapshotManagedKodePersistenceSession.acquireForSnapshot(SnapshotVersion.CURRENT);

                AEnumKode aEnumKodeA = kodePersistenceSession.get(AEnumKodeId.KodeAId);
                Assert.assertEquals("Kode A(nynorsk)", aEnumKodeA.getBeskrivelse());

                AEnumKode aEnumKodeB = kodePersistenceSession.get(AEnumKodeId.KodeBId);
                Assert.assertEquals("Kode B(nynorsk)", aEnumKodeB.getBeskrivelse());

                ADbKode aDbKode1 = kodePersistenceSession.get(new ADbKodeId(1L, SnapshotVersion.CURRENT));
                Assert.assertEquals("Kodebeskrivelse for A1 nynorsk", aDbKode1.getBeskrivelse());
            } finally {
                snapshotManagedKodePersistenceSession.releaseForSnapshot(kodePersistenceSession);
            }
        } finally {
            hibernateSessionManager.close();
        }
    }

    @Test
    public void testUnikEnumKode() {
        EnumKodeManager enumKodeManager = new EnumKodeManager();
        enumKodeManager.installStatic(AEnumKodeId.class);

        KodeMsg kodeMsg = new DemoKodeMsg();

        ServiceContext context = new DefaultServiceContext();

        SnapshotManagedHibernateSession hibernateSessionManager = new SnapshotManagedHibernateSession(sessionFactoryManager);
        try {
            SnapshotManagedKodePersistenceSession snapshotManagedKodePersistenceSession = new SnapshotManagedKodePersistenceSession(hibernateSessionManager, enumKodeManager, kodeMsg, context);

            KodePersistenceSession kodePersistenceSession = null;
            try {
                kodePersistenceSession = snapshotManagedKodePersistenceSession.acquireForSnapshot(SnapshotVersion.CURRENT);

                AEnumKode aEnumKode1 = kodePersistenceSession.get(AEnumKodeId.KodeAId);
                AEnumKode aEnumKode2 = kodePersistenceSession.get(AEnumKodeId.KodeAId);

                Assert.assertFalse("Fikk samme kodeobjekt", aEnumKode1 == aEnumKode2);
            } finally {
                snapshotManagedKodePersistenceSession.releaseForSnapshot(kodePersistenceSession);
            }
        } finally {
            hibernateSessionManager.close();
        }
    }

    @Test
    public void testMixed() {
        EnumKodeManager enumKodeManager = new EnumKodeManager();
        enumKodeManager.installStatic(AEnumKodeId.class);
        enumKodeManager.installStatic(BEnumKodeId.class);
        enumKodeManager.installStatic(CEnumKodeId.class);

        KodeMsg kodeMsg = new DemoKodeMsg();

        ServiceContext context = new DefaultServiceContext();
        context.setLocale(new Locale("no", "NO"));

        SnapshotManagedHibernateSession hibernateSessionManager = new SnapshotManagedHibernateSession(sessionFactoryManager);
        try {
            SnapshotManagedKodePersistenceSession snapshotManagedKodePersistenceSession = new SnapshotManagedKodePersistenceSession(hibernateSessionManager, enumKodeManager, kodeMsg, context);

            KodePersistenceSession kodePersistenceSession = null;
            try {
                kodePersistenceSession = snapshotManagedKodePersistenceSession.acquireForSnapshot(SnapshotVersion.CURRENT);

                List<KodeId<? extends Kode>> ids = Arrays.<KodeId<? extends Kode>>asList(AEnumKodeId.KodeAId, new ADbKodeId(1L, SnapshotVersion.CURRENT));
                Collection<? extends Kode> koder = kodePersistenceSession.get(ids);

                boolean fantEnumKode = false;
                boolean fantDbKode = false;
                for (Kode kode : koder) {
                    if (kode instanceof AEnumKode) {
                        fantEnumKode = true;
                    } else if (kode instanceof ADbKode) {
                        fantDbKode = true;
                    }
                }
                Assert.assertTrue("Fant ikke enumkode", fantEnumKode);
                Assert.assertTrue("Fant ikke databasekode", fantDbKode);
            } finally {
                snapshotManagedKodePersistenceSession.releaseForSnapshot(kodePersistenceSession);
            }
        } finally {
            hibernateSessionManager.close();
        }
    }
}
