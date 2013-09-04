package no.statkart.skif.wsversioning.config;

import com.google.inject.Injector;
import no.statkart.skif.service.ejb.EJBInterceptorJEE;

/**
 * EJB interceptor for WSVersioning-prosjektet.
 *
 * @author Tor Egil R. Strand
 * @since 2.4.0
 */
public class WSVersioningEJBInterceptorJEE extends EJBInterceptorJEE {
    @Override
    protected Injector getInjector() {
        return WSVersioningServerInjector.getInjector();
    }
}
