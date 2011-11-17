package no.statkart.skif.skiftest.config;

import no.statkart.skif.service.ServicesListing;
import no.statkart.skif.skiftest.service.test2.Test2Service;
import no.statkart.skif.skiftest.service.test3.Test3Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Services i SkifTest som tar ServiceContext som parameter
 *
 * @author Henrik Fredholm
 * @since 2.0
 */
public class SkifTestGroup2Services implements ServicesListing {
    private static final List<Class<?>> services;

    static {
        List<Class<?>> modifiableList = new ArrayList<Class<?>>();
        
        modifiableList.add(Test2Service.class);
        modifiableList.add(Test3Service.class);

        services = Collections.unmodifiableList(modifiableList);
     }

    @Override
    public List<Class<?>> getServices() {
        return services;
    }
}
