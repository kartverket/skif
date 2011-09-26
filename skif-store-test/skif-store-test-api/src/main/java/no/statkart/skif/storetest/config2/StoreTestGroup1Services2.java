package no.statkart.skif.storetest.config2;

import no.statkart.skif.service.ServicesListing;
import no.statkart.skif.storetest.service2.storetest12.StoreTest1Service2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Services i StoreTest som ikke tar ServiceContext som parameter
 *
 * @author Henrik Fredholm
 * @since 2.0
 */
public class StoreTestGroup1Services2 implements ServicesListing {
    private static final List<Class<?>> services;

    static {
        List<Class<?>> modifiableList = new ArrayList<Class<?>>();
        
        modifiableList.add(StoreTest1Service2.class);

        services = Collections.unmodifiableList(modifiableList);
     }

    @Override
    public List<Class<?>> getServices() {
        return services;
    }
}
