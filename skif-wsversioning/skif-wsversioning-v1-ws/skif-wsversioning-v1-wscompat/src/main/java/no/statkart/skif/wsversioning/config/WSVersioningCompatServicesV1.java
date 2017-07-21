package no.statkart.skif.wsversioning.config;

import com.google.common.collect.ImmutableList;
import no.statkart.skif.service.ServicesListing;
import no.statkart.skif.wsversioning.service.GateService;

import java.util.List;

/**
 * Kompatibilitetsservicer for V1.
 *
 * @author Tor Egil R. Strand
 * @since 2.4.0
 */
public class WSVersioningCompatServicesV1 implements ServicesListing {
    private static final List<Class<?>> services;

        static {
            services = ImmutableList.of(
                    GateService.class
            );
         }

        @Override
        public List<Class<?>> getServices() {
            return services;
        }
}
