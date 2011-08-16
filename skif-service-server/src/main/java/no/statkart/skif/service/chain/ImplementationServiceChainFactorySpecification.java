package no.statkart.skif.service.chain;

import com.google.inject.Binder;

/**
 * @author Henrik Fredholm
 */
public class ImplementationServiceChainFactorySpecification extends FactorySpecification<ImplementationServiceChainFactory> {
    public ImplementationServiceChainFactorySpecification() {
        super(ImplementationServiceChainFactoryBase.class);
    }

    public ImplementationServiceChainFactorySpecification(Class<? extends ImplementationServiceChainFactory> factoryClass) {
        super(factoryClass);
    }

    @Override
    public <S> void bindProxyHandlersForService(Binder binder, Class<S> service) {
    }
}
