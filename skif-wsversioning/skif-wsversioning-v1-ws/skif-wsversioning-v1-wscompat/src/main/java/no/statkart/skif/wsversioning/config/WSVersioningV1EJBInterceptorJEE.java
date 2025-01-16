package no.statkart.skif.wsversioning.config;

import com.google.inject.Injector;
import no.statkart.skif.service.ejb.EJBInterceptorJEE;

import jakarta.ejb.EJB;

/**
 * EJB interceptor for WSVersioning-prosjektet.
 *
 * @author Tor Egil R. Strand
 * @since 2.4.0
 */
public class WSVersioningV1EJBInterceptorJEE extends EJBInterceptorJEE {
    @EJB
    private WSVersioningServerV1Injector wsVersioningServerV1Injector;

    @Override
    protected Injector getInjector() {
        return wsVersioningServerV1Injector.getInjector();
    }
}
