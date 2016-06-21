package no.statkart.skif.store;

import com.google.common.base.Preconditions;
import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.store.service.StoreService;
import no.statkart.skif.util.CopyHelper;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * StoreSession som utgjør avsluttende ledd på klienten. Klassen anvender en {@link StoreService} for å hente
 * objekter fra server
 *
 * @author Henrik Fredholm
 * @since 2.1
 */
public class StoreSessionClient extends AbstractStoreSession {
    private final StoreService storeService;
    private final SnapshotVersionContext snapshotVersionContext;
    @Nullable
    private final StoreClientReadCache readCache;
    private final Comparator<BubbleObject> versionComparator;


    public StoreSessionClient(StoreService storeService, SnapshotVersionContext snapshotVersionContext) {
        this(storeService, snapshotVersionContext, new StoreCache());
    }

    public StoreSessionClient(StoreService storeService, SnapshotVersionContext snapshotVersionContext, StoreCache storeCache) {
        this(storeService, snapshotVersionContext, storeCache, defaultComparator());
    }

    public StoreSessionClient(StoreService storeService, SnapshotVersionContext snapshotVersionContext, StoreCache storeCache, Comparator<BubbleObject> versionComparator) {
        this(storeService, snapshotVersionContext, storeCache, versionComparator, null);
    }

    public StoreSessionClient(StoreService storeService, SnapshotVersionContext snapshotVersionContext, StoreCache storeCache, Comparator<BubbleObject> versionComparator, @Nullable StoreClientReadCache readCache) {
        super(0, storeCache);
        this.snapshotVersionContext = snapshotVersionContext;
        this.versionComparator = versionComparator;
        this.readCache = readCache;
        this.storeService = (readCache==null) ? storeService : new StoreServiceWithReadCache(storeService, readCache);
    }

    public static Comparator<BubbleObject> defaultComparator() {
        return new Comparator<BubbleObject>() {
            @Override
            public int compare(BubbleObject o1, BubbleObject o2) {
                return Long.compare(o1.getVersjonId(), o2.getVersjonId());
            }
        };
    }

    protected boolean isLocked(StoreEntry storeEntry) {
        return storeEntry.isLocked();
    }

    @Override
    public <T extends BubbleObject> StoreEntry insertEntry(int level, T bubbleObject) {
        if (level == 0) {
            throw new ImplementationException("Insert on client must be done in a UnitOfWork and sent to server via getUnitOfWorkTransfer()");
        } else {
            return super.insertEntry(level, bubbleObject);
        }
    }

    @Override
    public <T extends BubbleObject> StoreEntry updateEntry(int level, T bubbleObject) {
        if (level == 0) {
            throw new ImplementationException("Update on client must be done in a UnitOfWork and sent to server via getUnitOfWorkTransfer()");
        } else {
            return super.updateEntry(level, bubbleObject);
        }
    }

    @Override
    public <T extends BubbleObject> StoreEntry deleteEntry(int level, T bubbleObject) {
        if (level == 0) {
            throw new ImplementationException("Delete on client must be done in a UnitOfWork and sent to server via getUnitOfWorkTransfer()");
        } else {
            return super.deleteEntry(level, bubbleObject);
        }
    }

    @Override
    public void commitUnitOfWork(Map<BubbleId<?>, StoreEntry> modified) {
        throw new ImplementationException("Commit of UnitOfWork directly against server is not supported, but must be done via getUnitOfWorkTransfer()");
    }

    @Override
    public <I extends BubbleId<?>> boolean isLocked(I bubbleId) {
        StoreEntry storeEntry = storeCache.get(bubbleId);
        return (storeEntry != null && isLocked(storeEntry));
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> StoreEntry loadEntry(int level, I bubbleId, boolean refresh) {
        SnapshotVersion oldSnapshotVersion = snapshotVersionContext.getSnapshotVersion();
        try {
            snapshotVersionContext.setSnapshotVersion(bubbleId.getSnapshotVersion());
            T bubbleObject = storeService.getObject(bubbleId);
            bubbleObject.register(store);
            //noinspection UnnecessaryLocalVariable
            StoreEntry entry = storeCache.register(level, bubbleObject, bubbleObject);
            return entry;
        } finally {
            snapshotVersionContext.setSnapshotVersion(oldSnapshotVersion);
        }
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> Collection<StoreEntry> loadEntries(int level, Set<I> bubbleIds, boolean refresh) {
        Collection<StoreEntry> result = new ArrayList<>(bubbleIds.size());

        Map<SnapshotVersion, Collection<I>> idsForVersions = new HashMap<>();
        for (I bubbleId : bubbleIds) {
            Collection<I> ids = idsForVersions.get(bubbleId.getSnapshotVersion());
            if (ids == null) {
                ids = new HashSet<>();
                idsForVersions.put(bubbleId.getSnapshotVersion(), ids);
            }
            ids.add(bubbleId);
        }

        SnapshotVersion orgSnapshotVersion = snapshotVersionContext.getSnapshotVersion();
        try {
            for (Map.Entry<SnapshotVersion, Collection<I>> snapshotEntry : idsForVersions.entrySet()) {
                snapshotVersionContext.setSnapshotVersion(snapshotEntry.getKey());
                Collection<T> objects = storeService.getObjects(snapshotEntry.getValue());
                for (T bubbleObject : objects) {
                    StoreEntry entry = storeCache.register(level, bubbleObject);
                    result.add(entry);

                }
            }
        } finally {
            snapshotVersionContext.setSnapshotVersion(orgSnapshotVersion);
        }

        return result;
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> Collection<StoreEntry> loadEntriesIgnoreMissing(int level, Set<I> bubbleIds, boolean refresh) {
        Collection<StoreEntry> result = new ArrayList<>(bubbleIds.size());

        Map<SnapshotVersion, Collection<I>> idsForVersions = new HashMap<>();
        for (I bubbleId : bubbleIds) {
            Collection<I> ids = idsForVersions.get(bubbleId.getSnapshotVersion());
            if (ids == null) {
                ids = new HashSet<>();
                idsForVersions.put(bubbleId.getSnapshotVersion(), ids);
            }
            ids.add(bubbleId);
        }

        SnapshotVersion orgSnapshotVersion = snapshotVersionContext.getSnapshotVersion();
        try {
            for (Map.Entry<SnapshotVersion, Collection<I>> snapshotEntry : idsForVersions.entrySet()) {
                snapshotVersionContext.setSnapshotVersion(snapshotEntry.getKey());
                Collection<T> objects = storeService.getObjectsIgnoreMissing(snapshotEntry.getValue());
                for (T bubbleObject : objects) {
                    StoreEntry entry = storeCache.register(level, bubbleObject);
                    result.add(entry);

                }
            }
        } finally {
            snapshotVersionContext.setSnapshotVersion(orgSnapshotVersion);
        }

        return result;
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> StoreEntry lockEntry(int level, I bubbleId) {
        StoreEntry storeEntry = storeCache.get(bubbleId);
        if (storeEntry != null) {
            // Entry finnes, må sjekke om objekt er låst på underliggende nivå
            int lockLevel = storeEntry.calcLockLevelStartingFrom(level);
            if (lockLevel != level) {
                // Ikke allerede låst for level
                if (lockLevel >= 0) {
                    // Låst for underliggende level
                    BubbleObject derivedBubbleObject = storeEntry.getDerivedBubbleObject(level - 1);
                    BubbleObject copy = CopyHelper.copy(derivedBubbleObject);
                    copy.register(store);
                    storeEntry.setLocked(level, copy);
                } else {
                    // Ikke låst, hent seneste versjon fra server og erstatt eksisterende readOnly instans med versjon fra server hvis nyere.
                    BubbleObject lockedBubbleObject = storeService.lock(bubbleId);
                    int levelForExisting = storeEntry.getLevelForDerivedBubbleObject(level);
                    BubbleObject existingInstance = storeEntry.getDerivedBubbleObject(levelForExisting);
                    if (isNewerThanExisting(existingInstance, lockedBubbleObject)) {
                        lockedBubbleObject.register(store);
                        storeEntry.setBubbleObject(levelForExisting, lockedBubbleObject);
                    }
                    // Lager en kopi til bruk for oppdatering slik at opprinnelig instans fra serveren forblir uendret og kan brukes ifm caching
                    BubbleObject copy = CopyHelper.copy(lockedBubbleObject);
                    copy.register(store);
                    storeEntry.setLocked(level, copy);
                    storeEntry.setLockCreatedByLevel(level);
                }
            }
        } else {
            BubbleObject lockedBubbleObject = storeService.lock(bubbleId);
            storeEntry = storeCache.register(level, null, lockedBubbleObject);
            BubbleObject copy = CopyHelper.copy(lockedBubbleObject);
            copy.register(store);
            storeEntry.setLocked(level, copy);
            storeEntry.setLockCreatedByLevel(level);
        }
        return storeEntry;
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
                            storeService.unlock(bubbleId);
                            storeEntry.setLockCreatedByLevel(-1);
                        }
                        if (level>0) {
                            // Objekter på level 0 skal ikke kastes. På klienten vil disse aldri være endret.
                            storeEntry.setBubbleObject(level, null);
                        }
                        storeEntry.unlock(level);
                    }
                    break;
                default:
                    throw new ImplementationException("Object has been changed and can not be unlocked");
            }
        }
        return storeEntry;
    }

    @Override
    public void registerEntries(int level, BubbleTransfer<?> bubbleTransfer) {
        Set<BubbleId> lockedIdsFromTransfer = bubbleTransfer.getLockedIds();
        for (BubbleObject bubbleObjectFromTransfer : bubbleTransfer.getBubbleObjects().values()) {
            StoreEntry entry = storeCache.get(bubbleObjectFromTransfer.getId());
            if (entry == null) {
                entry = storeCache.register(level, null, bubbleObjectFromTransfer);
                if (lockedIdsFromTransfer.contains(bubbleObjectFromTransfer.getId())) {
                    entry.setBubbleObject(0, null);
                    entry.setLocked(level, bubbleObjectFromTransfer);
                    entry.setLockCreatedByLevel(level);
                }
                store.getRelationCache().cacheMaterialisedRelationsAndClearLocallyCachedValues(bubbleObjectFromTransfer, level);
            } else {
                int lockLevel = entry.calcLockLevelStartingFrom(level);
                if (lockLevel < 0) {
                    // Objekt i Store er ikke låst på klienten
                    if (lockedIdsFromTransfer.contains(bubbleObjectFromTransfer.getId())) {
                        bubbleObjectFromTransfer.register(store);
                        entry.setLocked(level, bubbleObjectFromTransfer);
                        entry.setLockCreatedByLevel(level);
                        store.getRelationCache().cacheMaterialisedRelationsAndClearLocallyCachedValues(bubbleObjectFromTransfer, level);
                    } else {
                        // Hverken objekt i Store eller fra transfer er låst
                        int derivedLevel = entry.getLevelForDerivedBubbleObject(level);
                        int versionComparison = versionComparator.compare(entry.getDerivedBubbleObject(derivedLevel), bubbleObjectFromTransfer);
                        if (versionComparison == -1) {
                            // Objekt fra transfer er nyest
                            bubbleObjectFromTransfer.register(store);
                            entry.setBubbleObject(derivedLevel, bubbleObjectFromTransfer);
                            store.getRelationCache().cacheMaterialisedRelationsAndClearLocallyCachedValues(bubbleObjectFromTransfer, derivedLevel);
                        } else if (versionComparison == 0) {
                            // Objekt i store og transfer har samme versjon. Legg inn materialiserte relasjoner fra transfer hvis de finnes
                            store.getRelationCache().cacheMaterialisedRelationsAndClearLocallyCachedValues(bubbleObjectFromTransfer, derivedLevel);
                        }
                    }
                }
                // Hvis objektet i Store er allerede låst, så ignoreres den innkommende kopien fra transfer
            }
        }
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> boolean evictEntry(int level, I bubbleId) {
        evictFromReadCache(bubbleId);
        StoreEntry entry = storeCache.get(bubbleId);
        if (entry != null && entry.getDerivedState(level) == StoreEntryState.UNCHANGED && entry.calcLockLevelStartingFrom(level) == -1) {
            storeCache.remove(bubbleId);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean evictAllEntries(int level) {
        evictAllFromReadCache();
        boolean allWasEvicted = true;
        final Iterator<StoreEntry> iterator = storeCache.values().iterator();
        while (iterator.hasNext()) {
            final StoreEntry entry = iterator.next();
            if (entry != null && entry.getDerivedState(level) == StoreEntryState.UNCHANGED && entry.calcLockLevelStartingFrom(level) == -1) {
                iterator.remove();
            } else {
                allWasEvicted = false;
            }
        }
        return allWasEvicted;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public <T extends BubbleObject> void ensureFullyLoaded(T bubbleObject) {
        // No-op, bobler er alltid fullt lastet på klient
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> List<I> getVersions(I id, SnapshotVersion start, SnapshotVersion end) {
        return  storeService.getVersions(id, start, end);
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> Map<I, List<I>> getVersionsForList(Collection<I> ids, SnapshotVersion start, SnapshotVersion end) {
        return storeService.getVersionsForList(ids, start, end);
    }

    @Override
    public StoreUnitOfWork beginUnitOfWork() {
        return new StoreUnitOfWorkClient(level + 1, this, storeCache, store);
    }

    /**
     * På klienten vil level 0 inneholde det opprindelige objektet i uforandret state eller null dersom det er nytt
     */
    @Override
    public BubbleObject getPersistedBubbleObjectForLocked(StoreEntry storeEntry) {
        Preconditions.checkState(storeEntry.isLocked(), "Entry må være låst: %s", storeEntry);
        if (storeEntry.getState(0) == StoreEntryState.UNCHANGED) {
            return Preconditions.checkNotNull(storeEntry.getBubbleObject(0), "Entry.getBubbleObject[0] kan ikke være null: %s", storeEntry);
        } else {
            return storeEntry.getBubbleObject(0);
        }
    }

    private boolean isNewerThanExisting(BubbleObject existingBubbleObject, BubbleObject newBubbleObject) {
        return versionComparator.compare(existingBubbleObject, newBubbleObject) == -1;
    }

    private <T extends BubbleObject, I extends BubbleId<? extends T>> void evictFromReadCache(I bubbleId) {
        if (readCache!=null) readCache.evict(bubbleId);
    }

    private void evictAllFromReadCache() {
        if (readCache!=null) readCache.evictAll();
    }

    @Nullable
    public StoreClientReadCache getReadCache() {
        return readCache;
    }
}
