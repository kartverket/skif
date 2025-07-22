package no.statkart.skif.wsversioning.service;

import com.google.inject.Inject;
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.interceptor.Interceptors;
import no.statkart.skif.service.annotation.EJBServiceChain;
import no.statkart.skif.service.ejb.EJBTimedService;
import no.statkart.skif.store.BubbleId;
import no.statkart.skif.store.BubbleObject;
import no.statkart.skif.wsversioning.config.WSVersioningEJBInterceptorJEE;

import java.util.Collection;

/**
 * EJB for {@link LockService}.
 *
 * @author Tor Egil R. Strand
 * @since 2.4.0
 */
@Stateless(name = "no.statkart.skif.wsversioning.service.LockServiceEJBBean")
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
@Interceptors(WSVersioningEJBInterceptorJEE.class)
public class LockServiceEJBBean extends EJBTimedService implements LockService {
    @Inject
    @EJBServiceChain
    private LockService serviceChain;

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
    public void unlockForList(Collection<? extends BubbleId<?>> ids) {
        serviceChain.unlockForList(ids);
    }

    @Override
    public <I extends BubbleId<?>> boolean isLocked(I id) {
        return serviceChain.isLocked(id);
    }
}
