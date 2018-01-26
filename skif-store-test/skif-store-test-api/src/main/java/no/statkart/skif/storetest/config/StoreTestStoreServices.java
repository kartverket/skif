package no.statkart.skif.storetest.config;

import com.google.common.collect.ImmutableList;
import no.statkart.skif.service.ServicesListing;
import no.statkart.skif.storetest.service.endringslogg.EndringsloggService;
import no.statkart.skif.storetest.service.exceptiontest.ExceptionTestService;
import no.statkart.skif.storetest.service.histtest.HistTestService;
import no.statkart.skif.storetest.service.kodeliste.KodelisteService;
import no.statkart.skif.storetest.service.lock.LockService;
import no.statkart.skif.storetest.service.locking.LockingTestService;
import no.statkart.skif.storetest.service.nedlastning.NedlastningService;
import no.statkart.skif.storetest.service.store.StoreService;

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
        services = ImmutableList.of(
                StoreService.class,
                LockService.class,
                KodelisteService.class,
                HistTestService.class,
                LockingTestService.class,
                ExceptionTestService.class,
                EndringsloggService.class,
                NedlastningService.class
        );
    }

    @Override
    public List<Class<?>> getServices() {
        return services;
    }
}
