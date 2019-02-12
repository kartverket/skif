package no.statkart.skif.store.service;

import com.google.inject.Inject;
import no.statkart.skif.exception.LockedException;
import no.statkart.skif.store.BubbleId;
import no.statkart.skif.store.BubbleObject;
import no.statkart.skif.store.Store;

import java.util.Collection;

public class LockServiceImpl implements LockService {
    @Inject
    protected Store store;

    @Override
    public <I extends BubbleId<?>> boolean isLocked(I id) {
        return store.isLocked(id);
    }

    @Override
    public <T extends BubbleObject> T lock(BubbleId<? extends T> id) throws LockedException {
        final T bubbleObject = store.lock(id);
        store.ensureFullyLoaded(bubbleObject);
        return bubbleObject;
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> Collection<T> lockForList(Collection<I> ids) {
        Collection<T> bubbleObjects = store.lock(ids);
        // TODO: Opptimaliser for bulk
        for (T bubbleObject : bubbleObjects) {
            store.ensureFullyLoaded(bubbleObject);
        }
        return bubbleObjects;
    }

    @Override
    public <I extends BubbleId<?>> void unlock(I id) {
        store.unlock(id);
    }

    @Override
    public void unlockForList(Collection<? extends BubbleId<?>> ids) {
        store.unlock(ids);
    }

}
