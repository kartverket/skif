package no.statkart.skif.storetest.config;

import no.statkart.skif.service.ServicesListing;
import no.statkart.skif.storetest.service.test.TestService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Servicer for testrammeverket.
 *
 * @author Tor Egil R. Strand
 * @since 2.1
 */
public class StoreTestTestServices implements ServicesListing {
    private static final List<Class<?>> services;

    static {
        List<Class<?>> modifiableList = new ArrayList<Class<?>>();

        modifiableList.add(TestService.class);

        services = Collections.unmodifiableList(modifiableList);
     }

    @Override
    public List<Class<?>> getServices() {
        return services;
    }
}
