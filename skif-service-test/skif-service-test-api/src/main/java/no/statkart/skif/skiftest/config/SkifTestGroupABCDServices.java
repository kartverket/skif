package no.statkart.skif.skiftest.config;

import no.statkart.skif.service.ServicesListing;
import no.statkart.skif.skiftest.service.testa.AService;
import no.statkart.skif.skiftest.service.testb.BService;
import no.statkart.skif.skiftest.service.testc.CService;
import no.statkart.skif.skiftest.service.testd.DService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Services i SkifTest som tar ServiceContext som parameter
 *
 * @author Henrik Fredholm
 * @since 2.0
 */
public class SkifTestGroupABCDServices implements ServicesListing {
    private static final List<Class<?>> services;

    static {
        List<Class<?>> modifiableList = new ArrayList<Class<?>>();
        
        modifiableList.add(AService.class);
        modifiableList.add(BService.class);
        modifiableList.add(CService.class);
        modifiableList.add(DService.class);

        services = Collections.unmodifiableList(modifiableList);
     }

    @Override
    public List<Class<?>> getServices() {
        return services;
    }
}
