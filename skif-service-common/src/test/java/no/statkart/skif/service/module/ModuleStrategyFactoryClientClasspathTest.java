package no.statkart.skif.service.module;

import com.google.inject.Binder;
import com.google.inject.PrivateBinder;
import no.statkart.skif.ServiceMode;
import no.statkart.skif.mapper.Mapping;
import no.statkart.skif.module.EmptyModuleStrategyFactory;
import no.statkart.skif.module.ModuleConfiguration;
import no.statkart.skif.module.ModuleStrategyFactory;
import no.statkart.skif.module.StrategyTuple;
import no.statkart.skif.service.module.common.RemoteServiceModule;
import no.statkart.skif.service.module.common.RemoteServiceModuleStrategy;
import org.testng.annotations.Test;

import java.util.Collection;

import static org.testng.Assert.*;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
@Test
public class ModuleStrategyFactoryClientClasspathTest {

    public void testConfigureDefaultStrategy() {
        ModuleStrategyFactory factory = new EmptyModuleStrategyFactory();

        StrategyTuple<RemoteServiceModuleStrategy> tuple = new StrategyTuple<>();
        tuple.setStrategyClass(ServiceMode.JEE, TestStrategyJEE.class);
        tuple.setStrategyClass(ServiceMode.SINGLE_VM, TestStrategySingleVm.class);
        factory.addPrototype(RemoteServiceModule.class, tuple);

        StrategyTuple<RemoteServiceModuleStrategy> strategyConfig1 = factory.getPrototype(RemoteServiceModule.class);
        StrategyTuple<RemoteServiceModuleStrategy> strategyConfig2 = factory.getPrototype(TestRemoteServiceModule.class);
        assertSame(strategyConfig1, tuple);
        assertNull(strategyConfig2);

        StrategyTuple<RemoteServiceModuleStrategy> strategy1 = factory.createStrategyTuple(RemoteServiceModule.class);
        StrategyTuple<RemoteServiceModuleStrategy> strategy2 = factory.createStrategyTuple(TestRemoteServiceModule.class);
        assertNotSame(strategy1, strategy2);
        assertEquals(strategy1.getStrategyClassName(ServiceMode.JEE), strategy2.getStrategyClassName(ServiceMode.JEE));
        assertEquals(strategy1.getStrategyClassName(ServiceMode.SINGLE_VM), strategy2.getStrategyClassName(ServiceMode.SINGLE_VM));
        assertNotSame(strategy1.getStrategy(ServiceMode.JEE), strategy2.getStrategy(ServiceMode.JEE));
        assertNotSame(strategy1.getStrategy(ServiceMode.SINGLE_VM), strategy2.getStrategy(ServiceMode.SINGLE_VM));
        //noinspection ConstantConditions
        assertEquals(strategy1.getStrategy(ServiceMode.JEE).getClass(), TestStrategyJEE.class) ;
        //noinspection ConstantConditions
        assertEquals(strategy1.getStrategy(ServiceMode.SINGLE_VM).getClass(), TestStrategySingleVm.class) ;
    }

    public void testOverwriteDefaultStrategy() {
        ModuleStrategyFactory factory = new EmptyModuleStrategyFactory();

        StrategyTuple<RemoteServiceModuleStrategy> tuple = new StrategyTuple<>();
        tuple.setStrategyClass(ServiceMode.JEE, TestStrategyJEE.class);
        tuple.setStrategyClass(ServiceMode.SINGLE_VM, TestStrategySingleVm.class);
        factory.addPrototype(RemoteServiceModule.class, tuple);

        StrategyTuple<RemoteServiceModuleStrategy> tuple2 = new StrategyTuple<>();
        tuple2.setStrategyClass(ServiceMode.JEE, TestStrategyJEE.class);
        tuple2.setStrategyClass(ServiceMode.SINGLE_VM, TestStrategySingleVm.class);
        factory.addPrototype(TestRemoteServiceModule.class, tuple2);

        assertSame(factory.getPrototype(RemoteServiceModule.class), tuple);
        assertSame(factory.getPrototype(TestRemoteServiceModule.class), tuple2);
    }

    public abstract static class TestStrategy extends RemoteServiceModuleStrategy {
        @Override
        public <S> void bindService(Binder outerBinder, PrivateBinder innerBinder, Class<S> service) {
            //To change body of implemented methods use File | Settings | File Templates.
        }
    }

    public static class TestStrategyJEE extends TestStrategy {
    }

    public static class TestStrategySingleVm extends TestStrategy {
    }

    public static class TestRemoteServiceModule extends RemoteServiceModule {
        public TestRemoteServiceModule(ModuleConfiguration configuration, Collection<Class<?>> services, Mapping mapping) {
            super(configuration, services, mapping);
        }
    }
}


