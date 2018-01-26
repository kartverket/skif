package no.statkart.skif.storetest.service.lock;

import com.google.inject.Inject;
import no.statkart.skif.exception.LockedException;
import no.statkart.skif.service.annotation.EJBServiceChain;
import no.statkart.skif.service.ejb.EJBTimedService;
import no.statkart.skif.store.BubbleId;
import no.statkart.skif.store.BubbleObject;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.storetest.config.StoreTestEJBInterceptorJEE;
import no.statkart.skif.storetest.service.store.StoreService;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.interceptor.Interceptors;
import java.util.Collection;
import java.util.List;
import java.util.Map;

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