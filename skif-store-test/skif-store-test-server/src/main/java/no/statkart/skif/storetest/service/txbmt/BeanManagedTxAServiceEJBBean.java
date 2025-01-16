package no.statkart.skif.storetest.service.txbmt;

import com.google.inject.Inject;
import no.statkart.skif.service.annotation.EJBServiceChain;
import no.statkart.skif.service.ejb.EJBTimedService;
import no.statkart.skif.storetest.config.StoreTestEJBInterceptorJEE;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionManagement;
import jakarta.ejb.TransactionManagementType;
import jakarta.interceptor.Interceptors;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
@RolesAllowed("Innsyn")
@Stateless(name = "no.statkart.skif.storetest.service.txbmt.BeanManagedTxAServiceEJBBean")
@Interceptors(StoreTestEJBInterceptorJEE.class)
@TransactionManagement(TransactionManagementType.BEAN)
public class BeanManagedTxAServiceEJBBean extends EJBTimedService implements BeanManagedTxAService {

    @Inject @EJBServiceChain
    BeanManagedTxAService serviceChain;

    @Override
    public void clear() {
        serviceChain.clear();
    }

    @Override
    public String get(String key) {
        return serviceChain.get(key);
    }

    @Override
    public String put(String key, String value) {
        return serviceChain.put(key, value);
    }

    @Override
    public void multiPut(String key1, String value1, String key2, String value2) {
        serviceChain.multiPut(key1, value1, key2, value2);
    }

}
