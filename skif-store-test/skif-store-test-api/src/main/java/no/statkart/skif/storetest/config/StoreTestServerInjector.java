package no.statkart.skif.storetest.config;

import com.google.inject.Injector;
import no.statkart.skif.service.ServerInjector;

/**
 * Lokalt interface for injector-EJB.
 *
 * @author Tor Egil R. Strand
 */
public interface StoreTestServerInjector extends ServerInjector {
    Injector getInjector();
}
