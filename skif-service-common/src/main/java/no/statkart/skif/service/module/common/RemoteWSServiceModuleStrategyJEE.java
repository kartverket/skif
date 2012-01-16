package no.statkart.skif.service.module.common;

import com.google.inject.Binder;
import com.google.inject.TypeLiteral;
import no.statkart.skif.SkifUtil;
import no.statkart.skif.service.ws.JaxWsServiceProvider;
import no.statkart.skif.service.ws.JaxWsServiceWithDynamicRequestContextProvider;

/**
 * Kobler umappede webservice-kall opp mot JAX-WS-kjeden.
 *
 * @author Tor Egil R. Strand
 * @since 2.1
 */
public class RemoteWSServiceModuleStrategyJEE extends RemoteWSServiceModuleStrategy {

    @Override
    public void requireBindings(Binder binder) {
    }

    @Override
    public <S> void bindCallServiceChainFactoryForService(Binder outerBinder, Class<S> service) {
    }

    @Override
    public <S> void bindService(Binder outerBinder, Class<S> service) {
        TypeLiteral<JaxWsServiceProvider<S>> jaxWsServiceProviderType = SkifUtil.typeLiteral(JaxWsServiceWithDynamicRequestContextProvider.class, service);
        outerBinder.bind(service).toProvider(jaxWsServiceProviderType);
    }
}
