package no.statkart.skif.service.module.common;

import com.google.inject.TypeLiteral;
import no.statkart.skif.SkifModule;
import no.statkart.skif.module.ModuleConfiguration;
import no.statkart.skif.service.ws.JaxWsServiceProvider;

import java.util.List;

import static no.statkart.skif.SkifUtil.typeLiteral;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public class WSRemoteServiceModule extends SkifModule {
    protected List<Class<?>> serviceClasses;
    public WSRemoteServiceModule(ModuleConfiguration moduleConfiguration, List<Class<?>> serviceClases) {
        super(moduleConfiguration);
        this.serviceClasses = serviceClases;
    }

    @Override
    protected void configure() {
        for (Class<?> serviceClass : serviceClasses) {
            bindJaxWsServiceProvider(serviceClass);
        }
    }

    private <T> void bindJaxWsServiceProvider(Class<T> serviceClass) {
        TypeLiteral<JaxWsServiceProvider<T>> providerTypeLiteral = typeLiteral(JaxWsServiceProvider.class, serviceClass);
        bind(serviceClass).toProvider(providerTypeLiteral);
    }
}
