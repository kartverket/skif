package no.statkart.skif.storetest.config;

import com.google.inject.Injector;

/**
 * Lokalt interface for injector-EJB.
 *
 * @author Tor Egil R. Strand
 */
public interface StoreTestServerInjector {
    Injector getInjector();
}
