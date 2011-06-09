package no.statkart.skif.service.module.common;

import com.google.inject.Binder;
import no.statkart.skif.module.ModuleStrategy;

/**
 * @author Henrik Fredholm
 * @since 1.1
 */
public abstract class RemoteServerModuleStrategy extends ModuleStrategy {
    public abstract void configure(Binder binder);
}
