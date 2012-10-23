package no.statkart.skif.skiftest.config;

import com.google.inject.Binder;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.google.inject.name.Names;
import no.statkart.skif.ServiceMode;
import no.statkart.skif.SkifModule;
import no.statkart.skif.SkifUtil;
import no.statkart.skif.config.Configuration;
import no.statkart.skif.module.ModuleStrategyFactory;
import no.statkart.skif.service.ServiceRequestContext;
import no.statkart.skif.service.chain.EJBServiceChainFactory;
import no.statkart.skif.service.chain.EJBServiceChainFactorySpecification;
import no.statkart.skif.service.module.ServerModuleStrategyFactory;
import no.statkart.skif.service.module.server.RunOnServerServiceModule;
import no.statkart.skif.service.module.server.ServerServiceModule;
import no.statkart.skif.service.module.server.ServerModule;
import no.statkart.skif.service.proxy.ChainedProxyHandler;
import no.statkart.skif.service.proxy.ProxyHandler;
import no.statkart.skif.service.proxy.RuntimeExceptionProxyHandler;
import no.statkart.skif.skiftest.service.SkifTestServiceContext;

import javax.annotation.Nullable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public class SkifTestServerModule extends SkifModule {
    public SkifTestServerModule(Configuration configuration) {
        super(configuration);
    }

    @Override
    protected ModuleStrategyFactory defineDefaultModuleStrategyFactory() {
        return new ServerModuleStrategyFactory();
    }

    @Override
    protected void configure() {
        install(new ServerModule(moduleConfiguration).setServiceContextClass(SkifTestServiceContext.class));
        install(new RunOnServerServiceModule(moduleConfiguration));

        install(new ServerServiceModule(moduleConfiguration, new SkifTestGroup1Services().getServices()));
        install(new ServerServiceModule(moduleConfiguration, new SkifTestGroup2Services().getServices()));

        final ServerServiceModule serverServiceModule = new ServerServiceModule(moduleConfiguration, new SkifTestGroupABCDServices().getServices());
        serverServiceModule.getStrategy(ServiceMode.JEE).setEjbServiceChainFactorySpecification(new AnnotatingEjbServiceChainFactorySpecification());
        serverServiceModule.getStrategy(ServiceMode.SINGLE_VM).setEjbServiceChainFactorySpecification(new AnnotatingEjbServiceChainFactorySpecification());
        install(serverServiceModule);

        install(new ServerServiceModule(moduleConfiguration, new SkifTestGroupExServices().getServices()));

        bind(List.class).annotatedWith(Names.named("SharedList")).to(ArrayList.class).in(Singleton.class);
    }
}

class  AnnotatingEjbServiceChainFactorySpecification extends EJBServiceChainFactorySpecification {

    public AnnotatingEjbServiceChainFactorySpecification() {
        super(AnnotatingEjbServiceChainFactory.class);
    }

    @Override
    public <S> void bindProxyHandlersForService(Binder binder, Class<S> service) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}

/**
 * En EJBServiceChainFactory  for test formål som legger på en ProxyHandler som endre på returverdien for metodekallet og legger
 * på informasjon om i hvilken context kallet ble uført. Denne factory'en kan kun brukte på metoder som
 * returnerer {@code String}
 *
 * @param <S>
 */
class AnnotatingEjbServiceChainFactory<S> implements EJBServiceChainFactory<S> {
    private final Provider<ServiceRequestContext> serviceRequestContextProvider;

    @Inject
    public AnnotatingEjbServiceChainFactory(Provider<ServiceRequestContext> serviceRequestContextProvider) {
        this.serviceRequestContextProvider = serviceRequestContextProvider;
    }

    @Override
    public float getChainPosition() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ProxyHandler<S> createChain() {
        return null;
    }

    @Override
    public ProxyHandler<S> extendChain(@Nullable ProxyHandler<S> firstInChain) {
        final ServiceRequestContext serviceRequestContext = serviceRequestContextProvider.get();
        ChainedProxyHandler<S> h = new ChainedProxyHandler<S>() {
            @Override
            protected Object invokeMethod(Object proxy, Method method, Object[] args) throws Throwable {

                String result = SkifUtil.cast(String.class, chained.invoke(proxy, method, args));
                if (serviceRequestContext.isContinuation()) {
                    if (serviceRequestContext.isTransactional()) {
                        result = "w:" + result;
                    } else {
                        result = "r:" + result;
                    }
                } else {
                    if (serviceRequestContext.isTransactional()) {
                        result = "[Tx:" + result + "]";
                    } else {
                        result = "[NoTx:" + result + "]";
                    }
                }
                return result;
            }
        };
        h.setChained(firstInChain);
        return new RuntimeExceptionProxyHandler<S>()    .setChained(h);
    }
}

