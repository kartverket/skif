package no.statkart.skif.store.module.server;

import com.google.inject.Binder;
import no.statkart.skif.module.ModuleStrategy;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public abstract class ServerStoreModuleStrategy extends ModuleStrategy {
    public abstract void configure(Binder binder);

}
