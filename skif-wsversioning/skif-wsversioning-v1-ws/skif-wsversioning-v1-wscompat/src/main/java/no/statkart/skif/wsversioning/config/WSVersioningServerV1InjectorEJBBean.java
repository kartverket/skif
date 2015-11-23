package no.statkart.skif.wsversioning.config;

import com.google.common.base.Supplier;
import com.google.inject.Injector;
import no.statkart.skif.ServerInjectorRegistry;
import no.statkart.skif.module.ModuleConfiguration;

import javax.ejb.EJB;
import javax.ejb.Stateless;

/**
 * Definere hvilken injector som skal brukes internt i V1-api-et og hvordan denne konfigureres opp.
 *
 * @author Tor Egil R. Strand
 * @since 2.4.0
 */
@Stateless
public class WSVersioningServerV1InjectorEJBBean implements WSVersioningServerV1Injector {
    @EJB
    private WSVersioningServerInjector wsVersioningServerInjector;

    @Override
    public Injector getInjector() {
        return ServerInjectorRegistry.getInjectorCustom("WSVersioningServerModuleV1", new Supplier<Injector>() {
            @Override
            public Injector get() {
                Injector parentInjector = wsVersioningServerInjector.getInjector();
                ModuleConfiguration moduleConfiguration = parentInjector.getInstance(ModuleConfiguration.class);

                return parentInjector.createChildInjector(
                        new WSVersioningV1ServerModule(moduleConfiguration)
                );
            }
        });
    }
}
