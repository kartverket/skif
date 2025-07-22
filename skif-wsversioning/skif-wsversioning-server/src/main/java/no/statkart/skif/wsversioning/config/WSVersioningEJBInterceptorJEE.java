package no.statkart.skif.wsversioning.config;

import com.google.inject.Injector;
import jakarta.ejb.EJB;
import no.statkart.skif.service.ejb.EJBInterceptorJEE;

/**
 * EJB interceptor for WSVersioning-prosjektet.
 *
 * @author Tor Egil R. Strand
 * @since 2.4.0
 */
public class WSVersioningEJBInterceptorJEE extends EJBInterceptorJEE {
    @EJB
    private WSVersioningServerInjector wsVersioningServerInjector;

    @Override
    protected Injector getInjector() {
        return wsVersioningServerInjector.getInjector();
    }
}
