package no.statkart.skif.store.memorydomain;

import com.google.inject.Injector;
import no.statkart.skif.exception.ObjectNotFoundException;
import no.statkart.skif.store.BubbleId;
import no.statkart.skif.store.BubbleObject;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.SnapshotVersionContext;
import no.statkart.skif.store.Store;
import no.statkart.skif.store.StoreCache;
import no.statkart.skif.store.StoreClient;
import no.statkart.skif.store.StoreSessionClient;
import no.statkart.skif.store.service.LockService;
import no.statkart.skif.store.service.StoreService;
import org.mockito.Mockito;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

public class MemoryDomainHelper {
    public static Store createStore(Function<BubbleId<?>, BubbleObject> objectSupplier) {
        StoreService storeService = new StoreService() {
            @SuppressWarnings("unchecked")
            @Override
            public <T extends BubbleObject> T getObject(BubbleId<? extends T> id) {
                BubbleObject object = objectSupplier.apply(id);
                if (object == null) throw new ObjectNotFoundException(id);
                return (T) object;
            }

            @Override
            public <T extends BubbleObject, I extends BubbleId<? extends T>> Collection<T> getObjects(Collection<I> ids) {
                return ids.stream()
                        .map(this::getObject)
                        .collect(Collectors.toList());
            }

            @SuppressWarnings("unchecked")
            @Override
            public <T extends BubbleObject, I extends BubbleId<? extends T>> Collection<T> getObjectsIgnoreMissing(Collection<I> ids) {
                return ids.stream()
                        .map(objectSupplier)
                        .filter(Objects::nonNull)
                        .map(object -> (T) object)
                        .collect(Collectors.toList());
            }

            @Override
            public <I extends BubbleId<?>> List<I> getVersions(I id, SnapshotVersion start, SnapshotVersion end) {
                throw new UnsupportedOperationException();
            }

            @Override
            public <I extends BubbleId<?>> Map<I, List<I>> getVersionsForList(Collection<? extends I> ids, SnapshotVersion start, SnapshotVersion end) {
                throw new UnsupportedOperationException();
            }
        };

        LockService lockService = new LockService() {
            @Override
            public <T extends BubbleObject> T lock(BubbleId<? extends T> id) {
                return storeService.getObject(id);
            }

            @Override
            public <T extends BubbleObject, I extends BubbleId<? extends T>> Collection<T> lockForList(Collection<I> ids) {
                return storeService.getObjects(ids);
            }

            @Override
            public <I extends BubbleId<?>> void unlock(I id) {
            }

            @Override
            public void unlockForList(Collection<? extends BubbleId<?>> ids) {
            }

            @Override
            public <I extends BubbleId<?>> boolean isLocked(I id) {
                return false;
            }
        };

        StoreSessionClient storeSessionClient = new StoreSessionClient(storeService, lockService, SnapshotVersionContext.getInstance(), new StoreCache());

        return new StoreClient(storeSessionClient, Mockito.mock(Injector.class));
    }
}
