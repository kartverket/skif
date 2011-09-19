package no.statkart.skif.storetest.service.store;

import com.google.inject.Inject;
import no.statkart.skif.store.BubbleId;
import no.statkart.skif.store.BubbleObject;
import no.statkart.skif.store.StoreReadService;
import no.statkart.skif.storetest.domain.StoreTestBubbleId;

import java.util.List;

/**
 * @author Henrik Fredholm
 */
public class StoreReadServiceClient implements StoreReadService {
    final private StoreService storeService;

    @Inject
    public StoreReadServiceClient(StoreService storeService) {
        this.storeService = storeService;
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> T get(I bubbleId) {
        T bubbleObject = (T)storeService.getObject((StoreTestBubbleId) bubbleId);
        return bubbleObject;
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> List<T> get(List<I> bubbleIds) {
        List<T> bubbleObjects =(List<T>) storeService.getObjects((List) bubbleIds);
        return bubbleObjects;
    }
}