package no.statkart.skif.skiftest.config;

import no.statkart.skif.service.ServicesListing;
import no.statkart.skif.skiftest.service.testex.TestExService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Services i SkifTest som tar ServiceContext som parameter
 *
 * @author Henrik Fredholm
 * @since 2.0
 */
public class SkifTestGroupExServices implements ServicesListing {
    private static final List<Class<?>> services;

    static {
        List<Class<?>> modifiableList = new ArrayList<Class<?>>();
        
        modifiableList.add(TestExService.class);

        services = Collections.unmodifiableList(modifiableList);
     }

    @Override
    public List<Class<?>> getServices() {
        return services;
    }
}
