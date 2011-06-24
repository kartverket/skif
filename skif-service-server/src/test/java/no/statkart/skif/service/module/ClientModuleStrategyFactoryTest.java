package no.statkart.skif.service.module;

import no.statkart.skif.ServiceMode;
import no.statkart.skif.module.ModuleStrategyFactory;
import no.statkart.skif.module.StrategyTuple;
import no.statkart.skif.service.chain.ClientCallServiceChainFactoryJEE;
import no.statkart.skif.service.chain.ClientCallServiceChainFactorySingleVm;
import no.statkart.skif.service.chain.CallServiceChainFactory;
import no.statkart.skif.service.module.common.RemoteServerModule;
import no.statkart.skif.service.module.common.RemoteServiceModule;
import no.statkart.skif.service.module.common.RemoteServiceModuleStrategy;
import org.testng.annotations.Test;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertSame;

/**
 * @author Henrik Fredholm
 * @since 1.1
 */
@Test
public class ClientModuleStrategyFactoryTest  {
    public void testCreateInstance() {
        ModuleStrategyFactory factory = new ClientModuleStrategyFactory();
        final StrategyTuple<RemoteServiceModuleStrategy> remoteServiceStrategy = factory.getPrototype(RemoteServiceModule.class);
        assertNotNull(remoteServiceStrategy);

        final Class<? extends CallServiceChainFactory> callServiceChainFactoryClassSingleVm =
                remoteServiceStrategy.getStrategy(ServiceMode.SINGLE_VM).getCallServiceChainFactoryClass();
        assertSame(callServiceChainFactoryClassSingleVm, ClientCallServiceChainFactorySingleVm.class);

        final Class<? extends CallServiceChainFactory> callServiceChainFactoryClassJEE =
                remoteServiceStrategy.getStrategy(ServiceMode.JEE).getCallServiceChainFactoryClass();
        assertSame(callServiceChainFactoryClassJEE, ClientCallServiceChainFactoryJEE.class);


        assertNotNull(factory.getPrototype(RemoteServerModule.class));

    }


}
