package no.statkart.skif.service.module.common;

import com.google.inject.Binder;
import com.google.inject.Injector;
import no.statkart.skif.config.ConfigurationConstants;
import no.statkart.skif.service.SingleVmServer;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public class RemoteServerModuleStrategySingleVm extends RemoteServerModuleStrategy {
    protected Injector injector;

    @Override
    public void configure(Binder binder) {
        if (injector==null) {
            injector = (Injector) getConfiguration().getProperty(ConfigurationConstants.SINGLE_VM_SERVER_INJECTOR);
        }
        binder.bind(SingleVmServer.class).toInstance(new SingleVmServer(injector));
    }
}
