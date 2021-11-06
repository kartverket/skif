package no.statkart.skif.storetest.service.store;

import com.google.inject.Inject;
import no.statkart.skif.exception.ObjectNotFoundException;
import no.statkart.skif.service.annotation.EJBServiceChain;
import no.statkart.skif.service.ejb.EJBTimedService;
import no.statkart.skif.store.BubbleId;
import no.statkart.skif.store.BubbleObject;
import no.statkart.skif.store.UnitOfWorkTransfer;
import no.statkart.skif.storetest.config.StoreTestEJBInterceptorJEE;
import no.statkart.skif.storetest.config.StoreTestEJBInterceptorSpring;

import javax.annotation.Nullable;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.interceptor.Interceptors;
import java.util.Collection;

/**
 * @author Roar Ingebrigtsen
 * @since 2.0
 */
@Stateless(name = "no.statkart.skif.storetest.service.store.StoreUpdateServiceEJBBean")
@StoreTestEJBInterceptorSpring
@Interceptors(StoreTestEJBInterceptorJEE.class)
@TransactionAttribute(TransactionAttributeType.REQUIRED) // Springs default for metoder er ingen transaksjonshåndtering. Må angi REQUIRED her hvis det skal være default for klassen
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
