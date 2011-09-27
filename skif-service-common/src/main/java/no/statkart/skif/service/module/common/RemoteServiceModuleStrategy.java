package no.statkart.skif.service.module.common;

import com.google.inject.Binder;
import com.google.inject.PrivateBinder;
import no.statkart.skif.module.ModuleStrategy;
import no.statkart.skif.service.chain.CallServiceChainFactory;
import no.statkart.skif.service.chain.CallServiceChainFactorySpecification;
import no.statkart.skif.service.chain.ServiceChainFactories;

/**
 * @author Henrik Fredholm
 * @since 1.1
 */
public abstract class RemoteServiceModuleStrategy extends ModuleStrategy {
    private CallServiceChainFactorySpecification callServiceChainFactorySpecification;

    /**
     * Definerer mapping mellom intern og webservice service-klasser.
     * <br />
     * Skal være på formen {@code <intern>:<extern>}
     */
    protected String[] classWSPackageMappings = {"api:wsapi", "service:wsapi.service"};

    public CallServiceChainFactorySpecification getCallServiceChainFactorySpecification() {
        return callServiceChainFactorySpecification;
    }

    public RemoteServiceModuleStrategy setCallServiceChainFactorySpecification(CallServiceChainFactorySpecification callServiceChainFactorySpecification) {
        this.callServiceChainFactorySpecification = callServiceChainFactorySpecification;
        return this;
    }


    /**
     * @return {@link #classWSPackageMappings}
     */
    public String[] getClassWSPackageMappings() {
        return classWSPackageMappings;
    }

    /**
     * @see #classWSPackageMappings
     */
    public RemoteServiceModuleStrategy setClassWSPackageMappings(String... classWSPackageMappings) {
        this.classWSPackageMappings = classWSPackageMappings;
        return this;
    }

    public void requireBindings(Binder binder) {

    };


    public <S> void bindCallServiceChainFactoryForService(Binder outerBinder, PrivateBinder innerBinder,  Class<S> service) {
        ServiceChainFactories.multibindFactory(outerBinder, CallServiceChainFactory.class, service, callServiceChainFactorySpecification.getFactoryClass());
    }

    public abstract <S> void bindService(Binder outerBinder, PrivateBinder innerBinder, Class<S> service);

}
