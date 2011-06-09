package no.statkart.skif.service.module.common;

import com.google.inject.TypeLiteral;
import no.statkart.skif.SkifModule;
import no.statkart.skif.config.Configuration;
import no.statkart.skif.module.ModuleConfiguration;
import no.statkart.skif.service.ws.JaxWsServiceWithDynamicRequestContextProvider;

import java.util.List;

import static no.statkart.skif.SkifUtil.typeLiteral;

/**
 * @author Henrik Fredholm
 * @since 1.1
 */
public class WSRemoteServiceModule extends SkifModule {
    protected List<Class<?>> serviceClasses;
    public WSRemoteServiceModule(ModuleConfiguration moduleConfiguration, List<Class<?>> serviceClases) {
        super(moduleConfiguration);
        this.serviceClasses = serviceClases;
    }

    public WSRemoteServiceModule(Configuration configuration) {
        super(configuration);
    }

    @Override
    protected void configure() {
        for (Class<?> serviceClass : serviceClasses) {
            bind(serviceClass).toProvider(typeLiteral(JaxWsServiceWithDynamicRequestContextProvider.class, serviceClass));
        }
    }
}
