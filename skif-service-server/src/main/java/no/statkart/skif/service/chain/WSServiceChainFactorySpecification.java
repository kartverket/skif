package no.statkart.skif.service.chain;

import com.google.inject.Binder;

/**
 * @author Henrik Fredholm
 */
public class WSServiceChainFactorySpecification extends FactorySpecification<WSServiceChainFactory> {
    public WSServiceChainFactorySpecification() {
        super(WSServiceChainFactoryBase.class);
    }

    public WSServiceChainFactorySpecification(Class<? extends WSServiceChainFactory> factoryClass) {
        super(factoryClass);
    }

    @Override
    public <S> void bindProxyHandlersForService(Binder binder, Class<S> service) {
    }

    @Override
    public WSServiceChainFactorySpecification clone() {
        return (WSServiceChainFactorySpecification) super.clone();
    }

}
