package no.statkart.skif.store;

import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.exception.NotImplementedException;
import no.statkart.skif.exception.NotLockedException;
import no.statkart.skif.exception.ObjectNotFoundException;
import no.statkart.skif.service.sequence.IdService;

import java.util.*;

import static no.statkart.skif.guava.Preconditions.checkNotNull;

/**
 * @author Henrik Fredholm
 */
public abstract class AbstractStoreSession implements WrappableStoreSession {
    protected final int level;
    protected final StoreCache storeCache;
    protected final LinkedHashMap<BubbleId<?>, StoreEntry> modifiedMap;
    protected Store store;
    protected IdService idService;

    protected AbstractStoreSession(int level, StoreCache storeCache) {
        this.level = level;
        this.storeCache = storeCache;
        this.modifiedMap = new LinkedHashMap<BubbleId<?>, StoreEntry>(1000);
    }

    public void setStore(Store store) {
        this.store = store;
        this.storeCache.setStore(store);
        this.idService = store.getInstance(IdService.class);
    }

    protected void markModified() {
        // Nothing do do by default
    }

    protected boolean isLocked(StoreEntry storeEntry) {
        throw new NotImplementedException();
    }

    protected void ensureLocked(StoreEntry storeEntry) {
        if (!isLocked(storeEntry)) {
            throw new NotLockedException("Object not locked: " + storeEntry.getId());
        }
    }

    @Override
    public final <T extends BubbleObject, I extends BubbleId<? extends T>> T get(I bubbleId) {

        StoreEntry entry = storeCache.get(bubbleId);
        if (entry == null) {
            entry = loadEntry(level, bubbleId, false);
        }

        final T bubble = (T) entry.getDerivedBubbleObjectCopyIfLocked(level, store);
        if (bubble==null) {
            throw new ObjectNotFoundException(bubbleId);
        }
        return bubble;
    }

    private void addModified(StoreEntry storeEntry) {
        modifiedMap.put(storeEntry.getId(), storeEntry);
        markModified();
    }

    private void removeModified(StoreEntry storeEntry) {
        if (storeEntry != null) {
            modifiedMap.remove(storeEntry.getId());
            markModified();
        }
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> Collection<T> get(Collection<I> bubbleIds) {
        Collection<T> bubbleObjects;
        if (bubbleIds instanceof Set) {
            bubbleObjects = get((Set<I>) bubbleIds);
        } else if (bubbleIds instanceof List) {
            bubbleObjects = get((List<I>) bubbleIds);
        } else {
            checkNotNull(bubbleIds, "bubbleIds");
            bubbleObjects = get(new ArrayList<I>(bubbleIds));
        }
        return bubbleObjects;
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> Set<T> get(Set<I> bubbleIds) {
        Set<T> result = new HashSet<T>();
        get(bubbleIds, result);
        return result;
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> List<T> get(List<I> bubbleIds) {
        List<T> result = new ArrayList<T>();
        get(bubbleIds, result);
        return result;
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> void get(Collection<I> bubbleIds, Collection<T> bubbleObjects) {
        checkNotNull(bubbleIds, "bubbleIds");
        Set<I> missingBubbleIds = null;

        for (I bubbleId : bubbleIds) {
            final StoreEntry storeEntry = storeCache.get(bubbleId);
            final BubbleObject bubbleObject = storeEntry == null ? null : storeEntry.getBubbleObject(level);
            if (bubbleObject != null) {
                bubbleObjects.add((T) bubbleObject);
            } else {
                if (missingBubbleIds == null) {
                    missingBubbleIds = new HashSet<I>(bubbleIds.size());
                }
                missingBubbleIds.add(bubbleId);
            }
        }

        if (missingBubbleIds != null) {
            if (missingBubbleIds.size() == 1) {
                StoreEntry entry = loadEntry(level, missingBubbleIds.iterator().next(), false);
                bubbleObjects.add((T) entry.getBubbleObject(level));
            } else {
                Collection<StoreEntry> entries = loadEntries(level, missingBubbleIds, false);
                for (StoreEntry entry : entries) {
                    bubbleObjects.add((T) entry.getBubbleObject(level));

                }
            }
        }
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> Collection<T> getOrdered(Collection<I> bubbleIds) {
        Collection<T> bubbleObjects;
        if (bubbleIds instanceof Set) {
            bubbleObjects = getOrdered((Set<I>) bubbleIds);
        } else if (bubbleIds instanceof List) {
            bubbleObjects = getOrdered((List<I>) bubbleIds);
        } else {
            checkNotNull(bubbleIds, "bubbleIds");
            bubbleObjects = getOrdered(new ArrayList<I>(bubbleIds));
        }
        return bubbleObjects;
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> Set<T> getOrdered(Set<I> bubbleIds) {
        Set<T> result = new LinkedHashSet<T>();
        getOrdered(bubbleIds, result);
        return result;
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> List<T> getOrdered(List<I> bubbleIds) {
        List<T> result = new ArrayList<T>();
        getOrdered(bubbleIds, result);
        return result;
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> void getOrdered(Collection<I> bubbleIds, Collection<T> bubbleObjects) {
        checkNotNull(bubbleIds, "bubbleIds");

        ArrayList<T> bubbleObjectsFound = new ArrayList<T>(bubbleIds.size());
        ArrayList<I> orderedBubbleIds = new ArrayList<I>(bubbleIds.size());
        Set<I> missingBubbleIds = null;

        for (I bubbleId : bubbleIds) {
            orderedBubbleIds.add(bubbleId);
            final StoreEntry storeEntry = storeCache.get(bubbleId);
            if (storeEntry != null && storeEntry.getBubbleObject(level) != null) {
                bubbleObjectsFound.add((T) storeEntry.getBubbleObject(level));
            } else {
                bubbleObjectsFound.add(null);  // null er plassholder
                if (missingBubbleIds == null) {
                    missingBubbleIds = new HashSet<I>(bubbleIds.size());
                }
                missingBubbleIds.add(bubbleId);
            }
        }

        // Hent de som ikke ble funnet og legg inn i hashmap
        if (missingBubbleIds != null) {
            final Collection<StoreEntry> storeEntries = loadEntries(level, missingBubbleIds, false);
            final Map<BubbleId<?>, StoreEntry> storeEntryMap = new HashMap<BubbleId<?>, StoreEntry>(storeEntries.size());
            for (StoreEntry storeEntry : storeEntries) {
                storeEntryMap.put(storeEntry.getId(), storeEntry);
            }
            for (int i = 0; i < bubbleObjects.size(); i++) {
                if (bubbleObjectsFound.get(i) == null) {
                    final StoreEntry storeEntry = storeEntryMap.get(orderedBubbleIds.get(i));
                    bubbleObjectsFound.set(i, (T) storeEntry.getBubbleObject(level));
                }
            }
        }
        bubbleObjects.addAll(bubbleObjectsFound);

    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> StoreEntry insertEntry(int level, T bubbleObject) {
        StoreEntry storeEntry = storeCache.get(bubbleObject.getId());
        if (storeEntry == null) {
            storeEntry = storeCache.createEntry(level, bubbleObject.getId());
            storeEntry.setState(level, StoreEntryState.INSERTED);
        } else {
            switch (storeEntry.getDerivedState(level)) {
                case NULL:
                case UNCHANGED:
                    throw new ImplementationException("Forsøk på å kalle insert for eksisterende objekt: " + bubbleObject.getId());
                case INSERTED_DELETED:
                    storeEntry.setState(level, StoreEntryState.INSERTED);
                    break;
                case DELETED_INSERTED:
                case INSERTED:
                    throw new ImplementationException("Forsøk på å kalle insert for objekt hvor insert allerede har blitt kaldt: " + bubbleObject.getId());
                case UPDATED:
                    throw new ImplementationException("Forsøk på å kalle insert for objekt hvor update allerede har blitt kaldt: " + bubbleObject.getId());
                case DELETED:
                    storeEntry.setState(level, StoreEntryState.DELETED_INSERTED);
                    break;
            }
            storeEntry.checkNotDerivedInstance(level, bubbleObject);
        }
        BubbleObject oldInstance = storeEntry.getBubbleObject(level);
        if (oldInstance != bubbleObject) {
            bubbleObject.register(store);
            // TODO: Make oldInstance stale
        }
        onInsertEntry(level, storeEntry, bubbleObject);
        storeEntry.setLocked(level);       // TODO: fix
        return storeEntry;
    }


    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> StoreEntry updateEntry(int level, T bubbleObject) {
        StoreEntry storeEntry = storeCache.get(bubbleObject.getId());


        if (storeEntry == null) {
            storeEntry = storeCache.createEntry(level, bubbleObject.getId());
            ensureLocked(storeEntry);
            storeEntry.setState(level, StoreEntryState.UPDATED);
        } else {
            ensureLocked(storeEntry);
            switch (storeEntry.getState(level)) {
                case NULL:
                    storeEntry.setState(level, StoreEntryState.UPDATED);
                    break;
                case UNCHANGED:
                    // TOOD: checke locked
                    storeEntry.setState(level, StoreEntryState.UPDATED);
                    break;
                case INSERTED:
                    break;
                case DELETED_INSERTED:
                    // TOOD: checke locked
                    storeEntry.setState(level, StoreEntryState.UPDATED);
                case UPDATED:
                    break;
                case INSERTED_DELETED:
                case DELETED:
                    throw new ImplementationException("Forsøk på å kalle update for objekt hvor delete har blitt kaldt: " + bubbleObject.getId());
            }
            storeEntry.checkNotDerivedInstance(level, bubbleObject);

        }
        BubbleObject oldInstance = storeEntry.getBubbleObject(level);
        if (oldInstance != bubbleObject) {
            bubbleObject.register(store);
            // TODO: Make oldInstance stale
        }

        onUpdateEntry(level, storeEntry, bubbleObject);
        storeEntry.setLocked(level);       // TODO: fix
        return storeEntry;
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> StoreEntry deleteEntry(int level, T bubbleObject) {
        StoreEntry storeEntry = storeCache.get(bubbleObject.getId());


        if (storeEntry == null) {
            storeEntry = storeCache.createEntry(level, bubbleObject.getId());
            ensureLocked(storeEntry);
            storeEntry.setState(level, StoreEntryState.DELETED);
        } else {
            ensureLocked(storeEntry);
            switch (storeEntry.getState(level)) {
                case NULL:
                    storeEntry.setState(level, StoreEntryState.DELETED);
                    break;
                case INSERTED:
                    storeEntry.setState(level, StoreEntryState.INSERTED_DELETED);
                    break;
                case DELETED_INSERTED:
                case UNCHANGED:
                    // check locked
                case UPDATED:
                    storeEntry.setState(level, StoreEntryState.DELETED);
                    break;
                case DELETED:
                case INSERTED_DELETED:
                    throw new ImplementationException("Forsøk på å kalle update for objekt hvor delete har blitt kaldt: " + bubbleObject.getId());
            }
            storeEntry.checkNotDerivedInstance(level, bubbleObject);
        }
        T oldInstance = (T) storeEntry.getDerivedBubbleObject(level);
        if (oldInstance != bubbleObject) {
            bubbleObject.register(store);
            // TODO marker oldInstance som stale
        }
        onDeleteEntry(level, storeEntry, bubbleObject);
        storeEntry.setLocked(level);       // TODO: fix
        return storeEntry;
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> boolean evict(I bubbleId) {
        return evictEntry(level, bubbleId);
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> boolean evictAll() {
        return evictAllEntries(level);
    }

    @Override
    public final <T extends BubbleObject> void insert(T bubbleObject) {
        // Opprett BubbleId av riktig type hvis null
        if (bubbleObject.getId()==null) {
            final BubbleId<? extends BubbleObject> bubbleId = idService.getNextId(BubbleIds.getBubbleIdClass(bubbleObject.getClass()));
            bubbleObject.setId(bubbleId);
        }
        StoreEntry storeEntry = insertEntry(level, bubbleObject);
        addModified(storeEntry);
    }

    @Override
    public final <T extends BubbleObject> void update(T bubbleObject) {
        StoreEntry storeEntry = updateEntry(level, bubbleObject);
        addModified(storeEntry);
    }

    @Override
    public final <T extends BubbleObject> void delete(T bubbleObject) {
        StoreEntry storeEntry = deleteEntry(level, bubbleObject);
        addModified(storeEntry);
    }

    @Override
    public final <T extends BubbleObject, I extends BubbleId<? extends T>> T lock(I bubbleId) {
        StoreEntry entry = lockEntry(level, bubbleId);
        return (T) entry.getBubbleObject(level);
    }

    @Override
    public final <T extends BubbleObject, I extends BubbleId<? extends T>> void unlock(I bubbleId) {
        StoreEntry storeEntry = unlockEntry(level, bubbleId);
        removeModified(storeEntry);
    }

    public StoreUnitOfWork beginUnitOfWork() {
        return new StoreUnitOfWork(level + 1, this, storeCache, store);
    }

    @Override
    public void commitUnitOfWork(Map<BubbleId<?>, StoreEntry> modified) {
        for (Map.Entry<BubbleId<?>, StoreEntry> mapEntry : modified.entrySet()) {
            StoreEntry entry = mapEntry.getValue();
            switch (entry.getState(level + 1)) {
                case INSERTED:
                    commitInsert(entry);
                    break;
                case UPDATED:
                    commitUpdate(entry);
                    break;
                case DELETED:
                    commitDelete(entry);
                    break;
                case INSERTED_DELETED:
                    if (entry.getDerivedState(level) == StoreEntryState.INSERTED) {
                        commitDelete(entry);
                    } else {
                        storeCache.remove(entry.getId());
                    }
                    break;
                case DELETED_INSERTED:
                    if (entry.getDerivedState(level) == StoreEntryState.DELETED) {
                        commitInsert(entry);
                    } else {
                        entry.clear(level + 1);
                    }
                    break;
            }
        }
    }

    protected void commitInsert(StoreEntry entry) {
        switch (entry.getState(level)) {
            case NULL:
                entry.setState(level, StoreEntryState.INSERTED);
                break;
            case UNCHANGED:
                throw new ImplementationException("Forsøk på å kalle insert for eksisterende objekt: " + entry.getId());
            case INSERTED:
                throw new ImplementationException("Forsøk på å kalle insert for objekt hvor insert allerede har blitt kaldt: " + entry.getId());
            case UPDATED:
                throw new ImplementationException("Forsøk på å kalle insert for objekt hvor update allerede har blitt kaldt: " + entry.getId());
            case DELETED:
                entry.setState(level, StoreEntryState.UPDATED);
                break;
            case INSERTED_DELETED:
                entry.setState(level, StoreEntryState.INSERTED);
                break;
        }
        BubbleObject oldInstance = entry.getBubbleObject(level);
        BubbleObject newInstance = entry.getBubbleObject(level + 1);
        if (oldInstance != null && oldInstance != entry.getBubbleObject(level + 1)) {
            // TODO marker old instance som  stale
        }
        entry.commit(level + 1); // TODO fix
        if (entry.getState(level) == StoreEntryState.INSERTED) {
            onInsertEntry(level, entry, newInstance);
        } else {
            //onUpdatedEntry(level, entry, newInstance);
        }
        addModified(entry);
    }

    protected <T extends BubbleObject> void onInsertEntry(int level, StoreEntry storeEntry, T bubbleObject) {
        storeEntry.setBubbleObject(level, bubbleObject);
    }

    protected void commitUpdate(StoreEntry entry) {
        switch (entry.getState(level)) {
            case NULL:
                entry.setState(level, StoreEntryState.UPDATED);
                break;
            case INSERTED:
                break;
            case UNCHANGED:
                entry.setState(level, StoreEntryState.UPDATED);
                break;
            case UPDATED:
                break;
            case DELETED:
            case INSERTED_DELETED:
                throw new ImplementationException("Forsøk på å kalle update for objekt hvor delete har blitt kaldt: " + entry.getId());
        }
        BubbleObject oldInstance = entry.getBubbleObject(level);
        BubbleObject newInstance = entry.getBubbleObject(level + 1);
        if (oldInstance != null && oldInstance != entry.getBubbleObject(level + 1)) {
            // TODO marker old instance som  stale
        }
        entry.commit(level + 1); // TODO fix
        onUpdateEntry(level, entry, newInstance);
        addModified(entry);
    }


    protected <T extends BubbleObject> void onUpdateEntry(int level, StoreEntry storeEntry, T bubbleObject) {
        storeEntry.setBubbleObject(level, bubbleObject);
    }

    protected void commitDelete(StoreEntry entry) {
        switch (entry.getState(level)) {
            case NULL:
                entry.setState(level, StoreEntryState.DELETED);
                break;
            case INSERTED:
                entry.setState(level, StoreEntryState.INSERTED_DELETED);
                break;
            case UNCHANGED:
            case UPDATED:
                entry.setState(level, StoreEntryState.DELETED);
                break;
            case DELETED:
            case INSERTED_DELETED:
                throw new ImplementationException("Forsøk på å kalle delete for objekt hvor delete har blitt kaldt: " + entry.getId());
        }
        BubbleObject oldInstance = entry.getBubbleObject(level);
        BubbleObject newInstance = entry.getBubbleObject(level + 1);
        if (oldInstance != null && oldInstance != entry.getBubbleObject(level + 1)) {
            // TODO marker old instance som  stale
        }
        entry.commit(level + 1); // TODO fix
        onDeleteEntry(level, entry, newInstance);
        addModified(entry);
    }

    protected <T extends BubbleObject> void onDeleteEntry(int level, StoreEntry storeEntry, T bubbleObject) {
        storeEntry.setBubbleObject(level, bubbleObject);
    }

    @Override
    public void register(BubbleTransfer bubbleTransfer) {
        registerEntries(level, bubbleTransfer);
    }
}
