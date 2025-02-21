package no.statkart.skif.skiftest.wsapi.service.test;

import no.statkart.skif.SkifModule;
import no.statkart.skif.config.Configuration;
import no.statkart.skif.module.ModuleConfiguration;
import no.statkart.skif.module.ModuleStrategyFactory;
import no.statkart.skif.service.module.client.ClientModuleStrategyFactory;
import no.statkart.skif.service.module.common.RemoteServerModule;
import no.statkart.skif.service.module.common.RemoteWSServiceModule;
import no.statkart.skif.service.module.server.WSServerServiceModule;
import no.statkart.skif.skiftest.config.SkifTestGroup1Services;
import no.statkart.skif.skiftest.config.SkifTestServerModule;
import no.statkart.skif.skiftest.service.SkifTestServiceContext;
import no.statkart.skif.skiftest.wsapi.SkifTestServiceContextMapper;
import no.statkart.skif.skiftest.wsapi.domain.SkifTestContext;
import no.statkart.skif.skiftest.wsapi.exception.ServiceException;
import no.statkart.skif.skiftest.wsapi.mapping.SkifTestMapper;
import no.statkart.skif.skiftest.wsapi.service.test1.Test1Service;
import no.statkart.skif.util.testsupport.SkifTestCase;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Tester muligheten til å kjøre klient direkte mot webservice. Dette skal virke både i single-VM og client-server.
 *
 * @author Tor Egil R. Strand
 * @since 2.1
 */
@Test
public class RemoteWSServiceModuleTest extends SkifTestCase {
    public RemoteWSServiceModuleTest() {
        setModuleClass(ClientModule.class);
        setSingleVmServerModuleClass(ServerModule.class);
    }



    @Test
    public void testTest1ServiceWSDirect() throws ServiceException {
        Test1Service test1Service = injector.getInstance(Test1Service.class);

        SkifTestContext context = new SkifTestContext();
        context.setLocale(Locale.getDefault().toString());
        context.setSystemVersion("2.1");

        String result = test1Service.helloWorld("TestWS", context);
        Assert.assertEquals("Hello1: TestWS", result);
    }

    public static class ClientModule extends SkifModule {

        @Override
        protected ModuleStrategyFactory defineDefaultModuleStrategyFactory() {
            return new ClientModuleStrategyFactory();
        }

        public ClientModule(ModuleConfiguration moduleConfiguration) {
            super(moduleConfiguration);
        }

        @Override
        protected void configure() {
            install(new RemoteServerModule(moduleConfiguration).setServiceContextClass(SkifTestServiceContext.class));

            List<Class<?>> wsServices = new ArrayList<>(1);
            wsServices.add(Test1Service.class);

            install(new RemoteWSServiceModule(moduleConfiguration, wsServices));
        }
    }

    public static class ServerModule extends SkifTestServerModule {

        public ServerModule(Configuration configuration) {
            super(configuration);
        }

        @Override
        protected void configure() {
            super.configure();
            ClassLoader classLoader = getClass().getClassLoader();

            install(
                new WSServerServiceModule(moduleConfiguration, new SkifTestGroup1Services().getServices(), new SkifTestMapper().getMapping(), classLoader)
                    .setServiceContextMapperClass(SkifTestServiceContextMapper.class)
            );
        }
    }
}
