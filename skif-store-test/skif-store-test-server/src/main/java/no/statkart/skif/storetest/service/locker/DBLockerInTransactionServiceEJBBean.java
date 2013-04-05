package no.statkart.skif.storetest.service.locker;

import com.google.inject.Inject;
import no.statkart.skif.service.annotation.EJBServiceChain;
import no.statkart.skif.storetest.config.StoreTestEJBInterceptorJEE;

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
public class DBLockerInTransactionServiceEJBBean implements DBLockerInTransactionService {

    @Inject @EJBServiceChain
    DBLockerInTransactionService serviceChain;

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED) // TODO: Bør være mandatory
    public int consumeAllLocks(String owner) {
        return serviceChain.consumeAllLocks(owner);
    }
}
