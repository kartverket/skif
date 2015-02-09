package no.statkart.skif.storetest.config;

import com.google.inject.Injector;
import no.statkart.skif.service.ejb.EJBInterceptorJEE;

import javax.ejb.EJB;

/**
 * EJB Interceptor som definere hvilken injector EJB'ene i skif-store-test serveren skal bruke
 * @author Henrik Fredholm
 */
public class StoreTestEJBInterceptorJEE extends EJBInterceptorJEE {
    @EJB
    private StoreTestServerInjector storeTestServerInjector;

    @Override
    protected Injector getInjector() {
        return storeTestServerInjector.getInjector();
    }
}
