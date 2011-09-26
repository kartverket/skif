package no.statkart.skif.storetest.service2.storetest12;

import com.google.inject.Inject;
import no.statkart.skif.service.annotation.EJBServiceChain;
import no.statkart.skif.service.ejb.EJBTimedService;
import no.statkart.skif.storetest.config.StoreTestEJBInterceptorJEE;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.interceptor.Interceptors;

/**
 * @author Roar Ingebrigtsen
 * @since 0.6
 */
@RolesAllowed("Innsyn")
@Stateless(name = "no.statkart.skif.storetest.service.test1.Test1Service2EJBBean")
@Interceptors(StoreTestEJBInterceptorJEE.class)
public class StoreTest1Service2EJBBean extends EJBTimedService implements StoreTest1Service2 {

    @Inject @EJBServiceChain
    StoreTest1Service2 serviceChain;

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public String put(String key, String value) {
        return serviceChain.put(key,value);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public String get(String key) {
        return serviceChain.get(key);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public String remove(String key) {
        return serviceChain.remove(key);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void clear() {
        serviceChain.clear();
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public String putThatFails(String key, String value) {
        return serviceChain.putThatFails(key, value);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public String putViaJDBCConnection(String key, String value) {
        return serviceChain.putViaJDBCConnection(key, value);
    }


}
