package no.statkart.skif.storetest.service2.store2;

import com.google.inject.Inject;
import no.statkart.skif.service.annotation.EJBServiceChain;
import no.statkart.skif.service.ejb.EJBTimedService;
import no.statkart.skif.storetest.domain2.StoreTestBubble2;
import no.statkart.skif.storetest.domain2.StoreTestBubbleId2;
import no.statkart.skif.storetest.config2.StoreTestEJBInterceptorJEE2;

import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import java.util.List;

/**
 * @author Henrik Fredholm
 */
@Stateless(name = "no.statkart.skif.storetest.service.store.StoreService2EJBBean")
@Interceptors(StoreTestEJBInterceptorJEE2.class)
public class StoreService2EJBBean extends EJBTimedService implements StoreService2 {
    @Inject  @EJBServiceChain
    private StoreService2 serviceImpl;

    @Override
    public <T extends StoreTestBubble2, I extends StoreTestBubbleId2<? extends T>> T getObject(I ids) {
        return serviceImpl.getObject(ids);
    }

    @Override
    public <T extends StoreTestBubble2, I extends StoreTestBubbleId2<? extends T>> List<T> getObjects(List<I> ids) {
        return serviceImpl.getObjects(ids);
    }
}