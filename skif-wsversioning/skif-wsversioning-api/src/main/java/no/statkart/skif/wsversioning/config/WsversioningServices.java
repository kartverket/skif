package no.statkart.skif.wsversioning.config;

/**
 * Eksponerte servicer i WSVersioning-prosjektet.
 */
import com.google.common.collect.ImmutableList;
import no.statkart.skif.service.ServicesListing;
import no.statkart.skif.wsversioning.service.StoreService;
import no.statkart.skif.wsversioning.service.VegService;

import java.util.List;

public class WSVersioningServices implements ServicesListing {
    private static final List<Class<?>> services;

    static {
        services = ImmutableList.of(
                StoreService.class,
                VegService.class
        );
     }

    @Override
    public List<Class<?>> getServices() {
        return services;
    }
}
