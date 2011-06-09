package no.statkart.skif.service.module.server;

import com.google.inject.Binder;
import com.google.inject.TypeLiteral;
import no.statkart.skif.SkifUtil;
import no.statkart.skif.service.chain.EJBServiceChainFactoryDefaultImpl;
import no.statkart.skif.service.chain.ImplementationServiceChainFactoryDefaultImpl;
import no.statkart.skif.service.chain.ServerCallServiceChainFactory;
import no.statkart.skif.service.ejb.EJBCallProxyHandler;
import no.statkart.skif.service.ejb.EJBInterceptorSingleVm;

/**
 * @author Henrik Fredholm
 * @since 1.1
 */
public class ServerServiceModuleStrategySingleVm extends ServerServiceModuleStrategy {
    public ServerServiceModuleStrategySingleVm() {
        setCallServiceChainFactoryClass(ServerCallServiceChainFactory.class);
        setEjbServiceChainFactoryClass(EJBServiceChainFactoryDefaultImpl.class);
        setImplementationServiceChainFactoryClass(ImplementationServiceChainFactoryDefaultImpl.class);
    }

    @Override
    protected <S> void bindEJBCallProxyHandler(Binder binder, Class<S> service) {
        TypeLiteral<EJBCallProxyHandler<S>> ejbProxyHandlerType = SkifUtil.typeLiteral(EJBCallProxyHandler.class, service);
        TypeLiteral<EJBInterceptorSingleVm<S>> ejbImplProxyHandlerType = SkifUtil.typeLiteral(EJBInterceptorSingleVm.class, service);
        binder.bind(ejbProxyHandlerType).to(ejbImplProxyHandlerType);

//        TypeLiteral<EJBCallTypeChooserProxyHandler<S>> ejbInvokerProxyHandlerType = SkifUtil.typeLiteral(EJBCallTypeChooserProxyHandler.class, service);
//        binder.bind(ejbInvokerProxyHandlerType).annotatedWith(EJBRef.class);
    }
}
