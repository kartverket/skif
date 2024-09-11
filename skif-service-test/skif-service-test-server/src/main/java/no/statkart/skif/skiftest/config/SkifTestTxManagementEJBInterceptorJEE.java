package no.statkart.skif.skiftest.config;

import com.google.inject.Injector;
import no.statkart.skif.service.ejb.EJBInterceptorJEE;

import jakarta.ejb.EJB;

/**
 * EJB Interceptor som definere hvilken injector EJB'ene i skif-test-serveren skal bruke
 * @author Henrik Fredholm
 */
public class SkifTestTxManagementEJBInterceptorJEE extends EJBInterceptorJEE {
    @EJB
    private SkifTestTxManagementServerInjector skifTestTxManagementServerInjector;

    @Override
    protected Injector getInjector() {
        return skifTestTxManagementServerInjector.getInjector();
    }
}
