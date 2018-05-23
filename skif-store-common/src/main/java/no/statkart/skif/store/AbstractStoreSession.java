package no.statkart.skif.store;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import no.statkart.skif.exception.*;
import no.statkart.skif.service.sequence.IdService;
import no.statkart.skif.store.relation.cache.StoreRelationCache;
import no.statkart.skif.util.CopyHelper;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executor;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

/**
 * @author Henrik Fredholm
 * @author Tor Egil R. Strand
 */
public abstract class AbstractStoreSession implements WrappableStoreSession {
    protected final int level;
    protected final StoreCache storeCache;
    protected final LinkedHashMap<BubbleId<?>, StoreEntry> modifiedMap;
    protected AbstractStore store;
    protected IdService idService;

    protected AbstractStoreSession(int level, StoreCache storeCache) {
        this.level = level;
        this.storeCache = storeCache;
        this.modifiedMap = new LinkedHashMap<>(1000);
    }

    public void setStore(AbstractStore store) {
        this.store = store;
        this.storeCache.setStore(store);
        this.idService = store.getInstance(IdService.class);
    }

    protected void markModified() {
        // Nothing do do by default
    }

    protected boolean hasModifications() {
        return !modifiedMap.isEmpty();
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
    public final <T extends BubbleObject> T get(BubbleId<? extends T> bubbleId) {

        StoreEntry entry = storeCache.get(bubbleId);
        if (entry == null) {
            entry = loadEntry(level, bubbleId, false);
        }

        StoreEntryState state = entry.getState(level);
        if (state == StoreEntryState.DELETED || state == StoreEntryState.INSERTED_DELETED) {
            throw new ObjectNotFoundException(bubbleId);
        }

        //noinspection unchecked
        final T bubble = (T) entry.getDerivedBubbleObjectCopyIfLocked(level, store);
        if (bubble == null) {
            throw new ObjectNotFoundException(bubbleId);
        }
        return bubble;
    }

    protected void addModified(StoreEntry storeEntry) {
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
            bubbleObjects = get(new ArrayList<>(bubbleIds));
        }
        return bubbleObjects;
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> Set<T> get(Set<I> bubbleIds) {
        Set<T> result = new HashSet<>();
        get(bubbleIds, result);
        return result;
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> List<T> get(List<I> bubbleIds) {
        List<T> result = new ArrayList<>();
        get(bubbleIds, result);
        return result;
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> void get(Collection<I> bubbleIds, Collection<T> bubbleObjects) {
        checkNotNull(bubbleIds, "bubbleIds");
        Set<I> missingBubbleIds = null;
        Set<I> deletedBubbleIds = null;

        for (I bubbleId : bubbleIds) {
            final StoreEntry storeEntry = storeCache.get(bubbleId);
            final StoreEntryState state = storeEntry != null ? storeEntry.getState(level) : StoreEntryState.NULL;
            final boolean isDeleted = state == StoreEntryState.DELETED || state == StoreEntryState.INSERTED_DELETED;
            if (isDeleted) {
                if (deletedBubbleIds == null) {
                    deletedBubbleIds = new HashSet<>(bubbleIds.size());
                }
                deletedBubbleIds.add(bubbleId);
            } else {
                final BubbleObject bubbleObject = storeEntry == null ? null : storeEntry.getDerivedBubbleObjectCopyIfLocked(level, store);
                if (bubbleObject != null) {
                    //noinspection unchecked
                    bubbleObjects.add((T) bubbleObject);
                } else {
                    if (missingBubbleIds == null) {
                        missingBubbleIds = new HashSet<>(bubbleIds.size());
                    }
                    missingBubbleIds.add(bubbleId);
                }
            }
        }

        if (missingBubbleIds != null) {
            try {
                if (missingBubbleIds.size() == 1) {
                    StoreEntry entry = loadEntry(level, missingBubbleIds.iterator().next(), false);
                    //noinspection unchecked
                    bubbleObjects.add((T) entry.getDerivedBubbleObjectCopyIfLocked(level, store));
                } else {
                    Collection<StoreEntry> entries = loadEntries(level, missingBubbleIds, false);
                    for (StoreEntry entry : entries) {
                        //noinspection unchecked
                        bubbleObjects.add((T) entry.getDerivedBubbleObjectCopyIfLocked(level, store));

                    }
                }
            } catch (ObjectsNotFoundException e) {
                if (deletedBubbleIds != null) {
                    throw new ObjectsNotFoundException(ImmutableSet.<BubbleId<?>>builder().addAll(e.getIdsNotFound()).addAll(deletedBubbleIds).build());
                } else {
                    throw e;
                }
            }
        }
        if (deletedBubbleIds != null) {
            throw new ObjectsNotFoundException(ImmutableSet.copyOf(deletedBubbleIds));
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
            bubbleObjects = getOrdered(new ArrayList<>(bubbleIds));
        }
        return bubbleObjects;
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> Set<T> getOrdered(Set<I> bubbleIds) {
        Set<T> result = new LinkedHashSet<>();
        getOrdered(bubbleIds, result);
        return result;
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> List<T> getOrdered(List<I> bubbleIds) {
        List<T> result = new ArrayList<>();
        getOrdered(bubbleIds, result);
        return result;
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> void getOrdered(Collection<I> bubbleIds, Collection<T> bubbleObjects) {
        checkNotNull(bubbleIds, "bubbleIds");

        ArrayList<T> bubbleObjectsFound = new ArrayList<>(bubbleIds.size());
        ArrayList<I> orderedBubbleIds = new ArrayList<>(bubbleIds.size());
        Set<I> missingBubbleIds = null;
        Set<I> deletedBubbleIds = null;

        for (I bubbleId : bubbleIds) {
            orderedBubbleIds.add(bubbleId);
            final StoreEntry storeEntry = storeCache.get(bubbleId);
            final StoreEntryState state = storeEntry != null ? storeEntry.getState(level) : StoreEntryState.NULL;
            final boolean isDeleted = state == StoreEntryState.DELETED || state == StoreEntryState.INSERTED_DELETED;
            if (isDeleted) {
                if (deletedBubbleIds == null) {
                    deletedBubbleIds = new HashSet<>(bubbleIds.size());
                }
                deletedBubbleIds.add(bubbleId);
                bubbleObjectsFound.add(null);  // null er plassholder
            } else {
                final BubbleObject bubbleObject = storeEntry == null ? null : storeEntry.getDerivedBubbleObjectCopyIfLocked(level, store);
                if (bubbleObject != null) {
                    //noinspection unchecked
                    bubbleObjectsFound.add((T) bubbleObject);
                } else {
                    bubbleObjectsFound.add(null);  // null er plassholder
                    if (missingBubbleIds == null) {
                        missingBubbleIds = new HashSet<>(bubbleIds.size());
                    }
                    missingBubbleIds.add(bubbleId);
                }
            }
        }

        // Hent de som ikke ble funnet og legg inn i hashmap
        if (missingBubbleIds != null) {
            final Collection<StoreEntry> storeEntries;
            try {
                storeEntries = loadEntries(level, missingBubbleIds, false);
            } catch (ObjectsNotFoundException e) {
                if (deletedBubbleIds != null) {
                    throw new ObjectsNotFoundException(ImmutableSet.<BubbleId<?>>builder().addAll(e.getIdsNotFound()).addAll(deletedBubbleIds).build());
                } else {
                    throw e;
                }
            }

            final Map<BubbleId<?>, StoreEntry> storeEntryMap = new HashMap<>(storeEntries.size());
            for (StoreEntry storeEntry : storeEntries) {
                storeEntryMap.put(storeEntry.getId(), storeEntry);
            }
            for (int i = 0; i < bubbleObjectsFound.size(); i++) {
                if (bubbleObjectsFound.get(i) == null) {
                    I id = orderedBubbleIds.get(i);
                    if (deletedBubbleIds == null || !deletedBubbleIds.contains(id)) {
                        final StoreEntry storeEntry = storeEntryMap.get(id);
                        //noinspection unchecked
                        bubbleObjectsFound.set(i, (T) storeEntry.getDerivedBubbleObjectCopyIfLocked(level, store));
                    }
                }
            }
        }
        if(deletedBubbleIds != null) {
            throw new ObjectsNotFoundException(ImmutableSet.copyOf(deletedBubbleIds));
        }
        bubbleObjects.addAll(bubbleObjectsFound);

    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> Collection<T> getIgnoreMissing(Collection<I> bubbleIds) {
        Collection<T> bubbleObjects;
        if (bubbleIds instanceof Set) {
            bubbleObjects = getIgnoreMissing((Set<I>) bubbleIds);
        } else if (bubbleIds instanceof List) {
            bubbleObjects = getIgnoreMissing((List<I>) bubbleIds);
        } else {
            checkNotNull(bubbleIds, "bubbleIds");
            bubbleObjects = getIgnoreMissing(new ArrayList<>(bubbleIds));
        }
        return bubbleObjects;
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> Set<T> getIgnoreMissing(Set<I> bubbleIds) {
        Set<T> result = new HashSet<>();
        getIgnoreMissing(bubbleIds, result);
        return result;
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> List<T> getIgnoreMissing(List<I> bubbleIds) {
        List<T> result = new ArrayList<>();
        getIgnoreMissing(bubbleIds, result);
        return result;
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> void getIgnoreMissing(Collection<I> bubbleIds, Collection<T> bubbleObjects) {
        checkNotNull(bubbleIds, "bubbleIds");
        Set<I> missingBubbleIds = null;

        for (I bubbleId : bubbleIds) {
            final StoreEntry storeEntry = storeCache.get(bubbleId);
            final StoreEntryState state = storeEntry != null ? storeEntry.getState(level) : StoreEntryState.NULL;
            final boolean isDeleted = state == StoreEntryState.DELETED || state == StoreEntryState.INSERTED_DELETED;
            if (!isDeleted) {
                final BubbleObject bubbleObject = storeEntry == null ? null : storeEntry.getDerivedBubbleObjectCopyIfLocked(level, store);
                if (bubbleObject != null) {
                    //noinspection unchecked
                    bubbleObjects.add((T) bubbleObject);
                } else {
                    if (missingBubbleIds == null) {
                        missingBubbleIds = new HashSet<>(bubbleIds.size());
                    }
                    missingBubbleIds.add(bubbleId);
                }
            }
        }

        if (missingBubbleIds != null) {
            if (missingBubbleIds.size() == 1) {
                try {
                    StoreEntry entry = loadEntry(level, missingBubbleIds.iterator().next(), false);
                    //noinspection unchecked
                    bubbleObjects.add((T) entry.getDerivedBubbleObjectCopyIfLocked(level, store));
                } catch (ObjectNotFoundException ignore) {
                    // OK, så fantes den ikke, da.
                }
            } else {
                Collection<StoreEntry> entries = loadEntriesIgnoreMissing(level, missingBubbleIds, false);
                for (StoreEntry entry : entries) {
                    //noinspection unchecked
                    bubbleObjects.add((T) entry.getDerivedBubbleObjectCopyIfLocked(level, store));
                }
            }
        }
    }

    @Override
    public <T extends BubbleObject> StoreEntry insertEntry(int level, T bubbleObject) {
        StoreEntry storeEntry = storeCache.get(bubbleObject.getId());
        if (storeEntry == null) {
            storeEntry = storeCache.createEntry(level, bubbleObject.getId());
            storeEntry.setState(level, StoreEntryState.INSERTED);
        } else {
            switch (storeEntry.getDerivedState(level)) {
                case NULL:
                case UNCHANGED:
                    throw new ImplementationException("Attempt at inserting existing object: " + bubbleObject.getId());
                case INSERTED_DELETED:
                    storeEntry.setState(level, StoreEntryState.INSERTED);
                    break;
                case DELETED_INSERTED:
                case INSERTED:
                    throw new ImplementationException("Attempt at inserting inserted object: " + bubbleObject.getId());
                case UPDATED:
                    throw new ImplementationException("Attempt at inserting updated object: " + bubbleObject.getId());
                case DELETED:
                    storeEntry.setState(level, StoreEntryState.DELETED_INSERTED);
                    break;
            }
            storeEntry.checkNotDerivedInstance(level, bubbleObject);
        }
        // Dette kan være en re-insert av et objekt som tidligere har blitt slettet, men dette krever ingen
        // ingen ekstra håndtering fordi:
        // 1) Hvis RelationCache er enabled så vil relations i oldInstance ha blitt fjerne da oldInstance ble slettet fra
        //    Store
        // 2) Hvis bubbleObject allerede er knyttet til Store, så skader det ikke at bubbleObject registreres i Store
        // og at RelationCache oppdaters på nytt
        onInsertEntry(level, storeEntry, bubbleObject);
        storeEntry.setLocked(level);       // TODO: Kunne denne bli satt av ensureLocked når den må hente status via lockerStrategy?
        bubbleObject.register(store);
        StoreRelationCache relationCache = store.getRelationCache();
        if (relationCache.isEnabled()) {
            if (bubbleObject instanceof InverseRelationParticipation) {
                relationCache.updateAdded(bubbleObject.getBubbleId(), (InverseRelationParticipation) bubbleObject);
            }
            if (bubbleObject instanceof BubbleObjectWithIdent) {
                ((BubbleObjectWithIdent<?>)bubbleObject).onIdentChanged();
            }
        }
        return storeEntry;
    }


    @Override
    public <T extends BubbleObject> StoreEntry updateEntry(int level, T bubbleObject) {
        StoreEntry storeEntry = storeCache.get(bubbleObject.getId());

        if (storeEntry == null) {
            // Konverterer id til base i tilfelle subtypeendring
            storeEntry = loadEntry(level, bubbleObject.getId().asBase(), false);
            //storeEntry = storeCache.createEntry(level, bubbleObject.getId());
            ensureLocked(storeEntry);
            storeEntry.setState(level, StoreEntryState.UPDATED);
        } else {
            ensureLocked(storeEntry);
            switch (storeEntry.getState(level)) {
                case NULL:
                    storeEntry.setState(level, StoreEntryState.UPDATED);
                    break;
                case UNCHANGED:
                    storeEntry.setState(level, StoreEntryState.UPDATED);
                    break;
                case INSERTED:
                    break;
                case DELETED_INSERTED:
                    storeEntry.setState(level, StoreEntryState.UPDATED);
                case UPDATED:
                    break;
                case INSERTED_DELETED:
                case DELETED:
                    throw new ImplementationException("Attempt at updating deleted object: " + bubbleObject.getId());
            }
            storeEntry.checkNotDerivedInstance(level, bubbleObject);

        }
        // Må her sjekkes om bubbleObject er en helt ny instans. Dersom det er tilfellet må relasjoner
        // som lå i oldInstance fjernes fra RelationCache. I tillegg fjernes også relasjoner som ligger i bubbleObject
        // siden den knyttes til Store.
        BubbleObject oldInstance = storeEntry.getDerivedBubbleObject(level);
        onUpdateEntry(level, storeEntry, bubbleObject);
        storeEntry.setLocked(level);       // TODO: Kunne denne bli satt av ensureLocked når den må hente status via lockerStrategy?

        if (oldInstance != bubbleObject) {
            bubbleObject.register(store);
            // TODO: Make oldInstance stale in order to detect continued usage of oldInstance

            StoreRelationCache relationCache = store.getRelationCache();
            if (relationCache.isEnabled()) {
                if (oldInstance != null && oldInstance instanceof InverseRelationParticipation) {
                    relationCache.updateRemoved(oldInstance.getBubbleId(), (InverseRelationParticipation) oldInstance);
                }
                if (bubbleObject instanceof InverseRelationParticipation) {
                    relationCache.updateAdded(bubbleObject.getBubbleId(), (InverseRelationParticipation) bubbleObject);
                }
                if (bubbleObject instanceof BubbleObjectWithIdent) {
                    Object oldIdent = (oldInstance == null) ? null : ((BubbleObjectWithIdent<?>) oldInstance).getIdent();
                    BubbleObjectWithIdent<?> bubbleBubbleObjectWithIdent = (BubbleObjectWithIdent<?>) bubbleObject;
                    if (!bubbleBubbleObjectWithIdent.getIdent().equals(oldIdent)) {
                        bubbleBubbleObjectWithIdent.onIdentChanged();
                    }
                }
            }
        }
        return storeEntry;
    }

    @Override
    public <T extends BubbleObject> StoreEntry deleteEntry(int level, T bubbleObject) {
        StoreEntry storeEntry = storeCache.get(bubbleObject.getId());

        if (storeEntry == null) {
            storeEntry = loadEntry(level, bubbleObject.getId(), false);
            //storeEntry = storeCache.createEntry(level, bubbleObject.getId());
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
                    throw new ImplementationException("Attempt at updating deleted object: " + bubbleObject.getId());
            }
            storeEntry.checkNotDerivedInstance(level, bubbleObject);
        }

        // Må her sjekkes om bubbleObject er en helt ny instans. Dersom det er tilfellet må relasjoner
        // som lå i oldInstance også fjernes fra relasjonscachen og videre må bubbleObject registres
        // og knyttes til store.
        BubbleObject oldInstance = storeEntry.getDerivedBubbleObject(level);
        onDeleteEntry(level, storeEntry, bubbleObject);
        storeEntry.setLocked(level);       // TODO: Kunne denne bli satt av ensureLocked når den må hente status via lockerStrategy?

        StoreRelationCache relationCache = store.getRelationCache();
        if (oldInstance != bubbleObject) {
            bubbleObject.register(store);
            // TODO: Make oldInstance stale in order to detect continued usage of oldInstance

            if (relationCache.isEnabled()) {
                if (oldInstance != null && oldInstance instanceof InverseRelationParticipation) {
                    relationCache.updateRemoved(oldInstance.getBubbleId(), (InverseRelationParticipation) oldInstance);

                }
            }
        }
        if (relationCache.isEnabled()) {
            if (bubbleObject instanceof InverseRelationParticipation) {
                relationCache.updateRemoved(bubbleObject.getBubbleId(), (InverseRelationParticipation) bubbleObject);
            }
            if (bubbleObject instanceof BubbleObjectWithIdent) {
                relationCache.onIdentRemoved((BubbleObjectWithIdent)bubbleObject);
            }
        }
        return storeEntry;
    }

    @Override
    public <T extends BubbleObject> boolean undoEntry(int level, T bubbleObject) {
        if (level == 0) {
            throw new ImplementationException("Can't undo on level 0");
        }

        StoreEntry storeEntry = storeCache.get(bubbleObject.getId());

        if (storeEntry == null) {
            throw new ImplementationException("Attempt at undoing object not in Store: " + bubbleObject.getId());
        } else {
            ensureLocked(storeEntry);
            storeEntry.checkNotDerivedInstance(level, bubbleObject);
            StoreRelationCache relationCache = store.getRelationCache();
            if (relationCache.isEnabled()) {
                throw new UnsupportedOperationException("Attempt at undoing an object while StoreRelationCache is enabled: " + bubbleObject.getId());
            }

            BubbleObject derivedBubbleObject = storeEntry.getDerivedBubbleObject(level - 1);
            if (derivedBubbleObject == null) {
                // Objektet har kommet utenfra og ikke opp fra lavere lag. Ta vekk hele StoreEntry.
                storeCache.remove(storeEntry.getId());
                return false;
            } else {
                // Blank bare ut entry for dette nivået.
                storeEntry.setState(level, StoreEntryState.NULL);
                storeEntry.setBubbleObject(level, null);
                return true;
            }
        }
    }

    @Override
    public <I extends BubbleId<?>> boolean evict(I bubbleId) {
        return evictEntry(level, bubbleId);
    }

    @Override
    public boolean evictAll() {
        return evictAllEntries(level);
    }

    @Override
    public final <T extends BubbleObject> void insert(T bubbleObject) {
        // Opprett BubbleId av riktig type hvis null
        if (bubbleObject.getId() == null) {
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
    public <T extends BubbleObject> void undo(T bubbleObject) {
        boolean entryKept = undoEntry(level, bubbleObject);
        if (!entryKept) {
            modifiedMap.remove(bubbleObject.getId());
            markModified();
        }
    }

    @Override
    public <I extends BubbleId<?>> void reorderModification(I bubbleId) {
        final StoreEntry storeEntry = modifiedMap.remove(bubbleId);
        markModified();
        if (storeEntry == null) {
            throw new ImplementationException("BubbleId not modified in session: " + bubbleId);
        }
        modifiedMap.put(bubbleId, storeEntry);
    }

    @Override
    public <T extends BubbleObject> T lock(BubbleId<? extends T> bubbleId) {
        StoreEntry entry = lockEntry(level, bubbleId);
        //noinspection unchecked
        return (T) entry.getBubbleObject(level);
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> Collection<T> lock(Collection<I> bubbleIds) {
        Collection<T> bubbleObjects;
        if (bubbleIds instanceof Set) {
            bubbleObjects = lock((Set<I>) bubbleIds);
        } else if (bubbleIds instanceof List) {
            bubbleObjects = lock((List<I>) bubbleIds);
        } else {
            checkNotNull(bubbleIds, "bubbleIds");
            bubbleObjects = lock(new ArrayList<>(bubbleIds));
        }
        return bubbleObjects;
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> Set<T> lock(Set<I> bubbleIds) {
        Set<T> result = new HashSet<>();
        lock(bubbleIds, result);
        return result;
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> List<T> lock(List<I> bubbleIds) {
        List<T> result = new ArrayList<>();
        lock(bubbleIds, result);
        return result;
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> void lock(Collection<I> bubbleIds, Collection<T> bubbleObjects) {
        checkNotNull(bubbleIds, "bubbleIds");
        Set<I> unlockedBubbleIds = null;

        for (I bubbleId : bubbleIds) {
            final StoreEntry storeEntry = storeCache.get(bubbleId);
            if (storeEntry != null && storeEntry.isLocked()) {
                //noinspection unchecked
                bubbleObjects.add((T) storeEntry.getDerivedBubbleObjectCopyIfLocked(level, store));
            } else {
                if (unlockedBubbleIds == null) {
                    unlockedBubbleIds = new HashSet<>(bubbleIds.size());
                }
                unlockedBubbleIds.add(bubbleId);
            }
        }

        if (unlockedBubbleIds != null) {
            if (unlockedBubbleIds.size() == 1) {
                StoreEntry entry = lockEntry(level, unlockedBubbleIds.iterator().next());
                //noinspection unchecked
                bubbleObjects.add((T) entry.getDerivedBubbleObjectCopyIfLocked(level, store));
            } else {
                Collection<StoreEntry> entries = lockEntries(level, unlockedBubbleIds);
                for (StoreEntry entry : entries) {
                    //noinspection unchecked
                    bubbleObjects.add((T) entry.getDerivedBubbleObjectCopyIfLocked(level, store));
                }
            }
        }
    }

    @Override
    public final <I extends BubbleId<?>> void unlock(I bubbleId) {
        StoreEntry storeEntry = unlockEntry(level, bubbleId);
        removeModified(storeEntry);
    }

    @Override
    public void unlock(Collection<? extends BubbleId<?>> bubbleIds) {
        Collection<StoreEntry> storeEntries = unlockEntries(level, bubbleIds);
        for (StoreEntry storeEntry : storeEntries) {
            removeModified(storeEntry);
        }
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
                case UNCHANGED:
                    commitUnchanged(entry);
                    break;
            }
        }
    }

    private void commitUnchanged(StoreEntry entry) {
        int levelForDerivedBubbleObject = entry.getLevelForDerivedBubbleObject(level);
        if (levelForDerivedBubbleObject!=level) {
            onUpdateEntry(level, entry, CopyHelper.copy(entry.getBubbleObject(levelForDerivedBubbleObject)));
        }
        entry.commit(level + 1);
    }

    protected void commitInsert(StoreEntry entry) {
        switch (entry.getState(level)) {
            case NULL:
                entry.setState(level, StoreEntryState.INSERTED);
                break;
            case UNCHANGED:
                throw new ImplementationException("Attempt at inserting existing object: " + entry.getId());
            case INSERTED:
                throw new ImplementationException("Attempt at inserting inserted object: " + entry.getId());
            case UPDATED:
                throw new ImplementationException("Attempt at inserting updated object: " + entry.getId());
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
        entry.commit(level + 1);
        if (entry.getState(level) == StoreEntryState.INSERTED) {
            onInsertEntry(level, entry, newInstance);
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
        entry.commit(level + 1);
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
                throw new ImplementationException("Attempt at deleting deleted object: " + entry.getId());
        }
        BubbleObject oldInstance = entry.getBubbleObject(level);
        BubbleObject newInstance = entry.getBubbleObject(level + 1);
        if (oldInstance != null && oldInstance != entry.getBubbleObject(level + 1)) {
            // TODO marker old instance som  stale
        }
        entry.commit(level + 1);
        onDeleteEntry(level, entry, newInstance);
        addModified(entry);
    }

    protected <T extends BubbleObject> void onDeleteEntry(int level, StoreEntry storeEntry, T bubbleObject) {
        storeEntry.setBubbleObject(level, bubbleObject);
    }

    @Override
    public void register(Transfer<?> transfer) {
        Collection<StoreEntry> lockedEntriesNotAlreadyLocked = registerEntries(level, transfer);
        for (StoreEntry entry : lockedEntriesNotAlreadyLocked) {
            modifiedMap.put(entry.getId(), entry);
        }
        markModified();
    }

    @Override
    public int getLevel() {
        return level;
    }

    @Override
    public boolean inAttachedMode() {
        return false;
    }

    /**
     * Ved enabling av RelationCaching må cachen oppdateres med endringer i alle modifiserte objekter, herunder også de som bare er
     * låste fordi de også kan inneholde endringer. Deretter vil trackingen skje synkront når et objekt endres eller
     * registreres i Store. For hvert endret objekt må de gamle relasjonsverdier fjernes og de nye legges inn. Siden
     * objektene ikke selv ved hva deres gamle relasjonsverdier var, må Store hjelpe til her. Dette er løst ved å sikre
     * at Store tar vare på de opprinnelige umodifiserte bobleobjektene der hvor det er behov for det (dvs. alle andre
     * steder enn for StoreServerSession hvor hibernate holder objektene i synk med databasen via autoflushing). Store
     * tar vare på en kopi når objektet låses eller også hentes en kopi fra databasen eller serveren første gang ett
     * detached bobleobjekt oppdateres eller slettes.
     */
    public void onEnableRelationCache(int level) {
        StoreRelationCache relationCache = store.getRelationCache();
        checkState(relationCache.isEnabled());
        // Må her itererer på kopi av storeCache fordi nye objekter kan bli lastet inn i denne ifm RelationCache
        // beregningen. For eksempel ved caching av identer som byggs utfra flere bobler som da må lastes. Mengden
        // av objekter som er aktuelle for cacheberegningen er dog uforandret så det er uproblematisk at cachen vokser.
        Collection<StoreEntry> values = Lists.newArrayList(storeCache.values());
        for (StoreEntry storeEntry : values) {
            if (storeEntry.isLocked()) {
                BubbleObject persistedBubbleObject;
                BubbleObject bubbleObject;
                switch (storeEntry.getDerivedState(level)) {
                    case INSERTED:
                        bubbleObject = storeEntry.getDerivedBubbleObject(level);
                        if (bubbleObject instanceof InverseRelationParticipation) {
                            relationCache.updateAdded(bubbleObject.getBubbleId(), (InverseRelationParticipation) bubbleObject);
                        }
                        if (bubbleObject instanceof BubbleObjectWithIdent) {
                            BubbleObjectWithIdent<?> bubbleBubbleObjectWithIdent = (BubbleObjectWithIdent<?>) bubbleObject;
                            bubbleBubbleObjectWithIdent.onIdentChanged();
                        }
                        break;

                    case DELETED_INSERTED:
                    case UPDATED:
                    case UNCHANGED: /* UNCHANGED representerer objekter er låst hvor Store.update() ikke har blitt kallt ennå. Objektet kan likevel være endret */
                        persistedBubbleObject = getPersistedBubbleObjectForLocked(storeEntry);
                        bubbleObject = storeEntry.getDerivedBubbleObject(level);
                        if (persistedBubbleObject != bubbleObject) {
                            if (persistedBubbleObject != null && persistedBubbleObject instanceof InverseRelationParticipation) {
                                relationCache.updateRemoved(persistedBubbleObject.getBubbleId(), (InverseRelationParticipation) persistedBubbleObject, new WithoutUnitOfWorkExecutor());
                            }
                            if (bubbleObject instanceof InverseRelationParticipation) {
                                relationCache.updateAdded(bubbleObject.getBubbleId(), (InverseRelationParticipation) bubbleObject);
                            }
                            if (bubbleObject instanceof BubbleObjectWithIdent) {
                                // For composite identer vil 'oldIdent' slik den beregnes her kun være forskjellig fra 'ident'
                                // for den delen som tilhører denne boblen. Hvis kun den avledede delen er endret vil dette bli
                                // plukket opp når den avledede boblen behandles. På det tidspunkt vil da 'onIdentChanged'
                                // bli kallt automatisk på boblen som behandles her. Derfor er det greit at 'onIdentChanged'
                                // kun blir kallt her hvis boblens egen del av identen er endret.
                                Object oldIdent = (persistedBubbleObject == null) ? null : ((BubbleObjectWithIdent<?>) persistedBubbleObject).getIdent();
                                BubbleObjectWithIdent<?> bubbleBubbleObjectWithIdent = (BubbleObjectWithIdent<?>) bubbleObject;
                                Object ident = bubbleBubbleObjectWithIdent.getIdent();
                                if ((ident==null && oldIdent!=null) || !ident.equals(oldIdent)) {
                                    bubbleBubbleObjectWithIdent.onIdentChanged();
                                }
                            }
                        } else {
                            // Kan komme her hvis vi er på serveren og når derived level er 0. Da vil objektet være i synk
                            // databasen og det er derfor ikke nødvendig å gjøre noen ting.

                            // Kan også kommer her hvis objektet er låst i StoreSessionClient (dvs før UnitOfWork ble startet).
                            // Slik objekter må under ingen omstendigheter endres hvis beregningen av cachingen skal bli
                            // riktig, men systemet hindre ikke dette direkte bortsett fra at man ikke vil kunne kalle
                            // Store.update(object) med objektet. Dvs man må trikse det til ved å kalle
                            // Store.update(CopyHelper.copy(object)) og det skal man jo egentlig ikke gøre.
                            // UnitOfWork.undo() vil jo heller ikke virker hvis man holder på slikt. Caching algoritmen
                            // vil jo også feile hvis men holder på å endre objekter som ikke er låst.
                            //
                            // Hvis man gjør endringer riktik, dvs. starter UnitOfWork og sier Store.get() på objektet
                            // man vil endre blir cachingen riktig. Man vil dog uansett kommer her for de objekter som
                            // er låste men ennå ikke hentet ut for endring i UnitOfWork. Det er riktig at systemet ikke
                            // trenger å gjøre noe for disse objektene.
                            Preconditions.checkState(storeEntry.getLevelForDerivedBubbleObject(level) == 0, "storeEntry.getLevelForDerivedBubbleObject(level)==0. StoreSessionClass=%s, level=%d, storeEntry=%s", this.getClass().getName(), storeEntry);
                        }
                        break;

                    case DELETED:
                    case INSERTED_DELETED:
                        persistedBubbleObject = getPersistedBubbleObjectForLocked(storeEntry);
                        bubbleObject = storeEntry.getDerivedBubbleObject(level);
                        if (persistedBubbleObject != bubbleObject) {
                            if (persistedBubbleObject != null && persistedBubbleObject instanceof InverseRelationParticipation) {
                                relationCache.updateRemoved(persistedBubbleObject.getBubbleId(), (InverseRelationParticipation) persistedBubbleObject, new WithoutUnitOfWorkExecutor());
                            }
                            if (bubbleObject instanceof InverseRelationParticipation) {
                                relationCache.updateRemoved(bubbleObject.getBubbleId(), (InverseRelationParticipation) bubbleObject);
                            }
                            if (bubbleObject instanceof BubbleObjectWithIdent) {
                                relationCache.onIdentRemoved((BubbleObjectWithIdent)bubbleObject);
                            }
                        } else {
                            // Kan komme her hvis vi er på serveren og når derived level er 0. Da vil objektet være i synk
                            // databasen og det er derfor ikke nødvendig å gjøre noen ting.

                            // Kan også kommer her hvis objektet er låst i StoreSessionClient (dvs før UnitOfWork ble startet).
                            // Slik objekter må under ingen omstendigheter endres hvis beregningen av cachingen skal bli
                            // riktig, men systemet hindre ikke dette direkte bortsett fra at man ikke vil kunne kalle
                            // Store.update(object) med objektet. Dvs man må trikse det til ved å kalle
                            // Store.update(CopyHelper.copy(object)) og det skal man jo egentlig ikke gøre.
                            // UnitOfWork.undo() vil jo heller ikke virker hvis man holder på slikt. Caching algoritmen
                            // vil jo også feile hvis men holder på å endre objekter som ikke er låst.
                            //
                            // Hvis man gjør endringer riktik, dvs. starter UnitOfWork og sier Store.get() på objektet
                            // man vil endre blir cachingen riktig. Man vil dog uansett kommer her for de objekter som
                            // er låste men ennå ikke hentet ut for endring i UnitOfWork. Det er riktig at systemet ikke
                            // trenger å gjøre noe for disse objektene.
                            Preconditions.checkState(storeEntry.getLevelForDerivedBubbleObject(level) == 0, "storeEntry.getLevelForDerivedBubbleObject(level)==0. StoreSessionClass=%s, level=%d, storeEntry=%s", this.getClass().getName(), storeEntry);
                        }
                        break;
                    default:
                        throw new IllegalStateException("Unexpected state: " + storeEntry.getDerivedState(level));
                }
            }
        }
    }

    @Override
    public UnitOfWorkTransfer getSessionSnapshot() {
        List<BubbleObject> insertedObjects = Lists.newArrayListWithCapacity(modifiedMap.size());
        List<BubbleObject> updatedObjects = Lists.newArrayListWithCapacity(modifiedMap.size());
        List<BubbleObject> deletedObjects = Lists.newArrayList();

        for (StoreEntry storeCacheEntry : modifiedMap.values()) {
            StoreEntryState state = storeCacheEntry.getState(level);
            switch (state) {
                case INSERTED:
                    insertedObjects.add(storeCacheEntry.getBubbleObject(level));
                    break;
                case DELETED_INSERTED:
                case UPDATED:
                    updatedObjects.add(storeCacheEntry.getBubbleObject(level));
                    break;
                case DELETED:
                    deletedObjects.add(storeCacheEntry.getBubbleObject(level));
                    break;
            }
        }
        return new UnitOfWorkTransfer(insertedObjects, updatedObjects, deletedObjects);
    }

    @Override
    public <T extends Transfer<?>> T getAllLoaded(T transfer) {
        for (StoreEntry storeEntry : storeCache.values()) {
            StoreEntryState state = storeEntry.getState(level);
            if (state !=StoreEntryState.INSERTED && state != StoreEntryState.INSERTED_DELETED) {
                transfer.add(storeEntry.getBubbleObject(0));
            }
        }
        return transfer;
    }

    private class WithoutUnitOfWorkExecutor implements Executor {
        @Override
        public void execute(@Nonnull Runnable command) {
            // Må midlertidig poppe av alle unit-of-works, slik at navigering gjennom de forskjellige get() går på nivå 0.
            WrappableStoreSession originalSession = store.storeSession;
            try {
                while (store.storeSession instanceof StoreUnitOfWork) {
                    store.storeSession = ((StoreUnitOfWork) store.storeSession).wrappedStoreSession;
                }

                command.run();
            } finally {
                store.storeSession = originalSession;
            }
        }
    }
}
