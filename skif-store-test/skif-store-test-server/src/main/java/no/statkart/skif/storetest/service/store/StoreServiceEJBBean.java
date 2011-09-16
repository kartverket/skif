package no.statkart.skif.storetest.service.store;

import com.google.inject.Inject;
import no.statkart.skif.service.annotation.EJBServiceChain;
import no.statkart.skif.service.ejb.EJBTimedService;
import no.statkart.skif.storetest.config.StoreTestEJBInterceptorJEE;
import no.statkart.skif.storetest.domain.StoreTestBubble;
import no.statkart.skif.storetest.domain.StoreTestBubbleId;

import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import java.util.List;

/**
 * @author Henrik Fredholm
 */
@Stateless(name = "no.statkart.skif.storetest.service.store.StoreServiceEJBBean")
@Interceptors(StoreTestEJBInterceptorJEE.class)
public class StoreServiceEJBBean extends EJBTimedService implements StoreService {
    @Inject  @EJBServiceChain
    private StoreService serviceImpl;

    @Override
    public <T extends StoreTestBubble, I extends StoreTestBubbleId<? extends T>> T getObject(I ids) {
        return serviceImpl.getObject(ids);
    }

    @Override
    public <T extends StoreTestBubble, I extends StoreTestBubbleId<? extends T>> List<T> getObjects(List<I> ids) {
        return serviceImpl.getObjects(ids);
    }
}