package no.statkart.skif.service.module.common;

import com.google.inject.Binder;
import no.statkart.skif.module.ModuleStrategy;

/**
 * @author Tor Egil R. Strand
 * @since 2.1
 */
public abstract class RemoteWSServiceModuleStrategy extends ModuleStrategy {

    public abstract void requireBindings(Binder binder);

    public abstract <S> void bindCallServiceChainFactoryForService(Binder outerBinder, Class<S> service);

    public abstract <S> void bindService(Binder outerBinder, Class<S> service);
}
