package no.statkart.skif.storetest2.config;

import no.statkart.skif.service.ServicesListing;
import no.statkart.skif.storetest2.service.test.TestdataService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Servicer for testrammeverket.
 *
 * @author Tor Egil R. Strand
 * @since 2.2.0
 */
public class StoreTest2TestServices implements ServicesListing {
    private static final List<Class<?>> services;

    static {
        List<Class<?>> modifiableList = new ArrayList<Class<?>>();

        modifiableList.add(TestdataService.class);

        services = Collections.unmodifiableList(modifiableList);
     }

    @Override
    public List<Class<?>> getServices() {
        return services;
    }
}
