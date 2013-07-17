package no.statkart.skif.service.module.common;

import com.google.inject.Binder;
import com.google.inject.PrivateBinder;
import com.google.inject.TypeLiteral;
import no.statkart.skif.SkifUtil;
import no.statkart.skif.service.SingleVmServer;
import no.statkart.skif.service.chain.CallServiceChainFactorySpecification;
import no.statkart.skif.service.chain.ClientCallServiceChainFactorySingleVm;
import no.statkart.skif.service.provider.ServiceProvider;
import no.statkart.skif.service.proxy.SingleVmRemoteCallProxyHandler;
import no.statkart.skif.service.proxy.SingleVmViaWSWithApiContextRemoteCallProxyHandler;

/**
 * Strategi for kjøring i SingleVM, men med serialisering via XML mellom klient og tjener.
 * <p/>
 * Denne klassen arver fra {@link RemoteServiceModuleStrategyJEE} siden den har mest logikk til felles, men det betyr at
 * noe superklassen setter må overskrives med verdier tilsvarende {@link RemoteServiceModuleStrategySingleVm}.
 *
 * @author Tor Egil R. Strand
 * @since 2.3.0
 */
public class RemoteServiceModuleStrategySingleVmXml extends RemoteServiceModuleStrategyJEE {
    public RemoteServiceModuleStrategySingleVmXml() {
        // Overskriv det superklassen har satt
        setCallServiceChainFactorySpecification(new CallServiceChainFactorySpecification(ClientCallServiceChainFactorySingleVm.class));
    }

    @Override
    public void requireBindings(Binder binder) {
        requireBinding(binder, SingleVmServer.class);
    }

    @Override
    public <S> void bindService(Binder outerBinder, PrivateBinder innerBinder, Class<S> service) {
        Class<?> webServiceClass = findWebServicePortClass(service);

        TypeLiteral<ServiceProvider<S>> remoteServiceProviderType = SkifUtil.typeLiteral(ServiceProvider.class, service);
        TypeLiteral<SingleVmRemoteCallProxyHandler<S>> singleVmProxyHandlerType = SkifUtil.typeLiteral(SingleVmRemoteCallProxyHandler.class, service);
        TypeLiteral<SingleVmViaWSWithApiContextRemoteCallProxyHandler<S, ?>> singleVmProxyHandlerImplType = SkifUtil.typeLiteral(SingleVmViaWSWithApiContextRemoteCallProxyHandler.class, service, webServiceClass);

        innerBinder.bind(singleVmProxyHandlerType).to(singleVmProxyHandlerImplType);
        innerBinder.expose(singleVmProxyHandlerType);
        outerBinder.bind(service).toProvider(remoteServiceProviderType);
    }
}
