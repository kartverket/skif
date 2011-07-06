package no.statkart.skif.storetest.config;

import com.google.inject.Inject;
import com.google.inject.Provider;
import no.statkart.skif.ServiceMode;
import no.statkart.skif.SkifModule;
import no.statkart.skif.SkifUtil;
import no.statkart.skif.config.Configuration;
import no.statkart.skif.module.ModuleStrategyFactory;
import no.statkart.skif.service.ServiceRequestContext;
import no.statkart.skif.service.chain.EJBServiceChainFactory;
import no.statkart.skif.service.module.ServerModuleStrategyFactory;
import no.statkart.skif.service.module.server.ServerServiceModule;
import no.statkart.skif.service.module.server.ServerModule;
import no.statkart.skif.service.proxy.ChainedProxyHandler;
import no.statkart.skif.service.proxy.ProxyHandler;
import no.statkart.skif.service.proxy.RuntimeExceptionProxyHandler;

import javax.annotation.Nullable;
import java.lang.reflect.Method;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public class StoreTestServerModule extends SkifModule {
    public StoreTestServerModule(Configuration configuration) {
        super(configuration);
    }

    @Override
    protected ModuleStrategyFactory defineDefaultModuleStrategyFactory() {
        return new ServerModuleStrategyFactory();
    }

    @Override
    protected void configure() {
        install(new ServerModule(moduleConfiguration));
        install(new ServerServiceModule(moduleConfiguration, new StoreTestGroup1Services().getServices()));
    }
}

