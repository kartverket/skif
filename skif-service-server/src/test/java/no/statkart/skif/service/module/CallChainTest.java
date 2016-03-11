package no.statkart.skif.service.module;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.util.Types;
import no.statkart.skif.ServiceMode;
import no.statkart.skif.SkifModule;
import no.statkart.skif.module.DefaultModuleConfiguration;
import no.statkart.skif.service.annotation.Call;
import no.statkart.skif.service.chain.CallServiceChainFactorySpecification;
import no.statkart.skif.service.chain.ServerCallServiceChainFactory;
import no.statkart.skif.service.module.server.ServerModule;
import no.statkart.skif.service.module.server.ServerServiceModule;
import no.statkart.skif.service.proxy.ChainedProxyHandler;
import no.statkart.skif.service.test.service.Test2Service;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Collections;
import java.util.List;

/**
 * Tester opprettelse av proxy-kjede for kall.
 */
@Test(groups = "singlevm-required")
public class CallChainTest {
    public void buildCustomChain() {
        DefaultModuleConfiguration moduleConfiguration = new DefaultModuleConfiguration();
        moduleConfiguration.setServiceMode(ServiceMode.SINGLE_VM);
        moduleConfiguration.setStrategyFactory(new ServerModuleStrategyFactory());

        Module module = new SkifModule(moduleConfiguration) {
            @Override
            protected void configure() {
                install(new ServerModule(moduleConfiguration));

                ServerServiceModule serverServiceModule = new ServerServiceModule(moduleConfiguration, Collections.<Class<?>>singleton(Test2Service.class));
                serverServiceModule.getStrategy(ServiceMode.SINGLE_VM).setCallServiceChainFactorySpecification(new CallServiceChainFactorySpecification(ServerCallServiceChainFactory.class, TestProxyHandler.class));
                install(serverServiceModule);
            }
        };
        Injector injector = Guice.createInjector(module);
        Object instance = injector.getInstance(Key.get(Types.listOf(Types.newParameterizedType(ChainedProxyHandler.class, Test2Service.class)), Call.class));

        Assert.assertTrue(instance instanceof List);

        List list = (List) instance;
        Assert.assertEquals(list.size(), 1, "Feil antall proxykjedeledd");

        Object ledd = list.get(0);
        Assert.assertTrue(ledd instanceof TestProxyHandler, "Ledd har feil type");

        TestProxyHandler testLedd = (TestProxyHandler) ledd;
        Assert.assertEquals(testLedd.getType().getRawType(), Test2Service.class, "Ledd er for feil service");
    }

}
