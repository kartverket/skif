package no.statkart.skif.storetest.service.storetest1;

import com.google.inject.Inject;
import no.statkart.skif.service.annotation.EJBServiceChain;
import no.statkart.skif.service.ejb.EJBTimedService;
import no.statkart.skif.storetest.config.StoreTestEJBInterceptorJEE;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

/**
 * @author Roar Ingebrigtsen
 * @since 0.6
 */
@RolesAllowed("Innsyn")
@Stateless(name = "no.statkart.skif.storetest.service.test1.Test1ServiceEJBBean")
@Interceptors(StoreTestEJBInterceptorJEE.class)
public class StoreTest1ServiceEJBBean extends EJBTimedService implements StoreTest1Service {

    @Inject @EJBServiceChain
    StoreTest1Service serviceChain;

    @Override
    public String put(String key, String value) {
        return serviceChain.put(key,value);
    }

    @Override
    public String get(String key) {
        return serviceChain.get(key);
    }

    @Override
    public String remove(String key) {
        return serviceChain.remove(key);
    }

    @Override
    public void clear() {
        serviceChain.clear();
    }
}
