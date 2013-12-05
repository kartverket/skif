package no.statkart.skif.store;

import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.service.ServiceContext;
import no.statkart.skif.store.service.StoreService;
import no.statkart.skif.util.CopyHelper;

import java.util.*;

/**
 * StoreSession som utgjør avsluttende ledd på klienten. Klassen anvender en {@link StoreService} for å hente
 * objekter fra server
 *
 * @author Henrik Fredholm
 * @since 2.1
 */
public class StoreSessionClient extends AbstractStoreSession {
    private final StoreService storeService;
    private final ServiceContext serviceContext;


    public StoreSessionClient(StoreService storeService, ServiceContext serviceContext) {
        this(storeService, serviceContext, new StoreCache());
    }

    public StoreSessionClient(StoreService storeService, ServiceContext serviceContextProvider, StoreCache storeCache) {
        super(0, storeCache);
        this.storeService = storeService;
        this.serviceContext = serviceContextProvider;
    }

    protected boolean isLocked(StoreEntry storeEntry) {
        return storeEntry.isLocked();
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> StoreEntry insertEntry(int level, T bubbleObject) {
        if (level == 0) {
            throw new ImplementationException("Insert on client must be done in a UnitOfWork and sent to server via getUnitOfWorkTransfer()");
        } else {
            return super.insertEntry(level, bubbleObject);
        }
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> StoreEntry updateEntry(int level, T bubbleObject) {
        if (level == 0) {
            throw new ImplementationException("Update on client must be done in a UnitOfWork and sent to server via getUnitOfWorkTransfer()");
        } else {
            return super.updateEntry(level, bubbleObject);
        }
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> StoreEntry deleteEntry(int level, T bubbleObject) {
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
        SnapshotVersion oldSnapshotVersion = serviceContext.getSnapshotVersion();
        try {
            serviceContext.setSnapshotVersion(bubbleId.getSnapshotVersion());
            T bubbleObject = storeService.getObject(bubbleId);
            bubbleObject.register(store);
            StoreEntry entry = storeCache.register(level, bubbleObject, bubbleObject);
            return entry;
        } finally {
            serviceContext.setSnapshotVersion(oldSnapshotVersion);
        }
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> Collection<StoreEntry> loadEntries(int level, Set<I> bubbleIds, boolean refresh) {
        Collection<StoreEntry> result = new ArrayList<StoreEntry>(bubbleIds.size());

        Map<SnapshotVersion, Collection<I>> idsForVersions = new HashMap<SnapshotVersion, Collection<I>>();
        for (I bubbleId : bubbleIds) {
            Collection<I> ids = idsForVersions.get(bubbleId.getSnapshotVersion());
            if (ids == null) {
                ids = new HashSet<I>();
                idsForVersions.put(bubbleId.getSnapshotVersion(), ids);
            }
            ids.add(bubbleId);
        }

        SnapshotVersion orgSnapshotVersion = serviceContext.getSnapshotVersion();
        try {
            for (Map.Entry<SnapshotVersion, Collection<I>> snapshotEntry : idsForVersions.entrySet()) {
                serviceContext.setSnapshotVersion(snapshotEntry.getKey());
                Collection<T> objects = storeService.getObjects(snapshotEntry.getValue());
                for (T bubbleObject : objects) {
                    StoreEntry entry = storeCache.register(level, bubbleObject);
                    result.add(entry);

                }
            }
        } finally {
            serviceContext.setSnapshotVersion(orgSnapshotVersion);
        }

        return result;
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> Collection<StoreEntry> loadEntriesIgnoreMissing(int level, Set<I> bubbleIds, boolean refresh) {
        Collection<StoreEntry> result = new ArrayList<StoreEntry>(bubbleIds.size());

        Map<SnapshotVersion, Collection<I>> idsForVersions = new HashMap<SnapshotVersion, Collection<I>>();
        for (I bubbleId : bubbleIds) {
            Collection<I> ids = idsForVersions.get(bubbleId.getSnapshotVersion());
            if (ids == null) {
                ids = new HashSet<I>();
                idsForVersions.put(bubbleId.getSnapshotVersion(), ids);
            }
            ids.add(bubbleId);
        }

        SnapshotVersion orgSnapshotVersion = serviceContext.getSnapshotVersion();
        try {
            for (Map.Entry<SnapshotVersion, Collection<I>> snapshotEntry : idsForVersions.entrySet()) {
                serviceContext.setSnapshotVersion(snapshotEntry.getKey());
                Collection<T> objects = storeService.getObjectsIgnoreMissing(snapshotEntry.getValue());
                for (T bubbleObject : objects) {
                    StoreEntry entry = storeCache.register(level, bubbleObject);
                    result.add(entry);

                }
            }
        } finally {
            serviceContext.setSnapshotVersion(orgSnapshotVersion);
        }

        return result;
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> StoreEntry lockEntry(int level, I bubbleId) {
        StoreEntry storeEntry = storeCache.get(bubbleId);
        if (storeEntry != null) {
            // Entry finnes, må sjekk om objekt er låst på underliggende nivå
            int lockLevel = storeEntry.calcLockLevelStartingFrom(level);
            if (lockLevel == level) {
                // Allerede låst for level
            } else if (lockLevel >= 0) {
                // Låst for underliggende level
                BubbleObject derivedBubbleObject = storeEntry.getDerivedBubbleObject(level - 1);
                BubbleObject copy = CopyHelper.copy(derivedBubbleObject);
                copy.register(store);
                storeEntry.setLocked(level, copy);
            } else {
                // Ikke låst, hent fra server
                BubbleObject lockedBubbleObject = storeService.lock(bubbleId);
                // TODO: fjern allerede leste versjoner hvis timestamp/versjon er eldre
                storeEntry.setLocked(level, lockedBubbleObject);
                storeEntry.setLockCreatedByLevel(level);
            }
        } else {
            BubbleObject lockedBubbleObject = storeService.lock(bubbleId);
            storeEntry = storeCache.register(level, null, lockedBubbleObject);
            // Ønsker ikke å ta en kopi av objektet så vi setter level 0 til null i stedet
            storeEntry.setBubbleObject(0, null);
            storeEntry.setLocked(level, lockedBubbleObject);
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
                        storeEntry.setBubbleObject(level, null);
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
    public void registerEntries(int level, BubbleTransfer bubbleTransfer) {
        for (Object object : bubbleTransfer.getObjects().values()) {
            BubbleObject bubbleObject = (BubbleObject) object;
            StoreEntry entry = storeCache.get(bubbleObject.getId());
            if (entry == null) {
                storeCache.register(level, bubbleObject, bubbleObject);
            } else {
                // TODO: sjekk of object i transfer er nyere/låst
            }
        }
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> boolean evictEntry(int level, I bubbleId) {
        StoreEntry entry = storeCache.get(bubbleId);
        if (entry != null && entry.getDerivedState(level) == StoreEntryState.UNCHANGED && entry.calcLockLevelStartingFrom(level) == -1) {
            storeCache.remove(bubbleId);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> boolean evictAllEntries(int level) {
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
        return storeService.getVersions(id, start, end);
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> Map<I, List<I>> getVersionsForList(Collection<I> ids, SnapshotVersion start, SnapshotVersion end) {
        return storeService.getVersionsForList(ids, start, end);
    }

    @Override
    public StoreUnitOfWork beginUnitOfWork() {
        return new StoreUnitOfWorkClient(level + 1, this, storeCache, store);
    }
}
