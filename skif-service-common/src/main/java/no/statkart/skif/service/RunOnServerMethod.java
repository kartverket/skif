package no.statkart.skif.service;

import com.google.inject.Injector;

/**
 * @author Henrik Fredholm
 */
public abstract class RunOnServerMethod {
    protected transient Injector injector;

    public final void init(Injector injector) {
        this.injector = injector;
        injector.injectMembers(this);
    }
    public abstract Object run();
}
