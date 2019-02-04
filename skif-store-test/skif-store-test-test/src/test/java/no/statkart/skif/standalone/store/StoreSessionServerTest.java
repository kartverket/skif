package no.statkart.skif.standalone.store;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provides;
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
import no.statkart.skif.store.BubbleDependencyComparator;
import no.statkart.skif.store.BubbleId;
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
import no.statkart.skif.store.persistence.PersistenceSessionManager;
import no.statkart.skif.store.persistence.hibernate.HibernatePersistenceSessionMaster;
import no.statkart.skif.store.persistence.hibernate.HibernatePersistenceSessionMasterImpl;
import no.statkart.skif.store.persistence.hibernate.HibernateSessionFactoryBuilder;
import no.statkart.skif.store.persistence.hibernate.HibernateSessionFactoryManagerBundle;
import no.statkart.skif.store.persistence.kodeliste.DefaultKodelistePersistenceSessionSubtypeHandler;
import no.statkart.skif.store.persistence.kodeliste.EnumKodelisteManager;
import no.statkart.skif.storetest.domain.basic.Simple;
import no.statkart.skif.storetest.domain.basic.SimpleId;
import no.statkart.skif.storetest.domain.demo.koder.AEnumKodeId;
import no.statkart.skif.storetest.domain.demo.koder.BEnumKodeId;
import no.statkart.skif.storetest.domain.demo.koder.SEnumKodeId;
import no.statkart.skif.storetest.domain.standalone.ChildBubble;
import no.statkart.skif.storetest.domain.standalone.ChildBubbleEmptyColOptimizer;
import no.statkart.skif.storetest.domain.standalone.ChildBubbleEmptyColOptimizerId;
import no.statkart.skif.storetest.domain.standalone.ChildBubbleId;
import no.statkart.skif.storetest.domain.standalone.FilteredBubble;
import no.statkart.skif.storetest.domain.standalone.FilteredBubbleId;
import no.statkart.skif.storetest.domain.standalone.ParentBubble;
import no.statkart.skif.storetest.domain.standalone.ParentBubbleEmptyColOptimizer;
import no.statkart.skif.storetest.domain.standalone.ParentBubbleEmptyColOptimizerId;
import no.statkart.skif.storetest.domain.standalone.ParentBubbleEmptyColOptimizerSub1;
import no.statkart.skif.storetest.domain.standalone.ParentBubbleEmptyColOptimizerSub1Id;
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
import org.assertj.core.api.Assertions;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.exception.ConstraintViolationException;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Collections;
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
import static no.statkart.skif.standalone.util.testsupport.StandAloneTestHelper.deletePreviouslyWrittenTestBubbles;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.extractProperty;
import static org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNotSame;
import static org.testng.Assert.assertNull;
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

    private ChildBubbleEmptyColOptimizerId<ChildBubbleEmptyColOptimizer> childBubbleEmptyColOptimizerId_11 = new ChildBubbleEmptyColOptimizerId<>(11L);
    private ChildBubbleEmptyColOptimizerId<ChildBubbleEmptyColOptimizer> childBubbleEmptyColOptimizerId_21 = new ChildBubbleEmptyColOptimizerId<>(21L);
    private ChildBubbleEmptyColOptimizerId<ChildBubbleEmptyColOptimizer> childBubbleEmptyColOptimizerId_31 = new ChildBubbleEmptyColOptimizerId<>(31L);
    private ChildBubbleEmptyColOptimizerId<ChildBubbleEmptyColOptimizer> childBubbleEmptyColOptimizerId_41 = new ChildBubbleEmptyColOptimizerId<>(41L);
    private ChildBubbleEmptyColOptimizerId<ChildBubbleEmptyColOptimizer> childBubbleEmptyColOptimizerId_61 = new ChildBubbleEmptyColOptimizerId<>(61L);

    private ParentBubbleEmptyColOptimizerId<ParentBubbleEmptyColOptimizer> parentBubbleEmptyColOptimizerId_1 = new ParentBubbleEmptyColOptimizerId<>(1L);
    private ParentBubbleEmptyColOptimizerId<ParentBubbleEmptyColOptimizer> parentBubbleEmptyColOptimizerId_2 = new ParentBubbleEmptyColOptimizerId<>(2L);
    private ParentBubbleEmptyColOptimizerId<ParentBubbleEmptyColOptimizer> parentBubbleEmptyColOptimizerId_3 = new ParentBubbleEmptyColOptimizerId<>(3L);
    private ParentBubbleEmptyColOptimizerId<ParentBubbleEmptyColOptimizer> parentBubbleEmptyColOptimizerId_4 = new ParentBubbleEmptyColOptimizerId<>(4L);
    private ParentBubbleEmptyColOptimizerId<ParentBubbleEmptyColOptimizer> parentBubbleEmptyColOptimizerId_5 = new ParentBubbleEmptyColOptimizerId<>(5L);
    private ParentBubbleEmptyColOptimizerSub1Id<ParentBubbleEmptyColOptimizerSub1> parentBubbleEmptyColOptimizerId_6 = new ParentBubbleEmptyColOptimizerSub1Id<>(6L);
    private ParentBubbleEmptyColOptimizerSub1Id<ParentBubbleEmptyColOptimizerSub1> parentBubbleEmptyColOptimizerId_7 = new ParentBubbleEmptyColOptimizerSub1Id<>(7L);
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

        HibernatePersistenceSessionMasterImpl masterCurrent = new HibernatePersistenceSessionMasterImpl(
                sessionFactoryManagerBundle.getBundle().get(0)
        );
        HibernatePersistenceSessionMasterImpl masterOld = new HibernatePersistenceSessionMasterImpl(
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
        final BubbleDependencyComparator dependencyComparator = StandAloneTestHelper.getBubbleDependencyComparator();

        Injector fakeInjector = Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                bind(IdService.class).toProvider(Providers.<IdService>of(null));
                TypeLiteral<MemoryLocker<Long>> memoryLockerLongType = SkifUtil.typeLiteral(MemoryLocker.class, Long.class);
                bind(memoryLockerLongType).in(Singleton.class);
                bind((TypeLiteral<DBLockerService>)SkifUtil.typeLiteral(DBLockerService.class, Long.class)).to(memoryLockerLongType);
                bind((TypeLiteral<DBLockerInTransactionService>)SkifUtil.typeLiteral(DBLockerInTransactionService.class, Long.class)).to(memoryLockerLongType);
                bind(LockerStrategy.class).to(TransactionalLockerStrategy.class);
                bind(TransactionalLockerStrategy.class).in(Singleton.class);
                bind(Configuration.class).toInstance(new SkifServerConfiguration());
                ServiceRequestContext serviceRequestContext = new ServiceRequestContext(new PrincipalImpl("test"), 0);
                bind(ServiceRequestContext.class).toInstance(serviceRequestContext);
                bind(PersistenceSessionManager.class).toInstance(persistenceSessionManager);
            }

            @Provides
            private StoreSessionServer createStoreSessionServer(Injector injector,
                                                                LockerStrategy lockerStrategy,
                                                                TestBubbleFilter testBubbleFilter,
                                                                TestBubbleFinishFilter testBubbleFinishFilter) {
                List<StoreSessionReadListener> readListeners = ImmutableList.of(testBubbleFilter);
                List<StoreSessionWriteListener> writeListeners = ImmutableList.of(
                        testBubbleFilter
                );
                List<StoreSessionFinishListener> finishListeners = ImmutableList.of(
                        testBubbleFinishFilter,
                        new UpdatingFinishListener() /* StoreListener som endre på boble som bruker empty collection flag */
                );

                StoreSessionServer storeSessionServer = new StoreSessionServer(persistenceSessionManager, Providers.<VersionFinder>of(null), Providers.of(SnapshotVersion.CURRENT), lockerStrategy, dependencyComparator, readListeners, writeListeners, finishListeners);
                injector.injectMembers(storeSessionServer);
                return storeSessionServer;
            }
            @Provides
            private StoreServer createStoreServer(StoreSessionServer storeSessionServer, Injector injector) {
                StoreServer storeServer = new StoreServer(storeSessionServer, injector);
                injector.injectMembers(storeServer);
                return storeServer;
            }

        });

        storeServer = fakeInjector.getInstance(StoreServer.class);
        deletePreviouslyWrittenTestBubbles(persistenceSessionManager.getForSnapshotVersion(SnapshotVersion.CURRENT));
    }

    /**
     * Denne listener endre ParentBubbleEmptyColOptimizer med id 4.
     */
    static class UpdatingFinishListener implements StoreSessionFinishListener {

        @Override
        public void onFinish(StoreServer storeServer) {
            BubbleId<?> bubbleId = storeServer.getUpdatedIds().stream()
                    .filter(id -> id.equals(new ParentBubbleEmptyColOptimizerId<>(4)))
                    .findFirst().orElse(null);
            if (bubbleId!=null) {
                ParentBubbleEmptyColOptimizer parent = (ParentBubbleEmptyColOptimizer) storeServer.get(bubbleId);
                parent.setText("UpdatingFinishListener changed collection children1Ids");
                parent.getChildren1Ids().clear();
            }
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

        // Test at multippel uthenting gir samme objekter
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

    public void testInsertFilteredKlasse_aktivt_filter() {
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
//        assertSame(storeServer.get(TestBubbleId_101), copy);
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
    @SuppressWarnings("JpaQlInspection")
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
        TestBubbleId<?> id = testBubbleIdFinder();
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
//        assertSame(storeServer.get(TestBubbleId_101_CURRENT), copy);
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
//        assertSame(storeServer.get(TestBubbleId_101), copy);
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
//        assertSame(storeServer.get(TestBubbleId_101_CURRENT), copy);
        storeServer.commitUnitOfWork(unitOfWork2);
        storeServer.commitUnitOfWork(unitOfWork1);
        assertNotFound(storeServer, TestBubbleId_101_CURRENT);
        storeServer.commitTransaction();
        assertEquals(countInDatabase(persistenceSessionForSnapshot, TestBubbleId_101), 0);
    }

    public void testInsertUpdateDeleteObjectViaMultiNestedUnitOfWork_AbortInner() {
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
        MemoryProfileUtil.promptAndWait("Take Heap Snapshot 2");
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
        MemoryProfileUtil.promptAndWait("Take Heap Snapshot 2");
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
     * Forventer at cachet readonly data tømmes helt og at låst data forblir uendret ved kall til clear()
     */
    public void testClearedWhileInTransaction() {
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
     * Tester forsøksvis sletting av boble som gir constraint feil ved sletting. Tester at endringer
     * gjort før og etter attemptDelete kommer med når transaksjonen committes.
     */
    public void testAttemptDelete() {
        //Opprett 1 parent bubble og 2 child bubbles
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
        // Check at child 201 fortsatt er låst og ikke er markert som slettet i Store
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
        assertEquals(storeServer.get(new ParentBubbleId<ParentBubble>(1000)).getText(), "Updated parent " + 1000);
        assertEquals(storeServer.get(new ParentBubbleId<ParentBubble>(1000 + MAX_SAVEPOINTS - 1)).getText(),"Updated parent " + (1000 + MAX_SAVEPOINTS-1));
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

    public void testLockSingleUngotten() {
        SimpleId<?> id = new SimpleId<>(17L);
        Simple object = new Simple(id);

        PersistenceSessionManager persistenceSessionManager = Mockito.mock(PersistenceSessionManager.class);
        Mockito.doReturn(object).when(persistenceSessionManager).refresh(id);
        LockerStrategy lockerStrategy = Mockito.mock(LockerStrategy.class);
        Mockito.doReturn(true).when(lockerStrategy).lock(id);
        IdService idService = Mockito.mock(IdService.class);

        Injector injector = Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                bind(IdService.class).toInstance(idService);
            }
        });

        StoreSessionServer storeSessionServer = new StoreSessionServer(
                persistenceSessionManager,
                Providers.of(null),
                Providers.of(SnapshotVersion.CURRENT),
                lockerStrategy,
                StandAloneTestHelper.getBubbleDependencyComparator(),
                null,
                null,
                null
        );

        StoreServer storeServer = new StoreServer(storeSessionServer, injector);
        Simple locked = storeServer.lock(id);

        assertSame(locked, object);
        Mockito.verify(persistenceSessionManager).refresh(id);
        Mockito.verify(lockerStrategy).lock(id);
        Mockito.verifyNoMoreInteractions(persistenceSessionManager, lockerStrategy, idService);

        Simple locked2 = storeServer.lock(id);
        assertSame(locked2, object);
        Mockito.verifyNoMoreInteractions(persistenceSessionManager, lockerStrategy, idService);
    }

    public void testLockSingleGotten() {
        SimpleId<?> id = new SimpleId<>(17L);
        Simple object = new Simple(id);

        PersistenceSessionManager persistenceSessionManager = Mockito.mock(PersistenceSessionManager.class);
        Mockito.doReturn(object).when(persistenceSessionManager).get(id);
        Mockito.doAnswer(invocation -> {
            Simple object1 = (Simple) invocation.getArguments()[0];
            object1.setText("A");
            return null;
        }).when(persistenceSessionManager).refresh(object);
        LockerStrategy lockerStrategy = Mockito.mock(LockerStrategy.class);
        Mockito.doReturn(true).when(lockerStrategy).lock(id);
        IdService idService = Mockito.mock(IdService.class);

        Injector injector = Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                bind(IdService.class).toInstance(idService);
            }
        });

        StoreSessionServer storeSessionServer = new StoreSessionServer(
                persistenceSessionManager,
                Providers.of(null),
                Providers.of(SnapshotVersion.CURRENT),
                lockerStrategy,
                StandAloneTestHelper.getBubbleDependencyComparator(),
                null,
                null,
                null
        );

        StoreServer storeServer = new StoreServer(storeSessionServer, injector);

        Simple gotten = storeServer.get(id);
        assertSame(gotten, object);
        assertNull(gotten.getText());
        Mockito.verify(persistenceSessionManager).get(id);

        Simple locked = storeServer.lock(id);

        assertSame(locked, object);
        assertEquals(locked.getText(), "A");
        Mockito.verify(persistenceSessionManager).refresh(object);
        Mockito.verify(lockerStrategy).lock(id);
        Mockito.verifyNoMoreInteractions(persistenceSessionManager, lockerStrategy, idService);
    }

    public void testLockSinglePrelocked() {
        SimpleId<?> id = new SimpleId<>(17L);
        Simple object = new Simple(id);

        PersistenceSessionManager persistenceSessionManager = Mockito.mock(PersistenceSessionManager.class);
        Mockito.doReturn(object).when(persistenceSessionManager).get(id);
        LockerStrategy lockerStrategy = Mockito.mock(LockerStrategy.class);
        Mockito.doReturn(false).when(lockerStrategy).lock(id);
        IdService idService = Mockito.mock(IdService.class);

        Injector injector = Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                bind(IdService.class).toInstance(idService);
            }
        });

        StoreSessionServer storeSessionServer = new StoreSessionServer(
                persistenceSessionManager,
                Providers.of(null),
                Providers.of(SnapshotVersion.CURRENT),
                lockerStrategy,
                StandAloneTestHelper.getBubbleDependencyComparator(),
                null,
                null,
                null
        );

        StoreServer storeServer = new StoreServer(storeSessionServer, injector);

        Simple gotten = storeServer.get(id);
        assertSame(gotten, object);
        Mockito.verify(persistenceSessionManager).get(id);

        Simple locked = storeServer.lock(id);

        assertSame(locked, object);
        Mockito.verify(lockerStrategy).lock(id);
        Mockito.verifyNoMoreInteractions(persistenceSessionManager, lockerStrategy, idService);
    }

    // At refresh blir kalt enkeltvis er en implementasjonsdetalj, ikke slik det skal være
    public void testLockMultipleUngotten() {
        SimpleId<?> id1 = new SimpleId<>(1L);
        SimpleId<?> id2 = new SimpleId<>(2L);
        Simple object1 = new Simple(id1, "A");
        Simple object2 = new Simple(id2, "B");

        PersistenceSessionManager persistenceSessionManager = Mockito.mock(PersistenceSessionManager.class);
        Mockito.doReturn(object1).when(persistenceSessionManager).refresh(id1);
        Mockito.doReturn(object2).when(persistenceSessionManager).refresh(id2);
        LockerStrategy lockerStrategy = Mockito.mock(LockerStrategy.class);
        Mockito.doReturn(ImmutableSet.of(id1, id2)).when(lockerStrategy).lock(ImmutableSet.of(id1, id2));
        IdService idService = Mockito.mock(IdService.class);

        Injector injector = Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                bind(IdService.class).toInstance(idService);
            }
        });

        StoreSessionServer storeSessionServer = new StoreSessionServer(
                persistenceSessionManager,
                Providers.of(null),
                Providers.of(SnapshotVersion.CURRENT),
                lockerStrategy,
                StandAloneTestHelper.getBubbleDependencyComparator(),
                null,
                null,
                null
        );

        StoreServer storeServer = new StoreServer(storeSessionServer, injector);
        Set<Simple> locked = storeServer.lock(ImmutableSet.of(id1, id2));
        ImmutableMap<? extends SimpleId<?>, Simple> lockedMap = Maps.uniqueIndex(locked, Simple::getId);

        Assertions.assertThat(locked).containsOnly(object1, object2);
        assertSame(lockedMap.get(id1), object1);
        assertSame(lockedMap.get(id2), object2);
        Mockito.verify(persistenceSessionManager).refresh(id1);
        Mockito.verify(persistenceSessionManager).refresh(id2);
        Mockito.verify(lockerStrategy).lock(ImmutableSet.of(id1, id2));
        Mockito.verifyNoMoreInteractions(persistenceSessionManager, lockerStrategy, idService);

        Set<Simple> locked2 = storeServer.lock(ImmutableSet.of(id1, id2));
        Assertions.assertThat(locked2).containsOnly(object1, object2);
        ImmutableMap<? extends SimpleId<?>, Simple> lockedMap2 = Maps.uniqueIndex(locked2, Simple::getId);
        assertSame(lockedMap.get(id1), object1);
        assertSame(lockedMap.get(id2), object2);
        Mockito.verifyNoMoreInteractions(persistenceSessionManager, lockerStrategy, idService);
    }

    public void testLockMultipleOneGotten() {
        SimpleId<?> id1 = new SimpleId<>(1L);
        SimpleId<?> id2 = new SimpleId<>(2L);
        Simple object1 = new Simple(id1);
        Simple object2 = new Simple(id2, "B");

        PersistenceSessionManager persistenceSessionManager = Mockito.mock(PersistenceSessionManager.class);
        Mockito.doReturn(object1).when(persistenceSessionManager).get(id1);
        Mockito.doReturn(object2).when(persistenceSessionManager).refresh(id2);
        Mockito.doAnswer(invocation -> {
            Simple object = (Simple) invocation.getArguments()[0];
            object.setText("A");
            return null;
        }).when(persistenceSessionManager).refresh(object1);
        LockerStrategy lockerStrategy = Mockito.mock(LockerStrategy.class);
        Mockito.doReturn(ImmutableSet.of(id1, id2)).when(lockerStrategy).lock(ImmutableSet.of(id1, id2));
        IdService idService = Mockito.mock(IdService.class);

        Injector injector = Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                bind(IdService.class).toInstance(idService);
            }
        });

        StoreSessionServer storeSessionServer = new StoreSessionServer(
                persistenceSessionManager,
                Providers.of(null),
                Providers.of(SnapshotVersion.CURRENT),
                lockerStrategy,
                StandAloneTestHelper.getBubbleDependencyComparator(),
                null,
                null,
                null
        );

        StoreServer storeServer = new StoreServer(storeSessionServer, injector);

        Simple gotten = storeServer.get(id1);
        assertSame(gotten, object1);
        assertNull(gotten.getText());
        Mockito.verify(persistenceSessionManager).get(id1);

        Set<Simple> locked = storeServer.lock(ImmutableSet.of(id1, id2));
        ImmutableMap<? extends SimpleId<?>, Simple> lockedMap = Maps.uniqueIndex(locked, Simple::getId);

        Assertions.assertThat(locked).containsOnly(object1, object2);
        Simple locked1 = lockedMap.get(id1);
        Simple locked2 = lockedMap.get(id2);
        assertSame(locked1, object1);
        assertSame(locked2, object2);
        assertEquals(locked1.getText(), "A");
        assertEquals(locked2.getText(), "B");
        Mockito.verify(persistenceSessionManager).refresh(object1);
        Mockito.verify(persistenceSessionManager).refresh(id2);
        Mockito.verify(lockerStrategy).lock(ImmutableSet.of(id1, id2));
        Mockito.verifyNoMoreInteractions(persistenceSessionManager, lockerStrategy, idService);
    }

    public void testLockMultipleAllPrelocked() {
        SimpleId<?> id1 = new SimpleId<>(1L);
        SimpleId<?> id2 = new SimpleId<>(2L);
        Simple object1 = new Simple(id1, "A");
        Simple object2 = new Simple(id2, "B");

        PersistenceSessionManager persistenceSessionManager = Mockito.mock(PersistenceSessionManager.class);
        Mockito.doReturn(ImmutableSet.of(object1, object2)).when(persistenceSessionManager).get(ImmutableSet.of(id1, id2));
        LockerStrategy lockerStrategy = Mockito.mock(LockerStrategy.class);
        Mockito.doReturn(Collections.emptySet()).when(lockerStrategy).lock(ImmutableSet.of(id1, id2));
        IdService idService = Mockito.mock(IdService.class);

        Injector injector = Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                bind(IdService.class).toInstance(idService);
            }
        });

        StoreSessionServer storeSessionServer = new StoreSessionServer(
                persistenceSessionManager,
                Providers.of(null),
                Providers.of(SnapshotVersion.CURRENT),
                lockerStrategy,
                StandAloneTestHelper.getBubbleDependencyComparator(),
                null,
                null,
                null
        );

        StoreServer storeServer = new StoreServer(storeSessionServer, injector);

        Set<Simple> gotten = storeServer.get(ImmutableSet.of(id1, id2));
        Assertions.assertThat(gotten).containsOnly(object1, object2);
        ImmutableMap<? extends SimpleId<?>, Simple> gottenMap = Maps.uniqueIndex(gotten, Simple::getId);
        assertSame(gottenMap.get(id1), object1);
        assertSame(gottenMap.get(id2), object2);
        Mockito.verify(persistenceSessionManager).get(ImmutableSet.of(id1, id2));

        Set<Simple> locked = storeServer.lock(ImmutableSet.of(id1, id2));
        ImmutableMap<? extends SimpleId<?>, Simple> lockedMap = Maps.uniqueIndex(locked, Simple::getId);

        Assertions.assertThat(locked).containsOnly(object1, object2);
        assertSame(lockedMap.get(id1), object1);
        assertSame(lockedMap.get(id2), object2);
        Mockito.verify(lockerStrategy).lock(ImmutableSet.of(id1, id2));
        Mockito.verifyNoMoreInteractions(persistenceSessionManager, lockerStrategy, idService);
    }

    public void testLockMultipleOnePrelockedOtherUngotten() {
        SimpleId<?> id1 = new SimpleId<>(1L);
        SimpleId<?> id2 = new SimpleId<>(2L);
        Simple object1 = new Simple(id1, "A");
        Simple object2 = new Simple(id2, "B");

        PersistenceSessionManager persistenceSessionManager = Mockito.mock(PersistenceSessionManager.class);
        Mockito.doReturn(object1).when(persistenceSessionManager).get(id1);
        Mockito.doReturn(object2).when(persistenceSessionManager).refresh(id2);
        LockerStrategy lockerStrategy = Mockito.mock(LockerStrategy.class);
        Mockito.doReturn(ImmutableSet.of(id2)).when(lockerStrategy).lock(ImmutableSet.of(id1, id2));
        IdService idService = Mockito.mock(IdService.class);

        Injector injector = Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                bind(IdService.class).toInstance(idService);
            }
        });

        StoreSessionServer storeSessionServer = new StoreSessionServer(
                persistenceSessionManager,
                Providers.of(null),
                Providers.of(SnapshotVersion.CURRENT),
                lockerStrategy,
                StandAloneTestHelper.getBubbleDependencyComparator(),
                null,
                null,
                null
        );

        StoreServer storeServer = new StoreServer(storeSessionServer, injector);

        Simple gotten = storeServer.get(id1);
        assertSame(gotten, object1);
        Mockito.verify(persistenceSessionManager).get(id1);

        Set<Simple> locked = storeServer.lock(ImmutableSet.of(id1, id2));
        ImmutableMap<? extends SimpleId<?>, Simple> lockedMap = Maps.uniqueIndex(locked, Simple::getId);

        Assertions.assertThat(locked).containsOnly(object1, object2);
        assertSame(lockedMap.get(id1), object1);
        assertSame(lockedMap.get(id2), object2);
        Mockito.verify(lockerStrategy).lock(ImmutableSet.of(id1, id2));
        Mockito.verify(persistenceSessionManager).refresh(id2);
        Mockito.verifyNoMoreInteractions(persistenceSessionManager, lockerStrategy, idService);
    }

    public void testLockMultipleOnePrelockedOtherGotten() {
        SimpleId<?> id1 = new SimpleId<>(1L);
        SimpleId<?> id2 = new SimpleId<>(2L);
        Simple object1 = new Simple(id1, "A");
        Simple object2 = new Simple(id2);

        PersistenceSessionManager persistenceSessionManager = Mockito.mock(PersistenceSessionManager.class);
        Mockito.doReturn(ImmutableSet.of(object1, object2)).when(persistenceSessionManager).get(ImmutableSet.of(id1, id2));
        Mockito.doAnswer(invocation -> {
            Simple object = (Simple) invocation.getArguments()[0];
            object.setText("B");
            return null;
        }).when(persistenceSessionManager).refresh(object2);
        LockerStrategy lockerStrategy = Mockito.mock(LockerStrategy.class);
        Mockito.doReturn(ImmutableSet.of(id2)).when(lockerStrategy).lock(ImmutableSet.of(id1, id2));
        IdService idService = Mockito.mock(IdService.class);

        Injector injector = Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                bind(IdService.class).toInstance(idService);
            }
        });

        StoreSessionServer storeSessionServer = new StoreSessionServer(
                persistenceSessionManager,
                Providers.of(null),
                Providers.of(SnapshotVersion.CURRENT),
                lockerStrategy,
                StandAloneTestHelper.getBubbleDependencyComparator(),
                null,
                null,
                null
        );

        StoreServer storeServer = new StoreServer(storeSessionServer, injector);

        Set<Simple> gotten = storeServer.get(ImmutableSet.of(id1, id2));
        ImmutableMap<? extends SimpleId<?>, Simple> gottenMap = Maps.uniqueIndex(gotten, Simple::getId);
        assertSame(gottenMap.get(id1), object1);
        assertSame(gottenMap.get(id2), object2);
        assertEquals(gottenMap.get(id1).getText(), "A");
        assertNull(gottenMap.get(id2).getText());
        Mockito.verify(persistenceSessionManager).get(ImmutableSet.of(id1, id2));

        Set<Simple> locked = storeServer.lock(ImmutableSet.of(id1, id2));
        ImmutableMap<? extends SimpleId<?>, Simple> lockedMap = Maps.uniqueIndex(locked, Simple::getId);

        Assertions.assertThat(locked).containsOnly(object1, object2);
        assertSame(lockedMap.get(id1), object1);
        assertSame(lockedMap.get(id2), object2);
        assertEquals(lockedMap.get(id1).getText(), "A");
        assertEquals(lockedMap.get(id2).getText(), "B");
        Mockito.verify(lockerStrategy).lock(ImmutableSet.of(id1, id2));
        Mockito.verify(persistenceSessionManager).refresh(object2);
        Mockito.verifyNoMoreInteractions(persistenceSessionManager, lockerStrategy, idService);
    }

    public void testUnlockSingleUnloaded() {
        SimpleId<?> id = new SimpleId<>(17L);

        PersistenceSessionManager persistenceSessionManager = Mockito.mock(PersistenceSessionManager.class);
        LockerStrategy lockerStrategy = Mockito.mock(LockerStrategy.class);
        Mockito.when(lockerStrategy.isLockedByCaller(id)).thenReturn(true, false);
        IdService idService = Mockito.mock(IdService.class);

        Injector injector = Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                bind(IdService.class).toInstance(idService);
            }
        });

        StoreSessionServer storeSessionServer = new StoreSessionServer(
                persistenceSessionManager,
                Providers.of(null),
                Providers.of(SnapshotVersion.CURRENT),
                lockerStrategy,
                StandAloneTestHelper.getBubbleDependencyComparator(),
                null,
                null,
                null
        );

        StoreServer storeServer = new StoreServer(storeSessionServer, injector);
        storeServer.unlock(id);

        Mockito.verify(lockerStrategy).isLockedByCaller(id);
        Mockito.verify(lockerStrategy).unlock(id);
        Mockito.verifyNoMoreInteractions(persistenceSessionManager, lockerStrategy, idService);

        storeServer.unlock(id);
        Mockito.verify(lockerStrategy, Mockito.times(2)).isLockedByCaller(id);
        Mockito.verifyNoMoreInteractions(persistenceSessionManager, lockerStrategy, idService);
    }

    public void testUnlockSingleLocked() {
        SimpleId<?> id = new SimpleId<>(17L);
        Simple object = new Simple(id);

        PersistenceSessionManager persistenceSessionManager = Mockito.mock(PersistenceSessionManager.class);
        Mockito.doReturn(object).when(persistenceSessionManager).refresh(id);
        LockerStrategy lockerStrategy = Mockito.mock(LockerStrategy.class);
        Mockito.doReturn(true).when(lockerStrategy).lock(id);
        IdService idService = Mockito.mock(IdService.class);

        Injector injector = Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                bind(IdService.class).toInstance(idService);
            }
        });

        StoreSessionServer storeSessionServer = new StoreSessionServer(
                persistenceSessionManager,
                Providers.of(null),
                Providers.of(SnapshotVersion.CURRENT),
                lockerStrategy,
                StandAloneTestHelper.getBubbleDependencyComparator(),
                null,
                null,
                null
        );

        StoreServer storeServer = new StoreServer(storeSessionServer, injector);
        storeServer.lock(id);
        Mockito.reset(persistenceSessionManager, lockerStrategy);

        storeServer.unlock(id);
        Mockito.verify(lockerStrategy).unlock(id);
        Mockito.verifyNoMoreInteractions(persistenceSessionManager, lockerStrategy, idService);
    }

    public void testUnlockMultipleUnloaded() {
        SimpleId<?> id1 = new SimpleId<>(1L);
        SimpleId<?> id2 = new SimpleId<>(2L);

        PersistenceSessionManager persistenceSessionManager = Mockito.mock(PersistenceSessionManager.class);
        LockerStrategy lockerStrategy = Mockito.mock(LockerStrategy.class);
        Mockito.when(lockerStrategy.isLockedByCaller(id1)).thenReturn(true, false);
        Mockito.when(lockerStrategy.isLockedByCaller(id2)).thenReturn(true, false);
        IdService idService = Mockito.mock(IdService.class);

        Injector injector = Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                bind(IdService.class).toInstance(idService);
            }
        });

        StoreSessionServer storeSessionServer = new StoreSessionServer(
                persistenceSessionManager,
                Providers.of(null),
                Providers.of(SnapshotVersion.CURRENT),
                lockerStrategy,
                StandAloneTestHelper.getBubbleDependencyComparator(),
                null,
                null,
                null
        );

        StoreServer storeServer = new StoreServer(storeSessionServer, injector);
        storeServer.unlock(ImmutableSet.of(id1, id2));

        Mockito.verify(lockerStrategy).isLockedByCaller(id1);
        Mockito.verify(lockerStrategy).isLockedByCaller(id2);
        Mockito.verify(lockerStrategy).unlock(ImmutableSet.of(id1, id2));
        Mockito.verifyNoMoreInteractions(persistenceSessionManager, lockerStrategy, idService);

        storeServer.unlock(ImmutableSet.of(id1, id2));
        Mockito.verify(lockerStrategy, Mockito.times(2)).isLockedByCaller(id1);
        Mockito.verify(lockerStrategy, Mockito.times(2)).isLockedByCaller(id2);
        Mockito.verifyNoMoreInteractions(persistenceSessionManager, lockerStrategy, idService);
    }

    public void testUnlockMultipleLocked() {
        SimpleId<?> id1 = new SimpleId<>(1L);
        SimpleId<?> id2 = new SimpleId<>(2L);
        Simple object1 = new Simple(id1, "A");
        Simple object2 = new Simple(id2, "B");

        PersistenceSessionManager persistenceSessionManager = Mockito.mock(PersistenceSessionManager.class);
        Mockito.doReturn(object1).when(persistenceSessionManager).refresh(id1);
        Mockito.doReturn(object2).when(persistenceSessionManager).refresh(id2);
        LockerStrategy lockerStrategy = Mockito.mock(LockerStrategy.class);
        Mockito.doReturn(ImmutableSet.of(id1, id2)).when(lockerStrategy).lock(ImmutableSet.of(id1, id2));
        IdService idService = Mockito.mock(IdService.class);

        Injector injector = Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                bind(IdService.class).toInstance(idService);
            }
        });

        StoreSessionServer storeSessionServer = new StoreSessionServer(
                persistenceSessionManager,
                Providers.of(null),
                Providers.of(SnapshotVersion.CURRENT),
                lockerStrategy,
                StandAloneTestHelper.getBubbleDependencyComparator(),
                null,
                null,
                null
        );

        StoreServer storeServer = new StoreServer(storeSessionServer, injector);
        storeServer.lock(ImmutableSet.of(id1, id2));
        Mockito.reset(persistenceSessionManager, lockerStrategy);

        storeServer.unlock(ImmutableSet.of(id1, id2));
        Mockito.verify(lockerStrategy).unlock(ImmutableSet.of(id1, id2));
        Mockito.verifyNoMoreInteractions(persistenceSessionManager, lockerStrategy, idService);
    }

    /**
     * Sjekker at testdata kan leses og forventet innhold av objekter som brukes i tester av empty collections flagg
     */
    public void testEmptyCollectionsOptimizer_KanLeseObjekter() {
        ChildBubbleEmptyColOptimizer child21 = storeServer.get(childBubbleEmptyColOptimizerId_21);
        ChildBubbleEmptyColOptimizer child31 = storeServer.get(childBubbleEmptyColOptimizerId_31);
        assertNotNull(child21);
        assertNotNull(child31);
        ParentBubbleEmptyColOptimizer parent1 = storeServer.get(parentBubbleEmptyColOptimizerId_1);
        assertNotNull(parent1);
        assertThat(parent1.getChildren1Ids()).isEmpty();
        assertThat(parent1.getChildren2Ids()).isEmpty();
        assertThat(parent1.getChildren3Ids()).isEmpty();
        assertThat(parent1.getEmptyCollectionsFlag()).describedAs("Bitt 0 og 1 skal være satt, da children1 og children2 er tomme").isEqualTo(3);

        ParentBubbleEmptyColOptimizer parent2 = storeServer.get(parentBubbleEmptyColOptimizerId_2);
        assertNotNull(parent2);
        assertThat(parent2.getChildren1Ids()).isEmpty();
        assertThat(parent2.getChildren2Ids()).containsOnly(childBubbleEmptyColOptimizerId_21);
        assertThat(parent2.getChildren3Ids()).isEmpty();
        assertThat(parent2.getEmptyCollectionsFlag()).describedAs("Bitt 1 skal ikke være satt").isEqualTo(1);

        ParentBubbleEmptyColOptimizer parent3 = storeServer.get(parentBubbleEmptyColOptimizerId_3);
        assertNotNull(parent3);
        assertThat(parent3.getChildren1Ids()).containsOnly(childBubbleEmptyColOptimizerId_31);
        assertThat(parent3.getChildren2Ids()).containsOnly(childBubbleEmptyColOptimizerId_31);
        assertThat(parent3.getChildren3Ids()).isEmpty();
        assertThat(parent3.getEmptyCollectionsFlag()).describedAs("Bitt 0 og 1 skal ikke være satt").isEqualTo(0);

        ParentBubbleEmptyColOptimizer parent4 = storeServer.get(parentBubbleEmptyColOptimizerId_4);
        assertNotNull(parent4);
        assertThat(parent4.getChildren1Ids()).containsOnly(childBubbleEmptyColOptimizerId_41);
        assertThat(parent4.getChildren2Ids()).isEmpty();
        assertThat(parent4.getChildren3Ids()).isEmpty();
        assertThat(parent4.getEmptyCollectionsFlag()).describedAs("Bitt 1 skal være satt").isEqualTo(2);

        ParentBubbleEmptyColOptimizer parent5 = storeServer.get(parentBubbleEmptyColOptimizerId_5);
        assertNotNull(parent5);
        assertThat(parent5.getChildren1Ids()).isEmpty();
        assertThat(parent5.getChildren2Ids()).isEmpty();
        assertThat(parent5.getChildren3Ids()).isEmpty();
        assertThat(parent5.getEmptyCollectionsFlag()).describedAs("Bitt 0 og 1 skal ikke være satt, da flagget er nullstilt for denne boblen").isEqualTo(0);

        ParentBubbleEmptyColOptimizerSub1 parent6 = storeServer.get(parentBubbleEmptyColOptimizerId_6);
        assertNotNull(parent6);
        assertThat(parent6.getId()).isInstanceOf(ParentBubbleEmptyColOptimizerSub1Id.class);
        assertThat(parent6.getChildren1Ids()).containsOnly(childBubbleEmptyColOptimizerId_61);
        assertThat(parent6.getChildren2Ids()).isEmpty();
        assertThat(parent6.getChildren3Ids()).isEmpty();
        assertThat(parent6.getChildren4Ids()).isEmpty();
        assertThat(parent6.getEmptyCollectionsFlag()).describedAs("Bitt 1 og 2 skal ikke være satt, da flagget er nullstilt for denne boblen").isEqualTo(6);
    }

    /**
     * Tester effekten av EmptyCollectionsOptimizer ved å sjekke hvilke collections som har blitt initialisert.
     * Tomme collections som er styrt av EmptyCollectionsOptimizer vil være alltid være initialisert, mens collections
     * som ikke er tomme eller ikke er styrt av flagget ikke vil være initialisert når boblen lastes. Det er vanskelig
     * å sjekke automatisk hvilke slq-er som hibernate utfører ved lasting av bobler som har tom collection, men dette
     * kan kontrolleres manuelt ved å slå på Hibernate sql logging. Denne testen skal utføre i alt 3 sql-er for å lese
     * ParentBubbleEmptyColOptimizer boblene en og en - og ingen andre sql-er, da lazy loading er slått på for
     * collections. Testen vil feile hvis flagget ikke virker som det skal eller hvis Hibernate eager initialisere
     * alle collections uansett. Kun de tomme collections som bruker flagget skal være initialisert.
     */
    public void testEmptyCollectionsOptimizer_InitialisererEmptyCollections() {
        enableLazyLoading(); // Ønsker ikke at tvinge initialisering av collections.
        ParentBubbleEmptyColOptimizer parent1 = storeServer.get(parentBubbleEmptyColOptimizerId_1);
        assertThat(parent1.getEmptyCollectionsFlag()).describedAs("Bitt 0 og 1 skal være satt, da children1 og children2 er tomme").isEqualTo(3);
        assertThat(Hibernate.isInitialized(parent1.getChildren1Ids())).isTrue();
        assertThat(Hibernate.isInitialized(parent1.getChildren2Ids())).isTrue();
        assertThat(Hibernate.isInitialized(parent1.getChildren3Ids())).isFalse();

        ParentBubbleEmptyColOptimizer parent2 = storeServer.get(parentBubbleEmptyColOptimizerId_2);
        assertThat(parent2.getEmptyCollectionsFlag()).describedAs("Bitt 1 skal ikke være satt").isEqualTo(1);
        assertThat(Hibernate.isInitialized(parent2.getChildren1Ids())).isTrue();
        assertThat(Hibernate.isInitialized(parent2.getChildren2Ids())).isFalse();
        assertThat(Hibernate.isInitialized(parent2.getChildren3Ids())).isFalse();

        ParentBubbleEmptyColOptimizer parent3 = storeServer.get(parentBubbleEmptyColOptimizerId_3);
        assertThat(parent3.getEmptyCollectionsFlag()).describedAs("Bitt 0 og 1 skal ikke være satt").isEqualTo(0);
        assertThat(Hibernate.isInitialized(parent3.getChildren1Ids())).isFalse();
        assertThat(Hibernate.isInitialized(parent3.getChildren2Ids())).isFalse();
        assertThat(Hibernate.isInitialized(parent3.getChildren3Ids())).isFalse();

        ParentBubbleEmptyColOptimizer parent4 = storeServer.get(parentBubbleEmptyColOptimizerId_4);
        assertThat(parent4.getEmptyCollectionsFlag()).describedAs("Bitt 0 og 1 skal ikke være satt").isEqualTo(2);
        assertThat(Hibernate.isInitialized(parent4.getChildren1Ids())).isFalse();
        assertThat(Hibernate.isInitialized(parent4.getChildren2Ids())).isTrue();
        assertThat(Hibernate.isInitialized(parent4.getChildren3Ids())).isFalse();
    }

    /**
     * Boblen som leses har en tom collection for children1. Empty collections flagget har bitt 0 satt for denne
     * collection. Når det legges til elementer i children1 så skal bitt 0 fjernes når det utføres en flush. Collections
     * som ikke var lastet før flush skal ikke bli lastet av flush. Finish skal heller ikke laste disse collections.
     * <p>
     * Forventer 3 sql, en for lese boble, en for å oppdatere children1 collection og en for oppdatering av flagget.
     * Det skal ikke være noen sql-er for å laste children1 collection. Disse sql-er må sjekkes manuelt i log output.
     * <p>
     * Denne test ruller endringer tilbake slik at testdata er uendret etter testen.
     */
    public void testEmptyCollectionsOptimizer_FlushSkalSetteBittNaarCollectionFaarInnhold() {
        enableLazyLoading();
        storeServer.beginTransaction();
        try {
            ParentBubbleEmptyColOptimizer parent = storeServer.lock(parentBubbleEmptyColOptimizerId_2);
            assertThat(parent.getEmptyCollectionsFlag()).describedAs("Bitt 0 skal være satt").isEqualTo(1);
            assertThat(parent.getChildren1Ids()).isEmpty();
            parent.getChildren1Ids().add(childBubbleEmptyColOptimizerId_11);
            assertThat(Hibernate.isInitialized(parent.getChildren2Ids())).isFalse();
            assertThat(Hibernate.isInitialized(parent.getChildren3Ids())).isFalse();
            storeServer.flush();
            assertThat(parent.getEmptyCollectionsFlag()).describedAs("Bitt 0 ikke være satt").isEqualTo(0);
            assertThat(Hibernate.isInitialized(parent.getChildren2Ids())).isFalse();
            assertThat(Hibernate.isInitialized(parent.getChildren3Ids())).isFalse();
        } finally {
            storeServer.rollbackTransaction();
        }
    }

    /**
     * Tester at bitt blir satt når en collection blir tom. I denne test så oppdateres children2 til å være tom. Collections
     * som ikke er lastet skal ikke påvirkes.
     * <p>
     * Denne test ruller endringer tilbake slik at testdata er uendret etter testen.
     */
    public void testEmptyCollectionsOptimizer_RiktigBittSkalSettes() {
        enableLazyLoading();
        storeServer.beginTransaction();
        try {
            ParentBubbleEmptyColOptimizer parent = storeServer.lock(parentBubbleEmptyColOptimizerId_3);
            assertThat(parent.getEmptyCollectionsFlag()).describedAs("Bitt 0 og 1 skal ikke være satt").isEqualTo(0);
            assertThat(Hibernate.isInitialized(parent.getChildren1Ids())).isFalse();
            assertThat(Hibernate.isInitialized(parent.getChildren2Ids())).isFalse();
            assertThat(Hibernate.isInitialized(parent.getChildren3Ids())).isFalse();
            parent.getChildren2Ids().clear();
            storeServer.flush(); // Fører til at flagget beregnes for children2Ids
            assertThat(parent.getEmptyCollectionsFlag()).describedAs("Bitt 1 skal være satt").isEqualTo(2);
            assertThat(Hibernate.isInitialized(parent.getChildren1Ids())).isFalse();
            assertThat(Hibernate.isInitialized(parent.getChildren3Ids())).isFalse();
        } finally {
            storeServer.rollbackTransaction();
        }
    }

    /**
     * Automatisk beregning av flagg ved oppdatering påvirker ikke umaterialiserte collections
     * <p>
     * Denne test ruller endringer tilbake slik at testdata er uendret etter testen.
     */
    public void testEmptyCollectionsOptimizer_CollectionsSomIkkeErMateralisertRoeresIkke() {
        enableLazyLoading();
        storeServer.beginTransaction();
        try {
            ParentBubbleEmptyColOptimizer parent1 = storeServer.lock(parentBubbleEmptyColOptimizerId_1);
            ParentBubbleEmptyColOptimizer parent3 = storeServer.lock(parentBubbleEmptyColOptimizerId_3);
            parent1.setText("Jeg er endret");
            parent3.setText("Jeg er endret");
            assertThat(Hibernate.isInitialized(parent1.getChildren1Ids())).isTrue();
            assertThat(Hibernate.isInitialized(parent1.getChildren2Ids())).isTrue();
            assertThat(Hibernate.isInitialized(parent1.getChildren3Ids())).isFalse();
            assertThat(Hibernate.isInitialized(parent3.getChildren1Ids())).isFalse();
            assertThat(Hibernate.isInitialized(parent3.getChildren2Ids())).isFalse();
            assertThat(Hibernate.isInitialized(parent3.getChildren3Ids())).isFalse();
            assertThat(parent1.getEmptyCollectionsFlag()).isEqualTo(3);
            assertThat(parent3.getEmptyCollectionsFlag()).isEqualTo(0);
            storeServer.update(parent1);
            storeServer.update(parent3);
            assertThat(parent1.getEmptyCollectionsFlag()).isEqualTo(3);
            assertThat(parent3.getEmptyCollectionsFlag()).isEqualTo(0);
            assertThat(Hibernate.isInitialized(parent1.getChildren3Ids())).isFalse();
            assertThat(Hibernate.isInitialized(parent3.getChildren1Ids())).isFalse();
            assertThat(Hibernate.isInitialized(parent3.getChildren2Ids())).isFalse();
            assertThat(Hibernate.isInitialized(parent3.getChildren3Ids())).isFalse();
        } finally {
            storeServer.rollbackTransaction();
        }
    }

    /**
     * En bobler som får endret en collection under kall til finish vil få oppdatert flagget sitt. Umaterialiserte
     * collections berøres ikke. I denne test endres children1 for boble med idvalue 4.
     * <p>
     * Denne test ruller endringer tilbake slik at testdata er uendret etter testen.
     */
    public void testEmptyCollectionsOptimizer_FinishListenerSomEndreCollectionPaavirkerFlagg() {
        enableLazyLoading();
        storeServer.beginTransaction();
        try {
            ParentBubbleEmptyColOptimizer parent = storeServer.lock(parentBubbleEmptyColOptimizerId_4);
            parent.setText("Jeg er endret");
            storeServer.update(parent);
            assertThat(Hibernate.isInitialized(parent.getChildren1Ids())).isFalse();// Denne er ikke tom og derfor ennå ikke lastet
            assertThat(Hibernate.isInitialized(parent.getChildren2Ids())).isTrue(); // Denne er tom og derfor lastet allerede
            storeServer.update(parent);
            assertThat(parent.getEmptyCollectionsFlag()).describedAs("Bitt 1 skal være satt").isEqualTo(2);
            storeServer.finish(); // UpdatingFinishListener endre collection children1 for boble med idvalue 4
            assertThat(parent.getEmptyCollectionsFlag()).describedAs("Bitt 1 og 2 skal være satt").isEqualTo(3);
            assertThat(Hibernate.isInitialized(parent.getChildren1Ids())).isTrue();
            assertThat(parent.getChildren1Ids()).isEmpty();
            assertThat(parent.getText()).isEqualTo("UpdatingFinishListener changed collection children1Ids");
        } finally {
            storeServer.rollbackTransaction();
        }
    }

    /**
     * Leser en boble hvor flagget ikke har satt bitt-en for en collection som faktisk er tom og derfor med fordel
     * kunne ha bitten satt. Testen viser at kall til flush ikke vil endre på flagget. Flagget skal kun endres hvis
     * boblen oppdateres via {@code Store.update} eller en collection endres før kall flush og det skjer ikke i denne testen.
     * <p>
     * Denne test ruller endringer tilbake slik at testdata er uendret etter testen.
     */
    public void testEmptyCollectionsOptimizer_LesingOgFlushingAvObjectEndreIkkeFlagget() {
        enableLazyLoading();
        storeServer.beginTransaction();
        try {
            ParentBubbleEmptyColOptimizer parent = storeServer.get(parentBubbleEmptyColOptimizerId_5);
            storeServer.ensureFullyLoaded(parent);
            assertThat(parent.getEmptyCollectionsFlag()).isEqualTo(0);
            assertThat(parent.getChildren1Ids()).isEmpty(); // Flagg bør ha bitt 1 satt, men det skal ikke skje ved les
            storeServer.flush();
            assertThat(parent.getEmptyCollectionsFlag()).describedAs("Flag skal ikke oppdateres ved les").isEqualTo(0);
        } finally {
            storeServer.rollbackTransaction();
        }
    }

    /**
     * Leser en bobler hvor flagget ikke er optimalt satt, dvs flagget er 0, men bitt 0 kunne være satt siden
     * children1 collection er tom. Flush beregner ikke flagget på nytt da alle collections er uendret, men update
     * gjør det.
     * <p>
     * Denne test ruller endringer tilbake slik at testdata er uendret etter testen.
     */
    public void testEmptyCollectionsOptimizer_FlushOppdatererIkkeFlaggetHvisIngenCollectionsErEndretMenUpdateGjoerDet() {
        enableLazyLoading();
        storeServer.beginTransaction();
        try {
            ParentBubbleEmptyColOptimizer parent = storeServer.lock(parentBubbleEmptyColOptimizerId_5);
            assertThat(parent.getEmptyCollectionsFlag()).isEqualTo(0);
            assertThat(parent.getChildren1Ids()).isEmpty(); // denne collection er tom, så bitt 0 i flagget kan settes
            parent.setText("Dette felt på objektet er oppdatert, men ingen collections er endret");
            storeServer.flush();
            assertThat(parent.getEmptyCollectionsFlag()).describedAs("Flush endrer ikke flagget siden alle collections er uendret").isEqualTo(0);
            storeServer.update(parent);
            assertThat(parent.getEmptyCollectionsFlag()).describedAs("Update endre flagget for children1").isEqualTo(1);
        } finally {
            storeServer.rollbackTransaction();
        }
    }

    /**
     * Leser en bobler hvor flagget ikke er optimalt satt, dvs flagget er 0, men bitt 0 kunne være satt siden
     * children1 collection er tom. Flush beregner flagget på nytt for alle lastede collections (inkl. children1)
     * siden minst en collection (children2) oppdateres.
     * <p>
     * Denne test ruller endringer tilbake slik at testdata er uendret etter testen.
     */
    public void testEmptyCollectionsOptimizer_FlaggetOppdateresForAlleMaterialiserteCollectionsHvisMinstEnCollectionsErEndret() {
        enableLazyLoading();
        storeServer.beginTransaction();
        try {
            ParentBubbleEmptyColOptimizer parent = storeServer.lock(parentBubbleEmptyColOptimizerId_5);
            assertThat(parent.getEmptyCollectionsFlag()).isEqualTo(0);
            assertThat(parent.getChildren1Ids()).isEmpty(); // denne collection er tom, så bitt 0 i flagget kan settes
            assertThat(parent.getChildren2Ids()).doesNotContain(childBubbleEmptyColOptimizerId_21);
            parent.getChildren2Ids().add(childBubbleEmptyColOptimizerId_21);
            storeServer.flush();
            assertThat(parent.getEmptyCollectionsFlag()).describedAs("Flush oppdatere flagget siden minst en collection er endret").isEqualTo(1);
        } finally {
            storeServer.rollbackTransaction();
        }
    }

    /**
     * Leser en bobler hvor flagget ikke er optimalt satt, dvs flagget er 0, men bitt 0 kunne være satt siden
     * children1Ids collection er tom. Flush beregner ikke flagget på nytt da collections ikke er materialisert
     * når flush kalles. Etterfølgende materialisering av collection påvirker ikke dette..
     * <p>
     * Denne test ruller endringer tilbake slik at testdata er uendret etter testen.
     */
    public void testEmptyCollectionsOptimizer_FlaggetOppdateresIkkeForUmaterialiserteCollectionsSomLastesEtterFlush() {
        enableLazyLoading();
        storeServer.beginTransaction();
        try {
            ParentBubbleEmptyColOptimizer parent = storeServer.lock(parentBubbleEmptyColOptimizerId_5);
            assertThat(parent.getEmptyCollectionsFlag()).isEqualTo(0);
            assertThat(parent.getChildren2Ids()).doesNotContain(childBubbleEmptyColOptimizerId_21);
            parent.getChildren2Ids().add(childBubbleEmptyColOptimizerId_21);
            storeServer.update(parent);
            storeServer.flush();
            assertThat(parent.getEmptyCollectionsFlag()).isEqualTo(0);
            assertThat(parent.getChildren1Ids()).isEmpty(); // Collection children1Ids materialiseres først nå
            storeServer.flush();
            assertThat(parent.getEmptyCollectionsFlag()).describedAs("Flush skal ikke endre flagget siden collections er uendret siden forrige kall til flush").isEqualTo(0);
        } finally {
            storeServer.rollbackTransaction();
        }
    }


    /**
     * Flagget beregnes ved kall til insert
     * <p>
     * Denne test ruller endringer tilbake slik at testdata er uendret etter testen.
     */
    public void testEmptyCollectionsOptimizer_FlaggetBeregnesVedInsert() {
        enableLazyLoading();
        storeServer.beginTransaction();
        try {
            ParentBubbleEmptyColOptimizer parent = new ParentBubbleEmptyColOptimizer(new ParentBubbleEmptyColOptimizerId<>(101));
            storeServer.insert(parent);
            assertThat(parent.getEmptyCollectionsFlag()).isEqualTo(3);
        } finally {
            storeServer.rollbackTransaction();
        }
    }

    /**
     * Tester at empty collections flagg blir satt riktig ved endring av subtype. I dette testcase så hadde
     * opprinnelig objekt en collection som bevarer innholdet (children1). Når det opprettes en ny subtype så opprettes
     * en ny collection instans for children1 som genererer en PreUpdateCollectionsEvent, hvilket igjen fører til at
     * flagget beregnes for alle collections. Flagget har dog allerede blitt oppdatert ved kall til {@code Store.update},
     * men eventen gjør ingen skade.
     * <p>
     * Denne test ruller endringer tilbake slik at testdata er uendret etter testen.
     */
    public void testEmptyCollectionsOptimizer_FlagBlirBeregnetRiktigVedSubtypeEndringNaarIkkeAlleCollectionsErTomme() {
        enableLazyLoading();
        storeServer.beginTransaction();
        try {
            ParentBubbleEmptyColOptimizerSub1 parent = storeServer.lock(parentBubbleEmptyColOptimizerId_6);
            assertThat(parent.getEmptyCollectionsFlag()).isEqualTo(6); // Collections children2 og 4 er tomme. Disse bruker bitt 1 og 2. Derfor 2+4=6
            assertThat(parent.getChildren1Ids()).isNotEmpty();
            // Må lage et nytt objekt for å skifte subtype. Flagget skal ikke aldri kopieres mellom objekter.
            ParentBubbleEmptyColOptimizer withChangedSubtype = new ParentBubbleEmptyColOptimizer();
            assertThat(withChangedSubtype.getEmptyCollectionsFlag()).isEqualTo(0);
            withChangedSubtype.setId(new ParentBubbleEmptyColOptimizerId<>(parent.getId().getValue()));
            withChangedSubtype.setText(parent.getText());
            withChangedSubtype.setChildren1Ids(parent.getChildren1Ids());
            withChangedSubtype.setChildren2Ids(parent.getChildren2Ids());
            storeServer.update(withChangedSubtype);
            assertThat(withChangedSubtype.getEmptyCollectionsFlag()).describedAs("Forventet at flagget oppdateres automatisk ved kall til update").isEqualTo(2);
            // Hibernate generere en PreUpdateCollectionEvent, da en collection children1 får satt innhold fra opprinnelig collection.
            // Flagget er dog allerede beregnet
            storeServer.flush();
            assertThat(withChangedSubtype.getEmptyCollectionsFlag()).isEqualTo(2); // Collection children2 bruker bitt 2 og er tom
        } finally {
            storeServer.rollbackTransaction();
        }
    }

    /**
     * Tester at empty collections flagg blir satt riktig ved endring av subtype. I dette testcase så er alle collections
     * som bevares ved sub-typeskifte tomme og Hibernate genererer derfor ikke noen PreUpdateCollectionsEvent men siden
     * flagget beregnes eksplisitt ved kall til update er dette ikke noe problem.
     */
    public void testEmptyCollectionsOptimizer_FlagBlirRiktigVedSubtypeEndringNaarAlleCollectionsErTomme() {
        enableLazyLoading();
        storeServer.beginTransaction();
        try {
            ParentBubbleEmptyColOptimizerSub1 parent = storeServer.lock(parentBubbleEmptyColOptimizerId_7);
            assertThat(parent.getEmptyCollectionsFlag()).isEqualTo(7);
            // Må lage et nytt objekt for å skifte subtype. Flagget skal ikke aldri kopieres mellom objekter.
            ParentBubbleEmptyColOptimizer withChangedSubtype = new ParentBubbleEmptyColOptimizer();
            assertThat(withChangedSubtype.getEmptyCollectionsFlag()).isEqualTo(0);
            withChangedSubtype.setId(new ParentBubbleEmptyColOptimizerId<>(parent.getId().getValue()));
            withChangedSubtype.setText(parent.getText());
            withChangedSubtype.setChildren1Ids(parent.getChildren1Ids());
            withChangedSubtype.setChildren2Ids(parent.getChildren2Ids());
            storeServer.update(withChangedSubtype);
            assertThat(withChangedSubtype.getEmptyCollectionsFlag()).describedAs("Forventet at flagget oppdateres automatisk ved kall til update").isEqualTo(3);
        } finally {
            storeServer.rollbackTransaction();
        }
    }

    private void enableLazyLoading() {
        PersistenceSessionForSnapshot forSnapshotVersion = persistenceSessionManager.getForSnapshotVersion(SnapshotVersion.CURRENT);
        HibernatePersistenceSessionMasterImpl implementation = forSnapshotVersion.getImplementation(HibernatePersistenceSessionMasterImpl.class);
        implementation.setLazyLoadedBubblesAllowed(true);
    }
}
