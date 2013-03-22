package no.statkart.skif.storetest2.config;

import no.statkart.skif.service.ServicesListing;
import no.statkart.skif.storetest2.service.store.StoreService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Services i StoreTest som ikke tar ServiceContext som parameter
 *
 * @author Henrik Fredholm
 * @since 2.2.0
 */
public class StoreTest2StoreServices implements ServicesListing {
    private static final List<Class<?>> services;

    static {
        List<Class<?>> modifiableList = new ArrayList<Class<?>>();
        
        modifiableList.add(StoreService.class);
//        modifiableList.add(KodelisteService.class);

        services = Collections.unmodifiableList(modifiableList);
     }

    @Override
    public List<Class<?>> getServices() {
        return services;
    }
}
