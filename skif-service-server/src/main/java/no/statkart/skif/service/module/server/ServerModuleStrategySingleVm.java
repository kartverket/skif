package no.statkart.skif.service.module.server;

import com.google.inject.Binder;
import no.statkart.skif.service.SingleVmRemoteCallContext;
import no.statkart.skif.service.scope.ServiceRequestScoped;

/**
 * @author Henrik Fredholm
 * @since 1.1
 */
public class ServerModuleStrategySingleVm extends ServerModuleStrategy {

    @Override
    public void configure(Binder binder) {
        binder.bind(SingleVmRemoteCallContext.class).in(ServiceRequestScoped.class);
    }
}
