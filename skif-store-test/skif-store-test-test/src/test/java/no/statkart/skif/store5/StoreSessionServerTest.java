package no.statkart.skif.store5;

import no.statkart.skif.ConfigurationConverter;
import no.statkart.skif.config.Configuration;
import no.statkart.skif.config.PropertiesConfiguration;
import no.statkart.skif.service.DefaultServiceContext;
import no.statkart.skif.service.ServiceContext;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store5.persistence.DefaultPersistenceSessionManager;
import no.statkart.skif.store5.persistence.DefaultPersistenceSessionStrategy;
import no.statkart.skif.store5.persistence.PersistenceSessionManager;
import no.statkart.skif.store5.persistence.hibernate.DefaultHibernatePersistenceSession;
import no.statkart.skif.store5.persistence.hibernate.HibernatePersistenceSessionMaster;
import no.statkart.skif.store5.persistence.hibernate.HibernateSessionFactoryManagerBundle;
import no.statkart.skif.store5.persistence.kode.DefaultKodePersistenceSession;
import no.statkart.skif.store5.persistence.kode.EnumKodeManager;
import no.statkart.skif.storetest.TestHelper5;
import no.statkart.skif.storetest.domain.demo.Foo;
import no.statkart.skif.storetest.domain.demo.FooId;
import no.statkart.skif.storetest.domain.demo.TestBubble;
import no.statkart.skif.storetest.domain.demo.TestBubbleId;
import no.statkart.skif.storetest.domain.demo.koder.ADbKode;
import no.statkart.skif.storetest.domain.demo.koder.AEnumKodeId;
import no.statkart.skif.storetest.domain.demo.koder.BEnumKodeId;
import no.statkart.skif.storetest.domain.demo.koder.CEnumKodeId;
import no.statkart.skif.storetest.util.DemoKodeMsg;
import no.statkart.skif.util.KodeMsg;
import org.hibernate.Session;
import org.testng.annotations.*;

import java.util.HashSet;
import java.util.Locale;
import java.util.Properties;
import java.util.Set;

import static no.statkart.skif.storetest.TestHelper5.createHibernateSessionFactorManagerBundle;
import static org.fest.assertions.Assertions.assertThat;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertSame;

/**
 * @author Henrik Fredholm
 */
@Test
public class StoreSessionServerTest {
    Properties hibernateProperties;
    static String T1 = "2011-10-02 08:01:00.00";
    static String T2 = "2011-10-02 08:02:00.00";
    static String T3 = "2011-10-02 08:03:00.00";
    static String T4 = "2011-10-02 08:04:00.00";
    static SnapshotVersion CURRENT = SnapshotVersion.CURRENT;
    static SnapshotVersion OLD = SnapshotVersion.OLD;
    static SnapshotVersion S1 = SnapshotVersion.createInstance(T1);
    static SnapshotVersion S2 = SnapshotVersion.createInstance(T2);
    static SnapshotVersion S3 = SnapshotVersion.createInstance(T3);
    static SnapshotVersion S4 = SnapshotVersion.createInstance(T4);

    FooId<Foo> FooId_100_CURRENT = new FooId<Foo>(100L, SnapshotVersion.CURRENT);
    FooId<Foo> FooId_100_OLD = new FooId<Foo>(100L, SnapshotVersion.OLD);
    FooId<Foo> FooId_100_S1 = new FooId<Foo>(100L, S1);
    FooId<Foo> FooId_100_S2 = new FooId<Foo>(100L, S2);
    FooId<Foo> FooId_100_S3 = new FooId<Foo>(100L, S3);
    FooId<Foo> FooId_100_S4 = new FooId<Foo>(100L, S4);

    FooId<Foo> FooId_101_CURRENT = new FooId<Foo>(101L, SnapshotVersion.CURRENT);
    FooId<Foo> FooId_101_S3 = new FooId<Foo>(101L, S3);
    FooId<Foo> FooId_101_S4 = new FooId<Foo>(101L, S4);
    FooId<Foo> FooId_101_OLD = new FooId<Foo>(101L, SnapshotVersion.OLD);

    TestBubbleId<TestBubble> TestBubbleId_101 = new TestBubbleId<TestBubble>(101);

    HibernateSessionFactoryManagerBundle sessionFactoryManagerBundle;
    PersistenceSessionManager persistenceSessionManager;

    StoreServer storeServer;

    public StoreSessionServerTest() {
        Configuration cfg = new PropertiesConfiguration("no/statkart/skif/storetest/config/persistence/skiftest-hibernate-singlevm.properties");
        hibernateProperties = ConfigurationConverter.getProperties(cfg);
    }

    @BeforeClass
    public void setUp() {
        no.statkart.skif.store5.persistence.hibernate.HibernateSessionFactoryBuilder sessionFactoryBuilder = TestHelper5.createHibernateSessionFactoryBuilderWithHistory();
        sessionFactoryBuilder.addResourceUsingRelativePath("kodeliste", ADbKode.class);
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

    @BeforeMethod
    public void createStore() {
        persistenceSessionManager = createPersistenceSessionManager();
        storeServer = new StoreServer(new StoreSessionServer(persistenceSessionManager, MemoryLockerSingleton.getInstance()));

        HibernatePersistenceSessionMaster persistenceSessionMaster = persistenceSessionManager.getForSnapshot(SnapshotVersion.CURRENT).getImplementation(HibernatePersistenceSessionMaster.class);
        try {
            Session session = persistenceSessionMaster.reserveSession();
            session.createQuery("delete from TestBubble where id>100").executeUpdate();
        } finally {
            persistenceSessionMaster.releaseSession();
        }
    }

    @AfterMethod
    public void closeStore() {
        persistenceSessionManager.close();
    }


    /**
     * Test uthenting av objekt som har historikk. Hent ut enkeltvis og via collection. Test at multiple uthentinger gir samme
     * instanser
     */
    public void testLoadObjectWithHistory() {
        Foo foo_100_CURRENT = storeServer.get(FooId_100_CURRENT);
        assertEquals(foo_100_CURRENT.getId(), FooId_100_CURRENT);
        assertSame(foo_100_CURRENT, storeServer.get(foo_100_CURRENT.getId()));

        Foo foo_100_OLD = storeServer.get(FooId_100_OLD);
        assertEquals(foo_100_OLD.getId(), FooId_100_OLD);
        assertSame(foo_100_OLD, storeServer.get(foo_100_OLD.getId()));

        Foo foo_100_S1 = storeServer.get(FooId_100_S1);
        assertEquals(foo_100_S1.getId(), FooId_100_S1);
        assertSame(foo_100_S1, storeServer.get(foo_100_S1.getId()));

        Set<FooId<?>> fooIds = new HashSet<FooId<?>>(3);
        fooIds.add(FooId_101_OLD);
        fooIds.add(FooId_101_S3);
        Set<Foo> foos = storeServer.get(fooIds);
        assertThat(foos).onProperty("id").containsOnly(FooId_101_OLD, FooId_101_S3);

        // Test at multipel uthenting gir samme objekter
        fooIds = new HashSet<FooId<?>>(3);
        fooIds.add(foo_100_CURRENT.getId());
        fooIds.add(foo_100_OLD.getId());
        fooIds.add(foo_100_S1.getId());
        foos = storeServer.get(fooIds);
        assertThat(foos).containsOnly(foo_100_CURRENT, foo_100_OLD, foo_100_S1);
        for (Foo foo : foos) {
            if (foo.getId().equals(FooId_100_CURRENT)) {
                assertThat(foo).isSameAs(foo_100_CURRENT);
            }
            if (foo.getId().equals(FooId_100_OLD)) {
                assertThat(foo).isSameAs(foo_100_OLD);
            }
            if (foo.getId().equals(FooId_100_S1)) {
                assertThat(foo).isSameAs(foo_100_S1);
            }
        }
    }

    /**
     * Tester at objekter som er hentet ut via store eller persistence manager kan registreres.
     */
    public void testRegisterObjects() {
        Foo foo_100_CURRENT = storeServer.get(FooId_100_CURRENT);
        assertSame(storeServer.register(foo_100_CURRENT), foo_100_CURRENT);

        Foo foo_100_OLD = persistenceSessionManager.get(FooId_100_OLD);
        assertSame(storeServer.register(foo_100_OLD), foo_100_OLD);
    }


    /**
     * Tester insert object
     */
    public void testInsert() {
        TestBubble testBubble_100 = new TestBubble(TestBubbleId_101);
        testBubble_100.setText("Insert 1");
        storeServer.beginTransaction();
        storeServer.insert(testBubble_100);
        storeServer.finish();
        storeServer.commit();
    }

    /**
     * Tester update object etter insert
     */
    public void testUpdate() {
        testInsert();
        TestBubble testBubble_101 = storeServer.lock(TestBubbleId_101);
        assertEquals(testBubble_101.getText(), "Insert 1");

        storeServer.beginTransaction();
        testBubble_101.setText("Update 1");

        storeServer.update(testBubble_101);
        storeServer.commit();
        assertEquals(storeServer.get(TestBubbleId_101).getText(), "Update 1");
    }

    /**
     * Tester update object etter update
     */
    public void testUpdateMultipleTimes() {
        testUpdate();
        storeServer.beginTransaction();
        TestBubble testBubble_101 = storeServer.lock(TestBubbleId_101);
        testBubble_101.setText("Update 2");
        storeServer.update(testBubble_101);
        storeServer.commit();
        testBubble_101 = storeServer.get(TestBubbleId_101);
        assertEquals(testBubble_101.getText(), "Update 2");
    }

    /**
     * Tester update object etter update
     */
    public void testUpdateMultipleInstances() {
        testInsert();
        storeServer.beginTransaction();
        TestBubble testBubble_101 = storeServer.lock(TestBubbleId_101);
        testBubble_101.setText("Update 2");
        storeServer.update(testBubble_101);

        TestBubble testBubble_101_2 = new TestBubble(TestBubbleId_101);
        testBubble_101_2.setText("Update 3");
        storeServer.update(testBubble_101_2);

        storeServer.commit();
        assertSame(storeServer.get(TestBubbleId_101), testBubble_101_2);
    }
}
