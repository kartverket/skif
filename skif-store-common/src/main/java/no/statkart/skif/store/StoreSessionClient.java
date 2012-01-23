package no.statkart.skif.store;

import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.util.CopyHelper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static no.statkart.skif.guava.Preconditions.checkNotNull;

/**
 * StoreSession som utgjør avsluttende ledd på klienten. Klassen anvender en {@link StoreService} for å hente
 * objekter fra server
 *
 * @author Henrik Fredholm
 * @since 2.1
 */
public class StoreSessionClient extends AbstractStoreSession {
    private final StoreService storeService;
    private Store store;


    public StoreSessionClient(StoreService storeService) {
        this(storeService, new StoreCache());
    }

    public StoreSessionClient(StoreService storeService, StoreCache storeCache) {
        super(0, storeCache);
        this.storeService = storeService;
    }


    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> StoreEntry loadEntry(int level, I bubbleId) {
        T bubbleObject = load(bubbleId);
        return storeCache.registerUnchanged(level, bubbleObject);
    }

    private <T extends BubbleObject, I extends BubbleId<? extends T>> T load(I bubbleId) {
        T bubbleObject = storeService.getObject(bubbleId);
        bubbleObject.register(store);
        return bubbleObject;
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> Collection<StoreEntry> getEntries(int level, Collection<I> bubbleIds) {
        // TODO: Optimize for bulk access
        Collection<StoreEntry> result = new ArrayList<StoreEntry>(bubbleIds.size());
        for (I bubbleId : bubbleIds) {
              result.add(getEntry(level, bubbleId));
        }
        return result;
    }


    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> Collection<StoreEntry> loadEntries(int level, Collection<I> bubbleIds) {
        // TODO: Optimize for bulk access
        Collection<StoreEntry> result = new ArrayList<StoreEntry>(bubbleIds.size());
        for (I bubbleId : bubbleIds) {
            result.add(loadEntry(level, bubbleId));
        }
        return result;
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> Collection<T> get(Collection<I> bubbleIds) {
        Collection<T> result = new ArrayList<T>(bubbleIds.size());
        get(bubbleIds, result);
        return result;
    }


    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> void get(Collection<I> bubbleIds, Collection<T> bubbleObjects) {
        checkNotNull(bubbleIds, "bubbleIds");
        List<I> missingBubbleIds = null;

        for (I bubbleId : bubbleIds) {
            final StoreEntry storeEntry = storeCache.get(bubbleId);
            final BubbleObject bubbleObject = storeEntry == null ? null : storeEntry.getBubbleObject(level);
            if (bubbleObject != null) {
                bubbleObjects.add((T) bubbleObject);
            } else {
                if (missingBubbleIds == null) {
                    missingBubbleIds = new ArrayList<I>(bubbleIds.size());
                }
                missingBubbleIds.add(bubbleId);
            }
        }

        if (missingBubbleIds != null) {
            if (missingBubbleIds.size() == 1) {
                StoreEntry entry = loadEntry(level, missingBubbleIds.get(0));
                bubbleObjects.add((T) entry.getBubbleObject(level));
            } else {
                Collection<StoreEntry> entries = loadEntries(level, missingBubbleIds);
                for (StoreEntry entry : entries) {
                    bubbleObjects.add((T) entry.getBubbleObject(level));

                }
            }
        }
    }

    /**
     * For StoreSessionServer har denne meotden samme funksjonalitet som {@link #getEntry(int, BubbleId)}
     */
    @Override
    public <T extends BubbleObject> StoreEntry registerEntry(int level, T bubbleObject) {
        return getEntry(level, bubbleObject.getId());
    }

    /**
     * For StoreSessionServer har denne meotden samme funksjonalitet som {@link #lockEntry(int, BubbleId)}
     */
    @Override
    public <T extends BubbleObject> StoreEntry registerLockedEntry(int level, T bubbleObject) {
        return lockEntry(level, bubbleObject.getId());
    }

    @Override
    public void registerTransfer(int level, UnitOfWorkTransfer transfer) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public <T extends BubbleObject> StoreEntry insertEntry(int level, T bubbleObject) {
        modifiedByThisLevel.add(bubbleObject.getId());

        StoreEntry entry = storeCache.get(bubbleObject.getId());

        if (entry == null) {
            entry = storeCache.registerInserted(level, bubbleObject);
            bubbleObject.register(store);
        } else {
            switch (entry.getDerivedState(level)) {
                case UNCHANGED:
                    throw new ImplementationException("Forsøk på å kalle insert for eksisterende objekt: " + bubbleObject.getId());
                case INSERTED:
                    throw new ImplementationException("Forsøk på å kalle insert for objekt hvor insert allerede har blitt kaldt: " + bubbleObject.getId());
                case UPDATED:
                    throw new ImplementationException("Forsøk på å kalle insert for objekt hvor update allerede har blitt kaldt: " + bubbleObject.getId());
                case DELETED:
                    entry.setState(level, StoreEntryState.UPDATED);
                    break;
                case INSERTED_DELETED:
                    entry.setState(level, StoreEntryState.INSERTED);
                    break;
            }
            T oldInstance = (T) entry.getBubbleObject(level);
            if (oldInstance != bubbleObject) {
                // TODO markere entry.getBubbleObject som stale
                entry.setBubbleObject(level, bubbleObject);
            }
        }
        return entry;
    }

    @Override
    public <T extends BubbleObject> StoreEntry updateEntry(int level, T bubbleObject) {

        boolean isNewInstance;
        StoreEntry entry = storeCache.get(bubbleObject.getId());

        if (entry == null) {
            entry = storeCache.registerNewUpdated(level, bubbleObject);
            modifiedByThisLevel.add(bubbleObject.getId());
            bubbleObject.register(store);
            isNewInstance = true;
        } else {
            switch (entry.getState(level)) {
                case INSERTED:
                    break;
                case UNCHANGED:
                    entry.setStateCheckLocked(level, StoreEntryState.UPDATED);
                    modifiedByThisLevel.add(bubbleObject.getId());
                    break;
                case UPDATED:
                    break;
                case DELETED:
                case INSERTED_DELETED:
                    throw new ImplementationException("Forsøk på å kalle update for objekt hvor delete har blitt kaldt: " + bubbleObject.getId());
            }

            T oldInstance = (T) entry.getBubbleObject(level);
            if (oldInstance != bubbleObject) {
                entry.checkNotDerivedInstance(level, bubbleObject);
                // TODO markere oldInstance som stale
                entry.setBubbleObject(level, bubbleObject);
                bubbleObject.register(store);
                isNewInstance = true;
            } else {
                isNewInstance = false;
            }
        }
        return entry;
    }

    public <T extends BubbleObject> StoreEntry deleteEntry(int level, T bubbleObject) {
        modifiedByThisLevel.add(bubbleObject.getId());

        StoreEntry entry = storeCache.get(bubbleObject.getId());
        if (entry == null) {
            entry = storeCache.registerNewDeleted(level, bubbleObject);
            bubbleObject.register(store);
        } else {
            switch (entry.getState(level)) {
                case INSERTED:
                    entry.setStateCheckLocked(level, StoreEntryState.INSERTED_DELETED);
                    break;
                case UNCHANGED:
                case UPDATED:
                    entry.setStateCheckLocked(level, StoreEntryState.DELETED);
                    break;
                case DELETED:
                case INSERTED_DELETED:
                    throw new ImplementationException("Forsøk på å kalle update for objekt hvor delete har blitt kaldt: " + bubbleObject.getId());
            }
            T oldInstance = (T) entry.getBubbleObject(level);
            if (oldInstance != bubbleObject) {
                // TODO marker oldInstance som stale
                entry.setBubbleObject(level, bubbleObject);
                bubbleObject.register(store);
            }
        }

        return entry;
    }

    public <T extends BubbleObject, I extends BubbleId<? extends T>> boolean evictEntry(int level, I bubbleId) {
        boolean evictedFromCache = storeCache.evict(level, bubbleId);
        return evictedFromCache;
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
        storeCache.clear();
    }

    public void beginTransaction() {
    }

    public void commit() {
        throw new ImplementationException("Cant commit");
    }

    /**
     * Låser objekt og lager en kopi av objektet hvis låsingen skjer i en unit of work. Hvis låsingen skjer direkte
     * på StoreSessionServer lages ingen kopi og objekt som er koblet mot underliggende session brukes.
     *
     * Objektet kan være følgende tilstander:
     * <ul>
     *     <li>Allerede låst for level</li>
     *     <li>Låst for lavere level</li>
     *     <li>Ikke låst</li>
     *     <li>Ikke loaded, men allerede låst</li>
     *     <li>Ikke loaded og ikke låst</li>
     * </ul>
     *
     * Et av målene for implementasjonen er å utnytte tilgjengelig informasjon for å ungå å måtte gjøre kall mot
     * databasen.
     *
     * @param level StoreSession level som ønsker å låse objektet
     * @param bubbleId objekt som skal låses
     * @return låst objekt
     */
    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> StoreEntry lockEntry(int level, I bubbleId) {
        StoreEntry storeEntry = storeCache.get(bubbleId);
        if (storeEntry != null) {
            int lockLevel = storeEntry.calcLockLevelStartingFrom(level);
            if (lockLevel == level) {
                // Allerede låst for level
            } else if (lockLevel == 0) {
                // Låst for level 0, må fullinitialiserer og lage kopi
                T bubbleObject = (T) storeEntry.getBubbleObject(0);
                T copy = CopyHelper.copy(bubbleObject);
                storeEntry.setLocked(level, copy);
            } else if (lockLevel != -1) {
                // Låst på mellomliggende nivå, må lage en kopi
                T bubbleObject = (T) storeEntry.getBubbleObject(lockLevel);
                T copy = CopyHelper.copy(bubbleObject);
                storeEntry.setLocked(level, copy);
            } else {
                // Uvist om låst
                boolean isNew = true; //transactionalLocker.lock(bubbleId);
                if (isNew) {
                    // Objekt var ikke låst fra før, må gjøre en refresh
                    T bubbleObject = (T) storeEntry.getBubbleObject(0);
                    //persistenceSessionManager.refresh(bubbleObject);
                    if (level != 0) {
                        T copy = CopyHelper.copy(bubbleObject);
                        storeEntry.setLocked(level, copy);
                    }
                } else {
                    // Objekt var allrede låst, ingen behov for refresh
                    if (level == 0) {
                        storeEntry.setLocked();
                    } else {
                        T bubbleObject = (T) storeEntry.getBubbleObject(0);
                        T copy = CopyHelper.copy(bubbleObject);
                        storeEntry.setLocked(level, copy);
                    }
                }
            }
        } else {
            // Ingen entry
            boolean isNew = true; // transactionalLocker.lock(bubbleId);
            T bubbleObject;
            if (isNew) {
                // Objekt var ikke låst fra før, må gjøre en refresh
                bubbleObject = null; //persistenceSessionManager.refresh(bubbleId);
            } else {
                // Objekt var allrede låst, ingen behov for refresh
                bubbleObject = null; //persistenceSessionManager.get(bubbleId);
            }
            storeEntry = storeCache.registerUnchanged(0, bubbleObject);
            if (level == 0) {
                storeEntry.setLocked();
            } else {
                T copy = CopyHelper.copy(bubbleObject);
                storeEntry.setLocked(level, copy);
            }
        }
        return storeEntry;
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> List<I> getVersions(I id, SnapshotVersion start, SnapshotVersion end) {
        return storeService.getVersions(id, start,end);
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> Map<I, List<I>> getVersionsForList(List<I> ids, SnapshotVersion start, SnapshotVersion end) {
        return storeService.getVersionsForList(ids, start,end);
    }
}
