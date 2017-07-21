package no.statkart.skif.storetest.config;

import no.statkart.skif.service.ServicesListing;
import no.statkart.skif.storetest.service.store.StoreUpdateService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Oppdateringsservices i StoreTestStore testapplikasjon
 *
 * @author Henrik Fredholm
 * @since 2.0
 */
public class StoreTestStoreUpdateServices implements ServicesListing {
    private static final List<Class<?>> services;

    static {
        List<Class<?>> modifiableList = new ArrayList<>();
        
        modifiableList.add(StoreUpdateService.class);

        services = Collections.unmodifiableList(modifiableList);
     }

    @Override
    public List<Class<?>> getServices() {
        return services;
    }
}
