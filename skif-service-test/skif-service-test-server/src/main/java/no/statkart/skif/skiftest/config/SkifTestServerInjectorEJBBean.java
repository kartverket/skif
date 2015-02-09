package no.statkart.skif.skiftest.config;


import com.google.common.base.Supplier;
import com.google.inject.Injector;
import no.statkart.skif.ServerInjectorRegistry;
import no.statkart.skif.module.ModuleBuilder;

import javax.ejb.Stateless;


/**
 * Definere hvilken injector som skal brukes intern i serveren og hvordan denne konfigureres opp.
 *
 * @author Henrik Fredholm
 * @since 2.0
 */
@Stateless
public class SkifTestServerInjectorEJBBean implements SkifTestServerInjector {
    @Override
    public Injector getInjector() {
        return ServerInjectorRegistry.getInjector("SkifTestServerModule", new Supplier<ModuleBuilder>() {
            @Override
            public ModuleBuilder get() {
                return new ModuleBuilder()
                        .setModuleClass(SkifTestServerModule.class);
            }
        });
    }
}
