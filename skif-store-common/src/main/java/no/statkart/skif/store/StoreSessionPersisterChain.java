package no.statkart.skif.store;


import com.sun.xml.internal.bind.v2.runtime.unmarshaller.LocatorEx;
import sun.org.mozilla.javascript.internal.NativeObject;

import java.util.*;

/**
 * Avsluttende StoreSession-kjedeledd på server som henter og skriver objekter til underleggende {@link StorePersister}.
 * StoreSessionPersisterChain anvender et StorePersisterStrategy objekt til å velge hvilken StorePersister som skal brukes
 * for hver AbstractBubbleId. På den måte er det mulig å bruke flere StorePersister objekter samtidig.
 *
 * @author Henrik Fredholm
 * @since 0.3
 */
public class StoreSessionPersisterChain implements StoreSessionReadChain, StoreSessionUpdateChain {
    private final StorePersisterStrategy persisterStrategy;
    private final LockerStrategy lockerStrategy;
    private final Map<Class<BubbleId>, List<BubbleId>> idsPerPersister = new HashMap<Class<BubbleId>, List<BubbleId>>();
    private final Map<Class<BubbleId>, List<BubbleId>> idsPerLocker = new HashMap<Class<BubbleId>, List<BubbleId>>();

    public StoreSessionPersisterChain(StorePersisterStrategy persisterStrategy, LockerStrategy lockerStrategy) {
        this.persisterStrategy = persisterStrategy;
        this.lockerStrategy = lockerStrategy;
    }

    @Override
    public StoreSessionReadChain setNextInReadChain(StoreSessionReadChain next) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T extends BubbleObject> StoreEntry<T> register(T bubbleObject) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void init(StoreCache storeCache) {
        // No-op
    }

    @Override
    public void clear() {
        // No-op
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> StoreEntry<T> get(I bubbleId) {
        StorePersister<T, I> persister = persisterStrategy.getPersister(bubbleId);
        T bubble = persister.get(bubbleId);
        return new StoreEntry<T>(bubble);
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> Collection<StoreEntry<T>> get(Collection<I> bubbleIds) {
        Map<StorePersister, Map<SnapshotVersion, List<BubbleId>>> bubbleIdsForSnapshotMap = classifyIds(bubbleIds);
        List<StoreEntry<T>> result = new ArrayList<StoreEntry<T>>(bubbleIds.size());
        for (Map.Entry<StorePersister, Map<SnapshotVersion, List<BubbleId>>> bubbleIdsForSnapshotEntry : bubbleIdsForSnapshotMap.entrySet()) {
            StorePersister persister = bubbleIdsForSnapshotEntry.getKey();
            Map<SnapshotVersion, Collection<T>> bubblesForSnapshotMap = persister.get(bubbleIdsForSnapshotEntry.getValue());
            for (Collection<T> bubblesForSnapshot : bubblesForSnapshotMap.values()) {
                for (T bubble : bubblesForSnapshot) {
                    result.add(new StoreEntry<T>(bubble));
                }
            }
        }
        return result;
    }

    private Map<StorePersister, Map<SnapshotVersion, List<BubbleId>>> classifyIds(Collection<? extends BubbleId> bubbleIds) {
        Object lastClassifier = null;
        List<BubbleId> lastList = null;
        SnapshotVersion lastSnapshotVersion = null;
        Map<StorePersister, Map<SnapshotVersion, List<BubbleId>>> map = new HashMap<StorePersister, Map<SnapshotVersion, List<BubbleId>>>();
        for (BubbleId bubbleId : bubbleIds) {
            StorePersister persister = persisterStrategy.getPersister(bubbleId);
            SnapshotVersion snapshotVersion = bubbleId.getSnapshotVersion();
            if (persister != lastClassifier && !snapshotVersion.equals(lastSnapshotVersion)) {
                lastClassifier = persister;
                lastSnapshotVersion = snapshotVersion;
                Map<SnapshotVersion, List<BubbleId>> bubbleIdsForSnapshotVersionMap = map.get(persister);
                if (bubbleIdsForSnapshotVersionMap == null) {
                    bubbleIdsForSnapshotVersionMap = new HashMap<SnapshotVersion, List<BubbleId>>();
                    map.put(persister, bubbleIdsForSnapshotVersionMap);
                }
                lastList = bubbleIdsForSnapshotVersionMap.get(snapshotVersion);
                if (lastList == null) {
                    lastList = new ArrayList<BubbleId>();
                    bubbleIdsForSnapshotVersionMap.put(bubbleId.getSnapshotVersion(), lastList);
                }
            }
            lastList.add(bubbleId);
        }
        return map;
    }

    @Override
    public StoreSessionUpdateChain setNextInWriteChain(StoreSessionUpdateChain next) {
        throw new UnsupportedOperationException();

    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> StoreEntry<T> lock(I bubbleId) {
        return get(bubbleId);
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> Collection<StoreEntry<T>> lock(Collection<I> bubbleIds) {
        return get(bubbleIds);
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> boolean isLocked(I bubbleId) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public <T extends BubbleObject> StoreEntry<T> registerLocked(T bubbleObject) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T extends BubbleObject> StoreEntry<T> registerNew(T bubbleObject) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T extends BubbleObject> StoreEntry<T> registerUpdated(T bubbleObject) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> void registerDeleted(I bubbleId) {
        throw new UnsupportedOperationException();
    }
}
