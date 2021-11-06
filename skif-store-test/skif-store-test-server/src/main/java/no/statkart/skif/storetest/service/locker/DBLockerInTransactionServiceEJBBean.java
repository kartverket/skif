package no.statkart.skif.storetest.service.locker;

import com.google.inject.Inject;
import no.statkart.skif.service.annotation.EJBServiceChain;
import no.statkart.skif.storetest.config.StoreTestEJBInterceptorJEE;
import no.statkart.skif.storetest.config.StoreTestEJBInterceptorSpring;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.interceptor.Interceptors;

/**
 * @author Tor Egil R. Strand
 * @since 2.1
 */
@RolesAllowed("Innsyn")
@Stateless(name = "no.statkart.skif.storetest.service.locker.DBLockerInTransactionServiceEJBBean")
@Interceptors(StoreTestEJBInterceptorJEE.class)
@StoreTestEJBInterceptorSpring
@TransactionAttribute(TransactionAttributeType.REQUIRED) // Springs default for metoder er ingen transaksjonshåndtering. Må angi REQUIRED her hvis det skal være default for klassen
public class DBLockerInTransactionServiceEJBBean implements DBLockerInTransactionService {

    @Inject @EJBServiceChain
    DBLockerInTransactionService serviceChain;

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED) // TODO: Bør være mandatory
    public int consumeAllLocks(String owner) {
        return serviceChain.consumeAllLocks(owner);
    }
}
