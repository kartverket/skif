package no.statkart.skif.storetest.service.store;

import com.google.inject.Inject;
import no.statkart.skif.exception.LockedException;
import no.statkart.skif.service.annotation.EJBServiceChain;
import no.statkart.skif.service.ejb.EJBTimedService;
import no.statkart.skif.store.BubbleId;
import no.statkart.skif.store.BubbleObject;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.storetest.config.StoreTestEJBInterceptorJEE;
import no.statkart.skif.storetest.domain.StoreTestBubble;
import no.statkart.skif.storetest.domain.StoreTestBubbleId;

import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import java.util.List;
import java.util.Map;

/**
 * @author Henrik Fredholm
 */
@Stateless(name = "no.statkart.skif.storetest.service.store.StoreServiceEJBBean")
@Interceptors(StoreTestEJBInterceptorJEE.class)
public class StoreServiceEJBBean extends EJBTimedService implements StoreService {
    @Inject  @EJBServiceChain
    private StoreService serviceChain;

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> T getObject(I ids) {
        return serviceChain.getObject(ids);
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> List<T> getObjects(List<I> ids) {
        return serviceChain.getObjects(ids);
    }

    @Override
    public <I extends BubbleId<?>> List<I> getVersions(I id, SnapshotVersion start, SnapshotVersion end) {
        return serviceChain.getVersions(id, start, end);
    }

    @Override
    public <I extends BubbleId<?>> Map<I, List<I>> getVersionsForList(List<I> ids, SnapshotVersion start, SnapshotVersion end) {
        return serviceChain.getVersionsForList(ids, start, end);
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> boolean isLocked(I id) {
        return serviceChain.isLocked(id);
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> T lock(I id) throws LockedException {
        return serviceChain.lock(id);
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> void unlock(I id) {
        serviceChain.unlock(id);
    }
}