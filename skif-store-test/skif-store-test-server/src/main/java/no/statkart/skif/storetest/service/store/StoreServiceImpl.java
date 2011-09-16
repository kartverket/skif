package no.statkart.skif.storetest.service.store;

import com.google.inject.Inject;
import no.statkart.skif.service.ServiceRequestContext;
import no.statkart.skif.store.StoreServer;
import no.statkart.skif.storetest.domain.StoreTestBubble;
import no.statkart.skif.storetest.domain.StoreTestBubbleId;

import java.util.List;


/**
 * @author Henrik Fredholm
 */
public class StoreServiceImpl implements StoreService {
    @Inject
    StoreServer store;

    @Inject
    ServiceRequestContext serviceRequestContext;

    @Override
    public <T extends StoreTestBubble, I extends StoreTestBubbleId<? extends T>> T getObject(I id) {
        return store.get(id);
    /*
        T bubble = id.createTypeInstance();
        bubble.setId(id);
        return (T)bubble;
     */
    }

    @Override
    public <T extends StoreTestBubble, I extends StoreTestBubbleId<? extends T>> List<T> getObjects(List<I> ids) {
        return store.get(ids);
    }
}