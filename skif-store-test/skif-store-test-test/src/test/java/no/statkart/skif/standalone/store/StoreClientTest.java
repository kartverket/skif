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
import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.mockup.TestIdServiceLong;
import no.statkart.skif.mockup.TestNumber;
import no.statkart.skif.service.sequence.IdService;
import no.statkart.skif.store.BubbleId;
import no.statkart.skif.store.BubbleObject;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.SnapshotVersionContext;
import no.statkart.skif.store.Store;
import no.statkart.skif.store.StoreClient;
import no.statkart.skif.store.StoreSessionClient;
import no.statkart.skif.store.UnitOfWork;
import no.statkart.skif.store.service.LockService;
import no.statkart.skif.store.service.StoreService;
import no.statkart.skif.storetest.domain.basic.Simple;
import no.statkart.skif.storetest.domain.basic.SimpleId;
import no.statkart.skif.storetest.domain.standalone.TestBubble;
import no.statkart.skif.storetest.domain.standalone.TestBubbleId;
import org.assertj.core.api.Assertions;
import org.mockito.Mockito;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tester grunnleggende ting i {@link no.statkart.skif.store.StoreClient}. Dette er stort sett implementert i diverse session-klasser.
 * <p>
 * Dette er en stand-alone-test som ikke bruker StoreTestServer modulen.
 *
 * @author Tor Egil R. Strand
 * @since 2.1
 */
@Test
public class StoreClientTest {
    private static Injector createInjector() {
        AbstractModule module = new AbstractModule() {
            @Override
            protected void configure() {
                bind(IdService.class).to(TestIdServiceLong.class);
                bind(TestNumber.class).toInstance(new TestNumber(100000, 1));
                bind(Store.class).to(StoreClient.class);
                bind(StoreService.class).to(StoreClientTestStoreService.class);
                bind(LockService.class).to(StoreClientTestStoreService.class);
                bind(StoreClientTestStoreService.class).in(Singleton.class);
                bind(SnapshotVersionContext.class).toInstance(SnapshotVersionContext.getInstance());
            }

            @Provides
            protected SnapshotVersion providesSnapshotVersion(SnapshotVersionContext snapshotVersionContext) {
                return snapshotVersionContext.getSnapshotVersion();
            }

            @Provides
            @Singleton
            protected StoreClient provideStoreClient(StoreService storeService, LockService lockService, Injector injector, SnapshotVersionContext snapshotVersionContext) {
                StoreSessionClient storeSessionClient = new StoreSessionClient(storeService, lockService, snapshotVersionContext);
                return new StoreClient(storeSessionClient, injector);
            }
        };
        return Guice.createInjector(module);
    }

    public void endNestedUnitOfWork() {
        Injector injector = createInjector();
        Store store = injector.getInstance(Store.class);

        //noinspection unused
        UnitOfWork unitOfWork1 = store.beginUnitOfWork();
        UnitOfWork unitOfWork2 = store.beginUnitOfWork();

        try {
            store.endUnitOfWork(unitOfWork2);
            Assert.fail("Skulle fått feilmelding");
        } catch (ImplementationException e) {
            Assert.assertTrue(e.getMessage().contains("In nested UnitOfWork"));
        }
    }

    public void forgetLocksOnEndUnitOfWork() {
        Injector injector = createInjector();
        Store store = injector.getInstance(Store.class);

        TestBubbleId<?> id = new TestBubbleId(1L);

        UnitOfWork unitOfWork = store.beginUnitOfWork();
        store.lock(id);
        Assert.assertTrue(store.isLocked(id), "Objektet ble ikke låst");
        store.getUnitOfWorkTransfer();
        store.endUnitOfWork(unitOfWork);

        // Simuler at tjeneren åpner alle låser for brukeren
        injector.getInstance(StoreClientTestStoreService.class).clearLocks();

        Assert.assertFalse(store.isLocked(id), "Objektet er fortsatt låst");
    }

    public void unlockOnAbortUnitOfWork() {
        Injector injector = createInjector();
        Store store = injector.getInstance(Store.class);
        StoreClientTestStoreService storeService = injector.getInstance(StoreClientTestStoreService.class);

        TestBubbleId<?> id = new TestBubbleId(1L);

        UnitOfWork unitOfWork = store.beginUnitOfWork();
        store.lock(id);

        Assert.assertTrue(store.isLocked(id), "Objektet er ikke låst");
        Assert.assertTrue(storeService.isLocked(id), "Objektet er ikke låst ordentlig");

        store.abortUnitOfWork(unitOfWork);

        Assert.assertFalse(store.isLocked(id), "Objektet ble ikke låst opp");
        Assert.assertFalse(storeService.isLocked(id), "Objektet ble ikke låst opp ordentlig");
    }

    public void dontUnlockOnCommitUnitOfWork() {
        Injector injector = createInjector();
        Store store = injector.getInstance(Store.class);
        StoreClientTestStoreService storeService = injector.getInstance(StoreClientTestStoreService.class);

        TestBubbleId<?> id = new TestBubbleId(1L);

        //noinspection unused
        UnitOfWork unitOfWork1 = store.beginUnitOfWork();

        UnitOfWork unitOfWork2 = store.beginUnitOfWork();

        store.lock(id);

        Assert.assertTrue(store.isLocked(id), "Objektet er ikke låst");
        Assert.assertTrue(storeService.isLocked(id), "Objektet er ikke låst ordentlig");

        store.commitUnitOfWork(unitOfWork2);

        Assert.assertTrue(store.isLocked(id), "Objektet ble låst opp");
        Assert.assertTrue(storeService.isLocked(id), "Objektet ble låst opp");
    }

    public void commitLevel1NotAllowed() {
        Injector injector = createInjector();
        Store store = injector.getInstance(Store.class);

        UnitOfWork unitOfWork = store.beginUnitOfWork();
        try {
            store.commitUnitOfWork(unitOfWork);
            Assert.fail("Skulle fått feilmelding");
        } catch (ImplementationException e) {
            Assert.assertTrue(e.getMessage().contains("Commit of UnitOfWork directly against server is not supported"));
        }
    }

    public void caching() {
        Injector injector = createInjector();
        Store store = injector.getInstance(Store.class);

        TestBubbleId<?> id = new TestBubbleId(1L);

        TestBubble testBubble1 = store.get(id);
        TestBubble testBubble2 = store.get(id);

        Assert.assertSame(testBubble2, testBubble1, "Fikk to forskjellige objekter, altså ingen caching");

        UnitOfWork unitOfWork = store.beginUnitOfWork();
        TestBubble testBubble3 = store.get(id);
        store.abortUnitOfWork(unitOfWork);

        Assert.assertSame(testBubble3, testBubble1, "Fikk to forskjellige objekter, altså ingen caching");
    }

    public void evict() {
        Injector injector = createInjector();
        Store store = injector.getInstance(Store.class);

        TestBubbleId<?> id = new TestBubbleId(1L);

        TestBubble testBubble1 = store.get(id);
        Assert.assertTrue(store.evict(id), "Evict at ikke låst boble skal gi true");
        TestBubble testBubble2 = store.get(id);

        Assert.assertNotSame(testBubble2, testBubble1, "Fikk tilbake samme objekt");
        Assert.assertEquals(testBubble2.getId(), testBubble1.getId(), "Fikk tilbake objekter med forskjellig id");
    }

    public void evictLaast() {
        Injector injector = createInjector();
        Store store = injector.getInstance(Store.class);
        try (UnitOfWork ignore = store.beginUnitOfWork()) {
            TestBubbleId<?> id = new TestBubbleId(1L);

            TestBubble testBubble1 = store.lock(id);
            Assert.assertFalse(store.evict(id), "Evict at låst boble skal gi false");
            TestBubble testBubble2 = store.get(id);

            Assert.assertSame(testBubble2, testBubble1, "Fikk ikke tilbake samme objekt");
            Assert.assertEquals(testBubble2.getId(), testBubble1.getId(), "Fikk tilbake objekter med forskjellig id");
        }
    }

    public void evictNonExistingId() {
        Injector injector = createInjector();
        Store store = injector.getInstance(Store.class);

        TestBubbleId<?> id = new TestBubbleId(1L);

        Assert.assertTrue(store.evict(id), "Evict av objekt som ikke er lastet skal gi true");
    }

    public void evictNull() {
        Injector injector = createInjector();
        Store store = injector.getInstance(Store.class);

        TestBubbleId<?> id = null;

        //noinspection ConstantConditions
        Assert.assertTrue(store.evict(id), "Evict av id=null skal gi true");
    }

    public void evictInUnitOfWork() {
        Injector injector = createInjector();
        Store store = injector.getInstance(Store.class);

        TestBubbleId<?> id = new TestBubbleId(1L);
        TestBubble testBubble1 = store.get(id);

        try (UnitOfWork ignore = store.beginUnitOfWork()) {
            Assert.assertTrue(store.evict(id), "Evict at ikke låst boble skal gi true");
            TestBubble testBubble2 = store.get(id);

            Assert.assertNotSame(testBubble2, testBubble1, "Fikk tilbake samme objekt");
            Assert.assertEquals(testBubble2.getId(), testBubble1.getId(), "Fikk tilbake objekter med forskjellig id");
        }
    }

    public void evictCollection() {
        Injector injector = createInjector();
        Store store = injector.getInstance(Store.class);

        TestBubbleId<?> id1 = new TestBubbleId(1L);
        TestBubbleId<?> id2 = new TestBubbleId(2L);
        ImmutableList<TestBubbleId<?>> ids = ImmutableList.of(id1, id2);

        List<TestBubble> testBubbles = store.getOrdered(ids);
        Assert.assertTrue(store.evict(ids), "Evict av ikke låst boble skal gi true");
        List<TestBubble> testBubbles2 = store.getOrdered(ids);

        Assert.assertNotSame(testBubbles.get(0), testBubbles2.get(0), "Fikk tilbake samme objekt");
        Assert.assertNotSame(testBubbles.get(1), testBubbles2.get(1), "Fikk tilbake samme objekt");
    }

    public void evictCollectionLocked() {
        Injector injector = createInjector();
        Store store = injector.getInstance(Store.class);

        try (UnitOfWork ignore=store.beginUnitOfWork()) {
            TestBubbleId<?> id1 = new TestBubbleId(1L);
            TestBubbleId<?> id2 = new TestBubbleId(2L);
            ImmutableList<TestBubbleId<?>> ids = ImmutableList.of(id1, id2);

            List<TestBubble> testBubbles = store.getOrdered(ids);
            TestBubble lockedBubble = store.lock(id2);
            Assert.assertFalse(store.evict(ids), "Evict med låst boble skal gi false");
            List<TestBubble> testBubbles2 = store.getOrdered(ids);

            Assert.assertNotSame(testBubbles.get(0), testBubbles2.get(0), "Fikk tilbake samme objekt");
            Assert.assertSame(lockedBubble, testBubbles2.get(1), "Fikk ikke tilbake samme objekt");
        }
    }

    public void evictAll() {
        Injector injector = createInjector();
        Store store = injector.getInstance(Store.class);

        TestBubbleId<?> id = new TestBubbleId(1L);

        TestBubble testBubble1 = store.get(id);
        Assert.assertTrue(store.evictAll());
        TestBubble testBubble2 = store.get(id);

        Assert.assertNotSame(testBubble2, testBubble1, "Fikk tilbake samme objekt");
        Assert.assertEquals(testBubble2.getId(), testBubble1.getId(), "Fikk tilbake objekter med forskjellig id");
    }

    public void evictAllMedLaastObjekt() {
        Injector injector = createInjector();
        Store store = injector.getInstance(Store.class);

        try (UnitOfWork ignore = store.beginUnitOfWork()) {
            TestBubbleId<?> id = new TestBubbleId(1L);

            TestBubble testBubble1 = store.lock(id);
            assertThat(store.evictAll()).isFalse();
            TestBubble testBubble2 = store.get(id);

            Assert.assertSame(testBubble2, testBubble1, "Fikk ikke tilbake samme objekt");
        }
    }

    public void testLockSingleUngotten() {
        SimpleId<?> id = new SimpleId<>(17L);
        Simple object = new Simple(id);

        StoreService storeService = Mockito.mock(StoreService.class);
        LockService lockService = Mockito.mock(LockService.class);
        Mockito.doReturn(object).when(lockService).lock(id);
        IdService idService = Mockito.mock(IdService.class);

        Injector injector = Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                bind(IdService.class).toInstance(idService);
            }
        });

        StoreSessionClient storeSessionClient = new StoreSessionClient(
                storeService,
                lockService,
                SnapshotVersionContext.getInstance()
        );

        StoreClient storeClient = new StoreClient(storeSessionClient, injector);
        try (UnitOfWork ignored = storeClient.beginUnitOfWork()) {
            Simple locked = storeClient.lock(id);

            assertThat(locked).isNotSameAs(object).isEqualTo(object);
            Mockito.verify(lockService).lock(id);
            Mockito.verifyNoMoreInteractions(storeService, idService);

            Simple locked2 = storeClient.lock(id);
            assertThat(locked2).isSameAs(locked);
            Mockito.verifyNoMoreInteractions(storeService, idService);
        }

        Mockito.verify(lockService).unlockForList(ImmutableSet.of(id));
        Mockito.verifyNoMoreInteractions(storeService, lockService, idService);
    }

    public void testLockSingleGotten() {
        SimpleId<?> id = new SimpleId<>(17L);
        Simple object = new Simple(id);
        Simple objectLocked = new Simple(id, "A");

        StoreService storeService = Mockito.mock(StoreService.class);
        LockService lockService = Mockito.mock(LockService.class);
        Mockito.doReturn(object).when(storeService).getObject(id);
        Mockito.doReturn(objectLocked).when(lockService).lock(id);
        IdService idService = Mockito.mock(IdService.class);

        Injector injector = Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                bind(IdService.class).toInstance(idService);
            }
        });

        StoreSessionClient storeSessionClient = new StoreSessionClient(
                storeService,
                lockService,
                SnapshotVersionContext.getInstance()
        );

        StoreClient storeClient = new StoreClient(storeSessionClient, injector);
        try (UnitOfWork ignored = storeClient.beginUnitOfWork()) {
            Simple gotten = storeClient.get(id);
            assertThat(gotten).isSameAs(object);
            assertThat(gotten.getText()).isNull();
            Mockito.verify(storeService).getObject(id);

            Simple locked = storeClient.lock(id);

            assertThat(locked).isNotSameAs(gotten).isNotSameAs(objectLocked).isEqualTo(objectLocked);
            assertThat(locked.getText()).isEqualTo("A");
            Mockito.verify(lockService).lock(id);
            Mockito.verifyNoMoreInteractions(storeService, lockService, idService);
        }
    }

    public void testLockSinglePrelocked() {
        SimpleId<?> id = new SimpleId<>(17L);
        Simple object = new Simple(id);

        StoreService storeService = Mockito.mock(StoreService.class);
        LockService lockService = Mockito.mock(LockService.class);
        Mockito.doReturn(object).when(storeService).getObject(id);
        Mockito.doReturn(object).when(lockService).lock(id);
        IdService idService = Mockito.mock(IdService.class);

        Injector injector = Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                bind(IdService.class).toInstance(idService);
            }
        });

        StoreSessionClient storeSessionClient = new StoreSessionClient(
                storeService,
                lockService,
                SnapshotVersionContext.getInstance()
        );

        StoreClient storeClient = new StoreClient(storeSessionClient, injector);
        try (UnitOfWork ignored = storeClient.beginUnitOfWork()) {
            Simple gotten = storeClient.get(id);
            assertThat(gotten).isSameAs(object);
            Mockito.verify(storeService).getObject(id);

            Simple locked = storeClient.lock(id);

            assertThat(locked).isNotSameAs(gotten).isEqualTo(gotten);
            Mockito.verify(lockService).lock(id);
            Mockito.verifyNoMoreInteractions(storeService, lockService, idService);
        }
    }

    // At refresh blir kalt enkeltvis er en implementasjonsdetalj, ikke slik det skal være
    public void testLockMultipleUngotten() {
        SimpleId<?> id1 = new SimpleId<>(1L);
        SimpleId<?> id2 = new SimpleId<>(2L);
        Simple object1 = new Simple(id1, "A");
        Simple object2 = new Simple(id2, "B");

        StoreService storeService = Mockito.mock(StoreService.class);
        LockService lockService = Mockito.mock(LockService.class);
        Mockito.doReturn(ImmutableSet.of(object1, object2)).when(lockService).lockForList(ImmutableSet.of(id1, id2));
        IdService idService = Mockito.mock(IdService.class);

        Injector injector = Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                bind(IdService.class).toInstance(idService);
            }
        });

        StoreSessionClient storeSessionClient = new StoreSessionClient(
                storeService,
                lockService,
                SnapshotVersionContext.getInstance()
        );

        StoreClient storeClient = new StoreClient(storeSessionClient, injector);
        try (UnitOfWork ignored = storeClient.beginUnitOfWork()) {
            Set<Simple> locked = storeClient.lock(ImmutableSet.of(id1, id2));
            ImmutableMap<? extends SimpleId<?>, Simple> lockedMap = Maps.uniqueIndex(locked, Simple::getId);

            assertThat(locked).containsOnly(object1, object2);
            assertThat(lockedMap.get(id1)).isNotSameAs(object1).isEqualTo(object1);
            assertThat(lockedMap.get(id2)).isNotSameAs(object2).isEqualTo(object2);
            Mockito.verify(lockService).lockForList(ImmutableSet.of(id1, id2));
            Mockito.verifyNoMoreInteractions(storeService, idService);

            Set<Simple> locked2 = storeClient.lock(ImmutableSet.of(id1, id2));
            Assertions.assertThat(locked2).containsOnly(object1, object2);
            ImmutableMap<? extends SimpleId<?>, Simple> lockedMap2 = Maps.uniqueIndex(locked2, Simple::getId);
            assertThat(lockedMap2.get(id1)).isSameAs(lockedMap.get(id1));
            assertThat(lockedMap2.get(id2)).isSameAs(lockedMap.get(id2));
            Mockito.verifyNoMoreInteractions(storeService, idService);
        }

        Mockito.verify(lockService).unlockForList(ImmutableSet.of(id1, id2));
        Mockito.verifyNoMoreInteractions(storeService, lockService, idService);
    }

    public void testLockMultipleOneGotten() {
        SimpleId<?> id1 = new SimpleId<>(1L);
        SimpleId<?> id2 = new SimpleId<>(2L);
        Simple object1 = new Simple(id1);
        Simple object1Locked = new Simple(id1, "A");
        Simple object2 = new Simple(id2, "B");

        StoreService storeService = Mockito.mock(StoreService.class);
        LockService lockService = Mockito.mock(LockService.class);
        Mockito.doReturn(object1).when(storeService).getObject(id1);
        Mockito.doReturn(ImmutableSet.of(object1Locked, object2)).when(lockService).lockForList(ImmutableSet.of(id1, id2));
        IdService idService = Mockito.mock(IdService.class);

        Injector injector = Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                bind(IdService.class).toInstance(idService);
            }
        });

        StoreSessionClient storeSessionClient = new StoreSessionClient(
                storeService,
                lockService,
                SnapshotVersionContext.getInstance()
        );

        StoreClient storeClient = new StoreClient(storeSessionClient, injector);
        try (UnitOfWork ignored = storeClient.beginUnitOfWork()) {
            Simple gotten = storeClient.get(id1);
            assertThat(gotten).isSameAs(object1);
            assertThat(gotten.getText()).isNull();
            Mockito.verify(storeService).getObject(id1);

            Set<Simple> locked = storeClient.lock(ImmutableSet.of(id1, id2));
            ImmutableMap<? extends SimpleId<?>, Simple> lockedMap = Maps.uniqueIndex(locked, Simple::getId);

            assertThat(locked).containsOnly(object1, object2);
            Simple locked1 = lockedMap.get(id1);
            Simple locked2 = lockedMap.get(id2);
            assertThat(locked1).isNotSameAs(object1Locked).isNotSameAs(gotten).isEqualTo(gotten).isEqualTo(object1Locked);
            assertThat(locked2).isNotSameAs(object2).isEqualTo(object2);
            assertThat(locked1.getText()).isEqualTo("A");
            assertThat(locked2.getText()).isEqualTo("B");
            Mockito.verify(lockService).lockForList(ImmutableSet.of(id1, id2));
            Mockito.verifyNoMoreInteractions(storeService, idService);
        }

        Mockito.verify(lockService).unlockForList(ImmutableSet.of(id1, id2));
        Mockito.verifyNoMoreInteractions(storeService, lockService, idService);
    }

    // Denne testen er foreløpig ikke mulig, da klienten ikke ser forskjell på nye og gamle låser.
    @Test(enabled = false)
    public void testLockMultipleAllPrelocked() {
        SimpleId<?> id1 = new SimpleId<>(1L);
        SimpleId<?> id2 = new SimpleId<>(2L);
        Simple object1 = new Simple(id1, "A");
        Simple object2 = new Simple(id2, "B");

        StoreService storeService = Mockito.mock(StoreService.class);
        LockService lockService = Mockito.mock(LockService.class);
        Mockito.doReturn(ImmutableSet.of(object1, object2)).when(storeService).getObjects(ImmutableSet.of(id1, id2));
        Mockito.doReturn(ImmutableSet.of(object1, object2)).when(lockService).lockForList(ImmutableSet.of(id1, id2));
        IdService idService = Mockito.mock(IdService.class);

        Injector injector = Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                bind(IdService.class).toInstance(idService);
            }
        });

        StoreSessionClient storeSessionClient = new StoreSessionClient(
                storeService,
                lockService,
                SnapshotVersionContext.getInstance()
        );

        StoreClient storeClient = new StoreClient(storeSessionClient, injector);
        try (UnitOfWork ignored = storeClient.beginUnitOfWork()) {
            Set<Simple> gotten = storeClient.get(ImmutableSet.of(id1, id2));
            assertThat(gotten).containsOnly(object1, object2);
            ImmutableMap<? extends SimpleId<?>, Simple> gottenMap = Maps.uniqueIndex(gotten, Simple::getId);
            assertThat(gottenMap.get(id1)).isSameAs(object1);
            assertThat(gottenMap.get(id2)).isSameAs(object2);
            Mockito.verify(storeService).getObjects(ImmutableSet.of(id1, id2));

            Set<Simple> locked = storeClient.lock(ImmutableSet.of(id1, id2));
            ImmutableMap<? extends SimpleId<?>, Simple> lockedMap = Maps.uniqueIndex(locked, Simple::getId);

            assertThat(locked).containsOnly(object1, object2);
            assertThat(lockedMap.get(id1)).isNotSameAs(gottenMap.get(id1)).isEqualTo(object1);
            assertThat(lockedMap.get(id2)).isNotSameAs(gottenMap.get(id2)).isEqualTo(object2);
            Mockito.verify(lockService).lockForList(ImmutableSet.of(id1, id2));
            Mockito.verifyNoMoreInteractions(storeService, lockService, idService);
        }

        Mockito.verifyNoMoreInteractions(storeService, lockService, idService);
    }

    // Denne testen er foreløpig ikke mulig, da klienten ikke ser forskjell på nye og gamle låser.
    @Test(enabled = false)
    public void testLockMultipleOnePrelockedOtherUngotten() {
        SimpleId<?> id1 = new SimpleId<>(1L);
        SimpleId<?> id2 = new SimpleId<>(2L);
        Simple object1 = new Simple(id1, "A");
        Simple object2 = new Simple(id2, "B");

        StoreService storeService = Mockito.mock(StoreService.class);
        LockService lockService = Mockito.mock(LockService.class);
        Mockito.doReturn(object1).when(storeService).getObject(id1);
        Mockito.doReturn(ImmutableSet.of(object1, object2)).when(lockService).lockForList(ImmutableSet.of(id1, id2));
        IdService idService = Mockito.mock(IdService.class);

        Injector injector = Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                bind(IdService.class).toInstance(idService);
            }
        });

        StoreSessionClient storeSessionClient = new StoreSessionClient(
                storeService,
                lockService,
                SnapshotVersionContext.getInstance()
        );

        StoreClient storeClient = new StoreClient(storeSessionClient, injector);
        try (UnitOfWork ignored = storeClient.beginUnitOfWork()) {
            Simple gotten = storeClient.get(id1);
            assertThat(gotten).isSameAs(object1);
            Mockito.verify(storeService).getObject(id1);

            Set<Simple> locked = storeClient.lock(ImmutableSet.of(id1, id2));
            ImmutableMap<? extends SimpleId<?>, Simple> lockedMap = Maps.uniqueIndex(locked, Simple::getId);

            assertThat(locked).containsOnly(object1, object2);
            assertThat(lockedMap.get(id1)).isNotSameAs(gotten).isNotSameAs(object1).isEqualTo(object1);
            assertThat(lockedMap.get(id2)).isNotSameAs(object2).isEqualTo(object2);
            Mockito.verify(lockService).lockForList(ImmutableSet.of(id1, id2));
            Mockito.verifyNoMoreInteractions(storeService, lockService, idService);
        }

        Mockito.verifyNoMoreInteractions(storeService, lockService, idService);
    }

    // Denne testen er foreløpig ikke mulig, da klienten ikke ser forskjell på nye og gamle låser.
    @Test(enabled = false)
    public void testLockMultipleOnePrelockedOtherGotten() {
        SimpleId<?> id1 = new SimpleId<>(1L);
        SimpleId<?> id2 = new SimpleId<>(2L);
        Simple object1 = new Simple(id1, "A");
        Simple object2 = new Simple(id2);
        Simple object2Locked = new Simple(id2, "B");

        StoreService storeService = Mockito.mock(StoreService.class);
        LockService lockService = Mockito.mock(LockService.class);
        Mockito.doReturn(ImmutableSet.of(object1, object2)).when(storeService).getObjects(ImmutableSet.of(id1, id2));
        Mockito.doReturn(ImmutableSet.of(object1, object2Locked)).when(lockService).lockForList(ImmutableSet.of(id1, id2));
        IdService idService = Mockito.mock(IdService.class);

        Injector injector = Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                bind(IdService.class).toInstance(idService);
            }
        });

        StoreSessionClient storeSessionClient = new StoreSessionClient(
                storeService,
                lockService,
                SnapshotVersionContext.getInstance()
        );

        StoreClient storeClient = new StoreClient(storeSessionClient, injector);
        try (UnitOfWork ignored = storeClient.beginUnitOfWork()) {
            Set<Simple> gotten = storeClient.get(ImmutableSet.of(id1, id2));
            ImmutableMap<? extends SimpleId<?>, Simple> gottenMap = Maps.uniqueIndex(gotten, Simple::getId);
            assertThat(gottenMap.get(id1)).isSameAs(object1);
            assertThat(gottenMap.get(id2)).isSameAs(object2);
            assertThat(gottenMap.get(id1).getText()).isEqualTo("A");
            assertThat(gottenMap.get(id2).getText()).isNull();
            Mockito.verify(storeService).getObjects(ImmutableSet.of(id1, id2));

            Set<Simple> locked = storeClient.lock(ImmutableSet.of(id1, id2));
            ImmutableMap<? extends SimpleId<?>, Simple> lockedMap = Maps.uniqueIndex(locked, Simple::getId);

            Assertions.assertThat(locked).containsOnly(object1, object2);
            assertThat(lockedMap.get(id1)).isNotSameAs(object1).isEqualTo(object1);
            assertThat(lockedMap.get(id2)).isNotSameAs(object2Locked).isNotSameAs(gottenMap.get(id2)).isEqualTo(object2Locked);
            assertThat(lockedMap.get(id1).getText()).isEqualTo("A");
            assertThat(lockedMap.get(id2).getText()).isEqualTo("B");
            Mockito.verify(lockService).lockForList(ImmutableSet.of(id1, id2));
            Mockito.verifyNoMoreInteractions(storeService, lockService, idService);
        }
        
        Mockito.verifyNoMoreInteractions(storeService, lockService, idService);
    }

    /**
     * En mockup-StoreService som bare returnerer nyinstansierte bobleobjekter.
     */
    public static class StoreClientTestStoreService implements StoreService, LockService {
        private final Set<BubbleId<?>> lockedIds = new HashSet<>();

        public void clearLocks() {
            lockedIds.clear();
        }

        private <T extends BubbleObject, I extends BubbleId<? extends T>> T createBubble(I id) {
            try {
                Class<? extends T> bubbleType = id.getType();
                T bubble = bubbleType.newInstance();
                bubble.setId(id);
                return bubble;
            } catch (InstantiationException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public <T extends BubbleObject> T getObject(BubbleId<? extends T> id) {
            return createBubble(id);
        }

        @Override
        public <T extends BubbleObject, I extends BubbleId<? extends T>> Collection<T> getObjects(Collection<I> ids) {
            List<T> objects = new ArrayList<>(ids.size());
            for (I id : ids) {
                objects.add(getObject(id));
            }
            return objects;
        }

        @Override
        public <T extends BubbleObject, I extends BubbleId<? extends T>> Collection<T> getObjectsIgnoreMissing(Collection<I> ids) {
            return getObjects(ids);
        }

        @Override
        public <I extends BubbleId<?>> List<I> getVersions(I id, SnapshotVersion start, SnapshotVersion end) {
            throw new UnsupportedOperationException();
        }

        @Override
        public <I extends BubbleId<?>> Map<I, List<I>> getVersionsForList(Collection<? extends I> ids, SnapshotVersion start, SnapshotVersion end) {
            throw new UnsupportedOperationException();
        }

        @Override
        public <T extends BubbleObject> T lock(BubbleId<? extends T> id) {
            T bubble = createBubble(id);
            lockedIds.add(id);
            return bubble;
        }

        @Override
        public <T extends BubbleObject, I extends BubbleId<? extends T>> Collection<T> lockForList(Collection<I> ids) {
            List<T> objects = new ArrayList<>(ids.size());
            for (I id : ids) {
                objects.add(lock(id));
            }
            return objects;
        }

        @Override
        public <I extends BubbleId<?>> void unlock(I id) {
            lockedIds.remove(id);
        }

        @Override
        public void unlockForList(Collection<? extends BubbleId<?>> ids) {
            lockedIds.removeAll(ids);
        }

        @Override
        public <I extends BubbleId<?>> boolean isLocked(I id) {
            return lockedIds.contains(id);
        }
    }
}
