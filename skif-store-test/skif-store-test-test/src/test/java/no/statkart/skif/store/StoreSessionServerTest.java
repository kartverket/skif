package no.statkart.skif.store;

import com.google.inject.util.Providers;
import no.statkart.skif.ConfigurationConverter;
import no.statkart.skif.config.Configuration;
import no.statkart.skif.config.PropertiesConfiguration;
import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.exception.ObjectNotFoundException;
import no.statkart.skif.persistence.VersionFinder;
import no.statkart.skif.service.DefaultServiceContext;
import no.statkart.skif.service.ServiceContext;
import no.statkart.skif.store.persistence.DefaultPersistenceSessionManager;
import no.statkart.skif.store.persistence.DefaultPersistenceSessionStrategy;
import no.statkart.skif.store.persistence.PersistenceSessionForSnapshot;
import no.statkart.skif.store.persistence.PersistenceSessionManager;
import no.statkart.skif.store.persistence.hibernate.*;
import no.statkart.skif.store.persistence.hibernate.HibernatePersistenceSessionMasterImpl;
import no.statkart.skif.store.persistence.hibernate.HibernatePersistenceSessionMaster;
import no.statkart.skif.store.persistence.hibernate.HibernateSessionFactoryManagerBundle;
import no.statkart.skif.store.persistence.kode.DefaultKodePersistenceSession;
import no.statkart.skif.store.persistence.kode.EnumKodeManager;
import no.statkart.skif.storetest.TestHelper;
import no.statkart.skif.storetest.domain.demo.*;
import no.statkart.skif.storetest.domain.demo.koder.ADbKode;
import no.statkart.skif.storetest.domain.demo.koder.AEnumKodeId;
import no.statkart.skif.storetest.domain.demo.koder.BEnumKodeId;
import no.statkart.skif.storetest.domain.demo.koder.CEnumKodeId;
import no.statkart.skif.storetest.util.DemoKodeMsg;
import no.statkart.skif.util.CopyHelper;
import no.statkart.skif.util.KodeMsg;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.testng.annotations.*;

import java.util.*;

import static no.statkart.skif.storetest.TestHelper.countInDatabase;
import static no.statkart.skif.storetest.TestHelper.createHibernateSessionFactorManagerBundle;
import static org.fest.assertions.Assertions.assertThat;
import static org.testng.Assert.*;
import static org.testng.FileAssert.fail;

/**
 * @author Henrik Fredholm
 * @author Jan Holmen
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

    TestBubbleId<TestBubble> TestBubbleId_1 = new TestBubbleId<TestBubble>(1);
    TestBubbleId<TestBubble> TestBubbleId_101 = new TestBubbleId<TestBubble>(101);
    ParrentBubbleId<ParrentBubble> ParrentBubbleId_101 = new ParrentBubbleId<ParrentBubble>(101);
    ChildBubbleId<ChildBubble> ChildBubbleId_101 = new ChildBubbleId<ChildBubble>(101);
    ChildBubbleId<ChildBubble> ChildBubbleId_102 = new ChildBubbleId<ChildBubble>(102);
    Long childForParrentId_102 = (long) 102;

    HibernateSessionFactoryManagerBundle sessionFactoryManagerBundle;
    PersistenceSessionManager persistenceSessionManager;
    PersistenceSessionForSnapshot persistenceSessionForSnapshot;

    StoreServer storeServer;

    public StoreSessionServerTest() {
        Configuration cfg = new PropertiesConfiguration("no/statkart/skif/storetest/config/persistence/skiftest-hibernate-singlevm.properties");
        hibernateProperties = ConfigurationConverter.getProperties(cfg);
    }

    @BeforeClass
    public void setUp() {
        HibernateSessionFactoryBuilder sessionFactoryBuilder = TestHelper.createHibernateSessionFactoryBuilderWithHistory();
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

        HibernatePersistenceSessionMasterImpl masterCurrent = new HibernatePersistenceSessionMasterImpl(
                sessionFactoryManagerBundle.getBundle().get(0)
        );
        HibernatePersistenceSessionMasterImpl masterOld = new HibernatePersistenceSessionMasterImpl(
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
        persistenceSessionForSnapshot = persistenceSessionManager.getForSnapshotVersion(SnapshotVersion.CURRENT);
        storeServer = new StoreServer(new StoreSessionServer(persistenceSessionManager, Providers.<VersionFinder>of(null), MemoryLockerSingleton5.getInstance()));

        HibernatePersistenceSessionMaster persistenceSessionMaster = persistenceSessionManager.getForSnapshotVersion(SnapshotVersion.CURRENT).getImplementation(HibernatePersistenceSessionMaster.class);
        try {
            Session session = persistenceSessionMaster.reserveSession();
            Transaction transaction = session.beginTransaction();
            session.createQuery("delete from TestBubble where id>100").executeUpdate();
            session.createQuery("delete from ChildForParrent where id>100").executeUpdate();
            session.createQuery("delete from ParrentBubble where id>100").executeUpdate();
            session.createQuery("delete from ChildBubble where id>100").executeUpdate();
            transaction.commit();
        } finally {
            persistenceSessionMaster.releaseSession();
        }
    }

    @AfterMethod
    public void closeStore() {
        persistenceSessionManager.close();
    }

    private void assertNotFound(Store store, TestBubbleId<TestBubble> bubbleId) {
        try {
            store.get(bubbleId);
            fail("Objekt skal ikke være i store");
        } catch (ObjectNotFoundException e) {
        }
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
     * Tester insert object
     */
    public void testInsert() {
        TestBubble testBubble_100 = new TestBubble(TestBubbleId_101);
        testBubble_100.setText("Insert 1");
        storeServer.beginTransaction();
        storeServer.insert(testBubble_100);
        storeServer.commitTransaction();
        assertEquals(countInDatabase(persistenceSessionForSnapshot, TestBubbleId_101), 1);
    }


    /**
     * Tester lock object
     */
    @Test(groups = "broken")
    public void testLockObject() {
        testInsert();
        TestBubble testBubble_101 = storeServer.lock(TestBubbleId_101);
        assertSame(storeServer.lock(TestBubbleId_101), testBubble_101);

        storeServer.beginTransaction();
        storeServer.commitTransaction();

        // Denne skal ikke gi en ekstra select statement
        assertSame(storeServer.get(TestBubbleId_101), testBubble_101);
        // Denne skal gi en ekstra select statement for refresh
        assertSame(storeServer.lock(TestBubbleId_101), testBubble_101);
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
        storeServer.commitTransaction();
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
        storeServer.commitTransaction();
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

        storeServer.commitTransaction();
        assertSame(storeServer.get(TestBubbleId_101), testBubble_101_2);
    }

    public void testInsertHirarki() {
        ParrentBubble testBubble_101 = new ParrentBubble(ParrentBubbleId_101);
        testBubble_101.setText("Insert parrent 1");

        ChildBubble test2Bubble_101 = new ChildBubble(ChildBubbleId_101);
        test2Bubble_101.setText("Insert child 1");
        testBubble_101.addChild(ChildBubbleId_101,(long)101);

        ChildBubble test2Bubble_102 = new ChildBubble(ChildBubbleId_102);
        test2Bubble_102.setText("Insert child 2");
        testBubble_101.addChild(ChildBubbleId_102,(long)102);
        test2Bubble_102.setTestBubbleId(new TestBubbleId<TestBubble>(2));

        storeServer.beginTransaction();
        storeServer.insert(test2Bubble_101);
        storeServer.insert(test2Bubble_102);
        storeServer.insert(testBubble_101);

        ChildForParrent childForParrent = testBubble_101.getChildForParrent(ChildBubbleId_101);
        ParrentBubble parrentBubble = childForParrent.getParrentBubble();
        assertNotNull(parrentBubble);
        storeServer.commitTransaction();
    }

//    /**
//     * Dennne kan kjøres manuelt. Det er sjekket at ingen ting kommer i databasen hvis en exception kastes.
//     */
//    @Test(invocationCount = 0)
//    public void testInsertHirarki_Exception() {
//        ParrentBubble testBubble_101 = new ParrentBubble(ParrentBubbleId_101);
//        testBubble_101.setText("Insert parrent 1");
//
//        ChildBubble test2Bubble_101 = new ChildBubble(ChildBubbleId_101);
//        test2Bubble_101.setText("Insert child 1");
//        testBubble_101.addChild(ChildBubbleId_101,(long)101);
//
//        ChildBubble test2Bubble_102 = new ChildBubble(ChildBubbleId_102);
//        test2Bubble_102.setText("Insert child 2");
//        testBubble_101.addChild(ChildBubbleId_102,(long)102);
//        test2Bubble_102.setTestBubbleId(new TestBubbleId<TestBubble>(2));
//
//        storeServer.beginTransaction();
//        storeServer.insert(test2Bubble_101);
//        storeServer.insert(test2Bubble_102);
//        storeServer.insert(testBubble_101);
//
//        ChildForParrent childForParrent = testBubble_101.getChildForParrent(ChildBubbleId_101);
//        ParrentBubble parrentBubble = childForParrent.getParrentBubble();
//        assertNotNull(parrentBubble);
//         if(1==1)throw new ImplementationException("test");
//        storeServer.finish();
//        storeServer.commit();
//    }


    /**
     * Tester delete object
     */
    public void testDeleteObject() {
        testInsert();
        storeServer.beginTransaction();
        TestBubble testBubble_101 = storeServer.lock(TestBubbleId_101);
        storeServer.delete(testBubble_101);
        storeServer.commitTransaction();
        assertEquals(countInDatabase(persistenceSessionForSnapshot, TestBubbleId_101), 0);
    }

    /**
     * Tester delete object via annen instans
     */
    public void testDeleteObjectAnnenInstans() {
        testInsert();
        storeServer.beginTransaction();
        TestBubble testBubble_101 = storeServer.lock(TestBubbleId_101);
        TestBubble copy = CopyHelper.copy(testBubble_101);
        storeServer.delete(copy);
        assertSame(storeServer.get(TestBubbleId_101), copy);
        storeServer.commitTransaction();
        assertEquals(countInDatabase(persistenceSessionForSnapshot, TestBubbleId_101), 0);
    }

    /**
     * Tester insert delete insert object via annen instans som er endret
     */
    public void testDeleteInsertObject() {
        testInsert();
        storeServer.beginTransaction();
        TestBubble testBubble_101 = storeServer.lock(TestBubbleId_101);
        storeServer.delete(testBubble_101);
        storeServer.insert(testBubble_101);
        storeServer.commitTransaction();
        assertEquals(countInDatabase(persistenceSessionForSnapshot, TestBubbleId_101), 1);
    }

    /**
     * Tester delete insert update cycle
     */
    public void testDeleteInsertUpdateObject() {
        testInsert();
        storeServer.beginTransaction();
        TestBubble testBubble_101 = storeServer.lock(TestBubbleId_101);
        storeServer.delete(testBubble_101);
        storeServer.insert(testBubble_101);
        storeServer.update(testBubble_101);
        storeServer.delete(testBubble_101);
        storeServer.insert(testBubble_101);
        storeServer.commitTransaction();
        assertEquals(countInDatabase(persistenceSessionForSnapshot, TestBubbleId_101), 1);
    }

    public void testNonLoadedEvictObject() {
        assertTrue(storeServer.evict(TestBubbleId_1));
    }

    public void testEvictLoadedObject() {
        TestBubble testBubble1 = storeServer.get(TestBubbleId_1);
        assertTrue(storeServer.evict(new TestBubbleId<TestBubble>(1L)));
        TestBubble testBubble2 = storeServer.get(TestBubbleId_1);
        assertEquals(testBubble1, testBubble2);
        assertNotSame(testBubble1, testBubble2);
    }

    public void testEvictLockedObject() {
        TestBubble testBubble1 = storeServer.lock(TestBubbleId_1);
        assertTrue(storeServer.isLocked(new TestBubbleId<TestBubble>(1L)));
        assertTrue(storeServer.evict(new TestBubbleId<TestBubble>(1L)));
        assertTrue(storeServer.isLocked(new TestBubbleId<TestBubble>(1L)));
        TestBubble testBubble2 = storeServer.get(TestBubbleId_1);
        assertEquals(testBubble1, testBubble2);
        assertNotSame(testBubble1, testBubble2);
    }

    public void testEvictInsertedObject() {
        TestBubble testBubble_101 = new TestBubble(TestBubbleId_101);
        testBubble_101.setText("Insert 1");
        storeServer.beginTransaction();
        storeServer.insert(testBubble_101);
        assertFalse(storeServer.evict(TestBubbleId_101));
        assertTrue(storeServer.isLocked(TestBubbleId_101));
        storeServer.finish();
        storeServer.commitTransaction();
        assertEquals(countInDatabase(persistenceSessionForSnapshot, TestBubbleId_101), 1);
        assertFalse(storeServer.isLocked(TestBubbleId_101));
    }

    public void testEvictUpdatedObject() {
        testInsert();
        storeServer.beginTransaction();
        TestBubble testBubble_101 = storeServer.lock(TestBubbleId_101);
        testBubble_101.setText("Updated 101");
        storeServer.update(testBubble_101);
        assertFalse(storeServer.evict(TestBubbleId_101));
        assertTrue(storeServer.isLocked(TestBubbleId_101));
        storeServer.finish();
        storeServer.commitTransaction();
        assertEquals(countInDatabase(persistenceSessionForSnapshot, TestBubbleId_101), 1);
        assertFalse(storeServer.isLocked(TestBubbleId_101));
    }

    public void testEvictBeforeUpdateObject() {
        testInsert();
        storeServer.beginTransaction();
        TestBubble testBubble_101 = storeServer.lock(TestBubbleId_101);
        testBubble_101.setText("Updated 101");
        assertTrue(storeServer.evict(TestBubbleId_101));
        storeServer.update(testBubble_101);
        assertTrue(storeServer.isLocked(TestBubbleId_101));
        storeServer.finish();
        storeServer.commitTransaction();
        assertEquals(countInDatabase(persistenceSessionForSnapshot, TestBubbleId_101), 1);
        assertFalse(storeServer.isLocked(TestBubbleId_101));
    }

    public void testEvictDeletedObject() {
        testInsert();
        storeServer.beginTransaction();
        TestBubble testBubble_101 = storeServer.lock(TestBubbleId_101);
        storeServer.delete(testBubble_101);
        assertFalse(storeServer.evict(TestBubbleId_101));
        assertTrue(storeServer.isLocked(TestBubbleId_101));
        storeServer.finish();
        storeServer.commitTransaction();
        assertEquals(countInDatabase(persistenceSessionForSnapshot, TestBubbleId_101), 0);
        assertFalse(storeServer.isLocked(TestBubbleId_101));
    }

    public void testEvictBeforeDeleteObject() {
        testInsert();
        storeServer.beginTransaction();
        TestBubble testBubble_101 = storeServer.lock(TestBubbleId_101);
        assertTrue(storeServer.evict(TestBubbleId_101));
        storeServer.delete(testBubble_101);
        assertTrue(storeServer.isLocked(TestBubbleId_101));
        storeServer.finish();
        storeServer.commitTransaction();
        assertEquals(countInDatabase(persistenceSessionForSnapshot, TestBubbleId_101), 0);
        assertFalse(storeServer.isLocked(TestBubbleId_101));
    }

    public void testUnlockObject() {
        testInsert();
        assertFalse(storeServer.isLocked(TestBubbleId_101));
        storeServer.lock(TestBubbleId_101);
        assertTrue(storeServer.isLocked(TestBubbleId_101));
        storeServer.unlock(TestBubbleId_101);
        assertFalse(storeServer.isLocked(TestBubbleId_101));
        storeServer.lock(TestBubbleId_101);
        assertTrue(storeServer.isLocked(TestBubbleId_101));
    }


    @Test(expectedExceptions = ImplementationException.class)
    public void testUnlockInsertedObject() {
        TestBubble testBubble_100 = new TestBubble(TestBubbleId_101);
        testBubble_100.setText("Insert 1");
        storeServer.beginTransaction();
        storeServer.insert(testBubble_100);
        storeServer.unlock(TestBubbleId_101);
    }

    @Test(expectedExceptions = ImplementationException.class)
    public void testUnlockUpdatedObject() {
        testInsert();
        storeServer.beginTransaction();
        TestBubble testBubble_101 = storeServer.lock(TestBubbleId_101);
        assertEquals(testBubble_101.getText(), "Insert 1");
        testBubble_101.setText("Update 1");
        storeServer.update(testBubble_101);
        storeServer.unlock(TestBubbleId_101);
    }

    @Test(expectedExceptions = ImplementationException.class)
    public void testUnlockDeletedObject() {
        testInsert();
        storeServer.beginTransaction();
        TestBubble testBubble_101 = storeServer.lock(TestBubbleId_101);
        storeServer.delete(testBubble_101);
        storeServer.unlock(TestBubbleId_101);
    }

    /**
     * Test finder som laster inn objekt i hibernate via query. Finderen returnerer id og ikke selve objektet slik at store har mulighet
     * for å returnere en filtrert eller oppdatert instans.
     *
     * @return
     */
    private TestBubbleId testBubbleIdFinder() {
        try {
            Session hibernateSession = persistenceSessionForSnapshot.getImplementation(HibernatePersistenceSessionMaster.class).reserveSession();
            TestBubble testBubble = (TestBubble) hibernateSession.createQuery("from TestBubble where id=:id").setLong("id", TestBubbleId_1.getValue()).uniqueResult();
            return testBubble.getId();

        } finally {
            persistenceSessionForSnapshot.getImplementation(HibernatePersistenceSessionMaster.class).releaseSession();
        }
    }

    public void testLoadObjectViaFinder() {
        TestBubbleId id = testBubbleIdFinder();
        // Dette kallet skal ikke gjøre select kall mot databasen da objekt allerede er lastet via finder
        BubbleObject bubbleObject = storeServer.get(id);
    }


    public void testRollbackTransaction() {
        TestBubble testBubble_101 = new TestBubble(TestBubbleId_101);
        testBubble_101.setText("Insert 1");
        storeServer.beginTransaction();
        storeServer.insert(testBubble_101);
        storeServer.rollbackTransaction();
        assertEquals(countInDatabase(persistenceSessionForSnapshot, TestBubbleId_101), 0);

        try {
            storeServer.get(TestBubbleId_101);
            fail("Objekt skal ikke være igjen i store etter rollback");
        } catch (ObjectNotFoundException e) {
        }

        storeServer.beginTransaction();
        storeServer.insert(testBubble_101);
        storeServer.commitTransaction();
        assertEquals(countInDatabase(persistenceSessionForSnapshot, TestBubbleId_101), 1);

    }

    public void testBeginEndMultiLevelEmptyUnitOfWorks() {
        storeServer.beginTransaction();
        storeServer.beginUnitOfWork();
        storeServer.beginUnitOfWork();
        storeServer.beginUnitOfWork();
        storeServer.commitUnitOfWork();
        storeServer.commitUnitOfWork();
        storeServer.commitUnitOfWork();
        storeServer.commitTransaction();
    }

    public void testInsertViaUOW() {
        storeServer.beginTransaction();
        storeServer.beginUnitOfWork();
        TestBubble testBubble_100 = new TestBubble(TestBubbleId_101);
        testBubble_100.setText("Insert 1");
        storeServer.insert(testBubble_100);
        storeServer.commitUnitOfWork();
        storeServer.commitTransaction();
        assertEquals(countInDatabase(persistenceSessionForSnapshot, TestBubbleId_101), 1);
    }

    public void testInsertViaUOW_Abort() {
        storeServer.beginTransaction();
        storeServer.beginUnitOfWork();
        TestBubble testBubble_100 = new TestBubble(TestBubbleId_101);
        testBubble_100.setText("Insert 1");
        storeServer.insert(testBubble_100);
        storeServer.abortUnitOfWork();
        assertNotFound(storeServer, TestBubbleId_101);
        storeServer.commitTransaction();
        assertEquals(countInDatabase(persistenceSessionForSnapshot, TestBubbleId_101), 0);
    }

    public void testUpdateViaUOW() {
        testInsert();
        storeServer.beginTransaction();

        storeServer.beginUnitOfWork();
        TestBubble testBubble_101 = storeServer.lock(TestBubbleId_101);
        assertEquals(testBubble_101.getText(), "Insert 1");
        testBubble_101.setText("Update 1");
        storeServer.update(testBubble_101);
        assertEquals(storeServer.get(TestBubbleId_101).getText(), "Update 1");
        storeServer.commitUnitOfWork();

        storeServer.commitTransaction();
        storeServer.evict(testBubble_101.getId());
        assertEquals(storeServer.get(TestBubbleId_101).getText(), "Update 1");
    }

    public void testUpdateViaUOW_Abort() {
        testInsert();
        storeServer.beginTransaction();

        storeServer.beginUnitOfWork();
        TestBubble testBubble_101 = storeServer.lock(TestBubbleId_101);
        assertEquals(testBubble_101.getText(), "Insert 1");
        testBubble_101.setText("Update 1");
        storeServer.update(testBubble_101);
        assertEquals(storeServer.get(TestBubbleId_101).getText(), "Update 1");
        storeServer.abortUnitOfWork();
        assertEquals(storeServer.get(TestBubbleId_101).getText(), "Insert 1");
        storeServer.commitTransaction();
        storeServer.evict(testBubble_101.getId());
        assertEquals(storeServer.get(TestBubbleId_101).getText(), "Insert 1");
    }

    public void testDeleteViaUOW() {
        testInsert();
        storeServer.beginTransaction();

        storeServer.beginUnitOfWork();
        TestBubble testBubble_101 = storeServer.lock(TestBubbleId_101);
        storeServer.delete(testBubble_101);
        storeServer.commitUnitOfWork();

        storeServer.commitTransaction();
        assertEquals(countInDatabase(persistenceSessionForSnapshot, TestBubbleId_101), 0);
    }

    public void testDeleteViaUOW_Abort() {
        testInsert();
        storeServer.beginTransaction();

        storeServer.beginUnitOfWork();
        TestBubble testBubble_101 = storeServer.lock(TestBubbleId_101);
        testBubble_101.setText("Deleted");
        storeServer.delete(testBubble_101);
        storeServer.abortUnitOfWork();
        assertEquals(storeServer.get(TestBubbleId_101).getText(), "Insert 1");

        storeServer.commitTransaction();
        assertEquals(countInDatabase(persistenceSessionForSnapshot, TestBubbleId_101), 1);
    }

    public void testInsertDeleteObjectInSameUnitOfWork() {

        storeServer.beginTransaction();
        storeServer.beginUnitOfWork();
        TestBubble testBubble1 = new TestBubble(TestBubbleId_101, "TestBubble 101");
        storeServer.insert(testBubble1);
        storeServer.delete(testBubble1);
        storeServer.commitUnitOfWork();
        storeServer.commitTransaction();
        assertEquals(countInDatabase(persistenceSessionForSnapshot, TestBubbleId_101), 0);
        assertNotFound(storeServer, TestBubbleId_101);
    }

    public void testInsertDeleteObjectInSameUnitOfWork_Abort() {

        storeServer.beginTransaction();
        storeServer.beginUnitOfWork();
        TestBubble testBubble1 = new TestBubble(TestBubbleId_101, "TestBubble 101");
        storeServer.insert(testBubble1);
        storeServer.delete(testBubble1);
        storeServer.abortUnitOfWork();
        assertNotFound(storeServer, TestBubbleId_101);
        storeServer.commitTransaction();
        assertEquals(countInDatabase(persistenceSessionForSnapshot, TestBubbleId_101), 0);
        assertNotFound(storeServer, TestBubbleId_101);
    }


    public void testInsertDeleteObjectViaNestedUnitOfWork() {
        storeServer.beginTransaction();
        storeServer.beginUnitOfWork();
        TestBubbleId<TestBubble> TestBubbleId_101_CURRENT = new TestBubbleId<TestBubble>(101L);
        TestBubble testBubble1 = new TestBubble(TestBubbleId_101_CURRENT, "TestBubble 101");
        storeServer.insert(testBubble1);
        storeServer.beginUnitOfWork();
        TestBubble copy = CopyHelper.copy(testBubble1);
        storeServer.delete(copy);
        storeServer.commitUnitOfWork();
        assertSame(storeServer.get(TestBubbleId_101_CURRENT), copy);
        storeServer.commitUnitOfWork();
        assertNotFound(storeServer, TestBubbleId_101_CURRENT);
        storeServer.commitTransaction();
        assertEquals(countInDatabase(persistenceSessionForSnapshot, TestBubbleId_101), 0);
    }

    public void testInsertDeleteObjectViaNestedUnitOfWork_AbortInner() {
        storeServer.beginTransaction();
        storeServer.beginUnitOfWork();
        TestBubble testBubble1 = new TestBubble(TestBubbleId_101, "TestBubble 101");
        storeServer.insert(testBubble1);
        storeServer.beginUnitOfWork();
        TestBubble copy = CopyHelper.copy(testBubble1);
        storeServer.delete(copy);
        storeServer.abortUnitOfWork();
        assertSame(storeServer.get(TestBubbleId_101), testBubble1);
        storeServer.commitUnitOfWork();
        assertEquals(storeServer.get(TestBubbleId_101).getText(), "TestBubble 101");
        storeServer.commitTransaction();
        assertEquals(countInDatabase(persistenceSessionForSnapshot, TestBubbleId_101), 1);
    }

    public void testInsertDeleteObjectViaNestedUnitOfWork_AbortOuter() {
        storeServer.beginTransaction();
        storeServer.beginUnitOfWork();
        TestBubble testBubble1 = new TestBubble(TestBubbleId_101, "TestBubble 101");
        storeServer.insert(testBubble1);
        storeServer.beginUnitOfWork();
        TestBubble copy = CopyHelper.copy(testBubble1);
        storeServer.delete(copy);
        storeServer.commitUnitOfWork();
        assertSame(storeServer.get(TestBubbleId_101), copy);
        storeServer.abortUnitOfWork();
        assertNotFound(storeServer, TestBubbleId_101);
        storeServer.commitTransaction();
        assertEquals(countInDatabase(persistenceSessionForSnapshot, TestBubbleId_101), 0);
    }

    public void testInsertDeleteObjectViaNestedUnitOfWork_AbortBoth() {
        storeServer.beginTransaction();
        storeServer.beginUnitOfWork();
        TestBubble testBubble1 = new TestBubble(TestBubbleId_101, "TestBubble 101");
        storeServer.insert(testBubble1);
        storeServer.beginUnitOfWork();
        TestBubble copy = CopyHelper.copy(testBubble1);
        storeServer.delete(copy);
        storeServer.abortUnitOfWork();
        assertSame(storeServer.get(TestBubbleId_101), testBubble1);
        storeServer.abortUnitOfWork();
        assertNotFound(storeServer, TestBubbleId_101);
        storeServer.commitTransaction();
        assertEquals(countInDatabase(persistenceSessionForSnapshot, TestBubbleId_101), 0);
    }

    public void testInsertUpdateDeleteObjectViaMultiNestedUnitOfWork() {
        storeServer.beginTransaction();
        storeServer.beginUnitOfWork();
        TestBubbleId<TestBubble> TestBubbleId_101_CURRENT = new TestBubbleId<TestBubble>(101L);
        TestBubble testBubble1 = new TestBubble(TestBubbleId_101_CURRENT, "TestBubble 101");
        storeServer.insert(testBubble1);
        storeServer.beginUnitOfWork();
        TestBubble testBubble2 = storeServer.get(testBubble1.getId());
        testBubble2.setText("TestBubble updated");
        storeServer.update(testBubble2);
        storeServer.beginUnitOfWork();
        TestBubble copy = storeServer.get(TestBubbleId_101);
        storeServer.delete(copy);
        storeServer.commitUnitOfWork();
        assertSame(storeServer.get(TestBubbleId_101_CURRENT), copy);
        storeServer.commitUnitOfWork();
        storeServer.commitUnitOfWork();
        assertNotFound(storeServer, TestBubbleId_101_CURRENT);
        storeServer.commitTransaction();
        assertEquals(countInDatabase(persistenceSessionForSnapshot, TestBubbleId_101), 0);
    }

    public void testInsertUpdateDeleteObjectViaMultiNestedUnitOfWork_AbortInnder() {
        storeServer.beginTransaction();
        storeServer.beginUnitOfWork();
        TestBubble testBubble1 = new TestBubble(TestBubbleId_101, "TestBubble 101");
        storeServer.insert(testBubble1);
        storeServer.beginUnitOfWork();
        TestBubble testBubble2 = storeServer.get(testBubble1.getId());
        testBubble2.setText("TestBubble updated");
        storeServer.update(testBubble2);
        storeServer.beginUnitOfWork();
        TestBubble copy = storeServer.get(TestBubbleId_101);
        storeServer.delete(copy);
        storeServer.abortUnitOfWork();
        assertSame(storeServer.get(TestBubbleId_101), testBubble2);
        assertEquals(storeServer.get(TestBubbleId_101).getText(), "TestBubble updated");
        storeServer.commitUnitOfWork();
        storeServer.commitUnitOfWork();
        assertSame(storeServer.get(TestBubbleId_101), testBubble2);
        assertEquals(storeServer.get(TestBubbleId_101).getText(), "TestBubble updated");
        storeServer.commitTransaction();
        assertEquals(countInDatabase(persistenceSessionForSnapshot, TestBubbleId_101), 1);
        assertSame(storeServer.get(TestBubbleId_101), testBubble2);
    }
}
