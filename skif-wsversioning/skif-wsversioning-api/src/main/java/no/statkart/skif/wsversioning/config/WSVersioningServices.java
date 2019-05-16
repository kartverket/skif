package no.statkart.skif.wsversioning.config;

import com.google.common.collect.ImmutableList;
import no.statkart.skif.service.ServicesListing;
import no.statkart.skif.wsversioning.service.LockService;
import no.statkart.skif.wsversioning.service.StoreService;
import no.statkart.skif.wsversioning.service.VegService;

import java.util.List;

/**
 * Eksponerte tjenester i WSVersioning-prosjektet.
 */
public class WSVersioningServices implements ServicesListing {
    private static final List<Class<?>> services;

    static {
        services = ImmutableList.of(
                StoreService.class,
                LockService.class,
                VegService.class
        );
     }

    @Override
    public List<Class<?>> getServices() {
        return services;
    }
}
