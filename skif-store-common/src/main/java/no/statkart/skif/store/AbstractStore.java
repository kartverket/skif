package no.statkart.skif.store;

import com.google.common.collect.Sets;
import com.google.inject.Injector;
import com.google.inject.Key;
import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.exception.NotImplementedException;
import no.statkart.skif.util.CopyHelper;

import javax.annotation.Nullable;
import java.util.*;

import static no.statkart.skif.guava.Preconditions.checkNotNull;

/**
 * @author Henrik Fredholm
 */
public class AbstractStore implements Store {
    protected WrappableStoreSession storeSession;
    final private Injector injector;

    public AbstractStore(WrappableStoreSession storeSession, Injector injector) {
        this.injector = injector;
        this.storeSession = storeSession;
    }

    protected StoreUnitOfWork storeUnitOfWork() {
        if (storeSession instanceof StoreUnitOfWork) return (StoreUnitOfWork) storeSession;
        throw new ImplementationException("Not in UnitOfWork");
    }

    @Override
    public <T> T getInstance(Class<T> type) {
        return injector.getInstance(type);
    }

    public <T> T getInstance(Key<T> key) {
        return injector.getInstance(key);
    }

    @Override
    public void clear() {
        throw new NotImplementedException("todo");
    }

    @Override
    public <T extends BubbleObject> T get(@Nullable BubbleId<? extends T> bubbleId) {
        if (bubbleId == null) return null;
        return storeSession.get(bubbleId);
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> Collection<T> get(Collection<I> bubbleIds) {
        return storeSession.get(bubbleIds);
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> Set<T> get(Set<I> bubbleIds) {
        return storeSession.get(bubbleIds);
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> List<T> get(List<I> bubbleIds) {
        return storeSession.get(bubbleIds);
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> void get(Collection<I> bubbleIds, Collection<T> bubbleObjects) {
        storeSession.get(bubbleIds, bubbleObjects);
    }


    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> Collection<T> getOrdered(Collection<I> bubbleIds) {
        return storeSession.getOrdered(bubbleIds);
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> Set<T> getOrdered(Set<I> bubbleIds) {
        return storeSession.getOrdered(bubbleIds);
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> List<T> getOrdered(List<I> bubbleIds) {
        return storeSession.getOrdered(bubbleIds);
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> void getOrdered(Collection<I> bubbleIds, Collection<T> bubbleObjects) {
        storeSession.getOrdered(bubbleIds, bubbleObjects);
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> Collection<T> getIgnoreMissing(Collection<I> bubbleIds) {
        return storeSession.getIgnoreMissing(bubbleIds);
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> Set<T> getIgnoreMissing(Set<I> bubbleIds) {
        return storeSession.getIgnoreMissing(bubbleIds);
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> List<T> getIgnoreMissing(List<I> bubbleIds) {
        return storeSession.getIgnoreMissing(bubbleIds);
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> void getIgnoreMissing(Collection<I> bubbleIds, Collection<T> bubbleObjects) {
        storeSession.getIgnoreMissing(bubbleIds, bubbleObjects);
    }

    @Override
    public <T extends BubbleObject> T lock(@Nullable BubbleId<? extends T> bubbleId) {
        if (bubbleId == null) return null;
        return storeSession.lock(bubbleId);
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> Collection<T> lock(Collection<I> bubbleIds) {
        List<T> bubbleObjects = new ArrayList<T>(bubbleIds.size());
        lock(bubbleIds, bubbleObjects);
        return bubbleObjects;
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> Set<T> lock(Set<I> bubbleIds) {
        Set<T> bubbleObjects = new HashSet<T>(bubbleIds.size());
        lock(bubbleIds, bubbleObjects);
        return bubbleObjects;
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> List<T> lock(List<I> bubbleIds) {
        List<T> bubbleObjects = new ArrayList<T>(bubbleIds.size());
        lock(bubbleIds, bubbleObjects);
        return bubbleObjects;
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> void lock(Collection<I> bubbleIds, Collection<T> bubbleObjects) {
        // TODO: implementer som batch
        for (I bubbleId : bubbleIds) {
            bubbleObjects.add(storeSession.lock(bubbleId));
        }
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> void unlock(@Nullable I bubbleId) {
        if (bubbleId == null) return;
        storeSession.unlock(bubbleId);
    }

    @Override
    public void register(BubbleTransfer transfer) {
        storeSession.register(transfer);
    }

    @Override
    public void registerTransfer(UnitOfWorkTransfer transfer) {
        Set<BubbleId<?>> ids = Sets.newHashSet();
        if (transfer.isShared()) {
            transfer = CopyHelper.copy(transfer);
        }

        try {
            beginUnitOfWork();
            for (BubbleObject bubbleObject : transfer.getInsertedObjects()) {
                if (ids.add(bubbleObject.getId())==false) {
                    throw new ImplementationException("Duplicate object in transfer: " + bubbleObject.getId());
                }
                insert(bubbleObject);
            }
            for (BubbleObject bubbleObject : transfer.getUpdatedObjects()) {
                if (ids.add(bubbleObject.getId())==false) {
                    throw new ImplementationException("Duplicate object in transfer: " + bubbleObject.getId());
                }
                update(bubbleObject);
            }
            for (BubbleObject bubbleObject : transfer.getDeletedObjects()) {
                if (ids.add(bubbleObject.getId())==false) {
                    throw new ImplementationException("Duplicate object in transfer: " + bubbleObject.getId());
                }
                delete(bubbleObject);
            }
            commitUnitOfWork();
        } catch (RuntimeException e) {
            abortUnitOfWork();
            throw e;
        }
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> boolean isLocked(@Nullable I bubbleId) {
        if (bubbleId == null) return false;
        return storeSession.isLocked(bubbleId);
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> boolean evict(@Nullable I bubbleId) {
        if (bubbleId == null) return false;
        return storeSession.evict(bubbleId);
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> boolean evict(Collection<I> bubbleIds) {
        // TODO: Hva bør egentlig returneres her?
        boolean allWasEviced = true;
        for (I bubbleId : bubbleIds) {
            allWasEviced &= storeSession.evict(bubbleId);
        }
        return allWasEviced;
    }

    @Override
    public boolean evictAll() {
        return storeSession.evictAll();
    }

    @Override
    public <I extends BubbleId<?>> List<I> getVersions(I id, SnapshotVersion start, SnapshotVersion end) {
        return storeSession.getVersions(id, start, end);
    }

    @Override
    public <I extends BubbleId<?>> Map<I, List<I>> getVersionsForList(List<I> ids, SnapshotVersion start, SnapshotVersion end) {
        return storeSession.getVersionsForList(ids, start, end);
    }

    @Override
    public <T extends BubbleObject> void insert(T bubbleObject) {
        storeSession.insert(bubbleObject);
    }

    @Override
    public <T extends BubbleObject> void update(T bubbleObject) {
        storeSession.update(bubbleObject);
    }

    @Override
    public <T extends BubbleObject> void delete(T bubbleObject) {
        storeSession.delete(bubbleObject);
    }

    @Override
    public <T extends BubbleObject> void undo(T bubbleObject) {
        storeSession.undo(bubbleObject);
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> void reorderModification(I bubbleId) {
        storeSession.reorderModification(bubbleId);
    }


    @Override
    public <T extends BubbleObject> void ensureFullyLoaded(@Nullable T bubbleObject) {
        if (bubbleObject!=null) {
            storeSession.ensureFullyLoaded(bubbleObject);
        }
    }

    @Override
    public void beginUnitOfWork() {
        // TODO: Burde lage en dummy UnitOfWork først som aldrig feiler slik at abortUnitOfWork popper riktig av stakken hvis storeSession.beginUnitOfWork() feiler
        storeSession = storeSession.beginUnitOfWork();
    }

    @Override
    public UnitOfWorkTransfer getUnitOfWorkTransfer() {
        return storeUnitOfWork().getUnitOfWorkTransfer();
    }

    @Override
    public void abortUnitOfWork() {
        // TODO: Bør sikre at denne alltid popper av et nivå av unit of work også selv om det kastes exception.
        storeSession = storeUnitOfWork().abortUnitOfWork();
    }

    @Override
    public void endUnitOfWork() {
        // TODO: Ikke sikker på at denne skal være her
        StoreUnitOfWork storeUnitOfWork = storeUnitOfWork();
        storeSession = storeUnitOfWork.endUnitOfWork();
    }

    @Override
    public boolean inUnitOfWork() {
        return storeSession instanceof StoreUnitOfWork;
    }

    //@Override
    public void commitUnitOfWork() {
        storeSession = storeUnitOfWork().commitUnitOfWork();
    }
}
