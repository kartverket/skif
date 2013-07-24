package no.statkart.skif.store;

import com.google.inject.*;
import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.mockup.TestIdServiceLong;
import no.statkart.skif.mockup.TestNumber;
import no.statkart.skif.service.sequence.IdService;
import no.statkart.skif.store.service.StoreService;
import no.statkart.skif.storetest.domain.demo.TestBubble;
import no.statkart.skif.storetest.domain.demo.TestBubbleId;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.*;

/**
 * Tester grunnleggende ting i {@link StoreClient}. Dette er stort sett implementert i diverse session-klasser.
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
                bind(StoreClientTestStoreService.class).in(Singleton.class);
            }

            @Provides
            @Singleton
            protected StoreClient provideStoreClient(StoreService storeService, Injector injector) {
                StoreSessionClient storeSessionClient = new StoreSessionClient(storeService);
                return new StoreClient(storeSessionClient, injector);
            }
        };
        return Guice.createInjector(module);
    }

    public void endNestedUnitOfWork() {
        Injector injector = createInjector();
        Store store = injector.getInstance(Store.class);

        store.beginUnitOfWork();
        store.beginUnitOfWork();

        try {
            store.endUnitOfWork();
            Assert.fail("Skulle fått feilmelding");
        } catch (ImplementationException e) {
            Assert.assertTrue(e.getMessage().contains("In nested UnitOfWork"));
        }
    }

    public void forgetLocksOnEndUnitOfWork() {
        Injector injector = createInjector();
        Store store = injector.getInstance(Store.class);

        TestBubbleId<?> id = new TestBubbleId(1L);

        store.beginUnitOfWork();
        store.lock(id);
        Assert.assertTrue(store.isLocked(id), "Objektet ble ikke låst");
        store.getUnitOfWorkTransfer();
        store.endUnitOfWork();

        // Simuler at tjeneren åpner alle låser for brukeren
        injector.getInstance(StoreClientTestStoreService.class).clearLocks();

        Assert.assertFalse(store.isLocked(id), "Objektet er fortsatt låst");
    }

    public void unlockOnAbortUnitOfWork() {
        Injector injector = createInjector();
        Store store = injector.getInstance(Store.class);
        StoreClientTestStoreService storeService = injector.getInstance(StoreClientTestStoreService.class);

        TestBubbleId<?> id = new TestBubbleId(1L);

        store.beginUnitOfWork();
        store.lock(id);

        Assert.assertTrue(store.isLocked(id), "Objektet er ikke låst");
        Assert.assertTrue(storeService.isLocked(id), "Objektet er ikke låst ordentlig");

        store.abortUnitOfWork();

        Assert.assertFalse(store.isLocked(id), "Objektet ble ikke låst opp");
        Assert.assertFalse(storeService.isLocked(id), "Objektet ble ikke låst opp ordentlig");
    }

    public void dontUnlockOnCommitUnitOfWork() {
        Injector injector = createInjector();
        Store store = injector.getInstance(Store.class);
        StoreClientTestStoreService storeService = injector.getInstance(StoreClientTestStoreService.class);

        TestBubbleId<?> id = new TestBubbleId(1L);

        store.beginUnitOfWork();

        store.beginUnitOfWork();

        store.lock(id);

        Assert.assertTrue(store.isLocked(id), "Objektet er ikke låst");
        Assert.assertTrue(storeService.isLocked(id), "Objektet er ikke låst ordentlig");

        store.commitUnitOfWork();

        Assert.assertTrue(store.isLocked(id), "Objektet ble låst opp");
        Assert.assertTrue(storeService.isLocked(id), "Objektet ble låst opp");
    }

    public void commitLevel1NotAllowed() {
        Injector injector = createInjector();
        Store store = injector.getInstance(Store.class);

        store.beginUnitOfWork();
        try {
            store.commitUnitOfWork();
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

        store.beginUnitOfWork();
        TestBubble testBubble3 = store.get(id);
        store.abortUnitOfWork();

        Assert.assertSame(testBubble3, testBubble1, "Fikk to forskjellige objekter, altså ingen caching");
    }

    public void evict() {
        Injector injector = createInjector();
        Store store = injector.getInstance(Store.class);

        TestBubbleId<?> id = new TestBubbleId(1L);

        TestBubble testBubble1 = store.get(id);
        store.evict(id);
        TestBubble testBubble2 = store.get(id);

        Assert.assertNotSame(testBubble2, testBubble1, "Fikk tilbake samme objekt");
        Assert.assertEquals(testBubble2.getId(), testBubble1.getId(), "Fikk tilbake objekter med forskjellig id");
    }

    public void evictAll() {
        Injector injector = createInjector();
        Store store = injector.getInstance(Store.class);

        TestBubbleId<?> id = new TestBubbleId(1L);

        TestBubble testBubble1 = store.get(id);
        store.evictAll();
        TestBubble testBubble2 = store.get(id);

        Assert.assertNotSame(testBubble2, testBubble1, "Fikk tilbake samme objekt");
        Assert.assertEquals(testBubble2.getId(), testBubble1.getId(), "Fikk tilbake objekter med forskjellig id");
    }

    /**
     * En mockup-StoreService som bare returnerer nyinstansierte bobleobjekter.
     */
    public static class StoreClientTestStoreService implements StoreService {
        private final Set<BubbleId<?>> lockedIds = new HashSet<BubbleId<?>>();

        public void clearLocks() {
            lockedIds.clear();
        }

        private <T extends BubbleObject, I extends BubbleId<? extends T>> T createBubble(I id) {
            try {
                Class<? extends T> bubbleType = id.getType();
                T bubble = bubbleType.newInstance();
                bubble.setId(id);
                return bubble;
            } catch (InstantiationException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public <T extends BubbleObject> T getObject(BubbleId<? extends T> id) {
            return createBubble(id);
        }

        @Override
        public <T extends BubbleObject, I extends BubbleId<? extends T>> Collection<T> getObjects(Collection<I> ids) {
            List<T> objects = new ArrayList<T>(ids.size());
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
        public <I extends BubbleId<?>> Map<I, List<I>> getVersionsForList(List<I> ids, SnapshotVersion start, SnapshotVersion end) {
            throw new UnsupportedOperationException();
        }

        @Override
        public <T extends BubbleObject> T lock(BubbleId<? extends T> id) {
            T bubble = createBubble(id);
            lockedIds.add(id);
            return bubble;
        }

        @Override
        public <I extends BubbleId<?>> void unlock(I id) {
            lockedIds.remove(id);
        }

        @Override
        public <I extends BubbleId<?>> boolean isLocked(I id) {
            return lockedIds.contains(id);
        }
    }
}
