package no.statkart.skif.service.module.server;

import com.google.inject.Binder;
import com.google.inject.PrivateBinder;
import no.statkart.skif.module.ModuleStrategy;
import no.statkart.skif.service.annotation.WSServiceChain;
import no.statkart.skif.service.chain.WSServiceChainFactory;
import no.statkart.skif.service.chain.WSServiceChainFactoryDefaultImpl;
import no.statkart.skif.service.ejb.EJBCallProxyHandler;
import no.statkart.skif.service.provider.WSServiceChainProvider;
import no.statkart.skif.service.proxy.TerminatingProxyHandler;
import no.statkart.skif.service.proxy.W2DAdapterWithServiceContextMapperProxyHandler;
import no.statkart.skif.service.ws.ServiceWSI;
import no.statkart.skif.service.ws.SkifWSInterceptor;
import no.statkart.skif.service.ws.WebServiceImplementationFactory;
import no.statkart.skif.service.ws.WebServiceImplementationFactoryImpl;

import static no.statkart.skif.SkifUtil.typeLiteral;

/**
 * @author Henrik Fredholm
 * @since 1.1
 */
public class WSServerServiceModuleStrategyJEE extends WSServerServiceModuleStrategy {
    public WSServerServiceModuleStrategyJEE() {
        setWsServiceChainFactoryClassForWSI(WSServiceChainFactoryDefaultImpl.class);
        setWsServiceChainFactoryClassForService(WSServiceChainFactoryDefaultImpl.class);
    }
}
