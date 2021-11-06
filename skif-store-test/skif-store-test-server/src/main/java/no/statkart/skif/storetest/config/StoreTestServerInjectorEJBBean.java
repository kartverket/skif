package no.statkart.skif.storetest.config;

import com.google.common.base.Supplier;
import com.google.inject.Injector;
import no.statkart.skif.ServerInjectorRegistry;
import no.statkart.skif.config.SkifConfiguration;
import no.statkart.skif.config.SkifServerConfiguration;
import no.statkart.skif.module.ModuleBuilder;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ejb.Stateless;

/**
 * Definere hvilken injector som skal brukes internt i serveren og hvordan denne konfigureres opp.
 *
 * @author Henrik Fredholm
 * @since 2.0
 */
@Stateless
public class StoreTestServerInjectorEJBBean implements StoreTestServerInjector {
    @Autowired
    private SkifConfiguration configuration;

    @Override
    public Injector getInjector() {
        // Only Spring injects a configuration, for Weblogic we create one manually
        if (configuration == null) {
            configuration = new SkifServerConfiguration();
        }
        return ServerInjectorRegistry.getInjector("SkifTestServerModule", new Supplier<ModuleBuilder>() {
            @Override
            public ModuleBuilder get() {
                return new ModuleBuilder()
                        .setConfiguration(configuration)
                        .setModuleClass(StoreTestServerModule.class);
            }
        });
    }
}
