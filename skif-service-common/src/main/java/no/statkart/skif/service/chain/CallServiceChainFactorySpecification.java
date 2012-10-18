package no.statkart.skif.service.chain;

import com.google.inject.Binder;

/**
 * @author Henrik Fredholm
 */
public class CallServiceChainFactorySpecification extends FactorySpecification<CallServiceChainFactory> {
    public CallServiceChainFactorySpecification(Class<? extends CallServiceChainFactory> factoryClass) {
        super(factoryClass);
    }

    @Override
    public <S> void bindProxyHandlersForService(Binder binder, Class<S> service) {
    }

    @Override
    public CallServiceChainFactorySpecification clone() {
        return (CallServiceChainFactorySpecification) super.clone();
    }

}
