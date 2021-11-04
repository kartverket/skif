package no.statkart.skif.skiftest.config;

import com.google.inject.Injector;
import no.statkart.skif.service.ejb.EJBInterceptorSpring;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * EJB Interceptor som definere hvilken injector EJB'ene i skif-test-serveren skal bruke
 * @author Henrik Fredholm
 */
@Aspect
@Component
public class SkifTestTxManagementEJBInterceptorJEESpring extends EJBInterceptorSpring {
    private final SkifTestTxManagementServerInjector skifTestTxManagementServerInjector;

    public SkifTestTxManagementEJBInterceptorJEESpring(SkifTestTxManagementServerInjector skifTestTxManagementServerInjector) {
        this.skifTestTxManagementServerInjector = skifTestTxManagementServerInjector;
    }

    @Override
    protected Injector getInjector() {
        return skifTestTxManagementServerInjector.getInjector();
    }

    @Around("@within(no.statkart.skif.skiftest.config.SkifTestTxManagementEJBInterceptorSpring)")
    public Object aroundService(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        return super.aroundService(proceedingJoinPoint);
    }

}
