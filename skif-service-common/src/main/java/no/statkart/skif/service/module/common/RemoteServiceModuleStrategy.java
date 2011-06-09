package no.statkart.skif.service.module.common;

import com.google.inject.Binder;
import com.google.inject.PrivateBinder;
import no.statkart.skif.module.ModuleStrategy;
import no.statkart.skif.service.chain.CallServiceChainFactory;
import no.statkart.skif.service.chain.ServiceChainFactories;

/**
 * @author Henrik Fredholm
 * @since 1.1
 */
public abstract class RemoteServiceModuleStrategy extends ModuleStrategy {
    protected Class<? extends CallServiceChainFactory> callServiceChainFactoryClass;
    protected String[]classWSPackageMappings ={"api:wsapi", "service:wsapi.service"};

    public Class<? extends CallServiceChainFactory> getCallServiceChainFactoryClass() {
        return callServiceChainFactoryClass;
    }

    public RemoteServiceModuleStrategy setCallServiceChainFactoryClass(Class<? extends CallServiceChainFactory> callServiceChainFactoryClass) {
        this.callServiceChainFactoryClass = callServiceChainFactoryClass;
        return this;
    }

    public String[] getClassWSPackageMappings() {
        return classWSPackageMappings;
    }

    public RemoteServiceModuleStrategy setClassWSPackageMappings(String[] classWSPackageMappings) {
        this.classWSPackageMappings = classWSPackageMappings;
        return this;
    }

    public void requireBindings(Binder binder) {

    };


    public <S> void bindCallServiceChainFactoryForService(Binder outerBinder, PrivateBinder innerBinder,  Class<S> service) {
        ServiceChainFactories.multibindFactory(outerBinder, CallServiceChainFactory.class, service, callServiceChainFactoryClass);
    }

    public abstract <S> void bindService(Binder outerBinder, PrivateBinder innerBinder, Class<S> service);

}
