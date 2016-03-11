package no.statkart.skif.module;

import com.google.inject.Singleton;
import com.google.inject.name.Names;
import no.statkart.skif.service.module.server.RunOnServerServiceModule;

import java.util.ArrayList;
import java.util.List;

/**
 * TestServerModule med støtte for å kjøre tjenester på serveren
 * @author Henrik Fredholm
 * @since 2.0
 */
public class RunOnRemoteServerTestServerModule extends TestServerModule {
    public RunOnRemoteServerTestServerModule(ModuleConfiguration moduleConfiguration) {
        super(moduleConfiguration);
    }

    @Override
    protected void configure() {
        super.configure();
        install(new RunOnServerServiceModule(moduleConfiguration));
        bind(List.class).annotatedWith(Names.named("test")).to(ArrayList.class).in(Singleton.class); // OBS! Her er det kun bindingen fra @Named List som er singleton, ikke klassen ArrayList

    }
}
