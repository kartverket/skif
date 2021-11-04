package no.statkart.skif.skiftest.config;

import com.google.inject.Injector;
import no.statkart.skif.service.ServerInjector;

/**
 * Lokalt interface for injector-EJB.
 *
 * @author Tor Egil R. Strand
 */
public interface SkifTestTxManagementServerInjector extends ServerInjector {
    Injector getInjector();
}
