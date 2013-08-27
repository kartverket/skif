package no.statkart.skif.storetest2.config;

import com.google.common.collect.ImmutableList;
import no.statkart.skif.service.ServicesListing;

import java.util.List;

/**
 * Vanlige tjenester.
 *
 * @author Tor Egil R. Strand
 * @since 2.2.0
 */
public class StoreTest2Services implements ServicesListing {
    private static final List<Class<?>> services;

    static {
        ImmutableList.Builder<Class<?>> builder = ImmutableList.builder();

        services = builder.build();
     }

    @Override
    public List<Class<?>> getServices() {
        return services;
    }
}
