package no.statkart.skif.service.module.common;

import com.google.inject.Binder;
import com.google.inject.TypeLiteral;
import no.statkart.skif.SkifUtil;
import no.statkart.skif.service.SingleVmServer;
import no.statkart.skif.service.chain.CallServiceChainFactory;
import no.statkart.skif.service.chain.CallServiceChainFactorySpecification;
import no.statkart.skif.service.chain.ClientCallServiceChainFactorySingleVm;
import no.statkart.skif.service.chain.ServiceChainFactories;
import no.statkart.skif.service.provider.ServiceProvider;
import no.statkart.skif.service.proxy.SingleVmRemoteCallProxyHandler;
import no.statkart.skif.service.proxy.SingleVmWSRemoteCallProxyHandler;
import no.statkart.skif.service.ws.ServiceWSI;

/**
 * Kobler umappede webservice-kall direkte opp mot implementasjonskjeden på singlevm-tjeneren.
 *
 * @author Tor Egil R. Strand
 * @since 2.1
 */
public class RemoteWSServiceModuleStrategySingleVm extends RemoteWSServiceModuleStrategy {
    private CallServiceChainFactorySpecification callServiceChainFactorySpecification;

    public RemoteWSServiceModuleStrategySingleVm() {
        callServiceChainFactorySpecification = new CallServiceChainFactorySpecification(ClientCallServiceChainFactorySingleVm.class);
    }

    @Override
    public void requireBindings(Binder binder) {
        requireBinding(binder, SingleVmServer.class);
    }

    @Override
    public <S> void bindCallServiceChainFactoryForService(Binder outerBinder, Class<S> service) {
        ServiceChainFactories.multibindFactory(outerBinder, CallServiceChainFactory.class, service, callServiceChainFactorySpecification.getFactoryClass());
        callServiceChainFactorySpecification.bindProxyHandlersForService(outerBinder, service);
    }

    @Override
    public <S> void bindService(Binder outerBinder, Class<S> service) {
        TypeLiteral<ServiceProvider<S>> remoteServiceProviderType = SkifUtil.typeLiteral(ServiceProvider.class, service);
        TypeLiteral<SingleVmRemoteCallProxyHandler<S>> singleVmProxyHandlerType = SkifUtil.typeLiteral(SingleVmRemoteCallProxyHandler.class, service);

        Class<? extends ServiceWSI> wsiClass = SingleVmWSRemoteCallProxyHandler.findWSI(service);

        TypeLiteral<? extends SingleVmRemoteCallProxyHandler<S>> singleVmProxyHandlerImplType = SkifUtil.typeLiteral(SingleVmWSRemoteCallProxyHandler.class, service, wsiClass);

        outerBinder.bind(singleVmProxyHandlerType).to(singleVmProxyHandlerImplType);
        outerBinder.bind(service).toProvider(remoteServiceProviderType);
    }
}
