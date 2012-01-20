package no.statkart.skif.store;

import com.google.inject.Injector;
import com.google.inject.Key;
import no.statkart.skif.exception.ImplementationException;

import javax.annotation.Nullable;
import java.util.*;

import static no.statkart.skif.guava.Preconditions.checkNotNull;

/**
 * @author Henrik Fredholm
 */
public class AbstractStore5 implements Store {
    protected WrappableStoreSession5 storeSession;
    final private Injector injector;

    public AbstractStore5(StoreSessionServer5 storeSession, Injector injector) {
        this.injector = injector;
        this.storeSession = storeSession;
        storeSession.setStore(this);
    }

    protected StoreUnitOfWork5 storeUnitOfWork() {
        if (storeSession instanceof StoreUnitOfWork5) {
            return (StoreUnitOfWork5) storeSession;
        } else {
            throw new ImplementationException("Not in UnitOfWork");
        }
    }
    @Override
    public <T> T getInstance(Class<T> type) {
        return injector.getInstance(type);
    }

    public <T> T getInstance(Key<T> key) {
        return injector.getInstance(key);
    }

    @Override
    public void init() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void clear() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> T get(@Nullable I bubbleId) {
        if (bubbleId==null) return null;
        return storeSession.get(bubbleId);
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
        storeSession.get(bubbleIds, bubbleObjects);
    }


    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> Set<T> getOrdered(Set<I> bubbleIds) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> List<T> getOrdered(List<I> bubbleIds) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> void getOrdered(Collection<I> bubbleIds, Collection<T> bubbleObjects) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> T lock(@Nullable I bubbleId) {
        if (bubbleId==null) return null;
        return storeSession.lock(bubbleId);
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> Collection<T> lock(Collection<I> bubbleIds) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> Set<T> lock(Set<I> bubbleIds) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> List<T> lock(List<I> bubbleIds) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> void lock(Collection<I> bubbleIds, Collection<T> bubbleObjects) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public <T extends BubbleObject> T register(@Nullable T bubbleObject) {
        if (bubbleObject==null) return null;
        return storeSession.register(bubbleObject);
    }

    @Override
    public <T extends BubbleObject> Collection<? extends T> register(Collection<? extends T> bubbleObjects, Collection<? super T> resolvedObjects) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public <T extends BubbleObject> T registerLocked(T bubbleObject) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> void registerLocked(Collection<T> bubbleObjects, Collection<T> resolvedObjects) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> void registerTransfer(UnitOfWorkTransfer transfer) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> boolean isLocked(@Nullable I bubbleId) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> boolean evict(@Nullable I bubbleId) {
        if (bubbleId==null) return false;
        return storeSession.evict(bubbleId);
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> boolean evict(Collection<I> bubbleIds) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> boolean evictAll() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public <I extends BubbleId<?>> List<I> getVersions(I id, SnapshotVersion start, SnapshotVersion end) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public <I extends BubbleId<?>> Map<I, List<I>> getVersionsForList(List<I> ids, SnapshotVersion start, SnapshotVersion end) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public <T extends BubbleObject> void insert(T bubbleObject) {
        storeSession.insert(bubbleObject);
    }

    @Override
    public <T extends BubbleObject> void update(T bubbleObject) {
        storeSession.update(bubbleObject);
    }

    @Override
    public <T extends BubbleObject> void delete(T bubbleObject) {
        storeSession.delete(bubbleObject);
    }

    @Override
    public void startUnitOfWork() {
        storeSession = storeUnitOfWork().beginUnitOfWork();
    }

    @Override
    public UnitOfWorkTransfer getUnitOfWorkTransfer() {
        return storeUnitOfWork().getUnitOfWorkTransfer();
    }

    @Override
    public void abortUnitOfWork() {
        storeSession = storeUnitOfWork().abortUnitOfWork();
    }

    @Override
    public void endUnitOfWork() {
        storeSession = storeUnitOfWork().endUnitOfWork();
    }

    @Override
    public boolean inUnitOfWork() {
        return storeSession instanceof StoreUnitOfWork5;
    }

    //@Override
    public void commitUnitOfWork() {
        storeUnitOfWork().commitUnitOfWork();
    }
}
