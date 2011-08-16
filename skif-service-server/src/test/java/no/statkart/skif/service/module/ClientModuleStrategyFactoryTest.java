package no.statkart.skif.service.module;

import no.statkart.skif.ServiceMode;
import no.statkart.skif.module.ModuleStrategyFactory;
import no.statkart.skif.module.StrategyTuple;
import no.statkart.skif.service.chain.CallServiceChainFactorySpecification;
import no.statkart.skif.service.chain.ClientCallServiceChainFactoryJEE;
import no.statkart.skif.service.chain.ClientCallServiceChainFactorySingleVm;
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

        final CallServiceChainFactorySpecification callServiceChainFactorySpecificationSingleVm =
                remoteServiceStrategy.getStrategy(ServiceMode.SINGLE_VM).getCallServiceChainFactorySpecification() ;
        assertSame(callServiceChainFactorySpecificationSingleVm.getFactoryClass(), ClientCallServiceChainFactorySingleVm.class);

        final CallServiceChainFactorySpecification callServiceChainFactorySpecificationJEE =
                remoteServiceStrategy.getStrategy(ServiceMode.JEE).getCallServiceChainFactorySpecification() ;
        assertSame(callServiceChainFactorySpecificationJEE.getFactoryClass(), ClientCallServiceChainFactoryJEE.class);

        assertNotNull(factory.getPrototype(RemoteServerModule.class));

    }


}
