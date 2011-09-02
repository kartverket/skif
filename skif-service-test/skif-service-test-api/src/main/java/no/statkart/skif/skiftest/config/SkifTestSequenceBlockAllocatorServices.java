package no.statkart.skif.skiftest.config;

import no.statkart.skif.service.ServicesListing;
import no.statkart.skif.skiftest.service.id.SequenceBlockAllocatorService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Roar Ingebrigtsen
 * @since 2.0
 */
public class SkifTestSequenceBlockAllocatorServices implements ServicesListing{
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
