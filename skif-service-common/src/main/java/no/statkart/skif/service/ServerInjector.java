package no.statkart.skif.service;

import com.google.inject.Injector;

/**
 * Interface som gir adgang til Guice injectoren som ServerInjector klasser holder på.
 */
public interface ServerInjector {
    Injector getInjector();
}
