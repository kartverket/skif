package no.statkart.skif.storetest.service.uow;

import com.google.inject.Inject;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.interceptor.Interceptors;
import no.statkart.skif.service.annotation.EJBServiceChain;
import no.statkart.skif.service.ejb.EJBTimedService;
import no.statkart.skif.store.BubbleId;
import no.statkart.skif.store.StoreBubbleTransfer;
import no.statkart.skif.storetest.config.StoreTestEJBInterceptorJEE;
import no.statkart.skif.storetest.domain.basic.SimpleId;

/**
 * Service for å støtte UnitOfWork testing
 *
 * @author Henrik Fredholm
 * @since 2.9
 */
@RolesAllowed("Innsyn")
@Stateless(name = "no.statkart.skif.storetest.service.uow.UowTestServiceEJBBean")
@Interceptors(StoreTestEJBInterceptorJEE.class)
public class UowTestServiceEJBBean extends EJBTimedService implements UowTestService {
    @Inject
    @EJBServiceChain
    UowTestService serviceChain;

    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    @Override
    public StoreBubbleTransfer findAndLock(BubbleId bubbleId) {
        return serviceChain.findAndLock(bubbleId);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    @Override
    public void updateTextInNewTransaction(SimpleId<?> simpleId, String text) {
        serviceChain.updateTextInNewTransaction(simpleId, text);
    }

    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    @Override
    public int antallLaaserForBruker() {
        return serviceChain.antallLaaserForBruker();
    }
}
