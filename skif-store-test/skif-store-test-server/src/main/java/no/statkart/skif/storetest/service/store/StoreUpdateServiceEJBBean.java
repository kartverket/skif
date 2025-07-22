package no.statkart.skif.storetest.service.store;

import com.google.inject.Inject;
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.interceptor.Interceptors;
import no.statkart.skif.exception.ObjectNotFoundException;
import no.statkart.skif.service.annotation.EJBServiceChain;
import no.statkart.skif.service.ejb.EJBTimedService;
import no.statkart.skif.store.BubbleId;
import no.statkart.skif.store.BubbleObject;
import no.statkart.skif.store.UnitOfWorkTransfer;
import no.statkart.skif.storetest.config.StoreTestEJBInterceptorJEE;

import javax.annotation.Nullable;
import java.util.Collection;

/**
 * @author Roar Ingebrigtsen
 * @since 2.0
 */
@Stateless(name = "no.statkart.skif.storetest.service.store.StoreUpdateServiceEJBBean")
@Interceptors(StoreTestEJBInterceptorJEE.class)
public class StoreUpdateServiceEJBBean extends EJBTimedService implements StoreUpdateService {

    @Inject
    @EJBServiceChain
    private StoreUpdateService serviceChain;

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public <T extends BubbleObject, I extends BubbleId<? extends T>> T lockObject(@Nullable I bubbleId) throws ObjectNotFoundException {
        return serviceChain.lockObject(bubbleId);
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> Collection<T> lockObjects(Collection<I> bubbleIds) {
        return serviceChain.lockObjects(bubbleIds);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void saveTransfer(UnitOfWorkTransfer transfer) {
        serviceChain.saveTransfer(transfer);
    }

}
