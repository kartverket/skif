package no.statkart.skif.store;

import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.exception.NotImplementedException;
import no.statkart.skif.exception.NotLockedException;
import no.statkart.skif.util.CopyHelper;

import java.util.*;

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


    public StoreSessionClient(StoreService storeService) {
        this(storeService, new StoreCache());
    }

    public StoreSessionClient(StoreService storeService, StoreCache storeCache) {
        super(0, storeCache);
        this.storeService = storeService;
    }

    protected boolean isLocked(StoreEntry storeEntry) {
        return storeEntry.isLocked();
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> StoreEntry insertEntry(int level, T bubbleObject) {
        if (level==0) {
            throw new ImplementationException("Insert på klient må gjøres i en StoreUnitOfWork og sendes til server via getUnitOfWorkTransfer");
        }  else {
            return super.insertEntry(level, bubbleObject);
        }
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> StoreEntry updateEntry(int level, T bubbleObject) {
        if (level==0) {
            throw new ImplementationException("Update på klient må gjøres i en StoreUnitOfWork og sendes til server via getUnitOfWorkTransfer");
        }  else {
            return super.updateEntry(level, bubbleObject);
        }
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> StoreEntry deleteEntry(int level, T bubbleObject) {
        if (level==0) {
            throw new ImplementationException("Delete på klient må gjøres i en StoreUnitOfWork og sendes til server via getUnitOfWorkTransfer");
        }  else {
            return super.deleteEntry(level, bubbleObject);
        }
    }

    @Override
    public void commitUnitOfWork(Map<BubbleId<?>, StoreEntry> modified) {
        throw new ImplementationException("Commit av UnitOfWork direkte mot server støttes ikke, men må gjøres via getUnitOfWorkTransfer");
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> boolean isLocked(I bubbleId) {
        StoreEntry storeEntry = storeCache.get(bubbleId);
        return (storeEntry!=null && isLocked(storeEntry));
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> StoreEntry loadEntry(int level, I bubbleId, boolean refresh) {
        T bubbleObject = storeService.getObject(bubbleId);
        bubbleObject.register(store);
        StoreEntry entry = storeCache.register(level, bubbleObject, bubbleObject);
        return entry;
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> Collection<StoreEntry> loadEntries(int level, Set<I> bubbleIds, boolean refresh) {
        Collection<StoreEntry> result = new ArrayList<StoreEntry>(bubbleIds.size());
        Collection<T> objects = storeService.getObjects(bubbleIds);
        for (T bubbleObject : objects) {
            StoreEntry entry = storeCache.register(level, bubbleObject);
            result.add(entry);

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
            storeEntry = storeCache.register(level, lockedBubbleObject, lockedBubbleObject);
            storeEntry.setLocked(level);
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
                    throw new ImplementationException("Objekt har blitt endret og kan ikke låses opp");
            }
        }
        return storeEntry;
    }

    @Override
    public void registerEntries(int level,  BubbleTransfer bubbleTransfer) {
        for (Object object : bubbleTransfer.getObjects().values()) {
            BubbleObject bubbleObject = (BubbleObject) object;
            StoreEntry entry = storeCache.get(bubbleObject.getId());
            if (entry==null) {
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
    public <T extends BubbleObject> void ensureFullyLoaded(T bubbleObject) {
        // No-op, bobler er alltid fullt lastet på klient
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> List<I> getVersions(I id, SnapshotVersion start, SnapshotVersion end) {
        return storeService.getVersions(id, start, end);
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> Map<I, List<I>> getVersionsForList(List<I> ids, SnapshotVersion start, SnapshotVersion end) {
        return storeService.getVersionsForList(ids, start, end);
    }

    @Override
    public StoreUnitOfWork beginUnitOfWork() {
        return new StoreUnitOfWorkClient(level + 1, this, storeCache, store);
    }
}
