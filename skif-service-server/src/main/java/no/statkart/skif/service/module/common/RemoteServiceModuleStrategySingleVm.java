package no.statkart.skif.service.module.common;

import com.google.inject.Binder;
import com.google.inject.PrivateBinder;
import com.google.inject.TypeLiteral;
import no.statkart.skif.SkifUtil;
import no.statkart.skif.service.SingleVmServer;
import no.statkart.skif.service.chain.CallServiceChainFactorySpecification;
import no.statkart.skif.service.chain.ClientCallServiceChainFactorySingleVm;
import no.statkart.skif.service.provider.ServiceProvider;
import no.statkart.skif.service.proxy.SingleVmNoWSWithServiceContextMapperRemoteCallProxyHandler;
import no.statkart.skif.service.proxy.SingleVmRemoteCallProxyHandler;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public class RemoteServiceModuleStrategySingleVm extends RemoteServiceModuleStrategy {
    final Class<? extends SingleVmNoWSWithServiceContextMapperRemoteCallProxyHandler> singleVmRemoteCallProxyHandlerImplClass;

    public RemoteServiceModuleStrategySingleVm() {
        this(SingleVmNoWSWithServiceContextMapperRemoteCallProxyHandler.class);
    }

    public RemoteServiceModuleStrategySingleVm(Class<? extends SingleVmNoWSWithServiceContextMapperRemoteCallProxyHandler> singleVmRemoteCallProxyHandlerImplClass) {
        setCallServiceChainFactorySpecification(new CallServiceChainFactorySpecification(ClientCallServiceChainFactorySingleVm.class));
        this.singleVmRemoteCallProxyHandlerImplClass = singleVmRemoteCallProxyHandlerImplClass;
    }

    @Override
    public void requireBindings(Binder binder) {
        requireBinding(binder, SingleVmServer.class);
    }


    @Override
    public <S> void bindService(Binder outerBinder, PrivateBinder innerBinder, Class<S> service) {
        TypeLiteral<ServiceProvider<S>> remoteServiceProviderType = SkifUtil.typeLiteral(ServiceProvider.class, service);
        TypeLiteral<SingleVmRemoteCallProxyHandler<S>> singleVmProxyHandlerType = SkifUtil.typeLiteral(SingleVmRemoteCallProxyHandler.class, service);
        TypeLiteral<? extends SingleVmRemoteCallProxyHandler<S>> singleVmProxyHandlerImplType = SkifUtil.typeLiteral(singleVmRemoteCallProxyHandlerImplClass, service);

        outerBinder.bind(singleVmProxyHandlerType).to(singleVmProxyHandlerImplType);
        outerBinder.bind(service).toProvider(remoteServiceProviderType);
    }
}
