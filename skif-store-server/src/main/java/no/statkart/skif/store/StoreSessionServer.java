package no.statkart.skif.store;

import com.beust.jcommander.internal.Lists;
import com.google.inject.Provider;
import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.exception.NotLockedException;
import no.statkart.skif.persistence.VersionFinder;
import no.statkart.skif.store.persistence.PersistenceSessionManager;
import no.statkart.skif.util.CopyHelper;

import javax.annotation.Nullable;
import java.util.*;

/**
 * @author Henrik Fredholm
 */
public class StoreSessionServer extends AbstractStoreSession {
    private final PersistenceSessionManager persistenceSessionManager;
    private final List<StoreSessionReadListener> readListeners = new ArrayList<StoreSessionReadListener>();
    private final List<StoreSessionWriteListener> writeListeners = new ArrayList<StoreSessionWriteListener>();
    private final List<StoreSessionFinishListener> finishListeners = new ArrayList<StoreSessionFinishListener>();
    private Provider<VersionFinder> versionFinderProvider;
    private final BubbleDependencyComparator bubbleDependencyComparator;

    private ModifiedCache modifiedCache;

    private static class ModifiedCache {
        private LinkedHashSet<BubbleId<?>> insertedIds;
        private LinkedHashSet<BubbleId<?>> updatedIds;
        private LinkedHashSet<BubbleId<?>> deletedIds;
        private LinkedHashSet<BubbleId<?>> lockedIds;
        private LinkedHashMap<BubbleId<?>, StoreEntry> modifiedMap;

        public ModifiedCache(LinkedHashMap<BubbleId<?>, StoreEntry> modifiedMap) {
            this.modifiedMap = modifiedMap;
        }

        public void clearCache() {
            insertedIds = null;
            updatedIds = null;
            deletedIds = null;
            lockedIds = null;
        }

        private void calc() {
            insertedIds = new LinkedHashSet<BubbleId<?>>();
            updatedIds = new LinkedHashSet<BubbleId<?>>();
            deletedIds = new LinkedHashSet<BubbleId<?>>();
            lockedIds = new LinkedHashSet<BubbleId<?>>();
            for (StoreEntry storeEntry : modifiedMap.values()) {
                switch (storeEntry.getState(0)) {
                    case UNCHANGED:
                        if (storeEntry.isLocked()) {
                            lockedIds.add(storeEntry.getId());
                        }
                        break;
                    case INSERTED:
                        insertedIds.add(storeEntry.getId());
                        break;
                    case UPDATED:
                        updatedIds.add(storeEntry.getId());
                        break;
                    case DELETED:
                        deletedIds.add(storeEntry.getId());
                        break;
                }
            }
        }

        public LinkedHashSet<BubbleId<?>> getDeletedIds() {
            if (deletedIds == null) calc();
            return deletedIds;
        }

        public LinkedHashSet<BubbleId<?>> getInsertedIds() {
            if (insertedIds == null) calc();
            return insertedIds;
        }

        public LinkedHashSet<BubbleId<?>> getLockedIds() {
            if (lockedIds == null) calc();
            return lockedIds;
        }

        public LinkedHashSet<BubbleId<?>> getUpdatedIds() {
            if (lockedIds == null) calc();
            return updatedIds;
        }
    }

    private static class StoreMapEntryComparator implements Comparator<Map.Entry<BubbleId<?>, StoreEntry>> {
        private final BubbleDependencyComparator bubbleDependencyComparator;
        private final int level;

        public StoreMapEntryComparator(BubbleDependencyComparator bubbleDependencyComparator, int level) {
            this.bubbleDependencyComparator = bubbleDependencyComparator;
            this.level = level;
        }

        @Override
        public int compare(Map.Entry<BubbleId<?>, StoreEntry> o1, Map.Entry<BubbleId<?>, StoreEntry> o2) {
            return bubbleDependencyComparator.compare(o1.getValue().getBubbleObject(level), o2.getValue().getBubbleObject(level));
        }
    }

    /**
     * Låser tatt for inneværende service
     */
    private LockerStrategy lockerStrategy;


    public StoreSessionServer(PersistenceSessionManager persistenceSessionManager, Provider<VersionFinder> versionFinderProvider, LockerStrategy lockerStrategy, BubbleDependencyComparator bubbleDependencyComparator) {
        this(persistenceSessionManager, new StoreCache(), versionFinderProvider, lockerStrategy, bubbleDependencyComparator, null, null, null);
    }

    public StoreSessionServer(PersistenceSessionManager persistenceSessionManager, Provider<VersionFinder> versionFinderProvider, LockerStrategy lockerStrategy, BubbleDependencyComparator bubbleDependencyComparator, @Nullable List<StoreSessionReadListener> readListeners, @Nullable List<StoreSessionWriteListener> writeListeners, @Nullable List<StoreSessionFinishListener> finishListeners) {
        this(persistenceSessionManager, new StoreCache(), versionFinderProvider, lockerStrategy, bubbleDependencyComparator, readListeners, writeListeners, finishListeners);
    }

    public StoreSessionServer(PersistenceSessionManager persistenceSessionManager, StoreCache storeCache, Provider<VersionFinder> versionFinderProvider, LockerStrategy lockerStrategy, BubbleDependencyComparator bubbleDependencyComparator, @Nullable List<StoreSessionReadListener> readListeners, @Nullable List<StoreSessionWriteListener> writeListeners, @Nullable List<StoreSessionFinishListener> finishListeners) {
        super(0, storeCache);
        this.persistenceSessionManager = persistenceSessionManager;
        this.lockerStrategy = lockerStrategy;
        this.versionFinderProvider = versionFinderProvider;
        this.bubbleDependencyComparator = bubbleDependencyComparator;
        if (readListeners != null) {
            this.readListeners.addAll(readListeners);
        }
        if (writeListeners != null) {
            this.writeListeners.addAll(writeListeners);
        }
        if (finishListeners != null) {
            this.finishListeners.addAll(finishListeners);
        }
        modifiedCache = new ModifiedCache(modifiedMap);
    }

    protected void markModified() {
        modifiedCache.clearCache();
    }


    protected void ensureLocked(StoreEntry storeEntry) {
        if (!isLocked(storeEntry)) {
            throw new NotLockedException("Object not locked: " + storeEntry.getId());
        }
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> void reorderModification(I bubbleId) {
        throw new ImplementationException("Operasjon kun støttet i UnitOfWork. UnitOfWork er ikke aktiv. BubbleId: " + bubbleId);
    }

    public <T extends BubbleObject, I extends BubbleId<? extends T>> boolean evictEntry(int level, I bubbleId) {
        boolean evicted;
        StoreEntry storeEntry = storeCache.get(bubbleId);
        if (storeEntry == null) {
            persistenceSessionManager.evict(bubbleId);
            evicted = true;
        } else if (storeEntry.isModified()) {
            evicted = false;
        } else {
            if (storeEntry.getLockCreatedByLevel() > 0) {
                // Kan ikke entry for UnitOfWork må kunne gjøre en unlock ved abort
                evicted = false;
            } else {
                StoreEntry evictedEntry = storeCache.remove(bubbleId);
                // TODO: marker evictedEntry som stale
                evicted = evictedEntry != null;
                persistenceSessionManager.evict(bubbleId);
            }
        }
        return evicted;
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> boolean evictAllEntries(int level) {
        boolean allWasEvicted = true;
        final Iterator<StoreEntry> iterator = storeCache.values().iterator();
        while (iterator.hasNext()) {
            final StoreEntry storeEntry = iterator.next();
            if (storeEntry.isModified()) {
                allWasEvicted = false;
            } else {
                if (storeEntry.getLockCreatedByLevel() > 0) {
                    // Kan ikke entry for UnitOfWork må kunne gjøre en unlock ved abort
                    allWasEvicted = false;
                } else {
                    iterator.remove();
                    // TODO: marker evictedEntry som stale
                    persistenceSessionManager.evict(storeEntry.getId());
                }
            }
        }
        return allWasEvicted;  //To change body of implemented methods use File | Settings | File Templates.
    }


    public void finishBatch() {
        // TODO: Sende finishEvent til WriteListeners
    }

    public void finish() {
        //Flusher først for å sikre at sql kjørt i finishListeners kjøres mot riktige data
        flush();

        for (StoreSessionFinishListener finishListener : finishListeners) {
            finishListener.onFinish((StoreServer) store);
        }
        clear();
    }

    private void clear() {
        storeCache.clear();
        modifiedMap.clear();
        markModified();
    }

    /**
     * TODO: Blir kun kalt av tester. persistenceSessionManager.beginTransaction() blir kalt av andre ting til vanlig.
     */
    public void beginTransaction() {
        persistenceSessionManager.beginTransaction();
    }

    /**
     * TODO: Blir kun kalt av tester. persistenceSessionManager.commit() blir kalt av andre ting til vanlig.
     */
    public void commitTransaction() {
        finish();
        persistenceSessionManager.commit();
        lockerStrategy.consumeAllLocks();
    }

    /**
     * TODO: Blir kun kalt av tester. persistenceSessionManager.rollback() blir kalt av andre ting til vanlig.
     */
    public void rollbackTransaction() {
        persistenceSessionManager.rollback();
        lockerStrategy.releaseLocksOnRollback();
        clear();
    }

    @Override
    public void commitUnitOfWork(Map<BubbleId<?>, StoreEntry> modified) {
        // TODO: Opptimaliser

        // Sorter bobler i henhold til definert bubble dependency ordering
        List<Map.Entry<BubbleId<?>, StoreEntry>> inserted = Lists.newArrayList();
        List<Map.Entry<BubbleId<?>, StoreEntry>> updated = Lists.newArrayList();
        List<Map.Entry<BubbleId<?>, StoreEntry>> deleted = Lists.newArrayList();

        // Legg inn i ovenstående lister;
        for (Map.Entry<BubbleId<?>, StoreEntry> entry : modified.entrySet()) {
            switch (entry.getValue().getState(level + 1)) {
                case INSERTED:
                    inserted.add(entry);
                    break;
                case UPDATED:
                    updated.add(entry);
                    break;
                case DELETED:
                    deleted.add(entry);
                    break;
                case INSERTED_DELETED:
                    deleted.add(entry);
                    break;
                case DELETED_INSERTED:
                    inserted.add(entry);
                    break;
            }
        }
        final StoreMapEntryComparator c = new StoreMapEntryComparator(bubbleDependencyComparator, level + 1);
        Collections.sort(inserted, c);
        Collections.sort(updated, c);
        Collections.reverse(deleted);
        Map<BubbleId<?>, StoreEntry> modifiedSorted = new LinkedHashMap<BubbleId<?>, StoreEntry>(modified.size());
        for (Map.Entry<BubbleId<?>, StoreEntry> mapEntry : inserted) {
            modifiedSorted.put(mapEntry.getKey(), mapEntry.getValue());
        }
        for (Map.Entry<BubbleId<?>, StoreEntry> mapEntry : updated) {
            modifiedSorted.put(mapEntry.getKey(), mapEntry.getValue());
        }
        for (Map.Entry<BubbleId<?>, StoreEntry> mapEntry : deleted) {
            modifiedSorted.put(mapEntry.getKey(), mapEntry.getValue());
        }
        super.commitUnitOfWork(modifiedSorted);

    }

    /**
     * Låser objekt og lager en kopi av objektet hvis låsingen skjer i en unit of work. Hvis låsingen skjer direkte
     * på StoreSessionServer lages ingen kopi og objekt som er koblet mot underliggende session brukes.
     * <p/>
     * Objektet kan være følgende tilstander:
     * <ul>
     * <li>Allerede låst for level</li>
     * <li>Låst for lavere level</li>
     * <li>Ikke låst</li>
     * <li>Ikke loaded, men allerede låst</li>
     * <li>Ikke loaded og ikke låst</li>
     * </ul>
     * <p/>
     * Et av målene for implementasjonen er å utnytte tilgjengelig informasjon for å ungå å måtte gjøre kall mot
     * databasen.
     *
     * @param level    StoreSession level som ønsker å låse objektet
     * @param bubbleId objekt som skal låses
     * @return låst objekt
     */
    public <T extends BubbleObject, I extends BubbleId<? extends T>> StoreEntry lockEntry(int level, I bubbleId) {
        // TODO check sluttdato
        StoreEntry storeEntry = storeCache.get(bubbleId);
        if (storeEntry != null) {
            // Entry finnes, må sjekk om objekt er låst på underliggende nivå
            int lockLevel = storeEntry.calcLockLevelStartingFrom(level);
            if (lockLevel == level) {
                // Allerede låst for level
            } else if (lockLevel >= 0) {
                // Låst for underliggende level
                lockEntry(storeEntry, level, false);
            } else {
                // Uvist om låst
                boolean isNewLock = lockerStrategy.lock(bubbleId);
                if (isNewLock) {
                    // Objekt var ikke låst fra før, må gjøre en refresh
                    refreshEntry(storeEntry);
                }
                lockEntry(storeEntry, level, isNewLock);
            }
        } else {
            // Ingen entry, opprett entry, refresh objekt hvis det ikke allerede er låst
            boolean isNewLock = lockerStrategy.lock(bubbleId);
            storeEntry = loadEntry(level, bubbleId, isNewLock);
            lockEntry(storeEntry, level, true);
        }
        return storeEntry;
    }

    private void lockEntry(StoreEntry storeEntry, int level, boolean isNewLock) {
        if (level == 0) {
            storeEntry.setLocked(0);
        } else {
            BubbleObject derivedBubbleObject = storeEntry.getDerivedBubbleObject(level - 1);
            if (derivedBubbleObject == storeEntry.getDerivedBubbleObject(0) && !storeEntry.hasSeparatePersistentBubbleObject()) {
                persistenceSessionManager.ensureFullyLoaded(derivedBubbleObject);
            }
            BubbleObject copy = CopyHelper.copy(derivedBubbleObject);
            storeEntry.setLocked(level, copy);
        }
        if (isNewLock) {
            storeEntry.setLockedCreatedByLevel(level);
        }
    }

    private <T extends BubbleObject, I extends BubbleId<? extends T>> void refreshEntry(StoreEntry storeEntry) {
        T persistentBubbleObject = (T) storeEntry.getPersistentBubbleObject();
        persistenceSessionManager.refresh(persistentBubbleObject);
        T bubbleObject = persistentBubbleObject;
        for (StoreSessionReadListener readListener : readListeners) {
            bubbleObject = readListener.onRegister(bubbleObject);
        }
        storeEntry.setPersistentBubbleObject(bubbleObject, persistentBubbleObject);
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> StoreEntry unlockEntry(int level, I bubbleId) {
        StoreEntry storeEntry = storeCache.get(bubbleId);
        if (storeEntry != null) {
            switch (storeEntry.getDerivedState(level)) {
                case NULL:
                case UNCHANGED:
                    if (isLocked(storeEntry)) {
                        if (storeEntry.getLockCreatedByLevel() == level) {
                            lockerStrategy.unlock(bubbleId);
                            storeEntry.setLockCreatedByLevel(-1);
                        }
                        storeEntry.setBubbleObject(level, null);
                        storeEntry.unlock(level);
                    }
                    break;
                default:
                    throw new ImplementationException("Objekt har blitt endret og kan ikke låses opp");
            }
        }
        return storeEntry;
    }


    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> List<I> getVersions(I id, SnapshotVersion start, SnapshotVersion end) {
        return versionFinderProvider.get().findBubbleIdsForInterval(id, start, end);
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> Map<I, List<I>> getVersionsForList(List<I> ids, SnapshotVersion start, SnapshotVersion end) {
        VersionFinder versionFinder = versionFinderProvider.get();
        Map<I, List<I>> retur = new HashMap<I, List<I>>();
        for (I id : ids) {
            retur.put(id, versionFinder.findBubbleIdsForInterval(id, start, end));
        }
        return retur;
    }

    @Override
    public <T extends BubbleObject> void ensureFullyLoaded(T bubbleObject) {
        StoreEntry storeEntry = storeCache.get(bubbleObject.getId());
        BubbleObject persistentBubbleObject = storeEntry.getPersistentBubbleObject();
        persistenceSessionManager.ensureFullyLoaded(persistentBubbleObject);
    }

    protected boolean isLocked(StoreEntry storeEntry) {
        return storeEntry.isLocked() || lockerStrategy.isLockedByCaller(storeEntry.getId());
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> boolean isLocked(I bubbleId) {
        boolean isLocked;
        StoreEntry storeEntry = storeCache.get(bubbleId);
        if (storeEntry == null) {
            isLocked = lockerStrategy.isLockedByCaller(bubbleId);
        } else {
            isLocked = storeEntry.isLocked();
        }
        return isLocked;
    }


    protected <T extends BubbleObject> void onInsertEntry(int level, StoreEntry storeEntry, T bubbleObject) {
        if (level == 0) {
            onInsertObject(storeEntry, bubbleObject);
        } else {
            storeEntry.setBubbleObject(level, bubbleObject);
        }
    }

    private <T extends BubbleObject> void onInsertObject(StoreEntry storeEntry, T bubbleObject) {
        T resultingPersistentBubbleObject = (T) bubbleObject;
        T persistentBubbleObject = (T) storeEntry.getPersistentBubbleObject();
        for (StoreSessionWriteListener writeListener : writeListeners) {
            resultingPersistentBubbleObject = writeListener.onInsert(bubbleObject, persistentBubbleObject);
            persistentBubbleObject = resultingPersistentBubbleObject;
        }
        storeEntry.setPersistentBubbleObject(bubbleObject, resultingPersistentBubbleObject);
        lockerStrategy.registerInserted(resultingPersistentBubbleObject.getId());
        persistenceSessionManager.insert(resultingPersistentBubbleObject);
    }


    @Override
    protected <T extends BubbleObject> void onUpdateEntry(int level, StoreEntry storeEntry, T bubbleObject) {
        if (level == 0) {
            onUpdateObject(storeEntry, bubbleObject);
        } else {
            storeEntry.setBubbleObject(level, bubbleObject);
        }
    }

    private <T extends BubbleObject> void onUpdateObject(StoreEntry storeEntry, T bubbleObject) {
        T resultingPersistentBubbleObject = (T) bubbleObject;
        T persistentBubbleObject = (T) storeEntry.getPersistentBubbleObject();
        for (StoreSessionWriteListener writeListener : writeListeners) {
            resultingPersistentBubbleObject = writeListener.onUpdate(bubbleObject, persistentBubbleObject);
            persistentBubbleObject = resultingPersistentBubbleObject;
        }
        storeEntry.setPersistentBubbleObject(bubbleObject, resultingPersistentBubbleObject);
        lockerStrategy.registerUpdated(resultingPersistentBubbleObject.getId());
        persistenceSessionManager.update(resultingPersistentBubbleObject);
    }

    @Override
    protected <T extends BubbleObject> void onDeleteEntry(int level, StoreEntry storeEntry, T bubbleObject) {
        if (level == 0) {
            onDeleteObject(storeEntry, bubbleObject);
        } else {
            storeEntry.setBubbleObject(level, bubbleObject);
        }
    }

    private <T extends BubbleObject> void onDeleteObject(StoreEntry storeEntry, T bubbleObject) {
        T resultingPersistentBubbleObject = (T) bubbleObject;
        T persistentBubbleObject = (T) storeEntry.getPersistentBubbleObject();
        for (StoreSessionWriteListener writeListener : writeListeners) {
            resultingPersistentBubbleObject = writeListener.onDelete(bubbleObject, persistentBubbleObject);
            persistentBubbleObject = resultingPersistentBubbleObject;
        }
        storeEntry.setPersistentBubbleObject(bubbleObject, resultingPersistentBubbleObject);
        lockerStrategy.registerRemoved(resultingPersistentBubbleObject.getId());
        persistenceSessionManager.delete(resultingPersistentBubbleObject);
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> StoreEntry loadEntry(int level, I bubbleId, boolean refresh) {
        T persistentBubbleObject;
        if (refresh) {
            persistentBubbleObject = persistenceSessionManager.refresh(bubbleId);
        } else {
            persistentBubbleObject = persistenceSessionManager.get(bubbleId);
        }
        return createEntry(level, persistentBubbleObject);
    }

    private <T extends BubbleObject> StoreEntry createEntry(int level, T persistentBubbleObject) {
        T bubbleObject = persistentBubbleObject;
        for (StoreSessionReadListener readListener : readListeners) {
            bubbleObject = readListener.onRegister(bubbleObject);
        }
        return storeCache.register(level, persistentBubbleObject, bubbleObject);
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> Collection<StoreEntry> loadEntries(int level, Set<I> bubbleIds, boolean refresh) {
        Collection<T> persistentBubbleObjects;
        if (refresh) {
            // TODO: bulk optimize
            persistentBubbleObjects = new ArrayList<T>(bubbleIds.size());
            for (I bubbleId : bubbleIds) {
                persistentBubbleObjects.add(persistenceSessionManager.refresh(bubbleId));
            }
        } else {
            persistentBubbleObjects = (Collection<T>) persistenceSessionManager.get(bubbleIds);
        }

        Collection<StoreEntry> entries = new ArrayList<StoreEntry>(bubbleIds.size());
        for (T originalBubbleObject : persistentBubbleObjects) {
            entries.add(createEntry(level, originalBubbleObject));
        }
        return entries;
    }


    public void registerWriteListener(StoreSessionWriteListener listener) {
        if (!writeListeners.contains(listener)) {
            writeListeners.add(listener);
        }
    }

    public boolean removeWriteListener(StoreSessionWriteListener listener) {
        return writeListeners.remove(listener);
    }

    public void registerReadListener(StoreSessionReadListener listener) {
        if (!readListeners.contains(listener)) {
            readListeners.add(listener);
        }
    }

    public boolean removeReadListener(StoreSessionReadListener listener) {
        return readListeners.remove(listener);
    }

    public void flush() {
        persistenceSessionManager.flush();
    }

    public LinkedHashSet<BubbleId<?>> getDeletedIds() {
        return modifiedCache.getDeletedIds();

    }

    public LinkedHashSet<BubbleId<?>> getInsertedIds() {
        return modifiedCache.getInsertedIds();

    }

    public LinkedHashSet<BubbleId<?>> getLockedIds() {
        return modifiedCache.getLockedIds();

    }

    public LinkedHashSet<BubbleId<?>> getUpdatedIds() {
        return modifiedCache.getUpdatedIds();

    }

    @Override
    public void registerEntries(int level, BubbleTransfer bubbleTransfer) {
        // No-op; alle objekter hentes fra persistence session
    }
}
