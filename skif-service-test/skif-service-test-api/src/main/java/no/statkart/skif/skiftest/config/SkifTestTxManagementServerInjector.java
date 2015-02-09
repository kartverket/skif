package no.statkart.skif.skiftest.config;

import com.google.inject.Injector;

/**
 * Lokalt interface for injector-EJB.
 *
 * @author Tor Egil R. Strand
 */
public interface SkifTestTxManagementServerInjector {
    Injector getInjector();
}
