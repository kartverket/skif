package no.statkart.skif.storetest2.service.store;

import com.google.inject.Inject;
import no.statkart.skif.exception.LockedException;
import no.statkart.skif.service.annotation.EJBServiceChain;
import no.statkart.skif.service.ejb.EJBTimedService;
import no.statkart.skif.store.BubbleId;
import no.statkart.skif.store.BubbleObject;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.storetest2.config.StoreTest2EJBInterceptorJEE;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.interceptor.Interceptors;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author Henrik Fredholm
 * @since 2.2.0
 */
@Stateless(name = "no.statkart.skif.storetest2.service.store.StoreServiceEJBBean")
@Interceptors(StoreTest2EJBInterceptorJEE.class)
// TODO: legge på riktig transattributes
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
    public <I extends BubbleId<?>> Map<I, List<I>> getVersionsForList(List<I> ids, SnapshotVersion start, SnapshotVersion end) {
        return serviceChain.getVersionsForList(ids, start, end);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public <T extends BubbleObject, I extends BubbleId<? extends T>> boolean isLocked(I id) {
        return serviceChain.isLocked(id);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public <T extends BubbleObject> T lock(BubbleId<? extends T> id) throws LockedException {
        return serviceChain.lock(id);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public <T extends BubbleObject, I extends BubbleId<? extends T>> void unlock(I id) {
        serviceChain.unlock(id);
    }
}