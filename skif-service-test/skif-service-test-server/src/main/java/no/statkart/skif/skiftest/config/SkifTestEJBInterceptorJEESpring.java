package no.statkart.skif.skiftest.config;

import com.google.inject.Injector;
import no.statkart.skif.service.ejb.EJBInterceptorSpring;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * EJB Interceptor som definere hvilken injector EJB'ene i skif-test-severen skal bruke
 * @author Henrik Fredholm
 */
@Aspect
@Component
public class SkifTestEJBInterceptorJEESpring extends EJBInterceptorSpring {

    private final SkifTestServerInjector skifTestServerInjector;

    public SkifTestEJBInterceptorJEESpring(SkifTestServerInjector skifTestServerInjector) {
        this.skifTestServerInjector = skifTestServerInjector;
    }

    @Override
    protected Injector getInjector() {
        return skifTestServerInjector.getInjector();
    }

    @Around("@within(no.statkart.skif.skiftest.config.SkifTestEJBInterceptorSpring)")
    public Object aroundService(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        return super.aroundService(proceedingJoinPoint);
    }
}
