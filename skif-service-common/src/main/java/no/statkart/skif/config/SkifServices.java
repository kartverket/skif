package no.statkart.skif.config;

import no.statkart.skif.service.ServicesListing;
import no.statkart.skif.service.locker.DBLockerService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Opplisting av tjenester som tilbys av SKIF
 *
 * @author Roar Ingebrigtsen
 * @since 2.0
 */
public class SkifServices implements ServicesListing {
    private static final List<Class<?>> services;

    static{
        List<Class<?>> modifiableList = new ArrayList<Class<?>>();
        modifiableList.add(DBLockerService.class);
        services = Collections.unmodifiableList(modifiableList);
    }

    @Override
    public List<Class<?>> getServices() {
        return services;
    }
}
