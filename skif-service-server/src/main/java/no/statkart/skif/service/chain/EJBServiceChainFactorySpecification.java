package no.statkart.skif.service.chain;

import com.google.inject.Binder;

/**
 * @author Henrik Fredholm
 */
public class EJBServiceChainFactorySpecification extends FactorySpecification<EJBServiceChainFactory> {
    public EJBServiceChainFactorySpecification() {
        super(EJBServiceChainFactoryBase.class);
    }

    public EJBServiceChainFactorySpecification(Class<? extends EJBServiceChainFactory> factoryClass) {
        super(factoryClass);
    }

    @Override
    public <S> void bindProxyHandlersForService(Binder binder, Class<S> service) {
    }
}
