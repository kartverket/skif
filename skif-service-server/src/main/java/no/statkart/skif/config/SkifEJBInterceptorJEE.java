package no.statkart.skif.config;

import com.google.inject.Injector;
import no.statkart.skif.service.ejb.EJBInterceptorJEE;

/**
 * @author Roar Ingebrigtsen
 * @since 2.0
 */
public class SkifEJBInterceptorJEE extends EJBInterceptorJEE {
    @Override
    protected Injector getInjector() {
        return SkifServerInjector.getInjector();
    }
}
