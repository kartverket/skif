package no.statkart.skif.storetest.config;

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
public class StoreTestEJBInterceptorJEESpring extends EJBInterceptorSpring {
    final private StoreTestServerInjector storeTestServerInjector;

    public StoreTestEJBInterceptorJEESpring(StoreTestServerInjector storeTestServerInjector) {
        this.storeTestServerInjector = storeTestServerInjector;
    }

    @Override
    protected Injector getInjector() {
        return storeTestServerInjector.getInjector();
    }

    @Around("@within(no.statkart.skif.storetest.config.StoreTestEJBInterceptorSpring)")
    public Object aroundService(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        return super.aroundService(proceedingJoinPoint);
    }
}
