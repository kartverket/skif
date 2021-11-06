package no.statkart.skif.storetest.service.uow;

import com.google.inject.Inject;
import no.statkart.skif.service.annotation.EJBServiceChain;
import no.statkart.skif.service.ejb.EJBTimedService;
import no.statkart.skif.store.BubbleId;
import no.statkart.skif.store.StoreBubbleTransfer;
import no.statkart.skif.storetest.config.StoreTestEJBInterceptorJEE;
import no.statkart.skif.storetest.config.StoreTestEJBInterceptorSpring;
import no.statkart.skif.storetest.domain.basic.SimpleId;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.interceptor.Interceptors;

/**
 * Service for å støtte UnitOfWork testing
 *
 * @author Henrik Fredholm
 * @since 2.9
 */
@RolesAllowed("Innsyn")
@Stateless(name = "no.statkart.skif.storetest.service.uow.UowTestServiceEJBBean")
@StoreTestEJBInterceptorSpring
@Interceptors(StoreTestEJBInterceptorJEE.class)
@TransactionAttribute(TransactionAttributeType.REQUIRED) // Springs default for metoder er ingen transaksjonshåndtering. Må angi REQUIRED her hvis det skal være default for klassen
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
