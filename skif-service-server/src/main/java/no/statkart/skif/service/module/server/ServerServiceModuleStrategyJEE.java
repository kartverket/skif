package no.statkart.skif.service.module.server;

import com.google.inject.Binder;
import com.google.inject.TypeLiteral;
import no.statkart.skif.SkifUtil;
import no.statkart.skif.service.chain.EJBServiceChainFactoryDefaultImpl;
import no.statkart.skif.service.chain.ImplementationServiceChainFactoryDefaultImpl;
import no.statkart.skif.service.chain.ServerCallServiceChainFactory;
import no.statkart.skif.service.ejb.EJBCallProxyHandler;
import no.statkart.skif.service.ejb.EJBCallProxyHandlerJEE;


/**
 * @author Henrik Fredholm
 * @since 1.1
 */
public class ServerServiceModuleStrategyJEE extends ServerServiceModuleStrategy {
    public ServerServiceModuleStrategyJEE() {
        setCallServiceChainFactoryClass(ServerCallServiceChainFactory.class);
        setEjbServiceChainFactoryClass(EJBServiceChainFactoryDefaultImpl.class);
        setImplementationServiceChainFactoryClass(ImplementationServiceChainFactoryDefaultImpl.class);
    }

    @Override
    protected <S> void bindEJBCallProxyHandler(Binder binder, Class<S> service) {
        TypeLiteral<EJBCallProxyHandler<S>> ejbProxyHandlerType = SkifUtil.typeLiteral(EJBCallProxyHandler.class, service);
        TypeLiteral<EJBCallProxyHandlerJEE<S>> ejbImplProxyHandlerType = SkifUtil.typeLiteral(EJBCallProxyHandlerJEE.class, service);
        binder.bind(ejbProxyHandlerType).to(ejbImplProxyHandlerType);
    }
}
