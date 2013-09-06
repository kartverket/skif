package no.statkart.skif.wsversioning.config;

import com.google.common.collect.ImmutableList;
import no.statkart.skif.service.ServicesListing;
import no.statkart.skif.wsversioning.service.StoreService;

import java.util.List;

/**
 * Siden service i wsapi-v1 ikke direkte tilsvarer de interne.
 *
 * @author Tor Egil R. Strand
 * @since 2.4.0
 */
public class WSVersioningServicesV1 implements ServicesListing {
    private static final List<Class<?>> services;

    static {
        services = ImmutableList.<Class<?>>of(
                StoreService.class
        );
     }

    @Override
    public List<Class<?>> getServices() {
        return services;
    }
}
