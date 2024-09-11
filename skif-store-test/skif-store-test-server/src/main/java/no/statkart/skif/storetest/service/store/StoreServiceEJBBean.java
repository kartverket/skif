package no.statkart.skif.storetest.service.store;

import com.google.inject.Inject;
import no.statkart.skif.exception.LockedException;
import no.statkart.skif.service.annotation.EJBServiceChain;
import no.statkart.skif.service.ejb.EJBTimedService;
import no.statkart.skif.store.BubbleId;
import no.statkart.skif.store.BubbleObject;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.storetest.config.StoreTestEJBInterceptorJEE;

import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.interceptor.Interceptors;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author Henrik Fredholm
 */
@SuppressWarnings("unused")

@Stateless(name = "no.statkart.skif.storetest.service.store.StoreServiceEJBBean")
@Interceptors(StoreTestEJBInterceptorJEE.class)
public class StoreServiceEJBBean extends EJBTimedService implements StoreService {
    @Inject  @EJBServiceChain
    private StoreService serviceChain;

    @Override
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public <T extends BubbleObject> T getObject(BubbleId<? extends T> ids) {
        return serviceChain.getObject(ids);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public <T extends BubbleObject, I extends BubbleId<? extends T>> Collection<T> getObjects(Collection<I> ids) {
        return serviceChain.getObjects(ids);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public <T extends BubbleObject, I extends BubbleId<? extends T>> Collection<T> getObjectsIgnoreMissing(Collection<I> ids) {
        return serviceChain.getObjectsIgnoreMissing(ids);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public <I extends BubbleId<?>> List<I> getVersions(I id, SnapshotVersion start, SnapshotVersion end) {
        return serviceChain.getVersions(id, start, end);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public <I extends BubbleId<?>> Map<I, List<I>> getVersionsForList(Collection<? extends I> ids, SnapshotVersion start, SnapshotVersion end) {
        return serviceChain.getVersionsForList(ids, start, end);
    }
}