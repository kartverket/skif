package no.statkart.skif.storetest.config;

import no.statkart.skif.service.ServicesListing;
import no.statkart.skif.storetest.service.endringslogg.EndringsloggService;
import no.statkart.skif.storetest.service.locker.DBLockerInTransactionService;
import no.statkart.skif.storetest.service.locker.DBLockerService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Services i StoreTest som kun benyttes internt og dermed ikke har noen tilhørende webservice.
 *
 * @author Tor Egil R. Strand
 * @since 2.1
 */
public class StoreTestLocalServices implements ServicesListing {
    private static final List<Class<?>> services;

    static {
        List<Class<?>> modifiableList = new ArrayList<Class<?>>();

        modifiableList.add(DBLockerService.class);
        modifiableList.add(DBLockerInTransactionService.class);

        services = Collections.unmodifiableList(modifiableList);
     }

    @Override
    public List<Class<?>> getServices() {
        return services;
    }
}
