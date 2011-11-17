package no.statkart.skif.service.module.server;

import no.statkart.skif.service.chain.WSServiceChainFactoryBase;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public class WSServerServiceModuleStrategyJEE extends WSServerServiceModuleStrategy {
    public WSServerServiceModuleStrategyJEE() {
        setWsServiceChainFactoryClassForWSI(WSServiceChainFactoryBase.class);
        setWsServiceChainFactoryClassForService(WSServiceChainFactoryBase.class);
    }
}
