package no.statkart.skif.service.module.server;

import com.google.inject.Binder;
import com.google.inject.TypeLiteral;
import no.statkart.skif.SkifUtil;
import no.statkart.skif.service.chain.*;
import no.statkart.skif.service.ejb.EJBCallProxyHandler;
import no.statkart.skif.service.ejb.EJBInterceptorSingleVm;

/**
 * Denne klasse brukes via refelction
 *
 * @author Henrik Fredholm
 * @since 2.0
 */
public class ServerServiceModuleStrategySingleVm extends ServerServiceModuleStrategy {
    public ServerServiceModuleStrategySingleVm() {
        setCallServiceChainFactorySpecification(new CallServiceChainFactorySpecification(ServerCallServiceChainFactory.class));
        setEjbServiceChainFactorySpecification(new EJBServiceChainFactorySpecification());
        setImplementationServiceChainFactorySpecification(new ImplementationServiceChainFactorySpecification());
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
