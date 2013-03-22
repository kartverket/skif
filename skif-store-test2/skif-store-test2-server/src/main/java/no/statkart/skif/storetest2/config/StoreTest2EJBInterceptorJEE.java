package no.statkart.skif.storetest2.config;

import com.google.inject.Injector;
import no.statkart.skif.service.ejb.EJBInterceptorJEE;

/**
 * EJB Interceptor som definere hvilken injector EJB'ene i skif-store-test serveren skal bruke
 * @author Henrik Fredholm
 * @since 2.2.0
 */
public class StoreTest2EJBInterceptorJEE extends EJBInterceptorJEE {
    @Override
    protected Injector getInjector() {
        return StoreTest2ServerInjector.getInjector();
    }
}
