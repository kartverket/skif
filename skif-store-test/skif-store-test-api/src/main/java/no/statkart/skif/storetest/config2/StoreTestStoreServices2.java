package no.statkart.skif.storetest.config2;

import no.statkart.skif.service.ServicesListing;
import no.statkart.skif.storetest.service2.kodeliste2.KodelisteService2;
import no.statkart.skif.storetest.service2.store2.StoreService2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Services i StoreTest som ikke tar ServiceContext som parameter
 *
 * @author Henrik Fredholm
 * @since 2.0
 */
public class StoreTestStoreServices2 implements ServicesListing {
    private static final List<Class<?>> services;

    static {
        List<Class<?>> modifiableList = new ArrayList<Class<?>>();
        
        modifiableList.add(StoreService2.class);
        modifiableList.add(KodelisteService2.class);

        services = Collections.unmodifiableList(modifiableList);
     }

    @Override
    public List<Class<?>> getServices() {
        return services;
    }
}
