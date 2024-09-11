package no.statkart.skif.storetest.service.locker;

import com.google.inject.Inject;
import no.statkart.skif.service.annotation.EJBServiceChain;
import no.statkart.skif.storetest.config.StoreTestEJBInterceptorJEE;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.interceptor.Interceptors;

/**
 * @author Tor Egil R. Strand
 * @since 2.1
 */
@RolesAllowed("Innsyn")
@Stateless(name = "no.statkart.skif.storetest.service.locker.DBLockerInTransactionServiceEJBBean")
@Interceptors(StoreTestEJBInterceptorJEE.class)
public class DBLockerInTransactionServiceEJBBean implements DBLockerInTransactionService {

    @Inject @EJBServiceChain
    DBLockerInTransactionService serviceChain;

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED) // TODO: Bør være mandatory
    public int consumeAllLocks(String owner) {
        return serviceChain.consumeAllLocks(owner);
    }
}
