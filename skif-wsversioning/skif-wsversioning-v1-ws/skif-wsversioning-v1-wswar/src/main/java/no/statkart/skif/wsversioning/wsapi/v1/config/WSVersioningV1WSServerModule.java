package no.statkart.skif.wsversioning.wsapi.v1.config;

import no.statkart.skif.config.SkifServerModule;
import no.statkart.skif.mapper.Mapping;
import no.statkart.skif.module.ModuleConfiguration;
import no.statkart.skif.service.module.server.WSServerServiceModule;
import no.statkart.skif.wsversioning.config.WSVersioningCompatServicesV1;
import no.statkart.skif.wsversioning.config.WSVersioningServicesV1;
import no.statkart.skif.wsversioning.wsapi.v1.exception.mapping.WSVersioningExceptionMapper;
import no.statkart.skif.wsversioning.wsapi.v1.mapping.WSVersioningMapper;
import no.statkart.skif.wsversioning.wsapi.v1.mapping.WSVersioningServiceContextMapper;

/**
 * Modul som samler oppsett av webservicer i V1.
 *
 * @author Tor Egil R. Strand
 * @since 2.4.0
 */
public class WSVersioningV1WSServerModule extends SkifServerModule {
    private final ClassLoader classLoader;

    public WSVersioningV1WSServerModule(ModuleConfiguration moduleConfiguration, ClassLoader classLoader) {
        super(moduleConfiguration);
        this.classLoader = classLoader;
    }

    @Override
    protected void configure() {
        final Mapping mapping = new WSVersioningMapper().getMapping();

        install(new WSServerServiceModule(moduleConfiguration, new WSVersioningServicesV1().getServices(), mapping, classLoader)
                .setClassWSIPackageMappings("service:wsapi.v1.service")
                .setExceptionMapping(new WSVersioningExceptionMapper().getMapping())
                .setServiceContextMapperClass(WSVersioningServiceContextMapper.class));
        install(new WSServerServiceModule(moduleConfiguration, new WSVersioningCompatServicesV1().getServices(), mapping, classLoader)
                .setClassWSIPackageMappings("service:wsapi.v1.service")
                .setExceptionMapping(new WSVersioningExceptionMapper().getMapping())
                .setServiceContextMapperClass(WSVersioningServiceContextMapper.class));
    }
}
