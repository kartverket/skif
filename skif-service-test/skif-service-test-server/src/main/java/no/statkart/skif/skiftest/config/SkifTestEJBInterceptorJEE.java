package no.statkart.skif.skiftest.config;

import com.google.inject.Injector;
import no.statkart.skif.service.ejb.EJBInterceptorJEE;

import jakarta.ejb.EJB;

/**
 * EJB Interceptor som definere hvilken injector EJB'ene i skif-test-severen skal bruke
 * @author Henrik Fredholm
 */
public class SkifTestEJBInterceptorJEE extends EJBInterceptorJEE {
    @EJB
    private SkifTestServerInjector skifTestServerInjector;

    @Override
    protected Injector getInjector() {
        return skifTestServerInjector.getInjector();
    }
}
