package no.statkart.skif.store;

import com.google.common.base.Preconditions;
import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.store.service.LockService;
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
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * StoreSession som utgjør avsluttende ledd på klienten. Klassen anvender en {@link StoreService} for å hente
 * objekter fra server
 *
 * @author Henrik Fredholm
 * @since 2.1
 */
public class StoreSessionClient extends AbstractStoreSession {
    private final StoreService storeService;
    private final LockService lockService;
    private final SnapshotVersionContext snapshotVersionContext;
    @Nullable
    private final StoreClientReadCache readCache;
    @Nullable
    private final Comparator<BubbleObject> versionComparator;


    public StoreSessionClient(StoreService storeService, LockService lockService, SnapshotVersionContext snapshotVersionContext) {
        this(storeService, lockService, snapshotVersionContext, new StoreCache());
    }

    public StoreSessionClient(StoreService storeService, LockService lockService, SnapshotVersionContext snapshotVersionContext, StoreCache storeCache) {
        this(storeService, lockService, snapshotVersionContext, storeCache, null);
    }

    public StoreSessionClient(StoreService storeService, LockService lockService, SnapshotVersionContext snapshotVersionContext, StoreCache storeCache, Comparator<BubbleObject> versionComparator) {
        this(storeService, lockService, snapshotVersionContext, storeCache, versionComparator, null);
    }

    public StoreSessionClient(StoreService storeService, LockService lockService, SnapshotVersionContext snapshotVersionContext, StoreCache storeCache, @Nullable Comparator<BubbleObject> versionComparator, @Nullable StoreClientReadCache readCache) {
        super(0, storeCache);
        this.snapshotVersionContext = snapshotVersionContext;
        this.versionComparator = versionComparator;
        this.readCache = readCache;
        if (readCache != null) {
            StoreServiceWithReadCache cache = new StoreServiceWithReadCache(storeService, lockService, readCache);
            this.storeService = cache;
            this.lockService = cache;
        } else {
            this.storeService = storeService;
            this.lockService = lockService;
        }
    }

    @Override
    public <T extends BubbleObject> T lock(BubbleId<? extends T> bubbleId) {
        if (level==0) {
            throw new ImplementationException("Lock on client must be done in a UnitOfWork");
        }
        return super.lock(bubbleId);
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> void lock(Collection<I> bubbleIds, Collection<T> bubbleObjects) {
        if (level==0) {
            throw new ImplementationException("Lock on client must be done in a UnitOfWork");
        }
        super.lock(bubbleIds, bubbleObjects);
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
    public void commitUnitOfWork(Map<BubbleId<?>, StoreEntry> modifiedAndLocked) {
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
                    BubbleObject lockedBubbleObject = lockService.lock(bubbleId);
                    int levelForExisting = storeEntry.getLevelForDerivedBubbleObject(level);
                    BubbleObject existingInstance = storeEntry.getDerivedBubbleObject(levelForExisting);
                    if (replaceVersion(existingInstance, lockedBubbleObject)) {
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
            BubbleObject lockedBubbleObject = lockService.lock(bubbleId);
            storeEntry = storeCache.register(level, null, lockedBubbleObject);
            BubbleObject copy = CopyHelper.copy(lockedBubbleObject);
            copy.register(store);
            storeEntry.setLocked(level, copy);
            storeEntry.setLockCreatedByLevel(level);
        }
        return storeEntry;
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> Collection<StoreEntry> lockEntries(int level, Set<I> bubbleIds) {
        Collection<StoreEntry> result = new ArrayList<>(bubbleIds.size());

        List<StoreEntry> unlockedEntries = new ArrayList<>();
        List<I> missing = new ArrayList<>();

        for (I bubbleId : bubbleIds) {
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
                        result.add(storeEntry);
                    } else {
                        unlockedEntries.add(storeEntry);
                    }
                }
            } else {
                missing.add(bubbleId);
            }
        }

        Set<BubbleId<?>> fetchIds = Stream.concat(unlockedEntries.stream().map(StoreEntry::getId), missing.stream()).collect(Collectors.toSet());
        Map<? extends BubbleId<?>, BubbleObject> lockedObjects = lockService.lockForList(fetchIds).stream().collect(Collectors.toMap(BubbleObject::getId, Function.identity()));

        for (StoreEntry storeEntry : unlockedEntries) {
            // Ikke låst. Erstatt eksisterende readOnly instans med hent seneste versjon fra server hvis nyere.
            BubbleObject lockedBubbleObject = lockedObjects.get(storeEntry.getId());
            int levelForExisting = storeEntry.getLevelForDerivedBubbleObject(level);
            BubbleObject existingInstance = storeEntry.getDerivedBubbleObject(levelForExisting);
            if (replaceVersion(existingInstance, lockedBubbleObject)) {
                lockedBubbleObject.register(store);
                storeEntry.setBubbleObject(levelForExisting, lockedBubbleObject);
            }
            // Lager en kopi til bruk for oppdatering slik at opprinnelig instans fra serveren forblir uendret og kan brukes ifm caching
            BubbleObject copy = CopyHelper.copy(lockedBubbleObject);
            copy.register(store);
            storeEntry.setLocked(level, copy);
            storeEntry.setLockCreatedByLevel(level);
            result.add(storeEntry);
        }

        for (I bubbleId : missing) {
            BubbleObject lockedBubbleObject = lockedObjects.get(bubbleId);
            StoreEntry storeEntry = storeCache.register(level, null, lockedBubbleObject);
            BubbleObject copy = CopyHelper.copy(lockedBubbleObject);
            copy.register(store);
            storeEntry.setLocked(level, copy);
            storeEntry.setLockCreatedByLevel(level);
            result.add(storeEntry);
        }

        return result;
    }

    @Override
    public StoreEntry unlockEntry(int level, BubbleId<?> bubbleId) {
        StoreEntry storeEntry = storeCache.get(bubbleId);
        if (storeEntry != null) {
            switch (storeEntry.getDerivedState(level)) {
                case NULL:
                case UNCHANGED:
                    if (isLocked(storeEntry)) {
                        if (storeEntry.getLockCreatedByLevel() == level) {
                            lockService.unlock(bubbleId);
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
        } else {
            lockService.unlock(bubbleId);
        }
        return storeEntry;
    }

    @Override
    public Collection<StoreEntry> unlockEntries(int level, Collection<? extends BubbleId<?>> bubbleIds) {
        List<StoreEntry> entries = new ArrayList<>(bubbleIds.size());
        Set<BubbleId<?>> unlockIds = new HashSet<>(bubbleIds.size());

        // Gjør dette i tre trinn ettersom hvor sannsynlig det er at de feiler, slik at ingen trinn skal bli bare delvis gjennomført.
        // Trinn 1 (denne kan ende opp med å bare bli delvis gjennomført, men den er uten sideeffekter)
        for (BubbleId<?> bubbleId : bubbleIds) {
            StoreEntry storeEntry = storeCache.get(bubbleId);
            if (storeEntry != null) {
                switch (storeEntry.getDerivedState(level)) {
                    case NULL:
                    case UNCHANGED:
                        if (isLocked(storeEntry)) {
                            if (storeEntry.getLockCreatedByLevel() == level) {
                                unlockIds.add(bubbleId);
                            }
                        }
                        entries.add(storeEntry);
                        break;
                    default:
                        throw new ImplementationException("Object has been changed and can not be unlocked");
                }
            } else {
                unlockIds.add(bubbleId);
            }
        }

        // Trinn 2
        if (!unlockIds.isEmpty()) {
            lockService.unlockForList(unlockIds);
        }

        // Trinn 3 (dette skal være ren bokføring)
        for (StoreEntry storeEntry : entries) {
            if (isLocked(storeEntry)) {
                if (storeEntry.getLockCreatedByLevel() == level) {
                    storeEntry.setLockCreatedByLevel(-1);
                }
                if (level>0) {
                    // Objekter på level 0 skal ikke kastes. På klienten vil disse aldri være endret.
                    storeEntry.setBubbleObject(level, null);
                }
                storeEntry.unlock(level);
            }
        }

        return entries;
    }

    @Override
    public Collection<StoreEntry> registerEntries(int level, Transfer<?> transfer) {
        Set<BubbleId> lockedIdsFromTransfer = transfer.getLockedIds();
        if (level==0 && !lockedIdsFromTransfer.isEmpty()) {
            throw new ImplementationException("Lock on client must be done in a UnitOfWork");
        }
        Set<StoreEntry> lockedEntriesNotAlreadyLocked = new HashSet<>(lockedIdsFromTransfer.size());
        for (BubbleObject bubbleObjectFromTransfer : transfer.getBubbleObjects().values()) {
            StoreEntry entry = storeCache.get(bubbleObjectFromTransfer.getId());
            if (entry == null) {
                entry = storeCache.register(level, bubbleObjectFromTransfer, bubbleObjectFromTransfer);
                if (lockedIdsFromTransfer.contains(bubbleObjectFromTransfer.getId())) {
                    BubbleObject copy = CopyHelper.copy(bubbleObjectFromTransfer);
                    copy.register(store);
                    entry.setLocked(level, copy);
                    entry.setLockCreatedByLevel(level);
                    lockedEntriesNotAlreadyLocked.add(entry);
                }
                store.getRelationCache().cacheMaterialisedRelationsAndClearLocallyCachedValues(bubbleObjectFromTransfer, level);
            } else {
                // Entry finnes, må sjekke om objekt er låst på underliggende nivå
                int lockLevel = entry.calcLockLevelStartingFrom(level);
                if (lockLevel < 0) {
                    // Objekt ikke låst i klienten. Må sjekke om objekt i transfer skal erstatte eksisterende readonly instans og om objektet nå skal være låst
                    int derivedLevel = entry.getLevelForDerivedBubbleObject(level);
                    int versionComparison = selectVersion(entry.getDerivedBubbleObject(derivedLevel), bubbleObjectFromTransfer);
                    if (versionComparison < 0) {
                        // Objekt fra transfer skal brukes
                        bubbleObjectFromTransfer.register(store);
                        entry.setBubbleObject(derivedLevel, bubbleObjectFromTransfer);
                        store.getRelationCache().cacheMaterialisedRelationsAndClearLocallyCachedValues(bubbleObjectFromTransfer, derivedLevel);
                    } else if (versionComparison == 0) {
                        // Objekt i store og transfer er like. Legg inn materialiserte relasjoner fra transfer hvis de finnes
                        store.getRelationCache().cacheMaterialisedRelationsAndClearLocallyCachedValues(bubbleObjectFromTransfer, derivedLevel);
                    }
                    if (lockedIdsFromTransfer.contains(bubbleObjectFromTransfer.getId())) {
                        // Lager en kopi til bruk for oppdatering slik at opprinnelig instans fra serveren forblir uendret og kan brukes ifm caching
                        BubbleObject copy = CopyHelper.copy(bubbleObjectFromTransfer);
                        copy.register(store);
                        entry.setLocked(level, copy);
                        entry.setLockCreatedByLevel(level);
                        lockedEntriesNotAlreadyLocked.add(entry);
                    }
                }
                // Hvis objektet i Store er allerede låst, så ignoreres den innkommende kopien fra transfer
            }
        }
        return lockedEntriesNotAlreadyLocked;
    }

    @Override
    public boolean evictEntry(int level, BubbleId<?> bubbleId) {
        evictFromReadCache(bubbleId);
        StoreEntry entry = storeCache.get(bubbleId);
        if (entry==null) {
            return true;
        } else if (entry.getDerivedState(level) == StoreEntryState.UNCHANGED && entry.calcLockLevelStartingFrom(level) == -1) {
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
    public <T extends BubbleObject, I extends BubbleId<? extends T>> Map<I, List<I>> getVersionsForList(Collection<? extends I> ids, SnapshotVersion start, SnapshotVersion end) {
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

    /**
     * Returnerer true hvis {@code existingBubbleObject} skal erstattes med {@code incommingBubbleObject}
     */
    private boolean replaceVersion(BubbleObject existingBubbleObject, BubbleObject incommingBubbleObject) {
        return selectVersion(existingBubbleObject, incommingBubbleObject) < 0;
    }

    /**
     * Hvis {@link #versionComparator} er satt brukes returverdien fra denne. Ellers returneres {@code -1} slik at {@code
     * incommingBubbleObject} alltid vil bli brukt.
     * @return  Et negativ tall hvis {@code incommeingBubbleObject } skal brukes, 0 hvis de er like, og et positiv tall
     * hvis {@code existingBubbleObject} skal brukes. Tilsvarer {@link Comparator#compare(Object, Object)}).
     */
    private int selectVersion(BubbleObject existingBubbleObject, BubbleObject incommingBubbleObject) {
        return versionComparator == null ? -1 : versionComparator.compare(existingBubbleObject, incommingBubbleObject);
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

    @Override
    public UnitOfWorkTransfer getSnapshot() {
        throw new ImplementationException("Not in UnitOfWork");
    }

    @Override
    public UnitOfWorkTransfer getSessionSnapshot() {
        throw new ImplementationException("Not in UnitOfWork");
    }
}
