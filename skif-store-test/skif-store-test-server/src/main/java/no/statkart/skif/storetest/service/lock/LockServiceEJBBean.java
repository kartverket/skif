package no.statkart.skif.storetest.service.lock;

import com.google.inject.Inject;
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.interceptor.Interceptors;
import no.statkart.skif.exception.LockedException;
import no.statkart.skif.service.annotation.EJBServiceChain;
import no.statkart.skif.service.ejb.EJBTimedService;
import no.statkart.skif.store.BubbleId;
import no.statkart.skif.store.BubbleObject;
import no.statkart.skif.storetest.config.StoreTestEJBInterceptorJEE;

import java.util.Collection;

@SuppressWarnings("unused")
@Stateless(name = "no.statkart.skif.storetest.service.lock.LockServiceEJBBean")
@Interceptors(StoreTestEJBInterceptorJEE.class)
public class LockServiceEJBBean extends EJBTimedService implements LockService {
    @Inject  @EJBServiceChain
    private LockService serviceChain;

    @Override
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public <I extends BubbleId<?>> boolean isLocked(I id) {
        return serviceChain.isLocked(id);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public <T extends BubbleObject> T lock(BubbleId<? extends T> id) throws LockedException {
        return serviceChain.lock(id);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public <T extends BubbleObject, I extends BubbleId<? extends T>> Collection<T> lockForList(Collection<I> ids) {
        return serviceChain.lockForList(ids);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public <I extends BubbleId<?>> void unlock(I id) {
        serviceChain.unlock(id);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public void unlockForList(Collection<? extends BubbleId<?>> ids) {
        serviceChain.unlockForList(ids);
    }
}
