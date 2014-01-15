package no.statkart.skif.store.module.common;

import no.statkart.skif.service.module.common.RemoteServiceModuleStrategySingleVm;
import no.statkart.skif.service.proxy.SingleVmNoWSWithServiceContextSVMapperMapperRemoteCallProxyHandler;

/**
 * @author Henrik Fredholm
 * @since 2.4
 * @see  RemoteServiceModuleStrategyWithServiceContextSVMapper
 */
@SuppressWarnings("UnusedDeclaration") // Reflection
public class RemoteServiceModuleStrategyWithServiceContextSVMapperSingleVm extends RemoteServiceModuleStrategySingleVm {

    public RemoteServiceModuleStrategyWithServiceContextSVMapperSingleVm() {
        super(SingleVmNoWSWithServiceContextSVMapperMapperRemoteCallProxyHandler.class);
    }
}
