package no.statkart.skif.storetest.config;

import com.google.inject.Injector;
import no.statkart.skif.service.ejb.EJBInterceptorJEE;

/**
 * EJB Interceptor som definere hvilken injector EJB'ene i skif-test-severen skal bruke
 * @author Henrik Fredholm
 */
public class NonhistTestEJBInterceptorJEE extends EJBInterceptorJEE {
    @Override
    protected Injector getInjector() {
        return NonhistTestServerInjector.getInjector();
    }
}
