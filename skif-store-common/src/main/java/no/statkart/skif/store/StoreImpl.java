package no.statkart.skif.store;


import javax.annotation.Nullable;
import javax.inject.Inject;
import java.util.*;

import com.google.inject.Injector;
import no.statkart.skif.exception.ConfigurationException;

import static no.statkart.skif.guava.Preconditions.checkNotNull;

/**
 * @author Henrik Fredholm
 */
public class StoreImpl implements Store {
    final protected StoreCache storeCache;
    final protected StoreChain[] storeChain;
    final protected StoreReadChain readChain;
    final protected StoreUpdateChain writeChain;
    final protected UnitOfWorkChain uowChain;

    @Inject
    private Injector injector;

    public StoreImpl(StoreCache storeCache, StoreChain... storeChain) {
        this.storeCache = storeCache;
        this.storeChain = storeChain;

        this.readChain = initReadChain(storeChain);
        this.writeChain = initWriteChain(storeChain);
        this.uowChain = initUnitOfWorkChain(storeChain);
    }

    private StoreReadChain initReadChain(StoreChain[] storeChainList) {
        StoreReadChain root = null;
        StoreReadChain currentRead = null;
        for (StoreChain chain : storeChainList) {
            if (chain instanceof StoreReadChain) {
                if (currentRead == null) {
                    currentRead = (StoreReadChain) chain;
                    root = currentRead;
                } else {
                    currentRead = currentRead.setNextInReadChain((StoreReadChain) chain);
                }
            }
        }
        return root;
    }

    private StoreUpdateChain initWriteChain(StoreChain[] storeChainList) {
        StoreUpdateChain root = null;
        StoreUpdateChain currentWrite = null;
        for (StoreChain chain : storeChainList) {
            if (chain instanceof StoreUpdateChain) {
                if (currentWrite == null) {
                    currentWrite = (StoreUpdateChain) chain;
                    root = currentWrite;
                } else {
                    currentWrite = currentWrite.setNextInWriteChain((StoreUpdateChain) chain);
                }
            }
        }
        return root;
    }

    private UnitOfWorkChain initUnitOfWorkChain(StoreChain[] storeChainList) {
        UnitOfWorkChain root = createNoUnitOfWorkConfiguredChain();
        UnitOfWorkChain currentRead = null;
        for (StoreChain chain : storeChainList) {
            if (chain instanceof UnitOfWorkChain) {
                if (currentRead == null) {
                    currentRead = (UnitOfWorkChain) chain;
                    root = currentRead;
                } else {
                    throw new ConfigurationException("Multible UnitUnitOfWorks in chain are not supported");
                }
            }
        }
        return root;
    }

    private UnitOfWorkChain createNoUnitOfWorkConfiguredChain() {
        return new UnitOfWorkChain() {

            @Override
            public void startUnitOfWork() {
                throw new ConfigurationException("UnitOfWork not not configured for Store");
            }

            @Override
            public UnitOfWorkTransfer getUnitOfWorkTransfer() {
                throw new ConfigurationException("UnitOfWork not not configured for Store");
            }

            @Override
            public void abortUnitOfWork() {
                throw new ConfigurationException("UnitOfWork not not configured for Store");
            }

            @Override
            public void endUnitOfWork() {
                throw new ConfigurationException("UnitOfWork not not configured for Store");
            }

            @Override
            public boolean inUnitOfWork() {
                return false;  //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public void init(StoreCache storeCache) {
                //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public void clear() {
                //To change body of implemented methods use File | Settings | File Templates.
            }
        };
    }


    public void init() {
        storeCache.init(this);
        for (StoreChain chain : storeChain) {
            chain.init(storeCache);
        }
    }

    public void clear() {
        for (StoreChain chain : storeChain) {
            chain.clear();
        }
        storeCache.clear();
    }


    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> T get(@Nullable I bubbleId) {
        if (bubbleId == null) {
            return null;
        }

        StoreEntry<T> storeEntry = storeCache.get(bubbleId);
        T bubbleObject = storeEntry==null ? null : storeEntry.getBubbleObject();
        if (bubbleObject==null) {
            storeEntry = readChain.get(bubbleId);
            bubbleObject = storeEntry.getBubbleObject();
        }
        return bubbleObject;
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
        checkNotNull(bubbleIds, "bubbleIds");
        Set<T> bubbleObjects = new HashSet<T>(bubbleIds.size());
        get(bubbleIds, bubbleObjects);
        return bubbleObjects;
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> List<T> get(List<I> bubbleIds) {
        checkNotNull(bubbleIds, "bubbleIds");
        List<T> bubbleObjects = new ArrayList<T>(bubbleIds.size());
        get(bubbleIds, bubbleObjects);
        return bubbleObjects;
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> void get(Collection<I> bubbleIds, Collection<T> bubbleObjects) {
        checkNotNull(bubbleIds, "bubbleIds");
        List<I> missingBubbleIds = null;

        for (I bubbleId : bubbleIds) {
            final StoreEntry<T> storeEntry = storeCache.get(bubbleId);
            final T bubbleObject = storeEntry==null ? null : storeEntry.getBubbleObject();
            if (bubbleObject != null) {
                bubbleObjects.add(bubbleObject);
            } else {
                if (missingBubbleIds == null) {
                    missingBubbleIds = new ArrayList(bubbleIds.size());
                }
                missingBubbleIds.add(bubbleId);
            }
        }

        if (missingBubbleIds!=null) {
            if (missingBubbleIds.size() == 1) {
                final StoreEntry<T> storeEntry = readChain.get(missingBubbleIds.get(0));
                bubbleObjects.add(storeEntry.getBubbleObject());
            } else {
                final Collection<StoreEntry<T>> storeEntries = readChain.get(missingBubbleIds);
                for (StoreEntry<T> storeEntry : storeEntries) {
                    bubbleObjects.add(storeEntry.getBubbleObject());
                }
            }
        }
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> List<T> getOrdered(List<I> bubbleIds) {
        checkNotNull(bubbleIds, "bubbleIds");
        ArrayList<T> bubbleObjects = new ArrayList<T>(bubbleIds.size());
        Collections.fill(bubbleObjects, null);
        ArrayList<I> missingBubbleIds = null;

        for (int i = 0; i < bubbleIds.size(); i++) {
            final I bubbleId = bubbleIds.get(i);
            final StoreEntry<T> storeEntry = storeCache.get(bubbleId);
            if (storeEntry != null && storeEntry.getBubbleObject()!= null) {
                bubbleObjects.add(storeEntry.getBubbleObject());
            } else {
                bubbleObjects.add(null);  // null er plassholder
                if (missingBubbleIds == null) {
                    missingBubbleIds = new ArrayList<I>(bubbleIds.size());
                }
                missingBubbleIds.add(bubbleId);
            }
        }

        // Sjekk om alle ble funnet
        if (missingBubbleIds!=null) {
            final Collection<StoreEntry<T>> storeEntries = readChain.get(missingBubbleIds);
            final Map<BubbleId<?>, StoreEntry<T>> storeEntryMap = new HashMap<BubbleId<?>, StoreEntry<T>>(storeEntries.size());
            for (StoreEntry<T> storeEntry : storeEntries) {
                storeEntryMap.put(storeEntry.getId(), storeEntry);
            }
            for (int i = 0; i < bubbleObjects.size(); i++) {
                if (bubbleObjects.get(i)==null) {
                    final StoreEntry<T> storeEntry = storeEntryMap.get(bubbleIds.get(i));
                    bubbleObjects.set(i, storeEntry.getBubbleObject());
                }
            }
        }
        return bubbleObjects;
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> void getOrdered(Collection<I> bubbleIds, Collection<T> bubbleObjects) {
        // TODO;
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> Set<T> getOrdered(Set<I> bubbleIds) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> T lock(@Nullable I bubbleId) {
       if (bubbleId == null) {
            return null;
        }

        StoreEntry<T> storeEntry = storeCache.get(bubbleId);
        if (storeEntry == null || !storeEntry.isLocked()) {
            storeEntry = writeChain.lock(bubbleId);
        }
        T bubbleObject = storeEntry.getBubbleObject();
        return bubbleObject;
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
            bubbleObjects = lock(new ArrayList<I>(bubbleIds));
        }
        return bubbleObjects;
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> Set<T> lock(Set<I> bubbleIds) {
        checkNotNull(bubbleIds, "bubbleIds");
        Set<T> bubbleObjects = new HashSet<T>(bubbleIds.size());
        lock(bubbleIds, bubbleObjects);
        return bubbleObjects;
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> List<T> lock(List<I> bubbleIds) {
        checkNotNull(bubbleIds, "bubbleIds");
        List<T> bubbleObjects = new ArrayList<T>(bubbleIds.size());
        lock(bubbleIds, bubbleObjects);
        return bubbleObjects;
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> void lock(Collection<I> bubbleIds, Collection<T> bubbleObjects) {
        checkNotNull(bubbleIds, "bubbleIds");
        List<I> missingBubbleIds = null;

        for (I bubbleId : bubbleIds) {
            final StoreEntry<T> storeEntry = storeCache.get(bubbleId);
            final T bubbleObject = storeEntry==null ? null : storeEntry.getBubbleObjectIfLocked();
            if (bubbleObject != null) {
                bubbleObjects.add(bubbleObject);
            } else {
                if (missingBubbleIds == null) {
                    missingBubbleIds = new ArrayList(bubbleIds.size());
                }
                missingBubbleIds.add(bubbleId);
            }
        }

        if (missingBubbleIds!=null) {
            if (missingBubbleIds.size() == 1) {
                final StoreEntry<T> storeEntry = writeChain.lock(missingBubbleIds.get(0));
                bubbleObjects.add(storeEntry.getBubbleObject());
            } else {
                final Collection<StoreEntry<T>> storeEntries = writeChain.lock(missingBubbleIds);
                for (StoreEntry<T> storeEntry : storeEntries) {
                    bubbleObjects.add(storeEntry.getBubbleObject());
                }
            }
        }
    }

    @Override
    public <T extends BubbleObject> T register(@Nullable T bubbleObject) {
        if (bubbleObject==null)  {
            return null;
        }
        final StoreEntry<T> storeEntry = readChain.register(bubbleObject);
        return storeEntry.getBubbleObject();
    }

    @Override
    public <T extends BubbleObject> Collection<? extends T> register(Collection<? extends T> bubbleObjects, Collection<? super T> resolvedObjects) {
        for (T bubbleObject : bubbleObjects) {
            resolvedObjects.add(register(bubbleObject));
        }
        return (Collection<? extends T>) resolvedObjects;
    }

    @Override
    public <T extends BubbleObject> T registerLocked(T bubbleObject) {
        if (bubbleObject==null)  {
            return null;
        }
        final StoreEntry<T> storeEntry = writeChain.registerLocked(bubbleObject);
        return storeEntry.getBubbleObject();
    }


    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> void registerLocked(Collection<T> bubbleObjects, Collection<T> resolvedObjects) {
        for (T bubbleObject : bubbleObjects) {
            resolvedObjects.add(register(bubbleObject));
        }
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> void registerTransfer(UnitOfWorkTransfer<T,I> transfer) {
        throw new UnsupportedOperationException();  //TODO
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> boolean isLocked(I bubbleId) {
        final StoreEntry<T> storeEntry = storeCache.get(bubbleId);
        if (storeEntry!=null) {
            return storeEntry.state!=StoreEntryState.UNLOCKED;
        } else {
            return writeChain.isLocked(bubbleId);
        }
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> boolean evict(I bubbleId) {
        boolean objectEvicted = false;
        final StoreEntry<T> storeEntry = storeCache.get(bubbleId);
        if (storeEntry!=null) {
            if (storeEntry.state==StoreEntryState.UNLOCKED) {
                storeCache.remove(bubbleId);
                objectEvicted = true;
            } else {
                objectEvicted = false;
            }
        } else {
            objectEvicted = true;
        }
        return objectEvicted;
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> boolean evict(Collection<I> bubbleIds) {
        boolean objectEvicted = false;
        for (I bubbleId : bubbleIds) {
            objectEvicted |= evict(bubbleId);
        }
        return objectEvicted;
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> boolean evictAll() {
        boolean objectEvicted = false;
        for (Iterator<Map.Entry<BubbleId<?>, StoreEntry<?>>> iterator = storeCache.entrySet().iterator(); iterator.hasNext();) {
            Map.Entry<BubbleId<?>, StoreEntry<?>> entry = iterator.next();
            StoreEntry<?> storeEntry = entry.getValue();
            if (storeEntry.getState() == StoreEntryState.UNLOCKED) {
                iterator.remove();
                storeEntry.setBubbleObject(null);
                objectEvicted |= true;
            }
        }
        return objectEvicted;
    }

    @Override
    public void startUnitOfWork() {
        uowChain.startUnitOfWork();
    }

    @Override
    public UnitOfWorkTransfer getUnitOfWorkTransfer() {
        return uowChain.getUnitOfWorkTransfer();
    }

    @Override
    public void abortUnitOfWork() {
        uowChain.abortUnitOfWork();
    }

    @Override
    public void endUnitOfWork() {
        uowChain.endUnitOfWork();
    }

    @Override
    public boolean inUnitOfWork() {
        return uowChain.inUnitOfWork();
    }

    @Override
    public <S> S getService(Class<S> serviceClass) {
        return  injector.getInstance((serviceClass));

    }
}
