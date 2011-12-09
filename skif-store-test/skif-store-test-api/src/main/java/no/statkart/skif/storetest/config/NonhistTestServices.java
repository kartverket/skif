package no.statkart.skif.storetest.config;

import no.statkart.skif.service.ServicesListing;
import no.statkart.skif.storetest.service.kodeliste.KodelisteService;
import no.statkart.skif.storetest.service.nonhisttest.NonhistTestService;
import no.statkart.skif.storetest.service.store.StoreService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Services i SkifTest som test bean managed persistence
 *
 * @author Henrik Fredholm
 * @since 2.0
 */
public class NonhistTestServices implements ServicesListing {
    private static final List<Class<?>> services;

    static {
        List<Class<?>> modifiableList = new ArrayList<Class<?>>();

        modifiableList.add(StoreService.class);
        modifiableList.add(KodelisteService.class);
        modifiableList.add(NonhistTestService.class);
        services = Collections.unmodifiableList(modifiableList);
     }

    @Override
    public List<Class<?>> getServices() {
        return services;
    }
}
