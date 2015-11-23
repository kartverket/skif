package no.statkart.skif.wsversioning.config;

import com.google.inject.Injector;

/**
 * Lokalt interface for injector-EJB.
 */
public interface WSVersioningServerInjector {
    Injector getInjector();
}
