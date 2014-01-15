package no.statkart.skif.store.module.common;

import no.statkart.skif.service.module.common.RemoteServiceModuleStrategyJEE;
import no.statkart.skif.service.proxy.D2WAdapterWithServiceContextSVMapperProxyHandler;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public class RemoteServiceModuleStrategyWithServiceContextSVMapperJEE extends RemoteServiceModuleStrategyJEE {

    public RemoteServiceModuleStrategyWithServiceContextSVMapperJEE() {
        super(D2WAdapterWithServiceContextSVMapperProxyHandler.class);
    }
}
