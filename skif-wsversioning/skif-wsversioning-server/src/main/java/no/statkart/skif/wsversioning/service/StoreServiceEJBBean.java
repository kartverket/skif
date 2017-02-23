package no.statkart.skif.wsversioning.service;

import com.google.inject.Inject;
import no.statkart.skif.service.annotation.EJBServiceChain;
import no.statkart.skif.service.ejb.EJBTimedService;
import no.statkart.skif.store.BubbleId;
import no.statkart.skif.store.BubbleObject;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.wsversioning.config.WSVersioningEJBInterceptorJEE;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.interceptor.Interceptors;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * EJB for {@link StoreService}.
 *
 * @author Tor Egil R. Strand
 * @since 2.4.0
 */
@Stateless(name = "no.statkart.skif.wsversioning.service.StoreServiceEJBBean")
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
@Interceptors(WSVersioningEJBInterceptorJEE.class)
public class StoreServiceEJBBean extends EJBTimedService implements StoreService {
    @Inject
    @EJBServiceChain
    private StoreService serviceChain;

    @Override
    public <T extends BubbleObject> T getObject(BubbleId<? extends T> id) {
        return serviceChain.getObject(id);
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> Collection<T> getObjects(Collection<I> ids) {
        return serviceChain.getObjects(ids);
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> Collection<T> getObjectsIgnoreMissing(Collection<I> ids) {
        return serviceChain.getObjectsIgnoreMissing(ids);
    }

    @Override
    public <I extends BubbleId<?>> List<I> getVersions(I id, SnapshotVersion start, SnapshotVersion end) {
        return serviceChain.getVersions(id, start, end);
    }

    @Override
    public <I extends BubbleId<?>> Map<I, List<I>> getVersionsForList(Collection<? extends I> ids, SnapshotVersion start, SnapshotVersion end) {
        return serviceChain.getVersionsForList(ids, start, end);
    }

    @Override
    public <T extends BubbleObject> T lock(BubbleId<? extends T> id) {
        return serviceChain.lock(id);
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> Collection<T> lockForList(Collection<I> ids) {
        return serviceChain.lockForList(ids);
    }

    @Override
    public <I extends BubbleId<?>> void unlock(I id) {
        serviceChain.unlock(id);
    }

    @Override
    public <I extends BubbleId<?>> boolean isLocked(I id) {
        return serviceChain.isLocked(id);
    }
}
