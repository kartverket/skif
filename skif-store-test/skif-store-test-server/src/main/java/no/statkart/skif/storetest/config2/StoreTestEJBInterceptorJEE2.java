package no.statkart.skif.storetest.config2;

import com.google.inject.Injector;
import no.statkart.skif.service.ejb.EJBInterceptorJEE;

/**
 * EJB Interceptor som definere hvilken injector EJB'ene i skif-store-test serveren skal bruke
 * @author Henrik Fredholm
 */
public class StoreTestEJBInterceptorJEE2 extends EJBInterceptorJEE {
    @Override
    protected Injector getInjector() {
        return StoreTestServerInjector2.getInjector();
    }
}
