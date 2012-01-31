package no.statkart.skif.store;

import com.google.inject.Provider;
import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.persistence.VersionFinder;
import no.statkart.skif.store.persistence.PersistenceSessionManager;
import no.statkart.skif.util.CopyHelper;

import javax.annotation.Nullable;
import java.util.*;

import static no.statkart.skif.guava.Preconditions.checkNotNull;

/**
 * @author Henrik Fredholm
 */
public class StoreSessionServer extends AbstractStoreSession {
    private final PersistenceSessionManager persistenceSessionManager;
    private final List<StoreSessionReadListener> readListeners = new ArrayList<StoreSessionReadListener>();
    private final List<StoreSessionWriteListener> writeListeners = new ArrayList<StoreSessionWriteListener>();
    private Provider<VersionFinder> versionFinderProvider;

    private long lockTimeout = 240 * 60 * 1000 /* 4 timer */;
    /**
     * Låser tatt for inneværende service
     */
    private TransactionalLocker transactionalLocker;


    public StoreSessionServer(PersistenceSessionManager persistenceSessionManager, Provider<VersionFinder> versionFinderProvider, LockerService5 lockerService) {
        this(persistenceSessionManager, new StoreCache(), versionFinderProvider, lockerService, null, null);
    }
   public StoreSessionServer(PersistenceSessionManager persistenceSessionManager, Provider<VersionFinder> versionFinderProvider, LockerService5 lockerService,@Nullable List <StoreSessionReadListener> readListeners, @Nullable List <StoreSessionWriteListener> writeListeners) {
        this(persistenceSessionManager, new StoreCache(), versionFinderProvider, lockerService,readListeners,writeListeners);
    }

    public StoreSessionServer(PersistenceSessionManager persistenceSessionManager, StoreCache storeCache, Provider<VersionFinder> versionFinderProvider, LockerService5 lockerService, @Nullable List <StoreSessionReadListener> readListeners, @Nullable List <StoreSessionWriteListener> writeListeners) {
        super(0, storeCache);
        this.persistenceSessionManager = persistenceSessionManager;
        this.transactionalLocker = new ReleaseAllLocksOnUpdateTransactionalLocker5(lockerService, "principal", lockTimeout);
        this.versionFinderProvider = versionFinderProvider;
        if(readListeners != null){
            this.readListeners.addAll(readListeners);
        }
        if(writeListeners != null){
            this.writeListeners.addAll(writeListeners);
        }
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

    public void setStore(Store storeServer) {
        this.store = storeServer;
        this.storeCache.setStore(storeServer);
    }


    public void finishBatch() {
        // TODO: Sende finishEvent til WriteListeners
    }

    public void finish() {
        // TODO: Sende finishEvent til WriteListeners
        clear();
    }

    private void clear() {
        storeCache.clear();
        modifiedMap.clear();
    }

    public void beginTransaction() {
        persistenceSessionManager.beginTransaction();

        // TODO vurdere om dette er et midlertidig fix eller det skal være slik
        transactionalLocker.setUpdateService(true);
    }

    public void commitTransaction() {
        finish();
        persistenceSessionManager.commit();
        transactionalLocker.setUpdateService(true);
        transactionalLocker.serviceCompleted();
    }

    public void rollbackTransaction() {
        transactionalLocker.setRollbackOnly();
        persistenceSessionManager.rollback();
        transactionalLocker.serviceCompleted();
        clear();
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
        // TODO check tEnd
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
                boolean isNewLock = transactionalLocker.lock(bubbleId);
                if (isNewLock) {
                    // Objekt var ikke låst fra før, må gjøre en refresh
                    refreshEntry(storeEntry);
                }
                lockEntry(storeEntry, level, isNewLock);
            }
        } else {
            // Ingen entry, opprett entry, refresh objekt hvis det ikke allerede er låst
            boolean isNewLock = transactionalLocker.lock(bubbleId);
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
                            transactionalLocker.unlock(bubbleId);
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
        return storeEntry.isLocked() || transactionalLocker.isLockedByCaller(storeEntry.getId());
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> boolean isLocked(I bubbleId) {
        boolean isLocked;
        StoreEntry storeEntry = storeCache.get(bubbleId);
        if (storeEntry == null) {
            isLocked = transactionalLocker.isLockedByCaller(bubbleId);
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
        transactionalLocker.registerInserted(resultingPersistentBubbleObject.getId());
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
        transactionalLocker.registerUpdated(resultingPersistentBubbleObject.getId());
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
        transactionalLocker.registerRemoved(resultingPersistentBubbleObject.getId());
        persistenceSessionManager.delete(resultingPersistentBubbleObject);
    }

    private <T extends BubbleObject> StoreEntry XXEntry(int level, T persistentBubbleObject) {
        T bubbleObject = persistentBubbleObject;
        for (StoreSessionReadListener readListener : readListeners) {
            bubbleObject = readListener.onRegister(bubbleObject);
        }
        return storeCache.register(level, persistentBubbleObject, bubbleObject);
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



    public void registerWriteListener(StoreSessionWriteListener listener){
        if(!writeListeners.contains(listener)){
            writeListeners.add(listener);
        }
    }

    public boolean removeWriteListener(StoreSessionWriteListener listener){
        return writeListeners.remove(listener);
    }

    public void registerReadListener(StoreSessionReadListener listener){
        if(!readListeners.contains(listener)){
            readListeners.add(listener);
        }
    }

    public boolean removeReadListener(StoreSessionReadListener listener){
        return readListeners.remove(listener);
    }

    public void flush() {
        persistenceSessionManager.flush();
    }
}
