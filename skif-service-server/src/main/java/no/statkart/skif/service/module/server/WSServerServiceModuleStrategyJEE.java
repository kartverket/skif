package no.statkart.skif.service.module.server;

import no.statkart.skif.service.chain.WSServiceChainFactoryBase;

/**
 * @author Henrik Fredholm
 * @since 1.1
 */
public class WSServerServiceModuleStrategyJEE extends WSServerServiceModuleStrategy {
    public WSServerServiceModuleStrategyJEE() {
        setWsServiceChainFactoryClassForWSI(WSServiceChainFactoryBase.class);
        setWsServiceChainFactoryClassForService(WSServiceChainFactoryBase.class);
    }
}
