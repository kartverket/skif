package no.statkart.skif.service.module;

import com.google.inject.Binder;
import com.google.inject.PrivateBinder;
import no.statkart.skif.ServiceMode;
import no.statkart.skif.SkifConstants;
import no.statkart.skif.module.StrategyTuple;
import no.statkart.skif.service.module.common.RemoteServiceModuleStrategy;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
@Test
public class StrategyTupleTest {

    public void testNewStrategyTupleWithJustJEE() {
        RemoteServiceModuleStrategy REMOTE_STRATEGY_JEE = new TestStrategyJEE();

        StrategyTuple<RemoteServiceModuleStrategy> tuple = new StrategyTuple<RemoteServiceModuleStrategy>();
        tuple.setStrategy(ServiceMode.JEE, REMOTE_STRATEGY_JEE);
        assertSame(tuple.getStrategy(ServiceMode.JEE), REMOTE_STRATEGY_JEE);
        assertSame(tuple.getStrategyClass(ServiceMode.JEE), REMOTE_STRATEGY_JEE.getClass());
        assertEquals(tuple.getStrategyClassName(ServiceMode.JEE), REMOTE_STRATEGY_JEE.getClass().getName());

        assertNull(tuple.getStrategy(ServiceMode.SINGLE_VM));
        assertNull(tuple.getStrategyClass(ServiceMode.SINGLE_VM));
        assertNull(tuple.getStrategyClassName(ServiceMode.SINGLE_VM));
    }

    public void testNewStrategyTupleWithJustSingleVm() {
        RemoteServiceModuleStrategy REMOTE_STRATEGY_SVM = new TestStrategySingleVm();
        StrategyTuple<RemoteServiceModuleStrategy> tuple = new StrategyTuple<RemoteServiceModuleStrategy>();
        tuple.setStrategy(ServiceMode.SINGLE_VM, REMOTE_STRATEGY_SVM);
        assertSame(tuple.getStrategy(ServiceMode.SINGLE_VM), REMOTE_STRATEGY_SVM);
        assertSame(tuple.getStrategyClass(ServiceMode.SINGLE_VM), REMOTE_STRATEGY_SVM.getClass());
        assertEquals(tuple.getStrategyClassName(ServiceMode.SINGLE_VM), REMOTE_STRATEGY_SVM.getClass().getName());

        assertNull(tuple.getStrategy(ServiceMode.JEE));
        assertNull(tuple.getStrategyClass(ServiceMode.JEE));
        assertNull(tuple.getStrategyClassName(ServiceMode.JEE));
    }

    public void testNewStrategyTupleWithJEEAndSingleVm() {
        RemoteServiceModuleStrategy REMOTE_STRATEGY_SVM = new TestStrategySingleVm();
        RemoteServiceModuleStrategy REMOTE_STRATEGY_JEE = new TestStrategyJEE();
        StrategyTuple<RemoteServiceModuleStrategy> tuple = new StrategyTuple<RemoteServiceModuleStrategy>();

        tuple.setStrategy(ServiceMode.JEE, REMOTE_STRATEGY_JEE);
        assertSame(tuple.getStrategy(ServiceMode.JEE), REMOTE_STRATEGY_JEE);
        assertSame(tuple.getStrategyClass(ServiceMode.JEE), REMOTE_STRATEGY_JEE.getClass());
        assertEquals(tuple.getStrategyClassName(ServiceMode.JEE), REMOTE_STRATEGY_JEE.getClass().getName());

        tuple.setStrategy(ServiceMode.SINGLE_VM, REMOTE_STRATEGY_SVM);
        assertSame(tuple.getStrategy(ServiceMode.SINGLE_VM), REMOTE_STRATEGY_SVM);
        assertSame(tuple.getStrategyClass(ServiceMode.SINGLE_VM), REMOTE_STRATEGY_SVM.getClass());
        assertEquals(tuple.getStrategyClassName(ServiceMode.SINGLE_VM), REMOTE_STRATEGY_SVM.getClass().getName());
    }


    public void testNewStrategyConfigElementWithNoSingleVmClassOnClasspath() {
        StrategyTuple<RemoteServiceModuleStrategy> tuple = new StrategyTuple<RemoteServiceModuleStrategy>(AnotherStrategy.class);
        assertEquals(tuple.getStrategyClassName(ServiceMode.JEE), AnotherStrategyJEE.class.getName());
        assertEquals(tuple.getStrategyClassName(ServiceMode.SINGLE_VM), AnotherStrategy.class.getName() + SkifConstants.SINGLE_VM_POSTFIX);

        assertNotNull(tuple.getStrategy(ServiceMode.JEE));
        assertSame(tuple.getStrategy(ServiceMode.JEE).getClass(), AnotherStrategyJEE.class);

        try {
            assertNotNull(tuple.getStrategy(ServiceMode.SINGLE_VM));
            fail("Forventet exception");
        } catch (RuntimeException e) {
            //OK
        }
    }

    public void testCloneBeforeGetInstance() {
        StrategyTuple<RemoteServiceModuleStrategy> tuple = new StrategyTuple<RemoteServiceModuleStrategy>(TestStrategy.class);
        StrategyTuple<RemoteServiceModuleStrategy> tupleClone = tuple.clone();
        assertNotNull(tupleClone.getStrategy(ServiceMode.JEE));
        assertNotNull(tupleClone.getStrategy(ServiceMode.SINGLE_VM));
    }

    public void testCloneAfterGetInstance() {
        StrategyTuple<RemoteServiceModuleStrategy> tuple = new StrategyTuple<RemoteServiceModuleStrategy>(TestStrategy.class);
        assertNotNull(tuple.getStrategy(ServiceMode.JEE));
        StrategyTuple<RemoteServiceModuleStrategy> tupleClone = tuple.clone();
        assertNotNull(tupleClone.getStrategy(ServiceMode.JEE));
        assertNotNull(tupleClone.getStrategy(ServiceMode.SINGLE_VM));
    }

    public static abstract class TestStrategy extends RemoteServiceModuleStrategy {
        @Override
        public <S> void bindService(Binder outerBinder, PrivateBinder innerBinder, Class<S> service) {
            //To change body of implemented methods use File | Settings | File Templates.
        }
    }

    public static class TestStrategyJEE extends TestStrategy {
    }

    public static class TestStrategySingleVm extends TestStrategy {
    }


    public static abstract class AnotherStrategy extends TestStrategy {
    }

    public static class AnotherStrategyJEE extends AnotherStrategy {
    }

}

