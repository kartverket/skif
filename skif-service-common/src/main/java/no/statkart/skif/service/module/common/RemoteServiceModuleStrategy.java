package no.statkart.skif.service.module.common;

import com.google.inject.Binder;
import com.google.inject.PrivateBinder;
import no.statkart.skif.module.ModuleStrategy;
import no.statkart.skif.service.chain.CallServiceChainFactory;
import no.statkart.skif.service.chain.CallServiceChainFactorySpecification;
import no.statkart.skif.service.chain.ServiceChainFactories;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public abstract class RemoteServiceModuleStrategy extends ModuleStrategy {
    private CallServiceChainFactorySpecification callServiceChainFactorySpecification;

    /**
     * Definerer mapping mellom intern og webservice service-klasser.
     * <br>
     * Skal være på formen {@code &lt;intern&gt;:&lt;extern&gt;}
     */
    protected String[] classWSPackageMappings = {"api:wsapi", "service:wsapi.service","domain:wsapi.service.domain",
            // Midlertidig fix pga "SKIF-382 Håndtering av lange navne"
            "domain.relation.uni.component.entity:wsapi.service.domain.relation.uni.comp"
    };

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

    }


    public <S> void bindCallServiceChainFactoryForService(Binder outerBinder, PrivateBinder innerBinder,  Class<S> service) {
        ServiceChainFactories.multibindFactory(outerBinder, CallServiceChainFactory.class, service, callServiceChainFactorySpecification.getFactoryClass());
        callServiceChainFactorySpecification.bindProxyHandlersForService(outerBinder, service);
    }

    public abstract <S> void bindService(Binder outerBinder, PrivateBinder innerBinder, Class<S> service);

}
