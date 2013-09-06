package no.statkart.skif.wsversioning.wsapi.v2.config;

import no.statkart.skif.SkifModule;
import no.statkart.skif.mapper.Mapping;
import no.statkart.skif.module.ModuleConfiguration;
import no.statkart.skif.service.module.server.WSServerServiceModule;
import no.statkart.skif.wsversioning.config.WSVersioningServices;
import no.statkart.skif.wsversioning.wsapi.v2.exception.mapping.WSVersioningExceptionMapper;
import no.statkart.skif.wsversioning.wsapi.v2.mapping.WSVersioningMapper;
import no.statkart.skif.wsversioning.wsapi.v2.mapping.WSVersioningServiceContextMapper;

/**
 * Modul som samler oppsett av webservicer i V2.
 *
 * @author Tor Egil R. Strand
 * @since 2.4.0
 */
public class WSVersioningV2WSServerModule extends SkifModule {
    private final ClassLoader classLoader;

    public WSVersioningV2WSServerModule(ModuleConfiguration moduleConfiguration, ClassLoader classLoader) {
        super(moduleConfiguration);
        this.classLoader = classLoader;
    }

    @Override
    protected void configure() {
        final Mapping mapping = new WSVersioningMapper().getMapping();

        install(new WSServerServiceModule(moduleConfiguration, new WSVersioningServices().getServices(), mapping, classLoader)
                .setClassWSIPackageMappings("service:wsapi.v2.service")
                .setExceptionMapping(new WSVersioningExceptionMapper().getMapping())
                .setServiceContextMapperClass(WSVersioningServiceContextMapper.class)
        );
    }
}
