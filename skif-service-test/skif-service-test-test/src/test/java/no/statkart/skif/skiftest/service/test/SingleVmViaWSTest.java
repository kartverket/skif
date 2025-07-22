package no.statkart.skif.skiftest.service.test;

import com.google.inject.Inject;
import no.statkart.skif.SkifModule;
import no.statkart.skif.module.ModuleConfiguration;
import no.statkart.skif.module.ModuleStrategyFactory;
import no.statkart.skif.module.StrategyTuple;
import no.statkart.skif.service.module.client.ClientModuleStrategyFactory;
import no.statkart.skif.service.module.common.RemoteServerModule;
import no.statkart.skif.service.module.common.RemoteServiceModule;
import no.statkart.skif.service.module.common.RemoteServiceModuleStrategy;
import no.statkart.skif.service.module.common.RemoteServiceModuleStrategySingleVmXml;
import no.statkart.skif.skiftest.config.SkifTestGroup2Services;
import no.statkart.skif.skiftest.config.SkifTestServerModule;
import no.statkart.skif.skiftest.domain.A;
import no.statkart.skif.skiftest.domain.B;
import no.statkart.skif.skiftest.exception.SimpleException;
import no.statkart.skif.skiftest.service.SkifTestServiceContext;
import no.statkart.skif.skiftest.service.test2.Test2Service;
import no.statkart.skif.skiftest.service.test3.Test3Service;
import no.statkart.skif.skiftest.wsapi.exception.simple.mapping.SkifTestSimpleExceptionMapper;
import no.statkart.skif.skiftest.wsapi.mapping.SkifTestMapper;
import no.statkart.skif.util.testsupport.SkifTestCase;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Test av SingleVM-kall via XML.
 *
 * @author Tor Egil R. Strand
 * @since 2.3.0
 */
@Test(groups = "singlevm-required")
public class SingleVmViaWSTest extends SkifTestCase {
    @Inject
    private Test2Service test2Service;
    @Inject
    private Test3Service test3Service;

    public SingleVmViaWSTest() {
        setSingleVmServerModuleClass(SkifTestServerModule.class);
        setModuleClass(ClientModule.class);
    }

    public void testTestService2A2B() {
        A a = new A("Test");
        B b = test2Service.a2B(a);
        Assert.assertEquals(b.getText(), a.getText());
    }

    public void testExceptionMapping() {
        try {
            test3Service.testExceptionThrowing("SimpleException", "Test");
            Assert.fail("Skulle fått exception");
        } catch (SimpleException e) {
            Assert.assertEquals(e.getMessage(), "Test");
        }
    }

    public static class ClientModule extends SkifModule {
        public ClientModule(ModuleConfiguration moduleConfiguration) {
            super(moduleConfiguration);
        }

        @Override
        protected ModuleStrategyFactory defineDefaultModuleStrategyFactory() {
            ClientModuleStrategyFactory clientModuleStrategyFactory = new ClientModuleStrategyFactory();

            clientModuleStrategyFactory.addPrototype(RemoteServiceModule.class, new StrategyTuple<RemoteServiceModuleStrategy>(null, RemoteServiceModuleStrategySingleVmXml.class));
//            clientModuleStrategyFactory.addPrototype(RemoteServiceModule.class, new StrategyTuple<RemoteServiceModuleStrategy>(null, RemoteServiceModuleStrategySingleVmXmlWithSVMapping.class));

            return clientModuleStrategyFactory;
        }

        @Override
        protected void configure() {
            install(new RemoteServerModule(moduleConfiguration).setServiceContextClass(SkifTestServiceContext.class));
            install(new RemoteServiceModule(moduleConfiguration, new SkifTestGroup2Services().getServices(), new SkifTestMapper().getMapping()).
                                setExceptionMapping(new SkifTestSimpleExceptionMapper().getMapping()));
        }
    }
}
