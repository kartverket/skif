package no.statkart.skif.skiftest.service.test;

import com.google.inject.Inject;
import no.statkart.skif.ServiceMode;
import no.statkart.skif.SkifModule;
import no.statkart.skif.config.Configuration;
import no.statkart.skif.module.ModuleConfiguration;
import no.statkart.skif.module.ModuleStrategyFactory;
import no.statkart.skif.persistence.jdbc.DummyDataSourceModule;
import no.statkart.skif.service.chain.EJBServiceChainFactorySpecification;
import no.statkart.skif.service.logging.ServerCallLogger;
import no.statkart.skif.service.module.ServerModuleStrategyFactory;
import no.statkart.skif.service.module.client.ClientModuleStrategyFactory;
import no.statkart.skif.service.module.common.RemoteServerModule;
import no.statkart.skif.service.module.common.RemoteServiceModule;
import no.statkart.skif.service.module.server.ServerModule;
import no.statkart.skif.service.module.server.ServerServiceModule;
import no.statkart.skif.service.proxy.EjbLoggingProxyHandler;
import no.statkart.skif.skiftest.config.SkifTestGroup1Services;
import no.statkart.skif.skiftest.config.SkifTestGroupExServices;
import no.statkart.skif.skiftest.exception.SimpleException;
import no.statkart.skif.skiftest.exception.SimpleNonMappedException;
import no.statkart.skif.skiftest.service.SkifTestServiceContext;
import no.statkart.skif.skiftest.service.test1.Test1Service;
import no.statkart.skif.skiftest.service.testex.TestExService;
import no.statkart.skif.skiftest.wsapi.SkifTestServiceContextMapper;
import no.statkart.skif.skiftest.wsapi.mapping.SkifTestMapper;
import no.statkart.skif.util.testsupport.SkifTestCase;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.lang.reflect.Method;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Tester {@link no.statkart.skif.service.proxy.EjbLoggingProxyHandler}.
 *
 * @author Tor Egil R. Strand
 * @since 2.2.1
 */
@Test(groups = "singlevm-required")
public class EjbLoggingProxyTest extends SkifTestCase {
    @Inject
    private Test1Service test1Service;

    @Inject
    private TestExService testExService;

    private static final TestServerCallLogger globalCallLogger = new TestServerCallLogger();

    public EjbLoggingProxyTest() {
        setModuleClass(TestClientModule.class);
        setSingleVmServerModuleClass(TestServerModule.class);
    }

    @BeforeMethod
    public void resetLogger() {
        globalCallLogger.reset();
    }

    public void testLoggingSuccess() {
        Assert.assertEquals(globalCallLogger.getCalls(), 0, "Kall før");
        Assert.assertEquals(globalCallLogger.getReturns(), 0, "Returer før");
        Assert.assertEquals(globalCallLogger.getErrors(), 0, "Feil før");

        test1Service.helloWorld("world");

        Assert.assertEquals(globalCallLogger.getCalls(), 1, "Kall før");
        Assert.assertEquals(globalCallLogger.getReturns(), 1, "Returer før");
        Assert.assertEquals(globalCallLogger.getErrors(), 0, "Feil før");
    }

    @Test
    public void testLoggingException() {
        Assert.assertEquals(globalCallLogger.getCalls(), 0, "Kall før");
        Assert.assertEquals(globalCallLogger.getReturns(), 0, "Returer før");
        Assert.assertEquals(globalCallLogger.getErrors(), 0, "Feil før");

        try {
            testExService.noTx(SimpleException.class.getName(), "abc");
            Assert.fail("Skulle fått en exception");
        } catch (SimpleNonMappedException e) {
            Assert.fail("Skulle ikke fått denne exception");
        } catch (Throwable t) {
            assertThat(t).describedAs("forventet exception").isInstanceOf(SimpleException.class);
        }

        Assert.assertEquals(globalCallLogger.getCalls(), 1, "Kall før");
        Assert.assertEquals(globalCallLogger.getReturns(), 0, "Returer før");
        Assert.assertEquals(globalCallLogger.getErrors(), 1, "Feil før");
    }

    public static class TestServerCallLogger implements ServerCallLogger {
        private int calls = 0;
        private int returns = 0;
        private int errors = 0;

        public int getCalls() {
            return calls;
        }

        public int getReturns() {
            return returns;
        }

        public int getErrors() {
            return errors;
        }

        public void reset() {
            calls = returns = errors = 0;
        }

        @Override
        public void logEjbCall(Method method, Object[] args) {
            ++calls;
        }

        @Override
        public void logEjbReturn(Method method, Object[] args, Object returnValue, long time) {
            ++returns;
        }

        @Override
        public void logEjbError(Method method, Object[] args, Throwable t, long time) {
            ++errors;
        }

        @Override
        public void logWsCall(Method method, Object[] args) {
            Assert.fail("Testen skulle ikke ført til kall hit");
        }

        @Override
        public void logWsReturn(Method method, Object[] args, Object returnValue, long time) {
            Assert.fail("Testen skulle ikke ført til kall hit");
        }

        @Override
        public void logWsError(Method method, Object[] args, Throwable t, long time) {
            Assert.fail("Testen skulle ikke ført til kall hit");
        }
    }

    public static class TestServerModule extends SkifModule {
        public TestServerModule(Configuration configuration) {
            super(configuration);
        }

        @Override
        protected ModuleStrategyFactory defineDefaultModuleStrategyFactory() {
            return new ServerModuleStrategyFactory();
        }

        @Override
        protected void configure() {
            install(new ServerModule(moduleConfiguration).setServiceContextClass(SkifTestServiceContext.class));

            ServerServiceModule serviceModule = new ServerServiceModule(moduleConfiguration, new SkifTestGroup1Services().getServices());
            serviceModule.getStrategy(ServiceMode.SINGLE_VM).setEjbServiceChainFactorySpecification(new EJBServiceChainFactorySpecification(EjbLoggingProxyHandler.class));
            serviceModule.getStrategy(ServiceMode.JEE).setEjbServiceChainFactorySpecification(new EJBServiceChainFactorySpecification(EjbLoggingProxyHandler.class));
            install(serviceModule);

            serviceModule = new ServerServiceModule(moduleConfiguration, new SkifTestGroupExServices().getServices());
            serviceModule.getStrategy(ServiceMode.SINGLE_VM).setEjbServiceChainFactorySpecification(new EJBServiceChainFactorySpecification(EjbLoggingProxyHandler.class));
            serviceModule.getStrategy(ServiceMode.JEE).setEjbServiceChainFactorySpecification(new EJBServiceChainFactorySpecification(EjbLoggingProxyHandler.class));
            install(serviceModule);

            bind(ServerCallLogger.class).toInstance(globalCallLogger);

            install(new DummyDataSourceModule());
        }
    }

    public static class TestClientModule extends SkifModule {
        public TestClientModule(ModuleConfiguration moduleConfiguration) {
            super(moduleConfiguration);
        }

        @Override
        protected ModuleStrategyFactory defineDefaultModuleStrategyFactory() {
            return new ClientModuleStrategyFactory();
        }

        @Override
        protected void configure() {
            install(new RemoteServerModule(moduleConfiguration).setServiceContextClass(SkifTestServiceContext.class));
            install(new RemoteServiceModule(moduleConfiguration, new SkifTestGroup1Services().getServices(), new SkifTestMapper().getMapping()).setServiceContextMapperClass(SkifTestServiceContextMapper.class));
            install(new RemoteServiceModule(moduleConfiguration, new SkifTestGroupExServices().getServices(), new SkifTestMapper().getMapping()).setServiceContextMapperClass(SkifTestServiceContextMapper.class));
        }
    }
}
