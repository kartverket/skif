package no.statkart.skif.store;

import com.google.inject.*;
import no.statkart.skif.mockup.TestIdServiceLong;
import no.statkart.skif.mockup.TestNumber;
import no.statkart.skif.service.sequence.IdService;
import no.statkart.skif.store.service.StoreService;
import no.statkart.skif.storetest.domain.demo.TestBubbleId;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.*;

/**
 * Tester grunnleggende ting i {@link StoreClient}.
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

    @Test(groups = "broken")
    public void endNestedUnitOfWork() {
        Injector injector = createInjector();
        Store store = injector.getInstance(Store.class);

        store.beginUnitOfWork();
        store.beginUnitOfWork();

        try {
            store.endUnitOfWork();
            Assert.fail("Skulle fått feilmelding");
        } catch (Exception e) {
            Assert.assertTrue(e.getMessage().contains("In nested UnitOfWork"));
        }
    }

    @Test(groups = "broken")
    public void forgetLocksOnEndUnitOfWork() {
        Injector injector = createInjector();
        Store store = injector.getInstance(Store.class);

        TestBubbleId<?> id = new TestBubbleId(1L);

        store.beginUnitOfWork();
        store.lock(id);
        Assert.assertTrue(store.isLocked(id), "Objektet ble ikke låst");
        store.endUnitOfWork();

        // Simuler at tjeneren åpner alle låser for brukeren
        injector.getInstance(StoreClientTestStoreService.class).clearLocks();

        Assert.assertFalse(store.isLocked(id), "Objektet er fortsatt låst");
    }

    public static class StoreClientTestStoreService implements StoreService {
        private final Map<BubbleId<?>, BubbleObject> lockedMap = new HashMap<BubbleId<?>, BubbleObject>();

        public void clearLocks() {
            lockedMap.clear();
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
        public <T extends BubbleObject, I extends BubbleId<? extends T>> T getObject(I id) {
            Class<? extends T> bubbleType = id.getType();
            T bubbleObject = bubbleType.cast(lockedMap.get(id));
            if (bubbleObject == null) {
                bubbleObject = createBubble(id);
            }
            return bubbleObject;
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
        public <I extends BubbleId<?>> List<I> getVersions(I id, SnapshotVersion start, SnapshotVersion end) {
            throw new UnsupportedOperationException();
        }

        @Override
        public <I extends BubbleId<?>> Map<I, List<I>> getVersionsForList(List<I> ids, SnapshotVersion start, SnapshotVersion end) {
            throw new UnsupportedOperationException();
        }

        @Override
        public <T extends BubbleObject, I extends BubbleId<? extends T>> T lock(I id) {
            Class<? extends T> bubbleType = id.getType();
            T bubbleObject = bubbleType.cast(lockedMap.get(id));
            if (bubbleObject == null) {
                bubbleObject = createBubble(id);
                lockedMap.put(id, bubbleObject);
            }
            return bubbleObject;
        }

        @Override
        public <T extends BubbleObject, I extends BubbleId<? extends T>> void unlock(I id) {
            lockedMap.remove(id);
        }

        @Override
        public <T extends BubbleObject, I extends BubbleId<? extends T>> boolean isLocked(I id) {
            return lockedMap.containsKey(id);
        }
    }
}
