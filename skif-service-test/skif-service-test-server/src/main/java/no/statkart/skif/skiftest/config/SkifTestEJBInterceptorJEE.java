package no.statkart.skif.skiftest.config;

import com.google.inject.Injector;
import no.statkart.skif.service.ejb.EJBInterceptorJEE;

/**
 * GrunnbokFast EJB Interceptor som definere hvilken injector EJB'ene skal bruke
 * @author Henrik Fredholm
 */
public class SkifTestEJBInterceptorJEE extends EJBInterceptorJEE {
    @Override
    protected Injector getInjector() {
        return SkifTestServerInjector.getInjector();
    }
}
