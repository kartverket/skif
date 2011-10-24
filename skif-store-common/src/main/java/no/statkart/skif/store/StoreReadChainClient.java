package no.statkart.skif.store;

import com.google.inject.Inject;
import no.statkart.skif.store.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Henrik Fredholm
 */
public class StoreReadChainClient implements StoreReadChain {
    final private no.statkart.skif.store.StoreService storeService;

    @Inject
    public StoreReadChainClient(no.statkart.skif.store.StoreService storeService) {
        this.storeService = storeService;
    }

    @Override
    public StoreReadChain setNextInReadChain(StoreReadChain next) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> StoreEntry<T> get(I bubbleId) {
        T bubbleObject = (T)storeService.getObject(bubbleId);
        return new StoreEntry<T>(bubbleObject);
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> Collection<StoreEntry<T>> get(Collection<I> bubbleIds) {
        List<T> bubbleObjects =(List<T>) storeService.getObjects((List) bubbleIds);
        List<StoreEntry<T>> entries = new ArrayList<StoreEntry<T>>();
        for (T bubbleObject : bubbleObjects) {
            entries.add(new StoreEntry<T>(bubbleObject));
        }
        return entries;
    }

    @Override
    public <T extends BubbleObject> StoreEntry<T> register(T bubbleObject) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void init(StoreCache storeCache) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void clear() {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}