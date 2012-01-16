package no.statkart.skif.service.module.server;

import no.statkart.skif.service.chain.WSServiceChainFactoryBase;

/**
 * Denne er kun ment for testing av webservice i single-VM.
 *
 * @author Tor Egil R. Strand
 * @since 2.1
 */
public class WSServerServiceModuleStrategySingleVm extends WSServerServiceModuleStrategy {
    public WSServerServiceModuleStrategySingleVm() {
        setWsServiceChainFactoryClassForWSI(WSServiceChainFactoryBase.class);
        setWsServiceChainFactoryClassForService(WSServiceChainFactoryBase.class);
    }
}
