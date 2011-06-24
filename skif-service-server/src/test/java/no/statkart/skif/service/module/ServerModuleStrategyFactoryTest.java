package no.statkart.skif.service.module;

import no.statkart.skif.ServiceMode;
import no.statkart.skif.module.ModuleStrategyFactory;
import no.statkart.skif.module.StrategyTuple;
import no.statkart.skif.service.chain.EJBServiceChainFactoryDefaultImpl;
import no.statkart.skif.service.chain.ImplementationServiceChainFactoryDefaultImpl;
import no.statkart.skif.service.chain.ServerCallServiceChainFactory;
import no.statkart.skif.service.module.server.ServerServiceModule;
import no.statkart.skif.service.module.server.ServerServiceModuleStrategy;
import org.testng.annotations.Test;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertSame;

/**
 * @author Henrik Fredholm
 * @since 1.1
 */
@Test
public class ServerModuleStrategyFactoryTest {
    public void testCreateInstance() {
        ModuleStrategyFactory factory = new ServerModuleStrategyFactory();
        final StrategyTuple<ServerServiceModuleStrategy> serverImplServiceStrategy = factory.getPrototype(ServerServiceModule.class);
        assertNotNull(serverImplServiceStrategy);

        // Test SingleVm default settings
        assertSame(serverImplServiceStrategy.getStrategy(ServiceMode.SINGLE_VM).getCallServiceChainFactoryClass(), ServerCallServiceChainFactory.class);
        assertSame(serverImplServiceStrategy.getStrategy(ServiceMode.SINGLE_VM).getEjbServiceChainFactoryClass(), EJBServiceChainFactoryDefaultImpl.class);
        assertSame(serverImplServiceStrategy.getStrategy(ServiceMode.SINGLE_VM).getImplementationServiceChainFactoryClass(), ImplementationServiceChainFactoryDefaultImpl.class);

        // Test JEE default settings
        assertSame(serverImplServiceStrategy.getStrategy(ServiceMode.JEE).getCallServiceChainFactoryClass(), ServerCallServiceChainFactory.class);
        assertSame(serverImplServiceStrategy.getStrategy(ServiceMode.JEE).getEjbServiceChainFactoryClass(), EJBServiceChainFactoryDefaultImpl.class);
        assertSame(serverImplServiceStrategy.getStrategy(ServiceMode.JEE).getImplementationServiceChainFactoryClass(), ImplementationServiceChainFactoryDefaultImpl.class);

    }


}
