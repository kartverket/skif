package no.statkart.skif.service.module.server;

import com.google.inject.Binder;
import com.google.inject.TypeLiteral;
import no.statkart.skif.SkifUtil;
import no.statkart.skif.module.ModuleStrategy;
import no.statkart.skif.service.annotation.EJBServiceChain;
import no.statkart.skif.service.annotation.Implementation;
import no.statkart.skif.service.chain.*;
import no.statkart.skif.service.provider.EJBServiceChainProvider;
import no.statkart.skif.service.provider.ServiceProvider;

/**
 * @author Henrik Fredholm
 * @since 1.1
 */
public abstract class ServerServiceModuleStrategy extends ModuleStrategy {
    private EJBServiceChainFactorySpecification ejbServiceChainFactorySpecification;
    private ImplementationServiceChainFactorySpecification implementationServiceChainFactorySpecification;
    private CallServiceChainFactorySpecification callServiceChainFactorySpecification;

    public EJBServiceChainFactorySpecification getEjbServiceChainFactorySpecification() {
        return ejbServiceChainFactorySpecification;
    }

    public ServerServiceModuleStrategy setEjbServiceChainFactorySpecification(EJBServiceChainFactorySpecification ejbServiceChainFactorySpecification) {
        this.ejbServiceChainFactorySpecification = ejbServiceChainFactorySpecification;
        return this;
    }

    public ImplementationServiceChainFactorySpecification getImplementationServiceChainFactorySpecification() {
        return implementationServiceChainFactorySpecification;
    }

    public void setImplementationServiceChainFactorySpecification(ImplementationServiceChainFactorySpecification implementationServiceChainFactorySpecification) {
        this.implementationServiceChainFactorySpecification = implementationServiceChainFactorySpecification;
    }

    public CallServiceChainFactorySpecification getCallServiceChainFactorySpecification() {
        return callServiceChainFactorySpecification;
    }

    public void setCallServiceChainFactorySpecification(CallServiceChainFactorySpecification callServiceChainFactorySpecification) {
        this.callServiceChainFactorySpecification = callServiceChainFactorySpecification;
    }

    public <S> void bindServiceChainFactoriesForService(Binder binder, Class<S> service) {
        ServiceChainFactories.bindFactory(binder, EJBServiceChainFactory.class, service, ejbServiceChainFactorySpecification.getFactoryClass());
        ejbServiceChainFactorySpecification.bindProxyHandlersForService(binder, service);

        ServiceChainFactories.bindFactory(binder, ImplementationServiceChainFactory.class, service, implementationServiceChainFactorySpecification.getFactoryClass());
        ServiceChainFactories.multibindFactory(binder, CallServiceChainFactory.class, service, callServiceChainFactorySpecification.getFactoryClass());
    }

    protected abstract <S> void bindEJBCallProxyHandler(Binder binder, Class<S> service);


    public <S> void bindService(Binder binder, Class<S> service) {
        bindServiceImplementation(binder, service);
        bindEJBCallProxyHandler(binder, service);
        bindEJBServiceChainProxy(binder, service);
        bindServerServiceProvider(binder, service);
    }

    protected <S> void bindServerServiceProvider(Binder binder, Class<S> service) {
        TypeLiteral<ServiceProvider<S>> serverServiceProviderType = SkifUtil.typeLiteral(ServiceProvider.class, service);
        binder.bind(service).toProvider(serverServiceProviderType);
    }

    protected <S> void bindEJBServiceChainProxy(Binder binder, Class<S> service) {
        TypeLiteral<EJBServiceChainProvider<S>> ejbServiceChainProviderType = SkifUtil.typeLiteral(EJBServiceChainProvider.class, service);
        binder.bind(service).annotatedWith(EJBServiceChain.class).toProvider(ejbServiceChainProviderType);
    }

    protected <S> void bindServiceImplementation(Binder binder, Class<S> service) {
        Class<? extends S> serviceImplClass = getServiceImplementationClass(service);
        binder.bind(service).annotatedWith(Implementation.class).to(serviceImplClass);
    }

    private <S> Class<S> getServiceImplementationClass(Class<S> service) {
        try {
            return (Class<S>) Class.forName(service.getName() + "Impl");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

}
