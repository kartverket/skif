package no.statkart.skif.store;


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
    private final Map<Class<AbstractBubbleId>, List<AbstractBubbleId>> idsPerPersister = new HashMap<Class<AbstractBubbleId>, List<AbstractBubbleId>>();
    private final Map<Class<AbstractBubbleId>, List<AbstractBubbleId>> idsPerLocker = new HashMap<Class<AbstractBubbleId>, List<AbstractBubbleId>>();

    public StoreSessionPersisterChain(StorePersisterStrategy persisterStrategy, LockerStrategy lockerStrategy) {
        this.persisterStrategy = persisterStrategy;
        this.lockerStrategy = lockerStrategy;
    }

    @Override
    public StoreSessionReadChain setNextInReadChain(StoreSessionReadChain next) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T extends AbstractBubbleObject> StoreEntry<T> register(T bubbleObject) {
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
    public <T extends AbstractBubbleObject, I extends AbstractBubbleId<? extends T>> StoreEntry<T> get(I bubbleId) {
        StorePersister<T, I> persister = persisterStrategy.getPersister(bubbleId);
        T bubble = persister.get(bubbleId);
        return new StoreEntry<T>(bubble);
    }

    @Override
    public <T extends AbstractBubbleObject, I extends AbstractBubbleId<? extends T>> Collection<StoreEntry<T>> get(Collection<I> bubbleIds) {
        Map<StorePersister, List<AbstractBubbleId>> map = classifyIds(bubbleIds);
        List<StoreEntry<T>> result =  new ArrayList<StoreEntry<T>>(bubbleIds.size());
        for (Map.Entry<StorePersister, List<AbstractBubbleId>> entry : map.entrySet()) {
            StorePersister persister = entry.getKey();
            Collection<T> objects = persister.get(entry.getValue());
            for (T object : objects) {
                result.add(new StoreEntry<T>(object));
            }
        }
        return result;
    }

    private Map<StorePersister, List<AbstractBubbleId>> classifyIds(Collection<? extends AbstractBubbleId> bubbleIds) {
        Object lastClassifier = null;
        List<AbstractBubbleId> lastList = null;
        Map<StorePersister, List<AbstractBubbleId>> map = new HashMap<StorePersister, List<AbstractBubbleId>>();
        for (AbstractBubbleId bubbleId : bubbleIds) {
            StorePersister persister = persisterStrategy.getPersister(bubbleId);
            if (lastClassifier != persister) {
                lastClassifier = persister;
                lastList = map.get(persister);
                if (lastList == null) {
                    lastList = new ArrayList<AbstractBubbleId>();
                    map.put(persister, lastList);
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
    public <T extends AbstractBubbleObject, I extends AbstractBubbleId<? extends T>> StoreEntry<T> lock(I bubbleId) {
        return get(bubbleId);
    }

    @Override
    public <T extends AbstractBubbleObject, I extends AbstractBubbleId<? extends T>> Collection<StoreEntry<T>> lock(Collection<I> bubbleIds) {
        return get(bubbleIds);
    }

    @Override
    public <T extends AbstractBubbleObject, I extends AbstractBubbleId<? extends T>> boolean isLocked(I bubbleId) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public <T extends AbstractBubbleObject> StoreEntry<T> registerLocked(T bubbleObject) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T extends AbstractBubbleObject> StoreEntry<T> registerNew(T bubbleObject) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T extends AbstractBubbleObject> StoreEntry<T> registerUpdated(T bubbleObject) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T extends AbstractBubbleObject, I extends AbstractBubbleId<? extends T>> void registerDeleted(I bubbleId) {
        throw new UnsupportedOperationException();
    }
}
