package no.statkart.skif.store2;


import java.util.*;

/**
 * Avsluttende StoreSession-kjedeledd på server som henter og skriver objekter til underleggende {@link no.statkart.skif.store2.StorePersister2}.
 * StoreSessionPersisterChain anvender et StorePersisterStrategy2 objekt til å velge hvilken StorePersister2 som skal brukes
 * for hver AbstractBubbleId. På den måte er det mulig å bruke flere StorePersister2 objekter samtidig.
 *
 * @author Henrik Fredholm
 * @since 0.3
 */
public class StoreSessionPersisterChain2 implements StoreSessionReadChain2, StoreSessionUpdateChain2 {
    private final StorePersisterStrategy2 persisterStrategy;
    private final LockerStrategy2 lockerStrategy;
    private final Map<Class<BubbleId2>, List<BubbleId2>> idsPerPersister = new HashMap<Class<BubbleId2>, List<BubbleId2>>();
    private final Map<Class<BubbleId2>, List<BubbleId2>> idsPerLocker = new HashMap<Class<BubbleId2>, List<BubbleId2>>();

    public StoreSessionPersisterChain2(StorePersisterStrategy2 persisterStrategy, LockerStrategy2 lockerStrategy) {
        this.persisterStrategy = persisterStrategy;
        this.lockerStrategy = lockerStrategy;
    }

    @Override
    public StoreSessionReadChain2 setNextInReadChain(StoreSessionReadChain2 next) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T extends BubbleObject2> StoreEntry2<T> register(T bubbleObject) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void init(StoreCache2 storeCache) {
        // No-op
    }

    @Override
    public void clear() {
        // No-op
    }

    @Override
    public <T extends BubbleObject2, I extends BubbleId2<? extends T>> StoreEntry2<T> get(I bubbleId) {
        StorePersister2<T, I> persister = persisterStrategy.getPersister(bubbleId);
        T bubble = persister.get(bubbleId);
        return new StoreEntry2<T>(bubble);
    }

    @Override
    public <T extends BubbleObject2, I extends BubbleId2<? extends T>> Collection<StoreEntry2<T>> get(Collection<I> bubbleIds) {
        Map<StorePersister2, List<BubbleId2>> map = classifyIds(bubbleIds);
        List<StoreEntry2<T>> result =  new ArrayList<StoreEntry2<T>>(bubbleIds.size());
        for (Map.Entry<StorePersister2, List<BubbleId2>> entry : map.entrySet()) {
            StorePersister2 persister = entry.getKey();
            Collection<T> objects = persister.get(entry.getValue());
            for (T object : objects) {
                result.add(new StoreEntry2<T>(object));
            }
        }
        return result;
    }

    private Map<StorePersister2, List<BubbleId2>> classifyIds(Collection<? extends BubbleId2> bubbleIds) {
        Object lastClassifier = null;
        List<BubbleId2> lastList = null;
        Map<StorePersister2, List<BubbleId2>> map = new HashMap<StorePersister2, List<BubbleId2>>();
        for (BubbleId2 bubbleId : bubbleIds) {
            StorePersister2 persister = persisterStrategy.getPersister(bubbleId);
            if (lastClassifier != persister) {
                lastClassifier = persister;
                lastList = map.get(persister);
                if (lastList == null) {
                    lastList = new ArrayList<BubbleId2>();
                    map.put(persister, lastList);
                }
            }
            lastList.add(bubbleId);
        }
        return map;
    }

    @Override
    public StoreSessionUpdateChain2 setNextInWriteChain(StoreSessionUpdateChain2 next) {
        throw new UnsupportedOperationException();

    }

    @Override
    public <T extends BubbleObject2, I extends BubbleId2<? extends T>> StoreEntry2<T> lock(I bubbleId) {
        return get(bubbleId);
    }

    @Override
    public <T extends BubbleObject2, I extends BubbleId2<? extends T>> Collection<StoreEntry2<T>> lock(Collection<I> bubbleIds) {
        return get(bubbleIds);
    }

    @Override
    public <T extends BubbleObject2, I extends BubbleId2<? extends T>> boolean isLocked(I bubbleId) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public <T extends BubbleObject2> StoreEntry2<T> registerLocked(T bubbleObject) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T extends BubbleObject2> StoreEntry2<T> registerNew(T bubbleObject) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T extends BubbleObject2> StoreEntry2<T> registerUpdated(T bubbleObject) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T extends BubbleObject2, I extends BubbleId2<? extends T>> void registerDeleted(I bubbleId) {
        throw new UnsupportedOperationException();
    }
}
