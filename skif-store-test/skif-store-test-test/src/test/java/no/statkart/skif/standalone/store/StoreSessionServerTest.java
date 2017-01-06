package no.statkart.skif.standalone.store;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;
import com.google.inject.util.Providers;
import no.statkart.skif.SkifUtil;
import no.statkart.skif.config.Configuration;
import no.statkart.skif.config.SkifServerConfiguration;
import no.statkart.skif.exception.AttemptDeleteException;
import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.exception.ObjectNotFoundException;
import no.statkart.skif.persistence.VersionFinder;
import no.statkart.skif.service.DefaultServiceContext;
import no.statkart.skif.service.PrincipalImpl;
import no.statkart.skif.service.ServiceContext;
import no.statkart.skif.service.ServiceRequestContext;
import no.statkart.skif.service.locker.DBLockerInTransactionService;
import no.statkart.skif.service.locker.DBLockerService;
import no.statkart.skif.service.sequence.IdService;
import no.statkart.skif.standalone.util.testsupport.StandAloneTestHelper;
import no.statkart.skif.store.BubbleObject;
import no.statkart.skif.store.LockerStrategy;
import no.statkart.skif.store.MemoryLocker;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.StoreServer;
import no.statkart.skif.store.StoreSessionFinishListener;
import no.statkart.skif.store.StoreSessionReadListener;
import no.statkart.skif.store.StoreSessionServer;
import no.statkart.skif.store.StoreSessionWriteListener;
import no.statkart.skif.store.TransactionalLockerStrategy;
import no.statkart.skif.store.UnitOfWork;
import no.statkart.skif.store.persistence.DefaultPersistenceSessionManager;
import no.statkart.skif.store.persistence.DefaultPersistenceSessionStrategy;
import no.statkart.skif.store.persistence.PersistenceSessionForSnapshot;
import no.statkart.skif.store.persistence.hibernate.DefaultHibernatePersistenceSessionImplExt;
import no.statkart.skif.store.persistence.hibernate.HibernateBubbleDependencyComparator;
import no.statkart.skif.store.persistence.hibernate.HibernatePersistenceSessionMaster;
import no.statkart.skif.store.persistence.hibernate.HibernatePersistenceSessionMasterImpl;
import no.statkart.skif.store.persistence.hibernate.HibernateSessionFactoryBuilder;
import no.statkart.skif.store.persistence.hibernate.HibernateSessionFactoryManagerBundle;
import no.statkart.skif.store.persistence.kodeliste.DefaultKodelistePersistenceSessionSubtypeHandler;
import no.statkart.skif.store.persistence.kodeliste.EnumKodelisteManager;
import no.statkart.skif.storetest.domain.demo.koder.AEnumKodeId;
import no.statkart.skif.storetest.domain.demo.koder.BEnumKodeId;
import no.statkart.skif.storetest.domain.demo.koder.SEnumKodeId;
import no.statkart.skif.storetest.domain.standalone.ChildBubble;
import no.statkart.skif.storetest.domain.standalone.ChildBubbleId;
import no.statkart.skif.storetest.domain.standalone.FilteredBubble;
import no.statkart.skif.storetest.domain.standalone.FilteredBubbleId;
import no.statkart.skif.storetest.domain.standalone.ParentBubble;
import no.statkart.skif.storetest.domain.standalone.ParentBubbleId;
import no.statkart.skif.storetest.domain.standalone.SelfBubble;
import no.statkart.skif.storetest.domain.standalone.SelfBubbleId;
import no.statkart.skif.storetest.domain.standalone.TestBubble;
import no.statkart.skif.storetest.domain.standalone.TestBubbleId;
import no.statkart.skif.storetest.domain.standalone.TestBubbleWithHistory;
import no.statkart.skif.storetest.domain.standalone.TestBubbleWithHistoryId;
import no.statkart.skif.storetest.filter.TestBubbleFilter;
import no.statkart.skif.storetest.filter.TestBubbleFinishFilter;
import no.statkart.skif.util.CopyHelper;
import no.statkart.skif.util.MemoryProfileUtil;
import org.hibernate.Session;
import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.Set;

import static no.statkart.skif.standalone.util.testsupport.StandAloneTestHelper.S1;
import static no.statkart.skif.standalone.util.testsupport.StandAloneTestHelper.S2;
import static no.statkart.skif.standalone.util.testsupport.StandAloneTestHelper.S3;
import static no.statkart.skif.standalone.util.testsupport.StandAloneTestHelper.assertNotFound;
import static no.statkart.skif.standalone.util.testsupport.StandAloneTestHelper.countInDatabase;
import static no.statkart.skif.standalone.util.testsupport.StandAloneTestHelper.createHibernateSessionFactorManagerBundle;
import static no.statkart.skif.standalone.util.testsupport.StandAloneTestHelper.deletePriviouslyWritenTestBubbles;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.fest.assertions.api.Assertions.extractProperty;
import static org.fest.assertions.api.Assertions.failBecauseExceptionWasNotThrown;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNotSame;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;
import static org.testng.FileAssert.fail;

/**
 * Tester basal StoreSessionServer funksjonalitet. Testen anvender kun standalone domeneklassene {@code TestBubble,
 * TestBubbleWithHistory, TestBubbleFilter, SelfBubble, Parent og Child}. Disse klasser er veldig enkle og inneholder
 * bl.a ikke Koder. Mer avansert testing utføres i tester basert på StoreTestServer modulen og ved bruk av mockup-rammeverket.
 * <p>
 * Dette er en stand-alone-test som går direkte mot databasen uten å bruke StoreTestServer modulen. Mest naturlig at testene
 * kjøres i singleVM mode.
 *
 * @author Henrik Fredholm
 * @since 2.0
 */
@Test(groups = "singlevm-required")
public class StoreSessionServerTest {
    private Logger logger = LoggerFactory.getLogger(StoreSessionServerTest.class);

    private Properties hibernateProperties;

    private TestBubbleWithHistoryId<TestBubbleWithHistory> testBubbleWithHistoryId_10_CURRENT = new TestBubbleWithHistoryId<>(10L, SnapshotVersion.CURRENT);
    private TestBubbleWithHistoryId<TestBubbleWithHistory> testBubbleWithHistoryId_10_OLD = new TestBubbleWithHistoryId<>(10L, SnapshotVersion.OLD);
    private TestBubbleWithHistoryId<TestBubbleWithHistory> testBubbleWithHistoryId_10_S1 = new TestBubbleWithHistoryId<>(10L, S1);
    private TestBubbleWithHistoryId<TestBubbleWithHistory> testBubbleWithHistoryId_10_S2 = new TestBubbleWithHistoryId<>(10L, S2);
    private TestBubbleWithHistoryId<TestBubbleWithHistory> testBubbleWithHistoryId_10_S3 = new TestBubbleWithHistoryId<>(10L, S3);

    private TestBubbleWithHistoryId<TestBubbleWithHistory> testBubbleWithHistoryId_11_CURRENT = new TestBubbleWithHistoryId<>(11L, SnapshotVersion.CURRENT);
    private TestBubbleWithHistoryId<TestBubbleWithHistory> testBubbleWithHistoryId_11_S3 = new TestBubbleWithHistoryId<>(11L, S3);
    private TestBubbleWithHistoryId<TestBubbleWithHistory> testBubbleWithHistoryId_11_OLD = new TestBubbleWithHistoryId<>(11L, SnapshotVersion.OLD);

    private TestBubbleId<TestBubble> TestBubbleId_1 = new TestBubbleId<>(1);
    private TestBubbleId<TestBubble> TestBubbleId_101 = new TestBubbleId<>(101);

    private FilteredBubbleId<FilteredBubble> filteredBubbleId_1 = new FilteredBubbleId<>(1);
    private FilteredBubbleId<FilteredBubble> filteredBubbleId_2 = new FilteredBubbleId<>(2);
    private FilteredBubbleId<FilteredBubble> filteredBubbleId_101 = new FilteredBubbleId<>(101);

    private ParentBubbleId<ParentBubble> parentBubbleId_1 = new ParentBubbleId<>(1);
    private ParentBubbleId<ParentBubble> parentBubbleId_2 = new ParentBubbleId<>(2);

    private HibernateSessionFactoryManagerBundle sessionFactoryManagerBundle;
    private DefaultPersistenceSessionManager persistenceSessionManager;
    private PersistenceSessionForSnapshot persistenceSessionForSnapshot;

    private StoreServer storeServer;

    public StoreSessionServerTest() {
        hibernateProperties = StandAloneTestHelper.createHibernatePropertiesSingleVm();
    }

    @BeforeClass
    public void setUp() {
        HibernateSessionFactoryBuilder sessionFactoryBuilder = StandAloneTestHelper.createHibernateSessionFactoryBuilderWithHistory();
        sessionFactoryManagerBundle = createHibernateSessionFactorManagerBundle(sessionFactoryBuilder, hibernateProperties);
    }

    @AfterClass
    void tearDown() {
        sessionFactoryManagerBundle.close();
    }

    private DefaultPersistenceSessionManager createPersistenceSessionManager() {
        ServiceContext context = new DefaultServiceContext();
        context.setLocale(new Locale("no", "NO"));

        EnumKodelisteManager enumKodelistManager = new EnumKodelisteManager();
        enumKodelistManager.installStatic(AEnumKodeId.class);
        enumKodelistManager.installStatic(BEnumKodeId.class);
        enumKodelistManager.installStatic(SEnumKodeId.class);

        HibernatePersistenceSessionMasterImpl masterCurrent = new DefaultHibernatePersistenceSessionImplExt(
                sessionFactoryManagerBundle.getBundle().get(0)
        );
        HibernatePersistenceSessionMasterImpl masterOld = new DefaultHibernatePersistenceSessionImplExt(
                sessionFactoryManagerBundle.getBundle().get(1)
        );

        return new DefaultPersistenceSessionManager(
                new DefaultPersistenceSessionStrategy(
                        masterCurrent,
                        new DefaultKodelistePersistenceSessionSubtypeHandler(masterCurrent, enumKodelistManager)
                ),
                new DefaultPersistenceSessionStrategy(
                        masterOld,
                        new DefaultKodelistePersistenceSessionSubtypeHandler(masterOld, enumKodelistManager)
                )
        );
    }

    @BeforeMethod
    public void createStore() {
        persistenceSessionManager = createPersistenceSessionManager();
        persistenceSessionForSnapshot = persistenceSessionManager.getForSnapshotVersion(SnapshotVersion.CURRENT);
        //ReadListener
        List<StoreSessionReadListener> readListeners = new ArrayList<>();
        readListeners.add(new TestBubbleFilter());
        List<StoreSessionWriteListener> writeListeners = new ArrayList<>();
        writeListeners.add(new TestBubbleFilter());
        List<StoreSessionFinishListener> finishListeners = new ArrayList<>();
        finishListeners.add(new TestBubbleFinishFilter());

        Injector fakeInjector = Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                bind(IdService.class).toProvider(Providers.<IdService>of(null));
                TypeLiteral<MemoryLocker<Long>> memoryLockerLongType = SkifUtil.typeLiteral(MemoryLocker.class, Long.class);
                bind(memoryLockerLongType).in(Singleton.class);
                bind(SkifUtil.typeLiteral(DBLockerService.class, Long.class)).to(memoryLockerLongType);
                bind(SkifUtil.typeLiteral(DBLockerInTransactionService.class, Long.class)).to(memoryLockerLongType);
                bind(LockerStrategy.class).to(TransactionalLockerStrategy.class);
                bind(TransactionalLockerStrategy.class).in(Singleton.class);
                bind(Configuration.class).toInstance(new SkifServerConfiguration());
                ServiceRequestContext serviceRequestContext = new ServiceRequestContext(new PrincipalImpl("test"), "test", 0);
                bind(ServiceRequestContext.class).toInstance(serviceRequestContext);
            }
        });

        final HibernateBubbleDependencyComparator dependencyComparator = new HibernateBubbleDependencyComparator(sessionFactoryManagerBundle);

        storeServer = new StoreServer(new StoreSessionServer(persistenceSessionManager, Providers.<VersionFinder>of(null), Providers.of(SnapshotVersion.CURRENT), fakeInjector.getInstance(LockerStrategy.class), dependencyComparator, readListeners, writeListeners, finishListeners), fakeInjector);
        deletePriviouslyWritenTestBubbles(persistenceSessionManager.getForSnapshotVersion(SnapshotVersion.CURRENT));
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
        TestBubbleWithHistory TestBubbleWithHistory_100_CURRENT = storeServer.get(testBubbleWithHistoryId_10_CURRENT);
        assertEquals(TestBubbleWithHistory_100_CURRENT.getId(), testBubbleWithHistoryId_10_CURRENT);
        assertSame(TestBubbleWithHistory_100_CURRENT, storeServer.get(TestBubbleWithHistory_100_CURRENT.getId()));

        TestBubbleWithHistory TestBubbleWithHistory_100_OLD = storeServer.get(testBubbleWithHistoryId_10_OLD);
        assertEquals(TestBubbleWithHistory_100_OLD.getId(), testBubbleWithHistoryId_10_OLD);
        assertSame(TestBubbleWithHistory_100_OLD, storeServer.get(TestBubbleWithHistory_100_OLD.getId()));

        TestBubbleWithHistory TestBubbleWithHistory_100_S1 = storeServer.get(testBubbleWithHistoryId_10_S1);
        assertEquals(TestBubbleWithHistory_100_S1.getId(), testBubbleWithHistoryId_10_S1);
        assertSame(TestBubbleWithHistory_100_S1, storeServer.get(TestBubbleWithHistory_100_S1.getId()));

        Set<TestBubbleWithHistoryId<?>> TestBubbleWithHistoryIds = new HashSet<>(3);
        TestBubbleWithHistoryIds.add(testBubbleWithHistoryId_11_OLD);
        TestBubbleWithHistoryIds.add(testBubbleWithHistoryId_11_S3);
        Set<TestBubbleWithHistory> TestBubbleWithHistorys = storeServer.get(TestBubbleWithHistoryIds);
        assertThat(extractProperty("id").from(TestBubbleWithHistorys)).containsOnly(testBubbleWithHistoryId_11_OLD, testBubbleWithHistoryId_11_S3);

        // Test at multipel uthenting gir samme objekter
        TestBubbleWithHistoryIds = new HashSet<>(3);
        TestBubbleWithHistoryIds.add(TestBubbleWithHistory_100_CURRENT.getId());
        TestBubbleWithHistoryIds.add(TestBubbleWithHistory_100_OLD.getId());
        TestBubbleWithHistoryIds.add(TestBubbleWithHistory_100_S1.getId());
        TestBubbleWithHistorys = storeServer.get(TestBubbleWithHistoryIds);
        assertThat(TestBubbleWithHistorys).containsOnly(TestBubbleWithHistory_100_CURRENT, TestBubbleWithHistory_100_OLD, TestBubbleWithHistory_100_S1);
        for (TestBubbleWithHistory TestBubbleWithHistory : TestBubbleWithHistorys) {
            if (TestBubbleWithHistory.getId().equals(testBubbleWithHistoryId_10_CURRENT)) {
                assertThat(TestBubbleWithHistory).isSameAs(TestBubbleWithHistory_100_CURRENT);
            }
            if (TestBubbleWithHistory.getId().equals(testBubbleWithHistoryId_10_OLD)) {
                assertThat(TestBubbleWithHistory).isSameAs(TestBubbleWithHistory_100_OLD);
            }
            if (TestBubbleWithHistory.getId().equals(testBubbleWithHistoryId_10_S1)) {
                assertThat(TestBubbleWithHistory).isSameAs(TestBubbleWithHistory_100_S1);
            }
        }
    }

    /**
     * Tester insert object
     */
    public void testInsert() {
        TestBubble testBubble_101 = new TestBubble(TestBubbleId_101);
        testBubble_101.setText("Insert 1");
        storeServer.beginTransaction();
        storeServer.insert(testBubble_101);
        storeServer.commitTransaction();
        assertEquals(countInDatabase(persistenceSessionForSnapshot, TestBubbleId_101), 1);
    }

    /**
     * Tester lockObject object
     */
    public void testLockObject() {
        testInsert();
        TestBubble testBubble_101 = storeServer.lock(TestBubbleId_101);
        assertSame(storeServer.lock(TestBubbleId_101), testBubble_101);

        storeServer.beginTransaction();
        storeServer.commitTransaction();

        // Denne skal ikke gi en ekstra select statement (har verifisert dette, frehen)
        assertSame(storeServer.get(TestBubbleId_101), testBubble_101);
        // Denne skal gi en ekstra select statement for refresh (har verifisert dette, frehen)
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


    public void testLesFilteredKlasse() {
        FilteredBubble filteredBubble = storeServer.get(filteredBubbleId_1);
        assertNotNull(filteredBubble);
        assertFalse(filteredBubble.getFilterText().contains("*"));

        FilteredBubble filteredBubble2 = storeServer.get(filteredBubbleId_2);
        assertNotNull(filteredBubble2);
        assertTrue(filteredBubble2.getFilterText().contains("*"));
    }


    public void testInsertFilteredKlasse() {
        FilteredBubble filteredBubble = new FilteredBubble(filteredBubbleId_101, "Insert ufiltrert 101", false, "Insert filtrert 101");
        storeServer.beginTransaction();
        storeServer.insert(filteredBubble);
        storeServer.commitTransaction();
        FilteredBubble lest = storeServer.get(filteredBubble.getId());
        assertSame(filteredBubble, lest);
    }

    public void testInsertFilteredKlasse_aktivt_fileter() {
        String ftekst = "Skal overskrives på vei ned i basen 101";
        FilteredBubble filteredBubble = new FilteredBubble(filteredBubbleId_101, "Insert ufiltrert 101", true, ftekst);
        try {
            storeServer.beginTransaction();
            storeServer.insert(filteredBubble);
            storeServer.commitTransaction();
            fail("insert på filtrert objekt feilet ikke");
        } catch (Exception e) {
            //skal feile
        }
    }

    public void testLesOppdaterFilteredKlasse() {
        storeServer.beginTransaction();

        FilteredBubble filteredBubble = storeServer.lock(filteredBubbleId_2);
        assertNotNull(filteredBubble);
        filteredBubble.setFilterText(null);
        try {
            storeServer.update(filteredBubble);
            storeServer.commitTransaction();
            fail("skal ikke klare å lagre objekt som har filtering!");
        } catch (Exception e) {
            //skal feile
        }
        //assertTrue(storeServer.get(filteredBubbleId_101).getFilterText().contains("*"));
    }


    public void testFinishFilter() {
        String str = "Skal byttes ut i finish";
        storeServer.beginTransaction();
        FilteredBubble filteredBubble = new FilteredBubble(filteredBubbleId_101, "Finish 101", false, str);
        storeServer.insert(filteredBubble);
        storeServer.commitTransaction();

        FilteredBubble lest = storeServer.get(filteredBubbleId_101);
        assertNotSame(str, lest.getFilterText());
    }


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
        assertTrue(storeServer.evict(new TestBubbleId<>(1L)));
        TestBubble testBubble2 = storeServer.get(TestBubbleId_1);
        assertEquals(testBubble1, testBubble2);
        assertNotSame(testBubble1, testBubble2);
    }

    public void testEvictLockedObject() {
        TestBubble testBubble1 = storeServer.lock(TestBubbleId_1);
        assertTrue(storeServer.isLocked(new TestBubbleId<>(1L)));
        assertTrue(storeServer.evict(new TestBubbleId<>(1L)));
        assertTrue(storeServer.isLocked(new TestBubbleId<>(1L)));
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
        assertNotNull(bubbleObject);
    }


    @Test
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
            assertEquals(e.getNotFoundId(), TestBubbleId_101);
        }

        storeServer.beginTransaction();
        storeServer.insert(testBubble_101);
        storeServer.commitTransaction();
        assertEquals(countInDatabase(persistenceSessionForSnapshot, TestBubbleId_101), 1);

    }

    public void testBeginEndMultiLevelEmptyUnitOfWorks() {
        storeServer.beginTransaction();
        UnitOfWork unitOfWork1 = storeServer.beginUnitOfWork();
        UnitOfWork unitOfWork2 = storeServer.beginUnitOfWork();
        UnitOfWork unitOfWork3 = storeServer.beginUnitOfWork();
        storeServer.commitUnitOfWork(unitOfWork3);
        storeServer.commitUnitOfWork(unitOfWork2);
        storeServer.commitUnitOfWork(unitOfWork1);
        storeServer.commitTransaction();
    }

    public void testInsertViaUOW() {
        storeServer.beginTransaction();
        UnitOfWork unitOfWork = storeServer.beginUnitOfWork();
        TestBubble testBubble_100 = new TestBubble(TestBubbleId_101);
        testBubble_100.setText("Insert 1");
        storeServer.insert(testBubble_100);
        storeServer.commitUnitOfWork(unitOfWork);
        storeServer.commitTransaction();
        assertEquals(countInDatabase(persistenceSessionForSnapshot, TestBubbleId_101), 1);
    }

    public void testInsertViaUOW_Abort() {
        storeServer.beginTransaction();
        UnitOfWork unitOfWork = storeServer.beginUnitOfWork();
        TestBubble testBubble_100 = new TestBubble(TestBubbleId_101);
        testBubble_100.setText("Insert 1");
        storeServer.insert(testBubble_100);
        storeServer.abortUnitOfWork(unitOfWork);
        assertNotFound(storeServer, TestBubbleId_101);
        storeServer.commitTransaction();
        assertEquals(countInDatabase(persistenceSessionForSnapshot, TestBubbleId_101), 0);
    }

    public void testUpdateViaUOW() {
        testInsert();
        storeServer.beginTransaction();

        UnitOfWork unitOfWork = storeServer.beginUnitOfWork();
        TestBubble testBubble_101 = storeServer.lock(TestBubbleId_101);
        assertEquals(testBubble_101.getText(), "Insert 1");
        testBubble_101.setText("Update 1");
        storeServer.update(testBubble_101);
        assertEquals(storeServer.get(TestBubbleId_101).getText(), "Update 1");
        storeServer.commitUnitOfWork(unitOfWork);

        storeServer.commitTransaction();
        storeServer.evict(testBubble_101.getId());
        assertEquals(storeServer.get(TestBubbleId_101).getText(), "Update 1");
    }

    public void testUpdateViaUOW_Abort() {
        testInsert();
        storeServer.beginTransaction();

        UnitOfWork unitOfWork = storeServer.beginUnitOfWork();
        TestBubble testBubble_101 = storeServer.lock(TestBubbleId_101);
        assertEquals(testBubble_101.getText(), "Insert 1");
        testBubble_101.setText("Update 1");
        storeServer.update(testBubble_101);
        assertEquals(storeServer.get(TestBubbleId_101).getText(), "Update 1");
        storeServer.abortUnitOfWork(unitOfWork);
        assertEquals(storeServer.get(TestBubbleId_101).getText(), "Insert 1");
        storeServer.commitTransaction();
        storeServer.evict(testBubble_101.getId());
        assertEquals(storeServer.get(TestBubbleId_101).getText(), "Insert 1");
    }

    public void testDeleteViaUOW() {
        testInsert();
        storeServer.beginTransaction();

        UnitOfWork unitOfWork = storeServer.beginUnitOfWork();
        TestBubble testBubble_101 = storeServer.lock(TestBubbleId_101);
        storeServer.delete(testBubble_101);
        storeServer.commitUnitOfWork(unitOfWork);

        storeServer.commitTransaction();
        assertEquals(countInDatabase(persistenceSessionForSnapshot, TestBubbleId_101), 0);
    }

    public void testDeleteViaUOW_Abort() {
        testInsert();
        storeServer.beginTransaction();

        UnitOfWork unitOfWork = storeServer.beginUnitOfWork();
        TestBubble testBubble_101 = storeServer.lock(TestBubbleId_101);
        testBubble_101.setText("Deleted");
        storeServer.delete(testBubble_101);
        storeServer.abortUnitOfWork(unitOfWork);
        assertEquals(storeServer.get(TestBubbleId_101).getText(), "Insert 1");

        storeServer.commitTransaction();
        assertEquals(countInDatabase(persistenceSessionForSnapshot, TestBubbleId_101), 1);
    }

    public void testInsertDeleteObjectInSameUnitOfWork() {

        storeServer.beginTransaction();
        UnitOfWork unitOfWork = storeServer.beginUnitOfWork();
        TestBubble testBubble1 = new TestBubble(TestBubbleId_101, "TestBubble 101");
        storeServer.insert(testBubble1);
        storeServer.delete(testBubble1);
        storeServer.commitUnitOfWork(unitOfWork);
        storeServer.commitTransaction();
        assertEquals(countInDatabase(persistenceSessionForSnapshot, TestBubbleId_101), 0);
        assertNotFound(storeServer, TestBubbleId_101);
    }

    public void testInsertDeleteObjectInSameUnitOfWork_Abort() {

        storeServer.beginTransaction();
        UnitOfWork unitOfWork = storeServer.beginUnitOfWork();
        TestBubble testBubble1 = new TestBubble(TestBubbleId_101, "TestBubble 101");
        storeServer.insert(testBubble1);
        storeServer.delete(testBubble1);
        storeServer.abortUnitOfWork(unitOfWork);
        assertNotFound(storeServer, TestBubbleId_101);
        storeServer.commitTransaction();
        assertEquals(countInDatabase(persistenceSessionForSnapshot, TestBubbleId_101), 0);
        assertNotFound(storeServer, TestBubbleId_101);
    }


    public void testInsertDeleteObjectViaNestedUnitOfWork() {
        storeServer.beginTransaction();
        UnitOfWork unitOfWork1 = storeServer.beginUnitOfWork();
        TestBubbleId<TestBubble> TestBubbleId_101_CURRENT = new TestBubbleId<>(101L);
        TestBubble testBubble1 = new TestBubble(TestBubbleId_101_CURRENT, "TestBubble 101");
        storeServer.insert(testBubble1);
        UnitOfWork unitOfWork2 = storeServer.beginUnitOfWork();
        TestBubble copy = CopyHelper.copy(testBubble1);
        storeServer.delete(copy);
        storeServer.commitUnitOfWork(unitOfWork2);
        assertSame(storeServer.get(TestBubbleId_101_CURRENT), copy);
        storeServer.commitUnitOfWork(unitOfWork1);
        assertNotFound(storeServer, TestBubbleId_101_CURRENT);
        storeServer.commitTransaction();
        assertEquals(countInDatabase(persistenceSessionForSnapshot, TestBubbleId_101), 0);
    }

    public void testInsertDeleteObjectViaNestedUnitOfWork_AbortInner() {
        storeServer.beginTransaction();
        UnitOfWork unitOfWork1 = storeServer.beginUnitOfWork();
        TestBubble testBubble1 = new TestBubble(TestBubbleId_101, "TestBubble 101");
        storeServer.insert(testBubble1);
        UnitOfWork unitOfWork2 = storeServer.beginUnitOfWork();
        TestBubble copy = CopyHelper.copy(testBubble1);
        storeServer.delete(copy);
        storeServer.abortUnitOfWork(unitOfWork2);
        assertSame(storeServer.get(TestBubbleId_101), testBubble1);
        storeServer.commitUnitOfWork(unitOfWork1);
        assertEquals(storeServer.get(TestBubbleId_101).getText(), "TestBubble 101");
        storeServer.commitTransaction();
        assertEquals(countInDatabase(persistenceSessionForSnapshot, TestBubbleId_101), 1);
    }

    public void testInsertDeleteObjectViaNestedUnitOfWork_AbortOuter() {
        storeServer.beginTransaction();
        UnitOfWork unitOfWork1 = storeServer.beginUnitOfWork();
        TestBubble testBubble1 = new TestBubble(TestBubbleId_101, "TestBubble 101");
        storeServer.insert(testBubble1);
        UnitOfWork unitOfWork2 = storeServer.beginUnitOfWork();
        TestBubble copy = CopyHelper.copy(testBubble1);
        storeServer.delete(copy);
        storeServer.commitUnitOfWork(unitOfWork2);
        assertSame(storeServer.get(TestBubbleId_101), copy);
        storeServer.abortUnitOfWork(unitOfWork1);
        assertNotFound(storeServer, TestBubbleId_101);
        storeServer.commitTransaction();
        assertEquals(countInDatabase(persistenceSessionForSnapshot, TestBubbleId_101), 0);
    }

    public void testInsertDeleteObjectViaNestedUnitOfWork_AbortBoth() {
        storeServer.beginTransaction();
        UnitOfWork unitOfWork1 = storeServer.beginUnitOfWork();
        TestBubble testBubble1 = new TestBubble(TestBubbleId_101, "TestBubble 101");
        storeServer.insert(testBubble1);
        UnitOfWork unitOfWork2 = storeServer.beginUnitOfWork();
        TestBubble copy = CopyHelper.copy(testBubble1);
        storeServer.delete(copy);
        storeServer.abortUnitOfWork(unitOfWork2);
        assertSame(storeServer.get(TestBubbleId_101), testBubble1);
        storeServer.abortUnitOfWork(unitOfWork1);
        assertNotFound(storeServer, TestBubbleId_101);
        storeServer.commitTransaction();
        assertEquals(countInDatabase(persistenceSessionForSnapshot, TestBubbleId_101), 0);
    }

    public void testInsertUpdateDeleteObjectViaMultiNestedUnitOfWork() {
        storeServer.beginTransaction();
        UnitOfWork unitOfWork1 = storeServer.beginUnitOfWork();
        TestBubbleId<TestBubble> TestBubbleId_101_CURRENT = new TestBubbleId<>(101L);
        TestBubble testBubble1 = new TestBubble(TestBubbleId_101_CURRENT, "TestBubble 101");
        storeServer.insert(testBubble1);
        UnitOfWork unitOfWork2 = storeServer.beginUnitOfWork();
        TestBubble testBubble2 = storeServer.get(testBubble1.getId());
        testBubble2.setText("TestBubble updated");
        storeServer.update(testBubble2);
        UnitOfWork unitOfWork3 = storeServer.beginUnitOfWork();
        TestBubble copy = storeServer.get(TestBubbleId_101);
        storeServer.delete(copy);
        storeServer.commitUnitOfWork(unitOfWork3);
        assertSame(storeServer.get(TestBubbleId_101_CURRENT), copy);
        storeServer.commitUnitOfWork(unitOfWork2);
        storeServer.commitUnitOfWork(unitOfWork1);
        assertNotFound(storeServer, TestBubbleId_101_CURRENT);
        storeServer.commitTransaction();
        assertEquals(countInDatabase(persistenceSessionForSnapshot, TestBubbleId_101), 0);
    }

    public void testInsertUpdateDeleteObjectViaMultiNestedUnitOfWork_AbortInnder() {
        storeServer.beginTransaction();
        UnitOfWork unitOfWork1 = storeServer.beginUnitOfWork();
        TestBubble testBubble1 = new TestBubble(TestBubbleId_101, "TestBubble 101");
        storeServer.insert(testBubble1);
        UnitOfWork unitOfWork2 = storeServer.beginUnitOfWork();
        TestBubble testBubble2 = storeServer.get(testBubble1.getId());
        testBubble2.setText("TestBubble updated");
        storeServer.update(testBubble2);
        UnitOfWork unitOfWork3 = storeServer.beginUnitOfWork();
        TestBubble copy = storeServer.get(TestBubbleId_101);
        storeServer.delete(copy);
        storeServer.abortUnitOfWork(unitOfWork3);
        assertSame(storeServer.get(TestBubbleId_101), testBubble2);
        assertEquals(storeServer.get(TestBubbleId_101).getText(), "TestBubble updated");
        storeServer.commitUnitOfWork(unitOfWork2);
        storeServer.commitUnitOfWork(unitOfWork1);
        assertSame(storeServer.get(TestBubbleId_101), testBubble2);
        assertEquals(storeServer.get(TestBubbleId_101).getText(), "TestBubble updated");
        storeServer.commitTransaction();
        assertEquals(countInDatabase(persistenceSessionForSnapshot, TestBubbleId_101), 1);
        assertSame(storeServer.get(TestBubbleId_101), testBubble2);
    }


    public void testTestBubbleWithHistoryInsertUpdate() {

        TestBubbleWithHistoryId<TestBubbleWithHistory> testBubbleWithHistoryId_101 = new TestBubbleWithHistoryId<>(101L);

        storeServer.beginTransaction();
        TestBubbleWithHistory TestBubbleWithHistory1 = new TestBubbleWithHistory();
        TestBubbleWithHistory1.setId(testBubbleWithHistoryId_101);
        TestBubbleWithHistory1.setText("TestBubbleWithHistory Insert");
        storeServer.insert(TestBubbleWithHistory1);
        storeServer.commitTransaction();

        storeServer.beginTransaction();
        TestBubbleWithHistory TestBubbleWithHistory2 = storeServer.lock(testBubbleWithHistoryId_101);
        TestBubbleWithHistory2.setText("TestBubbleWithHistory Update");
        storeServer.update(TestBubbleWithHistory2);
        storeServer.commitTransaction();
    }

    public void testReorderModification() {
        storeServer.beginTransaction();

        SelfBubbleId<SelfBubble> selfBubbleId_102 = new SelfBubbleId<>(102L);
        SelfBubbleId<SelfBubble> selfBubbleId_101 = new SelfBubbleId<>(101L);
        SelfBubbleId<SelfBubble> selfBubbleId_103 = new SelfBubbleId<>(103L);
        SelfBubble b_101 = new SelfBubble(selfBubbleId_101);
        SelfBubble b_102 = new SelfBubble(selfBubbleId_102);
        SelfBubble b_103 = new SelfBubble(selfBubbleId_103);
        b_102.setRefId(b_101.getId());

        TestBubble testBubble = new TestBubble(TestBubbleId_101);

        // b_102 refererer b_101 og man bør derfor få referanse feil. Men dersom b_101 og b_102 er med i samme batch går det
        // greit likevel. Har derfor langt inn testBubble for å bryte batchen. For det skal virke må Store ikke
        // stokke om på rekkefølgen. Derfor har SelfBubble og TestBubble samme sorteringsindex.
        UnitOfWork unitOfWork = storeServer.beginUnitOfWork();
        storeServer.insert(b_102);
        storeServer.insert(testBubble);
        storeServer.insert(b_101);
        storeServer.insert(b_103);
        storeServer.reorderModification(b_102.getId());
        storeServer.commitUnitOfWork(unitOfWork);
        storeServer.commitTransaction();
    }

    @Test
    public void testReorderModificationNotInUnitOfWork() {
        try {
            storeServer.beginTransaction();

            SelfBubbleId<SelfBubble> selfBubbleId_101 = new SelfBubbleId<>(101L);
            SelfBubbleId<SelfBubble> selfBubbleId_102 = new SelfBubbleId<>(102L);
            SelfBubbleId<SelfBubble> selfBubbleId_103 = new SelfBubbleId<>(103L);
            SelfBubble b_101 = new SelfBubble(selfBubbleId_101);
            SelfBubble b_102 = new SelfBubble(selfBubbleId_102);
            SelfBubble b_103 = new SelfBubble(selfBubbleId_103);
            b_102.setRefId(b_101.getId());

            TestBubble testBubble = new TestBubble(TestBubbleId_101);

            // b_102 refererer b_101 og man bør derfor få referanse feil. Men dersom b_101 og b_102 er med i samme batch går det
            // greit likevel. Har derfor langt inn testBubble for å bryte batchen. For det skal virke må Store ikke
            // stokke om på rekkefølgen. Derfor har SelfBubble og TestBubble samme sorteringsindex.
            storeServer.insert(b_102);
            storeServer.insert(testBubble);
            storeServer.insert(b_101);
            storeServer.insert(b_103);
            storeServer.reorderModification(b_102.getId());
            fail();
        } catch (Throwable t) {
            assertThat(t).describedAs("forventet exception").isInstanceOf(ImplementationException.class);
        } finally {
            storeServer.rollbackTransaction();
        }
    }

    @Test(groups="oracleLatest")   // Tidligere versjoner av oracle enn 11.2.0.3.0 gir ikke constraintfeil hvis objekter er i samme batch
    public void testReorderModificationNoReorder() {
        try {
            storeServer.beginTransaction();

            SelfBubbleId<SelfBubble> selfBubbleId_101 = new SelfBubbleId<>(101L);
            SelfBubbleId<SelfBubble> selfBubbleId_102 = new SelfBubbleId<>(102L);
            SelfBubbleId<SelfBubble> selfBubbleId_103 = new SelfBubbleId<>(103L);
            SelfBubble b_101 = new SelfBubble(selfBubbleId_101);
            SelfBubble b_102 = new SelfBubble(selfBubbleId_102);
            SelfBubble b_103 = new SelfBubble(selfBubbleId_103);
            b_102.setRefId(b_101.getId());

            TestBubble testBubble = new TestBubble(TestBubbleId_101);

            // b_102 refererer b_101 og man bør derfor få referanse feil. Men dersom b_101 og b_102 er med i samme batch går det
            // greit likevel. Har derfor langt inn testBubble for å bryte batchen. For det skal virke må Store ikke
            // stokke om på rekkefølgen. Derfor har SelfBubble og TestBubble samme sorteringsindex.
            UnitOfWork unitOfWork = storeServer.beginUnitOfWork();
            storeServer.insert(b_102);
            storeServer.insert(testBubble);
            storeServer.insert(b_101);
            storeServer.insert(b_103);

            logger.warn("Denne test forsøker å bryte integritetsskranke 'SELF_FK'. Hibernate (org.hibernate.util.JDBCExceptionReporter) logger en ERROR om dette, hvilket er ok.");
            // Uten denne går det ikke bra:
            // storeServer.rescheduleModification(b_102.getId());
            storeServer.commitUnitOfWork(unitOfWork);
            storeServer.flush();
            fail("Skulle ha fått feil på flush: 'integritetsskranken (FREHEN_GB.SELF_FK) er overtrådt - hovednøkkel ikke funnet'");
        } catch (ConstraintViolationException e) {
            // Tidligere versjoner av oracle enn 11.2.0.3.0 gir ikke constraintfeil hvis objekter er i samme batch
        } finally {
            storeServer.rollbackTransaction();
        }
    }


    /**
     * Denne test er lagt til rette for å kunne bruker JProfiler.
     */
    public void testEvictAllAfterCommitRemovesAllObjectsReadOnly() {
        MemoryProfileUtil.setEnabled(false);
        MemoryProfileUtil.setUseMessageBox();
        ParentBubble Parent_1 = storeServer.get(parentBubbleId_1);
        // I JProfiler 'Record Memory'
        MemoryProfileUtil.promptAndWait("Enable Memory Record");
        ParentBubble Parent_2 = storeServer.get(parentBubbleId_2);
        storeServer.get(testBubbleWithHistoryId_10_OLD);
        storeServer.get(testBubbleWithHistoryId_10_S2);
        storeServer.get(testBubbleWithHistoryId_10_S3);
        MemoryProfileUtil.promptAndWait("Take Heap Snapshot 1");
        storeServer.evictAll();
        MemoryProfileUtil.promptAndWait("Take Heap Smapshot 2");
        persistenceSessionManager.verifySessionIsEmpty();
    }

    /**
     * Denne test er lagt til rette for å kunne bruker JProfiler.
     */
    public void testEvictAllAfterCommitRemovesAllObjectsAfterCommit() {
        MemoryProfileUtil.setEnabled(false);
        MemoryProfileUtil.setUseMessageBox();
        ParentBubble Parent_1 = storeServer.get(parentBubbleId_1);
        //I JProfiler 'Record Memory'
        MemoryProfileUtil.promptAndWait("Enable Memory Record");
        ParentBubble Parent_2 = storeServer.get(parentBubbleId_2);
        storeServer.get(testBubbleWithHistoryId_10_OLD);
        storeServer.get(testBubbleWithHistoryId_10_S2);
        storeServer.get(testBubbleWithHistoryId_10_S3);
        MemoryProfileUtil.promptAndWait("Take Heap Snapshot 1");
        testInsert();
        storeServer.evictAll();
        MemoryProfileUtil.promptAndWait("Take Heap Smapshot 2");
        persistenceSessionManager.verifySessionIsEmpty();
    }

    /**
     * Forventer at cachet data tømmes helt, dvs at ny instans må leses inn etter clear()
     */
    public void testClearForReadOnly() {
        ParentBubble Parent_1 = storeServer.get(parentBubbleId_1);
        ParentBubble Parent_2 = storeServer.get(parentBubbleId_2);
        storeServer.get(testBubbleWithHistoryId_10_OLD);
        storeServer.get(testBubbleWithHistoryId_10_S2);
        storeServer.get(testBubbleWithHistoryId_10_S3);
        storeServer.clear();
        persistenceSessionManager.verifySessionIsEmpty();
        ParentBubble Parent_1a = storeServer.get(parentBubbleId_1);
        assertNotSame(Parent_1, Parent_1a);
    }

    /**
     * Forventer at cachet data tømmes helt, dvs at ny instans må leses inn etter clear().
     * Dette skal også gjelde objekter som nettopp har blitt opprettet
     */
    public void testClearAfterCommit() {
        ParentBubble parent_1 = storeServer.get(parentBubbleId_1);
        ParentBubble parent_2 = storeServer.get(parentBubbleId_2);
        storeServer.get(testBubbleWithHistoryId_10_OLD);
        storeServer.get(testBubbleWithHistoryId_10_S2);
        storeServer.get(testBubbleWithHistoryId_10_S3);
        TestBubble testBubble_101 = new TestBubble(TestBubbleId_101);
        testBubble_101.setText("Insert 1");
        storeServer.beginTransaction();
        storeServer.insert(testBubble_101);
        storeServer.commitTransaction();
        assertEquals(countInDatabase(persistenceSessionForSnapshot, TestBubbleId_101), 1);
        storeServer.clear();
        persistenceSessionManager.verifySessionIsEmpty();
        assertNotSame(parent_1, storeServer.get(parentBubbleId_1));
        assertNotSame(testBubble_101, storeServer.get(TestBubbleId_101));
    }

    /**
     * Forventer at cachet data tømmes helt og at endret data er uendret og alt leses på nytt etter clear()
     */
    public void testIsClearedAfterRollback() {
        ParentBubble parent_1 = storeServer.get(parentBubbleId_1);
        ParentBubble parent_2 = storeServer.get(parentBubbleId_2);
        storeServer.get(testBubbleWithHistoryId_10_OLD);
        storeServer.get(testBubbleWithHistoryId_10_S2);
        storeServer.get(testBubbleWithHistoryId_10_S3);
        TestBubble testBubble_101 = new TestBubble(TestBubbleId_101);
        testBubble_101.setText("Insert 1");
        storeServer.beginTransaction();
        storeServer.insert(testBubble_101);
        storeServer.rollbackTransaction();
        assertEquals(countInDatabase(persistenceSessionForSnapshot, TestBubbleId_101), 0);
        persistenceSessionManager.verifySessionIsEmpty();
        assertNotSame(parent_1, storeServer.get(parentBubbleId_1));
        try {
            storeServer.get(TestBubbleId_101);
            failBecauseExceptionWasNotThrown(ObjectNotFoundException.class);
        } catch (ObjectNotFoundException e) {
            assertEquals(e.getNotFoundId(), TestBubbleId_101);
        }
    }

    /**
     * Forventer at cachet reaonly data tømmes helt og at låst data forblir uendret ved kall til clear()
     */
    public void testClearedWhileInTranaction() {
        ParentBubble parent_1 = storeServer.get(parentBubbleId_1);
        ParentBubble parent_2 = storeServer.get(parentBubbleId_2);
        storeServer.get(testBubbleWithHistoryId_10_OLD);
        storeServer.get(testBubbleWithHistoryId_10_S2);
        storeServer.get(testBubbleWithHistoryId_10_S3);
        TestBubble testBubble_101 = new TestBubble(TestBubbleId_101);
        testBubble_101.setText("Insert 1");
        storeServer.beginTransaction();
        storeServer.insert(testBubble_101);
        TestBubbleWithHistory TestBubbleWithHistory_101 = storeServer.lock(testBubbleWithHistoryId_11_CURRENT);
        TestBubbleWithHistory_101.setText("abc123");
        storeServer.update(TestBubbleWithHistory_101);
        storeServer.clear();
        assertEquals(countInDatabase(persistenceSessionForSnapshot, TestBubbleId_101), 1);
        assertSame(TestBubbleWithHistory_101, storeServer.get(testBubbleWithHistoryId_11_CURRENT));
        assertNotSame(parent_1, storeServer.get(parentBubbleId_1));
        storeServer.rollbackTransaction();
    }

    /**
     * Tester forsøkvis sletting av boble som gir constraint feil ved sletting. Tester at endringer
     * gjort før og etter attemptDelete kommer med når transaksjonen committes.
     */
    public void testAttemptDelete() {
        //Opprett 1 parentbubble og 2 child bubbles
        ParentBubbleId<ParentBubble> parentBubbleId_201 = new ParentBubbleId<>(201);
        ParentBubbleId<ParentBubble> parentBubbleId_202 = new ParentBubbleId<>(202);
        ChildBubbleId<ChildBubble> ChildBubbleId_201 = new ChildBubbleId<>(201);
        ChildBubbleId<ChildBubble> ChildBubbleId_202 = new ChildBubbleId<>(202);

        ParentBubble parentBubble_201 = new ParentBubble(parentBubbleId_201);
        parentBubble_201.setText("Insert parent 1");

        ChildBubble childBubble_201 = new ChildBubble(ChildBubbleId_201);
        childBubble_201.setText("Insert child 1");
        parentBubble_201.addChild(ChildBubbleId_201, (long) 201);

        ChildBubble childBubble_202 = new ChildBubble(ChildBubbleId_202);
        childBubble_202.setText("Insert child 2");
        childBubble_202.setTestBubbleId(new TestBubbleId<>(2));

        storeServer.beginTransaction();
        storeServer.insert(childBubble_201);
        storeServer.insert(parentBubble_201);
        storeServer.insert(childBubble_202);
        storeServer.commitTransaction();
        storeServer.clear();

        // Opprett parent 202 og legg inn child 202, set text() i parent 201 og 202 samt forsøk å endre text i child 201 og deretter slett child 201
        storeServer.beginTransaction();
        ParentBubble parentBubble_202 = new ParentBubble(parentBubbleId_202);
        parentBubble_202.setText("Insert parent 2");
        parentBubble_202.addChild(ChildBubbleId_202, (long) 202);
        storeServer.insert(parentBubble_202);
        childBubble_201 = storeServer.lock(childBubble_201.getId());
        String NEW_TEXT = "Oppdatert ifm attemptDelete";
        try {
            parentBubble_201 = storeServer.lock(parentBubble_201.getId());
            parentBubble_201.setText(NEW_TEXT);
            childBubble_201.setText(NEW_TEXT);
            storeServer.update(childBubble_201);
            storeServer.update(parentBubble_201);

            // Har nå endret noe i parent 201 og child 201
            logger.warn("Denne test forsøker å bryte integritetsskranke 'FK_CHILDFORPARENT_CHILD'. Hibernate (org.hibernate.util.JDBCExceptionReporter) logger en ERROR om dette, hvilket er ok.");
            storeServer.attemptDelete(childBubble_201.getBubbleId());
            Assert.fail("forventet exception");
        } catch (AttemptDeleteException e) {
            assertEquals(e.getBubbleId(), childBubble_201.getId());
        }
        // Check at child 201 forsatt er låst og ikke er markert som slettet i Store
        assertTrue(storeServer.isLocked(childBubble_201.getId()));
        assertTrue(storeServer.getUpdatedIds().contains(childBubble_201.getId()));
        assertThat(storeServer.getDeletedIds()).doesNotContain(childBubble_201.getId());
        parentBubble_202.setText(NEW_TEXT);
        storeServer.update(parentBubble_202);
        storeServer.commitTransaction();
        storeServer.clear();

        assertEquals(storeServer.get(childBubble_201.getId()).getText(), NEW_TEXT);
        ParentBubble parentBubble_201_NY = storeServer.get(parentBubble_201.getId());
        assertNotSame(parentBubble_201, parentBubble_201_NY);
        assertEquals(parentBubble_201_NY.getText(), NEW_TEXT);

        assertFalse(storeServer.isLocked(childBubble_201.getId()));
        assertEquals(storeServer.get(childBubble_201.getId()).getText(), NEW_TEXT);

    }

    public void testAttemptDeleteManyCallsWithFail() {
        int MAX_SAVEPOINTS = 1;
        ParentBubbleId<ParentBubble> parentBubbleId_201 = new ParentBubbleId<>(201);
        ChildBubbleId<ChildBubble> ChildBubbleId_201 = new ChildBubbleId<>(201);

        ParentBubble parentBubble_201 = new ParentBubble(parentBubbleId_201);
        parentBubble_201.setText("Insert parent 1");

        ChildBubble childBubble_201 = new ChildBubble(ChildBubbleId_201);
        childBubble_201.setText("Insert child 1");
        parentBubble_201.addChild(ChildBubbleId_201, (long) 201);

        storeServer.beginTransaction();
        storeServer.insert(childBubble_201);
        storeServer.insert(parentBubble_201);
        storeServer.commitTransaction();
        storeServer.clear();

        storeServer.beginTransaction();
        storeServer.lock(childBubble_201.getBubbleId());
        logger.warn("Denne test forsøker å bryte integritetsskranke 'FK_CHILDFORPARENT_CHILD'. Hibernate (org.hibernate.util.JDBCExceptionReporter) logger en ERROR om dette, hvilket er ok.");
        for (int i = 1000; i < 1000+MAX_SAVEPOINTS; i++) {
            ParentBubble parentBubble = new ParentBubble(new ParentBubbleId(i));
            parentBubble.setText("Insert parent" + i);
            storeServer.insert(parentBubble);
            try {
                storeServer.attemptDelete(childBubble_201.getBubbleId());
                Assert.fail();
            } catch (AttemptDeleteException e) {
                // OK
            }
            parentBubble.setText("Updated parent " + i );
            storeServer.update(parentBubble);
        }
        storeServer.commitTransaction();
        storeServer.clear();
        assertEquals(storeServer.get(new ParentBubbleId<>(1000)).getText(), "Updated parent " + 1000);
        assertEquals(storeServer.get(new ParentBubbleId<>(1000 + MAX_SAVEPOINTS - 1)).getText(),"Updated parent " + (1000 + MAX_SAVEPOINTS-1));
    }

    public void testAttemptDeleteManyCallsWithoutFail() {
        int MAX_SAVEPOINTS = 5;
        ParentBubbleId<ParentBubble> parentBubbleId_201 = new ParentBubbleId<>(201);
        ChildBubbleId<ChildBubble> ChildBubbleId_201 = new ChildBubbleId<>(201);

        ParentBubble parentBubble_201 = new ParentBubble(parentBubbleId_201);
        parentBubble_201.setText("Insert parent 1");

        ChildBubble childBubble_201 = new ChildBubble(ChildBubbleId_201);
        childBubble_201.setText("Insert child 1");
        parentBubble_201.addChild(ChildBubbleId_201, (long) 201);

        storeServer.beginTransaction();
        storeServer.insert(childBubble_201);
        storeServer.insert(parentBubble_201);
        storeServer.commitTransaction();
        storeServer.clear();

        storeServer.beginTransaction();
        storeServer.lock(childBubble_201.getBubbleId());
        ParentBubble previousParent= new ParentBubble(new ParentBubbleId(999));
        previousParent.setText("Insert parent" + 999);
        storeServer.insert(previousParent);
        for (int i = 1000; i < 1000+MAX_SAVEPOINTS; i++) {
            ParentBubble parentBubble = new ParentBubble(new ParentBubbleId(i));
            parentBubble.setText("Insert parent" + i);
            storeServer.insert(parentBubble);
            try {
                storeServer.attemptDelete(previousParent.getId());
            } catch (AttemptDeleteException e) {
                Assert.fail();
            }
            parentBubble.setText("Updated parent " + i );
            storeServer.update(parentBubble);
            previousParent=parentBubble;
        }
        storeServer.commitTransaction();
        storeServer.clear();
    }
}
