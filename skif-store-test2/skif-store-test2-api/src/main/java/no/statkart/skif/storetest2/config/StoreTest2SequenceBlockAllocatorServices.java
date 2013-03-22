package no.statkart.skif.storetest2.config;

import no.statkart.skif.service.ServicesListing;
import no.statkart.skif.storetest2.service.id.SequenceBlockAllocatorService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Roar Ingebrigtsen
 * @since 2.2.0
 */
public class StoreTest2SequenceBlockAllocatorServices implements ServicesListing {
    private static final List<Class<?>> services;

    static {
        List<Class<?>> modifiableList = new ArrayList<Class<?>>();

        modifiableList.add(SequenceBlockAllocatorService.class);
        services = Collections.unmodifiableList(modifiableList);
     }

    @Override
    public List<Class<?>> getServices() {
        return services;
    }
}
