package no.statkart.skif.storetest.config;

import no.statkart.skif.service.ServicesListing;
import no.statkart.skif.storetest.domain.relation.uni.component.entity.X2AAWithEntityComponentFinderService;
import no.statkart.skif.storetest.domain.relation.uni.direct.X1AAFinderService;
import no.statkart.skif.storetest.service.endringslogg.EndringsloggService;
import no.statkart.skif.storetest.service.histtest.HistTestService;
import no.statkart.skif.storetest.service.kodeliste.KodelisteService;
import no.statkart.skif.storetest.service.locking.LockingTestService;
import no.statkart.skif.storetest.service.store.StoreService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * DomainFinderServices i StoreTest. Disse services skal ha en {@code CallServiceChain} med  en ekstra proxy
 * som anvender {@link no.statkart.skif.store.relation.cache.StoreRelationCache}
 *
 * @author Henrik Fredholm
 * @since 2.4
 */
public class StoreTestDomainFinderServices implements ServicesListing {
    private static final List<Class<?>> services;

    static {
        List<Class<?>> modifiableList = new ArrayList<Class<?>>();
        
        modifiableList.add(X1AAFinderService.class);
        modifiableList.add(X2AAWithEntityComponentFinderService.class);

        services = Collections.unmodifiableList(modifiableList);
     }

    @Override
    public List<Class<?>> getServices() {
        return services;
    }
}
