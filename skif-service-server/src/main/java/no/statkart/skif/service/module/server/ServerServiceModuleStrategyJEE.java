package no.statkart.skif.service.module.server;

import com.google.inject.Binder;
import com.google.inject.TypeLiteral;
import no.statkart.skif.SkifUtil;
import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.service.annotation.EJBBean;
import no.statkart.skif.service.chain.CallServiceChainFactorySpecification;
import no.statkart.skif.service.chain.EJBServiceChainFactorySpecification;
import no.statkart.skif.service.chain.ImplementationServiceChainFactorySpecification;
import no.statkart.skif.service.chain.ServerCallServiceChainFactory;
import no.statkart.skif.service.ejb.EJBCallProxyHandler;
import no.statkart.skif.service.ejb.EJBCallProxyHandlerJEE;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NameNotFoundException;
import javax.naming.NamingException;


/**
 * @author Henrik Fredholm
 * @since 2.0
 */

@SuppressWarnings("UnusedDeclaration") // Reflection
public class ServerServiceModuleStrategyJEE extends ServerServiceModuleStrategy {
    public ServerServiceModuleStrategyJEE() {
        setCallServiceChainFactorySpecification(new CallServiceChainFactorySpecification(ServerCallServiceChainFactory.class));
        setEjbServiceChainFactorySpecification(new EJBServiceChainFactorySpecification());
        setImplementationServiceChainFactorySpecification(new ImplementationServiceChainFactorySpecification());
    }

    @Override
    protected <S> void bindEJBCallProxyHandler(Binder binder, Class<S> service) {
        TypeLiteral<EJBCallProxyHandler<S>> ejbProxyHandlerType = SkifUtil.typeLiteral(EJBCallProxyHandler.class, service);
        TypeLiteral<EJBCallProxyHandlerJEE<S>> ejbImplProxyHandlerType = SkifUtil.typeLiteral(EJBCallProxyHandlerJEE.class, service);
        binder.bind(ejbProxyHandlerType).to(ejbImplProxyHandlerType);
    }

    @Override
    public void lookupServices(Binder binder, Iterable<? extends Class<?>> services) {
        try {
            Context ctx = new InitialContext();
            for (Class<?> serviceClass : services) {
                String beanName = serviceClass.getName() + "EJBBean";
                Object o = ctx.lookup("java:module/" + beanName);
                bindEJB(binder, serviceClass, o);
            }
            ctx.close();
        } catch (NamingException e) {
            throw new ImplementationException(e);
        }
    }

    private <S> void bindEJB(Binder binder, Class<S> serviceClass, Object ejb) {
        S typedEjb = serviceClass.cast(ejb);
        binder.bind(serviceClass).annotatedWith(EJBBean.class).toInstance(typedEjb);
    }
}
