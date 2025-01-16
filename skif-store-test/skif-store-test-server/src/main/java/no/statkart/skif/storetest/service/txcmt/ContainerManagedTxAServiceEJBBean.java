package no.statkart.skif.storetest.service.txcmt;

import com.google.inject.Inject;
import no.statkart.skif.service.annotation.EJBServiceChain;
import no.statkart.skif.service.ejb.EJBTimedService;
import no.statkart.skif.storetest.config.StoreTestEJBInterceptorJEE;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.*;
import jakarta.interceptor.Interceptors;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
@RolesAllowed("Innsyn")
@Stateless(name = "no.statkart.skif.storetest.service.txcmt.ContainerManagedTxAServiceEJBBean")
@Interceptors(StoreTestEJBInterceptorJEE.class)
@TransactionManagement(TransactionManagementType.CONTAINER)
public class ContainerManagedTxAServiceEJBBean extends EJBTimedService implements ContainerManagedTxAService {

    @Inject @EJBServiceChain
    ContainerManagedTxAService serviceChain;

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void clear() {
        serviceChain.clear();
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public String get(String key) {
        return serviceChain.get(key);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public String put(String key, String value) {
        return serviceChain.put(key, value);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void multiPut(String key1, String value1, String key2, String value2) {
        serviceChain.multiPut(key1, value1, key2, value2);
    }
}
