package no.statkart.skif.storetest.service2.store2;

import com.google.inject.Inject;
import no.statkart.skif.service.ServiceRequestContext;
import no.statkart.skif.store2.StoreServer2;
import no.statkart.skif.storetest.domain2.StoreTestBubble2;
import no.statkart.skif.storetest.domain2.StoreTestBubbleId2;

import java.util.List;


/**
 * @author Henrik Fredholm
 */
public class StoreService2Impl implements StoreService2 {
    @Inject
    StoreServer2 store;

    @Inject
    ServiceRequestContext serviceRequestContext;

    @Override
    public <T extends StoreTestBubble2, I extends StoreTestBubbleId2<? extends T>> T getObject(I id) {
        return store.get(id);
    /*
        T bubble = id.createTypeInstance();
        bubble.setId(id);
        return (T)bubble;
     */
    }

    @Override
    public <T extends StoreTestBubble2, I extends StoreTestBubbleId2<? extends T>> List<T> getObjects(List<I> ids) {
        return store.get(ids);
    }
}