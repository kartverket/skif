package no.statkart.skif.storetest.service2.store2;

import com.google.inject.Inject;
import no.statkart.skif.store2.BubbleId2;
import no.statkart.skif.store2.BubbleObject2;
import no.statkart.skif.store2.StoreReadChain2;
import no.statkart.skif.storetest.domain2.StoreTestBubbleId2;

import java.util.List;

/**
 * @author Henrik Fredholm
 */
public class StoreReadChainClient2 implements StoreReadChain2 {
    final private StoreService2 storeService;

    @Inject
    public StoreReadChainClient2(StoreService2 storeService) {
        this.storeService = storeService;
    }

    @Override
    public <T extends BubbleObject2, I extends BubbleId2<? extends T>> T get(I bubbleId) {
        T bubbleObject = (T)storeService.getObject((StoreTestBubbleId2) bubbleId);
        return bubbleObject;
    }

    @Override
    public <T extends BubbleObject2, I extends BubbleId2<? extends T>> List<T> get(List<I> bubbleIds) {
        List<T> bubbleObjects =(List<T>) storeService.getObjects((List) bubbleIds);
        return bubbleObjects;
    }
}