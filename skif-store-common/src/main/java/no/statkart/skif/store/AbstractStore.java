package no.statkart.skif.store;

import com.google.common.collect.Sets;
import com.google.inject.Injector;
import com.google.inject.Key;
import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.exception.NotImplementedException;
import no.statkart.skif.store.relation.cache.StoreRelationCache;
import no.statkart.skif.store.relation.cache.StoreRelationCacheImpl;
import no.statkart.skif.util.CopyHelper;

import javax.annotation.Nullable;
import java.util.*;

import static com.google.common.base.Preconditions.checkState;

/**
 * @author Henrik Fredholm
 */
public abstract class AbstractStore implements Store {
    protected WrappableStoreSession storeSession;
    final private Injector injector;
    final protected StoreRelationCacheImpl storeRelationCache = new StoreRelationCacheImpl(this) {
        @Override
        protected WrappableStoreSession getStoreSession() {
            return storeSession;
        }
    };


    public AbstractStore(WrappableStoreSession storeSession, Injector injector) {
        this.injector = injector;
        this.storeSession = storeSession;
    }

    @Override
    public StoreRelationCache getRelationCache() {
        return storeRelationCache;
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
    public <T extends BubbleObject, I extends BubbleId<? extends T>> Collection<T> get(Collection<? extends I> bubbleIds) {
        return storeSession.get(bubbleIds);
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> Set<T> get(Set<? extends I> bubbleIds) {
        return storeSession.get(bubbleIds);
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> List<T> get(List<? extends I> bubbleIds) {
        return storeSession.get(bubbleIds);
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> void get(Collection<? extends I> bubbleIds, Collection<T> bubbleObjects) {
        storeSession.get(bubbleIds, bubbleObjects);
    }


    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> Collection<T> getOrdered(Collection<? extends I> bubbleIds) {
        return storeSession.getOrdered(bubbleIds);
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> Set<T> getOrdered(Set<? extends I> bubbleIds) {
        return storeSession.getOrdered(bubbleIds);
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> List<T> getOrdered(List<? extends I> bubbleIds) {
        return storeSession.getOrdered(bubbleIds);
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> void getOrdered(Collection<? extends I> bubbleIds, Collection<T> bubbleObjects) {
        storeSession.getOrdered(bubbleIds, bubbleObjects);
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> Collection<T> getIgnoreMissing(Collection<? extends I> bubbleIds) {
        return storeSession.getIgnoreMissing(bubbleIds);
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> Set<T> getIgnoreMissing(Set<? extends I> bubbleIds) {
        return storeSession.getIgnoreMissing(bubbleIds);
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> List<T> getIgnoreMissing(List<? extends I> bubbleIds) {
        return storeSession.getIgnoreMissing(bubbleIds);
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> void getIgnoreMissing(Collection<? extends I> bubbleIds, Collection<T> bubbleObjects) {
        storeSession.getIgnoreMissing(bubbleIds, bubbleObjects);
    }

    @Override
    public <T extends BubbleObject> T lock(@Nullable BubbleId<? extends T> bubbleId) {
        if (bubbleId == null) return null;
        return storeSession.lock(bubbleId);
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> Collection<T> lock(Collection<? extends I> bubbleIds) {
        List<T> bubbleObjects = new ArrayList<>(bubbleIds.size());
        lock(bubbleIds, bubbleObjects);
        return bubbleObjects;
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> Set<T> lock(Set<? extends I> bubbleIds) {
        Set<T> bubbleObjects = new HashSet<>(bubbleIds.size());
        lock(bubbleIds, bubbleObjects);
        return bubbleObjects;
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> List<T> lock(List<? extends I> bubbleIds) {
        List<T> bubbleObjects = new ArrayList<>(bubbleIds.size());
        lock(bubbleIds, bubbleObjects);
        return bubbleObjects;
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> void lock(Collection<? extends I> bubbleIds, Collection<T> bubbleObjects) {
        // TODO: implementer som batch
        for (I bubbleId : bubbleIds) {
            bubbleObjects.add(storeSession.lock(bubbleId));
        }
    }

    @Override
    public <I extends BubbleId<?>> void unlock(@Nullable I bubbleId) {
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
        // Opptimalisering: Last opprinnelig tilstand for alle objekter samlet. Lastes utenfor unit of work for kunne dra nytte av lazyloading
        storeSession.get(getIdsOfUpdatedOrDeleted(transfer));

        UnitOfWork unitOfWork = beginUnitOfWork();
        try {
            for (BubbleObject bubbleObject : transfer.getInsertedObjects()) {
                if (ids.add(bubbleObject.getId()) == false) {
                    throw new ImplementationException("Duplicate object in transfer: " + bubbleObject.getId());
                }
                insert(bubbleObject);
            }
            for (BubbleObject bubbleObject : transfer.getUpdatedObjects()) {
                if (ids.add(bubbleObject.getId()) == false) {
                    throw new ImplementationException("Duplicate object in transfer: " + bubbleObject.getId());
                }
                update(bubbleObject);
            }
            for (BubbleObject bubbleObject : transfer.getDeletedObjects()) {
                if (ids.add(bubbleObject.getId()) == false) {
                    throw new ImplementationException("Duplicate object in transfer: " + bubbleObject.getId());
                }
                delete(bubbleObject);
            }
            commitUnitOfWork(unitOfWork);
        } finally {
            closeUnitOfWork(unitOfWork);
        }
    }

    @Override
    public <I extends BubbleId<?>> boolean isLocked(@Nullable I bubbleId) {
        if (bubbleId == null) return false;
        return storeSession.isLocked(bubbleId);
    }

    @Override
    public <I extends BubbleId<?>> boolean evict(@Nullable I bubbleId) {
        if (bubbleId == null) return false;
        return storeSession.evict(bubbleId);
    }

    @Override
    public <I extends BubbleId<?>> boolean evict(Collection<? extends I> bubbleIds) {
        // TODO: Hva bør egentlig returneres her?
        boolean allWasEviced = true;
        for (I bubbleId : bubbleIds) {
            allWasEviced &= storeSession.evict(bubbleId);
        }
        return allWasEviced;
    }

    @Override
    public boolean evictAll() {
        boolean allEvicted = storeSession.evictAll();
        if (storeRelationCache.isEnabled()) {
            storeRelationCache.evictAll();
        }
        return allEvicted;
    }

    @Override
    public <I extends BubbleId<?>> List<I> getVersions(I id, SnapshotVersion start, SnapshotVersion end) {
        return storeSession.getVersions(id, start, end);
    }

    @Override
    public <I extends BubbleId<?>> Map<I, List<I>> getVersionsForList(Collection<? extends I> ids, SnapshotVersion start, SnapshotVersion end) {
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
    public <I extends BubbleId<?>> void reorderModification(I bubbleId) {
        storeSession.reorderModification(bubbleId);
    }


    @Override
    public <T extends BubbleObject> void ensureFullyLoaded(@Nullable T bubbleObject) {
        if (bubbleObject != null) {
            storeSession.ensureFullyLoaded(bubbleObject);
        }
    }

    @Override
    public UnitOfWork beginUnitOfWork() {
        // Oppretter alt først
        StoreUnitOfWork storeUnitOfWork = storeSession.beginUnitOfWork();
        UnitOfWork unitOfWork = new UnitOfWork(storeUnitOfWork);

        // Så det som ikke kan feile
        storeSession = storeUnitOfWork;
        storeRelationCache.onBeginUnitOfWork();
        return unitOfWork;
    }

    @Override
    public UnitOfWorkTransfer getUnitOfWorkTransfer() {
        return storeUnitOfWork().getUnitOfWorkTransfer();
    }

    @Override
    public UnitOfWorkTransfer getSnapshot() {
        return storeSession.getSnapshot();
    }

    @Override
    public UnitOfWorkTransfer getSessionSnapshot() {
        return storeSession.getSessionSnapshot();
    }

    @Override
    public void abortUnitOfWork(UnitOfWork unitOfWork) {
        validateUnitOfWorkCurrent(unitOfWork.getUnitOfWork(), false);
        // Hvis det kastes en exception her, så er løpet kjørt. Da må Store forkastes.
        storeSession = storeUnitOfWork().abortUnitOfWork();
        storeRelationCache.onAbortUnitOfWork();
    }

    @Override
    public void endUnitOfWork(UnitOfWork unitOfWork) {
        validateUnitOfWorkCurrent(unitOfWork.getUnitOfWork(), false);

        // TODO: Ikke sikker på at denne skal være her
        StoreUnitOfWork storeUnitOfWork = storeUnitOfWork();

        if (storeUnitOfWork.getLevel() != 1) {
            throw new ImplementationException("In nested UnitOfWork. Call commitUnitOfWork() or abortUnitOfWork() instead");
        }

        storeSession = storeUnitOfWork.endUnitOfWork();
        storeRelationCache.onCommitUnitOfWork();
    }

    @Override
    public void endUnitsOfWork(UnitOfWork unitOfWork) {
        validateUnitOfWorkCurrent(unitOfWork.getUnitOfWork(), false);

        do {
            StoreUnitOfWork storeUnitOfWork = storeUnitOfWork();
            storeSession = storeUnitOfWork.endUnitOfWork();
            storeRelationCache.onCommitUnitOfWork();
        } while (storeSession instanceof StoreUnitOfWork);
    }

    @Override
    public boolean inUnitOfWork() {
        return storeSession instanceof StoreUnitOfWork;
    }

    @Override
    public void commitUnitOfWork(UnitOfWork unitOfWork) {
        validateUnitOfWorkCurrent(unitOfWork.getUnitOfWork(), false);
        int level = storeSession.getLevel();
        if (level>0 && storeRelationCache.isEnabled(level-1)) {
            // Hvis underliggende session har caching enables på caching enables for inneværende unit of work før commit
            // for at cachingen for underliggende session skal bli riktig.
            storeRelationCache.setEnabled(true);
        }
        storeSession = storeUnitOfWork().commitUnitOfWork();
        storeRelationCache.onCommitUnitOfWork();
    }

    @Override
    public void closeUnitOfWork(UnitOfWork unitOfWork) {
        validateUnitOfWorkCurrent(unitOfWork.getUnitOfWork(), true);
        // Hvis det kastes en exception her, så er løpet kjørt. Da må Store forkastes.
        if (isUnitOfWorkActive(unitOfWork.getUnitOfWork())) {
            while (storeSession != unitOfWork.getUnitOfWork()) {
                storeSession = storeUnitOfWork().abortUnitOfWork();
                storeRelationCache.onAbortUnitOfWork();
            }
            storeSession = storeUnitOfWork().abortUnitOfWork();
            storeRelationCache.onAbortUnitOfWork();
        }
    }

    /**
     * Sjekker at unit-of-work er gjeldende unit-of-work.
     *
     * @param storeUnitOfWork unit-of-work sesjon
     * @param ignoreInactive  om det er greit at unit-of-work ikke lenger er aktiv (for abort)
     */
    protected void validateUnitOfWorkCurrent(StoreUnitOfWork storeUnitOfWork, boolean ignoreInactive) {
        if (storeUnitOfWork.store != this) {
            throw new ImplementationException("UnitOfWork does not belong to this Store");
        }
        if (storeSession != storeUnitOfWork) {
            // Dersom det er ok med inaktive unit-of-works, så må det sjekkes om den er aktiv
            if (!ignoreInactive || isUnitOfWorkActive(storeUnitOfWork)) {
                throw new ImplementationException("UnitOfWork is not current");
            }
        }
    }

    /**
     * Sjekker om gitt unit-of-work er aktiv, men ikke nødvendigvis gjeldende.
     *
     * @return <code>true</code> dersom unit-of-work ligger i kjeden
     */
    protected boolean isUnitOfWorkActive(StoreUnitOfWork storeUnitOfWork) {
        StoreSession session = storeSession;
        while (session instanceof StoreUnitOfWork) {
            if (session == storeUnitOfWork) {
                return true;
            }
            session = ((StoreUnitOfWork) session).wrappedStoreSession;
        }
        return false;
    }

    private Set<? extends BubbleId<?>> getIdsOfUpdatedOrDeleted(UnitOfWorkTransfer transfer) {
        Set<BubbleId<?>> result = Sets.newHashSetWithExpectedSize(transfer.getUpdatedObjects().size() + transfer.getDeletedObjects().size());
        for (BubbleObject bubbleObject : transfer.getUpdatedObjects()) {
            result.add(bubbleObject.getId());
        }
        for (BubbleObject bubbleObject : transfer.getDeletedObjects()) {
            result.add(bubbleObject.getId());
        }
        return result;
    }

    abstract protected boolean isServerStore();

}
