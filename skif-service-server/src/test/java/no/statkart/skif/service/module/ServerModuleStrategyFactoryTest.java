package no.statkart.skif.service.module;

import no.statkart.skif.ServiceMode;
import no.statkart.skif.module.ModuleStrategyFactory;
import no.statkart.skif.module.StrategyTuple;
import no.statkart.skif.service.chain.EJBServiceChainFactoryImpl;
import no.statkart.skif.service.chain.ImplementationServiceChainFactoryImpl;
import no.statkart.skif.service.chain.ServerCallServiceChainFactory;
import no.statkart.skif.service.module.server.ServerServiceModule;
import no.statkart.skif.service.module.server.ServerServiceModuleStrategy;
import org.testng.annotations.Test;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertSame;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
@Test
public class ServerModuleStrategyFactoryTest {
    @SuppressWarnings("ConstantConditions")
    public void testCreateInstance() {
        ModuleStrategyFactory factory = new ServerModuleStrategyFactory();
        final StrategyTuple<ServerServiceModuleStrategy> serverImplServiceStrategy = factory.getPrototype(ServerServiceModule.class);
        assertNotNull(serverImplServiceStrategy);

        // Test SingleVm default settings
        assertSame(serverImplServiceStrategy.getStrategy(ServiceMode.SINGLE_VM).getCallServiceChainFactorySpecification().getFactoryClass() , ServerCallServiceChainFactory.class);
        assertSame(serverImplServiceStrategy.getStrategy(ServiceMode.SINGLE_VM).getEjbServiceChainFactorySpecification().getFactoryClass(), EJBServiceChainFactoryImpl.class);
        assertSame(serverImplServiceStrategy.getStrategy(ServiceMode.SINGLE_VM).getImplementationServiceChainFactorySpecification().getFactoryClass(), ImplementationServiceChainFactoryImpl.class);

        // Test JEE default settings
        assertSame(serverImplServiceStrategy.getStrategy(ServiceMode.JEE).getCallServiceChainFactorySpecification().getFactoryClass(), ServerCallServiceChainFactory.class);
        assertSame(serverImplServiceStrategy.getStrategy(ServiceMode.JEE).getEjbServiceChainFactorySpecification().getFactoryClass(), EJBServiceChainFactoryImpl.class);
        assertSame(serverImplServiceStrategy.getStrategy(ServiceMode.JEE).getImplementationServiceChainFactorySpecification().getFactoryClass(), ImplementationServiceChainFactoryImpl.class);

    }


}
