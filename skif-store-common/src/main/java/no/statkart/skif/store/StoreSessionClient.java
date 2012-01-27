package no.statkart.skif.store;

import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.exception.NotImplementedException;
import no.statkart.skif.exception.NotLockedException;

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
    public <T extends BubbleObject, I extends BubbleId<? extends T>> boolean isLocked(I bubbleId) {
        StoreEntry storeEntry = storeCache.get(bubbleId);
        return (storeEntry!=null && isLocked(storeEntry));
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> StoreEntry loadEntry(int level, I bubbleId, boolean refresh) {
        T bubbleObject = storeService.getObject(bubbleId);
        bubbleObject.register(store);
        // TODO..
        StoreEntry entry = storeCache.registerUnchanged(level, bubbleObject);
        return entry;
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> Collection<StoreEntry> loadEntries(int level, Set<I> bubbleIds, boolean refresh) {
        // TODO: Optimize for bulk access
        Collection<StoreEntry> result = new ArrayList<StoreEntry>(bubbleIds.size());
        for (I bubbleId : bubbleIds) {
            result.add(loadEntry(level, bubbleId, false));
        }
        return result;
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> StoreEntry lockEntry(int level, I bubbleId) {
        // TODO
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> StoreEntry unlockEntry(int level, I bubbleId) {
        // TODO
        return null;  //To change body of implemented methods use File | Settings | File Templates.
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
