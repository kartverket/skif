package no.statkart.skif.service;

import com.google.inject.Injector;

/**
 * @author Henrik Fredholm
 * @since 1.1
 */
public class SingleVmServer {
    private Injector injector;

    public SingleVmServer(Injector serverInjector) {
        this.injector = serverInjector;
    }
    public Injector getInjector() {
        return injector;
    }
}
