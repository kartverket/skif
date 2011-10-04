package no.statkart.skif.storetest.config;

import no.statkart.skif.service.ServicesListing;
import no.statkart.skif.storetest.service.kodeliste.KodelisteService;
import no.statkart.skif.storetest.service.store.HistorikkService;
import no.statkart.skif.storetest.service.store.StoreService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Services i StoreTest som ikke tar ServiceContext som parameter
 *
 * @author Henrik Fredholm
 * @since 2.0
 */
public class StoreTestStoreServices implements ServicesListing {
    private static final List<Class<?>> services;

    static {
        List<Class<?>> modifiableList = new ArrayList<Class<?>>();
        
        modifiableList.add(StoreService.class);
        modifiableList.add(HistorikkService.class);
        modifiableList.add(KodelisteService.class);

        services = Collections.unmodifiableList(modifiableList);
     }

    @Override
    public List<Class<?>> getServices() {
        return services;
    }
}
